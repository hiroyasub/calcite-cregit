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
name|validate
operator|.
name|implicit
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
name|RelDataTypeFactory
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
name|validate
operator|.
name|SqlValidator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apiguardian
operator|.
name|api
operator|.
name|API
import|;
end_import

begin_comment
comment|/** Factory for {@link TypeCoercion} objects.  *  *<p>A type coercion factory allows you to include custom rules of  * implicit type coercion. Usually you should inherit the {@link TypeCoercionImpl}  * and override the methods that you want to customize.  *  *<p>This interface is experimental and would change without notice.  *  * @see SqlValidator.Config#withTypeCoercionFactory  */
end_comment

begin_interface
annotation|@
name|API
argument_list|(
name|status
operator|=
name|API
operator|.
name|Status
operator|.
name|EXPERIMENTAL
argument_list|,
name|since
operator|=
literal|"1.23"
argument_list|)
specifier|public
interface|interface
name|TypeCoercionFactory
block|{
comment|/**    * Creates a TypeCoercion.    *    * @param typeFactory Type factory    * @param validator   SQL validator    */
name|TypeCoercion
name|create
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlValidator
name|validator
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

