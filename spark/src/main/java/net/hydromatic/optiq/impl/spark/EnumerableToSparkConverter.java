begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|spark
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|rel
operator|.
name|convert
operator|.
name|ConverterRelImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
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
comment|/**  * Relational expression that converts input of {@link EnumerableConvention}  * into {@link SparkRel#CONVENTION Spark convention}.  *  *<p>Concretely, this means iterating over the contents of an  * {@link net.hydromatic.linq4j.Enumerable}, storing them in a list, and  * building an {@link org.apache.spark.rdd.RDD} on top of it.</p>  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableToSparkConverter
extends|extends
name|ConverterRelImpl
implements|implements
name|SparkRel
block|{
specifier|protected
name|EnumerableToSparkConverter
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
name|instance
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
name|EnumerableToSparkConverter
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
parameter_list|)
block|{
return|return
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
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
name|implementSpark
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
comment|// Generate:
comment|//   Enumerable source = ...;
comment|//   return SparkRuntime.createRdd(sparkContext, source);
specifier|final
name|BlockBuilder
name|list
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|EnumerableRel
name|child
init|=
operator|(
name|EnumerableRel
operator|)
name|getChild
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
name|Expression
name|source
init|=
literal|null
decl_stmt|;
comment|// TODO:
specifier|final
name|Expression
name|sparkContext
init|=
name|Expressions
operator|.
name|call
argument_list|(
name|SparkMethod
operator|.
name|GET_SPARK_CONTEXT
operator|.
name|method
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
name|Expressions
operator|.
name|call
argument_list|(
name|SparkMethod
operator|.
name|CREATE_RDD
operator|.
name|method
argument_list|,
name|sparkContext
argument_list|,
name|source
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
name|rdd
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
block|}
end_class

begin_comment
comment|// End EnumerableToSparkConverter.java
end_comment

end_unit

