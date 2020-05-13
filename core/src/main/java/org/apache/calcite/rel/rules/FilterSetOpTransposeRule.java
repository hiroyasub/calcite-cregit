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
name|Contexts
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
name|Filter
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
name|core
operator|.
name|SetOp
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexNode
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
name|RelBuilder
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
comment|/**  * Planner rule that pushes a {@link org.apache.calcite.rel.core.Filter}  * past a {@link org.apache.calcite.rel.core.SetOp}.  */
end_comment

begin_class
specifier|public
class|class
name|FilterSetOpTransposeRule
extends|extends
name|RelOptRule
implements|implements
name|TransformationRule
block|{
specifier|public
specifier|static
specifier|final
name|FilterSetOpTransposeRule
name|INSTANCE
init|=
operator|new
name|FilterSetOpTransposeRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a FilterSetOpTransposeRule.    */
specifier|public
name|FilterSetOpTransposeRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|Filter
operator|.
name|class
argument_list|,
name|operand
argument_list|(
name|SetOp
operator|.
name|class
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to  be removed before 2.0
specifier|public
name|FilterSetOpTransposeRule
parameter_list|(
name|RelFactories
operator|.
name|FilterFactory
name|filterFactory
parameter_list|)
block|{
name|this
argument_list|(
name|RelBuilder
operator|.
name|proto
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|filterFactory
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// implement RelOptRule
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|Filter
name|filterRel
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|SetOp
name|setOp
init|=
name|call
operator|.
name|rel
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RexNode
name|condition
init|=
name|filterRel
operator|.
name|getCondition
argument_list|()
decl_stmt|;
comment|// create filters on top of each setop child, modifying the filter
comment|// condition to reference each setop child
name|RexBuilder
name|rexBuilder
init|=
name|filterRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|origFields
init|=
name|setOp
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|int
index|[]
name|adjustments
init|=
operator|new
name|int
index|[
name|origFields
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|newSetOpInputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|setOp
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|RexNode
name|newCondition
init|=
name|condition
operator|.
name|accept
argument_list|(
operator|new
name|RelOptUtil
operator|.
name|RexInputConverter
argument_list|(
name|rexBuilder
argument_list|,
name|origFields
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|,
name|adjustments
argument_list|)
argument_list|)
decl_stmt|;
name|newSetOpInputs
operator|.
name|add
argument_list|(
name|relBuilder
operator|.
name|push
argument_list|(
name|input
argument_list|)
operator|.
name|filter
argument_list|(
name|newCondition
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// create a new setop whose children are the filters created above
name|SetOp
name|newSetOp
init|=
name|setOp
operator|.
name|copy
argument_list|(
name|setOp
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|newSetOpInputs
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newSetOp
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

