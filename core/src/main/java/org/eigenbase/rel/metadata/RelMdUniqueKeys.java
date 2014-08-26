begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|rules
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|BuiltinMethod
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|util
operator|.
name|BitSets
import|;
end_import

begin_comment
comment|/**  * RelMdUniqueKeys supplies a default implementation of {@link  * RelMetadataQuery#getUniqueKeys} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdUniqueKeys
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
name|BuiltinMethod
operator|.
name|UNIQUE_KEYS
operator|.
name|method
argument_list|,
operator|new
name|RelMdUniqueKeys
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdUniqueKeys
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Set
argument_list|<
name|BitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|FilterRelBase
name|rel
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getChild
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|BitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|SortRel
name|rel
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getChild
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|BitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|CorrelatorRel
name|rel
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|RelMetadataQuery
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
name|Set
argument_list|<
name|BitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|ProjectRelBase
name|rel
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
comment|// ProjectRel maps a set of rows to a different set;
comment|// Without knowledge of the mapping function(whether it
comment|// preserves uniqueness), it is only safe to derive uniqueness
comment|// info from the child of a project when the mapping is f(a) => a.
comment|//
comment|// Further more, the unique bitset coming from the child needs
comment|// to be mapped to match the output of the project.
name|Map
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|mapInToOutPos
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
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
name|Set
argument_list|<
name|BitSet
argument_list|>
name|projUniqueKeySet
init|=
operator|new
name|HashSet
argument_list|<
name|BitSet
argument_list|>
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
name|mapInToOutPos
operator|.
name|put
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|projExpr
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mapInToOutPos
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// if there's no RexInputRef in the projected expressions
comment|// return empty set.
return|return
name|projUniqueKeySet
return|;
block|}
name|Set
argument_list|<
name|BitSet
argument_list|>
name|childUniqueKeySet
init|=
name|RelMetadataQuery
operator|.
name|getUniqueKeys
argument_list|(
name|rel
operator|.
name|getChild
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
if|if
condition|(
name|childUniqueKeySet
operator|!=
literal|null
condition|)
block|{
comment|// Now add to the projUniqueKeySet the child keys that are fully
comment|// projected.
for|for
control|(
name|BitSet
name|colMask
range|:
name|childUniqueKeySet
control|)
block|{
name|BitSet
name|tmpMask
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|boolean
name|completeKeyProjected
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|bit
range|:
name|BitSets
operator|.
name|toIter
argument_list|(
name|colMask
argument_list|)
control|)
block|{
if|if
condition|(
name|mapInToOutPos
operator|.
name|containsKey
argument_list|(
name|bit
argument_list|)
condition|)
block|{
name|tmpMask
operator|.
name|set
argument_list|(
name|mapInToOutPos
operator|.
name|get
argument_list|(
name|bit
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Skip the child unique key if part of it is not
comment|// projected.
name|completeKeyProjected
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|completeKeyProjected
condition|)
block|{
name|projUniqueKeySet
operator|.
name|add
argument_list|(
name|tmpMask
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|projUniqueKeySet
return|;
block|}
specifier|public
name|Set
argument_list|<
name|BitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|JoinRelBase
name|rel
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
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
name|Set
argument_list|<
name|BitSet
argument_list|>
name|retSet
init|=
operator|new
name|HashSet
argument_list|<
name|BitSet
argument_list|>
argument_list|()
decl_stmt|;
name|Set
argument_list|<
name|BitSet
argument_list|>
name|leftSet
init|=
name|RelMetadataQuery
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
name|BitSet
argument_list|>
name|rightSet
init|=
literal|null
decl_stmt|;
name|Set
argument_list|<
name|BitSet
argument_list|>
name|tmpRightSet
init|=
name|RelMetadataQuery
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
argument_list|<
name|BitSet
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|BitSet
name|colMask
range|:
name|tmpRightSet
control|)
block|{
name|BitSet
name|tmpMask
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|bit
range|:
name|BitSets
operator|.
name|toIter
argument_list|(
name|colMask
argument_list|)
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
name|BitSet
name|colMaskRight
range|:
name|rightSet
control|)
block|{
for|for
control|(
name|BitSet
name|colMaskLeft
range|:
name|leftSet
control|)
block|{
name|BitSet
name|colMaskConcat
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|colMaskConcat
operator|.
name|or
argument_list|(
name|colMaskLeft
argument_list|)
expr_stmt|;
name|colMaskConcat
operator|.
name|or
argument_list|(
name|colMaskRight
argument_list|)
expr_stmt|;
name|retSet
operator|.
name|add
argument_list|(
name|colMaskConcat
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
name|Boolean
name|leftUnique
init|=
name|RelMetadataQuery
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
name|Boolean
name|rightUnique
init|=
name|RelMetadataQuery
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
operator|(
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
operator|)
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
operator|(
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnRight
argument_list|()
operator|)
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
name|BitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|SemiJoinRel
name|rel
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
comment|// only return the unique keys from the LHS since a semijoin only
comment|// returns the LHS
return|return
name|RelMetadataQuery
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
name|Set
argument_list|<
name|BitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|AggregateRelBase
name|rel
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
name|Set
argument_list|<
name|BitSet
argument_list|>
name|retSet
init|=
operator|new
name|HashSet
argument_list|<
name|BitSet
argument_list|>
argument_list|()
decl_stmt|;
comment|// group by keys form a unique key
if|if
condition|(
name|rel
operator|.
name|getGroupCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|BitSet
name|groupKey
init|=
operator|new
name|BitSet
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
name|rel
operator|.
name|getGroupCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|groupKey
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|retSet
operator|.
name|add
argument_list|(
name|groupKey
argument_list|)
expr_stmt|;
block|}
return|return
name|retSet
return|;
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Set
argument_list|<
name|BitSet
argument_list|>
name|getUniqueKeys
parameter_list|(
name|RelNode
name|rel
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

begin_comment
comment|// End RelMdUniqueKeys.java
end_comment

end_unit

