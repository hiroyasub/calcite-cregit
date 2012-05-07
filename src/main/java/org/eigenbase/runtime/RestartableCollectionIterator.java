begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|runtime
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

begin_comment
comment|/**  *<code>RestartableCollectionIterator</code> implements the {@link  * RestartableIterator} interface in terms of an underlying {@link Collection}.  *  *<p>TODO jvs 21-Mar-2006: This class is no longer used except by Saffron, so  * we should move it to Saffron.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RestartableCollectionIterator
implements|implements
name|RestartableIterator
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Collection
name|collection
decl_stmt|;
specifier|private
name|Iterator
name|iterator
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RestartableCollectionIterator
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
name|this
operator|.
name|collection
operator|=
name|collection
expr_stmt|;
name|iterator
operator|=
name|collection
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement Iterator
specifier|public
name|Object
name|next
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
return|;
block|}
comment|// implement Iterator
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iterator
operator|.
name|hasNext
argument_list|()
return|;
block|}
comment|// implement Iterator
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
comment|// implement RestartableIterator
specifier|public
name|void
name|restart
parameter_list|()
block|{
name|iterator
operator|=
name|collection
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RestartableCollectionIterator.java
end_comment

end_unit

