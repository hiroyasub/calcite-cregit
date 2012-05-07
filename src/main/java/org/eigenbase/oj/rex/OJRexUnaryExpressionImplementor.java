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
name|oj
operator|.
name|rex
package|;
end_package

begin_import
import|import
name|openjava
operator|.
name|ptree
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
comment|/**  * OJRexUnaryExpressionImplementor implements {@link OJRexImplementor} for row  * expressions which can be translated to instances of OpenJava {@link  * UnaryExpression}.  *  * @author Angel Chang  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|OJRexUnaryExpressionImplementor
implements|implements
name|OJRexImplementor
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|int
name|ojUnaryExpressionOrdinal
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|OJRexUnaryExpressionImplementor
parameter_list|(
name|int
name|ojUnaryExpressionOrdinal
parameter_list|)
block|{
name|this
operator|.
name|ojUnaryExpressionOrdinal
operator|=
name|ojUnaryExpressionOrdinal
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Expression
name|implement
parameter_list|(
name|RexToOJTranslator
name|translator
parameter_list|,
name|RexCall
name|call
parameter_list|,
name|Expression
index|[]
name|operands
parameter_list|)
block|{
return|return
operator|new
name|UnaryExpression
argument_list|(
name|operands
index|[
literal|0
index|]
argument_list|,
name|ojUnaryExpressionOrdinal
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|canImplement
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End OJRexUnaryExpressionImplementor.java
end_comment

end_unit

