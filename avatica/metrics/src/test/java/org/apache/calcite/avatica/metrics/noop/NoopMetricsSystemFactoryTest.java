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
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Test class for {@link NoopMetricsSystemFactory}.  */
end_comment

begin_class
specifier|public
class|class
name|NoopMetricsSystemFactoryTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSingleton
parameter_list|()
block|{
name|NoopMetricsSystemFactory
name|factory
init|=
operator|new
name|NoopMetricsSystemFactory
argument_list|()
decl_stmt|;
name|NoopMetricsSystemConfiguration
name|config
init|=
name|NoopMetricsSystemConfiguration
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The factory should only return one NoopMetricsSystem instance"
argument_list|,
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|)
operator|==
name|factory
operator|.
name|create
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End NoopMetricsSystemFactoryTest.java
end_comment

end_unit

