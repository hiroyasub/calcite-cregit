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
name|SqlIdentifier
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
name|sql
operator|.
name|SqlWindow
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
name|util
operator|.
name|SqlBasicVisitor
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
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
import|import static
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
name|SqlNonNullableAccessors
operator|.
name|getSelectList
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
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Visitor which throws an exception if any component of the expression is not a  * group expression.  */
end_comment

begin_class
class|class
name|AggChecker
extends|extends
name|SqlBasicVisitor
argument_list|<
name|Void
argument_list|>
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Deque
argument_list|<
name|SqlValidatorScope
argument_list|>
name|scopes
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|extraExprs
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|groupExprs
decl_stmt|;
specifier|private
name|boolean
name|distinct
decl_stmt|;
specifier|private
name|SqlValidatorImpl
name|validator
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AggChecker.    *    * @param validator  Validator    * @param scope      Scope    * @param groupExprs Expressions in GROUP BY (or SELECT DISTINCT) clause,    *                   that are therefore available    * @param distinct   Whether aggregation checking is because of a SELECT    *                   DISTINCT clause    */
name|AggChecker
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|AggregatingScope
name|scope
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|extraExprs
parameter_list|,
name|List
argument_list|<
name|SqlNode
argument_list|>
name|groupExprs
parameter_list|,
name|boolean
name|distinct
parameter_list|)
block|{
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
name|this
operator|.
name|extraExprs
operator|=
name|extraExprs
expr_stmt|;
name|this
operator|.
name|groupExprs
operator|=
name|groupExprs
expr_stmt|;
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
name|this
operator|.
name|scopes
operator|.
name|push
argument_list|(
name|scope
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
name|boolean
name|isGroupExpr
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|groupExpr
range|:
name|groupExprs
control|)
block|{
if|if
condition|(
name|groupExpr
operator|.
name|equalsDeep
argument_list|(
name|expr
argument_list|,
name|Litmus
operator|.
name|IGNORE
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
for|for
control|(
name|SqlNode
name|extraExpr
range|:
name|extraExprs
control|)
block|{
if|if
condition|(
name|extraExpr
operator|.
name|equalsDeep
argument_list|(
name|expr
argument_list|,
name|Litmus
operator|.
name|IGNORE
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|visit
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|)
block|{
if|if
condition|(
name|isGroupExpr
argument_list|(
name|id
argument_list|)
operator|||
name|id
operator|.
name|isStar
argument_list|()
condition|)
block|{
comment|// Star may validly occur in "SELECT COUNT(*) OVER w"
return|return
literal|null
return|;
block|}
comment|// Is it a call to a parentheses-free function?
specifier|final
name|SqlCall
name|call
init|=
name|validator
operator|.
name|makeNullaryCall
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|!=
literal|null
condition|)
block|{
return|return
name|call
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|// Didn't find the identifier in the group-by list as is, now find
comment|// it fully-qualified.
comment|// TODO: It would be better if we always compared fully-qualified
comment|// to fully-qualified.
specifier|final
name|SqlQualified
name|fqId
init|=
name|requireNonNull
argument_list|(
name|scopes
operator|.
name|peek
argument_list|()
argument_list|,
literal|"scopes.peek()"
argument_list|)
operator|.
name|fullyQualify
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|isGroupExpr
argument_list|(
name|fqId
operator|.
name|identifier
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SqlNode
name|originalExpr
init|=
name|validator
operator|.
name|getOriginal
argument_list|(
name|id
argument_list|)
decl_stmt|;
specifier|final
name|String
name|exprString
init|=
name|originalExpr
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|originalExpr
argument_list|,
name|distinct
condition|?
name|RESOURCE
operator|.
name|notSelectDistinctExpr
argument_list|(
name|exprString
argument_list|)
else|:
name|RESOURCE
operator|.
name|notGroupExpr
argument_list|(
name|exprString
argument_list|)
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|visit
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
specifier|final
name|SqlValidatorScope
name|scope
init|=
name|scopes
operator|.
name|peek
argument_list|()
decl_stmt|;
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
if|if
condition|(
name|distinct
condition|)
block|{
if|if
condition|(
name|scope
operator|instanceof
name|AggregatingSelectScope
condition|)
block|{
name|SqlNodeList
name|selectList
init|=
name|getSelectList
argument_list|(
operator|(
name|SqlSelect
operator|)
name|scope
operator|.
name|getNode
argument_list|()
argument_list|)
decl_stmt|;
comment|// Check if this aggregation function is just an element in the select
for|for
control|(
name|SqlNode
name|sqlNode
range|:
name|selectList
control|)
block|{
if|if
condition|(
name|sqlNode
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|AS
condition|)
block|{
name|sqlNode
operator|=
operator|(
operator|(
name|SqlCall
operator|)
name|sqlNode
operator|)
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|validator
operator|.
name|expand
argument_list|(
name|sqlNode
argument_list|,
name|scope
argument_list|)
operator|.
name|equalsDeep
argument_list|(
name|call
argument_list|,
name|Litmus
operator|.
name|IGNORE
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|// Cannot use agg fun in ORDER BY clause if have SELECT DISTINCT.
name|SqlNode
name|originalExpr
init|=
name|validator
operator|.
name|getOriginal
argument_list|(
name|call
argument_list|)
decl_stmt|;
specifier|final
name|String
name|exprString
init|=
name|originalExpr
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|RESOURCE
operator|.
name|notSelectDistinctExpr
argument_list|(
name|exprString
argument_list|)
argument_list|)
throw|;
block|}
comment|// For example, 'sum(sal)' in 'SELECT sum(sal) FROM emp GROUP
comment|// BY deptno'
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|FILTER
case|:
case|case
name|WITHIN_GROUP
case|:
case|case
name|RESPECT_NULLS
case|:
case|case
name|IGNORE_NULLS
case|:
case|case
name|WITHIN_DISTINCT
case|:
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
default|default:
break|break;
block|}
comment|// Visit the operand in window function
if|if
condition|(
name|call
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|OVER
condition|)
block|{
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
expr|<
name|SqlCall
operator|>
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|getOperandList
argument_list|()
control|)
block|{
name|operand
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|// Check the OVER clause
specifier|final
name|SqlNode
name|over
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|over
operator|instanceof
name|SqlCall
condition|)
block|{
name|over
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|over
operator|instanceof
name|SqlIdentifier
condition|)
block|{
comment|// Check the corresponding SqlWindow in WINDOW clause
specifier|final
name|SqlWindow
name|window
init|=
name|requireNonNull
argument_list|(
name|scope
argument_list|,
parameter_list|()
lambda|->
literal|"scope for "
operator|+
name|call
argument_list|)
operator|.
name|lookupWindow
argument_list|(
operator|(
operator|(
name|SqlIdentifier
operator|)
name|over
operator|)
operator|.
name|getSimple
argument_list|()
argument_list|)
decl_stmt|;
name|requireNonNull
argument_list|(
name|window
argument_list|,
parameter_list|()
lambda|->
literal|"window for "
operator|+
name|call
argument_list|)
expr_stmt|;
name|window
operator|.
name|getPartitionList
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|window
operator|.
name|getOrderList
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|isGroupExpr
argument_list|(
name|call
argument_list|)
condition|)
block|{
comment|// This call matches an expression in the GROUP BY clause.
return|return
literal|null
return|;
block|}
specifier|final
name|SqlCall
name|groupCall
init|=
name|SqlStdOperatorTable
operator|.
name|convertAuxiliaryToGroupCall
argument_list|(
name|call
argument_list|)
decl_stmt|;
if|if
condition|(
name|groupCall
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|isGroupExpr
argument_list|(
name|groupCall
argument_list|)
condition|)
block|{
comment|// This call is an auxiliary function that matches a group call in the
comment|// GROUP BY clause.
comment|//
comment|// For example TUMBLE_START is an auxiliary of the TUMBLE
comment|// group function, and
comment|//   TUMBLE_START(rowtime, INTERVAL '1' HOUR)
comment|// matches
comment|//   TUMBLE(rowtime, INTERVAL '1' HOUR')
return|return
literal|null
return|;
block|}
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|groupCall
argument_list|,
name|RESOURCE
operator|.
name|auxiliaryWithoutMatchingGroupCall
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|groupCall
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|call
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|QUERY
argument_list|)
condition|)
block|{
comment|// Allow queries for now, even though they may contain
comment|// references to forbidden columns.
return|return
literal|null
return|;
block|}
comment|// Switch to new scope.
name|SqlValidatorScope
name|newScope
init|=
name|requireNonNull
argument_list|(
name|scope
argument_list|,
parameter_list|()
lambda|->
literal|"scope for "
operator|+
name|call
argument_list|)
operator|.
name|getOperandScope
argument_list|(
name|call
argument_list|)
decl_stmt|;
name|scopes
operator|.
name|push
argument_list|(
name|newScope
argument_list|)
expr_stmt|;
comment|// Visit the operands (only expressions).
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|acceptCall
argument_list|(
name|this
argument_list|,
name|call
argument_list|,
literal|true
argument_list|,
name|ArgHandlerImpl
operator|.
name|instance
argument_list|()
argument_list|)
expr_stmt|;
comment|// Restore scope.
name|scopes
operator|.
name|pop
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

