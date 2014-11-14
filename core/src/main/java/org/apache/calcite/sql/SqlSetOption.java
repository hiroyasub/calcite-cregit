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
name|List
import|;
end_import

begin_comment
comment|/**  * SQL parse tree node to represent<code>ALTER scope SET option = value</code>  * statement.  *  *<p>Example:</p>  *  *<blockquote>ALTER SYSTEM SET myParam = 1</blockquote>  */
end_comment

begin_class
specifier|public
class|class
name|SqlSetOption
extends|extends
name|SqlCall
block|{
specifier|public
specifier|static
specifier|final
name|SqlSpecialOperator
name|OPERATOR
init|=
operator|new
name|SqlSpecialOperator
argument_list|(
literal|"SET_OPTION"
argument_list|,
name|SqlKind
operator|.
name|SET_OPTION
argument_list|)
decl_stmt|;
comment|/** Scope of the assignment. Values "SYSTEM" and "SESSION" are typical. */
name|String
name|scope
decl_stmt|;
name|String
name|name
decl_stmt|;
comment|/** Value of the option. May be a {@link org.apache.calcite.sql.SqlLiteral} or    * a {@link org.apache.calcite.sql.SqlIdentifier} with one    * part. Reserved words (currently just 'ON') are converted to    * identifiers by the parser. */
name|SqlNode
name|value
decl_stmt|;
comment|/**    * Creates a node.    *    * @param pos Parser position, must not be null.    * @param scope Scope (generally "SYSTEM" or "SESSION")    * @param name Name of option    * @param value Value of option, as an identifier or literal.    */
specifier|public
name|SqlSetOption
parameter_list|(
name|SqlParserPos
name|pos
parameter_list|,
name|String
name|scope
parameter_list|,
name|String
name|name
parameter_list|,
name|SqlNode
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
assert|assert
name|scope
operator|!=
literal|null
assert|;
assert|assert
name|name
operator|!=
literal|null
assert|;
assert|assert
name|value
operator|!=
literal|null
assert|;
block|}
annotation|@
name|Override
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|SqlKind
operator|.
name|SET_OPTION
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|OPERATOR
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|SqlIdentifier
argument_list|(
name|scope
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
name|name
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOperand
parameter_list|(
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|0
case|:
name|this
operator|.
name|scope
operator|=
operator|(
operator|(
name|SqlIdentifier
operator|)
name|operand
operator|)
operator|.
name|getSimple
argument_list|()
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|this
operator|.
name|name
operator|=
operator|(
operator|(
name|SqlIdentifier
operator|)
name|operand
operator|)
operator|.
name|getSimple
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|this
operator|.
name|value
operator|=
name|operand
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|i
argument_list|)
throw|;
block|}
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
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"ALTER"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
name|getScope
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"SET"
argument_list|)
expr_stmt|;
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
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|SIMPLE
argument_list|)
decl_stmt|;
name|writer
operator|.
name|identifier
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
name|value
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
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|validate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|validator
operator|.
name|validate
argument_list|(
name|value
argument_list|)
expr_stmt|;
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
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getScope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
specifier|public
name|void
name|setScope
parameter_list|(
name|String
name|scope
parameter_list|)
block|{
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
specifier|public
name|SqlNode
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|SqlNode
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlSetOption.java
end_comment

end_unit

