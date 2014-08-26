begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
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
name|util
operator|.
name|Util
import|;
end_import

begin_comment
comment|/**  * RelTraitPropagationVisitor traverses a RelNode and its<i>unregistered</i>  * children, making sure that each has a full complement of traits. When a  * RelNode is found to be missing one or more traits, they are copied from a  * RelTraitSet given during construction.  */
end_comment

begin_class
specifier|public
class|class
name|RelTraitPropagationVisitor
extends|extends
name|RelVisitor
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelTraitSet
name|baseTraits
decl_stmt|;
specifier|private
specifier|final
name|RelOptPlanner
name|planner
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelTraitPropagationVisitor
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelTraitSet
name|baseTraits
parameter_list|)
block|{
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
name|this
operator|.
name|baseTraits
operator|=
name|baseTraits
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|visit
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|parent
parameter_list|)
block|{
comment|// REVIEW: SWZ: 1/31/06: We assume that any special RelNodes, such
comment|// as the VolcanoPlanner's RelSubset always have a full complement
comment|// of traits and that they either appear as registered or do nothing
comment|// when childrenAccept is called on them.
if|if
condition|(
name|planner
operator|.
name|isRegistered
argument_list|(
name|rel
argument_list|)
condition|)
block|{
return|return;
block|}
name|RelTraitSet
name|relTraits
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|baseTraits
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>=
name|relTraits
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// Copy traits that the new rel doesn't know about.
name|Util
operator|.
name|discard
argument_list|(
name|RelOptUtil
operator|.
name|addTrait
argument_list|(
name|rel
argument_list|,
name|baseTraits
operator|.
name|getTrait
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// FIXME: Return the new rel. We can no longer traits in-place,
comment|//   because rels and traits are immutable.
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
else|else
block|{
comment|// Verify that the traits are from the same RelTraitDef
assert|assert
name|relTraits
operator|.
name|getTrait
argument_list|(
name|i
argument_list|)
operator|.
name|getTraitDef
argument_list|()
operator|==
name|baseTraits
operator|.
name|getTrait
argument_list|(
name|i
argument_list|)
operator|.
name|getTraitDef
argument_list|()
assert|;
block|}
block|}
name|rel
operator|.
name|childrenAccept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelTraitPropagationVisitor.java
end_comment

end_unit

