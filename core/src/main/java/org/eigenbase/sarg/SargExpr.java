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
name|sarg
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

begin_comment
comment|/**  * SargExpr represents an expression defining a possibly non-contiguous search  * subset of a scalar domain of a given datatype.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SargExpr
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Overrides the default Object.toString. The result must be safe for use in    * a RelNode digest.    */
name|String
name|toString
parameter_list|()
function_decl|;
comment|/**    * @return datatype for coordinates of search domain    */
name|RelDataType
name|getDataType
parameter_list|()
function_decl|;
comment|/**    * Resolves this expression into a fixed {@link SargIntervalSequence}.    *    *<p>TODO jvs 17-Jan-2006: add binding for dynamic params so they can be    * evaluated as well    *    * @return immutable ordered sequence of disjoint intervals    */
name|SargIntervalSequence
name|evaluate
parameter_list|()
function_decl|;
comment|/**    * Resolves the complement of this expression into a fixed {@link    * SargIntervalSequence}.    *    * @return immutable ordered sequence of disjoint intervals    */
name|SargIntervalSequence
name|evaluateComplemented
parameter_list|()
function_decl|;
comment|/**    * @return the factory which produced this expression    */
name|SargFactory
name|getFactory
parameter_list|()
function_decl|;
comment|/**    * Collects all dynamic parameters referenced by this expression.    *    * @param dynamicParams receives dynamic parameter references    */
name|void
name|collectDynamicParams
parameter_list|(
name|Set
argument_list|<
name|RexDynamicParam
argument_list|>
name|dynamicParams
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SargExpr.java
end_comment

end_unit

