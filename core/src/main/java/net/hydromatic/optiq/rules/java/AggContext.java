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
operator|.
name|rules
operator|.
name|java
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|Aggregation
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
name|RelDataType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Information on the aggregate calculation context.  * {@link AggAddContext} provides basic static information on types of arguments  * and the return value of the aggregate being implemented.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AggContext
block|{
comment|/**    * Returns the aggregation being implemented.    * @return aggregation being implemented.    */
name|Aggregation
name|aggregation
parameter_list|()
function_decl|;
comment|/**    * Returns the return type of the aggregate as {@link org.eigenbase.reltype.RelDataType}.    * This can be helpful to test {@link org.eigenbase.reltype.RelDataType#isNullable()}.    * @return return type of the aggregate as {@link org.eigenbase.reltype.RelDataType}    */
name|RelDataType
name|returnRelType
parameter_list|()
function_decl|;
comment|/**    * Returns the return type of the aggregate as {@link java.lang.reflect.Type}.    * @return return type of the aggregate as {@link java.lang.reflect.Type}    */
name|Type
name|returnType
parameter_list|()
function_decl|;
comment|/**    * Returns the parameter types of the aggregate as {@link org.eigenbase.reltype.RelDataType}.    * This can be helpful to test {@link org.eigenbase.reltype.RelDataType#isNullable()}.    * @return parameter types of the aggregate as {@link org.eigenbase.reltype.RelDataType}    */
name|List
argument_list|<
name|?
extends|extends
name|RelDataType
argument_list|>
name|parameterRelTypes
parameter_list|()
function_decl|;
comment|/**    * Returns the parameter types of the aggregate as {@link java.lang.reflect.Type}.    * @return parameter types of the aggregate as {@link java.lang.reflect.Type}    */
name|List
argument_list|<
name|?
extends|extends
name|Type
argument_list|>
name|parameterTypes
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

