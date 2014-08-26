begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
import|;
end_import

begin_comment
comment|/**  * Default implementation of {@link DirectedGraph}.  *  * @param<V> Vertex type  * @param<E> Edge type  */
end_comment

begin_class
specifier|public
class|class
name|DefaultDirectedGraph
parameter_list|<
name|V
parameter_list|,
name|E
extends|extends
name|DefaultEdge
parameter_list|>
implements|implements
name|DirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
block|{
specifier|final
name|Set
argument_list|<
name|E
argument_list|>
name|edges
init|=
operator|new
name|LinkedHashSet
argument_list|<
name|E
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|V
argument_list|,
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
argument_list|>
name|vertexMap
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|V
argument_list|,
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|EdgeFactory
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|edgeFactory
decl_stmt|;
comment|/** Creates a graph. */
specifier|public
name|DefaultDirectedGraph
parameter_list|(
name|EdgeFactory
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|edgeFactory
parameter_list|)
block|{
name|this
operator|.
name|edgeFactory
operator|=
name|edgeFactory
expr_stmt|;
block|}
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|DefaultDirectedGraph
argument_list|<
name|V
argument_list|,
name|DefaultEdge
argument_list|>
name|create
parameter_list|()
block|{
return|return
operator|new
name|DefaultDirectedGraph
argument_list|<
name|V
argument_list|,
name|DefaultEdge
argument_list|>
argument_list|(
name|DefaultEdge
operator|.
expr|<
name|V
operator|>
name|factory
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"graph("
argument_list|)
operator|.
name|append
argument_list|(
literal|"vertices: "
argument_list|)
operator|.
name|append
argument_list|(
name|vertexMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|", edges: "
argument_list|)
operator|.
name|append
argument_list|(
name|edges
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|addVertex
parameter_list|(
name|V
name|vertex
parameter_list|)
block|{
if|if
condition|(
name|vertexMap
operator|.
name|containsKey
argument_list|(
name|vertex
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|vertexMap
operator|.
name|put
argument_list|(
name|vertex
argument_list|,
operator|new
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
specifier|public
name|Set
argument_list|<
name|E
argument_list|>
name|edgeSet
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|edges
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
specifier|final
name|E
name|edge
init|=
name|edgeFactory
operator|.
name|createEdge
argument_list|(
name|vertex
argument_list|,
name|targetVertex
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
comment|// REVIEW: could instead use edges.get(new DefaultEdge(source, target))
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
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|Set
argument_list|<
name|V
argument_list|>
name|vertexSet
parameter_list|()
block|{
return|return
name|vertexMap
operator|.
name|keySet
argument_list|()
return|;
block|}
specifier|public
name|void
name|removeAllVertices
parameter_list|(
name|Collection
argument_list|<
name|V
argument_list|>
name|collection
parameter_list|)
block|{
name|vertexMap
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|collection
argument_list|)
expr_stmt|;
for|for
control|(
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|info
range|:
name|vertexMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
init|=
name|info
operator|.
name|outEdges
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|E
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//noinspection SuspiciousMethodCalls
if|if
condition|(
name|collection
operator|.
name|contains
argument_list|(
name|next
operator|.
name|target
argument_list|)
condition|)
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|List
argument_list|<
name|E
argument_list|>
name|getOutwardEdges
parameter_list|(
name|V
name|source
parameter_list|)
block|{
return|return
name|vertexMap
operator|.
name|get
argument_list|(
name|source
argument_list|)
operator|.
name|outEdges
return|;
block|}
specifier|public
name|List
argument_list|<
name|E
argument_list|>
name|getInwardEdges
parameter_list|(
name|V
name|target
parameter_list|)
block|{
specifier|final
name|ArrayList
argument_list|<
name|E
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|E
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|info
range|:
name|vertexMap
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|E
name|edge
range|:
name|info
operator|.
name|outEdges
control|)
block|{
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
name|list
operator|.
name|add
argument_list|(
name|edge
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|list
return|;
block|}
specifier|final
name|V
name|source
parameter_list|(
name|E
name|edge
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|V
operator|)
name|edge
operator|.
name|source
return|;
block|}
specifier|final
name|V
name|target
parameter_list|(
name|E
name|edge
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|V
operator|)
name|edge
operator|.
name|target
return|;
block|}
comment|/**    * Information about an edge.    *    * @param<V> Vertex type    * @param<E> Edge type    */
specifier|static
class|class
name|VertexInfo
parameter_list|<
name|V
parameter_list|,
name|E
parameter_list|>
block|{
specifier|public
name|List
argument_list|<
name|E
argument_list|>
name|outEdges
init|=
operator|new
name|ArrayList
argument_list|<
name|E
argument_list|>
argument_list|()
decl_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DefaultDirectedGraph.java
end_comment

end_unit

