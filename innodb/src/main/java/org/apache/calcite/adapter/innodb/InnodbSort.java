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
name|innodb
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
name|RelOptCost
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
name|RelOptPlanner
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
name|RelFieldCollation
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
name|metadata
operator|.
name|RelMetadataQuery
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkState
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link org.apache.calcite.rel.core.Sort}  * relational expression for an InnoDB data source.  */
end_comment

begin_class
specifier|public
class|class
name|InnodbSort
extends|extends
name|Sort
implements|implements
name|InnodbRel
block|{
name|InnodbSort
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RelCollation
name|collation
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|collation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|InnodbRel
operator|.
name|CONVENTION
assert|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|input
operator|.
name|getConvention
argument_list|()
assert|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
name|RelOptCost
name|cost
init|=
name|super
operator|.
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|mq
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|collation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|cost
operator|.
name|multiplyBy
argument_list|(
literal|0.05
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|cost
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Sort
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|,
name|RexNode
name|offset
parameter_list|,
name|RexNode
name|fetch
parameter_list|)
block|{
return|return
operator|new
name|InnodbSort
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|collation
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getInput
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|sortCollations
init|=
name|collation
operator|.
name|getFieldCollations
argument_list|()
decl_stmt|;
name|boolean
name|allDesc
init|=
name|sortCollations
operator|.
name|stream
argument_list|()
operator|.
name|allMatch
argument_list|(
name|r
lambda|->
name|r
operator|.
name|direction
operator|.
name|isDescending
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|allNonDesc
init|=
name|sortCollations
operator|.
name|stream
argument_list|()
operator|.
name|noneMatch
argument_list|(
name|r
lambda|->
name|r
operator|.
name|direction
operator|.
name|isDescending
argument_list|()
argument_list|)
decl_stmt|;
comment|// field collation should be in a series of ascending or descending collations
name|checkState
argument_list|(
name|allDesc
operator|||
name|allNonDesc
argument_list|,
literal|"ordering should be in a "
operator|+
literal|"series of ascending or descending collations "
operator|+
name|sortCollations
argument_list|)
expr_stmt|;
name|implementor
operator|.
name|setAscOrder
argument_list|(
operator|!
name|allDesc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
