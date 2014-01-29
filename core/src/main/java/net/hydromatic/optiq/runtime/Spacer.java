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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
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
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * Efficiently writes strings of spaces.  */
end_comment

begin_class
specifier|public
class|class
name|Spacer
block|{
specifier|private
specifier|static
specifier|final
name|ReentrantLock
name|LOCK
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
comment|/** Array of spaces at least as long as any Spacer in existence. */
specifier|private
specifier|static
name|char
index|[]
name|SPACES
init|=
block|{
literal|' '
block|}
decl_stmt|;
specifier|private
name|int
name|n
decl_stmt|;
comment|/** Creates a Spacer with zero spaces. */
specifier|public
name|Spacer
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a Spacer with a given number of spaces. */
specifier|public
name|Spacer
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|set
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the current number of spaces. */
specifier|public
name|Spacer
name|set
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
name|ensureSpaces
argument_list|(
name|n
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Returns the current number of spaces. */
specifier|public
name|int
name|get
parameter_list|()
block|{
return|return
name|n
return|;
block|}
comment|/** Increases the current number of spaces by {@code n}. */
specifier|public
name|Spacer
name|add
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|set
argument_list|(
name|this
operator|.
name|n
operator|+
name|n
argument_list|)
return|;
block|}
comment|/** Reduces the current number of spaces by {@code n}. */
specifier|public
name|Spacer
name|subtract
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|set
argument_list|(
name|this
operator|.
name|n
operator|-
name|n
argument_list|)
return|;
block|}
comment|/** Returns a string of the current number of spaces. */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|new
name|String
argument_list|(
name|SPACES
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
return|;
block|}
comment|/** Appends current number of spaces to a {@link StringBuilder}. */
specifier|public
name|StringBuilder
name|spaces
parameter_list|(
name|StringBuilder
name|buf
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|SPACES
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
comment|/** Appends current number of spaces to a {@link Writer}. */
specifier|public
name|Writer
name|spaces
parameter_list|(
name|Writer
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
name|buf
operator|.
name|write
argument_list|(
name|SPACES
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
comment|/** Appends current number of spaces to a {@link StringWriter}. */
specifier|public
name|StringWriter
name|spaces
parameter_list|(
name|StringWriter
name|buf
parameter_list|)
block|{
name|buf
operator|.
name|write
argument_list|(
name|SPACES
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
comment|/** Appends current number of spaces to a {@link PrintWriter}. */
specifier|public
name|PrintWriter
name|spaces
parameter_list|(
name|PrintWriter
name|buf
parameter_list|)
block|{
name|buf
operator|.
name|write
argument_list|(
name|SPACES
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
specifier|private
specifier|static
name|void
name|ensureSpaces
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|LOCK
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|SPACES
operator|.
name|length
operator|<
name|n
condition|)
block|{
name|char
index|[]
name|newSpaces
init|=
operator|new
name|char
index|[
name|n
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|newSpaces
argument_list|,
literal|' '
argument_list|)
expr_stmt|;
comment|// atomic assignment; other Spacer instances may be using this
name|SPACES
operator|=
name|newSpaces
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|LOCK
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** Returns a string that is padded on the right with spaces to the current    * length. */
specifier|public
name|String
name|padRight
parameter_list|(
name|String
name|string
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
operator|new
name|StringBuilder
argument_list|(
name|string
argument_list|)
operator|.
name|append
argument_list|(
name|SPACES
argument_list|,
literal|0
argument_list|,
name|x
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End Spacer.java
end_comment

end_unit

