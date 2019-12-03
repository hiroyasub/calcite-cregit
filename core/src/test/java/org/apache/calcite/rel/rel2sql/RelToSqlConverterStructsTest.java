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
name|rel
operator|.
name|rel2sql
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
name|jdbc
operator|.
name|CalciteSchema
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
name|Expression
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
name|RelCollation
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
name|RelDistribution
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
name|RelReferentialConstraint
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
name|rel
operator|.
name|type
operator|.
name|RelProtoDataType
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
name|Function
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
name|SchemaVersion
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
name|Table
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
name|dialect
operator|.
name|CalciteSqlDialect
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
name|ImmutableSet
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
name|Collection
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Tests for {@link RelToSqlConverter} on a schema that has nested structures of multiple  * levels.  */
end_comment

begin_class
specifier|public
class|class
name|RelToSqlConverterStructsTest
block|{
specifier|private
specifier|static
specifier|final
name|Schema
name|SCHEMA
init|=
operator|new
name|Schema
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Table
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|TABLE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTableNames
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"myTable"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelProtoDataType
name|getType
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTypeNames
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Function
argument_list|>
name|getFunctions
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getFunctionNames
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Schema
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getSubSchemaNames
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isMutable
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Schema
name|snapshot
parameter_list|(
name|SchemaVersion
name|version
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Table
name|TABLE
init|=
operator|new
name|Table
argument_list|()
block|{
comment|/**      * Table schema is as following:      *  myTable(      *          a: BIGINT,      *          n1: STRUCT<      *                n11: STRUCT<b: BIGINT>,      *                n12: STRUCT<c: BIGINT>      *>,      *          n2: STRUCT<d: BIGINT>,      *          e: BIGINT      *  )      */
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|tf
parameter_list|)
block|{
name|RelDataType
name|bigint
init|=
name|tf
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
decl_stmt|;
name|RelDataType
name|n1Type
init|=
name|tf
operator|.
name|createStructType
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|tf
operator|.
name|createStructType
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bigint
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"b"
argument_list|)
argument_list|)
argument_list|,
name|tf
operator|.
name|createStructType
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bigint
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"c"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"n11"
argument_list|,
literal|"n12"
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|n2Type
init|=
name|tf
operator|.
name|createStructType
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bigint
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"d"
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|tf
operator|.
name|createStructType
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bigint
argument_list|,
name|n1Type
argument_list|,
name|n2Type
argument_list|,
name|bigint
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"n1"
argument_list|,
literal|"n2"
argument_list|,
literal|"e"
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|STATS
return|;
block|}
annotation|@
name|Override
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
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
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Statistic
name|STATS
init|=
operator|new
name|Statistic
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Double
name|getRowCount
parameter_list|()
block|{
return|return
literal|0D
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getKeys
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RelReferentialConstraint
argument_list|>
name|getReferentialConstraints
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollations
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDistribution
name|getDistribution
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SchemaPlus
name|ROOT_SCHEMA
init|=
name|CalciteSchema
operator|.
name|createRootSchema
argument_list|(
literal|false
argument_list|)
operator|.
name|add
argument_list|(
literal|"myDb"
argument_list|,
name|SCHEMA
argument_list|)
operator|.
name|plus
argument_list|()
decl_stmt|;
specifier|private
name|RelToSqlConverterTest
operator|.
name|Sql
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|RelToSqlConverterTest
operator|.
name|Sql
argument_list|(
name|ROOT_SCHEMA
argument_list|,
name|sql
argument_list|,
name|CalciteSqlDialect
operator|.
name|DEFAULT
argument_list|,
name|RelToSqlConverterTest
operator|.
name|DEFAULT_REL_CONFIG
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedSchemaSelectStar
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT * FROM \"myTable\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"a\", "
operator|+
literal|"ROW(ROW(\"n1\".\"n11\".\"b\"), ROW(\"n1\".\"n12\".\"c\")) AS \"n1\", "
operator|+
literal|"ROW(\"n2\".\"d\") AS \"n2\", "
operator|+
literal|"\"e\"\n"
operator|+
literal|"FROM \"myDb\".\"myTable\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedSchemaRootColumns
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT \"a\", \"e\" FROM \"myTable\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"a\", "
operator|+
literal|"\"e\"\n"
operator|+
literal|"FROM \"myDb\".\"myTable\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNestedSchemaNestedColumns
parameter_list|()
block|{
name|String
name|query
init|=
literal|"SELECT \"a\", \"e\", "
operator|+
literal|"\"myTable\".\"n1\".\"n11\".\"b\", "
operator|+
literal|"\"myTable\".\"n2\".\"d\" "
operator|+
literal|"FROM \"myTable\""
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT \"a\", "
operator|+
literal|"\"e\", "
operator|+
literal|"\"n1\".\"n11\".\"b\", "
operator|+
literal|"\"n2\".\"d\"\n"
operator|+
literal|"FROM \"myDb\".\"myTable\""
decl_stmt|;
name|sql
argument_list|(
name|query
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

