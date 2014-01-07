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
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|SqlExplainLevel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Pair
import|;
end_import

begin_comment
comment|/**  * Callback for an expression to dump itself to.  *  *<p>It is used for generating EXPLAIN PLAN output, and also for serializing  * a tree of relational expressions to JSON.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelWriter
block|{
comment|/**    * Prints an explanation of a node, with a list of (term, value) pairs.    *    *<p>The term-value pairs are generally gathered by calling    * {@link RelNode#explain(RelWriter)}. Each sub-class of    * {@link RelNode} calls {@link #input(String, org.eigenbase.rel.RelNode)}    * and {@link #item(String, Object)} to declare term-value pairs.</p>    *    * @param rel       Relational expression    * @param valueList List of term-value pairs    */
name|void
name|explain
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|valueList
parameter_list|)
function_decl|;
comment|/**    * @return detail level at which plan should be generated    */
name|SqlExplainLevel
name|getDetailLevel
parameter_list|()
function_decl|;
comment|/**    * Adds an input to the explanation of the current node.    *    * @param term  Term for input, e.g. "left" or "input #1".    * @param input Input relational expression    */
name|RelWriter
name|input
parameter_list|(
name|String
name|term
parameter_list|,
name|RelNode
name|input
parameter_list|)
function_decl|;
comment|/**    * Adds an attribute to the explanation of the current node.    *    * @param term  Term for attribute, e.g. "joinType"    * @param value Attribute value    */
name|RelWriter
name|item
parameter_list|(
name|String
name|term
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**    * Adds an input to the explanation of the current node, if a condition    * holds.    */
name|RelWriter
name|itemIf
parameter_list|(
name|String
name|term
parameter_list|,
name|Object
name|value
parameter_list|,
name|boolean
name|condition
parameter_list|)
function_decl|;
comment|/**    * Writes the completed explanation.    */
name|RelWriter
name|done
parameter_list|(
name|RelNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Returns whether the writer prefers nested values. Traditional explain    * writers prefer flattened values.    */
name|boolean
name|nest
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelWriter.java
end_comment

end_unit

