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
name|plan
operator|.
name|volcano
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
name|RelNode
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
name|Pair
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
name|Map
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
name|Predicate
import|;
end_import

begin_comment
comment|/**  * A rule queue that manages rule matches for cascades planner.  */
end_comment

begin_class
class|class
name|TopDownRuleQueue
extends|extends
name|RuleQueue
block|{
specifier|private
specifier|final
name|Map
argument_list|<
name|RelNode
argument_list|,
name|Deque
argument_list|<
name|VolcanoRuleMatch
argument_list|>
argument_list|>
name|matches
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|names
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|TopDownRuleQueue
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
name|super
argument_list|(
name|planner
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addMatch
parameter_list|(
name|VolcanoRuleMatch
name|match
parameter_list|)
block|{
name|RelNode
name|rel
init|=
name|match
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Deque
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|queue
init|=
name|matches
operator|.
name|computeIfAbsent
argument_list|(
name|rel
argument_list|,
name|id
lambda|->
operator|new
name|ArrayDeque
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
name|addMatch
argument_list|(
name|match
argument_list|,
name|queue
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addMatch
parameter_list|(
name|VolcanoRuleMatch
name|match
parameter_list|,
name|Deque
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|queue
parameter_list|)
block|{
if|if
condition|(
operator|!
name|names
operator|.
name|add
argument_list|(
name|match
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
return|return;
block|}
comment|// The substitution rule would be applied first though it is added at the end of the queue.
comment|// The process looks like:
comment|//   1) put the non-substitution rule at the front and substitution rule at the end of the queue
comment|//   2) get each rule from the queue in order from first to last and generate an ApplyRule task
comment|//   3) push each ApplyRule task into the task stack
comment|// As a result, substitution rule is executed first since the ApplyRule(substitution) task is
comment|// popped earlier than the ApplyRule(non-substitution) task from the stack.
if|if
condition|(
operator|!
name|planner
operator|.
name|isSubstituteRule
argument_list|(
name|match
argument_list|)
condition|)
block|{
name|queue
operator|.
name|addFirst
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queue
operator|.
name|addLast
argument_list|(
name|match
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
annotation|@
name|Nullable
name|VolcanoRuleMatch
name|popMatch
parameter_list|(
name|Pair
argument_list|<
name|RelNode
argument_list|,
name|Predicate
argument_list|<
name|VolcanoRuleMatch
argument_list|>
argument_list|>
name|category
parameter_list|)
block|{
name|Deque
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|queue
init|=
name|matches
operator|.
name|get
argument_list|(
name|category
operator|.
name|left
argument_list|)
decl_stmt|;
if|if
condition|(
name|queue
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Iterator
argument_list|<
name|VolcanoRuleMatch
argument_list|>
name|iterator
init|=
name|queue
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|VolcanoRuleMatch
name|next
init|=
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|category
operator|.
name|right
operator|!=
literal|null
operator|&&
operator|!
name|category
operator|.
name|right
operator|.
name|test
argument_list|(
name|next
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|iterator
operator|.
name|remove
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|skipMatch
argument_list|(
name|next
argument_list|)
condition|)
block|{
return|return
name|next
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|clear
parameter_list|()
block|{
name|boolean
name|empty
init|=
name|matches
operator|.
name|isEmpty
argument_list|()
decl_stmt|;
name|matches
operator|.
name|clear
argument_list|()
expr_stmt|;
name|names
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return
operator|!
name|empty
return|;
block|}
block|}
end_class

end_unit

