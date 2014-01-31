begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Validates the parse tree of a SQL statement, and provides semantic  * information about the parse tree.  *  *<p>To create an instance of the default validator implementation, call {@link  * SqlValidatorUtil#newValidator}.  *  *<h2>Visitor pattern</h2>  *  *<p>The validator interface is an instance of the {@link  * org.eigenbase.util.Glossary#VISITOR_PATTERN visitor pattern}. Implementations  * of the {@link SqlNode#validate} method call the<code>validateXxx</code>  * method appropriate to the kind of node: {@link  * SqlLiteral#validate(SqlValidator, SqlValidatorScope)} calls {@link  * #validateLiteral(org.eigenbase.sql.SqlLiteral)}; {@link  * SqlCall#validate(SqlValidator, SqlValidatorScope)} calls {@link  * #validateCall(SqlCall, SqlValidatorScope)}; and so forth.  *  *<p>The {@link SqlNode#validateExpr(SqlValidator, SqlValidatorScope)} method  * is as {@link SqlNode#validate(SqlValidator, SqlValidatorScope)} but is called  * when the node is known to be a scalar expression.  *  *<h2>Scopes and namespaces</h2>  *  *<p>In order to resolve names to objects, the validator builds a map of the  * structure of the query. This map consists of two types of objects. A {@link  * SqlValidatorScope} describes the tables and columns accessible at a  * particular point in the query; and a {@link SqlValidatorNamespace} is a  * description of a data source used in a query.  *  *<p>There are different kinds of namespace for different parts of the query.  * for example {@link IdentifierNamespace} for table names, {@link  * SelectNamespace} for SELECT queries, {@link SetopNamespace} for UNION, EXCEPT  * and INTERSECT. A validator is allowed to wrap namespaces in other objects  * which implement {@link SqlValidatorNamespace}, so don't try to cast your  * namespace or use<code>instanceof</code>; use {@link  * SqlValidatorNamespace#unwrap(Class)} and {@link  * SqlValidatorNamespace#isWrapperFor(Class)} instead.</p>  *  *<p>The validator builds the map by making a quick scan over the query when  * the root {@link SqlNode} is first provided. Thereafter, it supplies the  * correct scope or namespace object when it calls validation methods.</p>  *  *<p>The methods {@link #getSelectScope}, {@link #getFromScope}, {@link  * #getWhereScope}, {@link #getGroupScope}, {@link #getHavingScope}, {@link  * #getOrderScope} and {@link #getJoinScope} get the correct scope to resolve  * names in a particular clause of a SQL statement.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlValidator
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns the dialect of SQL (SQL:2003, etc.) this validator recognizes.    * Default is {@link SqlConformance#Default}.    *    * @return dialect of SQL this validator recognizes    */
name|SqlConformance
name|getConformance
parameter_list|()
function_decl|;
comment|/**    * Returns the catalog reader used by this validator.    *    * @return catalog reader    */
name|SqlValidatorCatalogReader
name|getCatalogReader
parameter_list|()
function_decl|;
comment|/**    * Returns the operator table used by this validator.    *    * @return operator table    */
name|SqlOperatorTable
name|getOperatorTable
parameter_list|()
function_decl|;
comment|/**    * Validates an expression tree. You can call this method multiple times,    * but not reentrantly.    *    * @param topNode top of expression tree to be validated    * @return validated tree (possibly rewritten)    * @pre outermostNode == null    */
name|SqlNode
name|validate
parameter_list|(
name|SqlNode
name|topNode
parameter_list|)
function_decl|;
comment|/**    * Validates an expression tree. You can call this method multiple times,    * but not reentrantly.    *    * @param topNode       top of expression tree to be validated    * @param nameToTypeMap map of simple name to {@link RelDataType}; used to    *                      resolve {@link SqlIdentifier} references    * @return validated tree (possibly rewritten)    * @pre outermostNode == null    */
name|SqlNode
name|validateParameterizedExpression
parameter_list|(
name|SqlNode
name|topNode
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RelDataType
argument_list|>
name|nameToTypeMap
parameter_list|)
function_decl|;
comment|/**    * Checks that a query is valid.    *    *<p>Valid queries include:    *    *<ul>    *<li><code>SELECT</code> statement,    *<li>set operation (<code>UNION</code>,<code>INTERSECT</code>,<code>    * EXCEPT</code>)    *<li>identifier (e.g. representing use of a table in a FROM clause)    *<li>query aliased with the<code>AS</code> operator    *</ul>    *    * @param node  Query node    * @param scope Scope in which the query occurs    * @throws RuntimeException if the query is not valid    */
name|void
name|validateQuery
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Returns the type assigned to a node by validation.    *    * @param node the node of interest    * @return validated type, never null    */
name|RelDataType
name|getValidatedNodeType
parameter_list|(
name|SqlNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Returns the type assigned to a node by validation, or null if unknown.    * This allows for queries against nodes such as aliases, which have no type    * of their own. If you want to assert that the node of interest must have a    * type, use {@link #getValidatedNodeType} instead.    *    * @param node the node of interest    * @return validated type, or null if unknown or not applicable    */
name|RelDataType
name|getValidatedNodeTypeIfKnown
parameter_list|(
name|SqlNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Resolves an identifier to a fully-qualified name.    *    * @param id    Identifier    * @param scope Naming scope    */
name|void
name|validateIdentifier
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Validates a literal.    *    * @param literal Literal    */
name|void
name|validateLiteral
parameter_list|(
name|SqlLiteral
name|literal
parameter_list|)
function_decl|;
comment|/**    * Validates a {@link SqlIntervalQualifier}    *    * @param qualifier Interval qualifier    */
name|void
name|validateIntervalQualifier
parameter_list|(
name|SqlIntervalQualifier
name|qualifier
parameter_list|)
function_decl|;
comment|/**    * Validates an INSERT statement.    *    * @param insert INSERT statement    */
name|void
name|validateInsert
parameter_list|(
name|SqlInsert
name|insert
parameter_list|)
function_decl|;
comment|/**    * Validates an UPDATE statement.    *    * @param update UPDATE statement    */
name|void
name|validateUpdate
parameter_list|(
name|SqlUpdate
name|update
parameter_list|)
function_decl|;
comment|/**    * Validates a DELETE statement.    *    * @param delete DELETE statement    */
name|void
name|validateDelete
parameter_list|(
name|SqlDelete
name|delete
parameter_list|)
function_decl|;
comment|/**    * Validates a MERGE statement.    *    * @param merge MERGE statement    */
name|void
name|validateMerge
parameter_list|(
name|SqlMerge
name|merge
parameter_list|)
function_decl|;
comment|/**    * Validates a data type expression.    *    * @param dataType Data type    */
name|void
name|validateDataType
parameter_list|(
name|SqlDataTypeSpec
name|dataType
parameter_list|)
function_decl|;
comment|/**    * Validates a dynamic parameter.    *    * @param dynamicParam Dynamic parameter    */
name|void
name|validateDynamicParam
parameter_list|(
name|SqlDynamicParam
name|dynamicParam
parameter_list|)
function_decl|;
comment|/**    * Validates the right-hand side of an OVER expression. It might be either    * an {@link SqlIdentifier identifier} referencing a window, or an {@link    * SqlWindow inline window specification}.    *    * @param windowOrId SqlNode that can be either SqlWindow with all the    *                   components of a window spec or a SqlIdentifier with the    *                   name of a window spec.    * @param scope      Naming scope    * @param call       the SqlNode if a function call if the window is attached    *                   to one.    */
name|void
name|validateWindow
parameter_list|(
name|SqlNode
name|windowOrId
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
function_decl|;
comment|/**    * Validates a call to an operator.    *    * @param call  Operator call    * @param scope Naming scope    */
name|void
name|validateCall
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Validates parameters for aggregate function.    *    * @param aggFunction function containing COLUMN_LIST parameter    * @param scope       Syntactic scope    */
name|void
name|validateAggregateParams
parameter_list|(
name|SqlCall
name|aggFunction
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Validates a COLUMN_LIST parameter    *    * @param function function containing COLUMN_LIST parameter    * @param argTypes function arguments    * @param operands operands passed into the function call    */
name|void
name|validateColumnListParams
parameter_list|(
name|SqlFunction
name|function
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|)
function_decl|;
comment|/**    * Derives the type of a node in a given scope. If the type has already been    * inferred, returns the previous type.    *    * @param scope   Syntactic scope    * @param operand Parse tree node    * @return Type of the SqlNode. Should never return<code>NULL</code>    */
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
function_decl|;
comment|/**    * Adds "line x, column y" context to a validator exception.    *    *<p>Note that the input exception is checked (it derives from {@link    * Exception}) and the output exception is unchecked (it derives from {@link    * RuntimeException}). This is intentional -- it should remind code authors    * to provide context for their validation errors.    *    * @param e    The validation error    * @param node The place where the exception occurred    * @return Exception containing positional information    * @pre node != null    * @post return != null    */
name|EigenbaseException
name|newValidationError
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|SqlValidatorException
name|e
parameter_list|)
function_decl|;
comment|/**    * Returns whether a SELECT statement is an aggregation. Criteria are: (1)    * contains GROUP BY, or (2) contains HAVING, or (3) SELECT or ORDER BY    * clause contains aggregate functions. (Windowed aggregate functions, such    * as<code>SUM(x) OVER w</code>, don't count.)    *    * @param select SELECT statement    * @return whether SELECT statement is an aggregation    */
name|boolean
name|isAggregate
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
function_decl|;
comment|/**    * Returns whether a select list expression is an aggregate function.    *    * @param selectNode Expression in SELECT clause    * @return whether expression is an aggregate function    */
name|boolean
name|isAggregate
parameter_list|(
name|SqlNode
name|selectNode
parameter_list|)
function_decl|;
comment|/**    * Converts a window specification or window name into a fully-resolved    * window specification. For example, in<code>SELECT sum(x) OVER (PARTITION    * BY x ORDER BY y), sum(y) OVER w1, sum(z) OVER (w ORDER BY y) FROM t    * WINDOW w AS (PARTITION BY x)</code> all aggregations have the same    * resolved window specification<code>(PARTITION BY x ORDER BY y)</code>.    *    * @param windowOrRef    Either the name of a window (a {@link SqlIdentifier})    *                       or a window specification (a {@link SqlWindow}).    * @param scope          Scope in which to resolve window names    * @param populateBounds Whether to populate bounds. Doing so may alter the    *                       definition of the window. It is recommended that    *                       populate bounds when translating to physical algebra,    *                       but not when validating.    * @return A window    * @throws RuntimeException Validation exception if window does not exist    */
name|SqlWindow
name|resolveWindow
parameter_list|(
name|SqlNode
name|windowOrRef
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|boolean
name|populateBounds
parameter_list|)
function_decl|;
comment|/**    * Finds the namespace corresponding to a given node.    *    *<p>For example, in the query<code>SELECT * FROM (SELECT * FROM t), t1 AS    * alias</code>, the both items in the FROM clause have a corresponding    * namespace.    *    * @param node Parse tree node    * @return namespace of node    */
name|SqlValidatorNamespace
name|getNamespace
parameter_list|(
name|SqlNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Derives an alias for an expression. If no alias can be derived, returns    * null if<code>ordinal</code> is less than zero, otherwise generates an    * alias<code>EXPR$<i>ordinal</i></code>.    *    * @param node    Expression    * @param ordinal Ordinal of expression    * @return derived alias, or null if no alias can be derived and ordinal is    * less than zero    */
name|String
name|deriveAlias
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|int
name|ordinal
parameter_list|)
function_decl|;
comment|/**    * Returns a list of expressions, with every occurrence of "&#42;" or    * "TABLE.&#42;" expanded.    *    * @param selectList        Select clause to be expanded    * @param query             Query    * @param includeSystemVars Whether to include system variables    * @return expanded select clause    */
name|SqlNodeList
name|expandStar
parameter_list|(
name|SqlNodeList
name|selectList
parameter_list|,
name|SqlSelect
name|query
parameter_list|,
name|boolean
name|includeSystemVars
parameter_list|)
function_decl|;
comment|/**    * Returns the scope that expressions in the WHERE and GROUP BY clause of    * this query should use. This scope consists of the tables in the FROM    * clause, and the enclosing scope.    *    * @param select Query    * @return naming scope of WHERE clause    */
name|SqlValidatorScope
name|getWhereScope
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
function_decl|;
comment|/**    * Returns the type factory used by this validator.    *    * @return type factory    */
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
comment|/**    * Saves the type of a {@link SqlNode}, now that it has been validated.    *    * @param node A SQL parse tree node    * @param type Its type; must not be null    * @pre type != null    * @pre node != null    * @deprecated This method should not be in the {@link SqlValidator}    * interface. The validator should drive the type-derivation process, and    * store nodes' types when they have been derived.    */
name|void
name|setValidatedNodeType
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|RelDataType
name|type
parameter_list|)
function_decl|;
comment|/**    * Removes a node from the set of validated nodes    *    * @param node node to be removed    */
name|void
name|removeValidatedNodeType
parameter_list|(
name|SqlNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Returns an object representing the "unknown" type.    *    * @return unknown type    */
name|RelDataType
name|getUnknownType
parameter_list|()
function_decl|;
comment|/**    * Returns the appropriate scope for validating a particular clause of a    * SELECT statement.    *    *<p>Consider    *    *<blockquote><code>    *<pre>SELECT *    * FROM foo    * WHERE EXISTS (    *    SELECT deptno AS x    *    FROM emp    *       JOIN dept ON emp.deptno = dept.deptno    *    WHERE emp.deptno = 5    *    GROUP BY deptno    *    ORDER BY x)</pre>    *</code></blockquote>    *    * What objects can be seen in each part of the sub-query?    *    *<ul>    *<li>In FROM ({@link #getFromScope} , you can only see 'foo'.    *<li>In WHERE ({@link #getWhereScope}), GROUP BY ({@link #getGroupScope}),    * SELECT ({@link #getSelectScope}), and the ON clause of the JOIN ({@link    * #getJoinScope}) you can see 'emp', 'dept', and 'foo'.    *<li>In ORDER BY ({@link #getOrderScope}), you can see the column alias    * 'x'; and tables 'emp', 'dept', and 'foo'.    *</ul>    *    * @param select SELECT statement    * @return naming scope for SELECT statement    */
name|SqlValidatorScope
name|getSelectScope
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
function_decl|;
comment|/**    * Returns the scope for resolving the SELECT, GROUP BY and HAVING clauses.    * Always a {@link SelectScope}; if this is an aggregation query, the {@link    * AggregatingScope} is stripped away.    *    * @param select SELECT statement    * @return naming scope for SELECT statement, sans any aggregating scope    */
name|SelectScope
name|getRawSelectScope
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
function_decl|;
comment|/**    * Returns a scope containing the objects visible from the FROM clause of a    * query.    *    * @param select SELECT statement    * @return naming scope for FROM clause    */
name|SqlValidatorScope
name|getFromScope
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
function_decl|;
comment|/**    * Returns a scope containing the objects visible from the ON and USING    * sections of a JOIN clause.    *    * @param node The item in the FROM clause which contains the ON or USING    *             expression    * @return naming scope for JOIN clause    * @see #getFromScope    */
name|SqlValidatorScope
name|getJoinScope
parameter_list|(
name|SqlNode
name|node
parameter_list|)
function_decl|;
comment|/**    * Returns a scope containing the objects visible from the GROUP BY clause    * of a query.    *    * @param select SELECT statement    * @return naming scope for GROUP BY clause    */
name|SqlValidatorScope
name|getGroupScope
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
function_decl|;
comment|/**    * Returns a scope containing the objects visible from the HAVING clause of    * a query.    *    * @param select SELECT statement    * @return naming scope for HAVING clause    */
name|SqlValidatorScope
name|getHavingScope
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
function_decl|;
comment|/**    * Returns the scope that expressions in the SELECT and HAVING clause of    * this query should use. This scope consists of the FROM clause and the    * enclosing scope. If the query is aggregating, only columns in the GROUP    * BY clause may be used.    *    * @param select SELECT statement    * @return naming scope for ORDER BY clause    */
name|SqlValidatorScope
name|getOrderScope
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
function_decl|;
comment|/**    * Declares a SELECT expression as a cursor.    *    * @param select select expression associated with the cursor    * @param scope  scope of the parent query associated with the cursor    */
name|void
name|declareCursor
parameter_list|(
name|SqlSelect
name|select
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Pushes a new instance of a function call on to a function call stack.    */
name|void
name|pushFunctionCall
parameter_list|()
function_decl|;
comment|/**    * Removes the topmost entry from the function call stack.    */
name|void
name|popFunctionCall
parameter_list|()
function_decl|;
comment|/**    * Retrieves the name of the parent cursor referenced by a column list    * parameter.    *    * @param columnListParamName name of the column list parameter    * @return name of the parent cursor    */
name|String
name|getParentCursor
parameter_list|(
name|String
name|columnListParamName
parameter_list|)
function_decl|;
comment|/**    * Enables or disables expansion of identifiers other than column    * references.    *    * @param expandIdentifiers new setting    */
name|void
name|setIdentifierExpansion
parameter_list|(
name|boolean
name|expandIdentifiers
parameter_list|)
function_decl|;
comment|/**    * Enables or disables expansion of column references. (Currently this does    * not apply to the ORDER BY clause; may be fixed in the future.)    *    * @param expandColumnReferences new setting    */
name|void
name|setColumnReferenceExpansion
parameter_list|(
name|boolean
name|expandColumnReferences
parameter_list|)
function_decl|;
comment|/**    * @return whether column reference expansion is enabled    */
name|boolean
name|getColumnReferenceExpansion
parameter_list|()
function_decl|;
comment|/**    * Returns expansion of identifiers.    *    * @return whether this validator should expand identifiers    */
name|boolean
name|shouldExpandIdentifiers
parameter_list|()
function_decl|;
comment|/**    * Enables or disables rewrite of "macro-like" calls such as COALESCE.    *    * @param rewriteCalls new setting    */
name|void
name|setCallRewrite
parameter_list|(
name|boolean
name|rewriteCalls
parameter_list|)
function_decl|;
comment|/**    * Derives the type of a constructor.    *    * @param scope                 Scope    * @param call                  Call    * @param unresolvedConstructor TODO    * @param resolvedConstructor   TODO    * @param argTypes              Types of arguments    * @return Resolved type of constructor    */
name|RelDataType
name|deriveConstructorType
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlFunction
name|unresolvedConstructor
parameter_list|,
name|SqlFunction
name|resolvedConstructor
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
parameter_list|)
function_decl|;
comment|/**    * Handles a call to a function which cannot be resolved. Returns a an    * appropriately descriptive error, which caller must throw.    *    * @param call               Call    * @param unresolvedFunction Overloaded function which is the target of the    *                           call    * @param argTypes           Types of arguments    */
name|EigenbaseException
name|handleUnresolvedFunction
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlFunction
name|unresolvedFunction
parameter_list|,
name|List
argument_list|<
name|RelDataType
argument_list|>
name|argTypes
parameter_list|)
function_decl|;
comment|/**    * Expands an expression in the ORDER BY clause into an expression with the    * same semantics as expressions in the SELECT clause.    *    *<p>This is made necessary by a couple of dialect 'features':    *    *<ul>    *<li><b>ordinal expressions</b>: In "SELECT x, y FROM t ORDER BY 2", the    * expression "2" is shorthand for the 2nd item in the select clause, namely    * "y".    *<li><b>alias references</b>: In "SELECT x AS a, y FROM t ORDER BY a", the    * expression "a" is shorthand for the item in the select clause whose alias    * is "a"    *</ul>    *    * @param select    Select statement which contains ORDER BY    * @param orderExpr Expression in the ORDER BY clause.    * @return Expression translated into SELECT clause semantics    */
name|SqlNode
name|expandOrderExpr
parameter_list|(
name|SqlSelect
name|select
parameter_list|,
name|SqlNode
name|orderExpr
parameter_list|)
function_decl|;
comment|/**    * Expands an expression.    *    * @param expr  Expression    * @param scope Scope    * @return Expanded expression    */
name|SqlNode
name|expand
parameter_list|(
name|SqlNode
name|expr
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
function_decl|;
comment|/**    * Returns whether a field is a system field. Such fields may have    * particular properties such as sortedness and nullability.    *    *<p>In the default implementation, always returns {@code false}.    *    * @param field Field    * @return whether field is a system field    */
name|boolean
name|isSystemField
parameter_list|(
name|RelDataTypeField
name|field
parameter_list|)
function_decl|;
comment|/**    * Returns a description of how each field in the row type maps to a    * catalog, schema, table and column in the schema.    *    *<p>The returned list is never null, and has one element for each field    * in the row type. Each element is a list of four elements (catalog,    * schema, table, column), or may be null if the column is an expression.    *    * @param sqlQuery Query    * @return Description of how each field in the row type maps to a schema    * object    */
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getFieldOrigins
parameter_list|(
name|SqlNode
name|sqlQuery
parameter_list|)
function_decl|;
comment|/**    * Returns a record type that contains the name and type of each parameter.    * Returns a record type with no fields if there are no parameters.    *    * @param sqlQuery Query    * @return Record type    */
name|RelDataType
name|getParameterRowType
parameter_list|(
name|SqlNode
name|sqlQuery
parameter_list|)
function_decl|;
comment|/**    * Returns the scope of an OVER or VALUES node.    *    * @param node Node    * @return Scope    */
name|SqlValidatorScope
name|getOverScope
parameter_list|(
name|SqlNode
name|node
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlValidator.java
end_comment

end_unit

