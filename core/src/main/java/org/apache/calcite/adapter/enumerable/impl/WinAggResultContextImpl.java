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
operator|.
name|impl
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
name|adapter
operator|.
name|enumerable
operator|.
name|RexToLixTranslator
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
name|adapter
operator|.
name|enumerable
operator|.
name|WinAggFrameResultContext
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
name|adapter
operator|.
name|enumerable
operator|.
name|WinAggImplementor
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
name|adapter
operator|.
name|enumerable
operator|.
name|WinAggResultContext
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
name|linq4j
operator|.
name|tree
operator|.
name|BlockBuilder
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
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Implementation of  * {@link org.apache.calcite.adapter.enumerable.WinAggResultContext}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|WinAggResultContextImpl
extends|extends
name|AggResultContextImpl
implements|implements
name|WinAggResultContext
block|{
specifier|private
specifier|final
name|Function
argument_list|<
name|BlockBuilder
argument_list|,
name|WinAggFrameResultContext
argument_list|>
name|frame
decl_stmt|;
comment|/**    * Creates window aggregate result context.    *    * @param block code block that will contain the added initialization    * @param accumulator accumulator variables that store the intermediate    *                    aggregate state    */
specifier|protected
name|WinAggResultContextImpl
parameter_list|(
name|BlockBuilder
name|block
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|accumulator
parameter_list|,
name|Function
argument_list|<
name|BlockBuilder
argument_list|,
name|WinAggFrameResultContext
argument_list|>
name|frameContextBuilder
parameter_list|)
block|{
name|super
argument_list|(
name|block
argument_list|,
literal|null
argument_list|,
name|accumulator
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|frame
operator|=
name|frameContextBuilder
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"Guava"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|protected
name|WinAggResultContextImpl
parameter_list|(
name|BlockBuilder
name|block
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|accumulator
parameter_list|,
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
argument_list|<
name|BlockBuilder
argument_list|,
name|WinAggFrameResultContext
argument_list|>
name|frameContextBuilder
parameter_list|)
block|{
name|this
argument_list|(
name|block
argument_list|,
name|accumulator
argument_list|,
operator|(
name|Function
argument_list|<
name|BlockBuilder
argument_list|,
name|WinAggFrameResultContext
argument_list|>
operator|)
name|frameContextBuilder
operator|::
name|apply
argument_list|)
expr_stmt|;
block|}
specifier|private
name|WinAggFrameResultContext
name|getFrame
parameter_list|()
block|{
return|return
name|frame
operator|.
name|apply
argument_list|(
name|currentBlock
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
block|{
return|return
name|rowTranslator
argument_list|(
name|rowIndex
argument_list|)
operator|.
name|translateList
argument_list|(
name|rexArguments
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|computeIndex
parameter_list|(
name|Expression
name|offset
parameter_list|,
name|WinAggImplementor
operator|.
name|SeekType
name|seekType
parameter_list|)
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|computeIndex
argument_list|(
name|offset
argument_list|,
name|seekType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|rowInFrame
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|rowInFrame
argument_list|(
name|rowIndex
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|rowInPartition
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|rowInPartition
argument_list|(
name|rowIndex
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexToLixTranslator
name|rowTranslator
parameter_list|(
name|Expression
name|rowIndex
parameter_list|)
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|rowTranslator
argument_list|(
name|rowIndex
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|compareRows
parameter_list|(
name|Expression
name|a
parameter_list|,
name|Expression
name|b
parameter_list|)
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|compareRows
argument_list|(
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|index
parameter_list|()
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|index
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|startIndex
parameter_list|()
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|startIndex
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|endIndex
parameter_list|()
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|endIndex
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|hasRows
parameter_list|()
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|hasRows
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getFrameRowCount
parameter_list|()
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|getFrameRowCount
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|getPartitionRowCount
parameter_list|()
block|{
return|return
name|getFrame
argument_list|()
operator|.
name|getPartitionRowCount
argument_list|()
return|;
block|}
block|}
end_class

end_unit

