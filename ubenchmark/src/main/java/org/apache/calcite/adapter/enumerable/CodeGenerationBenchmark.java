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
name|enumerable
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
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|linq4j
operator|.
name|tree
operator|.
name|ClassDeclaration
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|plan
operator|.
name|ConventionTraitDef
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
name|plan
operator|.
name|RelOptCluster
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
name|plan
operator|.
name|RelTraitSet
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
name|plan
operator|.
name|volcano
operator|.
name|VolcanoPlanner
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
name|rel
operator|.
name|RelNode
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
name|rel
operator|.
name|core
operator|.
name|JoinRelType
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
name|rel
operator|.
name|core
operator|.
name|RelFactories
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
name|rel
operator|.
name|rules
operator|.
name|FilterToCalcRule
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
name|rel
operator|.
name|rules
operator|.
name|ProjectToCalcRule
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexNode
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
name|runtime
operator|.
name|ArrayBindable
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
name|runtime
operator|.
name|Bindable
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
name|runtime
operator|.
name|Typed
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
name|runtime
operator|.
name|Utilities
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
name|tools
operator|.
name|RelBuilder
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
name|cache
operator|.
name|Cache
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
name|cache
operator|.
name|CacheBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|CompilerFactoryFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|IClassBodyEvaluator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|commons
operator|.
name|compiler
operator|.
name|ICompilerFactory
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
name|profile
operator|.
name|GCProfiler
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
name|io
operator|.
name|StringReader
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
name|List
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
comment|/**  * A benchmark of the main methods that are dynamically  * generating and compiling Java code at runtime.  *  * The benchmark examines the behavior of existing methods  * and evaluates the potential of adding a caching layer on top.  */
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
argument_list|)
annotation|@
name|Warmup
argument_list|(
name|iterations
operator|=
literal|0
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
name|SECONDS
argument_list|)
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|Throughput
argument_list|)
specifier|public
class|class
name|CodeGenerationBenchmark
block|{
comment|/**    * State holding the generated queries/plans and additional information    * exploited by the embedded compiler in order to dynamically build a Java class.    */
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
name|QueryState
block|{
comment|/**      * The number of distinct queries to be generated.      */
annotation|@
name|Param
argument_list|(
block|{
literal|"1"
block|,
literal|"10"
block|,
literal|"100"
block|,
literal|"1000"
block|}
argument_list|)
name|int
name|queries
decl_stmt|;
comment|/**      * The number of joins for each generated query.      */
annotation|@
name|Param
argument_list|(
block|{
literal|"1"
block|,
literal|"10"
block|,
literal|"20"
block|}
argument_list|)
name|int
name|joins
decl_stmt|;
comment|/**      * The number of disjunctions for each generated query.      */
annotation|@
name|Param
argument_list|(
block|{
literal|"1"
block|,
literal|"10"
block|,
literal|"100"
block|}
argument_list|)
name|int
name|whereClauseDisjunctions
decl_stmt|;
comment|/**      * The necessary plan information for every generated query.      */
name|PlanInfo
index|[]
name|planInfos
decl_stmt|;
specifier|private
name|int
name|currentPlan
init|=
literal|0
decl_stmt|;
annotation|@
name|Setup
argument_list|(
name|Level
operator|.
name|Trial
argument_list|)
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|planInfos
operator|=
operator|new
name|PlanInfo
index|[
name|queries
index|]
expr_stmt|;
name|VolcanoPlanner
name|planner
init|=
operator|new
name|VolcanoPlanner
argument_list|()
decl_stmt|;
name|planner
operator|.
name|addRelTraitDef
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|ProjectToCalcRule
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_CALC_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_JOIN_RULE
argument_list|)
expr_stmt|;
name|planner
operator|.
name|addRule
argument_list|(
name|EnumerableRules
operator|.
name|ENUMERABLE_VALUES_RULE
argument_list|)
expr_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|(
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|RelOptCluster
name|cluster
init|=
name|RelOptCluster
operator|.
name|create
argument_list|(
name|planner
argument_list|,
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
decl_stmt|;
name|RelTraitSet
name|desiredTraits
init|=
name|cluster
operator|.
name|traitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|RelBuilder
name|relBuilder
init|=
name|RelFactories
operator|.
name|LOGICAL_BUILDER
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Generates queries of the following form depending on the configuration parameters.
comment|// SELECT `t`.`name`
comment|// FROM (VALUES  (1, 'Value0')) AS `t` (`id`, `name`)
comment|// INNER JOIN (VALUES  (1, 'Value1')) AS `t` (`id`, `name`) AS `t0` ON `t`.`id` = `t0`.`id`
comment|// INNER JOIN (VALUES  (2, 'Value2')) AS `t` (`id`, `name`) AS `t1` ON `t`.`id` = `t1`.`id`
comment|// INNER JOIN (VALUES  (3, 'Value3')) AS `t` (`id`, `name`) AS `t2` ON `t`.`id` = `t2`.`id`
comment|// INNER JOIN ...
comment|// WHERE
comment|//  `t`.`name` = 'name0' OR
comment|//  `t`.`name` = 'name1' OR
comment|//  `t`.`name` = 'name2' OR
comment|//  ...
comment|//  OR `t`.`id` = 0
comment|// The last disjunction (i.e, t.id = $i) is what makes the queries different from one another
comment|// by assigning a different constant literal.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|queries
condition|;
name|i
operator|++
control|)
block|{
name|relBuilder
operator|.
name|values
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"name"
block|}
argument_list|,
literal|1
argument_list|,
literal|"Value"
operator|+
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<=
name|joins
condition|;
name|j
operator|++
control|)
block|{
name|relBuilder
operator|.
name|values
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"id"
block|,
literal|"name"
block|}
argument_list|,
name|j
argument_list|,
literal|"Value"
operator|+
name|j
argument_list|)
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
literal|"id"
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|RexNode
argument_list|>
name|disjunctions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|whereClauseDisjunctions
condition|;
name|j
operator|++
control|)
block|{
name|disjunctions
operator|.
name|add
argument_list|(
name|relBuilder
operator|.
name|equals
argument_list|(
name|relBuilder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|relBuilder
operator|.
name|literal
argument_list|(
literal|"name"
operator|+
name|j
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|disjunctions
operator|.
name|add
argument_list|(
name|relBuilder
operator|.
name|equals
argument_list|(
name|relBuilder
operator|.
name|field
argument_list|(
literal|"id"
argument_list|)
argument_list|,
name|relBuilder
operator|.
name|literal
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|RelNode
name|query
init|=
name|relBuilder
operator|.
name|filter
argument_list|(
name|relBuilder
operator|.
name|or
argument_list|(
name|disjunctions
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|relBuilder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|RelNode
name|query0
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|query
argument_list|,
name|desiredTraits
argument_list|)
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|query0
argument_list|)
expr_stmt|;
name|PlanInfo
name|info
init|=
operator|new
name|PlanInfo
argument_list|()
decl_stmt|;
name|EnumerableRel
name|plan
init|=
operator|(
name|EnumerableRel
operator|)
name|planner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
name|EnumerableRelImplementor
name|relImplementor
init|=
operator|new
name|EnumerableRelImplementor
argument_list|(
name|plan
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|info
operator|.
name|classExpr
operator|=
name|relImplementor
operator|.
name|implementRoot
argument_list|(
name|plan
argument_list|,
name|EnumerableRel
operator|.
name|Prefer
operator|.
name|ARRAY
argument_list|)
expr_stmt|;
name|info
operator|.
name|javaCode
operator|=
name|Expressions
operator|.
name|toString
argument_list|(
name|info
operator|.
name|classExpr
operator|.
name|memberDeclarations
argument_list|,
literal|"\n"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|ICompilerFactory
name|compilerFactory
decl_stmt|;
try|try
block|{
name|compilerFactory
operator|=
name|CompilerFactoryFactory
operator|.
name|getDefaultCompilerFactory
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to instantiate java compiler"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|IClassBodyEvaluator
name|cbe
init|=
name|compilerFactory
operator|.
name|newClassBodyEvaluator
argument_list|()
decl_stmt|;
name|cbe
operator|.
name|setClassName
argument_list|(
name|info
operator|.
name|classExpr
operator|.
name|name
argument_list|)
expr_stmt|;
name|cbe
operator|.
name|setExtendedClass
argument_list|(
name|Utilities
operator|.
name|class
argument_list|)
expr_stmt|;
name|cbe
operator|.
name|setImplementedInterfaces
argument_list|(
name|plan
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|==
literal|1
condition|?
operator|new
name|Class
index|[]
block|{
name|Bindable
operator|.
name|class
block|,
name|Typed
operator|.
name|class
block|}
else|:
operator|new
name|Class
index|[]
block|{
name|ArrayBindable
operator|.
name|class
block|}
argument_list|)
expr_stmt|;
name|cbe
operator|.
name|setParentClassLoader
argument_list|(
name|EnumerableInterpretable
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
name|info
operator|.
name|cbe
operator|=
name|cbe
expr_stmt|;
name|planInfos
index|[
name|i
index|]
operator|=
name|info
expr_stmt|;
block|}
block|}
name|int
name|nextPlan
parameter_list|()
block|{
name|int
name|ret
init|=
name|currentPlan
decl_stmt|;
name|currentPlan
operator|=
operator|(
name|currentPlan
operator|+
literal|1
operator|)
operator|%
name|queries
expr_stmt|;
return|return
name|ret
return|;
block|}
block|}
comment|/***/
specifier|private
specifier|static
class|class
name|PlanInfo
block|{
name|ClassDeclaration
name|classExpr
decl_stmt|;
name|IClassBodyEvaluator
name|cbe
decl_stmt|;
name|String
name|javaCode
decl_stmt|;
block|}
comment|/**    * State holding a cache that is initialized    * once at the beginning of each iteration.    */
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
name|CacheState
block|{
annotation|@
name|Param
argument_list|(
block|{
literal|"10"
block|,
literal|"100"
block|,
literal|"1000"
block|}
argument_list|)
name|int
name|cacheSize
decl_stmt|;
name|Cache
argument_list|<
name|String
argument_list|,
name|Bindable
argument_list|>
name|cache
decl_stmt|;
annotation|@
name|Setup
argument_list|(
name|Level
operator|.
name|Iteration
argument_list|)
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|cache
operator|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|maximumSize
argument_list|(
name|cacheSize
argument_list|)
operator|.
name|concurrencyLevel
argument_list|(
literal|1
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Benchmarks the part creating Bindable instances from    * {@link EnumerableInterpretable#getBindable(ClassDeclaration, String, int)}    * method without any additional caching layer.    */
annotation|@
name|Benchmark
specifier|public
name|Bindable
argument_list|<
name|?
argument_list|>
name|getBindableNoCache
parameter_list|(
name|QueryState
name|state
parameter_list|)
throws|throws
name|Exception
block|{
name|PlanInfo
name|info
init|=
name|state
operator|.
name|planInfos
index|[
name|state
operator|.
name|nextPlan
argument_list|()
index|]
decl_stmt|;
return|return
operator|(
name|Bindable
operator|)
name|info
operator|.
name|cbe
operator|.
name|createInstance
argument_list|(
operator|new
name|StringReader
argument_list|(
name|info
operator|.
name|javaCode
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Benchmarks the part of creating Bindable instances from    * {@link EnumerableInterpretable#getBindable(ClassDeclaration, String, int)}    * method with an additional cache layer.    */
annotation|@
name|Benchmark
specifier|public
name|Bindable
argument_list|<
name|?
argument_list|>
name|getBindableWithCache
parameter_list|(
name|QueryState
name|jState
parameter_list|,
name|CacheState
name|chState
parameter_list|)
throws|throws
name|Exception
block|{
name|PlanInfo
name|info
init|=
name|jState
operator|.
name|planInfos
index|[
name|jState
operator|.
name|nextPlan
argument_list|()
index|]
decl_stmt|;
name|Cache
argument_list|<
name|String
argument_list|,
name|Bindable
argument_list|>
name|cache
init|=
name|chState
operator|.
name|cache
decl_stmt|;
name|EnumerableInterpretable
operator|.
name|StaticFieldDetector
name|detector
init|=
operator|new
name|EnumerableInterpretable
operator|.
name|StaticFieldDetector
argument_list|()
decl_stmt|;
name|info
operator|.
name|classExpr
operator|.
name|accept
argument_list|(
name|detector
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|detector
operator|.
name|containsStaticField
condition|)
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|info
operator|.
name|javaCode
argument_list|,
parameter_list|()
lambda|->
operator|(
name|Bindable
operator|)
name|info
operator|.
name|cbe
operator|.
name|createInstance
argument_list|(
operator|new
name|StringReader
argument_list|(
name|info
operator|.
name|javaCode
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Benchmark queries should not arrive here"
argument_list|)
throw|;
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
name|CodeGenerationBenchmark
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|addProfiler
argument_list|(
name|GCProfiler
operator|.
name|class
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
comment|// End CodeGenerationBenchmark.java
end_comment

end_unit

