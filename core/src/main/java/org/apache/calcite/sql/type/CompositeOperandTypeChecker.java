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
name|type
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
name|SqlCallBinding
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
name|SqlOperandCountRange
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
name|SqlOperator
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
name|implicit
operator|.
name|TypeCoercion
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
name|Collections
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * This class allows multiple existing {@link SqlOperandTypeChecker} rules to be  * combined into one rule. For example, allowing an operand to be either string  * or numeric could be done by:  *  *<blockquote>  *<pre><code>  * CompositeOperandsTypeChecking newCompositeRule =  *     new CompositeOperandsTypeChecking(Composition.OR,  *         new SqlOperandTypeChecker[]{stringRule, numericRule});  *</code></pre>  *</blockquote>  *  *<p>Similarly a rule that would only allow a numeric literal can be done by:  *  *<blockquote>  *<pre><code>  * CompositeOperandsTypeChecking newCompositeRule =  *     new CompositeOperandsTypeChecking(Composition.AND,  *         new SqlOperandTypeChecker[]{numericRule, literalRule});  *</code></pre>  *</blockquote>  *  *<p>Finally, creating a signature expecting a string for the first operand and  * a numeric for the second operand can be done by:  *  *<blockquote>  *<pre><code>  * CompositeOperandsTypeChecking newCompositeRule =  *     new CompositeOperandsTypeChecking(Composition.SEQUENCE,  *         new SqlOperandTypeChecker[]{stringRule, numericRule});  *</code></pre>  *</blockquote>  *  *<p>For SEQUENCE composition, the rules must be instances of  * SqlSingleOperandTypeChecker, and signature generation is not supported. For  * AND composition, only the first rule is used for signature generation.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeOperandTypeChecker
implements|implements
name|SqlOperandTypeChecker
block|{
specifier|private
specifier|final
name|SqlOperandCountRange
name|range
decl_stmt|;
comment|//~ Enums ------------------------------------------------------------------
comment|/** How operands are composed. */
specifier|public
enum|enum
name|Composition
block|{
name|AND
block|,
name|OR
block|,
name|SEQUENCE
block|,
name|REPEAT
block|}
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|?
extends|extends
name|SqlOperandTypeChecker
argument_list|>
name|allowedRules
decl_stmt|;
specifier|protected
specifier|final
name|Composition
name|composition
decl_stmt|;
specifier|private
specifier|final
name|String
name|allowedSignatures
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Package private. Use {@link OperandTypes#and},    * {@link OperandTypes#or}.    */
name|CompositeOperandTypeChecker
parameter_list|(
name|Composition
name|composition
parameter_list|,
name|ImmutableList
argument_list|<
name|?
extends|extends
name|SqlOperandTypeChecker
argument_list|>
name|allowedRules
parameter_list|,
annotation|@
name|Nullable
name|String
name|allowedSignatures
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandCountRange
name|range
parameter_list|)
block|{
name|this
operator|.
name|allowedRules
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|allowedRules
argument_list|)
expr_stmt|;
name|this
operator|.
name|composition
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|composition
argument_list|)
expr_stmt|;
name|this
operator|.
name|allowedSignatures
operator|=
name|allowedSignatures
expr_stmt|;
name|this
operator|.
name|range
operator|=
name|range
expr_stmt|;
assert|assert
operator|(
name|range
operator|!=
literal|null
operator|)
operator|==
operator|(
name|composition
operator|==
name|Composition
operator|.
name|REPEAT
operator|)
assert|;
assert|assert
name|allowedRules
operator|.
name|size
argument_list|()
operator|+
operator|(
name|range
operator|==
literal|null
condition|?
literal|0
else|:
literal|1
operator|)
operator|>
literal|1
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|boolean
name|isOptional
parameter_list|(
name|int
name|i
parameter_list|)
block|{
for|for
control|(
name|SqlOperandTypeChecker
name|allowedRule
range|:
name|allowedRules
control|)
block|{
if|if
condition|(
name|allowedRule
operator|.
name|isOptional
argument_list|(
name|i
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
name|ImmutableList
argument_list|<
name|?
extends|extends
name|SqlOperandTypeChecker
argument_list|>
name|getRules
parameter_list|()
block|{
return|return
name|allowedRules
return|;
block|}
specifier|public
name|Consistency
name|getConsistency
parameter_list|()
block|{
return|return
name|Consistency
operator|.
name|NONE
return|;
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|String
name|opName
parameter_list|)
block|{
if|if
condition|(
name|allowedSignatures
operator|!=
literal|null
condition|)
block|{
return|return
name|allowedSignatures
return|;
block|}
if|if
condition|(
name|composition
operator|==
name|Composition
operator|.
name|SEQUENCE
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"specify allowedSignatures or override getAllowedSignatures"
argument_list|)
throw|;
block|}
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|SqlOperandTypeChecker
argument_list|>
name|ord
range|:
name|Ord
operator|.
expr|<
name|SqlOperandTypeChecker
operator|>
name|zip
argument_list|(
name|allowedRules
argument_list|)
control|)
block|{
if|if
condition|(
name|ord
operator|.
name|i
operator|>
literal|0
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
name|SqlOperator
operator|.
name|NL
argument_list|)
expr_stmt|;
block|}
name|ret
operator|.
name|append
argument_list|(
name|ord
operator|.
name|e
operator|.
name|getAllowedSignatures
argument_list|(
name|op
argument_list|,
name|opName
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|composition
operator|==
name|Composition
operator|.
name|AND
condition|)
block|{
break|break;
block|}
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
switch|switch
condition|(
name|composition
condition|)
block|{
case|case
name|REPEAT
case|:
return|return
name|range
return|;
case|case
name|SEQUENCE
case|:
return|return
name|SqlOperandCountRanges
operator|.
name|of
argument_list|(
name|allowedRules
operator|.
name|size
argument_list|()
argument_list|)
return|;
case|case
name|AND
case|:
case|case
name|OR
case|:
default|default:
specifier|final
name|List
argument_list|<
name|SqlOperandCountRange
argument_list|>
name|ranges
init|=
operator|new
name|AbstractList
argument_list|<
name|SqlOperandCountRange
argument_list|>
argument_list|()
block|{
specifier|public
name|SqlOperandCountRange
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|allowedRules
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getOperandCountRange
argument_list|()
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|allowedRules
operator|.
name|size
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|int
name|min
init|=
name|minMin
argument_list|(
name|ranges
argument_list|)
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|maxMax
argument_list|(
name|ranges
argument_list|)
decl_stmt|;
name|SqlOperandCountRange
name|composite
init|=
operator|new
name|SqlOperandCountRange
argument_list|()
block|{
specifier|public
name|boolean
name|isValidCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
switch|switch
condition|(
name|composition
condition|)
block|{
case|case
name|AND
case|:
for|for
control|(
name|SqlOperandCountRange
name|range
range|:
name|ranges
control|)
block|{
if|if
condition|(
operator|!
name|range
operator|.
name|isValidCount
argument_list|(
name|count
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
case|case
name|OR
case|:
default|default:
for|for
control|(
name|SqlOperandCountRange
name|range
range|:
name|ranges
control|)
block|{
if|if
condition|(
name|range
operator|.
name|isValidCount
argument_list|(
name|count
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
block|}
specifier|public
name|int
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
specifier|public
name|int
name|getMax
parameter_list|()
block|{
return|return
name|max
return|;
block|}
block|}
decl_stmt|;
if|if
condition|(
name|max
operator|>=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|min
init|;
name|i
operator|<=
name|max
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|composite
operator|.
name|isValidCount
argument_list|(
name|i
argument_list|)
condition|)
block|{
comment|// Composite is not a simple range. Can't simplify,
comment|// so return the composite.
return|return
name|composite
return|;
block|}
block|}
block|}
return|return
name|min
operator|==
name|max
condition|?
name|SqlOperandCountRanges
operator|.
name|of
argument_list|(
name|min
argument_list|)
else|:
name|SqlOperandCountRanges
operator|.
name|between
argument_list|(
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
block|}
specifier|private
name|int
name|minMin
parameter_list|(
name|List
argument_list|<
name|SqlOperandCountRange
argument_list|>
name|ranges
parameter_list|)
block|{
name|int
name|min
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|SqlOperandCountRange
name|range
range|:
name|ranges
control|)
block|{
name|min
operator|=
name|Math
operator|.
name|min
argument_list|(
name|min
argument_list|,
name|range
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|min
return|;
block|}
specifier|private
name|int
name|maxMax
parameter_list|(
name|List
argument_list|<
name|SqlOperandCountRange
argument_list|>
name|ranges
parameter_list|)
block|{
name|int
name|max
init|=
name|Integer
operator|.
name|MIN_VALUE
decl_stmt|;
for|for
control|(
name|SqlOperandCountRange
name|range
range|:
name|ranges
control|)
block|{
if|if
condition|(
name|range
operator|.
name|getMax
argument_list|()
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|composition
operator|==
name|Composition
operator|.
name|OR
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
else|else
block|{
name|max
operator|=
name|Math
operator|.
name|max
argument_list|(
name|max
argument_list|,
name|range
operator|.
name|getMax
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|max
return|;
block|}
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
comment|// 1. Check eagerly for binary arithmetic expressions.
comment|// 2. Check the comparability.
comment|// 3. Check if the operands have the right type.
if|if
condition|(
name|callBinding
operator|.
name|isTypeCoercionEnabled
argument_list|()
condition|)
block|{
specifier|final
name|TypeCoercion
name|typeCoercion
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|getTypeCoercion
argument_list|()
decl_stmt|;
name|typeCoercion
operator|.
name|binaryArithmeticCoercion
argument_list|(
name|callBinding
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|check
argument_list|(
name|callBinding
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
name|throwOnFailure
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|composition
operator|==
name|Composition
operator|.
name|OR
condition|)
block|{
for|for
control|(
name|SqlOperandTypeChecker
name|allowedRule
range|:
name|allowedRules
control|)
block|{
name|allowedRule
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If no exception thrown, just throw a generic validation
comment|// signature error.
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
specifier|private
name|boolean
name|check
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|)
block|{
switch|switch
condition|(
name|composition
condition|)
block|{
case|case
name|REPEAT
case|:
if|if
condition|(
operator|!
name|range
operator|.
name|isValidCount
argument_list|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|operand
range|:
name|Util
operator|.
name|range
argument_list|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
argument_list|)
control|)
block|{
for|for
control|(
name|SqlOperandTypeChecker
name|rule
range|:
name|allowedRules
control|)
block|{
if|if
condition|(
operator|!
operator|(
operator|(
name|SqlSingleOperandTypeChecker
operator|)
name|rule
operator|)
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operand
argument_list|(
name|operand
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
condition|)
block|{
if|if
condition|(
name|callBinding
operator|.
name|isTypeCoercionEnabled
argument_list|()
condition|)
block|{
return|return
name|coerceOperands
argument_list|(
name|callBinding
argument_list|,
literal|true
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
case|case
name|SEQUENCE
case|:
if|if
condition|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
operator|!=
name|allowedRules
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|Ord
argument_list|<
name|SqlOperandTypeChecker
argument_list|>
name|ord
range|:
name|Ord
operator|.
expr|<
name|SqlOperandTypeChecker
operator|>
name|zip
argument_list|(
name|allowedRules
argument_list|)
control|)
block|{
name|SqlOperandTypeChecker
name|rule
init|=
name|ord
operator|.
name|e
decl_stmt|;
if|if
condition|(
operator|!
operator|(
operator|(
name|SqlSingleOperandTypeChecker
operator|)
name|rule
operator|)
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operand
argument_list|(
name|ord
operator|.
name|i
argument_list|)
argument_list|,
literal|0
argument_list|,
literal|false
argument_list|)
condition|)
block|{
if|if
condition|(
name|callBinding
operator|.
name|isTypeCoercionEnabled
argument_list|()
condition|)
block|{
return|return
name|coerceOperands
argument_list|(
name|callBinding
argument_list|,
literal|false
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
case|case
name|AND
case|:
for|for
control|(
name|Ord
argument_list|<
name|SqlOperandTypeChecker
argument_list|>
name|ord
range|:
name|Ord
operator|.
expr|<
name|SqlOperandTypeChecker
operator|>
name|zip
argument_list|(
name|allowedRules
argument_list|)
control|)
block|{
name|SqlOperandTypeChecker
name|rule
init|=
name|ord
operator|.
name|e
decl_stmt|;
if|if
condition|(
operator|!
name|rule
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|// Avoid trying other rules in AND if the first one fails.
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
case|case
name|OR
case|:
comment|// If there is an ImplicitCastOperandTypeChecker, check it without type coercion first,
comment|// if all check fails, try type coercion if it is allowed (default true).
if|if
condition|(
name|checkWithoutTypeCoercion
argument_list|(
name|callBinding
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
for|for
control|(
name|Ord
argument_list|<
name|SqlOperandTypeChecker
argument_list|>
name|ord
range|:
name|Ord
operator|.
expr|<
name|SqlOperandTypeChecker
operator|>
name|zip
argument_list|(
name|allowedRules
argument_list|)
control|)
block|{
name|SqlOperandTypeChecker
name|rule
init|=
name|ord
operator|.
name|e
decl_stmt|;
if|if
condition|(
name|rule
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
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
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
block|}
comment|/** Tries to coerce the operands based on the defined type families. */
specifier|private
name|boolean
name|coerceOperands
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|repeat
parameter_list|)
block|{
comment|// Type coercion for the call,
comment|// collect SqlTypeFamily and data type of all the operands.
name|List
argument_list|<
name|SqlTypeFamily
argument_list|>
name|families
init|=
name|allowedRules
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|r
lambda|->
name|r
operator|instanceof
name|ImplicitCastOperandTypeChecker
argument_list|)
comment|// All the rules are SqlSingleOperandTypeChecker.
operator|.
name|map
argument_list|(
name|r
lambda|->
operator|(
operator|(
name|ImplicitCastOperandTypeChecker
operator|)
name|r
operator|)
operator|.
name|getOperandSqlTypeFamily
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|families
operator|.
name|size
argument_list|()
operator|<
name|allowedRules
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// Not all the checkers are ImplicitCastOperandTypeChecker, returns early.
return|return
literal|false
return|;
block|}
if|if
condition|(
name|repeat
condition|)
block|{
assert|assert
name|families
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
name|families
operator|=
name|Collections
operator|.
name|nCopies
argument_list|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
argument_list|,
name|families
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|operandTypes
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
name|callBinding
operator|.
name|getOperandCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|operandTypes
operator|.
name|add
argument_list|(
name|callBinding
operator|.
name|getOperandType
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|TypeCoercion
name|typeCoercion
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|getTypeCoercion
argument_list|()
decl_stmt|;
return|return
name|typeCoercion
operator|.
name|builtinFunctionCoercion
argument_list|(
name|callBinding
argument_list|,
name|operandTypes
argument_list|,
name|families
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|checkWithoutTypeCoercion
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|)
block|{
if|if
condition|(
operator|!
name|callBinding
operator|.
name|isTypeCoercionEnabled
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|SqlOperandTypeChecker
name|rule
range|:
name|allowedRules
control|)
block|{
if|if
condition|(
name|rule
operator|instanceof
name|ImplicitCastOperandTypeChecker
condition|)
block|{
name|ImplicitCastOperandTypeChecker
name|rule1
init|=
operator|(
name|ImplicitCastOperandTypeChecker
operator|)
name|rule
decl_stmt|;
if|if
condition|(
name|rule1
operator|.
name|checkOperandTypesWithoutTypeCoercion
argument_list|(
name|callBinding
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
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

