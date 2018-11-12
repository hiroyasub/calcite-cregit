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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Directed graph where edges have attributes and allows multiple edges between  * any two vertices provided that their attributes are different.  *  * @param<V> Vertex type  * @param<E> Edge type  */
end_comment

begin_class
specifier|public
class|class
name|AttributedDirectedGraph
parameter_list|<
name|V
parameter_list|,
name|E
extends|extends
name|DefaultEdge
parameter_list|>
extends|extends
name|DefaultDirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
block|{
comment|/** Creates an attributed graph. */
specifier|public
name|AttributedDirectedGraph
parameter_list|(
name|AttributedEdgeFactory
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|edgeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|edgeFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|,
name|E
extends|extends
name|DefaultEdge
parameter_list|>
name|AttributedDirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|create
parameter_list|(
name|AttributedEdgeFactory
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|edgeFactory
parameter_list|)
block|{
return|return
operator|new
name|AttributedDirectedGraph
argument_list|<>
argument_list|(
name|edgeFactory
argument_list|)
return|;
block|}
comment|/** Returns the first edge between one vertex to another. */
annotation|@
name|Override
specifier|public
name|E
name|getEdge
parameter_list|(
name|V
name|source
parameter_list|,
name|V
name|target
parameter_list|)
block|{
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|info
init|=
name|vertexMap
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
for|for
control|(
name|E
name|outEdge
range|:
name|info
operator|.
name|outEdges
control|)
block|{
if|if
condition|(
name|outEdge
operator|.
name|target
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
return|return
name|outEdge
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** @deprecated Use {@link #addEdge(Object, Object, Object...)}. */
annotation|@
name|Deprecated
specifier|public
name|E
name|addEdge
parameter_list|(
name|V
name|vertex
parameter_list|,
name|V
name|targetVertex
parameter_list|)
block|{
return|return
name|super
operator|.
name|addEdge
argument_list|(
name|vertex
argument_list|,
name|targetVertex
argument_list|)
return|;
block|}
specifier|public
name|E
name|addEdge
parameter_list|(
name|V
name|vertex
parameter_list|,
name|V
name|targetVertex
parameter_list|,
name|Object
modifier|...
name|attributes
parameter_list|)
block|{
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|info
init|=
name|vertexMap
operator|.
name|get
argument_list|(
name|vertex
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no vertex "
operator|+
name|vertex
argument_list|)
throw|;
block|}
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|info2
init|=
name|vertexMap
operator|.
name|get
argument_list|(
name|targetVertex
argument_list|)
decl_stmt|;
if|if
condition|(
name|info2
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no vertex "
operator|+
name|targetVertex
argument_list|)
throw|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|AttributedEdgeFactory
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|f
init|=
operator|(
name|AttributedEdgeFactory
operator|)
name|this
operator|.
name|edgeFactory
decl_stmt|;
specifier|final
name|E
name|edge
init|=
name|f
operator|.
name|createEdge
argument_list|(
name|vertex
argument_list|,
name|targetVertex
argument_list|,
name|attributes
argument_list|)
decl_stmt|;
if|if
condition|(
name|edges
operator|.
name|add
argument_list|(
name|edge
argument_list|)
condition|)
block|{
name|info
operator|.
name|outEdges
operator|.
name|add
argument_list|(
name|edge
argument_list|)
expr_stmt|;
return|return
name|edge
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Returns all edges between one vertex to another. */
specifier|public
name|Iterable
argument_list|<
name|E
argument_list|>
name|getEdges
parameter_list|(
name|V
name|source
parameter_list|,
specifier|final
name|V
name|target
parameter_list|)
block|{
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|info
init|=
name|vertexMap
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
return|return
name|Util
operator|.
name|filter
argument_list|(
name|info
operator|.
name|outEdges
argument_list|,
name|outEdge
lambda|->
name|outEdge
operator|.
name|target
operator|.
name|equals
argument_list|(
name|target
argument_list|)
argument_list|)
return|;
block|}
comment|/** Removes all edges from a given vertex to another.    * Returns whether any were removed. */
specifier|public
name|boolean
name|removeEdge
parameter_list|(
name|V
name|source
parameter_list|,
name|V
name|target
parameter_list|)
block|{
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|info
init|=
name|vertexMap
operator|.
name|get
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|E
argument_list|>
name|outEdges
init|=
name|info
operator|.
name|outEdges
decl_stmt|;
name|int
name|removeCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|size
init|=
name|outEdges
operator|.
name|size
argument_list|()
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|E
name|edge
init|=
name|outEdges
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|edge
operator|.
name|target
operator|.
name|equals
argument_list|(
name|target
argument_list|)
condition|)
block|{
name|outEdges
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|edges
operator|.
name|remove
argument_list|(
name|edge
argument_list|)
expr_stmt|;
operator|++
name|removeCount
expr_stmt|;
block|}
block|}
return|return
name|removeCount
operator|>
literal|0
return|;
block|}
comment|/** Factory for edges that have attributes.    *    * @param<V> Vertex type    * @param<E> Edge type    */
specifier|public
interface|interface
name|AttributedEdgeFactory
parameter_list|<
name|V
parameter_list|,
name|E
parameter_list|>
extends|extends
name|EdgeFactory
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
block|{
name|E
name|createEdge
parameter_list|(
name|V
name|v0
parameter_list|,
name|V
name|v1
parameter_list|,
name|Object
modifier|...
name|attributes
parameter_list|)
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End AttributedDirectedGraph.java
end_comment

end_unit
