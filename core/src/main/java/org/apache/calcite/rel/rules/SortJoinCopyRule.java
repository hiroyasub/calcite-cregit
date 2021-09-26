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

begin_import
import|import
name|org
operator|.
name|immutables
operator|.
name|value
operator|.
name|Value
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
name|List
import|;
end_import

begin_comment
comment|/**  * Planner rule that copies a {@link org.apache.calcite.rel.core.Sort} past a  * {@link org.apache.calcite.rel.core.Join} without its limit and offset. The  * original {@link org.apache.calcite.rel.core.Sort} is preserved but can be  * potentially removed by {@link org.apache.calcite.rel.rules.SortRemoveRule} if  * redundant.  *  *<p>Some examples where {@link org.apache.calcite.rel.rules.SortJoinCopyRule}  * can be useful: allowing a {@link org.apache.calcite.rel.core.Sort} to be  * incorporated in an index scan; facilitating the use of operators requiring  * sorted inputs; and allowing the sort to be performed on a possibly smaller  * result.  *  * @see CoreRules#SORT_JOIN_COPY  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|SortJoinCopyRule
extends|extends
name|RelRule
argument_list|<
name|SortJoinCopyRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a SortJoinCopyRule. */
specifier|protected
name|SortJoinCopyRule
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
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|SortJoinCopyRule
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
comment|//~ Methods -----------------------------------------------------------------
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
specifier|final
name|RelMetadataQuery
name|metadataQuery
init|=
name|call
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|newLeftInput
decl_stmt|;
specifier|final
name|RelNode
name|newRightInput
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|leftFieldCollation
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|rightFieldCollation
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Decompose sort collations into left and right collations
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
name|rightFieldCollation
operator|.
name|add
argument_list|(
name|relFieldCollation
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|leftFieldCollation
operator|.
name|add
argument_list|(
name|relFieldCollation
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add sort to new left node only if sort collations
comment|// contained fields from left table
if|if
condition|(
name|leftFieldCollation
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|newLeftInput
operator|=
name|join
operator|.
name|getLeft
argument_list|()
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|RelCollation
name|leftCollation
init|=
name|RelCollationTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|RelCollations
operator|.
name|of
argument_list|(
name|leftFieldCollation
argument_list|)
argument_list|)
decl_stmt|;
comment|// If left table already sorted don't add a sort
if|if
condition|(
name|RelMdUtil
operator|.
name|checkInputForCollationAndLimit
argument_list|(
name|metadataQuery
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|leftCollation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|newLeftInput
operator|=
name|join
operator|.
name|getLeft
argument_list|()
expr_stmt|;
block|}
else|else
block|{
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
operator|.
name|replaceIf
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|leftCollation
argument_list|)
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|leftCollation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Add sort to new right node only if sort collations
comment|// contained fields from right table
if|if
condition|(
name|rightFieldCollation
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
name|RelCollations
operator|.
name|of
argument_list|(
name|rightFieldCollation
argument_list|)
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
comment|// If right table already sorted don't add a sort
if|if
condition|(
name|RelMdUtil
operator|.
name|checkInputForCollationAndLimit
argument_list|(
name|metadataQuery
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|rightCollation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
condition|)
block|{
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
name|replaceIf
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
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
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If no change was made no need to apply the rule
if|if
condition|(
name|newLeftInput
operator|==
name|join
operator|.
name|getLeft
argument_list|()
operator|&&
name|newRightInput
operator|==
name|join
operator|.
name|getRight
argument_list|()
condition|)
block|{
return|return;
block|}
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
annotation|@
name|Value
operator|.
name|Immutable
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
name|ImmutableSortJoinCopyRule
operator|.
name|Config
operator|.
name|of
argument_list|()
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
name|SortJoinCopyRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|SortJoinCopyRule
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

