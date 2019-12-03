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
name|prepare
operator|.
name|Prepare
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
name|util
operator|.
name|TryThreadLocal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Test that runs every Quidem file in the "core" module as a test.  */
end_comment

begin_class
specifier|public
class|class
name|CoreQuidemTest
extends|extends
name|QuidemTest
block|{
comment|/** Runs a test from the command line.    *    *<p>For example:    *    *<blockquote>    *<code>java CoreQuidemTest sql/dummy.iq</code>    *</blockquote> */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
operator|new
name|CoreQuidemTest
argument_list|()
operator|.
name|test
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** For {@link QuidemTest#test(String)} parameters. */
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
comment|// Start with a test file we know exists, then find the directory and list
comment|// its files.
specifier|final
name|String
name|first
init|=
literal|"sql/agg.iq"
decl_stmt|;
return|return
name|data
argument_list|(
name|first
argument_list|)
return|;
block|}
comment|/** Override settings for "sql/misc.iq". */
specifier|public
name|void
name|testSqlMisc
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|CalciteAssert
operator|.
name|DB
condition|)
block|{
case|case
name|ORACLE
case|:
comment|// There are formatting differences (e.g. "4.000" vs "4") when using
comment|// Oracle as the JDBC data source.
return|return;
block|}
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_EXPAND
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|checkRun
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Override settings for "sql/scalar.iq". */
specifier|public
name|void
name|testSqlScalar
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_EXPAND
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|checkRun
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Runs the dummy script "sql/dummy.iq", which is checked in empty but    * which you may use as scratch space during development. */
comment|// Do not disable this test; just remember not to commit changes to dummy.iq
specifier|public
name|void
name|testSqlDummy
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
name|TryThreadLocal
operator|.
name|Memo
name|ignored
init|=
name|Prepare
operator|.
name|THREAD_EXPAND
operator|.
name|push
argument_list|(
literal|true
argument_list|)
init|)
block|{
name|checkRun
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

