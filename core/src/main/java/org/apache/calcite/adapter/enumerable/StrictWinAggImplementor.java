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
name|adapter
operator|.
name|enumerable
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
name|tree
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
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
comment|/**  * The base implementation of strict window aggregate function.  * @see org.apache.calcite.adapter.enumerable.RexImpTable.FirstLastValueImplementor  * @see org.apache.calcite.adapter.enumerable.RexImpTable.RankImplementor  * @see org.apache.calcite.adapter.enumerable.RexImpTable.RowNumberImplementor  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|StrictWinAggImplementor
extends|extends
name|StrictAggImplementor
implements|implements
name|WinAggImplementor
block|{
specifier|protected
specifier|abstract
name|void
name|implementNotNullAdd
parameter_list|(
name|WinAggContext
name|info
parameter_list|,
name|WinAggAddContext
name|add
parameter_list|)
function_decl|;
specifier|protected
name|boolean
name|nonDefaultOnEmptySet
parameter_list|(
name|WinAggContext
name|info
parameter_list|)
block|{
return|return
name|super
operator|.
name|nonDefaultOnEmptySet
argument_list|(
name|info
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Type
argument_list|>
name|getNotNullState
parameter_list|(
name|WinAggContext
name|info
parameter_list|)
block|{
return|return
name|super
operator|.
name|getNotNullState
argument_list|(
name|info
argument_list|)
return|;
block|}
specifier|protected
name|void
name|implementNotNullReset
parameter_list|(
name|WinAggContext
name|info
parameter_list|,
name|WinAggResetContext
name|reset
parameter_list|)
block|{
name|super
operator|.
name|implementNotNullReset
argument_list|(
name|info
argument_list|,
name|reset
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Expression
name|implementNotNullResult
parameter_list|(
name|WinAggContext
name|info
parameter_list|,
name|WinAggResultContext
name|result
parameter_list|)
block|{
return|return
name|super
operator|.
name|implementNotNullResult
argument_list|(
name|info
argument_list|,
name|result
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
specifier|final
name|void
name|implementNotNullAdd
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggAddContext
name|add
parameter_list|)
block|{
name|implementNotNullAdd
argument_list|(
operator|(
name|WinAggContext
operator|)
name|info
argument_list|,
operator|(
name|WinAggAddContext
operator|)
name|add
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|nonDefaultOnEmptySet
parameter_list|(
name|AggContext
name|info
parameter_list|)
block|{
return|return
name|nonDefaultOnEmptySet
argument_list|(
operator|(
name|WinAggContext
operator|)
name|info
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|List
argument_list|<
name|Type
argument_list|>
name|getNotNullState
parameter_list|(
name|AggContext
name|info
parameter_list|)
block|{
return|return
name|getNotNullState
argument_list|(
operator|(
name|WinAggContext
operator|)
name|info
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
specifier|final
name|void
name|implementNotNullReset
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggResetContext
name|reset
parameter_list|)
block|{
name|implementNotNullReset
argument_list|(
operator|(
name|WinAggContext
operator|)
name|info
argument_list|,
operator|(
name|WinAggResetContext
operator|)
name|reset
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
specifier|final
name|Expression
name|implementNotNullResult
parameter_list|(
name|AggContext
name|info
parameter_list|,
name|AggResultContext
name|result
parameter_list|)
block|{
return|return
name|implementNotNullResult
argument_list|(
operator|(
name|WinAggContext
operator|)
name|info
argument_list|,
operator|(
name|WinAggResultContext
operator|)
name|result
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|needCacheWhenFrameIntact
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

