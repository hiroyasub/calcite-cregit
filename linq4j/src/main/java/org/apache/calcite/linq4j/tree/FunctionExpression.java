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
name|Functions
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
name|InvocationHandler
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
name|Method
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
name|Proxy
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
name|Type
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
name|TypeVariable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

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
comment|/**  * Represents a strongly typed lambda expression as a data structure in the form  * of an expression tree. This class cannot be inherited.  *  * @param<F> Function type  */
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
name|BlockStatement
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
comment|/**    * Cache the hash code for the expression    */
specifier|private
name|int
name|hash
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
name|BlockStatement
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
operator|:
literal|"type should not be null"
assert|;
assert|assert
name|function
operator|!=
literal|null
operator|||
name|body
operator|!=
literal|null
operator|:
literal|"both function and body should not be null"
assert|;
assert|assert
name|parameterList
operator|!=
literal|null
operator|:
literal|"parameterList should not be null"
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
name|BlockStatement
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
name|visitor
operator|=
name|visitor
operator|.
name|preVisit
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|BlockStatement
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
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|,
name|body
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
comment|//
comment|// if any arguments are primitive there is an extra bridge method:
comment|//
comment|//  new Function1() {
comment|//    public double apply(double p1, int p2) {
comment|//<body>
comment|//    }
comment|//    // box bridge method
comment|//    public Double apply(Double p1, Integer p2) {
comment|//      return apply(p1.doubleValue(), p2.intValue());
comment|//    }
comment|//    // bridge method
comment|//    public Object apply(Object p1, Object p2) {
comment|//      return apply((Double) p1, (Integer) p2);
comment|//    }
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
name|List
argument_list|<
name|String
argument_list|>
name|boxBridgeParams
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
name|boxBridgeArgs
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
specifier|final
name|Type
name|parameterType
init|=
name|parameterExpression
operator|.
name|getType
argument_list|()
decl_stmt|;
specifier|final
name|Type
name|parameterBoxType
init|=
name|Types
operator|.
name|box
argument_list|(
name|parameterType
argument_list|)
decl_stmt|;
specifier|final
name|String
name|parameterBoxTypeName
init|=
name|Types
operator|.
name|className
argument_list|(
name|parameterBoxType
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|parameterExpression
operator|.
name|declString
argument_list|()
argument_list|)
expr_stmt|;
name|bridgeParams
operator|.
name|add
argument_list|(
name|parameterExpression
operator|.
name|declString
argument_list|(
name|Object
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|bridgeArgs
operator|.
name|add
argument_list|(
literal|"("
operator|+
name|parameterBoxTypeName
operator|+
literal|") "
operator|+
name|parameterExpression
operator|.
name|name
argument_list|)
expr_stmt|;
name|boxBridgeParams
operator|.
name|add
argument_list|(
name|parameterExpression
operator|.
name|declString
argument_list|(
name|parameterBoxType
argument_list|)
argument_list|)
expr_stmt|;
name|boxBridgeArgs
operator|.
name|add
argument_list|(
name|parameterExpression
operator|.
name|name
operator|+
operator|(
name|Primitive
operator|.
name|is
argument_list|(
name|parameterType
argument_list|)
condition|?
literal|"."
operator|+
name|Primitive
operator|.
name|of
argument_list|(
name|parameterType
argument_list|)
operator|.
name|primitiveName
operator|+
literal|"Value()"
else|:
literal|""
operator|)
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
operator|&&
operator|!
operator|(
name|body
operator|.
name|getType
argument_list|()
operator|instanceof
name|TypeVariable
operator|)
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
name|String
name|methodName
init|=
name|getAbstractMethodName
argument_list|()
decl_stmt|;
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
literal|" "
operator|+
name|methodName
operator|+
literal|"("
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
comment|// Generate an intermediate bridge method if at least one parameter is
comment|// primitive.
specifier|final
name|String
name|bridgeResultTypeName
init|=
name|isAbstractMethodPrimitive
argument_list|()
condition|?
name|Types
operator|.
name|className
argument_list|(
name|bridgeResultType
argument_list|)
else|:
name|Types
operator|.
name|boxClassName
argument_list|(
name|bridgeResultType
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|boxBridgeParams
operator|.
name|equals
argument_list|(
name|params
argument_list|)
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"public "
argument_list|)
operator|.
name|append
argument_list|(
name|bridgeResultTypeName
argument_list|)
operator|.
name|list
argument_list|(
literal|" "
operator|+
name|methodName
operator|+
literal|"("
argument_list|,
literal|", "
argument_list|,
literal|") "
argument_list|,
name|boxBridgeParams
argument_list|)
operator|.
name|begin
argument_list|(
literal|"{\n"
argument_list|)
operator|.
name|list
argument_list|(
literal|"return "
operator|+
name|methodName
operator|+
literal|"(\n"
argument_list|,
literal|",\n"
argument_list|,
literal|");\n"
argument_list|,
name|boxBridgeArgs
argument_list|)
operator|.
name|end
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
block|}
comment|// Generate a bridge method. Argument types are looser (as if every
comment|// type parameter is set to 'Object').
comment|//
comment|// Skip the bridge method if there are no arguments. It would have the
comment|// same overload as the regular method.
if|if
condition|(
operator|!
name|bridgeParams
operator|.
name|equals
argument_list|(
name|params
argument_list|)
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|"public "
argument_list|)
operator|.
name|append
argument_list|(
name|bridgeResultTypeName
argument_list|)
operator|.
name|list
argument_list|(
literal|" "
operator|+
name|methodName
operator|+
literal|"("
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
literal|"return "
operator|+
name|methodName
operator|+
literal|"(\n"
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
block|}
name|writer
operator|.
name|end
argument_list|(
literal|"}\n"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|isAbstractMethodPrimitive
parameter_list|()
block|{
name|Method
name|method
init|=
name|getAbstractMethod
argument_list|()
decl_stmt|;
return|return
name|method
operator|!=
literal|null
operator|&&
name|Primitive
operator|.
name|is
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getAbstractMethodName
parameter_list|()
block|{
specifier|final
name|Method
name|abstractMethod
init|=
name|getAbstractMethod
argument_list|()
decl_stmt|;
assert|assert
name|abstractMethod
operator|!=
literal|null
assert|;
return|return
name|abstractMethod
operator|.
name|getName
argument_list|()
return|;
block|}
specifier|private
name|Method
name|getAbstractMethod
parameter_list|()
block|{
if|if
condition|(
name|type
operator|instanceof
name|Class
operator|&&
operator|(
operator|(
name|Class
operator|)
name|type
operator|)
operator|.
name|isInterface
argument_list|()
operator|&&
operator|(
operator|(
name|Class
operator|)
name|type
operator|)
operator|.
name|getDeclaredMethods
argument_list|()
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
operator|(
operator|(
name|Class
operator|)
name|type
operator|)
operator|.
name|getDeclaredMethods
argument_list|()
index|[
literal|0
index|]
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|FunctionExpression
name|that
init|=
operator|(
name|FunctionExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|?
operator|!
name|body
operator|.
name|equals
argument_list|(
name|that
operator|.
name|body
argument_list|)
else|:
name|that
operator|.
name|body
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|function
operator|!=
literal|null
condition|?
operator|!
name|function
operator|.
name|equals
argument_list|(
name|that
operator|.
name|function
argument_list|)
else|:
name|that
operator|.
name|function
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|parameterList
operator|.
name|equals
argument_list|(
name|that
operator|.
name|parameterList
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
init|=
name|hash
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|super
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|function
operator|!=
literal|null
condition|?
name|function
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|body
operator|!=
literal|null
condition|?
name|body
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|parameterList
operator|.
name|hashCode
argument_list|()
expr_stmt|;
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
literal|1
expr_stmt|;
block|}
name|hash
operator|=
name|result
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/** Function that can be invoked with a variable number of arguments. */
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

