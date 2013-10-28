begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Ord
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

begin_comment
comment|/**  * Relational expression which imposes a particular sort order on its input  * without otherwise changing its content.  */
end_comment

begin_class
specifier|public
class|class
name|SortRel
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RelCollation
name|collation
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|fieldExps
decl_stmt|;
specifier|public
specifier|final
name|RexNode
name|offset
decl_stmt|;
specifier|public
specifier|final
name|RexNode
name|fetch
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a sorter.      *      * @param cluster Cluster this relational expression belongs to      * @param traits Traits      * @param child input relational expression      * @param collation array of sort specifications      */
specifier|public
name|SortRel
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
name|RelCollation
name|collation
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|child
argument_list|,
name|collation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a sorter.      *      * @param cluster Cluster this relational expression belongs to      * @param traits Traits      * @param child input relational expression      * @param collation array of sort specifications      * @param offset Expression for number of rows to discard before returning      *               first row      * @param fetch Expression for number of rows to fetch      */
specifier|public
name|SortRel
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
name|RelCollation
name|collation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
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
name|collation
operator|=
name|collation
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|fetch
operator|=
name|fetch
expr_stmt|;
assert|assert
name|traits
operator|.
name|containsIfApplicable
argument_list|(
name|collation
argument_list|)
operator|:
literal|"traits="
operator|+
name|traits
operator|+
literal|", collation="
operator|+
name|collation
assert|;
assert|assert
operator|!
operator|(
name|fetch
operator|==
literal|null
operator|&&
name|offset
operator|==
literal|null
operator|&&
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|)
operator|:
literal|"trivial sort"
assert|;
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RexNode
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelFieldCollation
name|field
range|:
name|collation
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
name|int
name|index
init|=
name|field
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|cluster
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeInputRef
argument_list|(
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|fieldExps
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SortRel
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
name|collation
argument_list|)
return|;
block|}
specifier|public
name|SortRel
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|newInput
argument_list|,
name|newCollation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
specifier|public
name|SortRel
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|contains
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|SortRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|newInput
argument_list|,
name|newCollation
argument_list|,
name|offset
argument_list|,
name|fetch
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getChildExps
parameter_list|()
block|{
return|return
name|fieldExps
return|;
block|}
comment|/**      * Returns the array of {@link RelFieldCollation}s asked for by the sort      * specification, from most significant to least significant.      *      *<p>See also {@link #getCollationList()}, inherited from {@link RelNode},      * which lists all known collations. For example,      *<code>ORDER BY time_id</code> might also be sorted by      *<code>the_year, the_month</code> because of a known monotonicity      * constraint among the columns. {@code getCollations} would return      *<code>[time_id]</code> and {@code getCollationList} would return      *<code>[ [time_id], [the_year, the_month] ]</code>.</p>      */
specifier|public
name|RelCollation
name|getCollation
parameter_list|()
block|{
return|return
name|collation
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
comment|// TODO: include each prefix of the collation, e.g [[x, y], [x], []]
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|getCollation
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelOptPlanWriter
name|explainTerms
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
expr_stmt|;
assert|assert
name|fieldExps
operator|.
name|size
argument_list|()
operator|==
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|size
argument_list|()
assert|;
for|for
control|(
name|Ord
argument_list|<
name|RexNode
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|fieldExps
argument_list|)
control|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"sort"
operator|+
name|ord
operator|.
name|i
argument_list|,
name|ord
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Ord
argument_list|<
name|RelFieldCollation
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|collation
operator|.
name|getFieldCollations
argument_list|()
argument_list|)
control|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"dir"
operator|+
name|ord
operator|.
name|i
argument_list|,
name|ord
operator|.
name|e
operator|.
name|shortString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|itemIf
argument_list|(
literal|"offset"
argument_list|,
name|offset
argument_list|,
name|offset
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|pw
operator|.
name|itemIf
argument_list|(
literal|"fetch"
argument_list|,
name|fetch
argument_list|,
name|fetch
operator|!=
literal|null
argument_list|)
expr_stmt|;
return|return
name|pw
return|;
block|}
block|}
end_class

begin_comment
comment|// End SortRel.java
end_comment

end_unit

