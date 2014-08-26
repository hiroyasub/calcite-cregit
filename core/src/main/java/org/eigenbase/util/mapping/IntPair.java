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
name|util
operator|.
name|mapping
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
comment|/**  * An immutable pair of integers.  *  * @see Mapping#iterator()  */
end_comment

begin_class
specifier|public
class|class
name|IntPair
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
specifier|final
name|int
name|source
decl_stmt|;
specifier|public
specifier|final
name|int
name|target
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|IntPair
parameter_list|(
name|int
name|source
parameter_list|,
name|int
name|target
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
specifier|static
name|IntPair
name|of
parameter_list|(
name|int
name|left
parameter_list|,
name|int
name|right
parameter_list|)
block|{
return|return
operator|new
name|IntPair
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|source
operator|+
literal|"-"
operator|+
name|target
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|IntPair
condition|)
block|{
name|IntPair
name|that
init|=
operator|(
name|IntPair
operator|)
name|obj
decl_stmt|;
return|return
operator|(
name|this
operator|.
name|source
operator|==
name|that
operator|.
name|source
operator|)
operator|&&
operator|(
name|this
operator|.
name|target
operator|==
name|that
operator|.
name|target
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|source
operator|^
operator|(
name|this
operator|.
name|target
operator|<<
literal|4
operator|)
return|;
block|}
comment|/**    * Converts two lists into a list of {@link IntPair}s,    * whose length is the lesser of the lengths of the    * source lists.    *    * @param lefts Left list    * @param rights Right list    * @return List of pairs    */
specifier|public
specifier|static
name|List
argument_list|<
name|IntPair
argument_list|>
name|zip
parameter_list|(
name|List
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|lefts
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|rights
parameter_list|)
block|{
return|return
name|zip
argument_list|(
name|lefts
argument_list|,
name|rights
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Converts two lists into a list of {@link IntPair}s.    *    *<p>The length of the combined list is the lesser of the lengths of the    * source lists. But typically the source lists will be the same length.</p>    *    * @param lefts Left list    * @param rights Right list    * @param strict Whether to fail if lists have different size    * @return List of pairs    */
specifier|public
specifier|static
name|List
argument_list|<
name|IntPair
argument_list|>
name|zip
parameter_list|(
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|lefts
parameter_list|,
specifier|final
name|List
argument_list|<
name|?
extends|extends
name|Number
argument_list|>
name|rights
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
specifier|final
name|int
name|size
decl_stmt|;
if|if
condition|(
name|strict
condition|)
block|{
if|if
condition|(
name|lefts
operator|.
name|size
argument_list|()
operator|!=
name|rights
operator|.
name|size
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
name|size
operator|=
name|lefts
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|size
operator|=
name|Math
operator|.
name|min
argument_list|(
name|lefts
operator|.
name|size
argument_list|()
argument_list|,
name|rights
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|AbstractList
argument_list|<
name|IntPair
argument_list|>
argument_list|()
block|{
specifier|public
name|IntPair
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|IntPair
operator|.
name|of
argument_list|(
name|lefts
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|,
name|rights
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End IntPair.java
end_comment

end_unit

