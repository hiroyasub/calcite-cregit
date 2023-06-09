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
name|fun
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|RexCallBinding
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
name|sql
operator|.
name|SqlBasicCall
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
name|SqlLiteral
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
name|SqlOperatorBinding
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
name|SqlSyntax
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
name|SqlWriter
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
name|parser
operator|.
name|SqlParserPos
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
name|InferTypes
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
name|SqlOperandCountRanges
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
name|SqlTypeUtil
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
name|SqlValidator
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
name|SqlValidatorImpl
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
name|SqlValidatorScope
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
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
comment|/**  * An operator describing a<code>CASE</code>,<code>NULLIF</code> or<code>  * COALESCE</code> expression. All of these forms are normalized at parse time  * to a to a simple<code>CASE</code> statement like this:  *  *<blockquote><pre><code>CASE  *   WHEN&lt;when expression_0&gt; THEN&lt;then expression_0&gt;  *   WHEN&lt;when expression_1&gt; THEN&lt;then expression_1&gt;  *   ...  *   WHEN&lt;when expression_N&gt; THEN&lt;then expression_N&gt;  *   ELSE&lt;else expression&gt;  * END</code></pre></blockquote>  *  *<p>The switched form of the<code>CASE</code> statement is normalized to the  * simple form by inserting calls to the<code>=</code> operator. For  * example,</p>  *  *<blockquote><pre><code>CASE x + y  *   WHEN 1 THEN 'fee'  *   WHEN 2 THEN 'fie'  *   ELSE 'foe'  * END</code></pre></blockquote>  *  *<p>becomes</p>  *  *<blockquote><pre><code>CASE  * WHEN Equals(x + y, 1) THEN 'fee'  * WHEN Equals(x + y, 2) THEN 'fie'  * ELSE 'foe'  * END</code></pre></blockquote>  *  *<p>REVIEW jhyde 2004/3/19 Does<code>Equals</code> handle NULL semantics  * correctly?</p>  *  *<p><code>COALESCE(x, y, z)</code> becomes</p>  *  *<blockquote><pre><code>CASE  * WHEN x IS NOT NULL THEN x  * WHEN y IS NOT NULL THEN y  * ELSE z  * END</code></pre></blockquote>  *  *<p><code>NULLIF(x, -1)</code> becomes</p>  *  *<blockquote><pre><code>CASE  * WHEN x = -1 THEN NULL  * ELSE x  * END</code></pre></blockquote>  *  *<p>Note that some of these normalizations cause expressions to be duplicated.  * This may make it more difficult to write optimizer rules (because the rules  * will have to deduce that expressions are equivalent). It also requires that  * some part of the planning process (probably the generator of the calculator  * program) does common sub-expression elimination.</p>  *  *<p>REVIEW jhyde 2004/3/19. Expanding expressions at parse time has some other  * drawbacks. It is more difficult to give meaningful validation errors: given  *<code>COALESCE(DATE '2004-03-18', 3.5)</code>, do we issue a type-checking  * error against a<code>CASE</code> operator? Second, I'd like to use the  * {@link SqlNode} object model to generate SQL to send to 3rd-party databases,  * but there's now no way to represent a call to COALESCE or NULLIF. All in all,  * it would be better to have operators for COALESCE, NULLIF, and both simple  * and switched forms of CASE, then translate to simple CASE when building the  * {@link org.apache.calcite.rex.RexNode} tree.</p>  *  *<p>The arguments are physically represented as follows:</p>  *  *<ul>  *<li>The<i>when</i> expressions are stored in a {@link SqlNodeList}  * whenList.</li>  *<li>The<i>then</i> expressions are stored in a {@link SqlNodeList}  * thenList.</li>  *<li>The<i>else</i> expression is stored as a regular {@link SqlNode}.</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|SqlCaseOperator
extends|extends
name|SqlOperator
block|{
specifier|public
specifier|static
specifier|final
name|SqlCaseOperator
name|INSTANCE
init|=
operator|new
name|SqlCaseOperator
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|SqlCaseOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"CASE"
argument_list|,
name|SqlKind
operator|.
name|CASE
argument_list|,
name|MDX_PRECEDENCE
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
name|InferTypes
operator|.
name|RETURN_TYPE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|validateCall
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlValidatorScope
name|operandScope
parameter_list|)
block|{
specifier|final
name|SqlCase
name|sqlCase
init|=
operator|(
name|SqlCase
operator|)
name|call
decl_stmt|;
specifier|final
name|SqlNodeList
name|whenOperands
init|=
name|sqlCase
operator|.
name|getWhenOperands
argument_list|()
decl_stmt|;
specifier|final
name|SqlNodeList
name|thenOperands
init|=
name|sqlCase
operator|.
name|getThenOperands
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|elseOperand
init|=
name|sqlCase
operator|.
name|getElseOperand
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlNode
name|operand
range|:
name|whenOperands
control|)
block|{
name|operand
operator|.
name|validateExpr
argument_list|(
name|validator
argument_list|,
name|operandScope
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SqlNode
name|operand
range|:
name|thenOperands
control|)
block|{
name|operand
operator|.
name|validateExpr
argument_list|(
name|validator
argument_list|,
name|operandScope
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|elseOperand
operator|!=
literal|null
condition|)
block|{
name|elseOperand
operator|.
name|validateExpr
argument_list|(
name|validator
argument_list|,
name|operandScope
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
comment|// Do not try to derive the types of the operands. We will do that
comment|// later, top down.
return|return
name|validateOperands
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|SqlCase
name|caseCall
init|=
operator|(
name|SqlCase
operator|)
name|callBinding
operator|.
name|getCall
argument_list|()
decl_stmt|;
name|SqlNodeList
name|whenList
init|=
name|caseCall
operator|.
name|getWhenOperands
argument_list|()
decl_stmt|;
name|SqlNodeList
name|thenList
init|=
name|caseCall
operator|.
name|getThenOperands
argument_list|()
decl_stmt|;
assert|assert
name|whenList
operator|.
name|size
argument_list|()
operator|==
name|thenList
operator|.
name|size
argument_list|()
assert|;
comment|// checking that search conditions are ok...
for|for
control|(
name|SqlNode
name|node
range|:
name|whenList
control|)
block|{
comment|// should throw validation error if something wrong...
name|RelDataType
name|type
init|=
name|SqlTypeUtil
operator|.
name|deriveType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|inBooleanFamily
argument_list|(
name|type
argument_list|)
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newError
argument_list|(
name|RESOURCE
operator|.
name|expectedBoolean
argument_list|()
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
block|}
name|boolean
name|foundNotNull
init|=
literal|false
decl_stmt|;
for|for
control|(
name|SqlNode
name|node
range|:
name|thenList
control|)
block|{
if|if
condition|(
operator|!
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|node
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|foundNotNull
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|caseCall
operator|.
name|getElseOperand
argument_list|()
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|foundNotNull
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|foundNotNull
condition|)
block|{
comment|// according to the sql standard we can not have all of the THEN
comment|// statements and the ELSE returning null
if|if
condition|(
name|throwOnFailure
operator|&&
operator|!
name|callBinding
operator|.
name|isTypeCoercionEnabled
argument_list|()
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newError
argument_list|(
name|RESOURCE
operator|.
name|mustNotNullInElse
argument_list|()
argument_list|)
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
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
comment|// REVIEW jvs 4-June-2005:  can't these be unified?
if|if
condition|(
operator|!
operator|(
name|opBinding
operator|instanceof
name|SqlCallBinding
operator|)
condition|)
block|{
return|return
name|inferTypeFromOperands
argument_list|(
name|opBinding
argument_list|)
return|;
block|}
return|return
name|inferTypeFromValidator
argument_list|(
operator|(
name|SqlCallBinding
operator|)
name|opBinding
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RelDataType
name|inferTypeFromValidator
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|)
block|{
name|SqlCase
name|caseCall
init|=
operator|(
name|SqlCase
operator|)
name|callBinding
operator|.
name|getCall
argument_list|()
decl_stmt|;
name|SqlNodeList
name|thenList
init|=
name|caseCall
operator|.
name|getThenOperands
argument_list|()
decl_stmt|;
name|ArrayList
argument_list|<
name|SqlNode
argument_list|>
name|nullList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SqlNodeList
name|whenOperands
init|=
name|caseCall
operator|.
name|getWhenOperands
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|callBinding
operator|.
name|getTypeFactory
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
name|thenList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|node
init|=
name|thenList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RelDataType
name|type
init|=
name|SqlTypeUtil
operator|.
name|deriveType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|SqlNode
name|operand
init|=
name|whenOperands
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|operand
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|IS_NOT_NULL
operator|&&
name|type
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|SqlBasicCall
name|call
init|=
operator|(
name|SqlBasicCall
operator|)
name|operand
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getOperandList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
comment|// We're sure that the type is not nullable if the kind is IS NOT NULL.
name|type
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
name|argTypes
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
if|if
condition|(
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|node
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|nullList
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
name|SqlNode
name|elseOp
init|=
name|requireNonNull
argument_list|(
name|caseCall
operator|.
name|getElseOperand
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"elseOperand for "
operator|+
name|caseCall
argument_list|)
decl_stmt|;
name|argTypes
operator|.
name|add
argument_list|(
name|SqlTypeUtil
operator|.
name|deriveType
argument_list|(
name|callBinding
argument_list|,
name|elseOp
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|SqlUtil
operator|.
name|isNullLiteral
argument_list|(
name|elseOp
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|nullList
operator|.
name|add
argument_list|(
name|elseOp
argument_list|)
expr_stmt|;
block|}
name|RelDataType
name|ret
init|=
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|argTypes
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|ret
condition|)
block|{
name|boolean
name|coerced
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|callBinding
operator|.
name|isTypeCoercionEnabled
argument_list|()
condition|)
block|{
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
name|RelDataType
name|commonType
init|=
name|typeCoercion
operator|.
name|getWiderTypeFor
argument_list|(
name|argTypes
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// commonType is always with nullability as false, we do not consider the
comment|// nullability when deducing the common type. Use the deduced type
comment|// (with the correct nullability) in SqlValidator
comment|// instead of the commonType as the return type.
if|if
condition|(
literal|null
operator|!=
name|commonType
condition|)
block|{
name|coerced
operator|=
name|typeCoercion
operator|.
name|caseWhenCoercion
argument_list|(
name|callBinding
argument_list|)
expr_stmt|;
if|if
condition|(
name|coerced
condition|)
block|{
name|ret
operator|=
name|SqlTypeUtil
operator|.
name|deriveType
argument_list|(
name|callBinding
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|coerced
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationError
argument_list|(
name|RESOURCE
operator|.
name|illegalMixingOfTypes
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|final
name|SqlValidatorImpl
name|validator
init|=
operator|(
name|SqlValidatorImpl
operator|)
name|callBinding
operator|.
name|getValidator
argument_list|()
decl_stmt|;
name|requireNonNull
argument_list|(
name|ret
argument_list|,
parameter_list|()
lambda|->
literal|"return type for "
operator|+
name|callBinding
argument_list|)
expr_stmt|;
for|for
control|(
name|SqlNode
name|node
range|:
name|nullList
control|)
block|{
name|validator
operator|.
name|setValidatedNodeType
argument_list|(
name|node
argument_list|,
name|ret
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
specifier|private
specifier|static
name|RelDataType
name|inferTypeFromOperands
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
init|=
name|opBinding
operator|.
name|collectOperandTypes
argument_list|()
decl_stmt|;
assert|assert
operator|(
name|argTypes
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|)
operator|==
literal|1
operator|:
literal|"odd number of arguments expected: "
operator|+
name|argTypes
operator|.
name|size
argument_list|()
assert|;
assert|assert
name|argTypes
operator|.
name|size
argument_list|()
operator|>
literal|1
operator|:
literal|"CASE must have more than 1 argument. Given "
operator|+
name|argTypes
operator|.
name|size
argument_list|()
operator|+
literal|", "
operator|+
name|argTypes
assert|;
name|List
argument_list|<
name|RelDataType
argument_list|>
name|thenTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
operator|(
name|argTypes
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
condition|;
name|j
operator|+=
literal|2
control|)
block|{
name|RelDataType
name|argType
init|=
name|argTypes
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|opBinding
operator|instanceof
name|RexCallBinding
condition|)
block|{
specifier|final
name|RexCallBinding
name|rexCallBinding
init|=
operator|(
name|RexCallBinding
operator|)
name|opBinding
decl_stmt|;
specifier|final
name|RexNode
name|whenNode
init|=
name|rexCallBinding
operator|.
name|operands
argument_list|()
operator|.
name|get
argument_list|(
name|j
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|thenNode
init|=
name|rexCallBinding
operator|.
name|operands
argument_list|()
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
if|if
condition|(
name|whenNode
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|IS_NOT_NULL
operator|&&
name|argType
operator|.
name|isNullable
argument_list|()
condition|)
block|{
comment|// Type is not nullable if the kind is IS NOT NULL.
specifier|final
name|RexCall
name|isNotNullCall
init|=
operator|(
name|RexCall
operator|)
name|whenNode
decl_stmt|;
if|if
condition|(
name|isNotNullCall
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|equals
argument_list|(
name|thenNode
argument_list|)
condition|)
block|{
name|argType
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|argType
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|thenTypes
operator|.
name|add
argument_list|(
name|argType
argument_list|)
expr_stmt|;
block|}
name|thenTypes
operator|.
name|add
argument_list|(
name|Iterables
operator|.
name|getLast
argument_list|(
name|argTypes
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|requireNonNull
argument_list|(
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|thenTypes
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"Can't find leastRestrictive type for "
operator|+
name|thenTypes
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|any
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|SPECIAL
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"argument.type.incompatible"
argument_list|)
annotation|@
name|Override
specifier|public
name|SqlCall
name|createCall
parameter_list|(
annotation|@
name|Nullable
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
annotation|@
name|Nullable
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
assert|assert
name|functionQualifier
operator|==
literal|null
assert|;
assert|assert
name|operands
operator|.
name|length
operator|==
literal|4
assert|;
return|return
operator|new
name|SqlCase
argument_list|(
name|pos
argument_list|,
name|operands
index|[
literal|0
index|]
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
literal|1
index|]
argument_list|,
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
literal|2
index|]
argument_list|,
name|operands
index|[
literal|3
index|]
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call_
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|SqlCase
name|kase
init|=
operator|(
name|SqlCase
operator|)
name|call_
decl_stmt|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|CASE
argument_list|,
literal|"CASE"
argument_list|,
literal|"END"
argument_list|)
decl_stmt|;
assert|assert
name|kase
operator|.
name|whenList
operator|.
name|size
argument_list|()
operator|==
name|kase
operator|.
name|thenList
operator|.
name|size
argument_list|()
assert|;
if|if
condition|(
name|kase
operator|.
name|value
operator|!=
literal|null
condition|)
block|{
name|kase
operator|.
name|value
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlNode
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|kase
operator|.
name|whenList
argument_list|,
name|kase
operator|.
name|thenList
argument_list|)
control|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"WHEN"
argument_list|)
expr_stmt|;
name|pair
operator|.
name|left
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"THEN"
argument_list|)
expr_stmt|;
name|pair
operator|.
name|right
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|SqlNode
name|elseExpr
init|=
name|kase
operator|.
name|elseExpr
decl_stmt|;
if|if
condition|(
name|elseExpr
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"ELSE"
argument_list|)
expr_stmt|;
name|elseExpr
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

