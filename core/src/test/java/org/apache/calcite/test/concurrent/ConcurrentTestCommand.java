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
name|concurrent
package|;
end_package

begin_comment
comment|/**  * ConcurrentTestCommand represents a command, sequentially executed by  * {@link ConcurrentTestCommandExecutor}, during a concurrency test  *  *<p>ConcurrentTestCommand instances are normally instantiated by the  * {@link ConcurrentTestCommandGenerator} class.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConcurrentTestCommand
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Executes this command. The ConcurrentTestCommandExecutor provides    * access to a JDBC connection and previously prepared statements.    *    * @param exec the ConcurrentTestCommandExecutor firing this command.    *    * @throws Exception to indicate a test failure    *    * @see ConcurrentTestCommandExecutor#getStatement()    * @see ConcurrentTestCommandExecutor#setStatement(java.sql.Statement)    */
name|void
name|execute
parameter_list|(
name|ConcurrentTestCommandExecutor
name|exec
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Marks a command to show that it is expected to fail, and indicates how.    * Used for negative tests. Normally when a command fails the embracing test    * fails.    * But when a marked command fails, the error is caught and inspected: if it    * matches the expected error, the test continues. However if it does not    * match, if another kind of exception is thrown, or if no exception is    * caught, then the test fails. Assumes the error is indicated by a    * java.sql.SQLException. Optionally checks for the expected error condition    * by matching the error message against a regular expression. (Scans the    * list of chained SQLExceptions).    *    * @param comment a brief description of the expected error    * @param pattern null, or a regular expression that matches the expected    * error message.    */
name|ConcurrentTestCommand
name|markToFail
parameter_list|(
name|String
name|comment
parameter_list|,
name|String
name|pattern
parameter_list|)
function_decl|;
comment|/**    * Returns true if the command should fail. This allows special error    * handling for expected failures that don't have patterns.    *    * @return true if command is expected to fail    */
name|boolean
name|isFailureExpected
parameter_list|()
function_decl|;
comment|/**    * Set this command to expect a patternless failure.    */
name|ConcurrentTestCommand
name|markToFail
parameter_list|()
function_decl|;
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Indicates that a command should have failed, but instead succeeded, which    * is a test error.    */
class|class
name|ShouldHaveFailedException
extends|extends
name|RuntimeException
block|{
specifier|private
specifier|final
name|String
name|description
decl_stmt|;
specifier|public
name|ShouldHaveFailedException
parameter_list|(
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|description
return|;
block|}
block|}
block|}
end_interface

end_unit

