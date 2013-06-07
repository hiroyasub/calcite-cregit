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
name|rex
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
name|sql
operator|.
name|fun
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Rule which converts a {@link JoinRel} into a {@link CorrelatorRel}, which can  * then be implemented using nested loops.  *  *<p>For example,  *  *<blockquote><code>select * from emp join dept on emp.deptno =  * dept.deptno</code></blockquote>  *  * becomes a CorrelatorRel which restarts TableAccessRel("DEPT") for each row  * read from TableAccessRel("EMP").</p>  *  *<p>This rule is not applicable if for certain types of outer join. For  * example,  *  *<blockquote><code>select * from emp right join dept on emp.deptno =  * dept.deptno</code></blockquote>  *  * would require emitting a NULL emp row if a certain department contained no  * employees, and CorrelatorRel cannot do that.</p>  */
end_comment

begin_class
specifier|public
class|class
name|NestedLoopsJoinRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|NestedLoopsJoinRule
name|instance
init|=
operator|new
name|NestedLoopsJoinRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Private constructor; use singleton {@link #instance}.      */
specifier|private
name|NestedLoopsJoinRule
parameter_list|()
block|{
name|super
argument_list|(
name|any
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|JoinRel
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
condition|)
block|{
case|case
name|INNER
case|:
case|case
name|LEFT
case|:
return|return
literal|true
return|;
case|case
name|FULL
case|:
case|case
name|RIGHT
case|:
return|return
literal|false
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
assert|assert
name|matches
argument_list|(
name|call
argument_list|)
assert|;
specifier|final
name|JoinRel
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|rightKeys
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|RelNode
name|right
init|=
name|join
operator|.
name|getRight
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|left
init|=
name|join
operator|.
name|getLeft
argument_list|()
decl_stmt|;
name|RexNode
name|remainingCondition
init|=
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
decl_stmt|;
assert|assert
name|leftKeys
operator|.
name|size
argument_list|()
operator|==
name|rightKeys
operator|.
name|size
argument_list|()
assert|;
specifier|final
name|List
argument_list|<
name|CorrelatorRel
operator|.
name|Correlation
argument_list|>
name|correlationList
init|=
operator|new
name|ArrayList
argument_list|<
name|CorrelatorRel
operator|.
name|Correlation
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|leftKeys
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|join
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|RexNode
name|condition
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|p
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
control|)
block|{
specifier|final
name|String
name|dyn_inIdStr
init|=
name|cluster
operator|.
name|getQuery
argument_list|()
operator|.
name|createCorrel
argument_list|()
decl_stmt|;
specifier|final
name|int
name|dyn_inId
init|=
name|RelOptQuery
operator|.
name|getCorrelOrdinal
argument_list|(
name|dyn_inIdStr
argument_list|)
decl_stmt|;
comment|// Create correlation to say 'each row, set variable #id
comment|// to the value of column #leftKey'.
name|correlationList
operator|.
name|add
argument_list|(
operator|new
name|CorrelatorRel
operator|.
name|Correlation
argument_list|(
name|dyn_inId
argument_list|,
name|p
operator|.
name|left
argument_list|)
argument_list|)
expr_stmt|;
name|condition
operator|=
name|RelOptUtil
operator|.
name|andJoinFilters
argument_list|(
name|rexBuilder
argument_list|,
name|condition
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|equalsOperator
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|right
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|p
operator|.
name|right
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|p
operator|.
name|right
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeCorrel
argument_list|(
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|p
operator|.
name|left
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|dyn_inIdStr
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|right
operator|=
name|CalcRel
operator|.
name|createFilter
argument_list|(
name|right
argument_list|,
name|condition
argument_list|)
expr_stmt|;
block|}
name|RelNode
name|newRel
init|=
operator|new
name|CorrelatorRel
argument_list|(
name|join
operator|.
name|getCluster
argument_list|()
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|remainingCondition
argument_list|,
name|correlationList
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End NestedLoopsJoinRule.java
end_comment

end_unit

