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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * Represents a named parameter expression.  */
end_comment

begin_class
specifier|public
class|class
name|ParameterExpression
extends|extends
name|Expression
block|{
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|SEQ
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|int
name|modifier
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|ParameterExpression
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|this
argument_list|(
literal|0
argument_list|,
name|type
argument_list|,
literal|"p"
operator|+
name|SEQ
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ParameterExpression
parameter_list|(
name|int
name|modifier
parameter_list|,
name|Type
name|type
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|ExpressionType
operator|.
name|Parameter
argument_list|,
name|type
argument_list|)
expr_stmt|;
assert|assert
name|name
operator|!=
literal|null
operator|:
literal|"name should not be null"
assert|;
assert|assert
name|Character
operator|.
name|isJavaIdentifierStart
argument_list|(
name|name
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|:
literal|"parameter name should be valid java identifier: "
operator|+
name|name
operator|+
literal|". The first character is invalid."
assert|;
name|this
operator|.
name|modifier
operator|=
name|modifier
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
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
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
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
specifier|public
name|Object
name|evaluate
parameter_list|(
name|Evaluator
name|evaluator
parameter_list|)
block|{
return|return
name|evaluator
operator|.
name|peek
argument_list|(
name|this
argument_list|)
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
name|writer
operator|.
name|append
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|String
name|declString
parameter_list|()
block|{
return|return
name|declString
argument_list|(
name|type
argument_list|)
return|;
block|}
name|String
name|declString
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
specifier|final
name|String
name|modifiers
init|=
name|Modifier
operator|.
name|toString
argument_list|(
name|modifier
argument_list|)
decl_stmt|;
return|return
name|modifiers
operator|+
operator|(
name|modifiers
operator|.
name|isEmpty
argument_list|()
condition|?
literal|""
else|:
literal|" "
operator|)
operator|+
name|Types
operator|.
name|className
argument_list|(
name|type
argument_list|)
operator|+
literal|" "
operator|+
name|name
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
return|return
name|this
operator|==
name|o
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|System
operator|.
name|identityHashCode
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

