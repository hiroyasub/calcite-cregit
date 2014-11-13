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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Pair of an element and an ordinal.  *  * @param<E> Element type  */
end_comment

begin_class
specifier|public
class|class
name|Ord
parameter_list|<
name|E
parameter_list|>
implements|implements
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|E
argument_list|>
block|{
specifier|public
specifier|final
name|int
name|i
decl_stmt|;
specifier|public
specifier|final
name|E
name|e
decl_stmt|;
comment|/**    * Creates an Ord.    */
specifier|public
name|Ord
parameter_list|(
name|int
name|i
parameter_list|,
name|E
name|e
parameter_list|)
block|{
name|this
operator|.
name|i
operator|=
name|i
expr_stmt|;
name|this
operator|.
name|e
operator|=
name|e
expr_stmt|;
block|}
comment|/**    * Creates an Ord.    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Ord
argument_list|<
name|E
argument_list|>
name|of
parameter_list|(
name|int
name|n
parameter_list|,
name|E
name|e
parameter_list|)
block|{
return|return
operator|new
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|(
name|n
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/**    * Creates an iterable of {@code Ord}s over an iterable.    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Iterable
argument_list|<
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|>
name|zip
parameter_list|(
specifier|final
name|Iterable
argument_list|<
name|E
argument_list|>
name|iterable
parameter_list|)
block|{
return|return
operator|new
name|Iterable
argument_list|<
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|zip
argument_list|(
name|iterable
operator|.
name|iterator
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**    * Creates an iterator of {@code Ord}s over an iterator.    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Iterator
argument_list|<
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|>
name|zip
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|)
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|()
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
specifier|public
name|Ord
argument_list|<
name|E
argument_list|>
name|next
parameter_list|()
block|{
return|return
name|Ord
operator|.
name|of
argument_list|(
name|n
operator|++
argument_list|,
name|iterator
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**    * Returns a numbered list based on an array.    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|>
name|zip
parameter_list|(
specifier|final
name|E
index|[]
name|elements
parameter_list|)
block|{
return|return
name|zip
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|elements
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns a numbered list.    */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|>
name|zip
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|elements
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Ord
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Ord
argument_list|<
name|E
argument_list|>
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|index
argument_list|,
name|elements
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|elements
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Integer
name|getKey
parameter_list|()
block|{
return|return
name|i
return|;
block|}
specifier|public
name|E
name|getValue
parameter_list|()
block|{
return|return
name|e
return|;
block|}
specifier|public
name|E
name|setValue
parameter_list|(
name|E
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

begin_comment
comment|// End Ord.java
end_comment

end_unit

