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
name|avatica
operator|.
name|remote
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
name|avatica
operator|.
name|proto
operator|.
name|Requests
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * An extension of {@link Meta} which allows for native processing of calls with the Protobuf  * API objects instead of the POJOS (to avoid object translation). In the write-path, performing  * this conversion tends to represent a signficant portion of execution time. The introduction  * of this interface is to serve the purose of gradual migration to Meta implementations that  * can naturally function over Protobuf objects instead of the POJOs.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ProtobufMeta
extends|extends
name|Meta
block|{
comment|/**    * Executes a batch of commands on a prepared statement.    *    * @param h Statement handle    * @param parameterValues A collection of list of typed values, one list per batch    * @return An array of update counts containing one element for each command in the batch.    */
name|ExecuteBatchResult
name|executeBatchProtobuf
parameter_list|(
name|StatementHandle
name|h
parameter_list|,
name|List
argument_list|<
name|Requests
operator|.
name|UpdateBatch
argument_list|>
name|parameterValues
parameter_list|)
throws|throws
name|NoSuchStatementException
function_decl|;
block|}
end_interface

begin_comment
comment|// End ProtobufMeta.java
end_comment

end_unit

