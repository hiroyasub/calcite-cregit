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
name|runtime
package|;
end_package

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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/**  * Map that allows you to partition values into lists according to a common  * key, and then convert those lists into an iterator of sorted arrays.  *  * @param<K> Key type  * @param<V> Value type  */
end_comment

begin_class
specifier|public
class|class
name|SortedMultiMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|HashMap
argument_list|<
name|K
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
block|{
specifier|public
name|void
name|putMulti
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
name|List
argument_list|<
name|V
argument_list|>
name|list
init|=
name|put
argument_list|(
name|key
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|key
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Iterator
argument_list|<
name|V
index|[]
argument_list|>
name|arrays
parameter_list|(
specifier|final
name|Comparator
argument_list|<
name|V
argument_list|>
name|comparator
parameter_list|)
block|{
specifier|final
name|Iterator
argument_list|<
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|iterator
init|=
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|V
index|[]
argument_list|>
argument_list|()
block|{
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
name|V
index|[]
name|next
parameter_list|()
block|{
name|List
argument_list|<
name|V
argument_list|>
name|list
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|V
index|[]
name|vs
init|=
operator|(
name|V
index|[]
operator|)
name|list
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|vs
argument_list|,
name|comparator
argument_list|)
expr_stmt|;
return|return
name|vs
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
comment|/** Shortcut method if the partition key is empty. We know that we would end    * up with a map with just one entry, so save ourselves the trouble of all    * that hashing. */
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|Iterator
argument_list|<
name|V
index|[]
argument_list|>
name|singletonArrayIterator
parameter_list|(
name|Comparator
argument_list|<
name|V
argument_list|>
name|comparator
parameter_list|,
name|List
argument_list|<
name|V
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|SortedMultiMap
argument_list|<
name|Object
argument_list|,
name|V
argument_list|>
name|multiMap
init|=
operator|new
name|SortedMultiMap
argument_list|<>
argument_list|()
decl_stmt|;
name|multiMap
operator|.
name|put
argument_list|(
literal|"x"
argument_list|,
name|list
argument_list|)
expr_stmt|;
return|return
name|multiMap
operator|.
name|arrays
argument_list|(
name|comparator
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SortedMultiMap.java
end_comment

end_unit

