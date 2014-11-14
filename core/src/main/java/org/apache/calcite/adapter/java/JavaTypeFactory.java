begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
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
comment|/**  * Type factory that can register Java classes as record types.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JavaTypeFactory
extends|extends
name|RelDataTypeFactory
block|{
comment|/**    * Creates a record type based upon the public fields of a Java class.    *    * @param clazz Java class    * @return Record type that remembers its Java class    */
name|RelDataType
name|createStructType
parameter_list|(
name|Class
name|clazz
parameter_list|)
function_decl|;
comment|/**    * Creates a type, deducing whether a record, scalar or primitive type    * is needed.    *    * @param type Java type, such as a {@link Class}    * @return Record or scalar type    */
name|RelDataType
name|createType
parameter_list|(
name|Type
name|type
parameter_list|)
function_decl|;
name|Type
name|getJavaClass
parameter_list|(
name|RelDataType
name|type
parameter_list|)
function_decl|;
comment|/** Creates a synthetic Java class whose fields have the given Java    * types. */
name|Type
name|createSyntheticType
parameter_list|(
name|List
argument_list|<
name|Type
argument_list|>
name|types
parameter_list|)
function_decl|;
comment|/** Converts a type in Java format to a SQL-oriented type. */
name|RelDataType
name|toSql
parameter_list|(
name|RelDataType
name|type
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End JavaTypeFactory.java
end_comment

end_unit

