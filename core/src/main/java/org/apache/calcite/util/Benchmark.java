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
name|util
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
name|Function1
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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
comment|/**  * Helps to run benchmarks by running the same task repeatedly and averaging  * the running times.  */
end_comment

begin_class
specifier|public
class|class
name|Benchmark
block|{
comment|/**    * Certain tests are enabled only if logging is enabled at debug level or    * higher.    */
specifier|public
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|Benchmark
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Function1
argument_list|<
name|Statistician
argument_list|,
name|Void
argument_list|>
name|function
decl_stmt|;
specifier|private
specifier|final
name|int
name|repeat
decl_stmt|;
specifier|private
specifier|final
name|Statistician
name|statistician
decl_stmt|;
specifier|public
name|Benchmark
parameter_list|(
name|String
name|description
parameter_list|,
name|Function1
argument_list|<
name|Statistician
argument_list|,
name|Void
argument_list|>
name|function
parameter_list|,
name|int
name|repeat
parameter_list|)
block|{
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
name|this
operator|.
name|repeat
operator|=
name|repeat
expr_stmt|;
name|this
operator|.
name|statistician
operator|=
operator|new
name|Statistician
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns whether performance tests are enabled.    */
specifier|public
specifier|static
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
return|;
block|}
specifier|static
name|long
name|printDuration
parameter_list|(
name|String
name|desc
parameter_list|,
name|long
name|t0
parameter_list|)
block|{
specifier|final
name|long
name|t1
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
specifier|final
name|long
name|duration
init|=
name|t1
operator|-
name|t0
decl_stmt|;
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"{} took {} nanos"
argument_list|,
name|desc
argument_list|,
name|duration
argument_list|)
expr_stmt|;
return|return
name|duration
return|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|repeat
condition|;
name|i
operator|++
control|)
block|{
name|function
operator|.
name|apply
argument_list|(
name|statistician
argument_list|)
expr_stmt|;
block|}
name|statistician
operator|.
name|printDurations
argument_list|()
expr_stmt|;
block|}
comment|/**    * Collects statistics for a test that is run multiple times.    */
specifier|public
specifier|static
class|class
name|Statistician
block|{
specifier|private
specifier|final
name|String
name|desc
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Long
argument_list|>
name|durations
init|=
operator|new
name|ArrayList
argument_list|<
name|Long
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Statistician
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|desc
operator|=
name|desc
expr_stmt|;
block|}
specifier|public
name|void
name|record
parameter_list|(
name|long
name|start
parameter_list|)
block|{
name|durations
operator|.
name|add
argument_list|(
name|printDuration
argument_list|(
name|desc
operator|+
literal|" iteration #"
operator|+
operator|(
name|durations
operator|.
name|size
argument_list|()
operator|+
literal|1
operator|)
argument_list|,
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|printDurations
parameter_list|()
block|{
if|if
condition|(
operator|!
name|LOGGER
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
return|return;
block|}
name|List
argument_list|<
name|Long
argument_list|>
name|coreDurations
init|=
name|durations
decl_stmt|;
name|String
name|durationsString
init|=
name|durations
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|// save before sort
comment|// Ignore the first 3 readings. (JIT compilation takes a while to
comment|// kick in.)
if|if
condition|(
name|coreDurations
operator|.
name|size
argument_list|()
operator|>
literal|3
condition|)
block|{
name|coreDurations
operator|=
name|durations
operator|.
name|subList
argument_list|(
literal|3
argument_list|,
name|durations
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|coreDurations
argument_list|)
expr_stmt|;
comment|// Further ignore the max and min.
name|List
argument_list|<
name|Long
argument_list|>
name|coreCoreDurations
init|=
name|coreDurations
decl_stmt|;
if|if
condition|(
name|coreDurations
operator|.
name|size
argument_list|()
operator|>
literal|4
condition|)
block|{
name|coreCoreDurations
operator|=
name|coreDurations
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|coreDurations
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|long
name|sum
init|=
literal|0
decl_stmt|;
name|int
name|count
init|=
name|coreCoreDurations
operator|.
name|size
argument_list|()
decl_stmt|;
for|for
control|(
name|long
name|duration
range|:
name|coreCoreDurations
control|)
block|{
name|sum
operator|+=
name|duration
expr_stmt|;
block|}
specifier|final
name|double
name|avg
init|=
operator|(
operator|(
name|double
operator|)
name|sum
operator|)
operator|/
name|count
decl_stmt|;
name|double
name|y
init|=
literal|0
decl_stmt|;
for|for
control|(
name|long
name|duration
range|:
name|coreCoreDurations
control|)
block|{
name|double
name|x
init|=
name|duration
operator|-
name|avg
decl_stmt|;
name|y
operator|+=
name|x
operator|*
name|x
expr_stmt|;
block|}
specifier|final
name|double
name|stddev
init|=
name|Math
operator|.
name|sqrt
argument_list|(
name|y
operator|/
name|count
argument_list|)
decl_stmt|;
if|if
condition|(
name|durations
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"{}: {}"
argument_list|,
name|desc
argument_list|,
literal|"no runs"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"{}: {} first; {} +- {}; {} min; {} max; {} nanos"
argument_list|,
name|desc
argument_list|,
name|durations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|avg
argument_list|,
name|stddev
argument_list|,
name|coreDurations
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|Util
operator|.
name|last
argument_list|(
name|coreDurations
argument_list|)
argument_list|,
name|durationsString
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End Benchmark.java
end_comment

end_unit

