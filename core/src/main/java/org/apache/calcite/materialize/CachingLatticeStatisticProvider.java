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
name|base
operator|.
name|Throwables
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
name|cache
operator|.
name|CacheBuilder
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
name|cache
operator|.
name|CacheLoader
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
name|cache
operator|.
name|LoadingCache
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link LatticeStatisticProvider} that gets statistics by  * executing "SELECT COUNT(DISTINCT ...) ..." SQL queries.  */
end_comment

begin_class
class|class
name|CachingLatticeStatisticProvider
implements|implements
name|LatticeStatisticProvider
block|{
specifier|private
specifier|final
name|LoadingCache
argument_list|<
name|Pair
argument_list|<
name|Lattice
argument_list|,
name|Lattice
operator|.
name|Column
argument_list|>
argument_list|,
name|Integer
argument_list|>
name|cache
decl_stmt|;
comment|/** Creates a CachingStatisticProvider. */
specifier|public
name|CachingLatticeStatisticProvider
parameter_list|(
specifier|final
name|LatticeStatisticProvider
name|provider
parameter_list|)
block|{
name|cache
operator|=
name|CacheBuilder
operator|.
expr|<
name|Pair
argument_list|<
name|Lattice
argument_list|,
name|Lattice
operator|.
name|Column
argument_list|>
operator|>
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|(
operator|new
name|CacheLoader
argument_list|<
name|Pair
argument_list|<
name|Lattice
argument_list|,
name|Lattice
operator|.
name|Column
argument_list|>
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|load
parameter_list|(
name|Pair
argument_list|<
name|Lattice
argument_list|,
name|Lattice
operator|.
name|Column
argument_list|>
name|key
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|provider
operator|.
name|cardinality
argument_list|(
name|key
operator|.
name|left
argument_list|,
name|key
operator|.
name|right
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|cardinality
parameter_list|(
name|Lattice
name|lattice
parameter_list|,
name|Lattice
operator|.
name|Column
name|column
parameter_list|)
block|{
try|try
block|{
return|return
name|cache
operator|.
name|get
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|lattice
argument_list|,
name|column
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
throw|throw
name|Throwables
operator|.
name|propagate
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CachingLatticeStatisticProvider.java
end_comment

end_unit

