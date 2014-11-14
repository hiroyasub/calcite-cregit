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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|ListIterator
import|;
end_import

begin_comment
comment|/**  * CompoundClosableAllocation represents a collection of ClosableAllocations  * which share a common lifecycle. It guarantees that allocations are closed in  * the reverse order in which they were added.  */
end_comment

begin_class
specifier|public
class|class
name|CompoundClosableAllocation
implements|implements
name|ClosableAllocationOwner
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * List of owned ClosableAllocation objects.    */
specifier|protected
name|List
argument_list|<
name|ClosableAllocation
argument_list|>
name|allocations
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|CompoundClosableAllocation
parameter_list|()
block|{
name|allocations
operator|=
operator|new
name|LinkedList
argument_list|<
name|ClosableAllocation
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement ClosableAllocationOwner
specifier|public
name|void
name|addAllocation
parameter_list|(
name|ClosableAllocation
name|allocation
parameter_list|)
block|{
name|allocations
operator|.
name|add
argument_list|(
name|allocation
argument_list|)
expr_stmt|;
block|}
comment|// implement ClosableAllocation
specifier|public
name|void
name|closeAllocation
parameter_list|()
block|{
comment|// traverse in reverse order
name|ListIterator
argument_list|<
name|ClosableAllocation
argument_list|>
name|iter
init|=
name|allocations
operator|.
name|listIterator
argument_list|(
name|allocations
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasPrevious
argument_list|()
condition|)
block|{
name|ClosableAllocation
name|allocation
init|=
name|iter
operator|.
name|previous
argument_list|()
decl_stmt|;
comment|// NOTE:  nullify the entry just retrieved so that if allocation
comment|// calls back to forgetAllocation, it won't find itself
comment|// (this prevents a ConcurrentModificationException)
name|iter
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|allocation
operator|.
name|closeAllocation
argument_list|()
expr_stmt|;
block|}
name|allocations
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Forgets an allocation without closing it.    *    * @param allocation the allocation to forget    * @return whether the allocation was known    */
specifier|public
name|boolean
name|forgetAllocation
parameter_list|(
name|ClosableAllocation
name|allocation
parameter_list|)
block|{
return|return
name|allocations
operator|.
name|remove
argument_list|(
name|allocation
argument_list|)
return|;
block|}
comment|/**    * @return whether any allocations remain unclosed    */
specifier|public
name|boolean
name|hasAllocations
parameter_list|()
block|{
return|return
operator|!
name|allocations
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End CompoundClosableAllocation.java
end_comment

end_unit

