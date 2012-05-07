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
name|rex
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
name|Util
import|;
end_import

begin_comment
comment|/**  * An expression formed by a call to an operator with zero or more expressions  * as operands.  *  *<p>Operators may be binary, unary, functions, special syntactic constructs  * like<code>CASE ... WHEN ... END</code>, or even internally generated  * constructs like implicit type conversions. The syntax of the operator is  * really irrelevant, because row-expressions (unlike {@link  * org.eigenbase.sql.SqlNode SQL expressions}) do not directly represent a piece  * of source code.</p>  *  *<p>It's not often necessary to sub-class this class. The smarts should be in  * the operator, rather than the call. Any extra information about the call can  * often be encoded as extra arguments. (These don't need to be hidden, because  * no one is going to be generating source code from this tree.)</p>  *  * @author jhyde  * @version $Id$  * @since Nov 24, 2003  */
end_comment

begin_class
specifier|public
class|class
name|RexCall
extends|extends
name|RexNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlOperator
name|op
decl_stmt|;
specifier|public
specifier|final
name|RexNode
index|[]
name|operands
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|private
specifier|final
name|RexKind
name|kind
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RexCall
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|SqlOperator
name|op
parameter_list|,
name|RexNode
index|[]
name|operands
parameter_list|)
block|{
assert|assert
name|type
operator|!=
literal|null
operator|:
literal|"precondition: type != null"
assert|;
assert|assert
name|op
operator|!=
literal|null
operator|:
literal|"precondition: op != null"
assert|;
assert|assert
name|operands
operator|!=
literal|null
operator|:
literal|"precondition: operands != null"
assert|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
name|this
operator|.
name|operands
operator|=
name|operands
expr_stmt|;
name|this
operator|.
name|kind
operator|=
name|sqlKindToRexKind
argument_list|(
name|op
operator|.
name|getKind
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|this
operator|.
name|kind
operator|!=
literal|null
operator|:
name|op
assert|;
name|this
operator|.
name|digest
operator|=
name|computeDigest
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// TODO zfong 11/19/07 - Extend the check below to all types of
comment|// operators, similar to SqlOperator.checkOperandCount.  However,
comment|// that method operates on SqlCalls, which may have not have the
comment|// same number of operands as their corresponding RexCalls.  One
comment|// example is the CAST operator, which is originally a 2-operand
comment|// SqlCall, but is later converted to a 1-operand RexCall.
if|if
condition|(
name|op
operator|instanceof
name|SqlBinaryOperator
condition|)
block|{
assert|assert
operator|(
name|operands
operator|.
name|length
operator|==
literal|2
operator|)
assert|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the {@link RexKind} corresponding to a {@link SqlKind}. Fails if      * there is none.      *      * @post return != null      */
specifier|static
name|RexKind
name|sqlKindToRexKind
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|EQUALS
case|:
return|return
name|RexKind
operator|.
name|Equals
return|;
case|case
name|IDENTIFIER
case|:
return|return
name|RexKind
operator|.
name|Identifier
return|;
case|case
name|LITERAL
case|:
return|return
name|RexKind
operator|.
name|Literal
return|;
case|case
name|DYNAMIC_PARAM
case|:
return|return
name|RexKind
operator|.
name|DynamicParam
return|;
case|case
name|TIMES
case|:
return|return
name|RexKind
operator|.
name|Times
return|;
case|case
name|DIVIDE
case|:
return|return
name|RexKind
operator|.
name|Divide
return|;
case|case
name|PLUS
case|:
return|return
name|RexKind
operator|.
name|Plus
return|;
case|case
name|MINUS
case|:
return|return
name|RexKind
operator|.
name|Minus
return|;
case|case
name|LESS_THAN
case|:
return|return
name|RexKind
operator|.
name|LessThan
return|;
case|case
name|GREATER_THAN
case|:
return|return
name|RexKind
operator|.
name|GreaterThan
return|;
case|case
name|LESS_THAN_OR_EQUAL
case|:
return|return
name|RexKind
operator|.
name|LessThanOrEqual
return|;
case|case
name|GREATER_THAN_OR_EQUAL
case|:
return|return
name|RexKind
operator|.
name|GreaterThanOrEqual
return|;
case|case
name|NOT_EQUALS
case|:
return|return
name|RexKind
operator|.
name|NotEquals
return|;
case|case
name|OR
case|:
return|return
name|RexKind
operator|.
name|Or
return|;
case|case
name|AND
case|:
return|return
name|RexKind
operator|.
name|And
return|;
case|case
name|NOT
case|:
return|return
name|RexKind
operator|.
name|Not
return|;
case|case
name|IS_TRUE
case|:
return|return
name|RexKind
operator|.
name|IsTrue
return|;
case|case
name|IS_FALSE
case|:
return|return
name|RexKind
operator|.
name|IsFalse
return|;
case|case
name|IS_NULL
case|:
return|return
name|RexKind
operator|.
name|IsNull
return|;
case|case
name|IS_UNKNOWN
case|:
return|return
name|RexKind
operator|.
name|IsNull
return|;
case|case
name|PLUS_PREFIX
case|:
return|return
name|RexKind
operator|.
name|Plus
return|;
case|case
name|MINUS_PREFIX
case|:
return|return
name|RexKind
operator|.
name|MinusPrefix
return|;
case|case
name|VALUES
case|:
return|return
name|RexKind
operator|.
name|Values
return|;
case|case
name|ROW
case|:
return|return
name|RexKind
operator|.
name|Row
return|;
case|case
name|CAST
case|:
return|return
name|RexKind
operator|.
name|Cast
return|;
case|case
name|TRIM
case|:
return|return
name|RexKind
operator|.
name|Trim
return|;
case|case
name|OTHER_FUNCTION
case|:
return|return
name|RexKind
operator|.
name|Other
return|;
case|case
name|CASE
case|:
return|return
name|RexKind
operator|.
name|Other
return|;
case|case
name|OTHER
case|:
return|return
name|RexKind
operator|.
name|Other
return|;
case|case
name|LIKE
case|:
return|return
name|RexKind
operator|.
name|Like
return|;
case|case
name|SIMILAR
case|:
return|return
name|RexKind
operator|.
name|Similar
return|;
case|case
name|MULTISET_QUERY_CONSTRUCTOR
case|:
return|return
name|RexKind
operator|.
name|MultisetQueryConstructor
return|;
case|case
name|NEW_SPECIFICATION
case|:
return|return
name|RexKind
operator|.
name|NewSpecification
return|;
case|case
name|REINTERPRET
case|:
return|return
name|RexKind
operator|.
name|Reinterpret
return|;
case|case
name|COLUMN_LIST
case|:
return|return
name|RexKind
operator|.
name|Row
return|;
default|default:
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|kind
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|String
name|computeDigest
parameter_list|(
name|boolean
name|withType
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|operands
operator|.
name|length
operator|==
literal|0
operator|)
operator|&&
operator|(
name|op
operator|.
name|getSyntax
argument_list|()
operator|==
name|SqlSyntax
operator|.
name|FunctionId
operator|)
condition|)
block|{
comment|// Don't print params for empty arg list. For example, we want
comment|// "SYSTEM_USER", not "SYSTEM_USER()".
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|RexNode
name|operand
init|=
name|operands
index|[
name|i
index|]
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|operand
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|withType
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
comment|// NOTE jvs 16-Jan-2005:  for digests, it is very important
comment|// to use the full type string.
name|sb
operator|.
name|append
argument_list|(
name|type
operator|.
name|getFullTypeString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// REVIEW jvs 16-Jan-2005: For CAST and NEW, the type is really an
comment|// operand and needs to be printed out.  But special-casing it here is
comment|// ugly.
return|return
name|computeDigest
argument_list|(
name|isA
argument_list|(
name|RexKind
operator|.
name|Cast
argument_list|)
operator|||
name|isA
argument_list|(
name|RexKind
operator|.
name|NewSpecification
argument_list|)
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitCall
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|RexCall
name|clone
parameter_list|()
block|{
return|return
operator|new
name|RexCall
argument_list|(
name|type
argument_list|,
name|op
argument_list|,
name|RexUtil
operator|.
name|clone
argument_list|(
name|operands
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RexKind
name|getKind
parameter_list|()
block|{
return|return
name|kind
return|;
block|}
specifier|public
name|RexNode
index|[]
name|getOperands
parameter_list|()
block|{
return|return
name|operands
return|;
block|}
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|op
return|;
block|}
comment|/**      * Creates a new call to the same operator with different operands.      *      * @param type Return type      * @param operands Operands to call      *      * @return New call      */
specifier|public
name|RexCall
name|clone
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|RexNode
index|[]
name|operands
parameter_list|)
block|{
return|return
operator|new
name|RexCall
argument_list|(
name|type
argument_list|,
name|op
argument_list|,
name|operands
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexCall.java
end_comment

end_unit

