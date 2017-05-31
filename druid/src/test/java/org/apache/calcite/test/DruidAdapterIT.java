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
name|Ignore
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
comment|/**  * Tests for the {@code org.apache.calcite.adapter.druid} package.  *  *<p>Before calling this test, you need to populate Druid, as follows:  *  *<blockquote><code>  * git clone https://github.com/vlsi/calcite-test-dataset<br>  * cd calcite-test-dataset<br>  * mvn install  *</code></blockquote>  *  *<p>This will create a virtual machine with Druid and test data set.  *  *<p>Features not yet implemented:  *<ul>  *<li>push LIMIT into "select" query</li>  *<li>push SORT and/or LIMIT into "groupBy" query</li>  *<li>push HAVING into "groupBy" query</li>  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|DruidAdapterIT
block|{
comment|/** URL of the "druid-foodmart" model. */
specifier|public
specifier|static
specifier|final
name|URL
name|FOODMART
init|=
name|DruidAdapterIT
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/druid-foodmart-model.json"
argument_list|)
decl_stmt|;
comment|/** URL of the "druid-wiki" model    * and the "wikiticker" data set. */
specifier|public
specifier|static
specifier|final
name|URL
name|WIKI
init|=
name|DruidAdapterIT
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/druid-wiki-model.json"
argument_list|)
decl_stmt|;
comment|/** URL of the "druid-wiki-no-columns" model    * and the "wikiticker" data set. */
specifier|public
specifier|static
specifier|final
name|URL
name|WIKI_AUTO
init|=
name|DruidAdapterIT
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/druid-wiki-no-columns-model.json"
argument_list|)
decl_stmt|;
comment|/** URL of the "druid-wiki-no-tables" model    * and the "wikiticker" data set. */
specifier|public
specifier|static
specifier|final
name|URL
name|WIKI_AUTO2
init|=
name|DruidAdapterIT
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/druid-wiki-no-tables-model.json"
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
comment|/** Tests a query against the {@link #WIKI} data set.    *    *<p>Most of the others in this suite are against {@link #FOODMART},    * but our examples in "druid-adapter.md" use wikiticker. */
annotation|@
name|Test
specifier|public
name|void
name|testSelectDistinctWiki
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
literal|"  DruidQuery(table=[[wiki, wiki]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], "
operator|+
literal|"filter=[=($13, 'Jeremy Corbyn')], groups=[{5}], aggs=[[]])\n"
decl_stmt|;
name|checkSelectDistinctWiki
argument_list|(
name|WIKI
argument_list|,
literal|"wiki"
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
name|testSelectDistinctWikiNoColumns
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
literal|"  DruidQuery(table=[[wiki, wiki]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], "
operator|+
literal|"filter=[=($17, 'Jeremy Corbyn')], groups=[{7}], aggs=[[]])\n"
decl_stmt|;
name|checkSelectDistinctWiki
argument_list|(
name|WIKI_AUTO
argument_list|,
literal|"wiki"
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
name|testSelectDistinctWikiNoTables
parameter_list|()
block|{
comment|// Compared to testSelectDistinctWiki, table name is different (because it
comment|// is the raw dataSource name from Druid) and the field offsets are
comment|// different. This is expected.
comment|// Interval is different, as default is taken.
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"countryName\"\n"
operator|+
literal|"from \"wikiticker\"\n"
operator|+
literal|"where \"page\" = 'Jeremy Corbyn'"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[wiki, wikiticker]], "
operator|+
literal|"intervals=[[1900-01-01T00:00:00.000/3000-01-01T00:00:00.000]], "
operator|+
literal|"filter=[=($17, 'Jeremy Corbyn')], groups=[{7}], aggs=[[]])\n"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy',"
operator|+
literal|"'dataSource':'wikiticker','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'countryName'}],'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'selector','dimension':'page','value':'Jeremy Corbyn'},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-01T00:00:00.000/3000-01-01T00:00:00.000']}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"countryName=United Kingdom"
argument_list|,
literal|"countryName=null"
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
comment|// Because no tables are declared, foodmart is automatically present.
name|sql
argument_list|(
literal|"select count(*) as c from \"foodmart\""
argument_list|,
name|WIKI_AUTO2
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
name|testSelectTimestampColumnNoTables1
parameter_list|()
block|{
comment|// Since columns are not explicitly declared, we use the default time
comment|// column in the query.
specifier|final
name|String
name|sql
init|=
literal|"select sum(\"added\")\n"
operator|+
literal|"from \"wikiticker\"\n"
operator|+
literal|"group by floor(\"__time\" to DAY)"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(EXPR$0=[$1])\n"
operator|+
literal|"    DruidQuery(table=[[wiki, wikiticker]], intervals=[[1900-01-01T00:00:00.000/3000-01-01T00:00:00.000]], projects=[[FLOOR($0, FLAG(DAY)), $1]], groups=[{0}], aggs=[[SUM($1)]])\n"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries',"
operator|+
literal|"'dataSource':'wikiticker','descending':false,'granularity':'day',"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'EXPR$0','fieldName':'added'}],"
operator|+
literal|"'intervals':['1900-01-01T00:00:00.000/3000-01-01T00:00:00.000'],"
operator|+
literal|"'context':{'skipEmptyBuckets':true}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
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
name|testSelectTimestampColumnNoTables2
parameter_list|()
block|{
comment|// Since columns are not explicitly declared, we use the default time
comment|// column in the query.
specifier|final
name|String
name|sql
init|=
literal|"select \"__time\"\n"
operator|+
literal|"from \"wikiticker\"\n"
operator|+
literal|"limit 1\n"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[wiki, wikiticker]], intervals=[[1900-01-01T00:00:00.000/3000-01-01T00:00:00.000]], projects=[[$0]], fetch=[1])\n"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'select',"
operator|+
literal|"'dataSource':'wikiticker','descending':false,"
operator|+
literal|"'intervals':['1900-01-01T00:00:00.000/3000-01-01T00:00:00.000'],"
operator|+
literal|"'dimensions':[],'metrics':[],'granularity':'all','pagingSpec':{'threshold':1,'fromNext':true},"
operator|+
literal|"'context':{'druid.query.fetch':true}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"__time=2015-09-12 00:46:58"
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
name|testSelectTimestampColumnNoTables3
parameter_list|()
block|{
comment|// Since columns are not explicitly declared, we use the default time
comment|// column in the query.
specifier|final
name|String
name|sql
init|=
literal|"select floor(\"__time\" to DAY) as \"day\", sum(\"added\")\n"
operator|+
literal|"from \"wikiticker\"\n"
operator|+
literal|"group by floor(\"__time\" to DAY)"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[wiki, wikiticker]], intervals=[[1900-01-01T00:00:00.000/3000-01-01T00:00:00.000]], projects=[[FLOOR($0, FLAG(DAY)), $1]], groups=[{0}], aggs=[[SUM($1)]])\n"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries',"
operator|+
literal|"'dataSource':'wikiticker','descending':false,'granularity':'day',"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'EXPR$1','fieldName':'added'}],"
operator|+
literal|"'intervals':['1900-01-01T00:00:00.000/3000-01-01T00:00:00.000'],"
operator|+
literal|"'context':{'skipEmptyBuckets':true}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"day=2015-09-12 00:00:00; EXPR$1=9385573"
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
name|testSelectTimestampColumnNoTables4
parameter_list|()
block|{
comment|// Since columns are not explicitly declared, we use the default time
comment|// column in the query.
specifier|final
name|String
name|sql
init|=
literal|"select sum(\"added\") as \"s\", \"page\", "
operator|+
literal|"floor(\"__time\" to DAY) as \"day\"\n"
operator|+
literal|"from \"wikiticker\"\n"
operator|+
literal|"group by \"page\", floor(\"__time\" to DAY)\n"
operator|+
literal|"order by \"s\" desc"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(s=[$2], page=[$0], day=[$1])\n"
operator|+
literal|"    DruidQuery(table=[[wiki, wikiticker]], "
operator|+
literal|"intervals=[[1900-01-01T00:00:00.000/3000-01-01T00:00:00.000]], projects=[[$17, FLOOR"
operator|+
literal|"($0, FLAG(DAY)), $1]], groups=[{0, 1}], aggs=[[SUM($2)]], sort0=[2], dir0=[DESC])"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|limit
argument_list|(
literal|1
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"s=199818; page=User:QuackGuru/Electronic cigarettes 1; "
operator|+
literal|"day=2015-09-12 00:00:00"
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
literal|"'limitSpec':{'type':'default',"
operator|+
literal|"'columns':[{'dimension':'s','direction':'descending','dimensionOrder':'numeric'}]}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSkipEmptyBuckets
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select floor(\"__time\" to SECOND) as \"second\", sum(\"added\")\n"
operator|+
literal|"from \"wikiticker\"\n"
operator|+
literal|"where \"page\" = 'Jeremy Corbyn'\n"
operator|+
literal|"group by floor(\"__time\" to SECOND)"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'timeseries',"
operator|+
literal|"'dataSource':'wikiticker','descending':false,'granularity':'second',"
operator|+
literal|"'filter':{'type':'selector','dimension':'page','value':'Jeremy Corbyn'},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'EXPR$1','fieldName':'added'}],"
operator|+
literal|"'intervals':['1900-01-01T00:00:00.000/3000-01-01T00:00:00.000'],"
operator|+
literal|"'context':{'skipEmptyBuckets':true}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|limit
argument_list|(
literal|1
argument_list|)
comment|// Result without 'skipEmptyBuckets':true => "second=2015-09-12 00:46:58; EXPR$1=0"
operator|.
name|returnsUnordered
argument_list|(
literal|"second=2015-09-12 01:20:19; EXPR$1=1075"
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
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|checkSelectDistinctWiki
parameter_list|(
name|URL
name|url
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"countryName\"\n"
operator|+
literal|"from \""
operator|+
name|tableName
operator|+
literal|"\"\n"
operator|+
literal|"where \"page\" = 'Jeremy Corbyn'"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy',"
operator|+
literal|"'dataSource':'wikiticker','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'countryName'}],'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'selector','dimension':'page','value':'Jeremy Corbyn'},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
decl_stmt|;
return|return
name|sql
argument_list|(
name|sql
argument_list|,
name|url
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"countryName=United Kingdom"
argument_list|,
literal|"countryName=null"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
name|druidQuery
argument_list|)
argument_list|)
return|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1617">[CALCITE-1617]    * Druid adapter: Send timestamp literals to Druid as local time, not    * UTC</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testFilterTime
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select \"__time\"\n"
operator|+
literal|"from \"wikiticker\"\n"
operator|+
literal|"where \"__time\"< '2015-10-12 00:00:00'"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[wiki, wikiticker]], "
operator|+
literal|"intervals=[[1900-01-01T00:00:00.000/2015-10-12T00:00:00.000]], "
operator|+
literal|"projects=[[$0]])\n"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'select',"
operator|+
literal|"'dataSource':'wikiticker','descending':false,"
operator|+
literal|"'intervals':['1900-01-01T00:00:00.000/2015-10-12T00:00:00.000'],"
operator|+
literal|"'dimensions':[],'metrics':[],'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},"
operator|+
literal|"'context':{'druid.query.fetch':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"__time=2015-09-12 00:46:58"
argument_list|,
literal|"__time=2015-09-12 00:47:00"
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
name|testFilterTimeDistinct
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select distinct \"__time\"\n"
operator|+
literal|"from \"wikiticker\"\n"
operator|+
literal|"where \"__time\"< '2015-10-12 00:00:00'"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[wiki, wikiticker]], "
operator|+
literal|"intervals=[[1900-01-01T00:00:00.000/2015-10-12T00:00:00.000]], "
operator|+
literal|"groups=[{0}], aggs=[[]])\n"
decl_stmt|;
specifier|final
name|String
name|subDruidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'wikiticker',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract',"
operator|+
literal|"'extractionFn':{'type':'timeFormat'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"__time=2015-09-12 00:46:58"
argument_list|,
literal|"__time=2015-09-12 00:47:00"
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
literal|"TIMESTAMP(0)"
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
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{30}], aggs=[[]])"
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
literal|"'dimensions':[{'type':'default','dimension':'state_province'}],'limitSpec':{'type':'default'},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
name|Ignore
argument_list|(
literal|"TODO: fix invalid cast from Integer to Long"
argument_list|)
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
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], projects=[[$29, CAST($88):INTEGER]], groups=[{0}], aggs=[[SUM($1)]])"
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
literal|"PLAN=EnumerableInterpreter\n  BindableAggregate(group=[{0, 1}])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], filter=[=($1, 1020)],"
operator|+
literal|" projects=[[$90, $1]])\n"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'select','dataSource':'foodmart','descending':false,"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'filter':{'type':'selector','dimension':'product_id','value':'1020'},"
operator|+
literal|"'dimensions':['product_id'],'metrics':['store_sales'],'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},"
operator|+
literal|"'context':{'druid.query.fetch':false}}"
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
literal|"store_sales=0.5099999904632568; product_id=1020"
argument_list|,
literal|"store_sales=1.0199999809265137; product_id=1020"
argument_list|,
literal|"store_sales=1.5299999713897705; product_id=1020"
argument_list|,
literal|"store_sales=2.0399999618530273; product_id=1020"
argument_list|,
literal|"store_sales=2.549999952316284; product_id=1020"
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
literal|"'dimension':'product_id'}],"
operator|+
literal|"'limitSpec':{'type':'default'},'filter':{'type':'selector',"
operator|+
literal|"'dimension':'product_id','value':'1020'},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg',"
operator|+
literal|"'fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
name|returnsUnordered
argument_list|(
literal|"product_id=1020"
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
literal|"'dimensions':[{'type':'default','dimension':'product_id'}],"
operator|+
literal|"'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'selector','dimension':'product_id','value':'1020'},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$39, $30]], "
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
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'gender'},{'type':'default',"
operator|+
literal|"'dimension':'state_province'}],'limitSpec':{'type':'default',"
operator|+
literal|"'columns':[{'dimension':'state_province','direction':'ascending',"
operator|+
literal|"'dimensionOrder':'alphanumeric'},{'dimension':'gender',"
operator|+
literal|"'direction':'descending','dimensionOrder':'alphanumeric'}]},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg',"
operator|+
literal|"'fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"PLAN=EnumerableLimit(offset=[2], fetch=[3])\n"
operator|+
literal|"  EnumerableInterpreter\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$39, $30]], "
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
literal|"{'queryType':'select','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'dimensions':['state_province','product_name'],'metrics':[],'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},'context':{'druid.query.fetch':false}}"
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
literal|"{'queryType':'select','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'dimensions':['gender','state_province'],'metrics':[],'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':3,'fromNext':true},'context':{'druid.query.fetch':true}}"
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
literal|"'granularity':'all','dimensions':[{'type':'default','dimension':'gender'},"
operator|+
literal|"{'type':'default','dimension':'state_province'}],'limitSpec':{'type':'default',"
operator|+
literal|"'limit':3,'columns':[]},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$39, $30]], "
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
literal|"'dimension':'brand_name'},{'type':'default','dimension':'gender'}],"
operator|+
literal|"'limitSpec':{'type':'default','limit':3,'columns':[{'dimension':'S',"
operator|+
literal|"'direction':'descending','dimensionOrder':'numeric'}]},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'S','fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], "
operator|+
literal|"groups=[{2, 39}], aggs=[[SUM($89)]], sort0=[2], dir0=[DESC], fetch=[3])\n"
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
literal|"'dimension':{'type':'default','dimension':'brand_name'},'metric':'S',"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'S','fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'threshold':3}"
decl_stmt|;
specifier|final
name|String
name|exactDruid
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'brand_name'}],'limitSpec':{'type':'default','limit':3,"
operator|+
literal|"'columns':[{'dimension':'S','direction':'descending',"
operator|+
literal|"'dimensionOrder':'numeric'}]},'aggregations':[{'type':'longSum',"
operator|+
literal|"'name':'S','fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], "
operator|+
literal|"groups=[{2}], aggs=[[SUM($89)]], sort0=[1], dir0=[DESC], fetch=[3])\n"
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
literal|"select \"brand_name\", floor(\"timestamp\" to DAY) as d,"
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
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'day','dimensions':[{'type':'default','dimension':'brand_name'}],"
operator|+
literal|"'limitSpec':{'type':'default'},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'S','fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$2, FLOOR"
operator|+
literal|"($0, FLAG(DAY)), $89]], groups=[{0, 1}], aggs=[[SUM($2)]], sort0=[2], dir0=[DESC], "
operator|+
literal|"fetch=[30])"
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
literal|"select \"brand_name\", floor(\"timestamp\" to DAY) as d,"
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
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'brand_name'},{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'floor_day','extractionFn':{'type':'timeFormat'"
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
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$2, FLOOR"
operator|+
literal|"($0, FLAG(DAY)), $89]], groups=[{0, 1}], aggs=[[SUM($2)]], sort0=[2], dir0=[DESC], "
operator|+
literal|"fetch=[30])"
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
literal|"'dimension':'brand_name'},{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'floor_day','extractionFn':{'type':'timeFormat'"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$2, FLOOR"
operator|+
literal|"($0, FLAG(DAY)), $89]], groups=[{0, 1}], aggs=[[SUM($2)]], sort0=[0], dir0=[ASC])"
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
literal|"select * from \"foodmart\"\n"
operator|+
literal|"where \"product_id\" BETWEEN '1500' AND '1502'\n"
operator|+
literal|"order by \"state_province\" desc, \"product_id\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'select','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'filter':{'type':'and','fields':["
operator|+
literal|"{'type':'bound','dimension':'product_id','lower':'1500','lowerStrict':false,'ordering':'lexicographic'},"
operator|+
literal|"{'type':'bound','dimension':'product_id','upper':'1502','upperStrict':false,'ordering':'lexicographic'}]},"
operator|+
literal|"'dimensions':['product_id','brand_name','product_name','SKU','SRP','gross_weight','net_weight',"
operator|+
literal|"'recyclable_package','low_fat','units_per_case','cases_per_pallet','shelf_width','shelf_height',"
operator|+
literal|"'shelf_depth','product_class_id','product_subcategory','product_category','product_department',"
operator|+
literal|"'product_family','customer_id','account_num','lname','fname','mi','address1','address2','address3',"
operator|+
literal|"'address4','city','state_province','postal_code','country','customer_region_id','phone1','phone2',"
operator|+
literal|"'birthdate','marital_status','yearly_income','gender','total_children','num_children_at_home',"
operator|+
literal|"'education','date_accnt_opened','member_card','occupation','houseowner','num_cars_owned',"
operator|+
literal|"'fullname','promotion_id','promotion_district_id','promotion_name','media_type','cost','start_date',"
operator|+
literal|"'end_date','store_id','store_type','region_id','store_name','store_number','store_street_address',"
operator|+
literal|"'store_city','store_state','store_postal_code','store_country','store_manager','store_phone',"
operator|+
literal|"'store_fax','first_opened_date','last_remodel_date','store_sqft','grocery_sqft','frozen_sqft',"
operator|+
literal|"'meat_sqft','coffee_bar','video_store','salad_bar','prepared_food','florist','time_id','the_day',"
operator|+
literal|"'the_month','the_year','day_of_month','week_of_year','month_of_year','quarter','fiscal_period'],"
operator|+
literal|"'metrics':['unit_sales','store_sales','store_cost'],'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},'context':{'druid.query.fetch':false}}"
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
literal|"select * from \"foodmart\"\n"
operator|+
literal|"where \"product_id\" BETWEEN 1500 AND 1502\n"
operator|+
literal|"order by \"state_province\" desc, \"product_id\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'select','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'filter':{'type':'and','fields':["
operator|+
literal|"{'type':'bound','dimension':'product_id','lower':'1500','lowerStrict':false,'ordering':'numeric'},"
operator|+
literal|"{'type':'bound','dimension':'product_id','upper':'1502','upperStrict':false,'ordering':'numeric'}]},"
operator|+
literal|"'dimensions':['product_id','brand_name','product_name','SKU','SRP','gross_weight','net_weight',"
operator|+
literal|"'recyclable_package','low_fat','units_per_case','cases_per_pallet','shelf_width','shelf_height',"
operator|+
literal|"'shelf_depth','product_class_id','product_subcategory','product_category','product_department',"
operator|+
literal|"'product_family','customer_id','account_num','lname','fname','mi','address1','address2','address3',"
operator|+
literal|"'address4','city','state_province','postal_code','country','customer_region_id','phone1','phone2',"
operator|+
literal|"'birthdate','marital_status','yearly_income','gender','total_children','num_children_at_home',"
operator|+
literal|"'education','date_accnt_opened','member_card','occupation','houseowner','num_cars_owned',"
operator|+
literal|"'fullname','promotion_id','promotion_district_id','promotion_name','media_type','cost','start_date',"
operator|+
literal|"'end_date','store_id','store_type','region_id','store_name','store_number','store_street_address',"
operator|+
literal|"'store_city','store_state','store_postal_code','store_country','store_manager','store_phone',"
operator|+
literal|"'store_fax','first_opened_date','last_remodel_date','store_sqft','grocery_sqft','frozen_sqft',"
operator|+
literal|"'meat_sqft','coffee_bar','video_store','salad_bar','prepared_food','florist','time_id','the_day',"
operator|+
literal|"'the_month','the_year','day_of_month','week_of_year','month_of_year','quarter','fiscal_period'],"
operator|+
literal|"'metrics':['unit_sales','store_sales','store_cost'],'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},'context':{'druid.query.fetch':false}}"
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
literal|"select * from \"foodmart\"\n"
operator|+
literal|"where \"product_id\" = -1"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'select','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'filter':{'type':'selector','dimension':'product_id','value':'-1'},"
operator|+
literal|"'dimensions':['product_id','brand_name','product_name','SKU','SRP',"
operator|+
literal|"'gross_weight','net_weight','recyclable_package','low_fat','units_per_case',"
operator|+
literal|"'cases_per_pallet','shelf_width','shelf_height','shelf_depth',"
operator|+
literal|"'product_class_id','product_subcategory','product_category',"
operator|+
literal|"'product_department','product_family','customer_id','account_num',"
operator|+
literal|"'lname','fname','mi','address1','address2','address3','address4',"
operator|+
literal|"'city','state_province','postal_code','country','customer_region_id',"
operator|+
literal|"'phone1','phone2','birthdate','marital_status','yearly_income','gender',"
operator|+
literal|"'total_children','num_children_at_home','education','date_accnt_opened',"
operator|+
literal|"'member_card','occupation','houseowner','num_cars_owned','fullname',"
operator|+
literal|"'promotion_id','promotion_district_id','promotion_name','media_type','cost',"
operator|+
literal|"'start_date','end_date','store_id','store_type','region_id','store_name',"
operator|+
literal|"'store_number','store_street_address','store_city','store_state',"
operator|+
literal|"'store_postal_code','store_country','store_manager','store_phone',"
operator|+
literal|"'store_fax','first_opened_date','last_remodel_date','store_sqft','grocery_sqft',"
operator|+
literal|"'frozen_sqft','meat_sqft','coffee_bar','video_store','salad_bar','prepared_food',"
operator|+
literal|"'florist','time_id','the_day','the_month','the_year','day_of_month',"
operator|+
literal|"'week_of_year','month_of_year','quarter','fiscal_period'],"
operator|+
literal|"'metrics':['unit_sales','store_sales','store_cost'],'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},'context':{'druid.query.fetch':false}}"
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
literal|"select * from \"foodmart\"\n"
operator|+
literal|"where cast(\"product_id\" as integer) - 1500 BETWEEN 0 AND 2\n"
operator|+
literal|"order by \"state_province\" desc, \"product_id\""
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'select','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'dimensions':['product_id','brand_name','product_name','SKU','SRP','gross_weight',"
operator|+
literal|"'net_weight','recyclable_package','low_fat','units_per_case','cases_per_pallet',"
operator|+
literal|"'shelf_width','shelf_height','shelf_depth','product_class_id','product_subcategory',"
operator|+
literal|"'product_category','product_department','product_family','customer_id','account_num',"
operator|+
literal|"'lname','fname','mi','address1','address2','address3','address4','city','state_province',"
operator|+
literal|"'postal_code','country','customer_region_id','phone1','phone2','birthdate','marital_status',"
operator|+
literal|"'yearly_income','gender','total_children','num_children_at_home','education',"
operator|+
literal|"'date_accnt_opened','member_card','occupation','houseowner','num_cars_owned','fullname',"
operator|+
literal|"'promotion_id','promotion_district_id','promotion_name','media_type','cost','start_date',"
operator|+
literal|"'end_date','store_id','store_type','region_id','store_name','store_number','store_street_address',"
operator|+
literal|"'store_city','store_state','store_postal_code','store_country','store_manager','store_phone',"
operator|+
literal|"'store_fax','first_opened_date','last_remodel_date','store_sqft','grocery_sqft','frozen_sqft',"
operator|+
literal|"'meat_sqft','coffee_bar','video_store','salad_bar','prepared_food','florist','time_id','the_day',"
operator|+
literal|"'the_month','the_year','day_of_month','week_of_year','month_of_year','quarter','fiscal_period'],"
operator|+
literal|"'metrics':['unit_sales','store_sales','store_cost'],'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},'context':{'druid.query.fetch':false}}"
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
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{39}], aggs=[[]])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{37}], aggs=[[]])"
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
literal|"      DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{39}], aggs=[[]])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{37}], aggs=[[]])"
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
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'context':{'skipEmptyBuckets':true}}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[]], groups=[{}], aggs=[[COUNT()]])"
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
literal|"select count(*) as \"c\", floor(\"timestamp\" to MONTH) as \"month\"\n"
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
literal|"'queryType':'select'"
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
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{30}], "
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
name|String
name|druidQuery
init|=
literal|"'aggregations':["
operator|+
literal|"{'type':'longSum','name':'$f1','fieldName':'unit_sales'},"
operator|+
literal|"{'type':'count','name':'$f2','fieldName':'unit_sales'},"
operator|+
literal|"{'type':'count','name':'C','fieldName':'store_sqft'},"
operator|+
literal|"{'type':'count','name':'C0'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"state_province=CA; A=3; S=74748; C=24441; C0=24441"
argument_list|,
literal|"state_province=OR; A=3; S=67659; C=21610; C0=21610"
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
literal|"group by floor(\"timestamp\" to MONTH)"
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'select','dataSource':'foodmart'"
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
name|returnsUnordered
argument_list|(
literal|"S=21081; C=5793"
argument_list|,
literal|"S=23763; C=6762"
argument_list|,
literal|"S=25270; C=7026"
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
literal|"order by floor(\"timestamp\" to MONTH) ASC"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    BindableAggregate(group=[{0}], S=[SUM($1)], C=[COUNT($2)])\n"
operator|+
literal|"      BindableProject(M=[FLOOR($0, FLAG(MONTH))], unit_sales=[$2], store_sqft=[$1])\n"
operator|+
literal|"        DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$0, $71, $89]])"
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
argument_list|,
literal|"M=1997-04-01 00:00:00; S=20179; C=5523"
argument_list|,
literal|"M=1997-05-01 00:00:00; S=21081; C=5793"
argument_list|,
literal|"M=1997-06-01 00:00:00; S=21350; C=5863"
argument_list|,
literal|"M=1997-07-01 00:00:00; S=23763; C=6762"
argument_list|,
literal|"M=1997-08-01 00:00:00; S=21697; C=5915"
argument_list|,
literal|"M=1997-09-01 00:00:00; S=20388; C=5591"
argument_list|,
literal|"M=1997-10-01 00:00:00; S=19958; C=5606"
argument_list|,
literal|"M=1997-11-01 00:00:00; S=25270; C=7026"
argument_list|,
literal|"M=1997-12-01 00:00:00; S=26796; C=7338"
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
literal|"  BindableSort(sort0=[$0], dir0=[ASC], fetch=[3])\n"
operator|+
literal|"    BindableAggregate(group=[{0}], S=[SUM($1)], C=[COUNT($2)])\n"
operator|+
literal|"      BindableProject(M=[FLOOR($0, FLAG(MONTH))], unit_sales=[$2], store_sqft=[$1])\n"
operator|+
literal|"        DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$0, $71, $89]])"
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
literal|"group by floor(\"timestamp\" to DAY)"
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'select','dataSource':'foodmart'"
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
name|returnsUnordered
argument_list|(
literal|"S=1244; C=391"
argument_list|,
literal|"S=550; C=112"
argument_list|,
literal|"S=580; C=171"
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
literal|"group by floor(\"timestamp\" to MONTH)"
decl_stmt|;
name|String
name|druidQuery
init|=
literal|"{'queryType':'select','dataSource':'foodmart'"
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
name|returnsUnordered
argument_list|(
literal|"S=21081; C=5793"
argument_list|,
literal|"S=23763; C=6762"
argument_list|,
literal|"S=25270; C=7026"
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
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(S=[$2], M=[$3], P=[$0])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$30, FLOOR"
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
literal|"'dimension':'state_province'},{'type':'extraction','dimension':'__time',"
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
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"intervals=[[1997-01-01T00:00:00.000/1997-09-01T00:00:00.000]], projects=[[$30, FLOOR"
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
comment|// Note: We don't push down HAVING yet
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
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"    BindableFilter(condition=[>($1, 23000)])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{30}], aggs=[[COUNT()]])"
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
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(C=[$2], state_province=[$1], city=[$0])\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{29, 30}], aggs=[[COUNT()]], sort0=[2], dir0=[DESC], fetch=[2])"
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
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], groups=[{29, 30}], "
operator|+
literal|"aggs=[[]])"
decl_stmt|;
specifier|final
name|String
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default','dimension':'city'},"
operator|+
literal|"{'type':'default','dimension':'state_province'}],"
operator|+
literal|"'limitSpec':{'type':'default'},'aggregations':[{'type':'longSum',"
operator|+
literal|"'name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"PLAN="
operator|+
literal|"EnumerableInterpreter\n"
operator|+
literal|"  BindableProject(product_name=[$0], ZERO=[0])\n"
operator|+
literal|"    BindableSort(sort0=[$0], dir0=[ASC])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$3]])"
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
name|explainContains
argument_list|(
name|explain
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_name=ADJ Rosy Sunglasses; ZERO=0"
argument_list|,
literal|"product_name=ADJ Rosy Sunglasses; ZERO=0"
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
name|druidQuery
init|=
literal|"{'queryType':'groupBy','dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'state_province'},"
operator|+
literal|"{'type':'default','dimension':'city'},"
operator|+
literal|"{'type':'default','dimension':'product_name'}],'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'and','fields':[{'type':'selector','dimension':'product_name',"
operator|+
literal|"'value':'High Top Dried Mushrooms'},{'type':'or','fields':[{'type':'selector',"
operator|+
literal|"'dimension':'quarter','value':'Q2'},{'type':'selector','dimension':'quarter',"
operator|+
literal|"'value':'Q3'}]},{'type':'selector','dimension':'state_province','value':'WA'}]},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]],"
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
literal|"{'queryType':'select',"
operator|+
literal|"'dataSource':'foodmart',"
operator|+
literal|"'descending':false,"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
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
literal|"'dimensions':['state_province','city','product_name'],"
operator|+
literal|"'metrics':[],"
operator|+
literal|"'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},'context':{'druid.query.fetch':false}}"
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], "
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
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], filter=[AND(="
operator|+
literal|"(EXTRACT_DATE(FLAG(YEAR), /INT(Reinterpret($0), 86400000)), 1997), OR(=(EXTRACT_DATE"
operator|+
literal|"(FLAG(MONTH), /INT(Reinterpret($0), 86400000)), 4), =(EXTRACT_DATE(FLAG(MONTH), /INT"
operator|+
literal|"(Reinterpret($0), 86400000)), 6)))], groups=[{}], aggs=[[COUNT()]])"
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
literal|"C=13500"
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
literal|"  DruidQuery(table=[[foodmart, foodmart]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], filter=[=('High Top Dried Mushrooms', $3)], projects=[[$30]])"
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
comment|/** Tests a query that exposed several bugs in the interpreter. */
annotation|@
name|Test
specifier|public
name|void
name|testWhereGroupBy
parameter_list|()
block|{
name|String
name|sql
init|=
literal|"select \"wikiticker\".\"countryName\" as \"c0\",\n"
operator|+
literal|" sum(\"wikiticker\".\"count\") as \"m1\",\n"
operator|+
literal|" sum(\"wikiticker\".\"deleted\") as \"m2\",\n"
operator|+
literal|" sum(\"wikiticker\".\"delta\") as \"m3\"\n"
operator|+
literal|"from \"wiki\" as \"wikiticker\"\n"
operator|+
literal|"where (\"wikiticker\".\"countryName\" in ('Colombia', 'France',\n"
operator|+
literal|" 'Germany', 'India', 'Italy', 'Russia', 'United Kingdom',\n"
operator|+
literal|" 'United States') or \"wikiticker\".\"countryName\" is null)\n"
operator|+
literal|"group by \"wikiticker\".\"countryName\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|9
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1656">[CALCITE-1656]    * Improve cost function in DruidQuery to encourage early column    * pruning</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testFieldBasedCostColumnPruning
parameter_list|()
block|{
comment|// A query where filter cannot be pushed to Druid but
comment|// the project can still be pushed in order to prune extra columns.
name|String
name|sql
init|=
literal|"select \"countryName\", floor(\"time\" to DAY),\n"
operator|+
literal|"  cast(count(*) as integer) as c\n"
operator|+
literal|"from \"wiki\"\n"
operator|+
literal|"where floor(\"time\" to DAY)>= '1997-01-01 00:00:00'\n"
operator|+
literal|"and floor(\"time\" to DAY)< '1997-09-01 00:00:00'\n"
operator|+
literal|"group by \"countryName\", floor(\"time\" TO DAY)\n"
operator|+
literal|"order by c limit 5"
decl_stmt|;
name|String
name|plan
init|=
literal|"BindableProject(countryName=[$0], EXPR$1=[$1], C=[CAST($2):INTEGER NOT NULL])\n"
operator|+
literal|"    BindableSort(sort0=[$2], dir0=[ASC], fetch=[5])\n"
operator|+
literal|"      BindableAggregate(group=[{0, 1}], agg#0=[COUNT()])\n"
operator|+
literal|"        BindableProject(countryName=[$1], EXPR$1=[FLOOR($0, FLAG(DAY))])\n"
operator|+
literal|"          BindableFilter(condition=[AND(>=(FLOOR($0, FLAG(DAY)), 1997-01-01 00:00:00),<(FLOOR($0, FLAG(DAY)), 1997-09-01 00:00:00))])\n"
operator|+
literal|"            DruidQuery(table=[[wiki, wiki]], intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$0, $5]])"
decl_stmt|;
comment|// NOTE: Druid query only has countryName as the dimension
comment|// being queried after project is pushed to druid query.
name|String
name|druidQuery
init|=
literal|"{'queryType':'select',"
operator|+
literal|"'dataSource':'wikiticker',"
operator|+
literal|"'descending':false,"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'dimensions':['countryName'],"
operator|+
literal|"'metrics':[],"
operator|+
literal|"'granularity':'all',"
operator|+
literal|"'pagingSpec':{'threshold':16384,'fromNext':true},"
operator|+
literal|"'context':{'druid.query.fetch':false}}"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI
argument_list|)
operator|.
name|explainContains
argument_list|(
name|plan
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI
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
literal|"{\"queryType\":\"select\""
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
literal|"select \"product_id\", \"timestamp\" as \"time\" from \"foodmart\" "
operator|+
literal|"where \"product_id\" = 1016 "
operator|+
literal|"and \"timestamp\"< cast('1997-01-03' as timestamp) "
operator|+
literal|"and \"timestamp\"> cast('1990-01-01' as timestamp) "
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
literal|"product_id=1016; time=1997-01-02 00:00:00"
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
comment|// Calcite rewrite the extract function in the query as:
comment|// rel#85:BindableProject.BINDABLE.[](input=rel#69:Subset#1.BINDABLE.[],
comment|// hourOfDay=/INT(MOD(Reinterpret($0), 86400000), 3600000),product_id=$1).
comment|// Currently 'EXTRACT( hour from \"timestamp\")' is not pushed to Druid.
annotation|@
name|Ignore
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
literal|",'granularity':'all'"
argument_list|,
literal|"{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_0',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'H',"
operator|+
literal|"'timeZone':'UTC'}}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"month=01; product_id=1016"
argument_list|,
literal|"month=02; product_id=1016"
argument_list|,
literal|"month=03; product_id=1016"
argument_list|,
literal|"month=04; product_id=1016"
argument_list|,
literal|"month=05; product_id=1016"
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
literal|"intervals=[[1997-01-01T00:00:00.001/1997-01-20T00:00:00.000]], filter=[=($1, 1016)"
operator|+
literal|"], projects=[[EXTRACT_DATE(FLAG(DAY), /INT(Reinterpret($0), 86400000)), "
operator|+
literal|"EXTRACT_DATE(FLAG(MONTH), /INT(Reinterpret($0), 86400000)), EXTRACT_DATE(FLAG"
operator|+
literal|"(YEAR), /INT(Reinterpret($0), 86400000)), $1]], groups=[{0, 1, 2, 3}], aggs=[[]])\n"
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
literal|"intervals=[[1997-01-01T00:00:00.001/1997-01-20T00:00:00.000]], filter=[=($1, 1016)"
operator|+
literal|"], projects=[[EXTRACT_DATE(FLAG(DAY), /INT(Reinterpret($0), 86400000)), "
operator|+
literal|"EXTRACT_DATE(FLAG(MONTH), /INT(Reinterpret($0), 86400000)), EXTRACT_DATE(FLAG"
operator|+
literal|"(YEAR), /INT(Reinterpret($0), 86400000)), $1]], groups=[{0, 1, 2, 3}], aggs=[[]])\n"
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
literal|"intervals=[[1997-01-01T00:00:00.001/1997-01-20T00:00:00.000]], filter=[=($1, 1016)], "
operator|+
literal|"projects=[[EXTRACT_DATE(FLAG(DAY), /INT(Reinterpret($0), 86400000)), $1]], "
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
literal|"{'queryType':'timeseries','dataSource':'foodmart',"
operator|+
literal|"'descending':false,'granularity':'all','filter':{'type':'and',"
operator|+
literal|"'fields':[{'type':'bound','dimension':'cases_per_pallet','lower':'8',"
operator|+
literal|"'lowerStrict':false,'ordering':'numeric'},{'type':'bound',"
operator|+
literal|"'dimension':'cases_per_pallet','upper':'10','upperStrict':false,"
operator|+
literal|"'ordering':'numeric'},{'type':'bound','dimension':'units_per_case',"
operator|+
literal|"'upper':'15','upperStrict':true,'ordering':'numeric'},"
operator|+
literal|"{'type':'selector','dimension':'__time','value':'1997',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'yyyy','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}}]},'aggregations':[{'type':'doubleSum',"
operator|+
literal|"'name':'EXPR$0','fieldName':'store_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000'],"
operator|+
literal|"'context':{'skipEmptyBuckets':true}}"
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
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], filter=[AND(>=(CAST"
operator|+
literal|"($11):BIGINT, 8),<=(CAST($11):BIGINT, 10),<(CAST($10):BIGINT, 15), =(EXTRACT_DATE"
operator|+
literal|"(FLAG(YEAR), /INT(Reinterpret($0), 86400000)), 1997))], groups=[{}], "
operator|+
literal|"aggs=[[SUM($90)]])"
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
literal|"EXPR$0=75364.09998679161"
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
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'product_id'},{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'extract_day','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'d','timeZone':'UTC','locale':'en-US'}},{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_month',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'M','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}}],'limitSpec':{'type':'default'},"
operator|+
literal|"'filter':{'type':'and','fields':[{'type':'bound',"
operator|+
literal|"'dimension':'product_id','lower':'1549','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric'},{'type':'bound','dimension':'__time',"
operator|+
literal|"'lower':'30','lowerStrict':false,'ordering':'numeric',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'d','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}},{'type':'selector','dimension':'__time',"
operator|+
literal|"'value':'11','extractionFn':{'type':'timeFormat','format':'M',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}]},'aggregations':[{'type':'longSum',"
operator|+
literal|"'name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
argument_list|)
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
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'product_id'},{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'extract_day','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'d','timeZone':'UTC','locale':'en-US'}},{'type':'extraction',"
operator|+
literal|"'dimension':'__time','outputName':'extract_month',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'M','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}},{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'extract_year','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'yyyy','timeZone':'UTC','locale':'en-US'}}],"
operator|+
literal|"'limitSpec':{'type':'default'},'filter':{'type':'and',"
operator|+
literal|"'fields':[{'type':'bound','dimension':'product_id','lower':'1549',"
operator|+
literal|"'lowerStrict':false,'ordering':'numeric'},{'type':'bound',"
operator|+
literal|"'dimension':'__time','lower':'30','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric','extractionFn':{'type':'timeFormat','format':'d',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}},{'type':'selector',"
operator|+
literal|"'dimension':'__time','value':'11','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'M','timeZone':'UTC','locale':'en-US'}},{'type':'selector',"
operator|+
literal|"'dimension':'__time','value':'1997','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'yyyy','timeZone':'UTC','locale':'en-US'}}]},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg',"
operator|+
literal|"'fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"product_id=1549; EXPR$1=30; EXPR$2=11; EXPR$3=1997"
argument_list|,
literal|"product_id=1553; EXPR$1=30; EXPR$2=11; EXPR$3=1997"
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
literal|"{'queryType':'groupBy','dataSource':'foodmart',"
operator|+
literal|"'granularity':'all','dimensions':[{'type':'default',"
operator|+
literal|"'dimension':'product_id'},{'type':'extraction','dimension':'__time',"
operator|+
literal|"'outputName':'extract_month','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'M','timeZone':'UTC','locale':'en-US'}}],"
operator|+
literal|"'limitSpec':{'type':'default'},'filter':{'type':'and',"
operator|+
literal|"'fields':[{'type':'bound','dimension':'product_id','lower':'1558',"
operator|+
literal|"'lowerStrict':false,'ordering':'numeric'},{'type':'bound',"
operator|+
literal|"'dimension':'__time','lower':'10','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric','extractionFn':{'type':'timeFormat','format':'M',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}},{'type':'bound',"
operator|+
literal|"'dimension':'__time','upper':'11','upperStrict':false,"
operator|+
literal|"'ordering':'numeric','extractionFn':{'type':'timeFormat','format':'M',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}]},'aggregations':[{'type':'longSum',"
operator|+
literal|"'name':'dummy_agg','fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"{'queryType':'groupBy',"
operator|+
literal|"'dataSource':'foodmart','granularity':'all',"
operator|+
literal|"'dimensions':[{'type':'default','dimension':'product_id'},"
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
literal|"'ordering':'numeric'},{'type':'or','fields':[{'type':'selector',"
operator|+
literal|"'dimension':'__time','value':'10','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'M','timeZone':'UTC','locale':'en-US'}},{'type':'selector',"
operator|+
literal|"'dimension':'__time','value':'11','extractionFn':{'type':'timeFormat',"
operator|+
literal|"'format':'M','timeZone':'UTC','locale':'en-US'}}]}]},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg',"
operator|+
literal|"'fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
argument_list|)
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
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPushofOrderByWithMonthExtract
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
literal|"'locale':'en-US'}},{'type':'default','dimension':'product_id'}],"
operator|+
literal|"'limitSpec':{'type':'default','columns':[{'dimension':'extract_month',"
operator|+
literal|"'direction':'ascending','dimensionOrder':'numeric'},{'dimension':'S',"
operator|+
literal|"'direction':'ascending','dimensionOrder':'numeric'},"
operator|+
literal|"{'dimension':'product_id','direction':'ascending',"
operator|+
literal|"'dimensionOrder':'alphanumeric'}]},'filter':{'type':'bound',"
operator|+
literal|"'dimension':'product_id','lower':'1558','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric'},'aggregations':[{'type':'longSum','name':'S',"
operator|+
literal|"'fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
argument_list|)
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], filter=[>=(CAST($1)"
operator|+
literal|":BIGINT, 1558)], projects=[[EXTRACT_DATE(FLAG(MONTH), /INT(Reinterpret($0), "
operator|+
literal|"86400000)), $1, $89]], groups=[{0, 1}], aggs=[[SUM($2)]], sort0=[0], sort1=[2], "
operator|+
literal|"sort2=[1], dir0=[ASC], dir1=[ASC], dir2=[ASC])"
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
literal|"select  floor(\"timestamp\" to MONTH) as \"month\"\n"
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
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[FLOOR($0, "
operator|+
literal|"FLAG(MONTH))]], groups=[{0}], aggs=[[]], sort0=[0], dir0=[DESC])"
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
literal|"select  floor(\"timestamp\" to MONTH) as \"floor_month\"\n"
operator|+
literal|"from \"foodmart\"\n"
operator|+
literal|"group by floor(\"timestamp\" to MONTH)\n"
operator|+
literal|"order by \"floor_month\" DESC LIMIT 3"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableLimit(fetch=[3])\n"
operator|+
literal|"  EnumerableInterpreter\n"
operator|+
literal|"    DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[FLOOR($0, "
operator|+
literal|"FLAG(MONTH))]], groups=[{0}], aggs=[[]], sort0=[0], dir0=[DESC])"
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
name|returnsOrdered
argument_list|(
literal|"floor_month=1997-12-01 00:00:00"
argument_list|,
literal|"floor_month=1997-11-01 00:00:00"
argument_list|,
literal|"floor_month=1997-10-01 00:00:00"
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
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], filter=[>=(CAST($1)"
operator|+
literal|":BIGINT, 1558)], projects=[[EXTRACT_DATE(FLAG(YEAR), /INT(Reinterpret($0), 86400000)),"
operator|+
literal|" EXTRACT_DATE(FLAG(MONTH), /INT(Reinterpret($0), 86400000)), $1, $89]], groups=[{0, 1,"
operator|+
literal|" 2}], aggs=[[SUM($3)]], sort0=[0], sort1=[1], sort2=[3], sort3=[2], dir0=[DESC], "
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
literal|"'dimension':'product_id'}],'limitSpec':{'type':'default','limit':3,"
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
literal|"'dimensionOrder':'alphanumeric'}]},'filter':{'type':'bound',"
operator|+
literal|"'dimension':'product_id','lower':'1558','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric'},'aggregations':[{'type':'longSum','name':'S',"
operator|+
literal|"'fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], filter=[>=(CAST($1)"
operator|+
literal|":BIGINT, 1558)], projects=[[EXTRACT_DATE(FLAG(YEAR), /INT(Reinterpret($0), 86400000)),"
operator|+
literal|" EXTRACT_DATE(FLAG(MONTH), /INT(Reinterpret($0), 86400000)), $1, $89]], groups=[{0, 1,"
operator|+
literal|" 2}], aggs=[[SUM($3)]], sort0=[3], sort1=[1], sort2=[2], dir0=[DESC], dir1=[DESC], "
operator|+
literal|"dir2=[ASC], fetch=[3])"
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
literal|"'dimension':'product_id'}],'limitSpec':{'type':'default','limit':3,"
operator|+
literal|"'columns':[{'dimension':'S','direction':'descending',"
operator|+
literal|"'dimensionOrder':'numeric'},{'dimension':'extract_month',"
operator|+
literal|"'direction':'descending','dimensionOrder':'numeric'},"
operator|+
literal|"{'dimension':'product_id','direction':'ascending',"
operator|+
literal|"'dimensionOrder':'alphanumeric'}]},'filter':{'type':'bound',"
operator|+
literal|"'dimension':'product_id','lower':'1558','lowerStrict':false,"
operator|+
literal|"'ordering':'numeric'},'aggregations':[{'type':'longSum','name':'S',"
operator|+
literal|"'fieldName':'unit_sales'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"Y=1997; M=12; product_id=1558; S=30"
argument_list|,
literal|"Y=1997; M=3; product_id=1558; S=29"
argument_list|,
literal|"Y=1997; M=5; product_id=1558; S=27"
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
literal|"SELECT count(*) as c , SUM(\"unit_sales\") as s, floor(\"timestamp\""
operator|+
literal|" to month) FROM \"foodmart\" group by floor(\"timestamp\" to month) order by s DESC"
decl_stmt|;
name|sql
argument_list|(
name|sqlQuery
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableSort(sort0=[$1], dir0=[DESC])\n"
operator|+
literal|"    BindableProject(C=[$1], S=[$2], EXPR$2=[$0])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[FLOOR($0, "
operator|+
literal|"FLAG(MONTH)), $89]], groups=[{0}], aggs=[[COUNT(), SUM($1)]])"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|druidChecker
argument_list|(
literal|"'queryType':'timeseries'"
argument_list|)
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
literal|"SELECT \"timestamp\", count(*) as c, SUM(\"unit_sales\")  "
operator|+
literal|"as s FROM "
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
literal|"'dimensionOrder':'alphanumeric'},{'dimension':'C',"
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
literal|"'dimensionOrder':'alphanumeric'}]}"
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
literal|"\"product_id\" = 1558 and extract(week from \"timestamp\") IN (10, 11)group by extract"
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
literal|"'filter':{'type':'and','fields':[{'type':'selector',"
operator|+
literal|"'dimension':'product_id','value':'1558'},{'type':'or',"
operator|+
literal|"'fields':[{'type':'selector','dimension':'__time','value':'10',"
operator|+
literal|"'extractionFn':{'type':'timeFormat','format':'w','timeZone':'UTC',"
operator|+
literal|"'locale':'en-US'}},{'type':'selector','dimension':'__time',"
operator|+
literal|"'value':'11','extractionFn':{'type':'timeFormat','format':'w',"
operator|+
literal|"'timeZone':'UTC','locale':'en-US'}}]}]},"
operator|+
literal|"'aggregations':[{'type':'longSum','name':'dummy_agg',"
operator|+
literal|"'fieldName':'dummy_agg'}],"
operator|+
literal|"'intervals':['1900-01-09T00:00:00.000/2992-01-10T00:00:00.000']}"
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
literal|"    BindableProject(EXPR$0=[/INT(EXTRACT_DATE(FLAG(YEAR), /INT(Reinterpret($0), "
operator|+
literal|"86400000)), 100)])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], filter=[=($1, 1558)], "
operator|+
literal|"projects=[[$0]])"
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
literal|"'queryType':'select'"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=19"
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
literal|"group by \"product_id\""
decl_stmt|;
specifier|final
name|String
name|plan
init|=
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableAggregate(group=[{0}])\n"
operator|+
literal|"    BindableFilter(condition=[=($0, null)])\n"
operator|+
literal|"      DruidQuery(table=[[foodmart, foodmart]], "
operator|+
literal|"intervals=[[1900-01-09T00:00:00.000/2992-01-10T00:00:00.000]], projects=[[$1]])"
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
argument_list|()
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
comment|/**    *<a href="https://issues.apache.org/jira/browse/CALCITE-1805">[CALCITE-1805]    * Druid adapter can not handel count column without adding support for nested queries</a>.    */
annotation|@
name|Test
specifier|public
name|void
name|testCountColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT count(\"countryName\") FROM (SELECT \"countryName\" FROM "
operator|+
literal|"\"wikiticker\" WHERE \"countryName\"  IS NOT NULL) as a"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=3799"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql2
init|=
literal|"SELECT count(\"countryName\") FROM (SELECT \"countryName\" FROM "
operator|+
literal|"\"wikiticker\") as a"
decl_stmt|;
name|sql
argument_list|(
name|sql2
argument_list|,
name|WIKI_AUTO2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=3799"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN"
operator|+
literal|"=EnumerableInterpreter\n"
operator|+
literal|"  BindableAggregate(group=[{}], EXPR$0=[COUNT($0)])\n"
operator|+
literal|"    DruidQuery(table=[[wiki, wikiticker]], "
operator|+
literal|"intervals=[[1900-01-01T00:00:00.000/3000-01-01T00:00:00.000]], projects=[[$7]])"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DruidAdapterIT.java
end_comment

end_unit

