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
name|DataContext
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
name|linq4j
operator|.
name|Enumerable
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
name|Linq4j
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
name|schema
operator|.
name|ScannableTable
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
name|Schema
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
name|Statistic
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
name|Statistics
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
name|SqlCall
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
name|SqlNode
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
name|util
operator|.
name|ImmutableBitSet
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

begin_comment
comment|/** A table function that returns states and their boundaries; also national  * parks.  *  *<p>Has same content as  *<code>file/src/test/resources/geo/states.json</code>. */
end_comment

begin_class
specifier|public
class|class
name|StatesTableFunction
block|{
specifier|private
name|StatesTableFunction
parameter_list|()
block|{
block|}
specifier|private
specifier|static
specifier|final
name|Object
index|[]
index|[]
name|STATE_ROWS
init|=
block|{
block|{
literal|"NV"
block|,
literal|"Polygon((-120 42, -114 42, -114 37, -114.75 35.1, -120 39,"
operator|+
literal|" -120 42))"
block|}
block|,
block|{
literal|"UT"
block|,
literal|"Polygon((-114 42, -111.05 42, -111.05 41, -109.05 41, -109.05 37,"
operator|+
literal|" -114 37, -114 42))"
block|}
block|,
block|{
literal|"CA"
block|,
literal|"Polygon((-124.25 42, -120 42, -120 39, -114.75 35.1,"
operator|+
literal|" -114.75 32.5, -117.15 32.5, -118.30 33.75, -120.5 34.5,"
operator|+
literal|" -122.4 37.2, -124.25 42))"
block|}
block|,
block|{
literal|"AZ"
block|,
literal|"Polygon((-114 37, -109.05 37, -109.05 31.33, -111.07 31.33,"
operator|+
literal|" -114.75 32.5, -114.75 35.1, -114 37))"
block|}
block|,
block|{
literal|"CO"
block|,
literal|"Polygon((-109.05 41, -102 41, -102 37, -109.05 37, -109.05 41))"
block|}
block|,
block|{
literal|"OR"
block|,
literal|"Polygon((-123.9 46.2, -122.7 45.7, -119 46, -117 46, -116.5 45.5,"
operator|+
literal|" -117.03 44.2, -117.03 42, -124.25 42, -124.6 42.8,"
operator|+
literal|" -123.9 46.2))"
block|}
block|,
block|{
literal|"WA"
block|,
literal|"Polygon((-124.80 48.4, -123.2 48.2, -123.2 49, -117 49, -117 46,"
operator|+
literal|" -119 46, -122.7 45.7, -123.9 46.2, -124.80 48.4))"
block|}
block|,
block|{
literal|"ID"
block|,
literal|"Polygon((-117 49, -116.05 49, -116.05 48, -114.4 46.6,"
operator|+
literal|" -112.9 44.45, -111.05 44.45, -111.05 42, -117.03 42,"
operator|+
literal|" -117.03 44.2, -116.5 45.5, -117 46, -117 49))"
block|}
block|,
block|{
literal|"MT"
block|,
literal|"Polygon((-116.05 49, -104.05 49, -104.05 45, -111.05 45,"
operator|+
literal|" -111.05 44.45, -112.9 44.45, -114.4 46.6, -116.05 48,"
operator|+
literal|" -116.05 49))"
block|}
block|,
block|{
literal|"WY"
block|,
literal|"Polygon((-111.05 45, -104.05 45, -104.05 41, -111.05 41,"
operator|+
literal|" -111.05 45))"
block|}
block|,
block|{
literal|"NM"
block|,
literal|"Polygon((-109.05 37, -103 37, -103 32, -106.65 32, -106.5 31.8,"
operator|+
literal|" -108.2 31.8, -108.2 31.33, -109.05 31.33, -109.05 37))"
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Object
index|[]
index|[]
name|PARK_ROWS
init|=
block|{
block|{
literal|"Yellowstone NP"
block|,
literal|"Polygon((-111.2 45.1, -109.30 45.1, -109.30 44.1,"
operator|+
literal|" -109 43.8, -110 43, -111.2 43.4, -111.2 45.1))"
block|}
block|,
block|{
literal|"Yosemite NP"
block|,
literal|"Polygon((-120.2 38, -119.30 38.2, -119 37.7,"
operator|+
literal|" -119.9 37.6, -120.2 38))"
block|}
block|,
block|{
literal|"Death Valley NP"
block|,
literal|"Polygon((-118.2 37.3, -117 37, -116.3 35.7,"
operator|+
literal|" -117 35.7, -117.2 36.2, -117.8 36.4, -118.2 37.3))"
block|}
block|,   }
decl_stmt|;
specifier|public
specifier|static
name|ScannableTable
name|states
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
return|return
name|eval
argument_list|(
name|STATE_ROWS
argument_list|)
return|;
block|}
empty_stmt|;
specifier|public
specifier|static
name|ScannableTable
name|parks
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
return|return
name|eval
argument_list|(
name|PARK_ROWS
argument_list|)
return|;
block|}
empty_stmt|;
specifier|private
specifier|static
name|ScannableTable
name|eval
parameter_list|(
specifier|final
name|Object
index|[]
index|[]
name|rows
parameter_list|)
block|{
return|return
operator|new
name|ScannableTable
argument_list|()
block|{
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|rows
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|add
argument_list|(
literal|"geom"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|of
argument_list|(
name|rows
operator|.
name|length
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
return|;
block|}
specifier|public
name|boolean
name|isRolledUp
parameter_list|(
name|String
name|column
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|rolledUpColumnValidInsideAgg
parameter_list|(
name|String
name|column
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlNode
name|parent
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

