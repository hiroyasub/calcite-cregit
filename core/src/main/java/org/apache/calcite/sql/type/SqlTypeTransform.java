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
name|sql
operator|.
name|SqlOperatorBinding
import|;
end_import

begin_comment
comment|/**  * Strategy to transform one type to another. The transformation is dependent on  * the implemented strategy object and in the general case is a function of the  * type and the other operands. Can not be used by itself. Must be used in an  * object of type {@link SqlTypeTransformCascade}.  *  *<p>This class is an example of the  * {@link org.apache.calcite.util.Glossary#STRATEGY_PATTERN strategy pattern}.  *  * @see SqlTypeTransforms  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlTypeTransform
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Transforms a type.    *    * @param opBinding       call context in which transformation is being    *                        performed    * @param typeToTransform type to be transformed, never null    * @return transformed type, never null    */
name|RelDataType
name|transformType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|,
name|RelDataType
name|typeToTransform
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

