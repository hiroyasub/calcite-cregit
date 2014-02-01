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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|FunctionExpression
import|;
end_import

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
name|util
operator|.
name|Comparator
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link OrderedQueryable} by an  * {@link net.hydromatic.linq4j.Enumerable}.  *  * @param<T> Element type  */
end_comment

begin_class
class|class
name|EnumerableOrderedQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|EnumerableQueryable
argument_list|<
name|T
argument_list|>
implements|implements
name|OrderedQueryable
argument_list|<
name|T
argument_list|>
block|{
name|EnumerableOrderedQueryable
parameter_list|(
name|Enumerable
argument_list|<
name|T
argument_list|>
name|enumerable
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|rowType
parameter_list|,
name|QueryProvider
name|provider
parameter_list|,
name|Expression
name|expression
parameter_list|)
block|{
name|super
argument_list|(
name|provider
argument_list|,
name|rowType
argument_list|,
name|expression
argument_list|,
name|enumerable
argument_list|)
expr_stmt|;
block|}
specifier|public
parameter_list|<
name|TKey
extends|extends
name|Comparable
argument_list|<
name|TKey
argument_list|>
parameter_list|>
name|OrderedQueryable
argument_list|<
name|T
argument_list|>
name|thenBy
parameter_list|(
name|FunctionExpression
argument_list|<
name|Function1
argument_list|<
name|T
argument_list|,
name|TKey
argument_list|>
argument_list|>
name|keySelector
parameter_list|)
block|{
return|return
name|QueryableDefaults
operator|.
name|thenBy
argument_list|(
name|asOrderedQueryable
argument_list|()
argument_list|,
name|keySelector
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|TKey
parameter_list|>
name|OrderedQueryable
argument_list|<
name|T
argument_list|>
name|thenBy
parameter_list|(
name|FunctionExpression
argument_list|<
name|Function1
argument_list|<
name|T
argument_list|,
name|TKey
argument_list|>
argument_list|>
name|keySelector
parameter_list|,
name|Comparator
argument_list|<
name|TKey
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|QueryableDefaults
operator|.
name|thenBy
argument_list|(
name|asOrderedQueryable
argument_list|()
argument_list|,
name|keySelector
argument_list|,
name|comparator
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|TKey
extends|extends
name|Comparable
argument_list|<
name|TKey
argument_list|>
parameter_list|>
name|OrderedQueryable
argument_list|<
name|T
argument_list|>
name|thenByDescending
parameter_list|(
name|FunctionExpression
argument_list|<
name|Function1
argument_list|<
name|T
argument_list|,
name|TKey
argument_list|>
argument_list|>
name|keySelector
parameter_list|)
block|{
return|return
name|QueryableDefaults
operator|.
name|thenByDescending
argument_list|(
name|asOrderedQueryable
argument_list|()
argument_list|,
name|keySelector
argument_list|)
return|;
block|}
specifier|public
parameter_list|<
name|TKey
parameter_list|>
name|OrderedQueryable
argument_list|<
name|T
argument_list|>
name|thenByDescending
parameter_list|(
name|FunctionExpression
argument_list|<
name|Function1
argument_list|<
name|T
argument_list|,
name|TKey
argument_list|>
argument_list|>
name|keySelector
parameter_list|,
name|Comparator
argument_list|<
name|TKey
argument_list|>
name|comparator
parameter_list|)
block|{
return|return
name|QueryableDefaults
operator|.
name|thenByDescending
argument_list|(
name|asOrderedQueryable
argument_list|()
argument_list|,
name|keySelector
argument_list|,
name|comparator
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End EnumerableOrderedQueryable.java
end_comment

end_unit

