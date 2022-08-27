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
name|jdbc
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
name|DataContext
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
name|EnumerableRel
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
name|EnumerableRelImplementor
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
name|enumerable
operator|.
name|RexImpTable
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
name|config
operator|.
name|CalciteSystemProperty
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
name|ConstantExpression
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
name|ParameterExpression
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
name|linq4j
operator|.
name|tree
operator|.
name|UnaryExpression
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|runtime
operator|.
name|Hook
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
name|runtime
operator|.
name|SqlFunctions
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
name|schema
operator|.
name|Schemas
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
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
name|util
operator|.
name|SqlString
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Modifier
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|Calendar
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|DataSource
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Nullness
operator|.
name|castNonNull
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of a table in a JDBC data source.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcToEnumerableConverter
extends|extends
name|ConverterImpl
implements|implements
name|EnumerableRel
block|{
specifier|protected
name|JdbcToEnumerableConverter
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
name|JdbcToEnumerableConverter
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
annotation|@
name|Nullable
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
name|RelOptCost
name|cost
init|=
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|)
decl_stmt|;
if|if
condition|(
name|cost
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|cost
operator|.
name|multiplyBy
argument_list|(
literal|.1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
block|{
comment|// Generate:
comment|//   ResultSetEnumerable.of(schema.getDataSource(), "select ...")
specifier|final
name|BlockBuilder
name|builder0
init|=
operator|new
name|BlockBuilder
argument_list|(
literal|false
argument_list|)
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
name|pref
operator|.
name|prefer
argument_list|(
name|JavaRowFormat
operator|.
name|CUSTOM
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|JdbcConvention
name|jdbcConvention
init|=
operator|(
name|JdbcConvention
operator|)
name|requireNonNull
argument_list|(
name|child
operator|.
name|getConvention
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"child.getConvention() is null for "
operator|+
name|child
argument_list|)
decl_stmt|;
name|SqlString
name|sqlString
init|=
name|generateSql
argument_list|(
name|jdbcConvention
operator|.
name|dialect
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
name|sqlString
operator|.
name|getSql
argument_list|()
decl_stmt|;
if|if
condition|(
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
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
name|Hook
operator|.
name|QUERY_PLAN
operator|.
name|run
argument_list|(
name|sql
argument_list|)
expr_stmt|;
specifier|final
name|Expression
name|sql_
init|=
name|builder0
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
name|int
name|fieldCount
init|=
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
name|BlockBuilder
name|builder
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|resultSet_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|ResultSet
operator|.
name|class
argument_list|,
name|builder
operator|.
name|newName
argument_list|(
literal|"resultSet"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|SqlDialect
operator|.
name|CalendarPolicy
name|calendarPolicy
init|=
name|jdbcConvention
operator|.
name|dialect
operator|.
name|getCalendarPolicy
argument_list|()
decl_stmt|;
specifier|final
name|Expression
name|calendar_
decl_stmt|;
switch|switch
condition|(
name|calendarPolicy
condition|)
block|{
case|case
name|LOCAL
case|:
name|calendar_
operator|=
name|builder0
operator|.
name|append
argument_list|(
literal|"calendar"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Calendar
operator|.
name|class
argument_list|,
literal|"getInstance"
argument_list|,
name|getTimeZoneExpression
argument_list|(
name|implementor
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
name|calendar_
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|fieldCount
operator|==
literal|1
condition|)
block|{
specifier|final
name|ParameterExpression
name|value_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|builder
operator|.
name|newName
argument_list|(
literal|"value"
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|value_
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|generateGet
argument_list|(
name|implementor
argument_list|,
name|physType
argument_list|,
name|builder
argument_list|,
name|resultSet_
argument_list|,
literal|0
argument_list|,
name|value_
argument_list|,
name|calendar_
argument_list|,
name|calendarPolicy
argument_list|)
expr_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|value_
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|Expression
name|values_
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"values"
argument_list|,
name|Expressions
operator|.
name|newArrayBounds
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|1
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|fieldCount
argument_list|)
argument_list|)
argument_list|)
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
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|generateGet
argument_list|(
name|implementor
argument_list|,
name|physType
argument_list|,
name|builder
argument_list|,
name|resultSet_
argument_list|,
name|i
argument_list|,
name|Expressions
operator|.
name|arrayIndex
argument_list|(
name|values_
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
name|calendar_
argument_list|,
name|calendarPolicy
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|values_
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ParameterExpression
name|e_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|SQLException
operator|.
name|class
argument_list|,
name|builder
operator|.
name|newName
argument_list|(
literal|"e"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|rowBuilderFactory_
init|=
name|builder0
operator|.
name|append
argument_list|(
literal|"rowBuilderFactory"
argument_list|,
name|Expressions
operator|.
name|lambda
argument_list|(
name|Expressions
operator|.
name|block
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|lambda
argument_list|(
name|Expressions
operator|.
name|block
argument_list|(
name|Expressions
operator|.
name|tryCatch
argument_list|(
name|builder
operator|.
name|toBlock
argument_list|()
argument_list|,
name|Expressions
operator|.
name|catch_
argument_list|(
name|e_
argument_list|,
name|Expressions
operator|.
name|throw_
argument_list|(
name|Expressions
operator|.
name|new_
argument_list|(
name|RuntimeException
operator|.
name|class
argument_list|,
name|e_
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|resultSet_
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|enumerable
decl_stmt|;
if|if
condition|(
name|sqlString
operator|.
name|getDynamicParameters
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|sqlString
operator|.
name|getDynamicParameters
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|Expression
name|preparedStatementConsumer_
init|=
name|builder0
operator|.
name|append
argument_list|(
literal|"preparedStatementConsumer"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|CREATE_ENRICHER
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
literal|1
argument_list|,
name|toIndexesTableExpression
argument_list|(
name|sqlString
argument_list|)
argument_list|)
argument_list|,
name|DataContext
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
name|enumerable
operator|=
name|builder0
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
name|RESULT_SET_ENUMERABLE_OF_PREPARED
operator|.
name|method
argument_list|,
name|Schemas
operator|.
name|unwrap
argument_list|(
name|jdbcConvention
operator|.
name|expression
argument_list|,
name|DataSource
operator|.
name|class
argument_list|)
argument_list|,
name|sql_
argument_list|,
name|rowBuilderFactory_
argument_list|,
name|preparedStatementConsumer_
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|enumerable
operator|=
name|builder0
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
name|Schemas
operator|.
name|unwrap
argument_list|(
name|jdbcConvention
operator|.
name|expression
argument_list|,
name|DataSource
operator|.
name|class
argument_list|)
argument_list|,
name|sql_
argument_list|,
name|rowBuilderFactory_
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder0
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|enumerable
argument_list|,
name|BuiltInMethod
operator|.
name|RESULT_SET_ENUMERABLE_SET_TIMEOUT
operator|.
name|method
argument_list|,
name|DataContext
operator|.
name|ROOT
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|builder0
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
name|builder0
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|ConstantExpression
argument_list|>
name|toIndexesTableExpression
parameter_list|(
name|SqlString
name|sqlString
parameter_list|)
block|{
return|return
name|requireNonNull
argument_list|(
name|sqlString
operator|.
name|getDynamicParameters
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"sqlString.getDynamicParameters() is null for "
operator|+
name|sqlString
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|Expressions
operator|::
name|constant
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|UnaryExpression
name|getTimeZoneExpression
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|implementor
operator|.
name|getRootExpression
argument_list|()
argument_list|,
literal|"get"
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|"timeZone"
argument_list|)
argument_list|)
argument_list|,
name|TimeZone
operator|.
name|class
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|generateGet
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|PhysType
name|physType
parameter_list|,
name|BlockBuilder
name|builder
parameter_list|,
name|ParameterExpression
name|resultSet_
parameter_list|,
name|int
name|i
parameter_list|,
name|Expression
name|target
parameter_list|,
annotation|@
name|Nullable
name|Expression
name|calendar_
parameter_list|,
name|SqlDialect
operator|.
name|CalendarPolicy
name|calendarPolicy
parameter_list|)
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
specifier|final
name|RelDataType
name|fieldType
init|=
name|physType
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|dateTimeArgs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|dateTimeArgs
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|SqlTypeName
name|sqlTypeName
init|=
name|fieldType
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
name|boolean
name|offset
init|=
literal|false
decl_stmt|;
switch|switch
condition|(
name|calendarPolicy
condition|)
block|{
case|case
name|LOCAL
case|:
assert|assert
name|calendar_
operator|!=
literal|null
operator|:
literal|"calendar must not be null"
assert|;
name|dateTimeArgs
operator|.
name|add
argument_list|(
name|calendar_
argument_list|)
expr_stmt|;
break|break;
case|case
name|NULL
case|:
comment|// We don't specify a calendar at all, so we don't add an argument and
comment|// instead use the version of the getXXX that doesn't take a Calendar
break|break;
case|case
name|DIRECT
case|:
name|sqlTypeName
operator|=
name|SqlTypeName
operator|.
name|ANY
expr_stmt|;
break|break;
case|case
name|SHIFT
case|:
switch|switch
condition|(
name|sqlTypeName
condition|)
block|{
case|case
name|TIMESTAMP
case|:
case|case
name|DATE
case|:
name|offset
operator|=
literal|true
expr_stmt|;
break|break;
default|default:
break|break;
block|}
break|break;
default|default:
break|break;
block|}
specifier|final
name|Expression
name|source
decl_stmt|;
switch|switch
condition|(
name|sqlTypeName
condition|)
block|{
case|case
name|DATE
case|:
case|case
name|TIME
case|:
case|case
name|TIMESTAMP
case|:
name|source
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|getMethod
argument_list|(
name|sqlTypeName
argument_list|,
name|fieldType
operator|.
name|isNullable
argument_list|()
argument_list|,
name|offset
argument_list|)
argument_list|,
name|Expressions
operator|.
expr|<
name|Expression
operator|>
name|list
argument_list|()
operator|.
name|append
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|resultSet_
argument_list|,
name|getMethod2
argument_list|(
name|sqlTypeName
argument_list|)
argument_list|,
name|dateTimeArgs
argument_list|)
argument_list|)
operator|.
name|appendIf
argument_list|(
name|offset
argument_list|,
name|getTimeZoneExpression
argument_list|(
name|implementor
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|ARRAY
case|:
specifier|final
name|Expression
name|x
init|=
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|resultSet_
argument_list|,
name|jdbcGetMethod
argument_list|(
name|primitive
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|,
name|java
operator|.
name|sql
operator|.
name|Array
operator|.
name|class
argument_list|)
decl_stmt|;
name|source
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|JDBC_ARRAY_TO_LIST
operator|.
name|method
argument_list|,
name|x
argument_list|)
expr_stmt|;
break|break;
case|case
name|NULL
case|:
name|source
operator|=
name|RexImpTable
operator|.
name|NULL_EXPR
expr_stmt|;
break|break;
default|default:
name|source
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|resultSet_
argument_list|,
name|jdbcGetMethod
argument_list|(
name|primitive
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|target
argument_list|,
name|source
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// [CALCITE-596] If primitive type columns contain null value, returns null
comment|// object
if|if
condition|(
name|primitive
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|ifThen
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|resultSet_
argument_list|,
literal|"wasNull"
argument_list|)
argument_list|,
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|target
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Method
name|getMethod
parameter_list|(
name|SqlTypeName
name|sqlTypeName
parameter_list|,
name|boolean
name|nullable
parameter_list|,
name|boolean
name|offset
parameter_list|)
block|{
switch|switch
condition|(
name|sqlTypeName
condition|)
block|{
case|case
name|DATE
case|:
return|return
operator|(
name|nullable
condition|?
name|BuiltInMethod
operator|.
name|DATE_TO_INT_OPTIONAL
else|:
name|BuiltInMethod
operator|.
name|DATE_TO_INT
operator|)
operator|.
name|method
return|;
case|case
name|TIME
case|:
return|return
operator|(
name|nullable
condition|?
name|BuiltInMethod
operator|.
name|TIME_TO_INT_OPTIONAL
else|:
name|BuiltInMethod
operator|.
name|TIME_TO_INT
operator|)
operator|.
name|method
return|;
case|case
name|TIMESTAMP
case|:
return|return
operator|(
name|nullable
condition|?
operator|(
name|offset
condition|?
name|BuiltInMethod
operator|.
name|TIMESTAMP_TO_LONG_OPTIONAL_OFFSET
else|:
name|BuiltInMethod
operator|.
name|TIMESTAMP_TO_LONG_OPTIONAL
operator|)
else|:
operator|(
name|offset
condition|?
name|BuiltInMethod
operator|.
name|TIMESTAMP_TO_LONG_OFFSET
else|:
name|BuiltInMethod
operator|.
name|TIMESTAMP_TO_LONG
operator|)
operator|)
operator|.
name|method
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|sqlTypeName
operator|+
literal|":"
operator|+
name|nullable
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Method
name|getMethod2
parameter_list|(
name|SqlTypeName
name|sqlTypeName
parameter_list|)
block|{
switch|switch
condition|(
name|sqlTypeName
condition|)
block|{
case|case
name|DATE
case|:
return|return
name|BuiltInMethod
operator|.
name|RESULT_SET_GET_DATE2
operator|.
name|method
return|;
case|case
name|TIME
case|:
return|return
name|BuiltInMethod
operator|.
name|RESULT_SET_GET_TIME2
operator|.
name|method
return|;
case|case
name|TIMESTAMP
case|:
return|return
name|BuiltInMethod
operator|.
name|RESULT_SET_GET_TIMESTAMP2
operator|.
name|method
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|sqlTypeName
argument_list|)
throw|;
block|}
block|}
comment|/** E,g, {@code jdbcGetMethod(int)} returns "getInt". */
specifier|private
specifier|static
name|String
name|jdbcGetMethod
parameter_list|(
annotation|@
name|Nullable
name|Primitive
name|primitive
parameter_list|)
block|{
return|return
name|primitive
operator|==
literal|null
condition|?
literal|"getObject"
else|:
literal|"get"
operator|+
name|SqlFunctions
operator|.
name|initcap
argument_list|(
name|castNonNull
argument_list|(
name|primitive
operator|.
name|primitiveName
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|SqlString
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
name|visitRoot
argument_list|(
name|this
operator|.
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
return|;
block|}
block|}
end_class

end_unit

