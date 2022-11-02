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
name|util
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
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Supplier that awaits a value and allows the value to be set, once,  * to a not-null value. The value supplied by {@link #get} is never null.  *  *<p>Not thread-safe.  *  * @param<E> Element type  */
end_comment

begin_class
specifier|public
class|class
name|MonotonicSupplier
parameter_list|<
name|E
parameter_list|>
implements|implements
name|Consumer
argument_list|<
name|E
argument_list|>
implements|,
name|Supplier
argument_list|<
name|E
argument_list|>
block|{
specifier|private
annotation|@
name|Nullable
name|E
name|e
decl_stmt|;
comment|/**    * {@inheritDoc}    *    *<p>Sets the value once and for all.    */
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
name|E
name|e
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|e
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"accept has been called already"
argument_list|)
throw|;
block|}
name|this
operator|.
name|e
operator|=
name|requireNonNull
argument_list|(
name|e
argument_list|,
literal|"element must not be null"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|E
name|get
parameter_list|()
block|{
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"accept has not been called"
argument_list|)
throw|;
block|}
return|return
name|e
return|;
block|}
block|}
end_class

end_unit

