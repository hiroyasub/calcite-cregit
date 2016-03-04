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
name|dropwizard3
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
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|MetricRegistry
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
comment|/**  * A container which provides a {@link MetricRegistry} to a {@link DropwizardMetricsSystem}.  */
end_comment

begin_class
specifier|public
class|class
name|DropwizardMetricsSystemConfiguration
implements|implements
name|MetricsSystemConfiguration
argument_list|<
name|MetricRegistry
argument_list|>
block|{
specifier|private
specifier|final
name|MetricRegistry
name|registry
decl_stmt|;
specifier|public
name|DropwizardMetricsSystemConfiguration
parameter_list|(
name|MetricRegistry
name|registry
parameter_list|)
block|{
name|this
operator|.
name|registry
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|registry
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|MetricRegistry
name|get
parameter_list|()
block|{
return|return
name|registry
return|;
block|}
block|}
end_class

begin_comment
comment|// End DropwizardMetricsSystemConfiguration.java
end_comment

end_unit

