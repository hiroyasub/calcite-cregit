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
name|sql
operator|.
name|SqlBasicTypeNameSpec
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
name|SqlCollectionTypeNameSpec
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
name|SqlIdentifier
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
name|SqlRowTypeNameSpec
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
name|Locale
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeUtil
operator|.
name|areSameFamily
import|;
end_import

begin_import
import|import static
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
name|SqlTypeUtil
operator|.
name|convertTypeToSpec
import|;
end_import

begin_import
import|import static
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
name|SqlTypeUtil
operator|.
name|equalAsCollectionSansNullability
import|;
end_import

begin_import
import|import static
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
name|SqlTypeUtil
operator|.
name|equalAsMapSansNullability
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
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Test of {@link org.apache.calcite.sql.type.SqlTypeUtil}.  */
end_comment

begin_class
class|class
name|SqlTypeUtilTest
block|{
specifier|private
specifier|final
name|SqlTypeFixture
name|f
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
annotation|@
name|Test
name|void
name|testTypesIsSameFamilyWithNumberTypes
parameter_list|()
block|{
name|assertThat
argument_list|(
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlBigInt
argument_list|,
name|f
operator|.
name|sqlBigInt
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlInt
argument_list|,
name|f
operator|.
name|sqlBigInt
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlFloat
argument_list|,
name|f
operator|.
name|sqlBigInt
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlInt
argument_list|,
name|f
operator|.
name|sqlBigIntNullable
argument_list|)
argument_list|)
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
name|testTypesIsSameFamilyWithCharTypes
parameter_list|()
block|{
name|assertThat
argument_list|(
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlVarchar
argument_list|,
name|f
operator|.
name|sqlVarchar
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlVarchar
argument_list|,
name|f
operator|.
name|sqlChar
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlVarchar
argument_list|,
name|f
operator|.
name|sqlVarcharNullable
argument_list|)
argument_list|)
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
name|testTypesIsSameFamilyWithInconvertibleTypes
parameter_list|()
block|{
name|assertThat
argument_list|(
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlBoolean
argument_list|,
name|f
operator|.
name|sqlBigInt
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlFloat
argument_list|,
name|f
operator|.
name|sqlBoolean
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|f
operator|.
name|sqlInt
argument_list|,
name|f
operator|.
name|sqlDate
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
annotation|@
name|Test
name|void
name|testTypesIsSameFamilyWithNumberStructTypes
parameter_list|()
block|{
specifier|final
name|RelDataType
name|bigIntAndFloat
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlBigInt
argument_list|,
name|f
operator|.
name|sqlFloat
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|floatAndBigInt
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlFloat
argument_list|,
name|f
operator|.
name|sqlBigInt
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bigIntAndFloat
argument_list|,
name|floatAndBigInt
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bigIntAndFloat
argument_list|,
name|bigIntAndFloat
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|bigIntAndFloat
argument_list|,
name|bigIntAndFloat
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|floatAndBigInt
argument_list|,
name|floatAndBigInt
argument_list|)
argument_list|)
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
name|testTypesIsSameFamilyWithCharStructTypes
parameter_list|()
block|{
specifier|final
name|RelDataType
name|varCharStruct
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlVarchar
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|charStruct
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlChar
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|varCharStruct
argument_list|,
name|charStruct
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|charStruct
argument_list|,
name|varCharStruct
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|varCharStruct
argument_list|,
name|varCharStruct
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
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|charStruct
argument_list|,
name|charStruct
argument_list|)
argument_list|)
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
name|testTypesIsSameFamilyWithInconvertibleStructTypes
parameter_list|()
block|{
specifier|final
name|RelDataType
name|dateStruct
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlDate
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|boolStruct
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlBoolean
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|dateStruct
argument_list|,
name|boolStruct
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|charIntStruct
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlChar
argument_list|,
name|f
operator|.
name|sqlInt
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|charDateStruct
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlChar
argument_list|,
name|f
operator|.
name|sqlDate
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|charIntStruct
argument_list|,
name|charDateStruct
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|boolDateStruct
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlBoolean
argument_list|,
name|f
operator|.
name|sqlDate
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|floatIntStruct
init|=
name|struct
argument_list|(
name|f
operator|.
name|sqlInt
argument_list|,
name|f
operator|.
name|sqlFloat
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|areSameFamily
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|boolDateStruct
argument_list|,
name|floatIntStruct
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
annotation|@
name|Test
name|void
name|testModifyTypeCoercionMappings
parameter_list|()
block|{
name|SqlTypeMappingRules
operator|.
name|Builder
name|builder
init|=
name|SqlTypeMappingRules
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|SqlTypeCoercionRule
name|defaultRules
init|=
name|SqlTypeCoercionRule
operator|.
name|instance
argument_list|()
decl_stmt|;
name|builder
operator|.
name|addAll
argument_list|(
name|defaultRules
operator|.
name|getTypeMapping
argument_list|()
argument_list|)
expr_stmt|;
comment|// Do the tweak, for example, if we want to add a rule to allow
comment|// coerce BOOLEAN to TIMESTAMP.
name|builder
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|builder
operator|.
name|copyValues
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
operator|.
name|add
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
comment|// Initialize a SqlTypeCoercionRules with the new builder mappings.
name|SqlTypeCoercionRule
name|typeCoercionRules
init|=
name|SqlTypeCoercionRule
operator|.
name|instance
argument_list|(
name|builder
operator|.
name|map
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|canCastFrom
argument_list|(
name|f
operator|.
name|sqlTimestampPrec3
argument_list|,
name|f
operator|.
name|sqlBoolean
argument_list|,
literal|true
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|SqlTypeCoercionRule
operator|.
name|THREAD_PROVIDERS
operator|.
name|set
argument_list|(
name|typeCoercionRules
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|SqlTypeUtil
operator|.
name|canCastFrom
argument_list|(
name|f
operator|.
name|sqlTimestampPrec3
argument_list|,
name|f
operator|.
name|sqlBoolean
argument_list|,
literal|true
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// Recover the mappings to default.
name|SqlTypeCoercionRule
operator|.
name|THREAD_PROVIDERS
operator|.
name|set
argument_list|(
name|defaultRules
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEqualAsCollectionSansNullability
parameter_list|()
block|{
comment|// case array
name|assertThat
argument_list|(
name|equalAsCollectionSansNullability
argument_list|(
name|f
operator|.
name|typeFactory
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
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// case multiset
name|assertThat
argument_list|(
name|equalAsCollectionSansNullability
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|,
name|f
operator|.
name|multisetBigInt
argument_list|,
name|f
operator|.
name|multisetBigIntNullable
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// multiset and array are not equal.
name|assertThat
argument_list|(
name|equalAsCollectionSansNullability
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|,
name|f
operator|.
name|arrayBigInt
argument_list|,
name|f
operator|.
name|multisetBigInt
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEqualAsMapSansNullability
parameter_list|()
block|{
name|assertThat
argument_list|(
name|equalAsMapSansNullability
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|,
name|f
operator|.
name|mapOfInt
argument_list|,
name|f
operator|.
name|mapOfIntNullable
argument_list|)
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
name|testConvertTypeToSpec
parameter_list|()
block|{
name|SqlBasicTypeNameSpec
name|nullSpec
init|=
operator|(
name|SqlBasicTypeNameSpec
operator|)
name|convertTypeToSpec
argument_list|(
name|f
operator|.
name|sqlNull
argument_list|)
operator|.
name|getTypeNameSpec
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|nullSpec
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"NULL"
argument_list|)
argument_list|)
expr_stmt|;
name|SqlBasicTypeNameSpec
name|unknownSpec
init|=
operator|(
name|SqlBasicTypeNameSpec
operator|)
name|convertTypeToSpec
argument_list|(
name|f
operator|.
name|sqlUnknown
argument_list|)
operator|.
name|getTypeNameSpec
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|unknownSpec
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"UNKNOWN"
argument_list|)
argument_list|)
expr_stmt|;
name|SqlBasicTypeNameSpec
name|basicSpec
init|=
operator|(
name|SqlBasicTypeNameSpec
operator|)
name|convertTypeToSpec
argument_list|(
name|f
operator|.
name|sqlBigInt
argument_list|)
operator|.
name|getTypeNameSpec
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|basicSpec
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"BIGINT"
argument_list|)
argument_list|)
expr_stmt|;
name|SqlCollectionTypeNameSpec
name|arraySpec
init|=
operator|(
name|SqlCollectionTypeNameSpec
operator|)
name|convertTypeToSpec
argument_list|(
name|f
operator|.
name|arrayBigInt
argument_list|)
operator|.
name|getTypeNameSpec
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|arraySpec
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"ARRAY"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|arraySpec
operator|.
name|getElementTypeName
argument_list|()
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"BIGINT"
argument_list|)
argument_list|)
expr_stmt|;
name|SqlCollectionTypeNameSpec
name|multisetSpec
init|=
operator|(
name|SqlCollectionTypeNameSpec
operator|)
name|convertTypeToSpec
argument_list|(
name|f
operator|.
name|multisetBigInt
argument_list|)
operator|.
name|getTypeNameSpec
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|multisetSpec
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"MULTISET"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|multisetSpec
operator|.
name|getElementTypeName
argument_list|()
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"BIGINT"
argument_list|)
argument_list|)
expr_stmt|;
name|SqlRowTypeNameSpec
name|rowSpec
init|=
operator|(
name|SqlRowTypeNameSpec
operator|)
name|convertTypeToSpec
argument_list|(
name|f
operator|.
name|structOfInt
argument_list|)
operator|.
name|getTypeNameSpec
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
init|=
name|SqlIdentifier
operator|.
name|simpleNames
argument_list|(
name|rowSpec
operator|.
name|getFieldNames
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|fieldTypeNames
init|=
name|rowSpec
operator|.
name|getFieldTypes
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|f
lambda|->
name|f
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rowSpec
operator|.
name|getTypeName
argument_list|()
operator|.
name|getSimple
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"ROW"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldNames
argument_list|,
name|is
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"i"
argument_list|,
literal|"j"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|fieldTypeNames
argument_list|,
name|is
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|"INTEGER"
argument_list|,
literal|"INTEGER"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testGetMaxPrecisionScaleDecimal
parameter_list|()
block|{
name|RelDataType
name|decimal
init|=
name|SqlTypeUtil
operator|.
name|getMaxPrecisionScaleDecimal
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|decimal
argument_list|,
name|is
argument_list|(
name|f
operator|.
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|19
argument_list|,
literal|9
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RelDataType
name|struct
parameter_list|(
name|RelDataType
modifier|...
name|relDataTypes
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|f
operator|.
name|typeFactory
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|relDataTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
literal|"field"
operator|+
name|i
argument_list|,
name|relDataTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|private
name|void
name|compareTypesIgnoringNullability
parameter_list|(
name|String
name|comment
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|,
name|boolean
name|expectedResult
parameter_list|)
block|{
name|String
name|typeString1
init|=
name|type1
operator|.
name|getFullTypeString
argument_list|()
decl_stmt|;
name|String
name|typeString2
init|=
name|type2
operator|.
name|getFullTypeString
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
literal|"The result of SqlTypeUtil.equalSansNullability"
operator|+
literal|"(typeFactory, "
operator|+
name|typeString1
operator|+
literal|", "
operator|+
name|typeString2
operator|+
literal|") is incorrect: "
operator|+
name|comment
argument_list|,
name|SqlTypeUtil
operator|.
name|equalSansNullability
argument_list|(
name|f
operator|.
name|typeFactory
argument_list|,
name|type1
argument_list|,
name|type2
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedResult
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"The result of SqlTypeUtil.equalSansNullability"
operator|+
literal|"("
operator|+
name|typeString1
operator|+
literal|", "
operator|+
name|typeString2
operator|+
literal|") is incorrect: "
operator|+
name|comment
argument_list|,
name|SqlTypeUtil
operator|.
name|equalSansNullability
argument_list|(
name|type1
argument_list|,
name|type2
argument_list|)
argument_list|,
name|is
argument_list|(
name|expectedResult
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testEqualSansNullability
parameter_list|()
block|{
name|RelDataType
name|bigIntType
init|=
name|f
operator|.
name|sqlBigInt
decl_stmt|;
name|RelDataType
name|nullableBigIntType
init|=
name|f
operator|.
name|sqlBigIntNullable
decl_stmt|;
name|RelDataType
name|varCharType
init|=
name|f
operator|.
name|sqlVarchar
decl_stmt|;
name|RelDataType
name|bigIntType1
init|=
name|f
operator|.
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|nullableBigIntType
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|compareTypesIgnoringNullability
argument_list|(
literal|"different types should return false. "
argument_list|,
name|bigIntType
argument_list|,
name|varCharType
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|compareTypesIgnoringNullability
argument_list|(
literal|"types differing only in nullability should return true."
argument_list|,
name|bigIntType
argument_list|,
name|nullableBigIntType
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|compareTypesIgnoringNullability
argument_list|(
literal|"identical types should return true."
argument_list|,
name|bigIntType
argument_list|,
name|bigIntType1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCanAlwaysCastToUnknownFromBasic
parameter_list|()
block|{
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
name|RelDataType
name|nullableUnknownType
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
for|for
control|(
name|SqlTypeName
name|fromTypeName
range|:
name|SqlTypeName
operator|.
name|values
argument_list|()
control|)
block|{
name|BasicSqlType
name|fromType
decl_stmt|;
try|try
block|{
comment|// This only works for basic types. Ignore the rest.
name|fromType
operator|=
operator|(
name|BasicSqlType
operator|)
name|f
operator|.
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|fromTypeName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
continue|continue;
block|}
name|BasicSqlType
name|nullableFromType
init|=
name|fromType
operator|.
name|createWithNullability
argument_list|(
operator|!
name|fromType
operator|.
name|isNullable
argument_list|)
decl_stmt|;
name|assertCanCast
argument_list|(
name|unknownType
argument_list|,
name|fromType
argument_list|)
expr_stmt|;
name|assertCanCast
argument_list|(
name|unknownType
argument_list|,
name|nullableFromType
argument_list|)
expr_stmt|;
name|assertCanCast
argument_list|(
name|nullableUnknownType
argument_list|,
name|fromType
argument_list|)
expr_stmt|;
name|assertCanCast
argument_list|(
name|nullableUnknownType
argument_list|,
name|nullableFromType
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|assertCanCast
parameter_list|(
name|RelDataType
name|toType
parameter_list|,
name|RelDataType
name|fromType
parameter_list|)
block|{
name|assertThat
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Expected to be able to cast from %s to %s without coercion."
argument_list|,
name|fromType
argument_list|,
name|toType
argument_list|)
argument_list|,
name|SqlTypeUtil
operator|.
name|canCastFrom
argument_list|(
name|toType
argument_list|,
name|fromType
argument_list|,
comment|/* coerce= */
literal|false
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
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Expected to be able to cast from %s to %s with coercion."
argument_list|,
name|fromType
argument_list|,
name|toType
argument_list|)
argument_list|,
name|SqlTypeUtil
operator|.
name|canCastFrom
argument_list|(
name|toType
argument_list|,
name|fromType
argument_list|,
comment|/* coerce= */
literal|true
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

