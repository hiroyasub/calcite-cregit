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
name|core
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
name|RelOptCluster
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
name|RelOptCost
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
name|RelOptPlanner
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
name|plan
operator|.
name|RelTraitSet
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
name|RelWriter
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
name|SingleRel
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
name|hint
operator|.
name|Hintable
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
name|hint
operator|.
name|RelHint
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
name|metadata
operator|.
name|RelMdUtil
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|RexLocalRef
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
name|rex
operator|.
name|RexOver
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
name|rex
operator|.
name|RexProgramBuilder
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
name|RexShuttle
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
name|RexUtil
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
name|Litmus
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
comment|/**  *<code>Calc</code> is an abstract base class for implementations of  * {@link org.apache.calcite.rel.logical.LogicalCalc}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Calc
extends|extends
name|SingleRel
implements|implements
name|Hintable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|hints
decl_stmt|;
specifier|protected
specifier|final
name|RexProgram
name|program
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a Calc.    *    * @param cluster Cluster    * @param traits Traits    * @param hints Hints of this relational expression    * @param child Input relation    * @param program Calc program    */
specifier|protected
name|Calc
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|program
operator|.
name|getOutputRowType
argument_list|()
expr_stmt|;
name|this
operator|.
name|program
operator|=
name|program
expr_stmt|;
name|this
operator|.
name|hints
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|hints
argument_list|)
expr_stmt|;
assert|assert
name|isValid
argument_list|(
name|Litmus
operator|.
name|THROW
argument_list|,
literal|null
argument_list|)
assert|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|Calc
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|child
argument_list|,
name|program
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|Calc
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|child
argument_list|,
name|program
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|collationList
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
name|Calc
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|program
argument_list|)
return|;
block|}
comment|/**    * Creates a copy of this {@code Calc}.    *    * @param traitSet Traits    * @param child Input relation    * @param program Calc program    * @return New {@code Calc} if any parameter differs from the value of this    *   {@code Calc}, or just {@code this} if all the parameters are the same    *    * @see #copy(org.apache.calcite.plan.RelTraitSet, java.util.List)    */
specifier|public
specifier|abstract
name|Calc
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|)
function_decl|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|Calc
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|collationList
argument_list|)
expr_stmt|;
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|child
argument_list|,
name|program
argument_list|)
return|;
block|}
comment|/** Returns whether this Calc contains any windowed-aggregate functions. */
specifier|public
specifier|final
name|boolean
name|containsOver
parameter_list|()
block|{
return|return
name|RexOver
operator|.
name|containsOver
argument_list|(
name|program
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isValid
parameter_list|(
name|Litmus
name|litmus
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
if|if
condition|(
operator|!
name|RelOptUtil
operator|.
name|equal
argument_list|(
literal|"program's input type"
argument_list|,
name|program
operator|.
name|getInputRowType
argument_list|()
argument_list|,
literal|"child's output type"
argument_list|,
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|litmus
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|null
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|program
operator|.
name|isValid
argument_list|(
name|litmus
argument_list|,
name|context
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|null
argument_list|)
return|;
block|}
if|if
condition|(
operator|!
name|program
operator|.
name|isNormalized
argument_list|(
name|litmus
argument_list|,
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|litmus
operator|.
name|fail
argument_list|(
literal|null
argument_list|)
return|;
block|}
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
specifier|public
name|RexProgram
name|getProgram
parameter_list|()
block|{
return|return
name|program
return|;
block|}
annotation|@
name|Override
specifier|public
name|ImmutableList
argument_list|<
name|RelHint
argument_list|>
name|getHints
parameter_list|()
block|{
return|return
name|hints
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|estimateRowCount
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|RelMdUtil
operator|.
name|estimateFilteredRows
argument_list|(
name|getInput
argument_list|()
argument_list|,
name|program
argument_list|,
name|mq
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|double
name|dRows
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|double
name|dCpu
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|getInput
argument_list|()
argument_list|)
operator|*
name|program
operator|.
name|getExprCount
argument_list|()
decl_stmt|;
name|double
name|dIo
init|=
literal|0
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|dRows
argument_list|,
name|dCpu
argument_list|,
name|dIo
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|program
operator|.
name|explainCalc
argument_list|(
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RexShuttle
name|shuttle
parameter_list|)
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|oldExprs
init|=
name|program
operator|.
name|getExprList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprs
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|oldExprs
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RexLocalRef
argument_list|>
name|oldProjects
init|=
name|program
operator|.
name|getProjectList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexLocalRef
argument_list|>
name|projects
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|oldProjects
argument_list|)
decl_stmt|;
name|RexLocalRef
name|oldCondition
init|=
name|program
operator|.
name|getCondition
argument_list|()
decl_stmt|;
name|RexNode
name|condition
decl_stmt|;
if|if
condition|(
name|oldCondition
operator|!=
literal|null
condition|)
block|{
name|condition
operator|=
name|shuttle
operator|.
name|apply
argument_list|(
name|oldCondition
argument_list|)
expr_stmt|;
assert|assert
name|condition
operator|instanceof
name|RexLocalRef
operator|:
literal|"Invalid condition after rewrite. Expected RexLocalRef, got "
operator|+
name|condition
assert|;
block|}
else|else
block|{
name|condition
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|exprs
operator|==
name|oldExprs
operator|&&
name|projects
operator|==
name|oldProjects
operator|&&
name|condition
operator|==
name|oldCondition
condition|)
block|{
return|return
name|this
return|;
block|}
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|RexUtil
operator|.
name|createStructType
argument_list|(
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|projects
argument_list|,
name|this
operator|.
name|rowType
operator|.
name|getFieldNames
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|final
name|RexProgram
name|newProgram
init|=
name|RexProgramBuilder
operator|.
name|create
argument_list|(
name|rexBuilder
argument_list|,
name|program
operator|.
name|getInputRowType
argument_list|()
argument_list|,
name|exprs
argument_list|,
name|projects
argument_list|,
name|condition
argument_list|,
name|rowType
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
operator|.
name|getProgram
argument_list|(
literal|false
argument_list|)
decl_stmt|;
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|getInput
argument_list|()
argument_list|,
name|newProgram
argument_list|)
return|;
block|}
block|}
end_class

end_unit

