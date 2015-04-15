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
name|jdbc
operator|.
name|CalciteConnection
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
name|CalcitePrepare
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Statement within a Calcite server.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CalciteServerStatement
block|{
comment|/** Creates a context for preparing a statement for execution. */
name|CalcitePrepare
operator|.
name|Context
name|createPrepareContext
parameter_list|()
function_decl|;
comment|/** Returns the connection. */
name|CalciteConnection
name|getConnection
parameter_list|()
function_decl|;
name|void
name|setSignature
parameter_list|(
name|Meta
operator|.
name|Signature
name|signature
parameter_list|)
function_decl|;
name|Meta
operator|.
name|Signature
name|getSignature
parameter_list|()
function_decl|;
name|Iterator
argument_list|<
name|Object
argument_list|>
name|getResultSet
parameter_list|()
function_decl|;
name|void
name|setResultSet
parameter_list|(
name|Iterator
argument_list|<
name|Object
argument_list|>
name|resultSet
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End CalciteServerStatement.java
end_comment

end_unit

