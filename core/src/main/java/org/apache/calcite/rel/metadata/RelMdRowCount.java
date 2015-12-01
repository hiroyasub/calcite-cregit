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
name|SemiJoin
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

begin_comment
comment|/**  * RelMdRowCount supplies a default implementation of  * {@link RelMetadataQuery#getRowCount} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdRowCount
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
name|Double
name|getRowCount
parameter_list|(
name|Union
name|rel
parameter_list|)
block|{
name|double
name|rowCount
init|=
literal|0D
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
name|RelMetadataQuery
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
name|RelMetadataQuery
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
name|RelMetadataQuery
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
parameter_list|)
block|{
return|return
name|NumberUtil
operator|.
name|multiply
argument_list|(
name|RelMetadataQuery
operator|.
name|getSelectivity
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
argument_list|)
argument_list|,
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Project
name|rel
parameter_list|)
block|{
return|return
name|RelMetadataQuery
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
parameter_list|)
block|{
name|Double
name|rowCount
init|=
name|RelMetadataQuery
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
name|SemiJoin
name|rel
parameter_list|)
block|{
comment|// create a RexNode representing the selectivity of the
comment|// semijoin filter and pass it to getSelectivity
name|RexNode
name|semiJoinSelectivity
init|=
name|RelMdUtil
operator|.
name|makeSemiJoinSelectivityRexNode
argument_list|(
name|rel
argument_list|)
decl_stmt|;
return|return
name|NumberUtil
operator|.
name|multiply
argument_list|(
name|RelMetadataQuery
operator|.
name|getSelectivity
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|semiJoinSelectivity
argument_list|)
argument_list|,
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|Aggregate
name|rel
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
comment|// rowcount is the cardinality of the group by columns
name|Double
name|distinctRowCount
init|=
name|RelMetadataQuery
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
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
operator|/
literal|10D
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
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Double
name|getRowCount
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|rel
operator|.
name|getRows
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdRowCount.java
end_comment

end_unit

