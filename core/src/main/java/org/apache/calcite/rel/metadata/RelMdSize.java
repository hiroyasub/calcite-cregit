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
name|avatica
operator|.
name|util
operator|.
name|ByteString
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
name|AggregateCall
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
name|TableModify
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeField
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
name|RexCall
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
name|RexInputRef
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
name|ImmutableNullableList
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
name|NlsString
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
name|Pair
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

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * Default implementations of the  * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Size}  * metadata provider for the standard logical algebra.  *  * @see RelMetadataQuery#getAverageRowSize  * @see RelMetadataQuery#getAverageColumnSizes  * @see RelMetadataQuery#getAverageColumnSizesNotNull  */
end_comment

begin_class
specifier|public
class|class
name|RelMdSize
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|Size
argument_list|>
block|{
comment|/** Source for    * {@link org.apache.calcite.rel.metadata.BuiltInMetadata.Size}. */
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
operator|new
name|RelMdSize
argument_list|()
argument_list|,
name|BuiltInMetadata
operator|.
name|Size
operator|.
name|Handler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** Bytes per character (2). */
specifier|public
specifier|static
specifier|final
name|int
name|BYTES_PER_CHARACTER
init|=
name|Character
operator|.
name|SIZE
operator|/
name|Byte
operator|.
name|SIZE
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RelMdSize
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|Size
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|Size
operator|.
name|DEF
return|;
block|}
comment|/** Catch-all implementation for    * {@link BuiltInMetadata.Size#averageRowSize()},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#getAverageRowSize    */
specifier|public
annotation|@
name|Nullable
name|Double
name|averageRowSize
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
init|=
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
name|averageColumnSizes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|double
name|d
init|=
literal|0d
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|,
name|RelDataTypeField
argument_list|>
name|p
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|averageColumnSizes
argument_list|,
name|fields
argument_list|)
control|)
block|{
if|if
condition|(
name|p
operator|.
name|left
operator|==
literal|null
condition|)
block|{
name|Double
name|fieldValueSize
init|=
name|averageFieldValueSize
argument_list|(
name|p
operator|.
name|right
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldValueSize
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|d
operator|+=
name|fieldValueSize
expr_stmt|;
block|}
else|else
block|{
name|d
operator|+=
name|p
operator|.
name|left
expr_stmt|;
block|}
block|}
return|return
name|d
return|;
block|}
comment|/** Catch-all implementation for    * {@link BuiltInMetadata.Size#averageColumnSizes()},    * invoked using reflection.    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#getAverageColumnSizes    */
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
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
comment|// absolutely no idea
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|TableModify
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
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
name|getAverageColumnSizes
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Project
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|inputColumnSizes
init|=
name|mq
operator|.
name|getAverageColumnSizesNotNull
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableNullableList
operator|.
name|Builder
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|sizes
init|=
name|ImmutableNullableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|project
range|:
name|rel
operator|.
name|getProjects
argument_list|()
control|)
block|{
name|sizes
operator|.
name|add
argument_list|(
name|averageRexSize
argument_list|(
name|project
argument_list|,
name|inputColumnSizes
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sizes
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Calc
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|inputColumnSizes
init|=
name|mq
operator|.
name|getAverageColumnSizesNotNull
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableNullableList
operator|.
name|Builder
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|sizes
init|=
name|ImmutableNullableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|rel
operator|.
name|getProgram
argument_list|()
operator|.
name|split
argument_list|()
operator|.
name|left
operator|.
name|forEach
argument_list|(
name|exp
lambda|->
name|sizes
operator|.
name|add
argument_list|(
name|averageRexSize
argument_list|(
name|exp
argument_list|,
name|inputColumnSizes
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sizes
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Values
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableNullableList
operator|.
name|Builder
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|list
init|=
name|ImmutableNullableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|rel
operator|.
name|getTuples
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|averageTypeValueSize
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|double
name|d
init|=
literal|0
decl_stmt|;
for|for
control|(
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|literals
range|:
name|rel
operator|.
name|getTuples
argument_list|()
control|)
block|{
name|d
operator|+=
name|typeValueSize
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|,
name|literals
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getValueAs
argument_list|(
name|Comparable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|d
operator|/=
name|rel
operator|.
name|getTuples
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|TableScan
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|Size
operator|.
name|Handler
name|handler
init|=
name|rel
operator|.
name|getTable
argument_list|()
operator|.
name|unwrap
argument_list|(
name|BuiltInMetadata
operator|.
name|Size
operator|.
name|Handler
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|handler
operator|!=
literal|null
condition|)
block|{
return|return
name|handler
operator|.
name|averageColumnSizes
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableNullableList
operator|.
name|Builder
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|list
init|=
name|ImmutableNullableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|fields
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|averageTypeValueSize
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|inputColumnSizes
init|=
name|mq
operator|.
name|getAverageColumnSizesNotNull
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableNullableList
operator|.
name|Builder
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|list
init|=
name|ImmutableNullableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|key
range|:
name|rel
operator|.
name|getGroupSet
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|inputColumnSizes
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|AggregateCall
name|aggregateCall
range|:
name|rel
operator|.
name|getAggCallList
argument_list|()
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|averageTypeValueSize
argument_list|(
name|aggregateCall
operator|.
name|type
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|averageJoinColumnSizes
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|)
return|;
block|}
specifier|private
specifier|static
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageJoinColumnSizes
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|boolean
name|semiOrAntijoin
init|=
operator|!
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|projectsRight
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|left
init|=
name|rel
operator|.
name|getLeft
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|right
init|=
name|rel
operator|.
name|getRight
argument_list|()
decl_stmt|;
specifier|final
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|lefts
init|=
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|left
argument_list|)
decl_stmt|;
specifier|final
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|rights
init|=
name|semiOrAntijoin
condition|?
literal|null
else|:
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|right
argument_list|)
decl_stmt|;
if|if
condition|(
name|lefts
operator|==
literal|null
operator|&&
name|rights
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
name|fieldCount
init|=
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
annotation|@
name|Nullable
name|Double
index|[]
name|sizes
init|=
operator|new
name|Double
index|[
name|fieldCount
index|]
decl_stmt|;
if|if
condition|(
name|lefts
operator|!=
literal|null
condition|)
block|{
name|lefts
operator|.
name|toArray
argument_list|(
name|sizes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|rights
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|leftCount
init|=
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|rights
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|sizes
index|[
name|leftCount
operator|+
name|i
index|]
operator|=
name|rights
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|ImmutableNullableList
operator|.
name|copyOf
argument_list|(
name|sizes
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Intersect
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|rel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Minus
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|rel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|averageColumnSizes
parameter_list|(
name|Union
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|int
name|fieldCount
init|=
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
argument_list|>
name|inputColumnSizeList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
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
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|inputSizes
init|=
name|mq
operator|.
name|getAverageColumnSizes
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputSizes
operator|!=
literal|null
condition|)
block|{
name|inputColumnSizeList
operator|.
name|add
argument_list|(
name|inputSizes
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|inputColumnSizeList
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|null
return|;
comment|// all were null
case|case
literal|1
case|:
return|return
name|inputColumnSizeList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
comment|// all but one were null
default|default:
break|break;
block|}
specifier|final
name|ImmutableNullableList
operator|.
name|Builder
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|sizes
init|=
name|ImmutableNullableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|int
name|nn
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fieldCount
condition|;
name|i
operator|++
control|)
block|{
name|double
name|d
init|=
literal|0d
decl_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|List
argument_list|<
annotation|@
name|Nullable
name|Double
argument_list|>
name|inputColumnSizes
range|:
name|inputColumnSizeList
control|)
block|{
name|Double
name|d2
init|=
name|inputColumnSizes
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|d2
operator|!=
literal|null
condition|)
block|{
name|d
operator|+=
name|d2
expr_stmt|;
operator|++
name|n
expr_stmt|;
operator|++
name|nn
expr_stmt|;
block|}
block|}
name|sizes
operator|.
name|add
argument_list|(
name|n
operator|>
literal|0
condition|?
name|d
operator|/
name|n
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nn
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
comment|// all columns are null
block|}
return|return
name|sizes
operator|.
name|build
argument_list|()
return|;
block|}
comment|/** Estimates the average size (in bytes) of a value of a field, knowing    * nothing more than its type.    *    *<p>We assume that the proportion of nulls is negligible, even if the field    * is nullable.    */
specifier|protected
annotation|@
name|Nullable
name|Double
name|averageFieldValueSize
parameter_list|(
name|RelDataTypeField
name|field
parameter_list|)
block|{
return|return
name|averageTypeValueSize
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
comment|/** Estimates the average size (in bytes) of a value of a type.    *    *<p>We assume that the proportion of nulls is negligible, even if the type    * is nullable.    */
specifier|public
annotation|@
name|Nullable
name|Double
name|averageTypeValueSize
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|BOOLEAN
case|:
case|case
name|TINYINT
case|:
return|return
literal|1d
return|;
case|case
name|SMALLINT
case|:
return|return
literal|2d
return|;
case|case
name|INTEGER
case|:
case|case
name|REAL
case|:
case|case
name|DECIMAL
case|:
case|case
name|DATE
case|:
case|case
name|TIME
case|:
case|case
name|TIME_WITH_LOCAL_TIME_ZONE
case|:
case|case
name|INTERVAL_YEAR
case|:
case|case
name|INTERVAL_YEAR_MONTH
case|:
case|case
name|INTERVAL_MONTH
case|:
return|return
literal|4d
return|;
case|case
name|BIGINT
case|:
case|case
name|DOUBLE
case|:
case|case
name|FLOAT
case|:
comment|// sic
case|case
name|TIMESTAMP
case|:
case|case
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
case|:
case|case
name|INTERVAL_DAY
case|:
case|case
name|INTERVAL_DAY_HOUR
case|:
case|case
name|INTERVAL_DAY_MINUTE
case|:
case|case
name|INTERVAL_DAY_SECOND
case|:
case|case
name|INTERVAL_HOUR
case|:
case|case
name|INTERVAL_HOUR_MINUTE
case|:
case|case
name|INTERVAL_HOUR_SECOND
case|:
case|case
name|INTERVAL_MINUTE
case|:
case|case
name|INTERVAL_MINUTE_SECOND
case|:
case|case
name|INTERVAL_SECOND
case|:
return|return
literal|8d
return|;
case|case
name|BINARY
case|:
return|return
operator|(
name|double
operator|)
name|type
operator|.
name|getPrecision
argument_list|()
return|;
case|case
name|VARBINARY
case|:
return|return
name|Math
operator|.
name|min
argument_list|(
operator|(
name|double
operator|)
name|type
operator|.
name|getPrecision
argument_list|()
argument_list|,
literal|100d
argument_list|)
return|;
case|case
name|CHAR
case|:
return|return
operator|(
name|double
operator|)
name|type
operator|.
name|getPrecision
argument_list|()
operator|*
name|BYTES_PER_CHARACTER
return|;
case|case
name|VARCHAR
case|:
comment|// Even in large (say VARCHAR(2000)) columns most strings are small
return|return
name|Math
operator|.
name|min
argument_list|(
operator|(
name|double
operator|)
name|type
operator|.
name|getPrecision
argument_list|()
operator|*
name|BYTES_PER_CHARACTER
argument_list|,
literal|100d
argument_list|)
return|;
case|case
name|ROW
case|:
name|double
name|average
init|=
literal|0.0
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|type
operator|.
name|getFieldList
argument_list|()
control|)
block|{
name|Double
name|size
init|=
name|averageTypeValueSize
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|size
operator|!=
literal|null
condition|)
block|{
name|average
operator|+=
name|size
expr_stmt|;
block|}
block|}
return|return
name|average
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
comment|/** Estimates the average size (in bytes) of a value of a type.    *    *<p>Nulls count as 1 byte.    */
specifier|public
name|double
name|typeValueSize
parameter_list|(
name|RelDataType
name|type
parameter_list|,
annotation|@
name|Nullable
name|Comparable
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
literal|1d
return|;
block|}
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|BOOLEAN
case|:
case|case
name|TINYINT
case|:
return|return
literal|1d
return|;
case|case
name|SMALLINT
case|:
return|return
literal|2d
return|;
case|case
name|INTEGER
case|:
case|case
name|FLOAT
case|:
case|case
name|REAL
case|:
case|case
name|DATE
case|:
case|case
name|TIME
case|:
case|case
name|TIME_WITH_LOCAL_TIME_ZONE
case|:
case|case
name|INTERVAL_YEAR
case|:
case|case
name|INTERVAL_YEAR_MONTH
case|:
case|case
name|INTERVAL_MONTH
case|:
return|return
literal|4d
return|;
case|case
name|BIGINT
case|:
case|case
name|DOUBLE
case|:
case|case
name|TIMESTAMP
case|:
case|case
name|TIMESTAMP_WITH_LOCAL_TIME_ZONE
case|:
case|case
name|INTERVAL_DAY
case|:
case|case
name|INTERVAL_DAY_HOUR
case|:
case|case
name|INTERVAL_DAY_MINUTE
case|:
case|case
name|INTERVAL_DAY_SECOND
case|:
case|case
name|INTERVAL_HOUR
case|:
case|case
name|INTERVAL_HOUR_MINUTE
case|:
case|case
name|INTERVAL_HOUR_SECOND
case|:
case|case
name|INTERVAL_MINUTE
case|:
case|case
name|INTERVAL_MINUTE_SECOND
case|:
case|case
name|INTERVAL_SECOND
case|:
return|return
literal|8d
return|;
case|case
name|BINARY
case|:
case|case
name|VARBINARY
case|:
return|return
operator|(
operator|(
name|ByteString
operator|)
name|value
operator|)
operator|.
name|length
argument_list|()
return|;
case|case
name|CHAR
case|:
case|case
name|VARCHAR
case|:
return|return
operator|(
operator|(
name|NlsString
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
operator|.
name|length
argument_list|()
operator|*
name|BYTES_PER_CHARACTER
return|;
default|default:
return|return
literal|32
return|;
block|}
block|}
specifier|public
annotation|@
name|Nullable
name|Double
name|averageRexSize
argument_list|(
name|RexNode
name|node
argument_list|,
name|List
operator|<
condition|?
then|extends @
name|Nullable
name|Double
operator|>
name|inputColumnSizes
argument_list|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|INPUT_REF
case|:
return|return
name|inputColumnSizes
operator|.
name|get
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|node
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
case|case
name|LITERAL
case|:
return|return
name|typeValueSize
argument_list|(
name|node
operator|.
name|getType
argument_list|()
argument_list|,
operator|(
operator|(
name|RexLiteral
operator|)
name|node
operator|)
operator|.
name|getValueAs
argument_list|(
name|Comparable
operator|.
name|class
argument_list|)
argument_list|)
return|;
default|default:
if|if
condition|(
name|node
operator|instanceof
name|RexCall
condition|)
block|{
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|node
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|call
operator|.
name|getOperands
argument_list|()
control|)
block|{
comment|// It's a reasonable assumption that a function's result will have
comment|// similar size to its argument of a similar type. For example,
comment|// UPPER(c) has the same average size as c.
if|if
condition|(
name|operand
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|node
operator|.
name|getType
argument_list|()
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
return|return
name|averageRexSize
argument_list|(
name|operand
argument_list|,
name|inputColumnSizes
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|averageTypeValueSize
argument_list|(
name|node
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

