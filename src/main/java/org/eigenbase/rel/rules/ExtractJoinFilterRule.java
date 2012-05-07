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
operator|.
name|rules
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
name|RelDataTypeField
import|;
end_import

begin_comment
comment|/**  * Rule to convert an {@link JoinRel inner join} to a {@link FilterRel filter}  * on top of a {@link JoinRel cartesian inner join}.  *  *<p>One benefit of this transformation is that after it, the join condition  * can be combined with conditions and expressions above the join. It also makes  * the<code>FennelCartesianJoinRule</code> applicable.  *  * @author jhyde  * @version $Id$  * @since 3 February, 2006  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|ExtractJoinFilterRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * The singleton.      */
specifier|public
specifier|static
specifier|final
name|ExtractJoinFilterRule
name|instance
init|=
operator|new
name|ExtractJoinFilterRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an ExtractJoinFilterRule.      */
specifier|private
name|ExtractJoinFilterRule
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|JoinRel
name|joinRel
init|=
operator|(
name|JoinRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|joinRel
operator|.
name|getJoinType
argument_list|()
operator|!=
name|JoinRelType
operator|.
name|INNER
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|joinRel
operator|.
name|getCondition
argument_list|()
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
return|return;
block|}
if|if
condition|(
operator|!
name|joinRel
operator|.
name|getSystemFieldList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// FIXME Enable this rule for joins with system fields
return|return;
block|}
comment|// NOTE jvs 14-Mar-2006:  See SwapJoinRule for why we
comment|// preserve attribute semiJoinDone here.
name|RelNode
name|cartesianJoinRel
init|=
operator|new
name|JoinRel
argument_list|(
name|joinRel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|joinRel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|joinRel
operator|.
name|getRight
argument_list|()
argument_list|,
name|joinRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|,
name|joinRel
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|,
name|joinRel
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|RelDataTypeField
operator|>
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
name|RelNode
name|filterRel
init|=
name|CalcRel
operator|.
name|createFilter
argument_list|(
name|cartesianJoinRel
argument_list|,
name|joinRel
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|filterRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ExtractJoinFilterRule.java
end_comment

end_unit

