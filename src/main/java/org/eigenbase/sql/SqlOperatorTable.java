begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * SqlOperatorTable defines a directory interface for enumerating and looking up  * SQL operators and functions.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlOperatorTable
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Retrieves a list of operators with a given name and syntax. For example,      * by passing SqlSyntax.Function, the returned list is narrowed to only      * matching SqlFunction objects.      *      * @param opName name of operator      * @param category function category to look up, or null for any matching      * operator      * @param syntax syntax type of operator      *      * @return mutable list of SqlOperator objects (or immutable empty list if      * no matches)      */
specifier|public
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|lookupOperatorOverloads
parameter_list|(
name|SqlIdentifier
name|opName
parameter_list|,
name|SqlFunctionCategory
name|category
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|)
function_decl|;
comment|/**      * Retrieves a list of all functions and operators in this table. Used for      * automated testing.      *      * @return list of SqlOperator objects      */
specifier|public
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|getOperatorList
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlOperatorTable.java
end_comment

end_unit

