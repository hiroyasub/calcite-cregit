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
name|sql
operator|.
name|type
operator|.
name|ExtraSqlTypes
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
name|Types
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests types supported by {@link SqlTypeName}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlTypeNameTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testBit
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|BIT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"BIT did not map to BOOLEAN"
argument_list|,
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTinyint
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|TINYINT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"TINYINT did not map to TINYINT"
argument_list|,
name|SqlTypeName
operator|.
name|TINYINT
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSmallint
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|SMALLINT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"SMALLINT did not map to SMALLINT"
argument_list|,
name|SqlTypeName
operator|.
name|SMALLINT
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInteger
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"INTEGER did not map to INTEGER"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBigint
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|BIGINT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"BIGINT did not map to BIGINT"
argument_list|,
name|SqlTypeName
operator|.
name|BIGINT
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFloat
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|FLOAT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"FLOAT did not map to FLOAT"
argument_list|,
name|SqlTypeName
operator|.
name|FLOAT
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReal
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|REAL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"REAL did not map to REAL"
argument_list|,
name|SqlTypeName
operator|.
name|REAL
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDouble
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DOUBLE did not map to DOUBLE"
argument_list|,
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNumeric
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|NUMERIC
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"NUMERIC did not map to DECIMAL"
argument_list|,
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDecimal
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|DECIMAL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DECIMAL did not map to DECIMAL"
argument_list|,
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChar
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|CHAR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"CHAR did not map to CHAR"
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVarchar
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|VARCHAR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"VARCHAR did not map to VARCHAR"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLongvarchar
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|LONGVARCHAR
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"LONGVARCHAR did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDate
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DATE did not map to DATE"
argument_list|,
name|SqlTypeName
operator|.
name|DATE
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTime
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|TIME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"TIME did not map to TIME"
argument_list|,
name|SqlTypeName
operator|.
name|TIME
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTimestamp
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|TIMESTAMP
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"TIMESTAMP did not map to TIMESTAMP"
argument_list|,
name|SqlTypeName
operator|.
name|TIMESTAMP
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinary
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|BINARY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"BINARY did not map to BINARY"
argument_list|,
name|SqlTypeName
operator|.
name|BINARY
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testVarbinary
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|VARBINARY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"VARBINARY did not map to VARBINARY"
argument_list|,
name|SqlTypeName
operator|.
name|VARBINARY
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLongvarbinary
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|LONGVARBINARY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"LONGVARBINARY did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNull
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|NULL
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"NULL did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOther
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|OTHER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"OTHER did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJavaobject
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|JAVA_OBJECT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"JAVA_OBJECT did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDistinct
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|DISTINCT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DISTINCT did not map to DISTINCT"
argument_list|,
name|SqlTypeName
operator|.
name|DISTINCT
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStruct
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|STRUCT
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"STRUCT did not map to null"
argument_list|,
name|SqlTypeName
operator|.
name|STRUCTURED
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArray
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|ARRAY
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"ARRAY did not map to ARRAY"
argument_list|,
name|SqlTypeName
operator|.
name|ARRAY
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBlob
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|BLOB
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"BLOB did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClob
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|CLOB
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"CLOB did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRef
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|REF
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"REF did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDatalink
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|DATALINK
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DATALINK did not map to null"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBoolean
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|Types
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"BOOLEAN did not map to BOOLEAN"
argument_list|,
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRowid
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|ExtraSqlTypes
operator|.
name|ROWID
argument_list|)
decl_stmt|;
comment|// ROWID not supported yet
name|assertEquals
argument_list|(
literal|"ROWID maps to non-null type"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNchar
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|ExtraSqlTypes
operator|.
name|NCHAR
argument_list|)
decl_stmt|;
comment|// NCHAR not supported yet, currently maps to CHAR
name|assertEquals
argument_list|(
literal|"NCHAR did not map to CHAR"
argument_list|,
name|SqlTypeName
operator|.
name|CHAR
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNvarchar
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|ExtraSqlTypes
operator|.
name|NVARCHAR
argument_list|)
decl_stmt|;
comment|// NVARCHAR not supported yet, currently maps to VARCHAR
name|assertEquals
argument_list|(
literal|"NVARCHAR did not map to VARCHAR"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLongnvarchar
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|ExtraSqlTypes
operator|.
name|LONGNVARCHAR
argument_list|)
decl_stmt|;
comment|// LONGNVARCHAR not supported yet
name|assertEquals
argument_list|(
literal|"LONGNVARCHAR maps to non-null type"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNclob
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|ExtraSqlTypes
operator|.
name|NCLOB
argument_list|)
decl_stmt|;
comment|// NCLOB not supported yet
name|assertEquals
argument_list|(
literal|"NCLOB maps to non-null type"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlxml
parameter_list|()
block|{
name|SqlTypeName
name|tn
init|=
name|SqlTypeName
operator|.
name|getNameForJdbcType
argument_list|(
name|ExtraSqlTypes
operator|.
name|SQLXML
argument_list|)
decl_stmt|;
comment|// SQLXML not supported yet
name|assertEquals
argument_list|(
literal|"SQLXML maps to non-null type"
argument_list|,
literal|null
argument_list|,
name|tn
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlTypeNameTest.java
end_comment

end_unit

