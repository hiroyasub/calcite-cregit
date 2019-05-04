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
name|Function2
import|;
end_import

begin_comment
comment|/**  * Performs accumulation against a pre-collected list of input sources,  * used with {@link LazyAggregateLambdaFactory}.  *  * @param<TAccumulate> Type of the accumulator  * @param<TSource>     Type of the enumerable input source  */
end_comment

begin_class
specifier|public
class|class
name|BasicLazyAccumulator
parameter_list|<
name|TAccumulate
parameter_list|,
name|TSource
parameter_list|>
implements|implements
name|LazyAggregateLambdaFactory
operator|.
name|LazyAccumulator
argument_list|<
name|TAccumulate
argument_list|,
name|TSource
argument_list|>
block|{
specifier|private
specifier|final
name|Function2
argument_list|<
name|TAccumulate
argument_list|,
name|TSource
argument_list|,
name|TAccumulate
argument_list|>
name|accumulatorAdder
decl_stmt|;
specifier|public
name|BasicLazyAccumulator
parameter_list|(
name|Function2
argument_list|<
name|TAccumulate
argument_list|,
name|TSource
argument_list|,
name|TAccumulate
argument_list|>
name|accumulatorAdder
parameter_list|)
block|{
name|this
operator|.
name|accumulatorAdder
operator|=
name|accumulatorAdder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accumulate
parameter_list|(
name|Iterable
argument_list|<
name|TSource
argument_list|>
name|sourceIterable
parameter_list|,
name|TAccumulate
name|accumulator
parameter_list|)
block|{
name|TAccumulate
name|accumulator1
init|=
name|accumulator
decl_stmt|;
for|for
control|(
name|TSource
name|tSource
range|:
name|sourceIterable
control|)
block|{
name|accumulator1
operator|=
name|accumulatorAdder
operator|.
name|apply
argument_list|(
name|accumulator1
argument_list|,
name|tSource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End BasicLazyAccumulator.java
end_comment

end_unit
