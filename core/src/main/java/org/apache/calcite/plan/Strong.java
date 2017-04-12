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
name|plan
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
name|util
operator|.
name|ImmutableBitSet
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
name|Preconditions
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
name|Iterables
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
name|EnumMap
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

begin_comment
comment|/** Utilities for strong predicates.  *  *<p>A predicate is strong (or null-rejecting) if it is UNKNOWN if any of its  * inputs is UNKNOWN.</p>  *  *<p>By the way, UNKNOWN is just the boolean form of NULL.</p>  *  *<p>Examples:</p>  *<ul>  *<li>{@code UNKNOWN} is strong in [] (definitely null)  *<li>{@code c = 1} is strong in [c] (definitely null if and only if c is  *   null)  *<li>{@code c IS NULL} is not strong (always returns TRUE or FALSE, never  *   null)  *<li>{@code p1 AND p2} is strong in [p1, p2] (definitely null if either p1  *   is null or p2 is null)  *<li>{@code p1 OR p2} is strong if p1 and p2 are strong  *<li>{@code c1 = 1 OR c2 IS NULL} is strong in [c1] (definitely null if c1  *   is null)  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|Strong
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|SqlKind
argument_list|,
name|Policy
argument_list|>
name|MAP
init|=
name|createPolicyMap
argument_list|()
decl_stmt|;
specifier|public
name|Strong
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** Returns a checker that consults a bit set to find out whether particular    * inputs may be null. */
specifier|public
specifier|static
name|Strong
name|of
parameter_list|(
specifier|final
name|ImmutableBitSet
name|nullColumns
parameter_list|)
block|{
return|return
operator|new
name|Strong
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isNull
parameter_list|(
name|RexInputRef
name|ref
parameter_list|)
block|{
return|return
name|nullColumns
operator|.
name|get
argument_list|(
name|ref
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/** Returns whether the analyzed expression will definitely return null if    * all of a given set of input columns are null. */
specifier|public
specifier|static
name|boolean
name|isNull
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|ImmutableBitSet
name|nullColumns
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|nullColumns
argument_list|)
operator|.
name|isNull
argument_list|(
name|node
argument_list|)
return|;
block|}
comment|/** Returns whether the analyzed expression will definitely not return true    * (equivalently, will definitely not return null or false) if    * all of a given set of input columns are null. */
specifier|public
specifier|static
name|boolean
name|isNotTrue
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|ImmutableBitSet
name|nullColumns
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|nullColumns
argument_list|)
operator|.
name|isNotTrue
argument_list|(
name|node
argument_list|)
return|;
block|}
comment|/** Returns how to deduce whether a particular kind of expression is null,    * given whether its arguments are null. */
specifier|public
specifier|static
name|Policy
name|policy
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
return|return
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|MAP
operator|.
name|get
argument_list|(
name|kind
argument_list|)
argument_list|,
name|kind
argument_list|)
return|;
block|}
comment|/** Returns whether an expression is definitely not true. */
specifier|public
name|boolean
name|isNotTrue
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|IS_NOT_NULL
case|:
return|return
name|anyNull
argument_list|(
operator|(
operator|(
name|RexCall
operator|)
name|node
operator|)
operator|.
name|getOperands
argument_list|()
argument_list|)
return|;
default|default:
return|return
name|isNull
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
comment|/** Returns whether an expression is definitely null.    *    *<p>The answer is based on calls to {@link #isNull} for its constituent    * expressions, and you may override methods to test hypotheses such as    * "if {@code x} is null, is {@code x + y} null? */
specifier|public
name|boolean
name|isNull
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
specifier|final
name|Policy
name|policy
init|=
name|MAP
operator|.
name|get
argument_list|(
name|node
operator|.
name|getKind
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|policy
condition|)
block|{
case|case
name|NOT_NULL
case|:
return|return
literal|false
return|;
case|case
name|ANY
case|:
return|return
name|anyNull
argument_list|(
operator|(
operator|(
name|RexCall
operator|)
name|node
operator|)
operator|.
name|getOperands
argument_list|()
argument_list|)
return|;
default|default:
break|break;
block|}
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|LITERAL
case|:
return|return
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|getValue
argument_list|()
operator|==
literal|null
return|;
comment|// We can only guarantee AND to return NULL if both inputs are NULL  (similar for OR)
comment|// AND(NULL, FALSE) = FALSE
case|case
name|AND
case|:
case|case
name|OR
case|:
case|case
name|COALESCE
case|:
return|return
name|allNull
argument_list|(
operator|(
operator|(
name|RexCall
operator|)
name|node
operator|)
operator|.
name|getOperands
argument_list|()
argument_list|)
return|;
case|case
name|NULLIF
case|:
comment|// NULLIF(null, X) where X can be NULL, returns NULL
comment|// NULLIF(X, Y) where X is not NULL, then this may return NULL if X = Y, otherwise X.
return|return
name|allNull
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|(
operator|(
name|RexCall
operator|)
name|node
operator|)
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
name|INPUT_REF
case|:
return|return
name|isNull
argument_list|(
operator|(
name|RexInputRef
operator|)
name|node
argument_list|)
return|;
case|case
name|CASE
case|:
specifier|final
name|RexCall
name|caseCall
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|caseValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|caseCall
operator|.
name|getOperands
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|RexUtil
operator|.
name|isCasePredicate
argument_list|(
name|caseCall
argument_list|,
name|i
argument_list|)
condition|)
block|{
name|caseValues
operator|.
name|add
argument_list|(
name|caseCall
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|allNull
argument_list|(
name|caseValues
argument_list|)
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
comment|/** Returns whether a given input is definitely null. */
specifier|public
name|boolean
name|isNull
parameter_list|(
name|RexInputRef
name|ref
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
comment|/** Returns whether all expressions in a list are definitely null. */
specifier|private
name|boolean
name|allNull
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|)
block|{
for|for
control|(
name|RexNode
name|operand
range|:
name|operands
control|)
block|{
if|if
condition|(
operator|!
name|isNull
argument_list|(
name|operand
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
comment|/** Returns whether any expressions in a list are definitely null. */
specifier|private
name|boolean
name|anyNull
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|)
block|{
for|for
control|(
name|RexNode
name|operand
range|:
name|operands
control|)
block|{
if|if
condition|(
name|isNull
argument_list|(
name|operand
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
specifier|private
specifier|static
name|Map
argument_list|<
name|SqlKind
argument_list|,
name|Policy
argument_list|>
name|createPolicyMap
parameter_list|()
block|{
name|EnumMap
argument_list|<
name|SqlKind
argument_list|,
name|Policy
argument_list|>
name|map
init|=
operator|new
name|EnumMap
argument_list|<>
argument_list|(
name|SqlKind
operator|.
name|class
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|INPUT_REF
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|LOCAL_REF
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|DYNAMIC_PARAM
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
comment|// The following types of expressions could potentially be custom.
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|CASE
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|DECODE
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
comment|// NULLIF(1, NULL) yields 1, but NULLIF(1, 1) yields NULL
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|NULLIF
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
comment|// COALESCE(NULL, 2) yields 2
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|COALESCE
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|NVL
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
comment|// FALSE AND NULL yields FALSE
comment|// TRUE AND NULL yields NULL
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|AND
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
comment|// TRUE OR NULL yields TRUE
comment|// FALSE OR NULL yields NULL
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|OR
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
comment|// Expression types with custom handlers.
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|LITERAL
argument_list|,
name|Policy
operator|.
name|CUSTOM
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|EXISTS
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_DISTINCT_FROM
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_NULL
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_NOT_NULL
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_TRUE
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_NOT_TRUE
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_FALSE
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_NOT_FALSE
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_DISTINCT_FROM
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|,
name|Policy
operator|.
name|NOT_NULL
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|NOT
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|EQUALS
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|NOT_EQUALS
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|LESS_THAN
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|GREATER_THAN
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|LIKE
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|SIMILAR
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|PLUS_PREFIX
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|MINUS_PREFIX
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|PLUS
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|PLUS_PREFIX
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|MINUS
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|MINUS_PREFIX
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|TIMES
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|DIVIDE
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|CAST
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|REINTERPRET
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|TRIM
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|LTRIM
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|RTRIM
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|CEIL
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|FLOOR
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|EXTRACT
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|GREATEST
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|LEAST
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|TIMESTAMP_ADD
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|SqlKind
operator|.
name|TIMESTAMP_DIFF
argument_list|,
name|Policy
operator|.
name|ANY
argument_list|)
expr_stmt|;
comment|// Assume that any other expressions cannot be simplified.
for|for
control|(
name|SqlKind
name|k
range|:
name|Iterables
operator|.
name|concat
argument_list|(
name|SqlKind
operator|.
name|EXPRESSION
argument_list|,
name|SqlKind
operator|.
name|AGGREGATE
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
name|map
operator|.
name|containsKey
argument_list|(
name|k
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|k
argument_list|,
name|Policy
operator|.
name|AS_IS
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|map
return|;
block|}
comment|/** How whether an operator's operands are null affects whether a call to    * that operator evaluates to null. */
specifier|public
enum|enum
name|Policy
block|{
comment|/** This kind of expression is never null. No need to look at its arguments,      * if it has any. */
name|NOT_NULL
block|,
comment|/** This kind of expression has its own particular rules about whether it      * is null. */
name|CUSTOM
block|,
comment|/** This kind of expression is null if and only if at least one of its      * arguments is null. */
name|ANY
block|,
comment|/** This kind of expression may be null. There is no way to rewrite. */
name|AS_IS
block|,   }
block|}
end_class

begin_comment
comment|// End Strong.java
end_comment

end_unit

