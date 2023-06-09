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
name|profile
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
name|materialize
operator|.
name|Lattice
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
name|NullSentinel
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
name|runtime
operator|.
name|FlatLists
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
name|PartiallyOrderedSet
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
name|ImmutableSortedSet
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
name|Iterables
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
name|initialization
operator|.
name|qual
operator|.
name|UnknownInitialization
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
name|RequiresNonNull
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
name|BitSet
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
name|Collections
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
name|NavigableSet
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
comment|/**  * Basic implementation of {@link Profiler}.  */
end_comment

begin_class
specifier|public
class|class
name|SimpleProfiler
implements|implements
name|Profiler
block|{
annotation|@
name|Override
specifier|public
name|Profile
name|profile
parameter_list|(
name|Iterable
argument_list|<
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|rows
parameter_list|,
specifier|final
name|List
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|,
name|Collection
argument_list|<
name|ImmutableBitSet
argument_list|>
name|initialGroups
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|initialGroups
argument_list|)
expr_stmt|;
comment|// this profiler ignores initial groups
return|return
operator|new
name|Run
argument_list|(
name|columns
argument_list|)
operator|.
name|profile
argument_list|(
name|rows
argument_list|)
return|;
block|}
comment|/** Returns a measure of how much an actual value differs from expected.    * The formula is {@code abs(expected - actual) / (expected + actual)}.    *    *<p>Examples:<ul>    *<li>surprise(e, a) is always between 0 and 1;    *<li>surprise(e, a) is 0 if e = a;    *<li>surprise(e, 0) is 1 if e&gt; 0;    *<li>surprise(0, a) is 1 if a&gt; 0;    *<li>surprise(5, 0) is 100%;    *<li>surprise(5, 3) is 25%;    *<li>surprise(5, 4) is 11%;    *<li>surprise(5, 5) is 0%;    *<li>surprise(5, 6) is 9%;    *<li>surprise(5, 16) is 52%;    *<li>surprise(5, 100) is 90%;    *</ul>    *    * @param expected Expected value    * @param actual Actual value    * @return Measure of how much expected deviates from actual    */
specifier|public
specifier|static
name|double
name|surprise
parameter_list|(
name|double
name|expected
parameter_list|,
name|double
name|actual
parameter_list|)
block|{
if|if
condition|(
name|expected
operator|==
name|actual
condition|)
block|{
return|return
literal|0d
return|;
block|}
specifier|final
name|double
name|sum
init|=
name|expected
operator|+
name|actual
decl_stmt|;
if|if
condition|(
name|sum
operator|<=
literal|0d
condition|)
block|{
return|return
literal|1d
return|;
block|}
return|return
name|Math
operator|.
name|abs
argument_list|(
name|expected
operator|-
name|actual
argument_list|)
operator|/
name|sum
return|;
block|}
comment|/** A run of the profiler. */
specifier|static
class|class
name|Run
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Column
argument_list|>
name|columns
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Space
argument_list|>
name|spaces
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|Space
argument_list|>
name|singletonSpaces
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Statistic
argument_list|>
name|statistics
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|PartiallyOrderedSet
operator|.
name|Ordering
argument_list|<
name|Space
argument_list|>
name|ordering
init|=
parameter_list|(
name|e1
parameter_list|,
name|e2
parameter_list|)
lambda|->
name|e2
operator|.
name|columnOrdinals
operator|.
name|contains
argument_list|(
name|e1
operator|.
name|columnOrdinals
argument_list|)
decl_stmt|;
specifier|final
name|PartiallyOrderedSet
argument_list|<
name|Space
argument_list|>
name|results
init|=
operator|new
name|PartiallyOrderedSet
argument_list|<>
argument_list|(
name|ordering
argument_list|)
decl_stmt|;
specifier|final
name|PartiallyOrderedSet
argument_list|<
name|Space
argument_list|>
name|keyResults
init|=
operator|new
name|PartiallyOrderedSet
argument_list|<>
argument_list|(
name|ordering
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|keyOrdinalLists
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Run
parameter_list|(
specifier|final
name|List
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|)
block|{
for|for
control|(
name|Ord
argument_list|<
name|Column
argument_list|>
name|column
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|columns
argument_list|)
control|)
block|{
if|if
condition|(
name|column
operator|.
name|e
operator|.
name|ordinal
operator|!=
name|column
operator|.
name|i
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
block|}
name|this
operator|.
name|columns
operator|=
name|columns
expr_stmt|;
name|this
operator|.
name|singletonSpaces
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Collections
operator|.
name|nCopies
argument_list|(
name|columns
operator|.
name|size
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|ImmutableBitSet
name|ordinals
range|:
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|columns
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|powerSet
argument_list|()
control|)
block|{
specifier|final
name|Space
name|space
init|=
operator|new
name|Space
argument_list|(
name|ordinals
argument_list|,
name|toColumns
argument_list|(
name|ordinals
argument_list|)
argument_list|)
decl_stmt|;
name|spaces
operator|.
name|add
argument_list|(
name|space
argument_list|)
expr_stmt|;
if|if
condition|(
name|ordinals
operator|.
name|cardinality
argument_list|()
operator|==
literal|1
condition|)
block|{
name|singletonSpaces
operator|.
name|set
argument_list|(
name|ordinals
operator|.
name|nth
argument_list|(
literal|0
argument_list|)
argument_list|,
name|space
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|Profile
name|profile
parameter_list|(
name|Iterable
argument_list|<
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|rows
parameter_list|)
block|{
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
name|int
name|rowCount
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|List
argument_list|<
name|Comparable
argument_list|>
name|row
range|:
name|rows
control|)
block|{
operator|++
name|rowCount
expr_stmt|;
name|joint
label|:
for|for
control|(
name|Space
name|space
range|:
name|spaces
control|)
block|{
name|values
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
name|Column
name|column
range|:
name|space
operator|.
name|columns
control|)
block|{
specifier|final
name|Comparable
name|value
init|=
name|row
operator|.
name|get
argument_list|(
name|column
operator|.
name|ordinal
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|==
name|NullSentinel
operator|.
name|INSTANCE
condition|)
block|{
name|space
operator|.
name|nullCount
operator|++
expr_stmt|;
continue|continue
name|joint
continue|;
block|}
block|}
name|space
operator|.
name|values
operator|.
name|add
argument_list|(
name|FlatLists
operator|.
name|ofComparable
argument_list|(
name|values
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Populate unique keys
comment|// If [x, y] is a key,
comment|// then [x, y, z] is a key but not intersecting,
comment|// and [x, y] => [a] is a functional dependency but not interesting,
comment|// and [x, y, z] is not an interesting distribution.
specifier|final
name|Map
argument_list|<
name|ImmutableBitSet
argument_list|,
name|Distribution
argument_list|>
name|distributions
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Space
name|space
range|:
name|spaces
control|)
block|{
if|if
condition|(
name|space
operator|.
name|values
operator|.
name|size
argument_list|()
operator|==
name|rowCount
operator|&&
operator|!
name|containsKey
argument_list|(
name|space
operator|.
name|columnOrdinals
argument_list|,
literal|false
argument_list|)
condition|)
block|{
comment|// We have discovered a new key.
comment|// It is not an existing key or a super-set of a key.
name|statistics
operator|.
name|add
argument_list|(
operator|new
name|Unique
argument_list|(
name|space
operator|.
name|columns
argument_list|)
argument_list|)
expr_stmt|;
name|space
operator|.
name|unique
operator|=
literal|true
expr_stmt|;
name|keyOrdinalLists
operator|.
name|add
argument_list|(
name|space
operator|.
name|columnOrdinals
argument_list|)
expr_stmt|;
block|}
name|int
name|nonMinimal
init|=
literal|0
decl_stmt|;
name|dependents
label|:
for|for
control|(
name|Space
name|s
range|:
name|results
operator|.
name|getDescendants
argument_list|(
name|space
argument_list|)
control|)
block|{
if|if
condition|(
name|s
operator|.
name|cardinality
argument_list|()
operator|==
name|space
operator|.
name|cardinality
argument_list|()
condition|)
block|{
comment|// We have discovered a sub-set that has the same cardinality.
comment|// The column(s) that are not in common are functionally
comment|// dependent.
specifier|final
name|ImmutableBitSet
name|dependents
init|=
name|space
operator|.
name|columnOrdinals
operator|.
name|except
argument_list|(
name|s
operator|.
name|columnOrdinals
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
range|:
name|s
operator|.
name|columnOrdinals
control|)
block|{
specifier|final
name|Space
name|s1
init|=
name|singletonSpaces
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableBitSet
name|rest
init|=
name|s
operator|.
name|columnOrdinals
operator|.
name|clear
argument_list|(
name|i
argument_list|)
decl_stmt|;
for|for
control|(
name|ImmutableBitSet
name|dependent
range|:
name|requireNonNull
argument_list|(
name|s1
argument_list|,
literal|"s1"
argument_list|)
operator|.
name|dependents
control|)
block|{
if|if
condition|(
name|rest
operator|.
name|contains
argument_list|(
name|dependent
argument_list|)
condition|)
block|{
comment|// The "key" of this functional dependency is not minimal.
comment|// For instance, if we know that
comment|//   (a) -> x
comment|// then
comment|//   (a, b, x) -> y
comment|// is not minimal; we could say the same with a smaller key:
comment|//   (a, b) -> y
operator|++
name|nonMinimal
expr_stmt|;
continue|continue
name|dependents
continue|;
block|}
block|}
block|}
for|for
control|(
name|int
name|dependent
range|:
name|dependents
control|)
block|{
specifier|final
name|Space
name|s1
init|=
name|singletonSpaces
operator|.
name|get
argument_list|(
name|dependent
argument_list|)
decl_stmt|;
for|for
control|(
name|ImmutableBitSet
name|d
range|:
name|requireNonNull
argument_list|(
name|s1
argument_list|,
literal|"s1"
argument_list|)
operator|.
name|dependents
control|)
block|{
if|if
condition|(
name|s
operator|.
name|columnOrdinals
operator|.
name|contains
argument_list|(
name|d
argument_list|)
condition|)
block|{
operator|++
name|nonMinimal
expr_stmt|;
continue|continue
name|dependents
continue|;
block|}
block|}
block|}
name|space
operator|.
name|dependencies
operator|.
name|or
argument_list|(
name|dependents
operator|.
name|toBitSet
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|d
range|:
name|dependents
control|)
block|{
name|Space
name|spaceD
init|=
name|requireNonNull
argument_list|(
name|singletonSpaces
operator|.
name|get
argument_list|(
name|d
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"singletonSpaces.get(d) is null for "
operator|+
name|d
argument_list|)
decl_stmt|;
name|spaceD
operator|.
name|dependents
operator|.
name|add
argument_list|(
name|s
operator|.
name|columnOrdinals
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|int
name|nullCount
decl_stmt|;
specifier|final
name|SortedSet
argument_list|<
name|Comparable
argument_list|>
name|valueSet
decl_stmt|;
if|if
condition|(
name|space
operator|.
name|columns
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|nullCount
operator|=
name|space
operator|.
name|nullCount
expr_stmt|;
name|valueSet
operator|=
name|ImmutableSortedSet
operator|.
name|copyOf
argument_list|(
name|Util
operator|.
name|transform
argument_list|(
name|space
operator|.
name|values
argument_list|,
name|Iterables
operator|::
name|getOnlyElement
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nullCount
operator|=
operator|-
literal|1
expr_stmt|;
name|valueSet
operator|=
literal|null
expr_stmt|;
block|}
name|double
name|expectedCardinality
decl_stmt|;
specifier|final
name|double
name|cardinality
init|=
name|space
operator|.
name|cardinality
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|space
operator|.
name|columns
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
name|expectedCardinality
operator|=
literal|1d
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|expectedCardinality
operator|=
name|rowCount
expr_stmt|;
break|break;
default|default:
name|expectedCardinality
operator|=
name|rowCount
expr_stmt|;
for|for
control|(
name|Column
name|column
range|:
name|space
operator|.
name|columns
control|)
block|{
specifier|final
name|Distribution
name|d1
init|=
name|distributions
operator|.
name|get
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|column
operator|.
name|ordinal
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Distribution
name|d2
init|=
name|distributions
operator|.
name|get
argument_list|(
name|space
operator|.
name|columnOrdinals
operator|.
name|clear
argument_list|(
name|column
operator|.
name|ordinal
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|double
name|d
init|=
name|Lattice
operator|.
name|getRowCount
argument_list|(
name|rowCount
argument_list|,
name|requireNonNull
argument_list|(
name|d1
argument_list|,
literal|"d1"
argument_list|)
operator|.
name|cardinality
argument_list|,
name|requireNonNull
argument_list|(
name|d2
argument_list|,
literal|"d2"
argument_list|)
operator|.
name|cardinality
argument_list|)
decl_stmt|;
name|expectedCardinality
operator|=
name|Math
operator|.
name|min
argument_list|(
name|expectedCardinality
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|boolean
name|minimal
init|=
name|nonMinimal
operator|==
literal|0
operator|&&
operator|!
name|space
operator|.
name|unique
operator|&&
operator|!
name|containsKey
argument_list|(
name|space
operator|.
name|columnOrdinals
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|Distribution
name|distribution
init|=
operator|new
name|Distribution
argument_list|(
name|space
operator|.
name|columns
argument_list|,
name|valueSet
argument_list|,
name|cardinality
argument_list|,
name|nullCount
argument_list|,
name|expectedCardinality
argument_list|,
name|minimal
argument_list|)
decl_stmt|;
name|statistics
operator|.
name|add
argument_list|(
name|distribution
argument_list|)
expr_stmt|;
name|distributions
operator|.
name|put
argument_list|(
name|space
operator|.
name|columnOrdinals
argument_list|,
name|distribution
argument_list|)
expr_stmt|;
if|if
condition|(
name|distribution
operator|.
name|minimal
condition|)
block|{
name|results
operator|.
name|add
argument_list|(
name|space
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Space
name|s
range|:
name|singletonSpaces
control|)
block|{
for|for
control|(
name|ImmutableBitSet
name|dependent
range|:
name|requireNonNull
argument_list|(
name|s
argument_list|,
literal|"s"
argument_list|)
operator|.
name|dependents
control|)
block|{
if|if
condition|(
operator|!
name|containsKey
argument_list|(
name|dependent
argument_list|,
literal|false
argument_list|)
operator|&&
operator|!
name|hasNull
argument_list|(
name|dependent
argument_list|)
condition|)
block|{
name|statistics
operator|.
name|add
argument_list|(
operator|new
name|FunctionalDependency
argument_list|(
name|toColumns
argument_list|(
name|dependent
argument_list|)
argument_list|,
name|Iterables
operator|.
name|getOnlyElement
argument_list|(
name|s
operator|.
name|columns
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
operator|new
name|Profile
argument_list|(
name|columns
argument_list|,
operator|new
name|RowCount
argument_list|(
name|rowCount
argument_list|)
argument_list|,
name|Iterables
operator|.
name|filter
argument_list|(
name|statistics
argument_list|,
name|FunctionalDependency
operator|.
name|class
argument_list|)
argument_list|,
name|Iterables
operator|.
name|filter
argument_list|(
name|statistics
argument_list|,
name|Distribution
operator|.
name|class
argument_list|)
argument_list|,
name|Iterables
operator|.
name|filter
argument_list|(
name|statistics
argument_list|,
name|Unique
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns whether a set of column ordinals      * matches or contains a unique key.      * If {@code strict}, it must contain a unique key. */
specifier|private
name|boolean
name|containsKey
parameter_list|(
name|ImmutableBitSet
name|ordinals
parameter_list|,
name|boolean
name|strict
parameter_list|)
block|{
for|for
control|(
name|ImmutableBitSet
name|keyOrdinals
range|:
name|keyOrdinalLists
control|)
block|{
if|if
condition|(
name|ordinals
operator|.
name|contains
argument_list|(
name|keyOrdinals
argument_list|)
condition|)
block|{
return|return
operator|!
operator|(
name|strict
operator|&&
name|keyOrdinals
operator|.
name|equals
argument_list|(
name|ordinals
argument_list|)
operator|)
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|boolean
name|hasNull
parameter_list|(
name|ImmutableBitSet
name|columnOrdinals
parameter_list|)
block|{
for|for
control|(
name|Integer
name|columnOrdinal
range|:
name|columnOrdinals
control|)
block|{
name|Space
name|space
init|=
name|requireNonNull
argument_list|(
name|singletonSpaces
operator|.
name|get
argument_list|(
name|columnOrdinal
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"singletonSpaces.get(columnOrdinal) is null for "
operator|+
name|columnOrdinal
argument_list|)
decl_stmt|;
if|if
condition|(
name|space
operator|.
name|nullCount
operator|>
literal|0
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
annotation|@
name|RequiresNonNull
argument_list|(
literal|"columns"
argument_list|)
specifier|private
name|ImmutableSortedSet
argument_list|<
name|Column
argument_list|>
name|toColumns
parameter_list|(
annotation|@
name|UnknownInitialization
name|Run
name|this
parameter_list|,
name|Iterable
argument_list|<
name|Integer
argument_list|>
name|ordinals
parameter_list|)
block|{
comment|//noinspection Convert2MethodRef
return|return
name|ImmutableSortedSet
operator|.
name|copyOf
argument_list|(
name|Util
operator|.
name|transform
argument_list|(
name|ordinals
argument_list|,
name|idx
lambda|->
name|columns
operator|.
name|get
argument_list|(
name|idx
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** Work space for a particular combination of columns. */
specifier|static
class|class
name|Space
implements|implements
name|Comparable
argument_list|<
name|Space
argument_list|>
block|{
specifier|final
name|ImmutableBitSet
name|columnOrdinals
decl_stmt|;
specifier|final
name|ImmutableSortedSet
argument_list|<
name|Column
argument_list|>
name|columns
decl_stmt|;
name|int
name|nullCount
decl_stmt|;
specifier|final
name|NavigableSet
argument_list|<
name|FlatLists
operator|.
name|ComparableList
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|values
init|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
decl_stmt|;
name|boolean
name|unique
decl_stmt|;
specifier|final
name|BitSet
name|dependencies
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
specifier|final
name|Set
argument_list|<
name|ImmutableBitSet
argument_list|>
name|dependents
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|Space
parameter_list|(
name|ImmutableBitSet
name|columnOrdinals
parameter_list|,
name|Iterable
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|)
block|{
name|this
operator|.
name|columnOrdinals
operator|=
name|columnOrdinals
expr_stmt|;
name|this
operator|.
name|columns
operator|=
name|ImmutableSortedSet
operator|.
name|copyOf
argument_list|(
name|columns
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|columnOrdinals
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|instanceof
name|Space
operator|&&
name|columnOrdinals
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|Space
operator|)
name|o
operator|)
operator|.
name|columnOrdinals
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Space
name|o
parameter_list|)
block|{
return|return
name|columnOrdinals
operator|.
name|equals
argument_list|(
name|o
operator|.
name|columnOrdinals
argument_list|)
condition|?
literal|0
else|:
name|columnOrdinals
operator|.
name|contains
argument_list|(
name|o
operator|.
name|columnOrdinals
argument_list|)
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
comment|/** Number of distinct values. Null is counted as a value, if present. */
specifier|public
name|double
name|cardinality
parameter_list|()
block|{
return|return
name|values
operator|.
name|size
argument_list|()
operator|+
operator|(
name|nullCount
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

