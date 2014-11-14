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
name|parser
operator|.
name|SqlParserPos
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
name|UnmodifiableArrayList
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
comment|/**  * Implementation of {@link SqlCall} that keeps its operands in an array.  */
end_comment

begin_class
specifier|public
class|class
name|SqlBasicCall
extends|extends
name|SqlCall
block|{
specifier|private
name|SqlOperator
name|operator
decl_stmt|;
specifier|public
specifier|final
name|SqlNode
index|[]
name|operands
decl_stmt|;
specifier|private
specifier|final
name|SqlLiteral
name|functionQuantifier
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|expanded
decl_stmt|;
specifier|public
name|SqlBasicCall
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|)
block|{
name|this
argument_list|(
name|operator
argument_list|,
name|operands
argument_list|,
name|pos
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|SqlBasicCall
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|boolean
name|expanded
parameter_list|,
name|SqlLiteral
name|functionQualifier
parameter_list|)
block|{
name|super
argument_list|(
name|pos
argument_list|)
expr_stmt|;
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
name|this
operator|.
name|operands
operator|=
name|operands
expr_stmt|;
name|this
operator|.
name|expanded
operator|=
name|expanded
expr_stmt|;
name|this
operator|.
name|functionQuantifier
operator|=
name|functionQualifier
expr_stmt|;
block|}
specifier|public
name|SqlKind
name|getKind
parameter_list|()
block|{
return|return
name|operator
operator|.
name|getKind
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isExpanded
parameter_list|()
block|{
return|return
name|expanded
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOperand
parameter_list|(
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
name|operands
index|[
name|i
index|]
operator|=
name|operand
expr_stmt|;
block|}
specifier|public
name|void
name|setOperator
parameter_list|(
name|SqlOperator
name|operator
parameter_list|)
block|{
name|this
operator|.
name|operator
operator|=
name|operator
expr_stmt|;
block|}
specifier|public
name|SqlOperator
name|getOperator
parameter_list|()
block|{
return|return
name|operator
return|;
block|}
specifier|public
name|SqlNode
index|[]
name|getOperands
parameter_list|()
block|{
return|return
name|operands
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlNode
argument_list|>
name|getOperandList
parameter_list|()
block|{
return|return
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|operands
argument_list|)
return|;
comment|// not immutable, but quick
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
parameter_list|<
name|S
extends|extends
name|SqlNode
parameter_list|>
name|S
name|operand
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
operator|(
name|S
operator|)
name|operands
index|[
name|i
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|operandCount
parameter_list|()
block|{
return|return
name|operands
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlLiteral
name|getFunctionQuantifier
parameter_list|()
block|{
return|return
name|functionQuantifier
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlBasicCall.java
end_comment

end_unit

