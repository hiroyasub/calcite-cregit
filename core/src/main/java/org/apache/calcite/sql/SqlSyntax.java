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
name|sql
operator|.
name|validate
operator|.
name|SqlConformance
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

begin_comment
comment|/**  * Enumeration of possible syntactic types of {@link SqlOperator operators}.  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlSyntax
block|{
comment|/**    * Function syntax, as in "Foo(x, y)".    */
name|FUNCTION
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlOperator
name|operator
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
name|SqlUtil
operator|.
name|unparseFunctionSyntax
argument_list|(
name|operator
argument_list|,
name|writer
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**    * Function syntax, as in "Foo(x, y)", but uses "*" if there are no arguments,    * for example "COUNT(*)".    */
name|FUNCTION_STAR
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlOperator
name|operator
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
name|SqlUtil
operator|.
name|unparseFunctionSyntax
argument_list|(
name|operator
argument_list|,
name|writer
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**    * Binary operator syntax, as in "x + y".    */
name|BINARY
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlOperator
name|operator
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
name|SqlUtil
operator|.
name|unparseBinarySyntax
argument_list|(
name|operator
argument_list|,
name|call
argument_list|,
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**    * Prefix unary operator syntax, as in "- x".    */
name|PREFIX
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlOperator
name|operator
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
assert|assert
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|1
assert|;
name|writer
operator|.
name|keyword
argument_list|(
name|operator
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|operator
operator|.
name|getLeftPrec
argument_list|()
argument_list|,
name|operator
operator|.
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**    * Postfix unary operator syntax, as in "x ++".    */
name|POSTFIX
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlOperator
name|operator
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
assert|assert
name|call
operator|.
name|operandCount
argument_list|()
operator|==
literal|1
assert|;
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|operator
operator|.
name|getLeftPrec
argument_list|()
argument_list|,
name|operator
operator|.
name|getRightPrec
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|operator
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**    * Special syntax, such as that of the SQL CASE operator, "CASE x WHEN 1    * THEN 2 ELSE 3 END".    */
name|SPECIAL
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlOperator
name|operator
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
comment|// You probably need to override the operator's unparse
comment|// method.
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|this
argument_list|)
throw|;
block|}
block|}
block|,
comment|/**    * Function syntax which takes no parentheses if there are no arguments, for    * example "CURRENTTIME".    *    * @see SqlConformance#allowNiladicParentheses()    */
name|FUNCTION_ID
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlOperator
name|operator
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
name|SqlUtil
operator|.
name|unparseFunctionSyntax
argument_list|(
name|operator
argument_list|,
name|writer
argument_list|,
name|call
argument_list|)
expr_stmt|;
block|}
block|}
block|,
comment|/**    * Syntax of an internal operator, which does not appear in the SQL.    */
name|INTERNAL
block|{
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlOperator
name|operator
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
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Internal operator '"
operator|+
name|operator
operator|+
literal|"' "
operator|+
literal|"cannot be un-parsed"
argument_list|)
throw|;
block|}
block|}
block|;
comment|/**    * Converts a call to an operator of this syntax into a string.    */
specifier|public
specifier|abstract
name|void
name|unparse
argument_list|(
name|SqlWriter
name|writer
argument_list|,
name|SqlOperator
name|operator
argument_list|,
name|SqlCall
name|call
argument_list|,
name|int
name|leftPrec
argument_list|,
name|int
name|rightPrec
argument_list|)
decl_stmt|;
block|}
end_enum

end_unit

