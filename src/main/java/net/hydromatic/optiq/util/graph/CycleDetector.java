begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|util
operator|.
name|graph
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Detects cycles in directed graphs.  */
end_comment

begin_class
specifier|public
class|class
name|CycleDetector
parameter_list|<
name|V
parameter_list|,
name|E
parameter_list|>
block|{
specifier|public
name|CycleDetector
parameter_list|(
name|DirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|graph
parameter_list|)
block|{
block|}
specifier|public
name|Set
argument_list|<
name|V
argument_list|>
name|findCycles
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

begin_comment
comment|// End CycleDetector.java
end_comment

end_unit

