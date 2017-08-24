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
name|ImmutableMap
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
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.elasticsearch} package.  *  *<p>Before calling this test, you need to populate Elasticsearch, as follows:  *  *<blockquote><code>  * git clone https://github.com/vlsi/calcite-test-dataset<br>  * cd calcite-test-dataset<br>  * mvn install  *</code></blockquote>  *  *<p>This will create a virtual machine with Elasticsearch and the "zips" test  * dataset.  */
end_comment

begin_class
specifier|public
class|class
name|Elasticsearch5AdapterIT
block|{
comment|/**    * Whether to run Elasticsearch tests. Enabled by default, however test is only    * included if "it" profile is activated ({@code -Pit}). To disable,    * specify {@code -Dcalcite.test.elasticsearch=false} on the Java command line.    */
specifier|private
specifier|static
specifier|final
name|boolean
name|ENABLED
init|=
name|Util
operator|.
name|getBooleanProperty
argument_list|(
literal|"calcite.test.elasticsearch"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/** Connection factory based on the "zips-es" model. */
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|ZIPS
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|Elasticsearch5AdapterIT
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/elasticsearch-zips-model.json"
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Whether to run this test. */
specifier|private
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|ENABLED
return|;
block|}
comment|/** Returns a function that checks that a particular Elasticsearch pipeline is    * generated to implement a query. */
specifier|private
specifier|static
name|Function
argument_list|<
name|List
argument_list|,
name|Void
argument_list|>
name|elasticsearchChecker
parameter_list|(
specifier|final
name|String
modifier|...
name|strings
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
annotation|@
name|Nullable
annotation|@
name|Override
specifier|public
name|Void
name|apply
parameter_list|(
annotation|@
name|Nullable
name|List
name|actual
parameter_list|)
block|{
name|Object
index|[]
name|actualArray
init|=
name|actual
operator|==
literal|null
operator|||
name|actual
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
operator|(
operator|(
name|List
operator|)
name|actual
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|CalciteAssert
operator|.
name|assertArrayEqual
argument_list|(
literal|"expected Elasticsearch query not found"
argument_list|,
name|strings
argument_list|,
name|actualArray
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
return|;
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
literal|"PLAN=ElasticsearchToEnumerableConverter\n"
operator|+
literal|"  ElasticsearchSort(sort0=[$4], dir0=[ASC])\n"
operator|+
literal|"    ElasticsearchProject(city=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], longitude=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], latitude=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], pop=[CAST(ITEM($0, 'pop')):INTEGER], state=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], id=[CAST(ITEM($0, 'id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      ElasticsearchTableScan(table=[[elasticsearch_raw, zips]])"
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
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips order by \"state\""
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|10
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
name|sql
init|=
literal|"select \"state\", \"id\" from zips\n"
operator|+
literal|"order by \"state\", \"id\" offset 2 rows fetch next 3 rows only"
decl_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=AK; id=99503"
argument_list|,
literal|"state=AK; id=99504"
argument_list|,
literal|"state=AK; id=99505"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|elasticsearchChecker
argument_list|(
literal|"\"fields\" : [\"state\", \"id\"], \"script_fields\": {}"
argument_list|,
literal|"\"sort\": [ {\"state\": \"asc\"}, {\"id\": \"asc\"}]"
argument_list|,
literal|"\"from\": 2"
argument_list|,
literal|"\"size\": 3"
argument_list|)
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
specifier|final
name|String
name|sql
init|=
literal|"select \"state\", \"id\" from zips\n"
operator|+
literal|"offset 2 fetch next 3 rows only"
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
name|ZIPS
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
name|elasticsearchChecker
argument_list|(
literal|"\"from\": 2"
argument_list|,
literal|"\"size\": 3"
argument_list|,
literal|"\"fields\" : [\"state\", \"id\"], \"script_fields\": {}"
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
literal|"select \"state\", \"id\" from zips\n"
operator|+
literal|"fetch next 3 rows only"
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
name|ZIPS
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
name|elasticsearchChecker
argument_list|(
literal|"\"size\": 3"
argument_list|,
literal|"\"fields\" : [\"state\", \"id\"], \"script_fields\": {}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterSort
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from zips\n"
operator|+
literal|"where \"city\" = 'SPRINGFIELD' and \"id\">= '70000'\n"
operator|+
literal|"order by \"state\", \"id\""
decl_stmt|;
specifier|final
name|String
name|explain
init|=
literal|"PLAN=ElasticsearchToEnumerableConverter\n"
operator|+
literal|"  ElasticsearchSort(sort0=[$4], sort1=[$5], dir0=[ASC], dir1=[ASC])\n"
operator|+
literal|"    ElasticsearchProject(city=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], longitude=[CAST(ITEM(ITEM($0, 'loc'), 0)):FLOAT], latitude=[CAST(ITEM(ITEM($0, 'loc'), 1)):FLOAT], pop=[CAST(ITEM($0, 'pop')):INTEGER], state=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], id=[CAST(ITEM($0, 'id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"      ElasticsearchFilter(condition=[AND(=(CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'SPRINGFIELD'),>=(CAST(ITEM($0, 'id')):VARCHAR(5) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", '70000'))])\n"
operator|+
literal|"        ElasticsearchTableScan(table=[[elasticsearch_raw, zips]])"
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
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"city=SPRINGFIELD; longitude=-92.54567; latitude=35.274879; pop=752; state=AR; id=72157"
argument_list|,
literal|"city=SPRINGFIELD; longitude=-102.617322; latitude=37.406727; pop=1992; state=CO; id=81073"
argument_list|,
literal|"city=SPRINGFIELD; longitude=-90.577479; latitude=30.415738; pop=5597; state=LA; id=70462"
argument_list|,
literal|"city=SPRINGFIELD; longitude=-123.015259; latitude=44.06106; pop=32384; state=OR; id=97477"
argument_list|,
literal|"city=SPRINGFIELD; longitude=-122.917108; latitude=44.056056; pop=27521; state=OR; id=97478"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|elasticsearchChecker
argument_list|(
literal|"\"query\" : {\"constant_score\":{\"filter\":{\"bool\":"
operator|+
literal|"{\"must\":[{\"term\":{\"city\":\"springfield\"}},{\"range\":{\"id\":{\"gte\":\"70000\"}}}]}}}}"
argument_list|,
literal|"\"fields\" : [\"city\", \"pop\", \"state\", \"id\"], \"script_fields\": {\"longitude\":{\"script\":\"_source.loc[0]\"}, \"latitude\":{\"script\":\"_source.loc[1]\"}}"
argument_list|,
literal|"\"sort\": [ {\"state\": \"asc\"}, {\"id\": \"asc\"}]"
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
name|testFilterSortDesc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from zips\n"
operator|+
literal|"where \"pop\" BETWEEN 20000 AND 20100\n"
operator|+
literal|"order by \"state\" desc, \"pop\""
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
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|limit
argument_list|(
literal|4
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"city=SHERIDAN; longitude=-106.964795; latitude=44.78486; pop=20025; state=WY; id=82801"
argument_list|,
literal|"city=MOUNTLAKE TERRAC; longitude=-122.304036; latitude=47.793061; pop=20059; state=WA; id=98043"
argument_list|,
literal|"city=FALMOUTH; longitude=-77.404537; latitude=38.314557; pop=20039; state=VA; id=22405"
argument_list|,
literal|"city=FORT WORTH; longitude=-97.318409; latitude=32.725551; pop=20012; state=TX; id=76104"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterRedundant
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from zips\n"
operator|+
literal|"where \"state\"> 'CA' and \"state\"< 'AZ' and \"state\" = 'OK'"
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
name|ZIPS
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
name|elasticsearchChecker
argument_list|(
literal|""
operator|+
literal|"\"query\" : {\"constant_score\":{\"filter\":{\"bool\":"
operator|+
literal|"{\"must\":[{\"term\":{\"state\":\"ok\"}}]}}}}"
argument_list|,
literal|"\"fields\" : [\"city\", \"pop\", \"state\", \"id\"], \"script_fields\": {\"longitude\":{\"script\":\"_source.loc[0]\"}, \"latitude\":{\"script\":\"_source.loc[1]\"}}"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInPlan
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|searches
init|=
block|{
literal|"\"query\" : {\"constant_score\":{\"filter\":{\"bool\":{\"should\":"
operator|+
literal|"[{\"bool\":{\"must\":[{\"term\":{\"pop\":20012}}]}},{\"bool\":{\"must\":[{\"term\":"
operator|+
literal|"{\"pop\":15590}}]}}]}}}}"
block|,
literal|"\"fields\" : [\"city\", \"pop\", \"state\", \"id\"], \"script_fields\": {\"longitude\":{\"script\":\"_source.loc[0]\"}, \"latitude\":{\"script\":\"_source.loc[1]\"}}"
block|}
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
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from zips where \"pop\" in (20012, 15590)"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"city=COVINA; longitude=-117.884285; latitude=34.08596; pop=15590; state=CA; id=91723"
argument_list|,
literal|"city=ARLINGTON; longitude=-97.091987; latitude=32.654752; pop=15590; state=TX; id=76018"
argument_list|,
literal|"city=CROFTON; longitude=-76.680166; latitude=39.011163; pop=15590; state=MD; id=21114"
argument_list|,
literal|"city=FORT WORTH; longitude=-97.318409; latitude=32.725551; pop=20012; state=TX; id=76104"
argument_list|,
literal|"city=DINUBA; longitude=-119.39087; latitude=36.534931; pop=20012; state=CA; id=93618"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|elasticsearchChecker
argument_list|(
name|searches
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testZips
parameter_list|()
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
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"state\", \"city\" from zips"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
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
literal|"select \"state\", \"city\", 0 as \"zero\"\n"
operator|+
literal|"from zips\n"
operator|+
literal|"order by \"state\", \"city\""
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
name|ZIPS
argument_list|)
operator|.
name|query
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
literal|"state=AK; city=ELMENDORF AFB; zero=0"
argument_list|,
literal|"state=AK; city=EIELSON AFB; zero=0"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|elasticsearchChecker
argument_list|(
literal|"\"sort\": [ {\"state\": \"asc\"}, {\"city\": \"asc\"}]"
argument_list|,
literal|"\"fields\" : [\"state\", \"city\"], \"script_fields\": {\"zero\":{\"script\": \"0\"}}"
argument_list|)
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
name|explain
init|=
literal|"PLAN=ElasticsearchToEnumerableConverter\n"
operator|+
literal|"  ElasticsearchProject(state=[CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"], city=[CAST(ITEM($0, 'city')):VARCHAR(20) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\"])\n"
operator|+
literal|"    ElasticsearchFilter(condition=[=(CAST(ITEM($0, 'state')):VARCHAR(2) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\", 'CA')])\n"
operator|+
literal|"      ElasticsearchTableScan(table=[[elasticsearch_raw, zips]])"
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
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"state\", \"city\" from zips where \"state\" = 'CA'"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=CA; city=LOS ANGELES"
argument_list|,
literal|"state=CA; city=LOS ANGELES"
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
name|testFilterReversed
parameter_list|()
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
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"state\", \"city\" from zips where 'WI'< \"state\""
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=WV; city=WELCH"
argument_list|,
literal|"state=WV; city=HANOVER"
argument_list|)
expr_stmt|;
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
name|ZIPS
argument_list|)
operator|.
name|query
argument_list|(
literal|"select \"state\", \"city\" from zips where \"state\"> 'WI'"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"state=WV; city=WELCH"
argument_list|,
literal|"state=WV; city=HANOVER"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End Elasticsearch5AdapterIT.java
end_comment

end_unit

