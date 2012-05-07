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
name|optiq
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
name|Map
import|;
end_import

begin_comment
comment|/**  * A namespace that returns relations and functions.  *  *<p>Most of the time, {@link #get(String)} return a {@link Function}.  * The most common and important type of Function is the one with no  * arguments and a result type that is a collection of records. It is  * equivalent to a table in a relational database.</p>  *  *<p>For example, the query</p>  *  *<blockquote>select * from sales.emps</blockquote>  *  *<p>is valid if "sales" is a registered  * schema and "emps" is a function with zero parameters and a result type  * of {@code Collection(Record(int: "empno", String: "name"))}.</p>  *  *<p>If there are overloaded functions, then there may be more than one  * object with a particular name. In this case, {@link #get} returns  * an {@link Overload}.</p>  *  *<p>A schema is a {@link SchemaObject}, which implies that schemas can  * be nested within schemas.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Schema
block|{
name|SchemaObject
name|get
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
name|Map
argument_list|<
name|String
argument_list|,
name|SchemaObject
argument_list|>
name|asMap
parameter_list|()
function_decl|;
comment|/**      * Returns the expression for a sub-object "name" or "name(argument, ...)",      * given an expression for the schema.      *      *<p>For example, given schema based on reflection, if the schema is      * based on an object of type "class Foodmart" called "foodmart", then      * the "emps" member would be accessed via "foodmart.emps". If the schema      * were a map, the expression would be      * "(Enumerable&lt;Employee&gt;) foodmart.get(&quot;emps&quot;)".</p>      *      * @param schemaExpression Expression for schema      * @param name Name of schema object      * @param arguments Arguments to schema object (null means no argument list)      * @return Expression for a given schema object      */
name|Expression
name|getExpression
parameter_list|(
name|Expression
name|schemaExpression
parameter_list|,
name|SchemaObject
name|schemaObject
parameter_list|,
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End Schema.java
end_comment

end_unit

