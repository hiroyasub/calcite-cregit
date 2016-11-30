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
name|sql
operator|.
name|validate
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|sql
operator|.
name|SqlNode
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
name|util
operator|.
name|Pair
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
comment|/**  * A namespace describes the relation returned by a section of a SQL query.  *  *<p>For example, in the query<code>SELECT emp.deptno, age FROM emp,  * dept</code>, the FROM clause forms a namespace consisting of two tables EMP  * and DEPT, and a row type consisting of the combined columns of those tables.  *  *<p>Other examples of namespaces include a table in the from list (the  * namespace contains the constituent columns) and a sub-query (the namespace  * contains the columns in the SELECT clause of the sub-query).  *  *<p>These various kinds of namespace are implemented by classes  * {@link IdentifierNamespace} for table names, {@link SelectNamespace} for  * SELECT queries, {@link SetopNamespace} for UNION, EXCEPT and INTERSECT, and  * so forth. But if you are looking at a SELECT query and call  * {@link SqlValidator#getNamespace(org.apache.calcite.sql.SqlNode)}, you may  * not get a SelectNamespace. Why? Because the validator is allowed to wrap  * namespaces in other objects which implement  * {@link SqlValidatorNamespace}. Your SelectNamespace will be there somewhere,  * but might be one or two levels deep.  Don't try to cast the namespace or use  *<code>instanceof</code>; use {@link SqlValidatorNamespace#unwrap(Class)} and  * {@link SqlValidatorNamespace#isWrapperFor(Class)} instead.</p>  *  * @see SqlValidator  * @see SqlValidatorScope  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlValidatorNamespace
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the validator.    *    * @return validator    */
name|SqlValidator
name|getValidator
parameter_list|()
function_decl|;
comment|/**    * Returns the underlying table, or null if there is none.    */
name|SqlValidatorTable
name|getTable
parameter_list|()
function_decl|;
comment|/**    * Returns the row type of this namespace, which comprises a list of names    * and types of the output columns. If the scope's type has not yet been    * derived, derives it.    *    * @return Row type of this namespace, never null, always a struct    */
name|RelDataType
name|getRowType
parameter_list|()
function_decl|;
comment|/**    * Returns the type of this namespace.    *    * @return Row type converted to struct    */
name|RelDataType
name|getType
parameter_list|()
function_decl|;
comment|/**    * Sets the type of this namespace.    *    *<p>Allows the type for the namespace to be explicitly set, but usually is    * called during {@link #validate(RelDataType)}.</p>    *    *<p>Implicitly also sets the row type. If the type is not a struct, then    * the row type is the type wrapped as a struct with a single column,    * otherwise the type and row type are the same.</p>    */
name|void
name|setType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
function_decl|;
comment|/**    * Returns the row type of this namespace, sans any system columns.    *    * @return Row type sans system columns    */
name|RelDataType
name|getRowTypeSansSystemColumns
parameter_list|()
function_decl|;
comment|/**    * Validates this namespace.    *    *<p>If the scope has already been validated, does nothing.</p>    *    *<p>Please call {@link SqlValidatorImpl#validateNamespace} rather than    * calling this method directly.</p>    *    * @param targetRowType Desired row type, must not be null, may be the data    *                      type 'unknown'.    */
name|void
name|validate
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|)
function_decl|;
comment|/**    * Returns the parse tree node at the root of this namespace.    *    * @return parse tree node; null for {@link TableNamespace}    */
name|SqlNode
name|getNode
parameter_list|()
function_decl|;
comment|/**    * Returns the parse tree node that at is at the root of this namespace and    * includes all decorations. If there are no decorations, returns the same    * as {@link #getNode()}.    */
name|SqlNode
name|getEnclosingNode
parameter_list|()
function_decl|;
comment|/**    * Looks up a child namespace of a given name.    *    *<p>For example, in the query<code>select e.name from emps as e</code>,    *<code>e</code> is an {@link IdentifierNamespace} which has a child<code>    * name</code> which is a {@link FieldNamespace}.    *    * @param name Name of namespace    * @return Namespace    */
name|SqlValidatorNamespace
name|lookupChild
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns whether this namespace has a field of a given name.    *    * @param name Field name    * @return Whether field exists    */
name|boolean
name|fieldExists
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns a list of expressions which are monotonic in this namespace. For    * example, if the namespace represents a relation ordered by a column    * called "TIMESTAMP", then the list would contain a    * {@link org.apache.calcite.sql.SqlIdentifier} called "TIMESTAMP".    */
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
argument_list|>
name|getMonotonicExprs
parameter_list|()
function_decl|;
comment|/**    * Returns whether and how a given column is sorted.    */
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
function_decl|;
comment|/**    * Makes all fields in this namespace nullable (typically because it is on    * the outer side of an outer join.    */
name|void
name|makeNullable
parameter_list|()
function_decl|;
comment|/**    * Translates a field name to the name in the underlying namespace.    */
name|String
name|translate
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns this namespace, or a wrapped namespace, cast to a particular    * class.    *    * @param clazz Desired type    * @return This namespace cast to desired type    * @throws ClassCastException if no such interface is available    */
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
function_decl|;
comment|/**    * Returns whether this namespace implements a given interface, or wraps a    * class which does.    *    * @param clazz Interface    * @return Whether namespace implements given interface    */
name|boolean
name|isWrapperFor
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
function_decl|;
comment|/** If this namespace resolves to another namespace, returns that namespace,    * following links to the end of the chain.    *    *<p>A {@code WITH}) clause defines table names that resolve to queries    * (the body of the with-item). An {@link IdentifierNamespace} typically    * resolves to a {@link TableNamespace}.</p>    *    *<p>You must not call this method before {@link #validate(RelDataType)} has    * completed.</p> */
name|SqlValidatorNamespace
name|resolve
parameter_list|()
function_decl|;
comment|/** Returns whether this namespace is capable of giving results of the desired    * modality. {@code true} means streaming, {@code false} means relational.    *    * @param modality Modality    */
name|boolean
name|supportsModality
parameter_list|(
name|SqlModality
name|modality
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlValidatorNamespace.java
end_comment

end_unit

