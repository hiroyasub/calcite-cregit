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

begin_comment
comment|/**  * Implementation of {@link LatticeStatisticProvider} that delegates  * to an underlying provider.  */
end_comment

begin_class
specifier|public
class|class
name|DelegatingLatticeStatisticProvider
implements|implements
name|LatticeStatisticProvider
block|{
specifier|protected
specifier|final
name|LatticeStatisticProvider
name|provider
decl_stmt|;
comment|/** Creates a DelegatingLatticeStatisticProvider.    *    * @param provider Provider to which to delegate otherwise unhandled requests    */
specifier|protected
name|DelegatingLatticeStatisticProvider
parameter_list|(
name|LatticeStatisticProvider
name|provider
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
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
return|return
name|provider
operator|.
name|cardinality
argument_list|(
name|columns
argument_list|)
return|;
block|}
block|}
end_class

end_unit

