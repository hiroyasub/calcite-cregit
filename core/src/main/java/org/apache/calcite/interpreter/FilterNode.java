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
name|Filter
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

begin_comment
comment|/**  * Interpreter node that implements a  * {@link org.apache.calcite.rel.core.Filter}.  */
end_comment

begin_class
specifier|public
class|class
name|FilterNode
extends|extends
name|AbstractSingleNode
argument_list|<
name|Filter
argument_list|>
block|{
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
name|FilterNode
parameter_list|(
name|Interpreter
name|interpreter
parameter_list|,
name|Filter
name|rel
parameter_list|)
block|{
name|super
argument_list|(
name|interpreter
argument_list|,
name|rel
argument_list|)
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|interpreter
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
name|rel
operator|.
name|getRowType
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|interpreter
operator|.
name|createContext
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|Row
name|row
decl_stmt|;
while|while
condition|(
operator|(
name|row
operator|=
name|source
operator|.
name|receive
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|values
operator|=
name|row
operator|.
name|getValues
argument_list|()
expr_stmt|;
name|Boolean
name|b
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
name|b
operator|!=
literal|null
operator|&&
name|b
condition|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End FilterNode.java
end_comment

end_unit

