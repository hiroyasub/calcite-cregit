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

begin_comment
comment|/**  * Defines methods to create and execute queries that are described by a  * {@link Queryable} object.  *  *<p>Analogous to LINQ's System.Linq.QueryProvider.</p>  *  * @author jhyde  */
end_comment

begin_interface
specifier|public
interface|interface
name|QueryProvider
block|{
comment|/**    * Constructs a {@link Queryable} object that can evaluate the query    * represented by a specified expression tree.    *    *<p>NOTE: The {@link net.hydromatic.linq4j.Queryable#getExpression()}    * property of the returned {@link Queryable} object is equal to    * {@code expression}.</p>    *    * @param expression Expression    * @param rowType Row type    * @param<T> Row type    *    * @return Queryable    */
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|createQuery
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|rowType
parameter_list|)
function_decl|;
comment|/**    * Constructs a {@link Queryable} object that can evaluate the query    * represented by a specified expression tree. The row type may contain    * generic information.    *    * @param expression Expression    * @param rowType Row type    * @param<T> Row type    *    * @return Queryable    */
parameter_list|<
name|T
parameter_list|>
name|Queryable
argument_list|<
name|T
argument_list|>
name|createQuery
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Type
name|rowType
parameter_list|)
function_decl|;
comment|/**    * Executes the query represented by a specified expression tree.    *    *<p>This method executes queries that return a single value    * (instead of an enumerable sequence of values). Expression trees that    * represent queries that return enumerable results are executed when the    * {@link Queryable} object that contains the expression tree is    * enumerated.</p>    *    *<p>The Queryable standard query operator methods that return singleton    * results call {@code execute}. They pass it a    * {@link net.hydromatic.linq4j.expressions.MethodCallExpression}    * that represents a linq4j query.    */
parameter_list|<
name|T
parameter_list|>
name|T
name|execute
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
comment|/**    * Executes the query represented by a specified expression tree.    * The row type may contain type parameters.    */
parameter_list|<
name|T
parameter_list|>
name|T
name|execute
parameter_list|(
name|Expression
name|expression
parameter_list|,
name|Type
name|type
parameter_list|)
function_decl|;
comment|/**    * Executes a queryable, and returns an enumerator over the    * rows that it yields.    *    * @param queryable Queryable    *    * @return Enumerator over rows    */
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|executeQuery
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End QueryProvider.java
end_comment

end_unit

