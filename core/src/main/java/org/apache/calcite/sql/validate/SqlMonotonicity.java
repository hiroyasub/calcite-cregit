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
name|sql
operator|.
name|validate
package|;
end_package

begin_comment
comment|/**  * Enumeration of types of monotonicity.  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlMonotonicity
block|{
name|STRICTLY_INCREASING
block|,
name|INCREASING
block|,
name|STRICTLY_DECREASING
block|,
name|DECREASING
block|,
name|CONSTANT
block|,
comment|/**    * Catch-all value for expressions that have some monotonic properties.    * Maybe it isn't known whether the expression is increasing or decreasing;    * or maybe the value is neither increasing nor decreasing but the value    * never repeats.    */
name|MONOTONIC
block|,
name|NOT_MONOTONIC
block|;
comment|/**    * If this is a strict monotonicity (StrictlyIncreasing, StrictlyDecreasing)    * returns the non-strict equivalent (Increasing, Decreasing).    *    * @return non-strict equivalent monotonicity    */
specifier|public
name|SqlMonotonicity
name|unstrict
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|STRICTLY_INCREASING
case|:
return|return
name|INCREASING
return|;
case|case
name|STRICTLY_DECREASING
case|:
return|return
name|DECREASING
return|;
default|default:
return|return
name|this
return|;
block|}
block|}
comment|/**    * Returns the reverse monotonicity.    *    * @return reverse monotonicity    */
specifier|public
name|SqlMonotonicity
name|reverse
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|STRICTLY_INCREASING
case|:
return|return
name|STRICTLY_DECREASING
return|;
case|case
name|INCREASING
case|:
return|return
name|DECREASING
return|;
case|case
name|STRICTLY_DECREASING
case|:
return|return
name|STRICTLY_INCREASING
return|;
case|case
name|DECREASING
case|:
return|return
name|INCREASING
return|;
default|default:
return|return
name|this
return|;
block|}
block|}
comment|/**    * Whether values of this monotonicity are decreasing. That is, if a value    * at a given point in a sequence is X, no point later in the sequence will    * have a value greater than X.    *    * @return whether values are decreasing    */
specifier|public
name|boolean
name|isDecreasing
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|STRICTLY_DECREASING
case|:
case|case
name|DECREASING
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Returns whether values of this monotonicity may ever repeat after moving    * to another value: true for {@link #NOT_MONOTONIC} and {@link #CONSTANT},    * false otherwise.    *    *<p>If a column is known not to repeat, a sort on that column can make    * progress before all of the input has been seen.    *    * @return whether values repeat    */
specifier|public
name|boolean
name|mayRepeat
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|NOT_MONOTONIC
case|:
case|case
name|CONSTANT
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
block|}
end_enum

begin_comment
comment|// End SqlMonotonicity.java
end_comment

end_unit

