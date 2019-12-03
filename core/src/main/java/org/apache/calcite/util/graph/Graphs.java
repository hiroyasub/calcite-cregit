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
name|Pair
import|;
end_import

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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
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
name|HashMap
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
name|util
operator|.
name|Static
operator|.
name|cons
import|;
end_import

begin_comment
comment|/**  * Miscellaneous graph utilities.  */
end_comment

begin_class
specifier|public
class|class
name|Graphs
block|{
specifier|private
name|Graphs
parameter_list|()
block|{
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
name|List
argument_list|<
name|V
argument_list|>
name|predecessorListOf
parameter_list|(
name|DirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|graph
parameter_list|,
name|V
name|vertex
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|edges
init|=
name|graph
operator|.
name|getInwardEdges
argument_list|(
name|vertex
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractList
argument_list|<
name|V
argument_list|>
argument_list|()
block|{
specifier|public
name|V
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|V
operator|)
name|edges
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|source
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|edges
operator|.
name|size
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/** Returns a map of the shortest paths between any pair of nodes. */
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|,
name|E
extends|extends
name|DefaultEdge
parameter_list|>
name|FrozenGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|makeImmutable
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
name|DefaultDirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|graph1
init|=
operator|(
name|DefaultDirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
operator|)
name|graph
decl_stmt|;
name|Map
argument_list|<
name|Pair
argument_list|<
name|V
argument_list|,
name|V
argument_list|>
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|shortestPaths
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DefaultDirectedGraph
operator|.
name|VertexInfo
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|arc
range|:
name|graph1
operator|.
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
name|arc
operator|.
name|outEdges
control|)
block|{
specifier|final
name|V
name|source
init|=
name|graph1
operator|.
name|source
argument_list|(
name|edge
argument_list|)
decl_stmt|;
specifier|final
name|V
name|target
init|=
name|graph1
operator|.
name|target
argument_list|(
name|edge
argument_list|)
decl_stmt|;
name|shortestPaths
operator|.
name|put
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|source
argument_list|,
name|target
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
while|while
condition|(
literal|true
condition|)
block|{
comment|// Take a copy of the map's keys to avoid
comment|// ConcurrentModificationExceptions.
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|V
argument_list|,
name|V
argument_list|>
argument_list|>
name|previous
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|shortestPaths
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|changeCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|E
name|edge
range|:
name|graph
operator|.
name|edgeSet
argument_list|()
control|)
block|{
for|for
control|(
name|Pair
argument_list|<
name|V
argument_list|,
name|V
argument_list|>
name|edge2
range|:
name|previous
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
name|edge2
operator|.
name|left
argument_list|)
condition|)
block|{
specifier|final
name|Pair
argument_list|<
name|V
argument_list|,
name|V
argument_list|>
name|key
init|=
name|Pair
operator|.
name|of
argument_list|(
name|graph1
operator|.
name|source
argument_list|(
name|edge
argument_list|)
argument_list|,
name|edge2
operator|.
name|right
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|V
argument_list|>
name|bestPath
init|=
name|shortestPaths
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|V
argument_list|>
name|arc2Path
init|=
name|shortestPaths
operator|.
name|get
argument_list|(
name|edge2
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|bestPath
operator|==
literal|null
operator|)
operator|||
operator|(
name|bestPath
operator|.
name|size
argument_list|()
operator|>
operator|(
name|arc2Path
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|)
operator|)
condition|)
block|{
name|shortestPaths
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|cons
argument_list|(
name|graph1
operator|.
name|source
argument_list|(
name|edge
argument_list|)
argument_list|,
name|arc2Path
argument_list|)
argument_list|)
expr_stmt|;
name|changeCount
operator|++
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|changeCount
operator|==
literal|0
condition|)
block|{
break|break;
block|}
block|}
return|return
operator|new
name|FrozenGraph
argument_list|<>
argument_list|(
name|graph1
argument_list|,
name|shortestPaths
argument_list|)
return|;
block|}
comment|/**    * Immutable grap.    *    * @param<V> Vertex type    * @param<E> Edge type    */
specifier|public
specifier|static
class|class
name|FrozenGraph
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
name|DefaultDirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|graph
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Pair
argument_list|<
name|V
argument_list|,
name|V
argument_list|>
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|shortestPaths
decl_stmt|;
comment|/** Creates a frozen graph as a copy of another graph. */
name|FrozenGraph
parameter_list|(
name|DefaultDirectedGraph
argument_list|<
name|V
argument_list|,
name|E
argument_list|>
name|graph
parameter_list|,
name|Map
argument_list|<
name|Pair
argument_list|<
name|V
argument_list|,
name|V
argument_list|>
argument_list|,
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|shortestPaths
parameter_list|)
block|{
name|this
operator|.
name|graph
operator|=
name|graph
expr_stmt|;
name|this
operator|.
name|shortestPaths
operator|=
name|shortestPaths
expr_stmt|;
block|}
comment|/**      * Returns an iterator of all paths between two nodes, shortest first.      *      *<p>The current implementation is not optimal.</p>      */
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|getPaths
parameter_list|(
name|V
name|from
parameter_list|,
name|V
name|to
parameter_list|)
block|{
name|List
argument_list|<
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|findPaths
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|list
argument_list|)
expr_stmt|;
return|return
name|list
return|;
block|}
comment|/**      * Returns the shortest path between two points, null if there is no path.      *      * @param from From      * @param to To      *      * @return A list of arcs, null if there is no path.      */
specifier|public
name|List
argument_list|<
name|V
argument_list|>
name|getShortestPath
parameter_list|(
name|V
name|from
parameter_list|,
name|V
name|to
parameter_list|)
block|{
if|if
condition|(
name|from
operator|.
name|equals
argument_list|(
name|to
argument_list|)
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
return|return
name|shortestPaths
operator|.
name|get
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|findPaths
parameter_list|(
name|V
name|from
parameter_list|,
name|V
name|to
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|list
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|shortestPath
init|=
name|shortestPaths
operator|.
name|get
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|shortestPath
operator|==
literal|null
condition|)
block|{
return|return;
block|}
comment|//      final E edge = graph.getEdge(from, to);
comment|//      if (edge != null) {
comment|//        list.add(ImmutableList.of(from, to));
comment|//      }
specifier|final
name|List
argument_list|<
name|V
argument_list|>
name|prefix
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|prefix
operator|.
name|add
argument_list|(
name|from
argument_list|)
expr_stmt|;
name|findPathsExcluding
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
name|list
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|()
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
comment|/**      * Finds all paths from "from" to "to" of length 2 or greater, such that the      * intermediate nodes are not contained in "excludedNodes".      */
specifier|private
name|void
name|findPathsExcluding
parameter_list|(
name|V
name|from
parameter_list|,
name|V
name|to
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|V
argument_list|>
argument_list|>
name|list
parameter_list|,
name|Set
argument_list|<
name|V
argument_list|>
name|excludedNodes
parameter_list|,
name|List
argument_list|<
name|V
argument_list|>
name|prefix
parameter_list|)
block|{
name|excludedNodes
operator|.
name|add
argument_list|(
name|from
argument_list|)
expr_stmt|;
for|for
control|(
name|E
name|edge
range|:
name|graph
operator|.
name|edges
control|)
block|{
if|if
condition|(
name|edge
operator|.
name|source
operator|.
name|equals
argument_list|(
name|from
argument_list|)
condition|)
block|{
specifier|final
name|V
name|target
init|=
name|graph
operator|.
name|target
argument_list|(
name|edge
argument_list|)
decl_stmt|;
if|if
condition|(
name|target
operator|.
name|equals
argument_list|(
name|to
argument_list|)
condition|)
block|{
comment|// We found a path.
name|prefix
operator|.
name|add
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|prefix
argument_list|)
argument_list|)
expr_stmt|;
name|prefix
operator|.
name|remove
argument_list|(
name|prefix
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|excludedNodes
operator|.
name|contains
argument_list|(
name|target
argument_list|)
condition|)
block|{
comment|// ignore it
block|}
else|else
block|{
name|prefix
operator|.
name|add
argument_list|(
name|target
argument_list|)
expr_stmt|;
name|findPathsExcluding
argument_list|(
name|target
argument_list|,
name|to
argument_list|,
name|list
argument_list|,
name|excludedNodes
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
name|prefix
operator|.
name|remove
argument_list|(
name|prefix
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|excludedNodes
operator|.
name|remove
argument_list|(
name|from
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

