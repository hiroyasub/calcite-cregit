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
name|RelOptTable
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
name|sql
operator|.
name|SqlExplainLevel
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * RelMetadataQuery provides a strongly-typed facade on top of  * {@link RelMetadataProvider} for the set of relational expression metadata  * queries defined as standard within Calcite. The Javadoc on these methods  * serves as their primary specification.  *  *<p>To add a new standard query<code>Xyz</code> to this interface, follow  * these steps:  *  *<ol>  *<li>Add a static method<code>getXyz</code> specification to this class.  *<li>Add unit tests to {@code org.apache.calcite.test.RelMetadataTest}.  *<li>Write a new provider class<code>RelMdXyz</code> in this package. Follow  * the pattern from an existing class such as {@link RelMdColumnOrigins},  * overloading on all of the logical relational expressions to which the query  * applies.  *<li>Add a {@code SOURCE} static member, similar to  *     {@link RelMdColumnOrigins#SOURCE}.  *<li>Register the {@code SOURCE} object in {@link DefaultRelMetadataProvider}.  *<li>Get unit tests working.  *</ol>  *  *<p>Because relational expression metadata is extensible, extension projects  * can define similar facades in order to specify access to custom metadata.  * Please do not add queries here (nor on {@link RelNode}) which lack meaning  * outside of your extension.  *  *<p>Besides adding new metadata queries, extension projects may need to add  * custom providers for the standard queries in order to handle additional  * relational expressions (either logical or physical). In either case, the  * process is the same: write a reflective provider and chain it on to an  * instance of {@link DefaultRelMetadataProvider}, prepending it to the default  * providers. Then supply that instance to the planner via the appropriate  * plugin mechanism.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RelMetadataQuery
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the    * {@link BuiltInMetadata.RowCount#getRowCount()}    * statistic.    *    * @param rel the relational expression    * @return estimated row count, or null if no reliable estimate can be    * determined    */
specifier|public
specifier|static
name|Double
name|getRowCount
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|RowCount
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|RowCount
operator|.
name|class
argument_list|)
decl_stmt|;
name|Double
name|result
init|=
name|metadata
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
return|return
name|validateResult
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.CumulativeCost#getCumulativeCost()}    * statistic.    *    * @param rel the relational expression    * @return estimated cost, or null if no reliable estimate can be determined    */
specifier|public
specifier|static
name|RelOptCost
name|getCumulativeCost
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|CumulativeCost
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|CumulativeCost
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|getCumulativeCost
argument_list|()
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.NonCumulativeCost#getNonCumulativeCost()}    * statistic.    *    * @param rel the relational expression    * @return estimated cost, or null if no reliable estimate can be determined    */
specifier|public
specifier|static
name|RelOptCost
name|getNonCumulativeCost
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|NonCumulativeCost
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|NonCumulativeCost
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|getNonCumulativeCost
argument_list|()
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.PercentageOriginalRows#getPercentageOriginalRows()}    * statistic.    *    * @param rel the relational expression    * @return estimated percentage (between 0.0 and 1.0), or null if no    * reliable estimate can be determined    */
specifier|public
specifier|static
name|Double
name|getPercentageOriginalRows
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|PercentageOriginalRows
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|PercentageOriginalRows
operator|.
name|class
argument_list|)
decl_stmt|;
name|Double
name|result
init|=
name|metadata
operator|.
name|getPercentageOriginalRows
argument_list|()
decl_stmt|;
assert|assert
name|isPercentage
argument_list|(
name|result
argument_list|,
literal|true
argument_list|)
assert|;
return|return
name|result
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.ColumnOrigin#getColumnOrigins(int)}    * statistic.    *    * @param rel           the relational expression    * @param column 0-based ordinal for output column of interest    * @return set of origin columns, or null if this information cannot be    * determined (whereas empty set indicates definitely no origin columns at    * all)    */
specifier|public
specifier|static
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|int
name|column
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|ColumnOrigin
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|ColumnOrigin
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|getColumnOrigins
argument_list|(
name|column
argument_list|)
return|;
block|}
comment|/**    * Determines the origin of a column, provided the column maps to a single    * column that isn't derived.    *    * @see #getColumnOrigins(org.apache.calcite.rel.RelNode, int)    *    * @param rel the RelNode of the column    * @param column the offset of the column whose origin we are trying to    * determine    *    * @return the origin of a column provided it's a simple column; otherwise,    * returns null    */
specifier|public
specifier|static
name|RelColumnOrigin
name|getColumnOrigin
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|int
name|column
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|origins
init|=
name|getColumnOrigins
argument_list|(
name|rel
argument_list|,
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|origins
operator|==
literal|null
operator|||
name|origins
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RelColumnOrigin
name|origin
init|=
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|origins
argument_list|)
decl_stmt|;
return|return
name|origin
operator|.
name|isDerived
argument_list|()
condition|?
literal|null
else|:
name|origin
return|;
block|}
comment|/**    * Determines the origin of a {@link RelNode}, provided it maps to a single    * table, optionally with filtering and projection.    *    * @param rel the RelNode    *    * @return the table, if the RelNode is a simple table; otherwise null    */
specifier|public
specifier|static
name|RelOptTable
name|getTableOrigin
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
comment|// Determine the simple origin of the first column in the
comment|// RelNode.  If it's simple, then that means that the underlying
comment|// table is also simple, even if the column itself is derived.
specifier|final
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|colOrigins
init|=
name|getColumnOrigins
argument_list|(
name|rel
argument_list|,
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|colOrigins
operator|==
literal|null
operator|||
name|colOrigins
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|colOrigins
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
operator|.
name|getOriginTable
argument_list|()
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.Selectivity#getSelectivity(RexNode)}    * statistic.    *    * @param rel       the relational expression    * @param predicate predicate whose selectivity is to be estimated against    *                  rel's output    * @return estimated selectivity (between 0.0 and 1.0), or null if no    * reliable estimate can be determined    */
specifier|public
specifier|static
name|Double
name|getSelectivity
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|Selectivity
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|Selectivity
operator|.
name|class
argument_list|)
decl_stmt|;
name|Double
name|result
init|=
name|metadata
operator|.
name|getSelectivity
argument_list|(
name|predicate
argument_list|)
decl_stmt|;
assert|assert
name|isPercentage
argument_list|(
name|result
argument_list|,
literal|true
argument_list|)
assert|;
return|return
name|result
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.UniqueKeys#getUniqueKeys(boolean)}    * statistic.    *    * @param rel the relational expression    * @return set of keys, or null if this information cannot be determined    * (whereas empty set indicates definitely no keys at all)    */
specifier|public
specifier|static
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|UniqueKeys
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|UniqueKeys
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|getUniqueKeys
argument_list|(
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.UniqueKeys#getUniqueKeys(boolean)}    * statistic.    *    * @param rel         the relational expression    * @param ignoreNulls if true, ignore null values when determining    *                    whether the keys are unique    * @return set of keys, or null if this information cannot be determined    * (whereas empty set indicates definitely no keys at all)    */
specifier|public
specifier|static
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|UniqueKeys
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|UniqueKeys
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|getUniqueKeys
argument_list|(
name|ignoreNulls
argument_list|)
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.ColumnUniqueness#areColumnsUnique(org.apache.calcite.util.ImmutableBitSet, boolean)}    * statistic.    *    * @param rel     the relational expression    * @param columns column mask representing the subset of columns for which    *                uniqueness will be determined    * @return true or false depending on whether the columns are unique, or    * null if not enough information is available to make that determination    */
specifier|public
specifier|static
name|Boolean
name|areColumnsUnique
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|ColumnUniqueness
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|ColumnUniqueness
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|areColumnsUnique
argument_list|(
name|columns
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.ColumnUniqueness#areColumnsUnique(org.apache.calcite.util.ImmutableBitSet, boolean)}    * statistic.    *    * @param rel         the relational expression    * @param columns     column mask representing the subset of columns for which    *                    uniqueness will be determined    * @param ignoreNulls if true, ignore null values when determining column    *                    uniqueness    * @return true or false depending on whether the columns are unique, or    * null if not enough information is available to make that determination    */
specifier|public
specifier|static
name|Boolean
name|areColumnsUnique
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|ColumnUniqueness
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|ColumnUniqueness
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|areColumnsUnique
argument_list|(
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
comment|/**    * Returns the    * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Collation#collations()}    * statistic.    *    * @param rel         the relational expression    * @return List of sorted column combinations, or    * null if not enough information is available to make that determination    */
specifier|public
specifier|static
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|Collation
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|Collation
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|collations
argument_list|()
return|;
block|}
comment|/**    * Returns the    * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Distribution#distribution()}    * statistic.    *    * @param rel         the relational expression    * @return List of sorted column combinations, or    * null if not enough information is available to make that determination    */
specifier|public
specifier|static
name|RelDistribution
name|distribution
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|Distribution
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|Distribution
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|distribution
argument_list|()
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.PopulationSize#getPopulationSize(org.apache.calcite.util.ImmutableBitSet)}    * statistic.    *    * @param rel      the relational expression    * @param groupKey column mask representing the subset of columns for which    *                 the row count will be determined    * @return distinct row count for the given groupKey, or null if no reliable    * estimate can be determined    *    */
specifier|public
specifier|static
name|Double
name|getPopulationSize
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|PopulationSize
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|PopulationSize
operator|.
name|class
argument_list|)
decl_stmt|;
name|Double
name|result
init|=
name|metadata
operator|.
name|getPopulationSize
argument_list|(
name|groupKey
argument_list|)
decl_stmt|;
return|return
name|validateResult
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.DistinctRowCount#getDistinctRowCount(org.apache.calcite.util.ImmutableBitSet, org.apache.calcite.rex.RexNode)}    * statistic.    *    * @param rel       the relational expression    * @param groupKey  column mask representing group by columns    * @param predicate pre-filtered predicates    * @return distinct row count for groupKey, filtered by predicate, or null    * if no reliable estimate can be determined    */
specifier|public
specifier|static
name|Double
name|getDistinctRowCount
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|DistinctRowCount
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|DistinctRowCount
operator|.
name|class
argument_list|)
decl_stmt|;
name|Double
name|result
init|=
name|metadata
operator|.
name|getDistinctRowCount
argument_list|(
name|groupKey
argument_list|,
name|predicate
argument_list|)
decl_stmt|;
return|return
name|validateResult
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * Returns the    * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Predicates#getPredicates()}    * statistic.    *    * @param rel the relational expression    * @return Predicates that can be pulled above this RelNode    */
specifier|public
specifier|static
name|RelOptPredicateList
name|getPulledUpPredicates
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|Predicates
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|Predicates
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|metadata
operator|.
name|getPredicates
argument_list|()
return|;
block|}
comment|/**    * Returns the    * {@link BuiltInMetadata.ExplainVisibility#isVisibleInExplain(SqlExplainLevel)}    * statistic.    *    * @param rel          the relational expression    * @param explainLevel level of detail    * @return true for visible, false for invisible; if no metadata is available,    * defaults to true    */
specifier|public
specifier|static
name|boolean
name|isVisibleInExplain
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|SqlExplainLevel
name|explainLevel
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|ExplainVisibility
name|metadata
init|=
name|rel
operator|.
name|metadata
argument_list|(
name|BuiltInMetadata
operator|.
name|ExplainVisibility
operator|.
name|class
argument_list|)
decl_stmt|;
name|Boolean
name|b
init|=
name|metadata
operator|.
name|isVisibleInExplain
argument_list|(
name|explainLevel
argument_list|)
decl_stmt|;
return|return
name|b
operator|==
literal|null
operator|||
name|b
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isPercentage
parameter_list|(
name|Double
name|result
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
specifier|final
name|double
name|d
init|=
name|result
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0.0
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
if|if
condition|(
name|d
operator|>
literal|1.0
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isNonNegative
parameter_list|(
name|Double
name|result
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
specifier|final
name|double
name|d
init|=
name|result
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0.0
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
specifier|static
name|Double
name|validateResult
parameter_list|(
name|Double
name|result
parameter_list|)
block|{
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Never let the result go below 1, as it will result in incorrect
comment|// calculations if the row-count is used as the denominator in a
comment|// division expression.  Also, cap the value at the max double value
comment|// to avoid calculations using infinity.
if|if
condition|(
name|result
operator|.
name|isInfinite
argument_list|()
condition|)
block|{
name|result
operator|=
name|Double
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
assert|assert
name|isNonNegative
argument_list|(
name|result
argument_list|,
literal|true
argument_list|)
assert|;
if|if
condition|(
name|result
operator|<
literal|1.0
condition|)
block|{
name|result
operator|=
literal|1.0
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMetadataQuery.java
end_comment

end_unit

