begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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

begin_comment
comment|/**  *<code>AggregateRel</code> is a relational operator which eliminates  * duplicates and computes totals.  *  *<p>Rules:  *  *<ul>  *<li>{@link org.eigenbase.rel.rules.PullConstantsThroughAggregatesRule}  *<li>{@link org.eigenbase.rel.rules.RemoveDistinctAggregateRule}  *<li>{@link org.eigenbase.rel.rules.ReduceAggregatesRule}.  *  * @author jhyde  * @version $Id$  * @since 3 February, 2002  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|AggregateRel
extends|extends
name|AggregateRelBase
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an AggregateRel.      *      * @param cluster Cluster that this relational expression belongs to      * @param child input relational expression      * @param groupSet Bitset of grouping fields      * @param aggCalls Array of aggregates to compute      *      * @pre aggCalls != null      */
specifier|public
name|AggregateRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|BitSet
name|groupSet
parameter_list|,
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|aggCalls
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|child
argument_list|,
name|groupSet
argument_list|,
name|aggCalls
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an AggregateRel by parsing serialized output. */
specifier|public
name|AggregateRel
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|input
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|AggregateRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|groupSet
argument_list|,
name|aggCalls
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RelShuttle
name|shuttle
parameter_list|)
block|{
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggregateRel.java
end_comment

end_unit

