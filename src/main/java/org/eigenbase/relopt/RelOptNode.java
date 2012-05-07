begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_comment
comment|// Licensed to Julian Hyde under one or more contributor license
end_comment

begin_comment
comment|// agreements. See the NOTICE file distributed with this work for
end_comment

begin_comment
comment|// additional information regarding copyright ownership.
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Julian Hyde licenses this file to you under the Apache License,
end_comment

begin_comment
comment|// Version 2.0 (the "License"); you may not use this file except in
end_comment

begin_comment
comment|// compliance with the License. You may obtain a copy of the License at:
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// http://www.apache.org/licenses/LICENSE-2.0
end_comment

begin_comment
comment|//
end_comment

begin_comment
comment|// Unless required by applicable law or agreed to in writing, software
end_comment

begin_comment
comment|// distributed under the License is distributed on an "AS IS" BASIS,
end_comment

begin_comment
comment|// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
end_comment

begin_comment
comment|// See the License for the specific language governing permissions and
end_comment

begin_comment
comment|// limitations under the License.
end_comment

begin_expr_stmt
operator|*
operator|/
specifier|public
expr|interface
name|RelOptNode
block|{
comment|/**      * Returns the ID of this relational expression, unique among all relational      * expressions created since the server was started.      *      * @return Unique ID      */
name|int
name|getId
argument_list|()
block|;
comment|/**      * Returns a string which concisely describes the definition of this      * relational expression. Two relational expressions are equivalent if and      * only if their digests are the same.      *      *<p>The digest does not contain the relational expression's identity --      * that would prevent similar relational expressions from ever comparing      * equal -- but does include the identity of children (on the assumption      * that children have already been normalized).      *      *<p>If you want a descriptive string which contains the identity, call      * {@link Object#toString()}, which always returns "rel#{id}:{digest}".      */
name|String
name|getDigest
argument_list|()
block|;
comment|/**      * Retrieves this RelNode's traits. Note that although the RelTraitSet      * returned is modifiable, it<b>must not</b> be modified during      * optimization. It is legal to modify the traits of a RelNode before or      * after optimization, although doing so could render a tree of RelNodes      * unimplementable. If a RelNode's traits need to be modified during      * optimization, clone the RelNode and change the clone's traits.      *      * @return this RelNode's trait set      */
name|RelTraitSet
name|getTraitSet
argument_list|()
block|;
comment|// TODO: We don't want to require that nodes have very detailed row type. It
comment|// may not even be known at planning time.
name|RelDataType
name|getRowType
argument_list|()
block|;
comment|/**      * Returns a string which describes the relational expression and, unlike      * {@link #getDigest()}, also includes the identity. Typically returns      * "rel#{id}:{digest}".      */
name|String
name|getDescription
argument_list|()
block|;
name|List
argument_list|<
name|?
extends|extends
name|RelOptNode
argument_list|>
name|getInputs
argument_list|()
block|;
comment|/**      * Returns the cluster this relational expression belongs to.      *      * @return cluster      */
name|RelOptCluster
name|getCluster
argument_list|()
block|; }
end_expr_stmt

begin_comment
comment|// End RelNode.java
end_comment

end_unit

