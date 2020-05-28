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
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Experimental
import|;
end_import

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
name|Collection
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|CaseInsensitiveComparator
operator|.
name|COMPARATOR
import|;
end_import

begin_comment
comment|/** Multimap whose keys are names and can be accessed with and without case  * sensitivity.  *  * @param<V> Value type */
end_comment

begin_class
specifier|public
class|class
name|NameMultimap
parameter_list|<
name|V
parameter_list|>
block|{
specifier|private
specifier|final
name|NameMap
argument_list|<
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|map
decl_stmt|;
comment|/** Creates a NameMultimap based on an existing map. */
specifier|private
name|NameMultimap
parameter_list|(
name|NameMap
argument_list|<
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
assert|assert
name|map
operator|.
name|map
argument_list|()
operator|.
name|comparator
argument_list|()
operator|==
name|COMPARATOR
assert|;
block|}
comment|/** Creates a NameMultimap, initially empty. */
specifier|public
name|NameMultimap
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|NameMap
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|map
operator|.
name|toString
argument_list|()
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
name|map
operator|.
name|hashCode
argument_list|()
return|;
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
name|obj
parameter_list|)
block|{
return|return
name|this
operator|==
name|obj
operator|||
name|obj
operator|instanceof
name|NameMultimap
operator|&&
name|map
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NameMultimap
operator|)
name|obj
operator|)
operator|.
name|map
argument_list|)
return|;
block|}
comment|/** Adds an entry to this multimap. */
specifier|public
name|void
name|put
parameter_list|(
name|String
name|name
parameter_list|,
name|V
name|v
parameter_list|)
block|{
name|List
argument_list|<
name|V
argument_list|>
name|list
init|=
name|map
argument_list|()
operator|.
name|computeIfAbsent
argument_list|(
name|name
argument_list|,
name|k
lambda|->
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
comment|/** Removes all entries that have the given case-sensitive key.    *    * @return Whether a value was removed */
annotation|@
name|Experimental
specifier|public
name|boolean
name|remove
parameter_list|(
name|String
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|list
init|=
name|map
argument_list|()
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|list
operator|.
name|remove
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/** Returns a map containing all the entries in this multimap that match the    * given name. */
specifier|public
name|Collection
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|V
argument_list|>
argument_list|>
name|range
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|range
init|=
name|map
operator|.
name|range
argument_list|(
name|name
argument_list|,
name|caseSensitive
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|V
argument_list|>
argument_list|>
name|result
init|=
name|range
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|flatMap
argument_list|(
name|e
lambda|->
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|v
lambda|->
name|Pair
operator|.
name|of
argument_list|(
name|e
operator|.
name|getKey
argument_list|()
argument_list|,
name|v
argument_list|)
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/** Returns whether this map contains a given key, with a given    * case-sensitivity. */
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|name
argument_list|,
name|caseSensitive
argument_list|)
return|;
block|}
comment|/** Returns the underlying map.    * Its size is the number of keys, not the number of values. */
specifier|public
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|map
parameter_list|()
block|{
return|return
name|map
operator|.
name|map
argument_list|()
return|;
block|}
block|}
end_class

end_unit

