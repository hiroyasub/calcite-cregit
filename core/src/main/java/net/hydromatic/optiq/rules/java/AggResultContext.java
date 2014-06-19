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
name|BlockBuilder
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
name|expressions
operator|.
name|Expression
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
comment|/**  * Information for a call to {@link AggImplementor#implementResult(AggContext, AggResultContext)}  * Typically, the aggregation implementation will convert {@link #accumulator()}  * to the resulting value of the aggregation.  * The implementation MUST NOT destroy the cotents of {@link #accumulator()}.  */
end_comment

begin_class
specifier|public
class|class
name|AggResultContext
extends|extends
name|AggResetContext
block|{
comment|/**    * Creates aggregate result context    * @param block code block that will contain the result calculation statements    * @param accumulator accumulator variables that store the intermediate    *                    aggregate state    */
specifier|public
name|AggResultContext
parameter_list|(
name|BlockBuilder
name|block
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|accumulator
parameter_list|)
block|{
name|super
argument_list|(
name|block
argument_list|,
name|accumulator
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End AggResultContext.java
end_comment

end_unit

