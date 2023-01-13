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
name|sql
operator|.
name|type
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
name|RelDataTypeField
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
name|RelDataTypeFieldImpl
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
name|RelRecordType
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
name|Lists
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
name|ArrayList
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
name|isA
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
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
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Test for {@link SqlTypeFactoryImpl}.  */
end_comment

begin_class
class|class
name|SqlTypeFactoryTest
block|{
annotation|@
name|Test
name|void
name|testLeastRestrictiveWithAny
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|sqlBigInt
argument_list|,
name|f
operator|.
name|sqlAny
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveWithNumbers
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|sqlBigInt
argument_list|,
name|f
operator|.
name|sqlInt
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveWithNullability
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|sqlVarcharNullable
argument_list|,
name|f
operator|.
name|sqlAny
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2994">[CALCITE-2994]    * Least restrictive type among structs does not consider nullability</a>. */
annotation|@
name|Test
name|void
name|testLeastRestrictiveWithNullableStruct
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|structOfIntNullable
argument_list|,
name|f
operator|.
name|structOfInt
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|ROW
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveWithNull
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|sqlNull
argument_list|,
name|f
operator|.
name|sqlNull
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|NULL
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveStructWithNull
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|sqlNull
argument_list|,
name|f
operator|.
name|structOfInt
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|ROW
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForImpossibleWithArray
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|arraySqlChar10
argument_list|,
name|f
operator|.
name|sqlChar
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|leastRestrictive
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForArrays
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|arraySqlChar10
argument_list|,
name|f
operator|.
name|arraySqlChar1
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|ARRAY
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
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
name|leastRestrictive
operator|.
name|getComponentType
argument_list|()
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForMultisets
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|multisetSqlChar10Nullable
argument_list|,
name|f
operator|.
name|multisetSqlChar1
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|MULTISET
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
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
name|leastRestrictive
operator|.
name|getComponentType
argument_list|()
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForMultisetsAndArrays
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|multisetSqlChar10Nullable
argument_list|,
name|f
operator|.
name|arraySqlChar1
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|MULTISET
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
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
name|leastRestrictive
operator|.
name|getComponentType
argument_list|()
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForImpossibleWithMultisets
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|multisetSqlChar10Nullable
argument_list|,
name|f
operator|.
name|mapSqlChar1
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|leastRestrictive
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForMaps
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|mapSqlChar10Nullable
argument_list|,
name|f
operator|.
name|mapSqlChar1
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
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
name|leastRestrictive
operator|.
name|getKeyType
argument_list|()
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getValueType
argument_list|()
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForTimestamps
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|sqlTimestampPrec0
argument_list|,
name|f
operator|.
name|sqlTimestampPrec3
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
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
name|leastRestrictive
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForTimestamps2
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|sqlTimestampPrec3
argument_list|,
name|f
operator|.
name|sqlTimestampPrec0
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|leastRestrictive
operator|.
name|isNullable
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
name|leastRestrictive
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForTimestampAndDate
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|sqlTimestampPrec3
argument_list|,
name|f
operator|.
name|sqlDate
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|leastRestrictive
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testLeastRestrictiveForImpossibleWithMaps
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|leastRestrictive
init|=
name|f
operator|.
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Lists
operator|.
name|newArrayList
argument_list|(
name|f
operator|.
name|mapSqlChar10Nullable
argument_list|,
name|f
operator|.
name|arraySqlChar1
argument_list|)
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|leastRestrictive
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link SqlTypeUtil#comparePrecision(int, int)}    * and  {@link SqlTypeUtil#maxPrecision(int, int)}. */
annotation|@
name|Test
name|void
name|testMaxPrecision
parameter_list|()
block|{
specifier|final
name|int
name|un
init|=
name|RelDataType
operator|.
name|PRECISION_NOT_SPECIFIED
decl_stmt|;
name|checkPrecision
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkPrecision
argument_list|(
literal|2
argument_list|,
literal|1
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkPrecision
argument_list|(
literal|2
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|checkPrecision
argument_list|(
literal|2
argument_list|,
name|un
argument_list|,
name|un
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|checkPrecision
argument_list|(
name|un
argument_list|,
literal|2
argument_list|,
name|un
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|checkPrecision
argument_list|(
name|un
argument_list|,
name|un
argument_list|,
name|un
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Unit test for {@link ArraySqlType#getPrecedenceList()}. */
annotation|@
name|Test
name|void
name|testArrayPrecedenceList
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|checkPrecendenceList
argument_list|(
name|f
operator|.
name|arrayBigInt
argument_list|,
name|f
operator|.
name|arrayBigInt
argument_list|,
name|f
operator|.
name|arrayFloat
argument_list|)
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkPrecendenceList
argument_list|(
name|f
operator|.
name|arrayOfArrayBigInt
argument_list|,
name|f
operator|.
name|arrayOfArrayBigInt
argument_list|,
name|f
operator|.
name|arrayOfArrayFloat
argument_list|)
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkPrecendenceList
argument_list|(
name|f
operator|.
name|sqlBigInt
argument_list|,
name|f
operator|.
name|sqlBigInt
argument_list|,
name|f
operator|.
name|sqlFloat
argument_list|)
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkPrecendenceList
argument_list|(
name|f
operator|.
name|multisetBigInt
argument_list|,
name|f
operator|.
name|multisetBigInt
argument_list|,
name|f
operator|.
name|multisetFloat
argument_list|)
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|checkPrecendenceList
argument_list|(
name|f
operator|.
name|arrayBigInt
argument_list|,
name|f
operator|.
name|arrayBigInt
argument_list|,
name|f
operator|.
name|arrayBigIntNullable
argument_list|)
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|int
name|i
init|=
name|checkPrecendenceList
argument_list|(
name|f
operator|.
name|arrayBigInt
argument_list|,
name|f
operator|.
name|sqlBigInt
argument_list|,
name|f
operator|.
name|sqlInt
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Expected assert, got "
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"must contain type: BIGINT"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|int
name|checkPrecendenceList
parameter_list|(
name|RelDataType
name|t
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
return|return
name|t
operator|.
name|getPrecedenceList
argument_list|()
operator|.
name|compareTypePrecedence
argument_list|(
name|type1
argument_list|,
name|type2
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkPrecision
parameter_list|(
name|int
name|p0
parameter_list|,
name|int
name|p1
parameter_list|,
name|int
name|expectedMax
parameter_list|,
name|int
name|expectedComparison
parameter_list|)
block|{
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|maxPrecision
argument_list|(
name|p0
argument_list|,
name|p1
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedMax
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|maxPrecision
argument_list|(
name|p1
argument_list|,
name|p0
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedMax
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|maxPrecision
argument_list|(
name|p0
argument_list|,
name|p0
argument_list|)
argument_list|,
name|is
argument_list|(
name|p0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|maxPrecision
argument_list|(
name|p1
argument_list|,
name|p1
argument_list|)
argument_list|,
name|is
argument_list|(
name|p1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|comparePrecision
argument_list|(
name|p0
argument_list|,
name|p1
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedComparison
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|comparePrecision
argument_list|(
name|p0
argument_list|,
name|p0
argument_list|)
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|comparePrecision
argument_list|(
name|p1
argument_list|,
name|p1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2464">[CALCITE-2464]    * Allow to set nullability for columns of structured types</a>. */
annotation|@
name|Test
name|void
name|createStructTypeWithNullability
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataTypeFactory
name|typeFactory
init|=
name|f
operator|.
name|typeFactory
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|RelDataTypeField
name|field0
init|=
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"i"
argument_list|,
literal|0
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataTypeField
name|field1
init|=
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"s"
argument_list|,
literal|1
argument_list|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|)
decl_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|field0
argument_list|)
expr_stmt|;
name|fields
operator|.
name|add
argument_list|(
name|field1
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|recordType
init|=
operator|new
name|RelRecordType
argument_list|(
name|fields
argument_list|)
decl_stmt|;
comment|// nullable false by default
specifier|final
name|RelDataType
name|copyRecordType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|recordType
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertFalse
argument_list|(
name|recordType
operator|.
name|isNullable
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|copyRecordType
operator|.
name|isNullable
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3429">[CALCITE-3429]    * AssertionError thrown for user-defined table function with map argument</a>. */
annotation|@
name|Test
name|void
name|testCreateTypeWithJavaMapType
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|RelDataType
name|relDataType
init|=
name|f
operator|.
name|typeFactory
operator|.
name|createJavaType
argument_list|(
name|Map
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|relDataType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|MAP
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|relDataType
operator|.
name|getKeyType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|f
operator|.
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|MAP
argument_list|)
expr_stmt|;
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"use createMapType() instead"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3924">[CALCITE-3924]    * Fix flakey test to handle TIMESTAMP and TIMESTAMP(0) correctly</a>. */
annotation|@
name|Test
name|void
name|testCreateSqlTypeWithPrecision
parameter_list|()
block|{
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
name|checkCreateSqlTypeWithPrecision
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|,
name|SqlTypeName
operator|.
name|TIME
argument_list|)
expr_stmt|;
name|checkCreateSqlTypeWithPrecision
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
expr_stmt|;
name|checkCreateSqlTypeWithPrecision
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|,
name|SqlTypeName
operator|.
name|TIME_WITH_LOCAL_TIME_ZONE
argument_list|)
expr_stmt|;
name|checkCreateSqlTypeWithPrecision
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkCreateSqlTypeWithPrecision
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlTypeName
name|sqlTypeName
parameter_list|)
block|{
name|RelDataType
name|ts
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|)
decl_stmt|;
name|RelDataType
name|tsWithoutPrecision
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|RelDataType
name|tsWithPrecision0
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|RelDataType
name|tsWithPrecision1
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RelDataType
name|tsWithPrecision2
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|RelDataType
name|tsWithPrecision3
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
literal|3
argument_list|)
decl_stmt|;
comment|// for instance, 8 exceeds max precision for timestamp which is 3
name|RelDataType
name|tsWithPrecision8
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|sqlTypeName
argument_list|,
literal|8
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ts
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(0)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ts
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithoutPrecision
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithoutPrecision
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|" NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision0
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(0)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision0
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(0) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision1
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(1)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision1
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(1) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision2
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(2)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision2
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(2) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision3
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(3)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision3
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(3) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision8
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(3)"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision8
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
name|sqlTypeName
operator|.
name|getName
argument_list|()
operator|+
literal|"(3) NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ts
operator|!=
name|tsWithoutPrecision
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ts
operator|==
name|tsWithPrecision0
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|tsWithPrecision3
operator|==
name|tsWithPrecision8
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test that the {@code UNKNOWN} type is a {@link BasicSqlType} and remains    * so when nullified. */
annotation|@
name|Test
name|void
name|testUnknownCreateWithNullabilityTypeConsistency
parameter_list|()
block|{
specifier|final
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|unknownType
init|=
name|f
operator|.
name|typeFactory
operator|.
name|createUnknownType
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|unknownType
argument_list|,
name|isA
argument_list|(
name|BasicSqlType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|unknownType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|UNKNOWN
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unknownType
operator|.
name|isNullable
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|unknownType
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"UNKNOWN NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|nullableType
init|=
name|f
operator|.
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|unknownType
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|nullableType
argument_list|,
name|isA
argument_list|(
name|BasicSqlType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|nullableType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|UNKNOWN
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nullableType
operator|.
name|isNullable
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|nullableType
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"UNKNOWN"
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|unknownType2
init|=
name|f
operator|.
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|nullableType
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|unknownType2
argument_list|,
name|is
argument_list|(
name|unknownType
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|unknownType2
argument_list|,
name|isA
argument_list|(
name|BasicSqlType
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|unknownType2
operator|.
name|getSqlTypeName
argument_list|()
argument_list|,
name|is
argument_list|(
name|SqlTypeName
operator|.
name|UNKNOWN
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|unknownType2
operator|.
name|isNullable
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|unknownType2
operator|.
name|getFullTypeString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"UNKNOWN NOT NULL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

