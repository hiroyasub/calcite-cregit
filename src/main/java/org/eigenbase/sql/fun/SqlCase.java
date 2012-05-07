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

begin_comment
comment|/**  * A<code>SqlCase</code> is a node of a parse tree which represents a case  * statement. It warrants its own node type just because we have a lot of  * methods to put somewhere.  *  * @author wael  * @version $Id$  * @since Mar 14, 2004  */
end_comment

begin_class
specifier|public
class|class
name|SqlCase
extends|extends
name|SqlCall
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**      * WHEN_OPERANDS = 0      */
specifier|public
specifier|static
specifier|final
name|int
name|WHEN_OPERANDS
init|=
literal|0
decl_stmt|;
comment|/**      * THEN_OPERANDS = 1      */
specifier|public
specifier|static
specifier|final
name|int
name|THEN_OPERANDS
init|=
literal|1
decl_stmt|;
comment|/**      * ELSE_OPERAND = 2      */
specifier|public
specifier|static
specifier|final
name|int
name|ELSE_OPERAND
init|=
literal|2
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a SqlCase expression.      *      *<p>The operands are an array of SqlNodes where      *      *<ul>      *<li>operands[0] is a SqlNodeList of all WHEN expressions      *<li>operands[1] is a SqlNodeList of all THEN expressions      *<li>operands[2] is a SqlNode representing the implicit or explicit ELSE      * expression      *</ul>      *      *<p>See {@link #WHEN_OPERANDS}, {@link #THEN_OPERANDS}, {@link      * #ELSE_OPERAND}.      */
name|SqlCase
parameter_list|(
name|SqlCaseOperator
name|operator
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|super
argument_list|(
name|operator
argument_list|,
name|operands
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlNodeList
name|getWhenOperands
parameter_list|()
block|{
return|return
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
name|WHEN_OPERANDS
index|]
return|;
block|}
specifier|public
name|SqlNodeList
name|getThenOperands
parameter_list|()
block|{
return|return
operator|(
name|SqlNodeList
operator|)
name|operands
index|[
name|THEN_OPERANDS
index|]
return|;
block|}
specifier|public
name|SqlNode
name|getElseOperand
parameter_list|()
block|{
return|return
name|operands
index|[
name|ELSE_OPERAND
index|]
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCase.java
end_comment

end_unit

