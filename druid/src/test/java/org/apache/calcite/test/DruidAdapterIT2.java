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
name|adapter
operator|.
name|druid
operator|.
name|DruidQuery
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
name|adapter
operator|.
name|druid
operator|.
name|DruidSchema
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
name|config
operator|.
name|CalciteConnectionConfig
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
name|config
operator|.
name|CalciteConnectionProperty
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
name|type
operator|.
name|RelDataType
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
name|schema
operator|.
name|impl
operator|.
name|AbstractSchema
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|type
operator|.
name|SqlTypeName
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
name|base
operator|.
name|Function
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
name|ArrayListMultimap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Multimap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertSame
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.druid} package.  *  *<p>Before calling this test, you need to populate Druid, as follows:  *  *<blockquote><code>  * git clone https://github.com/vlsi/calcite-test-dataset<br>  * cd calcite-test-dataset<br>  * mvn install  *</code></blockquote>  *  *<p>This will create a virtual machine with Druid and test data set.  *  *<p>Features not yet implemented:  *<ul>  *<li>push LIMIT into "select" query</li>  *<li>push SORT and/or LIMIT into "groupBy" query</li>  *<li>push HAVING into "groupBy" query</li>  *</ul>  *  *<p>These tests use TIMESTAMP type for the Druid timestamp column, instead  * of TIMESTAMP WITH LOCAL TIME ZONE type as {@link DruidAdapterIT}.  */
end_comment

