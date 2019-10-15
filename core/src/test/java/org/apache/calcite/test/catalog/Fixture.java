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
operator|.
name|catalog
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
name|RelDataTypeComparability
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
name|StructKind
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
name|parser
operator|.
name|SqlParserPos
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
name|ObjectSqlType
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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/** Types used during initialization. */
end_comment

begin_class
specifier|final
class|class
name|Fixture
extends|extends
name|AbstractFixture
block|{
specifier|final
name|RelDataType
name|intType
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intTypeNull
init|=
name|nullable
argument_list|(
name|intType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|bigintType
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|decimalType
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varcharType
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varcharTypeNull
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar5Type
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|5
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar10Type
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|10
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar10TypeNull
init|=
name|nullable
argument_list|(
name|varchar10Type
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar20Type
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|20
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar20TypeNull
init|=
name|nullable
argument_list|(
name|varchar20Type
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|timestampType
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|timestampTypeNull
init|=
name|nullable
argument_list|(
name|timestampType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|dateType
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|booleanType
init|=
name|sqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|booleanTypeNull
init|=
name|nullable
argument_list|(
name|booleanType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rectilinearCoordType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"X"
argument_list|,
name|intType
argument_list|)
operator|.
name|add
argument_list|(
literal|"Y"
argument_list|,
name|intType
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rectilinearPeekCoordType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"X"
argument_list|,
name|intType
argument_list|)
operator|.
name|add
argument_list|(
literal|"Y"
argument_list|,
name|intType
argument_list|)
operator|.
name|add
argument_list|(
literal|"unit"
argument_list|,
name|varchar20Type
argument_list|)
operator|.
name|kind
argument_list|(
name|StructKind
operator|.
name|PEEK_FIELDS
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rectilinearPeekNoExpandCoordType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"M"
argument_list|,
name|intType
argument_list|)
operator|.
name|add
argument_list|(
literal|"SUB"
argument_list|,
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"A"
argument_list|,
name|intType
argument_list|)
operator|.
name|add
argument_list|(
literal|"B"
argument_list|,
name|intType
argument_list|)
operator|.
name|kind
argument_list|(
name|StructKind
operator|.
name|PEEK_FIELDS_NO_EXPAND
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|kind
argument_list|(
name|StructKind
operator|.
name|PEEK_FIELDS_NO_EXPAND
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|abRecordType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"A"
argument_list|,
name|varchar10Type
argument_list|)
operator|.
name|add
argument_list|(
literal|"B"
argument_list|,
name|varchar10Type
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
empty_stmt|;
specifier|final
name|RelDataType
name|skillRecordType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"TYPE"
argument_list|,
name|varchar10Type
argument_list|)
operator|.
name|add
argument_list|(
literal|"DESC"
argument_list|,
name|varchar20Type
argument_list|)
operator|.
name|add
argument_list|(
literal|"OTHERS"
argument_list|,
name|abRecordType
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|empRecordType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"EMPNO"
argument_list|,
name|intType
argument_list|)
operator|.
name|add
argument_list|(
literal|"ENAME"
argument_list|,
name|varchar10Type
argument_list|)
operator|.
name|add
argument_list|(
literal|"DETAIL"
argument_list|,
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"SKILLS"
argument_list|,
name|array
argument_list|(
name|skillRecordType
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|empListType
init|=
name|array
argument_list|(
name|empRecordType
argument_list|)
decl_stmt|;
specifier|final
name|ObjectSqlType
name|addressType
init|=
operator|new
name|ObjectSqlType
argument_list|(
name|SqlTypeName
operator|.
name|STRUCTURED
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
literal|"ADDRESS"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
literal|false
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"STREET"
argument_list|,
literal|0
argument_list|,
name|varchar20Type
argument_list|)
argument_list|,
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"CITY"
argument_list|,
literal|1
argument_list|,
name|varchar20Type
argument_list|)
argument_list|,
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"ZIP"
argument_list|,
literal|2
argument_list|,
name|intType
argument_list|)
argument_list|,
operator|new
name|RelDataTypeFieldImpl
argument_list|(
literal|"STATE"
argument_list|,
literal|3
argument_list|,
name|varchar20Type
argument_list|)
argument_list|)
argument_list|,
name|RelDataTypeComparability
operator|.
name|NONE
argument_list|)
decl_stmt|;
comment|// Row(f0 int, f1 varchar)
specifier|final
name|RelDataType
name|recordType1
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|intType
argument_list|,
name|varcharType
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"f1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Row(f0 int not null, f1 varchar null)
specifier|final
name|RelDataType
name|recordType2
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|intType
argument_list|,
name|nullable
argument_list|(
name|varcharType
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"f1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Row(f0 Row(ff0 int not null, ff1 varchar null) null, f1 timestamp not null)
specifier|final
name|RelDataType
name|recordType3
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|nullable
argument_list|(
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|intType
argument_list|,
name|varcharTypeNull
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"ff0"
argument_list|,
literal|"ff1"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|timestampType
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"f1"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Row(f0 bigint not null, f1 decimal null) array
specifier|final
name|RelDataType
name|recordType4
init|=
name|array
argument_list|(
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|bigintType
argument_list|,
name|nullable
argument_list|(
name|decimalType
argument_list|)
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"f1"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// Row(f0 varchar not null, f1 timestamp null) multiset
specifier|final
name|RelDataType
name|recordType5
init|=
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|varcharType
argument_list|,
name|timestampTypeNull
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"f1"
argument_list|)
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intArrayType
init|=
name|array
argument_list|(
name|intType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar5ArrayType
init|=
name|array
argument_list|(
name|varchar5Type
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intArrayArrayType
init|=
name|array
argument_list|(
name|intArrayType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar5ArrayArrayType
init|=
name|array
argument_list|(
name|varchar5ArrayType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intMultisetType
init|=
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|intType
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar5MultisetType
init|=
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|varchar5Type
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intMultisetArrayType
init|=
name|array
argument_list|(
name|intMultisetType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varchar5MultisetArrayType
init|=
name|array
argument_list|(
name|varchar5MultisetType
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|intArrayMultisetType
init|=
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|intArrayType
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
comment|// Row(f0 int array multiset, f1 varchar(5) array) array multiset
specifier|final
name|RelDataType
name|rowArrayMultisetType
init|=
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|array
argument_list|(
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|intArrayMultisetType
argument_list|,
name|varchar5ArrayType
argument_list|)
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|"f0"
argument_list|,
literal|"f1"
argument_list|)
argument_list|)
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Fixture
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RelDataType
name|nullable
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|sqlType
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|,
name|int
modifier|...
name|args
parameter_list|)
block|{
assert|assert
name|args
operator|.
name|length
operator|<
literal|3
operator|:
literal|"unknown size of additional int args"
assert|;
return|return
name|args
operator|.
name|length
operator|==
literal|2
condition|?
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
else|:
name|args
operator|.
name|length
operator|==
literal|1
condition|?
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|)
else|:
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|typeName
argument_list|)
return|;
block|}
specifier|private
name|RelDataType
name|array
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|type
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|/**  * Just a little trick to store factory ref before field init in fixture  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractFixture
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
name|AbstractFixture
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End Fixture.java
end_comment

end_unit

