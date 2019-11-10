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
name|SetOp
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
name|HashMultiset
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
name|HashSet
import|;
end_import

begin_comment
comment|/**  * Interpreter node that implements a  * {@link org.apache.calcite.rel.core.SetOp},  * including {@link org.apache.calcite.rel.core.Minus},  * {@link org.apache.calcite.rel.core.Union} and  * {@link org.apache.calcite.rel.core.Intersect}.  */
end_comment

begin_class
specifier|public
class|class
name|SetOpNode
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
name|SetOp
name|setOp
decl_stmt|;
specifier|public
name|SetOpNode
parameter_list|(
name|Compiler
name|compiler
parameter_list|,
name|SetOp
name|setOp
parameter_list|)
block|{
name|leftSource
operator|=
name|compiler
operator|.
name|source
argument_list|(
name|setOp
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|rightSource
operator|=
name|compiler
operator|.
name|source
argument_list|(
name|setOp
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|sink
operator|=
name|compiler
operator|.
name|sink
argument_list|(
name|setOp
argument_list|)
expr_stmt|;
name|this
operator|.
name|setOp
operator|=
name|setOp
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
name|Collection
argument_list|<
name|Row
argument_list|>
name|leftRows
decl_stmt|;
specifier|final
name|Collection
argument_list|<
name|Row
argument_list|>
name|rightRows
decl_stmt|;
if|if
condition|(
name|setOp
operator|.
name|all
condition|)
block|{
name|leftRows
operator|=
name|HashMultiset
operator|.
name|create
argument_list|()
expr_stmt|;
name|rightRows
operator|=
name|HashMultiset
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|leftRows
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|rightRows
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|Row
name|row
decl_stmt|;
while|while
condition|(
operator|(
name|row
operator|=
name|leftSource
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|leftRows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
operator|(
name|row
operator|=
name|rightSource
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|rightRows
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|setOp
operator|.
name|kind
condition|)
block|{
case|case
name|INTERSECT
case|:
for|for
control|(
name|Row
name|leftRow
range|:
name|leftRows
control|)
block|{
if|if
condition|(
name|rightRows
operator|.
name|remove
argument_list|(
name|leftRow
argument_list|)
condition|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|leftRow
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|EXCEPT
case|:
for|for
control|(
name|Row
name|leftRow
range|:
name|leftRows
control|)
block|{
if|if
condition|(
operator|!
name|rightRows
operator|.
name|remove
argument_list|(
name|leftRow
argument_list|)
condition|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|leftRow
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|UNION
case|:
name|leftRows
operator|.
name|addAll
argument_list|(
name|rightRows
argument_list|)
expr_stmt|;
for|for
control|(
name|Row
name|r
range|:
name|leftRows
control|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End SetOpNode.java
end_comment

end_unit
