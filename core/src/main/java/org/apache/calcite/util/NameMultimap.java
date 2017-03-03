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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|TreeMap
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
name|NameSet
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
decl_stmt|;
comment|/** Creates a NameMultimap based on an existing map. */
specifier|private
name|NameMultimap
parameter_list|(
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
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
assert|assert
name|this
operator|.
name|map
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
name|TreeMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|(
name|COMPARATOR
argument_list|)
argument_list|)
expr_stmt|;
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
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|list
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|caseSensitive
condition|)
block|{
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|list
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|!=
literal|null
operator|&&
operator|!
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
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
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|V
name|v
range|:
name|list
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|name
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
block|}
else|else
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
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
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|m
init|=
name|map
operator|.
name|subMap
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|true
argument_list|,
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|entry
range|:
name|m
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|V
name|v
range|:
name|entry
operator|.
name|getValue
argument_list|()
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
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
operator|!
name|range
argument_list|(
name|name
argument_list|,
name|caseSensitive
argument_list|)
operator|.
name|isEmpty
argument_list|()
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
return|;
block|}
block|}
end_class

begin_comment
comment|// End NameMultimap.java
end_comment

end_unit

