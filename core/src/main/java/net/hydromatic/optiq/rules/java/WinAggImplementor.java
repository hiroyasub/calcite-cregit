begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Implements a windowed aggregate function by generating expressions to  * initialize, add to, and get a result from, an accumulator.  * Windowed aggregate is more powerful than regular aggregate since it can  * access rows in the current partition by row indices.  * Regular aggregate can be used to implement windowed aggregate.  *  * @see net.hydromatic.optiq.rules.java.StrictWinAggImplementor  * @see net.hydromatic.optiq.rules.java.RexImpTable.FirstLastValueImplementor  * @see net.hydromatic.optiq.rules.java.RexImpTable.RankImplementor  * @see net.hydromatic.optiq.rules.java.RexImpTable.RowNumberImplementor  */
end_comment

begin_interface
specifier|public
interface|interface
name|WinAggImplementor
extends|extends
name|AggImplementor
block|{
comment|/**    * Allows to access rows in window partition relative to first/last and    * current row.    */
specifier|public
enum|enum
name|SeekType
block|{
comment|/**      * Start of window.      * @see WinAggFrameContext#startIndex()      */
name|START
block|,
comment|/**      * Row position in the frame.      * @see WinAggFrameContext#index()      */
name|SET
block|,
comment|/**      * The index of row that is aggregated.      * Valid only in {@link WinAggAddContext}.      * @see WinAggAddContext#currentPosition()      */
name|AGG_INDEX
block|,
comment|/**      * End of window.      * @see WinAggFrameContext#endIndex()      */
name|END
block|}
comment|/**    * Marker interface to allow {@link AggImplementor} to tell if it is used in    * regular or windowed context.    */
specifier|public
interface|interface
name|WinAggContext
extends|extends
name|AggContext
block|{   }
comment|/**    * Provides information on the current window.    * All the indexes are ready to be used in {@link WinAggFrameResultContext#arguments(Expression)},    * {@link WinAggFrameResultContext#rowTranslator(Expression)} and similar methods.    */
specifier|public
interface|interface
name|WinAggFrameContext
block|{
comment|/**      * Returns the index of the current row in the partition.      * In other words, it is close to ~ROWS BETWEEN CURRENT ROW.      * Note to use {@link #startIndex()} when you need zero-based row position.      * @return the index of the very first row in partition      */
name|Expression
name|index
parameter_list|()
function_decl|;
comment|/**      * Returns the index of the very first row in partition.      * @return index of the very first row in partition      */
name|Expression
name|startIndex
parameter_list|()
function_decl|;
comment|/**      * Returns the index of the very last row in partition.      * @return index of the very last row in partition      */
name|Expression
name|endIndex
parameter_list|()
function_decl|;
comment|/**      * Returns the boolean expression that tells if the partition has rows.      * The partition might lack rows in cases like ROWS BETWEEN 1000 PRECEDING      * AND 900 PRECEDING.      * @return boolean expression that tells if the partition has rows      */
name|Expression
name|hasRows
parameter_list|()
function_decl|;
comment|/**      * Returns the number of rows in the current partition.      * @return number of rows in the current partition or 0 if the partition      *   is empty      */
name|Expression
name|getPartitionRowCount
parameter_list|()
function_decl|;
block|}
comment|/**    * Provides information on the current window when computing the result of    * the aggregation.    */
specifier|public
interface|interface
name|WinAggFrameResultContext
block|{
comment|/**      * Returns {@link RexNode} representation of arguments.      * This can be useful for manual translation of required arguments with      * different {@link net.hydromatic.optiq.rules.java.NullPolicy}.      * @return {@link RexNode} representation of arguments      */
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexArguments
parameter_list|()
function_decl|;
comment|/**      * Returns Linq4j form of arguments.      * The resulting value is equivalent to      * {@code rowTranslator().translateList(rexArguments())}.      * This is handy if you need just operate on argument.      * @param rowIndex index of the requested row. The index must be in range      *                 of partition's startIndex and endIndex.      * @return Linq4j form of arguments of the particular row      */
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
function_decl|;
comment|/**      * Converts absolute index position of the given relative position.      * @param offset offset of the requested row      * @param seekType the type of offset (start of window, end of window, etc)      * @return absolute position of the requested row      */
name|Expression
name|computeIndex
parameter_list|(
name|Expression
name|offset
parameter_list|,
name|SeekType
name|seekType
parameter_list|)
function_decl|;
comment|/**      * Returns row translator for given absolute row position.      * @param rowIndex absolute index of the row.      * @return translator for the requested row      */
name|RexToLixTranslator
name|rowTranslator
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
function_decl|;
comment|/**      * Compares two rows given by absolute positions according to the order      * collation of the current window.      * @param a absolute index of the first row      * @param b absolute index of the second row      * @return result of comparison as as in {@link Comparable#compareTo}      */
name|Expression
name|compareRows
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
function_decl|;
block|}
block|}
end_interface

begin_comment
comment|// End WinAggImplementor.java
end_comment

end_unit

