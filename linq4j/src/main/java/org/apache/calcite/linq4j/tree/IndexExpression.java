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
comment|/**  * Represents indexing a property or array.  */
end_comment

begin_class
specifier|public
class|class
name|IndexExpression
extends|extends
name|Expression
block|{
specifier|public
specifier|final
name|Expression
name|array
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|indexExpressions
decl_stmt|;
specifier|public
name|IndexExpression
parameter_list|(
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
name|super
argument_list|(
name|ExpressionType
operator|.
name|ArrayIndex
argument_list|,
name|Types
operator|.
name|getComponentType
argument_list|(
name|array
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
assert|assert
name|array
operator|!=
literal|null
operator|:
literal|"array should not be null"
assert|;
assert|assert
name|indexExpressions
operator|!=
literal|null
operator|:
literal|"indexExpressions should not be null"
assert|;
assert|assert
operator|!
name|indexExpressions
operator|.
name|isEmpty
argument_list|()
operator|:
literal|"indexExpressions should not be empty"
assert|;
name|this
operator|.
name|array
operator|=
name|array
expr_stmt|;
name|this
operator|.
name|indexExpressions
operator|=
name|indexExpressions
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
name|array
init|=
name|this
operator|.
name|array
operator|.
name|accept
argument_list|(
name|shuttle
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|indexExpressions
init|=
name|Expressions
operator|.
name|acceptExpressions
argument_list|(
name|this
operator|.
name|indexExpressions
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
name|array
argument_list|,
name|indexExpressions
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
name|array
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
name|writer
operator|.
name|list
argument_list|(
literal|"["
argument_list|,
literal|", "
argument_list|,
literal|"]"
argument_list|,
name|indexExpressions
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
name|IndexExpression
name|that
init|=
operator|(
name|IndexExpression
operator|)
name|o
decl_stmt|;
if|if
condition|(
operator|!
name|array
operator|.
name|equals
argument_list|(
name|that
operator|.
name|array
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
name|indexExpressions
operator|.
name|equals
argument_list|(
name|that
operator|.
name|indexExpressions
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
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|nodeType
argument_list|,
name|type
argument_list|,
name|array
argument_list|,
name|indexExpressions
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End IndexExpression.java
end_comment

end_unit

