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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * An operator describing the<code>LIKE</code> and<code>SIMILAR</code>  * operators.  *  *<p>Syntax of the two operators:  *  *<ul>  *<li><code>src-value [NOT] LIKE pattern-value [ESCAPE  * escape-value]</code></li>  *<li><code>src-value [NOT] SIMILAR pattern-value [ESCAPE  * escape-value]</code></li>  *</ul>  *  *<p><b>NOTE</b> If the<code>NOT</code> clause is present the {@link  * org.eigenbase.sql.parser.SqlParser parser} will generate a eqvivalent to  *<code>NOT (src LIKE pattern ...)</code>  */
end_comment

begin_class
specifier|public
class|class
name|SqlLikeOperator
extends|extends
name|SqlSpecialOperator
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|boolean
name|negated
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a SqlLikeOperator.      *      * @param name Operator name      * @param kind Kind      * @param negated Whether this is 'NOT LIKE'      */
name|SqlLikeOperator
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|boolean
name|negated
parameter_list|)
block|{
comment|// LIKE is right-associative, because that makes it easier to capture
comment|// dangling ESCAPE clauses: "a like b like c escape d" becomes
comment|// "a like (b like c escape d)".
name|super
argument_list|(
name|name
argument_list|,
name|kind
argument_list|,
literal|30
argument_list|,
literal|false
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiNullableBoolean
argument_list|,
name|SqlTypeStrategies
operator|.
name|otiFirstKnown
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcStringSameX3
argument_list|)
expr_stmt|;
name|this
operator|.
name|negated
operator|=
name|negated
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns whether this is the 'NOT LIKE' operator.      *      * @return whether this is 'NOT LIKE'      */
specifier|public
name|boolean
name|isNegated
parameter_list|()
block|{
return|return
name|negated
return|;
block|}
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|between
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
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
switch|switch
condition|(
name|callBinding
operator|.
name|getOperandCount
argument_list|()
condition|)
block|{
case|case
literal|2
case|:
if|if
condition|(
operator|!
name|SqlTypeStrategies
operator|.
name|otcStringSameX2
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
case|case
literal|3
case|:
if|if
condition|(
operator|!
name|SqlTypeStrategies
operator|.
name|otcStringSameX3
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// calc implementation should
comment|// enforce the escape character length to be 1
break|break;
default|default:
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"unexpected number of args to "
operator|+
name|callBinding
operator|.
name|getCall
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|SqlTypeUtil
operator|.
name|isCharTypeComparable
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|getCall
argument_list|()
operator|.
name|getOperands
argument_list|()
argument_list|,
name|throwOnFailure
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
name|operands
index|[
literal|0
index|]
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|,
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
name|getName
argument_list|()
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
name|getLeftPrec
argument_list|()
argument_list|,
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|operands
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|writer
operator|.
name|sep
argument_list|(
literal|"ESCAPE"
argument_list|)
expr_stmt|;
name|operands
index|[
literal|2
index|]
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|getLeftPrec
argument_list|()
argument_list|,
name|getRightPrec
argument_list|()
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
name|int
name|reduceExpr
parameter_list|(
specifier|final
name|int
name|opOrdinal
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|list
parameter_list|)
block|{
comment|// Example:
comment|//   a LIKE b || c ESCAPE d || e AND f
comment|// |  |    |      |      |      |
comment|//  exp0    exp1          exp2
name|SqlNode
name|exp0
init|=
operator|(
name|SqlNode
operator|)
name|list
operator|.
name|get
argument_list|(
name|opOrdinal
operator|-
literal|1
argument_list|)
decl_stmt|;
name|SqlOperator
name|op
init|=
operator|(
operator|(
name|SqlParserUtil
operator|.
name|ToTreeListItem
operator|)
name|list
operator|.
name|get
argument_list|(
name|opOrdinal
argument_list|)
operator|)
operator|.
name|getOperator
argument_list|()
decl_stmt|;
assert|assert
name|op
operator|instanceof
name|SqlLikeOperator
assert|;
name|SqlNode
name|exp1
init|=
name|SqlParserUtil
operator|.
name|toTreeEx
argument_list|(
name|list
argument_list|,
name|opOrdinal
operator|+
literal|1
argument_list|,
name|getRightPrec
argument_list|()
argument_list|,
name|SqlKind
operator|.
name|ESCAPE
argument_list|)
decl_stmt|;
name|SqlNode
name|exp2
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|(
name|opOrdinal
operator|+
literal|2
operator|)
operator|<
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|Object
name|o
init|=
name|list
operator|.
name|get
argument_list|(
name|opOrdinal
operator|+
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|o
operator|instanceof
name|SqlParserUtil
operator|.
name|ToTreeListItem
condition|)
block|{
specifier|final
name|SqlOperator
name|op2
init|=
operator|(
operator|(
name|SqlParserUtil
operator|.
name|ToTreeListItem
operator|)
name|o
operator|)
operator|.
name|getOperator
argument_list|()
decl_stmt|;
if|if
condition|(
name|op2
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|ESCAPE
condition|)
block|{
name|exp2
operator|=
name|SqlParserUtil
operator|.
name|toTreeEx
argument_list|(
name|list
argument_list|,
name|opOrdinal
operator|+
literal|3
argument_list|,
name|getRightPrec
argument_list|()
argument_list|,
name|SqlKind
operator|.
name|ESCAPE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|SqlNode
index|[]
name|operands
decl_stmt|;
name|int
name|end
decl_stmt|;
if|if
condition|(
name|exp2
operator|!=
literal|null
condition|)
block|{
name|operands
operator|=
operator|new
name|SqlNode
index|[]
block|{
name|exp0
block|,
name|exp1
block|,
name|exp2
block|}
expr_stmt|;
name|end
operator|=
name|opOrdinal
operator|+
literal|4
expr_stmt|;
block|}
else|else
block|{
name|operands
operator|=
operator|new
name|SqlNode
index|[]
block|{
name|exp0
block|,
name|exp1
block|}
expr_stmt|;
name|end
operator|=
name|opOrdinal
operator|+
literal|2
expr_stmt|;
block|}
name|SqlCall
name|call
init|=
name|createCall
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|operands
argument_list|)
decl_stmt|;
name|SqlParserUtil
operator|.
name|replaceSublist
argument_list|(
name|list
argument_list|,
name|opOrdinal
operator|-
literal|1
argument_list|,
name|end
argument_list|,
name|call
argument_list|)
expr_stmt|;
return|return
name|opOrdinal
operator|-
literal|1
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlLikeOperator.java
end_comment

end_unit

