begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|resource
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Parameter type-checking strategy for a set operator (UNION, INTERSECT,  * EXCEPT).  *  *<p>Both arguments must be records with the same number of fields, and the  * fields must be union-compatible.  *  * @author Jack Frost  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SetopOperandTypeChecker
implements|implements
name|SqlOperandTypeChecker
block|{
comment|//~ Methods ----------------------------------------------------------------
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
name|Util
operator|.
name|permAssert
argument_list|(
name|argType
operator|.
name|isStruct
argument_list|()
argument_list|,
literal|"setop arg must be a struct"
argument_list|)
expr_stmt|;
comment|// Each operand must have the same number of columns.
specifier|final
name|RelDataTypeField
index|[]
name|fields
init|=
name|argType
operator|.
name|getFields
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
name|length
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
name|fields
operator|.
name|length
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
name|getCall
argument_list|()
operator|.
name|getOperands
argument_list|()
index|[
name|i
index|]
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
name|node
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|ColumnCountMismatchInSetop
operator|.
name|ex
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
name|RelDataType
index|[]
name|colTypes
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
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|argTypes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|RelDataTypeField
name|field
init|=
name|argTypes
index|[
name|j
index|]
operator|.
name|getFields
argument_list|()
index|[
name|i
index|]
decl_stmt|;
name|colTypes
index|[
name|j
index|]
operator|=
name|field
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
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
name|colTypes
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
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
name|getCall
argument_list|()
operator|.
name|operands
index|[
literal|0
index|]
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
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|ColumnTypeMismatchInSetop
operator|.
name|ex
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
return|return
literal|true
return|;
block|}
specifier|public
name|SqlOperandCountRange
name|getOperandCountRange
parameter_list|()
block|{
return|return
name|SqlOperandCountRange
operator|.
name|Two
return|;
block|}
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
comment|// todo: Wael, please review.
block|}
block|}
end_class

begin_comment
comment|// End SetopOperandTypeChecker.java
end_comment

end_unit

