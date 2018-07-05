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
name|plan
operator|.
name|hep
operator|.
name|HepRelVertex
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
name|volcano
operator|.
name|RelSubset
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
name|Converter
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
name|Exchange
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
name|SetOp
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|RexCall
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
name|RexLiteral
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
name|sql
operator|.
name|fun
operator|.
name|SqlStdOperatorTable
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
name|Pair
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
name|Set
import|;
end_import

begin_comment
comment|/**  * RelMdColumnUniqueness supplies a default implementation of  * {@link RelMetadataQuery#areColumnsUnique} for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdColumnUniqueness
implements|implements
name|MetadataHandler
argument_list|<
name|BuiltInMetadata
operator|.
name|ColumnUniqueness
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
name|BuiltInMethod
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
name|MetadataDef
argument_list|<
name|BuiltInMetadata
operator|.
name|ColumnUniqueness
argument_list|>
name|getDef
parameter_list|()
block|{
return|return
name|BuiltInMetadata
operator|.
name|ColumnUniqueness
operator|.
name|DEF
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|TableScan
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|rel
operator|.
name|getTable
argument_list|()
operator|.
name|isKey
argument_list|(
name|columns
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|Filter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
comment|/** Catch-all implementation for    * {@link BuiltInMetadata.ColumnUniqueness#areColumnsUnique(ImmutableBitSet, boolean)},    * invoked using reflection, for any relational expression not    * handled by a more specific method.    *    * @param rel Relational expression    * @param mq Metadata query    * @param columns column mask representing the subset of columns for which    *                uniqueness will be determined    * @param ignoreNulls if true, ignore null values when determining column    *                    uniqueness    * @return whether the columns are unique, or    * null if not enough information is available to make that determination    *    * @see org.apache.calcite.rel.metadata.RelMetadataQuery#areColumnsUnique(RelNode, ImmutableBitSet, boolean)    */
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
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
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|SetOp
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
comment|// If not ALL then the rows are distinct.
comment|// Therefore the set of all columns is a key.
return|return
operator|!
name|rel
operator|.
name|all
operator|&&
name|columns
operator|.
name|nextClearBit
argument_list|(
literal|0
argument_list|)
operator|>=
name|rel
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|Intersect
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
if|if
condition|(
name|areColumnsUnique
argument_list|(
operator|(
name|SetOp
operator|)
name|rel
argument_list|,
name|mq
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
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
name|Boolean
name|b
init|=
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|input
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
if|if
condition|(
name|b
operator|!=
literal|null
operator|&&
name|b
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
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|Minus
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
if|if
condition|(
name|areColumnsUnique
argument_list|(
operator|(
name|SetOp
operator|)
name|rel
argument_list|,
name|mq
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getInput
argument_list|(
literal|0
argument_list|)
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
name|Sort
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getInput
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
name|Exchange
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getInput
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
name|Correlate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
switch|switch
condition|(
name|rel
operator|.
name|getJoinType
argument_list|()
condition|)
block|{
case|case
name|ANTI
case|:
case|case
name|SEMI
case|:
return|return
name|mq
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
case|case
name|LEFT
case|:
case|case
name|INNER
case|:
specifier|final
name|Pair
argument_list|<
name|ImmutableBitSet
argument_list|,
name|ImmutableBitSet
argument_list|>
name|leftAndRightColumns
init|=
name|splitLeftAndRightColumns
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|columns
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|leftColumns
init|=
name|leftAndRightColumns
operator|.
name|left
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|rightColumns
init|=
name|leftAndRightColumns
operator|.
name|right
decl_stmt|;
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
if|if
condition|(
name|leftColumns
operator|.
name|cardinality
argument_list|()
operator|>
literal|0
operator|&&
name|rightColumns
operator|.
name|cardinality
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Boolean
name|leftUnique
init|=
name|mq
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
name|mq
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
name|leftUnique
operator|==
literal|null
operator|||
name|rightUnique
operator|==
literal|null
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
else|else
block|{
return|return
literal|null
return|;
block|}
default|default:
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown join type "
operator|+
name|rel
operator|.
name|getJoinType
argument_list|()
operator|+
literal|" for correlate relation "
operator|+
name|rel
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|Project
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
comment|// LogicalProject maps a set of rows to a different set;
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
name|ImmutableBitSet
operator|.
name|Builder
name|childColumns
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
name|columns
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
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|childColumns
operator|.
name|build
argument_list|()
argument_list|,
name|ignoreNulls
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|Join
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
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
specifier|final
name|Pair
argument_list|<
name|ImmutableBitSet
argument_list|,
name|ImmutableBitSet
argument_list|>
name|leftAndRightColumns
init|=
name|splitLeftAndRightColumns
argument_list|(
name|rel
operator|.
name|getLeft
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|columns
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|leftColumns
init|=
name|leftAndRightColumns
operator|.
name|left
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|rightColumns
init|=
name|leftAndRightColumns
operator|.
name|right
decl_stmt|;
comment|// If the original column mask contains columns from both the left and
comment|// right hand side, then the columns are unique if and only if they're
comment|// unique for their respective join inputs
name|Boolean
name|leftUnique
init|=
name|mq
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
name|mq
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
name|SemiJoin
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
comment|// only return the unique keys from the LHS since a semijoin only
comment|// returns the LHS
return|return
name|mq
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
name|Aggregate
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
comment|// group by keys form a unique key
name|ImmutableBitSet
name|groupKey
init|=
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|rel
operator|.
name|getGroupCount
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|columns
operator|.
name|contains
argument_list|(
name|groupKey
argument_list|)
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|Values
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
if|if
condition|(
name|rel
operator|.
name|tuples
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|Set
argument_list|<
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|set
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
name|tuple
range|:
name|rel
operator|.
name|tuples
control|)
block|{
for|for
control|(
name|int
name|column
range|:
name|columns
control|)
block|{
specifier|final
name|RexLiteral
name|literal
init|=
name|tuple
operator|.
name|get
argument_list|(
name|column
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|literal
operator|.
name|isNull
argument_list|()
condition|?
name|NullSentinel
operator|.
name|INSTANCE
else|:
name|literal
operator|.
name|getValueAs
argument_list|(
name|Comparable
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|set
operator|.
name|add
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|values
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|Boolean
name|areColumnsUnique
parameter_list|(
name|Converter
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getInput
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
name|HepRelVertex
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
return|return
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|rel
operator|.
name|getCurrentRel
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
name|RelSubset
name|rel
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|,
name|boolean
name|ignoreNulls
parameter_list|)
block|{
name|int
name|nullCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelNode
name|rel2
range|:
name|rel
operator|.
name|getRels
argument_list|()
control|)
block|{
if|if
condition|(
name|rel2
operator|instanceof
name|Aggregate
operator|||
name|rel2
operator|instanceof
name|Filter
operator|||
name|rel2
operator|instanceof
name|Values
operator|||
name|rel2
operator|instanceof
name|TableScan
operator|||
name|simplyProjects
argument_list|(
name|rel2
argument_list|,
name|columns
argument_list|)
condition|)
block|{
try|try
block|{
specifier|final
name|Boolean
name|unique
init|=
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|rel2
argument_list|,
name|columns
argument_list|,
name|ignoreNulls
argument_list|)
decl_stmt|;
if|if
condition|(
name|unique
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|unique
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
else|else
block|{
operator|++
name|nullCount
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|CyclicMetadataException
name|e
parameter_list|)
block|{
comment|// Ignore this relational expression; there will be non-cyclic ones
comment|// in this set.
block|}
block|}
block|}
return|return
name|nullCount
operator|==
literal|0
condition|?
literal|false
else|:
literal|null
return|;
block|}
specifier|private
name|boolean
name|simplyProjects
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|rel
operator|instanceof
name|Project
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Project
name|project
init|=
operator|(
name|Project
operator|)
name|rel
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
init|=
name|project
operator|.
name|getProjects
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|column
range|:
name|columns
control|)
block|{
if|if
condition|(
name|column
operator|>=
name|projects
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|projects
operator|.
name|get
argument_list|(
name|column
argument_list|)
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|RexInputRef
name|ref
init|=
operator|(
name|RexInputRef
operator|)
name|projects
operator|.
name|get
argument_list|(
name|column
argument_list|)
decl_stmt|;
if|if
condition|(
name|ref
operator|.
name|getIndex
argument_list|()
operator|!=
name|column
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/** Splits a column set between left and right sets. */
specifier|private
specifier|static
name|Pair
argument_list|<
name|ImmutableBitSet
argument_list|,
name|ImmutableBitSet
argument_list|>
name|splitLeftAndRightColumns
parameter_list|(
name|int
name|leftCount
parameter_list|,
specifier|final
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
name|ImmutableBitSet
operator|.
name|Builder
name|leftBuilder
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
name|ImmutableBitSet
operator|.
name|Builder
name|rightBuilder
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
name|columns
control|)
block|{
if|if
condition|(
name|bit
operator|<
name|leftCount
condition|)
block|{
name|leftBuilder
operator|.
name|set
argument_list|(
name|bit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|rightBuilder
operator|.
name|set
argument_list|(
name|bit
operator|-
name|leftCount
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Pair
operator|.
name|of
argument_list|(
name|leftBuilder
operator|.
name|build
argument_list|()
argument_list|,
name|rightBuilder
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdColumnUniqueness.java
end_comment

end_unit

