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
name|rel
operator|.
name|type
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
comment|/**  * RelDataTypeField represents the definition of a field in a structured  * {@link RelDataType}.  *  *<p>Extends the {@link java.util.Map.Entry} interface to allow convenient  * inter-operation with Java collections classes. In any implementation of this  * interface, {@link #getKey()} must be equivalent to {@link #getName()}  * and {@link #getValue()} must be equivalent to {@link #getType()}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDataTypeField
extends|extends
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
block|{
comment|/**    * Function to transform a set of {@link RelDataTypeField} to    * a set of {@link Integer} of the field keys.    *    * @deprecated Use {@code RelDataTypeField::getIndex}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
class|class
name|ToFieldIndex
implements|implements
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
argument_list|<
name|RelDataTypeField
argument_list|,
name|Integer
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|Integer
name|apply
parameter_list|(
name|RelDataTypeField
name|o
parameter_list|)
block|{
return|return
name|o
operator|.
name|getIndex
argument_list|()
return|;
block|}
block|}
comment|/**    * Function to transform a set of {@link RelDataTypeField} to    * a set of {@link String} of the field names.    *    * @deprecated Use {@code RelDataTypeField::getName}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
class|class
name|ToFieldName
implements|implements
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
argument_list|<
name|RelDataTypeField
argument_list|,
name|String
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|RelDataTypeField
name|o
parameter_list|)
block|{
return|return
name|o
operator|.
name|getName
argument_list|()
return|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Gets the name of this field, which is unique within its containing type.    *    * @return field name    */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Gets the ordinal of this field within its containing type.    *    * @return 0-based ordinal    */
name|int
name|getIndex
parameter_list|()
function_decl|;
comment|/**    * Gets the type of this field.    *    * @return field type    */
name|RelDataType
name|getType
parameter_list|()
function_decl|;
comment|/**    * Returns true if this is a dynamic star field.    */
name|boolean
name|isDynamicStar
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

