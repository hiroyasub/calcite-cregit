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
name|rex
package|;
end_package

begin_comment
comment|/**  * Default implementation of {@link RexBiVisitor}, which visits each node but  * does nothing while it's there.  *  * @param<R> Return type from each {@code visitXxx} method  * @param<P> Payload type  */
end_comment

begin_class
specifier|public
class|class
name|RexBiVisitorImpl
parameter_list|<
name|R
parameter_list|,
name|P
parameter_list|>
implements|implements
name|RexBiVisitor
argument_list|<
name|R
argument_list|,
name|P
argument_list|>
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|boolean
name|deep
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|RexBiVisitorImpl
parameter_list|(
name|boolean
name|deep
parameter_list|)
block|{
name|this
operator|.
name|deep
operator|=
name|deep
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|R
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visitOver
parameter_list|(
name|RexOver
name|over
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
name|R
name|r
init|=
name|visitCall
argument_list|(
name|over
argument_list|,
name|arg
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|deep
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RexWindow
name|window
init|=
name|over
operator|.
name|getWindow
argument_list|()
decl_stmt|;
for|for
control|(
name|RexFieldCollation
name|orderKey
range|:
name|window
operator|.
name|orderKeys
control|)
block|{
name|orderKey
operator|.
name|left
operator|.
name|accept
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|RexNode
name|partitionKey
range|:
name|window
operator|.
name|partitionKeys
control|)
block|{
name|partitionKey
operator|.
name|accept
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
name|window
operator|.
name|getLowerBound
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
expr_stmt|;
name|window
operator|.
name|getUpperBound
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
specifier|public
name|R
name|visitCorrelVariable
parameter_list|(
name|RexCorrelVariable
name|correlVariable
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
if|if
condition|(
operator|!
name|deep
condition|)
block|{
return|return
literal|null
return|;
block|}
name|R
name|r
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|call
operator|.
name|operands
control|)
block|{
name|r
operator|=
name|operand
operator|.
name|accept
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
name|R
name|visitDynamicParam
parameter_list|(
name|RexDynamicParam
name|dynamicParam
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visitRangeRef
parameter_list|(
name|RexRangeRef
name|rangeRef
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|R
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
if|if
condition|(
operator|!
name|deep
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|RexNode
name|expr
init|=
name|fieldAccess
operator|.
name|getReferenceExpr
argument_list|()
decl_stmt|;
return|return
name|expr
operator|.
name|accept
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|public
name|R
name|visitSubQuery
parameter_list|(
name|RexSubQuery
name|subQuery
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
if|if
condition|(
operator|!
name|deep
condition|)
block|{
return|return
literal|null
return|;
block|}
name|R
name|r
init|=
literal|null
decl_stmt|;
for|for
control|(
name|RexNode
name|operand
range|:
name|subQuery
operator|.
name|operands
control|)
block|{
name|r
operator|=
name|operand
operator|.
name|accept
argument_list|(
name|this
argument_list|,
name|arg
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitTableInputRef
parameter_list|(
name|RexTableInputRef
name|ref
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitPatternFieldRef
parameter_list|(
name|RexPatternFieldRef
name|fieldRef
parameter_list|,
name|P
name|arg
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

