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
comment|/**  * A<code>SqlOperator</code> is a type of node in a SQL parse tree (it is NOT a  * node in a SQL parse tree). It includes functions, operators such as '=', and  * syntactic constructs such as 'case' statements. Operators may represent  * query-level expressions (e.g. {@link SqlSelectOperator} or row-level  * expressions (e.g. {@link org.eigenbase.sql.fun.SqlBetweenOperator}.  *  *<p>Operators have<em>formal operands</em>, meaning ordered (and optionally  * named) placeholders for the values they operate on. For example, the division  * operator takes two operands; the first is the numerator and the second is the  * denominator. In the context of subclass {@link SqlFunction}, formal operands  * are referred to as<em>parameters</em>.  *  *<p>When an operator is instantiated via a {@link SqlCall}, it is supplied  * with<em>actual operands</em>. For example, in the expression<code>3 /  * 5</code>, the literal expression<code>3</code> is the actual operand  * corresponding to the numerator, and<code>5</code> is the actual operand  * corresponding to the denominator. In the context of SqlFunction, actual  * operands are referred to as<em>arguments</em>  *  *<p>In many cases, the formal/actual distinction is clear from context, in  * which case we drop these qualifiers.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlOperator
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|String
name|NL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|/**    * Maximum precedence.    */
specifier|protected
specifier|static
specifier|final
name|int
name|MDX_PRECEDENCE
init|=
literal|200
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * The name of the operator/function. Ex. "OVERLAY" or "TRIM"    */
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
comment|/**    * See {@link SqlKind}. It's possible to have a name that doesn't match the    * kind    */
specifier|public
specifier|final
name|SqlKind
name|kind
decl_stmt|;
comment|/**    * The precedence with which this operator binds to the expression to the    * left. This is less than the right precedence if the operator is    * left-associative.    */
specifier|private
specifier|final
name|int
name|leftPrec
decl_stmt|;
comment|/**    * The precedence with which this operator binds to the expression to the    * right. This is more than the left precedence if the operator is    * left-associative.    */
specifier|private
specifier|final
name|int
name|rightPrec
decl_stmt|;
comment|/**    * used to infer the return type of a call to this operator    */
specifier|private
specifier|final
name|SqlReturnTypeInference
name|returnTypeInference
decl_stmt|;
comment|/**    * used to infer types of unknown operands    */
specifier|private
specifier|final
name|SqlOperandTypeInference
name|operandTypeInference
decl_stmt|;
comment|/**    * used to validate operand types    */
specifier|private
specifier|final
name|SqlOperandTypeChecker
name|operandTypeChecker
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an operator.    *    * @pre kind != null    */
specifier|protected
name|SqlOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|leftPrecedence
parameter_list|,
name|int
name|rightPrecedence
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
name|Util
operator|.
name|pre
argument_list|(
name|kind
operator|!=
literal|null
argument_list|,
literal|"kind != null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|kind
operator|=
name|kind
expr_stmt|;
name|this
operator|.
name|leftPrec
operator|=
name|leftPrecedence
expr_stmt|;
name|this
operator|.
name|rightPrec
operator|=
name|rightPrecedence
expr_stmt|;
name|this
operator|.
name|returnTypeInference
operator|=
name|returnTypeInference
expr_stmt|;
name|this
operator|.
name|operandTypeInference
operator|=
name|operandTypeInference
expr_stmt|;
name|this
operator|.
name|operandTypeChecker
operator|=
name|operandTypeChecker
expr_stmt|;
block|}
comment|/**    * Creates an operator specifying left/right associativity.    */
specifier|protected
name|SqlOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|prec
parameter_list|,
name|boolean
name|leftAssoc
parameter_list|,
name|SqlReturnTypeInference
name|returnTypeInference
parameter_list|,
name|SqlOperandTypeInference
name|operandTypeInference
parameter_list|,
name|SqlOperandTypeChecker
name|operandTypeChecker
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
name|leftPrec
argument_list|(
name|prec
argument_list|,
name|leftAssoc
argument_list|)
argument_list|,
name|rightPrec
argument_list|(
name|prec
argument_list|,
name|leftAssoc
argument_list|)
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
specifier|static
name|int
name|leftPrec
parameter_list|(
name|int
name|prec
parameter_list|,
name|boolean
name|leftAssoc
parameter_list|)
block|{
assert|assert
operator|(
name|prec
operator|%
literal|2
operator|)
operator|==
literal|0
assert|;
if|if
condition|(
operator|!
name|leftAssoc
condition|)
block|{
operator|++
name|prec
expr_stmt|;
block|}
return|return
name|prec
return|;
block|}
specifier|protected
specifier|static
name|int
name|rightPrec
parameter_list|(
name|int
name|prec
parameter_list|,
name|boolean
name|leftAssoc
parameter_list|)
block|{
assert|assert
operator|(
name|prec
operator|%
literal|2
operator|)
operator|==
literal|0
assert|;
if|if
condition|(
name|leftAssoc
condition|)
block|{
operator|++
name|prec
expr_stmt|;
block|}
return|return
name|prec
return|;
block|}
specifier|public
name|SqlOperandTypeChecker
name|getOperandTypeChecker
parameter_list|()
block|{
return|return
name|operandTypeChecker
return|;
block|}
comment|/**    * Returns a constraint on the number of operands expected by this operator.    * Subclasses may override this method; when they don't, the range is    * derived from the {@link SqlOperandTypeChecker} associated with this    * operator.    *    * @return acceptable range    */
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
if|if
condition|(
name|operandTypeChecker
operator|!=
literal|null
condition|)
block|{
return|return
name|operandTypeChecker
operator|.
name|getOperandCountRange
argument_list|()
return|;
block|}
comment|// If you see this error you need to override this method
comment|// or give operandTypeChecker a value.
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|this
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|kind
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|int
name|getLeftPrec
parameter_list|()
block|{
return|return
name|leftPrec
return|;
block|}
specifier|public
name|int
name|getRightPrec
parameter_list|()
block|{
return|return
name|rightPrec
return|;
block|}
comment|/**    * Returns the syntactic type of this operator.    *    * @post return != null    */
specifier|public
specifier|abstract
name|SqlSyntax
name|getSyntax
parameter_list|()
function_decl|;
comment|/**    * Creates a call to this operand with an array of operands.    *    *<p>The position of the resulting call is the union of the<code>    * pos</code> and the positions of all of the operands.    *    * @param functionQualifier function qualifier (e.g. "DISTINCT"), may be    * @param pos               parser position of the identifier of the call    * @param operands          array of operands    */
specifier|public
name|SqlCall
name|createCall
parameter_list|(
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
name|pos
operator|=
name|pos
operator|.
name|plusAll
argument_list|(
name|operands
argument_list|)
expr_stmt|;
return|return
operator|new
name|SqlCall
argument_list|(
name|this
argument_list|,
name|operands
argument_list|,
name|pos
argument_list|,
literal|false
argument_list|,
name|functionQualifier
argument_list|)
return|;
block|}
comment|/**    * Creates a call to this operand with an array of operands.    *    *<p>The position of the resulting call is the union of the<code>    * pos</code> and the positions of all of the operands.    *    * @param pos      Parser position    * @param operands List of arguments    * @return call to this operator    */
specifier|public
specifier|final
name|SqlCall
name|createCall
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|SqlNode
modifier|...
name|operands
parameter_list|)
block|{
return|return
name|createCall
argument_list|(
literal|null
argument_list|,
name|pos
argument_list|,
name|operands
argument_list|)
return|;
block|}
comment|/**    * Creates a call to this operand with a list of operands contained in a    * {@link SqlNodeList}.    *    *<p>The position of the resulting call inferred from the SqlNodeList.    *    * @param nodeList List of arguments    * @return call to this operator    */
specifier|public
specifier|final
name|SqlCall
name|createCall
parameter_list|(
name|SqlNodeList
name|nodeList
parameter_list|)
block|{
return|return
name|createCall
argument_list|(
literal|null
argument_list|,
name|nodeList
operator|.
name|getParserPosition
argument_list|()
argument_list|,
name|nodeList
operator|.
name|toArray
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a call to this operand with a list of operands.    *    *<p>The position of the resulting call is the union of the<code>    * pos</code> and the positions of all of the operands.    */
specifier|public
specifier|final
name|SqlCall
name|createCall
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|SqlNode
argument_list|>
name|operandList
parameter_list|)
block|{
return|return
name|createCall
argument_list|(
literal|null
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
comment|/**    * Rewrites a call to this operator. Some operators are implemented as    * trivial rewrites (e.g. NULLIF becomes CASE). However, we don't do this at    * createCall time because we want to preserve the original SQL syntax as    * much as possible; instead, we do this before the call is validated (so    * the trivial operator doesn't need its own implementation of type    * derivation methods). The default implementation is to just return the    * original call without any rewrite.    *    * @param validator Validator    * @param call      Call to be rewritten    * @return rewritten call    */
specifier|public
name|SqlNode
name|rewriteCall
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
return|return
name|call
return|;
block|}
comment|/**    * Writes a SQL representation of a call to this operator to a writer,    * including parentheses if the operators on either side are of greater    * precedence.    *    *<p>The default implementation of this method delegates to {@link    * SqlSyntax#unparse}.    */
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
comment|// REVIEW jvs 9-June-2006: See http://issues.eigenbase.org/browse/FRG-149
comment|// for why this method exists.
specifier|protected
name|void
name|unparseListClause
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
name|clause
parameter_list|)
block|{
name|unparseListClause
argument_list|(
name|writer
argument_list|,
name|clause
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|unparseListClause
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
name|clause
parameter_list|,
name|SqlKind
name|sepKind
parameter_list|)
block|{
if|if
condition|(
name|clause
operator|instanceof
name|SqlNodeList
condition|)
block|{
if|if
condition|(
name|sepKind
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|SqlNodeList
operator|)
name|clause
operator|)
operator|.
name|andOrList
argument_list|(
name|writer
argument_list|,
name|sepKind
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|(
operator|(
name|SqlNodeList
operator|)
name|clause
operator|)
operator|.
name|commaList
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|clause
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
block|}
comment|// override Object
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|SqlOperator
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|obj
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SqlOperator
name|other
init|=
operator|(
name|SqlOperator
operator|)
name|obj
decl_stmt|;
return|return
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
operator|&&
name|kind
operator|==
name|other
operator|.
name|kind
return|;
block|}
specifier|public
name|boolean
name|isName
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
return|return
name|name
operator|.
name|equals
argument_list|(
name|testName
argument_list|)
return|;
block|}
comment|// override Object
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|kind
operator|.
name|hashCode
argument_list|()
operator|+
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Validates a call to this operator.    *    *<p>This method should not perform type-derivation or perform validation    * related related to types. That is done later, by {@link    * #deriveType(SqlValidator, SqlValidatorScope, SqlCall)}. This method    * should focus on structural validation.    *    *<p>A typical implementation of this method first validates the operands,    * then performs some operator-specific logic. The default implementation    * just validates the operands.    *    *<p>This method is the default implementation of {@link SqlCall#validate};    * but note that some sub-classes of {@link SqlCall} never call this method.    *    * @param call         the call to this operator    * @param validator    the active validator    * @param scope        validator scope    * @param operandScope validator scope in which to validate operands to this    *                     call; usually equal to scope, but not always because    *                     some operators introduce new scopes    * @see SqlNode#validateExpr(SqlValidator, SqlValidatorScope)    * @see #deriveType(SqlValidator, SqlValidatorScope, SqlCall)    */
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
assert|assert
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|this
assert|;
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
name|getOperands
argument_list|()
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
block|}
comment|/**    * Validates the operands of a call, inferring the return type in the    * process.    *    * @param validator active validator    * @param scope     validation scope    * @param call      call to be validated    * @return inferred type    */
specifier|public
specifier|final
name|RelDataType
name|validateOperands
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
comment|// Let subclasses know what's up.
name|preValidateCall
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
expr_stmt|;
comment|// Check the number of operands
name|checkOperandCount
argument_list|(
name|validator
argument_list|,
name|operandTypeChecker
argument_list|,
name|call
argument_list|)
expr_stmt|;
name|SqlCallBinding
name|opBinding
init|=
operator|new
name|SqlCallBinding
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
decl_stmt|;
name|checkOperandTypes
argument_list|(
name|opBinding
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Now infer the result type.
name|RelDataType
name|ret
init|=
name|inferReturnType
argument_list|(
name|opBinding
argument_list|)
decl_stmt|;
name|validator
operator|.
name|setValidatedNodeType
argument_list|(
name|call
argument_list|,
name|ret
argument_list|)
expr_stmt|;
return|return
name|ret
return|;
block|}
comment|/**    * Receives notification that validation of a call to this operator is    * beginning. Subclasses can supply custom behavior; default implementation    * does nothing.    *    * @param validator invoking validator    * @param scope     validation scope    * @param call      the call being validated    */
specifier|protected
name|void
name|preValidateCall
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
block|}
comment|/**    * Infers the return type of an invocation of this operator; only called    * after the number and types of operands have already been validated.    * Subclasses must either override this method or supply an instance of    * {@link SqlReturnTypeInference} to the constructor.    *    * @param opBinding description of invocation (not necessarily a {@link    *                  SqlCall})    * @return inferred return type    */
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
if|if
condition|(
name|returnTypeInference
operator|!=
literal|null
condition|)
block|{
return|return
name|returnTypeInference
operator|.
name|inferReturnType
argument_list|(
name|opBinding
argument_list|)
return|;
block|}
comment|// Derived type should have overridden this method, since it didn't
comment|// supply a type inference rule.
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|this
argument_list|)
throw|;
block|}
comment|/**    * Derives the type of a call to this operator.    *    *<p>This method is an intrinsic part of the validation process so, unlike    * {@link #inferReturnType}, specific operators would not typically override    * this method.    *    * @param validator Validator    * @param scope     Scope of validation    * @param call      Call to this operator    * @return Type of call    */
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
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
name|operands
control|)
block|{
name|RelDataType
name|nodeType
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
assert|assert
name|nodeType
operator|!=
literal|null
assert|;
block|}
name|RelDataType
name|type
init|=
name|validateOperands
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
decl_stmt|;
comment|// Validate and determine coercibility and resulting collation
comment|// name of binary operator if needed.
name|type
operator|=
name|adjustType
argument_list|(
name|validator
argument_list|,
name|call
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|SqlValidatorUtil
operator|.
name|checkCharsetAndCollateConsistentIfCharType
argument_list|(
name|type
argument_list|)
expr_stmt|;
return|return
name|type
return|;
block|}
comment|/**    * Validates and determines coercibility and resulting collation name of    * binary operator if needed.    */
specifier|protected
name|RelDataType
name|adjustType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
specifier|final
name|SqlCall
name|call
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|type
return|;
block|}
comment|/**    * Infers the type of a call to this operator with a given set of operand    * types. Shorthand for {@link #inferReturnType(SqlOperatorBinding)}.    */
specifier|public
specifier|final
name|RelDataType
name|inferReturnType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|operandTypes
parameter_list|)
block|{
return|return
name|inferReturnType
argument_list|(
operator|new
name|ExplicitOperatorBinding
argument_list|(
name|typeFactory
argument_list|,
name|this
argument_list|,
name|operandTypes
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Checks that the operand values in a {@link SqlCall} to this operator are    * valid. Subclasses must either override this method or supply an instance    * of {@link SqlOperandTypeChecker} to the constructor.    *    * @param callBinding    description of call    * @param throwOnFailure whether to throw an exception if check fails    *                       (otherwise returns false in that case)    * @return whether check succeeded    */
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
comment|// Check that all of the operands are of the right type.
if|if
condition|(
literal|null
operator|==
name|operandTypeChecker
condition|)
block|{
comment|// If you see this you must either give operandTypeChecker a value
comment|// or override this method.
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|this
argument_list|)
throw|;
block|}
return|return
name|operandTypeChecker
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
specifier|protected
name|void
name|checkOperandCount
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlOperandTypeChecker
name|argType
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|SqlOperandCountRange
name|od
init|=
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getOperandCountRange
argument_list|()
decl_stmt|;
if|if
condition|(
name|od
operator|.
name|isValidCount
argument_list|(
name|call
operator|.
name|operands
operator|.
name|length
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|od
operator|.
name|getMin
argument_list|()
operator|==
name|od
operator|.
name|getMax
argument_list|()
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|InvalidArgCount
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
argument_list|,
name|od
operator|.
name|getMin
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|call
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|WrongNumOfArguments
operator|.
name|ex
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns whether the given operands are valid. If not valid and    * {@code fail}, throws an assertion error.    *    *<p>Similar to {@link #checkOperandCount}, but some operators may have    * different valid operands in {@link SqlNode} and {@code RexNode} formats    * (some examples are CAST and AND), and this method throws internal errors,    * not user errors.</p>    */
specifier|public
name|boolean
name|validRexOperands
parameter_list|(
name|int
name|count
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Returns a template describing how the operator signature is to be built.    * E.g for the binary + operator the template looks like "{1} {0} {2}" {0}    * is the operator, subsequent numbers are operands.    *    * @param operandsCount is used with functions that can take a variable    *                      number of operands    * @return signature template, or null to indicate that a default template    * will suffice    */
specifier|public
name|String
name|getSignatureTemplate
parameter_list|(
specifier|final
name|int
name|operandsCount
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Returns a string describing the expected operand types of a call, e.g.    * "SUBSTR(VARCHAR, INTEGER, INTEGER)".    */
specifier|public
specifier|final
name|String
name|getAllowedSignatures
parameter_list|()
block|{
return|return
name|getAllowedSignatures
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Returns a string describing the expected operand types of a call, e.g.    * "SUBSTRING(VARCHAR, INTEGER, INTEGER)" where the name (SUBSTRING in this    * example) can be replaced by a specified name.    */
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|String
name|opNameToUse
parameter_list|)
block|{
assert|assert
name|operandTypeChecker
operator|!=
literal|null
operator|:
literal|"If you see this, assign operandTypeChecker a value "
operator|+
literal|"or override this function"
assert|;
return|return
name|operandTypeChecker
operator|.
name|getAllowedSignatures
argument_list|(
name|this
argument_list|,
name|opNameToUse
argument_list|)
operator|.
name|trim
argument_list|()
return|;
block|}
specifier|public
name|SqlOperandTypeInference
name|getOperandTypeInference
parameter_list|()
block|{
return|return
name|operandTypeInference
return|;
block|}
comment|/**    * Returns whether this operator is an aggregate function. By default,    * subclass type is used (an instance of SqlAggFunction is assumed to be an    * aggregator; anything else is not).    *    * @return whether this operator is an aggregator    */
specifier|public
name|boolean
name|isAggregator
parameter_list|()
block|{
return|return
name|this
operator|instanceof
name|SqlAggFunction
return|;
block|}
comment|/**    * Accepts a {@link SqlVisitor}, visiting each operand of a call. Returns    * null.    *    * @param visitor Visitor    * @param call    Call to visit    */
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
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
parameter_list|)
block|{
for|for
control|(
name|SqlNode
name|operand
range|:
name|call
operator|.
name|operands
control|)
block|{
if|if
condition|(
name|operand
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|operand
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Accepts a {@link SqlVisitor}, directing an {@link    * org.eigenbase.sql.util.SqlBasicVisitor.ArgHandler} to visit operand of a    * call. The argument handler allows fine control about how the operands are    * visited, and how the results are combined.    *    * @param visitor         Visitor    * @param call            Call to visit    * @param onlyExpressions If true, ignores operands which are not    *                        expressions. For example, in the call to the    *<code>AS</code> operator    * @param argHandler      Called for each operand    */
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
name|SqlNode
index|[]
name|operands
init|=
name|call
operator|.
name|operands
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
name|i
operator|++
control|)
block|{
name|argHandler
operator|.
name|visitChild
argument_list|(
name|visitor
argument_list|,
name|call
argument_list|,
name|i
argument_list|,
name|operands
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * @return the return type inference strategy for this operator, or null if    * return type inference is implemented by a subclass override    */
specifier|public
name|SqlReturnTypeInference
name|getReturnTypeInference
parameter_list|()
block|{
return|return
name|returnTypeInference
return|;
block|}
comment|/**    * Returns whether this operator is monotonic.    *    *<p>Default implementation returns {@link SqlMonotonicity#NotMonotonic}.    *    * @param call  Call to this operator    * @param scope Scope in which the call occurs    */
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
name|SqlMonotonicity
operator|.
name|NotMonotonic
return|;
block|}
comment|/**    * @return true iff a call to this operator is guaranteed to always return    * the same result given the same operands; true is assumed by default    */
specifier|public
name|boolean
name|isDeterministic
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * @return true iff it is unsafe to cache query plans referencing this    * operator; false is assumed by default    */
specifier|public
name|boolean
name|isDynamicFunction
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**    * Method to check if call requires expansion when it has decimal operands.    * The default implementation is to return true.    */
specifier|public
name|boolean
name|requiresDecimalExpansion
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Returns whether the<code>ordinal</code>th argument to this operator must    * be scalar (as opposed to a query).    *    *<p>If true (the default), the validator will attempt to convert the    * argument into a scalar subquery, which must have one column and return at    * most one row.    *    *<p>Operators such as<code>SELECT</code> and<code>EXISTS</code> override    * this method.    */
specifier|public
name|boolean
name|argumentMustBeScalar
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlOperator.java
end_comment

end_unit

