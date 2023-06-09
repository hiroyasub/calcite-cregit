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
name|benchmarks
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
name|graph
operator|.
name|DefaultDirectedGraph
import|;
end_import

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
name|graph
operator|.
name|DefaultEdge
import|;
end_import

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
name|graph
operator|.
name|DirectedGraph
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
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Benchmark
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|BenchmarkMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Mode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|OutputTimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Setup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|State
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|Runner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|RunnerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|options
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|options
operator|.
name|OptionsBuilder
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
name|List
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Benchmarks for {@link org.apache.calcite.util.graph.DefaultDirectedGraph}.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultDirectedGraphBenchmark
block|{
comment|/** Node in the graph. */
specifier|private
specifier|static
class|class
name|Node
block|{
specifier|final
name|int
name|id
decl_stmt|;
specifier|private
name|Node
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|instanceof
name|Node
operator|&&
operator|(
operator|(
name|Node
operator|)
name|o
operator|)
operator|.
name|id
operator|==
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
comment|/**    * State object for the benchmarks.    */
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Benchmark
argument_list|)
specifier|public
specifier|static
class|class
name|GraphState
block|{
specifier|static
specifier|final
name|int
name|NUM_LAYERS
init|=
literal|8
decl_stmt|;
name|DirectedGraph
argument_list|<
name|Node
argument_list|,
name|DefaultEdge
argument_list|>
name|graph
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|tenNodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|twentyNodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|thirtyNodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|fortyNodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|fiftyNodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|sixtyNodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|seventyNodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|eightyNodes
decl_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|ninetyNodes
decl_stmt|;
annotation|@
name|Setup
argument_list|(
name|Level
operator|.
name|Invocation
argument_list|)
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|nodes
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
comment|// create a binary tree
name|graph
operator|=
name|DefaultDirectedGraph
operator|.
name|create
argument_list|()
expr_stmt|;
comment|// create nodes
name|int
name|curId
init|=
literal|1
decl_stmt|;
name|Node
name|root
init|=
operator|new
name|Node
argument_list|(
name|curId
operator|++
argument_list|)
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|graph
operator|.
name|addVertex
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|prevLayerNodes
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|root
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|NUM_LAYERS
condition|;
name|i
operator|++
control|)
block|{
name|List
argument_list|<
name|Node
argument_list|>
name|curLayerNodes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|prevLayerNodes
control|)
block|{
name|Node
name|leftChild
init|=
operator|new
name|Node
argument_list|(
name|curId
operator|++
argument_list|)
decl_stmt|;
name|Node
name|rightChild
init|=
operator|new
name|Node
argument_list|(
name|curId
operator|++
argument_list|)
decl_stmt|;
name|curLayerNodes
operator|.
name|add
argument_list|(
name|leftChild
argument_list|)
expr_stmt|;
name|curLayerNodes
operator|.
name|add
argument_list|(
name|rightChild
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|leftChild
argument_list|)
expr_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|rightChild
argument_list|)
expr_stmt|;
name|graph
operator|.
name|addVertex
argument_list|(
name|leftChild
argument_list|)
expr_stmt|;
name|graph
operator|.
name|addVertex
argument_list|(
name|rightChild
argument_list|)
expr_stmt|;
name|graph
operator|.
name|addEdge
argument_list|(
name|node
argument_list|,
name|leftChild
argument_list|)
expr_stmt|;
name|graph
operator|.
name|addEdge
argument_list|(
name|node
argument_list|,
name|rightChild
argument_list|)
expr_stmt|;
block|}
name|prevLayerNodes
operator|=
name|curLayerNodes
expr_stmt|;
block|}
name|int
name|tenNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.1
operator|)
decl_stmt|;
name|int
name|twentyNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.2
operator|)
decl_stmt|;
name|int
name|thirtyNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.3
operator|)
decl_stmt|;
name|int
name|fortyNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.4
operator|)
decl_stmt|;
name|int
name|fiftyNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.5
operator|)
decl_stmt|;
name|int
name|sixtyNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.6
operator|)
decl_stmt|;
name|int
name|seventyNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.7
operator|)
decl_stmt|;
name|int
name|eightyNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.8
operator|)
decl_stmt|;
name|int
name|ninetyNodeCount
init|=
operator|(
name|int
operator|)
operator|(
name|nodes
operator|.
name|size
argument_list|()
operator|*
literal|0.9
operator|)
decl_stmt|;
name|tenNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|tenNodeCount
argument_list|)
expr_stmt|;
name|twentyNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|twentyNodeCount
argument_list|)
expr_stmt|;
name|thirtyNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|thirtyNodeCount
argument_list|)
expr_stmt|;
name|fortyNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|fortyNodeCount
argument_list|)
expr_stmt|;
name|fiftyNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|fiftyNodeCount
argument_list|)
expr_stmt|;
name|sixtyNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|sixtyNodeCount
argument_list|)
expr_stmt|;
name|seventyNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|seventyNodeCount
argument_list|)
expr_stmt|;
name|eightyNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|eightyNodeCount
argument_list|)
expr_stmt|;
name|ninetyNodes
operator|=
name|nodes
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|ninetyNodeCount
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|int
name|getInwardEdgesBenchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|int
name|sum
init|=
literal|0
decl_stmt|;
name|int
name|curId
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|GraphState
operator|.
name|NUM_LAYERS
condition|;
name|i
operator|++
control|)
block|{
comment|// get the first node in each layer
name|Node
name|curNode
init|=
name|state
operator|.
name|nodes
operator|.
name|get
argument_list|(
name|curId
operator|-
literal|1
argument_list|)
decl_stmt|;
name|sum
operator|+=
name|state
operator|.
name|graph
operator|.
name|getInwardEdges
argument_list|(
name|curNode
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|curId
operator|*=
literal|2
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|int
name|getOutwardEdgesBenchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|int
name|sum
init|=
literal|0
decl_stmt|;
name|int
name|curId
init|=
literal|1
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|GraphState
operator|.
name|NUM_LAYERS
condition|;
name|i
operator|++
control|)
block|{
comment|// get the first node in each layer
name|Node
name|curNode
init|=
name|state
operator|.
name|nodes
operator|.
name|get
argument_list|(
name|curId
operator|-
literal|1
argument_list|)
decl_stmt|;
name|sum
operator|+=
name|state
operator|.
name|graph
operator|.
name|getOutwardEdges
argument_list|(
name|curNode
argument_list|)
operator|.
name|size
argument_list|()
expr_stmt|;
name|curId
operator|*=
literal|2
expr_stmt|;
block|}
return|return
name|sum
return|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|boolean
name|addVertexBenchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|graph
operator|.
name|addVertex
argument_list|(
operator|new
name|Node
argument_list|(
literal|100
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|DefaultEdge
name|addEdgeBenchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|graph
operator|.
name|addEdge
argument_list|(
name|state
operator|.
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|state
operator|.
name|nodes
operator|.
name|get
argument_list|(
literal|5
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|DefaultEdge
name|getEdgeBenchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|graph
operator|.
name|getEdge
argument_list|(
name|state
operator|.
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|state
operator|.
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|boolean
name|removeEdgeBenchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
return|return
name|state
operator|.
name|graph
operator|.
name|removeEdge
argument_list|(
name|state
operator|.
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|state
operator|.
name|nodes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices10Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|tenNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices20Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|twentyNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices30Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|thirtyNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices40Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|fortyNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices50Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|fiftyNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices60Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|sixtyNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices70Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|seventyNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices80Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|eightyNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices90Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|ninetyNodes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
name|void
name|removeAllVertices100Benchmark
parameter_list|(
name|GraphState
name|state
parameter_list|)
block|{
name|state
operator|.
name|graph
operator|.
name|removeAllVertices
argument_list|(
name|state
operator|.
name|nodes
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|RunnerException
block|{
name|Options
name|opt
init|=
operator|new
name|OptionsBuilder
argument_list|()
operator|.
name|include
argument_list|(
name|DefaultDirectedGraphBenchmark
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|forks
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
operator|new
name|Runner
argument_list|(
name|opt
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

