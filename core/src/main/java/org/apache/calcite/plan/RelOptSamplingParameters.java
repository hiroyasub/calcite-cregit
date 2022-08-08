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
package|;
end_package

begin_comment
comment|/**  * RelOptSamplingParameters represents the parameters necessary to produce a  * sample of a relation.  *  *<p>It's parameters are derived from the SQL 2003 TABLESAMPLE clause.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptSamplingParameters
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|boolean
name|isBernoulli
decl_stmt|;
specifier|private
specifier|final
name|float
name|samplingPercentage
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isRepeatable
decl_stmt|;
specifier|private
specifier|final
name|int
name|repeatableSeed
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|RelOptSamplingParameters
parameter_list|(
name|boolean
name|isBernoulli
parameter_list|,
name|float
name|samplingPercentage
parameter_list|,
name|boolean
name|isRepeatable
parameter_list|,
name|int
name|repeatableSeed
parameter_list|)
block|{
name|this
operator|.
name|isBernoulli
operator|=
name|isBernoulli
expr_stmt|;
name|this
operator|.
name|samplingPercentage
operator|=
name|samplingPercentage
expr_stmt|;
name|this
operator|.
name|isRepeatable
operator|=
name|isRepeatable
expr_stmt|;
name|this
operator|.
name|repeatableSeed
operator|=
name|repeatableSeed
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Indicates whether Bernoulli or system sampling should be performed.    * Bernoulli sampling requires the decision whether to include each row in    * the sample to be independent across rows. System sampling allows    * implementation-dependent behavior.    *    * @return true if Bernoulli sampling is configured, false for system    * sampling    */
specifier|public
name|boolean
name|isBernoulli
parameter_list|()
block|{
return|return
name|isBernoulli
return|;
block|}
comment|/**    * Returns the sampling percentage. For Bernoulli sampling, the sampling    * percentage is the likelihood that any given row will be included in the    * sample. For system sampling, the sampling percentage indicates (roughly)    * what percentage of the rows will appear in the sample.    *    * @return the sampling percentage between 0.0 and 1.0, exclusive    */
specifier|public
name|float
name|getSamplingPercentage
parameter_list|()
block|{
return|return
name|samplingPercentage
return|;
block|}
comment|/**    * Indicates whether the sample results should be repeatable. Sample results    * are only required to repeat if no changes have been made to the    * relation's content or structure. If the sample is configured to be    * repeatable, then a user-specified seed value can be obtained via    * {@link #getRepeatableSeed()}.    *    * @return true if the sample results should be repeatable    */
specifier|public
name|boolean
name|isRepeatable
parameter_list|()
block|{
return|return
name|isRepeatable
return|;
block|}
comment|/**    * If {@link #isRepeatable()} returns<code>true</code>, this method returns a    * user-specified seed value. Samples of the same, unmodified relation    * should be identical if the sampling mode, sampling percentage and    * repeatable seed are the same.    *    * @return seed value for repeatable samples    */
specifier|public
name|int
name|getRepeatableSeed
parameter_list|()
block|{
return|return
name|repeatableSeed
return|;
block|}
block|}
end_class

end_unit

