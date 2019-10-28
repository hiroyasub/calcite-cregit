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
name|Exchange
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
name|Util
import|;
end_import

begin_comment
comment|/**  * RelMdMinRowCount supplies a default implementation of  * {@link RelMetadataQuery#getMinRowCount} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdMinRowCount
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|MinRowCount
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
name|MIN_ROW_COUNT
operator|.
name|method
argument_list|,
operator|new
name|RelMdMinRowCount
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|MinRowCount
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|MinRowCount
operator|.
name|DEF
return|;
block|}
specifier|public
name|Double
name|getMinRowCount
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
name|getMinRowCount
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|partialRowCount
operator|!=
literal|null
condition|)
block|{
name|rowCount
operator|+=
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
name|getMinRowCount
parameter_list|(
name|Intersect
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|0d
return|;
comment|// no lower bound
block|}
specifier|public
name|Double
name|getMinRowCount
parameter_list|(
name|Minus
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|0d
return|;
comment|// no lower bound
block|}
specifier|public
name|Double
name|getMinRowCount
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|0d
return|;
comment|// no lower bound
block|}
specifier|public
name|Double
name|getMinRowCount
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
name|getMinRowCount
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
name|getMinRowCount
parameter_list|(
name|Exchange
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getMinRowCount
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
name|getMinRowCount
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
name|getMinRowCount
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
name|rowCount
operator|=
literal|0D
expr_stmt|;
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
name|getMinRowCount
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
name|getMinRowCount
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
name|rowCount
operator|=
literal|0D
expr_stmt|;
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
name|getMinRowCount
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
if|if
condition|(
name|rel
operator|.
name|getGroupSet
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Aggregate with no GROUP BY always returns 1 row (even on empty table).
return|return
literal|1D
return|;
block|}
specifier|final
name|Double
name|rowCount
init|=
name|mq
operator|.
name|getMinRowCount
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
operator|!=
literal|null
operator|&&
name|rowCount
operator|>=
literal|1D
condition|)
block|{
return|return
operator|(
name|double
operator|)
name|rel
operator|.
name|getGroupSets
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
return|return
literal|0D
return|;
block|}
specifier|public
name|Double
name|getMinRowCount
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|0D
return|;
block|}
specifier|public
name|Double
name|getMinRowCount
parameter_list|(
name|TableScan
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|0D
return|;
block|}
specifier|public
name|Double
name|getMinRowCount
parameter_list|(
name|Values
name|values
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
comment|// For Values, the minimum row count is the actual row count.
return|return
operator|(
name|double
operator|)
name|values
operator|.
name|getTuples
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|Double
name|getMinRowCount
parameter_list|(
name|RelSubset
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
comment|// FIXME This is a short-term fix for [CALCITE-1018]. A complete
comment|// solution will come with [CALCITE-1048].
name|Util
operator|.
name|discard
argument_list|(
name|Bug
operator|.
name|CALCITE_1048_FIXED
argument_list|)
expr_stmt|;
for|for
control|(
name|RelNode
name|node
range|:
name|rel
operator|.
name|getRels
argument_list|()
control|)
block|{
if|if
condition|(
name|node
operator|instanceof
name|Sort
condition|)
block|{
name|Sort
name|sort
init|=
operator|(
name|Sort
operator|)
name|node
decl_stmt|;
if|if
condition|(
name|sort
operator|.
name|fetch
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|double
operator|)
name|RexLiteral
operator|.
name|intValue
argument_list|(
name|sort
operator|.
name|fetch
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|0D
return|;
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Double
name|getMinRowCount
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdMinRowCount.java
end_comment

end_unit

