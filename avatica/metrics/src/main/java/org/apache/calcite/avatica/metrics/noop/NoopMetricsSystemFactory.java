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
operator|.
name|noop
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
name|MetricsSystemConfiguration
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
name|metrics
operator|.
name|MetricsSystemFactory
import|;
end_import

begin_comment
comment|/**  * A {@link MetricsSystemFactory} for the {@link NoopMetricsSystem}.  *  * No service file is provided for this implementation. It is the fallback implementation if  * no implementation or more than one implementation is found on the classpath.  */
end_comment

begin_class
specifier|public
class|class
name|NoopMetricsSystemFactory
implements|implements
name|MetricsSystemFactory
block|{
annotation|@
name|Override
specifier|public
name|NoopMetricsSystem
name|create
parameter_list|(
name|MetricsSystemConfiguration
argument_list|<
name|?
argument_list|>
name|config
parameter_list|)
block|{
return|return
name|NoopMetricsSystem
operator|.
name|getInstance
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End NoopMetricsSystemFactory.java
end_comment

end_unit

