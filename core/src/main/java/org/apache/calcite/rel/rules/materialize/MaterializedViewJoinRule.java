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
operator|.
name|materialize
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
name|hep
operator|.
name|HepPlanner
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
name|hep
operator|.
name|HepProgram
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
name|JoinRelType
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
name|TableScan
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
name|rex
operator|.
name|RexSimplify
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
name|RexTableInputRef
operator|.
name|RelTableRef
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
name|util
operator|.
name|Pair
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
name|BiMap
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
name|Multimap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
name|Collection
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
comment|/** Materialized view rewriting for join.  *  * @param<C> Configuration type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MaterializedViewJoinRule
parameter_list|<
name|C
extends|extends
name|MaterializedViewRule
operator|.
name|Config
parameter_list|>
extends|extends
name|MaterializedViewRule
argument_list|<
name|C
argument_list|>
block|{
comment|/** Creates a MaterializedViewJoinRule. */
name|MaterializedViewJoinRule
parameter_list|(
name|C
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
name|Override
specifier|protected
name|boolean
name|isValidPlan
parameter_list|(
annotation|@
name|Nullable
name|Project
name|topProject
parameter_list|,
name|RelNode
name|node
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|isValidRelNodePlan
argument_list|(
name|node
argument_list|,
name|mq
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
annotation|@
name|Nullable
name|ViewPartialRewriting
name|compensateViewPartial
parameter_list|(
name|RelBuilder
name|relBuilder
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RelNode
name|input
parameter_list|,
annotation|@
name|Nullable
name|Project
name|topProject
parameter_list|,
name|RelNode
name|node
parameter_list|,
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|queryTableRefs
parameter_list|,
name|EquivalenceClasses
name|queryEC
parameter_list|,
annotation|@
name|Nullable
name|Project
name|topViewProject
parameter_list|,
name|RelNode
name|viewNode
parameter_list|,
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|viewTableRefs
parameter_list|)
block|{
comment|// We only create the rewriting in the minimal subtree of plan operators.
comment|// Otherwise we will produce many EQUAL rewritings at different levels of
comment|// the plan.
comment|// View: (A JOIN B) JOIN C
comment|// Query: (((A JOIN B) JOIN D) JOIN C) JOIN E
comment|// We produce it at:
comment|// ((A JOIN B) JOIN D) JOIN C
comment|// But not at:
comment|// (((A JOIN B) JOIN D) JOIN C) JOIN E
if|if
condition|(
name|config
operator|.
name|fastBailOut
argument_list|()
condition|)
block|{
for|for
control|(
name|RelNode
name|joinInput
range|:
name|node
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|tableReferences
init|=
name|mq
operator|.
name|getTableReferences
argument_list|(
name|joinInput
argument_list|)
decl_stmt|;
if|if
condition|(
name|tableReferences
operator|==
literal|null
operator|||
name|tableReferences
operator|.
name|containsAll
argument_list|(
name|viewTableRefs
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
comment|// Extract tables that are in the query and not in the view
specifier|final
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|extraTableRefs
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelTableRef
name|tRef
range|:
name|queryTableRefs
control|)
block|{
if|if
condition|(
operator|!
name|viewTableRefs
operator|.
name|contains
argument_list|(
name|tRef
argument_list|)
condition|)
block|{
comment|// Add to extra tables if table is not part of the view
name|extraTableRefs
operator|.
name|add
argument_list|(
name|tRef
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Rewrite the view and the view plan. We only need to add the missing
comment|// tables on top of the view and view plan using a cartesian product.
comment|// Then the rest of the rewriting algorithm can be executed in the same
comment|// fashion, and if there are predicates between the existing and missing
comment|// tables, the rewriting algorithm will enforce them.
name|Multimap
argument_list|<
name|Class
argument_list|<
name|?
extends|extends
name|RelNode
argument_list|>
argument_list|,
name|RelNode
argument_list|>
name|nodeTypes
init|=
name|mq
operator|.
name|getNodeTypes
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
name|nodeTypes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Collection
argument_list|<
name|RelNode
argument_list|>
name|tableScanNodes
init|=
name|nodeTypes
operator|.
name|get
argument_list|(
name|TableScan
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|newRels
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelTableRef
name|tRef
range|:
name|extraTableRefs
control|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelNode
name|relNode
range|:
name|tableScanNodes
control|)
block|{
name|TableScan
name|scan
init|=
operator|(
name|TableScan
operator|)
name|relNode
decl_stmt|;
if|if
condition|(
name|tRef
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|equals
argument_list|(
name|scan
operator|.
name|getTable
argument_list|()
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|tRef
operator|.
name|getEntityNumber
argument_list|()
operator|==
name|i
operator|++
condition|)
block|{
name|newRels
operator|.
name|add
argument_list|(
name|relNode
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
assert|assert
name|extraTableRefs
operator|.
name|size
argument_list|()
operator|==
name|newRels
operator|.
name|size
argument_list|()
assert|;
name|relBuilder
operator|.
name|push
argument_list|(
name|input
argument_list|)
expr_stmt|;
for|for
control|(
name|RelNode
name|newRel
range|:
name|newRels
control|)
block|{
comment|// Add to the view
name|relBuilder
operator|.
name|push
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|newView
init|=
name|relBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|topViewProject
operator|!=
literal|null
condition|?
name|topViewProject
else|:
name|viewNode
argument_list|)
expr_stmt|;
for|for
control|(
name|RelNode
name|newRel
range|:
name|newRels
control|)
block|{
comment|// Add to the view plan
name|relBuilder
operator|.
name|push
argument_list|(
name|newRel
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|join
argument_list|(
name|JoinRelType
operator|.
name|INNER
argument_list|,
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|newViewNode
init|=
name|relBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
name|ViewPartialRewriting
operator|.
name|of
argument_list|(
name|newView
argument_list|,
literal|null
argument_list|,
name|newViewNode
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
annotation|@
name|Nullable
name|RelNode
name|rewriteQuery
parameter_list|(
name|RelBuilder
name|relBuilder
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexSimplify
name|simplify
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|RexNode
name|compensationColumnsEquiPred
parameter_list|,
name|RexNode
name|otherCompensationPred
parameter_list|,
annotation|@
name|Nullable
name|Project
name|topProject
parameter_list|,
name|RelNode
name|node
parameter_list|,
name|BiMap
argument_list|<
name|RelTableRef
argument_list|,
name|RelTableRef
argument_list|>
name|viewToQueryTableMapping
parameter_list|,
name|EquivalenceClasses
name|viewEC
parameter_list|,
name|EquivalenceClasses
name|queryEC
parameter_list|)
block|{
comment|// Our target node is the node below the root, which should have the maximum
comment|// number of available expressions in the tree in order to maximize our
comment|// number of rewritings.
comment|// We create a project on top. If the program is available, we execute
comment|// it to maximize rewriting opportunities. For instance, a program might
comment|// pull up all the expressions that are below the aggregate so we can
comment|// introduce compensation filters easily. This is important depending on
comment|// the planner strategy.
name|RelNode
name|newNode
init|=
name|node
decl_stmt|;
name|RelNode
name|target
init|=
name|node
decl_stmt|;
name|HepProgram
name|unionRewritingPullProgram
init|=
name|config
operator|.
name|unionRewritingPullProgram
argument_list|()
decl_stmt|;
if|if
condition|(
name|unionRewritingPullProgram
operator|!=
literal|null
condition|)
block|{
specifier|final
name|HepPlanner
name|tmpPlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|unionRewritingPullProgram
argument_list|)
decl_stmt|;
name|tmpPlanner
operator|.
name|setRoot
argument_list|(
name|newNode
argument_list|)
expr_stmt|;
name|newNode
operator|=
name|tmpPlanner
operator|.
name|findBestExp
argument_list|()
expr_stmt|;
name|target
operator|=
name|newNode
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|// All columns required by compensating predicates must be contained
comment|// in the query.
name|List
argument_list|<
name|RexNode
argument_list|>
name|queryExprs
init|=
name|extractReferences
argument_list|(
name|rexBuilder
argument_list|,
name|target
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|compensationColumnsEquiPred
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
name|RexNode
name|newCompensationColumnsEquiPred
init|=
name|rewriteExpression
argument_list|(
name|rexBuilder
argument_list|,
name|mq
argument_list|,
name|target
argument_list|,
name|target
argument_list|,
name|queryExprs
argument_list|,
name|viewToQueryTableMapping
operator|.
name|inverse
argument_list|()
argument_list|,
name|queryEC
argument_list|,
literal|false
argument_list|,
name|compensationColumnsEquiPred
argument_list|)
decl_stmt|;
if|if
condition|(
name|newCompensationColumnsEquiPred
operator|==
literal|null
condition|)
block|{
comment|// Skip it
return|return
literal|null
return|;
block|}
name|compensationColumnsEquiPred
operator|=
name|newCompensationColumnsEquiPred
expr_stmt|;
block|}
comment|// For the rest, we use the query equivalence classes
if|if
condition|(
operator|!
name|otherCompensationPred
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
name|RexNode
name|newOtherCompensationPred
init|=
name|rewriteExpression
argument_list|(
name|rexBuilder
argument_list|,
name|mq
argument_list|,
name|target
argument_list|,
name|target
argument_list|,
name|queryExprs
argument_list|,
name|viewToQueryTableMapping
operator|.
name|inverse
argument_list|()
argument_list|,
name|viewEC
argument_list|,
literal|true
argument_list|,
name|otherCompensationPred
argument_list|)
decl_stmt|;
if|if
condition|(
name|newOtherCompensationPred
operator|==
literal|null
condition|)
block|{
comment|// Skip it
return|return
literal|null
return|;
block|}
name|otherCompensationPred
operator|=
name|newOtherCompensationPred
expr_stmt|;
block|}
specifier|final
name|RexNode
name|queryCompensationPred
init|=
name|RexUtil
operator|.
name|not
argument_list|(
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|compensationColumnsEquiPred
argument_list|,
name|otherCompensationPred
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
comment|// Generate query rewriting.
name|RelNode
name|rewrittenPlan
init|=
name|relBuilder
operator|.
name|push
argument_list|(
name|target
argument_list|)
operator|.
name|filter
argument_list|(
name|simplify
operator|.
name|simplifyUnknownAsFalse
argument_list|(
name|queryCompensationPred
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|unionRewritingPullProgram
operator|!=
literal|null
condition|)
block|{
name|rewrittenPlan
operator|=
name|newNode
operator|.
name|copy
argument_list|(
name|newNode
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rewrittenPlan
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|topProject
operator|!=
literal|null
condition|)
block|{
return|return
name|topProject
operator|.
name|copy
argument_list|(
name|topProject
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rewrittenPlan
argument_list|)
argument_list|)
return|;
block|}
return|return
name|rewrittenPlan
return|;
block|}
annotation|@
name|Override
specifier|protected
annotation|@
name|Nullable
name|RelNode
name|createUnion
parameter_list|(
name|RelBuilder
name|relBuilder
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|,
annotation|@
name|Nullable
name|RelNode
name|topProject
parameter_list|,
name|RelNode
name|unionInputQuery
parameter_list|,
name|RelNode
name|unionInputView
parameter_list|)
block|{
name|relBuilder
operator|.
name|push
argument_list|(
name|unionInputQuery
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|push
argument_list|(
name|unionInputView
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|union
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|relBuilder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|relBuilder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
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
name|relBuilder
operator|.
name|peek
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
comment|// We can take unionInputQuery as it is query based.
name|RelDataTypeField
name|field
init|=
name|unionInputQuery
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|exprList
operator|.
name|add
argument_list|(
name|rexBuilder
operator|.
name|ensureType
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|relBuilder
operator|.
name|peek
argument_list|()
argument_list|,
name|i
argument_list|)
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|nameList
operator|.
name|add
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|relBuilder
operator|.
name|project
argument_list|(
name|exprList
argument_list|,
name|nameList
argument_list|)
expr_stmt|;
return|return
name|relBuilder
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
annotation|@
name|Nullable
name|RelNode
name|rewriteView
parameter_list|(
name|RelBuilder
name|relBuilder
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexSimplify
name|simplify
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|MatchModality
name|matchModality
parameter_list|,
name|boolean
name|unionRewriting
parameter_list|,
name|RelNode
name|input
parameter_list|,
annotation|@
name|Nullable
name|Project
name|topProject
parameter_list|,
name|RelNode
name|node
parameter_list|,
annotation|@
name|Nullable
name|Project
name|topViewProject
parameter_list|,
name|RelNode
name|viewNode
parameter_list|,
name|BiMap
argument_list|<
name|RelTableRef
argument_list|,
name|RelTableRef
argument_list|>
name|queryToViewTableMapping
parameter_list|,
name|EquivalenceClasses
name|queryEC
parameter_list|)
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprs
init|=
name|topProject
operator|==
literal|null
condition|?
name|extractReferences
argument_list|(
name|rexBuilder
argument_list|,
name|node
argument_list|)
else|:
name|topProject
operator|.
name|getProjects
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|exprsLineage
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|exprs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|expr
range|:
name|exprs
control|)
block|{
name|Set
argument_list|<
name|RexNode
argument_list|>
name|s
init|=
name|mq
operator|.
name|getExpressionLineage
argument_list|(
name|node
argument_list|,
name|expr
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
comment|// Bail out
return|return
literal|null
return|;
block|}
assert|assert
name|s
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
comment|// Rewrite expr. Take first element from the corresponding equivalence class
comment|// (no need to swap the table references following the table mapping)
name|exprsLineage
operator|.
name|add
argument_list|(
name|RexUtil
operator|.
name|swapColumnReferences
argument_list|(
name|rexBuilder
argument_list|,
name|s
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|,
name|queryEC
operator|.
name|getEquivalenceClassesMap
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|RexNode
argument_list|>
name|viewExprs
init|=
name|topViewProject
operator|==
literal|null
condition|?
name|extractReferences
argument_list|(
name|rexBuilder
argument_list|,
name|viewNode
argument_list|)
else|:
name|topViewProject
operator|.
name|getProjects
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|rewrittenExprs
init|=
name|rewriteExpressions
argument_list|(
name|rexBuilder
argument_list|,
name|mq
argument_list|,
name|input
argument_list|,
name|viewNode
argument_list|,
name|viewExprs
argument_list|,
name|queryToViewTableMapping
operator|.
name|inverse
argument_list|()
argument_list|,
name|queryEC
argument_list|,
literal|true
argument_list|,
name|exprsLineage
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrittenExprs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|input
argument_list|)
operator|.
name|project
argument_list|(
name|rewrittenExprs
argument_list|)
operator|.
name|convert
argument_list|(
name|topProject
operator|!=
literal|null
condition|?
name|topProject
operator|.
name|getRowType
argument_list|()
else|:
name|node
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|false
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
annotation|@
name|Nullable
name|RelNode
argument_list|,
name|RelNode
argument_list|>
name|pushFilterToOriginalViewPlan
parameter_list|(
name|RelBuilder
name|builder
parameter_list|,
annotation|@
name|Nullable
name|RelNode
name|topViewProject
parameter_list|,
name|RelNode
name|viewNode
parameter_list|,
name|RexNode
name|cond
parameter_list|)
block|{
comment|// Nothing to do
return|return
name|Pair
operator|.
name|of
argument_list|(
name|topViewProject
argument_list|,
name|viewNode
argument_list|)
return|;
block|}
block|}
end_class

end_unit

