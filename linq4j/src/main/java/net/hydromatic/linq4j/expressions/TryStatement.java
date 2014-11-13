begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Represents a {@code try ... catch ... finally} block.  */
end_comment

begin_class
specifier|public
class|class
name|TryStatement
extends|extends
name|Statement
block|{
specifier|public
specifier|final
name|Statement
name|body
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|CatchBlock
argument_list|>
name|catchBlocks
decl_stmt|;
specifier|public
specifier|final
name|Statement
name|fynally
decl_stmt|;
specifier|public
name|TryStatement
parameter_list|(
name|Statement
name|body
parameter_list|,
name|List
argument_list|<
name|CatchBlock
argument_list|>
name|catchBlocks
parameter_list|,
name|Statement
name|fynally
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|Try
argument_list|,
name|body
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
assert|assert
name|body
operator|!=
literal|null
operator|:
literal|"body should not be null"
assert|;
assert|assert
name|catchBlocks
operator|!=
literal|null
operator|:
literal|"catchBlocks should not be null"
assert|;
name|this
operator|.
name|body
operator|=
name|body
expr_stmt|;
name|this
operator|.
name|catchBlocks
operator|=
name|catchBlocks
expr_stmt|;
name|this
operator|.
name|fynally
operator|=
name|fynally
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Statement
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
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
literal|"try "
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
for|for
control|(
name|CatchBlock
name|catchBlock
range|:
name|catchBlocks
control|)
block|{
name|writer
operator|.
name|backUp
argument_list|()
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|" catch ("
argument_list|)
operator|.
name|append
argument_list|(
name|catchBlock
operator|.
name|parameter
operator|.
name|declString
argument_list|()
argument_list|)
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
name|catchBlock
operator|.
name|body
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fynally
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|backUp
argument_list|()
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|" finally "
argument_list|)
operator|.
name|append
argument_list|(
name|Blocks
operator|.
name|toBlock
argument_list|(
name|fynally
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|TryStatement
name|that
init|=
operator|(
name|TryStatement
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
operator|!
name|catchBlocks
operator|.
name|equals
argument_list|(
name|that
operator|.
name|catchBlocks
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|fynally
operator|!=
literal|null
condition|?
operator|!
name|fynally
operator|.
name|equals
argument_list|(
name|that
operator|.
name|fynally
argument_list|)
else|:
name|that
operator|.
name|fynally
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
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
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
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|catchBlocks
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
name|fynally
operator|!=
literal|null
condition|?
name|fynally
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

begin_comment
comment|// End TryStatement.java
end_comment

end_unit

