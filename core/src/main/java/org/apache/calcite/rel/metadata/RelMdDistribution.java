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
name|plan
operator|.
name|RelOptTable
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
name|hep
operator|.
name|HepRelVertex
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
name|BiRel
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
name|RelDistribution
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
name|RelDistributions
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
name|SetOp
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
name|Snapshot
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
name|rex
operator|.
name|RexProgram
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
name|mapping
operator|.
name|Mappings
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * RelMdCollation supplies a default implementation of  * {@link RelMetadataQuery#distribution}  * for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdDistribution
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|Distribution
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
name|DISTRIBUTION
operator|.
name|method
argument_list|,
operator|new
name|RelMdDistribution
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdDistribution
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
name|Distribution
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|Distribution
operator|.
name|DEF
return|;
block|}
comment|/** Fallback method to deduce distribution for any relational expression not    * handled by a more specific method.    *    * @param rel Relational expression    * @return Relational expression's distribution    */
specifier|public
name|RelDistribution
name|distribution
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|RelDistributions
operator|.
name|SINGLETON
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
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
name|distribution
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
parameter_list|(
name|BiRel
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|distribution
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
parameter_list|(
name|SetOp
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|distribution
argument_list|(
name|rel
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
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
name|distribution
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
parameter_list|(
name|TableScan
name|scan
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|table
argument_list|(
name|scan
operator|.
name|getTable
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
parameter_list|(
name|Project
name|project
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|project
argument_list|(
name|mq
argument_list|,
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
parameter_list|(
name|Values
name|values
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|values
argument_list|(
name|values
operator|.
name|getRowType
argument_list|()
argument_list|,
name|values
operator|.
name|getTuples
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
parameter_list|(
name|Exchange
name|exchange
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|exchange
argument_list|(
name|exchange
operator|.
name|distribution
argument_list|)
return|;
block|}
specifier|public
name|RelDistribution
name|distribution
parameter_list|(
name|HepRelVertex
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
name|mq
operator|.
name|distribution
argument_list|(
name|rel
operator|.
name|getCurrentRel
argument_list|()
argument_list|)
return|;
block|}
comment|// Helper methods
comment|/** Helper method to determine a    * {@link TableScan}'s distribution. */
specifier|public
specifier|static
name|RelDistribution
name|table
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
return|return
name|table
operator|.
name|getDistribution
argument_list|()
return|;
block|}
comment|/** Helper method to determine a    * {@link Snapshot}'s distribution. */
specifier|public
specifier|static
name|RelDistribution
name|snapshot
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
return|return
name|mq
operator|.
name|distribution
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * {@link Sort}'s distribution. */
specifier|public
specifier|static
name|RelDistribution
name|sort
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
return|return
name|mq
operator|.
name|distribution
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * {@link Filter}'s distribution. */
specifier|public
specifier|static
name|RelDistribution
name|filter
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
return|return
name|mq
operator|.
name|distribution
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * limit's distribution. */
specifier|public
specifier|static
name|RelDistribution
name|limit
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
return|return
name|mq
operator|.
name|distribution
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * {@link org.apache.calcite.rel.core.Calc}'s distribution. */
specifier|public
specifier|static
name|RelDistribution
name|calc
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
assert|assert
name|program
operator|.
name|getCondition
argument_list|()
operator|!=
literal|null
operator|||
operator|!
name|program
operator|.
name|getProjectList
argument_list|()
operator|.
name|isEmpty
argument_list|()
assert|;
specifier|final
name|RelDistribution
name|inputDistribution
init|=
name|mq
operator|.
name|distribution
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|program
operator|.
name|getProjectList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|program
operator|.
name|getPartialMapping
argument_list|(
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|inputDistribution
operator|.
name|apply
argument_list|(
name|mapping
argument_list|)
return|;
block|}
return|return
name|inputDistribution
return|;
block|}
comment|/** Helper method to determine a {@link Project}'s distribution. */
specifier|public
specifier|static
name|RelDistribution
name|project
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|projects
parameter_list|)
block|{
specifier|final
name|RelDistribution
name|inputDistribution
init|=
name|mq
operator|.
name|distribution
argument_list|(
name|input
argument_list|)
decl_stmt|;
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Project
operator|.
name|getPartialMapping
argument_list|(
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|projects
argument_list|)
decl_stmt|;
return|return
name|inputDistribution
operator|.
name|apply
argument_list|(
name|mapping
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * {@link Values}'s distribution. */
specifier|public
specifier|static
name|RelDistribution
name|values
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|ImmutableList
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
parameter_list|)
block|{
return|return
name|RelDistributions
operator|.
name|BROADCAST_DISTRIBUTED
return|;
block|}
comment|/** Helper method to determine an    * {@link Exchange}'s    * or {@link org.apache.calcite.rel.core.SortExchange}'s distribution. */
specifier|public
specifier|static
name|RelDistribution
name|exchange
parameter_list|(
name|RelDistribution
name|distribution
parameter_list|)
block|{
return|return
name|distribution
return|;
block|}
block|}
end_class

end_unit

