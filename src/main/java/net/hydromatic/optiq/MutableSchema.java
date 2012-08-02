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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

begin_comment
comment|/**  * Schema that can be modified.  */
end_comment

begin_interface
specifier|public
interface|interface
name|MutableSchema
extends|extends
name|Schema
block|{
comment|/** Defines a table-function in this schema. There can be multiple      * table-functions with the same name; this method will not remove a      * table-function with the same name, just define another overloading. */
name|void
name|addTableFunction
parameter_list|(
name|String
name|name
parameter_list|,
name|TableFunction
name|tableFunction
parameter_list|)
function_decl|;
comment|/** Defines a table within this schema. */
name|void
name|addTable
parameter_list|(
name|String
name|name
parameter_list|,
name|Table
name|table
parameter_list|)
function_decl|;
comment|/** Adds a child schema of this schema. */
name|void
name|addSchema
parameter_list|(
name|String
name|name
parameter_list|,
name|Schema
name|schema
parameter_list|)
function_decl|;
comment|/** Returns the expression with which a sub-schema of this schema with a      * given name and type should be accessed. */
name|Expression
name|getSubSchemaExpression
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
name|type
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End MutableSchema.java
end_comment

end_unit

