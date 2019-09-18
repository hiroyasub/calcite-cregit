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
operator|.
name|volcano
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
name|RelOptListener
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
name|RelTrait
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
name|RelTraitDef
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
name|util
operator|.
name|trace
operator|.
name|CalciteTrace
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
name|org
operator|.
name|slf4j
operator|.
name|Logger
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
comment|/**  * A<code>RelSet</code> is an equivalence-set of expressions; that is, a set of  * expressions which have identical semantics. We are generally interested in  * using the expression which has the lowest cost.  *  *<p>All of the expressions in an<code>RelSet</code> have the same calling  * convention.</p>  */
end_comment

begin_class
class|class
name|RelSet
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|rels
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Relational expressions that have a subset in this set as a child. This    * is a multi-set. If multiple relational expressions in this set have the    * same parent, there will be multiple entries.    */
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|parents
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelSubset
argument_list|>
name|subsets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * List of {@link AbstractConverter} objects which have not yet been    * satisfied.    */
specifier|final
name|List
argument_list|<
name|AbstractConverter
argument_list|>
name|abstractConverters
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Set to the superseding set when this is found to be equivalent to another    * set.    */
name|RelSet
name|equivalentSet
decl_stmt|;
name|RelNode
name|rel
decl_stmt|;
comment|/**    * Variables that are set by relational expressions in this set    * and available for use by parent and child expressions.    */
specifier|final
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesPropagated
decl_stmt|;
comment|/**    * Variables that are used by relational expressions in this set.    */
specifier|final
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesUsed
decl_stmt|;
specifier|final
name|int
name|id
decl_stmt|;
comment|/**    * Reentrancy flag.    */
name|boolean
name|inMetadataQuery
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|RelSet
parameter_list|(
name|int
name|id
parameter_list|,
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesPropagated
parameter_list|,
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesUsed
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|variablesPropagated
operator|=
name|variablesPropagated
expr_stmt|;
name|this
operator|.
name|variablesUsed
operator|=
name|variablesUsed
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns all of the {@link RelNode}s which reference {@link RelNode}s in    * this set.    */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getParentRels
parameter_list|()
block|{
return|return
name|parents
return|;
block|}
comment|/**    * @return all of the {@link RelNode}s contained by any subset of this set    * (does not include the subset objects themselves)    */
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getRelsFromAllSubsets
parameter_list|()
block|{
return|return
name|rels
return|;
block|}
specifier|public
name|RelSubset
name|getSubset
parameter_list|(
name|RelTraitSet
name|traits
parameter_list|)
block|{
for|for
control|(
name|RelSubset
name|subset
range|:
name|subsets
control|)
block|{
if|if
condition|(
name|subset
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|traits
argument_list|)
condition|)
block|{
return|return
name|subset
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Removes all references to a specific {@link RelNode} in both the subsets    * and their parent relationships.    */
name|void
name|obliterateRelNode
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|parents
operator|.
name|remove
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds a relational expression to a set, with its results available under a    * particular calling convention. An expression may be in the set several    * times with different calling conventions (and hence different costs).    */
specifier|public
name|RelSubset
name|add
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
assert|assert
name|equivalentSet
operator|==
literal|null
operator|:
literal|"adding to a dead set"
assert|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|simplify
argument_list|()
decl_stmt|;
specifier|final
name|RelSubset
name|subset
init|=
name|getOrCreateSubset
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|)
decl_stmt|;
name|subset
operator|.
name|add
argument_list|(
name|rel
argument_list|)
expr_stmt|;
return|return
name|subset
return|;
block|}
specifier|private
name|void
name|addAbstractConverters
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|,
name|RelOptCluster
name|cluster
parameter_list|,
name|RelSubset
name|subset
parameter_list|,
name|boolean
name|subsetToOthers
parameter_list|)
block|{
comment|// Converters from newly introduced subset to all the remaining one (vice versa), only if
comment|// we can convert.  No point adding converters if it is not possible.
for|for
control|(
name|RelSubset
name|other
range|:
name|subsets
control|)
block|{
assert|assert
name|other
operator|.
name|getTraitSet
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|subset
operator|.
name|getTraitSet
argument_list|()
operator|.
name|size
argument_list|()
assert|;
if|if
condition|(
operator|(
name|other
operator|==
name|subset
operator|)
operator|||
operator|(
name|subsetToOthers
operator|&&
operator|!
name|subset
operator|.
name|getConvention
argument_list|()
operator|.
name|useAbstractConvertersForConversion
argument_list|(
name|subset
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|other
operator|.
name|getTraitSet
argument_list|()
argument_list|)
operator|)
operator|||
operator|(
operator|!
name|subsetToOthers
operator|&&
operator|!
name|other
operator|.
name|getConvention
argument_list|()
operator|.
name|useAbstractConvertersForConversion
argument_list|(
name|other
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|subset
operator|.
name|getTraitSet
argument_list|()
argument_list|)
operator|)
condition|)
block|{
continue|continue;
block|}
specifier|final
name|ImmutableList
argument_list|<
name|RelTrait
argument_list|>
name|difference
init|=
name|subset
operator|.
name|getTraitSet
argument_list|()
operator|.
name|difference
argument_list|(
name|other
operator|.
name|getTraitSet
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|addAbstractConverter
init|=
literal|true
decl_stmt|;
name|int
name|numTraitNeedConvert
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelTrait
name|curOtherTrait
range|:
name|difference
control|)
block|{
name|RelTraitDef
name|traitDef
init|=
name|curOtherTrait
operator|.
name|getTraitDef
argument_list|()
decl_stmt|;
name|RelTrait
name|curRelTrait
init|=
name|subset
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTrait
argument_list|(
name|traitDef
argument_list|)
decl_stmt|;
if|if
condition|(
name|curRelTrait
operator|==
literal|null
condition|)
block|{
name|addAbstractConverter
operator|=
literal|false
expr_stmt|;
break|break;
block|}
assert|assert
name|curRelTrait
operator|.
name|getTraitDef
argument_list|()
operator|==
name|traitDef
assert|;
name|boolean
name|canConvert
init|=
literal|false
decl_stmt|;
name|boolean
name|needConvert
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|subsetToOthers
condition|)
block|{
comment|// We can convert from subset to other.  So, add converter with subset as child and
comment|// traitset as the other's traitset.
name|canConvert
operator|=
name|traitDef
operator|.
name|canConvert
argument_list|(
name|cluster
operator|.
name|getPlanner
argument_list|()
argument_list|,
name|curRelTrait
argument_list|,
name|curOtherTrait
argument_list|,
name|subset
argument_list|)
expr_stmt|;
name|needConvert
operator|=
operator|!
name|curRelTrait
operator|.
name|satisfies
argument_list|(
name|curOtherTrait
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// We can convert from others to subset.
name|canConvert
operator|=
name|traitDef
operator|.
name|canConvert
argument_list|(
name|cluster
operator|.
name|getPlanner
argument_list|()
argument_list|,
name|curOtherTrait
argument_list|,
name|curRelTrait
argument_list|,
name|other
argument_list|)
expr_stmt|;
name|needConvert
operator|=
operator|!
name|curOtherTrait
operator|.
name|satisfies
argument_list|(
name|curRelTrait
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|canConvert
condition|)
block|{
name|addAbstractConverter
operator|=
literal|false
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|needConvert
condition|)
block|{
name|numTraitNeedConvert
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|addAbstractConverter
operator|&&
name|numTraitNeedConvert
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|subsetToOthers
condition|)
block|{
specifier|final
name|AbstractConverter
name|converter
init|=
operator|new
name|AbstractConverter
argument_list|(
name|cluster
argument_list|,
name|subset
argument_list|,
literal|null
argument_list|,
name|other
operator|.
name|getTraitSet
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|register
argument_list|(
name|converter
argument_list|,
name|other
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|AbstractConverter
name|converter
init|=
operator|new
name|AbstractConverter
argument_list|(
name|cluster
argument_list|,
name|other
argument_list|,
literal|null
argument_list|,
name|subset
operator|.
name|getTraitSet
argument_list|()
argument_list|)
decl_stmt|;
name|planner
operator|.
name|register
argument_list|(
name|converter
argument_list|,
name|subset
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
name|RelSubset
name|getOrCreateSubset
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|)
block|{
name|RelSubset
name|subset
init|=
name|getSubset
argument_list|(
name|traits
argument_list|)
decl_stmt|;
if|if
condition|(
name|subset
operator|==
literal|null
condition|)
block|{
name|subset
operator|=
operator|new
name|RelSubset
argument_list|(
name|cluster
argument_list|,
name|this
argument_list|,
name|traits
argument_list|)
expr_stmt|;
specifier|final
name|VolcanoPlanner
name|planner
init|=
operator|(
name|VolcanoPlanner
operator|)
name|cluster
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
name|addAbstractConverters
argument_list|(
name|planner
argument_list|,
name|cluster
argument_list|,
name|subset
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Need to first add to subset before adding the abstract converters (for others->subset)
comment|// since otherwise during register() the planner will try to add this subset again.
name|subsets
operator|.
name|add
argument_list|(
name|subset
argument_list|)
expr_stmt|;
name|addAbstractConverters
argument_list|(
name|planner
argument_list|,
name|cluster
argument_list|,
name|subset
argument_list|,
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|planner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|postEquivalenceEvent
argument_list|(
name|planner
argument_list|,
name|subset
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|subset
return|;
block|}
specifier|private
name|void
name|postEquivalenceEvent
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
name|RelOptListener
operator|.
name|RelEquivalenceEvent
name|event
init|=
operator|new
name|RelOptListener
operator|.
name|RelEquivalenceEvent
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|,
literal|"equivalence class "
operator|+
name|id
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|planner
operator|.
name|listener
operator|.
name|relEquivalenceFound
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds an expression<code>rel</code> to this set, without creating a    * {@link org.apache.calcite.plan.volcano.RelSubset}. (Called only from    * {@link org.apache.calcite.plan.volcano.RelSubset#add}.    *    * @param rel Relational expression    */
name|void
name|addInternal
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rels
operator|.
name|contains
argument_list|(
name|rel
argument_list|)
condition|)
block|{
name|rels
operator|.
name|add
argument_list|(
name|rel
argument_list|)
expr_stmt|;
for|for
control|(
name|RelTrait
name|trait
range|:
name|rel
operator|.
name|getTraitSet
argument_list|()
control|)
block|{
assert|assert
name|trait
operator|==
name|trait
operator|.
name|getTraitDef
argument_list|()
operator|.
name|canonize
argument_list|(
name|trait
argument_list|)
assert|;
block|}
name|VolcanoPlanner
name|planner
init|=
operator|(
name|VolcanoPlanner
operator|)
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
if|if
condition|(
name|planner
operator|.
name|listener
operator|!=
literal|null
condition|)
block|{
name|postEquivalenceEvent
argument_list|(
name|planner
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|this
operator|.
name|rel
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
block|}
else|else
block|{
comment|// Row types must be the same, except for field names.
name|RelOptUtil
operator|.
name|verifyTypeEquivalence
argument_list|(
name|this
operator|.
name|rel
argument_list|,
name|rel
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Merges<code>otherSet</code> into this RelSet.    *    *<p>One generally calls this method after discovering that two relational    * expressions are equivalent, and hence the<code>RelSet</code>s they    * belong to are equivalent also.    *    *<p>After this method completes,<code>otherSet</code> is obsolete, its    * {@link #equivalentSet} member points to this RelSet, and this RelSet is    * still alive.    *    * @param planner  Planner    * @param otherSet RelSet which is equivalent to this one    */
name|void
name|mergeWith
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|,
name|RelSet
name|otherSet
parameter_list|)
block|{
assert|assert
name|this
operator|!=
name|otherSet
assert|;
assert|assert
name|this
operator|.
name|equivalentSet
operator|==
literal|null
assert|;
assert|assert
name|otherSet
operator|.
name|equivalentSet
operator|==
literal|null
assert|;
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"Merge set#{} into set#{}"
argument_list|,
name|otherSet
operator|.
name|id
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|otherSet
operator|.
name|equivalentSet
operator|=
name|this
expr_stmt|;
comment|// remove from table
name|boolean
name|existed
init|=
name|planner
operator|.
name|allSets
operator|.
name|remove
argument_list|(
name|otherSet
argument_list|)
decl_stmt|;
assert|assert
name|existed
operator|:
literal|"merging with a dead otherSet"
assert|;
comment|// merge subsets
for|for
control|(
name|RelSubset
name|otherSubset
range|:
name|otherSet
operator|.
name|subsets
control|)
block|{
name|planner
operator|.
name|ruleQueue
operator|.
name|subsetImportances
operator|.
name|remove
argument_list|(
name|otherSubset
argument_list|)
expr_stmt|;
name|RelSubset
name|subset
init|=
name|getOrCreateSubset
argument_list|(
name|otherSubset
operator|.
name|getCluster
argument_list|()
argument_list|,
name|otherSubset
operator|.
name|getTraitSet
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|otherSubset
operator|.
name|bestCost
operator|.
name|isLt
argument_list|(
name|subset
operator|.
name|bestCost
argument_list|)
condition|)
block|{
name|subset
operator|.
name|bestCost
operator|=
name|otherSubset
operator|.
name|bestCost
expr_stmt|;
name|subset
operator|.
name|best
operator|=
name|otherSubset
operator|.
name|best
expr_stmt|;
block|}
for|for
control|(
name|RelNode
name|otherRel
range|:
name|otherSubset
operator|.
name|getRels
argument_list|()
control|)
block|{
name|planner
operator|.
name|reregister
argument_list|(
name|this
argument_list|,
name|otherRel
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Has another set merged with this?
assert|assert
name|equivalentSet
operator|==
literal|null
assert|;
comment|// Update all rels which have a child in the other set, to reflect the
comment|// fact that the child has been renamed.
comment|//
comment|// Copy array to prevent ConcurrentModificationException.
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|previousParents
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|otherSet
operator|.
name|getParentRels
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RelNode
name|parentRel
range|:
name|previousParents
control|)
block|{
name|planner
operator|.
name|rename
argument_list|(
name|parentRel
argument_list|)
expr_stmt|;
block|}
comment|// Renaming may have caused this set to merge with another. If so,
comment|// this set is now obsolete. There's no need to update the children
comment|// of this set - indeed, it could be dangerous.
if|if
condition|(
name|equivalentSet
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
comment|// Make sure the cost changes as a result of merging are propagated.
specifier|final
name|Set
argument_list|<
name|RelSubset
argument_list|>
name|activeSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|parentRel
range|:
name|getParentRels
argument_list|()
control|)
block|{
specifier|final
name|RelSubset
name|parentSubset
init|=
name|planner
operator|.
name|getSubset
argument_list|(
name|parentRel
argument_list|)
decl_stmt|;
name|parentSubset
operator|.
name|propagateCostImprovements
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|,
name|parentRel
argument_list|,
name|activeSet
argument_list|)
expr_stmt|;
block|}
assert|assert
name|activeSet
operator|.
name|isEmpty
argument_list|()
assert|;
assert|assert
name|equivalentSet
operator|==
literal|null
assert|;
comment|// Each of the relations in the old set now has new parents, so
comment|// potentially new rules can fire. Check for rule matches, just as if
comment|// it were newly registered.  (This may cause rules which have fired
comment|// once to fire again.)
for|for
control|(
name|RelNode
name|rel
range|:
name|rels
control|)
block|{
assert|assert
name|planner
operator|.
name|getSet
argument_list|(
name|rel
argument_list|)
operator|==
name|this
assert|;
name|planner
operator|.
name|fireRules
argument_list|(
name|rel
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End RelSet.java
end_comment

end_unit

