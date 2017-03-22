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
name|adapter
operator|.
name|enumerable
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|sql
operator|.
name|SqlAggFunction
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
name|ImmutableBitSet
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
name|SqlAggFunction
name|aggregation
parameter_list|()
function_decl|;
comment|/**    * Returns the return type of the aggregate as    * {@link org.apache.calcite.rel.type.RelDataType}.    * This can be helpful to test    * {@link org.apache.calcite.rel.type.RelDataType#isNullable()}.    *    * @return return type of the aggregate    */
name|RelDataType
name|returnRelType
parameter_list|()
function_decl|;
comment|/**    * Returns the return type of the aggregate as {@link java.lang.reflect.Type}.    * @return return type of the aggregate as {@link java.lang.reflect.Type}    */
name|Type
name|returnType
parameter_list|()
function_decl|;
comment|/**    * Returns the parameter types of the aggregate as    * {@link org.apache.calcite.rel.type.RelDataType}.    * This can be helpful to test    * {@link org.apache.calcite.rel.type.RelDataType#isNullable()}.    *    * @return Parameter types of the aggregate    */
name|List
argument_list|<
name|?
extends|extends
name|RelDataType
argument_list|>
name|parameterRelTypes
parameter_list|()
function_decl|;
comment|/**    * Returns the parameter types of the aggregate as    * {@link java.lang.reflect.Type}.    *    * @return Parameter types of the aggregate    */
name|List
argument_list|<
name|?
extends|extends
name|Type
argument_list|>
name|parameterTypes
parameter_list|()
function_decl|;
comment|/** Returns the ordinals of the input fields that make up the key. */
name|List
argument_list|<
name|Integer
argument_list|>
name|keyOrdinals
parameter_list|()
function_decl|;
comment|/**    * Returns the types of the group key as    * {@link org.apache.calcite.rel.type.RelDataType}.    */
name|List
argument_list|<
name|?
extends|extends
name|RelDataType
argument_list|>
name|keyRelTypes
parameter_list|()
function_decl|;
comment|/**    * Returns the types of the group key as    * {@link java.lang.reflect.Type}.    */
name|List
argument_list|<
name|?
extends|extends
name|Type
argument_list|>
name|keyTypes
parameter_list|()
function_decl|;
comment|/** Returns the grouping sets we are aggregating on. */
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|groupSets
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End AggContext.java
end_comment

end_unit

