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
name|type
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
name|avatica
operator|.
name|util
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|math3
operator|.
name|fraction
operator|.
name|BigFraction
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

begin_comment
comment|/** Time frame.  *  *<p>Belongs to a {@link TimeFrameSet}.  * The default set is {@link TimeFrames#CORE};  * to create custom time frame sets, call {@link TimeFrameSet#builder()}. */
end_comment

begin_interface
specifier|public
interface|interface
name|TimeFrame
block|{
comment|/** Returns the time frame set that this frame belongs to. */
name|TimeFrameSet
name|frameSet
parameter_list|()
function_decl|;
comment|/** Name of this time frame.    *    *<p>A time frame based on a built-in Avatica    * {@link org.apache.calcite.avatica.util.TimeUnit} will have the same    * name.    *    * @see TimeFrameSet#get(TimeUnit) */
name|String
name|name
parameter_list|()
function_decl|;
comment|/** If this time frame has units in common with another time frame, returns    * the number of this time frame in one of that time frame.    *    *<p>For example, {@code MONTH.per(YEAR)} returns 12;    * {@code YEAR.per(MONTH)} returns 1 / 12.    */
annotation|@
name|Nullable
name|BigFraction
name|per
parameter_list|(
name|TimeFrame
name|timeFrame
parameter_list|)
function_decl|;
comment|/** Returns a date where this time frame is at the start of a cycle.    *    *<p>For example, the {@code WEEK} time frame starts on a Monday,    * and {@code 1970-01-05} was a Monday,    * and the date {@code 1970-01-05} is represented as integer 5,    * so for the {@code WEEK} time frame this method returns 5.    * But it would also be valid to return the date value of {@code 1900/01/01},    * which was also a Monday.  Because we know that a week is 7 days, we can    * compute every other point at which a week advances. */
specifier|default
name|int
name|dateEpoch
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** Returns a timestamp where this time frame is at the start of a cycle.    *    * @see #dateEpoch() */
specifier|default
name|long
name|timestampEpoch
parameter_list|()
block|{
return|return
literal|0L
return|;
block|}
comment|/** Returns a month number where this time frame is at the start of a cycle.    *    * @see #dateEpoch()    */
specifier|default
name|int
name|monthEpoch
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
comment|/** Whether this frame can roll up to {@code toFrame}.    *    *<p>Examples:    *<ul>    *<li>{@code SECOND} can roll up to {@code MINUTE}, {@code HOUR},    *   {@code DAY}, {@code WEEK}, {@code MONTH}, {@code MILLENNIUM};    *<li>{@code SECOND} cannot roll up to {@code MILLISECOND} (because it is    *   finer grained);    *<li>{@code WEEK} cannot roll up to {@code MONTH}, {@code YEAR},    *   {@code MILLENNIUM} (because weeks cross month boundaries).    *</ul>    *    *<p>If two time frames have the same core, and one is an integer simple    * multiple of another, and they have the same offset, then they can roll up.    * For example, suppose that {@code MINUTE15} and {@code HOUR3} are both based    * on {@code SECOND};    * {@code MINUTE15} is 15 * 60 seconds and    * {@code HOUR3} is 3 * 60 * 60 seconds;    * therefore one {@code HOUR3} interval equals twelve {@code MINUTE15}    * intervals.    * They have the same offset (both start at {@code 1970-01-01 00:00:00}) and    * therefore {@code MINUTE15} can roll up to {@code HOUR3}.    *    *<p>Even if two frames are not multiples, if they are aligned then they can    * roll up. {@code MONTH} and {@code DAY} are an example. For more about    * alignment, see {@link TimeFrameSet.Builder#addRollup(String, String)}.    */
name|boolean
name|canRollUpTo
parameter_list|(
name|TimeFrame
name|toFrame
parameter_list|)
function_decl|;
comment|/** Returns the built-in unit of this frame, or null if it does not correspond    * to a built-in unit. */
specifier|default
annotation|@
name|Nullable
name|TimeUnit
name|unit
parameter_list|()
block|{
return|return
name|frameSet
argument_list|()
operator|.
name|getUnit
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

