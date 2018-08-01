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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|SqlCollation
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
name|test
operator|.
name|SqlTestFactory
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
name|test
operator|.
name|SqlTester
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
name|test
operator|.
name|SqlTesterImpl
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
name|SqlMonotonicity
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
name|SqlValidator
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
name|catalog
operator|.
name|MockCatalogReaderExtended
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
name|TestUtil
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
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|MethodRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|FrameworkMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|model
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * An abstract base class for implementing tests against {@link SqlValidator}.  *  *<p>A derived class can refine this test in two ways. First, it can add<code>  * testXxx()</code> methods, to test more functionality.  *  *<p>Second, it can override the {@link #getTester} method to return a  * different implementation of the {@link Tester} object. This encapsulates the  * differences between test environments, for example, which SQL parser or  * validator to use.</p>  */
end_comment

begin_class
specifier|public
class|class
name|SqlValidatorTestCase
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|private
specifier|static
specifier|final
name|Pattern
name|LINE_COL_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"At line ([0-9]+), column ([0-9]+)"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Pattern
name|LINE_COL_TWICE_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?s)From line ([0-9]+), column ([0-9]+) to line ([0-9]+), column ([0-9]+): (.*)"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlTestFactory
name|EXTENDED_TEST_FACTORY
init|=
name|SqlTestFactory
operator|.
name|INSTANCE
operator|.
name|withCatalogReader
argument_list|(
name|MockCatalogReaderExtended
operator|::
operator|new
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|SqlTesterImpl
name|EXTENDED_CATALOG_TESTER
init|=
operator|new
name|SqlTesterImpl
argument_list|(
name|EXTENDED_TEST_FACTORY
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|SqlTesterImpl
name|EXTENDED_CATALOG_TESTER_2003
init|=
operator|new
name|SqlTesterImpl
argument_list|(
name|EXTENDED_TEST_FACTORY
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|PRAGMATIC_2003
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|SqlTesterImpl
name|EXTENDED_CATALOG_TESTER_LENIENT
init|=
operator|new
name|SqlTesterImpl
argument_list|(
name|EXTENDED_TEST_FACTORY
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|LENIENT
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|MethodRule
name|TESTER_CONFIGURATION_RULE
init|=
operator|new
name|TesterConfigurationRule
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
name|SqlTester
name|tester
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a test case.    */
specifier|public
name|SqlValidatorTestCase
parameter_list|()
block|{
name|this
operator|.
name|tester
operator|=
name|getTester
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Returns a tester. Derived classes should override this method to run the    * same set of tests in a different testing environment.    */
specifier|public
name|SqlTester
name|getTester
parameter_list|()
block|{
return|return
operator|new
name|SqlTesterImpl
argument_list|(
name|SqlTestFactory
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
specifier|public
specifier|final
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
name|tester
argument_list|,
name|sql
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|Sql
name|expr
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
specifier|final
name|Sql
name|winSql
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
return|;
block|}
specifier|public
specifier|final
name|Sql
name|win
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|sql
argument_list|(
literal|"select * from emp "
operator|+
name|sql
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|winExp
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|winSql
argument_list|(
literal|"select "
operator|+
name|sql
operator|+
literal|" from emp window w as (order by deptno)"
argument_list|)
return|;
block|}
specifier|public
name|Sql
name|winExp2
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|winSql
argument_list|(
literal|"select "
operator|+
name|sql
operator|+
literal|" from emp"
argument_list|)
return|;
block|}
specifier|public
name|void
name|check
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|checkExp
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|SqlTesterImpl
operator|.
name|buildQuery
argument_list|(
name|sql
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks that a SQL query gives a particular error, or succeeds if {@code    * expected} is null.    */
specifier|public
specifier|final
name|void
name|checkFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|fails
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks that a SQL expression gives a particular error.    */
specifier|public
specifier|final
name|void
name|checkExpFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|SqlTesterImpl
operator|.
name|buildQuery
argument_list|(
name|sql
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks that a SQL expression gives a particular error, and that the    * location of the error is the whole expression.    */
specifier|public
specifier|final
name|void
name|checkWholeExpFails
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
assert|assert
name|sql
operator|.
name|indexOf
argument_list|(
literal|'^'
argument_list|)
operator|<
literal|0
assert|;
name|checkExpFails
argument_list|(
literal|"^"
operator|+
name|sql
operator|+
literal|"^"
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|void
name|checkExpType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|checkColumnType
argument_list|(
name|SqlTesterImpl
operator|.
name|buildQuery
argument_list|(
name|sql
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks that a query returns a single column, and that the column has the    * expected type. For example,    *    *<blockquote><code>checkColumnType("SELECT empno FROM Emp", "INTEGER NOT    * NULL");</code></blockquote>    *    * @param sql      Query    * @param expected Expected type, including nullability    */
specifier|public
specifier|final
name|void
name|checkColumnType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|checkColumnType
argument_list|(
name|sql
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks that a query returns a row of the expected type. For example,    *    *<blockquote><code>checkResultType("select empno, name from emp","{EMPNO    * INTEGER NOT NULL, NAME VARCHAR(10) NOT NULL}");</code></blockquote>    *    * @param sql      Query    * @param expected Expected row type    */
specifier|public
specifier|final
name|void
name|checkResultType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|checkResultType
argument_list|(
name|sql
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks that the first column returned by a query has the expected type.    * For example,    *    *<blockquote><code>checkQueryType("SELECT empno FROM Emp", "INTEGER NOT    * NULL");</code></blockquote>    *    * @param sql      Query    * @param expected Expected type, including nullability    */
specifier|public
specifier|final
name|void
name|checkIntervalConv
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|checkIntervalConv
argument_list|(
name|SqlTesterImpl
operator|.
name|buildQuery
argument_list|(
name|sql
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|final
name|void
name|assertExceptionIsThrown
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
block|{
assert|assert
name|expectedMsgPattern
operator|!=
literal|null
assert|;
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|sql
argument_list|,
name|expectedMsgPattern
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|checkCharset
parameter_list|(
name|String
name|sql
parameter_list|,
name|Charset
name|expectedCharset
parameter_list|)
block|{
name|tester
operator|.
name|checkCharset
argument_list|(
name|sql
argument_list|,
name|expectedCharset
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|checkCollation
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedCollationName
parameter_list|,
name|SqlCollation
operator|.
name|Coercibility
name|expectedCoercibility
parameter_list|)
block|{
name|tester
operator|.
name|checkCollation
argument_list|(
name|sql
argument_list|,
name|expectedCollationName
argument_list|,
name|expectedCoercibility
argument_list|)
expr_stmt|;
block|}
comment|/**    * Checks whether an exception matches the expected pattern. If<code>    * sap</code> contains an error location, checks this too.    *    * @param ex                 Exception thrown    * @param expectedMsgPattern Expected pattern    * @param sap                Query and (optional) position in query    */
specifier|public
specifier|static
name|void
name|checkEx
parameter_list|(
name|Throwable
name|ex
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|,
name|SqlParserUtil
operator|.
name|StringAndPos
name|sap
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|ex
condition|)
block|{
if|if
condition|(
name|expectedMsgPattern
operator|==
literal|null
condition|)
block|{
comment|// No error expected, and no error happened.
return|return;
block|}
else|else
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
literal|"]; expected ["
operator|+
name|expectedMsgPattern
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
name|Throwable
name|actualException
init|=
name|ex
decl_stmt|;
name|String
name|actualMessage
init|=
name|actualException
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|int
name|actualLine
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|actualColumn
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|actualEndLine
init|=
literal|100
decl_stmt|;
name|int
name|actualEndColumn
init|=
literal|99
decl_stmt|;
comment|// Search for an CalciteContextException somewhere in the stack.
name|CalciteContextException
name|ece
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Throwable
name|x
init|=
name|ex
init|;
name|x
operator|!=
literal|null
condition|;
name|x
operator|=
name|x
operator|.
name|getCause
argument_list|()
control|)
block|{
if|if
condition|(
name|x
operator|instanceof
name|CalciteContextException
condition|)
block|{
name|ece
operator|=
operator|(
name|CalciteContextException
operator|)
name|x
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|x
operator|.
name|getCause
argument_list|()
operator|==
name|x
condition|)
block|{
break|break;
block|}
block|}
comment|// Search for a SqlParseException -- with its position set -- somewhere
comment|// in the stack.
name|SqlParseException
name|spe
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Throwable
name|x
init|=
name|ex
init|;
name|x
operator|!=
literal|null
condition|;
name|x
operator|=
name|x
operator|.
name|getCause
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|x
operator|instanceof
name|SqlParseException
operator|)
operator|&&
operator|(
operator|(
operator|(
name|SqlParseException
operator|)
name|x
operator|)
operator|.
name|getPos
argument_list|()
operator|!=
literal|null
operator|)
condition|)
block|{
name|spe
operator|=
operator|(
name|SqlParseException
operator|)
name|x
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|x
operator|.
name|getCause
argument_list|()
operator|==
name|x
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|ece
operator|!=
literal|null
condition|)
block|{
name|actualLine
operator|=
name|ece
operator|.
name|getPosLine
argument_list|()
expr_stmt|;
name|actualColumn
operator|=
name|ece
operator|.
name|getPosColumn
argument_list|()
expr_stmt|;
name|actualEndLine
operator|=
name|ece
operator|.
name|getEndPosLine
argument_list|()
expr_stmt|;
name|actualEndColumn
operator|=
name|ece
operator|.
name|getEndPosColumn
argument_list|()
expr_stmt|;
if|if
condition|(
name|ece
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|actualException
operator|=
name|ece
operator|.
name|getCause
argument_list|()
expr_stmt|;
name|actualMessage
operator|=
name|actualException
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|spe
operator|!=
literal|null
condition|)
block|{
name|actualLine
operator|=
name|spe
operator|.
name|getPos
argument_list|()
operator|.
name|getLineNum
argument_list|()
expr_stmt|;
name|actualColumn
operator|=
name|spe
operator|.
name|getPos
argument_list|()
operator|.
name|getColumnNum
argument_list|()
expr_stmt|;
name|actualEndLine
operator|=
name|spe
operator|.
name|getPos
argument_list|()
operator|.
name|getEndLineNum
argument_list|()
expr_stmt|;
name|actualEndColumn
operator|=
name|spe
operator|.
name|getPos
argument_list|()
operator|.
name|getEndColumnNum
argument_list|()
expr_stmt|;
if|if
condition|(
name|spe
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|actualException
operator|=
name|spe
operator|.
name|getCause
argument_list|()
expr_stmt|;
name|actualMessage
operator|=
name|actualException
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
specifier|final
name|String
name|message
init|=
name|ex
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|Matcher
name|matcher
init|=
name|LINE_COL_TWICE_PATTERN
operator|.
name|matcher
argument_list|(
name|message
argument_list|)
decl_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|actualLine
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|actualColumn
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|actualEndLine
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|actualEndColumn
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|actualMessage
operator|=
name|matcher
operator|.
name|group
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|matcher
operator|=
name|LINE_COL_PATTERN
operator|.
name|matcher
argument_list|(
name|message
argument_list|)
expr_stmt|;
if|if
condition|(
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
name|actualLine
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|actualColumn
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|expectedMsgPattern
operator|!=
literal|null
operator|&&
name|actualMessage
operator|.
name|matches
argument_list|(
name|expectedMsgPattern
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|expectedMsgPattern
condition|)
block|{
name|actualException
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Validator threw unexpected exception"
operator|+
literal|"; query ["
operator|+
name|sap
operator|.
name|sql
operator|+
literal|"]; exception ["
operator|+
name|actualMessage
operator|+
literal|"]; class ["
operator|+
name|actualException
operator|.
name|getClass
argument_list|()
operator|+
literal|"]; pos [line "
operator|+
name|actualLine
operator|+
literal|" col "
operator|+
name|actualColumn
operator|+
literal|" thru line "
operator|+
name|actualLine
operator|+
literal|" col "
operator|+
name|actualColumn
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
name|String
name|sqlWithCarets
decl_stmt|;
if|if
condition|(
name|actualColumn
operator|<=
literal|0
operator|||
name|actualLine
operator|<=
literal|0
operator|||
name|actualEndColumn
operator|<=
literal|0
operator|||
name|actualEndLine
operator|<=
literal|0
condition|)
block|{
if|if
condition|(
name|sap
operator|.
name|pos
operator|!=
literal|null
condition|)
block|{
name|AssertionError
name|e
init|=
operator|new
name|AssertionError
argument_list|(
literal|"Expected error to have position,"
operator|+
literal|" but actual error did not: "
operator|+
literal|" actual pos [line "
operator|+
name|actualLine
operator|+
literal|" col "
operator|+
name|actualColumn
operator|+
literal|" thru line "
operator|+
name|actualEndLine
operator|+
literal|" col "
operator|+
name|actualEndColumn
operator|+
literal|"]"
argument_list|)
decl_stmt|;
name|e
operator|.
name|initCause
argument_list|(
name|actualException
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|sqlWithCarets
operator|=
name|sap
operator|.
name|sql
expr_stmt|;
block|}
else|else
block|{
name|sqlWithCarets
operator|=
name|SqlParserUtil
operator|.
name|addCarets
argument_list|(
name|sap
operator|.
name|sql
argument_list|,
name|actualLine
argument_list|,
name|actualColumn
argument_list|,
name|actualEndLine
argument_list|,
name|actualEndColumn
operator|+
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|sap
operator|.
name|pos
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Actual error had a position, but expected "
operator|+
literal|"error did not. Add error position carets to sql:\n"
operator|+
name|sqlWithCarets
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|actualMessage
operator|!=
literal|null
condition|)
block|{
name|actualMessage
operator|=
name|Util
operator|.
name|toLinux
argument_list|(
name|actualMessage
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|actualMessage
operator|==
literal|null
operator|||
operator|!
name|actualMessage
operator|.
name|matches
argument_list|(
name|expectedMsgPattern
argument_list|)
condition|)
block|{
name|actualException
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
specifier|final
name|String
name|actualJavaRegexp
init|=
operator|(
name|actualMessage
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|TestUtil
operator|.
name|quoteForJava
argument_list|(
name|TestUtil
operator|.
name|quotePattern
argument_list|(
name|actualMessage
argument_list|)
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Validator threw different "
operator|+
literal|"exception than expected; query ["
operator|+
name|sap
operator|.
name|sql
operator|+
literal|"];\n"
operator|+
literal|" expected pattern ["
operator|+
name|expectedMsgPattern
operator|+
literal|"];\n"
operator|+
literal|" actual ["
operator|+
name|actualMessage
operator|+
literal|"];\n"
operator|+
literal|" actual as java regexp ["
operator|+
name|actualJavaRegexp
operator|+
literal|"]; pos ["
operator|+
name|actualLine
operator|+
literal|" col "
operator|+
name|actualColumn
operator|+
literal|" thru line "
operator|+
name|actualEndLine
operator|+
literal|" col "
operator|+
name|actualEndColumn
operator|+
literal|"]; sql ["
operator|+
name|sqlWithCarets
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|sap
operator|.
name|pos
operator|!=
literal|null
operator|&&
operator|(
name|actualLine
operator|!=
name|sap
operator|.
name|pos
operator|.
name|getLineNum
argument_list|()
operator|||
name|actualColumn
operator|!=
name|sap
operator|.
name|pos
operator|.
name|getColumnNum
argument_list|()
operator|||
name|actualEndLine
operator|!=
name|sap
operator|.
name|pos
operator|.
name|getEndLineNum
argument_list|()
operator|||
name|actualEndColumn
operator|!=
name|sap
operator|.
name|pos
operator|.
name|getEndColumnNum
argument_list|()
operator|)
condition|)
block|{
name|fail
argument_list|(
literal|"Validator threw expected "
operator|+
literal|"exception ["
operator|+
name|actualMessage
operator|+
literal|"];\nbut at pos [line "
operator|+
name|actualLine
operator|+
literal|" col "
operator|+
name|actualColumn
operator|+
literal|" thru line "
operator|+
name|actualEndLine
operator|+
literal|" col "
operator|+
name|actualEndColumn
operator|+
literal|"];\nsql ["
operator|+
name|sqlWithCarets
operator|+
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Inner Interfaces -------------------------------------------------------
comment|/**    * Encapsulates differences between test environments, for example, which    * SQL parser or validator to use.    *    *<p>It contains a mock schema with<code>EMP</code> and<code>DEPT</code>    * tables, which can run without having to start up Farrago.    */
specifier|public
interface|interface
name|Tester
block|{
name|SqlNode
name|parseQuery
parameter_list|(
name|String
name|sql
parameter_list|)
throws|throws
name|SqlParseException
function_decl|;
name|SqlNode
name|parseAndValidate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|String
name|sql
parameter_list|)
function_decl|;
name|SqlValidator
name|getValidator
parameter_list|()
function_decl|;
comment|/**      * Checks that a query is valid, or, if invalid, throws the right      * message at the right location.      *      *<p>If<code>expectedMsgPattern</code> is null, the query must      * succeed.      *      *<p>If<code>expectedMsgPattern</code> is not null, the query must      * fail, and give an error location of (expectedLine, expectedColumn)      * through (expectedEndLine, expectedEndColumn).      *      * @param sql                SQL statement      * @param expectedMsgPattern If this parameter is null the query must be      *                           valid for the test to pass; If this parameter      *                           is not null the query must be malformed and the      *                           message given must match the pattern      */
name|void
name|assertExceptionIsThrown
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedMsgPattern
parameter_list|)
function_decl|;
comment|/**      * Returns the data type of the sole column of a SQL query.      *      *<p>For example,<code>getResultType("VALUES (1")</code> returns      *<code>INTEGER</code>.      *      *<p>Fails if query returns more than one column.      *      * @see #getResultType(String)      */
name|RelDataType
name|getColumnType
parameter_list|(
name|String
name|sql
parameter_list|)
function_decl|;
comment|/**      * Returns the data type of the row returned by a SQL query.      *      *<p>For example,<code>getResultType("VALUES (1, 'foo')")</code>      * returns<code>RecordType(INTEGER EXPR$0, CHAR(3) EXPR#1)</code>.      */
name|RelDataType
name|getResultType
parameter_list|(
name|String
name|sql
parameter_list|)
function_decl|;
name|void
name|checkCollation
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expectedCollationName
parameter_list|,
name|SqlCollation
operator|.
name|Coercibility
name|expectedCoercibility
parameter_list|)
function_decl|;
name|void
name|checkCharset
parameter_list|(
name|String
name|sql
parameter_list|,
name|Charset
name|expectedCharset
parameter_list|)
function_decl|;
comment|/**      * Checks that a query returns one column of an expected type. For      * example,<code>checkType("VALUES (1 + 2)", "INTEGER NOT      * NULL")</code>.      */
name|void
name|checkColumnType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
comment|/**      * Given a SQL query, returns a list of the origins of each result      * field.      *      * @param sql             SQL query      * @param fieldOriginList Field origin list, e.g.      *                        "{(CATALOG.SALES.EMP.EMPNO, null)}"      */
name|void
name|checkFieldOrigin
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|fieldOriginList
parameter_list|)
function_decl|;
comment|/**      * Checks that a query gets rewritten to an expected form.      *      * @param validator       validator to use; null for default      * @param query           query to test      * @param expectedRewrite expected SQL text after rewrite and unparse      */
name|void
name|checkRewrite
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|String
name|query
parameter_list|,
name|String
name|expectedRewrite
parameter_list|)
function_decl|;
comment|/**      * Checks that a query returns one column of an expected type. For      * example,<code>checkType("select empno, name from emp""{EMPNO INTEGER      * NOT NULL, NAME VARCHAR(10) NOT NULL}")</code>.      */
name|void
name|checkResultType
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
comment|/**      * Checks if the interval value conversion to milliseconds is valid. For      * example,<code>checkIntervalConv(VALUES (INTERVAL '1' Minute),      * "60000")</code>.      */
name|void
name|checkIntervalConv
parameter_list|(
name|String
name|sql
parameter_list|,
name|String
name|expected
parameter_list|)
function_decl|;
comment|/**      * Given a SQL query, returns the monotonicity of the first item in the      * SELECT clause.      *      * @param sql SQL query      * @return Monotonicity      */
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|sql
parameter_list|)
function_decl|;
name|SqlConformance
name|getConformance
parameter_list|()
function_decl|;
block|}
comment|/** Fluent testing API. */
specifier|static
class|class
name|Sql
block|{
specifier|private
specifier|final
name|SqlTester
name|tester
decl_stmt|;
specifier|private
specifier|final
name|String
name|sql
decl_stmt|;
comment|/** Creates a Sql.      *      * @param tester Tester      * @param sql SQL query or expression      * @param query True if {@code sql} is a query, false if it is an expression      */
name|Sql
parameter_list|(
name|SqlTester
name|tester
parameter_list|,
name|String
name|sql
parameter_list|,
name|boolean
name|query
parameter_list|)
block|{
name|this
operator|.
name|tester
operator|=
name|tester
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|query
condition|?
name|sql
else|:
name|SqlTesterImpl
operator|.
name|buildQuery
argument_list|(
name|sql
argument_list|)
expr_stmt|;
block|}
name|Sql
name|tester
parameter_list|(
name|SqlTester
name|tester
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|public
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
name|tester
argument_list|,
name|sql
argument_list|,
literal|true
argument_list|)
return|;
block|}
name|Sql
name|withExtendedCatalog
parameter_list|()
block|{
return|return
name|tester
argument_list|(
name|EXTENDED_CATALOG_TESTER
argument_list|)
return|;
block|}
name|Sql
name|withExtendedCatalog2003
parameter_list|()
block|{
return|return
name|tester
argument_list|(
name|EXTENDED_CATALOG_TESTER_2003
argument_list|)
return|;
block|}
name|Sql
name|withExtendedCatalogLenient
parameter_list|()
block|{
return|return
name|tester
argument_list|(
name|EXTENDED_CATALOG_TESTER_LENIENT
argument_list|)
return|;
block|}
name|Sql
name|ok
parameter_list|()
block|{
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|sql
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|Sql
name|fails
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|sql
argument_list|,
name|expected
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
name|Sql
name|failsIf
parameter_list|(
name|boolean
name|b
parameter_list|,
name|String
name|expected
parameter_list|)
block|{
if|if
condition|(
name|b
condition|)
block|{
name|fails
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ok
argument_list|()
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|Sql
name|type
parameter_list|(
name|String
name|expectedType
parameter_list|)
block|{
name|tester
operator|.
name|checkResultType
argument_list|(
name|sql
argument_list|,
name|expectedType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Sql
name|columnType
parameter_list|(
name|String
name|expectedType
parameter_list|)
block|{
name|tester
operator|.
name|checkColumnType
argument_list|(
name|sql
argument_list|,
name|expectedType
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Sql
name|monotonic
parameter_list|(
name|SqlMonotonicity
name|expectedMonotonicity
parameter_list|)
block|{
name|tester
operator|.
name|checkMonotonic
argument_list|(
name|sql
argument_list|,
name|expectedMonotonicity
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|Sql
name|bindType
parameter_list|(
specifier|final
name|String
name|bindType
parameter_list|)
block|{
name|tester
operator|.
name|check
argument_list|(
name|sql
argument_list|,
literal|null
argument_list|,
name|parameterRowType
lambda|->
name|assertThat
argument_list|(
name|parameterRowType
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|bindType
argument_list|)
argument_list|)
argument_list|,
name|result
lambda|->
block|{ }
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Removes the carets from the SQL string. Useful if you want to run      * a test once at a conformance level where it fails, then run it again      * at a conformance level where it succeeds. */
specifier|public
name|Sql
name|sansCarets
parameter_list|()
block|{
return|return
operator|new
name|Sql
argument_list|(
name|tester
argument_list|,
name|sql
operator|.
name|replace
argument_list|(
literal|"^"
argument_list|,
literal|""
argument_list|)
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
comment|/**    * Enables to configure {@link #tester} behavior on a per-test basis.    * {@code tester} object is created in the test object constructor, and there's no    * trivial way to override its features.    *<p>This JUnit rule enables post-process test object on a per test method basis</p>    */
specifier|private
specifier|static
class|class
name|TesterConfigurationRule
implements|implements
name|MethodRule
block|{
annotation|@
name|Override
specifier|public
name|Statement
name|apply
parameter_list|(
name|Statement
name|statement
parameter_list|,
name|FrameworkMethod
name|frameworkMethod
parameter_list|,
name|Object
name|o
parameter_list|)
block|{
return|return
operator|new
name|Statement
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|evaluate
parameter_list|()
throws|throws
name|Throwable
block|{
name|SqlValidatorTestCase
name|tc
init|=
operator|(
name|SqlValidatorTestCase
operator|)
name|o
decl_stmt|;
name|SqlTester
name|tester
init|=
name|tc
operator|.
name|tester
decl_stmt|;
name|WithLex
name|lex
init|=
name|frameworkMethod
operator|.
name|getAnnotation
argument_list|(
name|WithLex
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|lex
operator|!=
literal|null
condition|)
block|{
name|tester
operator|=
name|tester
operator|.
name|withLex
argument_list|(
name|lex
operator|.
name|value
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|tc
operator|.
name|tester
operator|=
name|tester
expr_stmt|;
name|statement
operator|.
name|evaluate
argument_list|()
expr_stmt|;
block|}
block|}
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlValidatorTestCase.java
end_comment

end_unit

