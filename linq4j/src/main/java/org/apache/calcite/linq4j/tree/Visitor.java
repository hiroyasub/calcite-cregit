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

begin_comment
comment|/**  * Node visitor.  *  * @param<R> Return type  */
end_comment

begin_interface
specifier|public
interface|interface
name|Visitor
parameter_list|<
name|R
parameter_list|>
block|{
name|R
name|visit
parameter_list|(
name|BinaryExpression
name|binaryExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|BlockStatement
name|blockStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ClassDeclaration
name|classDeclaration
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ConditionalExpression
name|conditionalExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ConditionalStatement
name|conditionalStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ConstantExpression
name|constantExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ConstructorDeclaration
name|constructorDeclaration
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|DeclarationStatement
name|declarationStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|DefaultExpression
name|defaultExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|DynamicExpression
name|dynamicExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|FieldDeclaration
name|fieldDeclaration
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ForStatement
name|forStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ForEachStatement
name|forEachStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|FunctionExpression
name|functionExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|GotoStatement
name|gotoStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|IndexExpression
name|indexExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|InvocationExpression
name|invocationExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|LabelStatement
name|labelStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|LambdaExpression
name|lambdaExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ListInitExpression
name|listInitExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|MemberExpression
name|memberExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|MemberInitExpression
name|memberInitExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|MethodCallExpression
name|methodCallExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|MethodDeclaration
name|methodDeclaration
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|NewArrayExpression
name|newArrayExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|NewExpression
name|newExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ParameterExpression
name|parameterExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|SwitchStatement
name|switchStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|TernaryExpression
name|ternaryExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|ThrowStatement
name|throwStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|TryStatement
name|tryStatement
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|TypeBinaryExpression
name|typeBinaryExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|UnaryExpression
name|unaryExpression
parameter_list|)
function_decl|;
name|R
name|visit
parameter_list|(
name|WhileStatement
name|whileStatement
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

