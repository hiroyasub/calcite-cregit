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
name|linq4j
operator|.
name|function
operator|.
name|Functions
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
name|OperandTypes
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
name|SqlOperandMetadata
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
name|SqlOperandTypeChecker
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
name|SqlOperandTypeInference
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
name|SqlReturnTypeInference
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
name|Util
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
name|org
operator|.
name|checkerframework
operator|.
name|dataflow
operator|.
name|qual
operator|.
name|Pure
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Nullness
operator|.
name|castNonNull
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
name|category
decl_stmt|;
specifier|private
specifier|final
annotation|@
name|Nullable
name|SqlIdentifier
name|sqlIdentifier
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new SqlFunction for a call to a built-in function.    *    * @param name                 Name of built-in function    * @param kind                 kind of operator implemented by function    * @param returnTypeInference  strategy to use for return type inference    * @param operandTypeInference strategy to use for parameter type inference    * @param operandTypeChecker   strategy to use for parameter type checking    * @param category             categorization for function    */
specifier|public
name|SqlFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
annotation|@
name|Nullable
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|SqlFunctionCategory
name|category
parameter_list|)
block|{
comment|// We leave sqlIdentifier as null to indicate
comment|// that this is a built-in.
name|this
argument_list|(
name|name
argument_list|,
literal|null
argument_list|,
name|kind
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|category
argument_list|)
expr_stmt|;
assert|assert
operator|!
operator|(
operator|(
name|category
operator|==
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_CONSTRUCTOR
operator|)
operator|&&
operator|(
name|returnTypeInference
operator|==
literal|null
operator|)
operator|)
assert|;
block|}
comment|/**    * Creates a placeholder SqlFunction for an invocation of a function with a    * possibly qualified name. This name must be resolved into either a built-in    * function or a user-defined function.    *    * @param sqlIdentifier        possibly qualified identifier for function    * @param returnTypeInference  strategy to use for return type inference    * @param operandTypeInference strategy to use for parameter type inference    * @param operandTypeChecker   strategy to use for parameter type checking    * @param paramTypes           array of parameter types    * @param funcType             function category    */
specifier|public
name|SqlFunction
parameter_list|(
name|SqlIdentifier
name|sqlIdentifier
parameter_list|,
annotation|@
name|Nullable
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|,
name|SqlFunctionCategory
name|funcType
parameter_list|)
block|{
name|this
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|sqlIdentifier
operator|.
name|names
argument_list|)
argument_list|,
name|sqlIdentifier
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|paramTypes
argument_list|,
name|funcType
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|SqlFunction
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|SqlIdentifier
name|sqlIdentifier
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
annotation|@
name|Nullable
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
annotation|@
name|Nullable
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|,
name|SqlFunctionCategory
name|category
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|sqlIdentifier
argument_list|,
name|kind
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|,
name|category
argument_list|)
expr_stmt|;
block|}
comment|/**    * Internal constructor.    */
specifier|protected
name|SqlFunction
parameter_list|(
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|SqlIdentifier
name|sqlIdentifier
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
annotation|@
name|Nullable
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
annotation|@
name|Nullable
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|,
name|SqlFunctionCategory
name|category
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
name|this
operator|.
name|sqlIdentifier
operator|=
name|sqlIdentifier
expr_stmt|;
name|this
operator|.
name|category
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|category
argument_list|,
literal|"category"
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
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
name|FUNCTION
return|;
block|}
comment|/**    * Returns the fully-qualified name of function, or null for a built-in    * function.    */
specifier|public
annotation|@
name|Nullable
name|SqlIdentifier
name|getSqlIdentifier
parameter_list|()
block|{
return|return
name|sqlIdentifier
return|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|getNameAsId
argument_list|()
return|;
block|}
comment|/** Use {@link SqlOperandMetadata#paramTypes(RelDataTypeFactory)} on the    * result of {@link #getOperandTypeChecker()}. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
name|RelDataType
argument_list|>
name|getParamTypes
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/** Use {@link SqlOperandMetadata#paramNames()} on the result of    * {@link #getOperandTypeChecker()}. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getParamNames
parameter_list|()
block|{
return|return
name|Functions
operator|.
name|generate
argument_list|(
name|castNonNull
argument_list|(
name|getParamTypes
argument_list|()
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|i
lambda|->
literal|"arg"
operator|+
name|i
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
name|call
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
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
comment|/**    * Return function category.    */
specifier|public
name|SqlFunctionCategory
name|getFunctionType
parameter_list|()
block|{
return|return
name|this
operator|.
name|category
return|;
block|}
comment|/**    * Returns whether this function allows a<code>DISTINCT</code> or<code>    * ALL</code> quantifier. The default is<code>false</code>; some aggregate    * functions return<code>true</code>.    */
annotation|@
name|Pure
specifier|public
name|boolean
name|isQuantifierAllowed
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
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
name|SqlLiteral
name|functionQuantifier
init|=
name|call
operator|.
name|getFunctionQuantifier
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
literal|null
operator|!=
name|functionQuantifier
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
name|functionQuantifier
argument_list|,
name|RESOURCE
operator|.
name|functionQuantifierNotAllowed
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
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|argNames
init|=
name|constructArgNameList
argument_list|(
name|call
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|args
init|=
name|constructOperandList
argument_list|(
name|validator
argument_list|,
name|call
argument_list|,
name|argNames
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
init|=
name|constructArgTypeList
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|,
name|args
argument_list|,
name|convertRowArgToColumnList
argument_list|)
decl_stmt|;
name|SqlFunction
name|function
init|=
operator|(
name|SqlFunction
operator|)
name|SqlUtil
operator|.
name|lookupRoutine
argument_list|(
name|validator
operator|.
name|getOperatorTable
argument_list|()
argument_list|,
name|validator
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getNameAsId
argument_list|()
argument_list|,
name|argTypes
argument_list|,
name|argNames
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
name|getKind
argument_list|()
argument_list|,
name|validator
operator|.
name|getCatalogReader
argument_list|()
operator|.
name|nameMatcher
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|// If the call already has an operator and its syntax is SPECIAL, it must
comment|// have been created intentionally by the parser.
if|if
condition|(
name|function
operator|==
literal|null
operator|&&
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getSyntax
argument_list|()
operator|==
name|SqlSyntax
operator|.
name|SPECIAL
operator|&&
name|call
operator|.
name|getOperator
argument_list|()
operator|instanceof
name|SqlFunction
operator|&&
name|validator
operator|.
name|getOperatorTable
argument_list|()
operator|.
name|getOperatorList
argument_list|()
operator|.
name|contains
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
argument_list|)
condition|)
block|{
name|function
operator|=
operator|(
name|SqlFunction
operator|)
name|call
operator|.
name|getOperator
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// if we have a match on function name and parameter count, but
comment|// couldn't find a function with  a COLUMN_LIST type, retry, but
comment|// this time, don't convert the row argument to a COLUMN_LIST type;
comment|// if we did find a match, go back and re-validate the row operands
comment|// (corresponding to column references), now that we can set the
comment|// scope to that of the source cursor referenced by that ColumnList
comment|// type
if|if
condition|(
name|convertRowArgToColumnList
operator|&&
name|containsRowArg
argument_list|(
name|args
argument_list|)
condition|)
block|{
if|if
condition|(
name|function
operator|==
literal|null
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
argument_list|,
name|validator
operator|.
name|getCatalogReader
argument_list|()
operator|.
name|nameMatcher
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
name|args
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
name|args
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
name|USER_DEFINED_CONSTRUCTOR
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
name|validCoercionType
label|:
if|if
condition|(
name|function
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|validator
operator|.
name|config
argument_list|()
operator|.
name|typeCoercionEnabled
argument_list|()
condition|)
block|{
comment|// try again if implicit type coercion is allowed.
name|function
operator|=
operator|(
name|SqlFunction
operator|)
name|SqlUtil
operator|.
name|lookupRoutine
argument_list|(
name|validator
operator|.
name|getOperatorTable
argument_list|()
argument_list|,
name|validator
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getNameAsId
argument_list|()
argument_list|,
name|argTypes
argument_list|,
name|argNames
argument_list|,
name|getFunctionType
argument_list|()
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
name|getKind
argument_list|()
argument_list|,
name|validator
operator|.
name|getCatalogReader
argument_list|()
operator|.
name|nameMatcher
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// try to coerce the function arguments to the declared sql type name.
comment|// if we succeed, the arguments would be wrapped with CAST operator.
if|if
condition|(
name|function
operator|!=
literal|null
condition|)
block|{
name|TypeCoercion
name|typeCoercion
init|=
name|validator
operator|.
name|getTypeCoercion
argument_list|()
decl_stmt|;
if|if
condition|(
name|typeCoercion
operator|.
name|userDefinedFunctionCoercion
argument_list|(
name|scope
argument_list|,
name|call
argument_list|,
name|function
argument_list|)
condition|)
block|{
break|break
name|validCoercionType
break|;
block|}
block|}
block|}
comment|// check if the identifier represents type
specifier|final
name|SqlFunction
name|x
init|=
operator|(
name|SqlFunction
operator|)
name|call
operator|.
name|getOperator
argument_list|()
decl_stmt|;
specifier|final
name|SqlIdentifier
name|identifier
init|=
name|Util
operator|.
name|first
argument_list|(
name|x
operator|.
name|getSqlIdentifier
argument_list|()
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
name|x
operator|.
name|getName
argument_list|()
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|type
init|=
name|validator
operator|.
name|getCatalogReader
argument_list|()
operator|.
name|getNamedType
argument_list|(
name|identifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|function
operator|=
operator|new
name|SqlTypeConstructorFunction
argument_list|(
name|identifier
argument_list|,
name|type
argument_list|)
expr_stmt|;
break|break
name|validCoercionType
break|;
block|}
comment|// if function doesn't exist within operator table and known function
comment|// handling is turned off then create a more permissive function
if|if
condition|(
name|function
operator|==
literal|null
operator|&&
name|validator
operator|.
name|config
argument_list|()
operator|.
name|lenientOperatorLookup
argument_list|()
condition|)
block|{
name|function
operator|=
operator|new
name|SqlUnresolvedFunction
argument_list|(
name|identifier
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|VARIADIC
argument_list|,
literal|null
argument_list|,
name|x
operator|.
name|getFunctionType
argument_list|()
argument_list|)
expr_stmt|;
break|break
name|validCoercionType
break|;
block|}
throw|throw
name|validator
operator|.
name|handleUnresolvedFunction
argument_list|(
name|call
argument_list|,
name|this
argument_list|,
name|argTypes
argument_list|,
name|argNames
argument_list|)
throw|;
block|}
comment|// REVIEW jvs 25-Mar-2005:  This is, in a sense, expanding
comment|// identifiers, but we ignore shouldExpandIdentifiers()
comment|// because otherwise later validation code will
comment|// choke on the unresolved function.
operator|(
operator|(
name|SqlBasicCall
operator|)
name|call
operator|)
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
specifier|private
specifier|static
name|boolean
name|containsRowArg
parameter_list|(
name|List
argument_list|<
name|SqlNode
argument_list|>
name|args
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|operand
range|:
name|args
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
end_class

end_unit

