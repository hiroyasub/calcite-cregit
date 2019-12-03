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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Represents a visitor or rewriter for expression trees.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExpressionVisitor
block|{
comment|/**    * Visits the children of the delegate expression.    */
parameter_list|<
name|T
extends|extends
name|Function
argument_list|<
name|?
argument_list|>
parameter_list|>
name|void
name|visitLambda
parameter_list|(
name|FunctionExpression
argument_list|<
name|T
argument_list|>
name|expression
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

