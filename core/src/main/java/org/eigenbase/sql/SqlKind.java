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
package|;
end_package

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
name|EnumSet
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
comment|/**  * Enumerates the possible types of {@link SqlNode}.  *  *<p>The values are immutable, canonical constants, so you can use Kinds to  * find particular types of expressions quickly. To identity a call to a common  * operator such as '=', use {@link org.eigenbase.sql.SqlNode#isA}:  *  *<blockquote>  *<pre>exp.{@link org.eigenbase.sql.SqlNode#isA isA}({@link #EQUALS})</pre>  *</blockquote>  *  *<p>Only commonly-used nodes have their own type; other nodes are of type  * {@link #OTHER}. Some of the values, such as {@link #SET_QUERY}, represent  * aggregates.</p>  *  *<p>To identify a category of expressions, you can use  * {@link org.eigenbase.sql.SqlNode#isA} with  * an aggregate SqlKind. The following expression will return<code>true</code>  * for calls to '=' and '&gt;=', but<code>false</code> for the constant '5', or  * a call to '+':</p>  *  *<blockquote>  *<pre>exp.{@link org.eigenbase.sql.SqlNode#isA isA}({@link #COMPARISON SqlKind.Comparison})</pre>  *</blockquote>  *  * To quickly choose between a number of options, use a switch statement:  *  *<blockquote>  *<pre>switch (exp.getKind()) {  * case {@link #EQUALS}:  *     ...;  * case {@link #NOT_EQUALS}:  *     ...;  * default:  *     throw {@link org.eigenbase.util.Util#unexpected Util.unexpected}(exp.getKind());  * }</pre>  *</blockquote>  *</p>  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlKind
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// the basics
comment|/**    * Expression not covered by any other {@link SqlKind} value.    *    * @see #OTHER_FUNCTION    */
name|OTHER
block|,
comment|/**    * SELECT statement or sub-query.    */
name|SELECT
block|,
comment|/**    * JOIN operator or compound FROM clause.    *    *<p>A FROM clause with more than one table is represented as if it were a    * join. For example, "FROM x, y, z" is represented as "JOIN(x, JOIN(x,    * y))".</p>    */
name|JOIN
block|,
comment|/**    * Identifier    */
name|IDENTIFIER
block|,
comment|/**    * A literal.    */
name|LITERAL
block|,
comment|/**    * Function that is not a special function.    *    * @see #FUNCTION    */
name|OTHER_FUNCTION
block|,
comment|/**    * EXPLAIN statement    */
name|EXPLAIN
block|,
comment|/**    * INSERT statement    */
name|INSERT
block|,
comment|/**    * DELETE statement    */
name|DELETE
block|,
comment|/**    * UPDATE statement    */
name|UPDATE
block|,
comment|/**    * A dynamic parameter.    */
name|DYNAMIC_PARAM
block|,
comment|/**    * ORDER BY clause.    *    * @see #DESCENDING    * @see #NULLS_FIRST    * @see #NULLS_LAST    */
name|ORDER_BY
block|,
comment|/**    * Union    */
name|UNION
block|,
comment|/**    * Except    */
name|EXCEPT
block|,
comment|/**    * Intersect    */
name|INTERSECT
block|,
comment|/**    * AS operator    */
name|AS
block|,
comment|/**    * OVER operator    */
name|OVER
block|,
comment|/**    * Window specification    */
name|WINDOW
block|,
comment|/**    * MERGE statement    */
name|MERGE
block|,
comment|/**    * TABLESAMPLE operator    */
name|TABLESAMPLE
block|,
comment|// binary operators
comment|/**    * The arithmetic multiplication operator, "*".    */
name|TIMES
block|,
comment|/**    * The arithmetic division operator, "/".    */
name|DIVIDE
block|,
comment|/**    * The arithmetic plus operator, "+".    *    * @see #PLUS_PREFIX    */
name|PLUS
block|,
comment|/**    * The arithmetic minus operator, "-".    *    * @see #MINUS_PREFIX    */
name|MINUS
block|,
comment|// comparison operators
comment|/**    * The "IN" operator.    */
name|IN
block|,
comment|/**    * The less-than operator, "&lt;".    */
name|LESS_THAN
block|,
comment|/**    * The greater-than operator, "&gt;".    */
name|GREATER_THAN
block|,
comment|/**    * The less-than-or-equal operator, "&lt;=".    */
name|LESS_THAN_OR_EQUAL
block|,
comment|/**    * The greater-than-or-equal operator, "&gt;=".    */
name|GREATER_THAN_OR_EQUAL
block|,
comment|/**    * The equals operator, "=".    */
name|EQUALS
block|,
comment|/**    * The not-equals operator, "&#33;=" or "&lt;&gt;".    */
name|NOT_EQUALS
block|,
comment|/**    * The is-distinct-from operator.    */
name|IS_DISTINCT_FROM
block|,
comment|/**    * The is-not-distinct-from operator.    */
name|IS_NOT_DISTINCT_FROM
block|,
comment|/**    * The logical "OR" operator.    */
name|OR
block|,
comment|/**    * The logical "AND" operator.    */
name|AND
block|,
comment|// other infix
comment|/**    * Dot    */
name|DOT
block|,
comment|/**    * The "OVERLAPS" operator.    */
name|OVERLAPS
block|,
comment|/**    * The "LIKE" operator.    */
name|LIKE
block|,
comment|/**    * The "SIMILAR" operator.    */
name|SIMILAR
block|,
comment|/**    * The "BETWEEN" operator.    */
name|BETWEEN
block|,
comment|/**    * A "CASE" expression.    */
name|CASE
block|,
comment|// prefix operators
comment|/**    * The logical "NOT" operator.    */
name|NOT
block|,
comment|/**    * The unary plus operator, as in "+1".    *    * @see #PLUS    */
name|PLUS_PREFIX
block|,
comment|/**    * The unary minus operator, as in "-1".    *    * @see #MINUS    */
name|MINUS_PREFIX
block|,
comment|/**    * The "EXISTS" operator.    */
name|EXISTS
block|,
comment|/**    * The "VALUES" operator.    */
name|VALUES
block|,
comment|/**    * Explicit table, e.g.<code>select * from (TABLE t)</code> or<code>TABLE    * t</code>. See also {@link #COLLECTION_TABLE}.    */
name|EXPLICIT_TABLE
block|,
comment|/**    * Scalar query; that is, a subquery used in an expression context, and    * returning one row and one column.    */
name|SCALAR_QUERY
block|,
comment|/**    * ProcedureCall    */
name|PROCEDURE_CALL
block|,
comment|/**    * NewSpecification    */
name|NEW_SPECIFICATION
block|,
comment|// postfix operators
comment|/**    * DESC in ORDER BY. A parse tree, not a true expression.    */
name|DESCENDING
block|,
comment|/**    * NULLS FIRST clause in ORDER BY. A parse tree, not a true expression.    */
name|NULLS_FIRST
block|,
comment|/**    * NULLS LAST clause in ORDER BY. A parse tree, not a true expression.    */
name|NULLS_LAST
block|,
comment|/**    * The "IS TRUE" operator.    */
name|IS_TRUE
block|,
comment|/**    * The "IS FALSE" operator.    */
name|IS_FALSE
block|,
comment|/**    * The "IS UNKNOWN" operator.    */
name|IS_UNKNOWN
block|,
comment|/**    * The "IS NULL" operator.    */
name|IS_NULL
block|,
comment|/**    * The "PRECEDING" qualifier of an interval end-point in a window    * specification.    */
name|PRECEDING
block|,
comment|/**    * The "FOLLOWING" qualifier of an interval end-point in a window    * specification.    */
name|FOLLOWING
block|,
comment|/**    * The field access operator, ".".    *    *<p>(Only used at the RexNode level; at    * SqlNode level, a field-access is part of an identifier.)</p>    */
name|FIELD_ACCESS
block|,
comment|/**    * Reference to an input field.    *    *<p>(Only used at the RexNode level.)</p>    */
name|INPUT_REF
block|,
comment|/**    * Reference to a sub-expression computed within the current relational    * operator.    *    *<p>(Only used at the RexNode level.)</p>    */
name|LOCAL_REF
block|,
comment|/**    * Reference to correlation variable.    *    *<p>(Only used at the RexNode level.)</p>    */
name|CORREL_VARIABLE
block|,
comment|// functions
comment|/**    * The row-constructor function. May be explicit or implicit:    * {@code VALUES 1, ROW (2)}.    */
name|ROW
block|,
comment|/**    * The non-standard constructor used to pass a    * COLUMN_LIST parameter to a user-defined transform.    */
name|COLUMN_LIST
block|,
comment|/**    * The "CAST" operator.    */
name|CAST
block|,
comment|/**    * The "TRIM" function.    */
name|TRIM
block|,
comment|/**    * Call to a function using JDBC function syntax.    */
name|JDBC_FN
block|,
comment|/**    * The MULTISET value constructor.    */
name|MULTISET_VALUE_CONSTRUCTOR
block|,
comment|/**    * The MULTISET query constructor.    */
name|MULTISET_QUERY_CONSTRUCTOR
block|,
comment|/**    * The "UNNEST" operator.    */
name|UNNEST
block|,
comment|/**    * The "LATERAL" qualifier to relations in the FROM clause.    */
name|LATERAL
block|,
comment|/**    * Table operator which converts user-defined transform into a relation, for    * example,<code>select * from TABLE(udx(x, y, z))</code>. See also the    * {@link #EXPLICIT_TABLE} prefix operator.    */
name|COLLECTION_TABLE
block|,
comment|/**    * Array Value Constructor, e.g. {@code Array[1, 2, 3]}.    */
name|ARRAY_VALUE_CONSTRUCTOR
block|,
comment|/**    * Array Query Constructor, e.g. {@code Array(select deptno from dept)}.    */
name|ARRAY_QUERY_CONSTRUCTOR
block|,
comment|/**    * Map Value Constructor, e.g. {@code Map['washington', 1, 'obama', 44]}.    */
name|MAP_VALUE_CONSTRUCTOR
block|,
comment|/**    * Map Query Constructor, e.g. {@code MAP (SELECT empno, deptno FROM emp)}.    */
name|MAP_QUERY_CONSTRUCTOR
block|,
comment|/**    * CURSOR constructor, for example,<code>select * from    * TABLE(udx(CURSOR(select ...), x, y, z))</code>    */
name|CURSOR
block|,
comment|// internal operators (evaluated in validator) 200-299
comment|/**    * Literal chain operator (for composite string literals).    * An internal operator that does not appear in SQL syntax.    */
name|LITERAL_CHAIN
block|,
comment|/**    * Escape operator (always part of LIKE or SIMILAR TO expression).    * An internal operator that does not appear in SQL syntax.    */
name|ESCAPE
block|,
comment|/**    * The internal REINTERPRET operator (meaning a reinterpret cast).    * An internal operator that does not appear in SQL syntax.    */
name|REINTERPRET
block|;
comment|//~ Static fields/initializers ---------------------------------------------
comment|// Most of the static fields are categories, aggregating several kinds into
comment|// a set.
comment|/**    * Category consisting of set-query node types.    *    *<p>Consists of:    * {@link #EXCEPT},    * {@link #INTERSECT},    * {@link #UNION}.    */
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|SqlKind
argument_list|>
name|SET_QUERY
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|UNION
argument_list|,
name|INTERSECT
argument_list|,
name|EXCEPT
argument_list|)
decl_stmt|;
comment|/**    * Category consisting of all expression operators.    *    *<p>A node is an expression if it is NOT one of the following:    * {@link #AS},    * {@link #DESCENDING},    * {@link #SELECT},    * {@link #JOIN},    * {@link #OTHER_FUNCTION},    * {@link #CAST},    * {@link #TRIM},    * {@link #LITERAL_CHAIN},    * {@link #JDBC_FN},    * {@link #PRECEDING},    * {@link #FOLLOWING},    * {@link #ORDER_BY},    * {@link #COLLECTION_TABLE},    * {@link #TABLESAMPLE}.    */
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|SqlKind
argument_list|>
name|EXPRESSION
init|=
name|EnumSet
operator|.
name|complementOf
argument_list|(
name|EnumSet
operator|.
name|of
argument_list|(
name|AS
argument_list|,
name|DESCENDING
argument_list|,
name|SELECT
argument_list|,
name|JOIN
argument_list|,
name|OTHER_FUNCTION
argument_list|,
name|CAST
argument_list|,
name|TRIM
argument_list|,
name|LITERAL_CHAIN
argument_list|,
name|JDBC_FN
argument_list|,
name|PRECEDING
argument_list|,
name|FOLLOWING
argument_list|,
name|ORDER_BY
argument_list|,
name|NULLS_FIRST
argument_list|,
name|NULLS_LAST
argument_list|,
name|COLLECTION_TABLE
argument_list|,
name|TABLESAMPLE
argument_list|)
argument_list|)
decl_stmt|;
comment|/**    * Category consisting of all DML operators.    *    *<p>Consists of:    * {@link #INSERT},    * {@link #UPDATE},    * {@link #DELETE},    * {@link #MERGE},    * {@link #PROCEDURE_CALL}.    *    *<p>NOTE jvs 1-June-2006: For now we treat procedure calls as DML;    * this makes it easy for JDBC clients to call execute or    * executeUpdate and not have to process dummy cursor results.  If    * in the future we support procedures which return results sets,    * we'll need to refine this.    */
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|SqlKind
argument_list|>
name|DML
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|INSERT
argument_list|,
name|DELETE
argument_list|,
name|UPDATE
argument_list|,
name|MERGE
argument_list|,
name|PROCEDURE_CALL
argument_list|)
decl_stmt|;
comment|/**    * Category consisting of query node types.    *    *<p>Consists of:    * {@link #SELECT},    * {@link #EXCEPT},    * {@link #INTERSECT},    * {@link #UNION},    * {@link #VALUES},    * {@link #ORDER_BY},    * {@link #EXPLICIT_TABLE}.    */
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|SqlKind
argument_list|>
name|QUERY
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|SELECT
argument_list|,
name|UNION
argument_list|,
name|INTERSECT
argument_list|,
name|EXCEPT
argument_list|,
name|VALUES
argument_list|,
name|ORDER_BY
argument_list|,
name|EXPLICIT_TABLE
argument_list|)
decl_stmt|;
comment|/**    * Category of all SQL statement types.    *    *<p>Consists of all types in {@link #QUERY} and {@link #DML}.    */
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|SqlKind
argument_list|>
name|TOP_LEVEL
decl_stmt|;
static|static
block|{
name|TOP_LEVEL
operator|=
name|EnumSet
operator|.
name|copyOf
argument_list|(
name|QUERY
argument_list|)
expr_stmt|;
name|TOP_LEVEL
operator|.
name|addAll
argument_list|(
name|DML
argument_list|)
expr_stmt|;
block|}
comment|/**    * Category consisting of regular and special functions.    *    *<p>Consists of regular functions {@link #OTHER_FUNCTION} and special    * functions {@link #ROW}, {@link #TRIM}, {@link #CAST}, {@link #JDBC_FN}.    */
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|SqlKind
argument_list|>
name|FUNCTION
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|OTHER_FUNCTION
argument_list|,
name|ROW
argument_list|,
name|TRIM
argument_list|,
name|CAST
argument_list|,
name|JDBC_FN
argument_list|)
decl_stmt|;
comment|/**    * Category of comparison operators.    *    *<p>Consists of:    * {@link #IN},    * {@link #EQUALS},    * {@link #NOT_EQUALS},    * {@link #LESS_THAN},    * {@link #GREATER_THAN},    * {@link #LESS_THAN_OR_EQUAL},    * {@link #GREATER_THAN_OR_EQUAL}.    */
specifier|public
specifier|static
specifier|final
name|Set
argument_list|<
name|SqlKind
argument_list|>
name|COMPARISON
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|IN
argument_list|,
name|EQUALS
argument_list|,
name|NOT_EQUALS
argument_list|,
name|LESS_THAN
argument_list|,
name|GREATER_THAN
argument_list|,
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|LESS_THAN_OR_EQUAL
argument_list|)
decl_stmt|;
comment|/**    * Returns whether this {@code SqlKind} belongs to a given category.    *    *<p>A category is a collection of kinds, not necessarily disjoint. For    * example, QUERY is { SELECT, UNION, INTERSECT, EXCEPT, VALUES, ORDER_BY,    * EXPLICIT_TABLE }.    *    * @param category Category    * @return Whether this kind belongs to the given category    */
specifier|public
specifier|final
name|boolean
name|belongsTo
parameter_list|(
name|Collection
argument_list|<
name|SqlKind
argument_list|>
name|category
parameter_list|)
block|{
return|return
name|category
operator|.
name|contains
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End SqlKind.java
end_comment

end_unit

