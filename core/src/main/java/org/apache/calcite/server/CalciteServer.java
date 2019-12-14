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
name|avatica
operator|.
name|Meta
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
name|avatica
operator|.
name|NoSuchStatementException
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
name|jdbc
operator|.
name|CalciteConnection
import|;
end_import

begin_comment
comment|/**  * Server.  *  *<p>Represents shared state among connections, and will have monitoring and  * management facilities.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CalciteServer
block|{
name|void
name|removeStatement
parameter_list|(
name|Meta
operator|.
name|StatementHandle
name|h
parameter_list|)
function_decl|;
name|void
name|addStatement
parameter_list|(
name|CalciteConnection
name|connection
parameter_list|,
name|Meta
operator|.
name|StatementHandle
name|h
parameter_list|)
function_decl|;
comment|/** Returns the statement with a given handle.    *    * @param h Statement handle    * @return Statement, never null    * @throws NoSuchStatementException if handle does not represent a statement    */
name|CalciteServerStatement
name|getStatement
parameter_list|(
name|Meta
operator|.
name|StatementHandle
name|h
parameter_list|)
throws|throws
name|NoSuchStatementException
function_decl|;
block|}
end_interface

end_unit

