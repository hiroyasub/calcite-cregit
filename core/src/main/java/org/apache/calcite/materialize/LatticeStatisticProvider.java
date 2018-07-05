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
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Estimates row counts for a lattice and its attributes.  */
end_comment

begin_interface
specifier|public
interface|interface
name|LatticeStatisticProvider
block|{
comment|/** Returns an estimate of the number of distinct values in a column    * or list of columns. */
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
function_decl|;
comment|/** Creates a {@link LatticeStatisticProvider} for a given    * {@link org.apache.calcite.materialize.Lattice}. */
interface|interface
name|Factory
extends|extends
name|Function
argument_list|<
name|Lattice
argument_list|,
name|LatticeStatisticProvider
argument_list|>
block|{   }
block|}
end_interface

begin_comment
comment|// End LatticeStatisticProvider.java
end_comment

end_unit

