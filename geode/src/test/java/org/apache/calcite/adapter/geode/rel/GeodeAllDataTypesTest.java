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
name|geode
operator|.
name|rel
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
name|geode
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|Region
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
name|Date
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
name|sql
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Test with different types of data like boolean, time, timestamp  */
end_comment

begin_class
specifier|public
class|class
name|GeodeAllDataTypesTest
extends|extends
name|AbstractGeodeTest
block|{
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
block|{
specifier|final
name|Cache
name|cache
init|=
name|POLICY
operator|.
name|cache
argument_list|()
decl_stmt|;
specifier|final
name|Region
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|region
init|=
name|cache
operator|.
expr|<
name|String
decl_stmt|,
name|Object
decl|>
name|createRegionFactory
argument_list|()
decl|.
name|create
argument_list|(
literal|"allDataTypesRegion"
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|mapList
init|=
name|createMapList
argument_list|()
decl_stmt|;
operator|new
name|JsonLoader
argument_list|(
name|region
argument_list|)
operator|.
name|loadMapList
argument_list|(
name|mapList
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|createMapList
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"booleanValue"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"dateValue"
argument_list|,
name|Date
operator|.
name|valueOf
argument_list|(
literal|"2018-02-03"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"timeValue"
argument_list|,
name|Time
operator|.
name|valueOf
argument_list|(
literal|"02:22:23"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"timestampValue"
argument_list|,
name|Timestamp
operator|.
name|valueOf
argument_list|(
literal|"2018-02-03 02:22:33"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"stringValue"
argument_list|,
literal|"abc"
argument_list|)
operator|.
name|put
argument_list|(
literal|"floatValue"
argument_list|,
literal|1.5678
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"booleanValue"
argument_list|,
literal|false
argument_list|)
operator|.
name|put
argument_list|(
literal|"dateValue"
argument_list|,
name|Date
operator|.
name|valueOf
argument_list|(
literal|"2018-02-04"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"timeValue"
argument_list|,
name|Time
operator|.
name|valueOf
argument_list|(
literal|"03:22:23"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"timestampValue"
argument_list|,
name|Timestamp
operator|.
name|valueOf
argument_list|(
literal|"2018-02-04 04:22:33"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"stringValue"
argument_list|,
literal|"def"
argument_list|)
operator|.
name|put
argument_list|(
literal|"floatValue"
argument_list|,
literal|3.5678
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
name|ImmutableMap
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"booleanValue"
argument_list|,
literal|true
argument_list|)
operator|.
name|put
argument_list|(
literal|"dateValue"
argument_list|,
name|Date
operator|.
name|valueOf
argument_list|(
literal|"2018-02-05"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"timeValue"
argument_list|,
name|Time
operator|.
name|valueOf
argument_list|(
literal|"04:22:23"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"timestampValue"
argument_list|,
name|Timestamp
operator|.
name|valueOf
argument_list|(
literal|"2018-02-05 04:22:33"
argument_list|)
argument_list|)
operator|.
name|put
argument_list|(
literal|"stringValue"
argument_list|,
literal|"ghi"
argument_list|)
operator|.
name|put
argument_list|(
literal|"floatValue"
argument_list|,
literal|8.9267
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
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
literal|"geode"
argument_list|,
operator|new
name|GeodeSchema
argument_list|(
name|POLICY
operator|.
name|cache
argument_list|()
argument_list|,
name|Collections
operator|.
name|singleton
argument_list|(
literal|"allDataTypesRegion"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|connection
return|;
block|}
block|}
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|calciteAssert
parameter_list|()
block|{
return|return
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
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlSingleBooleanWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT booleanValue as booleanValue "
operator|+
literal|"FROM geode.allDataTypesRegion WHERE booleanValue = true"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT booleanValue AS booleanValue FROM /allDataTypesRegion "
operator|+
literal|"WHERE booleanValue = true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlBooleanColumnFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT booleanValue as booleanValue "
operator|+
literal|"FROM geode.allDataTypesRegion WHERE booleanValue"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT booleanValue AS booleanValue FROM /allDataTypesRegion "
operator|+
literal|"WHERE booleanValue = true"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlBooleanColumnNotFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT booleanValue as booleanValue "
operator|+
literal|"FROM geode.allDataTypesRegion WHERE not booleanValue"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT booleanValue AS booleanValue FROM /allDataTypesRegion "
operator|+
literal|"WHERE booleanValue = false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlMultipleBooleanWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT booleanValue as booleanValue "
operator|+
literal|"FROM geode.allDataTypesRegion WHERE booleanValue = true OR booleanValue = false"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT booleanValue AS booleanValue FROM /allDataTypesRegion "
operator|+
literal|"WHERE booleanValue = true OR booleanValue = false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlWhereWithMultipleOrForLiteralFields
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT stringValue "
operator|+
literal|"FROM geode.allDataTypesRegion WHERE (stringValue = 'abc' OR stringValue = 'def') OR "
operator|+
literal|"(floatValue = 1.5678 OR floatValue = null) OR "
operator|+
literal|"(booleanValue = true OR booleanValue = false OR booleanValue = null)"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT stringValue AS stringValue "
operator|+
literal|"FROM /allDataTypesRegion WHERE "
operator|+
literal|"stringValue IN SET('abc', 'def') OR floatValue = 1.5678 "
operator|+
literal|"OR booleanValue = true OR booleanValue = false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlSingleDateWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT dateValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE dateValue = DATE '2018-02-03'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT dateValue AS dateValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE dateValue = DATE '2018-02-03'"
argument_list|)
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT dateValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE dateValue> DATE '2018-02-03'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT dateValue AS dateValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE dateValue> DATE '2018-02-03'"
argument_list|)
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT dateValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE dateValue< DATE '2018-02-03'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT dateValue AS dateValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE dateValue< DATE '2018-02-03'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlMultipleDateWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT dateValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE dateValue = DATE '2018-02-03'\n"
operator|+
literal|"  OR dateValue = DATE '2018-02-04'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT dateValue AS dateValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE dateValue IN SET(DATE '2018-02-03',"
operator|+
literal|" DATE '2018-02-04')"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlSingleTimeWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT timeValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE timeValue = TIME '02:22:23'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT timeValue AS timeValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE timeValue = TIME '02:22:23'"
argument_list|)
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT timeValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE timeValue> TIME '02:22:23'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT timeValue AS timeValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE timeValue> TIME '02:22:23'"
argument_list|)
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT timeValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE timeValue< TIME '02:22:23'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT timeValue AS timeValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE timeValue< TIME '02:22:23'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlMultipleTimeWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT timeValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE timeValue = TIME '02:22:23'\n"
operator|+
literal|"  OR timeValue = TIME '03:22:23'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT timeValue AS timeValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE timeValue IN SET(TIME '02:22:23', TIME '03:22:23')"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlSingleTimestampWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT timestampValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE timestampValue = TIMESTAMP '2018-02-03 02:22:33'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT timestampValue AS timestampValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE timestampValue = TIMESTAMP '2018-02-03 02:22:33'"
argument_list|)
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT timestampValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE timestampValue> TIMESTAMP '2018-02-03 02:22:33'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT timestampValue AS timestampValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE timestampValue> TIMESTAMP '2018-02-03 02:22:33'"
argument_list|)
argument_list|)
expr_stmt|;
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT timestampValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE timestampValue< TIMESTAMP '2018-02-03 02:22:33'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|0
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT timestampValue AS timestampValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE timestampValue< TIMESTAMP '2018-02-03 02:22:33'"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlMultipleTimestampWhereFilter
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT timestampValue\n"
operator|+
literal|"FROM geode.allDataTypesRegion\n"
operator|+
literal|"WHERE timestampValue = TIMESTAMP '2018-02-03 02:22:33'\n"
operator|+
literal|"  OR timestampValue = TIMESTAMP '2018-02-05 04:22:33'"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|2
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT timestampValue AS timestampValue "
operator|+
literal|"FROM /allDataTypesRegion "
operator|+
literal|"WHERE timestampValue IN SET("
operator|+
literal|"TIMESTAMP '2018-02-03 02:22:33', "
operator|+
literal|"TIMESTAMP '2018-02-05 04:22:33')"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlWhereWithMultipleOrForAllFields
parameter_list|()
block|{
name|calciteAssert
argument_list|()
operator|.
name|query
argument_list|(
literal|"SELECT stringValue "
operator|+
literal|"FROM geode.allDataTypesRegion WHERE (stringValue = 'abc' OR stringValue = 'def') OR "
operator|+
literal|"(floatValue = 1.5678 OR floatValue = null) OR "
operator|+
literal|"(dateValue = DATE '2018-02-05' OR dateValue = DATE '2018-02-06' ) OR "
operator|+
literal|"(timeValue = TIME '03:22:23' OR timeValue = TIME '07:22:23') OR "
operator|+
literal|"(timestampValue = TIMESTAMP '2018-02-05 04:22:33' OR "
operator|+
literal|"timestampValue = TIMESTAMP '2017-02-05 04:22:33') OR "
operator|+
literal|"(booleanValue = true OR booleanValue = false OR booleanValue = null)"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|3
argument_list|)
operator|.
name|queryContains
argument_list|(
name|GeodeAssertions
operator|.
name|query
argument_list|(
literal|"SELECT stringValue AS stringValue "
operator|+
literal|"FROM /allDataTypesRegion WHERE "
operator|+
literal|"stringValue IN SET('abc', 'def') OR floatValue = 1.5678 OR dateValue "
operator|+
literal|"IN SET(DATE '2018-02-05', DATE '2018-02-06') OR timeValue "
operator|+
literal|"IN SET(TIME '03:22:23', TIME '07:22:23') OR timestampValue "
operator|+
literal|"IN SET(TIMESTAMP '2018-02-05 04:22:33', TIMESTAMP '2017-02-05 04:22:33') "
operator|+
literal|"OR booleanValue = true OR booleanValue = false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End GeodeAllDataTypesTest.java
end_comment

end_unit

