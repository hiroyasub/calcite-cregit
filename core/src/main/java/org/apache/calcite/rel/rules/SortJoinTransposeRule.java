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
name|rel
operator|.
name|rules
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
name|plan
operator|.
name|RelOptRuleCall
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
name|RelRule
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
name|RelCollation
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
name|RelCollationTraitDef
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
name|RelCollations
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
name|RelFieldCollation
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
name|core
operator|.
name|Join
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
name|core
operator|.
name|JoinInfo
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
name|core
operator|.
name|JoinRelType
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
name|core
operator|.
name|Sort
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
name|logical
operator|.
name|LogicalJoin
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
name|logical
operator|.
name|LogicalSort
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
name|RelMdUtil
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
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_comment
comment|/**  * Planner rule that pushes a {@link org.apache.calcite.rel.core.Sort} past a  * {@link org.apache.calcite.rel.core.Join}.  *  *<p>At the moment, we only consider left/right outer joins.  * However, an extension for full outer joins for this rule could be envisioned.  * Special attention should be paid to null values for correctness issues.  *  * @see CoreRules#SORT_JOIN_TRANSPOSE  */
end_comment

begin_class
specifier|public
class|class
name|SortJoinTransposeRule
extends|extends
name|RelRule
argument_list|<
name|SortJoinTransposeRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a SortJoinTransposeRule. */
specifier|protected
name|SortJoinTransposeRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a SortJoinTransposeRule. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|SortJoinTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Sort
argument_list|>
name|sortClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withOperandFor
argument_list|(
name|sortClass
argument_list|,
name|joinClass
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|SortJoinTransposeRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Sort
argument_list|>
name|sortClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withOperandFor
argument_list|(
name|sortClass
argument_list|,
name|joinClass
argument_list|)
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|call
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|JoinInfo
name|joinInfo
init|=
name|JoinInfo
operator|.
name|of
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
decl_stmt|;
comment|// 1) If join is not a left or right outer, we bail out
comment|// 2) If sort is not a trivial order-by, and if there is
comment|// any sort column that is not part of the input where the
comment|// sort is pushed, we bail out
comment|// 3) If sort has an offset, and if the non-preserved side
comment|// of the join is not count-preserving against the join
comment|// condition, we bail out
if|if
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|LEFT
condition|)
block|{
if|if
condition|(
name|sort
operator|.
name|getCollation
argument_list|()
operator|!=
name|RelCollations
operator|.
name|EMPTY
condition|)
block|{
for|for
control|(
name|RelFieldCollation
name|relFieldCollation
range|:
name|sort
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
if|if
condition|(
name|relFieldCollation
operator|.
name|getFieldIndex
argument_list|()
operator|>=
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
if|if
condition|(
name|sort
operator|.
name|offset
operator|!=
literal|null
operator|&&
operator|!
name|RelMdUtil
operator|.
name|areColumnsDefinitelyUnique
argument_list|(
name|mq
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|joinInfo
operator|.
name|rightSet
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
if|else if
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|RIGHT
condition|)
block|{
if|if
condition|(
name|sort
operator|.
name|getCollation
argument_list|()
operator|!=
name|RelCollations
operator|.
name|EMPTY
condition|)
block|{
for|for
control|(
name|RelFieldCollation
name|relFieldCollation
range|:
name|sort
operator|.
name|getCollation
argument_list|()
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
if|if
condition|(
name|relFieldCollation
operator|.
name|getFieldIndex
argument_list|()
operator|<
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
if|if
condition|(
name|sort
operator|.
name|offset
operator|!=
literal|null
operator|&&
operator|!
name|RelMdUtil
operator|.
name|areColumnsDefinitelyUnique
argument_list|(
name|mq
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|joinInfo
operator|.
name|leftSet
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Sort
name|sort
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Join
name|join
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// We create a new sort operator on the corresponding input
specifier|final
name|RelNode
name|newLeftInput
decl_stmt|;
specifier|final
name|RelNode
name|newRightInput
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|call
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
if|if
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|LEFT
condition|)
block|{
comment|// If the input is already sorted and we are not reducing the number of tuples,
comment|// we bail out
if|if
condition|(
name|RelMdUtil
operator|.
name|checkInputForCollationAndLimit
argument_list|(
name|mq
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
condition|)
block|{
return|return;
block|}
name|newLeftInput
operator|=
name|sort
operator|.
name|copy
argument_list|(
name|sort
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
expr_stmt|;
name|newRightInput
operator|=
name|join
operator|.
name|getRight
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|RelCollation
name|rightCollation
init|=
name|RelCollationTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|RelCollations
operator|.
name|shift
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
operator|-
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// If the input is already sorted and we are not reducing the number of tuples,
comment|// we bail out
if|if
condition|(
name|RelMdUtil
operator|.
name|checkInputForCollationAndLimit
argument_list|(
name|mq
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|rightCollation
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
condition|)
block|{
return|return;
block|}
name|newLeftInput
operator|=
name|join
operator|.
name|getLeft
argument_list|()
expr_stmt|;
name|newRightInput
operator|=
name|sort
operator|.
name|copy
argument_list|(
name|sort
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|rightCollation
argument_list|)
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|rightCollation
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
expr_stmt|;
block|}
comment|// We copy the join and the top sort operator
specifier|final
name|RelNode
name|joinCopy
init|=
name|join
operator|.
name|copy
argument_list|(
name|join
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|newLeftInput
argument_list|,
name|newRightInput
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|join
operator|.
name|isSemiJoinDone
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|sortCopy
init|=
name|sort
operator|.
name|copy
argument_list|(
name|sort
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|joinCopy
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|sortCopy
argument_list|)
expr_stmt|;
block|}
comment|/** Rule configuration. */
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|EMPTY
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withOperandFor
argument_list|(
name|LogicalSort
operator|.
name|class
argument_list|,
name|LogicalJoin
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|SortJoinTransposeRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|SortJoinTransposeRule
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/** Defines an operand tree for the given classes. */
specifier|default
name|Config
name|withOperandFor
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Sort
argument_list|>
name|sortClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|joinClass
parameter_list|)
block|{
return|return
name|withOperandSupplier
argument_list|(
name|b0
lambda|->
name|b0
operator|.
name|operand
argument_list|(
name|sortClass
argument_list|)
operator|.
name|oneInput
argument_list|(
name|b1
lambda|->
name|b1
operator|.
name|operand
argument_list|(
name|joinClass
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

