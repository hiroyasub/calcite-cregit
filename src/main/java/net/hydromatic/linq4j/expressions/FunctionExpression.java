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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Represents a strongly typed lambda expression as a data structure in the form  * of an expression tree. This class cannot be inherited.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|FunctionExpression
parameter_list|<
name|F
extends|extends
name|Function
parameter_list|<
name|?
parameter_list|>
parameter_list|>
extends|extends
name|LambdaExpression
block|{
specifier|public
specifier|final
name|F
name|function
decl_stmt|;
specifier|public
specifier|final
name|BlockExpression
name|body
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameterList
decl_stmt|;
specifier|private
name|F
name|dynamicFunction
decl_stmt|;
specifier|private
name|FunctionExpression
parameter_list|(
name|Class
argument_list|<
name|F
argument_list|>
name|type
parameter_list|,
name|F
name|function
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
name|super
argument_list|(
name|ExpressionType
operator|.
name|Lambda
argument_list|,
name|type
argument_list|)
expr_stmt|;
assert|assert
name|type
operator|!=
literal|null
assert|;
assert|assert
name|function
operator|!=
literal|null
operator|||
name|body
operator|!=
literal|null
assert|;
name|this
operator|.
name|function
operator|=
name|function
expr_stmt|;
name|this
operator|.
name|body
operator|=
name|body
expr_stmt|;
name|this
operator|.
name|parameterList
operator|=
name|parameterList
expr_stmt|;
block|}
specifier|public
name|FunctionExpression
parameter_list|(
name|F
name|function
parameter_list|)
block|{
name|this
argument_list|(
operator|(
name|Class
operator|)
name|function
operator|.
name|getClass
argument_list|()
argument_list|,
name|function
argument_list|,
literal|null
argument_list|,
name|Collections
operator|.
expr|<
name|ParameterExpression
operator|>
name|emptyList
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FunctionExpression
parameter_list|(
name|Class
argument_list|<
name|F
argument_list|>
name|type
parameter_list|,
name|BlockExpression
name|body
parameter_list|,
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
literal|null
argument_list|,
name|body
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
name|BlockExpression
name|body
init|=
name|this
operator|.
name|body
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameterList
init|=
name|Expressions
operator|.
name|acceptParameterExpressions
argument_list|(
name|this
operator|.
name|parameterList
argument_list|,
name|visitor
argument_list|)
decl_stmt|;
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|,
name|body
argument_list|,
name|parameterList
argument_list|)
return|;
block|}
specifier|public
name|Invokable
name|compile
parameter_list|()
block|{
return|return
operator|new
name|Invokable
argument_list|()
block|{
specifier|public
name|Object
name|dynamicInvoke
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
block|{
specifier|final
name|Evaluator
name|evaluator
init|=
operator|new
name|Evaluator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|evaluator
operator|.
name|push
argument_list|(
name|parameterList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|evaluator
operator|.
name|evaluate
argument_list|(
name|body
argument_list|)
return|;
block|}
block|}
return|;
block|}
specifier|public
name|F
name|getFunction
parameter_list|()
block|{
if|if
condition|(
name|function
operator|!=
literal|null
condition|)
block|{
return|return
name|function
return|;
block|}
if|if
condition|(
name|dynamicFunction
operator|==
literal|null
condition|)
block|{
specifier|final
name|Invokable
name|x
init|=
name|compile
argument_list|()
decl_stmt|;
comment|//noinspection unchecked
name|dynamicFunction
operator|=
operator|(
name|F
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|Types
operator|.
name|toClass
argument_list|(
name|type
argument_list|)
block|}
argument_list|,
operator|new
name|InvocationHandler
argument_list|()
block|{
specifier|public
name|Object
name|invoke
parameter_list|(
name|Object
name|proxy
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
index|[]
name|args
parameter_list|)
throws|throws
name|Throwable
block|{
return|return
name|x
operator|.
name|dynamicInvoke
argument_list|(
name|args
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|dynamicFunction
return|;
block|}
annotation|@
name|Override
name|void
name|accept
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|,
name|int
name|lprec
parameter_list|,
name|int
name|rprec
parameter_list|)
block|{
comment|// "new Function1() {
comment|//    public Result apply(T1 p1, ...) {
comment|//<body>
comment|//    }
comment|//    // bridge method
comment|//    public Object apply(Object p1, ...) {
comment|//        return apply((T1) p1, ...);
comment|//    }
comment|// }
name|List
argument_list|<
name|String
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|bridgeParams
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|bridgeArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|ParameterExpression
name|parameterExpression
range|:
name|parameterList
control|)
block|{
name|params
operator|.
name|add
argument_list|(
name|Types
operator|.
name|boxClassName
argument_list|(
name|parameterExpression
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|" "
operator|+
name|parameterExpression
operator|.
name|name
argument_list|)
expr_stmt|;
name|bridgeParams
operator|.
name|add
argument_list|(
literal|"Object "
operator|+
name|parameterExpression
operator|.
name|name
argument_list|)
expr_stmt|;
name|bridgeArgs
operator|.
name|add
argument_list|(
literal|"("
operator|+
name|Types
operator|.
name|boxClassName
argument_list|(
name|parameterExpression
operator|.
name|getType
argument_list|()
argument_list|)
operator|+
literal|") "
operator|+
name|parameterExpression
operator|.
name|name
argument_list|)
expr_stmt|;
block|}
name|Type
name|bridgeResultType
init|=
name|Functions
operator|.
name|FUNCTION_RESULT_TYPES
operator|.
name|get
argument_list|(
name|this
operator|.
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|bridgeResultType
operator|==
literal|null
condition|)
block|{
name|bridgeResultType
operator|=
name|body
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
name|Type
name|resultType2
init|=
name|bridgeResultType
decl_stmt|;
if|if
condition|(
name|bridgeResultType
operator|==
name|Object
operator|.
name|class
operator|&&
operator|!
name|params
operator|.
name|equals
argument_list|(
name|bridgeParams
argument_list|)
condition|)
block|{
name|resultType2
operator|=
name|body
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|"new "
argument_list|)
operator|.
name|append
argument_list|(
name|type
argument_list|)
operator|.
name|append
argument_list|(
literal|"()"
argument_list|)
operator|.
name|begin
argument_list|(
literal|" {\n"
argument_list|)
operator|.
name|append
argument_list|(
literal|"public "
argument_list|)
operator|.
name|append
argument_list|(
name|Types
operator|.
name|className
argument_list|(
name|resultType2
argument_list|)
argument_list|)
operator|.
name|list
argument_list|(
literal|" apply("
argument_list|,
literal|", "
argument_list|,
literal|") "
argument_list|,
name|params
argument_list|)
operator|.
name|append
argument_list|(
name|Blocks
operator|.
name|toFunctionBlock
argument_list|(
name|body
argument_list|)
argument_list|)
expr_stmt|;
comment|// Generate a bridge method. Argument types are looser (as if every
comment|// type parameter is set to 'Object')
name|writer
operator|.
name|append
argument_list|(
literal|"public "
argument_list|)
operator|.
name|append
argument_list|(
name|Types
operator|.
name|className
argument_list|(
name|bridgeResultType
argument_list|)
argument_list|)
operator|.
name|list
argument_list|(
literal|" apply("
argument_list|,
literal|", "
argument_list|,
literal|") "
argument_list|,
name|bridgeParams
argument_list|)
operator|.
name|begin
argument_list|(
literal|"{\n"
argument_list|)
operator|.
name|list
argument_list|(
literal|"return apply(\n"
argument_list|,
literal|",\n"
argument_list|,
literal|");\n"
argument_list|,
name|bridgeArgs
argument_list|)
operator|.
name|end
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|end
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
block|}
specifier|public
interface|interface
name|Invokable
block|{
name|Object
name|dynamicInvoke
parameter_list|(
name|Object
modifier|...
name|args
parameter_list|)
function_decl|;
block|}
block|}
end_class

begin_comment
comment|// End FunctionExpression.java
end_comment

end_unit

