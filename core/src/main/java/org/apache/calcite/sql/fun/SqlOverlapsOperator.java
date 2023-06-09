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
name|sql
operator|.
name|SqlBinaryOperator
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
name|SqlCall
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
name|SqlWriter
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
name|InferTypes
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
name|OperandTypes
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
name|ReturnTypes
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
name|SqlOperandCountRanges
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
name|SqlSingleOperandTypeChecker
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  * SqlOverlapsOperator represents the SQL:1999 standard {@code OVERLAPS}  * function. Determines whether two anchored time intervals overlap.  */
end_comment

begin_class
specifier|public
class|class
name|SqlOverlapsOperator
extends|extends
name|SqlBinaryOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
name|SqlOverlapsOperator
parameter_list|(
name|SqlKind
name|kind
parameter_list|)
block|{
name|super
argument_list|(
name|kind
operator|.
name|sql
argument_list|,
name|kind
argument_list|,
literal|30
argument_list|,
literal|true
argument_list|,
name|ReturnTypes
operator|.
name|BOOLEAN_NULLABLE
argument_list|,
name|InferTypes
operator|.
name|FIRST_KNOWN
argument_list|,
name|OperandTypes
operator|.
name|sequence
argument_list|(
literal|"'<PERIOD> "
operator|+
name|kind
operator|.
name|sql
operator|+
literal|"<PERIOD>'"
argument_list|,
name|OperandTypes
operator|.
name|PERIOD
argument_list|,
name|OperandTypes
operator|.
name|PERIOD
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
name|SqlWriter
operator|.
name|FrameTypeEnum
operator|.
name|SIMPLE
argument_list|)
decl_stmt|;
name|arg
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|arg
argument_list|(
name|writer
argument_list|,
name|call
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
name|void
name|arg
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|,
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|SqlUtil
operator|.
name|isCallTo
argument_list|(
name|call
operator|.
name|operand
argument_list|(
name|i
argument_list|)
argument_list|,
name|SqlStdOperatorTable
operator|.
name|ROW
argument_list|)
condition|)
block|{
name|SqlCall
name|row
init|=
name|call
operator|.
name|operand
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"PERIOD"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|"("
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|row
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|","
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|row
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|sep
argument_list|(
literal|")"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|call
operator|.
name|operand
argument_list|(
name|i
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
block|}
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
name|String
name|opName
parameter_list|)
block|{
specifier|final
name|String
name|d
init|=
literal|"DATETIME"
decl_stmt|;
specifier|final
name|String
name|i
init|=
literal|"INTERVAL"
decl_stmt|;
name|String
index|[]
name|typeNames
init|=
block|{
name|d
block|,
name|d
block|,
name|d
block|,
name|i
block|,
name|i
block|,
name|d
block|,
name|i
block|,
name|i
block|}
decl_stmt|;
name|StringBuilder
name|ret
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|y
init|=
literal|0
init|;
name|y
operator|<
name|typeNames
operator|.
name|length
condition|;
name|y
operator|+=
literal|2
control|)
block|{
if|if
condition|(
name|y
operator|>
literal|0
condition|)
block|{
name|ret
operator|.
name|append
argument_list|(
name|NL
argument_list|)
expr_stmt|;
block|}
name|ret
operator|.
name|append
argument_list|(
name|SqlUtil
operator|.
name|getAliasedSignature
argument_list|(
name|this
argument_list|,
name|opName
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|d
argument_list|,
name|typeNames
index|[
name|y
index|]
argument_list|,
name|d
argument_list|,
name|typeNames
index|[
name|y
operator|+
literal|1
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
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
if|if
condition|(
operator|!
name|OperandTypes
operator|.
name|PERIOD
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|SqlSingleOperandTypeChecker
name|rightChecker
decl_stmt|;
switch|switch
condition|(
name|kind
condition|)
block|{
case|case
name|CONTAINS
case|:
name|rightChecker
operator|=
name|OperandTypes
operator|.
name|PERIOD_OR_DATETIME
expr_stmt|;
break|break;
default|default:
name|rightChecker
operator|=
name|OperandTypes
operator|.
name|PERIOD
expr_stmt|;
break|break;
block|}
if|if
condition|(
operator|!
name|rightChecker
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|callBinding
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|RelDataType
name|t0
init|=
name|callBinding
operator|.
name|getOperandType
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|t1
init|=
name|callBinding
operator|.
name|getOperandType
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isDatetime
argument_list|(
name|t1
argument_list|)
condition|)
block|{
specifier|final
name|RelDataType
name|t00
init|=
name|t0
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|t10
init|=
name|t1
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|sameNamedType
argument_list|(
name|t00
argument_list|,
name|t10
argument_list|)
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
name|newValidationSignatureError
argument_list|()
throw|;
block|}
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

