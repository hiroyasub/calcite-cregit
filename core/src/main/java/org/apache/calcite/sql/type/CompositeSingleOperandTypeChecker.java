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
name|util
operator|.
name|Util
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
comment|/**  * Allows multiple  * {@link org.apache.calcite.sql.type.SqlSingleOperandTypeChecker} rules to be  * combined into one rule.  */
end_comment

begin_class
specifier|public
class|class
name|CompositeSingleOperandTypeChecker
extends|extends
name|CompositeOperandTypeChecker
implements|implements
name|SqlSingleOperandTypeChecker
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Package private. Use {@link org.apache.calcite.sql.type.OperandTypes#and},    * {@link org.apache.calcite.sql.type.OperandTypes#or}.    */
name|CompositeSingleOperandTypeChecker
parameter_list|(
name|CompositeOperandTypeChecker
operator|.
name|Composition
name|composition
parameter_list|,
name|ImmutableList
argument_list|<
name|?
extends|extends
name|SqlSingleOperandTypeChecker
argument_list|>
name|allowedRules
parameter_list|,
name|String
name|allowedSignatures
parameter_list|)
block|{
name|super
argument_list|(
name|composition
argument_list|,
name|allowedRules
argument_list|,
name|allowedSignatures
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|ImmutableList
argument_list|<
name|?
extends|extends
name|SqlSingleOperandTypeChecker
argument_list|>
name|getRules
parameter_list|()
block|{
return|return
operator|(
name|ImmutableList
argument_list|<
name|?
extends|extends
name|SqlSingleOperandTypeChecker
argument_list|>
operator|)
name|allowedRules
return|;
block|}
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
assert|assert
name|allowedRules
operator|.
name|size
argument_list|()
operator|>=
literal|1
assert|;
specifier|final
name|ImmutableList
argument_list|<
name|?
extends|extends
name|SqlSingleOperandTypeChecker
argument_list|>
name|rules
init|=
name|getRules
argument_list|()
decl_stmt|;
if|if
condition|(
name|composition
operator|==
name|Composition
operator|.
name|SEQUENCE
condition|)
block|{
return|return
name|rules
operator|.
name|get
argument_list|(
name|iFormalOperand
argument_list|)
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|,
literal|0
argument_list|,
name|throwOnFailure
argument_list|)
return|;
block|}
name|int
name|typeErrorCount
init|=
literal|0
decl_stmt|;
name|boolean
name|throwOnAndFailure
init|=
operator|(
name|composition
operator|==
name|Composition
operator|.
name|AND
operator|)
operator|&&
name|throwOnFailure
decl_stmt|;
for|for
control|(
name|SqlSingleOperandTypeChecker
name|rule
range|:
name|rules
control|)
block|{
if|if
condition|(
operator|!
name|rule
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|,
name|iFormalOperand
argument_list|,
name|throwOnAndFailure
argument_list|)
condition|)
block|{
name|typeErrorCount
operator|++
expr_stmt|;
block|}
block|}
name|boolean
name|ret
decl_stmt|;
switch|switch
condition|(
name|composition
condition|)
block|{
case|case
name|AND
case|:
name|ret
operator|=
name|typeErrorCount
operator|==
literal|0
expr_stmt|;
break|break;
case|case
name|OR
case|:
name|ret
operator|=
name|typeErrorCount
operator|<
name|allowedRules
operator|.
name|size
argument_list|()
expr_stmt|;
break|break;
default|default:
comment|// should never come here
throw|throw
name|Util
operator|.
name|unexpected
argument_list|(
name|composition
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|ret
operator|&&
name|throwOnFailure
condition|)
block|{
comment|// In the case of a composite OR, we want to throw an error
comment|// describing in more detail what the problem was, hence doing the
comment|// loop again.
for|for
control|(
name|SqlSingleOperandTypeChecker
name|rule
range|:
name|rules
control|)
block|{
name|rule
operator|.
name|checkSingleOperandType
argument_list|(
name|callBinding
argument_list|,
name|node
argument_list|,
name|iFormalOperand
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// If no exception thrown, just throw a generic validation signature
comment|// error.
throw|throw
name|callBinding
operator|.
name|newValidationSignatureError
argument_list|()
throw|;
block|}
return|return
name|ret
return|;
block|}
block|}
end_class

begin_comment
comment|// End CompositeSingleOperandTypeChecker.java
end_comment

end_unit

