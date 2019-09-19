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
name|config
operator|.
name|CalciteConnectionProperty
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
name|jdbc
operator|.
name|CalciteConnection
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
name|materialize
operator|.
name|MaterializationService
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
name|plan
operator|.
name|Contexts
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
name|schema
operator|.
name|SchemaPlus
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
name|dialect
operator|.
name|CalciteSqlDialect
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
name|parser
operator|.
name|babel
operator|.
name|SqlBabelParserImpl
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
name|tools
operator|.
name|Frameworks
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
name|tools
operator|.
name|Planner
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
name|net
operator|.
name|hydromatic
operator|.
name|quidem
operator|.
name|AbstractCommand
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|quidem
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|quidem
operator|.
name|CommandHandler
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|quidem
operator|.
name|Quidem
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
name|Parameterized
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_comment
comment|/**  * Unit tests for the Babel SQL parser.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|BabelQuidemTest
extends|extends
name|QuidemTest
block|{
comment|/** Creates a BabelQuidemTest. Public per {@link Parameterized}. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"WeakerAccess"
argument_list|)
specifier|public
name|BabelQuidemTest
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/** Runs a test from the command line.    *    *<p>For example:    *    *<blockquote>    *<code>java BabelQuidemTest sql/table.iq</code>    *</blockquote> */
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
name|Exception
block|{
for|for
control|(
name|String
name|arg
range|:
name|args
control|)
block|{
operator|new
name|BabelQuidemTest
argument_list|(
name|arg
argument_list|)
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|MaterializationService
operator|.
name|setThreadLocal
argument_list|()
expr_stmt|;
name|super
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
comment|/** For {@link Parameterized} runner. */
annotation|@
name|Parameterized
operator|.
name|Parameters
argument_list|(
name|name
operator|=
literal|"{index}: quidem({0})"
argument_list|)
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
comment|// Start with a test file we know exists, then find the directory and list
comment|// its files.
specifier|final
name|String
name|first
init|=
literal|"sql/select.iq"
decl_stmt|;
return|return
name|data
argument_list|(
name|first
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Quidem
operator|.
name|ConnectionFactory
name|createConnectionFactory
parameter_list|()
block|{
return|return
operator|new
name|QuidemConnectionFactory
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|name
parameter_list|,
name|boolean
name|reference
parameter_list|)
throws|throws
name|Exception
block|{
switch|switch
condition|(
name|name
condition|)
block|{
case|case
literal|"babel"
case|:
return|return
name|BabelTest
operator|.
name|connect
argument_list|()
return|;
case|case
literal|"scott-babel"
case|:
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|SCOTT
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|PARSER_FACTORY
argument_list|,
name|SqlBabelParserImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#FACTORY"
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CONFORMANCE
argument_list|,
name|SqlConformanceEnum
operator|.
name|BABEL
argument_list|)
operator|.
name|connect
argument_list|()
return|;
case|case
literal|"scott-redshift"
case|:
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|SCOTT
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|FUN
argument_list|,
literal|"standard,postgresql,oracle"
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|PARSER_FACTORY
argument_list|,
name|SqlBabelParserImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"#FACTORY"
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|CONFORMANCE
argument_list|,
name|SqlConformanceEnum
operator|.
name|BABEL
argument_list|)
operator|.
name|with
argument_list|(
name|CalciteConnectionProperty
operator|.
name|LENIENT_OPERATOR_LOOKUP
argument_list|,
literal|true
argument_list|)
operator|.
name|connect
argument_list|()
return|;
default|default:
return|return
name|super
operator|.
name|connect
argument_list|(
name|name
argument_list|,
name|reference
argument_list|)
return|;
block|}
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|protected
name|CommandHandler
name|createCommandHandler
parameter_list|()
block|{
return|return
operator|new
name|BabelCommandHandler
argument_list|()
return|;
block|}
comment|/** Command that prints the validated parse tree of a SQL statement. */
specifier|static
class|class
name|ExplainValidatedCommand
extends|extends
name|AbstractCommand
block|{
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|lines
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|String
argument_list|>
name|content
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|productSet
decl_stmt|;
name|ExplainValidatedCommand
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|lines
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|content
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|productSet
parameter_list|)
block|{
name|this
operator|.
name|lines
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|lines
argument_list|)
expr_stmt|;
name|this
operator|.
name|content
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|this
operator|.
name|productSet
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|productSet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|execute
parameter_list|(
name|Context
name|x
parameter_list|,
name|boolean
name|execute
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|execute
condition|)
block|{
comment|// use Babel parser
specifier|final
name|SqlParser
operator|.
name|ConfigBuilder
name|parserConfig
init|=
name|SqlParser
operator|.
name|configBuilder
argument_list|()
operator|.
name|setParserFactory
argument_list|(
name|SqlBabelParserImpl
operator|.
name|FACTORY
argument_list|)
decl_stmt|;
comment|// extract named schema from connection and use it in planner
specifier|final
name|CalciteConnection
name|calciteConnection
init|=
name|x
operator|.
name|connection
argument_list|()
operator|.
name|unwrap
argument_list|(
name|CalciteConnection
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|schemaName
init|=
name|calciteConnection
operator|.
name|getSchema
argument_list|()
decl_stmt|;
specifier|final
name|SchemaPlus
name|schema
init|=
name|schemaName
operator|!=
literal|null
condition|?
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
operator|.
name|getSubSchema
argument_list|(
name|schemaName
argument_list|)
else|:
name|calciteConnection
operator|.
name|getRootSchema
argument_list|()
decl_stmt|;
specifier|final
name|Frameworks
operator|.
name|ConfigBuilder
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|defaultSchema
argument_list|(
name|schema
argument_list|)
operator|.
name|parserConfig
argument_list|(
name|parserConfig
operator|.
name|build
argument_list|()
argument_list|)
operator|.
name|context
argument_list|(
name|Contexts
operator|.
name|of
argument_list|(
name|calciteConnection
operator|.
name|config
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// parse, validate and un-parse
specifier|final
name|Quidem
operator|.
name|SqlCommand
name|sqlCommand
init|=
name|x
operator|.
name|previousSqlCommand
argument_list|()
decl_stmt|;
specifier|final
name|Planner
name|planner
init|=
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|config
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|node
init|=
name|planner
operator|.
name|parse
argument_list|(
name|sqlCommand
operator|.
name|sql
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|validateNode
init|=
name|planner
operator|.
name|validate
argument_list|(
name|node
argument_list|)
decl_stmt|;
specifier|final
name|SqlWriter
name|sqlWriter
init|=
operator|new
name|SqlPrettyWriter
argument_list|(
name|CalciteSqlDialect
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|validateNode
operator|.
name|unparse
argument_list|(
name|sqlWriter
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|x
operator|.
name|echo
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|sqlWriter
operator|.
name|toSqlString
argument_list|()
operator|.
name|getSql
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|x
operator|.
name|echo
argument_list|(
name|content
argument_list|)
expr_stmt|;
block|}
name|x
operator|.
name|echo
argument_list|(
name|lines
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Command handler that adds a "!explain-validated-on dialect..." command    * (see {@link ExplainValidatedCommand}). */
specifier|private
specifier|static
class|class
name|BabelCommandHandler
implements|implements
name|CommandHandler
block|{
annotation|@
name|Override
specifier|public
name|Command
name|parseCommand
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|lines
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|content
parameter_list|,
name|String
name|line
parameter_list|)
block|{
specifier|final
name|String
name|prefix
init|=
literal|"explain-validated-on"
decl_stmt|;
if|if
condition|(
name|line
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
specifier|final
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"explain-validated-on( [-_+a-zA-Z0-9]+)*?"
argument_list|)
decl_stmt|;
specifier|final
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|line
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
specifier|final
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|String
argument_list|>
name|set
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|matcher
operator|.
name|groupCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|set
operator|.
name|add
argument_list|(
name|matcher
operator|.
name|group
argument_list|(
name|i
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ExplainValidatedCommand
argument_list|(
name|lines
argument_list|,
name|content
argument_list|,
name|set
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End BabelQuidemTest.java
end_comment

end_unit

