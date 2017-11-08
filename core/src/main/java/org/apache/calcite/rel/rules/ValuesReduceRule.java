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
name|RelOptPredicateList
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
name|RelOptRuleOperand
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
name|RelFactories
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
name|Values
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
name|logical
operator|.
name|LogicalValues
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
name|RexBuilder
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
name|RexShuttle
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
name|RexUtil
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Util
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
name|util
operator|.
name|trace
operator|.
name|CalciteTrace
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
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
comment|/**  * Planner rule that folds projections and filters into an underlying  * {@link org.apache.calcite.rel.logical.LogicalValues}.  *  *<p>Returns a simplified {@code Values}, perhaps containing zero tuples  * if all rows are filtered away.  *  *<p>For example,</p>  *  *<blockquote><code>select a - b from (values (1, 2), (3, 5), (7, 11)) as t (a,  * b) where a + b&gt; 4</code></blockquote>  *  *<p>becomes</p>  *  *<blockquote><code>select x from (values (-2), (-4))</code></blockquote>  *  *<p>Ignores an empty {@code Values}; this is better dealt with by  * {@link PruneEmptyRules}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ValuesReduceRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
comment|/**    * Instance of this rule that applies to the pattern    * Filter(Values).    */
specifier|public
specifier|static
specifier|final
name|ValuesReduceRule
name|FILTER_INSTANCE
init|=
operator|new
name|ValuesReduceRule
argument_list|(
name|operand
argument_list|(
name|LogicalFilter
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalValues
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|.
name|IS_NOT_EMPTY
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"ValuesReduceRule(Filter)"
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
name|LogicalValues
name|values
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|apply
argument_list|(
name|call
argument_list|,
literal|null
argument_list|,
name|filter
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Instance of this rule that applies to the pattern    * Project(Values).    */
specifier|public
specifier|static
specifier|final
name|ValuesReduceRule
name|PROJECT_INSTANCE
init|=
operator|new
name|ValuesReduceRule
argument_list|(
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalValues
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|.
name|IS_NOT_EMPTY
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"ValuesReduceRule(Project)"
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
name|LogicalValues
name|values
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|apply
argument_list|(
name|call
argument_list|,
name|project
argument_list|,
literal|null
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Singleton instance of this rule that applies to the pattern    * Project(Filter(Values)).    */
specifier|public
specifier|static
specifier|final
name|ValuesReduceRule
name|PROJECT_FILTER_INSTANCE
init|=
operator|new
name|ValuesReduceRule
argument_list|(
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalFilter
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|LogicalValues
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|Values
operator|.
name|IS_NOT_EMPTY
argument_list|,
name|none
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|,
literal|"ValuesReduceRule(Project-Filter)"
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
name|LogicalFilter
name|filter
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|LogicalValues
name|values
init|=
name|call
operator|.
name|rel
argument_list|(
literal|2
argument_list|)
decl_stmt|;
name|apply
argument_list|(
name|call
argument_list|,
name|project
argument_list|,
name|filter
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a ValuesReduceRule.    *    * @param operand           Class of rels to which this rule should apply    * @param relBuilderFactory Builder for relational expressions    * @param desc              Description, or null to guess description    */
specifier|public
name|ValuesReduceRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|desc
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|relBuilderFactory
argument_list|,
name|desc
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|LOGGER
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Does the work.    *    * @param call    Rule call    * @param project Project, may be null    * @param filter  Filter, may be null    * @param values  Values rel to be reduced    */
specifier|protected
name|void
name|apply
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|,
name|LogicalProject
name|project
parameter_list|,
name|LogicalFilter
name|filter
parameter_list|,
name|LogicalValues
name|values
parameter_list|)
block|{
assert|assert
name|values
operator|!=
literal|null
assert|;
assert|assert
name|filter
operator|!=
literal|null
operator|||
name|project
operator|!=
literal|null
assert|;
specifier|final
name|RexNode
name|conditionExpr
init|=
operator|(
name|filter
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|filter
operator|.
name|getCondition
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projectExprs
init|=
operator|(
name|project
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|project
operator|.
name|getProjects
argument_list|()
decl_stmt|;
name|RexBuilder
name|rexBuilder
init|=
name|values
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
comment|// Find reducible expressions.
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|reducibleExps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|MyRexShuttle
name|shuttle
init|=
operator|new
name|MyRexShuttle
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|literalList
range|:
name|values
operator|.
name|getTuples
argument_list|()
control|)
block|{
name|shuttle
operator|.
name|literalList
operator|=
name|literalList
expr_stmt|;
if|if
condition|(
name|conditionExpr
operator|!=
literal|null
condition|)
block|{
name|RexNode
name|c
init|=
name|conditionExpr
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
decl_stmt|;
name|reducibleExps
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|projectExprs
operator|!=
literal|null
condition|)
block|{
name|int
name|k
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|RexNode
name|projectExpr
range|:
name|projectExprs
control|)
block|{
operator|++
name|k
expr_stmt|;
name|RexNode
name|e
init|=
name|projectExpr
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
decl_stmt|;
if|if
condition|(
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|e
argument_list|)
condition|)
block|{
name|e
operator|=
name|rexBuilder
operator|.
name|makeAbstractCast
argument_list|(
name|project
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|k
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|reducibleExps
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|int
name|fieldsPerRow
init|=
operator|(
operator|(
name|conditionExpr
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
literal|1
operator|)
operator|+
operator|(
operator|(
name|projectExprs
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|projectExprs
operator|.
name|size
argument_list|()
operator|)
decl_stmt|;
assert|assert
name|fieldsPerRow
operator|>
literal|0
assert|;
assert|assert
name|reducibleExps
operator|.
name|size
argument_list|()
operator|==
operator|(
name|values
operator|.
name|getTuples
argument_list|()
operator|.
name|size
argument_list|()
operator|*
name|fieldsPerRow
operator|)
assert|;
comment|// Compute the values they reduce to.
specifier|final
name|RelOptPredicateList
name|predicates
init|=
name|RelOptPredicateList
operator|.
name|EMPTY
decl_stmt|;
name|ReduceExpressionsRule
operator|.
name|reduceExpressions
argument_list|(
name|values
argument_list|,
name|reducibleExps
argument_list|,
name|predicates
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|int
name|changeCount
init|=
literal|0
decl_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuplesBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|row
init|=
literal|0
init|;
name|row
operator|<
name|values
operator|.
name|getTuples
argument_list|()
operator|.
name|size
argument_list|()
condition|;
operator|++
name|row
control|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|RexNode
name|reducedValue
decl_stmt|;
if|if
condition|(
name|conditionExpr
operator|!=
literal|null
condition|)
block|{
name|reducedValue
operator|=
name|reducibleExps
operator|.
name|get
argument_list|(
operator|(
name|row
operator|*
name|fieldsPerRow
operator|)
operator|+
name|i
argument_list|)
expr_stmt|;
operator|++
name|i
expr_stmt|;
if|if
condition|(
operator|!
name|reducedValue
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
operator|++
name|changeCount
expr_stmt|;
continue|continue;
block|}
block|}
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|valuesList
decl_stmt|;
if|if
condition|(
name|projectExprs
operator|!=
literal|null
condition|)
block|{
operator|++
name|changeCount
expr_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexLiteral
argument_list|>
name|tupleBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|fieldsPerRow
condition|;
operator|++
name|i
control|)
block|{
name|reducedValue
operator|=
name|reducibleExps
operator|.
name|get
argument_list|(
operator|(
name|row
operator|*
name|fieldsPerRow
operator|)
operator|+
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|reducedValue
operator|instanceof
name|RexLiteral
condition|)
block|{
name|tupleBuilder
operator|.
name|add
argument_list|(
operator|(
name|RexLiteral
operator|)
name|reducedValue
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|RexUtil
operator|.
name|isNullLiteral
argument_list|(
name|reducedValue
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|tupleBuilder
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|constantNull
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
block|}
name|valuesList
operator|=
name|tupleBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|valuesList
operator|=
name|values
operator|.
name|getTuples
argument_list|()
operator|.
name|get
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|tuplesBuilder
operator|.
name|add
argument_list|(
name|valuesList
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|changeCount
operator|>
literal|0
condition|)
block|{
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
if|if
condition|(
name|projectExprs
operator|!=
literal|null
condition|)
block|{
name|rowType
operator|=
name|project
operator|.
name|getRowType
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rowType
operator|=
name|values
operator|.
name|getRowType
argument_list|()
expr_stmt|;
block|}
specifier|final
name|RelNode
name|newRel
init|=
name|LogicalValues
operator|.
name|create
argument_list|(
name|values
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rowType
argument_list|,
name|tuplesBuilder
operator|.
name|build
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
else|else
block|{
comment|// Filter had no effect, so we can say that Filter(Values) ==
comment|// Values.
name|call
operator|.
name|transformTo
argument_list|(
name|values
argument_list|)
expr_stmt|;
block|}
comment|// New plan is absolutely better than old plan. (Moreover, if
comment|// changeCount == 0, we've proved that the filter was trivial, and that
comment|// can send the volcano planner into a loop; see dtbug 2070.)
if|if
condition|(
name|filter
operator|!=
literal|null
condition|)
block|{
name|call
operator|.
name|getPlanner
argument_list|()
operator|.
name|setImportance
argument_list|(
name|filter
argument_list|,
literal|0.0
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/** Shuttle that converts inputs to literals. */
specifier|private
specifier|static
class|class
name|MyRexShuttle
extends|extends
name|RexShuttle
block|{
specifier|private
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|literalList
decl_stmt|;
specifier|public
name|RexNode
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
return|return
name|literalList
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
block|}
block|}
end_class

begin_comment
comment|// End ValuesReduceRule.java
end_comment

end_unit

