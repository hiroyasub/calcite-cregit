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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Ordering
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apiguardian
operator|.
name|api
operator|.
name|API
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|initialization
operator|.
name|qual
operator|.
name|NotOnlyInitialized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|initialization
operator|.
name|qual
operator|.
name|UnknownInitialization
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Nullness
operator|.
name|castNonNull
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
argument_list|<>
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
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
annotation|@
name|NotOnlyInitialized
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
annotation|@
name|UnknownInitialization
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
name|create
argument_list|(
name|DefaultEdge
operator|.
name|factory
argument_list|()
argument_list|)
return|;
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
name|DefaultDirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|create
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
return|return
operator|new
name|DefaultDirectedGraph
argument_list|<>
argument_list|(
name|edgeFactory
argument_list|)
return|;
block|}
specifier|public
name|String
name|toStringUnordered
parameter_list|()
block|{
return|return
literal|"graph("
operator|+
literal|"vertices: "
operator|+
name|vertexMap
operator|.
name|keySet
argument_list|()
operator|+
literal|", edges: "
operator|+
name|edges
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Ordering
argument_list|<
name|V
argument_list|>
name|vertexOrdering
init|=
operator|(
name|Ordering
operator|)
name|Ordering
operator|.
name|usingToString
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Ordering
argument_list|<
name|E
argument_list|>
name|edgeOrdering
init|=
operator|(
name|Ordering
operator|)
name|Ordering
operator|.
name|usingToString
argument_list|()
decl_stmt|;
return|return
name|toString
argument_list|(
name|vertexOrdering
argument_list|,
name|edgeOrdering
argument_list|)
return|;
block|}
comment|/** Returns the string representation of this graph, using the given    * orderings to ensure that the output order of vertices and edges is    * deterministic. */
specifier|private
name|String
name|toString
parameter_list|(
name|Ordering
argument_list|<
name|V
argument_list|>
name|vertexOrdering
parameter_list|,
name|Ordering
argument_list|<
name|E
argument_list|>
name|edgeOrdering
parameter_list|)
block|{
return|return
literal|"graph("
operator|+
literal|"vertices: "
operator|+
name|vertexOrdering
operator|.
name|sortedCopy
argument_list|(
operator|(
name|Set
argument_list|<
name|V
argument_list|>
operator|)
name|vertexMap
operator|.
name|keySet
argument_list|()
argument_list|)
operator|+
literal|", edges: "
operator|+
name|edgeOrdering
operator|.
name|sortedCopy
argument_list|(
name|edges
argument_list|)
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
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
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|API
argument_list|(
name|since
operator|=
literal|"1.26"
argument_list|,
name|status
operator|=
name|API
operator|.
name|Status
operator|.
name|EXPERIMENTAL
argument_list|)
specifier|protected
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|getVertex
parameter_list|(
name|V
name|vertex
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"argument.type.incompatible"
argument_list|)
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
return|return
name|info
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
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
name|getVertex
argument_list|(
name|vertex
argument_list|)
decl_stmt|;
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|targetInfo
init|=
name|getVertex
argument_list|(
name|targetVertex
argument_list|)
decl_stmt|;
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
name|targetInfo
operator|.
name|inEdges
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
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
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
name|getVertex
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
annotation|@
name|Override
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
comment|// remove out edges
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|outEdges
init|=
name|getVertex
argument_list|(
name|source
argument_list|)
operator|.
name|outEdges
decl_stmt|;
name|boolean
name|outRemoved
init|=
literal|false
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
name|outRemoved
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|// remove in edges
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|inEdges
init|=
name|getVertex
argument_list|(
name|target
argument_list|)
operator|.
name|inEdges
decl_stmt|;
name|boolean
name|inRemoved
init|=
literal|false
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
name|inEdges
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
name|inEdges
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
name|source
operator|.
name|equals
argument_list|(
name|source
argument_list|)
condition|)
block|{
name|inEdges
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|inRemoved
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
assert|assert
name|outRemoved
operator|==
name|inRemoved
assert|;
return|return
name|outRemoved
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"return.type.incompatible"
argument_list|)
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|V
argument_list|>
name|vertexSet
parameter_list|()
block|{
comment|// Set<V extends @KeyFor("this.vertexMap") Object> -> Set<V>
return|return
name|vertexMap
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
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
comment|// The point at which collection is large enough to make the 'majority'
comment|// algorithm more efficient.
specifier|final
name|float
name|threshold
init|=
literal|0.35f
decl_stmt|;
specifier|final
name|int
name|thresholdSize
init|=
operator|(
name|int
operator|)
operator|(
name|vertexMap
operator|.
name|size
argument_list|()
operator|*
name|threshold
operator|)
decl_stmt|;
if|if
condition|(
name|collection
operator|.
name|size
argument_list|()
operator|>
name|thresholdSize
operator|&&
operator|!
operator|(
name|collection
operator|instanceof
name|Set
operator|)
condition|)
block|{
comment|// Convert collection to a set, so that collection.contains() is
comment|// faster. If there are duplicates, collection.size() will get smaller.
name|collection
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|collection
operator|.
name|size
argument_list|()
operator|>
name|thresholdSize
condition|)
block|{
name|removeMajorityVertices
argument_list|(
operator|(
name|Set
argument_list|<
name|V
argument_list|>
operator|)
name|collection
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|removeMinorityVertices
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Implementation of {@link #removeAllVertices(Collection)} that is efficient    * if {@code collection} is a small fraction of the set of vertices. */
specifier|private
name|void
name|removeMinorityVertices
parameter_list|(
name|Collection
argument_list|<
name|V
argument_list|>
name|collection
parameter_list|)
block|{
for|for
control|(
name|V
name|v
range|:
name|collection
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"argument.type.incompatible"
argument_list|)
comment|// nullable keys are supported by .get
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
name|v
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
comment|// remove all edges pointing to v
for|for
control|(
name|E
name|edge
range|:
name|info
operator|.
name|inEdges
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|V
name|source
init|=
operator|(
name|V
operator|)
name|edge
operator|.
name|source
decl_stmt|;
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|sourceInfo
init|=
name|getVertex
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|sourceInfo
operator|.
name|outEdges
operator|.
name|removeIf
argument_list|(
name|e
lambda|->
name|e
operator|.
name|target
operator|.
name|equals
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// remove all edges starting from v
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|V
name|target
init|=
operator|(
name|V
operator|)
name|edge
operator|.
name|target
decl_stmt|;
specifier|final
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|targetInfo
init|=
name|getVertex
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|targetInfo
operator|.
name|inEdges
operator|.
name|removeIf
argument_list|(
name|e
lambda|->
name|e
operator|.
name|source
operator|.
name|equals
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
block|}
comment|/** Implementation of {@link #removeAllVertices(Collection)} that is efficient    * if {@code vertexSet} is a large fraction of the set of vertices in the    * graph. */
specifier|private
name|void
name|removeMajorityVertices
parameter_list|(
name|Set
argument_list|<
name|V
argument_list|>
name|vertexSet
parameter_list|)
block|{
name|vertexMap
operator|.
name|keySet
argument_list|()
operator|.
name|removeAll
argument_list|(
name|vertexSet
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
name|info
operator|.
name|outEdges
operator|.
name|removeIf
argument_list|(
name|e
lambda|->
name|vertexSet
operator|.
name|contains
argument_list|(
name|castNonNull
argument_list|(
operator|(
name|V
operator|)
name|e
operator|.
name|target
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|info
operator|.
name|inEdges
operator|.
name|removeIf
argument_list|(
name|e
lambda|->
name|vertexSet
operator|.
name|contains
argument_list|(
name|castNonNull
argument_list|(
operator|(
name|V
operator|)
name|e
operator|.
name|source
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|getVertex
argument_list|(
name|source
argument_list|)
operator|.
name|outEdges
return|;
block|}
annotation|@
name|Override
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
return|return
name|getVertex
argument_list|(
name|target
argument_list|)
operator|.
name|inEdges
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
comment|/**    * Information about a vertex.    *    * @param<V> Vertex type    * @param<E> Edge type    */
specifier|static
class|class
name|VertexInfo
parameter_list|<
name|V
parameter_list|,
name|E
parameter_list|>
block|{
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|outEdges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|inEdges
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
block|}
block|}
end_class

end_unit

