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
name|avatica
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Definition of a property that may be specified on the JDBC connect string.  * {@link BuiltInConnectionProperty} enumerates built-in properties, but  * there may be others; the list is not closed.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConnectionProperty
block|{
comment|/** The name of this property. (E.g. "MATERIALIZATIONS_ENABLED".) */
name|String
name|name
parameter_list|()
function_decl|;
comment|/** The name of this property in camel-case.    * (E.g. "materializationsEnabled".) */
name|String
name|camelName
parameter_list|()
function_decl|;
comment|/** Returns the default value of this property. The type must match its data    * type. */
name|Object
name|defaultValue
parameter_list|()
function_decl|;
comment|/** Returns the data type of this property. */
name|Type
name|type
parameter_list|()
function_decl|;
comment|/** Wraps this property with a properties object from which its value can be    * obtained when needed. */
name|ConnectionConfigImpl
operator|.
name|PropEnv
name|wrap
parameter_list|(
name|Properties
name|properties
parameter_list|)
function_decl|;
comment|/** Data type of property. */
enum|enum
name|Type
block|{
name|BOOLEAN
block|,
name|STRING
block|,
name|ENUM
block|;
specifier|public
name|boolean
name|valid
parameter_list|(
name|Object
name|defaultValue
parameter_list|)
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|BOOLEAN
case|:
return|return
name|defaultValue
operator|instanceof
name|Boolean
return|;
case|case
name|STRING
case|:
return|return
name|defaultValue
operator|instanceof
name|String
return|;
default|default:
return|return
name|defaultValue
operator|instanceof
name|Enum
return|;
block|}
block|}
block|}
block|}
end_interface

begin_comment
comment|// End ConnectionProperty.java
end_comment

end_unit

