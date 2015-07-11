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
comment|/**  * Contains the interfaces for several common forms of metadata.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BuiltInMetadata
block|{
comment|/** Metadata about the selectivity of a predicate. */
specifier|public
interface|interface
name|Selectivity
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the percentage of an expression's output rows which satisfy a      * given predicate. Returns null to indicate that no reliable estimate can      * be produced.      *      * @param predicate predicate whose selectivity is to be estimated against      *                  rel's output      * @return estimated selectivity (between 0.0 and 1.0), or null if no      * reliable estimate can be determined      */
name|Double
name|getSelectivity
parameter_list|(
name|RexNode
name|predicate
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about which combinations of columns are unique identifiers. */
specifier|public
interface|interface
name|UniqueKeys
extends|extends
name|Metadata
block|{
comment|/**      * Determines the set of unique minimal keys for this expression. A key is      * represented as an {@link org.apache.calcite.util.ImmutableBitSet}, where      * each bit position represents a 0-based output column ordinal.      *      *<p>Nulls can be ignored if the relational expression has filtered out      * null values.      *      * @param ignoreNulls if true, ignore null values when determining      *                    whether the keys are unique      * @return set of keys, or null if this information cannot be determined      * (whereas empty set indicates definitely no keys at all)      */
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|boolean
name|ignoreNulls
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about whether a set of columns uniquely identifies a row. */
specifier|public
interface|interface
name|ColumnUniqueness
extends|extends
name|Metadata
block|{
comment|/**      * Determines whether a specified set of columns from a specified relational      * expression are unique.      *      *<p>For example, if the relational expression is a {@code TableScan} to      * T(A, B, C, D) whose key is (A, B), then:      *<ul>      *<li>{@code areColumnsUnique([0, 1])} yields true,      *<li>{@code areColumnsUnique([0])} yields false,      *<li>{@code areColumnsUnique([0, 2])} yields false.      *</ul>      *      *<p>Nulls can be ignored if the relational expression has filtered out      * null values.      *      * @param columns column mask representing the subset of columns for which      *                uniqueness will be determined      * @param ignoreNulls if true, ignore null values when determining column      *                    uniqueness      * @return whether the columns are unique, or      * null if not enough information is available to make that determination      */
name|Boolean
name|areColumnsUnique
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about which columns are sorted. */
specifier|public
interface|interface
name|Collation
extends|extends
name|Metadata
block|{
comment|/** Determines which columns are sorted. */
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about how a relational expression is distributed.    *    *<p>If you are an operator consuming a relational expression, which subset    * of the rows are you seeing? You might be seeing all of them (BROADCAST    * or SINGLETON), only those whose key column values have a particular hash    * code (HASH) or only those whose column values have particular values or    * ranges of values (RANGE).    *    *<p>When a relational expression is partitioned, it is often partitioned    * among nodes, but it may be partitioned among threads running on the same    * node. */
specifier|public
interface|interface
name|Distribution
extends|extends
name|Metadata
block|{
comment|/** Determines how the rows are distributed. */
name|RelDistribution
name|distribution
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about the number of rows returned by a relational expression. */
specifier|public
interface|interface
name|RowCount
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the number of rows which will be returned by a relational      * expression. The default implementation for this query asks the rel itself      * via {@link RelNode#estimateRowCount}, but metadata providers can override this      * with their own cost models.      *      * @return estimated row count, or null if no reliable estimate can be      * determined      */
name|Double
name|getRowCount
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about the maximum number of rows returned by a relational    * expression. */
specifier|public
interface|interface
name|MaxRowCount
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the max number of rows which will be returned by a relational      * expression.      *      *<p>The default implementation for this query returns      * {@link Double#POSITIVE_INFINITY},      * but metadata providers can override this with their own cost models.      *      * @return upper bound on the number of rows returned      */
name|Double
name|getMaxRowCount
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about the number of distinct rows returned by a set of columns    * in a relational expression. */
specifier|public
interface|interface
name|DistinctRowCount
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the number of rows which would be produced by a GROUP BY on the      * set of columns indicated by groupKey, where the input to the GROUP BY has      * been pre-filtered by predicate. This quantity (leaving out predicate) is      * often referred to as cardinality (as in gender being a "low-cardinality      * column").      *      * @param groupKey  column mask representing group by columns      * @param predicate pre-filtered predicates      * @return distinct row count for groupKey, filtered by predicate, or null      * if no reliable estimate can be determined      */
name|Double
name|getDistinctRowCount
parameter_list|(
name|ImmutableBitSet
name|groupKey
parameter_list|,
name|RexNode
name|predicate
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about the proportion of original rows that remain in a relational    * expression. */
specifier|public
interface|interface
name|PercentageOriginalRows
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the percentage of the number of rows actually produced by a      * relational expression out of the number of rows it would produce if all      * single-table filter conditions were removed.      *      * @return estimated percentage (between 0.0 and 1.0), or null if no      * reliable estimate can be determined      */
name|Double
name|getPercentageOriginalRows
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about the number of distinct values in the original source of a    * column or set of columns. */
specifier|public
interface|interface
name|PopulationSize
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the distinct row count in the original source for the given      * {@code groupKey}, ignoring any filtering being applied by the expression.      * Typically, "original source" means base table, but for derived columns,      * the estimate may come from a non-leaf rel such as a LogicalProject.      *      * @param groupKey column mask representing the subset of columns for which      *                 the row count will be determined      * @return distinct row count for the given groupKey, or null if no reliable      * estimate can be determined      */
name|Double
name|getPopulationSize
parameter_list|(
name|ImmutableBitSet
name|groupKey
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about the size of rows and columns. */
specifier|public
interface|interface
name|Size
extends|extends
name|Metadata
block|{
comment|/**      * Determines the average size (in bytes) of a row from this relational      * expression.      *      * @return average size of a row, in bytes, or null if not known      */
name|Double
name|averageRowSize
parameter_list|()
function_decl|;
comment|/**      * Determines the average size (in bytes) of a value of a column in this      * relational expression.      *      *<p>Null values are included (presumably they occupy close to 0 bytes).      *      *<p>It is left to the caller to decide whether the size is the compressed      * size, the uncompressed size, or memory allocation when the value is      * wrapped in an object in the Java heap. The uncompressed size is probably      * a good compromise.      *      * @return an immutable list containing, for each column, the average size      * of a column value, in bytes. Each value or the entire list may be null if      * the metadata is not available      */
name|List
argument_list|<
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about the origins of columns. */
specifier|public
interface|interface
name|ColumnOrigin
extends|extends
name|Metadata
block|{
comment|/**      * For a given output column of an expression, determines all columns of      * underlying tables which contribute to result values. An output column may      * have more than one origin due to expressions such as Union and      * LogicalProject. The optimizer may use this information for catalog access      * (e.g. index availability).      *      * @param outputColumn 0-based ordinal for output column of interest      * @return set of origin columns, or null if this information cannot be      * determined (whereas empty set indicates definitely no origin columns at      * all)      */
name|Set
argument_list|<
name|RelColumnOrigin
argument_list|>
name|getColumnOrigins
parameter_list|(
name|int
name|outputColumn
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about the cost of evaluating a relational expression, including    * all of its inputs. */
specifier|public
interface|interface
name|CumulativeCost
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the cost of executing a relational expression, including the      * cost of its inputs. The default implementation for this query adds      * {@link NonCumulativeCost#getNonCumulativeCost} to the cumulative cost of      * each input, but metadata providers can override this with their own cost      * models, e.g. to take into account interactions between expressions.      *      * @return estimated cost, or null if no reliable estimate can be      * determined      */
name|RelOptCost
name|getCumulativeCost
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about the cost of evaluating a relational expression, not    * including its inputs. */
specifier|public
interface|interface
name|NonCumulativeCost
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the cost of executing a relational expression, not counting the      * cost of its inputs. (However, the non-cumulative cost is still usually      * dependent on the row counts of the inputs.) The default implementation      * for this query asks the rel itself via {@link RelNode#computeSelfCost},      * but metadata providers can override this with their own cost models.      *      * @return estimated cost, or null if no reliable estimate can be      * determined      */
name|RelOptCost
name|getNonCumulativeCost
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about whether a relational expression should appear in a plan. */
specifier|public
interface|interface
name|ExplainVisibility
extends|extends
name|Metadata
block|{
comment|/**      * Determines whether a relational expression should be visible in EXPLAIN      * PLAN output at a particular level of detail.      *      * @param explainLevel level of detail      * @return true for visible, false for invisible      */
name|Boolean
name|isVisibleInExplain
parameter_list|(
name|SqlExplainLevel
name|explainLevel
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about the predicates that hold in the rows emitted from a    * relational expression. */
specifier|public
interface|interface
name|Predicates
extends|extends
name|Metadata
block|{
comment|/**      * Derives the predicates that hold on rows emitted from a relational      * expression.      *      * @return Predicate list      */
name|RelOptPredicateList
name|getPredicates
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about the degree of parallelism of a relational expression, and    * how its operators are assigned to processes with independent resource    * pools. */
specifier|public
interface|interface
name|Parallelism
extends|extends
name|Metadata
block|{
comment|/** Returns whether each physical operator implementing this relational      * expression belongs to a different process than its inputs.      *      *<p>A collection of operators processing all of the splits of a particular      * stage in the query pipeline is called a "phase". A phase starts with      * a leaf node such as a {@link org.apache.calcite.rel.core.TableScan},      * or with a phase-change node such as an      * {@link org.apache.calcite.rel.core.Exchange}. Hadoop's shuffle operator      * (a form of sort-exchange) causes data to be sent across the network. */
name|Boolean
name|isPhaseTransition
parameter_list|()
function_decl|;
comment|/** Returns the number of distinct splits of the data.      *      *<p>Note that splits must be distinct. For broadcast, where each copy is      * the same, returns 1.      *      *<p>Thus the split count is the<em>proportion</em> of the data seen by      * each operator instance.      */
name|Integer
name|splitCount
parameter_list|()
function_decl|;
block|}
comment|/** Metadata about the memory use of an operator. */
specifier|public
interface|interface
name|Memory
extends|extends
name|Metadata
block|{
comment|/** Returns the expected amount of memory, in bytes, required by a physical      * operator implementing this relational expression, across all splits.      *      *<p>How much memory is used depends very much on the algorithm; for      * example, an implementation of      * {@link org.apache.calcite.rel.core.Aggregate} that loads all data into a      * hash table requires approximately {@code rowCount * averageRowSize}      * bytes, whereas an implementation that assumes that the input is sorted      * requires only {@code averageRowSize} bytes to maintain a single      * accumulator for each aggregate function.      */
name|Double
name|memory
parameter_list|()
function_decl|;
comment|/** Returns the cumulative amount of memory, in bytes, required by the      * physical operator implementing this relational expression, and all other      * operators within the same phase, across all splits.      *      * @see Parallelism#splitCount()      */
name|Double
name|cumulativeMemoryWithinPhase
parameter_list|()
function_decl|;
comment|/** Returns the expected cumulative amount of memory, in bytes, required by      * the physical operator implementing this relational expression, and all      * operators within the same phase, within each split.      *      *<p>Basic formula:      *      *<blockquote>cumulativeMemoryWithinPhaseSplit      *     = cumulativeMemoryWithinPhase / Parallelism.splitCount</blockquote>      */
name|Double
name|cumulativeMemoryWithinPhaseSplit
parameter_list|()
function_decl|;
block|}
comment|/** The built-in forms of metadata. */
interface|interface
name|All
extends|extends
name|Selectivity
extends|,
name|UniqueKeys
extends|,
name|RowCount
extends|,
name|DistinctRowCount
extends|,
name|PercentageOriginalRows
extends|,
name|ColumnUniqueness
extends|,
name|ColumnOrigin
extends|,
name|Predicates
extends|,
name|Collation
extends|,
name|Distribution
extends|,
name|Size
extends|,
name|Parallelism
extends|,
name|Memory
block|{   }
block|}
end_class

begin_comment
comment|// End BuiltInMetadata.java
end_comment

end_unit

