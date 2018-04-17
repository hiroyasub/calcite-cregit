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
name|schema
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|materialize
operator|.
name|Lattice
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Bug
import|;
end_import

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
comment|/**  * Extension to the {@link Schema} interface.  *  *<p>Given a user-defined schema that implements the {@link Schema} interface,  * Calcite creates a wrapper that implements the {@code SchemaPlus} interface.  * This provides extra functionality, such as access to tables that have been  * added explicitly.  *  *<p>A user-defined schema does not need to implement this interface, but by  * the time a schema is passed to a method in a user-defined schema or  * user-defined table, it will have been wrapped in this interface.  *  *<p>SchemaPlus is intended to be used by users but not instantiated by them.  * Users should only use the SchemaPlus they are given by the system.  * The purpose of SchemaPlus is to expose to user code, in a read only manner,  * some of the extra information about schemas that Calcite builds up when a  * schema is registered. It appears in several SPI calls as context; for example  * {@link SchemaFactory#create(SchemaPlus, String, java.util.Map)} contains a  * parent schema that might be a wrapped instance of a user-defined  * {@link Schema}, or indeed might not.  */
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
specifier|default
name|SchemaPlus
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Bug
operator|.
name|upgrade
argument_list|(
literal|"janino 2.7.6 does not work without this method,"
operator|+
literal|"see https://github.com/janino-compiler/janino/issues/47"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
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
comment|/** Adds a lattice to this schema. */
name|void
name|add
parameter_list|(
name|String
name|name
parameter_list|,
name|Lattice
name|lattice
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
name|void
name|setCacheEnabled
parameter_list|(
name|boolean
name|cache
parameter_list|)
function_decl|;
name|boolean
name|isCacheEnabled
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End SchemaPlus.java
end_comment

end_unit

