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
name|metadata
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
name|RelOptPredicateList
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
name|hep
operator|.
name|HepRelVertex
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
name|volcano
operator|.
name|RelSubset
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
name|Aggregate
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
name|Exchange
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
name|Join
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
name|Sort
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
name|core
operator|.
name|Union
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
name|RexInputRef
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
name|RexTableInputRef
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
name|sql
operator|.
name|validate
operator|.
name|SqlValidatorUtil
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
name|BuiltInMethod
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
name|ImmutableBitSet
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
name|HashMultimap
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
name|Iterables
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashSet
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
name|Map
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
comment|/**  * Utility to extract Predicates that are present in the (sub)plan  * starting at this node.  *  *<p>This should be used to infer whether same filters are applied on  * a given plan by materialized view rewriting rules.  *  *<p>The output predicates might contain references to columns produced  * by TableScan operators ({@link RexTableInputRef}). In turn, each TableScan  * operator is identified uniquely by its qualified name and an identifier.  *  *<p>If the provider cannot infer the lineage for any of the expressions  * contain in any of the predicates, it will return null. Observe that  * this is different from the empty list of predicates, which means that  * there are not predicates in the (sub)plan.  *  */
end_comment

begin_class
specifier|public
class|class
name|RelMdAllPredicates
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|AllPredicates
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|RelMetadataProvider
name|SOURCE
init|=
name|ReflectiveRelMetadataProvider
operator|.
name|reflectiveSource
argument_list|(
name|BuiltInMethod
operator|.
name|ALL_PREDICATES
operator|.
name|method
argument_list|,
operator|new
name|RelMdAllPredicates
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|AllPredicates
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|AllPredicates
operator|.
name|DEF
return|;
block|}
comment|/** Catch-all implementation for    * {@link BuiltInMetadata.AllPredicates#getAllPredicates()},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#getAllPredicates(RelNode)    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|HepRelVertex
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|rel
operator|.
name|getCurrentRel
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|RelSubset
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|rel
operator|.
name|getBest
argument_list|()
argument_list|,
name|rel
operator|.
name|getOriginal
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Extract predicates for a table scan.    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|TableScan
name|table
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|RelOptPredicateList
operator|.
name|EMPTY
return|;
block|}
comment|/**    * Extract predicates for a project.    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|Project
name|project
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Add the Filter condition to the list obtained from the input.    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|RelNode
name|input
init|=
name|filter
operator|.
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|filter
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|pred
init|=
name|filter
operator|.
name|getCondition
argument_list|()
decl_stmt|;
specifier|final
name|RelOptPredicateList
name|predsBelow
init|=
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|predsBelow
operator|==
literal|null
condition|)
block|{
comment|// Safety check
return|return
literal|null
return|;
block|}
comment|// Extract input fields referenced by Filter condition
specifier|final
name|Set
argument_list|<
name|RelDataTypeField
argument_list|>
name|inputExtraFields
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelOptUtil
operator|.
name|InputFinder
name|inputFinder
init|=
operator|new
name|RelOptUtil
operator|.
name|InputFinder
argument_list|(
name|inputExtraFields
argument_list|)
decl_stmt|;
name|pred
operator|.
name|accept
argument_list|(
name|inputFinder
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableBitSet
name|inputFieldsUsed
init|=
name|inputFinder
operator|.
name|inputBitSet
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Infer column origin expressions for given references
specifier|final
name|Map
argument_list|<
name|RexInputRef
argument_list|,
name|Set
argument_list|<
name|RexNode
argument_list|>
argument_list|>
name|mapping
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
range|:
name|inputFieldsUsed
control|)
block|{
specifier|final
name|RexInputRef
name|ref
init|=
name|RexInputRef
operator|.
name|of
argument_list|(
name|idx
argument_list|,
name|filter
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|RexNode
argument_list|>
name|originalExprs
init|=
name|mq
operator|.
name|getExpressionLineage
argument_list|(
name|filter
argument_list|,
name|ref
argument_list|)
decl_stmt|;
if|if
condition|(
name|originalExprs
operator|==
literal|null
condition|)
block|{
comment|// Bail out
return|return
literal|null
return|;
block|}
name|mapping
operator|.
name|put
argument_list|(
name|ref
argument_list|,
name|originalExprs
argument_list|)
expr_stmt|;
block|}
comment|// Replace with new expressions and return union of predicates
specifier|final
name|Set
argument_list|<
name|RexNode
argument_list|>
name|allExprs
init|=
name|RelMdExpressionLineage
operator|.
name|createAllPossibleExpressions
argument_list|(
name|rexBuilder
argument_list|,
name|pred
argument_list|,
name|mapping
argument_list|)
decl_stmt|;
if|if
condition|(
name|allExprs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|predsBelow
operator|.
name|union
argument_list|(
name|rexBuilder
argument_list|,
name|RelOptPredicateList
operator|.
name|of
argument_list|(
name|rexBuilder
argument_list|,
name|allExprs
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Add the Join condition to the list obtained from the input.    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|Join
name|join
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
if|if
condition|(
name|join
operator|.
name|getJoinType
argument_list|()
operator|.
name|isOuterJoin
argument_list|()
condition|)
block|{
comment|// We cannot map origin of this expression.
return|return
literal|null
return|;
block|}
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|join
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|pred
init|=
name|join
operator|.
name|getCondition
argument_list|()
decl_stmt|;
specifier|final
name|Multimap
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|RelTableRef
argument_list|>
name|qualifiedNamesToRefs
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|RelOptPredicateList
name|newPreds
init|=
name|RelOptPredicateList
operator|.
name|EMPTY
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|join
operator|.
name|getInputs
argument_list|()
control|)
block|{
specifier|final
name|RelOptPredicateList
name|inputPreds
init|=
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputPreds
operator|==
literal|null
condition|)
block|{
comment|// Bail out
return|return
literal|null
return|;
block|}
comment|// Gather table references
specifier|final
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|tableRefs
init|=
name|mq
operator|.
name|getTableReferences
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|input
operator|==
name|join
operator|.
name|getLeft
argument_list|()
condition|)
block|{
comment|// Left input references remain unchanged
for|for
control|(
name|RelTableRef
name|leftRef
range|:
name|tableRefs
control|)
block|{
name|qualifiedNamesToRefs
operator|.
name|put
argument_list|(
name|leftRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|leftRef
argument_list|)
expr_stmt|;
block|}
name|newPreds
operator|=
name|newPreds
operator|.
name|union
argument_list|(
name|rexBuilder
argument_list|,
name|inputPreds
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Right input references might need to be updated if there are table name
comment|// clashes with left input
specifier|final
name|Map
argument_list|<
name|RelTableRef
argument_list|,
name|RelTableRef
argument_list|>
name|currentTablesMapping
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelTableRef
name|rightRef
range|:
name|tableRefs
control|)
block|{
name|int
name|shift
init|=
literal|0
decl_stmt|;
name|Collection
argument_list|<
name|RelTableRef
argument_list|>
name|lRefs
init|=
name|qualifiedNamesToRefs
operator|.
name|get
argument_list|(
name|rightRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lRefs
operator|!=
literal|null
condition|)
block|{
name|shift
operator|=
name|lRefs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|currentTablesMapping
operator|.
name|put
argument_list|(
name|rightRef
argument_list|,
name|RelTableRef
operator|.
name|of
argument_list|(
name|rightRef
operator|.
name|getTable
argument_list|()
argument_list|,
name|shift
operator|+
name|rightRef
operator|.
name|getEntityNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|updatedPreds
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|inputPreds
operator|.
name|pulledUpPredicates
argument_list|,
name|e
lambda|->
name|RexUtil
operator|.
name|swapTableReferences
argument_list|(
name|rexBuilder
argument_list|,
name|e
argument_list|,
name|currentTablesMapping
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|newPreds
operator|=
name|newPreds
operator|.
name|union
argument_list|(
name|rexBuilder
argument_list|,
name|RelOptPredicateList
operator|.
name|of
argument_list|(
name|rexBuilder
argument_list|,
name|updatedPreds
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Extract input fields referenced by Join condition
specifier|final
name|Set
argument_list|<
name|RelDataTypeField
argument_list|>
name|inputExtraFields
init|=
operator|new
name|LinkedHashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelOptUtil
operator|.
name|InputFinder
name|inputFinder
init|=
operator|new
name|RelOptUtil
operator|.
name|InputFinder
argument_list|(
name|inputExtraFields
argument_list|)
decl_stmt|;
name|pred
operator|.
name|accept
argument_list|(
name|inputFinder
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableBitSet
name|inputFieldsUsed
init|=
name|inputFinder
operator|.
name|inputBitSet
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Infer column origin expressions for given references
specifier|final
name|Map
argument_list|<
name|RexInputRef
argument_list|,
name|Set
argument_list|<
name|RexNode
argument_list|>
argument_list|>
name|mapping
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|fullRowType
init|=
name|SqlValidatorUtil
operator|.
name|createJoinType
argument_list|(
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|join
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|idx
range|:
name|inputFieldsUsed
control|)
block|{
specifier|final
name|RexInputRef
name|inputRef
init|=
name|RexInputRef
operator|.
name|of
argument_list|(
name|idx
argument_list|,
name|fullRowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|RexNode
argument_list|>
name|originalExprs
init|=
name|mq
operator|.
name|getExpressionLineage
argument_list|(
name|join
argument_list|,
name|inputRef
argument_list|)
decl_stmt|;
if|if
condition|(
name|originalExprs
operator|==
literal|null
condition|)
block|{
comment|// Bail out
return|return
literal|null
return|;
block|}
specifier|final
name|RexInputRef
name|ref
init|=
name|RexInputRef
operator|.
name|of
argument_list|(
name|idx
argument_list|,
name|fullRowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
decl_stmt|;
name|mapping
operator|.
name|put
argument_list|(
name|ref
argument_list|,
name|originalExprs
argument_list|)
expr_stmt|;
block|}
comment|// Replace with new expressions and return union of predicates
specifier|final
name|Set
argument_list|<
name|RexNode
argument_list|>
name|allExprs
init|=
name|RelMdExpressionLineage
operator|.
name|createAllPossibleExpressions
argument_list|(
name|rexBuilder
argument_list|,
name|pred
argument_list|,
name|mapping
argument_list|)
decl_stmt|;
if|if
condition|(
name|allExprs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|newPreds
operator|.
name|union
argument_list|(
name|rexBuilder
argument_list|,
name|RelOptPredicateList
operator|.
name|of
argument_list|(
name|rexBuilder
argument_list|,
name|allExprs
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Extract predicates for an Aggregate.    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|Aggregate
name|agg
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|agg
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Extract predicates for a Union.    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|Union
name|union
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|union
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Multimap
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|,
name|RelTableRef
argument_list|>
name|qualifiedNamesToRefs
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
name|RelOptPredicateList
name|newPreds
init|=
name|RelOptPredicateList
operator|.
name|EMPTY
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
name|union
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RelNode
name|input
init|=
name|union
operator|.
name|getInput
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|RelOptPredicateList
name|inputPreds
init|=
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputPreds
operator|==
literal|null
condition|)
block|{
comment|// Bail out
return|return
literal|null
return|;
block|}
comment|// Gather table references
specifier|final
name|Set
argument_list|<
name|RelTableRef
argument_list|>
name|tableRefs
init|=
name|mq
operator|.
name|getTableReferences
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
comment|// Left input references remain unchanged
for|for
control|(
name|RelTableRef
name|leftRef
range|:
name|tableRefs
control|)
block|{
name|qualifiedNamesToRefs
operator|.
name|put
argument_list|(
name|leftRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|leftRef
argument_list|)
expr_stmt|;
block|}
name|newPreds
operator|=
name|newPreds
operator|.
name|union
argument_list|(
name|rexBuilder
argument_list|,
name|inputPreds
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Right input references might need to be updated if there are table name
comment|// clashes with left input
specifier|final
name|Map
argument_list|<
name|RelTableRef
argument_list|,
name|RelTableRef
argument_list|>
name|currentTablesMapping
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|RelTableRef
name|rightRef
range|:
name|tableRefs
control|)
block|{
name|int
name|shift
init|=
literal|0
decl_stmt|;
name|Collection
argument_list|<
name|RelTableRef
argument_list|>
name|lRefs
init|=
name|qualifiedNamesToRefs
operator|.
name|get
argument_list|(
name|rightRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lRefs
operator|!=
literal|null
condition|)
block|{
name|shift
operator|=
name|lRefs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
name|currentTablesMapping
operator|.
name|put
argument_list|(
name|rightRef
argument_list|,
name|RelTableRef
operator|.
name|of
argument_list|(
name|rightRef
operator|.
name|getTable
argument_list|()
argument_list|,
name|shift
operator|+
name|rightRef
operator|.
name|getEntityNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Add to existing qualified names
for|for
control|(
name|RelTableRef
name|newRef
range|:
name|currentTablesMapping
operator|.
name|values
argument_list|()
control|)
block|{
name|qualifiedNamesToRefs
operator|.
name|put
argument_list|(
name|newRef
operator|.
name|getQualifiedName
argument_list|()
argument_list|,
name|newRef
argument_list|)
expr_stmt|;
block|}
comment|// Update preds
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|updatedPreds
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|Iterables
operator|.
name|transform
argument_list|(
name|inputPreds
operator|.
name|pulledUpPredicates
argument_list|,
name|e
lambda|->
name|RexUtil
operator|.
name|swapTableReferences
argument_list|(
name|rexBuilder
argument_list|,
name|e
argument_list|,
name|currentTablesMapping
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|newPreds
operator|=
name|newPreds
operator|.
name|union
argument_list|(
name|rexBuilder
argument_list|,
name|RelOptPredicateList
operator|.
name|of
argument_list|(
name|rexBuilder
argument_list|,
name|updatedPreds
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|newPreds
return|;
block|}
comment|/**    * Extract predicates for a Sort.    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|Sort
name|sort
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|sort
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Extract predicates for an Exchange.    */
specifier|public
name|RelOptPredicateList
name|getAllPredicates
parameter_list|(
name|Exchange
name|exchange
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAllPredicates
argument_list|(
name|exchange
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdAllPredicates.java
end_comment

end_unit

