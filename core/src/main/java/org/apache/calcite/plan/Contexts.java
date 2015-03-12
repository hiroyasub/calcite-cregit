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
name|plan
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
name|config
operator|.
name|CalciteConnectionConfig
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
name|base
operator|.
name|Preconditions
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
comment|/**  * Utilities for {@link Context}.  */
end_comment

begin_class
specifier|public
class|class
name|Contexts
block|{
specifier|public
specifier|static
specifier|final
name|EmptyContext
name|EMPTY_CONTEXT
init|=
operator|new
name|EmptyContext
argument_list|()
decl_stmt|;
specifier|private
name|Contexts
parameter_list|()
block|{
block|}
comment|/** Returns a context that contains a    * {@link org.apache.calcite.config.CalciteConnectionConfig}.    *    * @deprecated Use {@link #of}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|Context
name|withConfig
parameter_list|(
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|config
argument_list|)
return|;
block|}
comment|/** Returns a context that returns null for all inquiries. */
specifier|public
specifier|static
name|Context
name|empty
parameter_list|()
block|{
return|return
name|EMPTY_CONTEXT
return|;
block|}
comment|/** Returns a context that wraps an object.    *    *<p>A call to {@code unwrap(C)} will return {@code target} if it is an    * instance of {@code C}.    */
specifier|public
specifier|static
name|Context
name|of
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
operator|new
name|WrapContext
argument_list|(
name|o
argument_list|)
return|;
block|}
comment|/** Returns a context that wraps an object.    *    *<p>A call to {@code unwrap(C)} will return the first object that is an    * instance of {@code C}.    *    *<p>If any of the contexts is a {@link Context}, recursively looks in that    * object. Thus this method can be used to chain contexts.    */
specifier|public
specifier|static
name|Context
name|chain
parameter_list|(
name|Context
modifier|...
name|contexts
parameter_list|)
block|{
comment|// Flatten any chain contexts in the list, and remove duplicates
specifier|final
name|List
argument_list|<
name|Context
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Context
name|context
range|:
name|contexts
control|)
block|{
name|build
argument_list|(
name|list
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|empty
argument_list|()
return|;
case|case
literal|1
case|:
return|return
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
default|default:
return|return
operator|new
name|ChainContext
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|list
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** Recursively populates a list of contexts. */
specifier|private
specifier|static
name|void
name|build
parameter_list|(
name|List
argument_list|<
name|Context
argument_list|>
name|list
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
if|if
condition|(
name|context
operator|instanceof
name|ChainContext
condition|)
block|{
name|ChainContext
name|chainContext
init|=
operator|(
name|ChainContext
operator|)
name|context
decl_stmt|;
for|for
control|(
name|Context
name|child
range|:
name|chainContext
operator|.
name|contexts
control|)
block|{
name|build
argument_list|(
name|list
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
operator|!
name|list
operator|.
name|contains
argument_list|(
name|context
argument_list|)
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Context that wraps an object. */
specifier|private
specifier|static
class|class
name|WrapContext
implements|implements
name|Context
block|{
specifier|final
name|Object
name|target
decl_stmt|;
name|WrapContext
parameter_list|(
name|Object
name|target
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|target
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
comment|/** Empty context. */
specifier|static
class|class
name|EmptyContext
implements|implements
name|Context
block|{
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Context that wraps a chain of contexts. */
specifier|private
specifier|static
specifier|final
class|class
name|ChainContext
implements|implements
name|Context
block|{
specifier|final
name|ImmutableList
argument_list|<
name|Context
argument_list|>
name|contexts
decl_stmt|;
name|ChainContext
parameter_list|(
name|ImmutableList
argument_list|<
name|Context
argument_list|>
name|contexts
parameter_list|)
block|{
name|this
operator|.
name|contexts
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|contexts
argument_list|)
expr_stmt|;
for|for
control|(
name|Context
name|context
range|:
name|contexts
control|)
block|{
assert|assert
operator|!
operator|(
name|context
operator|instanceof
name|ChainContext
operator|)
operator|:
literal|"must be flat"
assert|;
block|}
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
for|for
control|(
name|Context
name|context
range|:
name|contexts
control|)
block|{
specifier|final
name|T
name|t
init|=
name|context
operator|.
name|unwrap
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
return|return
name|t
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Contexts.java
end_comment

end_unit

