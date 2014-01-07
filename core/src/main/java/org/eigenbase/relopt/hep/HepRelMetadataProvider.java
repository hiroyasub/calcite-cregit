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
name|relopt
operator|.
name|hep
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|metadata
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * HepRelMetadataProvider implements the {@link RelMetadataProvider} interface  * by combining metadata from the rels inside of a {@link HepRelVertex}.  */
end_comment

begin_class
class|class
name|HepRelMetadataProvider
implements|implements
name|RelMetadataProvider
block|{
comment|//~ Methods ----------------------------------------------------------------
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
if|if
condition|(
operator|!
operator|(
name|rel
operator|instanceof
name|HepRelVertex
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|HepRelVertex
name|vertex
init|=
operator|(
name|HepRelVertex
operator|)
name|rel
decl_stmt|;
comment|// Use current implementation.
name|Object
name|result
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getMetadataProvider
argument_list|()
operator|.
name|getRelMetadata
argument_list|(
name|vertex
operator|.
name|getCurrentRel
argument_list|()
argument_list|,
name|metadataQueryName
argument_list|,
name|args
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

begin_comment
comment|// End HepRelMetadataProvider.java
end_comment

end_unit

