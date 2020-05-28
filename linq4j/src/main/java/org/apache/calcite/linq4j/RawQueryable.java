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
name|tree
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|framework
operator|.
name|qual
operator|.
name|Covariant
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

begin_comment
comment|/**  * Core methods that define a {@link Queryable}.  *  *<p>The other methods in {@link Queryable}, defined in  * {@link ExtendedQueryable}, can easily be implemented by calling the  * corresponding static methods in {@link Extensions}.  *  * @param<T> Element type  */
end_comment

begin_interface
annotation|@
name|Covariant
argument_list|(
literal|0
argument_list|)
specifier|public
interface|interface
name|RawQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Enumerable
argument_list|<
name|T
argument_list|>
block|{
comment|/**    * Gets the type of the element(s) that are returned when the expression    * tree associated with this Queryable is executed.    */
name|Type
name|getElementType
parameter_list|()
function_decl|;
comment|/**    * Gets the expression tree that is associated with this Queryable.    * @return null if the expression is not available    */
annotation|@
name|Nullable
name|Expression
name|getExpression
parameter_list|()
function_decl|;
comment|/**    * Gets the query provider that is associated with this data source.    */
name|QueryProvider
name|getProvider
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

