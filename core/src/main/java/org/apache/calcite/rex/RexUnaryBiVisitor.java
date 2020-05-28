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

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Default implementation of a {@link RexBiVisitor} whose payload and return  * type are the same.  *  * @param<R> Return type from each {@code visitXxx} method  */
end_comment

begin_class
specifier|public
class|class
name|RexUnaryBiVisitor
parameter_list|<
annotation|@
name|Nullable
name|R
parameter_list|>
extends|extends
name|RexBiVisitorImpl
argument_list|<
name|R
argument_list|,
name|R
argument_list|>
block|{
comment|/** Creates a RexUnaryBiVisitor. */
specifier|protected
name|RexUnaryBiVisitor
parameter_list|(
name|boolean
name|deep
parameter_list|)
block|{
name|super
argument_list|(
name|deep
argument_list|)
expr_stmt|;
block|}
comment|/** Called as the last action of, and providing the result for,    *  each {@code visitXxx} method; derived classes may override. */
specifier|protected
name|R
name|end
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
return|return
name|arg
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|inputRef
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|localRef
argument_list|,
name|arg
argument_list|)
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
name|R
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|ref
argument_list|,
name|arg
argument_list|)
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
name|R
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|fieldRef
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitLiteral
parameter_list|(
name|RexLiteral
name|literal
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|literal
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitDynamicParam
parameter_list|(
name|RexDynamicParam
name|dynamicParam
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|dynamicParam
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitRangeRef
parameter_list|(
name|RexRangeRef
name|rangeRef
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|rangeRef
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitCorrelVariable
parameter_list|(
name|RexCorrelVariable
name|correlVariable
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
return|return
name|end
argument_list|(
name|correlVariable
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitOver
parameter_list|(
name|RexOver
name|over
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
name|super
operator|.
name|visitOver
argument_list|(
name|over
argument_list|,
name|arg
argument_list|)
expr_stmt|;
return|return
name|end
argument_list|(
name|over
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|,
name|arg
argument_list|)
expr_stmt|;
return|return
name|end
argument_list|(
name|call
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
name|super
operator|.
name|visitFieldAccess
argument_list|(
name|fieldAccess
argument_list|,
name|arg
argument_list|)
expr_stmt|;
return|return
name|end
argument_list|(
name|fieldAccess
argument_list|,
name|arg
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|R
name|visitSubQuery
parameter_list|(
name|RexSubQuery
name|subQuery
parameter_list|,
name|R
name|arg
parameter_list|)
block|{
name|super
operator|.
name|visitSubQuery
argument_list|(
name|subQuery
argument_list|,
name|arg
argument_list|)
expr_stmt|;
return|return
name|end
argument_list|(
name|subQuery
argument_list|,
name|arg
argument_list|)
return|;
block|}
block|}
end_class

end_unit

