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
name|metrics
operator|.
name|MetricsSystem
import|;
end_import

begin_comment
comment|/**  * An {@link AvaticaHandler} that is capable of collecting metrics.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MetricsAwareAvaticaHandler
extends|extends
name|AvaticaHandler
block|{
comment|/**    * General prefix for all metrics in a handler.    */
name|String
name|HANDLER_PREFIX
init|=
literal|"Handler."
decl_stmt|;
comment|/**    * Name for timing requests from users    */
name|String
name|REQUEST_TIMER_NAME
init|=
name|HANDLER_PREFIX
operator|+
literal|"RequestTimings"
decl_stmt|;
comment|/**    * @return An instance of the {@link MetricsSystem} for this AvaticaHandler.    */
name|MetricsSystem
name|getMetrics
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End MetricsAwareAvaticaHandler.java
end_comment

end_unit

