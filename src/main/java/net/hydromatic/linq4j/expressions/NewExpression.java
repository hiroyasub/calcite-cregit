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
name|expressions
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Member
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
comment|/**  * Represents a constructor call.  *  *<p>If {@link #memberDeclarations} is not null (even if empty) represents  * an anonymous class.</p>  */
end_comment

begin_class
specifier|public
class|class
name|NewExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|Type
name|type
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
decl_stmt|;
specifier|public
name|NewExpression
parameter_list|(
name|Type
name|type
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|,
name|List
argument_list|<
name|Member
argument_list|>
name|members
parameter_list|,
comment|// not used
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|New
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|arguments
operator|=
name|arguments
expr_stmt|;
name|Types
operator|.
name|discard
argument_list|(
name|members
argument_list|)
expr_stmt|;
name|this
operator|.
name|memberDeclarations
operator|=
name|memberDeclarations
expr_stmt|;
block|}
annotation|@
name|Override
name|void
name|accept
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|int
name|lprec
parameter_list|,
name|int
name|rprec
parameter_list|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"new "
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|list
argument_list|(
literal|"(\n"
argument_list|,
literal|",\n"
argument_list|,
literal|")"
argument_list|,
name|arguments
argument_list|)
expr_stmt|;
if|if
condition|(
name|memberDeclarations
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|list
argument_list|(
literal|"{\n"
argument_list|,
literal|"\n\n"
argument_list|,
literal|"}"
argument_list|,
name|memberDeclarations
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End NewExpression.java
end_comment

end_unit

