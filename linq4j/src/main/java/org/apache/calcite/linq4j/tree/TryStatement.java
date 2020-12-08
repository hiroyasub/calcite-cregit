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
name|linq4j
operator|.
name|tree
package|;
end_package

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|Objects
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
annotation|@
name|Nullable
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
annotation|@
name|Nullable
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
name|this
operator|.
name|body
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|body
argument_list|,
literal|"body"
argument_list|)
expr_stmt|;
name|this
operator|.
name|catchBlocks
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|catchBlocks
argument_list|,
literal|"catchBlocks"
argument_list|)
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
name|Shuttle
name|shuttle
parameter_list|)
block|{
name|shuttle
operator|=
name|shuttle
operator|.
name|preVisit
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Statement
name|body1
init|=
name|body
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|CatchBlock
argument_list|>
name|catchBlocks1
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|CatchBlock
name|cb
range|:
name|catchBlocks
control|)
block|{
name|Statement
name|cbBody
init|=
name|cb
operator|.
name|body
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
decl_stmt|;
name|catchBlocks1
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|catch_
argument_list|(
name|cb
operator|.
name|parameter
argument_list|,
name|cbBody
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Statement
name|fynally1
init|=
name|fynally
operator|==
literal|null
condition|?
literal|null
else|:
name|fynally
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
decl_stmt|;
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|,
name|body1
argument_list|,
name|catchBlocks1
argument_list|,
name|fynally1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|Visitor
argument_list|<
name|R
argument_list|>
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
annotation|@
name|Nullable
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
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|,
name|body
argument_list|,
name|catchBlocks
argument_list|,
name|fynally
argument_list|)
return|;
block|}
block|}
end_class

end_unit

