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
name|rel
operator|.
name|core
operator|.
name|Match
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
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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

begin_comment
comment|/** A nondeterministic finite-state automaton (NFA).  *  *<p>It is used to implement the {@link Match}  * relational expression (for the {@code MATCH_RECOGNIZE} clause in SQL).  *  * @see Pattern  * @see AutomatonBuilder  * @see DeterministicAutomaton  */
end_comment

begin_class
specifier|public
class|class
name|Automaton
block|{
specifier|final
name|State
name|startState
decl_stmt|;
specifier|final
name|State
name|endState
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|SymbolTransition
argument_list|>
name|transitions
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|EpsilonTransition
argument_list|>
name|epsilonTransitions
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|symbolNames
decl_stmt|;
comment|/** Use an {@link AutomatonBuilder}. */
name|Automaton
parameter_list|(
name|State
name|startState
parameter_list|,
name|State
name|endState
parameter_list|,
name|ImmutableList
argument_list|<
name|SymbolTransition
argument_list|>
name|transitions
parameter_list|,
name|ImmutableList
argument_list|<
name|EpsilonTransition
argument_list|>
name|epsilonTransitions
parameter_list|,
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|symbolNames
parameter_list|)
block|{
name|this
operator|.
name|startState
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|startState
argument_list|,
literal|"startState"
argument_list|)
expr_stmt|;
name|this
operator|.
name|endState
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|endState
argument_list|,
literal|"endState"
argument_list|)
expr_stmt|;
name|this
operator|.
name|transitions
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|transitions
argument_list|,
literal|"transitions"
argument_list|)
expr_stmt|;
name|this
operator|.
name|epsilonTransitions
operator|=
name|epsilonTransitions
expr_stmt|;
name|this
operator|.
name|symbolNames
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|symbolNames
argument_list|,
literal|"symbolNames"
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the set of states, represented as a bit set, that the graph is    * in when starting from {@code state} and receiving {@code symbol}.    *    * @param fromState Initial state    * @param symbol Symbol received    * @param bitSet Set of successor states (output)    */
name|void
name|successors
parameter_list|(
name|int
name|fromState
parameter_list|,
name|int
name|symbol
parameter_list|,
name|ImmutableBitSet
operator|.
name|Builder
name|bitSet
parameter_list|)
block|{
for|for
control|(
name|SymbolTransition
name|transition
range|:
name|transitions
control|)
block|{
if|if
condition|(
name|transition
operator|.
name|fromState
operator|.
name|id
operator|==
name|fromState
operator|&&
name|transition
operator|.
name|symbol
operator|==
name|symbol
condition|)
block|{
name|epsilonSuccessors
argument_list|(
name|transition
operator|.
name|toState
operator|.
name|id
argument_list|,
name|bitSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|void
name|epsilonSuccessors
parameter_list|(
name|int
name|state
parameter_list|,
name|ImmutableBitSet
operator|.
name|Builder
name|bitSet
parameter_list|)
block|{
name|bitSet
operator|.
name|set
argument_list|(
name|state
argument_list|)
expr_stmt|;
for|for
control|(
name|EpsilonTransition
name|transition
range|:
name|epsilonTransitions
control|)
block|{
if|if
condition|(
name|transition
operator|.
name|fromState
operator|.
name|id
operator|==
name|state
condition|)
block|{
if|if
condition|(
operator|!
name|bitSet
operator|.
name|get
argument_list|(
name|transition
operator|.
name|toState
operator|.
name|id
argument_list|)
condition|)
block|{
name|epsilonSuccessors
argument_list|(
name|transition
operator|.
name|toState
operator|.
name|id
argument_list|,
name|bitSet
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|public
name|ImmutableList
argument_list|<
name|SymbolTransition
argument_list|>
name|getTransitions
parameter_list|()
block|{
return|return
name|this
operator|.
name|transitions
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|EpsilonTransition
argument_list|>
name|getEpsilonTransitions
parameter_list|()
block|{
return|return
name|this
operator|.
name|epsilonTransitions
return|;
block|}
comment|/** Node in the finite-state automaton. A state has a number of    * transitions to other states, each labeled with the symbol that    * causes that transition. */
specifier|static
class|class
name|State
block|{
specifier|final
name|int
name|id
decl_stmt|;
name|State
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
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
name|State
operator|&&
operator|(
operator|(
name|State
operator|)
name|o
operator|)
operator|.
name|id
operator|==
name|id
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
name|id
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
literal|"State{"
operator|+
literal|"id="
operator|+
name|id
operator|+
literal|'}'
return|;
block|}
block|}
comment|/** Transition from one state to another in the finite-state automaton. */
specifier|abstract
specifier|static
class|class
name|Transition
block|{
specifier|final
name|State
name|fromState
decl_stmt|;
specifier|final
name|State
name|toState
decl_stmt|;
name|Transition
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|)
block|{
name|this
operator|.
name|fromState
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fromState
argument_list|,
literal|"fromState"
argument_list|)
expr_stmt|;
name|this
operator|.
name|toState
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|toState
argument_list|,
literal|"toState"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** A transition caused by reading a symbol. */
specifier|static
class|class
name|SymbolTransition
extends|extends
name|Transition
block|{
specifier|final
name|int
name|symbol
decl_stmt|;
name|SymbolTransition
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|,
name|int
name|symbol
parameter_list|)
block|{
name|super
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|)
expr_stmt|;
name|this
operator|.
name|symbol
operator|=
name|symbol
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|symbol
operator|+
literal|":"
operator|+
name|fromState
operator|.
name|id
operator|+
literal|"->"
operator|+
name|toState
operator|.
name|id
return|;
block|}
block|}
comment|/** A transition that may happen without reading a symbol. */
specifier|static
class|class
name|EpsilonTransition
extends|extends
name|Transition
block|{
name|EpsilonTransition
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|)
block|{
name|super
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"epsilon:"
operator|+
name|fromState
operator|.
name|id
operator|+
literal|"->"
operator|+
name|toState
operator|.
name|id
return|;
block|}
block|}
block|}
end_class

end_unit

