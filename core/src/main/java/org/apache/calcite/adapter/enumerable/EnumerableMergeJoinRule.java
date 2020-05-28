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
name|enumerable
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
name|linq4j
operator|.
name|Ord
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
name|Convention
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
name|convert
operator|.
name|ConverterRule
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
name|JoinInfo
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
name|LogicalJoin
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
name|RexUtil
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
name|Arrays
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
comment|/** Planner rule that converts a  * {@link org.apache.calcite.rel.logical.LogicalJoin} relational expression  * {@link EnumerableConvention enumerable calling convention}.  *  * @see EnumerableJoinRule  * @see EnumerableRules#ENUMERABLE_MERGE_JOIN_RULE  */
end_comment

begin_class
class|class
name|EnumerableMergeJoinRule
extends|extends
name|ConverterRule
block|{
comment|/** Default configuration. */
specifier|static
specifier|final
name|Config
name|DEFAULT_CONFIG
init|=
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
name|Convention
operator|.
name|NONE
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"EnumerableMergeJoinRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|EnumerableMergeJoinRule
operator|::
operator|new
argument_list|)
decl_stmt|;
comment|/** Called from the Config. */
specifier|protected
name|EnumerableMergeJoinRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|LogicalJoin
name|join
init|=
operator|(
name|LogicalJoin
operator|)
name|rel
decl_stmt|;
specifier|final
name|JoinInfo
name|info
init|=
name|join
operator|.
name|analyzeCondition
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|EnumerableMergeJoin
operator|.
name|isMergeJoinSupported
argument_list|(
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
condition|)
block|{
comment|// EnumerableMergeJoin only supports certain join types.
return|return
literal|null
return|;
block|}
if|if
condition|(
name|info
operator|.
name|pairs
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// EnumerableMergeJoin CAN support cartesian join, but disable it for now.
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RelNode
argument_list|>
name|ord
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|join
operator|.
name|getInputs
argument_list|()
argument_list|)
control|)
block|{
name|RelTraitSet
name|traits
init|=
name|ord
operator|.
name|e
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|info
operator|.
name|pairs
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|fieldCollations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|key
range|:
name|info
operator|.
name|keys
argument_list|()
operator|.
name|get
argument_list|(
name|ord
operator|.
name|i
argument_list|)
control|)
block|{
name|fieldCollations
operator|.
name|add
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|key
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|ASCENDING
argument_list|,
name|RelFieldCollation
operator|.
name|NullDirection
operator|.
name|LAST
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelCollation
name|collation
init|=
name|RelCollations
operator|.
name|of
argument_list|(
name|fieldCollations
argument_list|)
decl_stmt|;
name|collations
operator|.
name|add
argument_list|(
name|RelCollations
operator|.
name|shift
argument_list|(
name|collation
argument_list|,
name|offset
argument_list|)
argument_list|)
expr_stmt|;
name|traits
operator|=
name|traits
operator|.
name|replace
argument_list|(
name|collation
argument_list|)
expr_stmt|;
block|}
name|newInputs
operator|.
name|add
argument_list|(
name|convert
argument_list|(
name|ord
operator|.
name|e
argument_list|,
name|traits
argument_list|)
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|ord
operator|.
name|e
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
expr_stmt|;
block|}
specifier|final
name|RelNode
name|left
init|=
name|newInputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|right
init|=
name|newInputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|RelOptCluster
name|cluster
init|=
name|join
operator|.
name|getCluster
argument_list|()
decl_stmt|;
name|RelNode
name|newRel
decl_stmt|;
name|RelTraitSet
name|traitSet
init|=
name|join
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|collations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|traitSet
operator|=
name|traitSet
operator|.
name|replace
argument_list|(
name|collations
argument_list|)
expr_stmt|;
block|}
comment|// Re-arrange condition: first the equi-join elements, then the non-equi-join ones (if any);
comment|// this is not strictly necessary but it will be useful to avoid spurious errors in the
comment|// unit tests when verifying the plan.
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|join
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|equi
init|=
name|info
operator|.
name|getEquiCondition
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|rexBuilder
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|condition
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|isEqui
argument_list|()
condition|)
block|{
name|condition
operator|=
name|equi
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|RexNode
name|nonEqui
init|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|info
operator|.
name|nonEquiConditions
argument_list|)
decl_stmt|;
name|condition
operator|=
name|RexUtil
operator|.
name|composeConjunction
argument_list|(
name|rexBuilder
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|equi
argument_list|,
name|nonEqui
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|newRel
operator|=
operator|new
name|EnumerableMergeJoin
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|join
operator|.
name|getVariablesSet
argument_list|()
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|newRel
return|;
block|}
block|}
end_class

end_unit

