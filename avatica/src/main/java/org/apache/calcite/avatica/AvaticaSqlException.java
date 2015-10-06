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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * A client-facing {@link SQLException} which encapsulates errors from the remote Avatica server.  */
end_comment

begin_class
specifier|public
class|class
name|AvaticaSqlException
extends|extends
name|SQLException
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
specifier|private
specifier|final
name|String
name|errorMessage
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|stackTraces
decl_stmt|;
comment|/**    * Construct the Exception with information from the server.    *    * @param errorMessage A human-readable error message.    * @param errorCode An integer corresponding to a known error.    * @param stackTraces Server-side stacktrace.    */
specifier|public
name|AvaticaSqlException
parameter_list|(
name|String
name|errorMessage
parameter_list|,
name|String
name|sqlState
parameter_list|,
name|int
name|errorCode
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|stackTraces
parameter_list|)
block|{
name|super
argument_list|(
literal|"Error "
operator|+
name|errorCode
operator|+
literal|" ("
operator|+
name|sqlState
operator|+
literal|") : "
operator|+
name|errorMessage
argument_list|,
name|sqlState
argument_list|,
name|errorCode
argument_list|)
expr_stmt|;
name|this
operator|.
name|errorMessage
operator|=
name|errorMessage
expr_stmt|;
name|this
operator|.
name|stackTraces
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|stackTraces
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getErrorMessage
parameter_list|()
block|{
return|return
name|errorMessage
return|;
block|}
comment|/**    * @return The stacktraces for exceptions thrown on the Avatica server.    */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getStackTraces
parameter_list|()
block|{
return|return
name|stackTraces
return|;
block|}
block|}
end_class

begin_comment
comment|// End AvaticaSqlException.java
end_comment

end_unit

