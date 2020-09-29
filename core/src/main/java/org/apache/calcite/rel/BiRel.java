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
name|rel
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
name|RelOptCluster
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
name|plan
operator|.
name|RelTraitSet
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
name|FlatLists
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

begin_comment
comment|/**  * Abstract base class for relational expressions with a two inputs.  *  *<p>It is not required that two-input relational expressions use this  * class as a base class. However, default implementations of methods make life  * easier.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BiRel
extends|extends
name|AbstractRelNode
block|{
specifier|protected
name|RelNode
name|left
decl_stmt|;
specifier|protected
name|RelNode
name|right
decl_stmt|;
specifier|public
name|BiRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|)
expr_stmt|;
name|this
operator|.
name|left
operator|=
name|left
expr_stmt|;
name|this
operator|.
name|right
operator|=
name|right
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|childrenAccept
parameter_list|(
name|RelVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|left
argument_list|,
literal|0
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|visit
argument_list|(
name|right
argument_list|,
literal|1
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
block|{
return|return
name|FlatLists
operator|.
name|of
argument_list|(
name|left
argument_list|,
name|right
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|getLeft
parameter_list|()
block|{
return|return
name|left
return|;
block|}
specifier|public
name|RelNode
name|getRight
parameter_list|()
block|{
return|return
name|right
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|replaceInput
parameter_list|(
name|int
name|ordinalInParent
parameter_list|,
name|RelNode
name|p
parameter_list|)
block|{
switch|switch
condition|(
name|ordinalInParent
condition|)
block|{
case|case
literal|0
case|:
name|this
operator|.
name|left
operator|=
name|p
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|this
operator|.
name|right
operator|=
name|p
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IndexOutOfBoundsException
argument_list|(
literal|"Input "
operator|+
name|ordinalInParent
argument_list|)
throw|;
block|}
name|recomputeDigest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|input
argument_list|(
literal|"left"
argument_list|,
name|left
argument_list|)
operator|.
name|input
argument_list|(
literal|"right"
argument_list|,
name|right
argument_list|)
return|;
block|}
block|}
end_class

end_unit

