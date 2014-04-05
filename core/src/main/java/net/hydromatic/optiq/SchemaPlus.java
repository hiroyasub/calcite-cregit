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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  * Extension to the {@link Schema} interface.  *  *<p>Given a user-defined schema that implements the {@link Schema} interface,  * Optiq creates a wrapper that implements the {@code SchemaPlus} interface.  * This provides extra functionality, such as access to tables that have been  * added explicitly.</p>  *  *<p>A user-defined schema does not need to implement this interface, but by  * the time a schema is passed to a method in a user-defined schema or  * user-defined table, it will have been wrapped in this interface.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|SchemaPlus
extends|extends
name|Schema
block|{
comment|/**    * Returns the parent schema, or null if this schema has no parent.    */
name|SchemaPlus
name|getParentSchema
parameter_list|()
function_decl|;
comment|/**    * Returns the name of this schema.    *    *<p>The name must not be null, and must be unique within its parent.    * The root schema is typically named "".    */
name|String
name|getName
parameter_list|()
function_decl|;
comment|// override with stricter return
name|SchemaPlus
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/** Adds a schema as a sub-schema of this schema, and returns the wrapped    * object. */
name|SchemaPlus
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Schema
name|schema
parameter_list|)
function_decl|;
comment|/** Adds a table to this schema. */
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Table
name|table
parameter_list|)
function_decl|;
comment|/** Adds a function to this schema. */
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Function
name|function
parameter_list|)
function_decl|;
name|boolean
name|isMutable
parameter_list|()
function_decl|;
comment|/** Returns an underlying object. */
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
function_decl|;
name|void
name|setPath
parameter_list|(
name|ImmutableList
argument_list|<
name|ImmutableList
argument_list|<
name|String
argument_list|>
argument_list|>
name|path
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SchemaPlus.java
end_comment

end_unit

