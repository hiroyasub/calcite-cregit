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
name|RelTraitDef
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
name|logical
operator|.
name|LogicalSort
import|;
end_import

begin_comment
comment|/**  * Definition of the ordering trait.  *  *<p>Ordering is a physical property (i.e. a trait) because it can be changed  * without loss of information. The converter to do this is the  * {@link org.apache.calcite.rel.core.Sort} operator.  *  *<p>Unlike other current traits, a {@link RelNode} can have more than one  * value of this trait simultaneously. For example,  *<code>LogicalTableScan(table=TIME_BY_DAY)</code> might be sorted by  *<code>{the_year, the_month, the_date}</code> and also by  *<code>{time_id}</code>. We have to allow a RelNode to belong to more than  * one RelSubset (these RelSubsets are always in the same set).</p>  */
end_comment

begin_class
specifier|public
class|class
name|RelCollationTraitDef
extends|extends
name|RelTraitDef
argument_list|<
name|RelCollation
argument_list|>
block|{
specifier|public
specifier|static
specifier|final
name|RelCollationTraitDef
name|INSTANCE
init|=
operator|new
name|RelCollationTraitDef
argument_list|()
decl_stmt|;
specifier|private
name|RelCollationTraitDef
parameter_list|()
block|{
block|}
specifier|public
name|Class
argument_list|<
name|RelCollation
argument_list|>
name|getTraitClass
parameter_list|()
block|{
return|return
name|RelCollation
operator|.
name|class
return|;
block|}
specifier|public
name|String
name|getSimpleName
parameter_list|()
block|{
return|return
literal|"sort"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|multiple
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|RelCollation
name|getDefault
parameter_list|()
block|{
return|return
name|RelCollationImpl
operator|.
name|EMPTY
return|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelCollation
name|toCollation
parameter_list|,
name|boolean
name|allowInfiniteCostConverters
parameter_list|)
block|{
if|if
condition|(
name|toCollation
operator|==
name|RelCollationImpl
operator|.
name|PRESERVE
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|toCollation
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// An empty sort doesn't make sense.
return|return
literal|null
return|;
block|}
comment|// Create a logical sort, then ask the planner to convert its remaining
comment|// traits (e.g. convert it to an EnumerableSortRel if rel is enumerable
comment|// convention)
specifier|final
name|Sort
name|sort
init|=
name|LogicalSort
operator|.
name|create
argument_list|(
name|rel
argument_list|,
name|toCollation
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|RelNode
name|newRel
init|=
name|sort
decl_stmt|;
specifier|final
name|RelTraitSet
name|newTraitSet
init|=
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|toCollation
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|newRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|equals
argument_list|(
name|newTraitSet
argument_list|)
condition|)
block|{
name|newRel
operator|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|sort
argument_list|,
name|newTraitSet
argument_list|)
expr_stmt|;
block|}
return|return
name|newRel
return|;
block|}
specifier|public
name|boolean
name|canConvert
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelCollation
name|fromTrait
parameter_list|,
name|RelCollation
name|toTrait
parameter_list|)
block|{
return|return
name|toTrait
operator|!=
name|RelCollationImpl
operator|.
name|PRESERVE
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelCollationTraitDef.java
end_comment

end_unit

