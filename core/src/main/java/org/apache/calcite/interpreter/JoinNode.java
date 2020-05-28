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
name|interpreter
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
name|Join
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
name|rel
operator|.
name|core
operator|.
name|JoinRelType
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
name|ArrayList
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
name|Set
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Interpreter node that implements a  * {@link org.apache.calcite.rel.core.Join}.  */
end_comment

begin_class
specifier|public
class|class
name|JoinNode
implements|implements
name|Node
block|{
specifier|private
specifier|final
name|Source
name|leftSource
decl_stmt|;
specifier|private
specifier|final
name|Source
name|rightSource
decl_stmt|;
specifier|private
specifier|final
name|Sink
name|sink
decl_stmt|;
specifier|private
specifier|final
name|Join
name|rel
decl_stmt|;
specifier|private
specifier|final
name|Scalar
name|condition
decl_stmt|;
specifier|private
specifier|final
name|Context
name|context
decl_stmt|;
specifier|public
name|JoinNode
parameter_list|(
name|Compiler
name|compiler
parameter_list|,
name|Join
name|rel
parameter_list|)
block|{
name|this
operator|.
name|leftSource
operator|=
name|compiler
operator|.
name|source
argument_list|(
name|rel
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|rightSource
operator|=
name|compiler
operator|.
name|source
argument_list|(
name|rel
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|compiler
operator|.
name|sink
argument_list|(
name|rel
argument_list|)
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|compiler
operator|.
name|compile
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|rel
operator|.
name|getCondition
argument_list|()
argument_list|)
argument_list|,
name|compiler
operator|.
name|combinedRowType
argument_list|(
name|rel
operator|.
name|getInputs
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|compiler
operator|.
name|createContext
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|int
name|fieldCount
init|=
name|rel
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
operator|+
name|rel
operator|.
name|getRight
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
name|context
operator|.
name|values
operator|=
operator|new
name|Object
index|[
name|fieldCount
index|]
expr_stmt|;
comment|// source for the outer relation of nested loop
name|Source
name|outerSource
init|=
name|leftSource
decl_stmt|;
comment|// source for the inner relation of nested loop
name|Source
name|innerSource
init|=
name|rightSource
decl_stmt|;
if|if
condition|(
name|rel
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|RIGHT
condition|)
block|{
name|outerSource
operator|=
name|rightSource
expr_stmt|;
name|innerSource
operator|=
name|leftSource
expr_stmt|;
block|}
comment|// row from outer source
name|Row
name|outerRow
init|=
literal|null
decl_stmt|;
comment|// rows from inner source
name|List
argument_list|<
name|Row
argument_list|>
name|innerRows
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|Row
argument_list|>
name|matchRowSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|outerRow
operator|=
name|outerSource
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|innerRows
operator|==
literal|null
condition|)
block|{
name|innerRows
operator|=
operator|new
name|ArrayList
argument_list|<
name|Row
argument_list|>
argument_list|()
expr_stmt|;
name|Row
name|innerRow
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|innerRow
operator|=
name|innerSource
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|innerRows
operator|.
name|add
argument_list|(
name|innerRow
argument_list|)
expr_stmt|;
block|}
block|}
name|matchRowSet
operator|.
name|addAll
argument_list|(
name|doJoin
argument_list|(
name|outerRow
argument_list|,
name|innerRows
argument_list|,
name|rel
operator|.
name|getJoinType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rel
operator|.
name|getJoinType
argument_list|()
operator|==
name|JoinRelType
operator|.
name|FULL
condition|)
block|{
comment|// send un-match rows for full join on right source
name|List
argument_list|<
name|Row
argument_list|>
name|empty
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// TODO: CALCITE-4308, JointNode in Interpreter might fail with NPE for FULL join
for|for
control|(
name|Row
name|row
range|:
name|requireNonNull
argument_list|(
name|innerRows
argument_list|,
literal|"innerRows"
argument_list|)
control|)
block|{
if|if
condition|(
name|matchRowSet
operator|.
name|contains
argument_list|(
name|row
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|doSend
argument_list|(
name|row
argument_list|,
name|empty
argument_list|,
name|JoinRelType
operator|.
name|RIGHT
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Execution of the join action, returns the matched rows for the outer source row.    */
specifier|private
name|List
argument_list|<
name|Row
argument_list|>
name|doJoin
parameter_list|(
name|Row
name|outerRow
parameter_list|,
name|List
argument_list|<
name|Row
argument_list|>
name|innerRows
parameter_list|,
name|JoinRelType
name|joinRelType
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|boolean
name|outerRowOnLeft
init|=
name|joinRelType
operator|!=
name|JoinRelType
operator|.
name|RIGHT
decl_stmt|;
name|copyToContext
argument_list|(
name|outerRow
argument_list|,
name|outerRowOnLeft
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Row
argument_list|>
name|matchInnerRows
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Row
name|innerRow
range|:
name|innerRows
control|)
block|{
name|copyToContext
argument_list|(
name|innerRow
argument_list|,
operator|!
name|outerRowOnLeft
argument_list|)
expr_stmt|;
specifier|final
name|Boolean
name|execute
init|=
operator|(
name|Boolean
operator|)
name|condition
operator|.
name|execute
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|execute
operator|!=
literal|null
operator|&&
name|execute
condition|)
block|{
name|matchInnerRows
operator|.
name|add
argument_list|(
name|innerRow
argument_list|)
expr_stmt|;
block|}
block|}
name|doSend
argument_list|(
name|outerRow
argument_list|,
name|matchInnerRows
argument_list|,
name|joinRelType
argument_list|)
expr_stmt|;
return|return
name|matchInnerRows
return|;
block|}
comment|/**    * If there exists matched rows with the outer row, sends the corresponding joined result,    * otherwise, checks if need to use null value for column.    */
specifier|private
name|void
name|doSend
parameter_list|(
name|Row
name|outerRow
parameter_list|,
name|List
argument_list|<
name|Row
argument_list|>
name|matchInnerRows
parameter_list|,
name|JoinRelType
name|joinRelType
parameter_list|)
throws|throws
name|InterruptedException
block|{
if|if
condition|(
operator|!
name|matchInnerRows
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
switch|switch
condition|(
name|joinRelType
condition|)
block|{
case|case
name|INNER
case|:
case|case
name|LEFT
case|:
case|case
name|RIGHT
case|:
case|case
name|FULL
case|:
name|boolean
name|outerRowOnLeft
init|=
name|joinRelType
operator|!=
name|JoinRelType
operator|.
name|RIGHT
decl_stmt|;
name|copyToContext
argument_list|(
name|outerRow
argument_list|,
name|outerRowOnLeft
argument_list|)
expr_stmt|;
name|requireNonNull
argument_list|(
name|context
operator|.
name|values
argument_list|,
literal|"context.values"
argument_list|)
expr_stmt|;
for|for
control|(
name|Row
name|row
range|:
name|matchInnerRows
control|)
block|{
name|copyToContext
argument_list|(
name|row
argument_list|,
operator|!
name|outerRowOnLeft
argument_list|)
expr_stmt|;
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|asCopy
argument_list|(
name|context
operator|.
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|SEMI
case|:
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|asCopy
argument_list|(
name|outerRow
operator|.
name|getValues
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
else|else
block|{
switch|switch
condition|(
name|joinRelType
condition|)
block|{
case|case
name|LEFT
case|:
case|case
name|RIGHT
case|:
case|case
name|FULL
case|:
name|requireNonNull
argument_list|(
name|context
operator|.
name|values
argument_list|,
literal|"context.values"
argument_list|)
expr_stmt|;
name|int
name|nullColumnNum
init|=
name|context
operator|.
name|values
operator|.
name|length
operator|-
name|outerRow
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// for full join, use left source as outer source,
comment|// and send un-match rows in left source fist,
comment|// the un-match rows in right source will be process later.
name|copyToContext
argument_list|(
name|outerRow
argument_list|,
name|joinRelType
operator|.
name|generatesNullsOnRight
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|nullColumnStart
init|=
name|joinRelType
operator|.
name|generatesNullsOnRight
argument_list|()
condition|?
name|outerRow
operator|.
name|size
argument_list|()
else|:
literal|0
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
operator|new
name|Object
index|[
name|nullColumnNum
index|]
argument_list|,
literal|0
argument_list|,
name|context
operator|.
name|values
argument_list|,
name|nullColumnStart
argument_list|,
name|nullColumnNum
argument_list|)
expr_stmt|;
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|asCopy
argument_list|(
name|context
operator|.
name|values
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|ANTI
case|:
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|asCopy
argument_list|(
name|outerRow
operator|.
name|getValues
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
block|}
comment|/**    * Copies the value of row into context values.    */
specifier|private
name|void
name|copyToContext
parameter_list|(
name|Row
name|row
parameter_list|,
name|boolean
name|toLeftSide
parameter_list|)
block|{
annotation|@
name|Nullable
name|Object
index|[]
name|values
init|=
name|row
operator|.
name|getValues
argument_list|()
decl_stmt|;
name|requireNonNull
argument_list|(
name|context
operator|.
name|values
argument_list|,
literal|"context.values"
argument_list|)
expr_stmt|;
if|if
condition|(
name|toLeftSide
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|context
operator|.
name|values
argument_list|,
literal|0
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|context
operator|.
name|values
argument_list|,
name|context
operator|.
name|values
operator|.
name|length
operator|-
name|values
operator|.
name|length
argument_list|,
name|values
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

