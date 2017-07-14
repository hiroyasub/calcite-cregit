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
name|util
operator|.
name|SqlVisitor
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
name|SqlMoniker
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
name|SqlMonotonicity
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
comment|/**  * A<code>SqlCall</code> is a call to an {@link SqlOperator operator}.  * (Operators can be used to describe any syntactic construct, so in practice,  * every non-leaf node in a SQL parse tree is a<code>SqlCall</code> of some  * kind.)  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlCall
extends|extends
name|SqlNode
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlCall
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Whether this call was created by expanding a parentheses-free call to    * what was syntactically an identifier.    */
specifier|public
name|boolean
name|isExpanded
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Changes the value of an operand. Allows some rewrite by    * {@link SqlValidator}; use sparingly.    *    * @param i Operand index    * @param operand Operand value    */
specifier|public
name|void
name|setOperand
parameter_list|(
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|getOperator
argument_list|()
operator|.
name|getKind
argument_list|()
return|;
block|}
specifier|public
specifier|abstract
name|SqlOperator
name|getOperator
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
function_decl|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
parameter_list|<
name|S
extends|extends
name|SqlNode
parameter_list|>
name|S
name|operand
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
name|S
operator|)
name|getOperandList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
specifier|public
name|int
name|operandCount
parameter_list|()
block|{
return|return
name|getOperandList
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlNode
name|clone
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
init|=
name|getOperandList
argument_list|()
decl_stmt|;
return|return
name|getOperator
argument_list|()
operator|.
name|createCall
argument_list|(
name|getFunctionQuantifier
argument_list|()
argument_list|,
name|pos
argument_list|,
name|operandList
operator|.
name|toArray
argument_list|(
operator|new
name|SqlNode
index|[
name|operandList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlOperator
name|operator
init|=
name|getOperator
argument_list|()
decl_stmt|;
specifier|final
name|SqlDialect
name|dialect
init|=
name|writer
operator|.
name|getDialect
argument_list|()
decl_stmt|;
if|if
condition|(
name|leftPrec
operator|>
name|operator
operator|.
name|getLeftPrec
argument_list|()
operator|||
operator|(
name|operator
operator|.
name|getRightPrec
argument_list|()
operator|<=
name|rightPrec
operator|&&
operator|(
name|rightPrec
operator|!=
literal|0
operator|)
operator|)
operator|||
name|writer
operator|.
name|isAlwaysUseParentheses
argument_list|()
operator|&&
name|isA
argument_list|(
name|SqlKind
operator|.
name|EXPRESSION
argument_list|)
condition|)
block|{
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
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
name|dialect
operator|.
name|unparseCall
argument_list|(
name|writer
argument_list|,
name|this
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dialect
operator|.
name|unparseCall
argument_list|(
name|writer
argument_list|,
name|this
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Validates this call.    *    *<p>The default implementation delegates the validation to the operator's    * {@link SqlOperator#validateCall}. Derived classes may override (as do,    * for example {@link SqlSelect} and {@link SqlUpdate}).    */
specifier|public
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|validator
operator|.
name|validateCall
argument_list|(
name|this
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|findValidOptions
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|Collection
argument_list|<
name|SqlMoniker
argument_list|>
name|hintList
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|operand
range|:
name|getOperandList
argument_list|()
control|)
block|{
if|if
condition|(
name|operand
operator|instanceof
name|SqlIdentifier
condition|)
block|{
name|SqlIdentifier
name|id
init|=
operator|(
name|SqlIdentifier
operator|)
name|operand
decl_stmt|;
name|SqlParserPos
name|idPos
init|=
name|id
operator|.
name|getParserPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|idPos
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|pos
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
operator|(
operator|(
name|SqlValidatorImpl
operator|)
name|validator
operator|)
operator|.
name|lookupNameCompletionHints
argument_list|(
name|scope
argument_list|,
name|id
operator|.
name|names
argument_list|,
name|pos
argument_list|,
name|hintList
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
comment|// no valid options
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equalsDeep
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|Litmus
name|litmus
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|SqlCall
operator|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|node
argument_list|)
return|;
block|}
name|SqlCall
name|that
init|=
operator|(
name|SqlCall
operator|)
name|node
decl_stmt|;
comment|// Compare operators by name, not identity, because they may not
comment|// have been resolved yet. Use case insensitive comparison since
comment|// this may be a case insensitive system.
if|if
condition|(
operator|!
name|this
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|that
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"{} != {}"
argument_list|,
name|this
argument_list|,
name|node
argument_list|)
return|;
block|}
return|return
name|equalDeep
argument_list|(
name|this
operator|.
name|getOperandList
argument_list|()
argument_list|,
name|that
operator|.
name|getOperandList
argument_list|()
argument_list|,
name|litmus
argument_list|)
return|;
block|}
comment|/**    * Returns a string describing the actual argument types of a call, e.g.    * "SUBSTR(VARCHAR(12), NUMBER(3,2), INTEGER)".    */
specifier|protected
name|String
name|getCallSignature
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|signatureList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|SqlNode
name|operand
range|:
name|getOperandList
argument_list|()
control|)
block|{
specifier|final
name|RelDataType
name|argType
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operand
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|argType
condition|)
block|{
continue|continue;
block|}
name|signatureList
operator|.
name|add
argument_list|(
name|argType
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|SqlUtil
operator|.
name|getOperatorSignature
argument_list|(
name|getOperator
argument_list|()
argument_list|,
name|signatureList
argument_list|)
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
comment|// Delegate to operator.
specifier|final
name|SqlCallBinding
name|binding
init|=
operator|new
name|SqlCallBinding
argument_list|(
name|scope
operator|.
name|getValidator
argument_list|()
argument_list|,
name|scope
argument_list|,
name|this
argument_list|)
decl_stmt|;
return|return
name|getOperator
argument_list|()
operator|.
name|getMonotonicity
argument_list|(
name|binding
argument_list|)
return|;
block|}
comment|/**    * Test to see if it is the function COUNT(*)    *    * @return boolean true if function call to COUNT(*)    */
specifier|public
name|boolean
name|isCountStar
parameter_list|()
block|{
if|if
condition|(
name|getOperator
argument_list|()
operator|.
name|isName
argument_list|(
literal|"COUNT"
argument_list|)
operator|&&
name|operandCount
argument_list|()
operator|==
literal|1
condition|)
block|{
specifier|final
name|SqlNode
name|parm
init|=
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|parm
operator|instanceof
name|SqlIdentifier
condition|)
block|{
name|SqlIdentifier
name|id
init|=
operator|(
name|SqlIdentifier
operator|)
name|parm
decl_stmt|;
if|if
condition|(
name|id
operator|.
name|isStar
argument_list|()
operator|&&
name|id
operator|.
name|names
operator|.
name|size
argument_list|()
operator|==
literal|1
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
specifier|public
name|SqlLiteral
name|getFunctionQuantifier
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCall.java
end_comment

end_unit

