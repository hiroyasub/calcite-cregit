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
name|jdbc
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
name|sql
operator|.
name|test
operator|.
name|SqlTests
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
name|lang
operator|.
name|reflect
operator|.
name|Type
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
name|linq4j
operator|.
name|tree
operator|.
name|Types
operator|.
name|RecordType
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
name|assertEquals
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

begin_comment
comment|/**  * Test for {@link org.apache.calcite.jdbc.JavaTypeFactoryImpl}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|JavaTypeFactoryTest
block|{
specifier|private
specifier|static
specifier|final
name|JavaTypeFactoryImpl
name|TYPE_FACTORY
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|()
decl_stmt|;
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]    * Struct types with one field are not mapped correctly to Java Classes</a>. */
annotation|@
name|Test
name|void
name|testGetJavaClassWithOneFieldStructDataTypeV1
parameter_list|()
block|{
name|RelDataType
name|structWithOneField
init|=
name|TYPE_FACTORY
operator|.
name|createStructType
argument_list|(
name|OneFieldStruct
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|OneFieldStruct
operator|.
name|class
argument_list|,
name|TYPE_FACTORY
operator|.
name|getJavaClass
argument_list|(
name|structWithOneField
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]    * Struct types with one field are not mapped correctly to Java Classes</a>. */
annotation|@
name|Test
name|void
name|testGetJavaClassWithOneFieldStructDataTypeV2
parameter_list|()
block|{
name|RelDataType
name|structWithOneField
init|=
name|TYPE_FACTORY
operator|.
name|createStructType
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"intField"
argument_list|)
argument_list|)
decl_stmt|;
name|assertRecordType
argument_list|(
name|TYPE_FACTORY
operator|.
name|getJavaClass
argument_list|(
name|structWithOneField
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]    * Struct types with one field are not mapped correctly to Java Classes</a>. */
annotation|@
name|Test
name|void
name|testGetJavaClassWithTwoFieldsStructDataType
parameter_list|()
block|{
name|RelDataType
name|structWithTwoFields
init|=
name|TYPE_FACTORY
operator|.
name|createStructType
argument_list|(
name|TwoFieldStruct
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TwoFieldStruct
operator|.
name|class
argument_list|,
name|TYPE_FACTORY
operator|.
name|getJavaClass
argument_list|(
name|structWithTwoFields
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-2677">[CALCITE-2677]    * Struct types with one field are not mapped correctly to Java Classes</a>. */
annotation|@
name|Test
name|void
name|testGetJavaClassWithTwoFieldsStructDataTypeV2
parameter_list|()
block|{
name|RelDataType
name|structWithTwoFields
init|=
name|TYPE_FACTORY
operator|.
name|createStructType
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
argument_list|,
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"intField"
argument_list|,
literal|"strField"
argument_list|)
argument_list|)
decl_stmt|;
name|assertRecordType
argument_list|(
name|TYPE_FACTORY
operator|.
name|getJavaClass
argument_list|(
name|structWithTwoFields
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3029">[CALCITE-3029]    * Java-oriented field type is wrongly forced to be NOT NULL after being converted to    * SQL-oriented</a>. */
annotation|@
name|Test
name|void
name|testFieldNullabilityAfterConvertingToSqlStructType
parameter_list|()
block|{
name|RelDataType
name|javaStructType
init|=
name|TYPE_FACTORY
operator|.
name|createStructType
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|TYPE_FACTORY
operator|.
name|createJavaType
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|,
name|TYPE_FACTORY
operator|.
name|createJavaType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
literal|"a"
argument_list|,
literal|"b"
argument_list|)
argument_list|)
decl_stmt|;
name|RelDataType
name|sqlStructType
init|=
name|TYPE_FACTORY
operator|.
name|toSql
argument_list|(
name|javaStructType
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"RecordType(INTEGER a, INTEGER NOT NULL b) NOT NULL"
argument_list|,
name|SqlTests
operator|.
name|getTypeString
argument_list|(
name|sqlStructType
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertRecordType
parameter_list|(
name|Type
name|actual
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|actual
operator|instanceof
name|RecordType
argument_list|,
parameter_list|()
lambda|->
literal|"Type {"
operator|+
name|actual
operator|.
name|getTypeName
argument_list|()
operator|+
literal|"} is not a subtype of Types.RecordType"
argument_list|)
expr_stmt|;
block|}
comment|/** Struct with one field. */
specifier|private
specifier|static
class|class
name|OneFieldStruct
block|{
specifier|public
name|Integer
name|intField
decl_stmt|;
block|}
comment|/** Struct with two fields. */
specifier|private
specifier|static
class|class
name|TwoFieldStruct
block|{
specifier|public
name|Integer
name|intField
decl_stmt|;
specifier|public
name|String
name|strField
decl_stmt|;
block|}
block|}
end_class

end_unit

