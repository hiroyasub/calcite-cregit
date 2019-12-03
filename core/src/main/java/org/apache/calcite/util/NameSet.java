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
name|Maps
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
name|Comparator
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
comment|/** Set of names that can be accessed with and without case sensitivity. */
end_comment

begin_class
specifier|public
class|class
name|NameSet
block|{
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|String
argument_list|>
name|COMPARATOR
init|=
name|CaseInsensitiveComparator
operator|.
name|COMPARATOR
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Object
name|DUMMY
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|NameMap
argument_list|<
name|Object
argument_list|>
name|names
decl_stmt|;
comment|/** Creates a NameSet based on an existing set. */
specifier|private
name|NameSet
parameter_list|(
name|NameMap
argument_list|<
name|Object
argument_list|>
name|names
parameter_list|)
block|{
name|this
operator|.
name|names
operator|=
name|names
expr_stmt|;
block|}
comment|/** Creates a NameSet, initially empty. */
specifier|public
name|NameSet
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
comment|/** Creates a NameSet that is an immutable copy of a given collection. */
specifier|public
specifier|static
name|NameSet
name|immutableCopyOf
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
return|return
operator|new
name|NameSet
argument_list|(
name|NameMap
operator|.
name|immutableCopyOf
argument_list|(
name|Maps
operator|.
name|asMap
argument_list|(
name|names
argument_list|,
name|k
lambda|->
name|DUMMY
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|names
operator|.
name|map
argument_list|()
operator|.
name|keySet
argument_list|()
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
name|names
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
name|NameSet
operator|&&
name|names
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|NameSet
operator|)
name|obj
operator|)
operator|.
name|names
argument_list|)
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|names
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|DUMMY
argument_list|)
expr_stmt|;
block|}
comment|/** Returns an iterable over all the entries in the set that match the given    * name. If case-sensitive, that iterable will have 0 or 1 elements; if    * case-insensitive, it may have 0 or more. */
specifier|public
name|Collection
argument_list|<
name|String
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
return|return
name|names
operator|.
name|range
argument_list|(
name|name
argument_list|,
name|caseSensitive
argument_list|)
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/** Returns whether this set contains the given name, with a given    * case-sensitivity. */
specifier|public
name|boolean
name|contains
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
return|return
name|names
operator|.
name|containsKey
argument_list|(
name|name
argument_list|,
name|caseSensitive
argument_list|)
return|;
block|}
comment|/** Returns the contents as an iterable. */
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|iterable
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|names
operator|.
name|map
argument_list|()
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

