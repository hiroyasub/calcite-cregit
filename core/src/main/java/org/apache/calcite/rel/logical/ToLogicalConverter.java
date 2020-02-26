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
name|logical
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
name|EnumerableInterpreter
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
name|adapter
operator|.
name|jdbc
operator|.
name|JdbcToEnumerableConverter
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
name|RelCollation
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
name|RelCollations
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
name|RelShuttleImpl
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
name|Correlate
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
name|Uncollect
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
name|core
operator|.
name|Window
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
name|tools
operator|.
name|RelBuilder
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
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_comment
comment|/**  * Shuttle to convert any rel plan to a plan with all logical nodes.  */
end_comment

begin_class
specifier|public
class|class
name|ToLogicalConverter
extends|extends
name|RelShuttleImpl
block|{
specifier|private
specifier|final
name|RelBuilder
name|relBuilder
decl_stmt|;
specifier|public
name|ToLogicalConverter
parameter_list|(
name|RelBuilder
name|relBuilder
parameter_list|)
block|{
name|this
operator|.
name|relBuilder
operator|=
name|relBuilder
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|TableScan
name|scan
parameter_list|)
block|{
return|return
name|LogicalTableScan
operator|.
name|create
argument_list|(
name|scan
operator|.
name|getCluster
argument_list|()
argument_list|,
name|scan
operator|.
name|getTable
argument_list|()
argument_list|,
name|scan
operator|.
name|getHints
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|visit
parameter_list|(
name|RelNode
name|relNode
parameter_list|)
block|{
if|if
condition|(
name|relNode
operator|instanceof
name|Aggregate
condition|)
block|{
specifier|final
name|Aggregate
name|agg
init|=
operator|(
name|Aggregate
operator|)
name|relNode
decl_stmt|;
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|agg
operator|.
name|getInput
argument_list|()
argument_list|)
argument_list|)
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|agg
operator|.
name|getGroupSet
argument_list|()
argument_list|,
operator|(
name|Iterable
argument_list|<
name|ImmutableBitSet
argument_list|>
operator|)
name|agg
operator|.
name|groupSets
argument_list|)
argument_list|,
name|agg
operator|.
name|getAggCallList
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|TableScan
condition|)
block|{
return|return
name|visit
argument_list|(
operator|(
name|TableScan
operator|)
name|relNode
argument_list|)
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Filter
condition|)
block|{
specifier|final
name|Filter
name|filter
init|=
operator|(
name|Filter
operator|)
name|relNode
decl_stmt|;
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|filter
operator|.
name|getInput
argument_list|()
argument_list|)
argument_list|)
operator|.
name|filter
argument_list|(
name|filter
operator|.
name|getCondition
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Project
condition|)
block|{
specifier|final
name|Project
name|project
init|=
operator|(
name|Project
operator|)
name|relNode
decl_stmt|;
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|)
argument_list|)
operator|.
name|project
argument_list|(
name|project
operator|.
name|getProjects
argument_list|()
argument_list|,
name|project
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Union
condition|)
block|{
specifier|final
name|Union
name|union
init|=
operator|(
name|Union
operator|)
name|relNode
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|union
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|relBuilder
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|relBuilder
operator|.
name|union
argument_list|(
name|union
operator|.
name|all
argument_list|,
name|union
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Intersect
condition|)
block|{
specifier|final
name|Intersect
name|intersect
init|=
operator|(
name|Intersect
operator|)
name|relNode
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|intersect
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|relBuilder
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|relBuilder
operator|.
name|intersect
argument_list|(
name|intersect
operator|.
name|all
argument_list|,
name|intersect
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Minus
condition|)
block|{
specifier|final
name|Minus
name|minus
init|=
operator|(
name|Minus
operator|)
name|relNode
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|minus
operator|.
name|getInputs
argument_list|()
control|)
block|{
name|relBuilder
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|rel
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|relBuilder
operator|.
name|minus
argument_list|(
name|minus
operator|.
name|all
argument_list|,
name|minus
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Join
condition|)
block|{
specifier|final
name|Join
name|join
init|=
operator|(
name|Join
operator|)
name|relNode
decl_stmt|;
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|)
argument_list|)
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|join
operator|.
name|getRight
argument_list|()
argument_list|)
argument_list|)
operator|.
name|join
argument_list|(
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Correlate
condition|)
block|{
specifier|final
name|Correlate
name|corr
init|=
operator|(
name|Correlate
operator|)
name|relNode
decl_stmt|;
return|return
name|relBuilder
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|corr
operator|.
name|getLeft
argument_list|()
argument_list|)
argument_list|)
operator|.
name|push
argument_list|(
name|visit
argument_list|(
name|corr
operator|.
name|getRight
argument_list|()
argument_list|)
argument_list|)
operator|.
name|join
argument_list|(
name|corr
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|relBuilder
operator|.
name|literal
argument_list|(
literal|true
argument_list|)
argument_list|,
name|corr
operator|.
name|getVariablesSet
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Values
condition|)
block|{
specifier|final
name|Values
name|values
init|=
operator|(
name|Values
operator|)
name|relNode
decl_stmt|;
return|return
name|relBuilder
operator|.
name|values
argument_list|(
name|values
operator|.
name|tuples
argument_list|,
name|values
operator|.
name|getRowType
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Sort
condition|)
block|{
specifier|final
name|Sort
name|sort
init|=
operator|(
name|Sort
operator|)
name|relNode
decl_stmt|;
return|return
name|LogicalSort
operator|.
name|create
argument_list|(
name|visit
argument_list|(
name|sort
operator|.
name|getInput
argument_list|()
argument_list|)
argument_list|,
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|,
name|sort
operator|.
name|offset
argument_list|,
name|sort
operator|.
name|fetch
argument_list|)
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Window
condition|)
block|{
specifier|final
name|Window
name|window
init|=
operator|(
name|Window
operator|)
name|relNode
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|visit
argument_list|(
name|window
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|LogicalWindow
operator|.
name|create
argument_list|(
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
argument_list|,
name|window
operator|.
name|constants
argument_list|,
name|window
operator|.
name|getRowType
argument_list|()
argument_list|,
name|window
operator|.
name|groups
argument_list|)
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Calc
condition|)
block|{
specifier|final
name|Calc
name|calc
init|=
operator|(
name|Calc
operator|)
name|relNode
decl_stmt|;
return|return
name|LogicalCalc
operator|.
name|create
argument_list|(
name|visit
argument_list|(
name|calc
operator|.
name|getInput
argument_list|()
argument_list|)
argument_list|,
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|TableModify
condition|)
block|{
specifier|final
name|TableModify
name|tableModify
init|=
operator|(
name|TableModify
operator|)
name|relNode
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|visit
argument_list|(
name|tableModify
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|LogicalTableModify
operator|.
name|create
argument_list|(
name|tableModify
operator|.
name|getTable
argument_list|()
argument_list|,
name|tableModify
operator|.
name|getCatalogReader
argument_list|()
argument_list|,
name|input
argument_list|,
name|tableModify
operator|.
name|getOperation
argument_list|()
argument_list|,
name|tableModify
operator|.
name|getUpdateColumnList
argument_list|()
argument_list|,
name|tableModify
operator|.
name|getSourceExpressionList
argument_list|()
argument_list|,
name|tableModify
operator|.
name|isFlattened
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|EnumerableInterpreter
operator|||
name|relNode
operator|instanceof
name|JdbcToEnumerableConverter
condition|)
block|{
return|return
name|visit
argument_list|(
operator|(
operator|(
name|SingleRel
operator|)
name|relNode
operator|)
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|EnumerableLimit
condition|)
block|{
specifier|final
name|EnumerableLimit
name|limit
init|=
operator|(
name|EnumerableLimit
operator|)
name|relNode
decl_stmt|;
name|RelNode
name|logicalInput
init|=
name|visit
argument_list|(
name|limit
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
name|RelCollation
name|collation
init|=
name|RelCollations
operator|.
name|of
argument_list|()
decl_stmt|;
if|if
condition|(
name|logicalInput
operator|instanceof
name|Sort
condition|)
block|{
name|collation
operator|=
operator|(
operator|(
name|Sort
operator|)
name|logicalInput
operator|)
operator|.
name|collation
expr_stmt|;
name|logicalInput
operator|=
operator|(
operator|(
name|Sort
operator|)
name|logicalInput
operator|)
operator|.
name|getInput
argument_list|()
expr_stmt|;
block|}
return|return
name|LogicalSort
operator|.
name|create
argument_list|(
name|logicalInput
argument_list|,
name|collation
argument_list|,
name|limit
operator|.
name|offset
argument_list|,
name|limit
operator|.
name|fetch
argument_list|)
return|;
block|}
if|if
condition|(
name|relNode
operator|instanceof
name|Uncollect
condition|)
block|{
specifier|final
name|Uncollect
name|uncollect
init|=
operator|(
name|Uncollect
operator|)
name|relNode
decl_stmt|;
specifier|final
name|RelNode
name|input
init|=
name|visit
argument_list|(
name|uncollect
operator|.
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Uncollect
operator|.
name|create
argument_list|(
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
argument_list|,
name|uncollect
operator|.
name|withOrdinality
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Need to implement logical converter for "
operator|+
name|relNode
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

