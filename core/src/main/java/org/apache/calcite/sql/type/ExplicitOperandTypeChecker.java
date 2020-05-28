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
name|RelDataTypeField
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
name|SqlOperandCountRange
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
name|SqlOperator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Parameter type-checking strategy for Explicit Type.  */
end_comment

begin_class
specifier|public
class|class
name|ExplicitOperandTypeChecker
implements|implements
name|SqlOperandTypeChecker
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
specifier|public
name|ExplicitOperandTypeChecker
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOptional
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
literal|false
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
name|List
argument_list|<
name|SqlTypeFamily
argument_list|>
name|families
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldList
init|=
name|type
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fieldList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SqlTypeName
name|sqlTypeName
init|=
name|field
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
if|if
condition|(
name|sqlTypeName
operator|==
name|SqlTypeName
operator|.
name|ROW
condition|)
block|{
if|if
condition|(
name|field
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|callBinding
operator|.
name|getOperandType
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|families
operator|.
name|add
argument_list|(
name|SqlTypeFamily
operator|.
name|ANY
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|families
operator|.
name|add
argument_list|(
name|requireNonNull
argument_list|(
name|sqlTypeName
operator|.
name|getFamily
argument_list|()
argument_list|,
parameter_list|()
lambda|->
literal|"keyType.getSqlTypeName().getFamily() null, type is "
operator|+
name|sqlTypeName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|OperandTypes
operator|.
name|family
argument_list|(
name|families
argument_list|)
operator|.
name|checkOperandTypes
argument_list|(
name|callBinding
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRanges
operator|.
name|of
argument_list|(
name|type
operator|.
name|getFieldCount
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAllowedSignatures
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|String
name|opName
parameter_list|)
block|{
return|return
literal|"<TYPE> "
operator|+
name|opName
operator|+
literal|"<TYPE>"
return|;
block|}
annotation|@
name|Override
specifier|public
name|Consistency
name|getConsistency
parameter_list|()
block|{
return|return
name|Consistency
operator|.
name|NONE
return|;
block|}
block|}
end_class

end_unit

