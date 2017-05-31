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
name|logical
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
name|RelCollationTraitDef
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
name|RelDistribution
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
name|RelDistributionTraitDef
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
name|CorrelationId
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
name|RelMdCollation
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
name|RelMdDistribution
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
name|rules
operator|.
name|FilterToCalcRule
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
name|rules
operator|.
name|ProjectToCalcRule
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
name|base
operator|.
name|Supplier
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
comment|/**  * A relational expression which computes project expressions and also filters.  *  *<p>This relational expression combines the functionality of  * {@link LogicalProject} and {@link LogicalFilter}.  * It should be created in the later  * stages of optimization, by merging consecutive {@link LogicalProject} and  * {@link LogicalFilter} nodes together.  *  *<p>The following rules relate to<code>LogicalCalc</code>:</p>  *  *<ul>  *<li>{@link FilterToCalcRule} creates this from a {@link LogicalFilter}  *<li>{@link ProjectToCalcRule} creates this from a {@link LogicalFilter}  *<li>{@link org.apache.calcite.rel.rules.FilterCalcMergeRule}  *     merges this with a {@link LogicalFilter}  *<li>{@link org.apache.calcite.rel.rules.ProjectCalcMergeRule}  *     merges this with a {@link LogicalProject}  *<li>{@link org.apache.calcite.rel.rules.CalcMergeRule}  *     merges two {@code LogicalCalc}s  *</ul>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LogicalCalc
extends|extends
name|Calc
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|//~ Constructors -----------------------------------------------------------
comment|/** Creates a LogicalCalc. */
specifier|public
name|LogicalCalc
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
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
name|traitSet
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
specifier|public
name|LogicalCalc
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
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
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
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
specifier|public
specifier|static
name|LogicalCalc
name|create
parameter_list|(
specifier|final
name|RelNode
name|input
parameter_list|,
specifier|final
name|RexProgram
name|program
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|cluster
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
operator|.
name|replaceIfs
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
operator|new
name|Supplier
argument_list|<
name|List
argument_list|<
name|RelCollation
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|get
parameter_list|()
block|{
return|return
name|RelMdCollation
operator|.
name|calc
argument_list|(
name|mq
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
return|;
block|}
block|}
argument_list|)
operator|.
name|replaceIf
argument_list|(
name|RelDistributionTraitDef
operator|.
name|INSTANCE
argument_list|,
operator|new
name|Supplier
argument_list|<
name|RelDistribution
argument_list|>
argument_list|()
block|{
specifier|public
name|RelDistribution
name|get
parameter_list|()
block|{
return|return
name|RelMdDistribution
operator|.
name|calc
argument_list|(
name|mq
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogicalCalc
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|LogicalCalc
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
block|{
return|return
operator|new
name|LogicalCalc
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|,
name|program
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collectVariablesUsed
parameter_list|(
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variableSet
parameter_list|)
block|{
specifier|final
name|RelOptUtil
operator|.
name|VariableUsedVisitor
name|vuv
init|=
operator|new
name|RelOptUtil
operator|.
name|VariableUsedVisitor
argument_list|(
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|expr
range|:
name|program
operator|.
name|getExprList
argument_list|()
control|)
block|{
name|expr
operator|.
name|accept
argument_list|(
name|vuv
argument_list|)
expr_stmt|;
block|}
name|variableSet
operator|.
name|addAll
argument_list|(
name|vuv
operator|.
name|variables
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End LogicalCalc.java
end_comment

end_unit

