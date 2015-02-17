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
operator|.
name|core
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
name|rex
operator|.
name|RexNode
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
name|ImmutableIntList
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
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Base class for any join whose condition is based on column equality.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|EquiJoin
extends|extends
name|Join
block|{
specifier|public
specifier|final
name|ImmutableIntList
name|leftKeys
decl_stmt|;
specifier|public
specifier|final
name|ImmutableIntList
name|rightKeys
decl_stmt|;
comment|/** Creates an EquiJoin. */
specifier|public
name|EquiJoin
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|ImmutableIntList
name|leftKeys
parameter_list|,
name|ImmutableIntList
name|rightKeys
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|joinType
argument_list|,
name|variablesStopped
argument_list|)
expr_stmt|;
name|this
operator|.
name|leftKeys
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|leftKeys
argument_list|)
expr_stmt|;
name|this
operator|.
name|rightKeys
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|rightKeys
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ImmutableIntList
name|getLeftKeys
parameter_list|()
block|{
return|return
name|leftKeys
return|;
block|}
specifier|public
name|ImmutableIntList
name|getRightKeys
parameter_list|()
block|{
return|return
name|rightKeys
return|;
block|}
annotation|@
name|Override
specifier|public
name|JoinInfo
name|analyzeCondition
parameter_list|()
block|{
return|return
name|JoinInfo
operator|.
name|of
argument_list|(
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EquiJoin.java
end_comment

end_unit

