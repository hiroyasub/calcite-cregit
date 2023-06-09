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
name|sql
operator|.
name|SqlDialect
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
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
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
name|List
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
name|Consumer
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
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * Helper class for building fluent parser tests such as  * {@code sql("values 1").ok();}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlParserFixture
block|{
specifier|public
specifier|static
specifier|final
name|SqlTestFactory
name|FACTORY
init|=
name|SqlTestFactory
operator|.
name|INSTANCE
operator|.
name|withParserConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withQuoting
argument_list|(
name|Quoting
operator|.
name|DOUBLE_QUOTE
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|Casing
operator|.
name|TO_UPPER
argument_list|)
operator|.
name|withQuotedCasing
argument_list|(
name|Casing
operator|.
name|UNCHANGED
argument_list|)
operator|.
name|withConformance
argument_list|(
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlParserFixture
name|DEFAULT
init|=
operator|new
name|SqlParserFixture
argument_list|(
name|FACTORY
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
literal|"?"
argument_list|)
argument_list|,
literal|false
argument_list|,
name|SqlParserTest
operator|.
name|TesterImpl
operator|.
name|DEFAULT
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|,
name|parser
lambda|->
block|{
block|}
argument_list|)
decl_stmt|;
specifier|public
specifier|final
name|SqlTestFactory
name|factory
decl_stmt|;
specifier|public
specifier|final
name|StringAndPos
name|sap
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|expression
decl_stmt|;
specifier|public
specifier|final
name|SqlParserTest
operator|.
name|Tester
name|tester
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|convertToLinux
decl_stmt|;
specifier|public
specifier|final
annotation|@
name|Nullable
name|SqlDialect
name|dialect
decl_stmt|;
specifier|public
specifier|final
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
decl_stmt|;
name|SqlParserFixture
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|,
name|StringAndPos
name|sap
parameter_list|,
name|boolean
name|expression
parameter_list|,
name|SqlParserTest
operator|.
name|Tester
name|tester
parameter_list|,
annotation|@
name|Nullable
name|SqlDialect
name|dialect
parameter_list|,
name|boolean
name|convertToLinux
parameter_list|,
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserChecker
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|requireNonNull
argument_list|(
name|factory
argument_list|,
literal|"factory"
argument_list|)
expr_stmt|;
name|this
operator|.
name|sap
operator|=
name|requireNonNull
argument_list|(
name|sap
argument_list|,
literal|"sap"
argument_list|)
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|tester
operator|=
name|requireNonNull
argument_list|(
name|tester
argument_list|,
literal|"tester"
argument_list|)
expr_stmt|;
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|this
operator|.
name|convertToLinux
operator|=
name|convertToLinux
expr_stmt|;
name|this
operator|.
name|parserChecker
operator|=
name|requireNonNull
argument_list|(
name|parserChecker
argument_list|,
literal|"parserChecker"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SqlParserFixture
name|same
parameter_list|()
block|{
return|return
name|compare
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
return|;
block|}
specifier|public
name|SqlParserFixture
name|ok
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
if|if
condition|(
name|expected
operator|.
name|equals
argument_list|(
name|sap
operator|.
name|sql
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"you should call same()"
argument_list|)
throw|;
block|}
return|return
name|compare
argument_list|(
name|expected
argument_list|)
return|;
block|}
specifier|public
name|SqlParserFixture
name|compare
parameter_list|(
name|String
name|expected
parameter_list|)
block|{
specifier|final
name|UnaryOperator
argument_list|<
name|String
argument_list|>
name|converter
init|=
name|SqlParserTest
operator|.
name|linux
argument_list|(
name|convertToLinux
argument_list|)
decl_stmt|;
if|if
condition|(
name|expression
condition|)
block|{
name|tester
operator|.
name|checkExp
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|converter
argument_list|,
name|expected
argument_list|,
name|parserChecker
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|dialect
argument_list|,
name|converter
argument_list|,
name|expected
argument_list|,
name|parserChecker
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|SqlParserFixture
name|fails
parameter_list|(
name|String
name|expectedMsgPattern
parameter_list|)
block|{
if|if
condition|(
name|expression
condition|)
block|{
name|tester
operator|.
name|checkExpFails
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expectedMsgPattern
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|tester
operator|.
name|checkFails
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
literal|false
argument_list|,
name|expectedMsgPattern
argument_list|)
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|SqlParserFixture
name|hasWarning
parameter_list|(
name|Consumer
argument_list|<
name|List
argument_list|<
name|?
extends|extends
name|Throwable
argument_list|>
argument_list|>
name|messageMatcher
parameter_list|)
block|{
specifier|final
name|Consumer
argument_list|<
name|SqlParser
argument_list|>
name|parserConsumer
init|=
name|parser
lambda|->
name|messageMatcher
operator|.
name|accept
argument_list|(
name|parser
operator|.
name|getWarnings
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlParserFixture
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expression
argument_list|,
name|tester
argument_list|,
name|dialect
argument_list|,
name|convertToLinux
argument_list|,
name|parserConsumer
argument_list|)
return|;
block|}
specifier|public
name|SqlParserFixture
name|node
parameter_list|(
name|Matcher
argument_list|<
name|SqlNode
argument_list|>
name|matcher
parameter_list|)
block|{
name|tester
operator|.
name|checkNode
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Changes the SQL.    */
specifier|public
name|SqlParserFixture
name|sql
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
if|if
condition|(
name|sql
operator|.
name|equals
argument_list|(
name|this
operator|.
name|sap
operator|.
name|addCarets
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
name|StringAndPos
name|sap
init|=
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlParserFixture
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expression
argument_list|,
name|tester
argument_list|,
name|dialect
argument_list|,
name|convertToLinux
argument_list|,
name|parserChecker
argument_list|)
return|;
block|}
comment|/**    * Flags that this is an expression, not a whole query.    */
specifier|public
name|SqlParserFixture
name|expression
parameter_list|()
block|{
return|return
name|expression
argument_list|(
literal|true
argument_list|)
return|;
block|}
comment|/**    * Sets whether this is an expression (as opposed to a whole query).    */
specifier|public
name|SqlParserFixture
name|expression
parameter_list|(
name|boolean
name|expression
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|expression
operator|==
name|expression
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlParserFixture
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expression
argument_list|,
name|tester
argument_list|,
name|dialect
argument_list|,
name|convertToLinux
argument_list|,
name|parserChecker
argument_list|)
return|;
block|}
comment|/**    * Creates an instance of helper class {@link SqlParserListFixture} to test parsing a    * list of statements.    */
specifier|protected
name|SqlParserListFixture
name|list
parameter_list|()
block|{
return|return
operator|new
name|SqlParserListFixture
argument_list|(
name|factory
argument_list|,
name|tester
argument_list|,
name|dialect
argument_list|,
name|convertToLinux
argument_list|,
name|sap
argument_list|)
return|;
block|}
specifier|public
name|SqlParserFixture
name|withDialect
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
block|{
if|if
condition|(
name|dialect
operator|==
name|this
operator|.
name|dialect
condition|)
block|{
return|return
name|this
return|;
block|}
name|SqlTestFactory
name|factory
init|=
name|this
operator|.
name|factory
operator|.
name|withParserConfig
argument_list|(
name|dialect
operator|::
name|configureParser
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlParserFixture
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expression
argument_list|,
name|tester
argument_list|,
name|dialect
argument_list|,
name|convertToLinux
argument_list|,
name|parserChecker
argument_list|)
return|;
block|}
comment|/**    * Creates a copy of this fixture with a new test factory.    */
specifier|public
name|SqlParserFixture
name|withFactory
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlTestFactory
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|SqlTestFactory
name|factory
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|factory
argument_list|)
decl_stmt|;
if|if
condition|(
name|factory
operator|==
name|this
operator|.
name|factory
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlParserFixture
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expression
argument_list|,
name|tester
argument_list|,
name|dialect
argument_list|,
name|convertToLinux
argument_list|,
name|parserChecker
argument_list|)
return|;
block|}
specifier|public
name|SqlParserFixture
name|withConfig
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlParser
operator|.
name|Config
argument_list|>
name|transform
parameter_list|)
block|{
return|return
name|withFactory
argument_list|(
name|f
lambda|->
name|f
operator|.
name|withParserConfig
argument_list|(
name|transform
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|SqlParserFixture
name|withConformance
parameter_list|(
name|SqlConformance
name|conformance
parameter_list|)
block|{
return|return
name|withConfig
argument_list|(
name|c
lambda|->
name|c
operator|.
name|withConformance
argument_list|(
name|conformance
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|SqlParserFixture
name|withTester
parameter_list|(
name|SqlParserTest
operator|.
name|Tester
name|tester
parameter_list|)
block|{
if|if
condition|(
name|tester
operator|==
name|this
operator|.
name|tester
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlParserFixture
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expression
argument_list|,
name|tester
argument_list|,
name|dialect
argument_list|,
name|convertToLinux
argument_list|,
name|parserChecker
argument_list|)
return|;
block|}
comment|/**    * Sets whether to convert actual strings to Linux (converting Windows    * CR-LF line endings to Linux LF) before comparing them to expected.    * Default is true.    */
specifier|public
name|SqlParserFixture
name|withConvertToLinux
parameter_list|(
name|boolean
name|convertToLinux
parameter_list|)
block|{
if|if
condition|(
name|convertToLinux
operator|==
name|this
operator|.
name|convertToLinux
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlParserFixture
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expression
argument_list|,
name|tester
argument_list|,
name|dialect
argument_list|,
name|convertToLinux
argument_list|,
name|parserChecker
argument_list|)
return|;
block|}
specifier|public
name|SqlParser
name|parser
parameter_list|()
block|{
return|return
name|factory
operator|.
name|createParser
argument_list|(
name|sap
operator|.
name|addCarets
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|SqlNode
name|node
parameter_list|()
block|{
return|return
operator|(
operator|(
name|SqlParserTest
operator|.
name|TesterImpl
operator|)
name|tester
operator|)
operator|.
name|parseStmtAndHandleEx
argument_list|(
name|factory
argument_list|,
name|sap
operator|.
name|addCarets
argument_list|()
argument_list|,
name|parser
lambda|->
block|{
block|}
argument_list|)
return|;
block|}
specifier|public
name|SqlNodeList
name|nodeList
parameter_list|()
block|{
return|return
operator|(
operator|(
name|SqlParserTest
operator|.
name|TesterImpl
operator|)
name|tester
operator|)
operator|.
name|parseStmtsAndHandleEx
argument_list|(
name|factory
argument_list|,
name|sap
operator|.
name|addCarets
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

