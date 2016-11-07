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
name|rules
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
name|RelOptRule
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
name|RelOptRuleCall
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
name|RelFactories
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
name|LogicalIntersect
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
name|RexBuilder
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
name|tools
operator|.
name|RelBuilderFactory
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
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_comment
comment|/**  * Planner rule that translates a distinct  * {@link org.apache.calcite.rel.core.Intersect}  * (<code>all</code> =<code>false</code>)  * into a group of operators composed of  * {@link org.apache.calcite.rel.core.Union},  * {@link org.apache.calcite.rel.core.Aggregate}, etc.  *  *<p> Rewrite: (GB-Union All-GB)-GB-UDTF (on all attributes)  *  *<h3>Example</h3>  *  *<p>Query:<code>R1 Intersect All R2</code>  *  *<p><code>R3 = GB(R1 on all attributes, count(*) as c)<br>  *   union all<br>  *   GB(R2 on all attributes, count(*) as c)</code>  *  *<p><code>R4 = GB(R3 on all attributes, count(c) as cnt, min(c) as m)</code>  *  * Note that we do not need<code>min(c)</code> in intersect distinct.  *  *<p><code>R5 = Filter(cnt == #branch)</code>  *  *<p>If it is intersect all then  *  *<p><code>R6 = UDTF (R5) which will explode the tuples based on min(c)<br>  * R7 = Project(R6 on all attributes)</code>  *  *<p>Else  *  *<p><code>R6 = Proj(R5 on all attributes)</code>  *  * @see org.apache.calcite.rel.rules.UnionToDistinctRule  */
end_comment

begin_class
specifier|public
class|class
name|IntersectToDistinctRule
extends|extends
name|RelOptRule
block|{
specifier|public
specifier|static
specifier|final
name|IntersectToDistinctRule
name|INSTANCE
init|=
operator|new
name|IntersectToDistinctRule
argument_list|(
name|LogicalIntersect
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/** Creates an IntersectToDistinctRule. */
specifier|public
name|IntersectToDistinctRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Intersect
argument_list|>
name|intersectClazz
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|intersectClazz
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
specifier|final
name|Intersect
name|intersect
init|=
name|call
operator|.
name|rel
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|intersect
operator|.
name|all
condition|)
block|{
return|return;
comment|// nothing we can do
block|}
specifier|final
name|RelOptCluster
name|cluster
init|=
name|intersect
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelBuilder
name|relBuilder
init|=
name|call
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// 1st level GB: create a GB (col0, col1, count() as c) for each branch
for|for
control|(
name|RelNode
name|input
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
name|input
argument_list|)
expr_stmt|;
name|relBuilder
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|()
argument_list|)
argument_list|,
name|relBuilder
operator|.
name|countStar
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// create a union above all the branches
specifier|final
name|int
name|branchCount
init|=
name|intersect
operator|.
name|getInputs
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|relBuilder
operator|.
name|union
argument_list|(
literal|true
argument_list|,
name|branchCount
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|union
init|=
name|relBuilder
operator|.
name|peek
argument_list|()
decl_stmt|;
comment|// 2nd level GB: create a GB (col0, col1, count(c)) for each branch
comment|// the index of c is union.getRowType().getFieldList().size() - 1
specifier|final
name|int
name|fieldCount
init|=
name|union
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|groupSet
init|=
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|fieldCount
operator|-
literal|1
argument_list|)
decl_stmt|;
name|relBuilder
operator|.
name|aggregate
argument_list|(
name|relBuilder
operator|.
name|groupKey
argument_list|(
name|groupSet
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
argument_list|,
name|relBuilder
operator|.
name|countStar
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// add a filter count(c) = #branches
name|relBuilder
operator|.
name|filter
argument_list|(
name|relBuilder
operator|.
name|equals
argument_list|(
name|relBuilder
operator|.
name|field
argument_list|(
name|fieldCount
operator|-
literal|1
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeBigintLiteral
argument_list|(
operator|new
name|BigDecimal
argument_list|(
name|branchCount
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// Project all but the last field
name|relBuilder
operator|.
name|project
argument_list|(
name|Util
operator|.
name|skipLast
argument_list|(
name|relBuilder
operator|.
name|fields
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// the schema for intersect distinct is like this
comment|// R3 on all attributes + count(c) as cnt
comment|// finally add a project to project out the last column
name|call
operator|.
name|transformTo
argument_list|(
name|relBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End IntersectToDistinctRule.java
end_comment

end_unit

