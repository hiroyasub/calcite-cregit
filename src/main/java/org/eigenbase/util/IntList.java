begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
comment|/**  * Extension to {@link ArrayList} to help build an array of<code>int</code>  * values.  *  * @author jhyde  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|IntList
extends|extends
name|ArrayList
argument_list|<
name|Integer
argument_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|int
index|[]
name|toIntArray
parameter_list|()
block|{
return|return
name|toArray
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Converts a list of {@link Integer} objects to an array of primitive      *<code>int</code>s.      *      * @param integers List of Integer objects      *      * @return Array of primitive<code>int</code>s      */
specifier|public
specifier|static
name|int
index|[]
name|toArray
parameter_list|(
name|List
argument_list|<
name|Integer
argument_list|>
name|integers
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|ints
init|=
operator|new
name|int
index|[
name|integers
operator|.
name|size
argument_list|()
index|]
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
name|ints
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ints
index|[
name|i
index|]
operator|=
name|integers
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|ints
return|;
block|}
comment|/**      * Returns a list backed by an array of primitive<code>int</code> values.      *      *<p>The behavior is analogous to {@link Arrays#asList(Object[])}. Changes      * to the list are reflected in the array. The list cannot be extended.      *      * @param args Array of primitive<code>int</code> values      *      * @return List backed by array      */
specifier|public
specifier|static
name|List
argument_list|<
name|Integer
argument_list|>
name|asList
parameter_list|(
specifier|final
name|int
index|[]
name|args
parameter_list|)
block|{
return|return
operator|new
name|AbstractList
argument_list|<
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|args
index|[
name|index
index|]
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|args
operator|.
name|length
return|;
block|}
specifier|public
name|Integer
name|set
parameter_list|(
name|int
name|index
parameter_list|,
name|Integer
name|element
parameter_list|)
block|{
return|return
name|args
index|[
name|index
index|]
operator|=
name|element
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End IntList.java
end_comment

end_unit

