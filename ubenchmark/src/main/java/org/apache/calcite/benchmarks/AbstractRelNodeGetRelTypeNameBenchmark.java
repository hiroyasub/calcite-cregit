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
name|rel
operator|.
name|AbstractRelNode
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
name|Fork
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
name|Measurement
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
name|Param
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
name|annotations
operator|.
name|Threads
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
name|Warmup
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
name|Random
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
comment|/**  * A benchmark of alternative implementations for {@link AbstractRelNode#getRelTypeName()}  * method.  */
end_comment

begin_class
annotation|@
name|Fork
argument_list|(
name|value
operator|=
literal|1
argument_list|,
name|jvmArgsPrepend
operator|=
literal|"-Xmx1024m"
argument_list|)
annotation|@
name|Measurement
argument_list|(
name|iterations
operator|=
literal|10
argument_list|,
name|time
operator|=
literal|1
argument_list|,
name|timeUnit
operator|=
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
annotation|@
name|Warmup
argument_list|(
name|iterations
operator|=
literal|10
argument_list|,
name|time
operator|=
literal|1
argument_list|,
name|timeUnit
operator|=
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
annotation|@
name|Threads
argument_list|(
literal|1
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
specifier|public
class|class
name|AbstractRelNodeGetRelTypeNameBenchmark
block|{
comment|/**    * A state holding the full class names of all built-in implementors of the    * {@link org.apache.calcite.rel.RelNode} interface.    */
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Thread
argument_list|)
specifier|public
specifier|static
class|class
name|ClassNameState
block|{
specifier|private
specifier|final
name|String
index|[]
name|fullNames
init|=
operator|new
name|String
index|[]
block|{
literal|"org.apache.calcite.interpreter.InterpretableRel"
block|,
literal|"org.apache.calcite.interpreter.BindableRel"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableInterpretable"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableRel"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableLimit"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableUnion"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableCollect"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableTableFunctionScan"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableValues"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableSemiJoin"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableMinus"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableIntersect"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableUncollect"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableMergeJoin"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableProject"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableFilter"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcToEnumerableConverter"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableThetaJoin"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableTableScan"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableJoin"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableTableModify"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableAggregate"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableCorrelate"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableSort"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableWindow"
block|,
literal|"org.apache.calcite.plan.volcano.VolcanoPlannerTraitTest$FooRel"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableCalc"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableInterpreter"
block|,
literal|"org.apache.calcite.adapter.geode.rel.GeodeToEnumerableConverter"
block|,
literal|"org.apache.calcite.adapter.pig.PigToEnumerableConverter"
block|,
literal|"org.apache.calcite.adapter.mongodb.MongoToEnumerableConverter"
block|,
literal|"org.apache.calcite.adapter.csv.CsvTableScan"
block|,
literal|"org.apache.calcite.adapter.spark.SparkToEnumerableConverter"
block|,
literal|"org.apache.calcite.adapter.elasticsearch.ElasticsearchToEnumerableConverter"
block|,
literal|"org.apache.calcite.adapter.file.FileTableScan"
block|,
literal|"org.apache.calcite.adapter.cassandra.CassandraToEnumerableConverter"
block|,
literal|"org.apache.calcite.adapter.splunk.SplunkTableScan"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRel"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcTableScan"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcJoin"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcCalc"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcProject"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcFilter"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcAggregate"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcSort"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcUnion"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcIntersect"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcMinus"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcTableModify"
block|,
literal|"org.apache.calcite.adapter.jdbc.JdbcRules$JdbcValues"
block|,
literal|"org.apache.calcite.tools.PlannerTest$MockJdbcTableScan"
block|,
literal|"org.apache.calcite.rel.AbstractRelNode"
block|,
literal|"org.apache.calcite.rel.rules.MultiJoin"
block|,
literal|"org.apache.calcite.rel.core.TableFunctionScan"
block|,
literal|"org.apache.calcite.rel.BiRel"
block|,
literal|"org.apache.calcite.rel.SingleRel"
block|,
literal|"org.apache.calcite.rel.core.Values"
block|,
literal|"org.apache.calcite.rel.core.TableScan"
block|,
literal|"org.apache.calcite.plan.hep.HepRelVertex"
block|,
literal|"org.apache.calcite.plan.RelOptPlanReaderTest$MyRel"
block|,
literal|"org.apache.calcite.plan.volcano.TraitPropagationTest$PhysTable"
block|,
literal|"org.apache.calcite.plan.volcano.PlannerTests$TestLeafRel"
block|,
literal|"org.apache.calcite.plan.volcano.RelSubset"
block|,
literal|"org.apache.calcite.rel.core.SetOp"
block|,
literal|"org.apache.calcite.plan.volcano.VolcanoPlannerTraitTest$TestLeafRel"
block|,
literal|"org.apache.calcite.adapter.druid.DruidQuery"
block|,
literal|"org.apache.calcite.sql2rel.RelStructuredTypeFlattener$SelfFlatteningRel"
block|,
literal|"org.apache.calcite.rel.convert.Converter"
block|,
literal|"org.apache.calcite.rel.convert.ConverterImpl"
block|,
literal|"org.apache.calcite.plan.volcano.TraitPropagationTest$Phys"
block|,
literal|"org.apache.calcite.plan.volcano.TraitPropagationTest$PhysTable"
block|,
literal|"org.apache.calcite.plan.volcano.TraitPropagationTest$PhysSort"
block|,
literal|"org.apache.calcite.plan.volcano.TraitPropagationTest$PhysAgg"
block|,
literal|"org.apache.calcite.plan.volcano.TraitPropagationTest$PhysProj"
block|,
literal|"org.apache.calcite.interpreter.BindableRel"
block|,
literal|"org.apache.calcite.adapter.enumerable.EnumerableBindable"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableTableScan"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableFilter"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableProject"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableSort"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableJoin"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableUnion"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableValues"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableAggregate"
block|,
literal|"org.apache.calcite.interpreter.Bindables$BindableWindow"
block|,
literal|"org.apache.calcite.adapter.druid.DruidQuery"
block|,
literal|"org.apache.calcite.adapter.cassandra.CassandraRel"
block|,
literal|"org.apache.calcite.adapter.cassandra.CassandraFilter"
block|,
literal|"org.apache.calcite.adapter.cassandra.CassandraProject"
block|,
literal|"org.apache.calcite.adapter.cassandra.CassandraLimit"
block|,
literal|"org.apache.calcite.adapter.cassandra.CassandraSort"
block|,
literal|"org.apache.calcite.adapter.cassandra.CassandraTableScan"
block|,
literal|"org.apache.calcite.adapter.mongodb.MongoRel"
block|,
literal|"org.apache.calcite.adapter.mongodb.MongoTableScan"
block|,
literal|"org.apache.calcite.adapter.mongodb.MongoProject"
block|,
literal|"org.apache.calcite.adapter.mongodb.MongoFilter"
block|,
literal|"org.apache.calcite.adapter.mongodb.MongoAggregate"
block|,
literal|"org.apache.calcite.adapter.mongodb.MongoSort"
block|,
literal|"org.apache.calcite.adapter.spark.SparkRel"
block|,
literal|"org.apache.calcite.adapter.spark.JdbcToSparkConverter"
block|,
literal|"org.apache.calcite.adapter.spark.SparkRules$SparkValues"
block|,
literal|"org.apache.calcite.adapter.spark.EnumerableToSparkConverter"
block|,
literal|"org.apache.calcite.adapter.spark.SparkRules$SparkCalc"
block|,
literal|"org.apache.calcite.adapter.elasticsearch.ElasticsearchRel"
block|,
literal|"org.apache.calcite.adapter.elasticsearch.ElasticsearchFilter"
block|,
literal|"org.apache.calcite.adapter.elasticsearch.ElasticsearchProject"
block|,
literal|"org.apache.calcite.adapter.elasticsearch.ElasticsearchAggregate"
block|,
literal|"org.apache.calcite.adapter.elasticsearch.ElasticsearchTableScan"
block|,
literal|"org.apache.calcite.adapter.elasticsearch.ElasticsearchSort"
block|,
literal|"org.apache.calcite.adapter.geode.rel.GeodeRel"
block|,
literal|"org.apache.calcite.adapter.geode.rel.GeodeSort"
block|,
literal|"org.apache.calcite.adapter.geode.rel.GeodeTableScan"
block|,
literal|"org.apache.calcite.adapter.geode.rel.GeodeProject"
block|,
literal|"org.apache.calcite.adapter.geode.rel.GeodeFilter"
block|,
literal|"org.apache.calcite.adapter.geode.rel.GeodeAggregate"
block|,
literal|"org.apache.calcite.adapter.pig.PigRel"
block|,
literal|"org.apache.calcite.adapter.pig.PigTableScan"
block|,
literal|"org.apache.calcite.adapter.pig.PigAggregate"
block|,
literal|"org.apache.calcite.adapter.pig.PigJoin"
block|,
literal|"org.apache.calcite.adapter.pig.PigFilter"
block|,
literal|"org.apache.calcite.adapter.pig.PigProject"
block|}
decl_stmt|;
annotation|@
name|Param
argument_list|(
block|{
literal|"11"
block|,
literal|"31"
block|,
literal|"63"
block|}
argument_list|)
specifier|private
name|long
name|seed
decl_stmt|;
specifier|private
name|Random
name|r
init|=
literal|null
decl_stmt|;
comment|/**      * Setups the random number generator at the beginning of each iteration.      *      * To have relatively comparable results the generator should always use the same seed for the      * whole duration of the benchmark.      */
annotation|@
name|Setup
argument_list|(
name|Level
operator|.
name|Iteration
argument_list|)
specifier|public
name|void
name|setupRandom
parameter_list|()
block|{
name|r
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns a pseudo random class name which corresponds to an implementor of the RelNode      * interface.      */
specifier|public
name|String
name|nextName
parameter_list|()
block|{
return|return
name|fullNames
index|[
name|r
operator|.
name|nextInt
argument_list|(
name|fullNames
operator|.
name|length
argument_list|)
index|]
return|;
block|}
block|}
annotation|@
name|Benchmark
specifier|public
name|String
name|useStringLastIndexOfTwoTimesV1
parameter_list|(
name|ClassNameState
name|state
parameter_list|)
block|{
name|String
name|cn
init|=
name|state
operator|.
name|nextName
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|cn
operator|.
name|lastIndexOf
argument_list|(
literal|"$"
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|cn
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
name|i
operator|=
name|cn
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|cn
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
return|return
name|cn
return|;
block|}
annotation|@
name|Benchmark
specifier|public
name|String
name|useStringLastIndexOfTwoTimeV2
parameter_list|(
name|ClassNameState
name|state
parameter_list|)
block|{
name|String
name|cn
init|=
name|state
operator|.
name|nextName
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|cn
operator|.
name|lastIndexOf
argument_list|(
literal|'$'
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|cn
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
name|i
operator|=
name|cn
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|cn
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
return|return
name|cn
return|;
block|}
annotation|@
name|Benchmark
specifier|public
name|String
name|useCustomLastIndexOf
parameter_list|(
name|ClassNameState
name|state
parameter_list|)
block|{
name|String
name|cn
init|=
name|state
operator|.
name|nextName
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|cn
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
operator|--
name|i
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|cn
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'$'
operator|||
name|cn
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'.'
condition|)
block|{
return|return
name|cn
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
return|return
name|cn
return|;
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
name|AbstractRelNodeGetRelTypeNameBenchmark
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|detectJvmArgs
argument_list|()
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

begin_comment
comment|// End AbstractRelNodeGetRelTypeNameBenchmark.java
end_comment

end_unit

