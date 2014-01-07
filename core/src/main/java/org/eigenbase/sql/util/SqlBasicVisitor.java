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
name|util
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

begin_comment
comment|/**  * Basic implementation of {@link SqlVisitor} which does nothing at each node.  *  *<p>This class is useful as a base class for classes which implement the  * {@link SqlVisitor} interface. The derived class can override whichever  * methods it chooses.  */
end_comment

begin_class
specifier|public
class|class
name|SqlBasicVisitor
parameter_list|<
name|R
parameter_list|>
implements|implements
name|SqlVisitor
argument_list|<
name|R
argument_list|>
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|R
name|visit
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
return|return
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|acceptCall
argument_list|(
name|this
argument_list|,
name|call
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|SqlNodeList
name|nodeList
parameter_list|)
block|{
name|R
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodeList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|SqlNode
name|node
init|=
name|nodeList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|result
operator|=
name|node
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|SqlDataTypeSpec
name|type
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|SqlDynamicParam
name|param
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|SqlIntervalQualifier
name|intervalQualifier
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|//~ Inner Interfaces -------------------------------------------------------
comment|// REVIEW jvs 16-June-2006:  Without javadoc, the interaction between
comment|// ArgHandler and SqlBasicVisitor isn't obvious (nor why this interface
comment|// belongs here instead of at top-level).  visitChild already returns
comment|// R; why is a separate result() call needed?
specifier|public
interface|interface
name|ArgHandler
parameter_list|<
name|R
parameter_list|>
block|{
name|R
name|result
parameter_list|()
function_decl|;
name|R
name|visitChild
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|,
name|SqlNode
name|expr
parameter_list|,
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
function_decl|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Default implementation of {@link ArgHandler} which merely calls {@link    * SqlNode#accept} on each operand.    */
specifier|public
specifier|static
class|class
name|ArgHandlerImpl
parameter_list|<
name|R
parameter_list|>
implements|implements
name|ArgHandler
argument_list|<
name|R
argument_list|>
block|{
comment|// REVIEW jvs 16-June-2006:  This doesn't actually work, because it
comment|// is type-erased, and if you try to add<R>, you get the error
comment|// "non-static class R cannot be referenced from a static context."
specifier|public
specifier|static
specifier|final
name|ArgHandler
name|instance
init|=
operator|new
name|ArgHandlerImpl
argument_list|()
decl_stmt|;
specifier|public
name|R
name|result
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visitChild
parameter_list|(
name|SqlVisitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|,
name|SqlNode
name|expr
parameter_list|,
name|int
name|i
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
if|if
condition|(
name|operand
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|operand
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlBasicVisitor.java
end_comment

end_unit

