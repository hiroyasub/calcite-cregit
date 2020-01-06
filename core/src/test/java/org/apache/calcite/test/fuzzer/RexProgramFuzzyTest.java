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
name|test
operator|.
name|fuzzer
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
name|plan
operator|.
name|Strong
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
name|rex
operator|.
name|RexLiteral
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexProgramBuilderBase
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
name|rex
operator|.
name|RexUnknownAs
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
name|rex
operator|.
name|RexUtil
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
name|SqlOperator
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|type
operator|.
name|SqlTypeName
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
name|type
operator|.
name|SqlTypeUtil
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
name|util
operator|.
name|ImmutableBitSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Disabled
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
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
name|time
operator|.
name|Duration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|temporal
operator|.
name|ChronoUnit
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|PriorityQueue
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
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Validates that {@link org.apache.calcite.rex.RexSimplify} is able to deal  * with a randomized {@link RexNode}.  *  *<p>The default fuzzing time is 5 seconds to keep overall test duration  * reasonable. The test starts from a random point every time, so the longer it  * runs the more errors it detects.  */
end_comment

begin_class
specifier|public
class|class
name|RexProgramFuzzyTest
extends|extends
name|RexProgramBuilderBase
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RexProgramFuzzyTest
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Duration
name|TEST_DURATION
init|=
name|Duration
operator|.
name|of
argument_list|(
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"rex.fuzzing.duration"
argument_list|,
literal|5
argument_list|)
argument_list|,
name|ChronoUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|TEST_ITERATIONS
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"rex.fuzzing.iterations"
argument_list|,
literal|2000
argument_list|)
decl_stmt|;
comment|// Stop fuzzing after detecting MAX_FAILURES errors
specifier|private
specifier|static
specifier|final
name|int
name|MAX_FAILURES
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"rex.fuzzing.max.failures"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
comment|// Number of slowest to simplify expressions to show
specifier|private
specifier|static
specifier|final
name|int
name|TOPN_SLOWEST
init|=
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"rex.fuzzing.max.slowest"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|// 0 means use random seed
comment|// 42 is used to make sure tests pass in CI
specifier|private
specifier|static
specifier|final
name|long
name|SEED
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"rex.fuzzing.seed"
argument_list|,
literal|44
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_FUZZ_TEST_SEED
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"rex.fuzzing.default.seed"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Duration
name|DEFAULT_FUZZ_TEST_DURATION
init|=
name|Duration
operator|.
name|of
argument_list|(
name|Integer
operator|.
name|getInteger
argument_list|(
literal|"rex.fuzzing.default.duration"
argument_list|,
literal|5
argument_list|)
argument_list|,
name|ChronoUnit
operator|.
name|SECONDS
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_FUZZ_TEST_ITERATIONS
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"rex.fuzzing.default.iterations"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_FUZZ_TEST_FAIL
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"rex.fuzzing.default.fail"
argument_list|)
decl_stmt|;
specifier|private
name|PriorityQueue
argument_list|<
name|SimplifyTask
argument_list|>
name|slowestTasks
decl_stmt|;
specifier|private
name|long
name|currentSeed
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Strong
name|STRONG
init|=
name|Strong
operator|.
name|of
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * A bounded variation of {@link PriorityQueue}    *    * @param<E> the type of elements held in this collection    */
specifier|private
specifier|static
class|class
name|TopN
parameter_list|<
name|E
extends|extends
name|Comparable
parameter_list|<
name|E
parameter_list|>
parameter_list|>
extends|extends
name|PriorityQueue
argument_list|<
name|E
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|n
decl_stmt|;
specifier|private
name|TopN
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|this
operator|.
name|n
operator|=
name|n
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|offer
parameter_list|(
name|E
name|o
parameter_list|)
block|{
if|if
condition|(
name|size
argument_list|()
operator|==
name|n
condition|)
block|{
name|E
name|peek
init|=
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
name|peek
operator|!=
literal|null
operator|&&
name|peek
operator|.
name|compareTo
argument_list|(
name|o
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// If the smallest element in the queue exceeds the added one
comment|// then just ignore the offer
return|return
literal|false
return|;
block|}
comment|// otherwise extract the smallest element, and offer a new one
name|poll
argument_list|()
expr_stmt|;
block|}
return|return
name|super
operator|.
name|offer
argument_list|(
name|o
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|E
argument_list|>
name|iterator
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Order of elements is not defined, please use .peek"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Verifies {@code IS TRUE(IS NULL(null))} kind of expressions up to 4 level deep.    */
annotation|@
name|Test
specifier|public
name|void
name|testNestedCalls
parameter_list|()
block|{
name|nestedCalls
argument_list|(
name|trueLiteral
argument_list|)
expr_stmt|;
name|nestedCalls
argument_list|(
name|falseLiteral
argument_list|)
expr_stmt|;
name|nestedCalls
argument_list|(
name|nullBool
argument_list|)
expr_stmt|;
name|nestedCalls
argument_list|(
name|vBool
argument_list|()
argument_list|)
expr_stmt|;
name|nestedCalls
argument_list|(
name|vBoolNotNull
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|nestedCalls
parameter_list|(
name|RexNode
name|arg
parameter_list|)
block|{
name|SqlOperator
index|[]
name|operators
init|=
block|{
name|SqlStdOperatorTable
operator|.
name|NOT
block|,
name|SqlStdOperatorTable
operator|.
name|IS_FALSE
block|,
name|SqlStdOperatorTable
operator|.
name|IS_NOT_FALSE
block|,
name|SqlStdOperatorTable
operator|.
name|IS_TRUE
block|,
name|SqlStdOperatorTable
operator|.
name|IS_NOT_TRUE
block|,
name|SqlStdOperatorTable
operator|.
name|IS_NULL
block|,
name|SqlStdOperatorTable
operator|.
name|IS_NOT_NULL
block|,
name|SqlStdOperatorTable
operator|.
name|IS_UNKNOWN
block|,
name|SqlStdOperatorTable
operator|.
name|IS_NOT_UNKNOWN
block|}
decl_stmt|;
for|for
control|(
name|SqlOperator
name|op1
range|:
name|operators
control|)
block|{
name|RexNode
name|n1
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|op1
argument_list|,
name|arg
argument_list|)
decl_stmt|;
name|checkUnknownAs
argument_list|(
name|n1
argument_list|)
expr_stmt|;
for|for
control|(
name|SqlOperator
name|op2
range|:
name|operators
control|)
block|{
name|RexNode
name|n2
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|op2
argument_list|,
name|n1
argument_list|)
decl_stmt|;
name|checkUnknownAs
argument_list|(
name|n2
argument_list|)
expr_stmt|;
for|for
control|(
name|SqlOperator
name|op3
range|:
name|operators
control|)
block|{
name|RexNode
name|n3
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|op3
argument_list|,
name|n2
argument_list|)
decl_stmt|;
name|checkUnknownAs
argument_list|(
name|n3
argument_list|)
expr_stmt|;
for|for
control|(
name|SqlOperator
name|op4
range|:
name|operators
control|)
block|{
name|RexNode
name|n4
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|op4
argument_list|,
name|n3
argument_list|)
decl_stmt|;
name|checkUnknownAs
argument_list|(
name|n4
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|void
name|checkUnknownAs
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
name|checkUnknownAsAndShrink
argument_list|(
name|node
argument_list|,
name|RexUnknownAs
operator|.
name|FALSE
argument_list|)
expr_stmt|;
name|checkUnknownAsAndShrink
argument_list|(
name|node
argument_list|,
name|RexUnknownAs
operator|.
name|UNKNOWN
argument_list|)
expr_stmt|;
name|checkUnknownAsAndShrink
argument_list|(
name|node
argument_list|,
name|RexUnknownAs
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkUnknownAsAndShrink
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|RexUnknownAs
name|unknownAs
parameter_list|)
block|{
try|try
block|{
name|checkUnknownAs
argument_list|(
name|node
argument_list|,
name|unknownAs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Try shrink the example so human can understand it better
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
name|currentSeed
argument_list|)
expr_stmt|;
name|long
name|deadline
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|20000
decl_stmt|;
name|RexNode
name|original
init|=
name|node
decl_stmt|;
name|int
name|len
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|100000
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|deadline
condition|;
name|i
operator|++
control|)
block|{
name|RexShrinker
name|shrinker
init|=
operator|new
name|RexShrinker
argument_list|(
name|rnd
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
name|RexNode
name|newNode
init|=
name|node
operator|.
name|accept
argument_list|(
name|shrinker
argument_list|)
decl_stmt|;
try|try
block|{
name|checkUnknownAs
argument_list|(
name|newNode
argument_list|,
name|unknownAs
argument_list|)
expr_stmt|;
comment|// bad shrink
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
comment|// Good shrink
name|node
operator|=
name|newNode
expr_stmt|;
name|String
name|str
init|=
name|nodeToString
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|int
name|newLen
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|newLen
operator|<
name|len
condition|)
block|{
name|long
name|remaining
init|=
name|deadline
operator|-
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Shrinked to "
operator|+
name|newLen
operator|+
literal|" chars, time remaining "
operator|+
name|remaining
argument_list|)
expr_stmt|;
name|len
operator|=
name|newLen
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|original
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|node
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
comment|// Bad luck, throw original exception
throw|throw
name|e
throw|;
block|}
name|checkUnknownAs
argument_list|(
name|node
argument_list|,
name|unknownAs
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|checkUnknownAs
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|RexUnknownAs
name|unknownAs
parameter_list|)
block|{
name|RexNode
name|opt
decl_stmt|;
specifier|final
name|String
name|uaf
init|=
name|unknownAsString
argument_list|(
name|unknownAs
argument_list|)
decl_stmt|;
try|try
block|{
name|long
name|start
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|opt
operator|=
name|simplify
operator|.
name|simplifyUnknownAs
argument_list|(
name|node
argument_list|,
name|unknownAs
argument_list|)
expr_stmt|;
name|long
name|end
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|end
operator|-
name|start
operator|>
literal|1000
operator|&&
name|slowestTasks
operator|!=
literal|null
condition|)
block|{
name|slowestTasks
operator|.
name|add
argument_list|(
operator|new
name|SimplifyTask
argument_list|(
name|node
argument_list|,
name|currentSeed
argument_list|,
name|opt
argument_list|,
name|end
operator|-
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|AssertionError
name|a
parameter_list|)
block|{
name|String
name|message
init|=
name|a
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
name|message
operator|.
name|startsWith
argument_list|(
literal|"result mismatch"
argument_list|)
condition|)
block|{
throw|throw
name|a
throw|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to simplify "
operator|+
name|uaf
operator|+
name|nodeToString
argument_list|(
name|node
argument_list|)
argument_list|,
name|a
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to simplify "
operator|+
name|uaf
operator|+
name|nodeToString
argument_list|(
name|node
argument_list|)
argument_list|,
name|t
argument_list|)
throw|;
block|}
if|if
condition|(
name|trueLiteral
operator|.
name|equals
argument_list|(
name|opt
argument_list|)
operator|&&
name|node
operator|.
name|isAlwaysFalse
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|nodeToString
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|fail
argument_list|(
name|msg
operator|+
literal|" optimizes to TRUE, isAlwaysFalse MUST not be true "
operator|+
name|uaf
argument_list|)
expr_stmt|;
comment|//      This is a missing optimization, not a bug
comment|//      assertFalse(msg + " optimizes to TRUE, isAlwaysTrue MUST be true",
comment|//          !node.isAlwaysTrue());
block|}
if|if
condition|(
name|falseLiteral
operator|.
name|equals
argument_list|(
name|opt
argument_list|)
operator|&&
name|node
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
name|nodeToString
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|fail
argument_list|(
name|msg
operator|+
literal|" optimizes to FALSE, isAlwaysTrue MUST not be true "
operator|+
name|uaf
argument_list|)
expr_stmt|;
comment|//      This is a missing optimization, not a bug
comment|//      assertFalse(msg + " optimizes to FALSE, isAlwaysFalse MUST be true",
comment|//          !node.isAlwaysFalse());
block|}
if|if
condition|(
name|STRONG
operator|.
name|isNull
argument_list|(
name|opt
argument_list|)
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
name|fail
argument_list|(
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" optimizes to NULL: "
operator|+
name|nodeToString
argument_list|(
name|opt
argument_list|)
operator|+
literal|", isAlwaysTrue MUST be FALSE "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|node
operator|.
name|isAlwaysFalse
argument_list|()
condition|)
block|{
name|fail
argument_list|(
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" optimizes to NULL: "
operator|+
name|nodeToString
argument_list|(
name|opt
argument_list|)
operator|+
literal|", isAlwaysFalse MUST be FALSE "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|node
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|trueLiteral
operator|.
name|equals
argument_list|(
name|opt
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|trueLiteral
argument_list|,
name|opt
argument_list|,
parameter_list|()
lambda|->
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" isAlwaysTrue, so it should simplify to TRUE "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|node
operator|.
name|isAlwaysFalse
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|falseLiteral
operator|.
name|equals
argument_list|(
name|opt
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|falseLiteral
argument_list|,
name|opt
argument_list|,
parameter_list|()
lambda|->
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" isAlwaysFalse, so it should simplify to FALSE "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|STRONG
operator|.
name|isNull
argument_list|(
name|node
argument_list|)
condition|)
block|{
switch|switch
condition|(
name|unknownAs
condition|)
block|{
case|case
name|FALSE
case|:
if|if
condition|(
name|node
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
if|if
condition|(
operator|!
name|falseLiteral
operator|.
name|equals
argument_list|(
name|opt
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|falseLiteral
argument_list|,
name|opt
argument_list|,
parameter_list|()
lambda|->
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" is always null boolean, so it should simplify to FALSE "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|opt
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|node
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|opt
argument_list|,
parameter_list|()
lambda|->
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" is always null (non boolean), so it should simplify to NULL "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|TRUE
case|:
if|if
condition|(
name|node
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|BOOLEAN
condition|)
block|{
if|if
condition|(
operator|!
name|trueLiteral
operator|.
name|equals
argument_list|(
name|opt
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|trueLiteral
argument_list|,
name|opt
argument_list|,
parameter_list|()
lambda|->
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" is always null boolean, so it should simplify to TRUE "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|opt
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|node
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|opt
argument_list|,
parameter_list|()
lambda|->
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" is always null (non boolean), so it should simplify to NULL "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|UNKNOWN
case|:
if|if
condition|(
operator|!
name|RexUtil
operator|.
name|isNull
argument_list|(
name|opt
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|nullBool
argument_list|,
name|opt
argument_list|,
parameter_list|()
lambda|->
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" is always null, so it should simplify to NULL "
operator|+
name|uaf
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|unknownAs
operator|==
name|RexUnknownAs
operator|.
name|UNKNOWN
operator|&&
name|opt
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
operator|&&
operator|!
name|node
operator|.
name|getType
argument_list|()
operator|.
name|isNullable
argument_list|()
condition|)
block|{
name|fail
argument_list|(
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" had non-nullable type "
operator|+
name|opt
operator|.
name|getType
argument_list|()
operator|+
literal|", and it was optimized to "
operator|+
name|nodeToString
argument_list|(
name|opt
argument_list|)
operator|+
literal|" that has nullable type "
operator|+
name|opt
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|equalSansNullability
argument_list|(
name|typeFactory
argument_list|,
name|node
operator|.
name|getType
argument_list|()
argument_list|,
name|opt
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
name|node
operator|.
name|getType
argument_list|()
argument_list|,
name|opt
operator|.
name|getType
argument_list|()
argument_list|,
parameter_list|()
lambda|->
name|nodeToString
argument_list|(
name|node
argument_list|)
operator|+
literal|" has different type after simplification to "
operator|+
name|nodeToString
argument_list|(
name|opt
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Nonnull
specifier|private
name|String
name|unknownAsString
parameter_list|(
name|RexUnknownAs
name|unknownAs
parameter_list|)
block|{
switch|switch
condition|(
name|unknownAs
condition|)
block|{
case|case
name|UNKNOWN
case|:
default|default:
return|return
literal|""
return|;
case|case
name|FALSE
case|:
return|return
literal|"unknownAsFalse"
return|;
case|case
name|TRUE
case|:
return|return
literal|"unknownAsTrue"
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|nodeToString
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|node
operator|+
literal|"\n"
operator|+
name|node
operator|.
name|accept
argument_list|(
operator|new
name|RexToTestCodeShuttle
argument_list|()
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|trimStackTrace
parameter_list|(
name|Throwable
name|t
parameter_list|,
name|int
name|maxStackLines
parameter_list|)
block|{
name|StackTraceElement
index|[]
name|stackTrace
init|=
name|t
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
if|if
condition|(
name|stackTrace
operator|==
literal|null
operator|||
name|stackTrace
operator|.
name|length
operator|<=
name|maxStackLines
condition|)
block|{
return|return;
block|}
name|stackTrace
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|stackTrace
argument_list|,
name|maxStackLines
argument_list|)
expr_stmt|;
name|t
operator|.
name|setStackTrace
argument_list|(
name|stackTrace
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"Ignore for now: CALCITE-3457"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|defaultFuzzTest
parameter_list|()
block|{
try|try
block|{
name|runRexFuzzer
argument_list|(
name|DEFAULT_FUZZ_TEST_SEED
argument_list|,
name|DEFAULT_FUZZ_TEST_DURATION
argument_list|,
literal|1
argument_list|,
name|DEFAULT_FUZZ_TEST_ITERATIONS
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
for|for
control|(
name|Throwable
name|t
init|=
name|e
init|;
name|t
operator|!=
literal|null
condition|;
name|t
operator|=
name|t
operator|.
name|getCause
argument_list|()
control|)
block|{
name|trimStackTrace
argument_list|(
name|t
argument_list|,
name|DEFAULT_FUZZ_TEST_FAIL
condition|?
literal|8
else|:
literal|4
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|DEFAULT_FUZZ_TEST_FAIL
condition|)
block|{
throw|throw
name|e
throw|;
block|}
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Randomized test identified a potential defect. Feel free to fix that issue"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Disabled
argument_list|(
literal|"Ignore for now: CALCITE-3457"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testFuzzy
parameter_list|()
block|{
name|runRexFuzzer
argument_list|(
name|SEED
argument_list|,
name|TEST_DURATION
argument_list|,
name|MAX_FAILURES
argument_list|,
name|TEST_ITERATIONS
argument_list|,
name|TOPN_SLOWEST
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|runRexFuzzer
parameter_list|(
name|long
name|startSeed
parameter_list|,
name|Duration
name|testDuration
parameter_list|,
name|int
name|maxFailures
parameter_list|,
name|long
name|testIterations
parameter_list|,
name|int
name|topnSlowest
parameter_list|)
block|{
if|if
condition|(
name|testDuration
operator|.
name|toMillis
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return;
block|}
name|slowestTasks
operator|=
operator|new
name|TopN
argument_list|<>
argument_list|(
name|topnSlowest
operator|>
literal|0
condition|?
name|topnSlowest
else|:
literal|1
argument_list|)
expr_stmt|;
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
if|if
condition|(
name|startSeed
operator|!=
literal|0
condition|)
block|{
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Using seed {} for rex fuzzing"
argument_list|,
name|startSeed
argument_list|)
expr_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|startSeed
argument_list|)
expr_stmt|;
block|}
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|long
name|deadline
init|=
name|start
operator|+
name|testDuration
operator|.
name|toMillis
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Throwable
argument_list|>
name|exceptions
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|duplicates
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|total
init|=
literal|0
decl_stmt|;
name|int
name|dup
init|=
literal|0
decl_stmt|;
name|int
name|fail
init|=
literal|0
decl_stmt|;
name|RexFuzzer
name|fuzzer
init|=
operator|new
name|RexFuzzer
argument_list|(
name|rexBuilder
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|deadline
operator|&&
name|exceptions
operator|.
name|size
argument_list|()
operator|<
name|maxFailures
operator|&&
operator|(
name|testIterations
operator|==
literal|0
operator|||
name|total
operator|<
name|testIterations
operator|)
condition|)
block|{
name|long
name|seed
init|=
name|r
operator|.
name|nextLong
argument_list|()
decl_stmt|;
name|this
operator|.
name|currentSeed
operator|=
name|seed
expr_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
name|seed
argument_list|)
expr_stmt|;
try|try
block|{
name|total
operator|++
expr_stmt|;
name|generateRexAndCheckTrueFalse
argument_list|(
name|fuzzer
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|duplicates
operator|.
name|add
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
condition|)
block|{
name|dup
operator|++
expr_stmt|;
comment|// known exception, nothing to see here
continue|continue;
block|}
name|fail
operator|++
expr_stmt|;
name|StackTraceElement
index|[]
name|stackTrace
init|=
name|e
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|stackTrace
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|stackTrace
index|[
name|j
index|]
operator|.
name|getClassName
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"RexProgramTest"
argument_list|)
condition|)
block|{
name|e
operator|.
name|setStackTrace
argument_list|(
name|Arrays
operator|.
name|copyOf
argument_list|(
name|stackTrace
argument_list|,
name|j
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
name|e
operator|.
name|addSuppressed
argument_list|(
operator|new
name|Throwable
argument_list|(
literal|"seed "
operator|+
name|seed
argument_list|)
block|{
annotation|@
name|Override
specifier|public
specifier|synchronized
name|Throwable
name|fillInStackTrace
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|exceptions
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|long
name|rate
init|=
name|total
operator|*
literal|1000
operator|/
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
decl_stmt|;
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Rex fuzzing results: number of cases tested={}, failed cases={}, duplicate failures={}, fuzz rate={} per second"
argument_list|,
name|total
argument_list|,
name|fail
argument_list|,
name|dup
argument_list|,
name|rate
argument_list|)
expr_stmt|;
if|if
condition|(
name|topnSlowest
operator|>
literal|0
condition|)
block|{
name|LOGGER
operator|.
name|info
argument_list|(
literal|"The 5 slowest to simplify nodes were"
argument_list|)
expr_stmt|;
name|SimplifyTask
name|task
decl_stmt|;
name|RexToTestCodeShuttle
name|v
init|=
operator|new
name|RexToTestCodeShuttle
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|task
operator|=
name|slowestTasks
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|LOGGER
operator|.
name|info
argument_list|(
name|task
operator|.
name|duration
operator|/
literal|1000
operator|+
literal|" us ("
operator|+
name|task
operator|.
name|seed
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|info
argument_list|(
literal|"      "
operator|+
name|task
operator|.
name|node
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|info
argument_list|(
literal|"      "
operator|+
name|task
operator|.
name|node
operator|.
name|accept
argument_list|(
name|v
argument_list|)
argument_list|)
expr_stmt|;
name|LOGGER
operator|.
name|info
argument_list|(
literal|"    =>"
operator|+
name|task
operator|.
name|result
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|exceptions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Print the shortest fails first
name|exceptions
operator|.
name|sort
argument_list|(
name|Comparator
operator|.
expr|<
name|Throwable
operator|>
name|comparingInt
argument_list|(
name|t
lambda|->
name|t
operator|.
name|getMessage
argument_list|()
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|t
operator|.
name|getMessage
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
operator|.
name|thenComparing
argument_list|(
name|Throwable
operator|::
name|getMessage
argument_list|)
argument_list|)
expr_stmt|;
comment|// The first exception will be thrown, so the others go to printStackTrace
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|exceptions
operator|.
name|size
argument_list|()
operator|&&
name|i
operator|<
literal|100
condition|;
name|i
operator|++
control|)
block|{
name|Throwable
name|exception
init|=
name|exceptions
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|exception
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|Throwable
name|ex
init|=
name|exceptions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|ex
operator|instanceof
name|Error
condition|)
block|{
throw|throw
operator|(
name|Error
operator|)
name|ex
throw|;
block|}
if|if
condition|(
name|ex
operator|instanceof
name|RuntimeException
condition|)
block|{
throw|throw
operator|(
name|RuntimeException
operator|)
name|ex
throw|;
block|}
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Exception in runRexFuzzer"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
specifier|private
name|void
name|generateRexAndCheckTrueFalse
parameter_list|(
name|RexFuzzer
name|fuzzer
parameter_list|,
name|Random
name|r
parameter_list|)
block|{
name|RexNode
name|expression
init|=
name|fuzzer
operator|.
name|getExpression
argument_list|(
name|r
argument_list|,
name|r
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
argument_list|)
decl_stmt|;
name|checkUnknownAs
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"This is just a scaffold for quick investigation of a single fuzz test"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|singleFuzzyTest
parameter_list|()
block|{
name|Random
name|r
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
name|r
operator|.
name|setSeed
argument_list|(
literal|4887662474363391810L
argument_list|)
expr_stmt|;
name|RexFuzzer
name|fuzzer
init|=
operator|new
name|RexFuzzer
argument_list|(
name|rexBuilder
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
name|generateRexAndCheckTrueFalse
argument_list|(
name|fuzzer
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

