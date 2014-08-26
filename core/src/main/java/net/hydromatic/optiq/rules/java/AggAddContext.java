begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * Information for a call to {@link net.hydromatic.optiq.rules.java.AggImplementor#implementAdd(AggContext, AggAddContext)}.  * Typically, the aggregation implementation will use {@link #arguments()}  * or {@link #rexArguments()} to update aggregate value.  */
end_comment

begin_interface
specifier|public
interface|interface
name|AggAddContext
extends|extends
name|AggResultContext
block|{
comment|/**    * Returns {@link org.eigenbase.rex.RexNode} representation of arguments.    * This can be useful for manual translation of required arguments with    * different {@link NullPolicy}.    * @return {@link org.eigenbase.rex.RexNode} representation of arguments    */
name|List
argument_list|<
name|RexNode
argument_list|>
name|rexArguments
parameter_list|()
function_decl|;
comment|/**    * Returns Linq4j form of arguments.    * The resulting value is equivalent to    * {@code rowTranslator().translateList(rexArguments())}.    * This is handy if you need just operate on argument.    * @return Linq4j form of arguments.    */
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|()
function_decl|;
comment|/**    * Returns {@link net.hydromatic.optiq.rules.java.RexToLixTranslator} suitable to transform the arguments.    * @return {@link net.hydromatic.optiq.rules.java.RexToLixTranslator} suitable to transform the arguments.    */
name|RexToLixTranslator
name|rowTranslator
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End AggAddContext.java
end_comment

end_unit

