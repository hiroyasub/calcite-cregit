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

begin_comment
comment|/**  * An empty configuration for the {@link NoopMetricsSystem}.  */
end_comment

begin_class
specifier|public
class|class
name|NoopMetricsSystemConfiguration
implements|implements
name|MetricsSystemConfiguration
argument_list|<
name|Void
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|NoopMetricsSystemConfiguration
name|INSTANCE
init|=
operator|new
name|NoopMetricsSystemConfiguration
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|NoopMetricsSystemConfiguration
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
specifier|private
name|NoopMetricsSystemConfiguration
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|Void
name|get
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End NoopMetricsSystemConfiguration.java
end_comment

end_unit

