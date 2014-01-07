begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql2rel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Contains the context necessary for a {@link SqlRexConvertlet} to convert a  * {@link SqlNode} expression into a {@link RexNode}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlRexContext
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Converts an expression from {@link SqlNode} to {@link RexNode} format.    *    * @param expr Expression to translate    * @return Converted expression    */
name|RexNode
name|convertExpression
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
function_decl|;
comment|/**    * Returns the {@link RexBuilder} to use to create {@link RexNode} objects.    */
name|RexBuilder
name|getRexBuilder
parameter_list|()
function_decl|;
comment|/**    * Returns the expression used to access a given IN or EXISTS {@link    * SqlSelect sub-query}.    *    * @param call IN or EXISTS expression    * @return Expression used to access current row of sub-query    */
name|RexRangeRef
name|getSubqueryExpr
parameter_list|(
name|SqlCall
name|call
parameter_list|)
function_decl|;
comment|/**    * Returns the type factory.    */
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
comment|/**    * Returns the factory which supplies default values for INSERT, UPDATE, and    * NEW.    */
name|DefaultValueFactory
name|getDefaultValueFactory
parameter_list|()
function_decl|;
comment|/**    * Returns the validator.    */
name|SqlValidator
name|getValidator
parameter_list|()
function_decl|;
comment|/**    * Converts a literal.    */
name|RexNode
name|convertLiteral
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlRexContext.java
end_comment

end_unit

