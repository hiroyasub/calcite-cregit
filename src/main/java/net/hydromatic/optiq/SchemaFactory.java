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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory for {@link net.hydromatic.optiq.Schema} objects.  *  *<p>A class that implements SchemaFactory specified in a schema must have a  * public default constructor.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|SchemaFactory
block|{
comment|/** Creates a Schema.      *      *<p>The implementation must register the schema in the parent schema,      * by calling {@link MutableSchema#addSchema(String, Schema)}.</p>      *      * @param schema Parent schema      * @param name Name of this schema      * @param operand The "operand" JSON property      */
name|Schema
name|create
parameter_list|(
name|MutableSchema
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SchemaFactory.java
end_comment

end_unit

