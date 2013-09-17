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
name|volcano
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
comment|/**  * VolcanoRelMetadataProvider implements the {@link RelMetadataProvider}  * interface by combining metadata from the rels making up an equivalence class.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|VolcanoRelMetadataProvider
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
name|RelSubset
operator|)
condition|)
block|{
comment|// let someone else further down the chain sort it out
return|return
literal|null
return|;
block|}
name|RelSubset
name|subset
init|=
operator|(
name|RelSubset
operator|)
name|rel
decl_stmt|;
comment|// REVIEW jvs 29-Mar-2006: I'm not sure what the correct precedence
comment|// should be here.  Letting the current best plan take the first shot is
comment|// probably the right thing to do for physical estimates such as row
comment|// count.  Dunno about others, and whether we need a way to
comment|// discriminate.
comment|// First, try current best implementation.  If it knows how to answer
comment|// this query, treat it as the most reliable.
if|if
condition|(
name|subset
operator|.
name|best
operator|!=
literal|null
condition|)
block|{
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
name|subset
operator|.
name|best
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
comment|// Otherwise, try rels in same logical equivalence class to see if any
comment|// of them have a good answer.  We use the full logical equivalence
comment|// class rather than just the subset because many metadata providers
comment|// only know about logical metadata.
comment|// Equivalence classes can get tangled up in interesting ways, so avoid
comment|// an infinite loop.  REVIEW: There's a chance this will cause us to
comment|// fail on metadata queries which invoke other queries, e.g.
comment|// PercentageOriginalRows -> Selectivity.  If we implement caching at
comment|// this level, we could probably kill two birds with one stone (use
comment|// presence of pending cache entry to detect reentrancy at the correct
comment|// granularity).
if|if
condition|(
name|subset
operator|.
name|set
operator|.
name|inMetadataQuery
condition|)
block|{
return|return
literal|null
return|;
block|}
name|subset
operator|.
name|set
operator|.
name|inMetadataQuery
operator|=
literal|true
expr_stmt|;
try|try
block|{
for|for
control|(
name|RelNode
name|relCandidate
range|:
name|subset
operator|.
name|set
operator|.
name|rels
control|)
block|{
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
name|relCandidate
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
block|}
finally|finally
block|{
name|subset
operator|.
name|set
operator|.
name|inMetadataQuery
operator|=
literal|false
expr_stmt|;
block|}
comment|// Give up.
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End VolcanoRelMetadataProvider.java
end_comment

end_unit

