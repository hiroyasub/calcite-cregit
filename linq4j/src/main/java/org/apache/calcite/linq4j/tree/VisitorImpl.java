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

begin_comment
comment|/**  * Default implementation of {@link Visitor}, which traverses a tree but does  * nothing. In a derived class you can override selected methods.  *  * @param<R> Return type  */
end_comment

begin_class
specifier|public
class|class
name|VisitorImpl
parameter_list|<
name|R
parameter_list|>
implements|implements
name|Visitor
argument_list|<
name|R
argument_list|>
block|{
specifier|public
name|VisitorImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|BinaryExpression
name|binaryExpression
parameter_list|)
block|{
name|R
name|r0
init|=
name|binaryExpression
operator|.
name|expression0
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|R
name|r1
init|=
name|binaryExpression
operator|.
name|expression1
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|r1
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|BlockStatement
name|blockStatement
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|blockStatement
operator|.
name|statements
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|ClassDeclaration
name|classDeclaration
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|classDeclaration
operator|.
name|memberDeclarations
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|ConditionalExpression
name|conditionalExpression
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|conditionalExpression
operator|.
name|expressionList
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|ConditionalStatement
name|conditionalStatement
parameter_list|)
block|{
return|return
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|conditionalStatement
operator|.
name|expressionList
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|ConstantExpression
name|constantExpression
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
name|ConstructorDeclaration
name|constructorDeclaration
parameter_list|)
block|{
name|R
name|r0
init|=
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|constructorDeclaration
operator|.
name|parameters
argument_list|,
name|this
argument_list|)
decl_stmt|;
return|return
name|constructorDeclaration
operator|.
name|body
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|DeclarationStatement
name|declarationStatement
parameter_list|)
block|{
name|R
name|r
init|=
name|declarationStatement
operator|.
name|parameter
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|declarationStatement
operator|.
name|initializer
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
name|declarationStatement
operator|.
name|initializer
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|DefaultExpression
name|defaultExpression
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
name|DynamicExpression
name|dynamicExpression
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
name|FieldDeclaration
name|fieldDeclaration
parameter_list|)
block|{
name|R
name|r0
init|=
name|fieldDeclaration
operator|.
name|parameter
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|fieldDeclaration
operator|.
name|initializer
operator|==
literal|null
condition|?
literal|null
else|:
name|fieldDeclaration
operator|.
name|initializer
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|ForStatement
name|forStatement
parameter_list|)
block|{
name|R
name|r0
init|=
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|forStatement
operator|.
name|declarations
argument_list|,
name|this
argument_list|)
decl_stmt|;
name|R
name|r1
init|=
name|forStatement
operator|.
name|condition
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|R
name|r2
init|=
name|forStatement
operator|.
name|post
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|forStatement
operator|.
name|body
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|ForEachStatement
name|forEachStatement
parameter_list|)
block|{
name|R
name|r0
init|=
name|forEachStatement
operator|.
name|parameter
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|R
name|r1
init|=
name|forEachStatement
operator|.
name|iterable
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|forEachStatement
operator|.
name|body
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|FunctionExpression
name|functionExpression
parameter_list|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|Node
argument_list|>
name|parameterList
init|=
name|functionExpression
operator|.
name|parameterList
decl_stmt|;
name|R
name|r0
init|=
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|parameterList
argument_list|,
name|this
argument_list|)
decl_stmt|;
return|return
name|functionExpression
operator|.
name|body
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|GotoStatement
name|gotoStatement
parameter_list|)
block|{
return|return
name|gotoStatement
operator|.
name|expression
operator|==
literal|null
condition|?
literal|null
else|:
name|gotoStatement
operator|.
name|expression
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|IndexExpression
name|indexExpression
parameter_list|)
block|{
name|R
name|r0
init|=
name|indexExpression
operator|.
name|array
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|indexExpression
operator|.
name|indexExpressions
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|InvocationExpression
name|invocationExpression
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
name|LabelStatement
name|labelStatement
parameter_list|)
block|{
return|return
name|labelStatement
operator|.
name|defaultValue
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|LambdaExpression
name|lambdaExpression
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
name|ListInitExpression
name|listInitExpression
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
name|MemberExpression
name|memberExpression
parameter_list|)
block|{
name|R
name|r
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|memberExpression
operator|.
name|expression
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
name|memberExpression
operator|.
name|expression
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|MemberInitExpression
name|memberInitExpression
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
name|MethodCallExpression
name|methodCallExpression
parameter_list|)
block|{
name|R
name|r
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|methodCallExpression
operator|.
name|targetExpression
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
name|methodCallExpression
operator|.
name|targetExpression
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|methodCallExpression
operator|.
name|expressions
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|MethodDeclaration
name|methodDeclaration
parameter_list|)
block|{
name|R
name|r0
init|=
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|methodDeclaration
operator|.
name|parameters
argument_list|,
name|this
argument_list|)
decl_stmt|;
return|return
name|methodDeclaration
operator|.
name|body
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|NewArrayExpression
name|newArrayExpression
parameter_list|)
block|{
name|R
name|r
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|newArrayExpression
operator|.
name|bound
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
name|newArrayExpression
operator|.
name|bound
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|newArrayExpression
operator|.
name|expressions
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|NewExpression
name|newExpression
parameter_list|)
block|{
name|R
name|r0
init|=
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|newExpression
operator|.
name|arguments
argument_list|,
name|this
argument_list|)
decl_stmt|;
return|return
name|Expressions
operator|.
name|acceptNodes
argument_list|(
name|newExpression
operator|.
name|memberDeclarations
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|ParameterExpression
name|parameterExpression
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
name|SwitchStatement
name|switchStatement
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
name|TernaryExpression
name|ternaryExpression
parameter_list|)
block|{
name|R
name|r0
init|=
name|ternaryExpression
operator|.
name|expression0
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|R
name|r1
init|=
name|ternaryExpression
operator|.
name|expression1
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|ternaryExpression
operator|.
name|expression2
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|ThrowStatement
name|throwStatement
parameter_list|)
block|{
return|return
name|throwStatement
operator|.
name|expression
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|TryStatement
name|tryStatement
parameter_list|)
block|{
name|R
name|r
init|=
name|tryStatement
operator|.
name|body
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
for|for
control|(
name|CatchBlock
name|catchBlock
range|:
name|tryStatement
operator|.
name|catchBlocks
control|)
block|{
name|r
operator|=
name|catchBlock
operator|.
name|parameter
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|r
operator|=
name|catchBlock
operator|.
name|body
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|tryStatement
operator|.
name|fynally
operator|!=
literal|null
condition|)
block|{
name|r
operator|=
name|tryStatement
operator|.
name|fynally
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|TypeBinaryExpression
name|typeBinaryExpression
parameter_list|)
block|{
return|return
name|typeBinaryExpression
operator|.
name|expression
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|UnaryExpression
name|unaryExpression
parameter_list|)
block|{
return|return
name|unaryExpression
operator|.
name|expression
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|R
name|visit
parameter_list|(
name|WhileStatement
name|whileStatement
parameter_list|)
block|{
name|R
name|r0
init|=
name|whileStatement
operator|.
name|condition
operator|.
name|accept
argument_list|(
name|this
argument_list|)
decl_stmt|;
return|return
name|whileStatement
operator|.
name|body
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

