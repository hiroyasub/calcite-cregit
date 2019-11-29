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
name|benchmarks
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
name|sql
operator|.
name|SqlNode
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
name|sql
operator|.
name|parser
operator|.
name|SqlParseException
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
name|sql
operator|.
name|parser
operator|.
name|SqlParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Benchmark
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|BenchmarkMode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Fork
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Measurement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Mode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|OutputTimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Param
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Scope
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Setup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|State
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Threads
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|annotations
operator|.
name|Warmup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|profile
operator|.
name|GCProfiler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|Runner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|RunnerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|options
operator|.
name|Options
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openjdk
operator|.
name|jmh
operator|.
name|runner
operator|.
name|options
operator|.
name|OptionsBuilder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Benchmarks JavaCC-generated SQL parser  */
end_comment

begin_class
annotation|@
name|Fork
argument_list|(
name|value
operator|=
literal|1
argument_list|,
name|jvmArgsPrepend
operator|=
literal|"-Xmx128m"
argument_list|)
annotation|@
name|Measurement
argument_list|(
name|iterations
operator|=
literal|7
argument_list|,
name|time
operator|=
literal|1
argument_list|,
name|timeUnit
operator|=
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
annotation|@
name|Warmup
argument_list|(
name|iterations
operator|=
literal|7
argument_list|,
name|time
operator|=
literal|1
argument_list|,
name|timeUnit
operator|=
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
annotation|@
name|State
argument_list|(
name|Scope
operator|.
name|Thread
argument_list|)
annotation|@
name|Threads
argument_list|(
literal|1
argument_list|)
annotation|@
name|BenchmarkMode
argument_list|(
name|Mode
operator|.
name|AverageTime
argument_list|)
annotation|@
name|OutputTimeUnit
argument_list|(
name|TimeUnit
operator|.
name|MICROSECONDS
argument_list|)
specifier|public
class|class
name|ParserBenchmark
block|{
annotation|@
name|Param
argument_list|(
block|{
literal|"1000"
block|}
argument_list|)
name|int
name|length
decl_stmt|;
annotation|@
name|Param
argument_list|(
block|{
literal|"true"
block|}
argument_list|)
name|boolean
name|comments
decl_stmt|;
name|String
name|sql
decl_stmt|;
name|SqlParser
name|parser
decl_stmt|;
annotation|@
name|Setup
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|SqlParseException
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
operator|(
name|int
operator|)
operator|(
name|length
operator|*
literal|1.2
operator|)
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"select 1"
argument_list|)
expr_stmt|;
name|Random
name|rnd
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|rnd
operator|.
name|setSeed
argument_list|(
literal|424242
argument_list|)
expr_stmt|;
for|for
control|(
init|;
name|sb
operator|.
name|length
argument_list|()
operator|<
name|length
condition|;
control|)
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
literal|7
operator|&&
name|sb
operator|.
name|length
argument_list|()
operator|<
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|rnd
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|sb
operator|.
name|append
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|sb
operator|.
name|append
argument_list|(
name|rnd
operator|.
name|nextInt
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|sb
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
operator|.
name|append
argument_list|(
name|rnd
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
name|rnd
operator|.
name|nextLong
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|comments
operator|&&
name|sb
operator|.
name|length
argument_list|()
operator|<
name|length
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"// sb.append('\\'').append(rnd.nextLong()).append(rnd.nextLong()).append(rnd"
operator|+
literal|".nextLong())"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" from dual"
argument_list|)
expr_stmt|;
name|parser
operator|=
name|SqlParser
operator|.
name|create
argument_list|(
literal|"values(1)"
argument_list|)
expr_stmt|;
name|sql
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Benchmark
specifier|public
name|SqlNode
name|parseCached
parameter_list|()
throws|throws
name|SqlParseException
block|{
return|return
name|parser
operator|.
name|parseQuery
argument_list|(
name|sql
argument_list|)
return|;
block|}
annotation|@
name|Benchmark
specifier|public
name|SqlNode
name|parseNonCached
parameter_list|()
throws|throws
name|SqlParseException
block|{
return|return
name|SqlParser
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|parseQuery
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|RunnerException
block|{
name|Options
name|opt
init|=
operator|new
name|OptionsBuilder
argument_list|()
operator|.
name|include
argument_list|(
name|ParserBenchmark
operator|.
name|class
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|addProfiler
argument_list|(
name|GCProfiler
operator|.
name|class
argument_list|)
operator|.
name|addProfiler
argument_list|(
name|FlightRecorderProfiler
operator|.
name|class
argument_list|)
operator|.
name|detectJvmArgs
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
operator|new
name|Runner
argument_list|(
name|opt
argument_list|)
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ParserBenchmark.java
end_comment

end_unit
