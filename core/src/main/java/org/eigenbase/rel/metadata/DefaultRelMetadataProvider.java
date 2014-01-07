begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|metadata
package|;
end_package

begin_comment
comment|/**  * DefaultRelMetadataProvider supplies a default implementation of the {@link  * RelMetadataProvider} interface. It provides generic formulas and derivation  * rules for the standard logical algebra; coverage corresponds to the methods  * declared in {@link RelMetadataQuery}.  */
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
name|addProvider
argument_list|(
operator|new
name|RelMdPercentageOriginalRows
argument_list|()
argument_list|)
expr_stmt|;
name|addProvider
argument_list|(
operator|new
name|RelMdColumnOrigins
argument_list|()
argument_list|)
expr_stmt|;
name|addProvider
argument_list|(
operator|new
name|RelMdRowCount
argument_list|()
argument_list|)
expr_stmt|;
name|addProvider
argument_list|(
operator|new
name|RelMdUniqueKeys
argument_list|()
argument_list|)
expr_stmt|;
name|addProvider
argument_list|(
operator|new
name|RelMdColumnUniqueness
argument_list|()
argument_list|)
expr_stmt|;
name|addProvider
argument_list|(
operator|new
name|RelMdPopulationSize
argument_list|()
argument_list|)
expr_stmt|;
name|addProvider
argument_list|(
operator|new
name|RelMdDistinctRowCount
argument_list|()
argument_list|)
expr_stmt|;
name|addProvider
argument_list|(
operator|new
name|RelMdSelectivity
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DefaultRelMetadataProvider.java
end_comment

end_unit

