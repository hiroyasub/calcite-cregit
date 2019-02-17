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
name|parser
operator|.
name|SqlAbstractParserImpl
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
name|SqlParserImplFactory
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
name|SqlParserTest
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
name|SqlParserUtil
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
name|babel
operator|.
name|SqlBabelParserImpl
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
name|Throwables
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests the "Babel" SQL parser, that understands all dialects of SQL.  */
end_comment

begin_class
specifier|public
class|class
name|BabelParserTest
extends|extends
name|SqlParserTest
block|{
annotation|@
name|Override
specifier|protected
name|SqlParserImplFactory
name|parserImplFactory
parameter_list|()
block|{
return|return
name|SqlBabelParserImpl
operator|.
name|FACTORY
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReservedWords
parameter_list|()
block|{
name|assertThat
argument_list|(
name|isReserved
argument_list|(
literal|"escape"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc}    *    *<p>Copy-pasted from base method, but with some key differences.    */
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|testMetadata
parameter_list|()
block|{
name|SqlAbstractParserImpl
operator|.
name|Metadata
name|metadata
init|=
name|getSqlParser
argument_list|(
literal|""
argument_list|)
operator|.
name|getMetadata
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedFunctionName
argument_list|(
literal|"ABS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedFunctionName
argument_list|(
literal|"FOO"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_USER"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"ABS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isContextVariableName
argument_list|(
literal|"FOO"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"A"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"KEY"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"FOO"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isNonReservedKeyword
argument_list|(
literal|"ABS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// was false
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"ABS"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_USER"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"KEY"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"HAVING"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"A"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isKeyword
argument_list|(
literal|"BAR"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"SELECT"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"CURRENT_CATALOG"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// was true
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"CURRENT_SCHEMA"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// was true
name|assertThat
argument_list|(
name|metadata
operator|.
name|isReservedWord
argument_list|(
literal|"KEY"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|jdbcKeywords
init|=
name|metadata
operator|.
name|getJdbcKeywords
argument_list|()
decl_stmt|;
name|assertThat
argument_list|(
name|jdbcKeywords
operator|.
name|contains
argument_list|(
literal|",COLLECT,"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// was true
name|assertThat
argument_list|(
operator|!
name|jdbcKeywords
operator|.
name|contains
argument_list|(
literal|",SELECT,"
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 1 from t"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT 1\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testYearIsNotReserved
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select 1 as year from t"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT 1 AS `YEAR`\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** Tests that there are no reserved keywords. */
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|testKeywords
parameter_list|()
block|{
specifier|final
name|String
index|[]
name|reserved
init|=
block|{
literal|"AND"
block|,
literal|"ANY"
block|,
literal|"END-EXEC"
block|}
decl_stmt|;
specifier|final
name|StringBuilder
name|sql
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"select "
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|expected
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"SELECT "
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|keyword
range|:
name|keywords
argument_list|(
literal|null
argument_list|)
control|)
block|{
comment|// Skip "END-EXEC"; I don't know how a keyword can contain '-'
if|if
condition|(
operator|!
name|Arrays
operator|.
name|asList
argument_list|(
name|reserved
argument_list|)
operator|.
name|contains
argument_list|(
name|keyword
argument_list|)
condition|)
block|{
name|sql
operator|.
name|append
argument_list|(
literal|"1 as "
argument_list|)
operator|.
name|append
argument_list|(
name|keyword
argument_list|)
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"1 as `"
argument_list|)
operator|.
name|append
argument_list|(
name|keyword
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"`,\n"
argument_list|)
expr_stmt|;
block|}
block|}
name|sql
operator|.
name|setLength
argument_list|(
name|sql
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|// remove ', '
name|expected
operator|.
name|setLength
argument_list|(
name|expected
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
comment|// remove ',\n'
name|sql
operator|.
name|append
argument_list|(
literal|" from t"
argument_list|)
expr_stmt|;
name|expected
operator|.
name|append
argument_list|(
literal|"\nFROM t"
argument_list|)
expr_stmt|;
name|sql
argument_list|(
name|sql
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** In Babel, AS is not reserved. */
annotation|@
name|Test
specifier|public
name|void
name|testAs
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT `AS`\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
literal|"select as from t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** In Babel, DESC is not reserved. */
annotation|@
name|Test
specifier|public
name|void
name|testDesc
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select desc\n"
operator|+
literal|"from t\n"
operator|+
literal|"order by desc asc, desc desc"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `DESC`\n"
operator|+
literal|"FROM `T`\n"
operator|+
literal|"ORDER BY `DESC`, `DESC` DESC"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/**    * This is a failure test making sure the LOOKAHEAD for WHEN clause is 2 in Babel, where    * in core parser this number is 1.    *    * @see SqlParserTest#testCaseExpression()    * @see<a href="https://issues.apache.org/jira/browse/CALCITE-2847">[CALCITE-2847]    * Optimize global LOOKAHEAD for SQL parsers</a>    */
annotation|@
name|Test
specifier|public
name|void
name|testCaseExpressionBabel
parameter_list|()
block|{
name|checkFails
argument_list|(
literal|"case x when 2, 4 then 3 ^when^ then 5 else 4 end"
argument_list|,
literal|"(?s)Encountered \"when then\" at .*"
argument_list|)
expr_stmt|;
block|}
comment|/** In Redshift, DATE is a function. It requires special treatment in the    * parser because it is a reserved keyword.    * (Curiously, TIMESTAMP and TIME are not functions.) */
annotation|@
name|Test
specifier|public
name|void
name|testDateFunction
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"SELECT `DATE`(`X`)\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
literal|"select date(x) from t"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** In Redshift, PostgreSQL the DATEADD, DATEDIFF and DATE_PART functions have    * ordinary function syntax except that its first argument is a time unit    * (e.g. DAY). We must not parse that first argument as an identifier. */
annotation|@
name|Test
specifier|public
name|void
name|testRedshiftFunctionsWithDateParts
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"SELECT DATEADD(day, 1, t),\n"
operator|+
literal|" DATEDIFF(week, 2, t),\n"
operator|+
literal|" DATE_PART(year, t) FROM mytable"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"SELECT `DATEADD`(DAY, 1, `T`),"
operator|+
literal|" `DATEDIFF`(WEEK, 2, `T`), `DATE_PART`(YEAR, `T`)\n"
operator|+
literal|"FROM `MYTABLE`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/** PostgreSQL and Redshift allow TIMESTAMP literals that contain only a    * date part. */
annotation|@
name|Test
specifier|public
name|void
name|testShortTimestampLiteral
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select timestamp '1969-07-20'"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"SELECT TIMESTAMP '1969-07-20 00:00:00'"
argument_list|)
expr_stmt|;
comment|// PostgreSQL allows the following. We should too.
name|sql
argument_list|(
literal|"select ^timestamp '1969-07-20 1:2'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal TIMESTAMP literal '1969-07-20 1:2': not in format "
operator|+
literal|"'yyyy-MM-dd HH:mm:ss'"
argument_list|)
expr_stmt|;
comment|// PostgreSQL gives 1969-07-20 01:02:00
name|sql
argument_list|(
literal|"select ^timestamp '1969-07-20:23:'^"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"Illegal TIMESTAMP literal '1969-07-20:23:': not in format "
operator|+
literal|"'yyyy-MM-dd HH:mm:ss'"
argument_list|)
expr_stmt|;
comment|// PostgreSQL gives 1969-07-20 23:00:00
block|}
comment|/**    * Babel parser's global {@code LOOKAHEAD} is larger than the core    * parser's. This causes different parse error message between these two    * parsers. Here we define a looser error checker for Babel, so that we can    * reuse failure testing codes from {@link SqlParserTest}.    *    *<p>If a test case is written in this file -- that is, not inherited -- it    * is still checked by {@link SqlParserTest}'s checker.    */
annotation|@
name|Override
specifier|protected
name|Tester
name|getTester
parameter_list|()
block|{
return|return
operator|new
name|TesterImpl
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|checkEx
parameter_list|(
name|String
name|expectedMsgPattern
parameter_list|,
name|SqlParserUtil
operator|.
name|StringAndPos
name|sap
parameter_list|,
name|Throwable
name|thrown
parameter_list|)
block|{
if|if
condition|(
name|thrownByBabelTest
argument_list|(
name|thrown
argument_list|)
condition|)
block|{
name|super
operator|.
name|checkEx
argument_list|(
name|expectedMsgPattern
argument_list|,
name|sap
argument_list|,
name|thrown
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkExNotNull
argument_list|(
name|sap
argument_list|,
name|thrown
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|boolean
name|thrownByBabelTest
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|Throwable
name|rootCause
init|=
name|Throwables
operator|.
name|getRootCause
argument_list|(
name|ex
argument_list|)
decl_stmt|;
name|StackTraceElement
index|[]
name|stackTrace
init|=
name|rootCause
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
for|for
control|(
name|StackTraceElement
name|stackTraceElement
range|:
name|stackTrace
control|)
block|{
name|String
name|className
init|=
name|stackTraceElement
operator|.
name|getClassName
argument_list|()
decl_stmt|;
if|if
condition|(
name|Objects
operator|.
name|equals
argument_list|(
name|className
argument_list|,
name|BabelParserTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|void
name|checkExNotNull
parameter_list|(
name|SqlParserUtil
operator|.
name|StringAndPos
name|sap
parameter_list|,
name|Throwable
name|thrown
parameter_list|)
block|{
if|if
condition|(
name|thrown
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Expected query to throw exception, "
operator|+
literal|"but it did not; query ["
operator|+
name|sap
operator|.
name|sql
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
comment|/** Tests parsing PostgreSQL-style "::" cast operator. */
annotation|@
name|Test
specifier|public
name|void
name|testParseInfixCast
parameter_list|()
block|{
name|checkParseInfixCast
argument_list|(
literal|"integer"
argument_list|)
expr_stmt|;
name|checkParseInfixCast
argument_list|(
literal|"varchar"
argument_list|)
expr_stmt|;
name|checkParseInfixCast
argument_list|(
literal|"boolean"
argument_list|)
expr_stmt|;
name|checkParseInfixCast
argument_list|(
literal|"double"
argument_list|)
expr_stmt|;
name|checkParseInfixCast
argument_list|(
literal|"bigint"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select -('12' || '.34')::VARCHAR(30)::INTEGER as x\n"
operator|+
literal|"from t"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|""
operator|+
literal|"SELECT (- ('12' || '.34') :: VARCHAR(30) :: INTEGER) AS `X`\n"
operator|+
literal|"FROM `T`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkParseInfixCast
parameter_list|(
name|String
name|sqlType
parameter_list|)
block|{
name|String
name|sql
init|=
literal|"SELECT x::"
operator|+
name|sqlType
operator|+
literal|" FROM (VALUES (1, 2)) as tbl(x,y)"
decl_stmt|;
name|String
name|expected
init|=
literal|"SELECT `X` :: "
operator|+
name|sqlType
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"FROM (VALUES (ROW(1, 2))) AS `TBL` (`X`, `Y`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End BabelParserTest.java
end_comment

end_unit

