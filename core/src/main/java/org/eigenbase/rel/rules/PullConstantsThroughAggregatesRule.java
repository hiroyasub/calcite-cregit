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
name|util
operator|.
name|mapping
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
name|util
operator|.
name|BitSets
import|;
end_import

begin_comment
comment|/**  * PullConstantsThroughAggregatesRule removes constant expressions from the  * group list of an {@link AggregateRel}.  *  *<h4>Effect of the rule</h4>  *  *<p>Since the transformed relational expression has to match the original  * relational expression, the constants are placed in a projection above the  * reduced aggregate. If those constants are not used, another rule will remove  * them from the project.  *  *<p>AggregateRel needs its group columns to be on the prefix of its input  * relational expression. Therefore, if a constant is not on the trailing edge  * of the group list, removing it will leave a hole. In this case, the rule adds  * a project before the aggregate to reorder the columns, and permutes them back  * afterwards.  */
end_comment

begin_class
specifier|public
class|class
name|PullConstantsThroughAggregatesRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** The singleton. */
specifier|public
specifier|static
specifier|final
name|PullConstantsThroughAggregatesRule
name|INSTANCE
init|=
operator|new
name|PullConstantsThroughAggregatesRule
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Private: use singleton    */
specifier|private
name|PullConstantsThroughAggregatesRule
parameter_list|()
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|AggregateRel
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|CalcRel
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|AggregateRel
name|aggregate
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|CalcRel
name|child
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RexProgram
name|program
init|=
name|child
operator|.
name|getProgram
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|childRowType
init|=
name|child
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|int
name|groupCount
init|=
name|aggregate
operator|.
name|getGroupSet
argument_list|()
operator|.
name|cardinality
argument_list|()
decl_stmt|;
name|IntList
name|constantList
init|=
operator|new
name|IntList
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|RexNode
argument_list|>
name|constants
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|RexNode
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|BitSets
operator|.
name|toIter
argument_list|(
name|aggregate
operator|.
name|getGroupSet
argument_list|()
argument_list|)
control|)
block|{
specifier|final
name|RexLocalRef
name|ref
init|=
name|program
operator|.
name|getProjectList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|program
operator|.
name|isConstant
argument_list|(
name|ref
argument_list|)
condition|)
block|{
name|constantList
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|constants
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|program
operator|.
name|gatherExpr
argument_list|(
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// None of the group expressions are constant. Nothing to do.
if|if
condition|(
name|constantList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
specifier|final
name|int
name|newGroupCount
init|=
name|groupCount
operator|-
name|constantList
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|newAggregate
decl_stmt|;
comment|// If the constants are on the trailing edge of the group list, we just
comment|// reduce the group count.
if|if
condition|(
name|constantList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|==
name|newGroupCount
condition|)
block|{
comment|// Clone aggregate calls.
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newAggCalls
init|=
operator|new
name|ArrayList
argument_list|<
name|AggregateCall
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
name|newAggCalls
operator|.
name|add
argument_list|(
operator|new
name|AggregateCall
argument_list|(
name|aggCall
operator|.
name|getAggregation
argument_list|()
argument_list|,
name|aggCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|aggCall
operator|.
name|getArgList
argument_list|()
argument_list|,
name|aggCall
operator|.
name|getType
argument_list|()
argument_list|,
name|aggCall
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|newAggregate
operator|=
operator|new
name|AggregateRel
argument_list|(
name|aggregate
operator|.
name|getCluster
argument_list|()
argument_list|,
name|child
argument_list|,
name|BitSets
operator|.
name|range
argument_list|(
name|newGroupCount
argument_list|)
argument_list|,
name|newAggCalls
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Create the mapping from old field positions to new field
comment|// positions.
specifier|final
name|Permutation
name|mapping
init|=
operator|new
name|Permutation
argument_list|(
name|childRowType
operator|.
name|getFieldCount
argument_list|()
argument_list|)
decl_stmt|;
name|mapping
operator|.
name|identity
argument_list|()
expr_stmt|;
comment|// Ensure that the first positions in the mapping are for the new
comment|// group columns.
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|groupOrdinal
init|=
literal|0
init|,
name|constOrdinal
init|=
name|newGroupCount
init|;
name|i
operator|<
name|groupCount
condition|;
operator|++
name|i
control|)
block|{
if|if
condition|(
name|i
operator|>=
name|groupCount
condition|)
block|{
name|mapping
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|constants
operator|.
name|containsKey
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|mapping
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|constOrdinal
operator|++
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mapping
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|groupOrdinal
operator|++
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Create a projection to permute fields into these positions.
specifier|final
name|RelNode
name|project
init|=
name|createProjection
argument_list|(
name|mapping
argument_list|,
name|child
argument_list|)
decl_stmt|;
comment|// Adjust aggregate calls for new field positions.
specifier|final
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|newAggCalls
init|=
operator|new
name|ArrayList
argument_list|<
name|AggregateCall
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|AggregateCall
name|aggCall
range|:
name|aggregate
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
specifier|final
name|int
name|argCount
init|=
name|aggCall
operator|.
name|getArgList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|argCount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|argCount
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|Integer
name|arg
init|=
name|aggCall
operator|.
name|getArgList
argument_list|()
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|args
operator|.
name|add
argument_list|(
name|mapping
operator|.
name|getTarget
argument_list|(
name|arg
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|newAggCalls
operator|.
name|add
argument_list|(
operator|new
name|AggregateCall
argument_list|(
name|aggCall
operator|.
name|getAggregation
argument_list|()
argument_list|,
name|aggCall
operator|.
name|isDistinct
argument_list|()
argument_list|,
name|args
argument_list|,
name|aggCall
operator|.
name|getType
argument_list|()
argument_list|,
name|aggCall
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Aggregate on projection.
name|newAggregate
operator|=
operator|new
name|AggregateRel
argument_list|(
name|aggregate
operator|.
name|getCluster
argument_list|()
argument_list|,
name|project
argument_list|,
name|BitSets
operator|.
name|range
argument_list|(
name|newGroupCount
argument_list|)
argument_list|,
name|newAggCalls
argument_list|)
expr_stmt|;
block|}
comment|// Create a projection back again.
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|source
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|aggregate
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
control|)
block|{
name|RexNode
name|expr
decl_stmt|;
specifier|final
name|int
name|i
init|=
name|field
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|>=
name|groupCount
condition|)
block|{
comment|// Aggregate expressions' names and positions are unchanged.
name|expr
operator|=
name|RelOptUtil
operator|.
name|createInputRef
argument_list|(
name|newAggregate
argument_list|,
name|i
operator|-
name|constantList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|constantList
operator|.
name|contains
argument_list|(
name|i
argument_list|)
condition|)
block|{
comment|// Re-generate the constant expression in the project.
name|expr
operator|=
name|constants
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Project the aggregation expression, in its original
comment|// position.
name|expr
operator|=
name|RelOptUtil
operator|.
name|createInputRef
argument_list|(
name|newAggregate
argument_list|,
name|source
argument_list|)
expr_stmt|;
operator|++
name|source
expr_stmt|;
block|}
name|projects
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|expr
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|inverseProject
init|=
name|CalcRel
operator|.
name|createProject
argument_list|(
name|newAggregate
argument_list|,
name|projects
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|inverseProject
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a projection which permutes the fields of a given relational    * expression.    *    *<p>For example, given a relational expression [A, B, C, D] and a mapping    * [2:1, 3:0], returns a projection [$3 AS C, $2 AS B].    *    * @param mapping Mapping to apply to source columns    * @param child   Relational expression    * @return Relational expressions with permutation applied    */
specifier|private
specifier|static
name|RelNode
name|createProjection
parameter_list|(
specifier|final
name|Mapping
name|mapping
parameter_list|,
name|RelNode
name|child
parameter_list|)
block|{
comment|// Every target has precisely one source; every source has at most
comment|// one target.
assert|assert
name|mapping
operator|.
name|getMappingType
argument_list|()
operator|.
name|isA
argument_list|(
name|MappingType
operator|.
name|INVERSE_SURJECTION
argument_list|)
assert|;
specifier|final
name|RelDataType
name|childRowType
init|=
name|child
operator|.
name|getRowType
argument_list|()
decl_stmt|;
assert|assert
name|mapping
operator|.
name|getSourceCount
argument_list|()
operator|==
name|childRowType
operator|.
name|getFieldCount
argument_list|()
assert|;
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|projects
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|target
init|=
literal|0
init|;
name|target
operator|<
name|mapping
operator|.
name|getTargetCount
argument_list|()
condition|;
operator|++
name|target
control|)
block|{
name|int
name|source
init|=
name|mapping
operator|.
name|getSource
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|projects
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|RelOptUtil
operator|.
name|createInputRef
argument_list|(
name|child
argument_list|,
name|source
argument_list|)
argument_list|,
name|childRowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|source
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|CalcRel
operator|.
name|createProject
argument_list|(
name|child
argument_list|,
name|projects
argument_list|,
literal|false
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End PullConstantsThroughAggregatesRule.java
end_comment

end_unit

