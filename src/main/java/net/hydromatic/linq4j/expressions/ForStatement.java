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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Ord
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
comment|/**  * Represents an infinite loop. It can be exited with "break".  */
end_comment

begin_class
specifier|public
class|class
name|ForStatement
extends|extends
name|Statement
block|{
specifier|public
specifier|final
name|List
argument_list|<
name|DeclarationStatement
argument_list|>
name|declarations
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|condition
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|post
decl_stmt|;
specifier|public
specifier|final
name|Statement
name|body
decl_stmt|;
comment|/**    * Cache the hash code for the expression    */
specifier|private
name|int
name|hash
decl_stmt|;
specifier|public
name|ForStatement
parameter_list|(
name|List
argument_list|<
name|DeclarationStatement
argument_list|>
name|declarations
parameter_list|,
name|Expression
name|condition
parameter_list|,
name|Expression
name|post
parameter_list|,
name|Statement
name|body
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|For
argument_list|,
name|Void
operator|.
name|TYPE
argument_list|)
expr_stmt|;
assert|assert
name|declarations
operator|!=
literal|null
assert|;
assert|assert
name|body
operator|!=
literal|null
assert|;
name|this
operator|.
name|declarations
operator|=
name|declarations
expr_stmt|;
comment|// may be empty, not null
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
comment|// may be null
name|this
operator|.
name|post
operator|=
name|post
expr_stmt|;
comment|// may be null
name|this
operator|.
name|body
operator|=
name|body
expr_stmt|;
comment|// may be empty block, not null
block|}
annotation|@
name|Override
specifier|public
name|ForStatement
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|List
argument_list|<
name|DeclarationStatement
argument_list|>
name|decls1
init|=
name|Expressions
operator|.
name|acceptDeclarations
argument_list|(
name|declarations
argument_list|,
name|visitor
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|condition1
init|=
name|condition
operator|==
literal|null
condition|?
literal|null
else|:
name|condition
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|post1
init|=
name|post
operator|==
literal|null
condition|?
literal|null
else|:
name|post
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
decl_stmt|;
specifier|final
name|Statement
name|body1
init|=
name|body
operator|.
name|accept
argument_list|(
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
name|decls1
argument_list|,
name|condition1
argument_list|,
name|post1
argument_list|,
name|body1
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
name|writer
operator|.
name|append
argument_list|(
literal|"for ("
argument_list|)
expr_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|DeclarationStatement
argument_list|>
name|declaration
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|declarations
argument_list|)
control|)
block|{
name|declaration
operator|.
name|e
operator|.
name|accept2
argument_list|(
name|writer
argument_list|,
name|declaration
operator|.
name|i
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
if|if
condition|(
name|condition
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|condition
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
if|if
condition|(
name|post
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
name|post
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
operator|.
name|append
argument_list|(
name|Blocks
operator|.
name|toBlock
argument_list|(
name|body
argument_list|)
argument_list|)
expr_stmt|;
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
name|ForStatement
name|that
init|=
operator|(
name|ForStatement
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|body
operator|.
name|equals
argument_list|(
name|that
operator|.
name|body
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|condition
operator|!=
literal|null
condition|?
operator|!
name|condition
operator|.
name|equals
argument_list|(
name|that
operator|.
name|condition
argument_list|)
else|:
name|that
operator|.
name|condition
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|declarations
operator|.
name|equals
argument_list|(
name|that
operator|.
name|declarations
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|post
operator|!=
literal|null
condition|?
operator|!
name|post
operator|.
name|equals
argument_list|(
name|that
operator|.
name|post
argument_list|)
else|:
name|that
operator|.
name|post
operator|!=
literal|null
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
name|declarations
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
operator|(
name|condition
operator|!=
literal|null
condition|?
name|condition
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|post
operator|!=
literal|null
condition|?
name|post
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|body
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
comment|// End ForStatement.java
end_comment

end_unit

