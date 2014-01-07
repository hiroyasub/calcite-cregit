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
name|sql
operator|.
name|validate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
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
name|sql
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Visitor which looks for an aggregate function inside a tree of {@link  * SqlNode} objects.  */
end_comment

begin_class
class|class
name|AggFinder
extends|extends
name|SqlBasicVisitor
argument_list|<
name|Void
argument_list|>
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|boolean
name|over
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AggFinder.    *    * @param over Whether to find windowed function calls {@code Agg(x) OVER    *             windowSpec}    */
name|AggFinder
parameter_list|(
name|boolean
name|over
parameter_list|)
block|{
name|this
operator|.
name|over
operator|=
name|over
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Finds an aggregate.    *    * @param node Parse tree to search    * @return First aggregate function in parse tree, or null if not found    */
specifier|public
name|SqlNode
name|findAgg
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
try|try
block|{
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Util
operator|.
name|FoundOne
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
operator|(
name|SqlNode
operator|)
name|e
operator|.
name|getNode
argument_list|()
return|;
block|}
block|}
specifier|public
name|Void
name|visit
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|isAggregator
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Util
operator|.
name|FoundOne
argument_list|(
name|call
argument_list|)
throw|;
block|}
if|if
condition|(
name|call
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|QUERY
argument_list|)
condition|)
block|{
comment|// don't traverse into queries
return|return
literal|null
return|;
block|}
if|if
condition|(
name|call
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|OVER
condition|)
block|{
if|if
condition|(
name|over
condition|)
block|{
throw|throw
operator|new
name|Util
operator|.
name|FoundOne
argument_list|(
name|call
argument_list|)
throw|;
block|}
else|else
block|{
comment|// an aggregate function over a window is not an aggregate!
return|return
literal|null
return|;
block|}
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|call
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AggFinder.java
end_comment

end_unit

