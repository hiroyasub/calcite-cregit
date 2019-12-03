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
name|runtime
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
name|interpreter
operator|.
name|Row
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
name|AbstractEnumerable
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
name|Enumerable
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
name|Enumerator
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
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Utilities for processing {@link org.apache.calcite.linq4j.Enumerable}  * collections.  *  *<p>This class is a place to put things not yet added to linq4j.  * Methods are subject to removal without notice.  */
end_comment

begin_class
specifier|public
class|class
name|Enumerables
block|{
specifier|private
name|Enumerables
parameter_list|()
block|{
block|}
comment|/** Converts an enumerable over singleton arrays into the enumerable of their    * first elements. */
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Enumerable
argument_list|<
name|E
argument_list|>
name|slice0
parameter_list|(
name|Enumerable
argument_list|<
name|E
index|[]
argument_list|>
name|enumerable
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|enumerable
operator|.
name|select
argument_list|(
name|elements
lambda|->
name|elements
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/** Converts an {@link Enumerable} over object arrays into an    * {@link Enumerable} over {@link Row} objects. */
specifier|public
specifier|static
name|Enumerable
argument_list|<
name|Row
argument_list|>
name|toRow
parameter_list|(
specifier|final
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerable
parameter_list|)
block|{
return|return
name|enumerable
operator|.
name|select
argument_list|(
operator|(
name|Function1
argument_list|<
name|Object
index|[]
argument_list|,
name|Row
argument_list|>
operator|)
name|Row
operator|::
name|asCopy
argument_list|)
return|;
block|}
comment|/** Converts a supplier of an {@link Enumerable} over object arrays into a    * supplier of an {@link Enumerable} over {@link Row} objects. */
specifier|public
specifier|static
name|Supplier
argument_list|<
name|Enumerable
argument_list|<
name|Row
argument_list|>
argument_list|>
name|toRow
parameter_list|(
specifier|final
name|Supplier
argument_list|<
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|>
name|supplier
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
name|toRow
argument_list|(
name|supplier
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
argument_list|<
name|Enumerable
argument_list|<
name|Row
argument_list|>
argument_list|>
name|toRow
parameter_list|(
specifier|final
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Supplier
argument_list|<
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|>
name|supplier
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
name|toRow
argument_list|(
name|supplier
operator|.
name|get
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|,
name|TKey
parameter_list|,
name|TResult
parameter_list|>
name|Enumerable
argument_list|<
name|TResult
argument_list|>
name|match
parameter_list|(
name|Enumerable
argument_list|<
name|E
argument_list|>
name|enumerable
parameter_list|,
specifier|final
name|Function1
argument_list|<
name|E
argument_list|,
name|TKey
argument_list|>
name|keySelector
parameter_list|,
name|Matcher
argument_list|<
name|E
argument_list|>
name|matcher
parameter_list|,
name|Emitter
argument_list|<
name|E
argument_list|,
name|TResult
argument_list|>
name|emitter
parameter_list|,
name|int
name|history
parameter_list|,
name|int
name|future
parameter_list|)
block|{
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|TResult
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|TResult
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|Enumerator
argument_list|<
name|TResult
argument_list|>
argument_list|()
block|{
specifier|final
name|Enumerator
argument_list|<
name|E
argument_list|>
name|inputEnumerator
init|=
name|enumerable
operator|.
name|enumerator
argument_list|()
decl_stmt|;
comment|// State of each partition.
specifier|final
name|Map
argument_list|<
name|TKey
argument_list|,
name|Matcher
operator|.
name|PartitionState
argument_list|<
name|E
argument_list|>
argument_list|>
name|partitionStates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|inputRow
init|=
operator|-
literal|1
decl_stmt|;
specifier|final
name|Deque
argument_list|<
name|TResult
argument_list|>
name|emitRows
init|=
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
decl_stmt|;
comment|/** Current result row. Null if no row is ready. */
name|TResult
name|resultRow
decl_stmt|;
comment|/** Match counter is 1 based in Oracle */
specifier|final
name|AtomicInteger
name|matchCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|public
name|TResult
name|current
parameter_list|()
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|resultRow
argument_list|)
expr_stmt|;
return|return
name|resultRow
return|;
block|}
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
for|for
control|(
init|;
condition|;
control|)
block|{
name|resultRow
operator|=
name|emitRows
operator|.
name|pollFirst
argument_list|()
expr_stmt|;
if|if
condition|(
name|resultRow
operator|!=
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|// No rows are currently read to emit. Read the next input row,
comment|// see whether it completes a match (or matches), and if so, add
comment|// the resulting rows to the buffer.
if|if
condition|(
operator|!
name|inputEnumerator
operator|.
name|moveNext
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
operator|++
name|inputRow
expr_stmt|;
specifier|final
name|E
name|row
init|=
name|inputEnumerator
operator|.
name|current
argument_list|()
decl_stmt|;
specifier|final
name|TKey
name|key
init|=
name|keySelector
operator|.
name|apply
argument_list|(
name|row
argument_list|)
decl_stmt|;
specifier|final
name|Matcher
operator|.
name|PartitionState
argument_list|<
name|E
argument_list|>
name|partitionState
init|=
name|partitionStates
operator|.
name|computeIfAbsent
argument_list|(
name|key
argument_list|,
name|k
lambda|->
name|matcher
operator|.
name|createPartitionState
argument_list|(
name|history
argument_list|,
name|future
argument_list|)
argument_list|)
decl_stmt|;
name|partitionState
operator|.
name|getMemoryFactory
argument_list|()
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
name|matcher
operator|.
name|matchOne
argument_list|(
name|partitionState
operator|.
name|getRows
argument_list|()
argument_list|,
name|partitionState
argument_list|,
comment|// TODO 26.12.18 jf: add row states (whatever this is?)
name|matches
lambda|->
name|emitter
operator|.
name|emit
argument_list|(
name|matches
operator|.
name|rows
argument_list|,
literal|null
argument_list|,
name|matches
operator|.
name|symbols
argument_list|,
name|matchCounter
operator|.
name|getAndIncrement
argument_list|()
argument_list|,
name|emitRows
operator|::
name|add
argument_list|)
argument_list|)
expr_stmt|;
comment|/*               recentRows.add(e);               int earliestRetainedRow = Integer.MAX_VALUE;               for (int i = 0; i< partitionState.incompleteMatches.size(); i++) {                 MatchState match = partitionState.incompleteMatches.get(i);                 earliestRetainedRow = Math.min(earliestRetainedRow, match.firstRow);                 final int state = automaton.nextState(match.state, e);                 switch (state) {                 case Automaton.ACCEPT:                   final List<E> matchedRows =                       recentRows.subList(0, 0); // TODO:                   final List<Integer> rowStates = ImmutableList.of(); // TODO:                   emitRows.addAll(                       emitter.emit(matchedRows, rowStates,                           partitionState.matchCount++));                   // fall through                 case Automaton.FAIL:                   partitionState.incompleteMatches.remove(i--);                   break;                 default:                   match.state = state;                 }               }               // Try to start a match based on the current row               final int state = automaton.nextState(Automaton.START_STATE, e);               switch (state) {               case Automaton.ACCEPT:                 final List<E> matchedRows = ImmutableList.of(e);                 final List<Integer> rowStates = ImmutableList.of(state);                 emitRows.addAll(                     emitter.emit(matchedRows, rowStates,                         partitionState.matchCount++));                 // fall through               case Automaton.FAIL:                 // since it immediately succeeded or failed, don't add                 // it to the queue                 break;               default:                 partitionState.incompleteMatches.add(                     new MatchState(inputRow, state));               } */
block|}
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|void
name|close
parameter_list|()
block|{
name|inputEnumerator
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
return|;
block|}
comment|/** Given a match (a list of rows, and their states) produces a list    * of rows to be output.    *    * @param<E> element type    * @param<TResult> result type */
specifier|public
interface|interface
name|Emitter
parameter_list|<
name|E
parameter_list|,
name|TResult
parameter_list|>
block|{
name|void
name|emit
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|rows
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|rowStates
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|rowSymbols
parameter_list|,
name|int
name|match
parameter_list|,
name|Consumer
argument_list|<
name|TResult
argument_list|>
name|consumer
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

