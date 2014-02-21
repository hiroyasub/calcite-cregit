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
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|util
operator|.
name|SqlString
import|;
end_import

begin_comment
comment|/**  * A<code>SqlWriter</code> is the target to construct a SQL statement from a  * parse tree. It deals with dialect differences; for example, Oracle quotes  * identifiers as<code>"scott"</code>, while SQL Server quotes them as<code>  * [scott]</code>.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlWriter
block|{
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * Style of formatting subqueries.    */
enum|enum
name|SubqueryStyle
block|{
comment|/**      * Julian's style of subquery nesting. Like this:      *      *<pre>SELECT *      * FROM (      *     SELECT *      *     FROM t      * )      * WHERE condition</pre>      */
name|HYDE
block|,
comment|/**      * Damian's style of subquery nesting. Like this:      *      *<pre>SELECT *      * FROM      * (   SELECT *      *     FROM t      * )      * WHERE condition</pre>      */
name|BLACK
block|}
comment|/**    * Enumerates the types of frame.    */
enum|enum
name|FrameTypeEnum
implements|implements
name|FrameType
block|{
comment|/**      * SELECT query (or UPDATE or DELETE). The items in the list are the      * clauses: FROM, WHERE, etc.      */
name|SELECT
block|,
comment|/**      * Simple list.      */
name|SIMPLE
block|,
comment|/**      * The SELECT clause of a SELECT statement.      */
name|SELECT_LIST
block|,
comment|/**      * The WINDOW clause of a SELECT statement.      */
name|WINDOW_DECL_LIST
block|,
comment|/**      * The SET clause of an UPDATE statement.      */
name|UPDATE_SET_LIST
block|,
comment|/**      * Function declaration.      */
name|FUN_DECL
block|,
comment|/**      * Function call or datatype declaration.      *      *<p>Examples:      *<li>SUBSTRING('foobar' FROM 1 + 2 TO 4)</li>      *<li>DECIMAL(10, 5)</li>      */
name|FUN_CALL
block|,
comment|/**      * Window specification.      *      *<p>Examples:      *<li>SUM(x) OVER (ORDER BY hireDate ROWS 3 PRECEDING)</li>      *<li>WINDOW w1 AS (ORDER BY hireDate), w2 AS (w1 PARTITION BY gender      * RANGE BETWEEN INTERVAL '1' YEAR PRECEDING AND '2' MONTH      * PRECEDING)</li>      */
name|WINDOW
block|,
comment|/**      * ORDER BY clause of a SELECT statement. The "list" has only two items:      * the query and the order by clause, with ORDER BY as the separator.      */
name|ORDER_BY
block|,
comment|/**      * ORDER BY list.      *      *<p>Example:      *<li>ORDER BY x, y DESC, z      */
name|ORDER_BY_LIST
block|,
comment|/**      * WITH clause of a SELECT statement. The "list" has only two items:      * the WITH clause and the query, with AS as the separator.      */
name|WITH
block|,
comment|/**      * OFFSET clause.      *      *<p>Example:      *<li>OFFSET 10 ROWS</li></p>      */
name|OFFSET
block|,
comment|/**      * FETCH clause.      *      *<p>Example:      *<li>FETCH FIRST 3 ROWS ONLY</li></p>      */
name|FETCH
block|,
comment|/**      * GROUP BY list.      *      *<p>Example:      *<li>GROUP BY x, FLOOR(y)      */
name|GROUP_BY_LIST
block|,
comment|/**      * Sub-query list. Encloses a SELECT, UNION, EXCEPT, INTERSECT query      * with optional ORDER BY.      *      *<p>Example:      *<li>GROUP BY x, FLOOR(y)      */
name|SUB_QUERY
block|,
comment|/**      * Set operation.      *      *<p>Example:      *<li>SELECT * FROM a UNION SELECT * FROM b      */
name|SETOP
block|,
comment|/**      * FROM clause (containing various kinds of JOIN).      */
name|FROM_LIST
block|,
comment|/**      * WHERE clause.      */
name|WHERE_LIST
block|,
comment|/**      * Compound identifier.      *      *<p>Example:      *<li>"A"."B"."C"      */
name|IDENTIFIER
argument_list|(
literal|false
argument_list|)
block|;
specifier|private
specifier|final
name|boolean
name|needsIndent
decl_stmt|;
comment|/**      * Creates a list type.      */
name|FrameTypeEnum
parameter_list|()
block|{
name|this
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a list type.      */
name|FrameTypeEnum
parameter_list|(
name|boolean
name|needsIndent
parameter_list|)
block|{
name|this
operator|.
name|needsIndent
operator|=
name|needsIndent
expr_stmt|;
block|}
specifier|public
name|boolean
name|needsIndent
parameter_list|()
block|{
return|return
name|needsIndent
return|;
block|}
comment|/**      * Creates a frame type.      *      * @param name Name      * @return frame type      */
specifier|public
specifier|static
name|FrameType
name|create
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|FrameType
argument_list|()
block|{
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|boolean
name|needsIndent
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
argument_list|()
return|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Resets this writer so that it can format another expression. Does not    * affect formatting preferences (see {@link #resetSettings()}    */
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**    * Resets all properties to their default values.    */
name|void
name|resetSettings
parameter_list|()
function_decl|;
comment|/**    * Returns the dialect of SQL.    *    * @return SQL dialect    */
name|SqlDialect
name|getDialect
parameter_list|()
function_decl|;
comment|/**    * Returns the contents of this writer as a 'certified kocher' SQL string.    *    * @return SQL string    */
name|SqlString
name|toSqlString
parameter_list|()
function_decl|;
comment|/**    * Prints a literal, exactly as provided. Does not attempt to indent or    * convert to upper or lower case. Does not add quotation marks. Adds    * preceding whitespace if necessary.    */
name|void
name|literal
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
comment|/**    * Prints a sequence of keywords. Must not start or end with space, but may    * contain a space. For example,<code>keyword("SELECT")</code>,<code>    * keyword("CHARACTER SET")</code>.    */
name|void
name|keyword
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
comment|/**    * Prints a string, preceded by whitespace if necessary.    */
name|void
name|print
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
comment|/**    * Prints an integer.    *    * @param x Integer    */
name|void
name|print
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
comment|/**    * Prints an identifier, quoting as necessary.    */
name|void
name|identifier
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**    * Prints a new line, and indents.    */
name|void
name|newlineAndIndent
parameter_list|()
function_decl|;
comment|/**    * Returns whether this writer should quote all identifiers, even those    * that do not contain mixed-case identifiers or punctuation.    *    * @return whether to quote all identifiers    */
name|boolean
name|isQuoteAllIdentifiers
parameter_list|()
function_decl|;
comment|/**    * Returns whether this writer should start each clause (e.g. GROUP BY) on    * a new line.    *    * @return whether to start each clause on a new line    */
name|boolean
name|isClauseStartsLine
parameter_list|()
function_decl|;
comment|/**    * Returns whether the items in the SELECT clause should each be on a    * separate line.    *    * @return whether to put each SELECT clause item on a new line    */
name|boolean
name|isSelectListItemsOnSeparateLines
parameter_list|()
function_decl|;
comment|/**    * Returns whether to output all keywords (e.g. SELECT, GROUP BY) in lower    * case.    *    * @return whether to output SQL keywords in lower case    */
name|boolean
name|isKeywordsLowerCase
parameter_list|()
function_decl|;
comment|/**    * Starts a list which is a call to a function.    *    * @see #endFunCall(Frame)    */
name|Frame
name|startFunCall
parameter_list|(
name|String
name|funName
parameter_list|)
function_decl|;
comment|/**    * Ends a list which is a call to a function.    *    * @param frame Frame    * @see #startFunCall(String)    */
name|void
name|endFunCall
parameter_list|(
name|Frame
name|frame
parameter_list|)
function_decl|;
comment|/**    * Starts a list.    */
name|Frame
name|startList
parameter_list|(
name|String
name|open
parameter_list|,
name|String
name|close
parameter_list|)
function_decl|;
comment|/**    * Starts a list with no opening string.    *    * @param frameType Type of list. For example, a SELECT list will be    */
name|Frame
name|startList
parameter_list|(
name|FrameTypeEnum
name|frameType
parameter_list|)
function_decl|;
comment|/**    * Starts a list.    *    * @param frameType Type of list. For example, a SELECT list will be    *                  governed according to SELECT-list formatting preferences.    * @param open      String to start the list; typically "(" or the empty    *                  string.    * @param close     String to close the list    */
name|Frame
name|startList
parameter_list|(
name|FrameType
name|frameType
parameter_list|,
name|String
name|open
parameter_list|,
name|String
name|close
parameter_list|)
function_decl|;
comment|/**    * Ends a list.    *    * @param frame The frame which was created by {@link #startList}.    */
name|void
name|endList
parameter_list|(
name|Frame
name|frame
parameter_list|)
function_decl|;
comment|/**    * Writes a list separator, unless the separator is "," and this is the    * first occurrence in the list.    *    * @param sep List separator, typically ",".    */
name|void
name|sep
parameter_list|(
name|String
name|sep
parameter_list|)
function_decl|;
comment|/**    * Writes a list separator.    *    * @param sep        List separator, typically ","    * @param printFirst Whether to print the first occurrence of the separator    */
name|void
name|sep
parameter_list|(
name|String
name|sep
parameter_list|,
name|boolean
name|printFirst
parameter_list|)
function_decl|;
comment|/**    * Sets whether whitespace is needed before the next token.    */
name|void
name|setNeedWhitespace
parameter_list|(
name|boolean
name|needWhitespace
parameter_list|)
function_decl|;
comment|/**    * Returns the offset for each level of indentation. Default 4.    */
name|int
name|getIndentation
parameter_list|()
function_decl|;
comment|/**    * Returns whether to enclose all expressions in parentheses, even if the    * operator has high enough precedence that the parentheses are not    * required.    *    *<p>For example, the parentheses are required in the expression<code>(a +    * b) * c</code> because the '*' operator has higher precedence than the '+'    * operator, and so without the parentheses, the expression would be    * equivalent to<code>a + (b * c)</code>. The fully-parenthesized    * expression,<code>((a + b) * c)</code> is unambiguous even if you don't    * know the precedence of every operator.    */
name|boolean
name|isAlwaysUseParentheses
parameter_list|()
function_decl|;
comment|/**    * Returns whether we are currently in a query context (SELECT, INSERT,    * UNION, INTERSECT, EXCEPT, and the ORDER BY operator).    */
name|boolean
name|inQuery
parameter_list|()
function_decl|;
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/**    * A Frame is a piece of generated text which shares a common indentation    * level.    *    *<p>Every frame has a beginning, a series of clauses and separators, and    * an end. A typical frame is a comma-separated list. It begins with a "(",    * consists of expressions separated by ",", and ends with a ")".    *    *<p>A select statement is also a kind of frame. The beginning and end are    * are empty strings, but it consists of a sequence of clauses. "SELECT",    * "FROM", "WHERE" are separators.    *    *<p>A frame is current between a call to one of the {@link    * SqlWriter#startList} methods and the call to {@link    * SqlWriter#endList(Frame)}. If other code starts a frame in the mean time,    * the sub-frame is put onto a stack.    */
specifier|public
interface|interface
name|Frame
block|{   }
interface|interface
name|FrameType
block|{
comment|/**      * Returns the name of this frame type.      *      * @return name      */
name|String
name|getName
parameter_list|()
function_decl|;
comment|/**      * Returns whether this frame type should cause the code be further      * indented.      *      * @return whether to further indent code within a frame of this type      */
name|boolean
name|needsIndent
parameter_list|()
function_decl|;
block|}
block|}
end_interface

begin_comment
comment|// End SqlWriter.java
end_comment

end_unit

