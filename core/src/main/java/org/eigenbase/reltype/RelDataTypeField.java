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
name|reltype
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
name|Types
import|;
end_import

begin_comment
comment|/**  * RelDataTypeField represents the definition of a field in a structured {@link  * RelDataType}.  *  *<p>Extends the {@link java.util.Map.Entry} interface to allow convenient  * inter-operation with Java collections classes. In any implementation of this  * interface, {@link #getKey()} must be equivalent to {@link #getName()}  * and {@link #getValue()} must be equivalent to {@link #getType()}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDataTypeField
extends|extends
comment|/*Types.RecordField, */
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Gets the name of this field, which is unique within its containing type.      *      * @return field name      */
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Gets the ordinal of this field within its containing type.      *      * @return 0-based ordinal      */
specifier|public
name|int
name|getIndex
parameter_list|()
function_decl|;
comment|/**      * Gets the type of this field.      *      * @return field type      */
specifier|public
name|RelDataType
name|getType
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelDataTypeField.java
end_comment

end_unit

