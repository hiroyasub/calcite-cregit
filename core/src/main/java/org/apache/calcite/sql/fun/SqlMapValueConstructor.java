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
name|fun
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlCallBinding
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
name|SqlKind
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
name|SqlTypeUtil
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
name|Pair
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
name|Util
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Definition of the MAP constructor,  *<code>MAP [&lt;key&gt;,&lt;value&gt;, ...]</code>.  *  *<p>This is an extension to standard SQL.</p>  */
end_comment

begin_class
specifier|public
class|class
name|SqlMapValueConstructor
extends|extends
name|SqlMultisetValueConstructor
block|{
specifier|public
name|SqlMapValueConstructor
parameter_list|()
block|{
name|super
argument_list|(
literal|"MAP"
argument_list|,
name|SqlKind
operator|.
name|MAP_VALUE_CONSTRUCTOR
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
name|Pair
argument_list|<
name|RelDataType
argument_list|,
name|RelDataType
argument_list|>
name|type
init|=
name|getComponentTypes
argument_list|(
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|opBinding
operator|.
name|collectOperandTypes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|type
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|SqlTypeUtil
operator|.
name|createMapType
argument_list|(
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|type
operator|.
name|left
argument_list|,
name|type
operator|.
name|right
argument_list|,
literal|false
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|checkOperandTypes
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
init|=
name|SqlTypeUtil
operator|.
name|deriveType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|operands
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|argTypes
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationError
argument_list|(
name|RESOURCE
operator|.
name|mapRequiresTwoOrMoreArgs
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|argTypes
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|>
literal|0
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationError
argument_list|(
name|RESOURCE
operator|.
name|mapRequiresEvenArgCount
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Pair
argument_list|<
name|RelDataType
argument_list|,
name|RelDataType
argument_list|>
name|componentType
init|=
name|getComponentTypes
argument_list|(
name|callBinding
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|argTypes
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|componentType
operator|.
name|left
operator|||
literal|null
operator|==
name|componentType
operator|.
name|right
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|newValidationError
argument_list|(
name|RESOURCE
operator|.
name|needSameTypeParameter
argument_list|()
argument_list|)
throw|;
block|}
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|Pair
argument_list|<
name|RelDataType
argument_list|,
name|RelDataType
argument_list|>
name|getComponentTypes
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
parameter_list|)
block|{
return|return
name|Pair
operator|.
name|of
argument_list|(
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Util
operator|.
name|quotientList
argument_list|(
name|argTypes
argument_list|,
literal|2
argument_list|,
literal|0
argument_list|)
argument_list|)
argument_list|,
name|typeFactory
operator|.
name|leastRestrictive
argument_list|(
name|Util
operator|.
name|quotientList
argument_list|(
name|argTypes
argument_list|,
literal|2
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

