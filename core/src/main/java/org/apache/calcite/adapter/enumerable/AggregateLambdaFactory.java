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
name|adapter
operator|.
name|enumerable
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
name|linq4j
operator|.
name|function
operator|.
name|Function0
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|linq4j
operator|.
name|function
operator|.
name|Function2
import|;
end_import

begin_comment
comment|/**  * Generates lambda functions used in {@link EnumerableAggregate}.  *  *<p>This interface allows a implicit accumulator type variation.  * ({@code OAccumulate} {@literal ->} {@code TAccumulate})  *  * @param<TSource> Type of the enumerable input source  * @param<TOrigAccumulate> Type of the original accumulator  * @param<TAccumulate> Type of the varied accumulator  * @param<TResult> Type of the enumerable output result  * @param<TKey> Type of the group-by key  */
end_comment

begin_interface
specifier|public
interface|interface
name|AggregateLambdaFactory
parameter_list|<
name|TSource
parameter_list|,
name|TOrigAccumulate
parameter_list|,
name|TAccumulate
parameter_list|,
name|TResult
parameter_list|,
name|TKey
parameter_list|>
block|{
name|Function0
argument_list|<
name|TAccumulate
argument_list|>
name|accumulatorInitializer
parameter_list|()
function_decl|;
name|Function2
argument_list|<
name|TAccumulate
argument_list|,
name|TSource
argument_list|,
name|TAccumulate
argument_list|>
name|accumulatorAdder
parameter_list|()
function_decl|;
name|Function1
argument_list|<
name|TAccumulate
argument_list|,
name|TResult
argument_list|>
name|singleGroupResultSelector
parameter_list|(
name|Function1
argument_list|<
name|TOrigAccumulate
argument_list|,
name|TResult
argument_list|>
name|resultSelector
parameter_list|)
function_decl|;
name|Function2
argument_list|<
name|TKey
argument_list|,
name|TAccumulate
argument_list|,
name|TResult
argument_list|>
name|resultSelector
parameter_list|(
name|Function2
argument_list|<
name|TKey
argument_list|,
name|TOrigAccumulate
argument_list|,
name|TResult
argument_list|>
name|resultSelector
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

