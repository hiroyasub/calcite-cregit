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
name|mongodb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
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
name|relopt
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
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Pair
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Relational expression representing a scan of a MongoDB collection.  *  *<p> Additional operations might be applied,  * using the "find" or "aggregate" methods.</p>  */
end_comment

begin_class
specifier|public
class|class
name|MongoTableScan
extends|extends
name|TableAccessRelBase
implements|implements
name|MongoRel
block|{
specifier|final
name|MongoTable
name|mongoTable
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|ops
decl_stmt|;
specifier|final
name|RelDataType
name|projectRowType
decl_stmt|;
comment|/**    * Creates a MongoTableScan.    *    * @param cluster        Cluster    * @param traitSet       Traits    * @param table          Table    * @param mongoTable     MongoDB table    * @param projectRowType Fields& types to project; null to project raw row    * @param ops            List of operators to apply    */
specifier|protected
name|MongoTableScan
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|MongoTable
name|mongoTable
parameter_list|,
name|RelDataType
name|projectRowType
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|ops
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|table
argument_list|)
expr_stmt|;
name|this
operator|.
name|mongoTable
operator|=
name|mongoTable
expr_stmt|;
name|this
operator|.
name|projectRowType
operator|=
name|projectRowType
expr_stmt|;
name|this
operator|.
name|ops
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|ops
argument_list|)
expr_stmt|;
assert|assert
name|mongoTable
operator|!=
literal|null
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|MongoRel
operator|.
name|CONVENTION
assert|;
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
assert|assert
name|inputs
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|projectRowType
operator|!=
literal|null
condition|?
name|projectRowType
else|:
name|super
operator|.
name|deriveRowType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"ops"
argument_list|,
name|ops
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
comment|// scans with a small project list are cheaper
specifier|final
name|float
name|f
init|=
name|projectRowType
operator|==
literal|null
condition|?
literal|1f
else|:
operator|(
name|float
operator|)
name|projectRowType
operator|.
name|getFieldCount
argument_list|()
operator|/
literal|100f
decl_stmt|;
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
literal|.1
operator|*
name|f
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|MongoToEnumerableConverterRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
for|for
control|(
name|RelOptRule
name|rule
range|:
name|MongoRules
operator|.
name|RULES
control|)
block|{
name|planner
operator|.
name|addRule
argument_list|(
name|rule
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|mongoTable
operator|=
name|mongoTable
expr_stmt|;
name|implementor
operator|.
name|table
operator|=
name|table
expr_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|op
range|:
name|ops
control|)
block|{
name|implementor
operator|.
name|add
argument_list|(
name|op
operator|.
name|left
argument_list|,
name|op
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End MongoTableScan.java
end_comment

end_unit

