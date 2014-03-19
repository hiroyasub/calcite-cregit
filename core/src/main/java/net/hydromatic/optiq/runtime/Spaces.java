begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_comment
comment|/** Utilities for creating strings of spaces. */
end_comment

begin_class
specifier|public
class|class
name|Spaces
block|{
comment|/** It doesn't look like this list is ever updated. But it is - when a call to    * to {@link SpaceList#get} causes an {@link IndexOutOfBoundsException}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"MismatchedQueryAndUpdateOfCollection"
argument_list|)
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|SPACE_LIST
init|=
operator|new
name|SpaceList
argument_list|()
decl_stmt|;
comment|/** The longest possible string of spaces. Fine as long as you don't try    * to print it.    *    *<p>Use with {@link StringBuilder#append(CharSequence, int, int)} to    * append spaces without doing memory allocation.</p>    */
specifier|public
specifier|static
specifier|final
name|CharSequence
name|MAX
init|=
name|sequence
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
comment|// Utility class. Do not instantiate.
specifier|private
name|Spaces
parameter_list|()
block|{
block|}
comment|/** Creates a sequence of {@code n} spaces. */
specifier|public
specifier|static
name|CharSequence
name|sequence
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
operator|new
name|SpaceString
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/** Returns a string of {@code n} spaces. */
specifier|public
specifier|static
name|String
name|of
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|SPACE_LIST
operator|.
name|get
argument_list|(
name|n
argument_list|)
return|;
block|}
comment|/** Appends {@code n} spaces to an {@link Appendable}. */
specifier|public
specifier|static
name|Appendable
name|append
parameter_list|(
name|Appendable
name|buf
parameter_list|,
name|int
name|n
parameter_list|)
throws|throws
name|IOException
block|{
name|buf
operator|.
name|append
argument_list|(
name|MAX
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
name|buf
return|;
block|}
comment|/** Appends {@code n} spaces to a {@link PrintWriter}. */
specifier|public
specifier|static
name|PrintWriter
name|append
parameter_list|(
name|PrintWriter
name|pw
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|pw
operator|.
name|append
argument_list|(
name|MAX
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
name|pw
return|;
block|}
comment|/** Appends {@code n} spaces to a {@link StringWriter}. */
specifier|public
specifier|static
name|StringWriter
name|append
parameter_list|(
name|StringWriter
name|pw
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|pw
operator|.
name|append
argument_list|(
name|MAX
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
name|pw
return|;
block|}
comment|/** Appends {@code n} spaces to a {@link StringBuilder}. */
specifier|public
specifier|static
name|StringBuilder
name|append
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|MAX
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
name|buf
return|;
block|}
comment|/** Appends {@code n} spaces to a {@link StringBuffer}. */
specifier|public
specifier|static
name|StringBuffer
name|append
parameter_list|(
name|StringBuffer
name|buf
parameter_list|,
name|int
name|n
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|MAX
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
return|return
name|buf
return|;
block|}
comment|/** Returns a string that is padded on the right with spaces to the given    * length. */
specifier|public
specifier|static
name|String
name|padRight
parameter_list|(
name|String
name|string
parameter_list|,
name|int
name|n
parameter_list|)
block|{
specifier|final
name|int
name|x
init|=
name|n
operator|-
name|string
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|<=
literal|0
condition|)
block|{
return|return
name|string
return|;
block|}
comment|// Replacing StringBuffer with String would hurt performance.
comment|//noinspection StringBufferReplaceableByString
return|return
name|append
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|string
argument_list|)
argument_list|,
name|x
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns a string that is padded on the left with spaces to the given    * length. */
specifier|public
specifier|static
name|String
name|padLeft
parameter_list|(
name|String
name|string
parameter_list|,
name|int
name|n
parameter_list|)
block|{
specifier|final
name|int
name|x
init|=
name|n
operator|-
name|string
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|x
operator|<=
literal|0
condition|)
block|{
return|return
name|string
return|;
block|}
comment|// Replacing StringBuffer with String would hurt performance.
comment|//noinspection StringBufferReplaceableByString
return|return
name|append
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|,
name|x
argument_list|)
operator|.
name|append
argument_list|(
name|string
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** A string of spaces. */
specifier|private
specifier|static
class|class
name|SpaceString
implements|implements
name|CharSequence
block|{
specifier|private
specifier|final
name|int
name|length
decl_stmt|;
specifier|private
name|SpaceString
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
comment|// Do not override equals and hashCode to be like String. CharSequence does
comment|// not require it.
annotation|@
name|SuppressWarnings
argument_list|(
literal|"NullableProblems"
argument_list|)
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|of
argument_list|(
name|length
argument_list|)
return|;
block|}
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
specifier|public
name|char
name|charAt
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
literal|' '
return|;
block|}
specifier|public
name|CharSequence
name|subSequence
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
return|return
operator|new
name|SpaceString
argument_list|(
name|end
operator|-
name|start
argument_list|)
return|;
block|}
block|}
comment|/** List whose {@code i}th entry is a string consisting of {@code i} spaces.    * It populates itself the first time you ask for a particular string, and    * caches the result. */
specifier|private
specifier|static
class|class
name|SpaceList
extends|extends
name|CopyOnWriteArrayList
argument_list|<
name|String
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
try|try
block|{
return|return
name|super
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|populate
argument_list|(
name|Math
operator|.
name|max
argument_list|(
literal|16
argument_list|,
name|index
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Populates this list with all prefix strings of a given string. All      * of the prefix strings share the same backing array of chars.      */
specifier|private
specifier|synchronized
name|void
name|populate
parameter_list|(
name|int
name|newSize
parameter_list|)
block|{
specifier|final
name|int
name|size
init|=
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|newSize
operator|<=
name|size
condition|)
block|{
return|return;
block|}
specifier|final
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|newSize
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|chars
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
specifier|final
name|int
name|length
init|=
name|newSize
operator|-
name|size
decl_stmt|;
specifier|final
name|int
name|offset
init|=
name|size
decl_stmt|;
comment|// addAll is much more efficient than repeated add for
comment|// CopyOnWriteArrayList
name|addAll
argument_list|(
operator|new
name|AbstractList
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|new
name|String
argument_list|(
name|chars
argument_list|,
literal|0
argument_list|,
name|offset
operator|+
name|index
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|length
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Spaces.java
end_comment

end_unit

