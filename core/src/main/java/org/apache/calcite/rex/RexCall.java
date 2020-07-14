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
name|rex
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
name|RelNode
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
name|type
operator|.
name|SqlTypeName
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
name|ImmutableList
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * An expression formed by a call to an operator with zero or more expressions  * as operands.  *  *<p>Operators may be binary, unary, functions, special syntactic constructs  * like<code>CASE ... WHEN ... END</code>, or even internally generated  * constructs like implicit type conversions. The syntax of the operator is  * really irrelevant, because row-expressions (unlike  * {@link org.apache.calcite.sql.SqlNode SQL expressions})  * do not directly represent a piece of source code.  *  *<p>It's not often necessary to sub-class this class. The smarts should be in  * the operator, rather than the call. Any extra information about the call can  * often be encoded as extra arguments. (These don't need to be hidden, because  * no one is going to be generating source code from this tree.)</p>  */
end_comment

begin_class
specifier|public
class|class
name|RexCall
extends|extends
name|RexNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|SqlOperator
name|op
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|operands
decl_stmt|;
specifier|public
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|public
specifier|final
name|int
name|nodeCount
decl_stmt|;
comment|/**    * Cache of hash code.    */
specifier|protected
name|int
name|hash
init|=
literal|0
decl_stmt|;
comment|/**    * Cache of normalized variables used for #equals and #hashCode.    */
specifier|private
name|Pair
argument_list|<
name|SqlOperator
argument_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
argument_list|>
name|normalized
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
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|operands
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|,
literal|"type"
argument_list|)
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|op
argument_list|,
literal|"operator"
argument_list|)
expr_stmt|;
name|this
operator|.
name|operands
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|operands
argument_list|)
expr_stmt|;
name|this
operator|.
name|nodeCount
operator|=
name|RexUtil
operator|.
name|nodeCount
argument_list|(
literal|1
argument_list|,
name|this
operator|.
name|operands
argument_list|)
expr_stmt|;
assert|assert
name|op
operator|.
name|getKind
argument_list|()
operator|!=
literal|null
operator|:
name|op
assert|;
assert|assert
name|op
operator|.
name|validRexOperands
argument_list|(
name|operands
operator|.
name|size
argument_list|()
argument_list|,
name|Litmus
operator|.
name|THROW
argument_list|)
operator|:
name|this
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Appends call operands without parenthesis.    * {@link RexLiteral} might omit data type depending on the context.    * For instance, {@code null:BOOLEAN} vs {@code =(true, null)}.    * The idea here is to omit "obvious" types for readability purposes while    * still maintain {@link RelNode#getDigest()} contract.    *    * @see RexLiteral#computeDigest(RexDigestIncludeType)    * @param sb destination    */
specifier|protected
specifier|final
name|void
name|appendOperands
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|operands
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|operandDigests
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|operands
operator|.
name|size
argument_list|()
argument_list|)
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
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|operand
init|=
name|operands
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|operand
operator|instanceof
name|RexLiteral
operator|)
condition|)
block|{
name|operandDigests
operator|.
name|add
argument_list|(
name|operand
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// Type information might be omitted in certain cases to improve readability
comment|// For instance, AND/OR arguments should be BOOLEAN, so
comment|// AND(true, null) is better than AND(true, null:BOOLEAN), and we keep the same info.
comment|// +($0, 2) is better than +($0, 2:BIGINT). Note: if $0 is BIGINT, then 2 is expected to be
comment|// of BIGINT type as well.
name|RexDigestIncludeType
name|includeType
init|=
name|RexDigestIncludeType
operator|.
name|OPTIONAL
decl_stmt|;
if|if
condition|(
operator|(
name|isA
argument_list|(
name|SqlKind
operator|.
name|AND
argument_list|)
operator|||
name|isA
argument_list|(
name|SqlKind
operator|.
name|OR
argument_list|)
operator|)
operator|&&
name|operand
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
name|includeType
operator|=
name|RexDigestIncludeType
operator|.
name|NO_TYPE
expr_stmt|;
block|}
if|if
condition|(
name|SqlKind
operator|.
name|SIMPLE_BINARY_OPS
operator|.
name|contains
argument_list|(
name|getKind
argument_list|()
argument_list|)
condition|)
block|{
name|RexNode
name|otherArg
init|=
name|operands
operator|.
name|get
argument_list|(
literal|1
operator|-
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
operator|!
operator|(
name|otherArg
operator|instanceof
name|RexLiteral
operator|)
operator|||
operator|(
operator|(
name|RexLiteral
operator|)
name|otherArg
operator|)
operator|.
name|digestIncludesType
argument_list|()
operator|==
name|RexDigestIncludeType
operator|.
name|NO_TYPE
operator|)
operator|&&
name|SqlTypeUtil
operator|.
name|equalSansNullability
argument_list|(
name|operand
operator|.
name|getType
argument_list|()
argument_list|,
name|otherArg
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|includeType
operator|=
name|RexDigestIncludeType
operator|.
name|NO_TYPE
expr_stmt|;
block|}
block|}
name|operandDigests
operator|.
name|add
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|operand
operator|)
operator|.
name|computeDigest
argument_list|(
name|includeType
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|int
name|totalLength
init|=
operator|(
name|operandDigests
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|)
operator|*
literal|2
decl_stmt|;
comment|// commas
for|for
control|(
name|String
name|s
range|:
name|operandDigests
control|)
block|{
name|totalLength
operator|+=
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
name|sb
operator|.
name|ensureCapacity
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|+
name|totalLength
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
name|operandDigests
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|op
init|=
name|operandDigests
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|!=
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
name|sb
operator|.
name|append
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
annotation|@
name|Nonnull
name|String
name|computeDigest
parameter_list|(
name|boolean
name|withType
parameter_list|)
block|{
specifier|final
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
name|size
argument_list|()
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
name|FUNCTION_ID
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
name|appendOperands
argument_list|(
name|sb
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
specifier|public
specifier|final
annotation|@
name|Nonnull
name|String
name|toString
parameter_list|()
block|{
return|return
name|computeDigest
argument_list|(
name|digestWithType
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|digestWithType
parameter_list|()
block|{
return|return
name|isA
argument_list|(
name|SqlKind
operator|.
name|CAST
argument_list|)
operator|||
name|isA
argument_list|(
name|SqlKind
operator|.
name|NEW_SPECIFICATION
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
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
name|R
name|accept
parameter_list|(
name|RexBiVisitor
argument_list|<
name|R
argument_list|,
name|P
argument_list|>
name|visitor
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visitCall
argument_list|(
name|this
argument_list|,
name|arg
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
annotation|@
name|Override
specifier|public
name|boolean
name|isAlwaysTrue
parameter_list|()
block|{
comment|// "c IS NOT NULL" occurs when we expand EXISTS.
comment|// This reduction allows us to convert it to a semi-join.
switch|switch
condition|(
name|getKind
argument_list|()
condition|)
block|{
case|case
name|IS_NOT_NULL
case|:
return|return
operator|!
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
return|;
case|case
name|IS_NOT_TRUE
case|:
case|case
name|IS_FALSE
case|:
case|case
name|NOT
case|:
return|return
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isAlwaysFalse
argument_list|()
return|;
case|case
name|IS_NOT_FALSE
case|:
case|case
name|IS_TRUE
case|:
case|case
name|CAST
case|:
return|return
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isAlwaysTrue
argument_list|()
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAlwaysFalse
parameter_list|()
block|{
switch|switch
condition|(
name|getKind
argument_list|()
condition|)
block|{
case|case
name|IS_NULL
case|:
return|return
operator|!
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
return|;
case|case
name|IS_NOT_TRUE
case|:
case|case
name|IS_FALSE
case|:
case|case
name|NOT
case|:
return|return
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isAlwaysTrue
argument_list|()
return|;
case|case
name|IS_NOT_FALSE
case|:
case|case
name|IS_TRUE
case|:
case|case
name|CAST
case|:
return|return
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|isAlwaysFalse
argument_list|()
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|op
operator|.
name|kind
return|;
block|}
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
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
annotation|@
name|Override
specifier|public
name|int
name|nodeCount
parameter_list|()
block|{
return|return
name|nodeCount
return|;
block|}
comment|/**    * Creates a new call to the same operator with different operands.    *    * @param type     Return type    * @param operands Operands to call    * @return New call    */
specifier|public
name|RexCall
name|clone
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
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
specifier|private
name|Pair
argument_list|<
name|SqlOperator
argument_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
argument_list|>
name|getNormalized
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|normalized
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|normalized
operator|=
name|RexNormalize
operator|.
name|normalize
argument_list|(
name|this
operator|.
name|op
argument_list|,
name|this
operator|.
name|operands
argument_list|)
expr_stmt|;
block|}
return|return
name|this
operator|.
name|normalized
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Pair
argument_list|<
name|SqlOperator
argument_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
argument_list|>
name|x
init|=
name|getNormalized
argument_list|()
decl_stmt|;
name|RexCall
name|rexCall
init|=
operator|(
name|RexCall
operator|)
name|o
decl_stmt|;
name|Pair
argument_list|<
name|SqlOperator
argument_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
argument_list|>
name|y
init|=
name|rexCall
operator|.
name|getNormalized
argument_list|()
decl_stmt|;
return|return
name|x
operator|.
name|left
operator|.
name|equals
argument_list|(
name|y
operator|.
name|left
argument_list|)
operator|&&
name|x
operator|.
name|right
operator|.
name|equals
argument_list|(
name|y
operator|.
name|right
argument_list|)
operator|&&
name|type
operator|.
name|equals
argument_list|(
name|rexCall
operator|.
name|type
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hash
operator|==
literal|0
condition|)
block|{
name|hash
operator|=
name|RexNormalize
operator|.
name|hashCode
argument_list|(
name|this
operator|.
name|op
argument_list|,
name|this
operator|.
name|operands
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
block|}
end_class

end_unit

