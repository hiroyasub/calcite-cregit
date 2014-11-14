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
comment|/**  * Detects cycles in directed graphs.  *  * @param<V> Vertex type  * @param<E> Edge type  */
end_comment

begin_class
specifier|public
class|class
name|CycleDetector
parameter_list|<
name|V
parameter_list|,
name|E
extends|extends
name|DefaultEdge
parameter_list|>
block|{
specifier|private
specifier|final
name|DirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|graph
decl_stmt|;
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
name|this
operator|.
name|graph
operator|=
name|graph
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|V
argument_list|>
name|findCycles
parameter_list|()
block|{
return|return
operator|new
name|TopologicalOrderIterator
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
argument_list|(
name|graph
argument_list|)
operator|.
name|findCycles
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End CycleDetector.java
end_comment

end_unit

