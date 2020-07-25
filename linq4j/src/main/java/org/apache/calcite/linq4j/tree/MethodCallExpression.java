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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|Modifier
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
comment|/**  * Represents a call to either a static or an instance method.  */
end_comment

begin_class
specifier|public
class|class
name|MethodCallExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|Method
name|method
decl_stmt|;
specifier|public
specifier|final
name|Expression
name|targetExpression
decl_stmt|;
comment|// null for call to static method
specifier|public
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
decl_stmt|;
comment|/** Cached hash code for the expression. */
specifier|private
name|int
name|hash
decl_stmt|;
name|MethodCallExpression
parameter_list|(
name|Type
name|returnType
parameter_list|,
name|Method
name|method
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
name|super
argument_list|(
name|ExpressionType
operator|.
name|Call
argument_list|,
name|returnType
argument_list|)
expr_stmt|;
assert|assert
name|expressions
operator|!=
literal|null
operator|:
literal|"expressions should not be null"
assert|;
assert|assert
name|method
operator|!=
literal|null
operator|:
literal|"method should not be null"
assert|;
assert|assert
operator|(
name|targetExpression
operator|==
literal|null
operator|)
operator|==
name|Modifier
operator|.
name|isStatic
argument_list|(
name|method
operator|.
name|getModifiers
argument_list|()
argument_list|)
assert|;
assert|assert
name|Types
operator|.
name|toClass
argument_list|(
name|returnType
argument_list|)
operator|==
name|method
operator|.
name|getReturnType
argument_list|()
assert|;
name|this
operator|.
name|method
operator|=
name|method
expr_stmt|;
name|this
operator|.
name|targetExpression
operator|=
name|targetExpression
expr_stmt|;
name|this
operator|.
name|expressions
operator|=
name|expressions
expr_stmt|;
block|}
name|MethodCallExpression
parameter_list|(
name|Method
name|method
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
name|this
argument_list|(
name|method
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|method
argument_list|,
name|targetExpression
argument_list|,
name|expressions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|accept
parameter_list|(
name|Shuttle
name|shuttle
parameter_list|)
block|{
name|shuttle
operator|=
name|shuttle
operator|.
name|preVisit
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|Expression
name|targetExpression
init|=
name|Expressions
operator|.
name|accept
argument_list|(
name|this
operator|.
name|targetExpression
argument_list|,
name|shuttle
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
name|Expressions
operator|.
name|acceptExpressions
argument_list|(
name|this
operator|.
name|expressions
argument_list|,
name|shuttle
argument_list|)
decl_stmt|;
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|,
name|targetExpression
argument_list|,
name|expressions
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|R
parameter_list|>
name|R
name|accept
parameter_list|(
name|Visitor
argument_list|<
name|R
argument_list|>
name|visitor
parameter_list|)
block|{
return|return
name|visitor
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|evaluate
parameter_list|(
name|Evaluator
name|evaluator
parameter_list|)
block|{
specifier|final
name|Object
name|target
decl_stmt|;
if|if
condition|(
name|targetExpression
operator|==
literal|null
condition|)
block|{
name|target
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|target
operator|=
name|targetExpression
operator|.
name|evaluate
argument_list|(
name|evaluator
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Object
index|[]
name|args
init|=
operator|new
name|Object
index|[
name|expressions
operator|.
name|size
argument_list|()
index|]
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
name|expressions
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|expression
init|=
name|expressions
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|args
index|[
name|i
index|]
operator|=
name|expression
operator|.
name|evaluate
argument_list|(
name|evaluator
argument_list|)
expr_stmt|;
block|}
try|try
block|{
return|return
name|method
operator|.
name|invoke
argument_list|(
name|target
argument_list|,
name|args
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
decl||
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"error while evaluating "
operator|+
name|this
argument_list|,
name|e
argument_list|)
throw|;
block|}
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
if|if
condition|(
name|writer
operator|.
name|requireParentheses
argument_list|(
name|this
argument_list|,
name|lprec
argument_list|,
name|rprec
argument_list|)
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|targetExpression
operator|!=
literal|null
condition|)
block|{
comment|// instance method
name|targetExpression
operator|.
name|accept
argument_list|(
name|writer
argument_list|,
name|lprec
argument_list|,
name|nodeType
operator|.
name|lprec
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// static method
name|writer
operator|.
name|append
argument_list|(
name|method
operator|.
name|getDeclaringClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
operator|.
name|append
argument_list|(
name|method
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|int
name|k
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Expression
name|expression
range|:
name|expressions
control|)
block|{
if|if
condition|(
name|k
operator|++
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|expression
operator|.
name|accept
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
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
name|MethodCallExpression
name|that
init|=
operator|(
name|MethodCallExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|expressions
operator|.
name|equals
argument_list|(
name|that
operator|.
name|expressions
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|method
operator|.
name|equals
argument_list|(
name|that
operator|.
name|method
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|targetExpression
operator|!=
literal|null
condition|?
operator|!
name|targetExpression
operator|.
name|equals
argument_list|(
name|that
operator|.
name|targetExpression
argument_list|)
else|:
name|that
operator|.
name|targetExpression
operator|!=
literal|null
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
name|Objects
operator|.
name|hash
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|,
name|method
argument_list|,
name|targetExpression
argument_list|,
name|expressions
argument_list|)
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
block|}
end_class

end_unit

