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
name|plan
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
comment|/**  * Node in a planner.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptNode
block|{
comment|/**    * Returns the ID of this relational expression, unique among all relational    * expressions created since the server was started.    *    * @return Unique ID    */
name|int
name|getId
parameter_list|()
function_decl|;
comment|/**    * Returns a string which concisely describes the definition of this    * relational expression. Two relational expressions are equivalent if and    * only if their digests are the same.    *    *<p>The digest does not contain the relational expression's identity --    * that would prevent similar relational expressions from ever comparing    * equal -- but does include the identity of children (on the assumption    * that children have already been normalized).    *    *<p>If you want a descriptive string which contains the identity, call    * {@link Object#toString()}, which always returns "rel#{id}:{digest}".    *    * @return Digest of this {@code RelNode}    */
name|String
name|getDigest
parameter_list|()
function_decl|;
comment|/**    * Retrieves this RelNode's traits. Note that although the RelTraitSet    * returned is modifiable, it<b>must not</b> be modified during    * optimization. It is legal to modify the traits of a RelNode before or    * after optimization, although doing so could render a tree of RelNodes    * unimplementable. If a RelNode's traits need to be modified during    * optimization, clone the RelNode and change the clone's traits.    *    * @return this RelNode's trait set    */
name|RelTraitSet
name|getTraitSet
parameter_list|()
function_decl|;
comment|// TODO: We don't want to require that nodes have very detailed row type. It
comment|// may not even be known at planning time.
name|RelDataType
name|getRowType
parameter_list|()
function_decl|;
comment|/**    * Returns a string which describes the relational expression and, unlike    * {@link #getDigest()}, also includes the identity. Typically returns    * "rel#{id}:{digest}".    *    * @return String which describes the relational expression and, unlike    *   {@link #getDigest()}, also includes the identity    */
name|String
name|getDescription
parameter_list|()
function_decl|;
comment|/**    * Returns an array of this relational expression's inputs. If there are no    * inputs, returns an empty list, not {@code null}.    *    * @return Array of this relational expression's inputs    */
name|List
argument_list|<
name|?
extends|extends
name|RelOptNode
argument_list|>
name|getInputs
parameter_list|()
function_decl|;
comment|/**    * Returns the cluster this relational expression belongs to.    *    * @return cluster    */
name|RelOptCluster
name|getCluster
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelOptNode.java
end_comment

end_unit

