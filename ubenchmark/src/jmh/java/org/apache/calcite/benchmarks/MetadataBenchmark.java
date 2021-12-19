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
name|jdbc
operator|.
name|Driver
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
name|metadata
operator|.
name|DefaultRelMetadataProvider
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
name|metadata
operator|.
name|JaninoRelMetadataProvider
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
name|metadata
operator|.
name|ProxyingMetadataHandlerProvider
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|Hook
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
name|test
operator|.
name|CalciteAssert
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
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * A benchmark to compare metadata retrieval time for a complex query.  *  * Compares metadata retrieval performance on a large query  *  */
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
literal|"-Xmx2048m"
argument_list|)
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Benchmark
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
literal|100
argument_list|,
name|timeUnit
operator|=
name|TimeUnit
operator|.
name|MILLISECONDS
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
literal|100
argument_list|,
name|timeUnit
operator|=
name|TimeUnit
operator|.
name|MILLISECONDS
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
name|MILLISECONDS
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
name|MetadataBenchmark
block|{
annotation|@
name|Setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|SQLException
block|{
name|DriverManager
operator|.
name|registerDriver
argument_list|(
operator|new
name|Driver
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|test
parameter_list|(
specifier|final
name|Supplier
argument_list|<
name|RelMetadataQuery
argument_list|>
name|supplier
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"store\".\"store_country\" as \"c0\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\" as \"c1\",\n"
operator|+
literal|" \"product_class\".\"product_family\" as \"c2\",\n"
operator|+
literal|" count(\"sales_fact_1997\".\"product_id\") as \"m0\"\n"
operator|+
literal|"from \"store\" as \"store\",\n"
operator|+
literal|" \"sales_fact_1997\" as \"sales_fact_1997\",\n"
operator|+
literal|" \"time_by_day\" as \"time_by_day\",\n"
operator|+
literal|" \"product_class\" as \"product_class\",\n"
operator|+
literal|" \"product\" as \"product\"\n"
operator|+
literal|"where \"sales_fact_1997\".\"store_id\" = \"store\".\"store_id\"\n"
operator|+
literal|"and \"store\".\"store_country\" = 'USA'\n"
operator|+
literal|"and \"sales_fact_1997\".\"time_id\" = \"time_by_day\".\"time_id\"\n"
operator|+
literal|"and \"time_by_day\".\"the_year\" = 1997\n"
operator|+
literal|"and \"sales_fact_1997\".\"product_id\" = \"product\".\"product_id\"\n"
operator|+
literal|"and \"product\".\"product_class_id\" = \"product_class\".\"product_class_id\"\n"
operator|+
literal|"group by \"store\".\"store_country\",\n"
operator|+
literal|" \"time_by_day\".\"the_year\",\n"
operator|+
literal|" \"product_class\".\"product_family\""
argument_list|)
operator|.
name|withHook
argument_list|(
name|Hook
operator|.
name|CONVERTED
argument_list|,
operator|(
name|Consumer
argument_list|<
name|RelNode
argument_list|>
operator|)
name|rel
lambda|->
block|{
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|setMetadataQuerySupplier
argument_list|(
name|supplier
argument_list|)
expr_stmt|;
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|invalidateMetadataQuery
argument_list|()
expr_stmt|;
block|}
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|""
operator|+
literal|"EnumerableAggregate(group=[{1, 6, 10}], m0=[COUNT()])\n"
operator|+
literal|"  EnumerableMergeJoin(condition=[=($2, $8)], joinType=[inner])\n"
operator|+
literal|"    EnumerableSort(sort0=[$2], dir0=[ASC])\n"
operator|+
literal|"      EnumerableMergeJoin(condition=[=($3, $5)], joinType=[inner])\n"
operator|+
literal|"        EnumerableSort(sort0=[$3], dir0=[ASC])\n"
operator|+
literal|"          EnumerableHashJoin(condition=[=($0, $4)], joinType=[inner])\n"
operator|+
literal|"            EnumerableCalc(expr#0..23=[{inputs}], expr#24=['USA':VARCHAR(30)], "
operator|+
literal|"expr#25=[=($t9, $t24)], store_id=[$t0], store_country=[$t9], $condition=[$t25])\n"
operator|+
literal|"              EnumerableTableScan(table=[[foodmart2, store]])\n"
operator|+
literal|"            EnumerableCalc(expr#0..7=[{inputs}], proj#0..1=[{exprs}], "
operator|+
literal|"store_id=[$t4])\n"
operator|+
literal|"              EnumerableTableScan(table=[[foodmart2, sales_fact_1997]])\n"
operator|+
literal|"        EnumerableCalc(expr#0..9=[{inputs}], expr#10=[CAST($t4):INTEGER], "
operator|+
literal|"expr#11=[1997], expr#12=[=($t10, $t11)], time_id=[$t0], the_year=[$t4], "
operator|+
literal|"$condition=[$t12])\n"
operator|+
literal|"          EnumerableTableScan(table=[[foodmart2, time_by_day]])\n"
operator|+
literal|"    EnumerableHashJoin(condition=[=($0, $2)], joinType=[inner])\n"
operator|+
literal|"      EnumerableCalc(expr#0..14=[{inputs}], proj#0..1=[{exprs}])\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, product]])\n"
operator|+
literal|"      EnumerableCalc(expr#0..4=[{inputs}], product_class_id=[$t0], "
operator|+
literal|"product_family=[$t4])\n"
operator|+
literal|"        EnumerableTableScan(table=[[foodmart2, product_class]])"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"c0=USA; c1=1997; c2=Non-Consumable; m0=16414\n"
operator|+
literal|"c0=USA; c1=1997; c2=Drink; m0=7978\n"
operator|+
literal|"c0=USA; c1=1997; c2=Food; m0=62445\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
specifier|public
name|void
name|janino
parameter_list|()
block|{
name|test
argument_list|(
name|RelMetadataQuery
operator|::
name|instance
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
specifier|public
name|void
name|janinoWithCompile
parameter_list|()
block|{
name|JaninoRelMetadataProvider
operator|.
name|clearStaticCache
argument_list|()
expr_stmt|;
name|test
argument_list|(
parameter_list|()
lambda|->
operator|new
name|RelMetadataQuery
argument_list|(
name|JaninoRelMetadataProvider
operator|.
name|of
argument_list|(
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Benchmark
specifier|public
name|void
name|proxying
parameter_list|()
block|{
name|test
argument_list|(
parameter_list|()
lambda|->
operator|new
name|RelMetadataQuery
argument_list|(
operator|new
name|ProxyingMetadataHandlerProvider
argument_list|(
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

