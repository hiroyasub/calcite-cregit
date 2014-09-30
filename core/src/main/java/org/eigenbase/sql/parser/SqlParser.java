begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
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
name|sql
operator|.
name|parser
operator|.
name|impl
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

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|Casing
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|Quoting
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
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlAbstractParserImpl
name|parser
decl_stmt|;
specifier|private
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
name|Quoting
name|quoting
parameter_list|,
name|Casing
name|unquotedCasing
parameter_list|,
name|Casing
name|quotedCasing
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
name|quotedCasing
argument_list|)
expr_stmt|;
name|parser
operator|.
name|setUnquotedCasing
argument_list|(
name|unquotedCasing
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|quoting
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
comment|/**    * Creates a<code>SqlParser</code> to parse the given string using    * Calcite's parser implementation.    *    * @param s An SQL statement or expression to parse.    * @return A<code>SqlParser</code> object.    */
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
name|SqlParserImpl
operator|.
name|FACTORY
argument_list|,
name|s
argument_list|,
name|Quoting
operator|.
name|DOUBLE_QUOTE
argument_list|,
name|Casing
operator|.
name|TO_UPPER
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|)
return|;
block|}
comment|/**    * Creates a<code>SqlParser</code> to parse the given string using the    * parser implementation created from given {@link SqlParserImplFactory}    * with given quoting syntax and casing policies for identifiers.    *    * @param parserFactory {@link SqlParserImplFactory} to get the parser    *     implementation.    * @param s An SQL statement or expression to parse.    * @param quoting Syntax for quoting identifiers in SQL statements.    * @param unquotedCasing Policy for converting case of<i>unquoted</i>    *     identifiers.    * @param quotedCasing Policy for converting case of<i>quoted</i>    *     identifiers.    * @return A<code>SqlParser</code> object.    */
specifier|public
specifier|static
name|SqlParser
name|create
parameter_list|(
name|SqlParserImplFactory
name|parserFactory
parameter_list|,
name|String
name|s
parameter_list|,
name|Quoting
name|quoting
parameter_list|,
name|Casing
name|unquotedCasing
parameter_list|,
name|Casing
name|quotedCasing
parameter_list|)
block|{
name|SqlAbstractParserImpl
name|parser
init|=
name|parserFactory
operator|.
name|getParser
argument_list|(
operator|new
name|StringReader
argument_list|(
name|s
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlParser
argument_list|(
name|s
argument_list|,
name|parser
argument_list|,
name|quoting
argument_list|,
name|unquotedCasing
argument_list|,
name|quotedCasing
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
name|EigenbaseContextException
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
name|EigenbaseContextException
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
comment|/**    * Parses a<code>SELECT</code> statement.    *    * @return A {@link org.eigenbase.sql.SqlSelect} for a regular<code>    * SELECT</code> statement; a {@link org.eigenbase.sql.SqlBinaryOperator}    * for a<code>UNION</code>,<code>INTERSECT</code>, or<code>EXCEPT</code>.    * @throws SqlParseException if there is a parse error    */
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
name|EigenbaseContextException
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
name|EigenbaseContextException
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
name|EigenbaseContextException
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
name|EigenbaseContextException
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
block|}
end_class

begin_comment
comment|// End SqlParser.java
end_comment

end_unit

