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
name|util
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
name|avatica
operator|.
name|util
operator|.
name|Quoting
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
name|config
operator|.
name|Lex
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
name|runtime
operator|.
name|CalciteContextException
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
name|parser
operator|.
name|impl
operator|.
name|SqlParserImpl
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
name|validate
operator|.
name|SqlConformance
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
name|validate
operator|.
name|SqlConformanceEnum
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
name|validate
operator|.
name|SqlDelegatingConformance
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
name|ImmutableBeans
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
name|SourceStringReader
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
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A<code>SqlParser</code> parses a SQL statement.  */
end_comment

begin_class
specifier|public
class|class
name|SqlParser
block|{
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_IDENTIFIER_MAX_LENGTH
init|=
literal|128
decl_stmt|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_ALLOW_BANG_EQUAL
init|=
name|SqlConformanceEnum
operator|.
name|DEFAULT
operator|.
name|isBangEqualAllowed
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlAbstractParserImpl
name|parser
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|SqlParser
parameter_list|(
name|SqlAbstractParserImpl
name|parser
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|parser
operator|=
name|parser
expr_stmt|;
name|parser
operator|.
name|setTabSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setQuotedCasing
argument_list|(
name|config
operator|.
name|quotedCasing
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setUnquotedCasing
argument_list|(
name|config
operator|.
name|unquotedCasing
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setIdentifierMaxLength
argument_list|(
name|config
operator|.
name|identifierMaxLength
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setConformance
argument_list|(
name|config
operator|.
name|conformance
argument_list|()
argument_list|)
expr_stmt|;
name|parser
operator|.
name|switchTo
argument_list|(
name|SqlParserUtil
operator|.
name|quotingToParserState
argument_list|(
name|config
operator|.
name|quoting
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Creates a<code>SqlParser</code> to parse the given string using    * Calcite's parser implementation.    *    *<p>The default lexical policy is similar to Oracle.    *    * @see Lex#ORACLE    *    * @param s An SQL statement or expression to parse.    * @return A parser    */
specifier|public
specifier|static
name|SqlParser
name|create
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|s
argument_list|,
name|config
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a<code>SqlParser</code> to parse the given string using the    * parser implementation created from given {@link SqlParserImplFactory}    * with given quoting syntax and casing policies for identifiers.    *    * @param sql A SQL statement or expression to parse    * @param config The parser configuration (identifier max length, etc.)    * @return A parser    */
specifier|public
specifier|static
name|SqlParser
name|create
parameter_list|(
name|String
name|sql
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
return|return
name|create
argument_list|(
operator|new
name|SourceStringReader
argument_list|(
name|sql
argument_list|)
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    * Creates a<code>SqlParser</code> to parse the given string using the    * parser implementation created from given {@link SqlParserImplFactory}    * with given quoting syntax and casing policies for identifiers.    *    *<p>Unlike    * {@link #create(java.lang.String, org.apache.calcite.sql.parser.SqlParser.Config)},    * the parser is not able to return the original query string, but will    * instead return "?".    *    * @param reader The source for the SQL statement or expression to parse    * @param config The parser configuration (identifier max length, etc.)    * @return A parser    */
specifier|public
specifier|static
name|SqlParser
name|create
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|SqlAbstractParserImpl
name|parser
init|=
name|config
operator|.
name|parserFactory
argument_list|()
operator|.
name|getParser
argument_list|(
name|reader
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlParser
argument_list|(
name|parser
argument_list|,
name|config
argument_list|)
return|;
block|}
comment|/**    * Parses a SQL expression.    *    * @throws SqlParseException if there is a parse error    */
specifier|public
name|SqlNode
name|parseExpression
parameter_list|()
throws|throws
name|SqlParseException
block|{
try|try
block|{
return|return
name|parser
operator|.
name|parseSqlExpressionEof
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|instanceof
name|CalciteContextException
condition|)
block|{
specifier|final
name|String
name|originalSql
init|=
name|parser
operator|.
name|getOriginalSql
argument_list|()
decl_stmt|;
if|if
condition|(
name|originalSql
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|CalciteContextException
operator|)
name|ex
operator|)
operator|.
name|setOriginalStatement
argument_list|(
name|originalSql
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
name|parser
operator|.
name|normalizeException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/** Normalizes a SQL exception. */
specifier|private
name|SqlParseException
name|handleException
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
if|if
condition|(
name|ex
operator|instanceof
name|CalciteContextException
condition|)
block|{
specifier|final
name|String
name|originalSql
init|=
name|parser
operator|.
name|getOriginalSql
argument_list|()
decl_stmt|;
if|if
condition|(
name|originalSql
operator|!=
literal|null
condition|)
block|{
operator|(
operator|(
name|CalciteContextException
operator|)
name|ex
operator|)
operator|.
name|setOriginalStatement
argument_list|(
name|originalSql
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|parser
operator|.
name|normalizeException
argument_list|(
name|ex
argument_list|)
return|;
block|}
comment|/**    * Parses a<code>SELECT</code> statement.    *    * @return A {@link org.apache.calcite.sql.SqlSelect} for a regular<code>    * SELECT</code> statement; a {@link org.apache.calcite.sql.SqlBinaryOperator}    * for a<code>UNION</code>,<code>INTERSECT</code>, or<code>EXCEPT</code>.    * @throws SqlParseException if there is a parse error    */
specifier|public
name|SqlNode
name|parseQuery
parameter_list|()
throws|throws
name|SqlParseException
block|{
try|try
block|{
return|return
name|parser
operator|.
name|parseSqlStmtEof
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
throw|throw
name|handleException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Parses a<code>SELECT</code> statement and reuses parser.    *    * @param sql sql to parse    * @return A {@link org.apache.calcite.sql.SqlSelect} for a regular<code>    * SELECT</code> statement; a {@link org.apache.calcite.sql.SqlBinaryOperator}    * for a<code>UNION</code>,<code>INTERSECT</code>, or<code>EXCEPT</code>.    * @throws SqlParseException if there is a parse error    */
specifier|public
name|SqlNode
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
block|{
name|parser
operator|.
name|ReInit
argument_list|(
operator|new
name|StringReader
argument_list|(
name|sql
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|parseQuery
argument_list|()
return|;
block|}
comment|/**    * Parses an SQL statement.    *    * @return top-level SqlNode representing stmt    * @throws SqlParseException if there is a parse error    */
specifier|public
name|SqlNode
name|parseStmt
parameter_list|()
throws|throws
name|SqlParseException
block|{
return|return
name|parseQuery
argument_list|()
return|;
block|}
comment|/**    * Parses a list of SQL statements separated by semicolon.    * The semicolon is required between statements, but is    * optional at the end.    *    * @return list of SqlNodeList representing the list of SQL statements    * @throws SqlParseException if there is a parse error    */
specifier|public
name|SqlNodeList
name|parseStmtList
parameter_list|()
throws|throws
name|SqlParseException
block|{
try|try
block|{
return|return
name|parser
operator|.
name|parseSqlStmtList
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
throw|throw
name|handleException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get the parser metadata.    *    * @return {@link SqlAbstractParserImpl.Metadata} implementation of    *     underlying parser.    */
specifier|public
name|SqlAbstractParserImpl
operator|.
name|Metadata
name|getMetadata
parameter_list|()
block|{
return|return
name|parser
operator|.
name|getMetadata
argument_list|()
return|;
block|}
comment|/**    * Returns the warnings that were generated by the previous invocation    * of the parser.    */
specifier|public
name|List
argument_list|<
name|CalciteContextException
argument_list|>
name|getWarnings
parameter_list|()
block|{
return|return
name|parser
operator|.
name|warnings
return|;
block|}
comment|/** Returns a default {@link Config}. */
specifier|public
specifier|static
name|Config
name|config
parameter_list|()
block|{
return|return
name|Config
operator|.
name|DEFAULT
return|;
block|}
comment|/**    * Builder for a {@link Config}.    *    * @deprecated Use {@link #config()}    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|ConfigBuilder
name|configBuilder
parameter_list|()
block|{
return|return
operator|new
name|ConfigBuilder
argument_list|()
return|;
block|}
comment|/**    * Builder for a {@link Config} that starts with an existing {@code Config}.    *    * @deprecated Use {@code config}, and modify it using its mutator methods    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
name|ConfigBuilder
name|configBuilder
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
return|return
operator|new
name|ConfigBuilder
argument_list|()
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
return|;
block|}
comment|/**    * Interface to define the configuration for a SQL parser.    */
specifier|public
interface|interface
name|Config
block|{
comment|/** Default configuration. */
name|Config
name|DEFAULT
init|=
name|ImmutableBeans
operator|.
name|create
argument_list|(
name|Config
operator|.
name|class
argument_list|)
operator|.
name|withQuotedCasing
argument_list|(
name|Lex
operator|.
name|ORACLE
operator|.
name|quotedCasing
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Lex
operator|.
name|ORACLE
operator|.
name|unquotedCasing
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|Lex
operator|.
name|ORACLE
operator|.
name|quoting
argument_list|)
operator|.
name|withIdentifierMaxLength
argument_list|(
name|DEFAULT_IDENTIFIER_MAX_LENGTH
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
name|Lex
operator|.
name|ORACLE
operator|.
name|caseSensitive
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
operator|.
name|withParserFactory
argument_list|(
name|SqlParserImpl
operator|.
name|FACTORY
argument_list|)
decl_stmt|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|()
name|int
name|identifierMaxLength
parameter_list|()
function_decl|;
comment|/** Sets {@link #identifierMaxLength()}. */
name|Config
name|withIdentifierMaxLength
parameter_list|(
name|int
name|identifierMaxLength
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|(
name|required
operator|=
literal|true
argument_list|)
name|Casing
name|quotedCasing
parameter_list|()
function_decl|;
comment|/** Sets {@link #quotedCasing()}. */
name|Config
name|withQuotedCasing
parameter_list|(
name|Casing
name|casing
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|(
name|required
operator|=
literal|true
argument_list|)
name|Casing
name|unquotedCasing
parameter_list|()
function_decl|;
comment|/** Sets {@link #unquotedCasing()}. */
name|Config
name|withUnquotedCasing
parameter_list|(
name|Casing
name|casing
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|(
name|required
operator|=
literal|true
argument_list|)
name|Quoting
name|quoting
parameter_list|()
function_decl|;
comment|/** Sets {@link #quoting()}. */
name|Config
name|withQuoting
parameter_list|(
name|Quoting
name|quoting
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|()
name|boolean
name|caseSensitive
parameter_list|()
function_decl|;
comment|/** Sets {@link #caseSensitive()}. */
name|Config
name|withCaseSensitive
parameter_list|(
name|boolean
name|caseSensitive
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|(
name|required
operator|=
literal|true
argument_list|)
name|SqlConformance
name|conformance
parameter_list|()
function_decl|;
comment|/** Sets {@link #conformance()}. */
name|Config
name|withConformance
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
function_decl|;
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|boolean
name|allowBangEqual
parameter_list|()
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|(
name|required
operator|=
literal|true
argument_list|)
name|SqlParserImplFactory
name|parserFactory
parameter_list|()
function_decl|;
comment|/** Sets {@link #parserFactory()}. */
name|Config
name|withParserFactory
parameter_list|(
name|SqlParserImplFactory
name|factory
parameter_list|)
function_decl|;
specifier|default
name|Config
name|withLex
parameter_list|(
name|Lex
name|lex
parameter_list|)
block|{
return|return
name|withCaseSensitive
argument_list|(
name|lex
operator|.
name|caseSensitive
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|lex
operator|.
name|unquotedCasing
argument_list|)
operator|.
name|withQuotedCasing
argument_list|(
name|lex
operator|.
name|quotedCasing
argument_list|)
operator|.
name|withQuoting
argument_list|(
name|lex
operator|.
name|quoting
argument_list|)
return|;
block|}
block|}
comment|/** Builder for a {@link Config}. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
class|class
name|ConfigBuilder
block|{
specifier|private
name|Config
name|config
init|=
name|Config
operator|.
name|DEFAULT
decl_stmt|;
specifier|private
name|ConfigBuilder
parameter_list|()
block|{
block|}
comment|/** Sets configuration identical to a given {@link Config}. */
specifier|public
name|ConfigBuilder
name|setConfig
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|setQuotedCasing
parameter_list|(
name|Casing
name|quotedCasing
parameter_list|)
block|{
return|return
name|setConfig
argument_list|(
name|config
operator|.
name|withQuotedCasing
argument_list|(
name|quotedCasing
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|setUnquotedCasing
parameter_list|(
name|Casing
name|unquotedCasing
parameter_list|)
block|{
return|return
name|setConfig
argument_list|(
name|config
operator|.
name|withUnquotedCasing
argument_list|(
name|unquotedCasing
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|setQuoting
parameter_list|(
name|Quoting
name|quoting
parameter_list|)
block|{
return|return
name|setConfig
argument_list|(
name|config
operator|.
name|withQuoting
argument_list|(
name|quoting
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|setCaseSensitive
parameter_list|(
name|boolean
name|caseSensitive
parameter_list|)
block|{
return|return
name|setConfig
argument_list|(
name|config
operator|.
name|withCaseSensitive
argument_list|(
name|caseSensitive
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|setIdentifierMaxLength
parameter_list|(
name|int
name|identifierMaxLength
parameter_list|)
block|{
return|return
name|setConfig
argument_list|(
name|config
operator|.
name|withIdentifierMaxLength
argument_list|(
name|identifierMaxLength
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|ConfigBuilder
name|setAllowBangEqual
parameter_list|(
specifier|final
name|boolean
name|allowBangEqual
parameter_list|)
block|{
if|if
condition|(
name|allowBangEqual
operator|!=
name|config
operator|.
name|conformance
argument_list|()
operator|.
name|isBangEqualAllowed
argument_list|()
condition|)
block|{
return|return
name|setConformance
argument_list|(
operator|new
name|SqlDelegatingConformance
argument_list|(
name|config
operator|.
name|conformance
argument_list|()
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isBangEqualAllowed
parameter_list|()
block|{
return|return
name|allowBangEqual
return|;
block|}
block|}
argument_list|)
return|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|ConfigBuilder
name|setConformance
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
return|return
name|setConfig
argument_list|(
name|config
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|setParserFactory
parameter_list|(
name|SqlParserImplFactory
name|factory
parameter_list|)
block|{
return|return
name|setConfig
argument_list|(
name|config
operator|.
name|withParserFactory
argument_list|(
name|factory
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ConfigBuilder
name|setLex
parameter_list|(
name|Lex
name|lex
parameter_list|)
block|{
return|return
name|setConfig
argument_list|(
name|config
operator|.
name|withLex
argument_list|(
name|lex
argument_list|)
argument_list|)
return|;
block|}
comment|/** Builds a {@link Config}. */
specifier|public
name|Config
name|build
parameter_list|()
block|{
return|return
name|config
return|;
block|}
block|}
block|}
end_class

end_unit

