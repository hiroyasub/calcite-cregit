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
name|sql
operator|.
name|type
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A generalization of a binary operator to involve several (two or more)  * arguments, and keywords between each pair of arguments.  *  *<p>For example, the<code>BETWEEN</code> operator is ternary, and has syntax  *<code><i>exp1</i> BETWEEN<i>exp2</i> AND<i>exp3</i></code>.  */
end_comment

begin_class
specifier|public
class|class
name|SqlInfixOperator
extends|extends
name|SqlSpecialOperator
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|String
index|[]
name|names
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|SqlInfixOperator
parameter_list|(
name|String
index|[]
name|names
parameter_list|,
name|SqlKind
name|kind
parameter_list|,
name|int
name|precedence
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
name|super
argument_list|(
name|names
index|[
literal|0
index|]
argument_list|,
name|kind
argument_list|,
name|precedence
argument_list|,
literal|true
argument_list|,
name|returnTypeInference
argument_list|,
name|operandTypeInference
argument_list|,
name|operandTypeChecker
argument_list|)
expr_stmt|;
assert|assert
name|names
operator|.
name|length
operator|>
literal|1
assert|;
name|this
operator|.
name|names
operator|=
name|names
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
name|SPECIAL
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
assert|assert
name|operands
operator|.
name|length
operator|==
operator|(
name|names
operator|.
name|length
operator|+
literal|1
operator|)
assert|;
specifier|final
name|boolean
name|needWhitespace
init|=
name|needsSpace
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
name|writer
operator|.
name|setNeedWhitespace
argument_list|(
name|needWhitespace
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|names
index|[
name|i
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
name|writer
operator|.
name|setNeedWhitespace
argument_list|(
name|needWhitespace
argument_list|)
expr_stmt|;
block|}
name|operands
index|[
name|i
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
block|}
block|}
name|boolean
name|needsSpace
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlInfixOperator.java
end_comment

end_unit

