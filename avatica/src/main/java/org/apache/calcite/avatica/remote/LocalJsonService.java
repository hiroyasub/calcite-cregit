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
name|StringWriter
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.avatica.remote.Service}  * that goes to an in-process instance of {@code Service}.  */
end_comment

begin_class
specifier|public
class|class
name|LocalJsonService
extends|extends
name|JsonService
block|{
specifier|private
specifier|final
name|Service
name|service
decl_stmt|;
specifier|public
name|LocalJsonService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|this
operator|.
name|service
operator|=
name|service
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
name|Request
name|request2
init|=
name|MAPPER
operator|.
name|readValue
argument_list|(
name|request
argument_list|,
name|Request
operator|.
name|class
argument_list|)
decl_stmt|;
name|Response
name|response2
init|=
name|request2
operator|.
name|accept
argument_list|(
name|service
argument_list|)
decl_stmt|;
specifier|final
name|StringWriter
name|w
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|MAPPER
operator|.
name|writeValue
argument_list|(
name|w
argument_list|,
name|response2
argument_list|)
expr_stmt|;
return|return
name|w
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
name|handle
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End LocalJsonService.java
end_comment

end_unit

