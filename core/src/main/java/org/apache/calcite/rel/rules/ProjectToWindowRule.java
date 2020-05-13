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
name|linq4j
operator|.
name|Ord
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
name|RelOptRuleOperand
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
name|Calc
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
name|Project
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
name|LogicalWindow
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
name|RexCall
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
name|RexDynamicParam
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
name|RexFieldAccess
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
name|RexLiteral
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
name|RexVisitorImpl
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
name|RexWindow
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|ImmutableIntList
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
name|Pair
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
name|graph
operator|.
name|DefaultDirectedGraph
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
name|graph
operator|.
name|DefaultEdge
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
name|graph
operator|.
name|DirectedGraph
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
name|graph
operator|.
name|TopologicalOrderIterator
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
name|Preconditions
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
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
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Planner rule that slices a  * {@link org.apache.calcite.rel.core.Project}  * into sections which contain windowed  * aggregate functions and sections which do not.  *  *<p>The sections which contain windowed agg functions become instances of  * {@link org.apache.calcite.rel.logical.LogicalWindow}.  * If the {@link org.apache.calcite.rel.logical.LogicalCalc} does not contain  * any windowed agg functions, does nothing.  *  *<p>There is also a variant that matches  * {@link org.apache.calcite.rel.core.Calc} rather than {@code Project}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ProjectToWindowRule
extends|extends
name|RelOptRule
implements|implements
name|TransformationRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|ProjectToWindowRule
name|INSTANCE
init|=
operator|new
name|CalcToWindowRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ProjectToWindowRule
name|PROJECT
init|=
operator|new
name|ProjectToLogicalProjectAndWindowRule
argument_list|(
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a ProjectToWindowRule.    *    * @param operand           Root operand, must not be null    * @param description       Description, or null to guess description    * @param relBuilderFactory Builder for relational expressions    */
specifier|public
name|ProjectToWindowRule
parameter_list|(
name|RelOptRuleOperand
name|operand
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|,
name|relBuilderFactory
argument_list|,
name|description
argument_list|)
expr_stmt|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Instance of the rule that applies to a    * {@link org.apache.calcite.rel.core.Calc} that contains    * windowed aggregates and converts it into a mixture of    * {@link org.apache.calcite.rel.logical.LogicalWindow} and {@code Calc}.    */
specifier|public
specifier|static
class|class
name|CalcToWindowRule
extends|extends
name|ProjectToWindowRule
block|{
comment|/**      * Creates a CalcToWindowRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|CalcToWindowRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operandJ
argument_list|(
name|Calc
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|calc
lambda|->
name|RexOver
operator|.
name|containsOver
argument_list|(
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|)
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"ProjectToWindowRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
name|Calc
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
specifier|final
name|CalcRelSplitter
name|transform
init|=
operator|new
name|WindowedAggRelSplitter
argument_list|(
name|calc
argument_list|,
name|call
operator|.
name|builder
argument_list|()
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
comment|/**    * Instance of the rule that can be applied to a    * {@link org.apache.calcite.rel.core.Project} and that produces, in turn,    * a mixture of {@code LogicalProject}    * and {@link org.apache.calcite.rel.logical.LogicalWindow}.    */
specifier|public
specifier|static
class|class
name|ProjectToLogicalProjectAndWindowRule
extends|extends
name|ProjectToWindowRule
block|{
comment|/**      * Creates a ProjectToWindowRule.      *      * @param relBuilderFactory Builder for relational expressions      */
specifier|public
name|ProjectToLogicalProjectAndWindowRule
parameter_list|(
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operandJ
argument_list|(
name|Project
operator|.
name|class
argument_list|,
literal|null
argument_list|,
name|project
lambda|->
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
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|"ProjectToWindowRule:project"
argument_list|)
expr_stmt|;
block|}
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
name|Project
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
comment|// temporary LogicalCalc, never registered
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
specifier|final
name|CalcRelSplitter
name|transform
init|=
operator|new
name|WindowedAggRelSplitter
argument_list|(
name|calc
argument_list|,
name|call
operator|.
name|builder
argument_list|()
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
operator|!
operator|(
name|rel
operator|instanceof
name|LogicalCalc
operator|)
condition|)
block|{
return|return
name|rel
return|;
block|}
specifier|final
name|LogicalCalc
name|calc
init|=
operator|(
name|LogicalCalc
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
name|relBuilder
operator|.
name|push
argument_list|(
name|calc
operator|.
name|getInput
argument_list|()
argument_list|)
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
name|relBuilder
operator|.
name|filter
argument_list|(
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
name|relBuilder
operator|.
name|project
argument_list|(
name|Lists
operator|.
name|transform
argument_list|(
name|program
operator|.
name|getProjectList
argument_list|()
argument_list|,
name|program
operator|::
name|expandLocalRef
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
return|return
name|relBuilder
operator|.
name|build
argument_list|()
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
comment|/**    * Splitter that distinguishes between windowed aggregation expressions    * (calls to {@link RexOver}) and ordinary expressions.    */
specifier|static
class|class
name|WindowedAggRelSplitter
extends|extends
name|CalcRelSplitter
block|{
specifier|private
specifier|static
specifier|final
name|RelType
index|[]
name|REL_TYPES
init|=
block|{
operator|new
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
name|traitSet
argument_list|,
name|RelBuilder
name|relBuilder
argument_list|,
name|RelNode
name|input
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
name|program
operator|.
name|normalize
argument_list|(
name|cluster
operator|.
name|getRexBuilder
argument_list|()
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|super
operator|.
name|makeRel
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|relBuilder
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
return|;
block|}
block|}
end_class

begin_operator
operator|,
end_operator

begin_expr_stmt
operator|new
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
name|traitSet
parameter_list|,
name|RelBuilder
name|relBuilder
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
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
name|LogicalWindow
operator|.
name|create
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|relBuilder
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
return|;
block|}
block|}
end_expr_stmt

begin_expr_stmt
unit|};
name|WindowedAggRelSplitter
argument_list|(
name|Calc
name|calc
argument_list|,
name|RelBuilder
name|relBuilder
argument_list|)
block|{
name|super
argument_list|(
name|calc
argument_list|,
name|relBuilder
argument_list|,
name|REL_TYPES
argument_list|)
block|;     }
expr|@
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
argument_list|()
block|{
comment|// Two RexOver will be put in the same cohort
comment|// if the following conditions are satisfied
comment|// (1). They have the same RexWindow
comment|// (2). They are not dependent on each other
name|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprs
operator|=
name|this
operator|.
name|program
operator|.
name|getExprList
argument_list|()
block|;
name|final
name|DirectedGraph
argument_list|<
name|Integer
argument_list|,
name|DefaultEdge
argument_list|>
name|graph
operator|=
name|createGraphFromExpression
argument_list|(
name|exprs
argument_list|)
block|;
name|final
name|List
argument_list|<
name|Integer
argument_list|>
name|rank
operator|=
name|getRank
argument_list|(
name|graph
argument_list|)
block|;
name|final
name|List
argument_list|<
name|Pair
argument_list|<
name|RexWindow
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
argument_list|>
name|windowToIndices
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
block|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|exprs
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|RexNode
name|expr
init|=
name|exprs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|expr
operator|instanceof
name|RexOver
condition|)
block|{
specifier|final
name|RexOver
name|over
init|=
operator|(
name|RexOver
operator|)
name|expr
decl_stmt|;
comment|// If we can found an existing cohort which satisfies the two conditions,
comment|// we will add this RexOver into that cohort
name|boolean
name|isFound
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|RexWindow
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|pair
range|:
name|windowToIndices
control|)
block|{
comment|// Check the first condition
if|if
condition|(
name|pair
operator|.
name|left
operator|.
name|equals
argument_list|(
name|over
operator|.
name|getWindow
argument_list|()
argument_list|)
condition|)
block|{
comment|// Check the second condition
name|boolean
name|hasDependency
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|ordinal
range|:
name|pair
operator|.
name|right
control|)
block|{
if|if
condition|(
name|isDependent
argument_list|(
name|graph
argument_list|,
name|rank
argument_list|,
name|ordinal
argument_list|,
name|i
argument_list|)
condition|)
block|{
name|hasDependency
operator|=
literal|true
expr_stmt|;
break|break;
block|}
end_expr_stmt

begin_expr_stmt
unit|}                if
operator|(
operator|!
name|hasDependency
operator|)
block|{
name|pair
operator|.
name|right
operator|.
name|add
argument_list|(
name|i
argument_list|)
block|;
name|isFound
operator|=
literal|true
block|;
break|break;
block|}
end_expr_stmt

begin_comment
unit|}           }
comment|// This RexOver cannot be added into any existing cohort
end_comment

begin_if_stmt
if|if
condition|(
operator|!
name|isFound
condition|)
block|{
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|newSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|i
argument_list|)
argument_list|)
decl_stmt|;
name|windowToIndices
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|over
operator|.
name|getWindow
argument_list|()
argument_list|,
name|newSet
argument_list|)
argument_list|)
expr_stmt|;
block|}
end_if_stmt

