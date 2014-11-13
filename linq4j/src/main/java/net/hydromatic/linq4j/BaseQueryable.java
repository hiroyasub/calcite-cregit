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
name|expressions
operator|.
name|Expression
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
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Skeleton implementation of {@link Queryable}.  *  *<p>The default implementation of {@link #enumerator()} calls the provider's  * {@link QueryProvider#executeQuery(Queryable)} method, but the derived class  * can override.  *  * @param<TSource> Element type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BaseQueryable
parameter_list|<
name|TSource
parameter_list|>
extends|extends
name|AbstractQueryable
argument_list|<
name|TSource
argument_list|>
block|{
specifier|protected
specifier|final
name|QueryProvider
name|provider
decl_stmt|;
specifier|protected
specifier|final
name|Type
name|elementType
decl_stmt|;
specifier|protected
specifier|final
name|Expression
name|expression
decl_stmt|;
specifier|public
name|BaseQueryable
parameter_list|(
name|QueryProvider
name|provider
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|this
operator|.
name|provider
operator|=
name|provider
expr_stmt|;
name|this
operator|.
name|elementType
operator|=
name|elementType
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
block|}
specifier|public
name|QueryProvider
name|getProvider
parameter_list|()
block|{
return|return
name|provider
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|elementType
return|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
return|return
name|expression
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|TSource
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|enumeratorIterator
argument_list|(
name|enumerator
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|TSource
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|provider
operator|.
name|executeQuery
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End BaseQueryable.java
end_comment

end_unit

