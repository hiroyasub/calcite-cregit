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
name|linq4j
operator|.
name|Linq4j
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMultimap
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
name|ImmutableSet
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
name|Maps
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
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * RelMdUniqueKeys supplies a default implementation of  * {@link RelMetadataQuery#getUniqueKeys} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdUniqueKeys
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|UniqueKeys
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
operator|new
name|RelMdUniqueKeys
argument_list|()
argument_list|,
name|BuiltInMetadata
operator|.
name|UniqueKeys
operator|.
name|Handler
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdUniqueKeys
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
name|UniqueKeys
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|UniqueKeys
operator|.
name|DEF
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Correlate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|TableModify
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Project
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|getProjectUniqueKeys
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|,
name|ignoreNulls
argument_list|,
name|rel
operator|.
name|getProjects
argument_list|()
argument_list|)
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Calc
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
name|RexProgram
name|program
init|=
name|rel
operator|.
name|getProgram
argument_list|()
decl_stmt|;
return|return
name|getProjectUniqueKeys
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|,
name|ignoreNulls
argument_list|,
name|Util
operator|.
name|transform
argument_list|(
name|program
operator|.
name|getProjectList
argument_list|()
argument_list|,
name|program
operator|::
name|expandLocalRef
argument_list|)
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getProjectUniqueKeys
parameter_list|(
name|SingleRel
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|projExprs
parameter_list|)
block|{
comment|// LogicalProject maps a set of rows to a different set;
comment|// Without knowledge of the mapping function(whether it
comment|// preserves uniqueness), it is only safe to derive uniqueness
comment|// info from the child of a project when the mapping is f(a) => a.
comment|//
comment|// Further more, the unique bitset coming from the child needs
comment|// to be mapped to match the output of the project.
comment|// Single input can be mapped to multiple outputs
name|ImmutableMultimap
operator|.
name|Builder
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|inToOutPosBuilder
init|=
name|ImmutableMultimap
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ImmutableBitSet
operator|.
name|Builder
name|mappedInColumnsBuilder
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// Build an input to output position map.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|projExprs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RexNode
name|projExpr
init|=
name|projExprs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|projExpr
operator|instanceof
name|RexInputRef
condition|)
block|{
name|int
name|inputIndex
init|=
operator|(
operator|(
name|RexInputRef
operator|)
name|projExpr
operator|)
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|inToOutPosBuilder
operator|.
name|put
argument_list|(
name|inputIndex
argument_list|,
name|i
argument_list|)
expr_stmt|;
name|mappedInColumnsBuilder
operator|.
name|set
argument_list|(
name|inputIndex
argument_list|)
expr_stmt|;
block|}
block|}
name|ImmutableBitSet
name|inColumnsUsed
init|=
name|mappedInColumnsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|inColumnsUsed
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// if there's no RexInputRef in the projected expressions
comment|// return empty set.
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|childUniqueKeySet
init|=
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
if|if
condition|(
name|childUniqueKeySet
operator|==
literal|null
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
name|Map
argument_list|<
name|Integer
argument_list|,
name|ImmutableBitSet
argument_list|>
name|mapInToOutPos
init|=
name|Maps
operator|.
name|transformValues
argument_list|(
name|inToOutPosBuilder
operator|.
name|build
argument_list|()
operator|.
name|asMap
argument_list|()
argument_list|,
name|ImmutableBitSet
operator|::
name|of
argument_list|)
decl_stmt|;
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ImmutableBitSet
argument_list|>
name|resultBuilder
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
comment|// Now add to the projUniqueKeySet the child keys that are fully
comment|// projected.
for|for
control|(
name|ImmutableBitSet
name|colMask
range|:
name|childUniqueKeySet
control|)
block|{
if|if
condition|(
operator|!
name|inColumnsUsed
operator|.
name|contains
argument_list|(
name|colMask
argument_list|)
condition|)
block|{
comment|// colMask contains a column that is not projected as RexInput => the key is not unique
continue|continue;
block|}
comment|// colMask is mapped to output project, however, the column can be mapped more than once:
comment|// select id, id, id, unique2, unique2
comment|// the resulting unique keys would be {{0},{3}}, {{0},{4}}, {{0},{1},{4}}, ...
name|Iterable
argument_list|<
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
argument_list|>
name|product
init|=
name|Linq4j
operator|.
name|product
argument_list|(
name|Util
operator|.
name|transform
argument_list|(
name|colMask
argument_list|,
name|in
lambda|->
name|Util
operator|.
name|filter
argument_list|(
name|requireNonNull
argument_list|(
name|mapInToOutPos
operator|.
name|get
argument_list|(
name|in
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"no entry for column "
operator|+
name|in
operator|+
literal|" in mapInToOutPos: "
operator|+
name|mapInToOutPos
argument_list|)
operator|.
name|powerSet
argument_list|()
argument_list|,
name|bs
lambda|->
operator|!
name|bs
operator|.
name|isEmpty
argument_list|()
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|resultBuilder
operator|.
name|addAll
argument_list|(
name|Util
operator|.
name|transform
argument_list|(
name|product
argument_list|,
name|ImmutableBitSet
operator|::
name|union
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|resultBuilder
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|projectsRight
argument_list|()
condition|)
block|{
comment|// only return the unique keys from the LHS since a semijoin only
comment|// returns the LHS
return|return
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
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
comment|// first add the different combinations of concatenated unique keys
comment|// from the left and the right, adjusting the right hand side keys to
comment|// reflect the addition of the left hand side
comment|//
comment|// NOTE zfong 12/18/06 - If the number of tables in a join is large,
comment|// the number of combinations of unique key sets will explode.  If
comment|// that is undesirable, use RelMetadataQuery.areColumnsUnique() as
comment|// an alternative way of getting unique key information.
specifier|final
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|retSet
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|leftSet
init|=
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|left
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|rightSet
init|=
literal|null
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|tmpRightSet
init|=
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|right
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
name|int
name|nFieldsOnLeft
init|=
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|tmpRightSet
operator|!=
literal|null
condition|)
block|{
name|rightSet
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|ImmutableBitSet
name|colMask
range|:
name|tmpRightSet
control|)
block|{
name|ImmutableBitSet
operator|.
name|Builder
name|tmpMask
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|bit
range|:
name|colMask
control|)
block|{
name|tmpMask
operator|.
name|set
argument_list|(
name|bit
operator|+
name|nFieldsOnLeft
argument_list|)
expr_stmt|;
block|}
name|rightSet
operator|.
name|add
argument_list|(
name|tmpMask
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|leftSet
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|ImmutableBitSet
name|colMaskRight
range|:
name|rightSet
control|)
block|{
for|for
control|(
name|ImmutableBitSet
name|colMaskLeft
range|:
name|leftSet
control|)
block|{
name|retSet
operator|.
name|add
argument_list|(
name|colMaskLeft
operator|.
name|union
argument_list|(
name|colMaskRight
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|// locate the columns that participate in equijoins
specifier|final
name|JoinInfo
name|joinInfo
init|=
name|rel
operator|.
name|analyzeCondition
argument_list|()
decl_stmt|;
comment|// determine if either or both the LHS and RHS are unique on the
comment|// equijoin columns
specifier|final
name|Boolean
name|leftUnique
init|=
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|left
argument_list|,
name|joinInfo
operator|.
name|leftSet
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
specifier|final
name|Boolean
name|rightUnique
init|=
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|right
argument_list|,
name|joinInfo
operator|.
name|rightSet
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
comment|// if the right hand side is unique on its equijoin columns, then we can
comment|// add the unique keys from left if the left hand side is not null
comment|// generating
if|if
condition|(
operator|(
name|rightUnique
operator|!=
literal|null
operator|)
operator|&&
name|rightUnique
operator|&&
operator|(
name|leftSet
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
condition|)
block|{
name|retSet
operator|.
name|addAll
argument_list|(
name|leftSet
argument_list|)
expr_stmt|;
block|}
comment|// same as above except left and right are reversed
if|if
condition|(
operator|(
name|leftUnique
operator|!=
literal|null
operator|)
operator|&&
name|leftUnique
operator|&&
operator|(
name|rightSet
operator|!=
literal|null
operator|)
operator|&&
operator|!
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnRight
argument_list|()
condition|)
block|{
name|retSet
operator|.
name|addAll
argument_list|(
name|rightSet
argument_list|)
expr_stmt|;
block|}
return|return
name|retSet
return|;
block|}
specifier|public
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
if|if
condition|(
name|Aggregate
operator|.
name|isSimple
argument_list|(
name|rel
argument_list|)
condition|)
block|{
specifier|final
name|ImmutableBitSet
name|groupKeys
init|=
name|rel
operator|.
name|getGroupSet
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|inputUniqueKeys
init|=
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputUniqueKeys
operator|==
literal|null
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|groupKeys
argument_list|)
return|;
block|}
comment|// Try to find more precise unique keys.
specifier|final
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|preciseUniqueKeys
init|=
name|inputUniqueKeys
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|groupKeys
operator|::
name|contains
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|preciseUniqueKeys
operator|.
name|isEmpty
argument_list|()
condition|?
name|ImmutableSet
operator|.
name|of
argument_list|(
name|groupKeys
argument_list|)
else|:
name|preciseUniqueKeys
return|;
block|}
if|else if
condition|(
name|ignoreNulls
condition|)
block|{
comment|// group by keys form a unique key
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|rel
operator|.
name|getGroupSet
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
comment|// If the aggregate has grouping sets, all group by keys might be null which means group by
comment|// keys do not form a unique key.
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
block|}
specifier|public
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Union
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
if|if
condition|(
operator|!
name|rel
operator|.
name|all
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
comment|/**    * Any unique key of any input of Intersect is an unique key of the Intersect.    */
specifier|public
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Intersect
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|ImmutableBitSet
argument_list|>
name|keys
init|=
operator|new
name|ImmutableSet
operator|.
name|Builder
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
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|uniqueKeys
init|=
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|input
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
if|if
condition|(
name|uniqueKeys
operator|!=
literal|null
condition|)
block|{
name|keys
operator|.
name|addAll
argument_list|(
name|uniqueKeys
argument_list|)
expr_stmt|;
block|}
block|}
name|ImmutableSet
argument_list|<
name|ImmutableBitSet
argument_list|>
name|uniqueKeys
init|=
name|keys
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|uniqueKeys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|uniqueKeys
return|;
block|}
if|if
condition|(
operator|!
name|rel
operator|.
name|all
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
comment|/**    * The unique keys of Minus are precisely the unique keys of its first input.    */
specifier|public
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|Minus
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|uniqueKeys
init|=
name|mq
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
if|if
condition|(
name|uniqueKeys
operator|!=
literal|null
condition|)
block|{
return|return
name|uniqueKeys
return|;
block|}
if|if
condition|(
operator|!
name|rel
operator|.
name|all
condition|)
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|(
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|TableScan
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
specifier|final
name|BuiltInMetadata
operator|.
name|UniqueKeys
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
name|UniqueKeys
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
name|getUniqueKeys
argument_list|(
name|rel
argument_list|,
name|mq
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|final
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|keys
init|=
name|rel
operator|.
name|getTable
argument_list|()
operator|.
name|getKeys
argument_list|()
decl_stmt|;
if|if
condition|(
name|keys
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|ImmutableBitSet
name|key
range|:
name|keys
control|)
block|{
assert|assert
name|rel
operator|.
name|getTable
argument_list|()
operator|.
name|isKey
argument_list|(
name|key
argument_list|)
assert|;
block|}
return|return
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|keys
argument_list|)
return|;
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
comment|// no information available
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

