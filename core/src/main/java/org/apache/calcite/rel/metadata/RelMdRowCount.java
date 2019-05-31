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
name|metadata
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
name|EnumerableLimit
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
name|volcano
operator|.
name|RelSubset
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
name|rel
operator|.
name|SingleRel
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
name|Aggregate
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
name|Calc
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
name|Filter
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
name|Intersect
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
name|Minus
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
name|Project
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
name|Sort
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
name|TableScan
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
name|Union
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
name|Values
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
name|RexDynamicParam
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
name|RexLiteral
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
name|Bug
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
name|BuiltInMethod
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
name|ImmutableBitSet
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
name|NumberUtil
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

begin_comment
comment|/**  * RelMdRowCount supplies a default implementation of  * {@link RelMetadataQuery#getRowCount} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdRowCount
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|RowCount
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|RelMetadataProvider
name|SOURCE
init|=
name|ReflectiveRelMetadataProvider
operator|.
name|reflectiveSource
argument_list|(
name|BuiltInMethod
operator|.
name|ROW_COUNT
operator|.
name|method
argument_list|,
operator|new
name|RelMdRowCount
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|RowCount
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|RowCount
operator|.
name|DEF
return|;
block|}
comment|/** Catch-all implementation for    * {@link BuiltInMetadata.RowCount#getRowCount()},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#getRowCount(RelNode)    */
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|rel
operator|.
name|estimateRowCount
argument_list|(
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|RelSubset
name|subset
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
if|if
condition|(
operator|!
name|Bug
operator|.
name|CALCITE_1048_FIXED
condition|)
block|{
return|return
name|mq
operator|.
name|getRowCount
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|subset
operator|.
name|getBest
argument_list|()
argument_list|,
name|subset
operator|.
name|getOriginal
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
name|Double
name|v
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RelNode
name|r
range|:
name|subset
operator|.
name|getRels
argument_list|()
control|)
block|{
try|try
block|{
name|v
operator|=
name|NumberUtil
operator|.
name|min
argument_list|(
name|v
argument_list|,
name|mq
operator|.
name|getRowCount
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CyclicMetadataException
name|e
parameter_list|)
block|{
comment|// ignore this rel; there will be other, non-cyclic ones
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|Util
operator|.
name|first
argument_list|(
name|v
argument_list|,
literal|1e6d
argument_list|)
return|;
comment|// if set is empty, estimate large
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Union
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|double
name|rowCount
init|=
literal|0.0
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|rel
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|Double
name|partialRowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|partialRowCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|rowCount
operator|+=
name|partialRowCount
expr_stmt|;
block|}
return|return
name|rowCount
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Intersect
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|Double
name|rowCount
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|rel
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|Double
name|partialRowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowCount
operator|==
literal|null
operator|||
name|partialRowCount
operator|!=
literal|null
operator|&&
name|partialRowCount
operator|<
name|rowCount
condition|)
block|{
name|rowCount
operator|=
name|partialRowCount
expr_stmt|;
block|}
block|}
return|return
name|rowCount
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Minus
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|Double
name|rowCount
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|rel
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|Double
name|partialRowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowCount
operator|==
literal|null
operator|||
name|partialRowCount
operator|!=
literal|null
operator|&&
name|partialRowCount
operator|<
name|rowCount
condition|)
block|{
name|rowCount
operator|=
name|partialRowCount
expr_stmt|;
block|}
block|}
return|return
name|rowCount
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|RelMdUtil
operator|.
name|estimateFilteredRows
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|rel
operator|.
name|getCondition
argument_list|()
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Calc
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|RelMdUtil
operator|.
name|estimateFilteredRows
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|rel
operator|.
name|getProgram
argument_list|()
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Project
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|Double
name|rowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|rel
operator|.
name|offset
operator|instanceof
name|RexDynamicParam
condition|)
block|{
return|return
name|rowCount
return|;
block|}
specifier|final
name|int
name|offset
init|=
name|rel
operator|.
name|offset
operator|==
literal|null
condition|?
literal|0
else|:
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|rel
operator|.
name|offset
argument_list|)
decl_stmt|;
name|rowCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|rowCount
operator|-
name|offset
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
if|if
condition|(
name|rel
operator|.
name|fetch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rel
operator|.
name|fetch
operator|instanceof
name|RexDynamicParam
condition|)
block|{
return|return
name|rowCount
return|;
block|}
specifier|final
name|int
name|limit
init|=
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|rel
operator|.
name|fetch
argument_list|)
decl_stmt|;
if|if
condition|(
name|limit
operator|<
name|rowCount
condition|)
block|{
return|return
operator|(
name|double
operator|)
name|limit
return|;
block|}
block|}
return|return
name|rowCount
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|EnumerableLimit
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|Double
name|rowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|rowCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|rel
operator|.
name|offset
operator|instanceof
name|RexDynamicParam
condition|)
block|{
return|return
name|rowCount
return|;
block|}
specifier|final
name|int
name|offset
init|=
name|rel
operator|.
name|offset
operator|==
literal|null
condition|?
literal|0
else|:
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|rel
operator|.
name|offset
argument_list|)
decl_stmt|;
name|rowCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|rowCount
operator|-
name|offset
argument_list|,
literal|0D
argument_list|)
expr_stmt|;
if|if
condition|(
name|rel
operator|.
name|fetch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|rel
operator|.
name|fetch
operator|instanceof
name|RexDynamicParam
condition|)
block|{
return|return
name|rowCount
return|;
block|}
specifier|final
name|int
name|limit
init|=
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|rel
operator|.
name|fetch
argument_list|)
decl_stmt|;
if|if
condition|(
name|limit
operator|<
name|rowCount
condition|)
block|{
return|return
operator|(
name|double
operator|)
name|limit
return|;
block|}
block|}
return|return
name|rowCount
return|;
block|}
comment|// Covers Converter, Interpreter
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|SingleRel
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|RelMdUtil
operator|.
name|getJoinRowCount
argument_list|(
name|mq
argument_list|,
name|rel
argument_list|,
name|rel
operator|.
name|getCondition
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|ImmutableBitSet
name|groupKey
init|=
name|rel
operator|.
name|getGroupSet
argument_list|()
decl_stmt|;
comment|// .range(rel.getGroupCount());
comment|// rowCount is the cardinality of the group by columns
name|Double
name|distinctRowCount
init|=
name|mq
operator|.
name|getDistinctRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|groupKey
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|distinctRowCount
operator|==
literal|null
condition|)
block|{
name|distinctRowCount
operator|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
operator|/
literal|10
expr_stmt|;
block|}
comment|// Grouping sets multiply
name|distinctRowCount
operator|*=
name|rel
operator|.
name|getGroupSets
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
return|return
name|distinctRowCount
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|TableScan
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|rel
operator|.
name|estimateRowCount
argument_list|(
name|mq
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Values
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|rel
operator|.
name|estimateRowCount
argument_list|(
name|mq
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdRowCount.java
end_comment

end_unit

