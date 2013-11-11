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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A JoinRel represents two relational expressions joined according to some  * condition.  *  *<p>Some rules:  *  *<ul><li>{@link org.eigenbase.rel.rules.ExtractJoinFilterRule} converts an  * {@link JoinRel inner join} to a {@link FilterRel filter} on top of a {@link  * JoinRel cartesian inner join}.<li>{@code  * net.sf.farrago.fennel.rel.FennelCartesianJoinRule} implements a JoinRel as a  * cartesian product.</ul>  *  * @author jhyde  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|JoinRel
extends|extends
name|JoinRelBase
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|// NOTE jvs 14-Mar-2006:  Normally we don't use state like this
comment|// to control rule firing, but due to the non-local nature of
comment|// semijoin optimizations, it's pretty much required.
specifier|private
specifier|final
name|boolean
name|semiJoinDone
decl_stmt|;
specifier|private
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|systemFieldList
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a JoinRel.      *      * @param cluster Cluster      * @param left Left input      * @param right Right input      * @param condition Join condition      * @param joinType Join type      * @param variablesStopped Set of names of variables which are set by the      * LHS and used by the RHS and are not available to nodes above this JoinRel      * in the tree      */
specifier|public
name|JoinRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|joinType
argument_list|,
name|variablesStopped
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
expr|<
name|RelDataTypeField
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a JoinRel, flagged with whether it has been translated to a      * semi-join.      *      * @param cluster Cluster      * @param left Left input      * @param right Right input      * @param condition Join condition      * @param joinType Join type      * @param variablesStopped Set of names of variables which are set by the      * LHS and used by the RHS and are not available to nodes above this JoinRel      * in the tree      * @param semiJoinDone Whether this join has been translated to a semi-join      * @param systemFieldList List of system fields that will be prefixed to      * output row type; typically empty but must not be null      *      * @see #isSemiJoinDone()      */
specifier|public
name|JoinRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|,
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|systemFieldList
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
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|joinType
argument_list|,
name|variablesStopped
argument_list|)
expr_stmt|;
assert|assert
name|systemFieldList
operator|!=
literal|null
assert|;
name|this
operator|.
name|semiJoinDone
operator|=
name|semiJoinDone
expr_stmt|;
name|this
operator|.
name|systemFieldList
operator|=
name|systemFieldList
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|JoinRel
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RexNode
name|conditionExpr
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
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
name|JoinRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|conditionExpr
argument_list|,
name|this
operator|.
name|joinType
argument_list|,
name|this
operator|.
name|variablesStopped
argument_list|,
name|this
operator|.
name|semiJoinDone
argument_list|,
name|systemFieldList
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
specifier|public
name|RelOptPlanWriter
name|explainTerms
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
comment|// NOTE jvs 14-Mar-2006: Do it this way so that semijoin state
comment|// don't clutter things up in optimizers that don't use semijoins
if|if
condition|(
operator|!
name|semiJoinDone
condition|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
return|;
block|}
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
literal|"semiJoinDone"
argument_list|,
name|semiJoinDone
argument_list|)
return|;
block|}
comment|/**      * Returns whether this JoinRel has already spawned a {@link      * org.eigenbase.rel.rules.SemiJoinRel} via {@link      * org.eigenbase.rel.rules.AddRedundantSemiJoinRule}.      *      * @return whether this join has already spawned a semi join      */
specifier|public
name|boolean
name|isSemiJoinDone
parameter_list|()
block|{
return|return
name|semiJoinDone
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|getSystemFieldList
parameter_list|()
block|{
return|return
name|systemFieldList
return|;
block|}
block|}
end_class

begin_comment
comment|// End JoinRel.java
end_comment

end_unit

