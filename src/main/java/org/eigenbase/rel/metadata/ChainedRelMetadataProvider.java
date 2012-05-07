begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * ChainedRelMetadataProvider implements the {@link RelMetadataProvider}  * interface via the {@link  * org.eigenbase.util.Glossary#ChainOfResponsibilityPattern}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|ChainedRelMetadataProvider
implements|implements
name|RelMetadataProvider
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|List
argument_list|<
name|RelMetadataProvider
argument_list|>
name|providers
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a new empty chain.      */
specifier|public
name|ChainedRelMetadataProvider
parameter_list|()
block|{
name|providers
operator|=
operator|new
name|ArrayList
argument_list|<
name|RelMetadataProvider
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Adds a provider, giving it higher priority than all those already in      * chain. Chain order matters, since the first provider which answers a      * query is used.      *      * @param provider provider to add      */
specifier|public
name|void
name|addProvider
parameter_list|(
name|RelMetadataProvider
name|provider
parameter_list|)
block|{
name|providers
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|provider
argument_list|)
expr_stmt|;
block|}
comment|// implement RelMetadataProvider
specifier|public
name|Object
name|getRelMetadata
parameter_list|(
name|RelNode
name|rel
parameter_list|,
name|String
name|metadataQueryName
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
block|{
for|for
control|(
name|RelMetadataProvider
name|provider
range|:
name|providers
control|)
block|{
name|Object
name|result
init|=
name|provider
operator|.
name|getRelMetadata
argument_list|(
name|rel
argument_list|,
name|metadataQueryName
argument_list|,
name|args
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
return|return
name|result
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End ChainedRelMetadataProvider.java
end_comment

end_unit

