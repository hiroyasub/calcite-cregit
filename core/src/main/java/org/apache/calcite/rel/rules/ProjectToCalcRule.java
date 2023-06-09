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
name|RelRule
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
name|logical
operator|.
name|LogicalCalc
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
name|logical
operator|.
name|LogicalProject
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
name|RexProgram
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
name|org
operator|.
name|immutables
operator|.
name|value
operator|.
name|Value
import|;
end_import

begin_comment
comment|/**  * Rule to convert a  * {@link org.apache.calcite.rel.logical.LogicalProject} to a  * {@link org.apache.calcite.rel.logical.LogicalCalc}.  *  *<p>The rule does not fire if the child is a  * {@link org.apache.calcite.rel.logical.LogicalProject},  * {@link org.apache.calcite.rel.logical.LogicalFilter} or  * {@link org.apache.calcite.rel.logical.LogicalCalc}. If it did, then the same  * {@link org.apache.calcite.rel.logical.LogicalCalc} would be formed via  * several transformation paths, which is a waste of effort.  *  * @see FilterToCalcRule  * @see CoreRules#PROJECT_TO_CALC  */
end_comment

begin_class
annotation|@
name|Value
operator|.
name|Enclosing
specifier|public
class|class
name|ProjectToCalcRule
extends|extends
name|RelRule
argument_list|<
name|ProjectToCalcRule
operator|.
name|Config
argument_list|>
implements|implements
name|TransformationRule
block|{
comment|/** Creates a ProjectToCalcRule. */
specifier|protected
name|ProjectToCalcRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|ProjectToCalcRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|this
argument_list|(
name|Config
operator|.
name|DEFAULT
operator|.
name|withRelBuilderFactory
argument_list|(
name|relBuilderFactory
argument_list|)
operator|.
name|as
argument_list|(
name|Config
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|LogicalProject
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|project
operator|.
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|RexProgram
name|program
init|=
name|RexProgram
operator|.
name|create
argument_list|(
name|input
operator|.
name|getRowType
argument_list|()
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
literal|null
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
argument_list|,
name|project
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|LogicalCalc
name|calc
init|=
name|LogicalCalc
operator|.
name|create
argument_list|(
name|input
argument_list|,
name|program
argument_list|)
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|calc
argument_list|)
expr_stmt|;
block|}
comment|/** Rule configuration. */
annotation|@
name|Value
operator|.
name|Immutable
specifier|public
interface|interface
name|Config
extends|extends
name|RelRule
operator|.
name|Config
block|{
name|Config
name|DEFAULT
init|=
name|ImmutableProjectToCalcRule
operator|.
name|Config
operator|.
name|of
argument_list|()
operator|.
name|withOperandSupplier
argument_list|(
name|b
lambda|->
name|b
operator|.
name|operand
argument_list|(
name|LogicalProject
operator|.
name|class
argument_list|)
operator|.
name|anyInputs
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|default
name|ProjectToCalcRule
name|toRule
parameter_list|()
block|{
return|return
operator|new
name|ProjectToCalcRule
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

