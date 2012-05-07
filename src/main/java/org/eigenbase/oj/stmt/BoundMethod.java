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
name|oj
operator|.
name|stmt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * BoundMethod is a "thunk": a method which has already been bound to a  * particular object on which it should be invoked, together with the arguments  * which should be passed on invocation.  */
end_comment

begin_class
specifier|public
class|class
name|BoundMethod
block|{
comment|//~ Instance fields --------------------------------------------------------
name|Method
name|method
decl_stmt|;
name|Object
name|o
decl_stmt|;
name|String
index|[]
name|parameterNames
decl_stmt|;
name|Object
index|[]
name|args
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|BoundMethod
parameter_list|(
name|Object
name|o
parameter_list|,
name|Method
name|method
parameter_list|,
name|String
index|[]
name|parameterNames
parameter_list|)
block|{
name|this
operator|.
name|o
operator|=
name|o
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|parameterNames
operator|=
name|parameterNames
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Object
name|call
parameter_list|()
throws|throws
name|IllegalAccessException
throws|,
name|InvocationTargetException
block|{
return|return
name|method
operator|.
name|invoke
argument_list|(
name|o
argument_list|,
name|args
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End BoundMethod.java
end_comment

end_unit

