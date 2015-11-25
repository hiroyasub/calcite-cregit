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
name|server
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
name|remote
operator|.
name|Driver
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
name|avatica
operator|.
name|remote
operator|.
name|Service
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|Handler
import|;
end_import

begin_comment
comment|/**  * Factory that instantiates the desired implementation, typically differing on the method  * used to serialize messages, for use in the Avatica server.  */
end_comment

begin_class
specifier|public
class|class
name|HandlerFactory
block|{
comment|/**    * The desired implementation for the given serialization method.    *    * @param serialization The desired message serialization    */
specifier|public
name|Handler
name|getHandler
parameter_list|(
name|Service
name|service
parameter_list|,
name|Driver
operator|.
name|Serialization
name|serialization
parameter_list|)
block|{
switch|switch
condition|(
name|serialization
condition|)
block|{
case|case
name|JSON
case|:
return|return
operator|new
name|AvaticaJsonHandler
argument_list|(
name|service
argument_list|)
return|;
case|case
name|PROTOBUF
case|:
return|return
operator|new
name|AvaticaProtobufHandler
argument_list|(
name|service
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown Avatica handler for "
operator|+
name|serialization
operator|.
name|name
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End HandlerFactory.java
end_comment

end_unit

