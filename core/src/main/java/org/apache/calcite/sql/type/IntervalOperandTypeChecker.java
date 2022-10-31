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
name|avatica
operator|.
name|util
operator|.
name|TimeUnitRange
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
name|SqlIntervalQualifier
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
name|util
operator|.
name|Static
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
name|ImmutableSet
import|;
end_import

begin_comment
comment|/**  * Parameter type-checking strategy whether the operand must be an interval.  */
end_comment

begin_class
specifier|public
class|class
name|IntervalOperandTypeChecker
implements|implements
name|SqlSingleOperandTypeChecker
block|{
specifier|private
specifier|final
name|ImmutableSet
argument_list|<
name|TimeUnitRange
argument_list|>
name|unitSet
decl_stmt|;
name|IntervalOperandTypeChecker
parameter_list|(
name|ImmutableSet
argument_list|<
name|TimeUnitRange
argument_list|>
name|unitSet
parameter_list|)
block|{
name|this
operator|.
name|unitSet
operator|=
name|unitSet
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|checkSingleOperandType
parameter_list|(
name|SqlCallBinding
name|callBinding
parameter_list|,
name|SqlNode
name|node
parameter_list|,
name|int
name|iFormalOperand
parameter_list|,
name|boolean
name|throwOnFailure
parameter_list|)
block|{
specifier|final
name|SqlNode
name|operand
init|=
name|callBinding
operator|.
name|operand
argument_list|(
name|iFormalOperand
argument_list|)
decl_stmt|;
if|if
condition|(
name|operand
operator|instanceof
name|SqlIntervalQualifier
condition|)
block|{
specifier|final
name|SqlIntervalQualifier
name|interval
init|=
operator|(
name|SqlIntervalQualifier
operator|)
name|operand
decl_stmt|;
if|if
condition|(
name|unitSet
operator|.
name|contains
argument_list|(
name|interval
operator|.
name|timeUnitRange
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|throwOnFailure
condition|)
block|{
throw|throw
name|callBinding
operator|.
name|getValidator
argument_list|()
operator|.
name|newValidationError
argument_list|(
name|operand
argument_list|,
name|Static
operator|.
name|RESOURCE
operator|.
name|invalidTimeFrame
argument_list|(
name|interval
operator|.
name|timeUnitRange
operator|.
name|name
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
return|return
literal|false
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
literal|"<INTERVAL>"
return|;
block|}
block|}
end_class

end_unit
