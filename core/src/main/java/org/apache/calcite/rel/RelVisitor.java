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
package|;
end_package

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * A<code>RelVisitor</code> is a Visitor role in the  * {@link org.apache.calcite.util.Glossary#VISITOR_PATTERN visitor pattern} and  * visits {@link RelNode} objects as the role of Element. Other components in  * the pattern: {@link RelNode#childrenAccept(RelVisitor)}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelVisitor
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
annotation|@
name|Nullable
name|RelNode
name|root
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Visits a node during a traversal.    *    * @param node    Node to visit    * @param ordinal Ordinal of node within its parent    * @param parent  Parent of the node, or null if it is the root of the    *                traversal    */
specifier|public
name|void
name|visit
parameter_list|(
name|RelNode
name|node
parameter_list|,
name|int
name|ordinal
parameter_list|,
annotation|@
name|Nullable
name|RelNode
name|parent
parameter_list|)
block|{
name|node
operator|.
name|childrenAccept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Replaces the root node of this traversal.    *    * @param node The new root node    */
specifier|public
name|void
name|replaceRoot
parameter_list|(
annotation|@
name|Nullable
name|RelNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|node
expr_stmt|;
block|}
comment|/**    * Starts an iteration.    */
specifier|public
annotation|@
name|Nullable
name|RelNode
name|go
parameter_list|(
name|RelNode
name|p
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|p
expr_stmt|;
name|visit
argument_list|(
name|p
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|root
return|;
block|}
block|}
end_class

end_unit

