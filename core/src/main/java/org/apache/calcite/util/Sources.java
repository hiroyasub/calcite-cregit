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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_comment
comment|/**  * Utilities for {@link Source}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Sources
block|{
specifier|private
name|Sources
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|Source
name|of
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
operator|new
name|FileSource
argument_list|(
name|file
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Source
name|file
parameter_list|(
name|File
name|baseDirectory
parameter_list|,
name|String
name|fileName
parameter_list|)
block|{
specifier|final
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
if|if
condition|(
name|baseDirectory
operator|!=
literal|null
operator|&&
operator|!
name|file
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
return|return
name|of
argument_list|(
operator|new
name|File
argument_list|(
name|baseDirectory
argument_list|,
name|fileName
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|of
argument_list|(
name|file
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
name|Source
name|url
parameter_list|(
name|String
name|url
parameter_list|)
block|{
try|try
block|{
name|URL
name|url_
init|=
operator|new
name|URL
argument_list|(
name|url
argument_list|)
decl_stmt|;
return|return
operator|new
name|FileSource
argument_list|(
name|url_
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Malformed URL: '"
operator|+
name|url
operator|+
literal|"'"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Looks for a suffix on a path and returns    * either the path with the suffix removed    * or null. */
specifier|private
specifier|static
name|String
name|trimOrNull
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|suffix
parameter_list|)
block|{
return|return
name|s
operator|.
name|endsWith
argument_list|(
name|suffix
argument_list|)
condition|?
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
operator|-
name|suffix
operator|.
name|length
argument_list|()
argument_list|)
else|:
literal|null
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isFile
parameter_list|(
name|Source
name|source
parameter_list|)
block|{
return|return
name|source
operator|.
name|protocol
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
return|;
block|}
comment|/** Implementation of {@link Source}. */
specifier|private
specifier|static
class|class
name|FileSource
implements|implements
name|Source
block|{
specifier|private
specifier|final
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|URL
name|url
decl_stmt|;
specifier|private
name|FileSource
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|url
operator|.
name|getProtocol
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|this
operator|.
name|file
operator|=
operator|new
name|File
argument_list|(
name|url
operator|.
name|getFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|file
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|private
name|FileSource
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|this
operator|.
name|url
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
operator|(
name|url
operator|!=
literal|null
condition|?
name|url
else|:
name|file
operator|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|URL
name|url
parameter_list|()
block|{
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
return|return
name|url
return|;
block|}
specifier|public
name|File
name|file
parameter_list|()
block|{
if|if
condition|(
name|file
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
return|return
name|file
return|;
block|}
specifier|public
name|String
name|protocol
parameter_list|()
block|{
return|return
name|file
operator|!=
literal|null
condition|?
literal|"file"
else|:
name|url
operator|.
name|getProtocol
argument_list|()
return|;
block|}
specifier|public
name|String
name|path
parameter_list|()
block|{
return|return
name|file
operator|!=
literal|null
condition|?
name|file
operator|.
name|getPath
argument_list|()
else|:
name|url
operator|.
name|toExternalForm
argument_list|()
return|;
block|}
specifier|public
name|Reader
name|reader
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|InputStream
name|is
decl_stmt|;
if|if
condition|(
name|path
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".gz"
argument_list|)
condition|)
block|{
specifier|final
name|InputStream
name|fis
init|=
name|openStream
argument_list|()
decl_stmt|;
name|is
operator|=
operator|new
name|GZIPInputStream
argument_list|(
name|fis
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|is
operator|=
name|openStream
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
specifier|public
name|InputStream
name|openStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|url
operator|.
name|openStream
argument_list|()
return|;
block|}
block|}
specifier|public
name|Source
name|trim
parameter_list|(
name|String
name|suffix
parameter_list|)
block|{
name|Source
name|x
init|=
name|trimOrNull
argument_list|(
name|suffix
argument_list|)
decl_stmt|;
return|return
name|x
operator|==
literal|null
condition|?
name|this
else|:
name|x
return|;
block|}
specifier|public
name|Source
name|trimOrNull
parameter_list|(
name|String
name|suffix
parameter_list|)
block|{
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|s
init|=
name|Sources
operator|.
name|trimOrNull
argument_list|(
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|,
name|suffix
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
literal|null
else|:
name|Sources
operator|.
name|url
argument_list|(
name|s
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|String
name|s
init|=
name|Sources
operator|.
name|trimOrNull
argument_list|(
name|file
operator|.
name|getPath
argument_list|()
argument_list|,
name|suffix
argument_list|)
decl_stmt|;
return|return
name|s
operator|==
literal|null
condition|?
literal|null
else|:
name|of
argument_list|(
operator|new
name|File
argument_list|(
name|s
argument_list|)
argument_list|)
return|;
block|}
block|}
specifier|public
name|Source
name|append
parameter_list|(
name|Source
name|child
parameter_list|)
block|{
name|String
name|path
decl_stmt|;
if|if
condition|(
name|isFile
argument_list|(
name|child
argument_list|)
condition|)
block|{
name|path
operator|=
name|child
operator|.
name|file
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
if|if
condition|(
name|child
operator|.
name|file
argument_list|()
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
return|return
name|child
return|;
block|}
block|}
else|else
block|{
name|path
operator|=
name|child
operator|.
name|url
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
name|child
return|;
block|}
block|}
if|if
condition|(
name|url
operator|!=
literal|null
condition|)
block|{
return|return
name|Sources
operator|.
name|url
argument_list|(
name|url
operator|+
literal|"/"
operator|+
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Sources
operator|.
name|file
argument_list|(
name|file
argument_list|,
name|path
argument_list|)
return|;
block|}
block|}
specifier|public
name|Source
name|relative
parameter_list|(
name|Source
name|parent
parameter_list|)
block|{
if|if
condition|(
name|isFile
argument_list|(
name|parent
argument_list|)
condition|)
block|{
if|if
condition|(
name|isFile
argument_list|(
name|this
argument_list|)
operator|&&
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|startsWith
argument_list|(
name|parent
operator|.
name|file
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|rest
init|=
name|file
operator|.
name|getPath
argument_list|()
operator|.
name|substring
argument_list|(
name|parent
operator|.
name|file
argument_list|()
operator|.
name|getPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rest
operator|.
name|startsWith
argument_list|(
name|File
operator|.
name|separator
argument_list|)
condition|)
block|{
return|return
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|rest
operator|.
name|substring
argument_list|(
name|File
operator|.
name|separator
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|this
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|isFile
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|String
name|rest
init|=
name|Sources
operator|.
name|trimOrNull
argument_list|(
name|url
operator|.
name|toExternalForm
argument_list|()
argument_list|,
name|parent
operator|.
name|url
argument_list|()
operator|.
name|toExternalForm
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rest
operator|!=
literal|null
operator|&&
name|rest
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
return|return
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|rest
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
block|}
return|return
name|this
return|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End Sources.java
end_comment

end_unit

