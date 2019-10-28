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
name|test
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
name|SqlWriter
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
name|SqlWriterConfig
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
name|dialect
operator|.
name|AnsiSqlDialect
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
name|SqlParseException
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
name|SqlParser
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
name|pretty
operator|.
name|SqlPrettyWriter
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
name|test
operator|.
name|DiffRepository
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
name|Litmus
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Disabled
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|UnaryOperator
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertTrue
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link SqlPrettyWriter}.  *  *<p>You must provide the system property "source.dir".  */
end_comment

begin_class
specifier|public
class|class
name|SqlPrettyWriterTest
block|{
specifier|protected
name|DiffRepository
name|getDiffRepos
parameter_list|()
block|{
return|return
name|DiffRepository
operator|.
name|lookup
argument_list|(
name|SqlPrettyWriterTest
operator|.
name|class
argument_list|)
return|;
block|}
comment|/**    * Parses a SQL query. To use a different parser, override this method.    */
specifier|protected
name|SqlNode
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|SqlNode
name|node
decl_stmt|;
try|try
block|{
name|node
operator|=
name|SqlParser
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|parseQuery
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SqlParseException
name|e
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Received error while parsing SQL '"
operator|+
name|sql
operator|+
literal|"'"
operator|+
literal|"; error is:\n"
operator|+
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
name|message
argument_list|)
throw|;
block|}
return|return
name|node
return|;
block|}
comment|/** Helper. */
class|class
name|Sql
block|{
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|expr
decl_stmt|;
specifier|private
specifier|final
name|String
name|desc
decl_stmt|;
specifier|private
specifier|final
name|String
name|formatted
decl_stmt|;
specifier|private
specifier|final
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|transform
decl_stmt|;
name|Sql
parameter_list|(
name|String
name|sql
parameter_list|,
name|boolean
name|expr
parameter_list|,
name|String
name|desc
parameter_list|,
name|String
name|formatted
parameter_list|,
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|transform
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|sql
argument_list|)
expr_stmt|;
name|this
operator|.
name|expr
operator|=
name|expr
expr_stmt|;
name|this
operator|.
name|desc
operator|=
name|desc
expr_stmt|;
name|this
operator|.
name|formatted
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|formatted
argument_list|)
expr_stmt|;
name|this
operator|.
name|transform
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|transform
argument_list|)
expr_stmt|;
block|}
name|Sql
name|withWriter
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlWriterConfig
argument_list|>
name|transform
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|transform
argument_list|)
expr_stmt|;
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expr
argument_list|,
name|desc
argument_list|,
name|formatted
argument_list|,
name|w
lambda|->
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|transform
operator|.
name|apply
argument_list|(
name|w
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
name|Sql
name|expectingDesc
parameter_list|(
name|String
name|desc
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|desc
argument_list|,
name|desc
argument_list|)
condition|?
name|this
else|:
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expr
argument_list|,
name|desc
argument_list|,
name|formatted
argument_list|,
name|transform
argument_list|)
return|;
block|}
name|Sql
name|withExpr
parameter_list|(
name|boolean
name|expr
parameter_list|)
block|{
return|return
name|this
operator|.
name|expr
operator|==
name|expr
condition|?
name|this
else|:
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expr
argument_list|,
name|desc
argument_list|,
name|formatted
argument_list|,
name|transform
argument_list|)
return|;
block|}
name|Sql
name|expectingFormatted
parameter_list|(
name|String
name|formatted
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|formatted
argument_list|,
name|formatted
argument_list|)
condition|?
name|this
else|:
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|expr
argument_list|,
name|desc
argument_list|,
name|formatted
argument_list|,
name|transform
argument_list|)
return|;
block|}
name|Sql
name|check
parameter_list|()
block|{
specifier|final
name|SqlWriterConfig
name|config
init|=
name|transform
operator|.
name|apply
argument_list|(
name|SqlPrettyWriter
operator|.
name|config
argument_list|()
operator|.
name|withDialect
argument_list|(
name|AnsiSqlDialect
operator|.
name|DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|config
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|node
decl_stmt|;
if|if
condition|(
name|expr
condition|)
block|{
specifier|final
name|SqlCall
name|valuesCall
init|=
operator|(
name|SqlCall
operator|)
name|parseQuery
argument_list|(
literal|"VALUES ("
operator|+
name|sql
operator|+
literal|")"
argument_list|)
decl_stmt|;
specifier|final
name|SqlCall
name|rowCall
init|=
name|valuesCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|node
operator|=
name|rowCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node
operator|=
name|parseQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
comment|// Describe settings
if|if
condition|(
name|desc
operator|!=
literal|null
condition|)
block|{
specifier|final
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|final
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|describe
argument_list|(
name|pw
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|pw
operator|.
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|String
name|desc
init|=
name|sw
operator|.
name|toString
argument_list|()
decl_stmt|;
name|getDiffRepos
argument_list|()
operator|.
name|assertEquals
argument_list|(
literal|"desc"
argument_list|,
name|this
operator|.
name|desc
argument_list|,
name|desc
argument_list|)
expr_stmt|;
block|}
comment|// Format
specifier|final
name|String
name|formatted
init|=
name|prettyWriter
operator|.
name|format
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|getDiffRepos
argument_list|()
operator|.
name|assertEquals
argument_list|(
literal|"formatted"
argument_list|,
name|this
operator|.
name|formatted
argument_list|,
name|formatted
argument_list|)
expr_stmt|;
comment|// Now parse the result, and make sure it is structurally equivalent
comment|// to the original.
specifier|final
name|String
name|actual2
init|=
name|formatted
operator|.
name|replaceAll
argument_list|(
literal|"`"
argument_list|,
literal|"\""
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|node2
decl_stmt|;
if|if
condition|(
name|expr
condition|)
block|{
specifier|final
name|SqlCall
name|valuesCall
init|=
operator|(
name|SqlCall
operator|)
name|parseQuery
argument_list|(
literal|"VALUES ("
operator|+
name|actual2
operator|+
literal|")"
argument_list|)
decl_stmt|;
specifier|final
name|SqlCall
name|rowCall
init|=
name|valuesCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|node2
operator|=
name|rowCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|node2
operator|=
name|parseQuery
argument_list|(
name|actual2
argument_list|)
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|node
operator|.
name|equalsDeep
argument_list|(
name|node2
argument_list|,
name|Litmus
operator|.
name|THROW
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
specifier|private
name|Sql
name|simple
parameter_list|()
block|{
return|return
name|sql
argument_list|(
literal|"select x as a, b as b, c as c, d,"
operator|+
literal|" 'mixed-Case string',"
operator|+
literal|" unquotedCamelCaseId,"
operator|+
literal|" \"quoted id\" "
operator|+
literal|"from"
operator|+
literal|" (select *"
operator|+
literal|" from t"
operator|+
literal|" where x = y and a> 5"
operator|+
literal|" group by z, zz"
operator|+
literal|" window w as (partition by c),"
operator|+
literal|"  w1 as (partition by c,d order by a, b"
operator|+
literal|"   range between interval '2:2' hour to minute preceding"
operator|+
literal|"    and interval '1' day following)) "
operator|+
literal|"order by gg"
argument_list|)
return|;
block|}
specifier|private
name|Sql
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|"${formatted}"
argument_list|,
name|w
lambda|->
name|w
argument_list|)
return|;
block|}
specifier|private
name|Sql
name|expr
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withExpr
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|// ~ Tests ----------------------------------------------------------------
annotation|@
name|Test
specifier|public
name|void
name|testDefault
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIndent8
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|expectingDesc
argument_list|(
literal|"${desc}"
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withIndentation
argument_list|(
literal|8
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClausesNotOnNewLine
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withClauseStartsLine
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectListItemsOnSeparateLines
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectListNoExtraIndentFlag
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
operator|.
name|withSelectListExtraIndentFlag
argument_list|(
literal|false
argument_list|)
operator|.
name|withClauseEndsLine
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFold
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|FOLD
argument_list|)
operator|.
name|withFoldLength
argument_list|(
literal|45
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChop
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|CHOP
argument_list|)
operator|.
name|withFoldLength
argument_list|(
literal|45
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testChopLeadingComma
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|CHOP
argument_list|)
operator|.
name|withFoldLength
argument_list|(
literal|45
argument_list|)
operator|.
name|withLeadingComma
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLeadingComma
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withLeadingComma
argument_list|(
literal|true
argument_list|)
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
operator|.
name|withSelectListExtraIndentFlag
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClauseEndsLine
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withClauseEndsLine
argument_list|(
literal|true
argument_list|)
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|WIDE
argument_list|)
operator|.
name|withFoldLength
argument_list|(
literal|45
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClauseEndsLineTall
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withClauseEndsLine
argument_list|(
literal|true
argument_list|)
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withFoldLength
argument_list|(
literal|45
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClauseEndsLineFold
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withClauseEndsLine
argument_list|(
literal|true
argument_list|)
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|FOLD
argument_list|)
operator|.
name|withFoldLength
argument_list|(
literal|45
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** Tests formatting a query with Looker's preferences. */
annotation|@
name|Test
specifier|public
name|void
name|testLooker
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withFoldLength
argument_list|(
literal|60
argument_list|)
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|STEP
argument_list|)
operator|.
name|withSelectFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withFromFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withWhereFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withHavingFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withClauseEndsLine
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testKeywordsLowerCase
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withKeywordsLowerCase
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParenthesizeAllExprs
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withAlwaysUseParentheses
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnlyQuoteIdentifiersWhichNeedIt
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withQuoteAllIdentifiers
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBlackSubQueryStyle
parameter_list|()
block|{
comment|// Note that ( is at the indent, SELECT is on the same line, and ) is
comment|// below it.
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withSubQueryStyle
argument_list|(
name|SqlWriter
operator|.
name|SubQueryStyle
operator|.
name|BLACK
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBlackSubQueryStyleIndent0
parameter_list|()
block|{
name|simple
argument_list|()
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withSubQueryStyle
argument_list|(
name|SqlWriter
operator|.
name|SubQueryStyle
operator|.
name|BLACK
argument_list|)
operator|.
name|withIndentation
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValuesNewline
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from (values (1, 2), (3, 4)) as t"
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withValuesListNewline
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValuesLeadingCommas
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from (values (1, 2), (3, 4)) as t"
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withValuesListNewline
argument_list|(
literal|true
argument_list|)
operator|.
name|withLeadingComma
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Disabled
argument_list|(
literal|"default SQL parser cannot parse DDL"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testExplain
parameter_list|()
block|{
name|sql
argument_list|(
literal|"explain select * from t"
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCase
parameter_list|()
block|{
comment|// Note that CASE is rewritten to the searched form. Wish it weren't
comment|// so, but that's beyond the control of the pretty-printer.
comment|// todo: indent should be 4 not 8
specifier|final
name|String
name|sql
init|=
literal|"case 1\n"
operator|+
literal|" when 2 + 3 then 4\n"
operator|+
literal|" when case a when b then c else d end then 6\n"
operator|+
literal|" else 7\n"
operator|+
literal|"end"
decl_stmt|;
specifier|final
name|String
name|formatted
init|=
literal|"CASE\n"
operator|+
literal|"WHEN 1 = 2 + 3\n"
operator|+
literal|"THEN 4\n"
operator|+
literal|"WHEN 1 = CASE\n"
operator|+
literal|"        WHEN `A` = `B`\n"
comment|// todo: indent should be 4 not 8
operator|+
literal|"        THEN `C`\n"
operator|+
literal|"        ELSE `D`\n"
operator|+
literal|"        END\n"
operator|+
literal|"THEN 6\n"
operator|+
literal|"ELSE 7\n"
operator|+
literal|"END"
decl_stmt|;
name|expr
argument_list|(
name|sql
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withCaseClausesOnNewLines
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|expectingFormatted
argument_list|(
name|formatted
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCase2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"case 1"
operator|+
literal|" when 2 + 3 then 4"
operator|+
literal|" when case a when b then c else d end then 6"
operator|+
literal|" else 7 end"
decl_stmt|;
specifier|final
name|String
name|formatted
init|=
literal|"CASE WHEN 1 = 2 + 3 THEN 4"
operator|+
literal|" WHEN 1 = CASE WHEN `A` = `B` THEN `C` ELSE `D` END THEN 6"
operator|+
literal|" ELSE 7 END"
decl_stmt|;
name|expr
argument_list|(
name|sql
argument_list|)
operator|.
name|expectingFormatted
argument_list|(
name|formatted
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBetween
parameter_list|()
block|{
comment|// todo: remove leading
name|expr
argument_list|(
literal|"x not between symmetric y and z"
argument_list|)
operator|.
name|expectingFormatted
argument_list|(
literal|"`X` NOT BETWEEN SYMMETRIC `Y` AND `Z`"
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
comment|// space
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCast
parameter_list|()
block|{
name|expr
argument_list|(
literal|"cast(x + y as decimal(5, 10))"
argument_list|)
operator|.
name|expectingFormatted
argument_list|(
literal|"CAST(`X` + `Y` AS DECIMAL(5, 10))"
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLiteralChain
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"'x' /* comment */ 'y'\n"
operator|+
literal|"  'z' "
decl_stmt|;
specifier|final
name|String
name|formatted
init|=
literal|"'x'\n"
operator|+
literal|"'y'\n"
operator|+
literal|"'z'"
decl_stmt|;
name|expr
argument_list|(
name|sql
argument_list|)
operator|.
name|expectingFormatted
argument_list|(
name|formatted
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverlaps
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"(x,xx) overlaps (y,yy) or x is not null"
decl_stmt|;
specifier|final
name|String
name|formatted
init|=
literal|"PERIOD (`X`, `XX`) OVERLAPS PERIOD (`Y`, `YY`)"
operator|+
literal|" OR `X` IS NOT NULL"
decl_stmt|;
name|expr
argument_list|(
name|sql
argument_list|)
operator|.
name|expectingFormatted
argument_list|(
name|formatted
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from t "
operator|+
literal|"union select * from ("
operator|+
literal|"  select * from u "
operator|+
literal|"  union select * from v) "
operator|+
literal|"union select * from w "
operator|+
literal|"order by a, b"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiset
parameter_list|()
block|{
name|sql
argument_list|(
literal|"values (multiset (select * from t))"
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinComma
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from x, y as y1, z, (select * from a, a2 as a3),\n"
operator|+
literal|" (select * from b) as b2\n"
operator|+
literal|"where p = q\n"
operator|+
literal|"and exists (select 1 from v, w)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInnerJoin
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from x inner join y on x.k=y.k"
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinTall
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from x inner join y on x.k=y.k left join z using (a)"
argument_list|)
operator|.
name|withWriter
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinTallClauseEndsLine
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from x inner join y on x.k=y.k left join z using (a)"
argument_list|)
operator|.
name|withWriter
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withClauseEndsLine
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJoinLateralSubQueryTall
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select *\n"
operator|+
literal|"from (select a from customers where b< c group by d) as c,\n"
operator|+
literal|" products,\n"
operator|+
literal|" lateral (select e from orders where exists (\n"
operator|+
literal|"    select 1 from promotions)) as t5\n"
operator|+
literal|"group by f"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withWriter
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereListItemsOnSeparateLinesOr
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x"
operator|+
literal|" from y"
operator|+
literal|" where h is not null and i< j"
operator|+
literal|" or ((a or b) is true) and d not in (f,g)"
operator|+
literal|" or x<> z"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
operator|.
name|withSelectListExtraIndentFlag
argument_list|(
literal|false
argument_list|)
operator|.
name|withWhereListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereListItemsOnSeparateLinesAnd
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x"
operator|+
literal|" from y"
operator|+
literal|" where h is not null and (i< j"
operator|+
literal|" or ((a or b) is true)) and (d not in (f,g)"
operator|+
literal|" or v<> ((w * x) + y) * z)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
operator|.
name|withSelectListExtraIndentFlag
argument_list|(
literal|false
argument_list|)
operator|.
name|withWhereListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
comment|/** As {@link #testWhereListItemsOnSeparateLinesAnd()}, but    * with {@link SqlWriterConfig#clauseEndsLine ClauseEndsLine=true}. */
annotation|@
name|Test
specifier|public
name|void
name|testWhereListItemsOnSeparateLinesAndNewline
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x"
operator|+
literal|" from y"
operator|+
literal|" where h is not null and (i< j"
operator|+
literal|" or ((a or b) is true)) and (d not in (f,g)"
operator|+
literal|" or v<> ((w * x) + y) * z)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
operator|.
name|withSelectListExtraIndentFlag
argument_list|(
literal|false
argument_list|)
operator|.
name|withWhereListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
operator|.
name|withClauseEndsLine
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp\n"
operator|+
literal|"set mgr = mgr + 1, deptno = 5\n"
operator|+
literal|"where deptno = 10 and name = 'Fred'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateNoLine
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp\n"
operator|+
literal|"set mgr = mgr + 1, deptno = 5\n"
operator|+
literal|"where deptno = 10 and name = 'Fred'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withUpdateSetListNewline
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUpdateNoLine2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"update emp\n"
operator|+
literal|"set mgr = mgr + 1, deptno = 5\n"
operator|+
literal|"where deptno = 10 and name = 'Fred'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withWriter
argument_list|(
name|w
lambda|->
name|w
operator|.
name|withUpdateSetListNewline
argument_list|(
literal|false
argument_list|)
operator|.
name|withClauseStartsLine
argument_list|(
literal|false
argument_list|)
argument_list|)
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|SqlParseException
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x as a, b as b, c as c, d,"
operator|+
literal|" 'mixed-Case string',"
operator|+
literal|" unquotedCamelCaseId,"
operator|+
literal|" \"quoted id\" "
operator|+
literal|"from"
operator|+
literal|" (select *"
operator|+
literal|" from t"
operator|+
literal|" where x = y and a> 5"
operator|+
literal|" group by z, zz"
operator|+
literal|" window w as (partition by c),"
operator|+
literal|"  w1 as (partition by c,d order by a, b"
operator|+
literal|"   range between interval '2:2' hour to minute preceding"
operator|+
literal|"    and interval '1' day following)) "
operator|+
literal|"order by gg desc nulls last, hh asc"
decl_stmt|;
specifier|final
name|SqlNode
name|node
init|=
name|SqlParser
operator|.
name|create
argument_list|(
name|sql
argument_list|)
operator|.
name|parseQuery
argument_list|()
decl_stmt|;
specifier|final
name|SqlWriterConfig
name|config
init|=
name|SqlPrettyWriter
operator|.
name|config
argument_list|()
operator|.
name|withLineFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|STEP
argument_list|)
operator|.
name|withSelectFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withFromFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withWhereFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withHavingFolding
argument_list|(
name|SqlWriterConfig
operator|.
name|LineFolding
operator|.
name|TALL
argument_list|)
operator|.
name|withIndentation
argument_list|(
literal|4
argument_list|)
operator|.
name|withClauseEndsLine
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|SqlPrettyWriter
argument_list|(
name|config
argument_list|)
operator|.
name|format
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

