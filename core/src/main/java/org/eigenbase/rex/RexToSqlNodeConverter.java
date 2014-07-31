begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rex
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Converts expressions from {@link RexNode} to {@link SqlNode}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RexToSqlNodeConverter
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Converts a {@link RexNode} to a {@link SqlNode} expression,    * typically by dispatching to one of the other interface methods.    *    * @param node RexNode to translate    * @return SqlNode, or null if no translation was available    */
name|SqlNode
name|convertNode
parameter_list|(
name|RexNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Converts a {@link RexCall} to a {@link SqlNode} expression.    *    * @param call RexCall to translate    * @return SqlNode, or null if no translation was available    */
name|SqlNode
name|convertCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
function_decl|;
comment|/**    * Converts a {@link RexLiteral} to a {@link SqlLiteral}.    *    * @param literal RexLiteral to translate    * @return SqlNode, or null if no translation was available    */
name|SqlNode
name|convertLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
function_decl|;
comment|/**    * Converts a {@link RexInputRef} to a {@link SqlIdentifier}.    *    * @param ref RexInputRef to translate    * @return SqlNode, or null if no translation was available    */
name|SqlNode
name|convertInputRef
parameter_list|(
name|RexInputRef
name|ref
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RexToSqlNodeConverter.java
end_comment

end_unit

