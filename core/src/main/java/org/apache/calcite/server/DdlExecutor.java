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
name|server
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
name|jdbc
operator|.
name|CalcitePrepare
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
name|SqlNode
import|;
end_import

begin_comment
comment|/**  * Executes DDL commands.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DdlExecutor
block|{
comment|/** DDL executor that cannot handle any DDL. */
name|DdlExecutor
name|USELESS
init|=
parameter_list|(
name|context
parameter_list|,
name|node
parameter_list|)
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"DDL not supported: "
operator|+
name|node
argument_list|)
throw|;
block|}
decl_stmt|;
comment|/** Executes a DDL statement.    *    *<p>The statement identified itself as DDL in the    * {@link org.apache.calcite.jdbc.CalcitePrepare.ParseResult#kind} field. */
name|void
name|executeDdl
parameter_list|(
name|CalcitePrepare
operator|.
name|Context
name|context
parameter_list|,
name|SqlNode
name|node
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

