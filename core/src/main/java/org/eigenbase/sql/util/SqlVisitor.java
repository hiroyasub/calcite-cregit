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
name|sql
operator|.
name|util
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
comment|/**  * Visitor class, follows the {@link org.eigenbase.util.Glossary#VisitorPattern  * visitor pattern}.  *  *<p>The type parameter<code>R</code> is the return type of each<code>  * visit()</code> method. If the methods do not need to return a value, use  * {@link Void}.  *  * @author jhyde  * @version $Id$  * @see SqlBasicVisitor  * @see SqlNode#accept(SqlVisitor)  * @see SqlOperator#acceptCall  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlVisitor
parameter_list|<
name|R
parameter_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Visits a literal.      *      * @param literal Literal      *      * @see SqlLiteral#accept(SqlVisitor)      */
name|R
name|visit
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
function_decl|;
comment|/**      * Visits a call to a {@link SqlOperator}.      *      * @param call Call      *      * @see SqlCall#accept(SqlVisitor)      */
name|R
name|visit
parameter_list|(
name|SqlCall
name|call
parameter_list|)
function_decl|;
comment|/**      * Visits a list of {@link SqlNode} objects.      *      * @param nodeList list of nodes      *      * @see SqlNodeList#accept(SqlVisitor)      */
name|R
name|visit
parameter_list|(
name|SqlNodeList
name|nodeList
parameter_list|)
function_decl|;
comment|/**      * Visits an identifier.      *      * @param id identifier      *      * @see SqlIdentifier#accept(SqlVisitor)      */
name|R
name|visit
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|)
function_decl|;
comment|/**      * Visits a datatype specification.      *      * @param type datatype specification      *      * @see SqlDataTypeSpec#accept(SqlVisitor)      */
name|R
name|visit
parameter_list|(
name|SqlDataTypeSpec
name|type
parameter_list|)
function_decl|;
comment|/**      * Visits a dynamic parameter.      *      * @param param Dynamic parameter      *      * @see SqlDynamicParam#accept(SqlVisitor)      */
name|R
name|visit
parameter_list|(
name|SqlDynamicParam
name|param
parameter_list|)
function_decl|;
comment|/**      * Visits an interval qualifier      *      * @param intervalQualifier Interval qualifier      *      * @see SqlIntervalQualifier#accept(SqlVisitor)      */
name|R
name|visit
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlVisitor.java
end_comment

end_unit

