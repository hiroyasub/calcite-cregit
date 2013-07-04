begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|FlatLists
import|;
end_import

begin_comment
comment|/**  * An immutable list of {@link Integer} values backed by an array of  * {@code int}s.  */
end_comment

begin_class
specifier|public
class|class
name|ImmutableIntList
extends|extends
name|FlatLists
operator|.
name|AbstractFlatList
argument_list|<
name|Integer
argument_list|>
block|{
specifier|private
specifier|final
name|int
index|[]
name|ints
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ImmutableIntList
name|EMPTY
init|=
operator|new
name|ImmutableIntList
argument_list|()
decl_stmt|;
comment|// Does not copy array. Must remain private.
specifier|private
name|ImmutableIntList
parameter_list|(
name|int
modifier|...
name|ints
parameter_list|)
block|{
name|this
operator|.
name|ints
operator|=
name|ints
expr_stmt|;
block|}
comment|/** Creates an ImmutableIntList from an array of {@code int}. */
specifier|public
specifier|static
name|ImmutableIntList
name|of
parameter_list|(
name|int
modifier|...
name|ints
parameter_list|)
block|{
return|return
operator|new
name|ImmutableIntList
argument_list|(
name|ints
operator|.
name|clone
argument_list|()
argument_list|)
return|;
block|}
comment|/** Creates an ImmutableIntList from an array of {@code int}. */
specifier|public
specifier|static
name|ImmutableIntList
name|copyOf
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|list
parameter_list|)
block|{
if|if
condition|(
name|list
operator|instanceof
name|ImmutableIntList
condition|)
block|{
return|return
operator|(
name|ImmutableIntList
operator|)
name|list
return|;
block|}
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
specifier|final
name|int
index|[]
name|ints
init|=
operator|new
name|int
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Number
name|number
range|:
name|list
control|)
block|{
name|ints
index|[
name|i
operator|++
index|]
operator|=
name|number
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ImmutableIntList
argument_list|(
name|ints
argument_list|)
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
name|Arrays
operator|.
name|hashCode
argument_list|(
name|ints
argument_list|)
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
name|ImmutableIntList
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|ints
argument_list|,
operator|(
operator|(
name|ImmutableIntList
operator|)
name|obj
operator|)
operator|.
name|ints
argument_list|)
operator|||
name|obj
operator|instanceof
name|List
operator|&&
name|obj
operator|.
name|equals
argument_list|(
name|this
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
name|Arrays
operator|.
name|toString
argument_list|(
name|ints
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|ints
operator|.
name|length
operator|==
literal|0
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|ints
operator|.
name|length
return|;
block|}
specifier|public
name|Object
index|[]
name|toArray
parameter_list|()
block|{
specifier|final
name|Object
index|[]
name|objects
init|=
operator|new
name|Object
index|[
name|ints
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|objects
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|objects
index|[
name|i
index|]
operator|=
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
return|return
name|objects
return|;
block|}
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
index|[]
name|toArray
parameter_list|(
name|T
index|[]
name|a
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|ints
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|length
operator|<
name|size
condition|)
block|{
comment|// Make a new array of a's runtime type, but my contents:
name|a
operator|=
name|a
operator|.
name|getClass
argument_list|()
operator|==
name|Object
index|[]
operator|.
name|class
condition|?
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
name|size
index|]
else|:
operator|(
name|T
index|[]
operator|)
name|Array
operator|.
name|newInstance
argument_list|(
name|a
operator|.
name|getClass
argument_list|()
operator|.
name|getComponentType
argument_list|()
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|getClass
argument_list|()
operator|==
name|Integer
index|[]
operator|.
name|class
condition|)
block|{
specifier|final
name|Integer
index|[]
name|integers
init|=
operator|(
name|Integer
index|[]
operator|)
name|a
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|integers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|integers
index|[
name|i
index|]
operator|=
name|ints
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|toArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|a
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|a
operator|.
name|length
operator|>
name|size
condition|)
block|{
name|a
index|[
name|size
index|]
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|a
return|;
block|}
specifier|public
name|Integer
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|ints
index|[
name|index
index|]
return|;
block|}
specifier|public
name|int
name|indexOf
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Integer
condition|)
block|{
specifier|final
name|int
name|seek
init|=
operator|(
name|Integer
operator|)
name|o
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|ints
index|[
name|i
index|]
operator|==
name|seek
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|public
name|int
name|lastIndexOf
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Integer
condition|)
block|{
specifier|final
name|int
name|seek
init|=
operator|(
name|Integer
operator|)
name|o
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|ints
operator|.
name|length
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
operator|--
name|i
control|)
block|{
if|if
condition|(
name|ints
index|[
name|i
index|]
operator|==
name|seek
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

begin_comment
comment|// End ImmutableIntList.java
end_comment

end_unit

