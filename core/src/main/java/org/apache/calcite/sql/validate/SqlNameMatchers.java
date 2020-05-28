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
name|sql
operator|.
name|validate
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlIdentifier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Util
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
name|LinkedHashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
comment|/**  * Helpers for {@link SqlNameMatcher}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlNameMatchers
block|{
specifier|private
specifier|static
specifier|final
name|BaseMatcher
name|CASE_SENSITIVE
init|=
operator|new
name|BaseMatcher
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|BaseMatcher
name|CASE_INSENSITIVE
init|=
operator|new
name|BaseMatcher
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|SqlNameMatchers
parameter_list|()
block|{
block|}
comment|/** Returns a name matcher with the given case sensitivity. */
specifier|public
specifier|static
name|SqlNameMatcher
name|withCaseSensitive
parameter_list|(
specifier|final
name|boolean
name|caseSensitive
parameter_list|)
block|{
return|return
name|caseSensitive
condition|?
name|CASE_SENSITIVE
else|:
name|CASE_INSENSITIVE
return|;
block|}
comment|/** Creates a name matcher that can suggest corrections to what the user    * typed. It matches liberally (case-insensitively) and also records the last    * match. */
specifier|public
specifier|static
name|SqlNameMatcher
name|liberal
parameter_list|()
block|{
return|return
operator|new
name|LiberalNameMatcher
argument_list|()
return|;
block|}
comment|/** Partial implementation of {@link SqlNameMatcher}. */
specifier|private
specifier|static
class|class
name|BaseMatcher
implements|implements
name|SqlNameMatcher
block|{
specifier|private
specifier|final
name|boolean
name|caseSensitive
decl_stmt|;
name|BaseMatcher
parameter_list|(
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|()
block|{
return|return
name|caseSensitive
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|String
name|string
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|caseSensitive
condition|?
name|string
operator|.
name|equals
argument_list|(
name|name
argument_list|)
else|:
name|string
operator|.
name|equalsIgnoreCase
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|listMatches
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list0
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|list1
parameter_list|)
block|{
if|if
condition|(
name|list0
operator|.
name|size
argument_list|()
operator|!=
name|list1
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list0
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
name|s0
init|=
name|list0
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|s1
init|=
name|list1
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matches
argument_list|(
name|s0
argument_list|,
name|s1
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|K
extends|extends
name|List
argument_list|<
name|String
argument_list|>
parameter_list|,
name|V
parameter_list|>
annotation|@
name|Nullable
name|V
name|get
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|prefixNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|key
init|=
name|concat
argument_list|(
name|prefixNames
argument_list|,
name|names
argument_list|)
decl_stmt|;
if|if
condition|(
name|caseSensitive
condition|)
block|{
comment|//noinspection SuspiciousMethodCalls
return|return
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|listMatches
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|matched
argument_list|(
name|prefixNames
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|entry
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|concat
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|prefixNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
if|if
condition|(
name|prefixNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|names
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|prefixNames
argument_list|)
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
specifier|protected
name|void
name|matched
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|prefixNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
block|}
specifier|protected
name|List
argument_list|<
name|String
argument_list|>
name|bestMatch
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|bestString
parameter_list|()
block|{
return|return
name|SqlIdentifier
operator|.
name|getString
argument_list|(
name|bestMatch
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataTypeField
name|field
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|rowType
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|,
name|caseSensitive
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|frequency
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|names
control|)
block|{
if|if
condition|(
name|matches
argument_list|(
name|s
argument_list|,
name|name
argument_list|)
condition|)
block|{
operator|++
name|n
expr_stmt|;
block|}
block|}
return|return
name|n
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|createSet
parameter_list|()
block|{
return|return
name|isCaseSensitive
argument_list|()
condition|?
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
else|:
operator|new
name|TreeSet
argument_list|<>
argument_list|(
name|String
operator|.
name|CASE_INSENSITIVE_ORDER
argument_list|)
return|;
block|}
block|}
comment|/** Matcher that remembers the requests that were made of it. */
specifier|private
specifier|static
class|class
name|LiberalNameMatcher
extends|extends
name|BaseMatcher
block|{
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|matchedNames
decl_stmt|;
name|LiberalNameMatcher
parameter_list|()
block|{
name|super
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|listMatches
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|list0
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|list1
parameter_list|)
block|{
specifier|final
name|boolean
name|b
init|=
name|super
operator|.
name|listMatches
argument_list|(
name|list0
argument_list|,
name|list1
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
condition|)
block|{
name|matchedNames
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|list1
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|matched
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|prefixNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|matchedNames
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|Util
operator|.
name|startsWith
argument_list|(
name|names
argument_list|,
name|prefixNames
argument_list|)
condition|?
name|Util
operator|.
name|skip
argument_list|(
name|names
argument_list|,
name|prefixNames
operator|.
name|size
argument_list|()
argument_list|)
else|:
name|names
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|bestMatch
parameter_list|()
block|{
return|return
name|requireNonNull
argument_list|(
name|matchedNames
argument_list|,
literal|"matchedNames"
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

