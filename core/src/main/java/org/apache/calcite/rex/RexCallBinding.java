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
name|rex
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
name|RelCollation
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
name|RelFieldCollation
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
name|runtime
operator|.
name|CalciteException
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
name|runtime
operator|.
name|Resources
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
name|sql
operator|.
name|validate
operator|.
name|SqlMonotonicity
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
name|SqlValidatorException
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
comment|/**  *<code>RexCallBinding</code> implements {@link SqlOperatorBinding} by  * referring to an underlying collection of {@link RexNode} operands.  */
end_comment

begin_class
specifier|public
class|class
name|RexCallBinding
extends|extends
name|SqlOperatorBinding
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|inputCollations
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RexCallBinding
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlOperator
name|sqlOperator
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|inputCollations
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|,
name|sqlOperator
argument_list|)
expr_stmt|;
name|this
operator|.
name|operands
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|operands
argument_list|)
expr_stmt|;
name|this
operator|.
name|inputCollations
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|inputCollations
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a binding of the appropriate type. */
specifier|public
specifier|static
name|RexCallBinding
name|create
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RexCall
name|call
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|inputCollations
parameter_list|)
block|{
switch|switch
condition|(
name|call
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|CAST
case|:
return|return
operator|new
name|RexCastCallBinding
argument_list|(
name|typeFactory
argument_list|,
name|call
operator|.
name|getOperator
argument_list|()
argument_list|,
name|call
operator|.
name|getOperands
argument_list|()
argument_list|,
name|call
operator|.
name|getType
argument_list|()
argument_list|,
name|inputCollations
argument_list|)
return|;
block|}
return|return
operator|new
name|RexCallBinding
argument_list|(
name|typeFactory
argument_list|,
name|call
operator|.
name|getOperator
argument_list|()
argument_list|,
name|call
operator|.
name|getOperands
argument_list|()
argument_list|,
name|inputCollations
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
name|String
name|getStringLiteralOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|RexLiteral
operator|.
name|stringValue
argument_list|(
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
name|int
name|getIntLiteralOperand
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|getOperandLiteralValue
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
block|{
specifier|final
name|RexNode
name|node
init|=
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|RexLiteral
condition|)
block|{
return|return
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|getValueAs
argument_list|(
name|clazz
argument_list|)
return|;
block|}
return|return
name|clazz
operator|.
name|cast
argument_list|(
name|RexLiteral
operator|.
name|value
argument_list|(
name|node
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlMonotonicity
name|getOperandMonotonicity
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|RexNode
name|operand
init|=
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|operand
operator|instanceof
name|RexInputRef
condition|)
block|{
for|for
control|(
name|RelCollation
name|ic
range|:
name|inputCollations
control|)
block|{
if|if
condition|(
name|ic
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|RelFieldCollation
name|rfc
range|:
name|ic
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
if|if
condition|(
name|rfc
operator|.
name|getFieldIndex
argument_list|()
operator|==
operator|(
operator|(
name|RexInputRef
operator|)
name|operand
operator|)
operator|.
name|getIndex
argument_list|()
condition|)
block|{
return|return
name|rfc
operator|.
name|direction
operator|.
name|monotonicity
argument_list|()
return|;
comment|// TODO: Is it possible to have more than one RelFieldCollation for a RexInputRef?
block|}
block|}
block|}
block|}
if|else if
condition|(
name|operand
operator|instanceof
name|RexCall
condition|)
block|{
specifier|final
name|RexCallBinding
name|binding
init|=
name|RexCallBinding
operator|.
name|create
argument_list|(
name|typeFactory
argument_list|,
operator|(
name|RexCall
operator|)
name|operand
argument_list|,
name|inputCollations
argument_list|)
decl_stmt|;
return|return
operator|(
operator|(
name|RexCall
operator|)
name|operand
operator|)
operator|.
name|getOperator
argument_list|()
operator|.
name|getMonotonicity
argument_list|(
name|binding
argument_list|)
return|;
block|}
return|return
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOperandNull
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|boolean
name|allowCast
parameter_list|)
block|{
return|return
name|RexUtil
operator|.
name|isNullLiteral
argument_list|(
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
argument_list|,
name|allowCast
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isOperandLiteral
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|boolean
name|allowCast
parameter_list|)
block|{
return|return
name|RexUtil
operator|.
name|isLiteral
argument_list|(
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
argument_list|,
name|allowCast
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
parameter_list|()
block|{
return|return
name|operands
return|;
block|}
comment|// implement SqlOperatorBinding
annotation|@
name|Override
specifier|public
name|int
name|getOperandCount
parameter_list|()
block|{
return|return
name|operands
operator|.
name|size
argument_list|()
return|;
block|}
comment|// implement SqlOperatorBinding
annotation|@
name|Override
specifier|public
name|RelDataType
name|getOperandType
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
name|operands
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
operator|.
name|getType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|CalciteException
name|newError
parameter_list|(
name|Resources
operator|.
name|ExInst
argument_list|<
name|SqlValidatorException
argument_list|>
name|e
parameter_list|)
block|{
return|return
name|SqlUtil
operator|.
name|newContextException
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/** To be compatible with {@code SqlCall}, CAST needs to pretend that it    * has two arguments, the second of which is the target type. */
specifier|private
specifier|static
class|class
name|RexCastCallBinding
extends|extends
name|RexCallBinding
block|{
specifier|private
specifier|final
name|RelDataType
name|type
decl_stmt|;
name|RexCastCallBinding
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlOperator
name|sqlOperator
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|operands
parameter_list|,
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|inputCollations
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|,
name|sqlOperator
argument_list|,
name|operands
argument_list|,
name|inputCollations
argument_list|)
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|getOperandType
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
if|if
condition|(
name|ordinal
operator|==
literal|1
condition|)
block|{
return|return
name|type
return|;
block|}
return|return
name|super
operator|.
name|getOperandType
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

