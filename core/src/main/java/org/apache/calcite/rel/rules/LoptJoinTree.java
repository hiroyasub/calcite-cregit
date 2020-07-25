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
name|rel
operator|.
name|rules
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
name|RelNode
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
name|rel
operator|.
name|core
operator|.
name|Join
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Utility class used to store a {@link org.apache.calcite.rel.core.Join} tree  * and the factors that make up the tree.  *  *<p>Because {@link RelNode}s can be duplicated in a query  * when you have a self-join, factor ids are needed to distinguish between the  * different join inputs that correspond to identical tables. The class  * associates factor ids with a join tree, matching the order of the factor ids  * with the order of those factors in the join tree.  */
end_comment

begin_class
specifier|public
class|class
name|LoptJoinTree
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|BinaryTree
name|factorTree
decl_stmt|;
specifier|private
specifier|final
name|RelNode
name|joinTree
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|removableSelfJoin
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a join-tree consisting of a single node.    *    * @param joinTree RelNode corresponding to the single node    * @param factorId factor id of the node    */
specifier|public
name|LoptJoinTree
parameter_list|(
name|RelNode
name|joinTree
parameter_list|,
name|int
name|factorId
parameter_list|)
block|{
name|this
operator|.
name|joinTree
operator|=
name|joinTree
expr_stmt|;
name|this
operator|.
name|factorTree
operator|=
operator|new
name|Leaf
argument_list|(
name|factorId
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|removableSelfJoin
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Associates the factor ids with a join-tree.    *    * @param joinTree RelNodes corresponding to the join tree    * @param factorTree tree of the factor ids    * @param removableSelfJoin whether the join corresponds to a removable    * self-join    */
specifier|public
name|LoptJoinTree
parameter_list|(
name|RelNode
name|joinTree
parameter_list|,
name|BinaryTree
name|factorTree
parameter_list|,
name|boolean
name|removableSelfJoin
parameter_list|)
block|{
name|this
operator|.
name|joinTree
operator|=
name|joinTree
expr_stmt|;
name|this
operator|.
name|factorTree
operator|=
name|factorTree
expr_stmt|;
name|this
operator|.
name|removableSelfJoin
operator|=
name|removableSelfJoin
expr_stmt|;
block|}
comment|/**    * Associates the factor ids with a join-tree given the factors corresponding    * to the left and right subtrees of the join.    *    * @param joinTree RelNodes corresponding to the join tree    * @param leftFactorTree tree of the factor ids for left subtree    * @param rightFactorTree tree of the factor ids for the right subtree    */
specifier|public
name|LoptJoinTree
parameter_list|(
name|RelNode
name|joinTree
parameter_list|,
name|BinaryTree
name|leftFactorTree
parameter_list|,
name|BinaryTree
name|rightFactorTree
parameter_list|)
block|{
name|this
argument_list|(
name|joinTree
argument_list|,
name|leftFactorTree
argument_list|,
name|rightFactorTree
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Associates the factor ids with a join-tree given the factors corresponding    * to the left and right subtrees of the join. Also indicates whether the    * join is a removable self-join.    *    * @param joinTree RelNodes corresponding to the join tree    * @param leftFactorTree tree of the factor ids for left subtree    * @param rightFactorTree tree of the factor ids for the right subtree    * @param removableSelfJoin true if the join is a removable self-join    */
specifier|public
name|LoptJoinTree
parameter_list|(
name|RelNode
name|joinTree
parameter_list|,
name|BinaryTree
name|leftFactorTree
parameter_list|,
name|BinaryTree
name|rightFactorTree
parameter_list|,
name|boolean
name|removableSelfJoin
parameter_list|)
block|{
name|factorTree
operator|=
operator|new
name|Node
argument_list|(
name|leftFactorTree
argument_list|,
name|rightFactorTree
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|this
operator|.
name|joinTree
operator|=
name|joinTree
expr_stmt|;
name|this
operator|.
name|removableSelfJoin
operator|=
name|removableSelfJoin
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelNode
name|getJoinTree
parameter_list|()
block|{
return|return
name|joinTree
return|;
block|}
specifier|public
name|LoptJoinTree
name|getLeft
parameter_list|()
block|{
specifier|final
name|Node
name|node
init|=
operator|(
name|Node
operator|)
name|factorTree
decl_stmt|;
return|return
operator|new
name|LoptJoinTree
argument_list|(
operator|(
operator|(
name|Join
operator|)
name|joinTree
operator|)
operator|.
name|getLeft
argument_list|()
argument_list|,
name|node
operator|.
name|getLeft
argument_list|()
argument_list|,
name|node
operator|.
name|getLeft
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|isRemovableSelfJoin
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|LoptJoinTree
name|getRight
parameter_list|()
block|{
specifier|final
name|Node
name|node
init|=
operator|(
name|Node
operator|)
name|factorTree
decl_stmt|;
return|return
operator|new
name|LoptJoinTree
argument_list|(
operator|(
operator|(
name|Join
operator|)
name|joinTree
operator|)
operator|.
name|getRight
argument_list|()
argument_list|,
name|node
operator|.
name|getRight
argument_list|()
argument_list|,
name|node
operator|.
name|getRight
argument_list|()
operator|.
name|getParent
argument_list|()
operator|.
name|isRemovableSelfJoin
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|BinaryTree
name|getFactorTree
parameter_list|()
block|{
return|return
name|factorTree
return|;
block|}
specifier|public
name|List
argument_list|<
name|Integer
argument_list|>
name|getTreeOrder
parameter_list|()
block|{
name|List
argument_list|<
name|Integer
argument_list|>
name|treeOrder
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|getTreeOrder
argument_list|(
name|treeOrder
argument_list|)
expr_stmt|;
return|return
name|treeOrder
return|;
block|}
specifier|public
name|void
name|getTreeOrder
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|treeOrder
parameter_list|)
block|{
name|factorTree
operator|.
name|getTreeOrder
argument_list|(
name|treeOrder
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRemovableSelfJoin
parameter_list|()
block|{
return|return
name|removableSelfJoin
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Simple binary tree class that stores an id in the leaf nodes and keeps    * track of the parent LoptJoinTree object associated with the binary tree.    */
specifier|protected
specifier|abstract
specifier|static
class|class
name|BinaryTree
block|{
specifier|private
specifier|final
name|LoptJoinTree
name|parent
decl_stmt|;
specifier|protected
name|BinaryTree
parameter_list|(
name|LoptJoinTree
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LoptJoinTree
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
specifier|public
specifier|abstract
name|void
name|getTreeOrder
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|treeOrder
parameter_list|)
function_decl|;
block|}
comment|/** Binary tree node that has no children. */
specifier|protected
specifier|static
class|class
name|Leaf
extends|extends
name|BinaryTree
block|{
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
specifier|public
name|Leaf
parameter_list|(
name|int
name|rootId
parameter_list|,
name|LoptJoinTree
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|rootId
expr_stmt|;
block|}
comment|/** Returns the id associated with a leaf node in a binary tree. */
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|void
name|getTreeOrder
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|treeOrder
parameter_list|)
block|{
name|treeOrder
operator|.
name|add
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Binary tree node that has two children. */
specifier|protected
specifier|static
class|class
name|Node
extends|extends
name|BinaryTree
block|{
specifier|private
specifier|final
name|BinaryTree
name|left
decl_stmt|;
specifier|private
specifier|final
name|BinaryTree
name|right
decl_stmt|;
specifier|public
name|Node
parameter_list|(
name|BinaryTree
name|left
parameter_list|,
name|BinaryTree
name|right
parameter_list|,
name|LoptJoinTree
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|left
argument_list|)
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|right
argument_list|)
expr_stmt|;
block|}
specifier|public
name|BinaryTree
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|BinaryTree
name|getRight
parameter_list|()
block|{
return|return
name|right
return|;
block|}
specifier|public
name|void
name|getTreeOrder
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|treeOrder
parameter_list|)
block|{
name|left
operator|.
name|getTreeOrder
argument_list|(
name|treeOrder
argument_list|)
expr_stmt|;
name|right
operator|.
name|getTreeOrder
argument_list|(
name|treeOrder
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

