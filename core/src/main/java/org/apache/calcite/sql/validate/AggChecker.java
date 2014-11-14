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
name|SqlUtil
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
name|Stacks
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
name|Lists
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
name|util
operator|.
name|Static
operator|.
name|RESOURCE
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
name|List
argument_list|<
name|SqlValidatorScope
argument_list|>
name|scopes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
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
name|groupExprs
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|groupExprs
argument_list|)
expr_stmt|;
name|this
operator|.
name|distinct
operator|=
name|distinct
expr_stmt|;
name|Stacks
operator|.
name|push
argument_list|(
name|this
operator|.
name|scopes
argument_list|,
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
literal|false
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
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// If it '*' or 'foo.*'?
if|if
condition|(
name|id
operator|.
name|isStar
argument_list|()
condition|)
block|{
assert|assert
literal|false
operator|:
literal|"star should have been expanded"
assert|;
block|}
comment|// Is it a call to a parentheses-free function?
name|SqlCall
name|call
init|=
name|SqlUtil
operator|.
name|makeCall
argument_list|(
name|validator
operator|.
name|getOperatorTable
argument_list|()
argument_list|,
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
name|SqlIdentifier
name|fqId
init|=
name|Stacks
operator|.
name|peek
argument_list|(
name|scopes
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
specifier|public
name|Void
name|visit
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
if|if
condition|(
name|distinct
condition|)
block|{
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
name|oldScope
init|=
name|Stacks
operator|.
name|peek
argument_list|(
name|scopes
argument_list|)
decl_stmt|;
name|SqlValidatorScope
name|newScope
init|=
name|oldScope
operator|.
name|getOperandScope
argument_list|(
name|call
argument_list|)
decl_stmt|;
name|Stacks
operator|.
name|push
argument_list|(
name|scopes
argument_list|,
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
expr|<
name|Void
operator|>
name|instance
argument_list|()
argument_list|)
expr_stmt|;
comment|// Restore scope.
name|Stacks
operator|.
name|pop
argument_list|(
name|scopes
argument_list|,
name|newScope
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggChecker.java
end_comment

end_unit

