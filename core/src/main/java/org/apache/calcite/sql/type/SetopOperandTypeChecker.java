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
name|SqlNode
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlSelect
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
name|SqlUtil
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
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|validate
operator|.
name|implicit
operator|.
name|TypeCoercion
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
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
comment|/**  * Parameter type-checking strategy for a set operator (UNION, INTERSECT,  * EXCEPT).  *  *<p>Both arguments must be records with the same number of fields, and the  * fields must be union-compatible.  */
end_comment

begin_class
specifier|public
class|class
name|SetopOperandTypeChecker
implements|implements
name|SqlOperandTypeChecker
block|{
comment|//~ Methods ----------------------------------------------------------------
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
assert|assert
name|callBinding
operator|.
name|getOperandCount
argument_list|()
operator|==
literal|2
operator|:
literal|"setops are binary (for now)"
assert|;
specifier|final
name|RelDataType
index|[]
name|argTypes
init|=
operator|new
name|RelDataType
index|[
name|callBinding
operator|.
name|getOperandCount
argument_list|()
index|]
decl_stmt|;
name|int
name|colCount
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|SqlValidator
name|validator
init|=
name|callBinding
operator|.
name|getValidator
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
name|argTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RelDataType
name|argType
init|=
name|argTypes
index|[
name|i
index|]
operator|=
name|callBinding
operator|.
name|getOperandType
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|argType
operator|.
name|isStruct
argument_list|()
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"setop arg must be a struct"
argument_list|)
throw|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
comment|// Each operand must have the same number of columns.
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|argType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|colCount
operator|=
name|fields
operator|.
name|size
argument_list|()
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|fields
operator|.
name|size
argument_list|()
operator|!=
name|colCount
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
name|SqlNode
name|node
init|=
name|callBinding
operator|.
name|operand
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|SqlSelect
condition|)
block|{
name|node
operator|=
operator|(
operator|(
name|SqlSelect
operator|)
name|node
operator|)
operator|.
name|getSelectList
argument_list|()
expr_stmt|;
block|}
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|requireNonNull
argument_list|(
name|node
argument_list|,
literal|"node"
argument_list|)
argument_list|,
name|RESOURCE
operator|.
name|columnCountMismatchInSetop
argument_list|(
name|callBinding
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
comment|// The columns must be pairwise union compatible. For each column
comment|// ordinal, form a 'slice' containing the types of the ordinal'th
comment|// column j.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|colCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|i2
init|=
name|i
decl_stmt|;
comment|// Get the ith column data types list for every record type fields pair.
comment|// For example,
comment|// for record type (f0: INT, f1: BIGINT, f2: VARCHAR)
comment|// and record type (f3: VARCHAR, f4: DECIMAL, f5: INT),
comment|// the list would be [[INT, VARCHAR], [BIGINT, DECIMAL], [VARCHAR, INT]].
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|columnIthTypes
init|=
operator|new
name|AbstractList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|RelDataType
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|argTypes
index|[
name|index
index|]
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|i2
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|argTypes
operator|.
name|length
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|callBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|leastRestrictive
argument_list|(
name|columnIthTypes
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|boolean
name|coerced
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|callBinding
operator|.
name|isTypeCoercionEnabled
argument_list|()
condition|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|callBinding
operator|.
name|getOperandCount
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|TypeCoercion
name|typeCoercion
init|=
name|validator
operator|.
name|getTypeCoercion
argument_list|()
decl_stmt|;
name|RelDataType
name|widenType
init|=
name|typeCoercion
operator|.
name|getWiderTypeFor
argument_list|(
name|columnIthTypes
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|widenType
condition|)
block|{
name|coerced
operator|=
name|typeCoercion
operator|.
name|rowTypeCoercion
argument_list|(
name|callBinding
operator|.
name|getScope
argument_list|()
argument_list|,
name|callBinding
operator|.
name|operand
argument_list|(
name|j
argument_list|)
argument_list|,
name|i
argument_list|,
name|widenType
argument_list|)
operator|||
name|coerced
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|coerced
condition|)
block|{
if|if
condition|(
name|throwOnFailure
condition|)
block|{
name|SqlNode
name|field
init|=
name|SqlUtil
operator|.
name|getSelectListItem
argument_list|(
name|callBinding
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|,
name|i
argument_list|)
decl_stmt|;
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|field
argument_list|,
name|RESOURCE
operator|.
name|columnTypeMismatchInSetop
argument_list|(
name|i
operator|+
literal|1
argument_list|,
comment|// 1-based
name|callBinding
operator|.
name|getOperator
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
return|return
literal|true
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
literal|2
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
literal|"{0} "
operator|+
name|opName
operator|+
literal|" {1}"
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

