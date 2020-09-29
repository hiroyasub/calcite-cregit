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
name|adapter
operator|.
name|file
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
name|Enumerator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jsoup
operator|.
name|select
operator|.
name|Elements
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

begin_comment
comment|/**  * Wraps {@link FileReader} and {@link FileRowConverter}, enumerates tr DOM  * elements as table rows.  */
end_comment

begin_class
class|class
name|FileEnumerator
implements|implements
name|Enumerator
argument_list|<
name|Object
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Elements
argument_list|>
name|iterator
decl_stmt|;
specifier|private
specifier|final
name|FileRowConverter
name|converter
decl_stmt|;
specifier|private
specifier|final
name|int
index|[]
name|fields
decl_stmt|;
specifier|private
name|Object
name|current
decl_stmt|;
name|FileEnumerator
parameter_list|(
name|Iterator
argument_list|<
name|Elements
argument_list|>
name|iterator
parameter_list|,
name|FileRowConverter
name|converter
parameter_list|)
block|{
name|this
argument_list|(
name|iterator
argument_list|,
name|converter
argument_list|,
name|identityList
argument_list|(
name|converter
operator|.
name|width
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|FileEnumerator
parameter_list|(
name|Iterator
argument_list|<
name|Elements
argument_list|>
name|iterator
parameter_list|,
name|FileRowConverter
name|converter
parameter_list|,
name|int
index|[]
name|fields
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|iterator
expr_stmt|;
name|this
operator|.
name|converter
operator|=
name|converter
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|current
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|moveNext
argument_list|()
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|this
operator|.
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Elements
name|row
init|=
name|this
operator|.
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|current
operator|=
name|this
operator|.
name|converter
operator|.
name|toRow
argument_list|(
name|row
argument_list|,
name|this
operator|.
name|fields
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
name|current
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
decl||
name|Error
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
comment|// required by linq4j Enumerator interface
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
comment|// required by linq4j Enumerator interface
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
comment|/** Returns an array of integers {0, ..., n - 1}. */
specifier|private
specifier|static
name|int
index|[]
name|identityList
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|int
index|[]
name|integers
init|=
operator|new
name|int
index|[
name|n
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
name|n
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
name|i
expr_stmt|;
block|}
return|return
name|integers
return|;
block|}
block|}
end_class

end_unit