begin_decl_stmt
unit|}       }
specifier|final
name|List
argument_list|<
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|cohorts
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
end_decl_stmt

begin_for
for|for
control|(
name|Pair
argument_list|<
name|RexWindow
argument_list|,
name|Set
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|pair
range|:
name|windowToIndices
control|)
block|{
name|cohorts
operator|.
name|add
argument_list|(
name|pair
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
end_for

begin_return
return|return
name|cohorts
return|;
end_return

begin_function
unit|}      private
name|boolean
name|isDependent
parameter_list|(
specifier|final
name|DirectedGraph
argument_list|<
name|Integer
argument_list|,
name|DefaultEdge
argument_list|>
name|graph
parameter_list|,
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|rank
parameter_list|,
specifier|final
name|int
name|ordinal1
parameter_list|,
specifier|final
name|int
name|ordinal2
parameter_list|)
block|{
if|if
condition|(
name|rank
operator|.
name|get
argument_list|(
name|ordinal2
argument_list|)
operator|>
name|rank
operator|.
name|get
argument_list|(
name|ordinal1
argument_list|)
condition|)
block|{
return|return
name|isDependent
argument_list|(
name|graph
argument_list|,
name|rank
argument_list|,
name|ordinal2
argument_list|,
name|ordinal1
argument_list|)
return|;
block|}
comment|// Check if the expression in ordinal1
comment|// could depend on expression in ordinal2 by Depth-First-Search
specifier|final
name|Deque
argument_list|<
name|Integer
argument_list|>
name|dfs
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|Integer
argument_list|>
name|visited
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|dfs
operator|.
name|push
argument_list|(
name|ordinal2
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|dfs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|int
name|source
init|=
name|dfs
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|visited
operator|.
name|contains
argument_list|(
name|source
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|source
operator|==
name|ordinal1
condition|)
block|{
return|return
literal|true
return|;
block|}
name|visited
operator|.
name|add
argument_list|(
name|source
argument_list|)
expr_stmt|;
for|for
control|(
name|DefaultEdge
name|e
range|:
name|graph
operator|.
name|getOutwardEdges
argument_list|(
name|source
argument_list|)
control|)
block|{
name|int
name|target
init|=
operator|(
name|int
operator|)
name|e
operator|.
name|target
decl_stmt|;
if|if
condition|(
name|rank
operator|.
name|get
argument_list|(
name|target
argument_list|)
operator|<=
name|rank
operator|.
name|get
argument_list|(
name|ordinal1
argument_list|)
condition|)
block|{
name|dfs
operator|.
name|push
argument_list|(
name|target
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
literal|false
return|;
block|}
end_function

begin_function
specifier|private
name|List
argument_list|<
name|Integer
argument_list|>
name|getRank
parameter_list|(
name|DirectedGraph
argument_list|<
name|Integer
argument_list|,
name|DefaultEdge
argument_list|>
name|graph
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|rankArr
init|=
operator|new
name|int
index|[
name|graph
operator|.
name|vertexSet
argument_list|()
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|rank
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|TopologicalOrderIterator
operator|.
name|of
argument_list|(
name|graph
argument_list|)
control|)
block|{
name|rankArr
index|[
name|i
index|]
operator|=
name|rank
operator|++
expr_stmt|;
block|}
return|return
name|ImmutableIntList
operator|.
name|of
argument_list|(
name|rankArr
argument_list|)
return|;
block|}
end_function

begin_function
specifier|private
name|DirectedGraph
argument_list|<
name|Integer
argument_list|,
name|DefaultEdge
argument_list|>
name|createGraphFromExpression
parameter_list|(
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprs
parameter_list|)
block|{
specifier|final
name|DirectedGraph
argument_list|<
name|Integer
argument_list|,
name|DefaultEdge
argument_list|>
name|graph
init|=
name|DefaultDirectedGraph
operator|.
name|create
argument_list|()
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
name|exprs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|graph
operator|.
name|addVertex
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
specifier|final
name|Ord
argument_list|<
name|RexNode
argument_list|>
name|expr
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|exprs
argument_list|)
control|)
block|{
name|expr
operator|.
name|e
operator|.
name|accept
argument_list|(
operator|new
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
argument_list|(
literal|true
argument_list|)
block|{
specifier|public
name|Void
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|)
block|{
name|graph
operator|.
name|addEdge
argument_list|(
name|localRef
operator|.
name|getIndex
argument_list|()
argument_list|,
name|expr
operator|.
name|i
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
assert|assert
name|graph
operator|.
name|vertexSet
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|exprs
operator|.
name|size
argument_list|()
assert|;
return|return
name|graph
return|;
block|}
end_function

unit|} }
end_unit

