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

begin_comment
comment|/**  * A mutable slot that can contain one object.  *  *<p>A holder is useful for implementing OUT or IN-OUT parameters.</p>  *  *<p>It is possible to sub-class to receive events on get or set.</p>  *  * @param<E> Element type  */
end_comment

begin_class
specifier|public
class|class
name|Holder
parameter_list|<
name|E
parameter_list|>
block|{
specifier|private
name|E
name|e
decl_stmt|;
comment|/** Creates a Holder containing a given value.    *    *<p>Call this method from a derived constructor or via the {@link #of}    * method. */
specifier|protected
name|Holder
parameter_list|(
name|E
name|e
parameter_list|)
block|{
name|this
operator|.
name|e
operator|=
name|e
expr_stmt|;
block|}
comment|/** Sets the value. */
specifier|public
name|void
name|set
parameter_list|(
name|E
name|e
parameter_list|)
block|{
name|this
operator|.
name|e
operator|=
name|e
expr_stmt|;
block|}
comment|/** Gets the value. */
specifier|public
name|E
name|get
parameter_list|()
block|{
return|return
name|e
return|;
block|}
comment|/** Creates a holder containing a given value. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Holder
argument_list|<
name|E
argument_list|>
name|of
parameter_list|(
name|E
name|e
parameter_list|)
block|{
return|return
operator|new
name|Holder
argument_list|<>
argument_list|(
name|e
argument_list|)
return|;
block|}
block|}
end_class

end_unit

