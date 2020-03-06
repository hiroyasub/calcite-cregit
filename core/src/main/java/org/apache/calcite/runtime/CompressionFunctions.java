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
name|runtime
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
name|ByteString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteOrder
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
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|DeflaterOutputStream
import|;
end_import

begin_comment
comment|/**  * A collection of functions used in compression and decompression.  */
end_comment

begin_class
specifier|public
class|class
name|CompressionFunctions
block|{
specifier|private
name|CompressionFunctions
parameter_list|()
block|{
block|}
comment|/**    * MySql Compression is based on zlib.    *<a href="https://docs.oracle.com/javase/8/docs/api/java/util/zip/Deflater.html">Deflater</a>    * is used to implement compression.    */
specifier|public
specifier|static
name|ByteString
name|compress
parameter_list|(
name|String
name|data
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|data
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|data
argument_list|)
condition|)
block|{
return|return
operator|new
name|ByteString
argument_list|(
operator|new
name|byte
index|[
literal|0
index|]
argument_list|)
return|;
block|}
name|ByteArrayOutputStream
name|outputStream
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ByteBuffer
name|dataLength
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|dataLength
operator|.
name|order
argument_list|(
name|ByteOrder
operator|.
name|LITTLE_ENDIAN
argument_list|)
expr_stmt|;
name|dataLength
operator|.
name|putInt
argument_list|(
name|data
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|outputStream
operator|.
name|write
argument_list|(
name|dataLength
operator|.
name|array
argument_list|()
argument_list|)
expr_stmt|;
name|DeflaterOutputStream
name|inflaterStream
init|=
operator|new
name|DeflaterOutputStream
argument_list|(
name|outputStream
argument_list|)
decl_stmt|;
name|inflaterStream
operator|.
name|write
argument_list|(
name|data
operator|.
name|getBytes
argument_list|(
name|Charset
operator|.
name|defaultCharset
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|inflaterStream
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
operator|new
name|ByteString
argument_list|(
name|outputStream
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

