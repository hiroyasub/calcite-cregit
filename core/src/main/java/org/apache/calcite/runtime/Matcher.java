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
name|linq4j
operator|.
name|MemoryFactory
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|Collection
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
name|HashSet
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
name|Set
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
name|Predicate
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Workspace that partialMatches patterns against an automaton.  * @param<E> Type of rows matched by this automaton  */
end_comment

begin_class
specifier|public
class|class
name|Matcher
parameter_list|<
name|E
parameter_list|>
block|{
specifier|private
specifier|final
name|DeterministicAutomaton
name|dfa
decl_stmt|;
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Predicate
argument_list|<
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|>
name|predicates
decl_stmt|;
comment|// The following members are work space. They can be shared among partitions,
comment|// but only one thread can use them at a time. Putting them here saves the
comment|// expense of creating a fresh object each call to "match".
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|Tuple
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|emptyStateSet
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|ImmutableBitSet
name|startSet
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|rowSymbols
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|/**    * Creates a Matcher; use {@link #builder}.    */
specifier|private
name|Matcher
parameter_list|(
name|Automaton
name|automaton
parameter_list|,
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Predicate
argument_list|<
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|>
name|predicates
parameter_list|)
block|{
name|this
operator|.
name|predicates
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|predicates
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableBitSet
operator|.
name|Builder
name|startSetBuilder
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
name|startSetBuilder
operator|.
name|set
argument_list|(
name|automaton
operator|.
name|startState
operator|.
name|id
argument_list|)
expr_stmt|;
name|automaton
operator|.
name|epsilonSuccessors
argument_list|(
name|automaton
operator|.
name|startState
operator|.
name|id
argument_list|,
name|startSetBuilder
argument_list|)
expr_stmt|;
name|startSet
operator|=
name|startSetBuilder
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Build the DFA
name|dfa
operator|=
operator|new
name|DeterministicAutomaton
argument_list|(
name|automaton
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
parameter_list|<
name|E
parameter_list|>
name|Builder
argument_list|<
name|E
argument_list|>
name|builder
parameter_list|(
name|Automaton
name|automaton
parameter_list|)
block|{
return|return
operator|new
name|Builder
argument_list|<>
argument_list|(
name|automaton
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|match
parameter_list|(
name|E
modifier|...
name|rows
parameter_list|)
block|{
return|return
name|match
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|rows
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|match
parameter_list|(
name|Iterable
argument_list|<
name|E
argument_list|>
name|rows
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|resultMatchBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|Consumer
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|resultMatchConsumer
init|=
name|resultMatchBuilder
operator|::
name|add
decl_stmt|;
specifier|final
name|PartitionState
argument_list|<
name|E
argument_list|>
name|partitionState
init|=
name|createPartitionState
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|E
name|row
range|:
name|rows
control|)
block|{
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
name|matchOne
argument_list|(
name|partitionState
operator|.
name|getRows
argument_list|()
argument_list|,
name|partitionState
argument_list|,
name|resultMatchConsumer
argument_list|)
expr_stmt|;
block|}
return|return
name|resultMatchBuilder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|PartitionState
argument_list|<
name|E
argument_list|>
name|createPartitionState
parameter_list|(
name|int
name|history
parameter_list|,
name|int
name|future
parameter_list|)
block|{
return|return
operator|new
name|PartitionState
argument_list|<>
argument_list|(
name|history
argument_list|,
name|future
argument_list|)
return|;
block|}
comment|/**    * Feeds a single input row into the given partition state,    * and writes the resulting output rows (if any).    * This method ignores the symbols that caused a transition.    */
specifier|protected
name|void
name|matchOne
parameter_list|(
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
name|rows
parameter_list|,
name|PartitionState
argument_list|<
name|E
argument_list|>
name|partitionState
parameter_list|,
name|Consumer
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|resultMatches
parameter_list|)
block|{
name|List
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|matches
init|=
name|matchOneWithSymbols
argument_list|(
name|rows
argument_list|,
name|partitionState
argument_list|)
decl_stmt|;
for|for
control|(
name|PartialMatch
argument_list|<
name|E
argument_list|>
name|pm
range|:
name|matches
control|)
block|{
name|resultMatches
operator|.
name|accept
argument_list|(
name|pm
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|List
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|matchOneWithSymbols
parameter_list|(
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
name|rows
parameter_list|,
name|PartitionState
argument_list|<
name|E
argument_list|>
name|partitionState
parameter_list|)
block|{
specifier|final
name|HashSet
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|newMatches
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Predicate
argument_list|<
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|>
name|predicate
range|:
name|predicates
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
name|PartialMatch
argument_list|<
name|E
argument_list|>
name|pm
range|:
name|partitionState
operator|.
name|getPartialMatches
argument_list|()
control|)
block|{
comment|// Remove this match
if|if
condition|(
name|predicate
operator|.
name|getValue
argument_list|()
operator|.
name|test
argument_list|(
name|rows
argument_list|)
condition|)
block|{
comment|// Check if we have transitions from here
specifier|final
name|List
argument_list|<
name|DeterministicAutomaton
operator|.
name|Transition
argument_list|>
name|transitions
init|=
name|dfa
operator|.
name|getTransitions
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|t
lambda|->
name|predicate
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|t
operator|.
name|symbol
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|t
lambda|->
name|pm
operator|.
name|currentState
operator|.
name|equals
argument_list|(
name|t
operator|.
name|fromState
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|DeterministicAutomaton
operator|.
name|Transition
name|transition
range|:
name|transitions
control|)
block|{
comment|// System.out.println("Append new transition to ");
specifier|final
name|PartialMatch
argument_list|<
name|E
argument_list|>
name|newMatch
init|=
name|pm
operator|.
name|append
argument_list|(
name|transition
operator|.
name|symbol
argument_list|,
name|rows
operator|.
name|get
argument_list|()
argument_list|,
name|transition
operator|.
name|toState
argument_list|)
decl_stmt|;
name|newMatches
operator|.
name|add
argument_list|(
name|newMatch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Check if a new Match starts here
if|if
condition|(
name|predicate
operator|.
name|getValue
argument_list|()
operator|.
name|test
argument_list|(
name|rows
argument_list|)
condition|)
block|{
specifier|final
name|List
argument_list|<
name|DeterministicAutomaton
operator|.
name|Transition
argument_list|>
name|transitions
init|=
name|dfa
operator|.
name|getTransitions
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|t
lambda|->
name|predicate
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
name|t
operator|.
name|symbol
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|t
lambda|->
name|dfa
operator|.
name|startState
operator|.
name|equals
argument_list|(
name|t
operator|.
name|fromState
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|DeterministicAutomaton
operator|.
name|Transition
name|transition
range|:
name|transitions
control|)
block|{
specifier|final
name|PartialMatch
argument_list|<
name|E
argument_list|>
name|newMatch
init|=
operator|new
name|PartialMatch
argument_list|<>
argument_list|(
operator|-
literal|1L
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|transition
operator|.
name|symbol
argument_list|)
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rows
operator|.
name|get
argument_list|()
argument_list|)
argument_list|,
name|transition
operator|.
name|toState
argument_list|)
decl_stmt|;
name|newMatches
operator|.
name|add
argument_list|(
name|newMatch
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Remove all current partitions
name|partitionState
operator|.
name|clearPartitions
argument_list|()
expr_stmt|;
comment|// Add all partial matches
name|partitionState
operator|.
name|addPartialMatches
argument_list|(
name|newMatches
argument_list|)
expr_stmt|;
comment|// Check if one of the new Matches is in a final state, otherwise add them
comment|// and go on
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|PartialMatch
argument_list|<
name|E
argument_list|>
name|match
range|:
name|newMatches
control|)
block|{
if|if
condition|(
name|dfa
operator|.
name|getEndStates
argument_list|()
operator|.
name|contains
argument_list|(
name|match
operator|.
name|currentState
argument_list|)
condition|)
block|{
comment|// This is the match, handle all "open" partial matches with a suitable
comment|// strategy
comment|// TODO add strategy
comment|// Return it!
name|builder
operator|.
name|add
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * State for each partition.    *    * @param<E> Row type    */
specifier|static
class|class
name|PartitionState
parameter_list|<
name|E
parameter_list|>
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|partialMatches
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|MemoryFactory
argument_list|<
name|E
argument_list|>
name|memoryFactory
decl_stmt|;
name|PartitionState
parameter_list|(
name|int
name|history
parameter_list|,
name|int
name|future
parameter_list|)
block|{
name|this
operator|.
name|memoryFactory
operator|=
operator|new
name|MemoryFactory
argument_list|<>
argument_list|(
name|history
argument_list|,
name|future
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|addPartialMatches
parameter_list|(
name|Collection
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|matches
parameter_list|)
block|{
name|partialMatches
operator|.
name|addAll
argument_list|(
name|matches
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Set
argument_list|<
name|PartialMatch
argument_list|<
name|E
argument_list|>
argument_list|>
name|getPartialMatches
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|partialMatches
argument_list|)
return|;
block|}
specifier|public
name|void
name|removePartialMatch
parameter_list|(
name|PartialMatch
argument_list|<
name|E
argument_list|>
name|pm
parameter_list|)
block|{
name|partialMatches
operator|.
name|remove
argument_list|(
name|pm
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|clearPartitions
parameter_list|()
block|{
name|partialMatches
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|public
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
name|getRows
parameter_list|()
block|{
return|return
name|memoryFactory
operator|.
name|create
argument_list|()
return|;
block|}
specifier|public
name|MemoryFactory
argument_list|<
name|E
argument_list|>
name|getMemoryFactory
parameter_list|()
block|{
return|return
name|this
operator|.
name|memoryFactory
return|;
block|}
block|}
comment|/**    * Partial match of the NFA.    *    *<p>This class is immutable; the {@link #copy()} and    * {@link #append(String, Object, DeterministicAutomaton.MultiState)}    * methods generate new instances.    *    * @param<E> Row type    */
specifier|static
class|class
name|PartialMatch
parameter_list|<
name|E
parameter_list|>
block|{
specifier|final
name|long
name|startRow
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|symbols
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|E
argument_list|>
name|rows
decl_stmt|;
specifier|final
name|DeterministicAutomaton
operator|.
name|MultiState
name|currentState
decl_stmt|;
name|PartialMatch
parameter_list|(
name|long
name|startRow
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|symbols
parameter_list|,
name|ImmutableList
argument_list|<
name|E
argument_list|>
name|rows
parameter_list|,
name|DeterministicAutomaton
operator|.
name|MultiState
name|currentState
parameter_list|)
block|{
name|this
operator|.
name|startRow
operator|=
name|startRow
expr_stmt|;
name|this
operator|.
name|symbols
operator|=
name|symbols
expr_stmt|;
name|this
operator|.
name|rows
operator|=
name|rows
expr_stmt|;
name|this
operator|.
name|currentState
operator|=
name|currentState
expr_stmt|;
block|}
specifier|public
name|PartialMatch
argument_list|<
name|E
argument_list|>
name|copy
parameter_list|()
block|{
return|return
operator|new
name|PartialMatch
argument_list|<>
argument_list|(
name|startRow
argument_list|,
name|symbols
argument_list|,
name|rows
argument_list|,
name|currentState
argument_list|)
return|;
block|}
specifier|public
name|PartialMatch
argument_list|<
name|E
argument_list|>
name|append
parameter_list|(
name|String
name|symbol
parameter_list|,
name|E
name|row
parameter_list|,
name|DeterministicAutomaton
operator|.
name|MultiState
name|toState
parameter_list|)
block|{
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|symbols
init|=
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|symbols
argument_list|)
operator|.
name|add
argument_list|(
name|symbol
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|ImmutableList
argument_list|<
name|E
argument_list|>
name|rows
init|=
name|ImmutableList
operator|.
expr|<
name|E
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|rows
argument_list|)
operator|.
name|add
argument_list|(
name|row
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
return|return
operator|new
name|PartialMatch
argument_list|<>
argument_list|(
name|startRow
argument_list|,
name|symbols
argument_list|,
name|rows
argument_list|,
name|toState
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|instanceof
name|PartialMatch
operator|&&
name|startRow
operator|==
operator|(
operator|(
name|PartialMatch
operator|)
name|o
operator|)
operator|.
name|startRow
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|symbols
argument_list|,
operator|(
operator|(
name|PartialMatch
operator|)
name|o
operator|)
operator|.
name|symbols
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|rows
argument_list|,
operator|(
operator|(
name|PartialMatch
operator|)
name|o
operator|)
operator|.
name|rows
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|currentState
argument_list|,
operator|(
operator|(
name|PartialMatch
operator|)
name|o
operator|)
operator|.
name|currentState
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|startRow
argument_list|,
name|symbols
argument_list|,
name|rows
argument_list|,
name|currentState
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rows
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|symbols
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|rows
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Builds a Matcher.    *    * @param<E> Type of rows matched by this automaton    */
specifier|public
specifier|static
class|class
name|Builder
parameter_list|<
name|E
parameter_list|>
block|{
specifier|final
name|Automaton
name|automaton
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Predicate
argument_list|<
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|>
name|symbolPredicates
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|Builder
parameter_list|(
name|Automaton
name|automaton
parameter_list|)
block|{
name|this
operator|.
name|automaton
operator|=
name|automaton
expr_stmt|;
block|}
comment|/**      * Associates a predicate with a symbol.      */
specifier|public
name|Builder
argument_list|<
name|E
argument_list|>
name|add
parameter_list|(
name|String
name|symbolName
parameter_list|,
name|Predicate
argument_list|<
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
argument_list|>
name|predicate
parameter_list|)
block|{
name|symbolPredicates
operator|.
name|put
argument_list|(
name|symbolName
argument_list|,
name|predicate
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Matcher
argument_list|<
name|E
argument_list|>
name|build
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|predicateSymbolsNotInGraph
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|(
name|symbolPredicates
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|predicateSymbolsNotInGraph
operator|.
name|removeAll
argument_list|(
name|automaton
operator|.
name|symbolNames
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|predicateSymbolsNotInGraph
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"not all predicate symbols ["
operator|+
name|predicateSymbolsNotInGraph
operator|+
literal|"] are in graph ["
operator|+
name|automaton
operator|.
name|symbolNames
operator|+
literal|"]"
argument_list|)
throw|;
block|}
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|Predicate
argument_list|<
name|MemoryFactory
operator|.
name|Memory
argument_list|<
name|E
argument_list|>
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|symbolName
range|:
name|automaton
operator|.
name|symbolNames
control|)
block|{
comment|// If a symbol does not have a predicate, it defaults to true.
comment|// By convention, "STRT" is used for the start symbol, but it could be
comment|// anything.
name|builder
operator|.
name|put
argument_list|(
name|symbolName
argument_list|,
name|symbolPredicates
operator|.
name|getOrDefault
argument_list|(
name|symbolName
argument_list|,
name|e
lambda|->
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Matcher
argument_list|<>
argument_list|(
name|automaton
argument_list|,
name|builder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Represents a Tuple of a symbol and a row    *    * @param<E> Type of Row    */
specifier|static
class|class
name|Tuple
parameter_list|<
name|E
parameter_list|>
block|{
specifier|final
name|String
name|symbol
decl_stmt|;
specifier|final
name|E
name|row
decl_stmt|;
name|Tuple
parameter_list|(
name|String
name|symbol
parameter_list|,
name|E
name|row
parameter_list|)
block|{
name|this
operator|.
name|symbol
operator|=
name|symbol
expr_stmt|;
name|this
operator|.
name|row
operator|=
name|row
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|instanceof
name|Tuple
operator|&&
operator|(
operator|(
name|Tuple
operator|)
name|o
operator|)
operator|.
name|symbol
operator|.
name|equals
argument_list|(
name|symbol
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|row
argument_list|,
operator|(
operator|(
name|Tuple
operator|)
name|o
operator|)
operator|.
name|row
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|symbol
argument_list|,
name|row
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"("
operator|+
name|symbol
operator|+
literal|", "
operator|+
name|row
operator|+
literal|")"
return|;
block|}
block|}
block|}
end_class

end_unit

