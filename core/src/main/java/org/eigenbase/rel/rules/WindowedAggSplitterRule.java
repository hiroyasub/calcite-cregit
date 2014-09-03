begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|rules
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
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
name|base
operator|.
name|Function
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * Rule that slices the {@link CalcRel} into sections which contain windowed  * agg functions and sections which do not.  *  *<p>The sections which contain windowed agg functions become instances of  * {@link org.eigenbase.rel.WindowRel}. If the {@link CalcRel} does not contain any  * windowed agg functions, does nothing.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|WindowedAggSplitterRule
extends|extends
name|RelOptRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * Instance of the rule that applies to a {@link CalcRelBase} that contains    * windowed aggregates and converts it into a mixture of    * {@link org.eigenbase.rel.WindowRel} and {@code CalcRelBase}.    */
specifier|public
specifier|static
specifier|final
name|WindowedAggSplitterRule
name|INSTANCE
init|=
operator|new
name|WindowedAggSplitterRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|CalcRelBase
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|any
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|super
operator|.
name|matches
argument_list|(
name|rel
argument_list|)
operator|&&
name|RexOver
operator|.
name|containsOver
argument_list|(
operator|(
operator|(
name|CalcRelBase
operator|)
name|rel
operator|)
operator|.
name|getProgram
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|,
literal|"WindowedAggSplitterRule"
argument_list|)
block|{
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|CalcRelBase
name|calc
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
assert|assert
name|RexOver
operator|.
name|containsOver
argument_list|(
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|)
assert|;
name|CalcRelSplitter
name|transform
init|=
operator|new
name|WindowedAggRelSplitter
argument_list|(
name|calc
argument_list|)
decl_stmt|;
name|RelNode
name|newRel
init|=
name|transform
operator|.
name|execute
argument_list|()
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|/**    * Instance of the rule that can be applied to a    * {@link org.eigenbase.rel.ProjectRelBase} and that produces, in turn,    * a mixture of {@code ProjectRel} and {@link org.eigenbase.rel.WindowRel}.    */
specifier|public
specifier|static
specifier|final
name|WindowedAggSplitterRule
name|PROJECT
init|=
operator|new
name|WindowedAggSplitterRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRelBase
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|any
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|super
operator|.
name|matches
argument_list|(
name|rel
argument_list|)
operator|&&
name|RexOver
operator|.
name|containsOver
argument_list|(
operator|(
operator|(
name|ProjectRelBase
operator|)
name|rel
operator|)
operator|.
name|getProjects
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
argument_list|,
literal|"WindowedAggSplitterRule:project"
argument_list|)
block|{
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
name|ProjectRelBase
name|project
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
assert|assert
name|RexOver
operator|.
name|containsOver
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
literal|null
argument_list|)
assert|;
specifier|final
name|RelNode
name|child
init|=
name|project
operator|.
name|getChild
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|project
operator|.
name|getRowType
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
name|child
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
comment|// temporary CalcRel, never registered
specifier|final
name|CalcRel
name|calc
init|=
operator|new
name|CalcRel
argument_list|(
name|project
operator|.
name|getCluster
argument_list|()
argument_list|,
name|project
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|child
argument_list|,
name|rowType
argument_list|,
name|program
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelCollation
operator|>
name|of
argument_list|()
argument_list|)
decl_stmt|;
name|CalcRelSplitter
name|transform
init|=
operator|new
name|WindowedAggRelSplitter
argument_list|(
name|calc
argument_list|)
block|{
annotation|@
name|Override
specifier|protected
name|RelNode
name|handle
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
if|if
condition|(
name|rel
operator|instanceof
name|CalcRel
condition|)
block|{
name|CalcRel
name|calc
init|=
operator|(
name|CalcRel
operator|)
name|rel
decl_stmt|;
specifier|final
name|RexProgram
name|program
init|=
name|calc
operator|.
name|getProgram
argument_list|()
decl_stmt|;
name|rel
operator|=
name|calc
operator|.
name|getChild
argument_list|()
expr_stmt|;
if|if
condition|(
name|program
operator|.
name|getCondition
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|rel
operator|=
operator|new
name|FilterRel
argument_list|(
name|calc
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
argument_list|,
name|program
operator|.
name|expandLocalRef
argument_list|(
name|program
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|program
operator|.
name|projectsOnlyIdentity
argument_list|()
condition|)
block|{
name|rel
operator|=
name|RelOptUtil
operator|.
name|createProject
argument_list|(
name|rel
argument_list|,
name|Lists
operator|.
name|transform
argument_list|(
name|program
operator|.
name|getProjectList
argument_list|()
argument_list|,
operator|new
name|Function
argument_list|<
name|RexLocalRef
argument_list|,
name|RexNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RexNode
name|apply
parameter_list|(
name|RexLocalRef
name|a0
parameter_list|)
block|{
return|return
name|program
operator|.
name|expandLocalRef
argument_list|(
name|a0
argument_list|)
return|;
block|}
block|}
argument_list|)
argument_list|,
name|calc
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|rel
return|;
block|}
block|}
decl_stmt|;
name|RelNode
name|newRel
init|=
name|transform
operator|.
name|execute
argument_list|()
decl_stmt|;
name|call
operator|.
name|transformTo
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a rule.    */
specifier|private
name|WindowedAggSplitterRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Splitter which distinguishes between windowed aggregation expressions    * (calls to {@link RexOver}) and ordinary expressions.    */
specifier|static
class|class
name|WindowedAggRelSplitter
extends|extends
name|CalcRelSplitter
block|{
name|WindowedAggRelSplitter
parameter_list|(
name|CalcRelBase
name|calc
parameter_list|)
block|{
name|super
argument_list|(
name|calc
argument_list|,
operator|new
name|RelType
index|[]
block|{
operator|new
name|CalcRelSplitter
operator|.
name|RelType
argument_list|(
literal|"CalcRelType"
argument_list|)
block|{
specifier|protected
name|boolean
name|canImplement
parameter_list|(
name|RexFieldAccess
name|field
parameter_list|)
block|{
return|return
literal|true
return|;
block_content|}
block|protected boolean canImplement(RexDynamicParam param
block|)
block|{
return|return
literal|true
return|;
block|}
specifier|protected
name|boolean
name|canImplement
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
return|return
literal|true
return|;
block_content|}
block|protected boolean canImplement(RexCall call
block|)
block|{
return|return
operator|!
operator|(
name|call
operator|instanceof
name|RexOver
operator|)
return|;
block|}
specifier|protected
name|RelNode
name|makeRel
argument_list|(
name|RelOptCluster
name|cluster
argument_list|,
name|RelTraitSet
name|traits
argument_list|,
name|RelDataType
name|rowType
argument_list|,
name|RelNode
name|child
argument_list|,
name|RexProgram
name|program
argument_list|)
block|{
assert|assert
operator|!
name|program
operator|.
name|containsAggs
argument_list|()
assert|;
name|program
operator|=
name|RexProgramBuilder
operator|.
name|normalize
argument_list|(
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
name|program
argument_list|)
argument_list|;                 return
name|super
operator|.
name|makeRel
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|rowType
argument_list|,
name|child
argument_list|,
name|program
argument_list|)
argument_list|;
block|}
block|}
operator|,
operator|new
name|CalcRelSplitter
operator|.
name|RelType
argument_list|(
literal|"WinAggRelType"
argument_list|)
block|{
specifier|protected
name|boolean
name|canImplement
parameter_list|(
name|RexFieldAccess
name|field
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|canImplement
parameter_list|(
name|RexDynamicParam
name|param
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|canImplement
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|canImplement
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
return|return
name|call
operator|instanceof
name|RexOver
return|;
block|}
specifier|protected
name|boolean
name|supportsCondition
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
name|RelNode
name|makeRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
name|Util
operator|.
name|permAssert
argument_list|(
name|program
operator|.
name|getCondition
argument_list|()
operator|==
literal|null
argument_list|,
literal|"WindowedAggregateRel cannot accept a condition"
argument_list|)
expr_stmt|;
return|return
name|WindowRel
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|,
name|program
argument_list|,
name|rowType
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_empty_stmt
unit|)
empty_stmt|;
end_empty_stmt

begin_function
unit|}      @
name|Override
specifier|protected
name|List
argument_list|<
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|getCohorts
parameter_list|()
block|{
comment|// Here used to be the implementation that treats all the RexOvers
comment|// as a single Cohort. This is flawed if the RexOvers
comment|// depend on each other (i.e. the second one uses the result
comment|// of the first).
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
end_function

begin_comment
unit|} }
comment|// End WindowedAggSplitterRule.java
end_comment

end_unit

