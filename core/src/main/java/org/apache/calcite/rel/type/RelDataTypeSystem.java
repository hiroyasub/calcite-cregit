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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_comment
comment|/**  * Type system.  *  *<p>Provides behaviors concerning type limits and behaviors. For example,  * in the default system, a DECIMAL can have maximum precision 19, but Hive  * overrides to 38.  *  *<p>The default implementation is {@link #DEFAULT}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDataTypeSystem
block|{
comment|/** Default type system. */
name|RelDataTypeSystem
name|DEFAULT
init|=
operator|new
name|RelDataTypeSystemImpl
argument_list|()
block|{ }
decl_stmt|;
comment|/** Returns the maximum scale of a given type. */
name|int
name|getMaxScale
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/**    * Returns default precision for this type if supported, otherwise -1 if    * precision is either unsupported or must be specified explicitly.    *    * @return Default precision    */
name|int
name|getDefaultPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/**    * Returns the maximum precision (or length) allowed for this type, or -1 if    * precision/length are not applicable for this type.    *    * @return Maximum allowed precision    */
name|int
name|getMaxPrecision
parameter_list|(
name|SqlTypeName
name|typeName
parameter_list|)
function_decl|;
comment|/** Returns the maximum scale of a NUMERIC or DECIMAL type. */
name|int
name|getMaxNumericScale
parameter_list|()
function_decl|;
comment|/** Returns the maximum precision of a NUMERIC or DECIMAL type. */
name|int
name|getMaxNumericPrecision
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelDataTypeSystem.java
end_comment

end_unit

