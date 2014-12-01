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
name|parser
operator|.
name|impl
operator|.
name|SqlParserImpl
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
name|base
operator|.
name|Preconditions
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
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlAbstractParserImpl
name|parser
decl_stmt|;
specifier|private
specifier|final
name|String
name|originalInput
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|SqlParser
parameter_list|(
name|String
name|s
parameter_list|,
name|SqlAbstractParserImpl
name|parser
parameter_list|,
name|Config
name|config
parameter_list|)
block|{
name|this
operator|.
name|originalInput
operator|=
name|s
expr_stmt|;
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
switch|switch
condition|(
name|config
operator|.
name|quoting
argument_list|()
condition|)
block|{
case|case
name|DOUBLE_QUOTE
case|:
name|parser
operator|.
name|switchTo
argument_list|(
literal|"DQID"
argument_list|)
expr_stmt|;
break|break;
case|case
name|BACK_TICK
case|:
name|parser
operator|.
name|switchTo
argument_list|(
literal|"BTID"
argument_list|)
expr_stmt|;
break|break;
case|case
name|BRACKET
case|:
name|parser
operator|.
name|switchTo
argument_list|(
literal|"DEFAULT"
argument_list|)
expr_stmt|;
break|break;
block|}
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
name|configBuilder
argument_list|()
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a<code>SqlParser</code> to parse the given string using the    * parser implementation created from given {@link SqlParserImplFactory}    * with given quoting syntax and casing policies for identifiers.    *    * @param sql A SQL statement or expression to parse.    * @param config The parser configuration (identifier max length, etc.)    * @return A parser    */
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
operator|new
name|StringReader
argument_list|(
name|sql
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlParser
argument_list|(
name|sql
argument_list|,
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
operator|(
name|ex
operator|instanceof
name|CalciteContextException
operator|)
operator|&&
operator|(
name|originalInput
operator|!=
literal|null
operator|)
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
name|originalInput
argument_list|)
expr_stmt|;
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
if|if
condition|(
operator|(
name|ex
operator|instanceof
name|CalciteContextException
operator|)
operator|&&
operator|(
name|originalInput
operator|!=
literal|null
operator|)
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
name|originalInput
argument_list|)
expr_stmt|;
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
comment|/**    * Parses an SQL statement.    *    * @return top-level SqlNode representing stmt    * @throws SqlParseException if there is a parse error    */
specifier|public
name|SqlNode
name|parseStmt
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
if|if
condition|(
operator|(
name|ex
operator|instanceof
name|CalciteContextException
operator|)
operator|&&
operator|(
name|originalInput
operator|!=
literal|null
operator|)
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
name|originalInput
argument_list|)
expr_stmt|;
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
comment|/**    * Builder for a {@link Config}.    */
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
comment|/**    * Builder for a {@link Config} that starts with an existing {@code Config}.    */
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
comment|/**    * Interface to define the configuration for a SQL parser.    *    * @see ConfigBuilder    */
specifier|public
interface|interface
name|Config
block|{
comment|/** Default configuration. */
name|Config
name|DEFAULT
init|=
name|configBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
name|int
name|identifierMaxLength
parameter_list|()
function_decl|;
name|Casing
name|quotedCasing
parameter_list|()
function_decl|;
name|Casing
name|unquotedCasing
parameter_list|()
function_decl|;
name|Quoting
name|quoting
parameter_list|()
function_decl|;
name|boolean
name|caseSensitive
parameter_list|()
function_decl|;
name|SqlParserImplFactory
name|parserFactory
parameter_list|()
function_decl|;
block|}
comment|/** Builder for a {@link Config}. */
specifier|public
specifier|static
class|class
name|ConfigBuilder
block|{
specifier|private
name|Casing
name|quotedCasing
init|=
name|Lex
operator|.
name|ORACLE
operator|.
name|quotedCasing
decl_stmt|;
specifier|private
name|Casing
name|unquotedCasing
init|=
name|Lex
operator|.
name|ORACLE
operator|.
name|unquotedCasing
decl_stmt|;
specifier|private
name|Quoting
name|quoting
init|=
name|Lex
operator|.
name|ORACLE
operator|.
name|quoting
decl_stmt|;
specifier|private
name|int
name|identifierMaxLength
init|=
name|DEFAULT_IDENTIFIER_MAX_LENGTH
decl_stmt|;
specifier|private
name|boolean
name|caseSensitive
init|=
name|Lex
operator|.
name|ORACLE
operator|.
name|caseSensitive
decl_stmt|;
specifier|private
name|SqlParserImplFactory
name|parserFactory
init|=
name|SqlParserImpl
operator|.
name|FACTORY
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
name|quotedCasing
operator|=
name|config
operator|.
name|quotedCasing
argument_list|()
expr_stmt|;
name|this
operator|.
name|unquotedCasing
operator|=
name|config
operator|.
name|unquotedCasing
argument_list|()
expr_stmt|;
name|this
operator|.
name|quoting
operator|=
name|config
operator|.
name|quoting
argument_list|()
expr_stmt|;
name|this
operator|.
name|identifierMaxLength
operator|=
name|config
operator|.
name|identifierMaxLength
argument_list|()
expr_stmt|;
name|this
operator|.
name|parserFactory
operator|=
name|config
operator|.
name|parserFactory
argument_list|()
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
name|this
operator|.
name|quotedCasing
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|quotedCasing
argument_list|)
expr_stmt|;
return|return
name|this
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
name|this
operator|.
name|unquotedCasing
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|unquotedCasing
argument_list|)
expr_stmt|;
return|return
name|this
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
name|this
operator|.
name|quoting
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|quoting
argument_list|)
expr_stmt|;
return|return
name|this
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
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
return|return
name|this
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
name|this
operator|.
name|identifierMaxLength
operator|=
name|identifierMaxLength
expr_stmt|;
return|return
name|this
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
name|this
operator|.
name|parserFactory
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|factory
argument_list|)
expr_stmt|;
return|return
name|this
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
name|setCaseSensitive
argument_list|(
name|lex
operator|.
name|caseSensitive
argument_list|)
expr_stmt|;
name|setUnquotedCasing
argument_list|(
name|lex
operator|.
name|unquotedCasing
argument_list|)
expr_stmt|;
name|setQuotedCasing
argument_list|(
name|lex
operator|.
name|quotedCasing
argument_list|)
expr_stmt|;
name|setQuoting
argument_list|(
name|lex
operator|.
name|quoting
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Builds a      * {@link Config}. */
specifier|public
name|Config
name|build
parameter_list|()
block|{
return|return
operator|new
name|ConfigImpl
argument_list|(
name|identifierMaxLength
argument_list|,
name|quotedCasing
argument_list|,
name|unquotedCasing
argument_list|,
name|quoting
argument_list|,
name|caseSensitive
argument_list|,
name|parserFactory
argument_list|)
return|;
block|}
block|}
comment|/** Implementation of    * {@link Config}.    * Called by builder; all values are in private final fields. */
specifier|private
specifier|static
class|class
name|ConfigImpl
implements|implements
name|Config
block|{
specifier|private
specifier|final
name|int
name|identifierMaxLength
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|caseSensitive
decl_stmt|;
specifier|private
specifier|final
name|Casing
name|quotedCasing
decl_stmt|;
specifier|private
specifier|final
name|Casing
name|unquotedCasing
decl_stmt|;
specifier|private
specifier|final
name|Quoting
name|quoting
decl_stmt|;
specifier|private
specifier|final
name|SqlParserImplFactory
name|parserFactory
decl_stmt|;
specifier|private
name|ConfigImpl
parameter_list|(
name|int
name|identifierMaxLength
parameter_list|,
name|Casing
name|quotedCasing
parameter_list|,
name|Casing
name|unquotedCasing
parameter_list|,
name|Quoting
name|quoting
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|SqlParserImplFactory
name|parserFactory
parameter_list|)
block|{
name|this
operator|.
name|identifierMaxLength
operator|=
name|identifierMaxLength
expr_stmt|;
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
name|this
operator|.
name|quotedCasing
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|quotedCasing
argument_list|)
expr_stmt|;
name|this
operator|.
name|unquotedCasing
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|unquotedCasing
argument_list|)
expr_stmt|;
name|this
operator|.
name|quoting
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|quoting
argument_list|)
expr_stmt|;
name|this
operator|.
name|parserFactory
operator|=
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|parserFactory
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|identifierMaxLength
parameter_list|()
block|{
return|return
name|identifierMaxLength
return|;
block|}
specifier|public
name|Casing
name|quotedCasing
parameter_list|()
block|{
return|return
name|quotedCasing
return|;
block|}
specifier|public
name|Casing
name|unquotedCasing
parameter_list|()
block|{
return|return
name|unquotedCasing
return|;
block|}
specifier|public
name|Quoting
name|quoting
parameter_list|()
block|{
return|return
name|quoting
return|;
block|}
specifier|public
name|boolean
name|caseSensitive
parameter_list|()
block|{
return|return
name|caseSensitive
return|;
block|}
specifier|public
name|SqlParserImplFactory
name|parserFactory
parameter_list|()
block|{
return|return
name|parserFactory
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlParser.java
end_comment

end_unit

