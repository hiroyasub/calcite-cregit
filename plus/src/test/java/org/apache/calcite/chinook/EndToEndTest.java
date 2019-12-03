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
name|chinook
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
name|test
operator|.
name|QuidemTest
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
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Entry point for all end-to-end tests based on Chinook data in HSQLDB wrapped  * by Calcite schema.  */
end_comment

begin_class
specifier|public
class|class
name|EndToEndTest
extends|extends
name|QuidemTest
block|{
comment|/** Runs a test from the command line.    *    *<p>For example:    *    *<blockquote>    *<code>java EndToEndTest sql/basic.iq</code>    *</blockquote> */
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
name|EndToEndTest
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
literal|"sql/basic.iq"
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
name|ConnectionFactory
argument_list|()
return|;
block|}
block|}
end_class

end_unit

