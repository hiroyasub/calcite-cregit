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
operator|.
name|fun
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
name|parser
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
name|validate
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

begin_comment
comment|/**  * Internal operator, by which the parser represents a continued string literal.  *  *<p>The string fragments are {@link SqlLiteral} objects, all of the same type,  * collected as the operands of an {@link SqlCall} using this operator. After  * validation, the fragments will be concatenated into a single literal.  *  *<p>For a chain of {@link org.eigenbase.sql.SqlCharStringLiteral} objects, a  * {@link SqlCollation} object is attached only to the head of the chain.  *  * @author Marc Berkowitz  * @version $Id$  * @since Sep 7, 2004  */
end_comment

begin_class
specifier|public
class|class
name|SqlLiteralChainOperator
extends|extends
name|SqlInternalOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
name|SqlLiteralChainOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"$LiteralChain"
argument_list|,
name|SqlKind
operator|.
name|LITERAL_CHAIN
argument_list|,
literal|80
argument_list|,
literal|true
argument_list|,
comment|// precedence tighter than the * and || operators
name|SqlTypeStrategies
operator|.
name|rtiFirstArgType
argument_list|,
name|SqlTypeStrategies
operator|.
name|otiFirstKnown
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcVariadic
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// all operands must be the same type
specifier|private
name|boolean
name|argTypesValid
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|)
block|{
if|if
condition|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
operator|<
literal|2
condition|)
block|{
return|return
literal|true
return|;
comment|// nothing to compare
block|}
name|SqlNode
name|operand
init|=
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operands
index|[
literal|0
index|]
decl_stmt|;
name|RelDataType
name|firstType
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|operand
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|operand
operator|=
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|operands
index|[
name|i
index|]
expr_stmt|;
name|RelDataType
name|otherType
init|=
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|deriveType
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|operand
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|sameNamedType
argument_list|(
name|firstType
argument_list|,
name|otherType
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
if|if
condition|(
operator|!
name|argTypesValid
argument_list|(
name|callBinding
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
comment|// Result type is the same as all the args, but its size is the
comment|// total size.
comment|// REVIEW mb 8/8/04: Possibly this can be achieved by combining
comment|// the strategy useFirstArgType with a new transformer.
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
comment|// Here we know all the operands have the same type,
comment|// which has a size (precision), but not a scale.
name|RelDataType
name|ret
init|=
name|opBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SqlTypeName
name|typeName
init|=
name|ret
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
assert|assert
name|typeName
operator|.
name|allowsPrecNoScale
argument_list|()
operator|:
literal|"LiteralChain has impossible operand type "
operator|+
name|typeName
assert|;
name|int
name|size
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelDataType
name|type
range|:
name|opBinding
operator|.
name|collectOperandTypes
argument_list|()
control|)
block|{
name|size
operator|+=
name|type
operator|.
name|getPrecision
argument_list|()
expr_stmt|;
assert|assert
operator|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
operator|.
name|equals
argument_list|(
name|typeName
argument_list|)
operator|)
assert|;
block|}
return|return
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|size
argument_list|)
return|;
block|}
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|String
name|opName
parameter_list|)
block|{
return|return
name|opName
operator|+
literal|"(...)"
return|;
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
comment|// per the SQL std, each string fragment must be on a different line
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|call
operator|.
name|operands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SqlParserPos
name|prevPos
init|=
name|call
operator|.
name|operands
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|getParserPosition
argument_list|()
decl_stmt|;
specifier|final
name|SqlNode
name|operand
init|=
name|call
operator|.
name|operands
index|[
name|i
index|]
decl_stmt|;
name|SqlParserPos
name|pos
init|=
name|operand
operator|.
name|getParserPosition
argument_list|()
decl_stmt|;
if|if
condition|(
name|pos
operator|.
name|getLineNum
argument_list|()
operator|<=
name|prevPos
operator|.
name|getLineNum
argument_list|()
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|operand
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|StringFragsOnSameLine
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
index|[]
name|rands
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
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
literal|""
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|SqlCollation
name|collation
init|=
literal|null
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
name|rands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|SqlLiteral
name|rand
init|=
operator|(
name|SqlLiteral
operator|)
name|rands
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
comment|// SQL:2003 says there must be a newline between string
comment|// fragments.
name|writer
operator|.
name|newlineAndIndent
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|rand
operator|instanceof
name|SqlCharStringLiteral
condition|)
block|{
name|NlsString
name|nls
init|=
operator|(
operator|(
name|SqlCharStringLiteral
operator|)
name|rand
operator|)
operator|.
name|getNlsString
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|collation
operator|=
name|nls
operator|.
name|getCollation
argument_list|()
expr_stmt|;
comment|// print with prefix
name|writer
operator|.
name|literal
argument_list|(
name|nls
operator|.
name|asSql
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// print without prefix
name|writer
operator|.
name|literal
argument_list|(
name|nls
operator|.
name|asSql
argument_list|(
literal|false
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// print with prefix
name|rand
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// print without prefix
if|if
condition|(
name|rand
operator|.
name|getTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BINARY
condition|)
block|{
name|BitString
name|bs
init|=
operator|(
name|BitString
operator|)
name|rand
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|writer
operator|.
name|literal
argument_list|(
literal|"'"
operator|+
name|bs
operator|.
name|toHexString
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|literal
argument_list|(
literal|"'"
operator|+
name|rand
operator|.
name|toValue
argument_list|()
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|collation
operator|!=
literal|null
condition|)
block|{
name|collation
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
comment|/**      * Concatenates the operands of a call to this operator.      */
specifier|public
specifier|static
name|SqlLiteral
name|concatenateOperands
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
assert|assert
name|call
operator|.
name|operands
operator|.
name|length
operator|>
literal|0
assert|;
assert|assert
name|call
operator|.
name|operands
index|[
literal|0
index|]
operator|instanceof
name|SqlLiteral
operator|:
name|call
operator|.
name|operands
index|[
literal|0
index|]
operator|.
name|getClass
argument_list|()
assert|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operandList
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|call
operator|.
name|operands
argument_list|)
decl_stmt|;
name|SqlLiteral
index|[]
name|fragments
init|=
name|operandList
operator|.
name|toArray
argument_list|(
operator|new
name|SqlLiteral
index|[
name|operandList
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
name|SqlUtil
operator|.
name|concatenateLiterals
argument_list|(
name|fragments
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlLiteralChainOperator.java
end_comment

end_unit

