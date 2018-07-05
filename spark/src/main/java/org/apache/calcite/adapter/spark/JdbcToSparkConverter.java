begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|spark
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|enumerable
operator|.
name|JavaRowFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|enumerable
operator|.
name|PhysType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|enumerable
operator|.
name|PhysTypeImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcConvention
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcImplementor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcRel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcSchema
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|tree
operator|.
name|BlockBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|tree
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|tree
operator|.
name|Primitive
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|ConventionTraitDef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptCluster
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptCost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelOptPlanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|plan
operator|.
name|RelTraitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|prepare
operator|.
name|CalcitePrepareImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|convert
operator|.
name|ConverterImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|metadata
operator|.
name|RelMetadataQuery
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlDialect
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|BuiltInMethod
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of a table in a JDBC data source  * that returns its results as a Spark RDD.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcToSparkConverter
extends|extends
name|ConverterImpl
implements|implements
name|SparkRel
block|{
specifier|protected
name|JdbcToSparkConverter
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|,
name|traits
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
operator|new
name|JdbcToSparkConverter
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|)
operator|.
name|multiplyBy
argument_list|(
literal|.1
argument_list|)
return|;
block|}
specifier|public
name|SparkRel
operator|.
name|Result
name|implementSpark
parameter_list|(
name|SparkRel
operator|.
name|Implementor
name|implementor
parameter_list|)
block|{
comment|// Generate:
comment|//   ResultSetEnumerable.of(schema.getDataSource(), "select ...")
specifier|final
name|BlockBuilder
name|list
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|JdbcRel
name|child
init|=
operator|(
name|JdbcRel
operator|)
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|PhysType
name|physType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|implementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|JavaRowFormat
operator|.
name|CUSTOM
argument_list|)
decl_stmt|;
specifier|final
name|JdbcConvention
name|jdbcConvention
init|=
operator|(
name|JdbcConvention
operator|)
name|child
operator|.
name|getConvention
argument_list|()
decl_stmt|;
name|String
name|sql
init|=
name|generateSql
argument_list|(
name|jdbcConvention
operator|.
name|dialect
argument_list|)
decl_stmt|;
if|if
condition|(
name|CalcitePrepareImpl
operator|.
name|DEBUG
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"["
operator|+
name|sql
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Expression
name|sqlLiteral
init|=
name|list
operator|.
name|append
argument_list|(
literal|"sql"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|sql
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Primitive
argument_list|>
name|primitives
init|=
operator|new
name|ArrayList
argument_list|<
name|Primitive
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Primitive
name|primitive
init|=
name|Primitive
operator|.
name|ofBoxOr
argument_list|(
name|physType
operator|.
name|fieldClass
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|primitives
operator|.
name|add
argument_list|(
name|primitive
operator|!=
literal|null
condition|?
name|primitive
else|:
name|Primitive
operator|.
name|OTHER
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Expression
name|primitivesLiteral
init|=
name|list
operator|.
name|append
argument_list|(
literal|"primitives"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|primitives
operator|.
name|toArray
argument_list|(
operator|new
name|Primitive
index|[
literal|0
index|]
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|enumerable
init|=
name|list
operator|.
name|append
argument_list|(
literal|"enumerable"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|RESULT_SET_ENUMERABLE_OF
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Expressions
operator|.
name|convert_
argument_list|(
name|jdbcConvention
operator|.
name|expression
argument_list|,
name|JdbcSchema
operator|.
name|class
argument_list|)
argument_list|,
name|BuiltInMethod
operator|.
name|JDBC_SCHEMA_DATA_SOURCE
operator|.
name|method
argument_list|)
argument_list|,
name|sqlLiteral
argument_list|,
name|primitivesLiteral
argument_list|)
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|enumerable
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|list
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|generateSql
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
block|{
specifier|final
name|JdbcImplementor
name|jdbcImplementor
init|=
operator|new
name|JdbcImplementor
argument_list|(
name|dialect
argument_list|,
operator|(
name|JavaTypeFactory
operator|)
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|JdbcImplementor
operator|.
name|Result
name|result
init|=
name|jdbcImplementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|result
operator|.
name|asStatement
argument_list|()
operator|.
name|toSqlString
argument_list|(
name|dialect
argument_list|)
operator|.
name|getSql
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcToSparkConverter.java
end_comment

end_unit

