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
name|runtime
operator|.
name|Automaton
operator|.
name|EpsilonTransition
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
name|runtime
operator|.
name|Automaton
operator|.
name|State
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
name|runtime
operator|.
name|Automaton
operator|.
name|SymbolTransition
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
name|runtime
operator|.
name|Automaton
operator|.
name|Transition
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
name|Util
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
name|base
operator|.
name|Preconditions
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
name|Comparator
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

begin_comment
comment|/** Builds a state-transition graph for deterministic finite automaton. */
end_comment

begin_class
specifier|public
class|class
name|AutomatonBuilder
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|symbolIds
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|State
argument_list|>
name|stateList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Transition
argument_list|>
name|transitionList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|State
name|startState
init|=
name|createState
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|State
name|endState
init|=
name|createState
argument_list|()
decl_stmt|;
comment|/** Adds a pattern as a start-to-end transition. */
name|AutomatonBuilder
name|add
parameter_list|(
name|Pattern
name|pattern
parameter_list|)
block|{
return|return
name|add
argument_list|(
name|pattern
argument_list|,
name|startState
argument_list|,
name|endState
argument_list|)
return|;
block|}
specifier|private
name|AutomatonBuilder
name|add
parameter_list|(
name|Pattern
name|pattern
parameter_list|,
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|)
block|{
specifier|final
name|Pattern
operator|.
name|AbstractPattern
name|p
init|=
operator|(
name|Pattern
operator|.
name|AbstractPattern
operator|)
name|pattern
decl_stmt|;
switch|switch
condition|(
name|p
operator|.
name|op
condition|)
block|{
case|case
name|SEQ
case|:
specifier|final
name|Pattern
operator|.
name|OpPattern
name|pSeq
init|=
operator|(
name|Pattern
operator|.
name|OpPattern
operator|)
name|p
decl_stmt|;
return|return
name|seq
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|pSeq
operator|.
name|patterns
argument_list|)
return|;
case|case
name|STAR
case|:
specifier|final
name|Pattern
operator|.
name|OpPattern
name|pStar
init|=
operator|(
name|Pattern
operator|.
name|OpPattern
operator|)
name|p
decl_stmt|;
return|return
name|star
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|pStar
operator|.
name|patterns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
case|case
name|PLUS
case|:
specifier|final
name|Pattern
operator|.
name|OpPattern
name|pPlus
init|=
operator|(
name|Pattern
operator|.
name|OpPattern
operator|)
name|p
decl_stmt|;
return|return
name|plus
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|pPlus
operator|.
name|patterns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
case|case
name|REPEAT
case|:
specifier|final
name|Pattern
operator|.
name|RepeatPattern
name|pRepeat
init|=
operator|(
name|Pattern
operator|.
name|RepeatPattern
operator|)
name|p
decl_stmt|;
return|return
name|repeat
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|pRepeat
operator|.
name|patterns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|pRepeat
operator|.
name|minRepeat
argument_list|,
name|pRepeat
operator|.
name|maxRepeat
argument_list|)
return|;
case|case
name|SYMBOL
case|:
specifier|final
name|Pattern
operator|.
name|SymbolPattern
name|pSymbol
init|=
operator|(
name|Pattern
operator|.
name|SymbolPattern
operator|)
name|p
decl_stmt|;
return|return
name|symbol
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|pSymbol
operator|.
name|name
argument_list|)
return|;
case|case
name|OR
case|:
specifier|final
name|Pattern
operator|.
name|OpPattern
name|pOr
init|=
operator|(
name|Pattern
operator|.
name|OpPattern
operator|)
name|p
decl_stmt|;
return|return
name|or
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|pOr
operator|.
name|patterns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|pOr
operator|.
name|patterns
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
case|case
name|OPTIONAL
case|:
comment|// Rewrite as {0,1}
specifier|final
name|Pattern
operator|.
name|OpPattern
name|pOptional
init|=
operator|(
name|Pattern
operator|.
name|OpPattern
operator|)
name|p
decl_stmt|;
return|return
name|optional
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|pOptional
operator|.
name|patterns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown op "
operator|+
name|p
operator|.
name|op
argument_list|)
throw|;
block|}
block|}
specifier|private
name|State
name|createState
parameter_list|()
block|{
specifier|final
name|State
name|state
init|=
operator|new
name|State
argument_list|(
name|stateList
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|stateList
operator|.
name|add
argument_list|(
name|state
argument_list|)
expr_stmt|;
return|return
name|state
return|;
block|}
comment|/** Builds the automaton. */
specifier|public
name|Automaton
name|build
parameter_list|()
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|SymbolTransition
argument_list|>
name|symbolTransitions
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|EpsilonTransition
argument_list|>
name|epsilonTransitions
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Transition
name|transition
range|:
name|transitionList
control|)
block|{
if|if
condition|(
name|transition
operator|instanceof
name|SymbolTransition
condition|)
block|{
name|symbolTransitions
operator|.
name|add
argument_list|(
operator|(
name|SymbolTransition
operator|)
name|transition
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|transition
operator|instanceof
name|EpsilonTransition
condition|)
block|{
name|epsilonTransitions
operator|.
name|add
argument_list|(
operator|(
name|EpsilonTransition
operator|)
name|transition
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Sort the symbols by their ids, which are assumed consecutive and
comment|// starting from zero.
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|symbolNames
init|=
name|symbolIds
operator|.
name|entrySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|sorted
argument_list|(
name|Comparator
operator|.
name|comparingInt
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getValue
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|Map
operator|.
name|Entry
operator|::
name|getKey
argument_list|)
operator|.
name|collect
argument_list|(
name|Util
operator|.
name|toImmutableList
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|Automaton
argument_list|(
name|stateList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|endState
argument_list|,
name|symbolTransitions
operator|.
name|build
argument_list|()
argument_list|,
name|epsilonTransitions
operator|.
name|build
argument_list|()
argument_list|,
name|symbolNames
argument_list|)
return|;
block|}
comment|/** Adds a symbol transition. */
name|AutomatonBuilder
name|symbol
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|name
argument_list|)
expr_stmt|;
specifier|final
name|int
name|symbolId
init|=
name|symbolIds
operator|.
name|computeIfAbsent
argument_list|(
name|name
argument_list|,
name|k
lambda|->
name|symbolIds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|SymbolTransition
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|,
name|symbolId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds a transition made up of a sequence of patterns. */
name|AutomatonBuilder
name|seq
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|,
name|List
argument_list|<
name|Pattern
argument_list|>
name|patterns
parameter_list|)
block|{
name|State
name|prevState
init|=
name|fromState
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
name|patterns
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Pattern
name|pattern
init|=
name|patterns
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|State
name|nextState
decl_stmt|;
if|if
condition|(
name|i
operator|==
name|patterns
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|nextState
operator|=
name|toState
expr_stmt|;
block|}
else|else
block|{
name|nextState
operator|=
name|createState
argument_list|()
expr_stmt|;
block|}
name|add
argument_list|(
name|pattern
argument_list|,
name|prevState
argument_list|,
name|nextState
argument_list|)
expr_stmt|;
name|prevState
operator|=
name|nextState
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
comment|/** Adds a transition for the 'or' pattern. */
name|AutomatonBuilder
name|or
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|,
name|Pattern
name|left
parameter_list|,
name|Pattern
name|right
parameter_list|)
block|{
comment|//
comment|//             left
comment|//         / -------->  toState
comment|//  fromState
comment|//         \ --------> toState
comment|//             right
name|add
argument_list|(
name|left
argument_list|,
name|fromState
argument_list|,
name|toState
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|right
argument_list|,
name|fromState
argument_list|,
name|toState
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds a transition made up of the Kleene star applied to a pattern. */
name|AutomatonBuilder
name|star
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
comment|//
comment|//     +------------------------- e ------------------------+
comment|//     |                +-------- e -------+                |
comment|//     |                |                  |                |
comment|//     |                V                  |                V
comment|// fromState -----> beforeState -----> afterState -----> toState
comment|//             e               pattern              e
comment|//
specifier|final
name|State
name|beforeState
init|=
name|createState
argument_list|()
decl_stmt|;
specifier|final
name|State
name|afterState
init|=
name|createState
argument_list|()
decl_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|fromState
argument_list|,
name|beforeState
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|pattern
argument_list|,
name|beforeState
argument_list|,
name|afterState
argument_list|)
expr_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|afterState
argument_list|,
name|beforeState
argument_list|)
argument_list|)
expr_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|afterState
argument_list|,
name|toState
argument_list|)
argument_list|)
expr_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds a transition made up of a pattern repeated 1 or more times. */
name|AutomatonBuilder
name|plus
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
comment|//
comment|//                      +-------- e -------+
comment|//                      |                  |
comment|//                      V                  |
comment|// fromState -----> beforeState -----> afterState -----> toState
comment|//             e               pattern              e
comment|//
specifier|final
name|State
name|beforeState
init|=
name|createState
argument_list|()
decl_stmt|;
specifier|final
name|State
name|afterState
init|=
name|createState
argument_list|()
decl_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|fromState
argument_list|,
name|beforeState
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|pattern
argument_list|,
name|beforeState
argument_list|,
name|afterState
argument_list|)
expr_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|afterState
argument_list|,
name|beforeState
argument_list|)
argument_list|)
expr_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|afterState
argument_list|,
name|toState
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Adds a transition made up of a pattern repeated between {@code minRepeat}    * and {@code maxRepeat} times. */
name|AutomatonBuilder
name|repeat
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|,
name|Pattern
name|pattern
parameter_list|,
name|int
name|minRepeat
parameter_list|,
name|int
name|maxRepeat
parameter_list|)
block|{
comment|// Diagram for repeat(1, 3)
comment|//                              +-------- e --------------+
comment|//                              |           +---- e ----+ |
comment|//                              |           |           V V
comment|// fromState ---> state0 ---> state1 ---> state2 ---> state3 ---> toState
comment|//            e        pattern     pattern     pattern        e
comment|//
name|Preconditions
operator|.
name|checkArgument
argument_list|(
literal|0
operator|<=
name|minRepeat
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|minRepeat
operator|<=
name|maxRepeat
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
literal|1
operator|<=
name|maxRepeat
argument_list|)
expr_stmt|;
name|State
name|prevState
init|=
name|fromState
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|maxRepeat
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|State
name|s
init|=
name|createState
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|fromState
argument_list|,
name|s
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|add
argument_list|(
name|pattern
argument_list|,
name|prevState
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>=
name|minRepeat
condition|)
block|{
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|s
argument_list|,
name|toState
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|prevState
operator|=
name|s
expr_stmt|;
block|}
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|prevState
argument_list|,
name|toState
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|private
name|AutomatonBuilder
name|optional
parameter_list|(
name|State
name|fromState
parameter_list|,
name|State
name|toState
parameter_list|,
name|Pattern
name|pattern
parameter_list|)
block|{
name|add
argument_list|(
name|pattern
argument_list|,
name|fromState
argument_list|,
name|toState
argument_list|)
expr_stmt|;
name|transitionList
operator|.
name|add
argument_list|(
operator|new
name|EpsilonTransition
argument_list|(
name|fromState
argument_list|,
name|toState
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

