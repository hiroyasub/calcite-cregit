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
name|rel
operator|.
name|rules
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
name|plan
operator|.
name|Convention
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
name|plan
operator|.
name|RelOptRule
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
name|plan
operator|.
name|RelOptRuleCall
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
name|plan
operator|.
name|RelOptUtil
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
name|RelNode
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
name|core
operator|.
name|RelFactories
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
name|tools
operator|.
name|RelBuilderFactory
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

begin_comment
comment|/**  * CoerceInputsRule pre-casts inputs to a particular type. This can be used to  * assist operator implementations which impose requirements on their input  * types.  */
end_comment

begin_class
specifier|public
class|class
name|CoerceInputsRule
extends|extends
name|RelOptRule
implements|implements
name|TransformationRule
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Class
name|consumerRelClass
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|coerceNames
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|CoerceInputsRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|consumerRelClass
parameter_list|,
name|boolean
name|coerceNames
parameter_list|)
block|{
name|this
argument_list|(
name|consumerRelClass
argument_list|,
name|coerceNames
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a CoerceInputsRule.    *    * @param consumerRelClass  Class of RelNode that will consume the inputs    * @param coerceNames       If true, coerce names and types; if false, coerce    *                          type only    * @param relBuilderFactory Builder for relational expressions    */
specifier|public
name|CoerceInputsRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
name|consumerRelClass
parameter_list|,
name|boolean
name|coerceNames
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|consumerRelClass
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"CoerceInputsRule:"
operator|+
name|consumerRelClass
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|consumerRelClass
operator|=
name|consumerRelClass
expr_stmt|;
name|this
operator|.
name|coerceNames
operator|=
name|coerceNames
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|Convention
name|getOutConvention
parameter_list|()
block|{
return|return
name|Convention
operator|.
name|NONE
return|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|RelNode
name|consumerRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|consumerRel
operator|.
name|getClass
argument_list|()
operator|!=
name|consumerRelClass
condition|)
block|{
comment|// require exact match on type
return|return;
block|}
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|consumerRel
operator|.
name|getInputs
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|inputs
argument_list|)
decl_stmt|;
name|boolean
name|coerce
init|=
literal|false
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
name|inputs
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|RelDataType
name|expectedType
init|=
name|consumerRel
operator|.
name|getExpectedInputRowType
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RelNode
name|input
init|=
name|inputs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|RelNode
name|newInput
init|=
name|RelOptUtil
operator|.
name|createCastRel
argument_list|(
name|input
argument_list|,
name|expectedType
argument_list|,
name|coerceNames
argument_list|)
decl_stmt|;
if|if
condition|(
name|newInput
operator|!=
name|input
condition|)
block|{
name|newInputs
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|newInput
argument_list|)
expr_stmt|;
name|coerce
operator|=
literal|true
expr_stmt|;
block|}
assert|assert
name|RelOptUtil
operator|.
name|areRowTypesEqual
argument_list|(
name|newInputs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getRowType
argument_list|()
argument_list|,
name|expectedType
argument_list|,
name|coerceNames
argument_list|)
assert|;
block|}
if|if
condition|(
operator|!
name|coerce
condition|)
block|{
return|return;
block|}
name|RelNode
name|newConsumerRel
init|=
name|consumerRel
operator|.
name|copy
argument_list|(
name|consumerRel
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newInputs
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newConsumerRel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

