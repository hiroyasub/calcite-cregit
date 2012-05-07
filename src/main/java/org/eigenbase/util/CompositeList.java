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
name|util
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
comment|/**  * Read-only list that is the concatenation of sub-lists.  *  *<p>The list is read-only; attempts to call methods such as  * {@link #add(Object)} or {@link #set(int, Object)} will throw.  *  *<p>Changes to the backing lists, including changes in length, will be  * reflected in this list.  *  *<p>This class is not thread-safe. Changes to backing lists will cause  * unspecified behavior.  *  * @param<T> Element type  *  * @author jhyde  * @version $Id$  * @since 31 October, 2009  */
end_comment

begin_class
specifier|public
class|class
name|CompositeList
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractList
argument_list|<
name|T
argument_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|T
argument_list|>
index|[]
name|lists
decl_stmt|;
comment|/**      * Creates a CompoundList.      *      * @param lists Constituent lists      */
specifier|public
name|CompositeList
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
modifier|...
name|lists
parameter_list|)
block|{
name|this
operator|.
name|lists
operator|=
name|lists
expr_stmt|;
block|}
comment|/**      * Creates a CompoundList.      *      *<p>More convenient than {@link #CompositeList(java.util.List[])},      * because element type is inferred. Use this method as you would      * {@link java.util.Arrays#asList(Object[])} or      * {@link java.util.EnumSet#of(Enum, Enum[])}.      *      * @param lists Consistituent lists      * @param<T> Element type      * @return List consisting of all lists      */
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|CompositeList
argument_list|<
name|T
argument_list|>
name|of
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
modifier|...
name|lists
parameter_list|)
block|{
return|return
operator|new
name|CompositeList
argument_list|<
name|T
argument_list|>
argument_list|(
name|lists
argument_list|)
return|;
block|}
specifier|public
name|T
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
for|for
control|(
name|List
argument_list|<
name|T
argument_list|>
name|list
range|:
name|lists
control|)
block|{
name|int
name|nextIndex
init|=
name|index
operator|-
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|nextIndex
operator|<
literal|0
condition|)
block|{
return|return
name|list
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
name|index
operator|=
name|nextIndex
expr_stmt|;
block|}
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|()
throw|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|List
argument_list|<
name|T
argument_list|>
name|list
range|:
name|lists
control|)
block|{
name|n
operator|+=
name|list
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
name|n
return|;
block|}
block|}
end_class

begin_comment
comment|// End CompositeList.java
end_comment

end_unit

