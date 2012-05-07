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
name|util
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
name|openjava
operator|.
name|mop
operator|.
name|*
import|;
end_import

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
name|reltype
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
comment|/**  * Extends {@link RexBuilder} to builds row-expressions including those  * involving Java code.  *  * @author jhyde  * @version $Id$  * @see JavaRowExpression  * @since Nov 23, 2003  */
end_comment

begin_class
specifier|public
class|class
name|JavaRexBuilder
extends|extends
name|RexBuilder
block|{
comment|//~ Instance fields --------------------------------------------------------
name|OJTranslator
name|translator
init|=
operator|new
name|OJTranslator
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|JavaRexBuilder
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RexNode
name|makeFieldAccess
parameter_list|(
name|RexNode
name|exp
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
name|exp
operator|instanceof
name|JavaRowExpression
condition|)
block|{
name|JavaRowExpression
name|jexp
init|=
operator|(
name|JavaRowExpression
operator|)
name|exp
decl_stmt|;
specifier|final
name|FieldAccess
name|fieldAccess
init|=
operator|new
name|FieldAccess
argument_list|(
name|jexp
operator|.
name|getExpression
argument_list|()
argument_list|,
name|fieldName
argument_list|)
decl_stmt|;
return|return
name|makeJava
argument_list|(
name|jexp
operator|.
name|env
argument_list|,
name|fieldAccess
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|makeFieldAccess
argument_list|(
name|exp
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
block|}
comment|/**      * Creates a call to a Java method.      *      * @param exp Target of the method      * @param methodName Name of the method      * @param args Argument expressions; null means no arguments      *      * @return Method call      */
specifier|public
name|RexNode
name|createMethodCall
parameter_list|(
name|Environment
name|env
parameter_list|,
name|RexNode
name|exp
parameter_list|,
name|String
name|methodName
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|args
parameter_list|)
block|{
name|ExpressionList
name|ojArgs
init|=
name|translator
operator|.
name|toJava
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|Expression
name|ojExp
init|=
name|translator
operator|.
name|toJava
argument_list|(
name|exp
argument_list|)
decl_stmt|;
return|return
name|makeJava
argument_list|(
name|env
argument_list|,
operator|new
name|MethodCall
argument_list|(
name|ojExp
argument_list|,
name|methodName
argument_list|,
name|ojArgs
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|makeJava
parameter_list|(
name|Environment
name|env
parameter_list|,
name|Expression
name|expr
parameter_list|)
block|{
specifier|final
name|OJClass
name|ojClass
decl_stmt|;
try|try
block|{
name|ojClass
operator|=
name|expr
operator|.
name|getType
argument_list|(
name|env
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"Error deriving type of expression "
operator|+
name|expr
argument_list|)
throw|;
block|}
name|RelDataType
name|type
init|=
name|OJUtil
operator|.
name|ojToType
argument_list|(
name|this
operator|.
name|typeFactory
argument_list|,
name|ojClass
argument_list|)
decl_stmt|;
return|return
operator|new
name|JavaRowExpression
argument_list|(
name|env
argument_list|,
name|type
argument_list|,
name|expr
argument_list|)
return|;
block|}
specifier|public
name|RexNode
name|makeCase
parameter_list|(
name|RexNode
name|rexCond
parameter_list|,
name|RexNode
name|rexTrueCase
parameter_list|,
name|RexNode
name|rexFalseCase
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|this
argument_list|)
throw|;
block|}
specifier|public
name|RexNode
name|makeCast
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|RexNode
name|exp
parameter_list|)
block|{
if|if
condition|(
name|exp
operator|instanceof
name|JavaRowExpression
condition|)
block|{
name|JavaRowExpression
name|java
init|=
operator|(
name|JavaRowExpression
operator|)
name|exp
decl_stmt|;
specifier|final
name|OJClass
name|ojClass
init|=
name|OJUtil
operator|.
name|typeToOJClass
argument_list|(
name|type
argument_list|,
name|typeFactory
argument_list|)
decl_stmt|;
specifier|final
name|CastExpression
name|castExpr
init|=
operator|new
name|CastExpression
argument_list|(
name|ojClass
argument_list|,
name|java
operator|.
name|getExpression
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|JavaRowExpression
argument_list|(
name|java
operator|.
name|env
argument_list|,
name|type
argument_list|,
name|castExpr
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|makeCast
argument_list|(
name|type
argument_list|,
name|exp
argument_list|)
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
specifier|private
specifier|static
class|class
name|OJTranslator
block|{
specifier|public
name|ExpressionList
name|toJava
parameter_list|(
name|List
argument_list|<
name|RexNode
argument_list|>
name|args
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Expression
name|toJava
parameter_list|(
name|RexNode
name|exp
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|needToImplement
argument_list|(
name|this
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JavaRexBuilder.java
end_comment

end_unit

