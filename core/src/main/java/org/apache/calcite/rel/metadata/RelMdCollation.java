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
name|adapter
operator|.
name|enumerable
operator|.
name|EnumerableMergeJoin
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
name|RelOptTable
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
name|RelCollationTraitDef
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
name|SortExchange
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
name|RexCallBinding
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
name|sql
operator|.
name|validate
operator|.
name|SqlMonotonicity
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
name|Pair
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
name|LinkedListMultimap
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Multimap
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
name|Ordering
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
name|Sets
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
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_comment
comment|/**  * RelMdCollation supplies a default implementation of  * {@link org.apache.calcite.rel.metadata.RelMetadataQuery#collations}  * for the standard logical algebra.  */
end_comment

begin_class
specifier|public
class|class
name|RelMdCollation
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
name|COLLATIONS
operator|.
name|method
argument_list|,
operator|new
name|RelMdCollation
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|RelMdCollation
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/** Fallback method to deduce collations for any relational expression not    * handled by a more specific method.    *    *<p>{@link org.apache.calcite.rel.core.Union},    * {@link org.apache.calcite.rel.core.Intersect},    * {@link org.apache.calcite.rel.core.Minus},    * {@link org.apache.calcite.rel.core.Join},    * {@link org.apache.calcite.rel.core.SemiJoin},    * {@link org.apache.calcite.rel.core.Correlate}    * do not in general return sorted results    * (but implementations using particular algorithms may).    *    * @param rel Relational expression    * @return Relational expression's collations    */
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|Window
name|rel
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|window
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|,
name|rel
operator|.
name|groups
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|Filter
name|rel
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|rel
operator|.
name|getInput
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|TableScan
name|scan
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|table
argument_list|(
name|scan
operator|.
name|getTable
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|EnumerableMergeJoin
name|join
parameter_list|)
block|{
comment|// In general a join is not sorted. But a merge join preserves the sort
comment|// order of the left and right sides.
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|RelMdCollation
operator|.
name|mergeJoin
argument_list|(
name|join
operator|.
name|getLeft
argument_list|()
argument_list|,
name|join
operator|.
name|getRight
argument_list|()
argument_list|,
name|join
operator|.
name|getLeftKeys
argument_list|()
argument_list|,
name|join
operator|.
name|getRightKeys
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|Sort
name|sort
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|RelMdCollation
operator|.
name|sort
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|SortExchange
name|sort
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|RelMdCollation
operator|.
name|sort
argument_list|(
name|sort
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|Project
name|project
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|project
argument_list|(
name|project
operator|.
name|getInput
argument_list|()
argument_list|,
name|project
operator|.
name|getProjects
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|Values
name|values
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|values
argument_list|(
name|values
operator|.
name|getRowType
argument_list|()
argument_list|,
name|values
operator|.
name|getTuples
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|HepRelVertex
name|rel
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|rel
operator|.
name|getCurrentRel
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collations
parameter_list|(
name|RelSubset
name|rel
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|getTraits
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
argument_list|)
return|;
block|}
comment|// Helper methods
comment|/** Helper method to determine a    * {@link org.apache.calcite.rel.core.TableScan}'s collation. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|table
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
return|return
name|table
operator|.
name|getCollationList
argument_list|()
return|;
block|}
comment|/** Helper method to determine a    * {@link org.apache.calcite.rel.core.Sort}'s collation. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|sort
parameter_list|(
name|RelCollation
name|collation
parameter_list|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|(
name|collation
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * {@link org.apache.calcite.rel.core.Filter}'s collation. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|filter
parameter_list|(
name|RelNode
name|input
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * limit's collation. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|limit
parameter_list|(
name|RelNode
name|input
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * {@link org.apache.calcite.rel.core.Calc}'s collation. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|calc
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
return|return
name|program
operator|.
name|getCollations
argument_list|(
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|input
argument_list|)
argument_list|)
return|;
block|}
comment|/** Helper method to determine a {@link Project}'s collation. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|project
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|projects
parameter_list|)
block|{
specifier|final
name|SortedSet
argument_list|<
name|RelCollation
argument_list|>
name|collations
init|=
name|Sets
operator|.
name|newTreeSet
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|inputCollations
init|=
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|input
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputCollations
operator|==
literal|null
operator|||
name|inputCollations
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|final
name|Multimap
argument_list|<
name|Integer
argument_list|,
name|Integer
argument_list|>
name|targets
init|=
name|LinkedListMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Integer
argument_list|,
name|SqlMonotonicity
argument_list|>
name|targetsWithMonotonicity
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RexNode
argument_list|>
name|project
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|projects
argument_list|)
control|)
block|{
if|if
condition|(
name|project
operator|.
name|e
operator|instanceof
name|RexInputRef
condition|)
block|{
name|targets
operator|.
name|put
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|project
operator|.
name|e
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|,
name|project
operator|.
name|i
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|project
operator|.
name|e
operator|instanceof
name|RexCall
condition|)
block|{
specifier|final
name|RexCall
name|call
init|=
operator|(
name|RexCall
operator|)
name|project
operator|.
name|e
decl_stmt|;
specifier|final
name|RexCallBinding
name|binding
init|=
name|RexCallBinding
operator|.
name|create
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|call
argument_list|,
name|inputCollations
argument_list|)
decl_stmt|;
name|targetsWithMonotonicity
operator|.
name|put
argument_list|(
name|project
operator|.
name|i
argument_list|,
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getMonotonicity
argument_list|(
name|binding
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|fieldCollations
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|loop
label|:
for|for
control|(
name|RelCollation
name|ic
range|:
name|inputCollations
control|)
block|{
if|if
condition|(
name|ic
operator|.
name|getFieldCollations
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|fieldCollations
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|RelFieldCollation
name|ifc
range|:
name|ic
operator|.
name|getFieldCollations
argument_list|()
control|)
block|{
specifier|final
name|Collection
argument_list|<
name|Integer
argument_list|>
name|integers
init|=
name|targets
operator|.
name|get
argument_list|(
name|ifc
operator|.
name|getFieldIndex
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|integers
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue
name|loop
continue|;
comment|// cannot do this collation
block|}
name|fieldCollations
operator|.
name|add
argument_list|(
name|ifc
operator|.
name|copy
argument_list|(
name|integers
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
assert|assert
operator|!
name|fieldCollations
operator|.
name|isEmpty
argument_list|()
assert|;
name|collations
operator|.
name|add
argument_list|(
name|RelCollations
operator|.
name|of
argument_list|(
name|fieldCollations
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|RelFieldCollation
argument_list|>
name|fieldCollationsForRexCalls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Integer
argument_list|,
name|SqlMonotonicity
argument_list|>
name|entry
range|:
name|targetsWithMonotonicity
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|SqlMonotonicity
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|value
condition|)
block|{
case|case
name|NOT_MONOTONIC
case|:
case|case
name|CONSTANT
case|:
break|break;
default|default:
name|fieldCollationsForRexCalls
operator|.
name|add
argument_list|(
operator|new
name|RelFieldCollation
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|RelFieldCollation
operator|.
name|Direction
operator|.
name|of
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|fieldCollationsForRexCalls
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|collations
operator|.
name|add
argument_list|(
name|RelCollations
operator|.
name|of
argument_list|(
name|fieldCollationsForRexCalls
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|collations
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * {@link org.apache.calcite.rel.core.Window}'s collation.    *    *<p>A Window projects the fields of its input first, followed by the output    * from each of its windows. Assuming (quite reasonably) that the    * implementation does not re-order its input rows, then any collations of its    * input are preserved. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|window
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|ImmutableList
argument_list|<
name|Window
operator|.
name|Group
argument_list|>
name|groups
parameter_list|)
block|{
return|return
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|input
argument_list|)
return|;
block|}
comment|/** Helper method to determine a    * {@link org.apache.calcite.rel.core.Values}'s collation.    *    *<p>We actually under-report the collations. A Values with 0 or 1 rows - an    * edge case, but legitimate and very common - is ordered by every permutation    * of every subset of the columns.    *    *<p>So, our algorithm aims to:<ul>    *<li>produce at most N collations (where N is the number of columns);    *<li>make each collation as long as possible;    *<li>do not repeat combinations already emitted -    *       if we've emitted {@code (a, b)} do not later emit {@code (b, a)};    *<li>probe the actual values and make sure that each collation is    *      consistent with the data    *</ul>    *    *<p>So, for an empty Values with 4 columns, we would emit    * {@code (a, b, c, d), (b, c, d), (c, d), (d)}. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|values
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|ImmutableList
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|tuples
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RelCollation
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|rowType
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|RelFieldCollation
argument_list|,
name|Ordering
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|pairs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|outer
label|:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|n
condition|;
name|i
operator|++
control|)
block|{
name|pairs
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<
name|n
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|RelFieldCollation
name|fieldCollation
init|=
operator|new
name|RelFieldCollation
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|Ordering
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|comparator
init|=
name|comparator
argument_list|(
name|fieldCollation
argument_list|)
decl_stmt|;
name|Ordering
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|ordering
decl_stmt|;
if|if
condition|(
name|pairs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ordering
operator|=
name|comparator
expr_stmt|;
block|}
else|else
block|{
name|ordering
operator|=
name|Util
operator|.
name|last
argument_list|(
name|pairs
argument_list|)
operator|.
name|right
operator|.
name|compound
argument_list|(
name|comparator
argument_list|)
expr_stmt|;
block|}
name|pairs
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|fieldCollation
argument_list|,
name|ordering
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|ordering
operator|.
name|isOrdered
argument_list|(
name|tuples
argument_list|)
condition|)
block|{
if|if
condition|(
name|j
operator|==
name|i
condition|)
block|{
continue|continue
name|outer
continue|;
block|}
name|pairs
operator|.
name|remove
argument_list|(
name|pairs
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|pairs
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|RelCollations
operator|.
name|of
argument_list|(
name|Pair
operator|.
name|left
argument_list|(
name|pairs
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
specifier|private
specifier|static
name|Ordering
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|comparator
parameter_list|(
name|RelFieldCollation
name|fieldCollation
parameter_list|)
block|{
specifier|final
name|int
name|nullComparison
init|=
name|fieldCollation
operator|.
name|nullDirection
operator|.
name|nullComparison
decl_stmt|;
specifier|final
name|int
name|x
init|=
name|fieldCollation
operator|.
name|getFieldIndex
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|fieldCollation
operator|.
name|direction
condition|)
block|{
case|case
name|ASCENDING
case|:
return|return
operator|new
name|Ordering
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|o1
parameter_list|,
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|o2
parameter_list|)
block|{
specifier|final
name|Comparable
name|c1
init|=
name|o1
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|Comparable
name|c2
init|=
name|o2
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
return|return
name|RelFieldCollation
operator|.
name|compare
argument_list|(
name|c1
argument_list|,
name|c2
argument_list|,
name|nullComparison
argument_list|)
return|;
block|}
block|}
return|;
default|default:
return|return
operator|new
name|Ordering
argument_list|<
name|List
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|o1
parameter_list|,
name|List
argument_list|<
name|RexLiteral
argument_list|>
name|o2
parameter_list|)
block|{
specifier|final
name|Comparable
name|c1
init|=
name|o1
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|Comparable
name|c2
init|=
name|o2
operator|.
name|get
argument_list|(
name|x
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
return|return
name|RelFieldCollation
operator|.
name|compare
argument_list|(
name|c2
argument_list|,
name|c1
argument_list|,
operator|-
name|nullComparison
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
comment|/** Helper method to determine a {@link Join}'s collation assuming that it    * uses a merge-join algorithm.    *    *<p>If the inputs are sorted on other keys<em>in addition to</em> the join    * key, the result preserves those collations too. */
specifier|public
specifier|static
name|List
argument_list|<
name|RelCollation
argument_list|>
name|mergeJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|ImmutableIntList
name|leftKeys
parameter_list|,
name|ImmutableIntList
name|rightKeys
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RelCollation
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|leftCollations
init|=
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|left
argument_list|)
decl_stmt|;
assert|assert
name|RelCollations
operator|.
name|contains
argument_list|(
name|leftCollations
argument_list|,
name|leftKeys
argument_list|)
operator|:
literal|"cannot merge join: left input is not sorted on left keys"
assert|;
name|builder
operator|.
name|addAll
argument_list|(
name|leftCollations
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|rightCollations
init|=
name|RelMetadataQuery
operator|.
name|collations
argument_list|(
name|right
argument_list|)
decl_stmt|;
assert|assert
name|RelCollations
operator|.
name|contains
argument_list|(
name|rightCollations
argument_list|,
name|rightKeys
argument_list|)
operator|:
literal|"cannot merge join: right input is not sorted on right keys"
assert|;
specifier|final
name|int
name|leftFieldCount
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
name|RelCollation
name|collation
range|:
name|rightCollations
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|RelCollations
operator|.
name|shift
argument_list|(
name|collation
argument_list|,
name|leftFieldCount
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelMdCollation.java
end_comment

end_unit

