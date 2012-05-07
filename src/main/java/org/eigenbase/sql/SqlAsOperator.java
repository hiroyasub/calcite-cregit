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
name|sql
package|;
end_package

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
name|resource
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
name|type
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
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The<code>AS</code> operator associates an expression with an alias.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SqlAsOperator
extends|extends
name|SqlSpecialOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an AS operator.      */
specifier|public
name|SqlAsOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"AS"
argument_list|,
name|SqlKind
operator|.
name|AS
argument_list|,
literal|20
argument_list|,
literal|true
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiFirstArgType
argument_list|,
name|SqlTypeStrategies
operator|.
name|otiReturnType
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcAnyX2
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
assert|assert
name|operands
operator|.
name|length
operator|>=
literal|2
assert|;
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
name|Simple
argument_list|)
decl_stmt|;
name|operands
index|[
literal|0
index|]
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|needsSpace
init|=
literal|true
decl_stmt|;
name|writer
operator|.
name|setNeedWhitespace
argument_list|(
name|needsSpace
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"AS"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setNeedWhitespace
argument_list|(
name|needsSpace
argument_list|)
expr_stmt|;
name|operands
index|[
literal|1
index|]
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getRightPrec
argument_list|()
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
if|if
condition|(
name|operands
operator|.
name|length
operator|>
literal|2
condition|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame1
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|Simple
argument_list|,
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<
name|operands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|operand
init|=
name|operands
index|[
name|i
index|]
decl_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|operand
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
name|frame1
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
comment|// The base method validates all operands. We override because
comment|// we don't want to validate the identifier.
specifier|final
name|SqlNode
index|[]
name|operands
init|=
name|call
operator|.
name|operands
decl_stmt|;
assert|assert
name|operands
operator|.
name|length
operator|==
literal|2
assert|;
assert|assert
name|operands
index|[
literal|1
index|]
operator|instanceof
name|SqlIdentifier
assert|;
name|operands
index|[
literal|0
index|]
operator|.
name|validateExpr
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|)
expr_stmt|;
name|SqlIdentifier
name|id
init|=
operator|(
name|SqlIdentifier
operator|)
name|operands
index|[
literal|1
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|id
operator|.
name|isSimple
argument_list|()
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|id
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|AliasMustBeSimpleIdentifier
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|void
name|acceptCall
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|boolean
name|onlyExpressions
parameter_list|,
name|SqlBasicVisitor
operator|.
name|ArgHandler
argument_list|<
name|R
argument_list|>
name|argHandler
parameter_list|)
block|{
if|if
condition|(
name|onlyExpressions
condition|)
block|{
comment|// Do not visit operands[1] -- it is not an expression.
name|argHandler
operator|.
name|visitChild
argument_list|(
name|visitor
argument_list|,
name|call
argument_list|,
literal|0
argument_list|,
name|call
operator|.
name|operands
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|acceptCall
argument_list|(
name|visitor
argument_list|,
name|call
argument_list|,
name|onlyExpressions
argument_list|,
name|argHandler
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// special case for AS:  never try to derive type for alias
name|RelDataType
name|nodeType
init|=
name|validator
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|call
operator|.
name|operands
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
assert|assert
name|nodeType
operator|!=
literal|null
assert|;
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
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
return|return
name|call
operator|.
name|operands
index|[
literal|0
index|]
operator|.
name|getMonotonicity
argument_list|(
name|scope
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlAsOperator.java
end_comment

end_unit

