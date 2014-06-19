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
comment|/**  * Information for a call to {@link AggImplementor#implementAdd(AggContext, AggAddContext)}.  * Typically, the aggregation implementation will use {@link #arguments()}  * or {@link #rexArguments()} to update aggregate value.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AggAddContext
extends|extends
name|AggResultContext
block|{
specifier|public
name|AggAddContext
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
comment|/**    * Returns {@link RexNode} representation of arguments.    * This can be useful for manual translation of required arguments with    * different {@link net.hydromatic.optiq.rules.java.NullPolicy}.    * @return {@link RexNode} representation of arguments    */
specifier|public
specifier|abstract
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexArguments
parameter_list|()
function_decl|;
comment|/**    * Returns Linq4j form of arguments.    * The resulting value is equivalent to    * {@code rowTranslator().translateList(rexArguments())}.    * This is handy if you need just operate on argument.    * @return Linq4j form of arguments.    */
specifier|public
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|()
block|{
return|return
name|rowTranslator
argument_list|()
operator|.
name|translateList
argument_list|(
name|rexArguments
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns {@link RexToLixTranslator} suitable to transform the arguments.    * @return {@link RexToLixTranslator} suitable to transform the arguments.    */
specifier|public
specifier|abstract
name|RexToLixTranslator
name|rowTranslator
parameter_list|()
function_decl|;
block|}
end_class

end_unit