begin_class
specifier|public
class|class
name|DruidAdapterIT2
block|{
comment|/** URL of the "druid-foodmart" model. */
specifier|public
specifier|static
specifier|final
name|URL
name|FOODMART
init|=
name|DruidAdapterIT2
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/druid-foodmart-model-timestamp.json"
argument_list|)
decl_stmt|;
comment|/** Whether to run Druid tests. Enabled by default, however test is only    * included if "it" profile is activated ({@code -Pit}). To disable,    * specify {@code -Dcalcite.test.druid=false} on the Java command line. */
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Util
operator|.
name|getBooleanProperty
argument_list|(
literal|"calcite.test.druid"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|VARCHAR_TYPE
init|=
literal|"VARCHAR CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\""
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FOODMART_TABLE
init|=
literal|"\"foodmart\""
decl_stmt|;
comment|/** Whether to run this test. */
specifier|protected
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|ENABLED
return|;
block|}
comment|/** Returns a function that checks that a particular Druid query is    * generated to implement a query. */
specifier|private
specifier|static
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
name|druidChecker
parameter_list|(
specifier|final
name|String
modifier|...
name|lines
parameter_list|)
block|{
return|return
operator|new
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|List
name|list
parameter_list|)
block|{
name|assertThat
argument_list|(
name|list
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|DruidQuery
operator|.
name|QuerySpec
name|querySpec
init|=
operator|(
name|DruidQuery
operator|.
name|QuerySpec
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|lines
control|)
block|{
specifier|final
name|String
name|s
init|=
name|line
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|querySpec
operator|.
name|getQueryString
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
name|containsString
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
comment|/**    * Creates a query against FOODMART with approximate parameters    * */
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|foodmartApprox
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|approxQuery
argument_list|(
name|FOODMART
argument_list|,
name|sql
argument_list|)
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|approxQuery
parameter_list|(
name|URL
name|url
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|APPROXIMATE_DISTINCT_COUNT
operator|.
name|camelName
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|APPROXIMATE_TOP_N
operator|.
name|camelName
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|APPROXIMATE_DECIMAL
operator|.
name|camelName
argument_list|()
argument_list|,
literal|true
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
return|;
block|}
comment|/** Creates a query against a data set given by a map. */
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|sql
parameter_list|(
name|String
name|sql
parameter_list|,
name|URL
name|url
parameter_list|)
block|{
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|url
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
return|;
block|}
comment|/** Creates a query against the {@link #FOODMART} data set. */
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMetadataColumns
parameter_list|()
throws|throws
name|Exception
block|{
name|sql
argument_list|(
literal|"values 1"
argument_list|)
operator|.
name|withConnection
argument_list|(
operator|new
name|Function
argument_list|<
name|Connection
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|Connection
name|c
parameter_list|)
block|{
try|try
block|{
specifier|final
name|DatabaseMetaData
name|metaData
init|=
name|c
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
specifier|final
name|ResultSet
name|r
init|=
name|metaData
operator|.
name|getColumns
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"foodmart"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Multimap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|map
init|=
name|ArrayListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
while|while
condition|(
name|r
operator|.
name|next
argument_list|()
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|r
operator|.
name|getString
argument_list|(
literal|"TYPE_NAME"
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|map
argument_list|)
expr_stmt|;
comment|// 1 timestamp, 2 float measure, 1 int measure, 88 dimensions
name|assertThat
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|values
argument_list|()
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|92
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"TIMESTAMP(0) NOT NULL"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"DOUBLE"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|get
argument_list|(
literal|"BIGINT"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|map
operator|.
name|get
argument_list|(
name|VARCHAR_TYPE
argument_list|)
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|88
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinct
parameter_list|()
block|{
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{30}], aggs=[[]])"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"state_province\" from \"foodmart\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'state_province','outputName':'state_province'"
operator|+
literal|",'outputType':'STRING'}],'limitSpec':{'type':'default'},"
operator|+
literal|"'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state_province=CA"
argument_list|,
literal|"state_province=OR"
argument_list|,
literal|"state_province=WA"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectGroupBySum
parameter_list|()
block|{
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"projects=[[$30, CAST($89):INTEGER]], groups=[{0}], aggs=[[SUM($1)]])"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select \"state_province\", sum(cast(\"unit_sales\" as integer)) as u\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state_province=CA; U=74748"
argument_list|,
literal|"state_province=OR; U=67659"
argument_list|,
literal|"state_province=WA; U=124366"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupbyMetric
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select  \"store_sales\" ,\"product_id\" from \"foodmart\" "
operator|+
literal|"where \"product_id\" = 1020"
operator|+
literal|"group by \"store_sales\" ,\"product_id\" "
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[=($1, 1020)],"
operator|+
literal|" projects=[[$90, $1]], groups=[{0, 1}], aggs=[[]])"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'store_sales',\"outputName\":\"store_sales\","
operator|+
literal|"'outputType':'DOUBLE'},{'type':'default','dimension':'product_id','outputName':"
operator|+
literal|"'product_id','outputType':'STRING'}],'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'bound','dimension':'product_id','lower':'1020','lowerStrict':false,"
operator|+
literal|"'upper':'1020','upperStrict':false,'ordering':'numeric'},'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"store_sales=0.51; product_id=1020"
argument_list|,
literal|"store_sales=1.02; product_id=1020"
argument_list|,
literal|"store_sales=1.53; product_id=1020"
argument_list|,
literal|"store_sales=2.04; product_id=1020"
argument_list|,
literal|"store_sales=2.55; product_id=1020"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushSimpleGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"product_id\" from \"foodmart\" where "
operator|+
literal|"\"product_id\" = 1020 group by \"product_id\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'product_id','outputName':'product_id','outputType':'STRING'}],"
operator|+
literal|"'limitSpec':{'type':'default'},'filter':{'type':'bound','dimension':'product_id',"
operator|+
literal|"'lower':'1020','lowerStrict':false,'upper':'1020','upperStrict':false,"
operator|+
literal|"'ordering':'numeric'},'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_id=1020"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testComplexPushGroupBy
parameter_list|()
block|{
specifier|final
name|String
name|innerQuery
init|=
literal|"select \"product_id\" as \"id\" from \"foodmart\" where "
operator|+
literal|"\"product_id\" = 1020"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select \"id\" from ("
operator|+
name|innerQuery
operator|+
literal|") group by \"id\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'product_id','outputName':'product_id',"
operator|+
literal|"'outputType':'STRING'}],'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'bound','dimension':'product_id','lower':'1020','lowerStrict':false,"
operator|+
literal|"'upper':'1020','upperStrict':false,'ordering':'numeric'},'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"id=1020"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1281">[CALCITE-1281]    * Druid adapter wrongly returns all numeric values as int or float</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectCount
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c from \"foodmart\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
operator|new
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|ResultSet
name|input
parameter_list|)
block|{
try|try
block|{
name|assertThat
argument_list|(
name|input
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getInt
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|86829
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getLong
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|86829L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"86829"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|wasNull
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|input
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSort
parameter_list|()
block|{
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], projects=[[$39, $30]], "
operator|+
literal|"groups=[{0, 1}], aggs=[[]], sort0=[1], sort1=[0], dir0=[ASC], dir1=[DESC])"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"gender\", \"state_province\"\n"
operator|+
literal|"from \"foodmart\" order by 2, 1 desc"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"gender=M; state_province=CA"
argument_list|,
literal|"gender=F; state_province=CA"
argument_list|,
literal|"gender=M; state_province=OR"
argument_list|,
literal|"gender=F; state_province=OR"
argument_list|,
literal|"gender=M; state_province=WA"
argument_list|,
literal|"gender=F; state_province=WA"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'gender','outputName':'gender',"
operator|+
literal|"'outputType':'STRING'},{'type':'default','dimension':'state_province',"
operator|+
literal|"'outputName':'state_province','outputType':'STRING'}],'limitSpec':"
operator|+
literal|"{'type':'default','columns':[{'dimension':'state_province','direction':'ascending'"
operator|+
literal|",'dimensionOrder':'lexicographic'},{'dimension':'gender','direction':'descending',"
operator|+
literal|"'dimensionOrder':'lexicographic'}]},'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSortLimit
parameter_list|()
block|{
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$1], sort1=[$0], dir0=[ASC], dir1=[DESC], offset=[2], fetch=[3])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], projects=[[$39, $30]], "
operator|+
literal|"groups=[{0, 1}], aggs=[[]], sort0=[1], sort1=[0], dir0=[ASC], dir1=[DESC])"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"gender\", \"state_province\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"order by 2, 1 desc offset 2 rows fetch next 3 rows only"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"gender=M; state_province=OR"
argument_list|,
literal|"gender=F; state_province=OR"
argument_list|,
literal|"gender=M; state_province=WA"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOffsetLimit
parameter_list|()
block|{
comment|// We do not yet push LIMIT into a Druid "select" query as a "threshold".
comment|// It is not possible to push OFFSET into Druid "select" query.
specifier|final
name|String
name|sql
init|=
literal|"select \"state_province\", \"product_name\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"offset 2 fetch next 3 rows only"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan','dataSource':'foodmart',"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'columns':['state_province','product_name'],"
operator|+
literal|"'resultFormat':'compactedList'}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"gender\", \"state_province\"\n"
operator|+
literal|"from \"foodmart\" fetch next 3 rows only"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan','dataSource':'foodmart',"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'columns':['gender','state_province'],"
operator|+
literal|"'resultFormat':'compactedList','limit':3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"gender\", \"state_province\"\n"
operator|+
literal|"from \"foodmart\" fetch next 3 rows only"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default','dimension':'gender',"
operator|+
literal|"'outputName':'gender','outputType':'STRING'},"
operator|+
literal|"{'type':'default','dimension':'state_province','outputName':'state_province',"
operator|+
literal|"'outputType':'STRING'}],'limitSpec':{'type':'default',"
operator|+
literal|"'limit':3,'columns':[]},"
operator|+
literal|"'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], projects=[[$39, $30]], "
operator|+
literal|"groups=[{0, 1}], aggs=[[]], fetch=[3])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"gender=F; state_province=CA"
argument_list|,
literal|"gender=F; state_province=OR"
argument_list|,
literal|"gender=F; state_province=WA"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1578">[CALCITE-1578]    * Druid adapter: wrong semantics of topN query limit with granularity</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupBySortLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"brand_name\", \"gender\", sum(\"unit_sales\") as s\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"brand_name\", \"gender\"\n"
operator|+
literal|"order by s desc limit 3"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'brand_name','outputName':'brand_name','outputType':'STRING'},"
operator|+
literal|"{'type':'default','dimension':'gender','outputName':'gender','outputType':'STRING'}],"
operator|+
literal|"'limitSpec':{'type':'default','limit':3,'columns':[{'dimension':'S',"
operator|+
literal|"'direction':'descending','dimensionOrder':'numeric'}]},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'S','fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$2, $39, $89]], groups=[{0, 1}], "
operator|+
literal|"aggs=[[SUM($2)]], sort0=[2], dir0=[DESC], fetch=[3])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsOrdered
argument_list|(
literal|"brand_name=Hermanos; gender=M; S=4286"
argument_list|,
literal|"brand_name=Hermanos; gender=F; S=4183"
argument_list|,
literal|"brand_name=Tell Tale; gender=F; S=4033"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1587">[CALCITE-1587]    * Druid adapter: topN returns approximate results</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupBySingleSortLimit
parameter_list|()
block|{
name|checkGroupBySingleSortLimit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testGroupBySingleSortLimit}, but allowing approximate results    * due to {@link CalciteConnectionConfig#approximateDistinctCount()}.    * Therefore we send a "topN" query to Druid. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupBySingleSortLimitApprox
parameter_list|()
block|{
name|checkGroupBySingleSortLimit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkGroupBySingleSortLimit
parameter_list|(
name|boolean
name|approx
parameter_list|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"brand_name\", sum(\"unit_sales\") as s\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"brand_name\"\n"
operator|+
literal|"order by s desc limit 3"
decl_stmt|;
specifier|final
name|String
name|approxDruid
init|=
literal|"{'queryType':'topN','dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimension':{'type':'default','dimension':'brand_name','outputName':'brand_name','outputType':'STRING'},'metric':'S',"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'S','fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'threshold':3}"
decl_stmt|;
specifier|final
name|String
name|exactDruid
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'brand_name','outputName':'brand_name',"
operator|+
literal|"'outputType':'STRING'}],'limitSpec':{'type':'default','limit':3,'columns':"
operator|+
literal|"[{'dimension':'S','direction':'descending','dimensionOrder':'numeric'}]},'aggregations':"
operator|+
literal|"[{'type':'longSum','name':'S','fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
name|approx
condition|?
name|approxDruid
else|:
name|exactDruid
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$2, $89]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($1)]], sort0=[1], dir0=[DESC], fetch=[3])"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|FOODMART
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|APPROXIMATE_TOP_N
operator|.
name|name
argument_list|()
argument_list|,
name|approx
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsOrdered
argument_list|(
literal|"brand_name=Hermanos; S=8469"
argument_list|,
literal|"brand_name=Tell Tale; S=7877"
argument_list|,
literal|"brand_name=Ebony; S=7438"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1578">[CALCITE-1578]    * Druid adapter: wrong semantics of groupBy query limit with granularity</a>.    *    *<p>Before CALCITE-1578 was fixed, this would use a "topN" query but return    * the wrong results. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByDaySortDescLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"brand_name\","
operator|+
literal|" floor(\"timestamp\" to DAY) as d,"
operator|+
literal|" sum(\"unit_sales\") as s\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"brand_name\", floor(\"timestamp\" to DAY)\n"
operator|+
literal|"order by s desc limit 30"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$2, FLOOR($0, FLAG(DAY)), $89]], "
operator|+
literal|"groups=[{0, 1}], aggs=[[SUM($2)]], sort0=[2], dir0=[DESC], fetch=[30])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsStartingWith
argument_list|(
literal|"brand_name=Ebony; D=1997-07-27 00:00:00; S=135"
argument_list|,
literal|"brand_name=Tri-State; D=1997-05-09 00:00:00; S=120"
argument_list|,
literal|"brand_name=Hermanos; D=1997-05-09 00:00:00; S=115"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|,
literal|"'granularity':'all'"
argument_list|,
literal|"'limitSpec"
operator|+
literal|"':{'type':'default','limit':30,'columns':[{'dimension':'S',"
operator|+
literal|"'direction':'descending','dimensionOrder':'numeric'}]}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1579">[CALCITE-1579]    * Druid adapter: wrong semantics of groupBy query limit with    * granularity</a>.    *    *<p>Before CALCITE-1579 was fixed, this would use a "groupBy" query but    * wrongly try to use a {@code limitSpec} to sort and filter. (A "topN" query    * was not possible because the sort was {@code ASC}.) */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByDaySortLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"brand_name\","
operator|+
literal|" floor(\"timestamp\" to DAY) as d,"
operator|+
literal|" sum(\"unit_sales\") as s\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"brand_name\", floor(\"timestamp\" to DAY)\n"
operator|+
literal|"order by s desc limit 30"
decl_stmt|;
specifier|final
name|String
name|druidQueryPart1
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart'"
decl_stmt|;
specifier|final
name|String
name|druidQueryPart2
init|=
literal|"'limitSpec':{'type':'default','limit':30,"
operator|+
literal|"'columns':[{'dimension':'S','direction':'descending',"
operator|+
literal|"'dimensionOrder':'numeric'}]},'aggregations':[{'type':'longSum',"
operator|+
literal|"'name':'S','fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$2, FLOOR($0, FLAG(DAY)), $89]], groups=[{0, 1}], "
operator|+
literal|"aggs=[[SUM($2)]], sort0=[2], dir0=[DESC], fetch=[30])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsStartingWith
argument_list|(
literal|"brand_name=Ebony; D=1997-07-27 00:00:00; S=135"
argument_list|,
literal|"brand_name=Tri-State; D=1997-05-09 00:00:00; S=120"
argument_list|,
literal|"brand_name=Hermanos; D=1997-05-09 00:00:00; S=115"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQueryPart1
argument_list|,
name|druidQueryPart2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1580">[CALCITE-1580]    * Druid adapter: Wrong semantics for ordering within groupBy queries</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByDaySortDimension
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"brand_name\", floor(\"timestamp\" to DAY) as d,"
operator|+
literal|" sum(\"unit_sales\") as s\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"brand_name\", floor(\"timestamp\" to DAY)\n"
operator|+
literal|"order by \"brand_name\""
decl_stmt|;
specifier|final
name|String
name|subDruidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'brand_name','outputName':'brand_name','outputType':'STRING'},"
operator|+
literal|"{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'floor_day','extractionFn':{'type':'timeFormat'"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$2, FLOOR($0, FLAG(DAY)), $89]], groups=[{0, 1}],"
operator|+
literal|" aggs=[[SUM($2)]], sort0=[0], dir0=[ASC])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsStartingWith
argument_list|(
literal|"brand_name=ADJ; D=1997-01-11 00:00:00; S=2"
argument_list|,
literal|"brand_name=ADJ; D=1997-01-12 00:00:00; S=3"
argument_list|,
literal|"brand_name=ADJ; D=1997-01-17 00:00:00; S=3"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|subDruidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a query that contains no GROUP BY and is therefore executed as a    * Druid "select" query. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterSortDesc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"product_name\" from \"foodmart\"\n"
operator|+
literal|"where \"product_id\" BETWEEN '1500' AND '1502'\n"
operator|+
literal|"order by \"state_province\" desc, \"product_id\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan','dataSource':'foodmart',"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'filter':{'type':'and','fields':["
operator|+
literal|"{'type':'bound','dimension':'product_id','lower':'1500','lowerStrict':false,'ordering':'lexicographic'},"
operator|+
literal|"{'type':'bound','dimension':'product_id','upper':'1502','upperStrict':false,'ordering':'lexicographic'}]},"
operator|+
literal|"'columns':['product_name','state_province','product_id'],"
operator|+
literal|"'resultFormat':'compactedList'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
operator|.
name|returns
argument_list|(
operator|new
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
literal|"product_name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Fort West Dried Apricots"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterSortDesc()} but the bounds are numeric. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterSortDescNumeric
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"product_name\" from \"foodmart\"\n"
operator|+
literal|"where \"product_id\" BETWEEN 1500 AND 1502\n"
operator|+
literal|"order by \"state_province\" desc, \"product_id\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan','dataSource':'foodmart',"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'filter':{'type':'and','fields':["
operator|+
literal|"{'type':'bound','dimension':'product_id','lower':'1500','lowerStrict':false,'ordering':'numeric'},"
operator|+
literal|"{'type':'bound','dimension':'product_id','upper':'1502','upperStrict':false,'ordering':'numeric'}]},"
operator|+
literal|"'columns':['product_name','state_province','product_id'],"
operator|+
literal|"'resultFormat':'compactedList'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
operator|.
name|returns
argument_list|(
operator|new
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
literal|"product_name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Fort West Dried Apricots"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a query whose filter removes all rows. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterOutEverything
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"product_name\" from \"foodmart\"\n"
operator|+
literal|"where \"product_id\" = -1"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan','dataSource':'foodmart',"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'filter':{'type':'bound','dimension':'product_id','lower':'-1','lowerStrict':false,"
operator|+
literal|"'upper':'-1','upperStrict':false,'ordering':'numeric'},"
operator|+
literal|"'columns':['product_name'],"
operator|+
literal|"'resultFormat':'compactedList'}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
operator|.
name|returnsUnordered
argument_list|()
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testFilterSortDescNumeric()} but with a filter that cannot    * be pushed down to Druid. */
annotation|@
name|Test
specifier|public
name|void
name|testNonPushableFilterSortDesc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"product_name\" from \"foodmart\"\n"
operator|+
literal|"where cast(\"product_id\" as integer) - 1500 BETWEEN 0 AND 2\n"
operator|+
literal|"order by \"state_province\" desc, \"product_id\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan','dataSource':'foodmart',"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
decl_stmt|;
specifier|final
name|String
name|druidFilter
init|=
literal|"\"filter\":{\"type\":\"and\","
operator|+
literal|"\"fields\":[{\"type\":\"expression\",\"expression\":\"((CAST(\\\"product_id\\\""
decl_stmt|;
specifier|final
name|String
name|druidQuery2
init|=
literal|"'columns':['product_name','state_province','product_id'],"
operator|+
literal|"'resultFormat':'compactedList'}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
operator|.
name|returns
argument_list|(
operator|new
name|Function
argument_list|<
name|ResultSet
argument_list|,
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|assertTrue
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
literal|"product_name"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"Fort West Dried Apricots"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|resultSet
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|,
name|druidFilter
argument_list|,
name|druidQuery2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnionPlan
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"gender\" from \"foodmart\"\n"
operator|+
literal|"union all\n"
operator|+
literal|"select distinct \"marital_status\" from \"foodmart\""
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableUnion(all=[true])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{39}], aggs=[[]])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{37}], aggs=[[]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"gender=F"
argument_list|,
literal|"gender=M"
argument_list|,
literal|"gender=M"
argument_list|,
literal|"gender=S"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterUnionPlan
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from (\n"
operator|+
literal|"  select distinct \"gender\" from \"foodmart\"\n"
operator|+
literal|"  union all\n"
operator|+
literal|"  select distinct \"marital_status\" from \"foodmart\")\n"
operator|+
literal|"where \"gender\" = 'M'"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableFilter(condition=[=($0, 'M')])\n"
operator|+
literal|"    BindableUnion(all=[true])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{39}], aggs=[[]])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{37}], aggs=[[]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"gender=M"
argument_list|,
literal|"gender=M"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountGroupByEmpty
parameter_list|()
block|{
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'granularity':'all',"
operator|+
literal|"'aggregations':[{'type':'count','name':'EXPR$0'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[0]], groups=[{}], aggs=[[COUNT()]])"
decl_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select count(*) from \"foodmart\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=86829\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByOneColumnNotProjected
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\" order by 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"C=21610"
argument_list|,
literal|"C=24441"
argument_list|,
literal|"C=40778"
argument_list|)
expr_stmt|;
block|}
comment|/** Unlike {@link #testGroupByTimeAndOneColumnNotProjected()}, we cannot use    * "topN" because we have a global limit, and that requires    * {@code granularity: all}. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByTimeAndOneColumnNotProjectedWithLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as \"c\","
operator|+
literal|" floor(\"timestamp\" to MONTH) as \"month\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH), \"state_province\"\n"
operator|+
literal|"order by \"c\" desc limit 3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"c=4070; month=1997-12-01 00:00:00"
argument_list|,
literal|"c=4033; month=1997-11-01 00:00:00"
argument_list|,
literal|"c=3511; month=1997-07-01 00:00:00"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByTimeAndOneMetricNotProjected
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as \"c\", floor(\"timestamp\" to MONTH) as \"month\", floor"
operator|+
literal|"(\"store_sales\") as sales\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH), \"state_province\", floor"
operator|+
literal|"(\"store_sales\")\n"
operator|+
literal|"order by \"c\" desc limit 3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"c=494; month=1997-11-01 00:00:00; SALES=5.0"
argument_list|,
literal|"c=475; month=1997-12-01 00:00:00; SALES=5.0"
argument_list|,
literal|"c=468; month=1997-03-01 00:00:00; SALES=5.0"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByTimeAndOneColumnNotProjected
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as \"c\",\n"
operator|+
literal|"  floor(\"timestamp\" to MONTH) as \"month\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH), \"state_province\"\n"
operator|+
literal|"having count(*)> 3500"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"c=3511; month=1997-07-01 00:00:00"
argument_list|,
literal|"c=4033; month=1997-11-01 00:00:00"
argument_list|,
literal|"c=4070; month=1997-12-01 00:00:00"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOneColumnNotProjected
parameter_list|()
block|{
comment|// Result including state: CA=24441, OR=21610, WA=40778
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\" order by \"state_province\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"C=24441"
argument_list|,
literal|"C=21610"
argument_list|,
literal|"C=40778"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByOneColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"state_province\", count(*) as c\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\"\n"
operator|+
literal|"order by \"state_province\""
decl_stmt|;
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$30]], groups=[{0}], "
operator|+
literal|"aggs=[[COUNT()]], sort0=[0], dir0=[ASC])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"state_province=CA; C=24441"
argument_list|,
literal|"state_province=OR; C=21610"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByOneColumnReversed
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c, \"state_province\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\"\n"
operator|+
literal|"order by \"state_province\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"C=24441; state_province=CA"
argument_list|,
literal|"C=21610; state_province=OR"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByAvgSumCount
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"state_province\",\n"
operator|+
literal|" avg(\"unit_sales\") as a,\n"
operator|+
literal|" sum(\"unit_sales\") as s,\n"
operator|+
literal|" count(\"store_sqft\") as c,\n"
operator|+
literal|" count(*) as c0\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\"\n"
operator|+
literal|"order by 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state_province=CA; A=3; S=74748; C=16347; C0=24441"
argument_list|,
literal|"state_province=OR; A=3; S=67659; C=21610; C0=21610"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(state_province=[$0], A=[CAST(/(CASE(=($2, 0), null, $1), $2)):BIGINT],"
operator|+
literal|" S=[CASE(=($2, 0), null, $1)], C=[$3], C0=[$4])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$30, $89, $71]], groups=[{0}], "
operator|+
literal|"aggs=[[$SUM0($1), COUNT($1), COUNT($2), COUNT()]], sort0=[0], dir0=[ASC])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all'"
operator|+
literal|",'dimensions':[{'type':'default','dimension':'state_province','outputName':'state_province'"
operator|+
literal|",'outputType':'STRING'}],'limitSpec':"
operator|+
literal|"{'type':'default','columns':[{'dimension':'state_province',"
operator|+
literal|"'direction':'ascending','dimensionOrder':'lexicographic'}]},'aggregations':"
operator|+
literal|"[{'type':'longSum','name':'$f1','fieldName':'unit_sales'},{'type':'filtered',"
operator|+
literal|"'filter':{'type':'not','field':{'type':'selector','dimension':'unit_sales',"
operator|+
literal|"'value':null}},'aggregator':{'type':'count','name':'$f2','fieldName':'unit_sales'}}"
operator|+
literal|",{'type':'filtered','filter':{'type':'not','field':{'type':'selector',"
operator|+
literal|"'dimension':'store_sqft','value':null}},'aggregator':{'type':'count','name':'C',"
operator|+
literal|"'fieldName':'store_sqft'}},{'type':'count','name':'C0'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMonthGranularity
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(\"unit_sales\") as s,\n"
operator|+
literal|" count(\"store_sqft\") as c\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH) order by s"
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|3
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(S=[$1], C=[$2])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[FLOOR($0, FLAG(MONTH)), $89, $71]], "
operator|+
literal|"groups=[{0}], aggs=[[SUM($1), COUNT($2)]], sort0=[1], dir0=[ASC])"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"S=19958; C=5606"
argument_list|,
literal|"S=20179; C=5523"
argument_list|,
literal|"S=20388; C=5591"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1577">[CALCITE-1577]    * Druid adapter: Incorrect result - limit on timestamp disappears</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMonthGranularitySort
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(\"unit_sales\") as s,\n"
operator|+
literal|" count(\"store_sqft\") as c\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH)\n"
operator|+
literal|"order by floor(\"timestamp\" to MONTH) ASC"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(S=[$1], C=[$2], EXPR$2=[$0])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], projects=[[FLOOR($0, "
operator|+
literal|"FLAG(MONTH)), $89, $71]], groups=[{0}], aggs=[[SUM($1), COUNT($2)]], sort0=[0], "
operator|+
literal|"dir0=[ASC])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"S=21628; C=5957"
argument_list|,
literal|"S=20957; C=5842"
argument_list|,
literal|"S=23706; C=6528"
argument_list|,
literal|"S=20179; C=5523"
argument_list|,
literal|"S=21081; C=5793"
argument_list|,
literal|"S=21350; C=5863"
argument_list|,
literal|"S=23763; C=6762"
argument_list|,
literal|"S=21697; C=5915"
argument_list|,
literal|"S=20388; C=5591"
argument_list|,
literal|"S=19958; C=5606"
argument_list|,
literal|"S=25270; C=7026"
argument_list|,
literal|"S=26796; C=7338"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMonthGranularitySortLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select floor(\"timestamp\" to MONTH) as m,\n"
operator|+
literal|" sum(\"unit_sales\") as s,\n"
operator|+
literal|" count(\"store_sqft\") as c\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH)\n"
operator|+
literal|"order by floor(\"timestamp\" to MONTH) limit 3"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[FLOOR($0, FLAG(MONTH)), $89, $71]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($1), COUNT($2)]], sort0=[0], dir0=[ASC], fetch=[3])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"M=1997-01-01 00:00:00; S=21628; C=5957"
argument_list|,
literal|"M=1997-02-01 00:00:00; S=20957; C=5842"
argument_list|,
literal|"M=1997-03-01 00:00:00; S=23706; C=6528"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByDayGranularity
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(\"unit_sales\") as s,\n"
operator|+
literal|" count(\"store_sqft\") as c\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to DAY) order by c desc"
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|3
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"S=3850; C=1230"
argument_list|,
literal|"S=3342; C=1071"
argument_list|,
literal|"S=3219; C=1024"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMonthGranularityFiltered
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(\"unit_sales\") as s,\n"
operator|+
literal|" count(\"store_sqft\") as c\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"where \"timestamp\">= '1996-01-01 00:00:00' and "
operator|+
literal|" \"timestamp\"< '1998-01-01 00:00:00'\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH) order by s asc"
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|3
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"S=19958; C=5606"
argument_list|,
literal|"S=20179; C=5523"
argument_list|,
literal|"S=20388; C=5591"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTopNMonthGranularity
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(\"unit_sales\") as s,\n"
operator|+
literal|"max(\"unit_sales\") as m,\n"
operator|+
literal|"\"state_province\" as p\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\", floor(\"timestamp\" to MONTH)\n"
operator|+
literal|"order by s desc limit 3"
decl_stmt|;
comment|// Cannot use a Druid "topN" query, granularity != "all";
comment|// have to use "groupBy" query followed by external Sort and fetch.
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableCalc(expr#0..3=[{inputs}], S=[$t2], M=[$t3], P=[$t0])\n"
operator|+
literal|"  EnumerableInterpreter\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], projects=[[$30, FLOOR"
operator|+
literal|"($0, FLAG(MONTH)), $89]], groups=[{0, 1}], aggs=[[SUM($2), MAX($2)]], sort0=[2], "
operator|+
literal|"dir0=[DESC], fetch=[3])"
decl_stmt|;
specifier|final
name|String
name|druidQueryPart1
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'state_province',\"outputName\":\"state_province\",\"outputType\":\"STRING\"},"
operator|+
literal|"{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'floor_month','extractionFn':{'type':'timeFormat','format'"
decl_stmt|;
specifier|final
name|String
name|druidQueryPart2
init|=
literal|"'limitSpec':{'type':'default','limit':3,"
operator|+
literal|"'columns':[{'dimension':'S','direction':'descending',"
operator|+
literal|"'dimensionOrder':'numeric'}]},'aggregations':[{'type':'longSum',"
operator|+
literal|"'name':'S','fieldName':'unit_sales'},{'type':'longMax','name':'M',"
operator|+
literal|"'fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"S=12399; M=6; P=WA"
argument_list|,
literal|"S=12297; M=7; P=WA"
argument_list|,
literal|"S=10640; M=6; P=WA"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQueryPart1
argument_list|,
name|druidQueryPart2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTopNDayGranularityFiltered
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(\"unit_sales\") as s,\n"
operator|+
literal|"max(\"unit_sales\") as m,\n"
operator|+
literal|"\"state_province\" as p\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"where \"timestamp\">= '1997-01-01 00:00:00' and "
operator|+
literal|" \"timestamp\"< '1997-09-01 00:00:00'\n"
operator|+
literal|"group by \"state_province\", floor(\"timestamp\" to DAY)\n"
operator|+
literal|"order by s desc limit 6"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(S=[$2], M=[$3], P=[$0])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1997-01-01T00:00:00.000Z/1997-09-01T00:00:00.000Z]], projects=[[$30, FLOOR"
operator|+
literal|"($0, FLAG(DAY)), $89]], groups=[{0, 1}], aggs=[[SUM($2), MAX($2)]], sort0=[2], "
operator|+
literal|"dir0=[DESC], fetch=[6])"
decl_stmt|;
specifier|final
name|String
name|druidQueryType
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions'"
decl_stmt|;
specifier|final
name|String
name|limitSpec
init|=
literal|"'limitSpec':{'type':'default','limit':6,"
operator|+
literal|"'columns':[{'dimension':'S','direction':'descending','dimensionOrder':'numeric'}]}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"S=2527; M=5; P=OR"
argument_list|,
literal|"S=2525; M=6; P=OR"
argument_list|,
literal|"S=2238; M=6; P=OR"
argument_list|,
literal|"S=1715; M=5; P=OR"
argument_list|,
literal|"S=1691; M=5; P=OR"
argument_list|,
literal|"S=1629; M=5; P=WA"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQueryType
argument_list|,
name|limitSpec
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByHaving
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"state_province\" as s, count(*) as c\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\" having count(*)> 23000 order by 1"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$30]], groups=[{0}], aggs=[[COUNT()]], "
operator|+
literal|"filter=[>($1, 23000)], sort0=[0], dir0=[ASC])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"S=CA; C=24441"
argument_list|,
literal|"S=WA; C=40778"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupComposite
parameter_list|()
block|{
comment|// Note: We don't push down SORT-LIMIT yet
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c, \"state_province\", \"city\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\", \"city\"\n"
operator|+
literal|"order by c desc limit 2"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"BindableProject(C=[$2], state_province=[$0], city=[$1])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], projects=[[$30, $29]], groups=[{0, 1}], aggs=[[COUNT()]], sort0=[2], dir0=[DESC], fetch=[2])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"C=7394; state_province=WA; city=Spokane"
argument_list|,
literal|"C=3958; state_province=WA; city=Olympia"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that distinct-count is pushed down to Druid and evaluated using    * "cardinality". The result is approximate, but gives the correct result in    * this example when rounded down using FLOOR. */
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCount
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"state_province\",\n"
operator|+
literal|" floor(count(distinct \"city\")) as cdc\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by \"state_province\"\n"
operator|+
literal|"order by 2 desc limit 2"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$1], dir0=[DESC], fetch=[2])\n"
operator|+
literal|"    BindableProject(state_province=[$0], CDC=[FLOOR($1)])\n"
operator|+
literal|"      BindableAggregate(group=[{1}], agg#0=[COUNT($0)])\n"
operator|+
literal|"        DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{29, 30}], "
operator|+
literal|"aggs=[[]])"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default','dimension':'city','outputName':'city'"
operator|+
literal|",'outputType':'STRING'},"
operator|+
literal|"{'type':'default','dimension':'state_province','outputName':'state_province','outputType':'STRING'}],"
operator|+
literal|"'limitSpec':{'type':'default'},'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state_province=CA; CDC=45"
argument_list|,
literal|"state_province=WA; CDC=22"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that projections of columns are pushed into the DruidQuery, and    * projections of expressions that Druid cannot handle (in this case, a    * literal 0) stay up. */
annotation|@
name|Test
specifier|public
name|void
name|testProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"product_name\", 0 as zero\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"order by \"product_name\""
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], projects=[[$3, 0]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_name=ADJ Rosy Sunglasses; ZERO=0"
argument_list|,
literal|"product_name=ADJ Rosy Sunglasses; ZERO=0"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"state_province\", \"city\",\n"
operator|+
literal|"  \"product_name\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"where \"product_name\" = 'High Top Dried Mushrooms'\n"
operator|+
literal|"and \"quarter\" in ('Q2', 'Q3')\n"
operator|+
literal|"and \"state_province\" = 'WA'"
decl_stmt|;
specifier|final
name|String
name|druidQuery1
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all'"
decl_stmt|;
specifier|final
name|String
name|druidQuery2
init|=
literal|"'filter':{'type':'and','fields':[{'type':'selector','dimension':"
operator|+
literal|"'product_name','value':'High Top Dried Mushrooms'},{'type':'or','fields':[{'type':'selector',"
operator|+
literal|"'dimension':'quarter','value':'Q2'},{'type':'selector','dimension':'quarter',"
operator|+
literal|"'value':'Q3'}]},{'type':'selector','dimension':'state_province','value':'WA'}]},"
operator|+
literal|"'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]],"
operator|+
literal|" filter=[AND(=($3, 'High Top Dried Mushrooms'),"
operator|+
literal|" OR(=($87, 'Q2'),"
operator|+
literal|" =($87, 'Q3')),"
operator|+
literal|" =($30, 'WA'))],"
operator|+
literal|" projects=[[$30, $29, $3]], groups=[{0, 1, 2}], aggs=[[]])\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery1
argument_list|,
name|druidQuery2
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state_province=WA; city=Bremerton; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Everett; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Kirkland; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Lynnwood; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Olympia; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Port Orchard; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Puyallup; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Spokane; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Tacoma; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Yakima; product_name=High Top Dried Mushrooms"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"state_province\", \"city\",\n"
operator|+
literal|"  \"product_name\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"where \"product_name\" = 'High Top Dried Mushrooms'\n"
operator|+
literal|"and \"quarter\" in ('Q2', 'Q3')\n"
operator|+
literal|"and \"state_province\" = 'WA'"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan',"
operator|+
literal|"'dataSource':'foodmart',"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'filter':{'type':'and','fields':["
operator|+
literal|"{'type':'selector','dimension':'product_name','value':'High Top Dried Mushrooms'},"
operator|+
literal|"{'type':'or','fields':["
operator|+
literal|"{'type':'selector','dimension':'quarter','value':'Q2'},"
operator|+
literal|"{'type':'selector','dimension':'quarter','value':'Q3'}]},"
operator|+
literal|"{'type':'selector','dimension':'state_province','value':'WA'}]},"
operator|+
literal|"'columns':['state_province','city','product_name'],"
operator|+
literal|"'resultFormat':'compactedList'}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[AND(=($3, 'High Top Dried Mushrooms'), "
operator|+
literal|"OR(=($87, 'Q2'), =($87, 'Q3')), =($30, 'WA'))], "
operator|+
literal|"projects=[[$30, $29, $3]])\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state_province=WA; city=Bremerton; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Everett; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Kirkland; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Lynnwood; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Olympia; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Port Orchard; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Puyallup; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Puyallup; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Spokane; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Spokane; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Spokane; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Tacoma; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Yakima; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Yakima; product_name=High Top Dried Mushrooms"
argument_list|,
literal|"state_province=WA; city=Yakima; product_name=High Top Dried Mushrooms"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that conditions applied to time units extracted via the EXTRACT    * function become ranges on the timestamp column    *    *<p>Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1334">[CALCITE-1334]    * Convert predicates on EXTRACT function calls into date ranges</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterTimestamp
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select count(*) as c\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"where extract(year from \"timestamp\") = 1997\n"
operator|+
literal|"and extract(month from \"timestamp\") in (4, 6)\n"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1997-04-01T00:00:00.000Z/"
operator|+
literal|"1997-05-01T00:00:00.000Z, 1997-06-01T00:00:00.000Z/1997-07-01T00:00:00.000Z]],"
operator|+
literal|" projects=[[0]], groups=[{}], aggs=[[COUNT()]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=13500"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterSwapped
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"state_province\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"where 'High Top Dried Mushrooms' = \"product_name\""
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[=('High Top Dried Mushrooms', $3)], projects=[[$30]])"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"'filter':{'type':'selector','dimension':'product_name',"
operator|+
literal|"'value':'High Top Dried Mushrooms'}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByMetricAndExtractTime
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(*), floor(\"timestamp\" to DAY), \"store_sales\" "
operator|+
literal|"FROM \"foodmart\"\n"
operator|+
literal|"GROUP BY \"store_sales\", floor(\"timestamp\" to DAY)\n ORDER BY \"store_sales\" DESC\n"
operator|+
literal|"LIMIT 10\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"groupBy\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterOnDouble
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"product_id\" from \"foodmart\"\n"
operator|+
literal|"where cast(\"product_id\" as double)< 0.41024 and \"product_id\"< 12223"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'type':'bound','dimension':'product_id','upper':'0.41024'"
argument_list|,
literal|"'upper':'12223'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushAggregateOnTime
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"product_id\", \"timestamp\" as \"time\" "
operator|+
literal|"from \"foodmart\" "
operator|+
literal|"where \"product_id\" = 1016 "
operator|+
literal|"and \"timestamp\"< '1997-01-03 00:00:00' "
operator|+
literal|"and \"timestamp\"> '1990-01-01 00:00:00' "
operator|+
literal|"group by \"timestamp\", \"product_id\" "
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'yyyy-MM-dd"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_id=1016; time=1997-01-02 00:00:00"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushAggregateOnTimeWithExtractYear
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select EXTRACT( year from \"timestamp\") as \"year\",\"product_id\" from "
operator|+
literal|"\"foodmart\" where \"product_id\" = 1016 and "
operator|+
literal|"\"timestamp\"< cast('1999-01-02' as timestamp) and \"timestamp\"> cast"
operator|+
literal|"('1997-01-01' as timestamp)"
operator|+
literal|" group by "
operator|+
literal|" EXTRACT( year from \"timestamp\"), \"product_id\" "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|",'granularity':'all'"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_year',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'yyyy',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"year=1997; product_id=1016"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushAggregateOnTimeWithExtractMonth
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select EXTRACT( month from \"timestamp\") as \"month\",\"product_id\" from "
operator|+
literal|"\"foodmart\" where \"product_id\" = 1016 and "
operator|+
literal|"\"timestamp\"< cast('1997-06-02' as timestamp) and \"timestamp\"> cast"
operator|+
literal|"('1997-01-01' as timestamp)"
operator|+
literal|" group by "
operator|+
literal|" EXTRACT( month from \"timestamp\"), \"product_id\" "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|",'granularity':'all'"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_month',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'M',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"month=1; product_id=1016"
argument_list|,
literal|"month=2; product_id=1016"
argument_list|,
literal|"month=3; product_id=1016"
argument_list|,
literal|"month=4; product_id=1016"
argument_list|,
literal|"month=5; product_id=1016"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushAggregateOnTimeWithExtractDay
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select EXTRACT( day from \"timestamp\") as \"day\","
operator|+
literal|"\"product_id\" from \"foodmart\""
operator|+
literal|" where \"product_id\" = 1016 and "
operator|+
literal|"\"timestamp\"< cast('1997-01-20' as timestamp) and \"timestamp\"> cast"
operator|+
literal|"('1997-01-01' as timestamp)"
operator|+
literal|" group by "
operator|+
literal|" EXTRACT( day from \"timestamp\"), \"product_id\" "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|",'granularity':'all'"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_day',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'d',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"day=2; product_id=1016"
argument_list|,
literal|"day=10; product_id=1016"
argument_list|,
literal|"day=13; product_id=1016"
argument_list|,
literal|"day=16; product_id=1016"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushAggregateOnTimeWithExtractHourOfDay
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select EXTRACT( hour from \"timestamp\") as \"hourOfDay\",\"product_id\"  from "
operator|+
literal|"\"foodmart\" where \"product_id\" = 1016 and "
operator|+
literal|"\"timestamp\"< cast('1997-06-02' as timestamp) and \"timestamp\"> cast"
operator|+
literal|"('1997-01-01' as timestamp)"
operator|+
literal|" group by "
operator|+
literal|" EXTRACT( hour from \"timestamp\"), \"product_id\" "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"hourOfDay=0; product_id=1016"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushAggregateOnTimeWithExtractYearMonthDay
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select EXTRACT( day from \"timestamp\") as \"day\", EXTRACT( month from "
operator|+
literal|"\"timestamp\") as \"month\",  EXTRACT( year from \"timestamp\") as \"year\",\""
operator|+
literal|"product_id\"  from \"foodmart\" where \"product_id\" = 1016 and "
operator|+
literal|"\"timestamp\"< cast('1997-01-20' as timestamp) and \"timestamp\"> cast"
operator|+
literal|"('1997-01-01' as timestamp)"
operator|+
literal|" group by "
operator|+
literal|" EXTRACT( day from \"timestamp\"), EXTRACT( month from \"timestamp\"),"
operator|+
literal|" EXTRACT( year from \"timestamp\"), \"product_id\" "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|",'granularity':'all'"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_day',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'d',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_month',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'M',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_year',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'yyyy',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1997-01-01T00:00:00.001Z/1997-01-20T00:00:00.000Z]], "
operator|+
literal|"filter=[=($1, 1016)], projects=[[EXTRACT(FLAG(DAY), $0), EXTRACT(FLAG(MONTH), $0), "
operator|+
literal|"EXTRACT(FLAG(YEAR), $0), $1]], groups=[{0, 1, 2, 3}], aggs=[[]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"day=2; month=1; year=1997; product_id=1016"
argument_list|,
literal|"day=10; month=1; year=1997; product_id=1016"
argument_list|,
literal|"day=13; month=1; year=1997; product_id=1016"
argument_list|,
literal|"day=16; month=1; year=1997; product_id=1016"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushAggregateOnTimeWithExtractYearMonthDayWithOutRenaming
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select EXTRACT( day from \"timestamp\"), EXTRACT( month from "
operator|+
literal|"\"timestamp\"), EXTRACT( year from \"timestamp\"),\""
operator|+
literal|"product_id\"  from \"foodmart\" where \"product_id\" = 1016 and "
operator|+
literal|"\"timestamp\"< cast('1997-01-20' as timestamp) and \"timestamp\"> cast"
operator|+
literal|"('1997-01-01' as timestamp)"
operator|+
literal|" group by "
operator|+
literal|" EXTRACT( day from \"timestamp\"), EXTRACT( month from \"timestamp\"),"
operator|+
literal|" EXTRACT( year from \"timestamp\"), \"product_id\" "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|",'granularity':'all'"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_day',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'d',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_month',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'M',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_year',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'yyyy',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1997-01-01T00:00:00.001Z/1997-01-20T00:00:00.000Z]], "
operator|+
literal|"filter=[=($1, 1016)], projects=[[EXTRACT(FLAG(DAY), $0), EXTRACT(FLAG(MONTH), $0), "
operator|+
literal|"EXTRACT(FLAG(YEAR), $0), $1]], groups=[{0, 1, 2, 3}], aggs=[[]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=2; EXPR$1=1; EXPR$2=1997; product_id=1016"
argument_list|,
literal|"EXPR$0=10; EXPR$1=1; EXPR$2=1997; product_id=1016"
argument_list|,
literal|"EXPR$0=13; EXPR$1=1; EXPR$2=1997; product_id=1016"
argument_list|,
literal|"EXPR$0=16; EXPR$1=1; EXPR$2=1997; product_id=1016"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushAggregateOnTimeWithExtractWithOutRenaming
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select EXTRACT( day from \"timestamp\"), "
operator|+
literal|"\"product_id\" as \"dayOfMonth\" from \"foodmart\" "
operator|+
literal|"where \"product_id\" = 1016 and \"timestamp\"< cast('1997-01-20' as timestamp) "
operator|+
literal|"and \"timestamp\"> cast('1997-01-01' as timestamp)"
operator|+
literal|" group by "
operator|+
literal|" EXTRACT( day from \"timestamp\"), EXTRACT( day from \"timestamp\"),"
operator|+
literal|" \"product_id\" "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|",'granularity':'all'"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_day',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'d',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1997-01-01T00:00:00.001Z/1997-01-20T00:00:00.000Z]], "
operator|+
literal|"filter=[=($1, 1016)], projects=[[EXTRACT(FLAG(DAY), $0), $1]], "
operator|+
literal|"groups=[{0, 1}], aggs=[[]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=2; dayOfMonth=1016"
argument_list|,
literal|"EXPR$0=10; dayOfMonth=1016"
argument_list|,
literal|"EXPR$0=13; dayOfMonth=1016"
argument_list|,
literal|"EXPR$0=16; dayOfMonth=1016"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushComplexFilter
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") from \"foodmart\" "
operator|+
literal|"where EXTRACT( year from \"timestamp\") = 1997 and "
operator|+
literal|"\"cases_per_pallet\">= 8 and \"cases_per_pallet\"<= 10 and "
operator|+
literal|"\"units_per_case\"< 15 "
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','filter':{'type':'and','fields':[{'type':'bound','dimension':"
operator|+
literal|"'cases_per_pallet','lower':'8','lowerStrict':false,'ordering':'numeric'},"
operator|+
literal|"{'type':'bound','dimension':'cases_per_pallet','upper':'10','upperStrict':false,"
operator|+
literal|"'ordering':'numeric'},{'type':'bound','dimension':'units_per_case','upper':'15',"
operator|+
literal|"'upperStrict':true,'ordering':'numeric'}]},'aggregations':[{'type':'doubleSum',"
operator|+
literal|"'name':'EXPR$0','fieldName':'store_sales'}],'intervals':['1997-01-01T00:00:00.000Z/"
operator|+
literal|"1998-01-01T00:00:00.000Z'],'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1997-01-01T00:00:00.000Z/1998-01-01T00:00:00.000Z]], "
operator|+
literal|"filter=[AND(>=(CAST($11):BIGINT, 8),<=(CAST($11):BIGINT, 10), "
operator|+
literal|"<(CAST($10):BIGINT, 15))], groups=[{}], "
operator|+
literal|"aggs=[[SUM($90)]])\n"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=75364.1"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushOfFilterExtractionOnDayAndMonth
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT \"product_id\" , EXTRACT(day from \"timestamp\"), EXTRACT(month from "
operator|+
literal|"\"timestamp\") from \"foodmart\" WHERE  EXTRACT(day from \"timestamp\")>= 30 AND "
operator|+
literal|"EXTRACT(month from \"timestamp\") = 11 "
operator|+
literal|"AND  \"product_id\">= 1549 group by \"product_id\", EXTRACT(day from "
operator|+
literal|"\"timestamp\"), EXTRACT(month from \"timestamp\")"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_id=1549; EXPR$1=30; EXPR$2=11"
argument_list|,
literal|"product_id=1553; EXPR$1=30; EXPR$2=11"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushOfFilterExtractionOnDayAndMonthAndYear
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT \"product_id\" , EXTRACT(day from \"timestamp\"), EXTRACT(month from "
operator|+
literal|"\"timestamp\") , EXTRACT(year from \"timestamp\") from \"foodmart\" "
operator|+
literal|"WHERE  EXTRACT(day from \"timestamp\")>= 30 AND EXTRACT(month from \"timestamp\") = 11 "
operator|+
literal|"AND  \"product_id\">= 1549 AND EXTRACT(year from \"timestamp\") = 1997"
operator|+
literal|"group by \"product_id\", EXTRACT(day from \"timestamp\"), "
operator|+
literal|"EXTRACT(month from \"timestamp\"), EXTRACT(year from \"timestamp\")"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_id=1549; EXPR$1=30; EXPR$2=11; EXPR$3=1997"
argument_list|,
literal|"product_id=1553; EXPR$1=30; EXPR$2=11; EXPR$3=1997"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterExtractionOnMonthWithBetween
parameter_list|()
block|{
name|String
name|sqlQuery
init|=
literal|"SELECT \"product_id\", EXTRACT(month from \"timestamp\") FROM \"foodmart\""
operator|+
literal|" WHERE EXTRACT(month from \"timestamp\") BETWEEN 10 AND 11 AND  \"product_id\">= 1558"
operator|+
literal|" GROUP BY \"product_id\", EXTRACT(month from \"timestamp\")"
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart'"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_id=1558; EXPR$1=10"
argument_list|,
literal|"product_id=1558; EXPR$1=11"
argument_list|,
literal|"product_id=1559; EXPR$1=11"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterExtractionOnMonthWithIn
parameter_list|()
block|{
name|String
name|sqlQuery
init|=
literal|"SELECT \"product_id\", EXTRACT(month from \"timestamp\") FROM \"foodmart\""
operator|+
literal|" WHERE EXTRACT(month from \"timestamp\") IN (10, 11) AND  \"product_id\">= 1558"
operator|+
literal|" GROUP BY \"product_id\", EXTRACT(month from \"timestamp\")"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_id=1558; EXPR$1=10"
argument_list|,
literal|"product_id=1558; EXPR$1=11"
argument_list|,
literal|"product_id=1559; EXPR$1=11"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy',"
operator|+
literal|"'dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'product_id','outputName':'product_id','outputType':'STRING'},"
operator|+
literal|"{'type':'extraction','dimension':'__time','outputName':'extract_month',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'M','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}}],'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'and','fields':[{'type':'bound',"
operator|+
literal|"'dimension':'product_id','lower':'1558','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric'},{'type':'or','fields':[{'type':'bound','dimension':'__time'"
operator|+
literal|",'lower':'10','lowerStrict':false,'upper':'10','upperStrict':false,"
operator|+
literal|"'ordering':'numeric','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'M','timeZone':'UTC','locale':'en-US'}},{'type':'bound',"
operator|+
literal|"'dimension':'__time','lower':'11','lowerStrict':false,'upper':'11',"
operator|+
literal|"'upperStrict':false,'ordering':'numeric','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'M','timeZone':'UTC','locale':'en-US'}}]}]},"
operator|+
literal|"'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushOfOrderByWithMonthExtract
parameter_list|()
block|{
name|String
name|sqlQuery
init|=
literal|"SELECT  extract(month from \"timestamp\") as m , \"product_id\", SUM"
operator|+
literal|"(\"unit_sales\") as s FROM \"foodmart\""
operator|+
literal|" WHERE \"product_id\">= 1558"
operator|+
literal|" GROUP BY extract(month from \"timestamp\"), \"product_id\" order by m, s, "
operator|+
literal|"\"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_month',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'M','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}},{'type':'default','dimension':'product_id','outputName':"
operator|+
literal|"'product_id','outputType':'STRING'}],"
operator|+
literal|"'limitSpec':{'type':'default','columns':[{'dimension':'extract_month',"
operator|+
literal|"'direction':'ascending','dimensionOrder':'numeric'},{'dimension':'S',"
operator|+
literal|"'direction':'ascending','dimensionOrder':'numeric'},"
operator|+
literal|"{'dimension':'product_id','direction':'ascending',"
operator|+
literal|"'dimensionOrder':'lexicographic'}]},'filter':{'type':'bound',"
operator|+
literal|"'dimension':'product_id','lower':'1558','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric'},'aggregations':[{'type':'longSum','name':'S',"
operator|+
literal|"'fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[>=(CAST($1):BIGINT, 1558)], projects=[[EXTRACT(FLAG(MONTH), $0), $1, $89]], "
operator|+
literal|"groups=[{0, 1}], aggs=[[SUM($2)]], sort0=[0], sort1=[2], sort2=[1], "
operator|+
literal|"dir0=[ASC], dir1=[ASC], dir2=[ASC])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByFloorTimeWithoutLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select floor(\"timestamp\" to MONTH) as \"month\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH)\n"
operator|+
literal|"order by \"month\" DESC"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'timeseries'"
argument_list|,
literal|"'descending':true"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z"
operator|+
literal|"/2992-01-10T00:00:00.000Z]], projects=[[FLOOR($0, FLAG(MONTH))]], groups=[{0}], "
operator|+
literal|"aggs=[[]], sort0=[0], dir0=[DESC])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByFloorTimeWithLimit
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select floor(\"timestamp\" to MONTH) as \"floorOfMonth\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH)\n"
operator|+
literal|"order by \"floorOfMonth\" DESC LIMIT 3"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[FLOOR($0, FLAG(MONTH))]], groups=[{0}], "
operator|+
literal|"aggs=[[]], sort0=[0], dir0=[DESC], fetch=[3])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"floorOfMonth=1997-12-01 00:00:00"
argument_list|,
literal|"floorOfMonth=1997-11-01 00:00:00"
argument_list|,
literal|"floorOfMonth=1997-10-01 00:00:00"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|,
literal|"'direction':'descending'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushofOrderByYearWithYearMonthExtract
parameter_list|()
block|{
name|String
name|sqlQuery
init|=
literal|"SELECT year(\"timestamp\") as y, extract(month from \"timestamp\") as m , "
operator|+
literal|"\"product_id\", SUM"
operator|+
literal|"(\"unit_sales\") as s FROM \"foodmart\""
operator|+
literal|" WHERE \"product_id\">= 1558"
operator|+
literal|" GROUP BY year(\"timestamp\"), extract(month from \"timestamp\"), \"product_id\" order"
operator|+
literal|" by y DESC, m ASC, s DESC, \"product_id\" LIMIT 3"
decl_stmt|;
specifier|final
name|String
name|expectedPlan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[>=(CAST($1):BIGINT, 1558)], projects=[[EXTRACT(FLAG(YEAR), $0), "
operator|+
literal|"EXTRACT(FLAG(MONTH), $0), $1, $89]], groups=[{0, 1, 2}], aggs=[[SUM($3)]], sort0=[0], "
operator|+
literal|"sort1=[1], sort2=[3], sort3=[2], dir0=[DESC], "
operator|+
literal|"dir1=[ASC], dir2=[DESC], dir3=[ASC], fetch=[3])"
decl_stmt|;
specifier|final
name|String
name|expectedDruidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_year',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'yyyy','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}},{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'extract_month','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'M','timeZone':'UTC','locale':'en-US'}},{'type':'default',"
operator|+
literal|"'dimension':'product_id','outputName':'product_id','outputType':'STRING'}],"
operator|+
literal|"'limitSpec':{'type':'default','limit':3,"
operator|+
literal|"'columns':[{'dimension':'extract_year','direction':'descending',"
operator|+
literal|"'dimensionOrder':'numeric'},{'dimension':'extract_month',"
operator|+
literal|"'direction':'ascending','dimensionOrder':'numeric'},{'dimension':'S',"
operator|+
literal|"'direction':'descending','dimensionOrder':'numeric'},"
operator|+
literal|"{'dimension':'product_id','direction':'ascending',"
operator|+
literal|"'dimensionOrder':'lexicographic'}]},'filter':{'type':'bound',"
operator|+
literal|"'dimension':'product_id','lower':'1558','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric'},'aggregations':[{'type':'longSum','name':'S',"
operator|+
literal|"'fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedPlan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedDruidQuery
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"Y=1997; M=1; product_id=1558; S=6"
argument_list|,
literal|"Y=1997; M=1; product_id=1559; S=6"
argument_list|,
literal|"Y=1997; M=2; product_id=1558; S=24"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushofOrderByMetricWithYearMonthExtract
parameter_list|()
block|{
name|String
name|sqlQuery
init|=
literal|"SELECT year(\"timestamp\") as y, extract(month from \"timestamp\") as m , "
operator|+
literal|"\"product_id\", SUM(\"unit_sales\") as s FROM \"foodmart\""
operator|+
literal|" WHERE \"product_id\">= 1558"
operator|+
literal|" GROUP BY year(\"timestamp\"), extract(month from \"timestamp\"), \"product_id\" order"
operator|+
literal|" by s DESC, m DESC, \"product_id\" LIMIT 3"
decl_stmt|;
specifier|final
name|String
name|expectedPlan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[>=(CAST($1):BIGINT, 1558)], projects=[[EXTRACT(FLAG(YEAR), $0), "
operator|+
literal|"EXTRACT(FLAG(MONTH), $0), $1, $89]], groups=[{0, 1, 2}], aggs=[[SUM($3)]], "
operator|+
literal|"sort0=[3], sort1=[1], sort2=[2], dir0=[DESC], dir1=[DESC], dir2=[ASC], fetch=[3])"
decl_stmt|;
specifier|final
name|String
name|expectedDruidQueryType
init|=
literal|"'queryType':'groupBy'"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"Y=1997; M=12; product_id=1558; S=30"
argument_list|,
literal|"Y=1997; M=3; product_id=1558; S=29"
argument_list|,
literal|"Y=1997; M=5; product_id=1558; S=27"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedPlan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedDruidQueryType
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByTimeSortOverMetrics
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"SELECT count(*) as c , SUM(\"unit_sales\") as s,"
operator|+
literal|" floor(\"timestamp\" to month)"
operator|+
literal|" FROM \"foodmart\" group by floor(\"timestamp\" to month) order by s DESC"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"C=8716; S=26796; EXPR$2=1997-12-01 00:00:00"
argument_list|,
literal|"C=8231; S=25270; EXPR$2=1997-11-01 00:00:00"
argument_list|,
literal|"C=7752; S=23763; EXPR$2=1997-07-01 00:00:00"
argument_list|,
literal|"C=7710; S=23706; EXPR$2=1997-03-01 00:00:00"
argument_list|,
literal|"C=7038; S=21697; EXPR$2=1997-08-01 00:00:00"
argument_list|,
literal|"C=7033; S=21628; EXPR$2=1997-01-01 00:00:00"
argument_list|,
literal|"C=6912; S=21350; EXPR$2=1997-06-01 00:00:00"
argument_list|,
literal|"C=6865; S=21081; EXPR$2=1997-05-01 00:00:00"
argument_list|,
literal|"C=6844; S=20957; EXPR$2=1997-02-01 00:00:00"
argument_list|,
literal|"C=6662; S=20388; EXPR$2=1997-09-01 00:00:00"
argument_list|,
literal|"C=6588; S=20179; EXPR$2=1997-04-01 00:00:00"
argument_list|,
literal|"C=6478; S=19958; EXPR$2=1997-10-01 00:00:00"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"DruidQuery(table=[[foodmart, foodmart]],"
operator|+
literal|" intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]],"
operator|+
literal|" projects=[[FLOOR($0, FLAG(MONTH)), $89]], groups=[{0}], "
operator|+
literal|"aggs=[[COUNT(), SUM($1)]], sort0=[2], dir0=[DESC])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNumericOrderingOfOrderByOperatorFullTime
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"SELECT \"timestamp\" as \"timestamp\","
operator|+
literal|" count(*) as c, SUM(\"unit_sales\") as s FROM "
operator|+
literal|"\"foodmart\" group by \"timestamp\" order by \"timestamp\" DESC, c DESC, s LIMIT 5"
decl_stmt|;
specifier|final
name|String
name|druidSubQuery
init|=
literal|"'limitSpec':{'type':'default','limit':5,"
operator|+
literal|"'columns':[{'dimension':'extract','direction':'descending',"
operator|+
literal|"'dimensionOrder':'lexicographic'},{'dimension':'C',"
operator|+
literal|"'direction':'descending','dimensionOrder':'numeric'},{'dimension':'S',"
operator|+
literal|"'direction':'ascending','dimensionOrder':'numeric'}]},"
operator|+
literal|"'aggregations':[{'type':'count','name':'C'},{'type':'longSum',"
operator|+
literal|"'name':'S','fieldName':'unit_sales'}]"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"timestamp=1997-12-30 00:00:00; C=22; S=36\ntimestamp=1997-12-29"
operator|+
literal|" 00:00:00; C=321; S=982\ntimestamp=1997-12-28 00:00:00; C=480; "
operator|+
literal|"S=1496\ntimestamp=1997-12-27 00:00:00; C=363; S=1156\ntimestamp=1997-12-26 00:00:00; "
operator|+
literal|"C=144; S=420"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidSubQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNumericOrderingOfOrderByOperatorTimeExtract
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"SELECT extract(day from \"timestamp\") as d, extract(month from "
operator|+
literal|"\"timestamp\") as m,  year(\"timestamp\") as y , count(*) as c, SUM(\"unit_sales\")  "
operator|+
literal|"as s FROM "
operator|+
literal|"\"foodmart\" group by  extract(day from \"timestamp\"), extract(month from \"timestamp\"), "
operator|+
literal|"year(\"timestamp\")  order by d DESC, m ASC, y DESC LIMIT 5"
decl_stmt|;
specifier|final
name|String
name|druidSubQuery
init|=
literal|"'limitSpec':{'type':'default','limit':5,"
operator|+
literal|"'columns':[{'dimension':'extract_day','direction':'descending',"
operator|+
literal|"'dimensionOrder':'numeric'},{'dimension':'extract_month',"
operator|+
literal|"'direction':'ascending','dimensionOrder':'numeric'},"
operator|+
literal|"{'dimension':'extract_year','direction':'descending',"
operator|+
literal|"'dimensionOrder':'numeric'}]}"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"D=30; M=3; Y=1997; C=114; S=351\nD=30; M=5; Y=1997; "
operator|+
literal|"C=24; S=34\nD=30; M=6; Y=1997; C=73; S=183\nD=30; M=7; Y=1997; C=29; S=54\nD=30; M=8; "
operator|+
literal|"Y=1997; C=137; S=422"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidSubQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNumericOrderingOfOrderByOperatorStringDims
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"SELECT \"brand_name\", count(*) as c, SUM(\"unit_sales\")  "
operator|+
literal|"as s FROM "
operator|+
literal|"\"foodmart\" group by \"brand_name\" order by \"brand_name\"  DESC LIMIT 5"
decl_stmt|;
specifier|final
name|String
name|druidSubQuery
init|=
literal|"'limitSpec':{'type':'default','limit':5,"
operator|+
literal|"'columns':[{'dimension':'brand_name','direction':'descending',"
operator|+
literal|"'dimensionOrder':'lexicographic'}]}"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"brand_name=Washington; C=576; S=1775\nbrand_name=Walrus; C=457;"
operator|+
literal|" S=1399\nbrand_name=Urban; C=299; S=924\nbrand_name=Tri-State; C=2339; "
operator|+
literal|"S=7270\nbrand_name=Toucan; C=123; S=380"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidSubQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByWeekExtract
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT extract(week from \"timestamp\") from \"foodmart\" where "
operator|+
literal|"\"product_id\" = 1558 and extract(week from \"timestamp\") IN (10, 11) group by extract"
operator|+
literal|"(week from \"timestamp\")"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_week',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'w','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}}],'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'and','fields':[{'type':'bound','dimension':'product_id',"
operator|+
literal|"'lower':'1558','lowerStrict':false,'upper':'1558','upperStrict':false,"
operator|+
literal|"'ordering':'numeric'},{'type':'or',"
operator|+
literal|"'fields':[{'type':'bound','dimension':'__time','lower':'10','lowerStrict':false,"
operator|+
literal|"'upper':'10','upperStrict':false,'ordering':'numeric',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'w','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}},{'type':'bound','dimension':'__time','lower':'11','lowerStrict':false,"
operator|+
literal|"'upper':'11','upperStrict':false,'ordering':'numeric',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'w',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}]}]},"
operator|+
literal|"'aggregations':[],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=10\nEXPR$0=11"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1765">[CALCITE-1765]    * Druid adapter: Gracefully handle granularity that cannot be pushed to    * extraction function</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testTimeExtractThatCannotBePushed
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT extract(CENTURY from \"timestamp\") from \"foodmart\" where "
operator|+
literal|"\"product_id\" = 1558 group by extract(CENTURY from \"timestamp\")"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableAggregate(group=[{0}])\n"
operator|+
literal|"    BindableProject(EXPR$0=[EXTRACT(FLAG(CENTURY), $0)])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[=($1, 1558)], projects=[[$0]])\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'scan'"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=20"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1770">[CALCITE-1770]    * Druid adapter: CAST(NULL AS ...) gives NPE</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testPushCast
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"product_id\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"where \"product_id\" = cast(NULL as varchar)\n"
operator|+
literal|"group by \"product_id\" order by \"product_id\" limit 5"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$0], dir0=[ASC], fetch=[5])\n"
operator|+
literal|"    BindableFilter(condition=[=($0, null)])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], intervals="
operator|+
literal|"[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{1}], aggs=[[]])"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"{\"queryType\":\"groupBy\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFalseFilter
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"Select count(*) as c from \"foodmart\" where false"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"1 == 2\"}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTrueFilter
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"Select count(*) as c from \"foodmart\" where true"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=86829"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFalseFilterCaseConjectionWithTrue
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"Select count(*) as c from \"foodmart\" where "
operator|+
literal|"\"product_id\" = 1558 and (true or false)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=60"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'timeseries'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1769">[CALCITE-1769]    * Druid adapter: Push down filters involving numeric cast of literals</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testPushCastNumeric
parameter_list|()
block|{
name|String
name|druidQuery
init|=
literal|"'filter':{'type':'bound','dimension':'product_id',"
operator|+
literal|"'upper':'10','upperStrict':true,'ordering':'numeric'}"
decl_stmt|;
name|sql
argument_list|(
literal|"?"
argument_list|)
operator|.
name|withRel
argument_list|(
operator|new
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RelNode
name|apply
parameter_list|(
name|RelBuilder
name|b
parameter_list|)
block|{
comment|// select product_id
comment|// from foodmart.foodmart
comment|// where product_id< cast(10 as varchar)
specifier|final
name|RelDataType
name|intType
init|=
name|b
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
return|return
name|b
operator|.
name|scan
argument_list|(
literal|"foodmart"
argument_list|,
literal|"foodmart"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|b
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeCall
argument_list|(
name|intType
argument_list|,
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RexNode
operator|>
name|of
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"product_id"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|b
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeCall
argument_list|(
name|intType
argument_list|,
name|SqlStdOperatorTable
operator|.
name|CAST
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|b
operator|.
name|literal
argument_list|(
literal|"10"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|b
operator|.
name|field
argument_list|(
literal|"product_id"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushFieldEqualsLiteral
parameter_list|()
block|{
name|sql
argument_list|(
literal|"?"
argument_list|)
operator|.
name|withRel
argument_list|(
operator|new
name|Function
argument_list|<
name|RelBuilder
argument_list|,
name|RelNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RelNode
name|apply
parameter_list|(
name|RelBuilder
name|b
parameter_list|)
block|{
comment|// select count(*) as c
comment|// from foodmart.foodmart
comment|// where product_id = 'id'
return|return
name|b
operator|.
name|scan
argument_list|(
literal|"foodmart"
argument_list|,
literal|"foodmart"
argument_list|)
operator|.
name|filter
argument_list|(
name|b
operator|.
name|call
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|b
operator|.
name|field
argument_list|(
literal|"product_id"
argument_list|)
argument_list|,
name|b
operator|.
name|literal
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|b
operator|.
name|groupKey
argument_list|()
argument_list|,
name|b
operator|.
name|countStar
argument_list|(
literal|"c"
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
argument_list|)
comment|// Should return one row, "c=0"; logged
comment|// [CALCITE-1775] "GROUP BY ()" on empty relation should return 1 row
operator|.
name|returnsUnordered
argument_list|(
literal|"c=0"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'timeseries'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPlusArithmeticOperation
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select sum(\"store_sales\") + sum(\"store_cost\") as a, "
operator|+
literal|"\"store_state\" from \"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"type':'expression','name':'A','expression':'(\\'$f1\\' + \\'$f2\\')'}]"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $90, $91]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($1), SUM($2)]], post_projects=[[+($1, $2), $0]], sort0=[0], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"A=369117.52790000016; store_state=WA"
argument_list|,
literal|"A=222698.26509999996; store_state=CA"
argument_list|,
literal|"A=199049.57059999998; store_state=OR"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivideArithmeticOperation
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", sum(\"store_sales\") / sum(\"store_cost\") "
operator|+
literal|"as a from \"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"[{'type':'expression','name':'A','expression':'(\\'$f1\\' / \\'$f2\\')"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $90, $91]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($1), SUM($2)]], post_projects=[[$0, /($1, $2)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=OR; A=2.506091302943239"
argument_list|,
literal|"store_state=CA; A=2.505379741272971"
argument_list|,
literal|"store_state=WA; A=2.5045806163801996"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiplyArithmeticOperation
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", sum(\"store_sales\") * sum(\"store_cost\") "
operator|+
literal|"as a from \"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"{'type':'expression','name':'A','expression':'(\\'$f1\\' * \\'$f2\\')'"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $90, $91]], groups=[{0}], aggs=[[SUM($1),"
operator|+
literal|" SUM($2)]], post_projects=[[$0, *($1, $2)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=WA; A=2.7783838325212463E10"
argument_list|,
literal|"store_state=CA; A=1.0112000537448784E10"
argument_list|,
literal|"store_state=OR; A=8.077425041941243E9"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMinusArithmeticOperation
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", sum(\"store_sales\") - sum(\"store_cost\") "
operator|+
literal|"as a from \"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"'postAggregations':[{'type':'expression','name':'A',"
operator|+
literal|"'expression':'(\\'$f1\\' - \\'$f2\\')'}]"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $90, $91]], groups=[{0}], aggs=[[SUM($1), "
operator|+
literal|"SUM($2)]], post_projects=[[$0, -($1, $2)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=WA; A=158468.91210000002"
argument_list|,
literal|"store_state=CA; A=95637.41489999992"
argument_list|,
literal|"store_state=OR; A=85504.56939999988"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstantPostAggregator
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", sum(\"store_sales\") + 100 as a from "
operator|+
literal|"\"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"{'type':'expression','name':'A','expression':'(\\'$f1\\' + 100)'}"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $90]], groups=[{0}], aggs=[[SUM($1)]], "
operator|+
literal|"post_projects=[[$0, +($1, 100)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=WA; A=263893.2200000001"
argument_list|,
literal|"store_state=CA; A=159267.83999999994"
argument_list|,
literal|"store_state=OR; A=142377.06999999992"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRecursiveArithmeticOperation
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", -1 * (a + b) as c from (select "
operator|+
literal|"(sum(\"store_sales\")-sum(\"store_cost\")) / (count(*) * 3) "
operator|+
literal|"AS a,sum(\"unit_sales\") AS b, \"store_state\"  from \"foodmart\"  group "
operator|+
literal|"by \"store_state\") order by c desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"'postAggregations':[{'type':'expression','name':'C','expression':"
operator|+
literal|"'(-1 * (((\\'$f1\\' - \\'$f2\\') / (\\'$f3\\' * 3)) + \\'B\\'))'}]"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $89, $90, $91]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($2), SUM($3), COUNT(), SUM($1)]], post_projects=[[$0, *(-1, +(/(-($1, $2), "
operator|+
literal|"*($3, 3)), $4))]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=OR; C=-67660.31890435601"
argument_list|,
literal|"store_state=CA; C=-74749.30433035882"
argument_list|,
literal|"store_state=WA; C=-124367.29537914316"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Turn on now count(distinct )    */
annotation|@
name|Test
specifier|public
name|void
name|testHyperUniquePostAggregator
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", sum(\"store_cost\") / count(distinct "
operator|+
literal|"\"brand_name\") as a from \"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
specifier|final
name|String
name|postAggString
init|=
literal|"[{'type':'expression','name':'A',"
operator|+
literal|"'expression':'(\\'$f1\\' / \\'$f2\\')'}]"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$2, $63, $91]], groups=[{1}], aggs=[[SUM($2), "
operator|+
literal|"COUNT(DISTINCT $0)]], post_projects=[[$0, /($1, $2)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|foodmartApprox
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractFilterWorkWithPostAggregations
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"store_state\", \"brand_name\", sum(\"store_sales\") - "
operator|+
literal|"sum(\"store_cost\") as a  from \"foodmart\" where extract (week from \"timestamp\")"
operator|+
literal|" IN (10,11) and \"brand_name\"='Bird Call' group by \"store_state\", \"brand_name\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"\"postAggregations\":[{\"type\":\"expression\",\"name\":\"A\","
operator|+
literal|"\"expression\":\"(\\\"$f2\\\" - \\\"$f3\\\")\"}]"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[AND(=("
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=CA; brand_name=Bird Call; A=34.364599999999996"
argument_list|,
literal|"store_state=OR; brand_name=Bird Call; A=39.16359999999999"
argument_list|,
literal|"store_state=WA; brand_name=Bird Call; A=53.742500000000014"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractFilterWorkWithPostAggregationsWithConstant
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"store_state\", 'Bird Call' as \"brand_name\", "
operator|+
literal|"sum(\"store_sales\") - sum(\"store_cost\") as a  from \"foodmart\" "
operator|+
literal|"where extract (week from \"timestamp\")"
operator|+
literal|" IN (10,11) and \"brand_name\"='Bird Call' group by \"store_state\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"type':'expression','name':'A','expression':'(\\'$f1\\' - \\'$f2\\')"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[AND(=($2, 'Bird Call'), OR(=(EXTRACT(FLAG(WEEK), $0), 10), "
operator|+
literal|"=(EXTRACT(FLAG(WEEK), $0), 11)))], projects=[[$0, $2, $63, $90, $91]], "
operator|+
literal|"groups=[{2}], aggs=[[SUM($3), SUM($4)]], post_projects=[[$0, 'Bird Call', -($1, $2)]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=CA; brand_name=Bird Call; A=34.364599999999996"
argument_list|,
literal|"store_state=OR; brand_name=Bird Call; A=39.16359999999999"
argument_list|,
literal|"store_state=WA; brand_name=Bird Call; A=53.742500000000014"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleAverageFunction
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", sum(\"store_cost\") / count(*) as a from "
operator|+
literal|"\"foodmart\" group by \"store_state\" order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"\"postAggregations\":[{\"type\":\"expression\",\"name\":\"A\","
operator|+
literal|"\"expression\":\"(\\\"$f1\\\" / \\\"$f2\\\")"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $91]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($1), COUNT()]], post_projects=[[$0, /($1, $2)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=OR; A=2.6271402406293403"
argument_list|,
literal|"store_state=CA; A=2.599338206292706"
argument_list|,
literal|"store_state=WA; A=2.5828708592868717"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPartiallyPostAggregation
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", sum(\"store_sales\") / sum(\"store_cost\")"
operator|+
literal|" as a, case when sum(\"unit_sales\")=0 then 1.0 else sum(\"unit_sales\") "
operator|+
literal|"end as b from \"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
specifier|final
name|String
name|postAggString
init|=
literal|"'postAggregations':[{'type':'expression','name':'A',"
operator|+
literal|"'expression':'(\\'$f1\\' / \\'$f2\\')'},{'type':'expression','name':'B',"
operator|+
literal|"'expression':'case_searched((\\'$f3\\' == 0),1.0,CAST(\\'$f3\\'"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $89, $90, $91]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($2), SUM($3), SUM($1)]], post_projects=[[$0, /($1, $2), "
operator|+
literal|"CASE(=($3, 0), 1.0, CAST($3):DECIMAL(19, 0))]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=OR; A=2.506091302943239; B=67659.0"
argument_list|,
literal|"store_state=CA; A=2.505379741272971; B=74748.0"
argument_list|,
literal|"store_state=WA; A=2.5045806163801996; B=124366.0"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDuplicateReferenceOnPostAggregation
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", a, a - b as c from (select \"store_state\", "
operator|+
literal|"sum(\"store_sales\") + 100 as a, sum(\"store_cost\") as b from \"foodmart\"  group by "
operator|+
literal|"\"store_state\") order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"[{'type':'expression','name':'A','expression':'(\\'$f1\\' + 100)'},"
operator|+
literal|"{'type':'expression','name':'C','expression':'((\\'$f1\\' + 100) - \\'B\\')'}]"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{63}], "
operator|+
literal|"aggs=[[SUM($90), SUM($91)]], post_projects=[[$0, +($1, 100), -(+($1, 100), $2)]], "
operator|+
literal|"sort0=[1], dir0=[DESC]"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=WA; A=263893.2200000001; C=158568.91210000002"
argument_list|,
literal|"store_state=CA; A=159267.83999999994; C=95737.41489999992"
argument_list|,
literal|"store_state=OR; A=142377.06999999992; C=85604.56939999988"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivideByZeroDoubleTypeInfinity
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", sum(\"store_cost\") / 0 as a from "
operator|+
literal|"\"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"'type':'expression','name':'A','expression':'(\\'$f1\\' / 0)'}]"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $91]], groups=[{0}], aggs=[[SUM($1)]], "
operator|+
literal|"post_projects=[[$0, /($1, 0)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=CA; A=Infinity"
argument_list|,
literal|"store_state=OR; A=Infinity"
argument_list|,
literal|"store_state=WA; A=Infinity"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivideByZeroDoubleTypeNegInfinity
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", -1.0 * sum(\"store_cost\") / 0 as "
operator|+
literal|"a from \"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"\"postAggregations\":[{\"type\":\"expression\",\"name\":\"A\","
operator|+
literal|"\"expression\":\"((-1.0 * \\\"$f1\\\") / 0)\"}],"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $91]], groups=[{0}], aggs=[[SUM($1)]], "
operator|+
literal|"post_projects=[[$0, /(*(-1.0, $1), 0)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=CA; A=-Infinity"
argument_list|,
literal|"store_state=OR; A=-Infinity"
argument_list|,
literal|"store_state=WA; A=-Infinity"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivideByZeroDoubleTypeNaN
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", (sum(\"store_cost\") - sum(\"store_cost\")) "
operator|+
literal|"/ 0 as a from \"foodmart\"  group by \"store_state\" order by a desc"
decl_stmt|;
specifier|final
name|String
name|postAggString
init|=
literal|"'postAggregations':[{'type':'expression','name':'A',"
operator|+
literal|"'expression':'((\\'$f1\\' - \\'$f1\\') / 0)'}"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $91]], groups=[{0}], aggs=[[SUM($1)]], "
operator|+
literal|"post_projects=[[$0, /(-($1, $1), 0)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=CA; A=NaN"
argument_list|,
literal|"store_state=OR; A=NaN"
argument_list|,
literal|"store_state=WA; A=NaN"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDivideByZeroIntegerType
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", (count(*) - "
operator|+
literal|"count(*)) / 0 as a from \"foodmart\"  group by \"store_state\" "
operator|+
literal|"order by a desc"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63]], groups=[{0}], aggs=[[COUNT()]], "
operator|+
literal|"post_projects=[[$0, /(-($1, $1), 0)]], sort0=[1], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|throws_
argument_list|(
literal|"Server returned HTTP response code: 500"
argument_list|)
expr_stmt|;
comment|//@TODO It seems like calcite is not handling 500 error,
comment|// need to catch it and parse exception message from druid,
comment|// e.g., throws_("/ by zero");
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInterleaveBetweenAggregateAndGroupOrderByOnMetrics
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", \"brand_name\", \"A\" from (\n"
operator|+
literal|"  select sum(\"store_sales\")-sum(\"store_cost\") as a, \"store_state\""
operator|+
literal|", \"brand_name\"\n"
operator|+
literal|"  from \"foodmart\"\n"
operator|+
literal|"  group by \"store_state\", \"brand_name\" ) subq\n"
operator|+
literal|"order by \"A\" limit 5"
decl_stmt|;
name|String
name|postAggString
init|=
literal|"\"postAggregations\":[{\"type\":\"expression\",\"name\":\"A\","
operator|+
literal|"\"expression\":\"(\\\"$f2\\\" - \\\"$f3\\\")\"}"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $2, $90, $91]], groups=[{0, 1}], "
operator|+
literal|"aggs=[[SUM($2), SUM($3)]], post_projects=[[$0, $1, -($2, $3)]]"
operator|+
literal|", sort0=[2], dir0=[ASC], fetch=[5])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=CA; brand_name=King; A=21.4632"
argument_list|,
literal|"store_state=OR; brand_name=Symphony; A=32.176"
argument_list|,
literal|"store_state=CA; brand_name=Toretti; A=32.24650000000001"
argument_list|,
literal|"store_state=WA; brand_name=King; A=34.6104"
argument_list|,
literal|"store_state=OR; brand_name=Toretti; A=36.3"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInterleaveBetweenAggregateAndGroupOrderByOnDimension
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_state\", \"brand_name\", \"A\" from \n"
operator|+
literal|"(select \"store_state\", sum(\"store_sales\")+sum(\"store_cost\") "
operator|+
literal|"as a, \"brand_name\" from \"foodmart\" group by \"store_state\", \"brand_name\") "
operator|+
literal|"order by \"brand_name\", \"store_state\" limit 5"
decl_stmt|;
specifier|final
name|String
name|postAggString
init|=
literal|"'postAggregations':[{'type':'expression','name':'A',"
operator|+
literal|"'expression':'(\\'$f2\\' + \\'$f3\\')'}]"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], groups=[{2, 63}], aggs=[[SUM($90), SUM($91)]], post_projects"
operator|+
literal|"=[[$1, $0, +($2, $3)]], sort0=[1], sort1=[0], dir0=[ASC], dir1=[ASC], fetch=[5])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"store_state=CA; brand_name=ADJ; A=222.1524"
argument_list|,
literal|"store_state=OR; brand_name=ADJ; A=186.60359999999997"
argument_list|,
literal|"store_state=WA; brand_name=ADJ; A=216.9912"
argument_list|,
literal|"store_state=CA; brand_name=Akron; A=250.349"
argument_list|,
literal|"store_state=OR; brand_name=Akron; A=278.69720000000007"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|postAggString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrderByOnMetricsInSelectDruidQuery
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select \"store_sales\" as a, \"store_cost\" as b, \"store_sales\" - "
operator|+
literal|"\"store_cost\" as c from \"foodmart\" where \"timestamp\" "
operator|+
literal|">= '1997-01-01 00:00:00' and \"timestamp\"< '1997-09-01 00:00:00' order by c "
operator|+
literal|"limit 5"
decl_stmt|;
name|String
name|queryType
init|=
literal|"'queryType':'scan'"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$2], dir0=[ASC], fetch=[5])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1997-01-01T00:00:00.000Z/1997-09-01T00:00:00.000Z]], "
operator|+
literal|"projects=[[$90, $91, -($90, $91)]])"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"A=0.51; B=0.2448; C=0.2652"
argument_list|,
literal|"A=0.51; B=0.2397; C=0.2703"
argument_list|,
literal|"A=0.57; B=0.285; C=0.285"
argument_list|,
literal|"A=0.5; B=0.21; C=0.29000000000000004"
argument_list|,
literal|"A=0.57; B=0.2793; C=0.29069999999999996"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|queryType
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests whether an aggregate with a filter clause has it's filter factored out    * when there is no outer filter    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseFactoredOut
parameter_list|()
block|{
comment|// Logically equivalent to
comment|// select sum("store_sales") from "foodmart" where "the_year">= 1997
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") "
operator|+
literal|"filter (where \"the_year\">= 1997) from \"foodmart\""
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','filter':{'type':'bound','dimension':'the_year','lower':'1997',"
operator|+
literal|"'lowerStrict':false,'ordering':'numeric'},'aggregations':[{'type':'doubleSum','name'"
operator|+
literal|":'EXPR$0','fieldName':'store_sales'}],'intervals':['1900-01-09T00:00:00.000Z/2992-01"
operator|+
literal|"-10T00:00:00.000Z'],'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests whether filter clauses with filters that are always true disappear or not    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseAlwaysTrueGone
parameter_list|()
block|{
comment|// Logically equivalent to
comment|// select sum("store_sales") from "foodmart"
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where 1 = 1) from \"foodmart\""
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','aggregations':[{'type':'doubleSum','name':'EXPR$0','fieldName':"
operator|+
literal|"'store_sales'}],'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests whether filter clauses with filters that are always true disappear in the presence    * of another aggregate without a filter clause    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseAlwaysTrueWithAggGone1
parameter_list|()
block|{
comment|// Logically equivalent to
comment|// select sum("store_sales"), sum("store_cost") from "foodmart"
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where 1 = 1), "
operator|+
literal|"sum(\"store_cost\") from \"foodmart\""
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','aggregations':[{'type':'doubleSum','name':'EXPR$0','fieldName':"
operator|+
literal|"'store_sales'},{'type':'doubleSum','name':'EXPR$1','fieldName':'store_cost'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests whether filter clauses with filters that are always true disappear in the presence    * of another aggregate with a filter clause    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseAlwaysTrueWithAggGone2
parameter_list|()
block|{
comment|// Logically equivalent to
comment|// select sum("store_sales"),
comment|// sum("store_cost") filter (where "store_state" = 'CA') from "foodmart"
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where 1 = 1), "
operator|+
literal|"sum(\"store_cost\") filter (where \"store_state\" = 'CA') "
operator|+
literal|"from \"foodmart\""
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','aggregations':[{'type':'doubleSum','name':'EXPR$0','fieldName'"
operator|+
literal|":'store_sales'},{'type':'filtered','filter':{'type':'selector','dimension':"
operator|+
literal|"'store_state','value':'CA'},'aggregator':{'type':'doubleSum','name':'EXPR$1',"
operator|+
literal|"'fieldName':'store_cost'}}],'intervals':"
operator|+
literal|"['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests whether an existing outer filter is untouched when an aggregate has a filter clause    * that is always true    */
annotation|@
name|Test
specifier|public
name|void
name|testOuterFilterRemainsWithAlwaysTrueClause
parameter_list|()
block|{
comment|// Logically equivalent to
comment|// select sum("store_sales"), sum("store_cost") from "foodmart" where "store_city" = 'Seattle'
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where 1 = 1), sum(\"store_cost\") "
operator|+
literal|"from \"foodmart\" where \"store_city\" = 'Seattle'"
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','filter':{'type':'selector','dimension':'store_city',"
operator|+
literal|"'value':'Seattle'},'aggregations':[{'type':'doubleSum','name':'EXPR$0',"
operator|+
literal|"'fieldName':'store_sales'},{'type':'doubleSum','name':'EXPR$1',"
operator|+
literal|"'fieldName':'store_cost'}],'intervals':"
operator|+
literal|"['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that an aggregate with a filter clause that is always false does not get pushed in    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseAlwaysFalseNotPushed
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where 1> 1) from \"foodmart\""
decl_stmt|;
comment|// Calcite takes care of the unsatisfiable filter
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[false], projects=[[$90, false]], groups=[{}], aggs=[[SUM($0)]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"timeseries\","
operator|+
literal|"\"dataSource\":\"foodmart\",\"descending\":false,\"granularity\":\"all\","
operator|+
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"1 == 2\"},"
operator|+
literal|"\"aggregations\":[{\"type\":\"doubleSum\",\"name\":\"EXPR$0\","
operator|+
literal|"\"fieldName\":\"store_sales\"}],"
operator|+
literal|"\"intervals\":[\"1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z\"],"
operator|+
literal|"\"context\":{\"skipEmptyBuckets\":false}}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedSubExplain
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that an aggregate with a filter clause that is always false does not get pushed when    * there is already an outer filter    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseAlwaysFalseNotPushedWithFilter
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where 1> 1) "
operator|+
literal|"from \"foodmart\" where \"store_city\" = 'Seattle'"
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[AND"
operator|+
literal|"(false, =($62, 'Seattle'))], projects=[[$90, false]], groups=[{}], aggs=[[SUM"
operator|+
literal|"($0)]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedSubExplain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type"
operator|+
literal|"\":\"and\",\"fields\":[{\"type\":\"expression\",\"expression\":\"1 == 2\"},"
operator|+
literal|"{\"type\":\"selector\",\"dimension\":\"store_city\",\"value\":\"Seattle\"}]}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that an aggregate with a filter clause that is the same as the outer filter has no    * references to that filter, and that the original outer filter remains    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseSameAsOuterFilterGone
parameter_list|()
block|{
comment|// Logically equivalent to
comment|// select sum("store_sales") from "foodmart" where "store_city" = 'Seattle'
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where \"store_city\" = 'Seattle') "
operator|+
literal|"from \"foodmart\" where \"store_city\" = 'Seattle'"
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','filter':{'type':'selector','dimension':'store_city','value':"
operator|+
literal|"'Seattle'},'aggregations':[{'type':'doubleSum','name':'EXPR$0','fieldName':"
operator|+
literal|"'store_sales'}],'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=52644.07000000001"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that an aggregate with a filter clause in the presence of another aggregate    * without a filter clause does not have it's filter factored out into the outer filter    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseNotFactoredOut1
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where \"store_state\" = 'CA'), "
operator|+
literal|"sum(\"store_cost\") from \"foodmart\""
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','aggregations':[{'type':'filtered','filter':{'type':'selector',"
operator|+
literal|"'dimension':'store_state','value':'CA'},'aggregator':{'type':'doubleSum','name':"
operator|+
literal|"'EXPR$0','fieldName':'store_sales'}},{'type':'doubleSum','name':'EXPR$1','fieldName'"
operator|+
literal|":'store_cost'}],'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that an aggregate with a filter clause in the presence of another aggregate    * without a filter clause, and an outer filter does not have it's    * filter factored out into the outer filter    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseNotFactoredOut2
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where \"store_state\" = 'CA'), "
operator|+
literal|"sum(\"store_cost\") from \"foodmart\" where \"the_year\">= 1997"
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','filter':{'type':'bound','dimension':'the_year','lower':'1997',"
operator|+
literal|"'lowerStrict':false,'ordering':'numeric'},'aggregations':[{'type':'filtered',"
operator|+
literal|"'filter':{'type':'selector','dimension':'store_state','value':'CA'},'aggregator':{"
operator|+
literal|"'type':'doubleSum','name':'EXPR$0','fieldName':'store_sales'}},{'type':'doubleSum',"
operator|+
literal|"'name':'EXPR$1','fieldName':'store_cost'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that multiple aggregates with filter clauses have their filters extracted to    * the outer filter field for data pruning    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClausesFactoredForPruning1
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select "
operator|+
literal|"sum(\"store_sales\") filter (where \"store_state\" = 'CA'), "
operator|+
literal|"sum(\"store_sales\") filter (where \"store_state\" = 'WA') "
operator|+
literal|"from \"foodmart\""
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','filter':{'type':'or','fields':[{'type':'selector','dimension':"
operator|+
literal|"'store_state','value':'CA'},{'type':'selector','dimension':'store_state',"
operator|+
literal|"'value':'WA'}]},'aggregations':[{'type':'filtered','filter':{'type':'selector',"
operator|+
literal|"'dimension':'store_state','value':'CA'},'aggregator':{'type':'doubleSum','name':"
operator|+
literal|"'EXPR$0','fieldName':'store_sales'}},{'type':'filtered','filter':{'type':'selector',"
operator|+
literal|"'dimension':'store_state','value':'WA'},'aggregator':{'type':'doubleSum','name':"
operator|+
literal|"'EXPR$1','fieldName':'store_sales'}}],'intervals':"
operator|+
literal|"['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=159167.83999999994; EXPR$1=263793.2200000001"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that multiple aggregates with filter clauses have their filters extracted to    * the outer filter field for data pruning in the presence of an outer filter    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClausesFactoredForPruning2
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select "
operator|+
literal|"sum(\"store_sales\") filter (where \"store_state\" = 'CA'), "
operator|+
literal|"sum(\"store_sales\") filter (where \"store_state\" = 'WA') "
operator|+
literal|"from \"foodmart\" where \"brand_name\" = 'Super'"
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','filter':{'type':'and','fields':[{'type':'or','fields':[{'type':"
operator|+
literal|"'selector','dimension':'store_state','value':'CA'},{'type':'selector','dimension':"
operator|+
literal|"'store_state','value':'WA'}]},{'type':'selector','dimension':'brand_name','value':"
operator|+
literal|"'Super'}]},'aggregations':[{'type':'filtered','filter':{'type':'selector',"
operator|+
literal|"'dimension':'store_state','value':'CA'},'aggregator':{'type':'doubleSum','name':"
operator|+
literal|"'EXPR$0','fieldName':'store_sales'}},{'type':'filtered','filter':{'type':'selector',"
operator|+
literal|"'dimension':'store_state','value':'WA'},'aggregator':{'type':'doubleSum','name':"
operator|+
literal|"'EXPR$1','fieldName':'store_sales'}}],'intervals':"
operator|+
literal|"['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=2600.01; EXPR$1=4486.4400000000005"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that multiple aggregates with the same filter clause have them factored    * out in the presence of an outer filter, and that they no longer refer to those filters    */
annotation|@
name|Test
specifier|public
name|void
name|testMultipleFiltersFactoredOutWithOuterFilter
parameter_list|()
block|{
comment|// Logically Equivalent to
comment|// select sum("store_sales"), sum("store_cost")
comment|// from "foodmart" where "brand_name" = 'Super' and "store_state" = 'CA'
name|String
name|sql
init|=
literal|"select "
operator|+
literal|"sum(\"store_sales\") filter (where \"store_state\" = 'CA'), "
operator|+
literal|"sum(\"store_cost\") filter (where \"store_state\" = 'CA') "
operator|+
literal|"from \"foodmart\" "
operator|+
literal|"where \"brand_name\" = 'Super'"
decl_stmt|;
comment|// Aggregates should lose reference to any filter clause
name|String
name|expectedAggregateExplain
init|=
literal|"aggs=[[SUM($0), SUM($2)]]"
decl_stmt|;
name|String
name|expectedQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','filter':{'type':'and','fields':[{'type':'selector','dimension':"
operator|+
literal|"'store_state','value':'CA'},{'type':'selector','dimension':'brand_name','value':"
operator|+
literal|"'Super'}]},'aggregations':[{'type':'doubleSum','name':'EXPR$0','fieldName':"
operator|+
literal|"'store_sales'},{'type':'doubleSum','name':'EXPR$1','fieldName':'store_cost'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedQuery
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedAggregateExplain
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=2600.01; EXPR$1=1013.162"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that when the resulting filter from factoring filter clauses out is always false,    * that they are still pushed to Druid to handle.    */
annotation|@
name|Test
specifier|public
name|void
name|testOuterFilterFalseAfterFactorSimplification
parameter_list|()
block|{
comment|// Normally we would factor out "the_year"> 1997 into the outer filter to prune the data
comment|// before aggregation and simplify the expression, but in this case that would produce:
comment|// "the_year"> 1997 AND "the_year"<= 1997 -> false (after simplification)
comment|// Since Druid cannot handle a "false" filter, we revert back to the
comment|// pre-simplified version. i.e the filter should be "the_year"> 1997 and "the_year"<= 1997
comment|// and let Druid handle an unsatisfiable expression
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where \"the_year\"> 1997) "
operator|+
literal|"from \"foodmart\" where \"the_year\"<= 1997"
decl_stmt|;
name|String
name|expectedFilter
init|=
literal|"filter':{'type':'and','fields':[{'type':'bound','dimension':'the_year'"
operator|+
literal|",'lower':'1997','lowerStrict':true,'ordering':'numeric'},{'type':'bound',"
operator|+
literal|"'dimension':'the_year','upper':'1997','upperStrict':false,'ordering':'numeric'}]}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedFilter
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that aggregates with filter clauses that Druid cannot handle are not pushed in    * as filtered aggregates.    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseNotPushable
parameter_list|()
block|{
comment|// Currently the adapter does not support the LIKE operator
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") "
operator|+
literal|"filter (where \"the_year\" like '199_') from \"foodmart\""
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[LIKE"
operator|+
literal|"($83, '199_')], projects=[[$90, IS TRUE(LIKE($83, '199_'))]], groups=[{}], "
operator|+
literal|"aggs=[[SUM($0)]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedSubExplain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type"
operator|+
literal|"\":\"expression\",\"expression\":\"like(\\\"the_year\\\","
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseWithMetricRef
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") filter (where \"store_cost\"> 10) from \"foodmart\""
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[>"
operator|+
literal|"($91, 10)], projects=[[$90, IS TRUE(>($91, 10))]], groups=[{}], aggs=[[SUM($0)"
operator|+
literal|"]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedSubExplain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"timeseries\""
argument_list|,
literal|"\"filter\":{\"type\":\"bound\","
operator|+
literal|"\"dimension\":\"store_cost\",\"lower\":\"10\",\"lowerStrict\":true,"
operator|+
literal|"\"ordering\":\"numeric\"}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=25.060000000000002"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseWithMetricRefAndAggregates
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\"), \"product_id\" "
operator|+
literal|"from \"foodmart\" where \"product_id\"> 1553 and \"store_cost\"> 5 group by \"product_id\""
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(EXPR$0=[$1], product_id=[$0])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[AND(>"
operator|+
literal|"(CAST($1):BIGINT, 1553),>($91, 5))], groups=[{1}], aggs=[[SUM($90)]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedSubExplain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|,
literal|"{\"type\":\"bound\","
operator|+
literal|"\"dimension\":\"store_cost\",\"lower\":\"5\",\"lowerStrict\":true,"
operator|+
literal|"\"ordering\":\"numeric\"}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=10.16; product_id=1554\n"
operator|+
literal|"EXPR$0=45.05; product_id=1556\n"
operator|+
literal|"EXPR$0=88.5; product_id=1555"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseWithMetricAndTimeAndAggregates
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\"), \"product_id\""
operator|+
literal|"from \"foodmart\" where \"product_id\"> 1555 and \"store_cost\"> 5 and extract(year "
operator|+
literal|"from \"timestamp\") = 1997 "
operator|+
literal|"group by floor(\"timestamp\" to DAY),\"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|,
literal|"{\"type\":\"bound\","
operator|+
literal|"\"dimension\":\"store_cost\",\"lower\":\"5\",\"lowerStrict\":true,"
operator|+
literal|"\"ordering\":\"numeric\"}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=10.6; product_id=1556\n"
operator|+
literal|"EXPR$0=10.6; product_id=1556\n"
operator|+
literal|"EXPR$0=10.6; product_id=1556\n"
operator|+
literal|"EXPR$0=13.25; product_id=1556"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that an aggregate with a nested filter clause has it's filter factored out    */
annotation|@
name|Test
specifier|public
name|void
name|testNestedFilterClauseFactored
parameter_list|()
block|{
comment|// Logically equivalent to
comment|// select sum("store_sales") from "foodmart" where "store_state" in ('CA', 'OR')
name|String
name|sql
init|=
literal|"select sum(\"store_sales\") "
operator|+
literal|"filter (where \"store_state\" = 'CA' or \"store_state\" = 'OR') from \"foodmart\""
decl_stmt|;
name|String
name|expectedFilterJson
init|=
literal|"filter':{'type':'or','fields':[{'type':'selector','dimension':"
operator|+
literal|"'store_state','value':'CA'},{'type':'selector',"
operator|+
literal|"'dimension':'store_state','value':'OR'}]}"
decl_stmt|;
name|String
name|expectedAggregateJson
init|=
literal|"'aggregations':[{'type':'doubleSum',"
operator|+
literal|"'name':'EXPR$0','fieldName':'store_sales'}]"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedFilterJson
argument_list|)
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedAggregateJson
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=301444.9099999999"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that aggregates with nested filters have their filters factored out    * into the outer filter for data pruning while still holding a reference to the filter clause    */
annotation|@
name|Test
specifier|public
name|void
name|testNestedFilterClauseInAggregates
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select "
operator|+
literal|"sum(\"store_sales\") filter "
operator|+
literal|"(where \"store_state\" = 'CA' and \"the_month\" = 'October'), "
operator|+
literal|"sum(\"store_cost\") filter "
operator|+
literal|"(where \"store_state\" = 'CA' and \"the_day\" = 'Monday') "
operator|+
literal|"from \"foodmart\""
decl_stmt|;
comment|// (store_state = CA AND the_month = October) OR (store_state = CA AND the_day = Monday)
name|String
name|expectedFilterJson
init|=
literal|"filter':{'type':'or','fields':[{'type':'and','fields':[{'type':"
operator|+
literal|"'selector','dimension':'store_state','value':'CA'},{'type':'selector','dimension':"
operator|+
literal|"'the_month','value':'October'}]},{'type':'and','fields':[{'type':'selector',"
operator|+
literal|"'dimension':'store_state','value':'CA'},{'type':'selector','dimension':'the_day',"
operator|+
literal|"'value':'Monday'}]}]}"
decl_stmt|;
name|String
name|expectedAggregatesJson
init|=
literal|"'aggregations':[{'type':'filtered','filter':{'type':'and',"
operator|+
literal|"'fields':[{'type':'selector','dimension':'store_state','value':'CA'},{'type':"
operator|+
literal|"'selector','dimension':'the_month','value':'October'}]},'aggregator':{'type':"
operator|+
literal|"'doubleSum','name':'EXPR$0','fieldName':'store_sales'}},{'type':'filtered',"
operator|+
literal|"'filter':{'type':'and','fields':[{'type':'selector','dimension':'store_state',"
operator|+
literal|"'value':'CA'},{'type':'selector','dimension':'the_day','value':'Monday'}]},"
operator|+
literal|"'aggregator':{'type':'doubleSum','name':'EXPR$1','fieldName':'store_cost'}}]"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedFilterJson
argument_list|)
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedAggregatesJson
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=13077.789999999992; EXPR$1=9830.7799"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountWithNonNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(\"timestamp\") from \"foodmart\"\n"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=86829"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to make sure the "not" filter has only 1 field, rather than an array of fields.    */
annotation|@
name|Test
specifier|public
name|void
name|testNotFilterForm
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select count(distinct \"the_month\") from "
operator|+
literal|"\"foodmart\" where \"the_month\"<> \'October\'"
decl_stmt|;
name|String
name|druidFilter
init|=
literal|"'filter':{'type':'not',"
operator|+
literal|"'field':{'type':'selector','dimension':'the_month','value':'October'}}"
decl_stmt|;
comment|// Check that the filter actually worked, and that druid was responsible for the filter
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidFilter
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=11"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that count(distinct ...) gets pushed to Druid when approximate results are    * acceptable    * */
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCountWhenApproxResultsAccepted
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select count(distinct \"store_state\") from \"foodmart\""
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], projects=[[$63]], groups=[{}], aggs=[[COUNT(DISTINCT $0)]])"
decl_stmt|;
name|String
name|expectedAggregate
init|=
literal|"{'type':'cardinality','name':"
operator|+
literal|"'EXPR$0','fieldNames':['store_state']}"
decl_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|true
argument_list|,
name|sql
argument_list|,
name|expectedSubExplain
argument_list|,
name|expectedAggregate
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that count(distinct ...) doesn't get pushed to Druid when approximate results    * are not acceptable    */
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCountWhenApproxResultsNotAccepted
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select count(distinct \"store_state\") from \"foodmart\""
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"  BindableAggregate(group=[{}], EXPR$0=[COUNT($0)])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"groups=[{63}], aggs=[[]])"
decl_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|false
argument_list|,
name|sql
argument_list|,
name|expectedSubExplain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCountOnMetric
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(distinct \"store_sales\") from \"foodmart\" "
operator|+
literal|"where \"store_state\" = 'WA'"
decl_stmt|;
specifier|final
name|String
name|expectedSubExplainNoApprox
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableAggregate(group=[{}], EXPR$0=[COUNT($0)])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[=($63, 'WA')"
operator|+
literal|"], groups=[{90}], aggs=[[]])"
decl_stmt|;
specifier|final
name|String
name|expectedSubPlanWithApprox
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], filter=[=($63, 'WA')], groups=[{}], "
operator|+
literal|"aggs=[[COUNT(DISTINCT $90)]])"
decl_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|true
argument_list|,
name|sql
argument_list|,
name|expectedSubPlanWithApprox
argument_list|,
literal|"'queryType':'timeseries'"
argument_list|)
expr_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|false
argument_list|,
name|sql
argument_list|,
name|expectedSubExplainNoApprox
argument_list|,
literal|"'queryType':'groupBy'"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that a count on a metric does not get pushed into Druid    */
annotation|@
name|Test
specifier|public
name|void
name|testCountOnMetric
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"brand_name\", count(\"store_sales\") from \"foodmart\" "
operator|+
literal|"group by \"brand_name\""
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], groups=[{2}], aggs=[[COUNT($90)]])"
decl_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|true
argument_list|,
name|sql
argument_list|,
name|expectedSubExplain
argument_list|,
literal|"\"queryType\":\"groupBy\""
argument_list|)
expr_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|false
argument_list|,
name|sql
argument_list|,
name|expectedSubExplain
argument_list|,
literal|"\"queryType\":\"groupBy\""
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to ensure that count(*) is pushed into Druid    */
annotation|@
name|Test
specifier|public
name|void
name|testCountStar
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select count(*) from \"foodmart\""
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[0]], groups=[{}], aggs=[[COUNT()]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
name|expectedSubExplain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountOnMetricRenamed
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"B\", count(\"A\") from "
operator|+
literal|"(select \"unit_sales\" as \"A\", \"store_state\" as \"B\" from \"foodmart\") "
operator|+
literal|"group by \"B\""
decl_stmt|;
name|String
name|expectedSubExplain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], groups=[{63}], aggs=[[COUNT($89)]])"
decl_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|true
argument_list|,
name|sql
argument_list|,
name|expectedSubExplain
argument_list|)
expr_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|false
argument_list|,
name|sql
argument_list|,
name|expectedSubExplain
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinctCountOnMetricRenamed
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"B\", count(distinct \"A\") from "
operator|+
literal|"(select \"unit_sales\" as \"A\", \"store_state\" as \"B\" from \"foodmart\") "
operator|+
literal|"group by \"B\""
decl_stmt|;
specifier|final
name|String
name|expectedSubExplainNoApprox
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableAggregate(group=[{0}], EXPR$1=[COUNT($1)])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$63, $89]], groups=[{0, 1}], aggs=[[]])"
decl_stmt|;
specifier|final
name|String
name|expectedPlanWithApprox
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], groups=[{63}], aggs=[[COUNT(DISTINCT $89)]])"
decl_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|true
argument_list|,
name|sql
argument_list|,
name|expectedPlanWithApprox
argument_list|,
literal|"'queryType':'groupBy'"
argument_list|)
expr_stmt|;
name|testCountWithApproxDistinct
argument_list|(
literal|false
argument_list|,
name|sql
argument_list|,
name|expectedSubExplainNoApprox
argument_list|,
literal|"'queryType':'groupBy'"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testCountWithApproxDistinct
parameter_list|(
name|boolean
name|approx
parameter_list|,
name|String
name|sql
parameter_list|,
name|String
name|expectedExplain
parameter_list|)
block|{
name|testCountWithApproxDistinct
argument_list|(
name|approx
argument_list|,
name|sql
argument_list|,
name|expectedExplain
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|testCountWithApproxDistinct
parameter_list|(
name|boolean
name|approx
parameter_list|,
name|String
name|sql
parameter_list|,
name|String
name|expectedExplain
parameter_list|,
name|String
name|expectedDruidQuery
parameter_list|)
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|FOODMART
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|APPROXIMATE_DISTINCT_COUNT
operator|.
name|camelName
argument_list|()
argument_list|,
name|approx
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|explainContains
argument_list|(
name|expectedExplain
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|expectedDruidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to make sure that if a complex metric is also a dimension, then    * {@link org.apache.calcite.adapter.druid.DruidTable} should allow it to be used like any other    * column.    * */
annotation|@
name|Test
specifier|public
name|void
name|testComplexMetricAlsoDimension
parameter_list|()
block|{
name|foodmartApprox
argument_list|(
literal|"select \"customer_id\" from \"foodmart\""
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
name|foodmartApprox
argument_list|(
literal|"select count(distinct \"the_month\"), \"customer_id\" "
operator|+
literal|"from \"foodmart\" group by \"customer_id\""
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default','dimension':"
operator|+
literal|"'customer_id','outputName':'customer_id','outputType':'STRING'}],"
operator|+
literal|"'limitSpec':{'type':'default'},'aggregations':[{"
operator|+
literal|"'type':'cardinality','name':'EXPR$0','fieldNames':['the_month']}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z']}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test to make sure that the mapping from a Table name to a Table returned from    * {@link org.apache.calcite.adapter.druid.DruidSchema} is always the same Java object.    * */
annotation|@
name|Test
specifier|public
name|void
name|testTableMapReused
parameter_list|()
block|{
name|AbstractSchema
name|schema
init|=
operator|new
name|DruidSchema
argument_list|(
literal|"http://localhost:8082"
argument_list|,
literal|"http://localhost:8081"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|schema
operator|.
name|getTable
argument_list|(
literal|"wikiticker"
argument_list|)
argument_list|,
name|schema
operator|.
name|getTable
argument_list|(
literal|"wikiticker"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushEqualsCastDimension
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select sum(\"store_cost\") as a "
operator|+
literal|"from \"foodmart\" "
operator|+
literal|"where cast(\"product_id\" as double) = 1016.0"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[=(CAST($1):DOUBLE, 1016.0)], groups=[{}], aggs=[[SUM($91)]])"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,'granularity':'all',"
operator|+
literal|"'filter':{'type':'bound','dimension':'product_id','lower':'1016.0',"
operator|+
literal|"'lowerStrict':false,'upper':'1016.0','upperStrict':false,'ordering':'numeric'},"
operator|+
literal|"'aggregations':[{'type':'doubleSum','name':'A','fieldName':'store_cost'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=85.31639999999999"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sqlQuery2
init|=
literal|"select sum(\"store_cost\") as a "
operator|+
literal|"from \"foodmart\" "
operator|+
literal|"where cast(\"product_id\" as double)<= 1016.0 "
operator|+
literal|"and cast(\"product_id\" as double)>= 1016.0"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery2
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=85.31639999999999"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushNotEqualsCastDimension
parameter_list|()
block|{
specifier|final
name|String
name|sqlQuery
init|=
literal|"select sum(\"store_cost\") as a "
operator|+
literal|"from \"foodmart\" "
operator|+
literal|"where cast(\"product_id\" as double)<> 1016.0"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[<>(CAST($1):DOUBLE, 1016.0)], groups=[{}], aggs=[[SUM($91)]])"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,'granularity':'all',"
operator|+
literal|"'filter':{'type':'not','field':{'type':'bound','dimension':'product_id','"
operator|+
literal|"lower':'1016.0','lowerStrict':false,'upper':'1016.0','upperStrict':false,'ordering':'numeric'}},"
operator|+
literal|"'aggregations':[{'type':'doubleSum','name':'A','fieldName':'store_cost'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=225541.91720000014"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sqlQuery2
init|=
literal|"select sum(\"store_cost\") as a "
operator|+
literal|"from \"foodmart\" "
operator|+
literal|"where cast(\"product_id\" as double)< 1016.0 "
operator|+
literal|"or cast(\"product_id\" as double)> 1016.0"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery2
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"A=225541.91720000014"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c "
operator|+
literal|"from \"foodmart\" "
operator|+
literal|"where \"product_id\" is null"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,'granularity':'all',"
operator|+
literal|"'filter':{'type':'selector','dimension':'product_id','value':null},"
operator|+
literal|"'aggregations':[{'type':'count','name':'C'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=0"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsNotNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select count(*) as c "
operator|+
literal|"from \"foodmart\" "
operator|+
literal|"where \"product_id\" is not null"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,'granularity':'all',"
operator|+
literal|"'filter':{'type':'not','field':{'type':'selector','dimension':'product_id','value':null}},"
operator|+
literal|"'aggregations':[{'type':'count','name':'C'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],"
operator|+
literal|"'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"C=86829"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterWithFloorOnTime
parameter_list|()
block|{
comment|// Test filter on floor on time column is pushed to druid
specifier|final
name|String
name|sql
init|=
literal|"Select floor(\"timestamp\" to MONTH) as t from \"foodmart\" where "
operator|+
literal|"floor(\"timestamp\" to MONTH) between '1997-01-01 00:00:00'"
operator|+
literal|"and '1997-03-01 00:00:00' order by t limit 2"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan','dataSource':'foodmart','intervals':"
operator|+
literal|"['1997-01-01T00:00:00.000Z/1997-04-01T00:00:00.000Z'],'virtualColumns':"
operator|+
literal|"[{'type':'expression','name':'vc','expression':'timestamp_floor(\\'__time\\'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"T=1997-01-01 00:00:00"
argument_list|,
literal|"T=1997-01-01 00:00:00"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectFloorOnTimeWithFilterOnFloorOnTime
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"Select floor(\"timestamp\" to MONTH) as t from "
operator|+
literal|"\"foodmart\" where floor(\"timestamp\" to MONTH)>= '1997-05-01 00:00:00' order by t"
operator|+
literal|" limit 1"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$0], dir0=[ASC], fetch=[1])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], filter=[>=(FLOOR($0, FLAG(MONTH)), 1997-05-01 00:00:00)], "
operator|+
literal|"projects=[[FLOOR($0, FLAG(MONTH))]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"T=1997-05-01 00:00:00"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTimeWithFilterOnFloorOnTimeAndCastToTimestamp
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"Select floor(\"timestamp\" to MONTH) as t from "
operator|+
literal|"\"foodmart\" where floor(\"timestamp\" to MONTH)>= cast('1997-05-01 00:00:00' as TIMESTAMP) order by t"
operator|+
literal|" limit 1"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'scan','dataSource':'foodmart','intervals':"
operator|+
literal|"['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],'filter':{'type':'bound',"
operator|+
literal|"'dimension':'__time','lower':'1997-05-01T00:00:00.000Z',"
operator|+
literal|"'lowerStrict':false,'ordering':'lexicographic','"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"T=1997-05-01 00:00:00"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2122">[CALCITE-2122]    * DateRangeRules issues</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCombinationOfValidAndNotValidAndInterval
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\" "
operator|+
literal|"WHERE  \"timestamp\"< CAST('1998-01-02' as TIMESTAMP) AND "
operator|+
literal|"EXTRACT(MONTH FROM \"timestamp\") = 01 AND EXTRACT(YEAR FROM \"timestamp\") = 1996 "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"timeseries\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorToDateRangeWithTimeZone
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"Select floor(\"timestamp\" to MONTH) as t from "
operator|+
literal|"\"foodmart\" where floor(\"timestamp\" to MONTH)>= '1997-05-01 00:00:00' "
operator|+
literal|"and floor(\"timestamp\" to MONTH)< '1997-05-02 00:00:00' order by t"
operator|+
literal|" limit 1"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{\"queryType\":\"scan\",\"dataSource\":\"foodmart\",\"intervals\":"
operator|+
literal|"[\"1997-05-01T00:00:00.000Z/1997-06-01T00:00:00.000Z\"],\"virtualColumns\":[{\"type\":"
operator|+
literal|"\"expression\",\"name\":\"vc\",\"expression\":\"timestamp_floor(\\\"__time\\\""
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|FOODMART
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"T=1997-05-01 00:00:00"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where ABS(-EXP(LN(SQRT"
operator|+
literal|"(\"store_sales\")))) = 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"pow(\\\"store_sales\\\""
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=32"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsFilter2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where CAST(SQRT(ABS(-\"store_sales\"))"
operator|+
literal|" /2 as INTEGER) = 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"(CAST((pow(abs((- \\\"store_sales\\\")),0.5) / 2),"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=62449"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsLikeFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where \"product_id\" LIKE '1%'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"like"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=36839"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsSTRLENFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where CHAR_LENGTH(\"product_id\") = 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"expression\":\"(strlen(\\\"product_id\\\") == 2"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=4876"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsUpperLowerFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where upper(lower(\"city\")) = "
operator|+
literal|"'SPOKANE'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"(upper"
operator|+
literal|"(lower(\\\"city\\\")) =="
argument_list|,
literal|"SPOKANE"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=7394"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsLowerUpperFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where lower(upper(\"city\")) = "
operator|+
literal|"'spokane'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"(lower"
operator|+
literal|"(upper(\\\"city\\\")) =="
argument_list|,
literal|"spokane"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=7394"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsLowerFilterNotMatching
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where lower(\"city\") = 'Spokane'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"(lower"
operator|+
literal|"(\\\"city\\\") =="
argument_list|,
literal|"Spokane"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsLowerFilterMatching
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where lower(\"city\") = 'spokane'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"(lower"
operator|+
literal|"(\\\"city\\\") =="
argument_list|,
literal|"spokane"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=7394"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsUpperFilterNotMatching
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where upper(\"city\") = 'Spokane'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"(upper"
operator|+
literal|"(\\\"city\\\") =="
argument_list|,
literal|"Spokane"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsUpperFilterMatching
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where upper(\"city\") = 'SPOKANE'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"(upper"
operator|+
literal|"(\\\"city\\\") =="
argument_list|,
literal|"SPOKANE"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=7394"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsConcatFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where (\"city\" || '_extra') = "
operator|+
literal|"'Spokane_extra'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"type\":\"expression\",\"expression\":\"(concat"
operator|+
literal|"(\\\"city\\\","
argument_list|,
literal|"Spokane_extra"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=7394"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsNotNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where (\"city\" || 'extra') IS NOT NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"type\":\"expression\",\"expression\":\"(concat"
operator|+
literal|"(\\\"city\\\","
argument_list|,
literal|"!= null"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=86829"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testComplexExpressionsIsNull
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where ( cast(null as INTEGER) + cast"
operator|+
literal|"(\"city\" as INTEGER)) IS NULL"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableAggregate(group=[{}], EXPR$0=[$SUM0($1)])\n"
operator|+
literal|"    BindableFilter(condition=[IS NULL(+(null, CAST($0):INTEGER))])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"groups=[{29}], aggs=[[COUNT()]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"groupBy\",\"dataSource\":\"foodmart\","
operator|+
literal|"\"granularity\":\"all\",\"dimensions\":[{\"type\":\"default\","
operator|+
literal|"\"dimension\":\"city\",\"outputName\":\"city\",\"outputType\":\"STRING\"}],"
operator|+
literal|"\"limitSpec\":{\"type\":\"default\"},"
operator|+
literal|"\"aggregations\":[{\"type\":\"count\",\"name\":\"EXPR$0\"}],"
operator|+
literal|"\"intervals\":[\"1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z\"]}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=86829"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsConcatFilterMultipleColumns
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where (\"city\" || \"state_province\")"
operator|+
literal|" = 'SpokaneWA'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"(concat(\\\"city\\\",\\\"state_province\\\") =="
argument_list|,
literal|"SpokaneWA"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=7394"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAndCombinationOfExpAndSimpleFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where (\"city\" || \"state_province\")"
operator|+
literal|" = 'SpokaneWA' "
operator|+
literal|"AND \"state_province\" = 'WA'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"(concat(\\\"city\\\",\\\"state_province\\\") =="
argument_list|,
literal|"SpokaneWA"
argument_list|,
literal|"{\"type\":\"selector\",\"dimension\":\"state_province\",\"value\":\"WA\"}]}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=7394"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrCombinationOfExpAndSimpleFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where (\"city\" || \"state_province\")"
operator|+
literal|" = 'SpokaneWA' "
operator|+
literal|"OR (\"state_province\" = 'CA' AND \"city\" IS NOT NULL)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"(concat(\\\"city\\\",\\\"state_province\\\") =="
argument_list|,
literal|"SpokaneWA"
argument_list|,
literal|"{\"type\":\"and\",\"fields\":[{\"type\":\"selector\","
operator|+
literal|"\"dimension\":\"state_province\",\"value\":\"CA\"},{\"type\":\"not\","
operator|+
literal|"\"field\":{\"type\":\"selector\",\"dimension\":\"city\",\"value\":null}}]}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=31835"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnAEqColumnB
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where \"city\" = \"state_province\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\""
operator|+
literal|"(\\\"city\\\" == \\\"state_province\\\")\"}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnANotEqColumnB
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where \"city\"<> \"state_province\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\""
operator|+
literal|"(\\\"city\\\" != \\\"state_province\\\")\"}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=86829"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAndCombinationOfComplexExpAndSimpleFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where ((\"city\" || "
operator|+
literal|"\"state_province\") = 'SpokaneWA' OR (\"city\" || '_extra') = 'Spokane_extra') "
operator|+
literal|"AND \"state_province\" = 'WA'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], filter=[AND(OR(="
operator|+
literal|"(||($29, $30), 'SpokaneWA'), =(||($29, '_extra'), 'Spokane_extra')), =($30, 'WA'))"
operator|+
literal|"], groups=[{}], aggs=[[COUNT()]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"(concat(\\\"city\\\",\\\"state_province\\\") =="
argument_list|,
literal|"SpokaneWA"
argument_list|,
literal|"{\"type\":\"selector\",\"dimension\":\"state_province\","
operator|+
literal|"\"value\":\"WA\"}]}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=7394"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsFilterWithCast
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\" where CAST(( SQRT(\"store_sales\") - 1 "
operator|+
literal|") / 3 + 1 AS INTEGER)> 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"(CAST((((pow(\\\"store_sales\\\",0.5) - 1) / 3) + 1)"
argument_list|,
literal|"LONG"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=476"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsFilterWithCastTimeToDateToChar
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\" where CAST(CAST(\"timestamp\" as "
operator|+
literal|"DATE) as VARCHAR) = '1997-01-01'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[=(CAST(CAST($0):DATE NOT NULL):VARCHAR CHARACTER SET \"ISO-8859-1\" "
operator|+
literal|"COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL, '1997-01-01')], "
operator|+
literal|"groups=[{}], aggs=[[COUNT()]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"type\":\"expression\","
operator|+
literal|"\"expression\":\"(timestamp_format(timestamp_floor(\\\"__time\\\""
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=117"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionsFilterWithExtract
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM \"foodmart\"  where CAST((EXTRACT(MONTH FROM "
operator|+
literal|"\"timestamp\") - 1 ) / 3 + 1 AS INTEGER) = 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|",\"filter\":{\"type\":\"expression\",\"expression\":\"((("
operator|+
literal|"(timestamp_extract(\\\"__time\\\""
argument_list|,
literal|"MONTH"
argument_list|,
literal|") - 1) / 3) + 1) == 1"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=21587"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractYearFilterExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(*) from \"foodmart\" WHERE"
operator|+
literal|" EXTRACT(YEAR from \"timestamp\") + 1> 1997"
decl_stmt|;
specifier|final
name|String
name|filterPart1
init|=
literal|"'filter':{'type':'expression','expression':"
operator|+
literal|"'((timestamp_extract(\\'__time\\'"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|FOODMART
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=86829"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|filterPart1
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractMonthFilterExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(*) from \"foodmart\" WHERE"
operator|+
literal|" EXTRACT(MONTH from \"timestamp\") + 1 = 02"
decl_stmt|;
specifier|final
name|String
name|filterPart1
init|=
literal|"'filter':{'type':'expression','expression':"
operator|+
literal|"'((timestamp_extract(\\'__time\\'"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|enable
argument_list|(
name|enabled
argument_list|()
argument_list|)
operator|.
name|with
argument_list|(
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|FOODMART
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=7033"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|filterPart1
argument_list|,
literal|"MONTH"
argument_list|,
literal|"== 2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTimeFloorExpressions
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT FLOOR(\"timestamp\" to DAY) as d from \"foodmart\" WHERE "
operator|+
literal|"CAST(FLOOR(CAST(\"timestamp\" AS DATE) to MONTH) AS DATE) = "
operator|+
literal|" CAST('1997-01-01' as DATE) GROUP BY  floor(\"timestamp\" to DAY) order by d limit 3"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], filter=[=(CAST(FLOOR(CAST($0):DATE NOT NULL, "
operator|+
literal|"FLAG(MONTH))):DATE NOT NULL, 1997-01-01)], projects=[[FLOOR($0, FLAG(DAY))]], "
operator|+
literal|"groups=[{0}], aggs=[[]], sort0=[0], dir0=[ASC], fetch=[3])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"D=1997-01-01 00:00:00"
argument_list|,
literal|"D=1997-01-02 00:00:00"
argument_list|,
literal|"D=1997-01-03 00:00:00"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDruidTimeFloorAndTimeParseExpressions
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"timestamp\", count(*) "
operator|+
literal|"from \"foodmart\" WHERE "
operator|+
literal|"CAST(('1997' || '-01' || '-01') AS DATE) = CAST(\"timestamp\" AS DATE) "
operator|+
literal|"GROUP BY \"timestamp\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"timestamp=1997-01-01 00:00:00; EXPR$1=117"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\""
operator|+
literal|"(timestamp_floor(timestamp_parse(concat(concat("
argument_list|,
literal|"== timestamp_floor(\\\"__time\\\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDruidTimeFloorAndTimeParseExpressions2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"timestamp\", count(*) "
operator|+
literal|"from \"foodmart\" WHERE "
operator|+
literal|"CAST(('1997' || '-01' || '-01') AS TIMESTAMP) = CAST(\"timestamp\" AS TIMESTAMP) "
operator|+
literal|"GROUP BY \"timestamp\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\""
operator|+
literal|"(timestamp_parse(concat(concat("
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"timestamp=1997-01-01 00:00:00; EXPR$1=117"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterFloorOnMetricColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(*) from \"foodmart\" WHERE floor(\"store_sales\") = 23"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]],"
operator|+
literal|" filter=[=(FLOOR($90), 23)], groups=[{}], aggs=[[COUNT()]]"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=2"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"timeseries\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExpressionFilterSimpleColumnAEqColumnB
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(*) from \"foodmart\" where \"product_id\" = \"city\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[=($1, $29)], groups=[{}], aggs=[[COUNT()]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\","
operator|+
literal|"\"expression\":\"(\\\"product_id\\\" == \\\"city\\\")\"}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCastPlusMathOps
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"WHERE (CAST(\"product_id\" AS INTEGER) + 1 * \"store_sales\")/(\"store_cost\" - 5) "
operator|+
literal|"<= floor(\"store_sales\") * 25 + 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"expression\",\"expression\":\"(((CAST(\\\"product_id\\\", "
argument_list|,
literal|"LONG"
argument_list|,
literal|") + (1 * \\\"store_sales\\\")) / (\\\"store_cost\\\" - 5))"
argument_list|,
literal|"<= ((floor(\\\"store_sales\\\") * 25) + 2))\"}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[<=(/(+(CAST($1):INTEGER, *(1, $90)), -($91, 5)), +(*(FLOOR($90), 25), 2))], "
operator|+
literal|"groups=[{}], aggs=[[COUNT()]])"
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=82129"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBooleanFilterExpressions
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(*) from "
operator|+
name|FOODMART_TABLE
operator|+
literal|" WHERE (CAST((\"product_id\"<> '1') AS BOOLEAN)) IS TRUE"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]],"
operator|+
literal|" intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]],"
operator|+
literal|" filter=[IS TRUE(CAST(<>($1, '1')):BOOLEAN)],"
operator|+
literal|" groups=[{}], aggs=[[COUNT()]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"timeseries\""
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=86803"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCombinationOfValidAndNotValidFilters
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"WHERE ((CAST(\"product_id\" AS INTEGER) + 1 * \"store_sales\")/(\"store_cost\" - 5) "
operator|+
literal|"<= floor(\"store_sales\") * 25 + 2) AND \"timestamp\"< CAST('1997-01-02' as TIMESTAMP)"
operator|+
literal|"AND CAST(\"store_sales\"> 0 AS BOOLEAN) IS TRUE "
operator|+
literal|"AND \"product_id\" like '1%' AND \"store_cost\"> 1 "
operator|+
literal|"AND EXTRACT(MONTH FROM \"timestamp\") = 01 AND EXTRACT(DAY FROM \"timestamp\") = 01 "
operator|+
literal|"AND EXTRACT(MONTH FROM \"timestamp\") / 4 + 1 = 1"
decl_stmt|;
specifier|final
name|String
name|queryType
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart'"
decl_stmt|;
specifier|final
name|String
name|filterExp1
init|=
literal|"{'type':'expression','expression':'(((CAST(\\'product_id\\'"
decl_stmt|;
specifier|final
name|String
name|filterExpPart2
init|=
literal|" (1 * \\'store_sales\\')) / (\\'store_cost\\' - 5)) "
operator|+
literal|"<= ((floor(\\'store_sales\\') * 25) + 2))'}"
decl_stmt|;
specifier|final
name|String
name|likeExpressionFilter
init|=
literal|"{'type':'expression','expression':'like(\\'product_id\\'"
decl_stmt|;
specifier|final
name|String
name|likeExpressionFilter2
init|=
literal|"1%"
decl_stmt|;
specifier|final
name|String
name|simpleBound
init|=
literal|"{'type':'bound','dimension':'store_cost','lower':'1',"
operator|+
literal|"'lowerStrict':true,'ordering':'numeric'}"
decl_stmt|;
specifier|final
name|String
name|timeSimpleFilter
init|=
literal|"{'type':'bound','dimension':'__time','upper':'1997-01-02T00:00:00.000Z',"
operator|+
literal|"'upperStrict':true,'ordering':'lexicographic','extractionFn':{'type':'timeFormat','format':'yyyy-MM-dd"
decl_stmt|;
specifier|final
name|String
name|simpleExtractFilterMonth
init|=
literal|"{'type':'bound','dimension':'__time','lower':'1',"
operator|+
literal|"'lowerStrict':false,'upper':'1','upperStrict':false,'ordering':'numeric',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'M','timeZone':'UTC','locale':'en-US'}}"
decl_stmt|;
specifier|final
name|String
name|simpleExtractFilterDay
init|=
literal|"{'type':'bound','dimension':'__time','lower':'1',"
operator|+
literal|"'lowerStrict':false,'upper':'1','upperStrict':false,'ordering':'numeric',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'d','timeZone':'UTC','locale':'en-US'}}"
decl_stmt|;
specifier|final
name|String
name|quarterAsExpressionFilter
init|=
literal|"{'type':'expression','expression':"
operator|+
literal|"'(((timestamp_extract(\\'__time\\'"
decl_stmt|;
specifier|final
name|String
name|quarterAsExpressionFilter2
init|=
literal|"MONTH"
decl_stmt|;
specifier|final
name|String
name|quarterAsExpressionFilterTimeZone
init|=
literal|"UTC"
decl_stmt|;
specifier|final
name|String
name|quarterAsExpressionFilter3
init|=
literal|"/ 4) + 1) == 1)'}]}"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]],"
operator|+
literal|" filter=[AND(<=(/(+(CAST($1):INTEGER, *(1, $90)), -($91, 5)), +(*(FLOOR($90), 25), 2)),"
operator|+
literal|" IS TRUE(CAST(>($90, 0)):BOOLEAN),"
operator|+
literal|" LIKE($1, '1%'),>($91, 1),<($0, 1997-01-02 00:00:00),"
operator|+
literal|" =(EXTRACT(FLAG(MONTH), $0), 1),"
operator|+
literal|" =(EXTRACT(FLAG(DAY), $0), 1), =(+(/(EXTRACT(FLAG(MONTH), $0), 4), 1), 1))],"
operator|+
literal|" groups=[{}], aggs=[[COUNT()]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=36"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|queryType
argument_list|,
name|filterExp1
argument_list|,
name|filterExpPart2
argument_list|,
name|likeExpressionFilter
argument_list|,
name|likeExpressionFilter2
argument_list|,
name|simpleBound
argument_list|,
name|timeSimpleFilter
argument_list|,
name|simpleExtractFilterMonth
argument_list|,
name|simpleExtractFilterDay
argument_list|,
name|quarterAsExpressionFilter
argument_list|,
name|quarterAsExpressionFilterTimeZone
argument_list|,
name|quarterAsExpressionFilter2
argument_list|,
name|quarterAsExpressionFilter3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCeilFilterExpression
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" WHERE ceil(\"store_sales\")> 1"
operator|+
literal|" AND ceil(\"timestamp\" TO DAY)< CAST('1997-01-05' AS TIMESTAMP)"
operator|+
literal|" AND ceil(\"timestamp\" TO MONTH)< CAST('1997-03-01' AS TIMESTAMP)"
operator|+
literal|" AND ceil(\"timestamp\" TO HOUR)> CAST('1997-01-01' AS TIMESTAMP) "
operator|+
literal|" AND ceil(\"timestamp\" TO MINUTE)> CAST('1997-01-01' AS TIMESTAMP) "
operator|+
literal|" AND ceil(\"timestamp\" TO SECOND)> CAST('1997-01-01' AS TIMESTAMP) "
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1997-01-01T00:00:00.001Z/"
operator|+
literal|"1997-01-04T00:00:00.001Z]], filter=[>(CEIL($90), 1)], groups=[{}], aggs=[[COUNT()]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=408"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubStringExpressionFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) AS C, SUBSTRING(\"product_id\" from 1 for 4) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" WHERE SUBSTRING(\"product_id\" from 1 for 4) like '12%' "
operator|+
literal|" AND CHARACTER_LENGTH(\"product_id\") = 4"
operator|+
literal|" AND SUBSTRING(\"product_id\" from 3 for 1) = '2'"
operator|+
literal|" AND CAST(SUBSTRING(\"product_id\" from 2 for 1) AS INTEGER) = 2"
operator|+
literal|" AND CAST(SUBSTRING(\"product_id\" from 4 for 1) AS INTEGER) = 7"
operator|+
literal|" AND CAST(SUBSTRING(\"product_id\" from 4) AS INTEGER) = 7"
operator|+
literal|" Group by SUBSTRING(\"product_id\" from 1 for 4)"
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(C=[$1], EXPR$1=[$0])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[AND(LIKE(SUBSTRING($1, 1, 4), '12%'), =(CHAR_LENGTH($1), 4), "
operator|+
literal|"=(SUBSTRING($1, 3, 1), '2'), =(CAST(SUBSTRING($1, 2, 1)):INTEGER, 2), "
operator|+
literal|"=(CAST(SUBSTRING($1, 4, 1)):INTEGER, 7), =(CAST(SUBSTRING($1, 4)):INTEGER, 7))], "
operator|+
literal|"projects=[[SUBSTRING($1, 1, 4)]], groups=[{0}], aggs=[[COUNT()]])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"C=60; EXPR$1=1227"
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|,
literal|"substring(\\\"product_id\\\""
argument_list|,
literal|"\"(strlen(\\\"product_id\\\")"
argument_list|,
literal|",\"virtualColumns\":[{\"type\":\"expression\",\"name\":\"vc\","
operator|+
literal|"\"expression\":\"substring(\\\"product_id\\\", 0, 4)\","
operator|+
literal|"\"outputType\":\"STRING\"}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubStringWithNonConstantIndexes
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" WHERE SUBSTRING(\"product_id\" from CAST(\"store_cost\" as INT)/1000 + 2  "
operator|+
literal|"for CAST(\"product_id\" as INT)) like '1%'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=10893"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"timeseries\""
argument_list|,
literal|"like(substring(\\\"product_id\\\""
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[LIKE(SUBSTRING($1, +(/(CAST($91):INTEGER, 1000), 2), CAST($1):INTEGER), '1%')], "
operator|+
literal|"groups=[{}], aggs=[[COUNT()]])\n\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubStringWithNonConstantIndex
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" WHERE SUBSTRING(\"product_id\" from CAST(\"store_cost\" as INT)/1000 + 1) like '1%'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=36839"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"like(substring(\\\"product_id\\\""
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[LIKE(SUBSTRING($1, +(/(CAST($91):INTEGER, 1000), 1)), '1%')],"
operator|+
literal|" groups=[{}], aggs=[[COUNT()]])\n\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test case for https://issues.apache.org/jira/browse/CALCITE-2098.    * Need to make sure that when there we have a valid filter with no conjunction we still push    * all the valid filters.    */
annotation|@
name|Test
specifier|public
name|void
name|testFilterClauseWithNoConjunction
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select sum(\"store_sales\")"
operator|+
literal|"from \"foodmart\" where \"product_id\"> 1555 or \"store_cost\"> 5 or extract(year "
operator|+
literal|"from \"timestamp\") = 1997 "
operator|+
literal|"group by floor(\"timestamp\" to DAY),\"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|,
literal|"{\"type\":\"bound\","
operator|+
literal|"\"dimension\":\"store_cost\",\"lower\":\"5\",\"lowerStrict\":true,"
operator|+
literal|"\"ordering\":\"numeric\"}"
argument_list|)
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/**    * Test case for https://issues.apache.org/jira/browse/CALCITE-2123    */
annotation|@
name|Test
specifier|public
name|void
name|testBetweenFilterWithCastOverNumeric
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" WHERE \"product_id\" = 16.0"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"filter\":{\"type\":\"bound\",\"dimension\":\"product_id\",\"lower\":\"16.0\","
operator|+
literal|"\"lowerStrict\":false,\"upper\":\"16.0\","
operator|+
literal|"\"upperStrict\":false,\"ordering\":\"numeric\"}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTrigonometryMathFunctions
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"WHERE "
operator|+
literal|"SIN(\"store_cost\")> SIN(20) AND COS(\"store_sales\")> COS(20) "
operator|+
literal|"AND FLOOR(TAN(\"store_cost\")) = 2 "
operator|+
literal|"AND ABS(TAN(\"store_cost\") - SIN(\"store_cost\") / COS(\"store_cost\"))< 10e-7"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=2"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[AND(>(SIN($91), SIN(20)),>(COS($90), COS(20)), =(FLOOR(TAN($91)), 2), "
operator|+
literal|"<(ABS(-(TAN($91), /(SIN($91), COS($91)))), 1.0E-6))], "
operator|+
literal|"groups=[{}], aggs=[[COUNT()]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCastLiteralToTimestamp
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" WHERE \"timestamp\"< CAST('1997-01-02' as TIMESTAMP)"
operator|+
literal|" AND EXTRACT(MONTH FROM \"timestamp\") / 4 + 1 = 1 "
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=117"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'timeseries','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'granularity':'all','filter':{'type':'and','fields':"
operator|+
literal|"[{'type':'bound','dimension':'__time','upper':'1997-01-02T00:00:00.000Z',"
operator|+
literal|"'upperStrict':true,'ordering':'lexicographic',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'yyyy-MM-dd"
argument_list|,
literal|"{'type':'expression','expression':'(((timestamp_extract(\\'__time\\',"
argument_list|,
literal|"/ 4) + 1) == 1)'}]},"
argument_list|,
literal|"'aggregations':[{'type':'count','name':'EXPR$0'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],'context':{'skipEmptyBuckets':false}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNotTrueSimpleFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"WHERE "
operator|+
literal|"(\"product_id\" = 1020 ) IS NOT TRUE AND (\"product_id\" = 1020 ) IS FALSE"
decl_stmt|;
specifier|final
name|String
name|result
init|=
literal|"EXPR$0=86773"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
name|result
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"filter=[AND(IS NOT TRUE(=($1, 1020)), IS FALSE(=($1, 1020)))],"
operator|+
literal|" groups=[{}], aggs=[[COUNT()]])"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"SELECT COUNT(*) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"WHERE "
operator|+
literal|"\"product_id\"<> 1020"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
comment|// ADDING COMPLEX PROJECT PUSHDOWN
annotation|@
name|Test
specifier|public
name|void
name|testPushOfSimpleMathOps
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COS(\"store_sales\") + 1, SIN(\"store_cost\"), EXTRACT(DAY from \"timestamp\") + 1  as D FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"WHERE \"store_sales\"< 20 order by D limit 3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=1.060758881219386; EXPR$1=0.5172204046388567; D=2\n"
operator|+
literal|"EXPR$0=0.8316025520509229; EXPR$1=0.6544084288365644; D=2\n"
operator|+
literal|"EXPR$0=0.24267723077545622; EXPR$1=0.9286289016881148; D=2"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$2], dir0=[ASC], fetch=[3])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], filter=[<($90, 20)], projects=[[+(COS($90), 1), SIN($91),"
operator|+
literal|" +(EXTRACT(FLAG(DAY), $0), 1)]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushOfSimpleColumnAPlusColumnB
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COS(\"store_sales\" + \"store_cost\") + 1, EXTRACT(DAY from \"timestamp\") + 1  as D FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"WHERE \"store_sales\"< 20 order by D limit 3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|runs
argument_list|()
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=0.5357357987441458; D=2\n"
operator|+
literal|"EXPR$0=0.22760480207557643; D=2\n"
operator|+
literal|"EXPR$0=0.11259322182897047; D=2"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$1], dir0=[ASC], fetch=[3])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], filter=[<($90, 20)], projects=[[+(COS(+($90, $91)), 1), "
operator|+
literal|"+(EXTRACT(FLAG(DAY), $0), 1)]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectExtractMonth
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT  EXTRACT(YEAR FROM \"timestamp\") FROM "
operator|+
name|FOODMART_TABLE
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|limit
argument_list|(
literal|1
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=1997"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"DruidQuery(table=[[foodmart, foodmart]], intervals="
operator|+
literal|"[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"projects=[[EXTRACT(FLAG(YEAR), $0)]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"virtualColumns\":[{\"type\":\"expression\",\"name\":\"vc\","
operator|+
literal|"\"expression\":\"timestamp_extract(\\\"__time\\\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggOnArithmeticProject
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(\"store_sales\" + 1) FROM "
operator|+
name|FOODMART_TABLE
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=652067.1299999984"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z]], "
operator|+
literal|"projects=[[+($90, 1)]], groups=[{}], aggs=[[SUM($0)]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"timeseries\""
argument_list|,
literal|"\"doubleSum\",\"name\":\"EXPR$0\",\"expression\":\"(\\\"store_sales\\\" + 1)\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggOnArithmeticProject2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(-\"store_sales\" * 2) as S FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"Group by \"timestamp\" order by s LIMIT 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"S=-15918.020000000002\nS=-14115.959999999988"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"BindableProject(S=[$1])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$0, *(-($90), 2)]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($1)]], sort0=[1], dir0=[ASC], fetch=[2])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|,
literal|"'granularity':'all'"
argument_list|,
literal|"{'dimension':'S','direction':'ascending','dimensionOrder':'numeric'}"
argument_list|,
literal|"{'type':'doubleSum','name':'S','expression':'((- \\'store_sales\\') * 2)'}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggOnArithmeticProject3
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(-\"store_sales\" * 2)-Max(\"store_cost\" * \"store_cost\") AS S,"
operator|+
literal|"Min(\"store_sales\" + \"store_cost\") as S2 FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"Group by \"timestamp\" order by s LIMIT 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"S=-16003.314460250002; S2=1.4768000000000001"
argument_list|,
literal|"S=-14181.569999999989; S2=0.8093999999999999"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$0, *(-($90), 2), *($91, $91), +($90, $91)]],"
operator|+
literal|" groups=[{0}], aggs=[[SUM($1), MAX($2), MIN($3)]], post_projects=[[-($1, $2), $3]],"
operator|+
literal|" sort0=[0], dir0=[ASC], fetch=[2])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|",\"aggregations\":[{\"type\":\"doubleSum\",\"name\":\"$f1\","
operator|+
literal|"\"expression\":\"((- \\\"store_sales\\\") * 2)\"},{\"type\":\"doubleMax\",\"name\""
operator|+
literal|":\"$f2\",\"expression\":\"(\\\"store_cost\\\" * \\\"store_cost\\\")\"},"
operator|+
literal|"{\"type\":\"doubleMin\",\"name\":\"S2\",\"expression\":\"(\\\"store_sales\\\" "
operator|+
literal|"+ \\\"store_cost\\\")\"}],\"postAggregations\":[{\"type\":\"expression\","
operator|+
literal|"\"name\":\"S\",\"expression\":\"(\\\"$f1\\\" - \\\"$f2\\\")\"}]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupByVirtualColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"product_id\" || '_' ||\"city\", SUM(\"store_sales\" + "
operator|+
literal|"CAST(\"cost\" AS DOUBLE)) as S FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"GROUP BY \"product_id\" || '_' || \"city\" LIMIT 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=1000_Albany; S=12385.21"
argument_list|,
literal|"EXPR$0=1000_Altadena; S=8.07"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[||(||($1, '_'), $29), "
operator|+
literal|"+($90, CAST($53):DOUBLE)]], groups=[{0}], aggs=[[SUM($1)]], fetch=[2])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'groupBy'"
argument_list|,
literal|"{'type':'doubleSum','name':'S','expression':'(\\'store_sales\\' + CAST(\\'cost\\'"
argument_list|,
literal|"'expression':'concat(concat(\\'product_id\\'"
argument_list|,
literal|"{'type':'default','dimension':'vc','outputName':'vc','outputType':'STRING'}],"
operator|+
literal|"'virtualColumns':[{'type':'expression','name':'vc"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCountOverVirtualColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(\"product_id\" || '_' || \"city\") FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|"WHERE \"state_province\" = 'CA'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=24441"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], filter=[=($30, 'CA')], projects=[[||(||($1, '_'), $29)]],"
operator|+
literal|" groups=[{}], aggs=[[COUNT($0)]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"timeseries\""
argument_list|,
literal|"\"aggregator\":{\"type\":\"count\",\"name\":\"EXPR$0\",\"expression\":"
operator|+
literal|"\"concat(concat(\\\"product_id\\\""
argument_list|,
literal|"\"aggregations\":[{\"type\":\"filtered\",\"filter\":{\"type\":\"not\",\"field\":"
operator|+
literal|"{\"type\":\"expression\",\"expression\":\"concat(concat(\\\"product_id\\\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggOverStringToLong
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(cast(\"product_id\" AS INTEGER)) FROM "
operator|+
name|FOODMART_TABLE
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'timeseries','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'granularity':'all','aggregations':[{'type':'longSum',"
operator|+
literal|"'name':'EXPR$0','expression':'CAST(\\'product_id\\'"
argument_list|,
literal|"LONG"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=68222919"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggOnTimeExtractColumn2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT MAX(EXTRACT(MONTH FROM \"timestamp\")) FROM \"foodmart\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=12"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'timeseries','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'granularity':'all','aggregations':[{"
operator|+
literal|"'type':'longMax','name':'EXPR$0','expression':'timestamp_extract(\\'__time\\'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStackedAggregateFilters
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT COUNT(\"product_id\") filter (WHERE \"state_province\" = 'CA' "
operator|+
literal|"OR \"store_sales\"> 100 AND \"product_id\"<> '100'), count(*) FROM "
operator|+
name|FOODMART_TABLE
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"{'queryType':'timeseries','dataSource':'foodmart','descending':false,"
operator|+
literal|"'granularity':'all','aggregations':[{'type':'filtered','filter':{'type':'or','fields':"
operator|+
literal|"[{'type':'selector','dimension':'state_province','value':'CA'},{'type':'and','fields':"
operator|+
literal|"[{'type':'bound','dimension':'store_sales','lower':'100','lowerStrict':true,"
operator|+
literal|"'ordering':'numeric'},{'type':'not','field':{'type':'selector','dimension':'product_id',"
operator|+
literal|"'value':'100'}}]}]},'aggregator':{'type':'filtered','filter':{'type':'not',"
operator|+
literal|"'field':{'type':'selector','dimension':'product_id','value':null}},'aggregator':"
operator|+
literal|"{'type':'count','name':'EXPR$0','fieldName':'product_id'}}},"
operator|+
literal|"{'type':'count','name':'EXPR$1'}],'intervals':['1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z'],'context':{'skipEmptyBuckets':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=24441; EXPR$1=86829"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCastOverPostAggregates
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT CAST(COUNT(*) + SUM(\"store_sales\") as INTEGER) FROM "
operator|+
name|FOODMART_TABLE
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=652067"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$90]], groups=[{}], "
operator|+
literal|"aggs=[[COUNT(), SUM($0)]], post_projects=[[CAST(+($0, $1)):INTEGER]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubStringOverPostAggregates
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"product_id\", SUBSTRING(\"product_id\" from 1 for 2) FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" GROUP BY \"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|limit
argument_list|(
literal|3
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"product_id=1; EXPR$1=1\nproduct_id=10; EXPR$1=10\nproduct_id=100; EXPR$1=10"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$1]], groups=[{0}], aggs=[[]], "
operator|+
literal|"post_projects=[[$0, SUBSTRING($0, 1, 2)]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableQueryExtractYearQuarter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM (SELECT CAST((MONTH(\"timestamp\") - 1) / 3 + 1 AS BIGINT)"
operator|+
literal|"AS qr_timestamp_ok,  SUM(\"store_sales\") AS sum_store_sales, YEAR(\"timestamp\") AS yr_timestamp_ok"
operator|+
literal|" FROM \"foodmart\" GROUP BY CAST((MONTH(\"timestamp\") - 1) / 3 + 1 AS BIGINT),"
operator|+
literal|" YEAR(\"timestamp\")) LIMIT_ZERO LIMIT 1"
decl_stmt|;
specifier|final
name|String
name|extract_year
init|=
literal|"{\"type\":\"extraction\",\"dimension\":\"__time\",\"outputName\":"
operator|+
literal|"\"extract_year\",\"extractionFn\":{\"type\":\"timeFormat\",\"format\":\"yyyy\","
operator|+
literal|"\"timeZone\":\"UTC\",\"locale\":\"en-US\"}}"
decl_stmt|;
specifier|final
name|String
name|extract_expression
init|=
literal|"\"expression\":\"(((timestamp_extract(\\\"__time\\\","
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"QR_TIMESTAMP_OK=1; SUM_STORE_SALES=139628.34999999971; YR_TIMESTAMP_OK=1997"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|,
name|extract_year
argument_list|,
name|extract_expression
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"BindableProject(QR_TIMESTAMP_OK=[$0], SUM_STORE_SALES=[$2], "
operator|+
literal|"YR_TIMESTAMP_OK=[$1])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[CAST(+(/(-(EXTRACT(FLAG(MONTH), $0), 1), 3), 1))"
operator|+
literal|":BIGINT NOT NULL, EXTRACT(FLAG(YEAR), $0), $90]], groups=[{0, 1}], aggs=[[SUM($2)]], fetch=[1])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableauQueryExtractMonthDayYear
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM (SELECT (((YEAR(\"foodmart\".\"timestamp\") * 10000) + "
operator|+
literal|"(MONTH(\"foodmart\".\"timestamp\") * 100)) + "
operator|+
literal|"EXTRACT(DAY FROM \"foodmart\".\"timestamp\")) AS md_t_timestamp_ok,\n"
operator|+
literal|"  SUM(\"foodmart\".\"store_sales\") AS sum_t_other_ok\n"
operator|+
literal|"FROM \"foodmart\"\n"
operator|+
literal|"GROUP BY (((YEAR(\"foodmart\".\"timestamp\") * 10000) + (MONTH(\"foodmart\".\"timestamp\")"
operator|+
literal|" * 100)) + EXTRACT(DAY FROM\"foodmart\".\"timestamp\"))) LIMIT 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"MD_T_TIMESTAMP_OK=19970101; SUM_T_OTHER_OK=706.34"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[+(+(*(EXTRACT(FLAG(YEAR), $0), 10000), "
operator|+
literal|"*(EXTRACT(FLAG(MONTH), $0), 100)), EXTRACT(FLAG(DAY), $0)), $90]], groups=[{0}], "
operator|+
literal|"aggs=[[SUM($1)]], fetch=[1])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableauQuerySubStringHourMinutes
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM (SELECT CAST(SUBSTRING(CAST(\"foodmart\".\"timestamp\" "
operator|+
literal|" AS VARCHAR) from 12 for 2) AS INT) AS hr_t_timestamp_ok,\n"
operator|+
literal|"  MINUTE(\"foodmart\".\"timestamp\") AS mi_t_timestamp_ok,\n"
operator|+
literal|"  SUM(\"foodmart\".\"store_sales\") AS sum_t_other_ok, EXTRACT(HOUR FROM \"timestamp\") "
operator|+
literal|" AS hr_t_timestamp_ok2 FROM  \"foodmart\" GROUP BY "
operator|+
literal|" CAST(SUBSTRING(CAST(\"foodmart\".\"timestamp\" AS VARCHAR) from 12 for 2 ) AS INT),"
operator|+
literal|"  MINUTE(\"foodmart\".\"timestamp\"), EXTRACT(HOUR FROM \"timestamp\")) LIMIT 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"HR_T_TIMESTAMP_OK=0; MI_T_TIMESTAMP_OK=0; "
operator|+
literal|"SUM_T_OTHER_OK=565238.1299999986; HR_T_TIMESTAMP_OK2=0"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"BindableProject(HR_T_TIMESTAMP_OK=[$0], MI_T_TIMESTAMP_OK=[$1], "
operator|+
literal|"SUM_T_OTHER_OK=[$3], HR_T_TIMESTAMP_OK2=[$2])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[CAST(SUBSTRING(CAST($0):VARCHAR"
operator|+
literal|" CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" "
operator|+
literal|"NOT NULL, 12, 2)):INTEGER NOT NULL, EXTRACT(FLAG(MINUTE), $0), "
operator|+
literal|"EXTRACT(FLAG(HOUR), $0), $90]], groups=[{0, 1, 2}], aggs=[[SUM($3)]], fetch=[1])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTableauQueryMinutesSecondsExtract
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT * FROM (SELECT SECOND(\"timestamp\") AS sc_t_timestamp_ok,"
operator|+
literal|"MINUTE(\"timestamp\") AS mi_t_timestamp_ok,  SUM(\"store_sales\") AS sum_store_sales "
operator|+
literal|" FROM \"foodmart\" GROUP BY SECOND(\"timestamp\"), MINUTE(\"timestamp\"))"
operator|+
literal|" LIMIT_ZERO LIMIT 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"SC_T_TIMESTAMP_OK=0; MI_T_TIMESTAMP_OK=0; SUM_STORE_SALES=565238.1299999986"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[EXTRACT(FLAG(SECOND), $0), "
operator|+
literal|"EXTRACT(FLAG(MINUTE), $0), $90]], groups=[{0, 1}], aggs=[[SUM($2)]], fetch=[1])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCastConcatOverPostAggregates
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT CAST(COUNT(*) + SUM(\"store_sales\") as VARCHAR) || '_' || CAST(SUM(\"store_cost\") "
operator|+
literal|"AS VARCHAR) FROM "
operator|+
name|FOODMART_TABLE
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=652067.1299999986_225627.2336000002"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$90, $91]], groups=[{}], aggs=[[COUNT(), "
operator|+
literal|"SUM($0), SUM($1)]], post_projects=[[||(||(CAST(+($0, $1)):VARCHAR CHARACTER SET "
operator|+
literal|"\"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", '_'), CAST($2):"
operator|+
literal|"VARCHAR CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\")]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testHavingSpecs
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"product_id\" AS P, SUM(\"store_sales\") AS S FROM \"foodmart\" "
operator|+
literal|" GROUP BY  \"product_id\" HAVING  SUM(\"store_sales\")> 220  ORDER BY P LIMIT 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"P=1; S=236.55"
argument_list|,
literal|"P=10; S=230.04"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$1, $90]], groups=[{0}], aggs=[[SUM($1)]], "
operator|+
literal|"filter=[>($1, 220)], sort0=[0], dir0=[ASC], fetch=[2])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'having':{'type':'filter','filter':{'type':'bound',"
operator|+
literal|"'dimension':'S','lower':'220','lowerStrict':true,'ordering':'numeric'}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTransposableHavingFilter
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"product_id\" AS P, SUM(\"store_sales\") AS S FROM \"foodmart\" "
operator|+
literal|" GROUP BY  \"product_id\" HAVING  SUM(\"store_sales\")> 220 AND \"product_id\"> '10'"
operator|+
literal|"  ORDER BY P LIMIT 2"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"P=100; S=343.19999999999993"
argument_list|,
literal|"P=1000; S=532.62"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], filter=[>($1, '10')], projects=[[$1, $90]], groups=[{0}],"
operator|+
literal|" aggs=[[SUM($1)]], filter=[>($1, 220)], sort0=[0], dir0=[ASC], fetch=[2])\n"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProjectSameColumnMultipleTimes
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"product_id\" as prod_id1, \"product_id\" as prod_id2, "
operator|+
literal|"\"store_sales\" as S1, \"store_sales\" as S2 FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" order by prod_id1 LIMIT 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$0], dir0=[ASC], fetch=[1])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$1, $1, $90, $90]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'scan','dataSource':'foodmart','intervals':"
operator|+
literal|"['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],'virtualColumns':["
operator|+
literal|"{'type':'expression','name':'vc','expression':'\\'product_id\\'','outputType':"
operator|+
literal|"'STRING'},{'type':'expression','name':'vc0','expression':'\\'store_sales\\'',"
operator|+
literal|"'outputType':'DOUBLE'}],'columns':['product_id','vc','store_sales','vc0'],"
operator|+
literal|"'resultFormat':'compactedList'}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"PROD_ID1=1; PROD_ID2=1; S1=11.4; S2=11.4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testProjectSameMetricsColumnMultipleTimes
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"product_id\" as prod_id1, \"product_id\" as prod_id2, "
operator|+
literal|"\"store_sales\" as S1, \"store_sales\" as S2 FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" order by prod_id1 LIMIT 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$0], dir0=[ASC], fetch=[1])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$1, $1, $90, $90]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"scan\",\"dataSource\":\"foodmart\",\"intervals\":"
operator|+
literal|"[\"1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z\"],\"virtualColumns\":"
operator|+
literal|"[{\"type\":\"expression\",\"name\":\"vc\",\"expression\":\"\\\"product_id\\\"\","
operator|+
literal|"\"outputType\":\"STRING\"},{\"type\":\"expression\",\"name\":\"vc0\","
operator|+
literal|"\"expression\":\"\\\"store_sales\\\"\",\"outputType\":\"DOUBLE\"}],\"columns\":"
operator|+
literal|"[\"product_id\",\"vc\",\"store_sales\",\"vc0\"],\"resultFormat\":\"compactedList\"}"
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"PROD_ID1=1; PROD_ID2=1; S1=11.4; S2=11.4"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggSameColumnMultipleTimes
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT \"product_id\" as prod_id1, \"product_id\" as prod_id2, "
operator|+
literal|"SUM(\"store_sales\") as S1, SUM(\"store_sales\") as S2 FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" GROUP BY \"product_id\" ORDER BY prod_id2 LIMIT 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"BindableProject(PROD_ID1=[$0], PROD_ID2=[$0], S1=[$1], S2=[$1])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000Z/"
operator|+
literal|"2992-01-10T00:00:00.000Z]], projects=[[$1, $90]], groups=[{0}], aggs=[[SUM($1)]], "
operator|+
literal|"sort0=[0], dir0=[ASC], fetch=[1])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"\"queryType\":\"groupBy\""
argument_list|)
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"PROD_ID1=1; PROD_ID2=1; S1=236.55; S2=236.55"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGroupBy1
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT SUM(\"store_sales\") FROM \"foodmart\" "
operator|+
literal|"GROUP BY 1 HAVING (COUNT(1)> 0)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=565238.1299999986"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'vc','outputName':'vc','outputType':'LONG'}],"
operator|+
literal|"'virtualColumns':[{'type':'expression','name':'vc','expression':'1','outputType':'LONG'}],"
operator|+
literal|"'limitSpec':{'type':'default'},'aggregations':[{'type':'doubleSum','name':'EXPR$0',"
operator|+
literal|"'fieldName':'store_sales'},{'type':'count','name':'$f2'}],'intervals':"
operator|+
literal|"['1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z'],'having':"
operator|+
literal|"{'type':'filter','filter':{'type':'bound','dimension':'$f2','lower':'0',"
operator|+
literal|"'lowerStrict':true,'ordering':'numeric'}}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorQuarter
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT floor(\"timestamp\" TO quarter), SUM(\"store_sales\") FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" GROUP BY floor(\"timestamp\" TO quarter)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"timeseries\",\"dataSource\":\"foodmart\",\"descending\":false,"
operator|+
literal|"\"granularity\":{\"type\":\"period\",\"period\":\"P3M\",\"timeZone\":\"UTC\"},"
operator|+
literal|"\"aggregations\":[{\"type\":\"doubleSum\",\"name\":\"EXPR$1\",\"fieldName\":\"store_sales\"}],"
operator|+
literal|"\"intervals\":[\"1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z\"],\"context\":{\"skipEmptyBuckets\":true}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloorQuarterPlusDim
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT floor(\"timestamp\" TO quarter),\"product_id\",  SUM(\"store_sales\") FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" GROUP BY floor(\"timestamp\" TO quarter), \"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"groupBy\",\"dataSource\":\"foodmart\",\"granularity\":\"all\",\"dimensions\":"
operator|+
literal|"[{\"type\":\"extraction\",\"dimension\":\"__time\",\"outputName\":\"floor_quarter\",\"extractionFn\":{\"type\":\"timeFormat\""
argument_list|,
literal|"\"granularity\":{\"type\":\"period\",\"period\":\"P3M\",\"timeZone\":\"UTC\"},\"timeZone\":\"UTC\",\"locale\":\"und\"}},"
operator|+
literal|"{\"type\":\"default\",\"dimension\":\"product_id\",\"outputName\":\"product_id\",\"outputType\":\"STRING\"}],"
operator|+
literal|"\"limitSpec\":{\"type\":\"default\"},\"aggregations\":[{\"type\":\"doubleSum\",\"name\":\"EXPR$2\",\"fieldName\":\"store_sales\"}],"
operator|+
literal|"\"intervals\":[\"1900-01-09T00:00:00.000Z/2992-01-10T00:00:00.000Z\"]}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractQuarterPlusDim
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT EXTRACT(quarter from \"timestamp\"),\"product_id\",  SUM(\"store_sales\") FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" WHERE \"product_id\" = 1"
operator|+
literal|" GROUP BY EXTRACT(quarter from \"timestamp\"), \"product_id\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=1; product_id=1; EXPR$2=37.050000000000004\n"
operator|+
literal|"EXPR$0=2; product_id=1; EXPR$2=62.7\n"
operator|+
literal|"EXPR$0=3; product_id=1; EXPR$2=88.35\n"
operator|+
literal|"EXPR$0=4; product_id=1; EXPR$2=48.45"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"groupBy\",\"dataSource\":\"foodmart\",\"granularity\":\"all\",\"dimensions\":"
operator|+
literal|"[{\"type\":\"default\",\"dimension\":\"vc\",\"outputName\":\"vc\",\"outputType\":\"LONG\"},"
operator|+
literal|"{\"type\":\"default\",\"dimension\":\"product_id\",\"outputName\":\"product_id\",\"outputType\":\"STRING\"}],"
operator|+
literal|"\"virtualColumns\":[{\"type\":\"expression\",\"name\":\"vc\",\"expression\":\"timestamp_extract(\\\"__time\\\","
argument_list|,
literal|"QUARTER"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractQuarter
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"SELECT EXTRACT(quarter from \"timestamp\"),  SUM(\"store_sales\") FROM "
operator|+
name|FOODMART_TABLE
operator|+
literal|" GROUP BY EXTRACT(quarter from \"timestamp\")"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"EXPR$0=1; EXPR$1=139628.34999999971\n"
operator|+
literal|"EXPR$0=2; EXPR$1=132666.26999999944\n"
operator|+
literal|"EXPR$0=3; EXPR$1=140271.88999999964\n"
operator|+
literal|"EXPR$0=4; EXPR$1=152671.61999999985"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{\"queryType\":\"groupBy\",\"dataSource\":\"foodmart\",\"granularity\":\"all\","
operator|+
literal|"\"dimensions\":[{\"type\":\"default\",\"dimension\":\"vc\",\"outputName\":\"vc\",\"outputType\":\"LONG\"}],"
operator|+
literal|"\"virtualColumns\":[{\"type\":\"expression\",\"name\":\"vc\",\"expression\":\"timestamp_extract(\\\"__time\\\","
argument_list|,
literal|"QUARTER"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCastTimestamp
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"Select cast(cast(\"timestamp\" as timestamp) as varchar) as t"
operator|+
literal|" from \"foodmart\" order by t limit 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|FOODMART
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"T=1997-01-01 00:00:00"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"UTC"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DruidAdapterIT2.java
end_comment

end_unit

