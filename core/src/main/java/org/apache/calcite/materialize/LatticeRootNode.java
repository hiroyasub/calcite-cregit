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
name|materialize
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
name|util
operator|.
name|Litmus
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/** Root node in a {@link Lattice}. It has no parent. */
end_comment

begin_class
specifier|public
class|class
name|LatticeRootNode
extends|extends
name|LatticeNode
block|{
comment|/** Descendants, in prefix order. This root node is at position 0. */
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|LatticeNode
argument_list|>
name|descendants
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|Path
argument_list|>
name|paths
decl_stmt|;
name|LatticeRootNode
parameter_list|(
name|LatticeSpace
name|space
parameter_list|,
name|MutableNode
name|mutableNode
parameter_list|)
block|{
name|super
argument_list|(
name|space
argument_list|,
literal|null
argument_list|,
name|mutableNode
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|LatticeNode
argument_list|>
name|b
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|flattenTo
argument_list|(
name|b
argument_list|)
expr_stmt|;
name|this
operator|.
name|descendants
operator|=
name|b
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|paths
operator|=
name|createPaths
argument_list|(
name|space
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ImmutableList
argument_list|<
name|Path
argument_list|>
name|createPaths
parameter_list|(
name|LatticeSpace
name|space
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Step
argument_list|>
name|steps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Path
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|createPathsRecurse
argument_list|(
name|space
argument_list|,
name|steps
argument_list|,
name|paths
argument_list|)
expr_stmt|;
assert|assert
name|steps
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|paths
argument_list|)
return|;
block|}
name|void
name|use
parameter_list|(
name|List
argument_list|<
name|LatticeNode
argument_list|>
name|usedNodes
parameter_list|)
block|{
if|if
condition|(
operator|!
name|usedNodes
operator|.
name|contains
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|usedNodes
operator|.
name|add
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Validates that nodes form a tree; each node except the first references    * a predecessor. */
name|boolean
name|isValid
parameter_list|(
name|Litmus
name|litmus
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|descendants
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|LatticeNode
name|node
init|=
name|descendants
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|node
operator|!=
name|this
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"node 0 should be root"
argument_list|)
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|LatticeChildNode
operator|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"node after 0 should be child"
argument_list|)
return|;
block|}
specifier|final
name|LatticeChildNode
name|child
init|=
operator|(
name|LatticeChildNode
operator|)
name|node
decl_stmt|;
if|if
condition|(
operator|!
name|descendants
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
operator|.
name|contains
argument_list|(
name|child
operator|.
name|parent
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|"parent not in preceding list"
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
comment|/** Whether this node's graph is a super-set of (or equal to) another node's    * graph. */
specifier|public
name|boolean
name|contains
parameter_list|(
name|LatticeRootNode
name|node
parameter_list|)
block|{
return|return
name|paths
operator|.
name|containsAll
argument_list|(
name|node
operator|.
name|paths
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LatticeRootNode.java
end_comment

end_unit
