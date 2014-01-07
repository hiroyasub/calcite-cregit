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
operator|.
name|mapping
package|;
end_package

begin_comment
comment|/**  * An immutable pair of integers.  * @see Mapping#iterator()  */
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
block|}
end_class

begin_comment
comment|// End IntPair.java
end_comment

end_unit

