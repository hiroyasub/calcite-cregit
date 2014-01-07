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
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Walks over a tree of {@link RelNode relational expressions}, walking a {@link  * RexShuttle} over every expression in that tree.  */
end_comment

begin_class
specifier|public
class|class
name|VisitorRelVisitor
extends|extends
name|RelVisitor
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|RexVisitor
argument_list|<
name|?
argument_list|>
name|visitor
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|VisitorRelVisitor
parameter_list|(
name|RexVisitor
argument_list|<
name|?
argument_list|>
name|visitor
parameter_list|)
block|{
name|this
operator|.
name|visitor
operator|=
name|visitor
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|visit
parameter_list|(
name|RelNode
name|p
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|RelNode
name|parent
parameter_list|)
block|{
name|List
argument_list|<
name|RexNode
argument_list|>
name|childExps
init|=
name|p
operator|.
name|getChildExps
argument_list|()
decl_stmt|;
for|for
control|(
name|RexNode
name|childExp
range|:
name|childExps
control|)
block|{
name|childExp
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
name|p
operator|.
name|childrenAccept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End VisitorRelVisitor.java
end_comment

end_unit

