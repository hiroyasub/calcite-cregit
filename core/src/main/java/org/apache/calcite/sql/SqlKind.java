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
comment|/**  * Enumerates the possible types of {@link SqlNode}.  *  *<p>The values are immutable, canonical constants, so you can use Kinds to  * find particular types of expressions quickly. To identity a call to a common  * operator such as '=', use {@link org.apache.calcite.sql.SqlNode#isA}:</p>  *  *<blockquote>  * exp.{@link org.apache.calcite.sql.SqlNode#isA isA}({@link #EQUALS})  *</blockquote>  *  *<p>Only commonly-used nodes have their own type; other nodes are of type  * {@link #OTHER}. Some of the values, such as {@link #SET_QUERY}, represent  * aggregates.</p>  *  *<p>To quickly choose between a number of options, use a switch statement:</p>  *  *<blockquote>  *<pre>switch (exp.getKind()) {  * case {@link #EQUALS}:  *     ...;  * case {@link #NOT_EQUALS}:  *     ...;  * default:  *     throw new AssertionError("unexpected");  * }</pre>  *</blockquote>  *  *<p>Note that we do not even have to check that a {@code SqlNode} is a  * {@link SqlCall}.</p>  *  *<p>To identify a category of expressions, use {@code SqlNode.isA} with  * an aggregate SqlKind. The following expression will return<code>true</code>  * for calls to '=' and '&gt;=', but<code>false</code> for the constant '5', or  * a call to '+':</p>  *  *<blockquote>  *<pre>exp.isA({@link #COMPARISON SqlKind.COMPARISON})</pre>  *</blockquote>  *  *<p>RexNode also has a {@code getKind} method; {@code SqlKind} values are  * preserved during translation from {@code SqlNode} to {@code RexNode}, where  * applicable.</p>  *  *<p>There is no water-tight definition of "common", but that's OK. There will  * always be operators that don't have their own kind, and for these we use the  * {@code SqlOperator}. But for really the common ones, e.g. the many places  * where we are looking for {@code AND}, {@code OR} and {@code EQUALS}, the enum  * helps.</p>  *  *<p>(If we were using Scala, {@link SqlOperator} would be a case  * class, and we wouldn't need {@code SqlKind}. But we're not.)</p>  */
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
comment|/**    * JOIN operator or compound FROM clause.    *    *<p>A FROM clause with more than one table is represented as if it were a    * join. For example, "FROM x, y, z" is represented as    * "JOIN(x, JOIN(x, y))".</p>    */
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
comment|/**    * "ALTER scope SET option = value" statement.    */
name|SET_OPTION
block|,
comment|/**    * A dynamic parameter.    */
name|DYNAMIC_PARAM
block|,
comment|/**    * ORDER BY clause.    *    * @see #DESCENDING    * @see #NULLS_FIRST    * @see #NULLS_LAST    */
name|ORDER_BY
block|,
comment|/** WITH clause. */
name|WITH
block|,
comment|/** Item in WITH clause. */
name|WITH_ITEM
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
comment|/**    * FILTER operator    */
name|FILTER
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
comment|/**    * The "IS NOT TRUE" operator.    */
name|IS_NOT_TRUE
block|,
comment|/**    * The "IS NOT FALSE" operator.    */
name|IS_NOT_FALSE
block|,
comment|/**    * The "IS UNKNOWN" operator.    */
name|IS_UNKNOWN
block|,
comment|/**    * The "IS NULL" operator.    */
name|IS_NULL
block|,
comment|/**    * The "IS NOT NULL" operator.    */
name|IS_NOT_NULL
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
comment|/**    * The "NEXT VALUE OF sequence" operator.    */
name|NEXT_VALUE
block|,
comment|/**    * The "CURRENT VALUE OF sequence" operator.    */
name|CURRENT_VALUE
block|,
comment|/**    * The "FLOOR" function    */
name|FLOOR
block|,
comment|/**    * The "CEIL" function    */
name|CEIL
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
block|,
comment|/** The internal {@code EXTEND} operator that qualifies a table name in the    * {@code FROM} clause. */
name|EXTEND
block|,
comment|/** The internal {@code CUBE} operator that occurs within a {@code GROUP BY}    * clause. */
name|CUBE
block|,
comment|/** The internal {@code ROLLUP} operator that occurs within a {@code GROUP BY}    * clause. */
name|ROLLUP
block|,
comment|/** The internal {@code GROUPING SETS} operator that occurs within a    * {@code GROUP BY} clause. */
name|GROUPING_SETS
block|,
comment|/** The internal {@code GROUPING(e)} function. */
name|GROUPING
block|,
comment|/** The internal {@code GROUPING_ID(e, ...)} function. */
name|GROUPING_ID
block|,
comment|/** The internal {@code GROUP_ID()} function. */
name|GROUP_ID
block|,
comment|// DDL and session control statements follow. The list is not exhaustive: feel
comment|// free to add more.
comment|/** {@code COMMIT} session control statement. */
name|COMMIT
block|,
comment|/** {@code ROLLBACK} session control statement. */
name|ROLLBACK
block|,
comment|/** {@code ALTER SESSION} DDL statement. */
name|ALTER_SESSION
block|,
comment|/** {@code CREATE TABLE} DDL statement. */
name|CREATE_TABLE
block|,
comment|/** {@code ALTER TABLE} DDL statement. */
name|ALTER_TABLE
block|,
comment|/** {@code DROP TABLE} DDL statement. */
name|DROP_TABLE
block|,
comment|/** {@code CREATE VIEW} DDL statement. */
name|CREATE_VIEW
block|,
comment|/** {@code ALTER VIEW} DDL statement. */
name|ALTER_VIEW
block|,
comment|/** {@code DROP VIEW} DDL statement. */
name|DROP_VIEW
block|,
comment|/** {@code CREATE SEQUENCE} DDL statement. */
name|CREATE_SEQUENCE
block|,
comment|/** {@code ALTER SEQUENCE} DDL statement. */
name|ALTER_SEQUENCE
block|,
comment|/** {@code DROP SEQUENCE} DDL statement. */
name|DROP_SEQUENCE
block|,
comment|/** {@code CREATE INDEX} DDL statement. */
name|CREATE_INDEX
block|,
comment|/** {@code ALTER INDEX} DDL statement. */
name|ALTER_INDEX
block|,
comment|/** {@code DROP INDEX} DDL statement. */
name|DROP_INDEX
block|,
comment|/** DDL statement not handled above.    *    *<p><b>Note to other projects</b>: If you are extending Calcite's SQL parser    * and have your own object types you no doubt want to define CREATE and DROP    * commands for them. Use OTHER_DDL in the short term, but we are happy to add    * new enum values for your object types. Just ask!    */
name|OTHER_DDL
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
name|CUBE
argument_list|,
name|ROLLUP
argument_list|,
name|GROUPING_SETS
argument_list|,
name|EXTEND
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
name|FLOOR
argument_list|,
name|CEIL
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
argument_list|,
name|VALUES
argument_list|,
name|WITH
argument_list|,
name|WITH_ITEM
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
comment|/**    * Category consisting of all DDL operators.    */
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|SqlKind
argument_list|>
name|DDL
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|COMMIT
argument_list|,
name|ROLLBACK
argument_list|,
name|ALTER_SESSION
argument_list|,
name|CREATE_TABLE
argument_list|,
name|ALTER_TABLE
argument_list|,
name|DROP_TABLE
argument_list|,
name|CREATE_VIEW
argument_list|,
name|ALTER_VIEW
argument_list|,
name|DROP_VIEW
argument_list|,
name|CREATE_SEQUENCE
argument_list|,
name|ALTER_SEQUENCE
argument_list|,
name|DROP_SEQUENCE
argument_list|,
name|CREATE_INDEX
argument_list|,
name|ALTER_INDEX
argument_list|,
name|DROP_INDEX
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
name|WITH
argument_list|,
name|ORDER_BY
argument_list|,
name|EXPLICIT_TABLE
argument_list|)
decl_stmt|;
comment|/**    * Category of all SQL statement types.    *    *<p>Consists of all types in {@link #QUERY}, {@link #DML} and {@link #DDL}.    */
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|SqlKind
argument_list|>
name|TOP_LEVEL
init|=
name|concat
argument_list|(
name|QUERY
argument_list|,
name|DML
argument_list|,
name|DDL
argument_list|)
decl_stmt|;
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
comment|/** Returns the kind that corresponds to this operator but in the opposite    * direction. Or returns this, if this kind is not reversible.    *    *<p>For example, {@code GREATER_THAN.reverse()} returns {@link #LESS_THAN}.    */
specifier|public
name|SqlKind
name|reverse
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|GREATER_THAN
case|:
return|return
name|LESS_THAN
return|;
case|case
name|GREATER_THAN_OR_EQUAL
case|:
return|return
name|LESS_THAN_OR_EQUAL
return|;
case|case
name|LESS_THAN
case|:
return|return
name|GREATER_THAN
return|;
case|case
name|LESS_THAN_OR_EQUAL
case|:
return|return
name|GREATER_THAN_OR_EQUAL
return|;
default|default:
return|return
name|this
return|;
block|}
block|}
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
annotation|@
name|SafeVarargs
specifier|private
specifier|static
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|EnumSet
argument_list|<
name|E
argument_list|>
name|concat
parameter_list|(
name|EnumSet
argument_list|<
name|E
argument_list|>
name|set0
parameter_list|,
name|EnumSet
argument_list|<
name|E
argument_list|>
modifier|...
name|sets
parameter_list|)
block|{
name|EnumSet
argument_list|<
name|E
argument_list|>
name|set
init|=
name|set0
operator|.
name|clone
argument_list|()
decl_stmt|;
for|for
control|(
name|EnumSet
argument_list|<
name|E
argument_list|>
name|s
range|:
name|sets
control|)
block|{
name|set
operator|.
name|addAll
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|set
return|;
block|}
block|}
end_enum

begin_comment
comment|// End SqlKind.java
end_comment

end_unit

