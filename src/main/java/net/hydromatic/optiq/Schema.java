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
name|QueryProvider
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
name|Expression
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
comment|/**  * A namespace for tables and table functions.  *  *<p>A schema can also contain sub-schemas, to any level of nesting. Most  * providers have a limited number of levels; for example, most JDBC databases  * have either one level ("schemas") or two levels ("database" and  * "catalog").</p>  *  *<p>There may be multiple overloaded table functions with the same name but  * different numbers or types of parameters.  * For this reason, {@link #getTableFunctions} returns a list of all  * members with the same name. Optiq will call  * {@link Schemas#resolve(String, java.util.List, java.util.List)} to choose the  * appropriate one.</p>  *  *<p>The most common and important type of member is the one with no  * arguments and a result type that is a collection of records. This is called a  *<dfn>relation</dfn>. It is equivalent to a table in a relational  * database.</p>  *  *<p>For example, the query</p>  *  *<blockquote>select * from sales.emps</blockquote>  *  *<p>is valid if "sales" is a registered  * schema and "emps" is a member with zero parameters and a result type  * of<code>Collection(Record(int: "empno", String: "name"))</code>.</p>  *  *<p>A schema may be nested within another schema; see  * {@link Schema#getSubSchema(String)}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Schema
extends|extends
name|DataContext
block|{
comment|/**      * Returns a list of table functions in this schema with the given name, or      * an empty list if there is no such table function.      *      * @param name Name of table function      * @return List of table functions with given name, or empty list      */
name|List
argument_list|<
name|TableFunction
argument_list|>
name|getTableFunctions
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Returns a table with the given name, or null.      *      * @param name Table name      * @param elementType Element type      * @return Table, or null      */
parameter_list|<
name|E
parameter_list|>
name|Table
argument_list|<
name|E
argument_list|>
name|getTable
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|elementType
parameter_list|)
function_decl|;
name|Expression
name|getExpression
parameter_list|()
function_decl|;
name|QueryProvider
name|getQueryProvider
parameter_list|()
function_decl|;
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|TableFunction
argument_list|>
argument_list|>
name|getTableFunctions
parameter_list|()
function_decl|;
name|Collection
argument_list|<
name|String
argument_list|>
name|getSubSchemaNames
parameter_list|()
function_decl|;
name|Collection
argument_list|<
name|String
argument_list|>
name|getTableNames
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Schema.java
end_comment

end_unit

