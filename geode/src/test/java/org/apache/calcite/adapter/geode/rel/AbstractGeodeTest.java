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
name|adapter
operator|.
name|geode
operator|.
name|rel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|extension
operator|.
name|RegisterExtension
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|parallel
operator|.
name|Execution
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|parallel
operator|.
name|ExecutionMode
import|;
end_import

begin_comment
comment|/**  * Base class that allows sharing same geode instance across all tests.  *  *<p>Also, due to legacy reasons, there can't be more than one Geode  * instance (running in parallel) for a single JVM.  */
end_comment

begin_class
annotation|@
name|Execution
argument_list|(
name|ExecutionMode
operator|.
name|CONCURRENT
argument_list|)
specifier|public
specifier|abstract
class|class
name|AbstractGeodeTest
block|{
annotation|@
name|RegisterExtension
specifier|public
specifier|static
specifier|final
name|GeodeEmbeddedPolicy
name|POLICY
init|=
name|GeodeEmbeddedPolicy
operator|.
name|create
argument_list|()
operator|.
name|share
argument_list|()
decl_stmt|;
block|}
end_class

end_unit

