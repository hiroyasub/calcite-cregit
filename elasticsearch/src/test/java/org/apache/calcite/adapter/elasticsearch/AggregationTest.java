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
name|elasticsearch
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
name|CalciteConnection
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
name|schema
operator|.
name|impl
operator|.
name|ViewTable
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
name|ViewTableMacro
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
name|apache
operator|.
name|calcite
operator|.
name|test
operator|.
name|ElasticsearchChecker
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonParser
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|ObjectMapper
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|databind
operator|.
name|node
operator|.
name|ObjectNode
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
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
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
name|ArrayList
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
name|Collections
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
name|Locale
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

begin_comment
comment|/**  * Testing Elasticsearch aggregation transformations.  */
end_comment

begin_class
specifier|public
class|class
name|AggregationTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|EmbeddedElasticsearchPolicy
name|NODE
init|=
name|EmbeddedElasticsearchPolicy
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"aggs"
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setupInstance
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|mappings
init|=
name|ImmutableMap
operator|.
expr|<
name|String
decl_stmt|,
name|String
decl|>
name|builder
argument_list|()
decl|.
name|put
argument_list|(
literal|"cat1"
argument_list|,
literal|"keyword"
argument_list|)
decl|.
name|put
argument_list|(
literal|"cat2"
argument_list|,
literal|"keyword"
argument_list|)
decl|.
name|put
argument_list|(
literal|"cat3"
argument_list|,
literal|"keyword"
argument_list|)
decl|.
name|put
argument_list|(
literal|"cat4"
argument_list|,
literal|"date"
argument_list|)
decl|.
name|put
argument_list|(
literal|"cat5"
argument_list|,
literal|"integer"
argument_list|)
decl|.
name|put
argument_list|(
literal|"val1"
argument_list|,
literal|"long"
argument_list|)
decl|.
name|put
argument_list|(
literal|"val2"
argument_list|,
literal|"long"
argument_list|)
decl|.
name|build
argument_list|()
decl_stmt|;
name|NODE
operator|.
name|createIndex
argument_list|(
name|NAME
argument_list|,
name|mappings
argument_list|)
expr_stmt|;
name|String
name|doc1
init|=
literal|"{cat1:'a', cat2:'g', val1:1, cat4:'2018-01-01', cat5:1}"
decl_stmt|;
name|String
name|doc2
init|=
literal|"{cat2:'g', cat3:'y', val2:5, cat4:'2019-12-12'}"
decl_stmt|;
name|String
name|doc3
init|=
literal|"{cat1:'b', cat2:'h', cat3:'z', cat5:2, val1:7, val2:42}"
decl_stmt|;
specifier|final
name|ObjectMapper
name|mapper
init|=
operator|new
name|ObjectMapper
argument_list|()
operator|.
name|enable
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_UNQUOTED_FIELD_NAMES
argument_list|)
comment|// user-friendly settings to
operator|.
name|enable
argument_list|(
name|JsonParser
operator|.
name|Feature
operator|.
name|ALLOW_SINGLE_QUOTES
argument_list|)
decl_stmt|;
comment|// avoid too much quoting
specifier|final
name|List
argument_list|<
name|ObjectNode
argument_list|>
name|docs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|text
range|:
name|Arrays
operator|.
name|asList
argument_list|(
name|doc1
argument_list|,
name|doc2
argument_list|,
name|doc3
argument_list|)
control|)
block|{
name|docs
operator|.
name|add
argument_list|(
operator|(
name|ObjectNode
operator|)
name|mapper
operator|.
name|readTree
argument_list|(
name|text
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|NODE
operator|.
name|insertBulk
argument_list|(
name|NAME
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|ConnectionFactory
name|newConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|CalciteAssert
operator|.
name|ConnectionFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|createConnection
parameter_list|()
throws|throws
name|SQLException
block|{
specifier|final
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:lex=JAVA"
argument_list|)
decl_stmt|;
specifier|final
name|SchemaPlus
name|root
init|=
name|connection
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
name|root
operator|.
name|add
argument_list|(
literal|"elastic"
argument_list|,
operator|new
name|ElasticsearchSchema
argument_list|(
name|NODE
operator|.
name|restClient
argument_list|()
argument_list|,
name|NODE
operator|.
name|mapper
argument_list|()
argument_list|,
name|NAME
argument_list|)
argument_list|)
expr_stmt|;
comment|// add calcite view programmatically
specifier|final
name|String
name|viewSql
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select _MAP['cat1'] AS \"cat1\", "
operator|+
literal|" _MAP['cat2']  AS \"cat2\", "
operator|+
literal|" _MAP['cat3'] AS \"cat3\", "
operator|+
literal|" _MAP['cat4'] AS \"cat4\", "
operator|+
literal|" _MAP['cat5'] AS \"cat5\", "
operator|+
literal|" _MAP['val1'] AS \"val1\", "
operator|+
literal|" _MAP['val2'] AS \"val2\" "
operator|+
literal|" from \"elastic\".\"%s\""
argument_list|,
name|NAME
argument_list|)
decl_stmt|;
name|ViewTableMacro
name|macro
init|=
name|ViewTable
operator|.
name|viewMacro
argument_list|(
name|root
argument_list|,
name|viewSql
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
literal|"elastic"
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"elastic"
argument_list|,
literal|"view"
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|root
operator|.
name|add
argument_list|(
literal|"view"
argument_list|,
name|macro
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|countStar
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from view"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"_source:false, 'stored_fields': '_none_', size:0, track_total_hits:true"
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=3\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from view where cat1 = 'a'"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*) from view where cat1 in ('a', 'b')"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=2\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|all
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*), sum(val1), sum(val2) from view"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"_source:false, size:0, track_total_hits:true"
argument_list|,
literal|"'stored_fields': '_none_'"
argument_list|,
literal|"aggregations:{'EXPR$0.value_count.field': '_id'"
argument_list|,
literal|"'EXPR$1.sum.field': 'val1'"
argument_list|,
literal|"'EXPR$2.sum.field': 'val2'}"
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=3; EXPR$1=8.0; EXPR$2=47.0\n"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select min(val1), max(val2), count(*) from view"
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"_source:false, 'stored_fields': '_none_', size:0, track_total_hits:true"
argument_list|,
literal|"aggregations:{'EXPR$0.min.field': 'val1'"
argument_list|,
literal|"'EXPR$1.max.field': 'val2'"
argument_list|,
literal|"'EXPR$2.value_count.field': '_id'}"
argument_list|)
argument_list|)
operator|.
name|returns
argument_list|(
literal|"EXPR$0=1.0; EXPR$1=42.0; EXPR$2=3\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cat1
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, sum(val1), sum(val2) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=null; EXPR$1=0.0; EXPR$2=5.0"
argument_list|,
literal|"cat1=a; EXPR$1=1.0; EXPR$2=0.0"
argument_list|,
literal|"cat1=b; EXPR$1=7.0; EXPR$2=42.0"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, count(*) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=null; EXPR$1=1"
argument_list|,
literal|"cat1=a; EXPR$1=1"
argument_list|,
literal|"cat1=b; EXPR$1=1"
argument_list|)
expr_stmt|;
comment|// different order for agg functions
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select count(*), cat1 from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=1; cat1=a"
argument_list|,
literal|"EXPR$0=1; cat1=b"
argument_list|,
literal|"EXPR$0=1; cat1=null"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, count(*), sum(val1), sum(val2) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; EXPR$1=1; EXPR$2=1.0; EXPR$3=0.0"
argument_list|,
literal|"cat1=b; EXPR$1=1; EXPR$2=7.0; EXPR$3=42.0"
argument_list|,
literal|"cat1=null; EXPR$1=1; EXPR$2=0.0; EXPR$3=5.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cat2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat2, min(val1), max(val1), min(val2), max(val2) from view group by cat2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat2=g; EXPR$1=1.0; EXPR$2=1.0; EXPR$3=5.0; EXPR$4=5.0"
argument_list|,
literal|"cat2=h; EXPR$1=7.0; EXPR$2=7.0; EXPR$3=42.0; EXPR$4=42.0"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat2, sum(val1), sum(val2) from view group by cat2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat2=g; EXPR$1=1.0; EXPR$2=5.0"
argument_list|,
literal|"cat2=h; EXPR$1=7.0; EXPR$2=42.0"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat2, count(*) from view group by cat2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat2=g; EXPR$1=2"
argument_list|,
literal|"cat2=h; EXPR$1=1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cat1Cat2
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, cat2, sum(val1), sum(val2) from view group by cat1, cat2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; cat2=g; EXPR$2=1.0; EXPR$3=0.0"
argument_list|,
literal|"cat1=null; cat2=g; EXPR$2=0.0; EXPR$3=5.0"
argument_list|,
literal|"cat1=b; cat2=h; EXPR$2=7.0; EXPR$3=42.0"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, cat2, count(*) from view group by cat1, cat2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; cat2=g; EXPR$2=1"
argument_list|,
literal|"cat1=null; cat2=g; EXPR$2=1"
argument_list|,
literal|"cat1=b; cat2=h; EXPR$2=1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cat1Cat3
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, cat3, sum(val1), sum(val2) from view group by cat1, cat3"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; cat3=null; EXPR$2=1.0; EXPR$3=0.0"
argument_list|,
literal|"cat1=null; cat3=y; EXPR$2=0.0; EXPR$3=5.0"
argument_list|,
literal|"cat1=b; cat3=z; EXPR$2=7.0; EXPR$3=42.0"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Testing {@link org.apache.calcite.sql.SqlKind#ANY_VALUE} aggregate function    */
annotation|@
name|Test
specifier|public
name|void
name|anyValue
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, any_value(cat2) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; EXPR$1=g"
argument_list|,
literal|"cat1=null; EXPR$1=g"
argument_list|,
literal|"cat1=b; EXPR$1=h"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat2, any_value(cat1) from view group by cat2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat2=g; EXPR$1=a"
argument_list|,
comment|// EXPR$1=null is also valid
literal|"cat2=h; EXPR$1=b"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat2, any_value(cat3) from view group by cat2"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat2=g; EXPR$1=y"
argument_list|,
comment|// EXPR$1=null is also valid
literal|"cat2=h; EXPR$1=z"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|anyValueWithOtherAgg
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, any_value(cat2), max(val1) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; EXPR$1=g; EXPR$2=1.0"
argument_list|,
literal|"cat1=null; EXPR$1=g; EXPR$2=null"
argument_list|,
literal|"cat1=b; EXPR$1=h; EXPR$2=7.0"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select max(val1), cat1, any_value(cat2) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=1.0; cat1=a; EXPR$2=g"
argument_list|,
literal|"EXPR$0=null; cat1=null; EXPR$2=g"
argument_list|,
literal|"EXPR$0=7.0; cat1=b; EXPR$2=h"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select any_value(cat2), cat1, max(val1) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=g; cat1=a; EXPR$2=1.0"
argument_list|,
literal|"EXPR$0=g; cat1=null; EXPR$2=null"
argument_list|,
literal|"EXPR$0=h; cat1=b; EXPR$2=7.0"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cat1Cat2Cat3
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, cat2, cat3, count(*), sum(val1), sum(val2) from view "
operator|+
literal|"group by cat1, cat2, cat3"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; cat2=g; cat3=null; EXPR$3=1; EXPR$4=1.0; EXPR$5=0.0"
argument_list|,
literal|"cat1=b; cat2=h; cat3=z; EXPR$3=1; EXPR$4=7.0; EXPR$5=42.0"
argument_list|,
literal|"cat1=null; cat2=g; cat3=y; EXPR$3=1; EXPR$4=0.0; EXPR$5=5.0"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Group by    *<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/date.html">    * date</a> data type.    */
annotation|@
name|Test
specifier|public
name|void
name|dateCat
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat4, sum(val1) from view group by cat4"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat4=1514764800000; EXPR$1=1.0"
argument_list|,
literal|"cat4=1576108800000; EXPR$1=0.0"
argument_list|,
literal|"cat4=null; EXPR$1=7.0"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Group by    *<a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/number.html">    * number</a> data type.    */
annotation|@
name|Test
specifier|public
name|void
name|integerCat
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat5, sum(val1) from view group by cat5"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat5=1; EXPR$1=1.0"
argument_list|,
literal|"cat5=null; EXPR$1=0.0"
argument_list|,
literal|"cat5=2; EXPR$1=7.0"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Validate {@link org.apache.calcite.sql.fun.SqlStdOperatorTable#APPROX_COUNT_DISTINCT}.    */
annotation|@
name|Test
specifier|public
name|void
name|approximateCountDistinct
parameter_list|()
block|{
comment|// approx_count_distinct counts distinct *non-null* values
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select approx_count_distinct(cat1) from view"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=2"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select approx_count_distinct(cat2) from view"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"EXPR$0=2"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, approx_count_distinct(val1) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; EXPR$1=1"
argument_list|,
literal|"cat1=b; EXPR$1=1"
argument_list|,
literal|"cat1=null; EXPR$1=0"
argument_list|)
expr_stmt|;
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
literal|"select cat1, approx_count_distinct(val2) from view group by cat1"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"cat1=a; EXPR$1=0"
argument_list|,
literal|"cat1=b; EXPR$1=1"
argument_list|,
literal|"cat1=null; EXPR$1=1"
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@code select max(cast(_MAP['foo'] as integer)) from tbl}    */
annotation|@
name|Test
specifier|public
name|void
name|aggregationWithCast
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|newConnectionFactory
argument_list|()
argument_list|)
operator|.
name|query
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"select max(cast(_MAP['val1'] as integer)) as v1, "
operator|+
literal|"min(cast(_MAP['val2'] as integer)) as v2 from elastic.%s"
argument_list|,
name|NAME
argument_list|)
argument_list|)
operator|.
name|queryContains
argument_list|(
name|ElasticsearchChecker
operator|.
name|elasticsearchChecker
argument_list|(
literal|"_source:false, 'stored_fields': '_none_', size:0, track_total_hits:true"
argument_list|,
literal|"aggregations:{'v1.max.field': 'val1'"
argument_list|,
literal|"'v2.min.field': 'val2'}"
argument_list|)
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"v1=7; v2=5"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

