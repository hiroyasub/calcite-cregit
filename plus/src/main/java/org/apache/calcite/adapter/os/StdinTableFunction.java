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
name|config
operator|.
name|CalciteConnectionConfig
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
name|SqlCall
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
name|SqlNode
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
name|io
operator|.
name|BufferedReader
import|;
end_import

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
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
comment|/**  * Table function that reads stdin and returns one row per line.  */
end_comment

begin_class
specifier|public
class|class
name|StdinTableFunction
block|{
specifier|private
name|StdinTableFunction
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
name|InputStream
name|is
init|=
name|DataContext
operator|.
name|Variable
operator|.
name|STDIN
operator|.
name|get
argument_list|(
name|root
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
specifier|final
name|InputStreamReader
name|in
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
specifier|final
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
name|String
name|line
decl_stmt|;
name|int
name|i
decl_stmt|;
specifier|public
name|Object
index|[]
name|current
parameter_list|()
block|{
if|if
condition|(
name|line
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
operator|new
name|Object
index|[]
block|{
name|i
block|,
name|line
block|}
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
try|try
block|{
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
operator|++
name|i
expr_stmt|;
return|return
name|line
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|IOException
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
try|try
block|{
name|br
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
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
literal|"ordinal"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|add
argument_list|(
literal|"line"
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
literal|1
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
specifier|public
name|boolean
name|isRolledUp
parameter_list|(
name|String
name|column
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|rolledUpColumnValidInsideAgg
parameter_list|(
name|String
name|column
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlNode
name|parent
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

