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
name|parser
operator|.
name|parserextensiontesting
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
name|parser
operator|.
name|SqlParserImplFactory
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
name|SqlParserTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsNull
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

begin_comment
comment|/**  * Testing for extension functionality of the base SQL parser impl.  *  *<p>This test runs all test cases of the base {@link SqlParserTest}, as well  * as verifying specific extension points.  */
end_comment

begin_class
specifier|public
class|class
name|ExtensionSqlParserTest
extends|extends
name|SqlParserTest
block|{
annotation|@
name|Override
specifier|protected
name|SqlParserImplFactory
name|parserImplFactory
parameter_list|()
block|{
return|return
name|ExtensionSqlParserImpl
operator|.
name|FACTORY
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAlterSystemExtension
parameter_list|()
block|{
name|sql
argument_list|(
literal|"alter system upload jar '/path/to/jar'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"ALTER SYSTEM UPLOAD JAR '/path/to/jar'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAlterSystemExtensionWithoutAlter
parameter_list|()
block|{
comment|// We need to include the scope for custom alter operations
name|sql
argument_list|(
literal|"^upload^ jar '/path/to/jar'"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s).*Encountered \"upload\" at .*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"CREATE TABLE foo.baz(i INTEGER, j VARCHAR(10) NOT NULL)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CREATE TABLE `FOO`.`BAZ` (`I` INTEGER, `J` VARCHAR(10) NOT NULL)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtendedSqlStmt
parameter_list|()
block|{
name|sql
argument_list|(
literal|"DESCRIBE SPACE POWER"
argument_list|)
operator|.
name|node
argument_list|(
operator|new
name|IsNull
argument_list|<
name|SqlNode
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"DESCRIBE SEA ^POWER^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"POWER\" at line 1, column 14..*"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

