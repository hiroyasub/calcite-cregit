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

begin_comment
comment|/**  * A<code>SqlFunction</code> is a type of operator which has conventional  * function-call syntax.  */
end_comment

begin_class
specifier|public
class|class
name|SqlFunction
extends|extends
name|SqlOperator
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlFunctionCategory
name|functionType
decl_stmt|;
specifier|private
specifier|final
name|SqlIdentifier
name|sqlIdentifier
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
index|[]
name|paramTypes
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new SqlFunction for a call to a builtin function.    *    * @param name                 of builtin function    * @param kind                 kind of operator implemented by function    * @param returnTypeInference  strategy to use for return type inference    * @param operandTypeInference strategy to use for parameter type inference    * @param operandTypeChecker   strategy to use for parameter type checking    * @param funcType             categorization for function    */
specifier|public
name|SqlFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|SqlFunctionCategory
name|funcType
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
expr_stmt|;
assert|assert
operator|!
operator|(
operator|(
name|funcType
operator|==
name|SqlFunctionCategory
operator|.
name|UserDefinedConstructor
operator|)
operator|&&
operator|(
name|returnTypeInference
operator|==
literal|null
operator|)
operator|)
assert|;
name|this
operator|.
name|functionType
operator|=
name|funcType
expr_stmt|;
comment|// NOTE jvs 18-Jan-2005:  we leave sqlIdentifier as null to indicate
comment|// that this is a builtin.  Same for paramTypes.
name|this
operator|.
name|sqlIdentifier
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|paramTypes
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Creates a placeholder SqlFunction for an invocation of a function with a    * possibly qualified name. This name must be resolved into either a builtin    * function or a user-defined function.    *    * @param sqlIdentifier        possibly qualified identifier for function    * @param returnTypeInference  strategy to use for return type inference    * @param operandTypeInference strategy to use for parameter type inference    * @param operandTypeChecker   strategy to use for parameter type checking    * @param paramTypes           array of parameter types    * @param funcType             function category    */
specifier|public
name|SqlFunction
parameter_list|(
name|SqlIdentifier
name|sqlIdentifier
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|RelDataType
index|[]
name|paramTypes
parameter_list|,
name|SqlFunctionCategory
name|funcType
parameter_list|)
block|{
name|super
argument_list|(
name|sqlIdentifier
operator|.
name|names
index|[
name|sqlIdentifier
operator|.
name|names
operator|.
name|length
operator|-
literal|1
index|]
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
expr_stmt|;
comment|// assert !(funcType == SqlFunctionCategory.UserDefinedConstructor
comment|//&&           returnTypeInference == null);
name|this
operator|.
name|sqlIdentifier
operator|=
name|sqlIdentifier
expr_stmt|;
name|this
operator|.
name|functionType
operator|=
name|funcType
expr_stmt|;
name|this
operator|.
name|paramTypes
operator|=
name|paramTypes
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|Function
return|;
block|}
comment|/**    * @return fully qualified name of function, or null for a builtin function    */
specifier|public
name|SqlIdentifier
name|getSqlIdentifier
parameter_list|()
block|{
return|return
name|sqlIdentifier
return|;
block|}
comment|/**    * @return fully qualified name of function    */
specifier|public
name|SqlIdentifier
name|getNameAsId
parameter_list|()
block|{
if|if
condition|(
name|sqlIdentifier
operator|!=
literal|null
condition|)
block|{
return|return
name|sqlIdentifier
return|;
block|}
return|return
operator|new
name|SqlIdentifier
argument_list|(
name|getName
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
return|;
block|}
comment|/**    * @return array of parameter types, or null for builtin function    */
specifier|public
name|RelDataType
index|[]
name|getParamTypes
parameter_list|()
block|{
return|return
name|paramTypes
return|;
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
name|operands
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|getSyntax
argument_list|()
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|this
argument_list|,
name|operands
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return function category    */
specifier|public
name|SqlFunctionCategory
name|getFunctionType
parameter_list|()
block|{
return|return
name|this
operator|.
name|functionType
return|;
block|}
comment|/**    * Returns whether this function allows a<code>DISTINCT</code> or<code>    * ALL</code> quantifier. The default is<code>false</code>; some aggregate    * functions return<code>true</code>.    */
specifier|public
name|boolean
name|isQuantifierAllowed
parameter_list|()
block|{
return|return
literal|false
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
comment|// This implementation looks for the quantifier keywords DISTINCT or
comment|// ALL as the first operand in the list.  If found then the literal is
comment|// not called to validate itself.  Further the function is checked to
comment|// make sure that a quantifier is valid for that particular function.
comment|//
comment|// If the first operand does not appear to be a quantifier then the
comment|// parent ValidateCall is invoked to do normal function validation.
name|super
operator|.
name|validateCall
argument_list|(
name|call
argument_list|,
name|validator
argument_list|,
name|scope
argument_list|,
name|operandScope
argument_list|)
expr_stmt|;
name|validateQuantifier
argument_list|(
name|validator
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
comment|/**    * Throws a validation error if a DISTINCT or ALL quantifier is present but    * not allowed.    */
specifier|protected
name|void
name|validateQuantifier
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
if|if
condition|(
operator|(
literal|null
operator|!=
name|call
operator|.
name|getFunctionQuantifier
argument_list|()
operator|)
operator|&&
operator|!
name|isQuantifierAllowed
argument_list|()
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
operator|.
name|getFunctionQuantifier
argument_list|()
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|FunctionQuantifierNotAllowed
operator|.
name|ex
argument_list|(
name|call
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
return|return
name|deriveType
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
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
parameter_list|,
name|boolean
name|convertRowArgToColumnList
parameter_list|)
block|{
specifier|final
name|SqlNode
index|[]
name|operands
init|=
name|call
operator|.
name|operands
decl_stmt|;
name|RelDataType
index|[]
name|argTypes
init|=
operator|new
name|RelDataType
index|[
name|operands
operator|.
name|length
index|]
decl_stmt|;
comment|// Scope for operands. Usually the same as 'scope'.
specifier|final
name|SqlValidatorScope
name|operandScope
init|=
name|scope
operator|.
name|getOperandScope
argument_list|(
name|call
argument_list|)
decl_stmt|;
comment|// Indicate to the validator that we're validating a new function call
name|validator
operator|.
name|pushFunctionCall
argument_list|()
expr_stmt|;
try|try
block|{
name|boolean
name|containsRowArg
init|=
literal|false
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
name|operands
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|RelDataType
name|nodeType
decl_stmt|;
comment|// for row arguments that should be converted to ColumnList
comment|// types, set the nodeType to a ColumnList type but defer
comment|// validating the arguments of the row constructor until we know
comment|// for sure that the row argument maps to a ColumnList type
if|if
condition|(
name|operands
index|[
name|i
index|]
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|ROW
operator|&&
name|convertRowArgToColumnList
condition|)
block|{
name|containsRowArg
operator|=
literal|true
expr_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|validator
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|nodeType
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|COLUMN_LIST
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodeType
operator|=
name|validator
operator|.
name|deriveType
argument_list|(
name|operandScope
argument_list|,
name|operands
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|validator
operator|.
name|setValidatedNodeType
argument_list|(
name|operands
index|[
name|i
index|]
argument_list|,
name|nodeType
argument_list|)
expr_stmt|;
name|argTypes
index|[
name|i
index|]
operator|=
name|nodeType
expr_stmt|;
block|}
name|SqlFunction
name|function
init|=
name|SqlUtil
operator|.
name|lookupRoutine
argument_list|(
name|validator
operator|.
name|getOperatorTable
argument_list|()
argument_list|,
name|getNameAsId
argument_list|()
argument_list|,
name|argTypes
argument_list|,
name|getFunctionType
argument_list|()
argument_list|)
decl_stmt|;
comment|// if we have a match on function name and parameter count, but
comment|// couldn't find a function with  a COLUMN_LIST type, retry, but
comment|// this time, don't convert the row argument to a COLUMN_LIST type;
comment|// if we did find a match, go back and re-validate the row operands
comment|// (corresponding to column references), now that we can set the
comment|// scope to that of the source cursor referenced by that ColumnList
comment|// type
if|if
condition|(
name|containsRowArg
condition|)
block|{
if|if
condition|(
operator|(
name|function
operator|==
literal|null
operator|)
operator|&&
name|SqlUtil
operator|.
name|matchRoutinesByParameterCount
argument_list|(
name|validator
operator|.
name|getOperatorTable
argument_list|()
argument_list|,
name|getNameAsId
argument_list|()
argument_list|,
name|argTypes
argument_list|,
name|getFunctionType
argument_list|()
argument_list|)
condition|)
block|{
comment|// remove the already validated node types corresponding to
comment|// row arguments before re-validating
for|for
control|(
name|SqlNode
name|operand
range|:
name|operands
control|)
block|{
if|if
condition|(
name|operand
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|ROW
condition|)
block|{
name|validator
operator|.
name|removeValidatedNodeType
argument_list|(
name|operand
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|deriveType
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|,
literal|false
argument_list|)
return|;
block|}
if|else if
condition|(
name|function
operator|!=
literal|null
condition|)
block|{
name|validator
operator|.
name|validateColumnListParams
argument_list|(
name|function
argument_list|,
name|argTypes
argument_list|,
name|operands
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|getFunctionType
argument_list|()
operator|==
name|SqlFunctionCategory
operator|.
name|UserDefinedConstructor
condition|)
block|{
return|return
name|validator
operator|.
name|deriveConstructorType
argument_list|(
name|scope
argument_list|,
name|call
argument_list|,
name|this
argument_list|,
name|function
argument_list|,
name|argTypes
argument_list|)
return|;
block|}
if|if
condition|(
name|function
operator|==
literal|null
condition|)
block|{
name|validator
operator|.
name|handleUnresolvedFunction
argument_list|(
name|call
argument_list|,
name|this
argument_list|,
name|argTypes
argument_list|)
expr_stmt|;
block|}
comment|// REVIEW jvs 25-Mar-2005:  This is, in a sense, expanding
comment|// identifiers, but we ignore shouldExpandIdentifiers()
comment|// because otherwise later validation code will
comment|// choke on the unresolved function.
name|call
operator|.
name|setOperator
argument_list|(
name|function
argument_list|)
expr_stmt|;
return|return
name|function
operator|.
name|validateOperands
argument_list|(
name|validator
argument_list|,
name|operandScope
argument_list|,
name|call
argument_list|)
return|;
block|}
finally|finally
block|{
name|validator
operator|.
name|popFunctionCall
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlFunction.java
end_comment

end_unit

