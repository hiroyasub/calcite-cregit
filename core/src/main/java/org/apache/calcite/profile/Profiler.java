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
name|JsonBuilder
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
name|ImmutableSortedSet
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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|MathContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|RoundingMode
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
name|SortedSet
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
comment|/**  * Analyzes data sets.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Profiler
block|{
comment|/** Creates a profile of a data set.    *    * @param rows List of rows. Can be iterated over more than once (maybe not    *             cheaply)    * @param columns Column definitions    *    * @param initialGroups List of combinations of columns that should be    *                     profiled early, because they may be interesting    *    * @return A profile describing relationships within the data set    */
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
function_decl|;
comment|/** Column. */
class|class
name|Column
implements|implements
name|Comparable
argument_list|<
name|Column
argument_list|>
block|{
specifier|public
specifier|final
name|int
name|ordinal
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
comment|/** Creates a Column.      *      * @param ordinal Unique and contiguous within a particular data set      * @param name Name of the column      */
specifier|public
name|Column
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|static
name|ImmutableBitSet
name|toOrdinals
parameter_list|(
name|Iterable
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|)
block|{
specifier|final
name|ImmutableBitSet
operator|.
name|Builder
name|builder
init|=
name|ImmutableBitSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Column
name|column
range|:
name|columns
control|)
block|{
name|builder
operator|.
name|set
argument_list|(
name|column
operator|.
name|ordinal
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
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|ordinal
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
name|this
operator|==
name|o
operator|||
name|o
operator|instanceof
name|Column
operator|&&
name|ordinal
operator|==
operator|(
operator|(
name|Column
operator|)
name|o
operator|)
operator|.
name|ordinal
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Column
name|column
parameter_list|)
block|{
return|return
name|Integer
operator|.
name|compare
argument_list|(
name|ordinal
argument_list|,
name|column
operator|.
name|ordinal
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|name
return|;
block|}
block|}
comment|/** Statistic produced by the profiler. */
interface|interface
name|Statistic
block|{
name|Object
name|toMap
parameter_list|(
name|JsonBuilder
name|jsonBuilder
parameter_list|)
function_decl|;
block|}
comment|/** Whole data set. */
class|class
name|RowCount
implements|implements
name|Statistic
block|{
specifier|final
name|int
name|rowCount
decl_stmt|;
specifier|public
name|RowCount
parameter_list|(
name|int
name|rowCount
parameter_list|)
block|{
name|this
operator|.
name|rowCount
operator|=
name|rowCount
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|toMap
parameter_list|(
name|JsonBuilder
name|jsonBuilder
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
argument_list|>
name|map
init|=
name|jsonBuilder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"rowCount"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"rowCount"
argument_list|,
name|rowCount
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
block|}
comment|/** Unique key. */
class|class
name|Unique
implements|implements
name|Statistic
block|{
specifier|final
name|NavigableSet
argument_list|<
name|Column
argument_list|>
name|columns
decl_stmt|;
specifier|public
name|Unique
parameter_list|(
name|SortedSet
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|)
block|{
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
name|Object
name|toMap
parameter_list|(
name|JsonBuilder
name|jsonBuilder
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
argument_list|>
name|map
init|=
name|jsonBuilder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"unique"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"columns"
argument_list|,
name|FunctionalDependency
operator|.
name|getObjects
argument_list|(
name|jsonBuilder
argument_list|,
name|columns
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
block|}
comment|/** Functional dependency. */
class|class
name|FunctionalDependency
implements|implements
name|Statistic
block|{
specifier|final
name|NavigableSet
argument_list|<
name|Column
argument_list|>
name|columns
decl_stmt|;
specifier|final
name|Column
name|dependentColumn
decl_stmt|;
name|FunctionalDependency
parameter_list|(
name|SortedSet
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|,
name|Column
name|dependentColumn
parameter_list|)
block|{
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
name|this
operator|.
name|dependentColumn
operator|=
name|dependentColumn
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|toMap
parameter_list|(
name|JsonBuilder
name|jsonBuilder
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
argument_list|>
name|map
init|=
name|jsonBuilder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"fd"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"columns"
argument_list|,
name|getObjects
argument_list|(
name|jsonBuilder
argument_list|,
name|columns
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"dependentColumn"
argument_list|,
name|dependentColumn
operator|.
name|name
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
annotation|@
name|Nullable
name|Object
argument_list|>
name|getObjects
parameter_list|(
name|JsonBuilder
name|jsonBuilder
parameter_list|,
name|NavigableSet
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
annotation|@
name|Nullable
name|Object
argument_list|>
name|list
init|=
name|jsonBuilder
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|Column
name|column
range|:
name|columns
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|column
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
comment|/** Value distribution, including cardinality and optionally values, of a    * column or set of columns. If the set of columns is empty, it describes    * the number of rows in the entire data set. */
class|class
name|Distribution
implements|implements
name|Statistic
block|{
specifier|static
specifier|final
name|MathContext
name|ROUND5
init|=
operator|new
name|MathContext
argument_list|(
literal|5
argument_list|,
name|RoundingMode
operator|.
name|HALF_EVEN
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|MathContext
name|ROUND3
init|=
operator|new
name|MathContext
argument_list|(
literal|3
argument_list|,
name|RoundingMode
operator|.
name|HALF_EVEN
argument_list|)
decl_stmt|;
specifier|final
name|NavigableSet
argument_list|<
name|Column
argument_list|>
name|columns
decl_stmt|;
specifier|final
annotation|@
name|Nullable
name|NavigableSet
argument_list|<
name|Comparable
argument_list|>
name|values
decl_stmt|;
specifier|final
name|double
name|cardinality
decl_stmt|;
specifier|final
name|int
name|nullCount
decl_stmt|;
specifier|final
name|double
name|expectedCardinality
decl_stmt|;
specifier|final
name|boolean
name|minimal
decl_stmt|;
comment|/** Creates a Distribution.      *      * @param columns Column or columns being described      * @param values Values of columns, or null if there are too many      * @param cardinality Number of distinct values      * @param nullCount Number of rows where this column had a null value;      * @param expectedCardinality Expected cardinality      * @param minimal Whether the distribution is not implied by a unique      *   or functional dependency      */
specifier|public
name|Distribution
parameter_list|(
name|SortedSet
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|,
annotation|@
name|Nullable
name|SortedSet
argument_list|<
name|Comparable
argument_list|>
name|values
parameter_list|,
name|double
name|cardinality
parameter_list|,
name|int
name|nullCount
parameter_list|,
name|double
name|expectedCardinality
parameter_list|,
name|boolean
name|minimal
parameter_list|)
block|{
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
name|this
operator|.
name|values
operator|=
name|values
operator|==
literal|null
condition|?
literal|null
else|:
name|ImmutableSortedSet
operator|.
name|copyOf
argument_list|(
name|values
argument_list|)
expr_stmt|;
name|this
operator|.
name|cardinality
operator|=
name|cardinality
expr_stmt|;
name|this
operator|.
name|nullCount
operator|=
name|nullCount
expr_stmt|;
name|this
operator|.
name|expectedCardinality
operator|=
name|expectedCardinality
expr_stmt|;
name|this
operator|.
name|minimal
operator|=
name|minimal
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|toMap
parameter_list|(
name|JsonBuilder
name|jsonBuilder
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
annotation|@
name|Nullable
name|Object
argument_list|>
name|map
init|=
name|jsonBuilder
operator|.
name|map
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"type"
argument_list|,
literal|"distribution"
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"columns"
argument_list|,
name|FunctionalDependency
operator|.
name|getObjects
argument_list|(
name|jsonBuilder
argument_list|,
name|columns
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
name|List
argument_list|<
annotation|@
name|Nullable
name|Object
argument_list|>
name|list
init|=
name|jsonBuilder
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|Comparable
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|java
operator|.
name|sql
operator|.
name|Date
condition|)
block|{
name|value
operator|=
name|value
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
literal|"values"
argument_list|,
name|list
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
literal|"cardinality"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
name|cardinality
argument_list|,
name|ROUND5
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|nullCount
operator|>
literal|0
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
literal|"nullCount"
argument_list|,
name|nullCount
argument_list|)
expr_stmt|;
block|}
name|map
operator|.
name|put
argument_list|(
literal|"expectedCardinality"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
name|expectedCardinality
argument_list|,
name|ROUND5
argument_list|)
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"surprise"
argument_list|,
operator|new
name|BigDecimal
argument_list|(
name|surprise
argument_list|()
argument_list|,
name|ROUND3
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|map
return|;
block|}
name|ImmutableBitSet
name|columnOrdinals
parameter_list|()
block|{
return|return
name|Column
operator|.
name|toOrdinals
argument_list|(
name|columns
argument_list|)
return|;
block|}
name|double
name|surprise
parameter_list|()
block|{
return|return
name|SimpleProfiler
operator|.
name|surprise
argument_list|(
name|expectedCardinality
argument_list|,
name|cardinality
argument_list|)
return|;
block|}
block|}
comment|/** The result of profiling, contains various statistics about the    * data in a table. */
class|class
name|Profile
block|{
specifier|public
specifier|final
name|RowCount
name|rowCount
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|FunctionalDependency
argument_list|>
name|functionalDependencyList
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Distribution
argument_list|>
name|distributionList
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Unique
argument_list|>
name|uniqueList
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|ImmutableBitSet
argument_list|,
name|Distribution
argument_list|>
name|distributionMap
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Distribution
argument_list|>
name|singletonDistributionList
decl_stmt|;
name|Profile
parameter_list|(
name|List
argument_list|<
name|Column
argument_list|>
name|columns
parameter_list|,
name|RowCount
name|rowCount
parameter_list|,
name|Iterable
argument_list|<
name|FunctionalDependency
argument_list|>
name|functionalDependencyList
parameter_list|,
name|Iterable
argument_list|<
name|Distribution
argument_list|>
name|distributionList
parameter_list|,
name|Iterable
argument_list|<
name|Unique
argument_list|>
name|uniqueList
parameter_list|)
block|{
name|this
operator|.
name|rowCount
operator|=
name|rowCount
expr_stmt|;
name|this
operator|.
name|functionalDependencyList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|functionalDependencyList
argument_list|)
expr_stmt|;
name|this
operator|.
name|distributionList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|distributionList
argument_list|)
expr_stmt|;
name|this
operator|.
name|uniqueList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|uniqueList
argument_list|)
expr_stmt|;
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|ImmutableBitSet
argument_list|,
name|Distribution
argument_list|>
name|m
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Distribution
name|distribution
range|:
name|distributionList
control|)
block|{
name|m
operator|.
name|put
argument_list|(
name|distribution
operator|.
name|columnOrdinals
argument_list|()
argument_list|,
name|distribution
argument_list|)
expr_stmt|;
block|}
name|distributionMap
operator|=
name|m
operator|.
name|build
argument_list|()
expr_stmt|;
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Distribution
argument_list|>
name|b
init|=
name|ImmutableList
operator|.
name|builder
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
name|columns
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|int
name|key
init|=
name|i
decl_stmt|;
name|b
operator|.
name|add
argument_list|(
name|requireNonNull
argument_list|(
name|distributionMap
operator|.
name|get
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"distributionMap.get(ImmutableBitSet.of(i)) for "
operator|+
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|singletonDistributionList
operator|=
name|b
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|Statistic
argument_list|>
name|statistics
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
expr|<
name|Statistic
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|rowCount
argument_list|)
operator|.
name|addAll
argument_list|(
name|functionalDependencyList
argument_list|)
operator|.
name|addAll
argument_list|(
name|distributionList
argument_list|)
operator|.
name|addAll
argument_list|(
name|uniqueList
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|double
name|cardinality
parameter_list|(
name|ImmutableBitSet
name|columnOrdinals
parameter_list|)
block|{
specifier|final
name|ImmutableBitSet
name|originalOrdinals
init|=
name|columnOrdinals
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
specifier|final
name|Distribution
name|distribution
init|=
name|distributionMap
operator|.
name|get
argument_list|(
name|columnOrdinals
argument_list|)
decl_stmt|;
if|if
condition|(
name|distribution
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|columnOrdinals
operator|==
name|originalOrdinals
condition|)
block|{
return|return
name|distribution
operator|.
name|cardinality
return|;
block|}
else|else
block|{
specifier|final
name|List
argument_list|<
name|Double
argument_list|>
name|cardinalityList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|cardinalityList
operator|.
name|add
argument_list|(
name|distribution
operator|.
name|cardinality
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|ordinal
range|:
name|originalOrdinals
operator|.
name|except
argument_list|(
name|columnOrdinals
argument_list|)
control|)
block|{
specifier|final
name|Distribution
name|d
init|=
name|singletonDistributionList
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
name|cardinalityList
operator|.
name|add
argument_list|(
name|d
operator|.
name|cardinality
argument_list|)
expr_stmt|;
block|}
return|return
name|Lattice
operator|.
name|getRowCount
argument_list|(
name|rowCount
operator|.
name|rowCount
argument_list|,
name|cardinalityList
argument_list|)
return|;
block|}
block|}
comment|// Clear the last bit and iterate.
comment|// Better would be to combine all of our nearest ancestors.
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|list
init|=
name|columnOrdinals
operator|.
name|asList
argument_list|()
decl_stmt|;
name|columnOrdinals
operator|=
name|columnOrdinals
operator|.
name|clear
argument_list|(
name|Util
operator|.
name|last
argument_list|(
name|list
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_interface

end_unit

