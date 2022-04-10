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
name|avatica
operator|.
name|util
operator|.
name|Spaces
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_comment
comment|/**  * Builder for JSON documents (represented as {@link List}, {@link Map},  * {@link String}, {@link Boolean}, {@link Long}).  */
end_comment

begin_class
specifier|public
class|class
name|JsonBuilder
block|{
comment|/**    * Creates a JSON object (represented by a {@link Map}).    */
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
argument_list|>
name|map
parameter_list|()
block|{
comment|// Use LinkedHashMap to preserve order.
return|return
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
return|;
block|}
comment|/**    * Creates a JSON object (represented by a {@link List}).    */
specifier|public
name|List
argument_list|<
annotation|@
name|Nullable
name|Object
argument_list|>
name|list
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|()
return|;
block|}
comment|/**    * Adds a key/value pair to a JSON object.    */
specifier|public
name|JsonBuilder
name|put
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
argument_list|>
name|map
parameter_list|,
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Object
name|value
parameter_list|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Adds a key/value pair to a JSON object if the value is not null.    */
specifier|public
name|JsonBuilder
name|putIf
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
argument_list|>
name|map
parameter_list|,
name|String
name|name
parameter_list|,
annotation|@
name|Nullable
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/**    * Serializes an object consisting of maps, lists and atoms into a JSON    * string.    *    *<p>We should use a JSON library such as Jackson when Mondrian needs    * one elsewhere.</p>    */
specifier|public
name|String
name|toJsonString
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|append
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|o
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Appends a JSON object to a string builder.    */
specifier|public
name|void
name|append
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|indent
parameter_list|,
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Map
condition|)
block|{
comment|//noinspection unchecked
name|appendMap
argument_list|(
name|buf
argument_list|,
name|indent
argument_list|,
operator|(
name|Map
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|List
condition|)
block|{
name|appendList
argument_list|(
name|buf
argument_list|,
name|indent
argument_list|,
operator|(
name|List
argument_list|<
name|?
argument_list|>
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|String
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
operator|.
name|append
argument_list|(
operator|(
operator|(
name|String
operator|)
name|o
operator|)
operator|.
name|replace
argument_list|(
literal|"\""
argument_list|,
literal|"\\\""
argument_list|)
operator|.
name|replace
argument_list|(
literal|"\n"
argument_list|,
literal|"\\n"
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|o
operator|instanceof
name|Number
operator|||
name|o
operator|instanceof
name|Boolean
assert|;
name|buf
operator|.
name|append
argument_list|(
name|o
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|appendMap
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|indent
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
argument_list|>
name|map
parameter_list|)
block|{
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"{}"
argument_list|)
expr_stmt|;
return|return;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|buf
argument_list|,
name|indent
operator|+
literal|1
argument_list|)
expr_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
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
name|n
operator|++
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|buf
argument_list|,
name|indent
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|append
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
expr_stmt|;
name|append
argument_list|(
name|buf
argument_list|,
name|indent
operator|+
literal|1
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|newline
argument_list|(
name|buf
argument_list|,
name|indent
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|newline
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|indent
parameter_list|)
block|{
name|Spaces
operator|.
name|append
argument_list|(
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
argument_list|,
name|indent
operator|*
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|appendList
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|indent
parameter_list|,
name|List
argument_list|<
name|?
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"[]"
argument_list|)
expr_stmt|;
return|return;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|buf
argument_list|,
name|indent
operator|+
literal|1
argument_list|)
expr_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list
control|)
block|{
if|if
condition|(
name|n
operator|++
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
name|newline
argument_list|(
name|buf
argument_list|,
name|indent
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|append
argument_list|(
name|buf
argument_list|,
name|indent
operator|+
literal|1
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
name|newline
argument_list|(
name|buf
argument_list|,
name|indent
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

