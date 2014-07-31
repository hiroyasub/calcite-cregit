begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|metadata
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
name|RelOptCost
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
name|RexNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|SqlExplainLevel
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
comment|/**      * Determines the set of unique minimal keys for this expression. A key is      * represented as a {@link BitSet}, where each bit position represents a      * 0-based output column ordinal.      *      *<p>Note that {@link RelNode#isDistinct} should return {@code true} if and      * only if at least one key is known.</p>      *      *<p>Nulls can be ignored if the relational expression has filtered out      * null values.      *      * @param ignoreNulls if true, ignore null values when determining      *                    whether the keys are unique      * @return set of keys, or null if this information cannot be determined      * (whereas empty set indicates definitely no keys at all)      */
name|Set
argument_list|<
name|BitSet
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
comment|/**      * Determines whether a specified set of columns from a specified relational      * expression are unique.      *      *<p>Nulls can be ignored if the relational expression has filtered out      * null values.</p>      *      * @param columns column mask representing the subset of columns for which      *                uniqueness will be determined      * @param ignoreNulls if true, ignore null values when determining column      *                    uniqueness      * @return whether the columns are unique, or      * null if not enough information is available to make that determination      */
name|Boolean
name|areColumnsUnique
parameter_list|(
name|BitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about the number of rows returned by a relational expression. */
specifier|public
interface|interface
name|RowCount
extends|extends
name|Metadata
block|{
comment|/**      * Estimates the number of rows which will be returned by a relational      * expression. The default implementation for this query asks the rel itself      * via {@link RelNode#getRows}, but metadata providers can override this      * with their own cost models.      *      * @return estimated row count, or null if no reliable estimate can be      * determined      */
name|Double
name|getRowCount
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
name|BitSet
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
comment|/**      * Estimates the distinct row count in the original source for the given      * {@code groupKey}, ignoring any filtering being applied by the expression.      * Typically, "original source" means base table, but for derived columns,      * the estimate may come from a non-leaf rel such as a ProjectRel.      *      * @param groupKey column mask representing the subset of columns for which      *                 the row count will be determined      * @return distinct row count for the given groupKey, or null if no reliable      * estimate can be determined      */
name|Double
name|getPopulationSize
parameter_list|(
name|BitSet
name|groupKey
parameter_list|)
function_decl|;
block|}
comment|/** Metadata about the origins of columns. */
specifier|public
interface|interface
name|ColumnOrigin
extends|extends
name|Metadata
block|{
comment|/**      * For a given output column of an expression, determines all columns of      * underlying tables which contribute to result values. An output column may      * have more than one origin due to expressions such as UnionRel and      * ProjectRel. The optimizer may use this information for catalog access      * (e.g. index availability).      *      * @param outputColumn 0-based ordinal for output column of interest      * @return set of origin columns, or null if this information cannot be      * determined (whereas empty set indicates definitely no origin columns at      * all)      */
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
block|{   }
block|}
end_class

begin_comment
comment|// End BuiltInMetadata.java
end_comment

end_unit

