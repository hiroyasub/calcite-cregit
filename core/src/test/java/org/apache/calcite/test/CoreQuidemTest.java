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
name|config
operator|.
name|CalciteConnectionProperty
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|quidem
operator|.
name|Quidem
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
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
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getPath
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
annotation|@
name|Override
specifier|protected
name|Quidem
operator|.
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|QuidemConnectionFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|name
condition|)
block|{
case|case
literal|"blank"
case|:
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|PARSER_FACTORY
argument_list|,
name|ExtensionDdlExecutor
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#PARSER_FACTORY"
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|BLANK
argument_list|)
operator|.
name|connect
argument_list|()
return|;
case|case
literal|"scott"
case|:
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|PARSER_FACTORY
argument_list|,
name|ExtensionDdlExecutor
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#PARSER_FACTORY"
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|SCOTT
argument_list|)
operator|.
name|connect
argument_list|()
return|;
default|default:
return|return
name|super
operator|.
name|connect
argument_list|(
name|name
argument_list|,
name|reference
argument_list|)
return|;
block|}
block|}
block|}
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
name|checkRun
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

