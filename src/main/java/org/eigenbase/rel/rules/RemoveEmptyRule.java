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

begin_comment
comment|/**  * Collection of rules which remove sections of a query plan known never to  * produce any rows.  *  * @author Julian Hyde  * @version $Id$  * @see EmptyRel  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RemoveEmptyRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * Singleton instance of rule which removes empty children of a {@link      * UnionRel}.      *      *<p>Examples:      *      *<ul>      *<li>Union(Rel, Empty, Rel2) becomes Union(Rel, Rel2)      *<li>Union(Rel, Empty, Empty) becomes Rel      *<li>Union(Empty, Empty) becomes Empty      *</ul>      */
specifier|public
specifier|static
specifier|final
name|RemoveEmptyRule
name|unionInstance
init|=
operator|new
name|RemoveEmptyRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|UnionRel
operator|.
name|class
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|EmptyRel
operator|.
name|class
argument_list|,
name|ANY
argument_list|)
argument_list|)
argument_list|,
literal|"Union"
argument_list|)
block|{
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|UnionRel
name|union
init|=
operator|(
name|UnionRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|childRels
init|=
name|call
operator|.
name|getChildRels
argument_list|(
name|union
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|newChildRels
init|=
operator|new
name|ArrayList
argument_list|<
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|childRel
range|:
name|childRels
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|childRel
operator|instanceof
name|EmptyRel
operator|)
condition|)
block|{
name|newChildRels
operator|.
name|add
argument_list|(
name|childRel
argument_list|)
expr_stmt|;
block|}
block|}
assert|assert
name|newChildRels
operator|.
name|size
argument_list|()
operator|<
name|childRels
operator|.
name|size
argument_list|()
operator|:
literal|"planner promised us at least one EmptyRel child"
assert|;
name|RelNode
name|newRel
decl_stmt|;
switch|switch
condition|(
name|newChildRels
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
name|newRel
operator|=
operator|new
name|EmptyRel
argument_list|(
name|union
operator|.
name|getCluster
argument_list|()
argument_list|,
name|union
operator|.
name|getRowType
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|newRel
operator|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|newChildRels
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|union
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
break|break;
default|default:
name|newRel
operator|=
operator|new
name|UnionRel
argument_list|(
name|union
operator|.
name|getCluster
argument_list|()
argument_list|,
name|newChildRels
argument_list|,
operator|!
name|union
operator|.
name|isDistinct
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|call
operator|.
name|transformTo
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**      * Singleton instance of rule which converts a {@link ProjectRel} to empty      * if its child is empty.      *      *<p>Examples:      *      *<ul>      *<li>Project(Empty) becomes Empty      *</ul>      */
specifier|public
specifier|static
specifier|final
name|RemoveEmptyRule
name|projectInstance
init|=
operator|new
name|RemoveEmptyRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
operator|(
name|RelTrait
operator|)
literal|null
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|EmptyRel
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
literal|"Project"
argument_list|)
block|{
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|ProjectRel
name|project
init|=
operator|(
name|ProjectRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|EmptyRel
argument_list|(
name|project
operator|.
name|getCluster
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**      * Singleton instance of rule which converts a {@link FilterRel} to empty if      * its child is empty.      *      *<p>Examples:      *      *<ul>      *<li>Filter(Empty) becomes Empty      *</ul>      */
specifier|public
specifier|static
specifier|final
name|RemoveEmptyRule
name|filterInstance
init|=
operator|new
name|RemoveEmptyRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|FilterRel
operator|.
name|class
argument_list|,
operator|(
name|RelTrait
operator|)
literal|null
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|EmptyRel
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
literal|"Filter"
argument_list|)
block|{
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|FilterRel
name|filter
init|=
operator|(
name|FilterRel
operator|)
name|call
operator|.
name|rels
index|[
literal|0
index|]
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
operator|new
name|EmptyRel
argument_list|(
name|filter
operator|.
name|getCluster
argument_list|()
argument_list|,
name|filter
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a RemoveEmptyRule.      *      * @param operand Operand      * @param desc Description      */
specifier|private
name|RemoveEmptyRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
literal|"RemoveEmptyRule:"
operator|+
name|desc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RemoveEmptyRule.java
end_comment

end_unit

