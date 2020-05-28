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
name|mutable
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
name|avatica
operator|.
name|util
operator|.
name|Spaces
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
name|plan
operator|.
name|RelOptCluster
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
name|type
operator|.
name|RelDataType
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
name|base
operator|.
name|Equivalence
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
name|Lists
import|;
end_import

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
comment|/** Mutable equivalent of {@link RelNode}.  *  *<p>Each node has mutable state, and keeps track of its parent and position  * within parent.  * It doesn't make sense to canonize {@code MutableRels},  * otherwise one node could end up with multiple parents.  * It follows that {@code #hashCode} and {@code #equals} are less efficient  * than their {@code RelNode} counterparts.  * But, you don't need to copy a {@code MutableRel} in order to change it.  * For this reason, you should use {@code MutableRel} for short-lived  * operations, and transcribe back to {@code RelNode} when you are done.</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MutableRel
block|{
comment|/** Equivalence that compares objects by their {@link Object#toString()}    * method. */
specifier|protected
specifier|static
specifier|final
name|Equivalence
argument_list|<
name|Object
argument_list|>
name|STRING_EQUIVALENCE
init|=
operator|new
name|Equivalence
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|boolean
name|doEquivalent
parameter_list|(
name|Object
name|o
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
return|return
name|o
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|o2
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|int
name|doHash
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|.
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
decl_stmt|;
comment|/** Equivalence that compares {@link Lists}s by the    * {@link Object#toString()} of their elements. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|protected
specifier|static
specifier|final
name|Equivalence
argument_list|<
name|List
argument_list|<
name|?
argument_list|>
argument_list|>
name|PAIRWISE_STRING_EQUIVALENCE
init|=
operator|(
name|Equivalence
operator|)
name|STRING_EQUIVALENCE
operator|.
name|pairwise
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|RelOptCluster
name|cluster
decl_stmt|;
specifier|public
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|protected
specifier|final
name|MutableRelType
name|type
decl_stmt|;
specifier|protected
annotation|@
name|Nullable
name|MutableRel
name|parent
decl_stmt|;
specifier|protected
name|int
name|ordinalInParent
decl_stmt|;
specifier|protected
name|MutableRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|MutableRelType
name|type
parameter_list|)
block|{
name|this
operator|.
name|cluster
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|cluster
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|rowType
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
annotation|@
name|Nullable
name|MutableRel
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
name|setInput
parameter_list|(
name|int
name|ordinalInParent
parameter_list|,
name|MutableRel
name|input
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|List
argument_list|<
name|MutableRel
argument_list|>
name|getInputs
parameter_list|()
function_decl|;
annotation|@
name|Override
specifier|public
specifier|abstract
name|MutableRel
name|clone
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|void
name|childrenAccept
parameter_list|(
name|MutableRelVisitor
name|visitor
parameter_list|)
function_decl|;
comment|/** Replaces this {@code MutableRel} in its parent with another node at the    * same position.    *    *<p>Before the method, {@code child} must be an orphan (have null parent)    * and after this method, this {@code MutableRel} is an orphan.    *    * @return The parent    */
specifier|public
annotation|@
name|Nullable
name|MutableRel
name|replaceInParent
parameter_list|(
name|MutableRel
name|child
parameter_list|)
block|{
specifier|final
name|MutableRel
name|parent
init|=
name|this
operator|.
name|parent
decl_stmt|;
if|if
condition|(
name|this
operator|!=
name|child
condition|)
block|{
if|if
condition|(
name|parent
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|setInput
argument_list|(
name|ordinalInParent
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|parent
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|ordinalInParent
operator|=
literal|0
expr_stmt|;
block|}
block|}
return|return
name|parent
return|;
block|}
specifier|public
specifier|abstract
name|StringBuilder
name|digest
parameter_list|(
name|StringBuilder
name|buf
parameter_list|)
function_decl|;
specifier|public
specifier|final
name|String
name|deep
parameter_list|()
block|{
return|return
operator|new
name|MutableRelDumper
argument_list|()
operator|.
name|apply
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
return|return
name|deep
argument_list|()
return|;
block|}
comment|/**    * Implementation of MutableVisitor that dumps the details    * of a MutableRel tree.    */
specifier|private
specifier|static
class|class
name|MutableRelDumper
extends|extends
name|MutableRelVisitor
block|{
specifier|private
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
name|int
name|level
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|visit
parameter_list|(
annotation|@
name|Nullable
name|MutableRel
name|node
parameter_list|)
block|{
name|Spaces
operator|.
name|append
argument_list|(
name|buf
argument_list|,
name|level
operator|*
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|digest
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
operator|++
name|level
expr_stmt|;
name|super
operator|.
name|visit
argument_list|(
name|node
argument_list|)
expr_stmt|;
operator|--
name|level
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|apply
parameter_list|(
name|MutableRel
name|rel
parameter_list|)
block|{
name|go
argument_list|(
name|rel
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

