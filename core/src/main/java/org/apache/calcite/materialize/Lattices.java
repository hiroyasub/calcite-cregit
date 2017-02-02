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

begin_comment
comment|/**  * Utilities for {@link Lattice}, {@link LatticeStatisticProvider}.  */
end_comment

begin_class
specifier|public
class|class
name|Lattices
block|{
specifier|private
name|Lattices
parameter_list|()
block|{
block|}
comment|/** Statistics provider that uses SQL. */
specifier|public
specifier|static
specifier|final
name|LatticeStatisticProvider
operator|.
name|Factory
name|SQL
init|=
name|SqlLatticeStatisticProvider
operator|.
name|FACTORY
decl_stmt|;
comment|/** Statistics provider that uses SQL then stores the results in a cache. */
specifier|public
specifier|static
specifier|final
name|LatticeStatisticProvider
operator|.
name|Factory
name|CACHED_SQL
init|=
name|SqlLatticeStatisticProvider
operator|.
name|CACHED_FACTORY
decl_stmt|;
comment|/** Statistics provider that uses a profiler. */
specifier|public
specifier|static
specifier|final
name|LatticeStatisticProvider
operator|.
name|Factory
name|PROFILER
init|=
name|ProfilerLatticeStatisticProvider
operator|.
name|FACTORY
decl_stmt|;
block|}
end_class

begin_comment
comment|// End Lattices.java
end_comment

end_unit

