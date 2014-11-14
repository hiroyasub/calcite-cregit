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
name|BitSets
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
name|java
operator|.
name|util
operator|.
name|BitSet
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
comment|/**  * RelMdPopulationSize supplies a default implementation of  * {@link RelMetadataQuery#getPopulationSize} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdPopulationSize
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
name|POPULATION_SIZE
operator|.
name|method
argument_list|,
operator|new
name|RelMdPopulationSize
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdPopulationSize
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getPopulationSize
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|groupKey
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getPopulationSize
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|groupKey
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|Union
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
name|Double
name|population
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
name|subPop
init|=
name|RelMetadataQuery
operator|.
name|getPopulationSize
argument_list|(
name|input
argument_list|,
name|groupKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|subPop
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|population
operator|+=
name|subPop
expr_stmt|;
block|}
return|return
name|population
return|;
block|}
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|Join
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
return|return
name|RelMdUtil
operator|.
name|getJoinPopulationSize
argument_list|(
name|rel
argument_list|,
name|groupKey
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|SemiJoin
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getPopulationSize
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|groupKey
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
name|BitSet
name|childKey
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|RelMdUtil
operator|.
name|setAggChildKeys
argument_list|(
name|groupKey
argument_list|,
name|rel
argument_list|,
name|childKey
argument_list|)
expr_stmt|;
return|return
name|RelMetadataQuery
operator|.
name|getPopulationSize
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|childKey
argument_list|)
return|;
block|}
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|Values
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
comment|// assume half the rows are duplicates
return|return
name|rel
operator|.
name|getRows
argument_list|()
operator|/
literal|2
return|;
block|}
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|Project
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
name|BitSet
name|baseCols
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|BitSet
name|projCols
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|projExprs
init|=
name|rel
operator|.
name|getProjects
argument_list|()
decl_stmt|;
name|RelMdUtil
operator|.
name|splitCols
argument_list|(
name|projExprs
argument_list|,
name|groupKey
argument_list|,
name|baseCols
argument_list|,
name|projCols
argument_list|)
expr_stmt|;
name|Double
name|population
init|=
name|RelMetadataQuery
operator|.
name|getPopulationSize
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|baseCols
argument_list|)
decl_stmt|;
if|if
condition|(
name|population
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// No further computation required if the projection expressions are
comment|// all column references
if|if
condition|(
name|projCols
operator|.
name|cardinality
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|population
return|;
block|}
for|for
control|(
name|int
name|bit
range|:
name|BitSets
operator|.
name|toIter
argument_list|(
name|projCols
argument_list|)
control|)
block|{
name|Double
name|subRowCount
init|=
name|RelMdUtil
operator|.
name|cardOfProjExpr
argument_list|(
name|rel
argument_list|,
name|projExprs
operator|.
name|get
argument_list|(
name|bit
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|subRowCount
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|population
operator|*=
name|subRowCount
expr_stmt|;
block|}
comment|// REVIEW zfong 6/22/06 - Broadbase did not have the call to
comment|// numDistinctVals.  This is needed; otherwise, population can be
comment|// larger than the number of rows in the RelNode.
return|return
name|RelMdUtil
operator|.
name|numDistinctVals
argument_list|(
name|population
argument_list|,
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
argument_list|)
return|;
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Double
name|getPopulationSize
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|BitSet
name|groupKey
parameter_list|)
block|{
comment|// if the keys are unique, return the row count; otherwise, we have
comment|// no further information on which to return any legitimate value
comment|// REVIEW zfong 4/11/06 - Broadbase code returns the product of each
comment|// unique key, which would result in the population being larger
comment|// than the total rows in the relnode
name|boolean
name|uniq
init|=
name|RelMdUtil
operator|.
name|areColumnsDefinitelyUnique
argument_list|(
name|rel
argument_list|,
name|groupKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|uniq
condition|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|rel
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdPopulationSize.java
end_comment

end_unit

