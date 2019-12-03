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
name|BlockStatement
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
name|sql
operator|.
name|validate
operator|.
name|SqlConformance
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
comment|/**  * Relational expression that converts input of  * {@link org.apache.calcite.adapter.spark.SparkRel#CONVENTION Spark convention}  * into {@link org.apache.calcite.adapter.enumerable.EnumerableConvention}.  *  *<p>Concretely, this means calling the  * {@link org.apache.spark.api.java.JavaRDD#collect()} method of an RDD  * and converting it to enumerable.</p>  */
end_comment

begin_class
specifier|public
class|class
name|SparkToEnumerableConverter
extends|extends
name|ConverterImpl
implements|implements
name|EnumerableRel
block|{
specifier|protected
name|SparkToEnumerableConverter
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
name|SparkToEnumerableConverter
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
literal|.01
argument_list|)
return|;
block|}
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
comment|//   RDD rdd = ...;
comment|//   return SparkRuntime.asEnumerable(rdd);
specifier|final
name|BlockBuilder
name|list
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|SparkRel
name|child
init|=
operator|(
name|SparkRel
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
name|SparkRel
operator|.
name|Implementor
name|sparkImplementor
init|=
operator|new
name|SparkImplementorImpl
argument_list|(
name|implementor
argument_list|)
decl_stmt|;
specifier|final
name|SparkRel
operator|.
name|Result
name|result
init|=
name|child
operator|.
name|implementSpark
argument_list|(
name|sparkImplementor
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|rdd
init|=
name|list
operator|.
name|append
argument_list|(
literal|"rdd"
argument_list|,
name|result
operator|.
name|block
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
name|SparkMethod
operator|.
name|AS_ENUMERABLE
operator|.
name|method
argument_list|,
name|rdd
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
comment|/** Implementation of    * {@link org.apache.calcite.adapter.spark.SparkRel.Implementor}. */
specifier|private
specifier|static
class|class
name|SparkImplementorImpl
extends|extends
name|SparkRel
operator|.
name|Implementor
block|{
specifier|private
specifier|final
name|EnumerableRelImplementor
name|implementor
decl_stmt|;
name|SparkImplementorImpl
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|)
block|{
name|super
argument_list|(
name|implementor
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|implementor
operator|=
name|implementor
expr_stmt|;
block|}
specifier|public
name|SparkRel
operator|.
name|Result
name|result
parameter_list|(
name|PhysType
name|physType
parameter_list|,
name|BlockStatement
name|blockStatement
parameter_list|)
block|{
return|return
operator|new
name|SparkRel
operator|.
name|Result
argument_list|(
name|physType
argument_list|,
name|blockStatement
argument_list|)
return|;
block|}
name|SparkRel
operator|.
name|Result
name|visitInput
parameter_list|(
name|SparkRel
name|parent
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|SparkRel
name|input
parameter_list|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
assert|assert
name|input
operator|==
name|parent
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
assert|;
block|}
return|return
name|input
operator|.
name|implementSpark
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|implementor
operator|.
name|getTypeFactory
argument_list|()
return|;
block|}
specifier|public
name|SqlConformance
name|getConformance
parameter_list|()
block|{
return|return
name|implementor
operator|.
name|getConformance
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

