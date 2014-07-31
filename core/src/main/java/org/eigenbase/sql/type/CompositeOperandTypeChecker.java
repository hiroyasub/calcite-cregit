begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
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
name|sql
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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Ord
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

begin_comment
comment|/**  * This class allows multiple existing {@link SqlOperandTypeChecker} rules to be  * combined into one rule. For example, allowing an operand to be either string  * or numeric could be done by:  *  *<blockquote>  *<pre><code>  *  * CompositeOperandsTypeChecking newCompositeRule =  *  new CompositeOperandsTypeChecking(  *    Composition.OR,  *    new SqlOperandTypeChecker[]{stringRule, numericRule});  *  *</code></pre>  *</blockquote>  *  * Similary a rule that would only allow a numeric literal can be done by:  *  *<blockquote>  *<pre><code>  *  * CompositeOperandsTypeChecking newCompositeRule =  *  new CompositeOperandsTypeChecking(  *    Composition.AND,  *    new SqlOperandTypeChecker[]{numericRule, literalRule});  *  *</code></pre>  *</blockquote>  *  *<p>Finally, creating a signature expecting a string for the first operand and  * a numeric for the second operand can be done by:  *  *<blockquote>  *<pre><code>  *  * CompositeOperandsTypeChecking newCompositeRule =  *  new CompositeOperandsTypeChecking(  *    Composition.SEQUENCE,  *    new SqlOperandTypeChecker[]{stringRule, numericRule});  *  *</code></pre>  *</blockquote>  *  *<p>For SEQUENCE composition, the rules must be instances of  * SqlSingleOperandTypeChecker, and signature generation is not supported. For  * AND composition, only the first rule is used for signature generation.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeOperandTypeChecker
implements|implements
name|SqlSingleOperandTypeChecker
block|{
comment|//~ Enums ------------------------------------------------------------------
specifier|public
enum|enum
name|Composition
block|{
name|AND
block|,
name|OR
block|,
name|SEQUENCE
block|}
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|SqlSingleOperandTypeChecker
argument_list|>
name|allowedRules
decl_stmt|;
specifier|private
specifier|final
name|Composition
name|composition
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
name|SqlSingleOperandTypeChecker
argument_list|>
name|allowedRules
parameter_list|)
block|{
assert|assert
literal|null
operator|!=
name|allowedRules
assert|;
assert|assert
name|allowedRules
operator|.
name|size
argument_list|()
operator|>
literal|1
assert|;
name|this
operator|.
name|allowedRules
operator|=
name|allowedRules
expr_stmt|;
name|this
operator|.
name|composition
operator|=
name|composition
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|ImmutableList
argument_list|<
name|SqlSingleOperandTypeChecker
argument_list|>
name|getRules
parameter_list|()
block|{
return|return
name|allowedRules
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
name|composition
operator|==
name|Composition
operator|.
name|SEQUENCE
condition|)
block|{
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
literal|"must override getAllowedSignatures"
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
name|SqlSingleOperandTypeChecker
argument_list|>
name|ord
range|:
name|Ord
operator|.
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
name|checkSingleOperandType
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|SqlNode
name|node
parameter_list|,
name|int
name|iFormalOperand
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
assert|assert
name|allowedRules
operator|.
name|size
argument_list|()
operator|>=
literal|1
assert|;
if|if
condition|(
name|composition
operator|==
name|Composition
operator|.
name|SEQUENCE
condition|)
block|{
return|return
name|allowedRules
operator|.
name|get
argument_list|(
name|iFormalOperand
argument_list|)
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
name|int
name|typeErrorCount
init|=
literal|0
decl_stmt|;
name|boolean
name|throwOnAndFailure
init|=
operator|(
name|composition
operator|==
name|Composition
operator|.
name|AND
operator|)
operator|&&
name|throwOnFailure
decl_stmt|;
for|for
control|(
name|SqlSingleOperandTypeChecker
name|rule
range|:
name|allowedRules
control|)
block|{
if|if
condition|(
operator|!
name|rule
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|,
name|iFormalOperand
argument_list|,
name|throwOnAndFailure
argument_list|)
condition|)
block|{
name|typeErrorCount
operator|++
expr_stmt|;
block|}
block|}
name|boolean
name|ret
decl_stmt|;
switch|switch
condition|(
name|composition
condition|)
block|{
case|case
name|AND
case|:
name|ret
operator|=
name|typeErrorCount
operator|==
literal|0
expr_stmt|;
break|break;
case|case
name|OR
case|:
name|ret
operator|=
name|typeErrorCount
operator|<
name|allowedRules
operator|.
name|size
argument_list|()
expr_stmt|;
break|break;
default|default:
comment|// should never come here
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|composition
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|ret
operator|&&
name|throwOnFailure
condition|)
block|{
comment|// In the case of a composite OR, we want to throw an error
comment|// describing in more detail what the problem was, hence doing the
comment|// loop again.
for|for
control|(
name|SqlSingleOperandTypeChecker
name|rule
range|:
name|allowedRules
control|)
block|{
name|rule
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|,
name|iFormalOperand
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// If no exception thrown, just throw a generic validation signature
comment|// error.
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
return|return
name|ret
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
name|int
name|typeErrorCount
init|=
literal|0
decl_stmt|;
name|label
label|:
for|for
control|(
name|Ord
argument_list|<
name|SqlSingleOperandTypeChecker
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|allowedRules
argument_list|)
control|)
block|{
name|SqlSingleOperandTypeChecker
name|rule
init|=
name|ord
operator|.
name|e
decl_stmt|;
switch|switch
condition|(
name|composition
condition|)
block|{
case|case
name|SEQUENCE
case|:
if|if
condition|(
name|ord
operator|.
name|i
operator|>=
name|callBinding
operator|.
name|getOperandCount
argument_list|()
condition|)
block|{
break|break
name|label
break|;
block|}
if|if
condition|(
operator|!
name|rule
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
name|typeErrorCount
operator|++
expr_stmt|;
block|}
break|break;
default|default:
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
name|typeErrorCount
operator|++
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
comment|// Avoid trying other rules in AND if the first one fails.
break|break
name|label
break|;
block|}
block|}
if|else if
condition|(
name|composition
operator|==
name|Composition
operator|.
name|OR
condition|)
block|{
break|break
name|label
break|;
comment|// true OR any == true, just break
block|}
break|break;
block|}
block|}
name|boolean
name|failed
decl_stmt|;
switch|switch
condition|(
name|composition
condition|)
block|{
case|case
name|AND
case|:
case|case
name|SEQUENCE
case|:
name|failed
operator|=
name|typeErrorCount
operator|>
literal|0
expr_stmt|;
break|break;
case|case
name|OR
case|:
name|failed
operator|=
name|typeErrorCount
operator|==
name|allowedRules
operator|.
name|size
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
if|if
condition|(
name|failed
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
comment|// In the case of a composite OR, we want to throw an error
comment|// describing in more detail what the problem was, hence doing
comment|// the loop again.
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
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End CompositeOperandTypeChecker.java
end_comment

end_unit

