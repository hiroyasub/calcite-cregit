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
name|jupiter
operator|.
name|api
operator|.
name|BeforeAll
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
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|parallel
operator|.
name|ResourceAccessMode
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
name|parallel
operator|.
name|ResourceLock
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
comment|/**  * Test of different boolean expressions (some more complex than others).  */
end_comment

begin_class
annotation|@
name|ResourceLock
argument_list|(
name|value
operator|=
literal|"elasticsearch-scrolls"
argument_list|,
name|mode
operator|=
name|ResourceAccessMode
operator|.
name|READ
argument_list|)
class|class
name|BooleanLogicTest
block|{
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
literal|"booleanlogic"
decl_stmt|;
comment|/**    * Creates {@code zips} index and inserts some data.    *    * @throws Exception when ES node setup failed    */
annotation|@
name|BeforeAll
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
name|mapping
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"keyword"
argument_list|,
literal|"b"
argument_list|,
literal|"keyword"
argument_list|,
literal|"c"
argument_list|,
literal|"keyword"
argument_list|,
literal|"int"
argument_list|,
literal|"long"
argument_list|)
decl_stmt|;
name|NODE
operator|.
name|createIndex
argument_list|(
name|NAME
argument_list|,
name|mapping
argument_list|)
expr_stmt|;
name|String
name|doc
init|=
literal|"{'a': 'a', 'b':'b', 'c':'c', 'int': 42}"
operator|.
name|replace
argument_list|(
literal|'\''
argument_list|,
literal|'"'
argument_list|)
decl_stmt|;
name|NODE
operator|.
name|insertDocument
argument_list|(
name|NAME
argument_list|,
operator|(
name|ObjectNode
operator|)
name|NODE
operator|.
name|mapper
argument_list|()
operator|.
name|readTree
argument_list|(
name|doc
argument_list|)
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
literal|"jdbc:calcite:"
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
literal|"select cast(_MAP['a'] AS varchar(2)) AS a, "
operator|+
literal|" cast(_MAP['b'] AS varchar(2)) AS b, "
operator|+
literal|" cast(_MAP['c'] AS varchar(2)) AS c, "
operator|+
literal|" cast(_MAP['int'] AS integer) AS num"
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
literal|"VIEW"
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
name|void
name|expressions
parameter_list|()
block|{
name|assertSingle
argument_list|(
literal|"select * from view"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where a = 'a'"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where a<> 'a'"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where  'a' = a"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where a = 'b'"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where 'b' = a"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where a in ('a', 'b')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where a in ('a', 'c') and b = 'b'"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where (a = 'ZZ' or a = 'a')  and b = 'b'"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where b = 'b' and a in ('a', 'c')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num = 42 and a in ('a', 'c')"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where a in ('a', 'c') and b = 'c'"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where a in ('a', 'c') and b = 'b' and num = 42"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where a in ('a', 'c') and b = 'b' and num>= 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where a in ('a', 'c') and b = 'b' and num<> 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where a in ('a', 'c') and b = 'b' and num> 42"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num = 42"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where 42 = num"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num> 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where 42> num"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num> 42 and num> 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num> 42 and num< 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num> 42 and num< 42 and num<> 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num> 42 and num< 42 and num = 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num> 42 or num< 42 and num = 42"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num> 42 and num< 42 or num = 42"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num> 42 or num< 42 or num = 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num is null"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num>= 42 and num<= 42 and num = 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num>= 42 and num<= 42 and num<> 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num< 42"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num<> 42"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num>= 42"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num<= 42"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num< 43"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num< 50"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num> 41"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where num> 0"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where (a = 'a' and b = 'b') or (num = 42 and c = 'c')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where c = 'c' and (a in ('a', 'b') or num in (41, 42))"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where (a = 'a' or b = 'b') or (num = 42 and c = 'c')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where a = 'a' and (b = '0' or (b = 'b' and "
operator|+
literal|"(c = '0' or (c = 'c' and num = 42))))"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests negations ({@code NOT} operator).    */
annotation|@
name|Test
name|void
name|notExpression
parameter_list|()
block|{
name|assertEmpty
argument_list|(
literal|"select * from view where not a = 'a'"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not not a = 'a'"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not not not a = 'a'"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not a<> 'a'"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not not not a<> 'a'"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not 'a' = a"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not 'a'<> a"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not a = 'b'"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not 'b' = a"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not a in ('a')"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where a not in ('a')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not a not in ('a')"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not a not in ('b')"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not not a not in ('a')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not not a not in ('b')"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not a in ('a', 'b')"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where a not in ('a', 'b')"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not a not in ('z')"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not a not in ('z')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not a in ('z')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not (not num = 42 or not a in ('a', 'c'))"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where not num> 0"
argument_list|)
expr_stmt|;
name|assertEmpty
argument_list|(
literal|"select * from view where num = 42 and a not in ('a', 'c')"
argument_list|)
expr_stmt|;
name|assertSingle
argument_list|(
literal|"select * from view where not (num> 42 or num< 42 and num = 42)"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertSingle
parameter_list|(
name|String
name|query
parameter_list|)
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
name|query
argument_list|)
operator|.
name|returns
argument_list|(
literal|"A=a; B=b; C=c; NUM=42\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertEmpty
parameter_list|(
name|String
name|query
parameter_list|)
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
name|query
argument_list|)
operator|.
name|returns
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

