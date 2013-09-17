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
comment|/**  * Enumerates the possible types of {@link SqlNode}.  *  *<p>Only commonly-used nodes have their own type; other nodes are of type  * {@link #OTHER}. Some of the values, such as {@link #SET_QUERY}, represent  * aggregates.</p>  *  * @author jhyde  * @version $Id$  * @since Dec 12, 2003  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlKind
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|// the basics
comment|/**      * Other      */
name|OTHER
block|,
comment|/**      * SELECT statement or sub-query      */
name|SELECT
block|,
comment|/**      * JOIN operator or compound FROM clause.      *      *<p>A FROM clause with more than one table is represented as if it were a      * join. For example, "FROM x, y, z" is represented as "JOIN(x, JOIN(x,      * y))".</p>      */
name|JOIN
block|,
comment|/**      * Identifier      */
name|IDENTIFIER
block|,
comment|/**      * Literal      */
name|LITERAL
block|,
comment|/**      * Function that is not a special function.      *      * @see #FUNCTION      */
name|OTHER_FUNCTION
block|,
comment|/**      * EXPLAIN statement      */
name|EXPLAIN
block|,
comment|/**      * INSERT statement      */
name|INSERT
block|,
comment|/**      * DELETE statement      */
name|DELETE
block|,
comment|/**      * UPDATE statement      */
name|UPDATE
block|,
comment|/**      * Dynamic Param      */
name|DYNAMIC_PARAM
block|,
comment|/**      * ORDER BY clause      */
name|ORDER_BY
block|,
comment|/**      * Union      */
name|UNION
block|,
comment|/**      * Except      */
name|EXCEPT
block|,
comment|/**      * Intersect      */
name|INTERSECT
block|,
comment|/**      * AS operator      */
name|AS
block|,
comment|/**      * OVER operator      */
name|OVER
block|,
comment|/**      * Window specification      */
name|WINDOW
block|,
comment|/**      * MERGE statement      */
name|MERGE
block|,
comment|/**      * TABLESAMPLE operator      */
name|TABLESAMPLE
block|,
comment|// binary operators
comment|/**      * Times      */
name|TIMES
block|,
comment|/**      * Divide      */
name|DIVIDE
block|,
comment|/**      * Plus      */
name|PLUS
block|,
comment|/**      * Minus      */
name|MINUS
block|,
comment|// comparison operators
comment|/**      * In      */
name|IN
block|,
comment|/**      * LessThan      */
name|LESS_THAN
block|,
comment|/**      * Greater Than      */
name|GREATER_THAN
block|,
comment|/**      * Less Than Or Equal      */
name|LESS_THAN_OR_EQUAL
block|,
comment|/**      * Greater Than Or Equal      */
name|GREATER_THAN_OR_EQUAL
block|,
comment|/**      * Equals      */
name|EQUALS
block|,
comment|/**      * Not Equals      */
name|NOT_EQUALS
block|,
comment|/**      * Or      */
name|OR
block|,
comment|/**      * And      */
name|AND
block|,
comment|// other infix
comment|/**      * Dot      */
name|DOT
block|,
comment|/**      * Overlaps      */
name|OVERLAPS
block|,
comment|/**      * Like      */
name|LIKE
block|,
comment|/**      * Similar      */
name|SIMILAR
block|,
comment|/**      * Between      */
name|BETWEEN
block|,
comment|/**      * CASE      */
name|CASE
block|,
comment|// prefix operators
comment|/**      * Not      */
name|NOT
block|,
comment|/**      * PlusPrefix      */
name|PLUS_PREFIX
block|,
comment|/**      * MinusPrefix      */
name|MINUS_PREFIX
block|,
comment|/**      * Exists      */
name|EXISTS
block|,
comment|/**      * Values      */
name|VALUES
block|,
comment|/**      * Explicit table, e.g.<code>select * from (TABLE t)</code> or<code>TABLE      * t</code>. See also {@link #COLLECTION_TABLE}.      */
name|EXPLICIT_TABLE
block|,
comment|/**      * Scalar query; that is, a subquery used in an expression context, and      * returning one row and one column.      */
name|SCALAR_QUERY
block|,
comment|/**      * ProcedureCall      */
name|PROCEDURE_CALL
block|,
comment|/**      * NewSpecification      */
name|NEW_SPECIFICATION
block|,
comment|// postfix operators
comment|/**      * DESC in ORDER BY. A parse tree, not a true expression.      */
name|DESCENDING
block|,
comment|/**      * NULLS FIRST clause in ORDER BY. A parse tree, not a true expression.      */
name|NULLS_FIRST
block|,
comment|/**      * NULLS LAST clause in ORDER BY. A parse tree, not a true expression.      */
name|NULLS_LAST
block|,
comment|/**      * IS TRUE operator.      */
name|IS_TRUE
block|,
comment|/**      * IS FALSE operator.      */
name|IS_FALSE
block|,
comment|/**      * IS UNKNOWN operator.      */
name|IS_UNKNOWN
block|,
comment|/**      * IS NULL operator.      */
name|IS_NULL
block|,
comment|/**      * PRECEDING      */
name|PRECEDING
block|,
comment|/**      * FOLLOWING      */
name|FOLLOWING
block|,
comment|// functions
comment|/**      * ROW function.      */
name|ROW
block|,
comment|/**      * The non-standard constructor used to pass a      * COLUMN_LIST parameter to a UDX.      */
name|COLUMN_LIST
block|,
comment|/**      * CAST operator.      */
name|CAST
block|,
comment|/**      * TRIM function.      */
name|TRIM
block|,
comment|/**      * Call to a function using JDBC function syntax.      */
name|JDBC_FN
block|,
comment|/**      * Multiset Value Constructor.      */
name|MULTISET_VALUE_CONSTRUCTOR
block|,
comment|/**      * Multiset Query Constructor.      */
name|MULTISET_QUERY_CONSTRUCTOR
block|,
comment|/**      * Unnest      */
name|UNNEST
block|,
comment|/**      * Lateral      */
name|LATERAL
block|,
comment|/**      * Table operator which converts user-defined transform into a relation, for      * example,<code>select * from TABLE(udx(x, y, z))</code>. See also the      * {@link #EXPLICIT_TABLE} prefix operator.      */
name|COLLECTION_TABLE
block|,
comment|/**      * Array Value Constructor, e.g. {@code Array[1, 2, 3]}.      */
name|ARRAY_VALUE_CONSTRUCTOR
block|,
comment|/**      * Array Query Constructor, e.g. {@code Array(select deptno from dept)}.      */
name|ARRAY_QUERY_CONSTRUCTOR
block|,
comment|/**      * Map Value Constructor, e.g. {@code Map['washington', 1, 'obama', 44]}.      */
name|MAP_VALUE_CONSTRUCTOR
block|,
comment|/**      * Map Query Constructor, e.g. {@code MAP (SELECT empno, deptno FROM emp)}.      */
name|MAP_QUERY_CONSTRUCTOR
block|,
comment|/**      * CURSOR constructor, for example,<code>select * from      * TABLE(udx(CURSOR(select ...), x, y, z))</code>      */
name|CURSOR
block|,
comment|// internal operators (evaluated in validator) 200-299
comment|/**      * LiteralChain operator (for composite string literals)      */
name|LITERAL_CHAIN
block|,
comment|/**      * Escape operator (always part of LIKE or SIMILAR TO expression)      */
name|ESCAPE
block|,
comment|/**      * Reinterpret operator (a reinterpret cast)      */
name|REINTERPRET
block|;
comment|//~ Static fields/initializers ---------------------------------------------
comment|// Most of the static fields are categories, aggregating several kinds into
comment|// a set.
comment|/**      * Category consisting of set-query node types.      *      *<p>Consists of:      * {@link #EXCEPT},      * {@link #INTERSECT},      * {@link #UNION}.      */
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
comment|/**      * Category consisting of all expression operators.      *      *<p>A node is an expression if it is NOT one of the following:      * {@link #AS},      * {@link #DESCENDING},      * {@link #SELECT},      * {@link #JOIN},      * {@link #OTHER_FUNCTION},      * {@link #CAST},      * {@link #TRIM},      * {@link #LITERAL_CHAIN},      * {@link #JDBC_FN},      * {@link #PRECEDING},      * {@link #FOLLOWING},      * {@link #ORDER_BY},      * {@link #COLLECTION_TABLE},      * {@link #TABLESAMPLE}.      */
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
comment|/**      * Category consisting of all DML operators.      *      *<p>Consists of:      * {@link #INSERT},      * {@link #UPDATE},      * {@link #DELETE},      * {@link #MERGE},      * {@link #PROCEDURE_CALL}.      *      *<p>NOTE jvs 1-June-2006: For now we treat procedure calls as DML;      * this makes it easy for JDBC clients to call execute or      * executeUpdate and not have to process dummy cursor results.  If      * in the future we support procedures which return results sets,      * we'll need to refine this.      */
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
comment|/**      * Category consisting of query node types.      *      *<p>Consists of:      * {@link #SELECT},      * {@link #EXCEPT},      * {@link #INTERSECT},      * {@link #UNION},      * {@link #VALUES},      * {@link #ORDER_BY},      * {@link #EXPLICIT_TABLE}.      */
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
comment|/**      * Category of all SQL statement types.      *      *<p>Consists of all types in {@link #QUERY} and {@link #DML}.      */
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
comment|/**      * Category consisting of regular and special functions.      *      *<p>Consists of regular functions {@link #OTHER_FUNCTION} and specical      * functions {@link #ROW}, {@link #TRIM}, {@link #CAST}, {@link #JDBC_FN}.      */
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
comment|/**      * Category of comparison operators.      *      *<p>Consists of:      * {@link #IN},      * {@link #EQUALS},      * {@link #NOT_EQUALS},      * {@link #LESS_THAN},      * {@link #GREATER_THAN},      * {@link #LESS_THAN_OR_EQUAL},      * {@link #GREATER_THAN_OR_EQUAL}.      */
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
comment|/**      * Returns whether this {@code SqlKind} belongs to a given category.      *      *<p>A category is a collection of kinds, not necessarily disjoint. For      * example, QUERY is { SELECT, UNION, INTERSECT, EXCEPT, VALUES, ORDER_BY,      * EXPLICIT_TABLE }.      *      * @param category Category      * @return Whether this kind belongs to the given cateogry      */
specifier|public
specifier|final
name|boolean
name|belongsTo
parameter_list|(
name|Set
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

