begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeFactory
import|;
end_import

begin_comment
comment|/**  * Parameter to a {@link Function}.  *  *<p>NOTE: We'd have called it {@code Parameter} but the overlap with  * {@link java.lang.reflect.Parameter} was too confusing.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|FunctionParameter
block|{
comment|/**    * Zero-based ordinal of this parameter within the member's parameter    * list.    *    * @return Parameter ordinal    */
name|int
name|getOrdinal
parameter_list|()
function_decl|;
comment|/**    * Name of the parameter.    *    * @return Parameter name    */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Returns the type of this parameter.    *    * @param typeFactory Type factory to be used to create the type    *    * @return Parameter type.    */
name|RelDataType
name|getType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End FunctionParameter.java
end_comment

end_unit

