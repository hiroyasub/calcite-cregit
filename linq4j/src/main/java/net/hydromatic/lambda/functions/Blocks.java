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
name|lambda
operator|.
name|functions
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
name|Linq4j
import|;
end_import

begin_comment
comment|/**  * Static utility methods pertaining to {@code Block} instances.  *  *<p>Based on {@code java.util.functions.Blocks}.</p>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Blocks
block|{
specifier|private
specifier|static
specifier|final
name|Block
argument_list|<
name|Object
argument_list|>
name|NOP
init|=
operator|new
name|Block
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
comment|// no thing
block|}
specifier|public
name|Block
argument_list|<
name|Object
argument_list|>
name|chain
parameter_list|(
name|Block
argument_list|<
name|?
super|super
name|Object
argument_list|>
name|second
parameter_list|)
block|{
return|return
name|Blocks
operator|.
name|chain
argument_list|(
name|this
argument_list|,
name|second
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Block
argument_list|<
name|Object
argument_list|>
name|REQUIRE_NON_NULL
init|=
operator|new
name|Block
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|apply
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|Linq4j
operator|.
name|requireNonNull
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Block
argument_list|<
name|Object
argument_list|>
name|chain
parameter_list|(
name|Block
argument_list|<
name|?
super|super
name|Object
argument_list|>
name|second
parameter_list|)
block|{
return|return
name|Blocks
operator|.
name|chain
argument_list|(
name|this
argument_list|,
name|second
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|private
name|Blocks
parameter_list|()
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Block
argument_list|<
name|T
argument_list|>
name|nop
parameter_list|()
block|{
return|return
operator|(
name|Block
argument_list|<
name|T
argument_list|>
operator|)
name|NOP
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Block
argument_list|<
name|T
argument_list|>
name|requireNonNull
parameter_list|()
block|{
return|return
operator|(
name|Block
argument_list|<
name|T
argument_list|>
operator|)
name|REQUIRE_NON_NULL
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Block
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
specifier|final
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
name|first
parameter_list|,
specifier|final
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
name|second
parameter_list|)
block|{
return|return
operator|new
name|Block
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|apply
parameter_list|(
name|T
name|t
parameter_list|)
block|{
name|first
operator|.
name|apply
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|second
operator|.
name|apply
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Block
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
name|second
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|Blocks
operator|.
name|chain
argument_list|(
operator|(
name|Block
operator|)
name|this
argument_list|,
name|second
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Block
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
specifier|final
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
modifier|...
name|sequence
parameter_list|)
block|{
return|return
operator|new
name|Block
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|apply
parameter_list|(
name|T
name|t
parameter_list|)
block|{
for|for
control|(
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
name|block
range|:
name|sequence
control|)
block|{
name|block
operator|.
name|apply
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Block
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
name|second
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|Blocks
operator|.
name|chain
argument_list|(
operator|(
name|Block
operator|)
name|this
argument_list|,
name|second
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|Block
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|?
extends|extends
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
argument_list|>
name|sequence
parameter_list|)
block|{
return|return
operator|new
name|Block
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
specifier|public
name|void
name|apply
parameter_list|(
name|T
name|t
parameter_list|)
block|{
for|for
control|(
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
name|block
range|:
name|sequence
control|)
block|{
name|block
operator|.
name|apply
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Block
argument_list|<
name|T
argument_list|>
name|chain
parameter_list|(
name|Block
argument_list|<
name|?
super|super
name|T
argument_list|>
name|second
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|Blocks
operator|.
name|chain
argument_list|(
operator|(
name|Block
operator|)
name|this
argument_list|,
name|second
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End Blocks.java
end_comment

end_unit

