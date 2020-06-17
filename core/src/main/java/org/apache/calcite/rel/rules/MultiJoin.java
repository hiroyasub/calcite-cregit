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
name|AbstractRelNode
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
name|RelWriter
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
name|JoinRelType
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
name|RexShuttle
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
name|ImmutableIntList
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|Lists
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
name|HashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A MultiJoin represents a join of N inputs, whereas regular Joins  * represent strictly binary joins.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MultiJoin
extends|extends
name|AbstractRelNode
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
decl_stmt|;
specifier|private
specifier|final
name|RexNode
name|joinFilter
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|isFullOuterJoin
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|outerJoinConditions
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|JoinRelType
argument_list|>
name|joinTypes
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|projFields
decl_stmt|;
specifier|public
specifier|final
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|ImmutableIntList
argument_list|>
name|joinFieldRefCountsMap
decl_stmt|;
specifier|private
specifier|final
name|RexNode
name|postJoinFilter
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Constructs a MultiJoin.    *    * @param cluster               cluster that join belongs to    * @param inputs                inputs into this multi-join    * @param joinFilter            join filter applicable to this join node    * @param rowType               row type of the join result of this node    * @param isFullOuterJoin       true if the join is a full outer join    * @param outerJoinConditions   outer join condition associated with each join    *                              input, if the input is null-generating in a    *                              left or right outer join; null otherwise    * @param joinTypes             the join type corresponding to each input; if    *                              an input is null-generating in a left or right    *                              outer join, the entry indicates the type of    *                              outer join; otherwise, the entry is set to    *                              INNER    * @param projFields            fields that will be projected from each input;    *                              if null, projection information is not    *                              available yet so it's assumed that all fields    *                              from the input are projected    * @param joinFieldRefCountsMap counters of the number of times each field    *                              is referenced in join conditions, indexed by    *                              the input #    * @param postJoinFilter        filter to be applied after the joins are    */
specifier|public
name|MultiJoin
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|,
name|RexNode
name|joinFilter
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|boolean
name|isFullOuterJoin
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|outerJoinConditions
parameter_list|,
name|List
argument_list|<
name|JoinRelType
argument_list|>
name|joinTypes
parameter_list|,
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|projFields
parameter_list|,
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|ImmutableIntList
argument_list|>
name|joinFieldRefCountsMap
parameter_list|,
name|RexNode
name|postJoinFilter
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|inputs
operator|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|inputs
argument_list|)
expr_stmt|;
name|this
operator|.
name|joinFilter
operator|=
name|joinFilter
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|isFullOuterJoin
operator|=
name|isFullOuterJoin
expr_stmt|;
name|this
operator|.
name|outerJoinConditions
operator|=
name|ImmutableNullableList
operator|.
name|copyOf
argument_list|(
name|outerJoinConditions
argument_list|)
expr_stmt|;
assert|assert
name|outerJoinConditions
operator|.
name|size
argument_list|()
operator|==
name|inputs
operator|.
name|size
argument_list|()
assert|;
name|this
operator|.
name|joinTypes
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|joinTypes
argument_list|)
expr_stmt|;
name|this
operator|.
name|projFields
operator|=
name|ImmutableNullableList
operator|.
name|copyOf
argument_list|(
name|projFields
argument_list|)
expr_stmt|;
name|this
operator|.
name|joinFieldRefCountsMap
operator|=
name|joinFieldRefCountsMap
expr_stmt|;
name|this
operator|.
name|postJoinFilter
operator|=
name|postJoinFilter
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|replaceInput
parameter_list|(
name|int
name|ordinalInParent
parameter_list|,
name|RelNode
name|p
parameter_list|)
block|{
name|inputs
operator|.
name|set
argument_list|(
name|ordinalInParent
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|recomputeDigest
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|MultiJoin
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|inputs
argument_list|,
name|joinFilter
argument_list|,
name|rowType
argument_list|,
name|isFullOuterJoin
argument_list|,
name|outerJoinConditions
argument_list|,
name|joinTypes
argument_list|,
name|projFields
argument_list|,
name|joinFieldRefCountsMap
argument_list|,
name|postJoinFilter
argument_list|)
return|;
block|}
comment|/**    * Returns a deep copy of {@link #joinFieldRefCountsMap}.    */
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|cloneJoinFieldRefCountsMap
parameter_list|()
block|{
name|Map
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|clonedMap
init|=
operator|new
name|HashMap
argument_list|<>
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
name|inputs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|clonedMap
operator|.
name|put
argument_list|(
name|i
argument_list|,
name|joinFieldRefCountsMap
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toIntArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|clonedMap
return|;
block|}
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|joinTypeNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|outerJoinConds
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|projFieldObjects
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|inputs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|joinTypeNames
operator|.
name|add
argument_list|(
name|joinTypes
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|outerJoinConditions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
name|outerJoinConds
operator|.
name|add
argument_list|(
literal|"NULL"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|outerJoinConds
operator|.
name|add
argument_list|(
name|outerJoinConditions
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|projFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
literal|null
condition|)
block|{
name|projFieldObjects
operator|.
name|add
argument_list|(
literal|"ALL"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|projFieldObjects
operator|.
name|add
argument_list|(
name|projFields
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
expr_stmt|;
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
name|inputs
argument_list|)
control|)
block|{
name|pw
operator|.
name|input
argument_list|(
literal|"input#"
operator|+
name|ord
operator|.
name|i
argument_list|,
name|ord
operator|.
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|pw
operator|.
name|item
argument_list|(
literal|"joinFilter"
argument_list|,
name|joinFilter
argument_list|)
operator|.
name|item
argument_list|(
literal|"isFullOuterJoin"
argument_list|,
name|isFullOuterJoin
argument_list|)
operator|.
name|item
argument_list|(
literal|"joinTypes"
argument_list|,
name|joinTypeNames
argument_list|)
operator|.
name|item
argument_list|(
literal|"outerJoinConditions"
argument_list|,
name|outerJoinConds
argument_list|)
operator|.
name|item
argument_list|(
literal|"projFields"
argument_list|,
name|projFieldObjects
argument_list|)
operator|.
name|itemIf
argument_list|(
literal|"postJoinFilter"
argument_list|,
name|postJoinFilter
argument_list|,
name|postJoinFilter
operator|!=
literal|null
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
block|{
return|return
name|inputs
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getChildExps
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|joinFilter
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RexShuttle
name|shuttle
parameter_list|)
block|{
name|RexNode
name|joinFilter
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|this
operator|.
name|joinFilter
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|outerJoinConditions
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|this
operator|.
name|outerJoinConditions
argument_list|)
decl_stmt|;
name|RexNode
name|postJoinFilter
init|=
name|shuttle
operator|.
name|apply
argument_list|(
name|this
operator|.
name|postJoinFilter
argument_list|)
decl_stmt|;
if|if
condition|(
name|joinFilter
operator|==
name|this
operator|.
name|joinFilter
operator|&&
name|outerJoinConditions
operator|==
name|this
operator|.
name|outerJoinConditions
operator|&&
name|postJoinFilter
operator|==
name|this
operator|.
name|postJoinFilter
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|MultiJoin
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|inputs
argument_list|,
name|joinFilter
argument_list|,
name|rowType
argument_list|,
name|isFullOuterJoin
argument_list|,
name|outerJoinConditions
argument_list|,
name|joinTypes
argument_list|,
name|projFields
argument_list|,
name|joinFieldRefCountsMap
argument_list|,
name|postJoinFilter
argument_list|)
return|;
block|}
comment|/**    * @return join filters associated with this MultiJoin    */
specifier|public
name|RexNode
name|getJoinFilter
parameter_list|()
block|{
return|return
name|joinFilter
return|;
block|}
comment|/**    * @return true if the MultiJoin corresponds to a full outer join.    */
specifier|public
name|boolean
name|isFullOuterJoin
parameter_list|()
block|{
return|return
name|isFullOuterJoin
return|;
block|}
comment|/**    * @return outer join conditions for null-generating inputs    */
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getOuterJoinConditions
parameter_list|()
block|{
return|return
name|outerJoinConditions
return|;
block|}
comment|/**    * @return join types of each input    */
specifier|public
name|List
argument_list|<
name|JoinRelType
argument_list|>
name|getJoinTypes
parameter_list|()
block|{
return|return
name|joinTypes
return|;
block|}
comment|/**    * @return bitmaps representing the fields projected from each input; if an    * entry is null, all fields are projected    */
specifier|public
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getProjFields
parameter_list|()
block|{
return|return
name|projFields
return|;
block|}
comment|/**    * @return the map of reference counts for each input, representing the    * fields accessed in join conditions    */
specifier|public
name|ImmutableMap
argument_list|<
name|Integer
argument_list|,
name|ImmutableIntList
argument_list|>
name|getJoinFieldRefCountsMap
parameter_list|()
block|{
return|return
name|joinFieldRefCountsMap
return|;
block|}
comment|/**    * @return a copy of the map of reference counts for each input,    * representing the fields accessed in join conditions    */
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|int
index|[]
argument_list|>
name|getCopyJoinFieldRefCountsMap
parameter_list|()
block|{
return|return
name|cloneJoinFieldRefCountsMap
argument_list|()
return|;
block|}
comment|/**    * @return post-join filter associated with this MultiJoin    */
specifier|public
name|RexNode
name|getPostJoinFilter
parameter_list|()
block|{
return|return
name|postJoinFilter
return|;
block|}
name|boolean
name|containsOuter
parameter_list|()
block|{
for|for
control|(
name|JoinRelType
name|joinType
range|:
name|joinTypes
control|)
block|{
if|if
condition|(
name|joinType
operator|.
name|isOuterJoin
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

