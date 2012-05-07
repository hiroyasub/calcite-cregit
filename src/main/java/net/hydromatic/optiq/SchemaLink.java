begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
package|;
end_package

begin_comment
comment|/**  * A schema included in another schema with a given name.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SchemaLink
implements|implements
name|SchemaObject
block|{
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
specifier|final
name|Schema
name|schema
decl_stmt|;
specifier|public
name|SchemaLink
parameter_list|(
name|String
name|name
parameter_list|,
name|Schema
name|schema
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
name|schema
operator|=
name|schema
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
end_class

begin_comment
comment|// End SchemaLink.java
end_comment

end_unit

