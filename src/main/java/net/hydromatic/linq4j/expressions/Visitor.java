begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
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

begin_comment
comment|/**  * Node visitor.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|Visitor
block|{
specifier|public
name|Statement
name|visit
parameter_list|(
name|WhileExpression
name|whileExpression
parameter_list|,
name|Expression
name|condition
parameter_list|,
name|Statement
name|body
parameter_list|)
block|{
return|return
name|condition
operator|==
name|whileExpression
operator|.
name|condition
operator|&&
name|body
operator|==
name|whileExpression
operator|.
name|body
condition|?
name|whileExpression
else|:
name|Expressions
operator|.
name|while_
argument_list|(
name|condition
argument_list|,
name|body
argument_list|)
return|;
block|}
specifier|public
name|BlockExpression
name|visit
parameter_list|(
name|BlockExpression
name|blockExpression
parameter_list|,
name|List
argument_list|<
name|Statement
argument_list|>
name|statements
parameter_list|)
block|{
return|return
name|statements
operator|.
name|equals
argument_list|(
name|blockExpression
operator|.
name|statements
argument_list|)
condition|?
name|blockExpression
else|:
name|Expressions
operator|.
name|block
argument_list|(
name|statements
argument_list|)
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|GotoExpression
name|gotoExpression
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|expression
operator|==
name|gotoExpression
operator|.
name|expression
condition|?
name|gotoExpression
else|:
name|Expressions
operator|.
name|makeGoto
argument_list|(
name|gotoExpression
operator|.
name|kind
argument_list|,
name|gotoExpression
operator|.
name|labelTarget
argument_list|,
name|expression
argument_list|)
return|;
block|}
specifier|public
name|LabelExpression
name|visit
parameter_list|(
name|LabelExpression
name|labelExpression
parameter_list|)
block|{
return|return
name|labelExpression
return|;
block|}
specifier|public
name|LoopExpression
name|visit
parameter_list|(
name|LoopExpression
name|loopExpression
parameter_list|)
block|{
return|return
name|loopExpression
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|DeclarationExpression
name|declarationExpression
parameter_list|,
name|ParameterExpression
name|parameter
parameter_list|,
name|Expression
name|initializer
parameter_list|)
block|{
return|return
name|declarationExpression
operator|.
name|parameter
operator|==
name|parameter
operator|&&
name|declarationExpression
operator|.
name|initializer
operator|==
name|initializer
condition|?
name|declarationExpression
else|:
name|Expressions
operator|.
name|declare
argument_list|(
name|declarationExpression
operator|.
name|modifiers
argument_list|,
name|parameter
argument_list|,
name|initializer
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|LambdaExpression
name|lambdaExpression
parameter_list|)
block|{
return|return
name|lambdaExpression
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|FunctionExpression
name|functionExpression
parameter_list|,
name|BlockExpression
name|body
parameter_list|,
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameterList
parameter_list|)
block|{
return|return
name|functionExpression
operator|.
name|body
operator|.
name|equals
argument_list|(
name|body
argument_list|)
operator|&&
name|functionExpression
operator|.
name|parameterList
operator|.
name|equals
argument_list|(
name|parameterList
argument_list|)
condition|?
name|functionExpression
else|:
name|Expressions
operator|.
name|lambda
argument_list|(
name|body
argument_list|,
name|parameterList
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|BinaryExpression
name|binaryExpression
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|)
block|{
return|return
name|binaryExpression
operator|.
name|expression0
operator|==
name|expression0
operator|&&
name|binaryExpression
operator|.
name|expression1
operator|==
name|expression1
condition|?
name|binaryExpression
else|:
name|Expressions
operator|.
name|makeBinary
argument_list|(
name|binaryExpression
operator|.
name|nodeType
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|TernaryExpression
name|ternaryExpression
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|,
name|Expression
name|expression2
parameter_list|)
block|{
return|return
name|ternaryExpression
operator|.
name|expression0
operator|==
name|expression0
operator|&&
name|ternaryExpression
operator|.
name|expression1
operator|==
name|expression1
operator|&&
name|ternaryExpression
operator|.
name|expression2
operator|==
name|expression2
condition|?
name|ternaryExpression
else|:
name|Expressions
operator|.
name|makeTernary
argument_list|(
name|ternaryExpression
operator|.
name|nodeType
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|,
name|expression2
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|IndexExpression
name|indexExpression
parameter_list|,
name|Expression
name|array
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|indexExpressions
parameter_list|)
block|{
return|return
name|indexExpression
operator|.
name|array
operator|==
name|array
operator|&&
name|indexExpression
operator|.
name|indexExpressions
operator|.
name|equals
argument_list|(
name|indexExpressions
argument_list|)
condition|?
name|indexExpression
else|:
operator|new
name|IndexExpression
argument_list|(
name|array
argument_list|,
name|indexExpressions
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|UnaryExpression
name|unaryExpression
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|unaryExpression
operator|.
name|expression
operator|==
name|expression
condition|?
name|unaryExpression
else|:
name|Expressions
operator|.
name|makeUnary
argument_list|(
name|unaryExpression
operator|.
name|nodeType
argument_list|,
name|expression
argument_list|,
name|unaryExpression
operator|.
name|type
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|MethodCallExpression
name|methodCallExpression
parameter_list|,
name|Expression
name|targetExpression
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
return|return
name|methodCallExpression
operator|.
name|targetExpression
operator|==
name|targetExpression
operator|&&
name|methodCallExpression
operator|.
name|expressions
operator|.
name|equals
argument_list|(
name|expressions
argument_list|)
condition|?
name|methodCallExpression
else|:
name|Expressions
operator|.
name|call
argument_list|(
name|targetExpression
argument_list|,
name|methodCallExpression
operator|.
name|method
argument_list|,
name|expressions
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|DefaultExpression
name|defaultExpression
parameter_list|)
block|{
return|return
name|defaultExpression
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|DynamicExpression
name|dynamicExpression
parameter_list|)
block|{
return|return
name|dynamicExpression
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|MemberExpression
name|memberExpression
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|memberExpression
operator|.
name|expression
operator|==
name|expression
condition|?
name|memberExpression
else|:
name|Expressions
operator|.
name|field
argument_list|(
name|expression
argument_list|,
name|memberExpression
operator|.
name|field
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|InvocationExpression
name|invocationExpression
parameter_list|)
block|{
return|return
name|invocationExpression
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|NewArrayExpression
name|newArrayExpression
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
return|return
name|expressions
operator|.
name|equals
argument_list|(
name|newArrayExpression
operator|.
name|expressions
argument_list|)
condition|?
name|newArrayExpression
else|:
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Types
operator|.
name|componentType
argument_list|(
name|newArrayExpression
operator|.
name|type
argument_list|)
argument_list|,
name|expressions
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|ListInitExpression
name|listInitExpression
parameter_list|)
block|{
return|return
name|listInitExpression
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|NewExpression
name|newExpression
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|,
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
return|return
name|arguments
operator|.
name|equals
argument_list|(
name|newExpression
operator|.
name|arguments
argument_list|)
operator|&&
name|equal
argument_list|(
name|memberDeclarations
argument_list|,
name|newExpression
operator|.
name|memberDeclarations
argument_list|)
condition|?
name|newExpression
else|:
name|Expressions
operator|.
name|new_
argument_list|(
name|newExpression
operator|.
name|type
argument_list|,
name|arguments
argument_list|,
name|memberDeclarations
argument_list|)
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|SwitchExpression
name|switchExpression
parameter_list|)
block|{
return|return
name|switchExpression
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|TryExpression
name|tryExpression
parameter_list|)
block|{
return|return
name|tryExpression
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|MemberInitExpression
name|memberInitExpression
parameter_list|)
block|{
return|return
name|memberInitExpression
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|TypeBinaryExpression
name|typeBinaryExpression
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|typeBinaryExpression
return|;
block|}
specifier|public
name|MemberDeclaration
name|visit
parameter_list|(
name|MethodDeclaration
name|methodDeclaration
parameter_list|,
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
parameter_list|,
name|BlockExpression
name|body
parameter_list|)
block|{
return|return
name|parameters
operator|.
name|equals
argument_list|(
name|methodDeclaration
operator|.
name|parameters
argument_list|)
operator|&&
name|body
operator|.
name|equals
argument_list|(
name|methodDeclaration
operator|.
name|body
argument_list|)
condition|?
name|methodDeclaration
else|:
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|methodDeclaration
operator|.
name|modifier
argument_list|,
name|methodDeclaration
operator|.
name|resultType
argument_list|,
name|methodDeclaration
operator|.
name|name
argument_list|,
name|parameters
argument_list|,
name|body
argument_list|)
return|;
block|}
specifier|public
name|MemberDeclaration
name|visit
parameter_list|(
name|FieldDeclaration
name|fieldDeclaration
parameter_list|,
name|ParameterExpression
name|parameter
parameter_list|,
name|Expression
name|initializer
parameter_list|)
block|{
return|return
name|parameter
operator|.
name|equals
argument_list|(
name|fieldDeclaration
operator|.
name|parameter
argument_list|)
operator|&&
name|initializer
operator|.
name|equals
argument_list|(
name|fieldDeclaration
operator|.
name|initializer
argument_list|)
condition|?
name|fieldDeclaration
else|:
name|Expressions
operator|.
name|fieldDecl
argument_list|(
name|fieldDeclaration
operator|.
name|modifier
argument_list|,
name|parameter
argument_list|,
name|initializer
argument_list|)
return|;
block|}
specifier|public
name|ParameterExpression
name|visit
parameter_list|(
name|ParameterExpression
name|parameterExpression
parameter_list|)
block|{
return|return
name|parameterExpression
return|;
block|}
specifier|public
name|ConstantExpression
name|visit
parameter_list|(
name|ConstantExpression
name|constantExpression
parameter_list|)
block|{
return|return
name|constantExpression
return|;
block|}
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|boolean
name|equal
parameter_list|(
name|T
name|t1
parameter_list|,
name|T
name|t2
parameter_list|)
block|{
return|return
name|t1
operator|==
name|t2
operator|||
name|t1
operator|!=
literal|null
operator|&&
name|t1
operator|.
name|equals
argument_list|(
name|t2
argument_list|)
return|;
block|}
specifier|public
name|ClassDeclaration
name|visit
parameter_list|(
name|ClassDeclaration
name|classDeclaration
parameter_list|,
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
parameter_list|)
block|{
return|return
name|classDeclaration
return|;
block|}
specifier|public
name|MemberDeclaration
name|visit
parameter_list|(
name|ConstructorDeclaration
name|constructorDeclaration
parameter_list|,
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
parameter_list|,
name|BlockExpression
name|body
parameter_list|)
block|{
return|return
name|constructorDeclaration
return|;
block|}
block|}
end_class

begin_comment
comment|// End Visitor.java
end_comment

end_unit

