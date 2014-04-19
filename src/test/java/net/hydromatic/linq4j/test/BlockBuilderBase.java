begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|test
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Base methods and constant for simplified Expression testing  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BlockBuilderBase
block|{
specifier|public
specifier|static
specifier|final
name|Expression
name|NULL
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|NULL_INTEGER
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|ONE
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|TWO
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|THREE
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|3
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|FOUR
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|4
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|TRUE
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|FALSE
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|TRUE_B
init|=
name|Expressions
operator|.
name|field
argument_list|(
literal|null
argument_list|,
name|Boolean
operator|.
name|class
argument_list|,
literal|"TRUE"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Expression
name|FALSE_B
init|=
name|Expressions
operator|.
name|field
argument_list|(
literal|null
argument_list|,
name|Boolean
operator|.
name|class
argument_list|,
literal|"FALSE"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|String
name|optimize
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
return|return
name|optimize
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|expr
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|optimize
parameter_list|(
name|Statement
name|statement
parameter_list|)
block|{
name|BlockBuilder
name|b
init|=
operator|new
name|BlockBuilder
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|b
operator|.
name|add
argument_list|(
name|statement
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toBlock
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|ParameterExpression
name|bool
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|parameter
argument_list|(
name|boolean
operator|.
name|class
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ParameterExpression
name|int_
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|parameter
argument_list|(
name|int
operator|.
name|class
argument_list|,
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ParameterExpression
name|integer
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|parameter
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

