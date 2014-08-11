begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|reltype
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
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|fun
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
comment|/**  * RelMdColumnUniqueness supplies a default implementation of {@link  * RelMetadataQuery#areColumnsUnique} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdColumnUniqueness
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
name|COLUMN_UNIQUENESS
operator|.
name|method
argument_list|,
operator|new
name|RelMdColumnUniqueness
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdColumnUniqueness
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|FilterRelBase
name|rel
parameter_list|,
name|BitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getChild
argument_list|()
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|SortRel
name|rel
parameter_list|,
name|BitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getChild
argument_list|()
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|CorrelatorRel
name|rel
parameter_list|,
name|BitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|ProjectRelBase
name|rel
parameter_list|,
name|BitSet
name|columns
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
comment|// Also need to map the input column set to the corresponding child
comment|// references
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
name|BitSet
name|childColumns
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
name|columns
argument_list|)
control|)
block|{
name|RexNode
name|projExpr
init|=
name|projExprs
operator|.
name|get
argument_list|(
name|bit
argument_list|)
decl_stmt|;
if|if
condition|(
name|projExpr
operator|instanceof
name|RexInputRef
condition|)
block|{
name|childColumns
operator|.
name|set
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
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|projExpr
operator|instanceof
name|RexCall
operator|&&
name|ignoreNulls
condition|)
block|{
comment|// If the expression is a cast such that the types are the same
comment|// except for the nullability, then if we're ignoring nulls,
comment|// it doesn't matter whether the underlying column reference
comment|// is nullable.  Check that the types are the same by making a
comment|// nullable copy of both types and then comparing them.
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|projExpr
decl_stmt|;
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|!=
name|SqlStdOperatorTable
operator|.
name|CAST
condition|)
block|{
continue|continue;
block|}
name|RexNode
name|castOperand
init|=
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|castOperand
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
continue|continue;
block|}
name|RelDataTypeFactory
name|typeFactory
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|RelDataType
name|castType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|projExpr
operator|.
name|getType
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|RelDataType
name|origType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|castOperand
operator|.
name|getType
argument_list|()
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|castType
operator|.
name|equals
argument_list|(
name|origType
argument_list|)
condition|)
block|{
name|childColumns
operator|.
name|set
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|castOperand
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// If the expression will not influence uniqueness of the
comment|// projection, then skip it.
continue|continue;
block|}
block|}
comment|// If no columns can affect uniqueness, then return unknown
if|if
condition|(
name|childColumns
operator|.
name|cardinality
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|RelMetadataQuery
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getChild
argument_list|()
argument_list|,
name|childColumns
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|JoinRelBase
name|rel
parameter_list|,
name|BitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
if|if
condition|(
name|columns
operator|.
name|cardinality
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
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
comment|// Divide up the input column mask into column masks for the left and
comment|// right sides of the join
name|BitSet
name|leftColumns
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|BitSet
name|rightColumns
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
name|int
name|nLeftColumns
init|=
name|left
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
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
name|columns
argument_list|)
control|)
block|{
if|if
condition|(
name|bit
operator|<
name|nLeftColumns
condition|)
block|{
name|leftColumns
operator|.
name|set
argument_list|(
name|bit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rightColumns
operator|.
name|set
argument_list|(
name|bit
operator|-
name|nLeftColumns
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If the original column mask contains columns from both the left and
comment|// right hand side, then the columns are unique if and only if they're
comment|// unique for their respective join inputs
name|Boolean
name|leftUnique
init|=
name|RelMetadataQuery
operator|.
name|areColumnsUnique
argument_list|(
name|left
argument_list|,
name|leftColumns
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
name|rightColumns
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|leftColumns
operator|.
name|cardinality
argument_list|()
operator|>
literal|0
operator|)
operator|&&
operator|(
name|rightColumns
operator|.
name|cardinality
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
if|if
condition|(
operator|(
name|leftUnique
operator|==
literal|null
operator|)
operator|||
operator|(
name|rightUnique
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|leftUnique
operator|&&
name|rightUnique
return|;
block|}
block|}
comment|// If we're only trying to determine uniqueness for columns that
comment|// originate from one join input, then determine if the equijoin
comment|// columns from the other join input are unique.  If they are, then
comment|// the columns are unique for the entire join if they're unique for
comment|// the corresponding join input, provided that input is not null
comment|// generating.
specifier|final
name|JoinInfo
name|joinInfo
init|=
name|rel
operator|.
name|analyzeCondition
argument_list|()
decl_stmt|;
if|if
condition|(
name|leftColumns
operator|.
name|cardinality
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnLeft
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Boolean
name|rightJoinColsUnique
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
if|if
condition|(
operator|(
name|rightJoinColsUnique
operator|==
literal|null
operator|)
operator|||
operator|(
name|leftUnique
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|rightJoinColsUnique
operator|&&
name|leftUnique
return|;
block|}
if|else if
condition|(
name|rightColumns
operator|.
name|cardinality
argument_list|()
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|rel
operator|.
name|getJoinType
argument_list|()
operator|.
name|generatesNullsOnRight
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Boolean
name|leftJoinColsUnique
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
if|if
condition|(
operator|(
name|leftJoinColsUnique
operator|==
literal|null
operator|)
operator|||
operator|(
name|rightUnique
operator|==
literal|null
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|leftJoinColsUnique
operator|&&
name|rightUnique
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|()
throw|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|SemiJoinRel
name|rel
parameter_list|,
name|BitSet
name|columns
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
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|AggregateRelBase
name|rel
parameter_list|,
name|BitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
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
return|return
name|BitSets
operator|.
name|contains
argument_list|(
name|columns
argument_list|,
name|groupKey
argument_list|)
return|;
block|}
else|else
block|{
comment|// interpret an empty set as asking whether the aggregation is full
comment|// table (in which case it returns at most one row);
comment|// TODO jvs 1-Sept-2008:  apply this convention consistently
comment|// to other relational expressions, as well as to
comment|// RelMetadataQuery.getUniqueKeys
return|return
name|columns
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
comment|// Catch-all rule when none of the others apply.
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|BitSet
name|columns
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
comment|// End RelMdColumnUniqueness.java
end_comment

end_unit

