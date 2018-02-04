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
name|Function2
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractCollection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractSet
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link Lookup} that uses an underlying map.  *  * @param<K> Key type  * @param<V> Value type  */
end_comment

begin_class
class|class
name|LookupImpl
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|AbstractEnumerable
argument_list|<
name|Grouping
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
implements|implements
name|Lookup
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|K
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|map
decl_stmt|;
comment|/**    * Creates a MultiMapImpl.    *    * @param map Underlying map    */
name|LookupImpl
parameter_list|(
name|Map
argument_list|<
name|K
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
block|}
specifier|public
name|Enumerator
argument_list|<
name|Grouping
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|Enumerator
argument_list|<
name|Grouping
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
argument_list|()
block|{
name|Enumerator
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|>
name|enumerator
init|=
name|Linq4j
operator|.
name|enumerator
argument_list|(
name|map
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|Grouping
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|current
parameter_list|()
block|{
specifier|final
name|Entry
argument_list|<
name|K
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|keyAndList
init|=
name|enumerator
operator|.
name|current
argument_list|()
decl_stmt|;
return|return
operator|new
name|GroupingImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|keyAndList
operator|.
name|getKey
argument_list|()
argument_list|,
name|keyAndList
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
name|enumerator
operator|.
name|moveNext
argument_list|()
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|enumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
comment|// Map methods
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|map
operator|.
name|isEmpty
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|V
argument_list|>
name|list
init|=
operator|(
name|List
argument_list|<
name|V
argument_list|>
operator|)
name|value
decl_stmt|;
return|return
name|map
operator|.
name|containsValue
argument_list|(
name|list
argument_list|)
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|V
argument_list|>
name|get
parameter_list|(
name|Object
name|key
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
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|list
operator|==
literal|null
condition|?
literal|null
else|:
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|list
argument_list|)
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|V
argument_list|>
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
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
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|list
operator|==
literal|null
condition|?
literal|null
else|:
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|list
argument_list|)
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|V
argument_list|>
name|remove
parameter_list|(
name|Object
name|key
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
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
return|return
name|list
operator|==
literal|null
condition|?
literal|null
else|:
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|list
argument_list|)
return|;
block|}
specifier|public
name|void
name|putAll
parameter_list|(
name|Map
argument_list|<
name|?
extends|extends
name|K
argument_list|,
name|?
extends|extends
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
name|m
parameter_list|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|?
extends|extends
name|K
argument_list|,
name|?
extends|extends
name|Enumerable
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
name|map
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|K
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|Collection
argument_list|<
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
name|values
parameter_list|()
block|{
specifier|final
name|Collection
argument_list|<
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|lists
init|=
name|map
operator|.
name|values
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractCollection
argument_list|<
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|()
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
name|lists
operator|.
name|iterator
argument_list|()
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
name|Enumerable
argument_list|<
name|V
argument_list|>
name|next
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
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
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|lists
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|>
name|entries
init|=
name|map
operator|.
name|entrySet
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractSet
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|>
name|iterator
init|=
name|entries
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
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
name|Entry
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
name|next
parameter_list|()
block|{
specifier|final
name|Entry
argument_list|<
name|K
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|entry
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
operator|new
name|AbstractMap
operator|.
name|SimpleEntry
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
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
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|entries
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|public
parameter_list|<
name|TResult
parameter_list|>
name|Enumerable
argument_list|<
name|TResult
argument_list|>
name|applyResultSelector
parameter_list|(
specifier|final
name|Function2
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|,
name|TResult
argument_list|>
name|resultSelector
parameter_list|)
block|{
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|TResult
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|TResult
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Enumerator
argument_list|<
name|Grouping
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|groupingEnumerator
init|=
name|LookupImpl
operator|.
name|this
operator|.
name|enumerator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|TResult
argument_list|>
argument_list|()
block|{
specifier|public
name|TResult
name|current
parameter_list|()
block|{
specifier|final
name|Grouping
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|grouping
init|=
name|groupingEnumerator
operator|.
name|current
argument_list|()
decl_stmt|;
return|return
name|resultSelector
operator|.
name|apply
argument_list|(
name|grouping
operator|.
name|getKey
argument_list|()
argument_list|,
name|grouping
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
return|return
name|groupingEnumerator
operator|.
name|moveNext
argument_list|()
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|groupingEnumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|groupingEnumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns an enumerable over the values in this lookup, in map order.    * If the map is sorted, the values will be emitted sorted by key.    */
specifier|public
name|Enumerable
argument_list|<
name|V
argument_list|>
name|valuesEnumerable
parameter_list|()
block|{
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|V
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Enumerator
argument_list|<
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
name|listEnumerator
init|=
name|Linq4j
operator|.
name|iterableEnumerator
argument_list|(
name|values
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
name|Enumerator
argument_list|<
name|V
argument_list|>
name|enumerator
init|=
name|Linq4j
operator|.
name|emptyEnumerator
argument_list|()
decl_stmt|;
specifier|public
name|V
name|current
parameter_list|()
block|{
return|return
name|enumerator
operator|.
name|current
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
name|enumerator
operator|.
name|moveNext
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|listEnumerator
operator|.
name|moveNext
argument_list|()
condition|)
block|{
name|enumerator
operator|=
name|Linq4j
operator|.
name|emptyEnumerator
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
name|enumerator
operator|=
name|listEnumerator
operator|.
name|current
argument_list|()
operator|.
name|enumerator
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|listEnumerator
operator|.
name|reset
argument_list|()
expr_stmt|;
name|enumerator
operator|=
name|Linq4j
operator|.
name|emptyEnumerator
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|enumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End LookupImpl.java
end_comment

end_unit

