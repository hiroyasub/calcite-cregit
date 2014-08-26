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
name|test
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
name|pretty
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
name|test
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|String
name|NL
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlPrettyWriterTest
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// ~ Helper methods -------------------------------------------------------
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
literal|"'; error is:"
operator|+
name|NL
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
specifier|protected
name|void
name|assertPrintsTo
parameter_list|(
name|boolean
name|newlines
parameter_list|,
specifier|final
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|SqlNode
name|node
init|=
name|parseQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setAlwaysUseParentheses
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|newlines
condition|)
block|{
name|prettyWriter
operator|.
name|setCaseClausesOnNewLines
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|String
name|actual
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
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
comment|// Now parse the result, and make sure it is structurally equivalent
comment|// to the original.
specifier|final
name|String
name|actual2
init|=
name|actual
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
init|=
name|parseQuery
argument_list|(
name|actual2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|node
operator|.
name|equalsDeep
argument_list|(
name|node2
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertExprPrintsTo
parameter_list|(
name|boolean
name|newlines
parameter_list|,
specifier|final
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
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
specifier|final
name|SqlNode
name|node
init|=
name|rowCall
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setAlwaysUseParentheses
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|newlines
condition|)
block|{
name|prettyWriter
operator|.
name|setCaseClausesOnNewLines
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|String
name|actual
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
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
comment|// Now parse the result, and make sure it is structurally equivalent
comment|// to the original.
specifier|final
name|String
name|actual2
init|=
name|actual
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
name|valuesCall2
init|=
name|parseQuery
argument_list|(
literal|"VALUES ("
operator|+
name|actual2
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|valuesCall
operator|.
name|equalsDeep
argument_list|(
name|valuesCall2
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// ~ Tests ----------------------------------------------------------------
specifier|protected
name|void
name|checkSimple
parameter_list|(
name|SqlPrettyWriter
name|prettyWriter
parameter_list|,
name|String
name|expectedDesc
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|SqlNode
name|node
init|=
name|parseQuery
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
decl_stmt|;
comment|// Describe settings
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
name|expectedDesc
argument_list|,
name|desc
argument_list|)
expr_stmt|;
comment|// Format
name|String
name|actual
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
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefault
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIndent8
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setIndentation
argument_list|(
literal|8
argument_list|)
expr_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testClausesNotOnNewLine
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setClauseStartsLine
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectListItemsOnSeparateLines
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelectListExtraIndentFlag
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|prettyWriter
operator|.
name|setSelectListExtraIndentFlag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testKeywordsLowerCase
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setKeywordsLowerCase
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParenthesizeAllExprs
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setAlwaysUseParentheses
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOnlyQuoteIdentifiersWhichNeedIt
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setQuoteAllIdentifiers
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDamiansSubqueryStyle
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Note that ( is at the indent, SELECT is on the same line, and ) is
comment|// below it.
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setSubqueryStyle
argument_list|(
name|SqlWriter
operator|.
name|SubqueryStyle
operator|.
name|BLACK
argument_list|)
expr_stmt|;
name|checkSimple
argument_list|(
name|prettyWriter
argument_list|,
literal|"${desc}"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
comment|// test disabled because default SQL parser cannot parse DDL
specifier|public
name|void
name|_testExplain
parameter_list|()
block|{
name|assertPrintsTo
argument_list|(
literal|false
argument_list|,
literal|"explain select * from t"
argument_list|,
literal|"foo"
argument_list|)
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
name|assertExprPrintsTo
argument_list|(
literal|true
argument_list|,
literal|"case 1 when 2 + 3 then 4 when case a when b then c else d end then 6 else 7 end"
argument_list|,
literal|"CASE"
operator|+
name|NL
operator|+
literal|"WHEN 1 = 2 + 3"
operator|+
name|NL
operator|+
literal|"THEN 4"
operator|+
name|NL
operator|+
literal|"WHEN 1 = CASE"
operator|+
name|NL
operator|+
literal|"        WHEN `A` = `B`"
operator|+
name|NL
comment|// todo: indent should be 4 not 8
operator|+
literal|"        THEN `C`"
operator|+
name|NL
operator|+
literal|"        ELSE `D`"
operator|+
name|NL
operator|+
literal|"        END"
operator|+
name|NL
operator|+
literal|"THEN 6"
operator|+
name|NL
operator|+
literal|"ELSE 7"
operator|+
name|NL
operator|+
literal|"END"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCase2
parameter_list|()
block|{
name|assertExprPrintsTo
argument_list|(
literal|false
argument_list|,
literal|"case 1 when 2 + 3 then 4 when case a when b then c else d end then 6 else 7 end"
argument_list|,
literal|"CASE WHEN 1 = 2 + 3 THEN 4 WHEN 1 = CASE WHEN `A` = `B` THEN `C` ELSE `D` END THEN 6 ELSE 7 END"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBetween
parameter_list|()
block|{
name|assertExprPrintsTo
argument_list|(
literal|true
argument_list|,
literal|"x not between symmetric y and z"
argument_list|,
literal|"`X` NOT BETWEEN SYMMETRIC `Y` AND `Z`"
argument_list|)
expr_stmt|;
comment|// todo: remove leading
comment|// space
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCast
parameter_list|()
block|{
name|assertExprPrintsTo
argument_list|(
literal|true
argument_list|,
literal|"cast(x + y as decimal(5, 10))"
argument_list|,
literal|"CAST(`X` + `Y` AS DECIMAL(5, 10))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLiteralChain
parameter_list|()
block|{
name|assertExprPrintsTo
argument_list|(
literal|true
argument_list|,
literal|"'x' /* comment */ 'y'"
operator|+
name|NL
operator|+
literal|"  'z' "
argument_list|,
literal|"'x'"
operator|+
name|NL
operator|+
literal|"'y'"
operator|+
name|NL
operator|+
literal|"'z'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOverlaps
parameter_list|()
block|{
name|assertExprPrintsTo
argument_list|(
literal|true
argument_list|,
literal|"(x,xx) overlaps (y,yy) or x is not null"
argument_list|,
literal|"(`X`, `XX`) OVERLAPS (`Y`, `YY`) OR `X` IS NOT NULL"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUnion
parameter_list|()
block|{
name|assertPrintsTo
argument_list|(
literal|true
argument_list|,
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
argument_list|,
comment|// todo: SELECT should not be indended from UNION, like this:
comment|// UNION
comment|//     SELECT *
comment|//     FROM `W`
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultiset
parameter_list|()
block|{
name|assertPrintsTo
argument_list|(
literal|false
argument_list|,
literal|"values (multiset (select * from t))"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInnerJoin
parameter_list|()
block|{
name|assertPrintsTo
argument_list|(
literal|true
argument_list|,
literal|"select * from x inner join y on x.k=y.k"
argument_list|,
literal|"${formatted}"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereListItemsOnSeparateLinesOr
parameter_list|()
throws|throws
name|Exception
block|{
name|checkPrettySeparateLines
argument_list|(
literal|"select x"
operator|+
literal|" from y"
operator|+
literal|" where h is not null and i< j"
operator|+
literal|" or ((a or b) is true) and d not in (f,g)"
operator|+
literal|" or x<> z"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testWhereListItemsOnSeparateLinesAnd
parameter_list|()
throws|throws
name|Exception
block|{
name|checkPrettySeparateLines
argument_list|(
literal|"select x"
operator|+
literal|" from y"
operator|+
literal|" where h is not null and (i< j"
operator|+
literal|" or ((a or b) is true)) and (d not in (f,g)"
operator|+
literal|" or v<> ((w * x) + y) * z)"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkPrettySeparateLines
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
specifier|final
name|SqlPrettyWriter
name|prettyWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|SqlDialect
operator|.
name|DUMMY
argument_list|)
decl_stmt|;
name|prettyWriter
operator|.
name|setSelectListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|prettyWriter
operator|.
name|setSelectListExtraIndentFlag
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SqlNode
name|node
init|=
name|parseQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
comment|// Describe settings
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
literal|"${desc}"
argument_list|,
name|desc
argument_list|)
expr_stmt|;
name|prettyWriter
operator|.
name|setWhereListItemsOnSeparateLines
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Format
name|String
name|actual
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
literal|"${formatted}"
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlPrettyWriterTest.java
end_comment

end_unit

