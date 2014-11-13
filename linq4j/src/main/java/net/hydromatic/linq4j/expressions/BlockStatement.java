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
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Represents a block that contains a sequence of expressions where variables  * can be defined.  */
end_comment

begin_class
specifier|public
class|class
name|BlockStatement
extends|extends
name|Statement
block|{
specifier|public
specifier|final
name|List
argument_list|<
name|Statement
argument_list|>
name|statements
decl_stmt|;
comment|/**    * Cache the hash code for the expression    */
specifier|private
name|int
name|hash
decl_stmt|;
name|BlockStatement
parameter_list|(
name|List
argument_list|<
name|Statement
argument_list|>
name|statements
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|Block
argument_list|,
name|type
argument_list|)
expr_stmt|;
assert|assert
name|statements
operator|!=
literal|null
operator|:
literal|"statements should not be null"
assert|;
name|this
operator|.
name|statements
operator|=
name|statements
expr_stmt|;
assert|assert
name|distinctVariables
argument_list|(
literal|true
argument_list|)
assert|;
block|}
specifier|private
name|boolean
name|distinctVariables
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Statement
name|statement
range|:
name|statements
control|)
block|{
if|if
condition|(
name|statement
operator|instanceof
name|DeclarationStatement
condition|)
block|{
name|String
name|name
init|=
operator|(
operator|(
name|DeclarationStatement
operator|)
name|statement
operator|)
operator|.
name|parameter
operator|.
name|name
decl_stmt|;
if|if
condition|(
operator|!
name|names
operator|.
name|add
argument_list|(
name|name
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"duplicate variable "
operator|+
name|name
assert|;
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|BlockStatement
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|=
name|visitor
operator|.
name|preVisit
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Statement
argument_list|>
name|newStatements
init|=
name|Expressions
operator|.
name|acceptStatements
argument_list|(
name|statements
argument_list|,
name|visitor
argument_list|)
decl_stmt|;
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|,
name|newStatements
argument_list|)
return|;
block|}
annotation|@
name|Override
name|void
name|accept0
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|)
block|{
if|if
condition|(
name|statements
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"{}"
argument_list|)
expr_stmt|;
return|return;
block|}
name|writer
operator|.
name|begin
argument_list|(
literal|"{\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Statement
name|node
range|:
name|statements
control|)
block|{
name|node
operator|.
name|accept
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|end
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|evaluate
parameter_list|(
name|Evaluator
name|evaluator
parameter_list|)
block|{
name|Object
name|o
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Statement
name|statement
range|:
name|statements
control|)
block|{
name|o
operator|=
name|statement
operator|.
name|evaluate
argument_list|(
name|evaluator
argument_list|)
expr_stmt|;
block|}
return|return
name|o
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
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|BlockStatement
name|that
init|=
operator|(
name|BlockStatement
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|statements
operator|.
name|equals
argument_list|(
name|that
operator|.
name|statements
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|hash
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|super
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|statements
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
literal|1
expr_stmt|;
block|}
name|hash
operator|=
name|result
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

begin_comment
comment|// End BlockStatement.java
end_comment

end_unit

