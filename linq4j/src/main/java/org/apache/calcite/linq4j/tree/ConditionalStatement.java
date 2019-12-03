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
name|linq4j
operator|.
name|tree
package|;
end_package

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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Represents an expression that has a conditional operator.  *  *<p>With an odd number of expressions  * {c0, e0, c1, e1, ..., c<sub>n-1</sub>, e<sub>n-1</sub>, e<sub>n</sub>}  * represents "if (c0) e0 else if (c1) e1 ... else e<sub>n</sub>";  * with an even number of expressions  * {c0, e0, c1, e1, ..., c<sub>n-1</sub>, e<sub>n-1</sub>}  * represents  * "if (c0) e0 else if (c1) e1 ... else if (c<sub>n-1</sub>) e<sub>n-1</sub>".  *</p>  */
end_comment

begin_class
specifier|public
class|class
name|ConditionalStatement
extends|extends
name|Statement
block|{
specifier|public
specifier|final
name|List
argument_list|<
name|Node
argument_list|>
name|expressionList
decl_stmt|;
specifier|public
name|ConditionalStatement
parameter_list|(
name|List
argument_list|<
name|Node
argument_list|>
name|expressionList
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|Conditional
argument_list|,
name|Void
operator|.
name|TYPE
argument_list|)
expr_stmt|;
assert|assert
name|expressionList
operator|!=
literal|null
operator|:
literal|"expressionList should not be null"
assert|;
name|this
operator|.
name|expressionList
operator|=
name|expressionList
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Statement
name|accept
parameter_list|(
name|Shuttle
name|shuttle
parameter_list|)
block|{
name|shuttle
operator|=
name|shuttle
operator|.
name|preVisit
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Node
argument_list|>
name|list
init|=
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|expressionList
argument_list|,
name|shuttle
argument_list|)
decl_stmt|;
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|,
name|list
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|Visitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
name|void
name|accept0
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|)
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
name|expressionList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|+=
literal|2
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|backUp
argument_list|()
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|" else "
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|"if ("
argument_list|)
operator|.
name|append
argument_list|(
name|expressionList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|") "
argument_list|)
operator|.
name|append
argument_list|(
name|Blocks
operator|.
name|toBlock
argument_list|(
name|expressionList
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expressionList
operator|.
name|size
argument_list|()
operator|%
literal|2
operator|==
literal|1
condition|)
block|{
name|writer
operator|.
name|backUp
argument_list|()
expr_stmt|;
name|writer
operator|.
name|append
argument_list|(
literal|" else "
argument_list|)
operator|.
name|append
argument_list|(
name|Blocks
operator|.
name|toBlock
argument_list|(
name|last
argument_list|(
name|expressionList
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|E
name|last
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|collection
parameter_list|)
block|{
return|return
name|collection
operator|.
name|get
argument_list|(
name|collection
operator|.
name|size
argument_list|()
operator|-
literal|1
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
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ConditionalStatement
name|that
init|=
operator|(
name|ConditionalStatement
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|expressionList
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expressionList
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
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
name|nodeType
argument_list|,
name|type
argument_list|,
name|expressionList
argument_list|)
return|;
block|}
block|}
end_class

end_unit

