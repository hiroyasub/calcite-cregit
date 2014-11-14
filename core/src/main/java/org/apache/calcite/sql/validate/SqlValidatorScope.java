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
name|SqlCall
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
name|SqlIdentifier
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
name|sql
operator|.
name|SqlNodeList
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
name|SqlWindow
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
comment|/**  * Name-resolution scope. Represents any position in a parse tree than an  * expression can be, or anything in the parse tree which has columns.  *  *<p>When validating an expression, say "foo"."bar", you first use the  * {@link #resolve} method of the scope where the expression is defined to  * locate "foo". If successful, this returns a  * {@link SqlValidatorNamespace namespace} describing the type of the resulting  * object.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlValidatorScope
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the validator which created this scope.    */
name|SqlValidator
name|getValidator
parameter_list|()
function_decl|;
comment|/**    * Returns the root node of this scope. Never null.    */
name|SqlNode
name|getNode
parameter_list|()
function_decl|;
comment|/**    * Looks up a node with a given name. Returns null if none is found.    *    * @param name        Name of node to find    * @param ancestorOut If not null, writes the ancestor scope here    * @param offsetOut   If not null, writes the offset within the ancestor here    */
name|SqlValidatorNamespace
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlValidatorScope
index|[]
name|ancestorOut
parameter_list|,
name|int
index|[]
name|offsetOut
parameter_list|)
function_decl|;
comment|/**    * Finds the table alias which is implicitly qualifying an unqualified    * column name. Throws an error if there is not exactly one table.    *    *<p>This method is only implemented in scopes (such as    * {@link org.apache.calcite.sql.validate.SelectScope}) which can be the    * context for name-resolution. In scopes such as    * {@link org.apache.calcite.sql.validate.IdentifierNamespace}, it throws    * {@link UnsupportedOperationException}.</p>    *    * @param columnName Column name    * @param ctx        Validation context, to appear in any error thrown    * @return Table alias    */
name|String
name|findQualifyingTableName
parameter_list|(
name|String
name|columnName
parameter_list|,
name|SqlNode
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Collects the {@link SqlMoniker}s of all possible columns in this scope.    *    * @param result an array list of strings to add the result to    */
name|void
name|findAllColumnNames
parameter_list|(
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|result
parameter_list|)
function_decl|;
comment|/**    * Collects the {@link SqlMoniker}s of all table aliases (uses of tables in    * query FROM clauses) available in this scope.    *    * @param result a list of monikers to add the result to    */
name|void
name|findAliases
parameter_list|(
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|result
parameter_list|)
function_decl|;
comment|/**    * Converts an identifier into a fully-qualified identifier. For example,    * the "empno" in "select empno from emp natural join dept" becomes    * "emp.empno".    */
name|SqlIdentifier
name|fullyQualify
parameter_list|(
name|SqlIdentifier
name|identifier
parameter_list|)
function_decl|;
comment|/**    * Registers a relation in this scope.    *    * @param ns    Namespace representing the result-columns of the relation    * @param alias Alias with which to reference the relation, must not be null    */
name|void
name|addChild
parameter_list|(
name|SqlValidatorNamespace
name|ns
parameter_list|,
name|String
name|alias
parameter_list|)
function_decl|;
comment|/**    * Finds a window with a given name. Returns null if not found.    */
name|SqlWindow
name|lookupWindow
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Returns whether an expression is monotonic in this scope. For example, if    * the scope has previously been sorted by columns X, Y, then X is monotonic    * in this scope, but Y is not.    */
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
function_decl|;
comment|/**    * Returns the expressions by which the rows in this scope are sorted. If    * the rows are unsorted, returns null.    */
name|SqlNodeList
name|getOrderList
parameter_list|()
function_decl|;
comment|/**    * Resolves a single identifier to a column, and returns the datatype of    * that column.    *    *<p>If it cannot find the column, returns null. If the column is    * ambiguous, throws an error with context<code>ctx</code>.    *    * @param name Name of column    * @param ctx  Context for exception    * @return Type of column, if found and unambiguous; null if not found    */
name|RelDataType
name|resolveColumn
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlNode
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Returns the scope within which operands to a call are to be validated.    * Usually it is this scope, but when the call is to an aggregate function    * and this is an aggregating scope, it will be a a different scope.    *    * @param call Call    * @return Scope within which to validate arguments to call.    */
name|SqlValidatorScope
name|getOperandScope
parameter_list|(
name|SqlCall
name|call
parameter_list|)
function_decl|;
comment|/**    * Performs any scope-specific validation of an expression. For example, an    * aggregating scope requires that expressions are valid aggregations. The    * expression has already been validated.    */
name|void
name|validateExpr
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
function_decl|;
comment|/**    * Looks up a table in this scope from its name. If found, returns the    * {@link TableNamespace} that wraps it. If the "table" is defined in a    * {@code WITH} clause it may be a query, not a table after all.    *    * @param names Name of table, may be qualified or fully-qualified    * @return Namespace of table    */
name|SqlValidatorNamespace
name|getTableNamespace
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlValidatorScope.java
end_comment

end_unit

