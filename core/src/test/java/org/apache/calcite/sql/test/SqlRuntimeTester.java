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
name|SqlParserUtil
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
name|validate
operator|.
name|SqlValidator
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
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * Tester of {@link SqlValidator} and runtime execution of the input SQL.  */
end_comment

begin_class
specifier|public
class|class
name|SqlRuntimeTester
extends|extends
name|AbstractSqlTester
block|{
specifier|public
name|SqlRuntimeTester
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|SqlTester
name|with
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|)
block|{
return|return
operator|new
name|SqlRuntimeTester
argument_list|(
name|factory
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkFails
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedError
parameter_list|,
name|boolean
name|runtime
parameter_list|)
block|{
specifier|final
name|String
name|sql
init|=
name|runtime
condition|?
name|buildQuery2
argument_list|(
name|expression
argument_list|)
else|:
name|buildQuery
argument_list|(
name|expression
argument_list|)
decl_stmt|;
name|assertExceptionIsThrown
argument_list|(
name|sql
argument_list|,
name|expectedError
argument_list|,
name|runtime
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertExceptionIsThrown
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
name|assertExceptionIsThrown
argument_list|(
name|sql
argument_list|,
name|expectedMsgPattern
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|assertExceptionIsThrown
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|,
name|boolean
name|runtime
parameter_list|)
block|{
specifier|final
name|SqlNode
name|sqlNode
decl_stmt|;
specifier|final
name|SqlParserUtil
operator|.
name|StringAndPos
name|sap
init|=
name|SqlParserUtil
operator|.
name|findPos
argument_list|(
name|sql
argument_list|)
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|parseQuery
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|checkParseEx
argument_list|(
name|e
argument_list|,
name|expectedMsgPattern
argument_list|,
name|sap
operator|.
name|sql
argument_list|)
expr_stmt|;
return|return;
block|}
name|Throwable
name|thrown
init|=
literal|null
decl_stmt|;
specifier|final
name|SqlTests
operator|.
name|Stage
name|stage
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|getValidator
argument_list|()
decl_stmt|;
if|if
condition|(
name|runtime
condition|)
block|{
name|stage
operator|=
name|SqlTests
operator|.
name|Stage
operator|.
name|RUNTIME
expr_stmt|;
name|SqlNode
name|validated
init|=
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|validated
argument_list|)
expr_stmt|;
try|try
block|{
name|check
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|SqlTests
operator|.
name|ANY_TYPE_CHECKER
argument_list|,
name|SqlTests
operator|.
name|ANY_PARAMETER_CHECKER
argument_list|,
name|SqlTests
operator|.
name|ANY_RESULT_CHECKER
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
comment|// get the real exception in runtime check
name|thrown
operator|=
name|ex
expr_stmt|;
block|}
block|}
else|else
block|{
name|stage
operator|=
name|SqlTests
operator|.
name|Stage
operator|.
name|VALIDATE
expr_stmt|;
try|try
block|{
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|thrown
operator|=
name|ex
expr_stmt|;
block|}
block|}
name|SqlTests
operator|.
name|checkEx
argument_list|(
name|thrown
argument_list|,
name|expectedMsgPattern
argument_list|,
name|sap
argument_list|,
name|stage
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlRuntimeTester.java
end_comment

end_unit

