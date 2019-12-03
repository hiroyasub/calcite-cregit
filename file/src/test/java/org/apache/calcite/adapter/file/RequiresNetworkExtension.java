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
name|file
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
name|ConditionEvaluationResult
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
name|extension
operator|.
name|ExecutionCondition
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
name|extension
operator|.
name|ExtensionContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|platform
operator|.
name|commons
operator|.
name|support
operator|.
name|AnnotationSupport
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|Socket
import|;
end_import

begin_import
import|import static
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
name|ConditionEvaluationResult
operator|.
name|disabled
import|;
end_import

begin_import
import|import static
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
name|ConditionEvaluationResult
operator|.
name|enabled
import|;
end_import

begin_comment
comment|/**  * Enables to activate test conditionally if the specified host is reachable.  * Note: it is recommended to avoid creating tests that depend on external servers.  */
end_comment

begin_class
specifier|public
class|class
name|RequiresNetworkExtension
implements|implements
name|ExecutionCondition
block|{
annotation|@
name|Override
specifier|public
name|ConditionEvaluationResult
name|evaluateExecutionCondition
parameter_list|(
name|ExtensionContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|getElement
argument_list|()
operator|.
name|flatMap
argument_list|(
name|element
lambda|->
name|AnnotationSupport
operator|.
name|findAnnotation
argument_list|(
name|element
argument_list|,
name|RequiresNetwork
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|net
lambda|->
block|{
lambda|try (Socket ignored
init|=
operator|new
name|Socket
argument_list|(
name|net
operator|.
name|host
argument_list|()
argument_list|,
name|net
operator|.
name|port
argument_list|()
argument_list|)
argument_list|)
block|{
return|return
name|enabled
argument_list|(
name|net
operator|.
name|host
argument_list|()
operator|+
literal|":"
operator|+
name|net
operator|.
name|port
argument_list|()
operator|+
literal|" is reachable"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
name|disabled
argument_list|(
name|net
operator|.
name|host
argument_list|()
operator|+
literal|":"
operator|+
name|net
operator|.
name|port
argument_list|()
operator|+
literal|" is unreachable: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
block|}
block|)
operator|.
name|orElseGet
argument_list|(
parameter_list|()
lambda|->
name|enabled
argument_list|(
literal|"@RequiresNetwork is not found"
argument_list|)
argument_list|)
expr_stmt|;
end_class

begin_comment
unit|} }
comment|// End RequiresNetworkExtension.java
end_comment

end_unit

