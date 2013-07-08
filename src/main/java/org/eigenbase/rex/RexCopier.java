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
name|rex
package|;
end_package

begin_comment
comment|/**  * Shuttle which creates a deep copy of a Rex expression.  *  *<p>This is useful when copying objects from one type factory or builder to  * another.  *  *<p>Due to the laziness of the author, not all Rex types are supported at  * present.  *  * @author jhyde  * @version $Id$  * @see RexBuilder#copy(RexNode)  */
end_comment

begin_class
class|class
name|RexCopier
extends|extends
name|RexShuttle
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RexBuilder
name|builder
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a RexCopier.      *      * @param builder Builder      */
name|RexCopier
parameter_list|(
name|RexBuilder
name|builder
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RexNode
name|visitOver
parameter_list|(
name|RexOver
name|over
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|RexWindow
name|visitWindow
parameter_list|(
name|RexWindow
name|window
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|RexNode
name|visitCall
parameter_list|(
specifier|final
name|RexCall
name|call
parameter_list|)
block|{
return|return
name|builder
operator|.
name|makeCall
argument_list|(
name|builder
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|copyType
argument_list|(
name|call
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|call
operator|.
name|getOperator
argument_list|()
argument_list|,
name|visitList
argument_list|(
name|call
operator|.
name|getOperands
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|visitCorrelVariable
parameter_list|(
name|RexCorrelVariable
name|variable
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|RexNode
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|)
block|{
return|return
name|builder
operator|.
name|makeFieldAccess
argument_list|(
name|fieldAccess
operator|.
name|getReferenceExpr
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
argument_list|,
name|fieldAccess
operator|.
name|getField
argument_list|()
operator|.
name|getIndex
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|RexNode
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|RexNode
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|)
block|{
return|return
operator|new
name|RexLiteral
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
argument_list|,
name|builder
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|copyType
argument_list|(
name|literal
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
name|literal
operator|.
name|getTypeName
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|visitDynamicParam
parameter_list|(
name|RexDynamicParam
name|dynamicParam
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|RexNode
name|visitRangeRef
parameter_list|(
name|RexRangeRef
name|rangeRef
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
end_class

begin_comment
comment|// End RexCopier.java
end_comment

end_unit

