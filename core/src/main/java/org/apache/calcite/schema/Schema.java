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
name|tree
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
name|Set
import|;
end_import

begin_comment
comment|/**  * A namespace for tables and functions.  *  *<p>A schema can also contain sub-schemas, to any level of nesting. Most  * providers have a limited number of levels; for example, most JDBC databases  * have either one level ("schemas") or two levels ("database" and  * "catalog").</p>  *  *<p>There may be multiple overloaded functions with the same name but  * different numbers or types of parameters.  * For this reason, {@link #getFunctions} returns a list of all  * members with the same name. Calcite will call  * {@link Schemas#resolve(org.apache.calcite.rel.type.RelDataTypeFactory, String, java.util.Collection, java.util.List)}  * to choose the appropriate one.</p>  *  *<p>The most common and important type of member is the one with no  * arguments and a result type that is a collection of records. This is called a  *<dfn>relation</dfn>. It is equivalent to a table in a relational  * database.</p>  *  *<p>For example, the query</p>  *  *<blockquote>select * from sales.emps</blockquote>  *  *<p>is valid if "sales" is a registered  * schema and "emps" is a member with zero parameters and a result type  * of<code>Collection(Record(int: "empno", String: "name"))</code>.</p>  *  *<p>A schema may be nested within another schema; see  * {@link Schema#getSubSchema(String)}.</p>  */
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
comment|/**    * Returns the names of the tables in this schema.    *    * @return Names of the tables in this schema    */
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
comment|/**    * Returns the names of the functions in this schema.    *    * @return Names of the functions in this schema    */
name|Set
argument_list|<
name|String
argument_list|>
name|getFunctionNames
parameter_list|()
function_decl|;
comment|/**    * Returns a sub-schema with a given name, or null.    *    * @param name Sub-schema name    * @return Sub-schema with a given name, or null    */
name|Schema
name|getSubSchema
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns the names of this schema's child schemas.    *    * @return Names of this schema's child schemas    */
name|Set
argument_list|<
name|String
argument_list|>
name|getSubSchemaNames
parameter_list|()
function_decl|;
comment|/**    * Returns the expression by which this schema can be referenced in generated    * code.    *    * @param parentSchema Parent schema    * @param name Name of this schema    * @return Expression by which this schema can be referenced in generated code    */
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
comment|/** Returns whether the user is allowed to create new tables, functions    * and sub-schemas in this schema, in addition to those returned automatically    * by methods such as {@link #getTable(String)}.    *    *<p>Even if this method returns true, the maps are not modified. Calcite    * stores the defined objects in a wrapper object.    *    * @return Whether the user is allowed to create new tables, functions    *   and sub-schemas in this schema    */
name|boolean
name|isMutable
parameter_list|()
function_decl|;
comment|/** Returns whether the contents of this schema have changed since a given    * time. The time is a millisecond value, as returned by    * {@link System#currentTimeMillis()}. If this method returns true, and    * caching is enabled, Calcite will re-build caches.    *    *<p>The default implementation in    * {@link org.apache.calcite.schema.impl.AbstractSchema} always returns    * {@code false}.</p>    *    *<p>To control whether Calcite caches the contents of a schema, use the    * "cache" JSON attribute. The default value is "true".</p>    *    * @param lastCheck The last time that Calcite called this method, or    *   {@link Long#MIN_VALUE} if this is the first call    * @param now The current time in millis, as returned by    *   {@link System#currentTimeMillis()}    *    * @return Whether contents changed after {@code lastCheckMillis}.    */
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
comment|/** A regular table.      *      *<p>Used by DB2, MySQL, PostgreSQL and others. */
name|TABLE
block|,
comment|/** A relation whose contents are calculated by evaluating a SQL      * expression.      *      *<p>Used by DB2, PostgreSQL and others. */
name|VIEW
block|,
comment|/** Foreign table.      *      *<p>Used by PostgreSQL. */
name|FOREIGN_TABLE
block|,
comment|/** Materialized view.      *      *<p>Used by PostgreSQL. */
name|MATERIALIZED_VIEW
block|,
comment|/** Index table.      *      *<p>Used by Apache Phoenix, PostgreSQL. */
name|INDEX
block|,
comment|/** Join table.      *      *<p>Used by Apache Phoenix. */
name|JOIN
block|,
comment|/** Sequence table.      *      *<p>Used by Apache Phoenix, Oracle, PostgreSQL and others.      * In Phoenix, must have a single BIGINT column called "$seq". */
name|SEQUENCE
block|,
comment|/** A structure, similar to a view, that is the basis for auto-generated      * materializations. It is either a single table or a collection of tables      * that are joined via many-to-one relationships from a central hub table.      * It is not available for queries, but is just used as an intermediate      * structure during query planning. */
name|STAR
block|,
comment|/** Stream. */
name|STREAM
block|,
comment|/** Type.      *      *<p>Used by PostgreSQL. */
name|TYPE
block|,
comment|/** A table maintained by the system. Data dictionary tables, such as the      * "TABLES" and "COLUMNS" table in the "metamodel" schema, examples of      * system tables.      *      *<p>Specified by the JDBC standard and used by DB2, MySQL, Oracle,      * PostgreSQL and others. */
name|SYSTEM_TABLE
block|,
comment|/** System view.      *      *<p>Used by PostgreSQL, MySQL. */
name|SYSTEM_VIEW
block|,
comment|/** System index.      *      *<p>Used by PostgreSQL. */
name|SYSTEM_INDEX
block|,
comment|/** System TOAST index.      *      *<p>Used by PostgreSQL. */
name|SYSTEM_TOAST_INDEX
block|,
comment|/** System TOAST table.      *      *<p>Used by PostgreSQL. */
name|SYSTEM_TOAST_TABLE
block|,
comment|/** Temporary index.      *      *<p>Used by PostgreSQL. */
name|TEMPORARY_INDEX
block|,
comment|/** Temporary sequence.      *      *<p>Used by PostgreSQL. */
name|TEMPORARY_SEQUENCE
block|,
comment|/** Temporary table.      *      *<p>Used by PostgreSQL. */
name|TEMPORARY_TABLE
block|,
comment|/** Temporary view.      *      *<p>Used by PostgreSQL. */
name|TEMPORARY_VIEW
block|,
comment|/** A table that is only visible to one connection.      *      *<p>Specified by the JDBC standard and used by PostgreSQL, MySQL. */
name|LOCAL_TEMPORARY
block|,
comment|/** A synonym.      *      *<p>Used by DB2, Oracle. */
name|SYNONYM
block|,
comment|/** An alias.      *      *<p>Specified by the JDBC standard. */
name|ALIAS
block|,
comment|/** A global temporary table.      *      *<p>Specified by the JDBC standard. */
name|GLOBAL_TEMPORARY
block|,
comment|/** An accel-only table.      *      *<p>Used by DB2.      */
name|ACCEL_ONLY_TABLE
block|,
comment|/** An auxiliary table.      *      *<p>Used by DB2.      */
name|AUXILIARY_TABLE
block|,
comment|/** A global temporary table.      *      *<p>Used by DB2.      */
name|GLOBAL_TEMPORARY_TABLE
block|,
comment|/** A hierarchy table.      *      *<p>Used by DB2.      */
name|HIERARCHY_TABLE
block|,
comment|/** An inoperative view.      *      *<p>Used by DB2.      */
name|INOPERATIVE_VIEW
block|,
comment|/** A materialized query table.      *      *<p>Used by DB2.      */
name|MATERIALIZED_QUERY_TABLE
block|,
comment|/** A nickname.      *      *<p>Used by DB2.      */
name|NICKNAME
block|,
comment|/** A typed table.      *      *<p>Used by DB2.      */
name|TYPED_TABLE
block|,
comment|/** A typed view.      *      *<p>Used by DB2.      */
name|TYPED_VIEW
block|,
comment|/** Table type not known to Calcite.      *      *<p>If you get one of these, please fix the problem by adding an enum      * value. */
name|OTHER
block|,   }
block|}
end_interface

begin_comment
comment|// End Schema.java
end_comment

end_unit

