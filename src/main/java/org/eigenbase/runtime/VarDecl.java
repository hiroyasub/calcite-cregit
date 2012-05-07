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

begin_comment
comment|/**  * An array of<code>VarDecl</code>s is returned from the<code>dummy()</code>  * method which is generated to implement a variable declaration, or a list of  * statements which contain variable declarations.  */
end_comment

begin_class
specifier|public
class|class
name|VarDecl
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|public
name|Class
name|clazz
decl_stmt|;
specifier|public
name|Object
name|value
decl_stmt|;
specifier|public
name|String
name|name
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|VarDecl
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
name|clazz
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End VarDecl.java
end_comment

end_unit

