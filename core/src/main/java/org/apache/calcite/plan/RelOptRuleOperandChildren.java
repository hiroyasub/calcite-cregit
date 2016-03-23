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
name|plan
package|;
end_package

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
comment|/**  * Children of a {@link org.apache.calcite.plan.RelOptRuleOperand} and the  * policy for matching them.  *  *<p>Often created by calling one of the following methods:  * {@link RelOptRule#some},  * {@link RelOptRule#none},  * {@link RelOptRule#any},  * {@link RelOptRule#unordered},</p>  */
end_comment

begin_class
specifier|public
class|class
name|RelOptRuleOperandChildren
block|{
specifier|static
specifier|final
name|RelOptRuleOperandChildren
name|ANY_CHILDREN
init|=
operator|new
name|RelOptRuleOperandChildren
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|ANY
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelOptRuleOperand
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|RelOptRuleOperandChildren
name|LEAF_CHILDREN
init|=
operator|new
name|RelOptRuleOperandChildren
argument_list|(
name|RelOptRuleOperandChildPolicy
operator|.
name|LEAF
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelOptRuleOperand
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelOptRuleOperandChildPolicy
name|policy
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|operands
decl_stmt|;
specifier|public
name|RelOptRuleOperandChildren
parameter_list|(
name|RelOptRuleOperandChildPolicy
name|policy
parameter_list|,
name|List
argument_list|<
name|RelOptRuleOperand
argument_list|>
name|operands
parameter_list|)
block|{
name|this
operator|.
name|policy
operator|=
name|policy
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
block|}
block|}
end_class

begin_comment
comment|// End RelOptRuleOperandChildren.java
end_comment

end_unit

