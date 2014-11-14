begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
import|;
end_import

begin_comment
comment|/**  * A namespace for tables and functions.  *  *<p>A schema can also contain sub-schemas, to any level of nesting. Most  * providers have a limited number of levels; for example, most JDBC databases  * have either one level ("schemas") or two levels ("database" and  * "catalog").</p>  *  *<p>There may be multiple overloaded functions with the same name but  * different numbers or types of parameters.  * For this reason, {@link #getFunctions} returns a list of all  * members with the same name. Calcite will call  * {@link Schemas#resolve(org.eigenbase.reltype.RelDataTypeFactory, String, java.util.Collection, java.util.List)}  * to choose the appropriate one.</p>  *  *<p>The most common and important type of member is the one with no  * arguments and a result type that is a collection of records. This is called a  *<dfn>relation</dfn>. It is equivalent to a table in a relational  * database.</p>  *  *<p>For example, the query</p>  *  *<blockquote>select * from sales.emps</blockquote>  *  *<p>is valid if "sales" is a registered  * schema and "emps" is a member with zero parameters and a result type  * of<code>Collection(Record(int: "empno", String: "name"))</code>.</p>  *  *<p>A schema may be nested within another schema; see  * {@link Schema#getSubSchema(String)}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Schema
block|{
comment|/**    * Returns a table with a given name, or null if not found.    *    * @param name Table name    * @return Table, or null    */
name|Table
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns the names of the tables in this schema.    */
name|Set
argument_list|<
name|String
argument_list|>
name|getTableNames
parameter_list|()
function_decl|;
comment|/**    * Returns a list of functions in this schema with the given name, or    * an empty list if there is no such function.    *    * @param name Name of function    * @return List of functions with given name, or empty list    */
name|Collection
argument_list|<
name|Function
argument_list|>
name|getFunctions
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns the names of the functions in this schema.    */
name|Set
argument_list|<
name|String
argument_list|>
name|getFunctionNames
parameter_list|()
function_decl|;
comment|/**    * Returns a sub-schema with a given name, or null.    */
name|Schema
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns the names of this schema's child schemas.    */
name|Set
argument_list|<
name|String
argument_list|>
name|getSubSchemaNames
parameter_list|()
function_decl|;
comment|/**    * Returns the expression by which this schema can be referenced in generated    * code.    *    * @param parentSchema Parent schema    * @param name Name of this schema    */
name|Expression
name|getExpression
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/** Returns whether the user is allowed to create new tables, functions    * and sub-schemas in this schema, in addition to those returned automatically    * by methods such as {@link #getTable(String)}.    *    *<p>Even if this method returns true, the maps are not modified. Calcite    * stores the defined objects in a wrapper object. */
name|boolean
name|isMutable
parameter_list|()
function_decl|;
comment|/** Returns whether the contents of this schema have changed since a given    * time. The time is a millisecond value, as returned by    * {@link System#currentTimeMillis()}. If this method returns true, and    * caching is enabled, Calcite will re-build caches.    *    *<p>The default implementation in    * {@link net.hydromatic.optiq.impl.AbstractSchema} always returns    * {@code false}.</p>    *    *<p>To control whether Calcite caches the contents of a schema, use the    * "cache" JSON attribute. The default value is "true".</p>    *    * @param lastCheck The last time that Calcite called this method, or    *   {@link Long#MIN_VALUE} if this is the first call    * @param now The current time in millis, as returned by    *   {@link System#currentTimeMillis()}    *    * @return Whether contents changed after {@code lastCheckMillis}.    */
name|boolean
name|contentsHaveChangedSince
parameter_list|(
name|long
name|lastCheck
parameter_list|,
name|long
name|now
parameter_list|)
function_decl|;
comment|/** Table type. */
enum|enum
name|TableType
block|{
comment|/** A regular table. */
name|TABLE
block|,
comment|/** A relation whose contents are calculated by evaluating a SQL      * expression. */
name|VIEW
block|,
comment|/** A table maintained by the system. Data dictionary tables, such as the      * "TABLES" and "COLUMNS" table in the "metamodel" schema, examples of      * system tables. */
name|SYSTEM_TABLE
block|,
comment|/** A table that is only visible to one connection. */
name|LOCAL_TEMPORARY
block|,
comment|/** A structure, similar to a view, that is the basis for auto-generated      * materializations. It is either a single table or a collection of tables      * that are joined via many-to-one relationships from a central hub table.      * It is not available for queries, but is just used as an intermediate      * structure during query planning. */
name|STAR
block|,
comment|/** Index table. (Used by Apache Phoenix.) */
name|INDEX
block|,
comment|/** Join table. (Used by Apache Phoenix.) */
name|JOIN
block|,   }
block|}
end_interface

begin_comment
comment|// End Schema.java
end_comment

end_unit

