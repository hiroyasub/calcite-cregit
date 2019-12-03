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
name|AbstractList
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
comment|/**  * Converts a list whose members are automatically down-cast to a given type.  *  *<p>If a member of the backing list is not an instanceof<code>E</code>, the  * accessing method (such as {@link List#get}) will throw a  * {@link ClassCastException}.  *  *<p>All modifications are automatically written to the backing list. Not  * synchronized.  *  * @param<E> Element type  */
end_comment

begin_class
specifier|public
class|class
name|CastingList
parameter_list|<
name|E
parameter_list|>
extends|extends
name|AbstractList
argument_list|<
name|E
argument_list|>
implements|implements
name|List
argument_list|<
name|E
argument_list|>
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|?
super|super
name|E
argument_list|>
name|list
decl_stmt|;
specifier|private
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|clazz
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|CastingList
parameter_list|(
name|List
argument_list|<
name|?
super|super
name|E
argument_list|>
name|list
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|clazz
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|list
operator|=
name|list
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|E
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|E
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|E
name|element
parameter_list|)
block|{
specifier|final
name|Object
name|o
init|=
name|list
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|element
argument_list|)
decl_stmt|;
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|o
argument_list|)
return|;
block|}
specifier|public
name|E
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|list
operator|.
name|remove
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|int
name|pos
parameter_list|,
name|E
name|o
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|pos
argument_list|,
name|o
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

