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
name|schema
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
name|Queryable
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
comment|/**  * A named expression in a schema.  *  *<h2>Examples of members</h2>  *  *<p>Several kinds of members crop up in real life. They all implement the  * {@code Member} interface, but tend to be treated differently by the  * back-end system if not by Calcite.</p>  *  *<p>A member that has zero arguments and a type that is a collection of  * records is referred to as a<i>relation</i>. In schemas backed by a  * relational database, tables and views will appear as relations.</p>  *  *<p>A member that has one or more arguments and a type that is a collection  * of records is referred to as a<i>parameterized relation</i>. Some relational  * databases support these; for example, Oracle calls them "table  * functions".</p>  *  *<p>Members may be also more typical of programming-language functions:  * they take zero or more arguments, and return a result of arbitrary type.</p>  *  *<p>From the above definitions, you can see that a member is a special  * kind of function. This makes sense, because even though it has no  * arguments, it is "evaluated" each time it is used in a query.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Member
block|{
comment|/**    * The name of this function.    */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**    * Returns the parameters of this member.    *    * @return Parameters; never null    */
name|List
argument_list|<
name|FunctionParameter
argument_list|>
name|getParameters
parameter_list|()
function_decl|;
comment|/**    * Returns the type of this function's result.    *    * @return Type of result; never null    */
name|RelDataType
name|getType
parameter_list|()
function_decl|;
comment|/**    * Evaluates this member to yield a result. The result is a    * {@link org.apache.calcite.linq4j.Queryable}.    *    * @param schemaInstance Object that is an instance of the containing    *                       {@link Schema}    * @param arguments List of arguments to the call; must match    *                  {@link #getParameters() parameters} in number and type    *    * @return An instance of this schema object, as a Queryable    */
name|Queryable
name|evaluate
parameter_list|(
name|Object
name|schemaInstance
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|arguments
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

