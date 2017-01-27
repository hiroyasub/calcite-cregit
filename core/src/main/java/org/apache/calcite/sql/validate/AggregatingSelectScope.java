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
name|sql
operator|.
name|validate
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
name|linq4j
operator|.
name|Linq4j
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
name|linq4j
operator|.
name|Ord
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
name|sql
operator|.
name|SqlCall
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
name|SqlNode
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
name|SqlNodeList
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
name|SqlSelect
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
name|ImmutableBitSet
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
name|Litmus
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
name|Pair
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
name|base
operator|.
name|Supplier
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
name|base
operator|.
name|Suppliers
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Sets
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlUtil
operator|.
name|stripAs
import|;
end_import

begin_comment
comment|/**  * Scope for resolving identifiers within a SELECT statement that has a  * GROUP BY clause.  *  *<p>The same set of identifiers are in scope, but it won't allow access to  * identifiers or expressions which are not group-expressions.  */
end_comment

begin_class
specifier|public
class|class
name|AggregatingSelectScope
extends|extends
name|DelegatingScope
implements|implements
name|AggregatingScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlSelect
name|select
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|distinct
decl_stmt|;
comment|/** Use while under construction. */
specifier|private
name|List
argument_list|<
name|SqlNode
argument_list|>
name|temporaryGroupExprList
decl_stmt|;
specifier|public
specifier|final
name|Supplier
argument_list|<
name|Resolved
argument_list|>
name|resolved
init|=
name|Suppliers
operator|.
name|memoize
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Resolved
argument_list|>
argument_list|()
block|{
specifier|public
name|Resolved
name|get
parameter_list|()
block|{
assert|assert
name|temporaryGroupExprList
operator|==
literal|null
assert|;
name|temporaryGroupExprList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
try|try
block|{
return|return
name|resolve
argument_list|()
return|;
block|}
finally|finally
block|{
name|temporaryGroupExprList
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AggregatingSelectScope    *    * @param selectScope Parent scope    * @param select      Enclosing SELECT node    * @param distinct    Whether SELECT is DISTINCT    */
name|AggregatingSelectScope
parameter_list|(
name|SqlValidatorScope
name|selectScope
parameter_list|,
name|SqlSelect
name|select
parameter_list|,
name|boolean
name|distinct
parameter_list|)
block|{
comment|// The select scope is the parent in the sense that all columns which
comment|// are available in the select scope are available. Whether they are
comment|// valid as aggregation expressions... now that's a different matter.
name|super
argument_list|(
name|selectScope
argument_list|)
expr_stmt|;
name|this
operator|.
name|select
operator|=
name|select
expr_stmt|;
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|private
name|Resolved
name|resolve
parameter_list|()
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|SqlNode
argument_list|>
name|extraExprs
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|groupExprProjection
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
if|if
condition|(
name|select
operator|.
name|getGroup
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|SqlNodeList
name|groupList
init|=
name|select
operator|.
name|getGroup
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidatorUtil
operator|.
name|GroupAnalyzer
name|groupAnalyzer
init|=
operator|new
name|SqlValidatorUtil
operator|.
name|GroupAnalyzer
argument_list|(
name|temporaryGroupExprList
argument_list|)
decl_stmt|;
for|for
control|(
name|SqlNode
name|groupExpr
range|:
name|groupList
control|)
block|{
name|SqlValidatorUtil
operator|.
name|analyzeGroupItem
argument_list|(
name|this
argument_list|,
name|groupAnalyzer
argument_list|,
name|builder
argument_list|,
name|groupExpr
argument_list|)
expr_stmt|;
block|}
name|extraExprs
operator|=
name|groupAnalyzer
operator|.
name|extraExprs
expr_stmt|;
name|groupExprProjection
operator|=
name|groupAnalyzer
operator|.
name|groupExprProjection
expr_stmt|;
block|}
specifier|final
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|flatGroupSets
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|(
name|ImmutableBitSet
operator|.
name|COMPARATOR
argument_list|)
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSet
range|:
name|Linq4j
operator|.
name|product
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|)
control|)
block|{
name|flatGroupSets
operator|.
name|add
argument_list|(
name|ImmutableBitSet
operator|.
name|union
argument_list|(
name|groupSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// For GROUP BY (), we need a singleton grouping set.
if|if
condition|(
name|flatGroupSets
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|flatGroupSets
operator|.
name|add
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Resolved
argument_list|(
name|extraExprs
argument_list|,
name|temporaryGroupExprList
argument_list|,
name|flatGroupSets
argument_list|,
name|groupExprProjection
argument_list|)
return|;
block|}
comment|/**    * Returns the expressions that are in the GROUP BY clause (or the SELECT    * DISTINCT clause, if distinct) and that can therefore be referenced    * without being wrapped in aggregate functions.    *    *<p>The expressions are fully-qualified, and any "*" in select clauses are    * expanded.    *    * @return list of grouping expressions    */
specifier|private
name|Pair
argument_list|<
name|ImmutableList
argument_list|<
name|SqlNode
argument_list|>
argument_list|,
name|ImmutableList
argument_list|<
name|SqlNode
argument_list|>
argument_list|>
name|getGroupExprs
parameter_list|()
block|{
if|if
condition|(
name|distinct
condition|)
block|{
comment|// Cannot compute this in the constructor: select list has not been
comment|// expanded yet.
assert|assert
name|select
operator|.
name|isDistinct
argument_list|()
assert|;
comment|// Remove the AS operator so the expressions are consistent with
comment|// OrderExpressionExpander.
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|SqlNode
argument_list|>
name|groupExprs
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|SelectScope
name|selectScope
init|=
operator|(
name|SelectScope
operator|)
name|parent
decl_stmt|;
for|for
control|(
name|SqlNode
name|selectItem
range|:
name|selectScope
operator|.
name|getExpandedSelectList
argument_list|()
control|)
block|{
name|groupExprs
operator|.
name|add
argument_list|(
name|stripAs
argument_list|(
name|selectItem
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Pair
operator|.
name|of
argument_list|(
name|ImmutableList
operator|.
expr|<
name|SqlNode
operator|>
name|of
argument_list|()
argument_list|,
name|groupExprs
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|select
operator|.
name|getGroup
argument_list|()
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|temporaryGroupExprList
operator|!=
literal|null
condition|)
block|{
comment|// we are in the middle of resolving
return|return
name|Pair
operator|.
name|of
argument_list|(
name|ImmutableList
operator|.
expr|<
name|SqlNode
operator|>
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|temporaryGroupExprList
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|Resolved
name|resolved
init|=
name|this
operator|.
name|resolved
operator|.
name|get
argument_list|()
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|resolved
operator|.
name|extraExprList
argument_list|,
name|resolved
operator|.
name|groupExprList
argument_list|)
return|;
block|}
block|}
else|else
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|ImmutableList
operator|.
expr|<
name|SqlNode
operator|>
name|of
argument_list|()
argument_list|,
name|ImmutableList
operator|.
expr|<
name|SqlNode
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|select
return|;
block|}
specifier|private
specifier|static
name|boolean
name|allContain
parameter_list|(
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|bitSets
parameter_list|,
name|int
name|bit
parameter_list|)
block|{
for|for
control|(
name|ImmutableBitSet
name|bitSet
range|:
name|bitSets
control|)
block|{
if|if
condition|(
operator|!
name|bitSet
operator|.
name|get
argument_list|(
name|bit
argument_list|)
condition|)
block|{
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
name|RelDataType
name|nullifyType
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
specifier|final
name|Resolved
name|r
init|=
name|this
operator|.
name|resolved
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|SqlNode
argument_list|>
name|groupExpr
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|r
operator|.
name|groupExprList
argument_list|)
control|)
block|{
if|if
condition|(
name|groupExpr
operator|.
name|e
operator|.
name|equalsDeep
argument_list|(
name|node
argument_list|,
name|Litmus
operator|.
name|IGNORE
argument_list|)
condition|)
block|{
if|if
condition|(
name|r
operator|.
name|isNullable
argument_list|(
name|groupExpr
operator|.
name|i
argument_list|)
condition|)
block|{
return|return
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|type
return|;
block|}
specifier|public
name|SqlValidatorScope
name|getOperandScope
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|isAggregator
argument_list|()
condition|)
block|{
comment|// If we're the 'SUM' node in 'select a + sum(b + c) from t
comment|// group by a', then we should validate our arguments in
comment|// the non-aggregating scope, where 'b' and 'c' are valid
comment|// column references.
return|return
name|parent
return|;
block|}
else|else
block|{
comment|// Check whether expression is constant within the group.
comment|//
comment|// If not, throws. Example, 'empno' in
comment|//    SELECT empno FROM emp GROUP BY deptno
comment|//
comment|// If it perfectly matches an expression in the GROUP BY
comment|// clause, we validate its arguments in the non-aggregating
comment|// scope. Example, 'empno + 1' in
comment|//
comment|//   SELECT empno + 1 FROM emp GROUP BY empno + 1
specifier|final
name|boolean
name|matches
init|=
name|checkAggregateExpr
argument_list|(
name|call
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|matches
condition|)
block|{
return|return
name|parent
return|;
block|}
block|}
return|return
name|super
operator|.
name|getOperandScope
argument_list|(
name|call
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|checkAggregateExpr
parameter_list|(
name|SqlNode
name|expr
parameter_list|,
name|boolean
name|deep
parameter_list|)
block|{
comment|// Fully-qualify any identifiers in expr.
if|if
condition|(
name|deep
condition|)
block|{
name|expr
operator|=
name|validator
operator|.
name|expand
argument_list|(
name|expr
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Make sure expression is valid, throws if not.
name|Pair
argument_list|<
name|ImmutableList
argument_list|<
name|SqlNode
argument_list|>
argument_list|,
name|ImmutableList
argument_list|<
name|SqlNode
argument_list|>
argument_list|>
name|pair
init|=
name|getGroupExprs
argument_list|()
decl_stmt|;
specifier|final
name|AggChecker
name|aggChecker
init|=
operator|new
name|AggChecker
argument_list|(
name|validator
argument_list|,
name|this
argument_list|,
name|pair
operator|.
name|left
argument_list|,
name|pair
operator|.
name|right
argument_list|,
name|distinct
argument_list|)
decl_stmt|;
if|if
condition|(
name|deep
condition|)
block|{
name|expr
operator|.
name|accept
argument_list|(
name|aggChecker
argument_list|)
expr_stmt|;
block|}
comment|// Return whether expression exactly matches one of the group
comment|// expressions.
return|return
name|aggChecker
operator|.
name|isGroupExpr
argument_list|(
name|expr
argument_list|)
return|;
block|}
specifier|public
name|void
name|validateExpr
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
block|{
name|checkAggregateExpr
argument_list|(
name|expr
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** Information about an aggregating scope that can only be determined    * after validation has occurred. Therefore it cannot be populated when    * the scope is created. */
specifier|public
class|class
name|Resolved
block|{
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|SqlNode
argument_list|>
name|extraExprList
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|SqlNode
argument_list|>
name|groupExprList
decl_stmt|;
specifier|public
specifier|final
name|ImmutableBitSet
name|groupSet
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|indicator
decl_stmt|;
specifier|public
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|groupExprProjection
decl_stmt|;
name|Resolved
parameter_list|(
name|List
argument_list|<
name|SqlNode
argument_list|>
name|extraExprList
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|groupExprList
parameter_list|,
name|Iterable
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|groupExprProjection
parameter_list|)
block|{
name|this
operator|.
name|extraExprList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|extraExprList
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupExprList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|groupExprList
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupSet
operator|=
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|groupExprList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupSets
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|groupSets
argument_list|)
expr_stmt|;
name|this
operator|.
name|indicator
operator|=
operator|!
name|this
operator|.
name|groupSets
operator|.
name|equals
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|groupSet
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|groupExprProjection
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|groupExprProjection
argument_list|)
expr_stmt|;
block|}
comment|/** Returns whether a field should be nullable due to grouping sets. */
specifier|public
name|boolean
name|isNullable
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|i
operator|<
name|groupExprList
operator|.
name|size
argument_list|()
operator|&&
operator|!
name|allContain
argument_list|(
name|groupSets
argument_list|,
name|i
argument_list|)
return|;
block|}
comment|/** Returns whether a given expression is equal to one of the grouping      * expressions. Determines whether it is valid as an operand to GROUPING. */
specifier|public
name|boolean
name|isGroupingExpr
parameter_list|(
name|SqlNode
name|operand
parameter_list|)
block|{
return|return
name|lookupGroupingExpr
argument_list|(
name|operand
argument_list|)
operator|>=
literal|0
return|;
block|}
specifier|public
name|int
name|lookupGroupingExpr
parameter_list|(
name|SqlNode
name|operand
parameter_list|)
block|{
for|for
control|(
name|Ord
argument_list|<
name|SqlNode
argument_list|>
name|groupExpr
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|groupExprList
argument_list|)
control|)
block|{
if|if
condition|(
name|operand
operator|.
name|equalsDeep
argument_list|(
name|groupExpr
operator|.
name|e
argument_list|,
name|Litmus
operator|.
name|IGNORE
argument_list|)
condition|)
block|{
return|return
name|groupExpr
operator|.
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End AggregatingSelectScope.java
end_comment

end_unit

