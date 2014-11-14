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
name|parser
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
name|avatica
operator|.
name|Casing
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
name|SqlFunctionCategory
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
name|SqlLiteral
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
name|SqlOperator
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
name|SqlSyntax
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
name|SqlUnresolvedFunction
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|Util
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_comment
comment|/**  * Abstract base for parsers generated from CommonParser.jj.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SqlAbstractParserImpl
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|ImmutableSet
argument_list|<
name|String
argument_list|>
name|SQL_92_RESERVED_WORD_SET
init|=
name|ImmutableSet
operator|.
name|of
argument_list|(
literal|"ABSOLUTE"
argument_list|,
literal|"ACTION"
argument_list|,
literal|"ADD"
argument_list|,
literal|"ALL"
argument_list|,
literal|"ALLOCATE"
argument_list|,
literal|"ALTER"
argument_list|,
literal|"AND"
argument_list|,
literal|"ANY"
argument_list|,
literal|"ARE"
argument_list|,
literal|"AS"
argument_list|,
literal|"ASC"
argument_list|,
literal|"ASSERTION"
argument_list|,
literal|"AT"
argument_list|,
literal|"AUTHORIZATION"
argument_list|,
literal|"AVG"
argument_list|,
literal|"BEGIN"
argument_list|,
literal|"BETWEEN"
argument_list|,
literal|"BIT"
argument_list|,
literal|"BIT_LENGTH"
argument_list|,
literal|"BOTH"
argument_list|,
literal|"BY"
argument_list|,
literal|"CASCADE"
argument_list|,
literal|"CASCADED"
argument_list|,
literal|"CASE"
argument_list|,
literal|"CAST"
argument_list|,
literal|"CATALOG"
argument_list|,
literal|"CHAR"
argument_list|,
literal|"CHARACTER"
argument_list|,
literal|"CHARACTER_LENGTH"
argument_list|,
literal|"CHAR_LENGTH"
argument_list|,
literal|"CHECK"
argument_list|,
literal|"CLOSE"
argument_list|,
literal|"COALESCE"
argument_list|,
literal|"COLLATE"
argument_list|,
literal|"COLLATION"
argument_list|,
literal|"COLUMN"
argument_list|,
literal|"COMMIT"
argument_list|,
literal|"CONNECT"
argument_list|,
literal|"CONNECTION"
argument_list|,
literal|"CONSTRAINT"
argument_list|,
literal|"CONSTRAINTS"
argument_list|,
literal|"CONTINUE"
argument_list|,
literal|"CONVERT"
argument_list|,
literal|"CORRESPONDING"
argument_list|,
literal|"COUNT"
argument_list|,
literal|"CREATE"
argument_list|,
literal|"CROSS"
argument_list|,
literal|"CURRENT"
argument_list|,
literal|"CURRENT_DATE"
argument_list|,
literal|"CURRENT_TIME"
argument_list|,
literal|"CURRENT_TIMESTAMP"
argument_list|,
literal|"CURRENT_USER"
argument_list|,
literal|"CURSOR"
argument_list|,
literal|"DATE"
argument_list|,
literal|"DAY"
argument_list|,
literal|"DEALLOCATE"
argument_list|,
literal|"DEC"
argument_list|,
literal|"DECIMAL"
argument_list|,
literal|"DECLARE"
argument_list|,
literal|"DEFAULT"
argument_list|,
literal|"DEFERRABLE"
argument_list|,
literal|"DEFERRED"
argument_list|,
literal|"DELETE"
argument_list|,
literal|"DESC"
argument_list|,
literal|"DESCRIBE"
argument_list|,
literal|"DESCRIPTOR"
argument_list|,
literal|"DIAGNOSTICS"
argument_list|,
literal|"DISCONNECT"
argument_list|,
literal|"DISTINCT"
argument_list|,
literal|"DOMAIN"
argument_list|,
literal|"DOUBLE"
argument_list|,
literal|"DROP"
argument_list|,
literal|"ELSE"
argument_list|,
literal|"END"
argument_list|,
literal|"END-EXEC"
argument_list|,
literal|"ESCAPE"
argument_list|,
literal|"EXCEPT"
argument_list|,
literal|"EXCEPTION"
argument_list|,
literal|"EXEC"
argument_list|,
literal|"EXECUTE"
argument_list|,
literal|"EXISTS"
argument_list|,
literal|"EXTERNAL"
argument_list|,
literal|"EXTRACT"
argument_list|,
literal|"FALSE"
argument_list|,
literal|"FETCH"
argument_list|,
literal|"FIRST"
argument_list|,
literal|"FLOAT"
argument_list|,
literal|"FOR"
argument_list|,
literal|"FOREIGN"
argument_list|,
literal|"FOUND"
argument_list|,
literal|"FROM"
argument_list|,
literal|"FULL"
argument_list|,
literal|"GET"
argument_list|,
literal|"GLOBAL"
argument_list|,
literal|"GO"
argument_list|,
literal|"GOTO"
argument_list|,
literal|"GRANT"
argument_list|,
literal|"GROUP"
argument_list|,
literal|"HAVING"
argument_list|,
literal|"HOUR"
argument_list|,
literal|"IDENTITY"
argument_list|,
literal|"IMMEDIATE"
argument_list|,
literal|"IN"
argument_list|,
literal|"INDICATOR"
argument_list|,
literal|"INITIALLY"
argument_list|,
literal|"INNER"
argument_list|,
literal|"INADD"
argument_list|,
literal|"INSENSITIVE"
argument_list|,
literal|"INSERT"
argument_list|,
literal|"INT"
argument_list|,
literal|"INTEGER"
argument_list|,
literal|"INTERSECT"
argument_list|,
literal|"INTERVAL"
argument_list|,
literal|"INTO"
argument_list|,
literal|"IS"
argument_list|,
literal|"ISOLATION"
argument_list|,
literal|"JOIN"
argument_list|,
literal|"KEY"
argument_list|,
literal|"LANGUAGE"
argument_list|,
literal|"LAST"
argument_list|,
literal|"LEADING"
argument_list|,
literal|"LEFT"
argument_list|,
literal|"LEVEL"
argument_list|,
literal|"LIKE"
argument_list|,
literal|"LOCAL"
argument_list|,
literal|"LOWER"
argument_list|,
literal|"MATCH"
argument_list|,
literal|"MAX"
argument_list|,
literal|"MIN"
argument_list|,
literal|"MINUTE"
argument_list|,
literal|"MODULE"
argument_list|,
literal|"MONTH"
argument_list|,
literal|"NAMES"
argument_list|,
literal|"NATIONAL"
argument_list|,
literal|"NATURAL"
argument_list|,
literal|"NCHAR"
argument_list|,
literal|"NEXT"
argument_list|,
literal|"NO"
argument_list|,
literal|"NOT"
argument_list|,
literal|"NULL"
argument_list|,
literal|"NULLIF"
argument_list|,
literal|"NUMERIC"
argument_list|,
literal|"OCTET_LENGTH"
argument_list|,
literal|"OF"
argument_list|,
literal|"ON"
argument_list|,
literal|"ONLY"
argument_list|,
literal|"OPEN"
argument_list|,
literal|"OPTION"
argument_list|,
literal|"OR"
argument_list|,
literal|"ORDER"
argument_list|,
literal|"OUTER"
argument_list|,
literal|"OUTADD"
argument_list|,
literal|"OVERLAPS"
argument_list|,
literal|"PAD"
argument_list|,
literal|"PARTIAL"
argument_list|,
literal|"POSITION"
argument_list|,
literal|"PRECISION"
argument_list|,
literal|"PREPARE"
argument_list|,
literal|"PRESERVE"
argument_list|,
literal|"PRIMARY"
argument_list|,
literal|"PRIOR"
argument_list|,
literal|"PRIVILEGES"
argument_list|,
literal|"PROCEDURE"
argument_list|,
literal|"PUBLIC"
argument_list|,
literal|"READ"
argument_list|,
literal|"REAL"
argument_list|,
literal|"REFERENCES"
argument_list|,
literal|"RELATIVE"
argument_list|,
literal|"RESTRICT"
argument_list|,
literal|"REVOKE"
argument_list|,
literal|"RIGHT"
argument_list|,
literal|"ROLLBACK"
argument_list|,
literal|"ROWS"
argument_list|,
literal|"SCHEMA"
argument_list|,
literal|"SCROLL"
argument_list|,
literal|"SECOND"
argument_list|,
literal|"SECTION"
argument_list|,
literal|"SELECT"
argument_list|,
literal|"SESSION"
argument_list|,
literal|"SESSION_USER"
argument_list|,
literal|"SET"
argument_list|,
literal|"SIZE"
argument_list|,
literal|"SMALLINT"
argument_list|,
literal|"SOME"
argument_list|,
literal|"SPACE"
argument_list|,
literal|"SQL"
argument_list|,
literal|"SQLCODE"
argument_list|,
literal|"SQLERROR"
argument_list|,
literal|"SQLSTATE"
argument_list|,
literal|"SUBSTRING"
argument_list|,
literal|"SUM"
argument_list|,
literal|"SYSTEM_USER"
argument_list|,
literal|"TABLE"
argument_list|,
literal|"TEMPORARY"
argument_list|,
literal|"THEN"
argument_list|,
literal|"TIME"
argument_list|,
literal|"TIMESTAMP"
argument_list|,
literal|"TIMEZONE_HOUR"
argument_list|,
literal|"TIMEZONE_MINUTE"
argument_list|,
literal|"TO"
argument_list|,
literal|"TRAILING"
argument_list|,
literal|"TRANSACTION"
argument_list|,
literal|"TRANSLATE"
argument_list|,
literal|"TRANSLATION"
argument_list|,
literal|"TRIM"
argument_list|,
literal|"TRUE"
argument_list|,
literal|"UNION"
argument_list|,
literal|"UNIQUE"
argument_list|,
literal|"UNKNOWN"
argument_list|,
literal|"UPDATE"
argument_list|,
literal|"UPPER"
argument_list|,
literal|"USAGE"
argument_list|,
literal|"USER"
argument_list|,
literal|"USING"
argument_list|,
literal|"VALUE"
argument_list|,
literal|"VALUES"
argument_list|,
literal|"VARCHAR"
argument_list|,
literal|"VARYING"
argument_list|,
literal|"VIEW"
argument_list|,
literal|"WHEN"
argument_list|,
literal|"WHENEVER"
argument_list|,
literal|"WHERE"
argument_list|,
literal|"WITH"
argument_list|,
literal|"WORK"
argument_list|,
literal|"WRITE"
argument_list|,
literal|"YEAR"
argument_list|,
literal|"ZONE"
argument_list|)
decl_stmt|;
comment|//~ Enums ------------------------------------------------------------------
comment|/**    * Type-safe enum for context of acceptable expressions.    */
specifier|protected
enum|enum
name|ExprContext
block|{
comment|/**      * Accept any kind of expression in this context.      */
name|ACCEPT_ALL
block|,
comment|/**      * Accept any kind of expression in this context, with the exception of      * CURSOR constructors.      */
name|ACCEPT_NONCURSOR
block|,
comment|/**      * Accept only query expressions in this context.      */
name|ACCEPT_QUERY
block|,
comment|/**      * Accept only non-query expressions in this context.      */
name|ACCEPT_NONQUERY
block|,
comment|/**      * Accept only parenthesized queries or non-query expressions in this      * context.      */
name|ACCEPT_SUBQUERY
block|,
comment|/**      * Accept only CURSOR constructors, parenthesized queries, or non-query      * expressions in this context.      */
name|ACCEPT_CURSOR
block|}
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Operator table containing the standard SQL operators and functions.    */
specifier|protected
specifier|final
name|SqlStdOperatorTable
name|opTab
init|=
name|SqlStdOperatorTable
operator|.
name|instance
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|nDynamicParams
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return immutable set of all reserved words defined by SQL-92    * @sql.92 Section 5.2    */
specifier|public
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getSql92ReservedWords
parameter_list|()
block|{
return|return
name|SQL_92_RESERVED_WORD_SET
return|;
block|}
comment|/**    * Creates a call.    *    * @param funName           Name of function    * @param pos               Position in source code    * @param funcType          Type of function    * @param functionQualifier Qualifier    * @param operands          Operands to call    * @return Call    */
specifier|protected
name|SqlCall
name|createCall
parameter_list|(
name|SqlIdentifier
name|funName
parameter_list|,
name|SqlParserPos
name|pos
parameter_list|,
name|SqlFunctionCategory
name|funcType
parameter_list|,
name|SqlLiteral
name|functionQualifier
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|)
block|{
name|SqlOperator
name|fun
init|=
literal|null
decl_stmt|;
comment|// First, try a half-hearted resolution as a builtin function.
comment|// If we find one, use it; this will guarantee that we
comment|// preserve the correct syntax (i.e. don't quote builtin function
comment|/// name when regenerating SQL).
if|if
condition|(
name|funName
operator|.
name|isSimple
argument_list|()
condition|)
block|{
specifier|final
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|list
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|opTab
operator|.
name|lookupOperatorOverloads
argument_list|(
name|funName
argument_list|,
literal|null
argument_list|,
name|SqlSyntax
operator|.
name|FUNCTION
argument_list|,
name|list
argument_list|)
expr_stmt|;
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|fun
operator|=
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Otherwise, just create a placeholder function.  Later, during
comment|// validation, it will be resolved into a real function reference.
if|if
condition|(
name|fun
operator|==
literal|null
condition|)
block|{
name|fun
operator|=
operator|new
name|SqlUnresolvedFunction
argument_list|(
name|funName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|funcType
argument_list|)
expr_stmt|;
block|}
return|return
name|fun
operator|.
name|createCall
argument_list|(
name|functionQualifier
argument_list|,
name|pos
argument_list|,
name|operands
argument_list|)
return|;
block|}
comment|/**    * Returns metadata about this parser: keywords, etc.    */
specifier|public
specifier|abstract
name|Metadata
name|getMetadata
parameter_list|()
function_decl|;
comment|/**    * Removes or transforms misleading information from a parse exception or    * error, and converts to {@link SqlParseException}.    *    * @param ex dirty excn    * @return clean excn    */
specifier|public
specifier|abstract
name|SqlParseException
name|normalizeException
parameter_list|(
name|Throwable
name|ex
parameter_list|)
function_decl|;
comment|/**    * Reinitializes parser with new input.    *    * @param reader provides new input    */
comment|// CHECKSTYLE: IGNORE 1
specifier|public
specifier|abstract
name|void
name|ReInit
parameter_list|(
name|Reader
name|reader
parameter_list|)
function_decl|;
comment|/**    * Parses a SQL expression ending with EOF and constructs a    * parse tree.    *    * @return constructed parse tree.    */
specifier|public
specifier|abstract
name|SqlNode
name|parseSqlExpressionEof
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Parses a SQL statement ending with EOF and constructs a    * parse tree.    *    * @return constructed parse tree.    */
specifier|public
specifier|abstract
name|SqlNode
name|parseSqlStmtEof
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Sets the tab stop size.    *    * @param tabSize Tab stop size    */
specifier|public
specifier|abstract
name|void
name|setTabSize
parameter_list|(
name|int
name|tabSize
parameter_list|)
function_decl|;
comment|/**    * Sets the casing policy for quoted identifiers.    *    * @param quotedCasing Casing to set.    */
specifier|public
specifier|abstract
name|void
name|setQuotedCasing
parameter_list|(
name|Casing
name|quotedCasing
parameter_list|)
function_decl|;
comment|/**    * Sets the casing policy for unquoted identifiers.    *    * @param unquotedCasing Casing to set.    */
specifier|public
specifier|abstract
name|void
name|setUnquotedCasing
parameter_list|(
name|Casing
name|unquotedCasing
parameter_list|)
function_decl|;
comment|/**    * Sets the maximum length for sql identifier.    */
specifier|public
specifier|abstract
name|void
name|setIdentifierMaxLength
parameter_list|(
name|int
name|identifierMaxLength
parameter_list|)
function_decl|;
comment|/**    * Change parser state.    *    * @param stateName new state.    */
specifier|public
specifier|abstract
name|void
name|switchTo
parameter_list|(
name|String
name|stateName
parameter_list|)
function_decl|;
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/**    * Metadata about the parser. For example:    *    *<ul>    *<li>"KEY" is a keyword: it is meaningful in certain contexts, such as    * "CREATE FOREIGN KEY", but can be used as an identifier, as in<code>    * "CREATE TABLE t (key INTEGER)"</code>.    *<li>"SELECT" is a reserved word. It can not be used as an identifier.    *<li>"CURRENT_USER" is the name of a context variable. It cannot be used    * as an identifier.    *<li>"ABS" is the name of a reserved function. It cannot be used as an    * identifier.    *<li>"DOMAIN" is a reserved word as specified by the SQL:92 standard.    *</ul>    */
specifier|public
interface|interface
name|Metadata
block|{
comment|/**      * Returns true if token is a keyword but not a reserved word. For      * example, "KEY".      */
name|boolean
name|isNonReservedKeyword
parameter_list|(
name|String
name|token
parameter_list|)
function_decl|;
comment|/**      * Returns whether token is the name of a context variable such as      * "CURRENT_USER".      */
name|boolean
name|isContextVariableName
parameter_list|(
name|String
name|token
parameter_list|)
function_decl|;
comment|/**      * Returns whether token is a reserved function name such as      * "CURRENT_USER".      */
name|boolean
name|isReservedFunctionName
parameter_list|(
name|String
name|token
parameter_list|)
function_decl|;
comment|/**      * Returns whether token is a keyword. (That is, a non-reserved keyword,      * a context variable, or a reserved function name.)      */
name|boolean
name|isKeyword
parameter_list|(
name|String
name|token
parameter_list|)
function_decl|;
comment|/**      * Returns whether token is a reserved word.      */
name|boolean
name|isReservedWord
parameter_list|(
name|String
name|token
parameter_list|)
function_decl|;
comment|/**      * Returns whether token is a reserved word as specified by the SQL:92      * standard.      */
name|boolean
name|isSql92ReservedWord
parameter_list|(
name|String
name|token
parameter_list|)
function_decl|;
comment|/**      * Returns comma-separated list of JDBC keywords.      */
name|String
name|getJdbcKeywords
parameter_list|()
function_decl|;
comment|/**      * Returns a list of all tokens in alphabetical order.      */
name|List
argument_list|<
name|String
argument_list|>
name|getTokens
parameter_list|()
function_decl|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Default implementation of the {@link Metadata} interface.    */
specifier|public
specifier|static
class|class
name|MetadataImpl
implements|implements
name|Metadata
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|reservedFunctionNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|contextVariableNames
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|nonReservedKeyWordSet
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Set of all tokens.      */
specifier|private
specifier|final
name|SortedSet
argument_list|<
name|String
argument_list|>
name|tokenSet
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Immutable list of all tokens, in alphabetical order.      */
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|tokenList
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|reservedWords
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|sql92ReservedWords
decl_stmt|;
comment|/**      * Creates a MetadataImpl.      *      * @param sqlParser Parser      */
specifier|public
name|MetadataImpl
parameter_list|(
name|SqlAbstractParserImpl
name|sqlParser
parameter_list|)
block|{
name|initList
argument_list|(
name|sqlParser
argument_list|,
name|reservedFunctionNames
argument_list|,
literal|"ReservedFunctionName"
argument_list|)
expr_stmt|;
name|initList
argument_list|(
name|sqlParser
argument_list|,
name|contextVariableNames
argument_list|,
literal|"ContextVariable"
argument_list|)
expr_stmt|;
name|initList
argument_list|(
name|sqlParser
argument_list|,
name|nonReservedKeyWordSet
argument_list|,
literal|"NonReservedKeyWord"
argument_list|)
expr_stmt|;
name|tokenList
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|tokenSet
argument_list|)
expr_stmt|;
name|sql92ReservedWords
operator|=
name|constructSql92ReservedWordList
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|reservedWordSet
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|reservedWordSet
operator|.
name|addAll
argument_list|(
name|tokenSet
argument_list|)
expr_stmt|;
name|reservedWordSet
operator|.
name|removeAll
argument_list|(
name|nonReservedKeyWordSet
argument_list|)
expr_stmt|;
name|reservedWords
operator|.
name|addAll
argument_list|(
name|reservedWordSet
argument_list|)
expr_stmt|;
block|}
comment|/**      * Initializes lists of keywords.      */
specifier|private
name|void
name|initList
parameter_list|(
name|SqlAbstractParserImpl
name|parserImpl
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|keywords
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|parserImpl
operator|.
name|ReInit
argument_list|(
operator|new
name|StringReader
argument_list|(
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|Object
name|o
init|=
name|virtualCall
argument_list|(
name|parserImpl
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|o
argument_list|)
expr_stmt|;
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"expected call to fail"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|parseException
parameter_list|)
block|{
comment|// First time through, build the list of all tokens.
specifier|final
name|String
index|[]
name|tokenImages
init|=
name|parseException
operator|.
name|getTokenImages
argument_list|()
decl_stmt|;
if|if
condition|(
name|tokenSet
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|token
range|:
name|tokenImages
control|)
block|{
name|String
name|tokenVal
init|=
name|SqlParserUtil
operator|.
name|getTokenVal
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenVal
operator|!=
literal|null
condition|)
block|{
name|tokenSet
operator|.
name|add
argument_list|(
name|tokenVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// Add the tokens which would have been expected in this
comment|// syntactic context to the list we're building.
specifier|final
name|int
index|[]
index|[]
name|expectedTokenSequences
init|=
name|parseException
operator|.
name|getExpectedTokenSequences
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|int
index|[]
name|tokens
range|:
name|expectedTokenSequences
control|)
block|{
assert|assert
name|tokens
operator|.
name|length
operator|==
literal|1
assert|;
specifier|final
name|int
name|tokenId
init|=
name|tokens
index|[
literal|0
index|]
decl_stmt|;
name|String
name|token
init|=
name|tokenImages
index|[
name|tokenId
index|]
decl_stmt|;
name|String
name|tokenVal
init|=
name|SqlParserUtil
operator|.
name|getTokenVal
argument_list|(
name|token
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokenVal
operator|!=
literal|null
condition|)
block|{
name|keywords
operator|.
name|add
argument_list|(
name|tokenVal
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"Unexpected error while building token lists"
argument_list|)
throw|;
block|}
block|}
comment|/**      * Uses reflection to invoke a method on this parser. The method must be      * public and have no parameters.      *      * @param parserImpl Parser      * @param name       Name of method. For example "ReservedFunctionName".      * @return Result of calling method      */
specifier|private
name|Object
name|virtualCall
parameter_list|(
name|SqlAbstractParserImpl
name|parserImpl
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|Throwable
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|parserImpl
operator|.
name|getClass
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Method
name|method
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
name|name
argument_list|,
operator|(
name|Class
index|[]
operator|)
literal|null
argument_list|)
decl_stmt|;
return|return
name|method
operator|.
name|invoke
argument_list|(
name|parserImpl
argument_list|,
operator|(
name|Object
index|[]
operator|)
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
name|Throwable
name|cause
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
throw|throw
name|parserImpl
operator|.
name|normalizeException
argument_list|(
name|cause
argument_list|)
throw|;
block|}
block|}
comment|/**      * Builds a comma-separated list of JDBC reserved words.      */
specifier|private
name|String
name|constructSql92ReservedWordList
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|TreeSet
argument_list|<
name|String
argument_list|>
name|jdbcReservedSet
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|jdbcReservedSet
operator|.
name|addAll
argument_list|(
name|tokenSet
argument_list|)
expr_stmt|;
name|jdbcReservedSet
operator|.
name|removeAll
argument_list|(
name|SQL_92_RESERVED_WORD_SET
argument_list|)
expr_stmt|;
name|jdbcReservedSet
operator|.
name|removeAll
argument_list|(
name|nonReservedKeyWordSet
argument_list|)
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|jdbcReserved
range|:
name|jdbcReservedSet
control|)
block|{
if|if
condition|(
name|j
operator|++
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|jdbcReserved
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getTokens
parameter_list|()
block|{
return|return
name|tokenList
return|;
block|}
specifier|public
name|boolean
name|isSql92ReservedWord
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|SQL_92_RESERVED_WORD_SET
operator|.
name|contains
argument_list|(
name|token
argument_list|)
return|;
block|}
specifier|public
name|String
name|getJdbcKeywords
parameter_list|()
block|{
return|return
name|sql92ReservedWords
return|;
block|}
specifier|public
name|boolean
name|isKeyword
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|isNonReservedKeyword
argument_list|(
name|token
argument_list|)
operator|||
name|isReservedFunctionName
argument_list|(
name|token
argument_list|)
operator|||
name|isContextVariableName
argument_list|(
name|token
argument_list|)
operator|||
name|isReservedWord
argument_list|(
name|token
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isNonReservedKeyword
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|nonReservedKeyWordSet
operator|.
name|contains
argument_list|(
name|token
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isReservedFunctionName
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|reservedFunctionNames
operator|.
name|contains
argument_list|(
name|token
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isContextVariableName
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|contextVariableNames
operator|.
name|contains
argument_list|(
name|token
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isReservedWord
parameter_list|(
name|String
name|token
parameter_list|)
block|{
return|return
name|reservedWords
operator|.
name|contains
argument_list|(
name|token
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlAbstractParserImpl.java
end_comment

end_unit

