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
name|metrics
package|;
end_package

begin_comment
comment|/**  * A container used by a {@link MetricsSystemFactory} to create a {@link MetricsSystem}.  *  * @param<T> Configuration/State for the {@link MetricsSystem}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MetricsSystemConfiguration
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * @return Some state or configuration to create a {@link MetricsSystem}.    */
name|T
name|get
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End MetricsSystemConfiguration.java
end_comment

end_unit

