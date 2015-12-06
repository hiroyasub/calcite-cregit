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
name|avatica
operator|.
name|util
package|;
end_package

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
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.avatica.util.Cursor}  * on top of an {@link Iterator} that  * returns a record for each row. The returned record is cached to avoid  * multiple computations of current row.  *  * @param<E> Element type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|IteratorCursor
parameter_list|<
name|E
parameter_list|>
extends|extends
name|PositionedCursor
argument_list|<
name|E
argument_list|>
block|{
specifier|private
name|Position
name|position
init|=
name|Position
operator|.
name|BEFORE_START
decl_stmt|;
specifier|private
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
decl_stmt|;
specifier|private
name|E
name|current
init|=
literal|null
decl_stmt|;
comment|/**    * Creates an {@code IteratorCursor}.    *    * @param iterator input iterator    */
specifier|protected
name|IteratorCursor
parameter_list|(
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
block|}
specifier|public
name|boolean
name|next
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
name|position
operator|=
name|Position
operator|.
name|OK
expr_stmt|;
return|return
literal|true
return|;
block|}
name|current
operator|=
literal|null
expr_stmt|;
name|position
operator|=
name|Position
operator|.
name|AFTER_END
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|current
operator|=
literal|null
expr_stmt|;
name|position
operator|=
name|Position
operator|.
name|CLOSED
expr_stmt|;
if|if
condition|(
name|iterator
operator|instanceof
name|AutoCloseable
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|AutoCloseable
operator|)
name|iterator
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
specifier|protected
name|E
name|current
parameter_list|()
block|{
if|if
condition|(
name|position
operator|!=
name|Position
operator|.
name|OK
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|current
return|;
block|}
comment|/** Are we positioned on a valid row? */
specifier|private
enum|enum
name|Position
block|{
name|CLOSED
block|,
name|BEFORE_START
block|,
name|OK
block|,
name|AFTER_END
block|}
block|}
end_class

begin_comment
comment|// End IteratorCursor.java
end_comment

end_unit

