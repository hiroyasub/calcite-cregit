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
name|RelDataTypeSystem
import|;
end_import

begin_comment
comment|/**  * Reusable {@link RelDataType} fixtures for tests.  */
end_comment

begin_class
class|class
name|SqlTypeFixture
block|{
name|SqlTypeFactoryImpl
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlBoolean
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlBigInt
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlBigIntNullable
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlInt
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlDate
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DATE
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlVarchar
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlChar
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|CHAR
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlVarcharNullable
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlNull
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|NULL
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlAny
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|ANY
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|sqlFloat
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|FLOAT
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|arrayFloat
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|sqlFloat
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|arrayBigInt
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|sqlBigIntNullable
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|multisetFloat
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|sqlFloat
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|multisetBigInt
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|sqlBigIntNullable
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|arrayBigIntNullable
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|sqlBigIntNullable
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|arrayOfArrayBigInt
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|arrayBigInt
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|arrayOfArrayFloat
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|arrayFloat
argument_list|,
operator|-
literal|1
argument_list|)
argument_list|,
literal|false
argument_list|)
decl_stmt|;
block|}
end_class

begin_comment
comment|// End SqlTypeFixture.java
end_comment

end_unit

