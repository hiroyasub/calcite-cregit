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
name|remote
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
name|AvaticaUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|net
operator|.
name|HttpURLConnection
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

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.avatica.remote.Service}  * that translates requests into JSON and sends them to a remote server,  * usually an HTTP server.  */
end_comment

begin_class
specifier|public
class|class
name|RemoteService
extends|extends
name|JsonService
block|{
specifier|private
specifier|final
name|URL
name|url
decl_stmt|;
specifier|public
name|RemoteService
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|request
parameter_list|)
block|{
try|try
block|{
specifier|final
name|HttpURLConnection
name|connection
init|=
operator|(
name|HttpURLConnection
operator|)
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|connection
operator|.
name|setRequestMethod
argument_list|(
literal|"POST"
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setDoInput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|connection
operator|.
name|setDoOutput
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|request
operator|.
name|length
argument_list|()
operator|<
literal|256
condition|)
block|{
name|connection
operator|.
name|setRequestProperty
argument_list|(
literal|"request"
argument_list|,
name|request
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
init|(
name|DataOutputStream
name|wr
init|=
operator|new
name|DataOutputStream
argument_list|(
name|connection
operator|.
name|getOutputStream
argument_list|()
argument_list|)
init|)
block|{
name|wr
operator|.
name|writeBytes
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|wr
operator|.
name|flush
argument_list|()
expr_stmt|;
name|wr
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|responseCode
init|=
name|connection
operator|.
name|getResponseCode
argument_list|()
decl_stmt|;
if|if
condition|(
name|responseCode
operator|!=
name|HttpURLConnection
operator|.
name|HTTP_OK
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"response code "
operator|+
name|responseCode
argument_list|)
throw|;
block|}
specifier|final
name|InputStream
name|inputStream
init|=
name|connection
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
return|return
name|AvaticaUtils
operator|.
name|readFully
argument_list|(
name|inputStream
argument_list|)
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
block|}
end_class

begin_comment
comment|// End RemoteService.java
end_comment

end_unit

