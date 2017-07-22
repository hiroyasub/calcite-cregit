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
name|os
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
name|DataContext
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
name|linq4j
operator|.
name|AbstractEnumerable
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Enumerator
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
name|RelDataTypeFactory
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
name|schema
operator|.
name|ScannableTable
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
name|schema
operator|.
name|Schema
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
name|schema
operator|.
name|Statistic
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
name|schema
operator|.
name|Statistics
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
name|type
operator|.
name|SqlTypeName
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
name|ImmutableBitSet
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
name|java
operator|.
name|util
operator|.
name|NoSuchElementException
import|;
end_import

begin_comment
comment|/**  * Table function that executes the OS "git log" command  * to discover git commits.  */
end_comment

begin_class
specifier|public
class|class
name|GitCommitsTableFunction
block|{
comment|/** An example of the timestamp + offset at the end of author and committer    * fields. */
specifier|private
specifier|static
specifier|final
name|String
name|TS_OFF
init|=
literal|"1500769547 -0700"
decl_stmt|;
comment|/** An example of the offset at the end of author and committer fields. */
specifier|private
specifier|static
specifier|final
name|String
name|OFF
init|=
literal|"-0700"
decl_stmt|;
specifier|private
name|GitCommitsTableFunction
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|ScannableTable
name|eval
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
return|return
operator|new
name|ScannableTable
argument_list|()
block|{
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
specifier|final
name|Enumerable
argument_list|<
name|String
argument_list|>
name|enumerable
init|=
name|Processes
operator|.
name|processLines
argument_list|(
literal|"git"
argument_list|,
literal|"log"
argument_list|,
literal|"--pretty=raw"
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
specifier|final
name|Enumerator
argument_list|<
name|String
argument_list|>
name|e
init|=
name|enumerable
operator|.
name|enumerator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|private
name|Object
index|[]
name|objects
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|public
name|Object
index|[]
name|current
parameter_list|()
block|{
if|if
condition|(
name|objects
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NoSuchElementException
argument_list|()
throw|;
block|}
return|return
name|objects
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|moveNext
argument_list|()
condition|)
block|{
name|objects
operator|=
literal|null
expr_stmt|;
return|return
literal|false
return|;
block|}
name|objects
operator|=
operator|new
name|Object
index|[
literal|9
index|]
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|String
name|line
init|=
name|e
operator|.
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
break|break;
comment|// next line will be start of comments
block|}
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"commit "
argument_list|)
condition|)
block|{
name|objects
index|[
literal|0
index|]
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"commit "
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"tree "
argument_list|)
condition|)
block|{
name|objects
index|[
literal|1
index|]
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"tree "
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"parent "
argument_list|)
condition|)
block|{
if|if
condition|(
name|objects
index|[
literal|2
index|]
operator|==
literal|null
condition|)
block|{
name|objects
index|[
literal|2
index|]
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"parent "
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|objects
index|[
literal|3
index|]
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"parent "
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"author "
argument_list|)
condition|)
block|{
name|objects
index|[
literal|4
index|]
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"author "
operator|.
name|length
argument_list|()
argument_list|,
name|line
operator|.
name|length
argument_list|()
operator|-
name|TS_OFF
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|objects
index|[
literal|5
index|]
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|line
operator|.
name|length
argument_list|()
operator|-
name|TS_OFF
operator|.
name|length
argument_list|()
argument_list|,
name|line
operator|.
name|length
argument_list|()
operator|-
name|OFF
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
literal|1000
expr_stmt|;
block|}
if|else if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
literal|"committer "
argument_list|)
condition|)
block|{
name|objects
index|[
literal|6
index|]
operator|=
name|line
operator|.
name|substring
argument_list|(
literal|"committer "
operator|.
name|length
argument_list|()
argument_list|,
name|line
operator|.
name|length
argument_list|()
operator|-
name|TS_OFF
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|objects
index|[
literal|7
index|]
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|line
operator|.
name|substring
argument_list|(
name|line
operator|.
name|length
argument_list|()
operator|-
name|TS_OFF
operator|.
name|length
argument_list|()
argument_list|,
name|line
operator|.
name|length
argument_list|()
operator|-
name|OFF
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
argument_list|)
operator|*
literal|1000
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|e
operator|.
name|moveNext
argument_list|()
condition|)
block|{
comment|// We have a row, and it's the last because input is empty
return|return
literal|true
return|;
block|}
block|}
for|for
control|(
init|;
condition|;
control|)
block|{
if|if
condition|(
operator|!
name|e
operator|.
name|moveNext
argument_list|()
condition|)
block|{
comment|// We have a row, and it's the last because input is empty
name|objects
index|[
literal|8
index|]
operator|=
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|final
name|String
name|line
init|=
name|e
operator|.
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// We're seeing the empty line at the end of message
name|objects
index|[
literal|8
index|]
operator|=
name|b
operator|.
name|toString
argument_list|()
expr_stmt|;
name|b
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
name|b
operator|.
name|append
argument_list|(
name|line
operator|.
name|substring
argument_list|(
literal|"    "
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
block|}
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
specifier|public
name|void
name|close
parameter_list|()
block|{
name|e
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
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"commit"
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
literal|40
argument_list|)
operator|.
name|add
argument_list|(
literal|"tree"
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
literal|40
argument_list|)
operator|.
name|add
argument_list|(
literal|"parent"
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
literal|40
argument_list|)
operator|.
name|add
argument_list|(
literal|"parent2"
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
literal|40
argument_list|)
operator|.
name|add
argument_list|(
literal|"author"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|add
argument_list|(
literal|"author_timestamp"
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
literal|"committer"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|add
argument_list|(
literal|"commit_timestamp"
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
literal|"message"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|of
argument_list|(
literal|1000d
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End GitCommitsTableFunction.java
end_comment

end_unit

