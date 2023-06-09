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
name|materialize
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
name|DataContexts
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
name|Enumerable
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
name|profile
operator|.
name|Profiler
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
name|profile
operator|.
name|ProfilerImpl
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
name|schema
operator|.
name|ScannableTable
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|impl
operator|.
name|MaterializedViewTable
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Suppliers
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link LatticeStatisticProvider} that uses a  * {@link org.apache.calcite.profile.Profiler}.  */
end_comment

begin_class
class|class
name|ProfilerLatticeStatisticProvider
implements|implements
name|LatticeStatisticProvider
block|{
specifier|static
specifier|final
name|Factory
name|FACTORY
init|=
name|ProfilerLatticeStatisticProvider
operator|::
operator|new
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Profiler
operator|.
name|Profile
argument_list|>
name|profile
decl_stmt|;
comment|/** Creates a ProfilerLatticeStatisticProvider. */
specifier|private
name|ProfilerLatticeStatisticProvider
parameter_list|(
name|Lattice
name|lattice
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|lattice
argument_list|,
literal|"lattice"
argument_list|)
expr_stmt|;
name|this
operator|.
name|profile
operator|=
name|Suppliers
operator|.
name|memoize
argument_list|(
parameter_list|()
lambda|->
block|{
specifier|final
name|ProfilerImpl
name|profiler
init|=
name|ProfilerImpl
operator|.
name|builder
argument_list|()
operator|.
name|withPassSize
argument_list|(
literal|200
argument_list|)
operator|.
name|withMinimumSurprise
argument_list|(
literal|0.3D
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Profiler
operator|.
name|Column
argument_list|>
name|columns
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Lattice
operator|.
name|Column
name|column
range|:
name|lattice
operator|.
name|columns
control|)
block|{
name|columns
operator|.
name|add
argument_list|(
operator|new
name|Profiler
operator|.
name|Column
argument_list|(
name|column
operator|.
name|ordinal
argument_list|,
name|column
operator|.
name|alias
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|sql
init|=
name|lattice
operator|.
name|sql
argument_list|(
name|ImmutableBitSet
operator|.
name|range
argument_list|(
name|lattice
operator|.
name|columns
operator|.
name|size
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Table
name|table
init|=
operator|new
name|MaterializationService
operator|.
name|DefaultTableFactory
argument_list|()
operator|.
name|createTable
argument_list|(
name|lattice
operator|.
name|rootSchema
argument_list|,
name|sql
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|ImmutableBitSet
argument_list|>
name|initialGroups
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|final
name|Enumerable
argument_list|<
name|List
argument_list|<
name|Comparable
argument_list|>
argument_list|>
name|rows
init|=
operator|(
operator|(
name|ScannableTable
operator|)
name|table
operator|)
operator|.
name|scan
argument_list|(
name|DataContexts
operator|.
name|of
argument_list|(
name|MaterializedViewTable
operator|.
name|MATERIALIZATION_CONNECTION
argument_list|,
name|lattice
operator|.
name|rootSchema
operator|.
name|plus
argument_list|()
argument_list|)
argument_list|)
operator|.
name|select
argument_list|(
name|values
lambda|->
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|NullSentinel
operator|.
name|INSTANCE
expr_stmt|;
block|}
block|}
comment|//noinspection unchecked
return|return
operator|(
name|List
argument_list|<
name|Comparable
argument_list|>
operator|)
operator|(
name|List
operator|)
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
return|;
block|}
argument_list|)
decl_stmt|;
return|return
name|profiler
operator|.
name|profile
argument_list|(
name|rows
argument_list|,
name|columns
argument_list|,
name|initialGroups
argument_list|)
return|;
block|}
argument_list|)
operator|::
name|get
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|cardinality
parameter_list|(
name|List
argument_list|<
name|Lattice
operator|.
name|Column
argument_list|>
name|columns
parameter_list|)
block|{
specifier|final
name|ImmutableBitSet
name|build
init|=
name|Lattice
operator|.
name|Column
operator|.
name|toBitSet
argument_list|(
name|columns
argument_list|)
decl_stmt|;
specifier|final
name|double
name|cardinality
init|=
name|profile
operator|.
name|get
argument_list|()
operator|.
name|cardinality
argument_list|(
name|build
argument_list|)
decl_stmt|;
comment|//    System.out.println(columns + ": " + cardinality);
return|return
name|cardinality
return|;
block|}
block|}
end_class

end_unit

