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

begin_comment
comment|/**  * Enumeration of valid SQL compatibility modes.  *  *<p>For most purposes, one of the built-in compatibility modes in enum  * {@link SqlConformanceEnum} will suffice.  *  *<p>If you wish to implement this interface to build your own conformance,  * we strongly recommend that you extend {@link SqlAbstractConformance},  * or use a {@link SqlDelegatingConformance},  * so that you won't be broken by future changes.  *  * @see SqlConformanceEnum  * @see SqlAbstractConformance  * @see SqlDelegatingConformance  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlConformance
block|{
comment|/** Short-cut for {@link SqlConformanceEnum#DEFAULT}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|DEFAULT
init|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#STRICT_92}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|STRICT_92
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_92
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#STRICT_99}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|STRICT_99
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_99
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#PRAGMATIC_99}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|PRAGMATIC_99
init|=
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_99
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#ORACLE_10}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|ORACLE_10
init|=
name|SqlConformanceEnum
operator|.
name|ORACLE_10
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#STRICT_2003}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|STRICT_2003
init|=
name|SqlConformanceEnum
operator|.
name|STRICT_2003
decl_stmt|;
comment|/** Short-cut for {@link SqlConformanceEnum#PRAGMATIC_2003}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|SqlConformanceEnum
name|PRAGMATIC_2003
init|=
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
decl_stmt|;
comment|/**    * Whether to allow aliases from the {@code SELECT} clause to be used as    * column names in the {@code GROUP BY} clause.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5};    * false otherwise.    */
name|boolean
name|isGroupByAlias
parameter_list|()
function_decl|;
comment|/**    * Whether {@code GROUP BY 2} is interpreted to mean 'group by the 2nd column    * in the select list'.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5};    * false otherwise.    */
name|boolean
name|isGroupByOrdinal
parameter_list|()
function_decl|;
comment|/**    * Whether to allow aliases from the {@code SELECT} clause to be used as    * column names in the {@code HAVING} clause.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5};    * false otherwise.    */
name|boolean
name|isHavingAlias
parameter_list|()
function_decl|;
comment|/**    * Whether '{@code ORDER BY 2}' is interpreted to mean 'sort by the 2nd    * column in the select list'.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#DEFAULT},    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5},    * {@link SqlConformanceEnum#ORACLE_10},    * {@link SqlConformanceEnum#ORACLE_12},    * {@link SqlConformanceEnum#STRICT_92},    * {@link SqlConformanceEnum#PRAGMATIC_99},    * {@link SqlConformanceEnum#PRAGMATIC_2003};    * {@link SqlConformanceEnum#SQL_SERVER_2008};    * false otherwise.    */
name|boolean
name|isSortByOrdinal
parameter_list|()
function_decl|;
comment|/**    * Whether '{@code ORDER BY x}' is interpreted to mean 'sort by the select    * list item whose alias is x' even if there is a column called x.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#DEFAULT},    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5},    * {@link SqlConformanceEnum#ORACLE_10},    * {@link SqlConformanceEnum#ORACLE_12},    * {@link SqlConformanceEnum#STRICT_92};    * {@link SqlConformanceEnum#SQL_SERVER_2008};    * false otherwise.    */
name|boolean
name|isSortByAlias
parameter_list|()
function_decl|;
comment|/**    * Whether "empno" is invalid in "select empno as x from emp order by empno"    * because the alias "x" obscures it.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#STRICT_92};    * false otherwise.    */
name|boolean
name|isSortByAliasObscures
parameter_list|()
function_decl|;
comment|/**    * Whether {@code FROM} clause is required in a {@code SELECT} statement.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#ORACLE_10},    * {@link SqlConformanceEnum#ORACLE_12},    * {@link SqlConformanceEnum#STRICT_92},    * {@link SqlConformanceEnum#STRICT_99},    * {@link SqlConformanceEnum#STRICT_2003};    * false otherwise.    */
name|boolean
name|isFromRequired
parameter_list|()
function_decl|;
comment|/**    * Whether the bang-equal token != is allowed as an alternative to&lt;&gt; in    * the parser.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5},    * {@link SqlConformanceEnum#ORACLE_10};    * {@link SqlConformanceEnum#ORACLE_12};    * false otherwise.    */
name|boolean
name|isBangEqualAllowed
parameter_list|()
function_decl|;
comment|/**    * Whether the "%" operator is allowed by the parser as an alternative to the    * {@code mod} function.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5};    * false otherwise.    */
name|boolean
name|isPercentRemainderAllowed
parameter_list|()
function_decl|;
comment|/**    * Whether {@code MINUS} is allowed as an alternative to {@code EXCEPT} in    * the parser.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#ORACLE_10};    * {@link SqlConformanceEnum#ORACLE_12};    * false otherwise.    *    *<p>Note: MySQL does not support {@code MINUS} or {@code EXCEPT} (as of    * version 5.5).    */
name|boolean
name|isMinusAllowed
parameter_list|()
function_decl|;
comment|/**    * Whether {@code CROSS APPLY} and {@code OUTER APPLY} operators are allowed    * in the parser.    *    *<p>{@code APPLY} invokes a table-valued function for each row returned    * by a table expression. It is syntactic sugar:<ul>    *    *<li>{@code SELECT * FROM emp CROSS APPLY TABLE(promote(empno)}<br>    * is equivalent to<br>    * {@code SELECT * FROM emp CROSS JOIN LATERAL TABLE(promote(empno)}    *    *<li>{@code SELECT * FROM emp OUTER APPLY TABLE(promote(empno)}<br>    * is equivalent to<br>    * {@code SELECT * FROM emp LEFT JOIN LATERAL TABLE(promote(empno)} ON true    *</ul>    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#SQL_SERVER_2008};    * {@link SqlConformanceEnum#ORACLE_12};    * false otherwise.    */
name|boolean
name|isApplyAllowed
parameter_list|()
function_decl|;
comment|/**    * Whether to allow {@code INSERT} (or {@code UPSERT}) with no column list    * but fewer values than the target table.    *    *<p>The N values provided are assumed to match the first N columns of the    * table, and for each of the remaining columns, the default value of the    * column is used. It is an error if any of these columns has no default    * value.    *    *<p>The default value of a column is specified by the {@code DEFAULT}    * clause in the {@code CREATE TABLE} statement, or is {@code NULL} if the    * column is not declared {@code NOT NULL}.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#PRAGMATIC_99},    * {@link SqlConformanceEnum#PRAGMATIC_2003};    * false otherwise.    */
name|boolean
name|isInsertSubsetColumnsAllowed
parameter_list|()
function_decl|;
comment|/**    * Whether to allow parentheses to be specified in calls to niladic functions    * and procedures (that is, functions and procedures with no parameters).    *    *<p>For example, {@code CURRENT_DATE} is a niladic system function. In    * standard SQL it must be invoked without parentheses:    *    *<blockquote><code>VALUES CURRENT_DATE</code></blockquote>    *    *<p>If {@code allowNiladicParentheses}, the following syntax is also valid:    *    *<blockquote><code>VALUES CURRENT_DATE()</code></blockquote>    *    *<p>Of the popular databases, MySQL, Apache Phoenix and VoltDB allow this    * behavior;    * Apache Hive, HSQLDB, IBM DB2, Microsoft SQL Server, Oracle, PostgreSQL do    * not.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5};    * false otherwise.    */
name|boolean
name|allowNiladicParentheses
parameter_list|()
function_decl|;
comment|/**    * Whether to allow mixing table columns with extended columns in    * {@code INSERT} (or {@code UPSERT}).    *    *<p>For example, suppose that the declaration of table {@code T} has columns    * {@code A} and {@code B}, and you want to insert data of column    * {@code C INTEGER} not present in the table declaration as an extended    * column. You can specify the columns in an {@code INSERT} statement as    * follows:    *    *<blockquote>    *<code>INSERT INTO T (A, B, C INTEGER) VALUES (1, 2, 3)</code>    *</blockquote>    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT};    * false otherwise.    */
name|boolean
name|allowExtend
parameter_list|()
function_decl|;
comment|/**    * Whether to allow the SQL syntax "{@code LIMIT start, count}".    *    *<p>The equivalent syntax in standard SQL is    * "{@code OFFSET start ROW FETCH FIRST count ROWS ONLY}",    * and in PostgreSQL "{@code LIMIT count OFFSET start}".    *    *<p>MySQL and CUBRID allow this behavior.    *    *<p>Among the built-in conformance levels, true in    * {@link SqlConformanceEnum#LENIENT},    * {@link SqlConformanceEnum#MYSQL_5};    * false otherwise.    */
name|boolean
name|isLimitStartCountAllowed
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlConformance.java
end_comment

end_unit

