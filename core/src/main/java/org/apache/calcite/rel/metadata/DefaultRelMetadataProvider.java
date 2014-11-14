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

begin_comment
comment|/**  * DefaultRelMetadataProvider supplies a default implementation of the  * {@link RelMetadataProvider} interface. It provides generic formulas and  * derivation rules for the standard logical algebra; coverage corresponds to  * the methods declared in {@link RelMetadataQuery}.  */
end_comment

begin_class
specifier|public
class|class
name|DefaultRelMetadataProvider
extends|extends
name|ChainedRelMetadataProvider
block|{
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new default provider. This provider defines "catch-all"    * handlers for generic RelNodes, so it should always be given lowest    * priority when chaining.    */
specifier|public
name|DefaultRelMetadataProvider
parameter_list|()
block|{
name|super
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|RelMdPercentageOriginalRows
operator|.
name|SOURCE
argument_list|,
name|RelMdColumnOrigins
operator|.
name|SOURCE
argument_list|,
name|RelMdRowCount
operator|.
name|SOURCE
argument_list|,
name|RelMdUniqueKeys
operator|.
name|SOURCE
argument_list|,
name|RelMdColumnUniqueness
operator|.
name|SOURCE
argument_list|,
name|RelMdPopulationSize
operator|.
name|SOURCE
argument_list|,
name|RelMdDistinctRowCount
operator|.
name|SOURCE
argument_list|,
name|RelMdSelectivity
operator|.
name|SOURCE
argument_list|,
name|RelMdExplainVisibility
operator|.
name|SOURCE
argument_list|,
name|RelMdPredicates
operator|.
name|SOURCE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DefaultRelMetadataProvider.java
end_comment

end_unit

