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
name|sql
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
comment|/**  * Extension to {@link SqlOperandTypeChecker} that also provides  * names and types of particular operands.  *  *<p>It is intended for user-defined functions (UDFs), and therefore the number  * of parameters is fixed.  *  * @see OperandTypes  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlOperandMetadata
extends|extends
name|SqlOperandTypeChecker
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/** Returns the types of the parameters. */
name|List
argument_list|<
name|RelDataType
argument_list|>
name|paramTypes
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
function_decl|;
comment|/** Returns the names of the parameters. */
name|List
argument_list|<
name|String
argument_list|>
name|paramNames
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

