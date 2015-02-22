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
name|linq4j
operator|.
name|tree
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
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Node visitor.  */
end_comment

begin_class
specifier|public
class|class
name|Visitor
block|{
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|WhileStatement
name|whileStatement
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|WhileStatement
name|whileStatement
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
name|whileStatement
operator|.
name|condition
operator|&&
name|body
operator|==
name|whileStatement
operator|.
name|body
condition|?
name|whileStatement
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
name|Visitor
name|preVisit
parameter_list|(
name|ConditionalStatement
name|conditionalStatement
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|ConditionalStatement
name|conditionalStatement
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|list
parameter_list|)
block|{
return|return
name|list
operator|.
name|equals
argument_list|(
name|conditionalStatement
operator|.
name|expressionList
argument_list|)
condition|?
name|conditionalStatement
else|:
name|Expressions
operator|.
name|ifThenElse
argument_list|(
name|list
argument_list|)
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|BlockStatement
name|blockStatement
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|BlockStatement
name|visit
parameter_list|(
name|BlockStatement
name|blockStatement
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
name|blockStatement
operator|.
name|statements
argument_list|)
condition|?
name|blockStatement
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
name|Visitor
name|preVisit
parameter_list|(
name|GotoStatement
name|gotoStatement
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|GotoStatement
name|gotoStatement
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|expression
operator|==
name|gotoStatement
operator|.
name|expression
condition|?
name|gotoStatement
else|:
name|Expressions
operator|.
name|makeGoto
argument_list|(
name|gotoStatement
operator|.
name|kind
argument_list|,
name|gotoStatement
operator|.
name|labelTarget
argument_list|,
name|expression
argument_list|)
return|;
block|}
specifier|public
name|LabelStatement
name|visit
parameter_list|(
name|LabelStatement
name|labelStatement
parameter_list|)
block|{
return|return
name|labelStatement
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|ForStatement
name|forStatement
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|ForStatement
name|visit
parameter_list|(
name|ForStatement
name|forStatement
parameter_list|,
name|List
argument_list|<
name|DeclarationStatement
argument_list|>
name|declarations
parameter_list|,
name|Expression
name|condition
parameter_list|,
name|Expression
name|post
parameter_list|,
name|Statement
name|body
parameter_list|)
block|{
return|return
name|declarations
operator|.
name|equals
argument_list|(
name|forStatement
operator|.
name|declarations
argument_list|)
operator|&&
name|condition
operator|==
name|forStatement
operator|.
name|condition
operator|&&
name|post
operator|==
name|forStatement
operator|.
name|post
operator|&&
name|body
operator|==
name|forStatement
operator|.
name|body
condition|?
name|forStatement
else|:
name|Expressions
operator|.
name|for_
argument_list|(
name|declarations
argument_list|,
name|condition
argument_list|,
name|post
argument_list|,
name|body
argument_list|)
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|ThrowStatement
name|throwStatement
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|ThrowStatement
name|throwStatement
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
return|return
name|expression
operator|==
name|throwStatement
operator|.
name|expression
condition|?
name|throwStatement
else|:
name|Expressions
operator|.
name|throw_
argument_list|(
name|expression
argument_list|)
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|DeclarationStatement
name|declarationStatement
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|DeclarationStatement
name|visit
parameter_list|(
name|DeclarationStatement
name|declarationStatement
parameter_list|,
name|Expression
name|initializer
parameter_list|)
block|{
return|return
name|declarationStatement
operator|.
name|initializer
operator|==
name|initializer
condition|?
name|declarationStatement
else|:
name|Expressions
operator|.
name|declare
argument_list|(
name|declarationStatement
operator|.
name|modifiers
argument_list|,
name|declarationStatement
operator|.
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
name|Visitor
name|preVisit
parameter_list|(
name|FunctionExpression
name|functionExpression
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|FunctionExpression
name|functionExpression
parameter_list|,
name|BlockStatement
name|body
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
condition|?
name|functionExpression
else|:
name|Expressions
operator|.
name|lambda
argument_list|(
name|body
argument_list|,
name|functionExpression
operator|.
name|parameterList
argument_list|)
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|BinaryExpression
name|binaryExpression
parameter_list|)
block|{
return|return
name|this
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
name|Visitor
name|preVisit
parameter_list|(
name|TernaryExpression
name|ternaryExpression
parameter_list|)
block|{
return|return
name|this
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
name|Visitor
name|preVisit
parameter_list|(
name|IndexExpression
name|indexExpression
parameter_list|)
block|{
return|return
name|this
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
name|Visitor
name|preVisit
parameter_list|(
name|UnaryExpression
name|unaryExpression
parameter_list|)
block|{
return|return
name|this
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
name|Visitor
name|preVisit
parameter_list|(
name|MethodCallExpression
name|methodCallExpression
parameter_list|)
block|{
return|return
name|this
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
name|Visitor
name|preVisit
parameter_list|(
name|MemberExpression
name|memberExpression
parameter_list|)
block|{
return|return
name|this
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
specifier|static
parameter_list|<
name|T
parameter_list|>
name|boolean
name|eq
parameter_list|(
name|T
name|t0
parameter_list|,
name|T
name|t1
parameter_list|)
block|{
return|return
name|t0
operator|==
name|t1
operator|||
name|t0
operator|!=
literal|null
operator|&&
name|t1
operator|!=
literal|null
operator|&&
name|t0
operator|.
name|equals
argument_list|(
name|t1
argument_list|)
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|NewArrayExpression
name|newArrayExpression
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Expression
name|visit
parameter_list|(
name|NewArrayExpression
name|newArrayExpression
parameter_list|,
name|int
name|dimension
parameter_list|,
name|Expression
name|bound
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
parameter_list|)
block|{
return|return
name|eq
argument_list|(
name|expressions
argument_list|,
name|newArrayExpression
operator|.
name|expressions
argument_list|)
operator|&&
name|eq
argument_list|(
name|bound
argument_list|,
name|newArrayExpression
operator|.
name|bound
argument_list|)
condition|?
name|newArrayExpression
else|:
name|expressions
operator|==
literal|null
condition|?
name|Expressions
operator|.
name|newArrayBounds
argument_list|(
name|Types
operator|.
name|getComponentTypeN
argument_list|(
name|newArrayExpression
operator|.
name|type
argument_list|)
argument_list|,
name|dimension
argument_list|,
name|bound
argument_list|)
else|:
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Types
operator|.
name|getComponentTypeN
argument_list|(
name|newArrayExpression
operator|.
name|type
argument_list|)
argument_list|,
name|dimension
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
name|Visitor
name|preVisit
parameter_list|(
name|NewExpression
name|newExpression
parameter_list|)
block|{
return|return
name|this
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
name|Objects
operator|.
name|equals
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
name|SwitchStatement
name|switchStatement
parameter_list|)
block|{
return|return
name|switchStatement
return|;
block|}
specifier|public
name|Statement
name|visit
parameter_list|(
name|TryStatement
name|tryStatement
parameter_list|)
block|{
return|return
name|tryStatement
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
name|Visitor
name|preVisit
parameter_list|(
name|TypeBinaryExpression
name|typeBinaryExpression
parameter_list|)
block|{
return|return
name|this
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
operator|.
name|expression
operator|==
name|expression
condition|?
name|typeBinaryExpression
else|:
operator|new
name|TypeBinaryExpression
argument_list|(
name|expression
operator|.
name|getNodeType
argument_list|()
argument_list|,
name|expression
argument_list|,
name|expression
operator|.
name|type
argument_list|)
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|MethodDeclaration
name|methodDeclaration
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|MemberDeclaration
name|visit
parameter_list|(
name|MethodDeclaration
name|methodDeclaration
parameter_list|,
name|BlockStatement
name|body
parameter_list|)
block|{
return|return
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
name|methodDeclaration
operator|.
name|parameters
argument_list|,
name|body
argument_list|)
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|FieldDeclaration
name|fieldDeclaration
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|MemberDeclaration
name|visit
parameter_list|(
name|FieldDeclaration
name|fieldDeclaration
parameter_list|,
name|Expression
name|initializer
parameter_list|)
block|{
return|return
name|eq
argument_list|(
name|initializer
argument_list|,
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
name|fieldDeclaration
operator|.
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
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|ClassDeclaration
name|classDeclaration
parameter_list|)
block|{
return|return
name|this
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
name|Objects
operator|.
name|equals
argument_list|(
name|memberDeclarations
argument_list|,
name|classDeclaration
operator|.
name|memberDeclarations
argument_list|)
condition|?
name|classDeclaration
else|:
name|Expressions
operator|.
name|classDecl
argument_list|(
name|classDeclaration
operator|.
name|modifier
argument_list|,
name|classDeclaration
operator|.
name|name
argument_list|,
name|classDeclaration
operator|.
name|extended
argument_list|,
name|classDeclaration
operator|.
name|implemented
argument_list|,
name|memberDeclarations
argument_list|)
return|;
block|}
specifier|public
name|Visitor
name|preVisit
parameter_list|(
name|ConstructorDeclaration
name|constructorDeclaration
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|MemberDeclaration
name|visit
parameter_list|(
name|ConstructorDeclaration
name|constructorDeclaration
parameter_list|,
name|BlockStatement
name|body
parameter_list|)
block|{
return|return
name|body
operator|.
name|equals
argument_list|(
name|constructorDeclaration
operator|.
name|body
argument_list|)
condition|?
name|constructorDeclaration
else|:
name|Expressions
operator|.
name|constructorDecl
argument_list|(
name|constructorDeclaration
operator|.
name|modifier
argument_list|,
name|constructorDeclaration
operator|.
name|resultType
argument_list|,
name|constructorDeclaration
operator|.
name|parameters
argument_list|,
name|body
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End Visitor.java
end_comment

end_unit

