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
name|Type
import|;
end_import

begin_comment
comment|/**  * Abstract implementation of {@link Node}.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractNode
implements|implements
name|Node
block|{
specifier|public
specifier|final
name|ExpressionType
name|nodeType
decl_stmt|;
specifier|public
specifier|final
name|Type
name|type
decl_stmt|;
name|AbstractNode
parameter_list|(
name|ExpressionType
name|nodeType
parameter_list|,
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|nodeType
operator|=
name|nodeType
expr_stmt|;
block|}
comment|/**    * Gets the node type of this Expression.    */
specifier|public
name|ExpressionType
name|getNodeType
parameter_list|()
block|{
return|return
name|nodeType
return|;
block|}
comment|/**    * Gets the static type of the expression that this Expression    * represents.    */
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|ExpressionWriter
name|writer
init|=
operator|new
name|ExpressionWriter
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|accept
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|)
block|{
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
name|void
name|accept0
parameter_list|(
name|ExpressionWriter
name|writer
parameter_list|)
block|{
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"un-parse not supported: "
operator|+
name|getClass
argument_list|()
operator|+
literal|":"
operator|+
name|nodeType
argument_list|)
throw|;
block|}
specifier|public
name|Node
name|accept
parameter_list|(
name|Visitor
name|visitor
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"visit not supported: "
operator|+
name|getClass
argument_list|()
operator|+
literal|":"
operator|+
name|nodeType
argument_list|)
throw|;
block|}
specifier|public
name|Object
name|evaluate
parameter_list|(
name|Evaluator
name|evaluator
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"evaluation not supported: "
operator|+
name|getClass
argument_list|()
operator|+
literal|":"
operator|+
name|nodeType
argument_list|)
throw|;
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
name|AbstractNode
name|that
init|=
operator|(
name|AbstractNode
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|nodeType
operator|!=
name|that
operator|.
name|nodeType
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|?
operator|!
name|type
operator|.
name|equals
argument_list|(
name|that
operator|.
name|type
argument_list|)
else|:
name|that
operator|.
name|type
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
name|nodeType
operator|!=
literal|null
condition|?
name|nodeType
operator|.
name|hashCode
argument_list|()
else|:
literal|0
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
operator|(
name|type
operator|!=
literal|null
condition|?
name|type
operator|.
name|hashCode
argument_list|()
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractNode.java
end_comment

end_unit

