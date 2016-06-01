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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
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
name|Arrays
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
comment|/**  * Extension to {@link ArrayList} to help build an array of<code>int</code>  * values.  */
end_comment

begin_class
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|Ints
operator|.
name|toArray
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**    * Converts a list of {@link Integer} objects to an array of primitive    *<code>int</code>s.    *    * @param integers List of Integer objects    * @return Array of primitive<code>int</code>s    *    * @deprecated Use {@link Ints#toArray(java.util.Collection)}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
return|return
name|Ints
operator|.
name|toArray
argument_list|(
name|integers
argument_list|)
return|;
block|}
comment|/**    * Returns a list backed by an array of primitive<code>int</code> values.    *    *<p>The behavior is analogous to {@link Arrays#asList(Object[])}. Changes    * to the list are reflected in the array. The list cannot be extended.    *    * @param args Array of primitive<code>int</code> values    * @return List backed by array    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
name|Ints
operator|.
name|asList
argument_list|(
name|args
argument_list|)
return|;
block|}
specifier|public
name|ImmutableIntList
name|asImmutable
parameter_list|()
block|{
return|return
name|ImmutableIntList
operator|.
name|copyOf
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End IntList.java
end_comment

end_unit

