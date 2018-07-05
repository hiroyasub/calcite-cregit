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
name|geode
operator|.
name|rel
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
name|Convention
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
name|RelOptRule
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
name|RelOptUtil
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
name|ConverterRule
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
name|LogicalAggregate
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
name|LogicalFilter
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
name|LogicalProject
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
name|type
operator|.
name|RelDataType
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
name|rex
operator|.
name|RexCall
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
name|rex
operator|.
name|RexInputRef
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
name|rex
operator|.
name|RexLiteral
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexVisitorImpl
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
name|SqlKind
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|type
operator|.
name|SqlTypeName
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
name|SqlValidatorUtil
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_comment
comment|/**  * Rules and relational operators for {@link GeodeRel#CONVENTION} calling convention.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeRules
block|{
specifier|public
specifier|static
specifier|final
name|RelOptRule
index|[]
name|RULES
init|=
block|{
name|GeodeFilterRule
operator|.
name|INSTANCE
block|,
name|GeodeProjectRule
operator|.
name|INSTANCE
block|,
name|GeodeSortLimitRule
operator|.
name|INSTANCE
block|,
name|GeodeAggregateRule
operator|.
name|INSTANCE
block|}
decl_stmt|;
specifier|private
name|GeodeRules
parameter_list|()
block|{
block|}
comment|/**    * Returns 'string' if it is a call to item['string'], null otherwise.    */
specifier|static
name|String
name|isItem
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|!=
name|SqlStdOperatorTable
operator|.
name|ITEM
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RexNode
name|op0
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|op1
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|op0
operator|instanceof
name|RexInputRef
operator|&&
operator|(
operator|(
name|RexInputRef
operator|)
name|op0
operator|)
operator|.
name|getIndex
argument_list|()
operator|==
literal|0
operator|&&
name|op1
operator|instanceof
name|RexLiteral
operator|&&
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
operator|instanceof
name|String
condition|)
block|{
return|return
operator|(
name|String
operator|)
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|geodeFieldNames
parameter_list|(
specifier|final
name|RelDataType
name|rowType
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
operator|new
name|AbstractList
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|rowType
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
block|}
decl_stmt|;
return|return
name|SqlValidatorUtil
operator|.
name|uniquify
argument_list|(
name|fieldNames
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**    * Translator from {@link RexNode} to strings in Geode's expression language.    */
specifier|static
class|class
name|RexToGeodeTranslator
extends|extends
name|RexVisitorImpl
argument_list|<
name|String
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|inFields
decl_stmt|;
specifier|protected
name|RexToGeodeTranslator
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|inFields
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|inFields
operator|=
name|inFields
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
return|return
name|inFields
operator|.
name|get
argument_list|(
name|inputRef
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
name|visitList
argument_list|(
name|call
operator|.
name|operands
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|ITEM
condition|)
block|{
specifier|final
name|RexNode
name|op1
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|op1
operator|instanceof
name|RexLiteral
condition|)
block|{
if|if
condition|(
name|op1
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|INTEGER
condition|)
block|{
return|return
name|stripQuotes
argument_list|(
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
literal|"["
operator|+
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
operator|+
literal|"]"
return|;
block|}
if|else if
condition|(
name|op1
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|CHAR
condition|)
block|{
return|return
name|stripQuotes
argument_list|(
name|strings
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
literal|"."
operator|+
operator|(
operator|(
name|RexLiteral
operator|)
name|op1
operator|)
operator|.
name|getValue2
argument_list|()
return|;
block|}
block|}
block|}
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
specifier|private
name|String
name|stripQuotes
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|s
operator|.
name|startsWith
argument_list|(
literal|"'"
argument_list|)
operator|&&
name|s
operator|.
name|endsWith
argument_list|(
literal|"'"
argument_list|)
condition|?
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
else|:
name|s
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|visitList
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|strings
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|node
range|:
name|list
control|)
block|{
name|strings
operator|.
name|add
argument_list|(
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
block|}
comment|/**    * Rule to convert a {@link LogicalProject} to a {@link GeodeProject}.    */
specifier|private
specifier|static
class|class
name|GeodeProjectRule
extends|extends
name|GeodeConverterRule
block|{
specifier|private
specifier|static
specifier|final
name|GeodeProjectRule
name|INSTANCE
init|=
operator|new
name|GeodeProjectRule
argument_list|()
decl_stmt|;
specifier|private
name|GeodeProjectRule
parameter_list|()
block|{
name|super
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
literal|"GeodeProjectRule"
argument_list|)
expr_stmt|;
block|}
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
name|LogicalProject
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|e
range|:
name|project
operator|.
name|getProjects
argument_list|()
control|)
block|{
if|if
condition|(
name|e
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|GEOMETRY
condition|)
block|{
comment|// For spatial Functions Drop to Calcite Enumerable
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|LogicalProject
name|project
init|=
operator|(
name|LogicalProject
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|project
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeodeProject
argument_list|(
name|project
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|out
argument_list|)
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert {@link org.apache.calcite.rel.core.Aggregate} to a    * {@link GeodeAggregate}.    */
specifier|private
specifier|static
class|class
name|GeodeAggregateRule
extends|extends
name|GeodeConverterRule
block|{
specifier|private
specifier|static
specifier|final
name|GeodeAggregateRule
name|INSTANCE
init|=
operator|new
name|GeodeAggregateRule
argument_list|()
decl_stmt|;
name|GeodeAggregateRule
parameter_list|()
block|{
name|super
argument_list|(
name|LogicalAggregate
operator|.
name|class
argument_list|,
literal|"GeodeAggregateRule"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|LogicalAggregate
name|aggregate
init|=
operator|(
name|LogicalAggregate
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|aggregate
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|out
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeodeAggregate
argument_list|(
name|aggregate
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|aggregate
operator|.
name|getInput
argument_list|()
argument_list|,
name|traitSet
operator|.
name|simplify
argument_list|()
argument_list|)
argument_list|,
name|aggregate
operator|.
name|indicator
argument_list|,
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|,
name|aggregate
operator|.
name|getGroupSets
argument_list|()
argument_list|,
name|aggregate
operator|.
name|getAggCallList
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Rule to convert the Limit in {@link org.apache.calcite.rel.core.Sort} to a    * {@link GeodeSort}.    */
specifier|private
specifier|static
class|class
name|GeodeSortLimitRule
extends|extends
name|RelOptRule
block|{
specifier|private
specifier|static
specifier|final
name|GeodeSortLimitRule
name|INSTANCE
init|=
operator|new
name|GeodeSortLimitRule
argument_list|(
comment|// OQL doesn't support for offsets (e.g. LIMIT 10 OFFSET 500)
name|sort
lambda|->
name|sort
operator|.
name|offset
operator|==
literal|null
argument_list|)
decl_stmt|;
name|GeodeSortLimitRule
parameter_list|(
name|Predicate
argument_list|<
name|Sort
argument_list|>
name|predicate
parameter_list|)
block|{
name|super
argument_list|(
name|operandJ
argument_list|(
name|Sort
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|predicate
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
literal|"GeodeSortLimitRule"
argument_list|)
expr_stmt|;
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
name|RelTraitSet
name|traitSet
init|=
name|sort
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|GeodeRel
operator|.
name|CONVENTION
argument_list|)
operator|.
name|replace
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
decl_stmt|;
name|GeodeSort
name|geodeSort
init|=
operator|new
name|GeodeSort
argument_list|(
name|sort
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|sort
operator|.
name|getInput
argument_list|()
argument_list|,
name|traitSet
operator|.
name|replace
argument_list|(
name|RelCollations
operator|.
name|EMPTY
argument_list|)
argument_list|)
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
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
name|geodeSort
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Rule to convert a {@link LogicalFilter} to a    * {@link GeodeFilter}.    */
specifier|private
specifier|static
class|class
name|GeodeFilterRule
extends|extends
name|RelOptRule
block|{
specifier|private
specifier|static
specifier|final
name|GeodeFilterRule
name|INSTANCE
init|=
operator|new
name|GeodeFilterRule
argument_list|()
decl_stmt|;
specifier|private
name|GeodeFilterRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|LogicalFilter
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|GeodeTableScan
operator|.
name|class
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|,
literal|"GeodeFilterRule"
argument_list|)
expr_stmt|;
block|}
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
comment|// Get the condition from the filter operation
name|LogicalFilter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|RexNode
name|condition
init|=
name|filter
operator|.
name|getCondition
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|GeodeRules
operator|.
name|geodeFieldNames
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|disjunctions
init|=
name|RelOptUtil
operator|.
name|disjunctions
argument_list|(
name|condition
argument_list|)
decl_stmt|;
if|if
condition|(
name|disjunctions
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
comment|// Check that all conjunctions are primary field conditions.
name|condition
operator|=
name|disjunctions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|RexNode
name|predicate
range|:
name|RelOptUtil
operator|.
name|conjunctions
argument_list|(
name|condition
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|isEqualityOnKey
argument_list|(
name|predicate
argument_list|,
name|fieldNames
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * Check if the node is a supported predicate (primary field condition).      *      * @param node       Condition node to check      * @param fieldNames Names of all columns in the table      * @return True if the node represents an equality predicate on a primary key      */
specifier|private
name|boolean
name|isEqualityOnKey
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
specifier|final
name|RexNode
name|left
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|right
init|=
name|call
operator|.
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|checkConditionContainsInputRefOrLiterals
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|fieldNames
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|checkConditionContainsInputRefOrLiterals
argument_list|(
name|right
argument_list|,
name|left
argument_list|,
name|fieldNames
argument_list|)
return|;
block|}
comment|/**      * Checks whether a condition contains input refs of literals.      *      * @param left       Left operand of the equality      * @param right      Right operand of the equality      * @param fieldNames Names of all columns in the table      * @return Whether condition is supported      */
specifier|private
name|boolean
name|checkConditionContainsInputRefOrLiterals
parameter_list|(
name|RexNode
name|left
parameter_list|,
name|RexNode
name|right
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
comment|// FIXME Ignore casts for rel and assume they aren't really necessary
if|if
condition|(
name|left
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|CAST
argument_list|)
condition|)
block|{
name|left
operator|=
operator|(
operator|(
name|RexCall
operator|)
name|left
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|right
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|CAST
argument_list|)
condition|)
block|{
name|right
operator|=
operator|(
operator|(
name|RexCall
operator|)
name|right
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|left
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|INPUT_REF
argument_list|)
operator|&&
name|right
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|LITERAL
argument_list|)
condition|)
block|{
specifier|final
name|RexInputRef
name|left1
init|=
operator|(
name|RexInputRef
operator|)
name|left
decl_stmt|;
name|String
name|name
init|=
name|fieldNames
operator|.
name|get
argument_list|(
name|left1
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|name
operator|!=
literal|null
return|;
block|}
if|else if
condition|(
name|left
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|INPUT_REF
argument_list|)
operator|&&
name|right
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|INPUT_REF
argument_list|)
condition|)
block|{
specifier|final
name|RexInputRef
name|left1
init|=
operator|(
name|RexInputRef
operator|)
name|left
decl_stmt|;
name|String
name|leftName
init|=
name|fieldNames
operator|.
name|get
argument_list|(
name|left1
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RexInputRef
name|right1
init|=
operator|(
name|RexInputRef
operator|)
name|right
decl_stmt|;
name|String
name|rightName
init|=
name|fieldNames
operator|.
name|get
argument_list|(
name|right1
operator|.
name|getIndex
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|leftName
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|rightName
operator|!=
literal|null
operator|)
return|;
block|}
if|if
condition|(
name|left
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|)
operator|&&
name|right
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|LITERAL
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|RexCall
operator|)
name|left
operator|)
operator|.
name|getOperator
argument_list|()
operator|!=
name|SqlStdOperatorTable
operator|.
name|ITEM
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Should be ITEM
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|LogicalFilter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|GeodeTableScan
name|scan
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|filter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|contains
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
condition|)
block|{
specifier|final
name|RelNode
name|converted
init|=
name|convert
argument_list|(
name|filter
argument_list|,
name|scan
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|converted
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|RelNode
name|convert
parameter_list|(
name|LogicalFilter
name|filter
parameter_list|,
name|GeodeTableScan
name|scan
parameter_list|)
block|{
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|filter
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|GeodeRel
operator|.
name|CONVENTION
argument_list|)
decl_stmt|;
return|return
operator|new
name|GeodeFilter
argument_list|(
name|filter
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|convert
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
argument_list|,
name|GeodeRel
operator|.
name|CONVENTION
argument_list|)
argument_list|,
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Base class for planner rules that convert a relational    * expression to Geode calling convention.    */
specifier|abstract
specifier|static
class|class
name|GeodeConverterRule
extends|extends
name|ConverterRule
block|{
specifier|protected
specifier|final
name|Convention
name|out
decl_stmt|;
name|GeodeConverterRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|clazz
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|clazz
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|GeodeRel
operator|.
name|CONVENTION
argument_list|,
name|description
argument_list|)
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|GeodeRel
operator|.
name|CONVENTION
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End GeodeRules.java
end_comment

end_unit

