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
name|test
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
name|config
operator|.
name|CalciteSystemProperty
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
name|materialize
operator|.
name|SqlStatisticProvider
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
name|RelOptTable
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
name|RelTraitDef
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
name|schema
operator|.
name|SchemaPlus
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
name|sql
operator|.
name|parser
operator|.
name|SqlParser
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
name|statistic
operator|.
name|CachingSqlStatisticProvider
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
name|statistic
operator|.
name|MapSqlStatisticProvider
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
name|statistic
operator|.
name|QuerySqlStatisticProvider
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
name|Frameworks
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
name|Programs
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
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link org.apache.calcite.materialize.SqlStatisticProvider}  * and implementations of it.  */
end_comment

begin_class
specifier|public
class|class
name|SqlStatisticProviderTest
block|{
comment|/** Creates a config based on the "foodmart" schema. */
specifier|public
specifier|static
name|Frameworks
operator|.
name|ConfigBuilder
name|config
parameter_list|()
block|{
specifier|final
name|SchemaPlus
name|rootSchema
init|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
decl_stmt|;
return|return
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|JDBC_FOODMART
argument_list|)
argument_list|)
operator|.
name|traitDefs
argument_list|(
operator|(
name|List
argument_list|<
name|RelTraitDef
argument_list|>
operator|)
literal|null
argument_list|)
operator|.
name|programs
argument_list|(
name|Programs
operator|.
name|heuristicJoinOrder
argument_list|(
name|Programs
operator|.
name|RULE_SET
argument_list|,
literal|true
argument_list|,
literal|2
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMapProvider
parameter_list|()
block|{
name|check
argument_list|(
name|MapSqlStatisticProvider
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQueryProvider
parameter_list|()
block|{
specifier|final
name|boolean
name|debug
init|=
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|String
argument_list|>
name|sqlConsumer
init|=
name|debug
condition|?
name|System
operator|.
name|out
operator|::
name|println
else|:
name|Util
operator|::
name|discard
decl_stmt|;
name|check
argument_list|(
operator|new
name|QuerySqlStatisticProvider
argument_list|(
name|sqlConsumer
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQueryProviderWithCache
parameter_list|()
block|{
name|Cache
argument_list|<
name|List
argument_list|,
name|Object
argument_list|>
name|cache
init|=
name|CacheBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|expireAfterAccess
argument_list|(
literal|5
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|AtomicInteger
name|counter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
name|QuerySqlStatisticProvider
name|provider
init|=
operator|new
name|QuerySqlStatisticProvider
argument_list|(
name|sql
lambda|->
name|counter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SqlStatisticProvider
name|cachingProvider
init|=
operator|new
name|CachingSqlStatisticProvider
argument_list|(
name|provider
argument_list|,
name|cache
argument_list|)
decl_stmt|;
name|check
argument_list|(
name|cachingProvider
argument_list|)
expr_stmt|;
specifier|final
name|int
name|expectedQueryCount
init|=
literal|6
decl_stmt|;
name|assertThat
argument_list|(
name|counter
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedQueryCount
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|cachingProvider
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|counter
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
name|expectedQueryCount
argument_list|)
argument_list|)
expr_stmt|;
comment|// no more queries
block|}
specifier|private
name|void
name|check
parameter_list|(
name|SqlStatisticProvider
name|provider
parameter_list|)
block|{
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|RelBuilder
operator|.
name|create
argument_list|(
name|config
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|productScan
init|=
name|relBuilder
operator|.
name|scan
argument_list|(
literal|"product"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|productTable
init|=
name|productScan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|salesScan
init|=
name|relBuilder
operator|.
name|scan
argument_list|(
literal|"sales_fact_1997"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|salesTable
init|=
name|salesScan
operator|.
name|getTable
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|employeeScan
init|=
name|relBuilder
operator|.
name|scan
argument_list|(
literal|"employee"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelOptTable
name|employeeTable
init|=
name|employeeScan
operator|.
name|getTable
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|provider
operator|.
name|tableCardinality
argument_list|(
name|productTable
argument_list|)
argument_list|,
name|is
argument_list|(
literal|1_560.0d
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|provider
operator|.
name|isKey
argument_list|(
name|productTable
argument_list|,
name|columns
argument_list|(
name|productTable
argument_list|,
literal|"product_id"
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|provider
operator|.
name|isKey
argument_list|(
name|salesTable
argument_list|,
name|columns
argument_list|(
name|salesTable
argument_list|,
literal|"product_id"
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|provider
operator|.
name|isForeignKey
argument_list|(
name|salesTable
argument_list|,
name|columns
argument_list|(
name|salesTable
argument_list|,
literal|"product_id"
argument_list|)
argument_list|,
name|productTable
argument_list|,
name|columns
argument_list|(
name|productTable
argument_list|,
literal|"product_id"
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Not a foreign key; product has some ids that are not referenced by any
comment|// sale
name|assertThat
argument_list|(
name|provider
operator|.
name|isForeignKey
argument_list|(
name|productTable
argument_list|,
name|columns
argument_list|(
name|productTable
argument_list|,
literal|"product_id"
argument_list|)
argument_list|,
name|salesTable
argument_list|,
name|columns
argument_list|(
name|salesTable
argument_list|,
literal|"product_id"
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// There is one supervisor_id, 0, which is not an employee_id
name|assertThat
argument_list|(
name|provider
operator|.
name|isForeignKey
argument_list|(
name|employeeTable
argument_list|,
name|columns
argument_list|(
name|employeeTable
argument_list|,
literal|"supervisor_id"
argument_list|)
argument_list|,
name|employeeTable
argument_list|,
name|columns
argument_list|(
name|employeeTable
argument_list|,
literal|"employee_id"
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|columns
parameter_list|(
name|RelOptTable
name|table
parameter_list|,
name|String
modifier|...
name|columnNames
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|columnNames
argument_list|)
operator|.
name|map
argument_list|(
name|columnName
lambda|->
name|table
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
operator|.
name|indexOf
argument_list|(
name|columnName
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

