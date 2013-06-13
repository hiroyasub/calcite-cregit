begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link RelOptUtil} and other classes in this package.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptUtilTest
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelOptUtilTest
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Test
specifier|public
name|void
name|testTypeDump
parameter_list|()
block|{
name|RelDataTypeFactory
name|typeFactory
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|()
decl_stmt|;
name|RelDataType
name|t1
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
operator|new
name|RelDataType
index|[]
block|{
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|5
argument_list|,
literal|2
argument_list|)
block|,
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|10
argument_list|)
block|,                 }
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"f0"
block|,
literal|"f1"
block|}
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"f0 DECIMAL(5, 2) NOT NULL,"
block|,
literal|"f1 VARCHAR(10) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL"
block|}
argument_list|)
argument_list|,
name|RelOptUtil
operator|.
name|dumpType
argument_list|(
name|t1
argument_list|)
argument_list|)
expr_stmt|;
name|RelDataType
name|t2
init|=
name|typeFactory
operator|.
name|createStructType
argument_list|(
operator|new
name|RelDataType
index|[]
block|{
name|t1
block|,
name|typeFactory
operator|.
name|createMultisetType
argument_list|(
name|t1
argument_list|,
operator|-
literal|1
argument_list|)
block|,                 }
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"f0"
block|,
literal|"f1"
block|}
argument_list|)
decl_stmt|;
name|TestUtil
operator|.
name|assertEqualsVerbose
argument_list|(
name|TestUtil
operator|.
name|fold
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"f0 RECORD ("
block|,
literal|"  f0 DECIMAL(5, 2) NOT NULL,"
block|,
literal|"  f1 VARCHAR(10) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL) NOT NULL,"
block|,
literal|"f1 RECORD ("
block|,
literal|"  f0 DECIMAL(5, 2) NOT NULL,"
block|,
literal|"  f1 VARCHAR(10) CHARACTER SET \"ISO-8859-1\" COLLATE \"ISO-8859-1$en_US$primary\" NOT NULL) NOT NULL MULTISET NOT NULL"
block|}
argument_list|)
argument_list|,
name|RelOptUtil
operator|.
name|dumpType
argument_list|(
name|t2
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * Tests the rules for how we name rules.      */
annotation|@
name|Test
specifier|public
name|void
name|testRuleGuessDescription
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"Bar"
argument_list|,
name|RelOptRule
operator|.
name|guessDescription
argument_list|(
literal|"com.foo.Bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Baz"
argument_list|,
name|RelOptRule
operator|.
name|guessDescription
argument_list|(
literal|"com.flatten.Bar$Baz"
argument_list|)
argument_list|)
expr_stmt|;
comment|// yields "1" (which as an integer is an invalid
try|try
block|{
name|Util
operator|.
name|discard
argument_list|(
name|RelOptRule
operator|.
name|guessDescription
argument_list|(
literal|"com.foo.Bar$1"
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Derived description of rule class com.foo.Bar$1 is an "
operator|+
literal|"integer, not valid. Supply a description manually."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelOptUtilTest.java
end_comment

end_unit

