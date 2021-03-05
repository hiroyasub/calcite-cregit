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
name|DataContext
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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|interpreter
operator|.
name|Interpreter
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
name|linq4j
operator|.
name|QueryProvider
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
name|hep
operator|.
name|HepPlanner
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
name|hep
operator|.
name|HepProgram
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
name|hep
operator|.
name|HepProgramBuilder
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
name|rel
operator|.
name|RelNode
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
name|rel
operator|.
name|RelRoot
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
name|rel
operator|.
name|rules
operator|.
name|CoreRules
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
name|tools
operator|.
name|FrameworkConfig
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|tools
operator|.
name|RelConversionException
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
name|ValidationException
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
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|AfterEach
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
name|BeforeEach
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
name|util
operator|.
name|ArrayList
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
name|Collections
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|equalTo
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link org.apache.calcite.interpreter.Interpreter}.  */
end_comment

begin_class
class|class
name|InterpreterTest
block|{
specifier|private
name|SchemaPlus
name|rootSchema
decl_stmt|;
specifier|private
name|Planner
name|planner
decl_stmt|;
specifier|private
name|MyDataContext
name|dataContext
decl_stmt|;
comment|/** Implementation of {@link DataContext} for executing queries without a    * connection. */
specifier|private
class|class
name|MyDataContext
implements|implements
name|DataContext
block|{
specifier|private
specifier|final
name|Planner
name|planner
decl_stmt|;
name|MyDataContext
parameter_list|(
name|Planner
name|planner
parameter_list|)
block|{
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
block|}
specifier|public
name|SchemaPlus
name|getRootSchema
parameter_list|()
block|{
return|return
name|rootSchema
return|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
operator|(
name|JavaTypeFactory
operator|)
name|planner
operator|.
name|getTypeFactory
argument_list|()
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Fluent class that contains information necessary to run a test. */
specifier|private
specifier|static
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
name|MyDataContext
name|dataContext
decl_stmt|;
specifier|private
specifier|final
name|Planner
name|planner
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|project
decl_stmt|;
name|Sql
parameter_list|(
name|String
name|sql
parameter_list|,
name|MyDataContext
name|dataContext
parameter_list|,
name|Planner
name|planner
parameter_list|,
name|boolean
name|project
parameter_list|)
block|{
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|dataContext
operator|=
name|dataContext
expr_stmt|;
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
name|this
operator|.
name|project
operator|=
name|project
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"SameParameterValue"
argument_list|)
name|Sql
name|withProject
parameter_list|(
name|boolean
name|project
parameter_list|)
block|{
return|return
operator|new
name|Sql
argument_list|(
name|sql
argument_list|,
name|dataContext
argument_list|,
name|planner
argument_list|,
name|project
argument_list|)
return|;
block|}
comment|/** Interprets the sql and checks result with specified rows, ordered. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedReturnValue"
argument_list|)
name|Sql
name|returnsRows
parameter_list|(
name|String
modifier|...
name|rows
parameter_list|)
block|{
return|return
name|returnsRows
argument_list|(
literal|false
argument_list|,
name|rows
argument_list|)
return|;
block|}
comment|/** Interprets the sql and checks result with specified rows, unordered. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedReturnValue"
argument_list|)
name|Sql
name|returnsRowsUnordered
parameter_list|(
name|String
modifier|...
name|rows
parameter_list|)
block|{
return|return
name|returnsRows
argument_list|(
literal|true
argument_list|,
name|rows
argument_list|)
return|;
block|}
comment|/** Interprets the sql and checks result with specified rows. */
specifier|private
name|Sql
name|returnsRows
parameter_list|(
name|boolean
name|unordered
parameter_list|,
name|String
index|[]
name|rows
parameter_list|)
block|{
try|try
block|{
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|SqlNode
name|validate
init|=
name|planner
operator|.
name|validate
argument_list|(
name|parse
argument_list|)
decl_stmt|;
specifier|final
name|RelRoot
name|root
init|=
name|planner
operator|.
name|rel
argument_list|(
name|validate
argument_list|)
decl_stmt|;
name|RelNode
name|convert
init|=
name|project
condition|?
name|root
operator|.
name|project
argument_list|()
else|:
name|root
operator|.
name|rel
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
name|dataContext
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
argument_list|,
name|unordered
argument_list|,
name|rows
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
catch|catch
parameter_list|(
name|ValidationException
decl||
name|SqlParseException
decl||
name|RelConversionException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|throwAsRuntime
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/** Creates a {@link Sql}. */
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
name|dataContext
argument_list|,
name|planner
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|private
name|void
name|reset
parameter_list|()
block|{
name|rootSchema
operator|=
name|Frameworks
operator|.
name|createRootSchema
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|FrameworkConfig
name|config
init|=
name|Frameworks
operator|.
name|newConfigBuilder
argument_list|()
operator|.
name|parserConfig
argument_list|(
name|SqlParser
operator|.
name|Config
operator|.
name|DEFAULT
argument_list|)
operator|.
name|defaultSchema
argument_list|(
name|CalciteAssert
operator|.
name|addSchema
argument_list|(
name|rootSchema
argument_list|,
name|CalciteAssert
operator|.
name|SchemaSpec
operator|.
name|HR
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|planner
operator|=
name|Frameworks
operator|.
name|getPlanner
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|dataContext
operator|=
operator|new
name|MyDataContext
argument_list|(
name|planner
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeEach
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|reset
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterEach
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|rootSchema
operator|=
literal|null
expr_stmt|;
name|planner
operator|=
literal|null
expr_stmt|;
name|dataContext
operator|=
literal|null
expr_stmt|;
block|}
comment|/** Tests executing a simple plan using an interpreter. */
annotation|@
name|Test
name|void
name|testInterpretProjectFilterValues
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select y, x\n"
operator|+
literal|"from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)\n"
operator|+
literal|"where x> 1"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[b, 2]"
argument_list|,
literal|"[c, 3]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests NULLIF operator. (NULLIF is an example of an operator that    * is implemented by expanding to simpler operators - in this case, CASE.) */
annotation|@
name|Test
name|void
name|testInterpretNullif
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select nullif(x, 2), x\n"
operator|+
literal|"from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1, 1]"
argument_list|,
literal|"[null, 2]"
argument_list|,
literal|"[3, 3]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests a plan where the sort field is projected away. */
annotation|@
name|Test
name|void
name|testInterpretOrder
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select y\n"
operator|+
literal|"from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)\n"
operator|+
literal|"order by -x"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withProject
argument_list|(
literal|true
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[c]"
argument_list|,
literal|"[b]"
argument_list|,
literal|"[a]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretMultiset
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select multiset['a', 'b', 'c']"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|withProject
argument_list|(
literal|true
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[[a, b, c]]"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertRows
parameter_list|(
name|Interpreter
name|interpreter
parameter_list|,
name|boolean
name|unordered
parameter_list|,
name|String
modifier|...
name|rows
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|Object
index|[]
name|row
range|:
name|interpreter
control|)
block|{
name|list
operator|.
name|add
argument_list|(
name|Arrays
operator|.
name|toString
argument_list|(
name|row
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|expected
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|rows
argument_list|)
decl_stmt|;
if|if
condition|(
name|unordered
condition|)
block|{
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|list
argument_list|,
name|equalTo
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests executing a simple plan using an interpreter. */
annotation|@
name|Test
name|void
name|testInterpretTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from \"hr\".\"emps\" order by \"empid\""
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[100, 10, Bill, 10000.0, 1000]"
argument_list|,
literal|"[110, 10, Theodore, 11500.0, 250]"
argument_list|,
literal|"[150, 10, Sebastian, 7000.0, null]"
argument_list|,
literal|"[200, 20, Eric, 8000.0, 500]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests executing a plan on a    * {@link org.apache.calcite.schema.ScannableTable} using an interpreter. */
annotation|@
name|Test
name|void
name|testInterpretScannableTable
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"beatles"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|BeatlesTable
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from \"beatles\" order by \"i\""
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[4, John]"
argument_list|,
literal|"[4, Paul]"
argument_list|,
literal|"[5, Ringo]"
argument_list|,
literal|"[6, George]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateCount
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"beatles"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|BeatlesTable
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select count(*) from \"beatles\""
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[4]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMax
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"beatles"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|BeatlesTable
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select max(\"i\") from \"beatles\""
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[6]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateMin
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"beatles"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|BeatlesTable
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select min(\"i\") from \"beatles\""
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[4]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateGroup
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"beatles"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|BeatlesTable
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select \"j\", count(*) from \"beatles\" group by \"j\""
argument_list|)
operator|.
name|returnsRowsUnordered
argument_list|(
literal|"[George, 1]"
argument_list|,
literal|"[Paul, 1]"
argument_list|,
literal|"[John, 1]"
argument_list|,
literal|"[Ringo, 1]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testAggregateGroupFilter
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"beatles"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|BeatlesTable
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select \"j\",\n"
operator|+
literal|"  count(*) filter (where char_length(\"j\")> 4)\n"
operator|+
literal|"from \"beatles\" group by \"j\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRowsUnordered
argument_list|(
literal|"[George, 1]"
argument_list|,
literal|"[Paul, 0]"
argument_list|,
literal|"[John, 0]"
argument_list|,
literal|"[Ringo, 1]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests executing a plan on a single-column    * {@link org.apache.calcite.schema.ScannableTable} using an interpreter. */
annotation|@
name|Test
name|void
name|testInterpretSimpleScannableTable
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"simple"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|SimpleTable
argument_list|()
argument_list|)
expr_stmt|;
name|sql
argument_list|(
literal|"select * from \"simple\" limit 2"
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[0]"
argument_list|,
literal|"[10]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests executing a UNION ALL query using an interpreter. */
annotation|@
name|Test
name|void
name|testInterpretUnionAll
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"simple"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|SimpleTable
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from \"simple\"\n"
operator|+
literal|"union all\n"
operator|+
literal|"select * from \"simple\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRowsUnordered
argument_list|(
literal|"[0]"
argument_list|,
literal|"[10]"
argument_list|,
literal|"[20]"
argument_list|,
literal|"[30]"
argument_list|,
literal|"[0]"
argument_list|,
literal|"[10]"
argument_list|,
literal|"[20]"
argument_list|,
literal|"[30]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests executing a UNION query using an interpreter. */
annotation|@
name|Test
name|void
name|testInterpretUnion
parameter_list|()
block|{
name|rootSchema
operator|.
name|add
argument_list|(
literal|"simple"
argument_list|,
operator|new
name|ScannableTableTest
operator|.
name|SimpleTable
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sql
init|=
literal|"select * from \"simple\"\n"
operator|+
literal|"union\n"
operator|+
literal|"select * from \"simple\""
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRowsUnordered
argument_list|(
literal|"[0]"
argument_list|,
literal|"[10]"
argument_list|,
literal|"[20]"
argument_list|,
literal|"[30]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretUnionWithNullValue
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1))),\n"
operator|+
literal|"(cast(NULL as int), cast(NULL as varchar(1)))) as t(x, y))\n"
operator|+
literal|"union\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1)))) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[null, null]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretUnionAllWithNullValue
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1))),\n"
operator|+
literal|"(cast(NULL as int), cast(NULL as varchar(1)))) as t(x, y))\n"
operator|+
literal|"union all\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1)))) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[null, null]"
argument_list|,
literal|"[null, null]"
argument_list|,
literal|"[null, null]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretIntersect
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y))\n"
operator|+
literal|"intersect\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'c'), (4, 'x')) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1, a]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretIntersectAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y))\n"
operator|+
literal|"intersect all\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'c'), (4, 'x')) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1, a]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretIntersectWithNullValue
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1))),\n"
operator|+
literal|" (cast(NULL as int), cast(NULL as varchar(1)))) as t(x, y))\n"
operator|+
literal|"intersect\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1)))) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[null, null]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretIntersectAllWithNullValue
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1))),\n"
operator|+
literal|" (cast(NULL as int), cast(NULL as varchar(1)))) as t(x, y))\n"
operator|+
literal|"intersect all\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1)))) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[null, null]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretMinus
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'b'), (2, 'b'), (3, 'c')) as t(x, y))\n"
operator|+
literal|"except\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'c'), (4, 'x')) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[2, b]"
argument_list|,
literal|"[3, c]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDuplicateRowInterpretMinus
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (2, 'b'), (2, 'b')) as t(x, y))\n"
operator|+
literal|"except\n"
operator|+
literal|"(select x, y from (values (2, 'b')) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretMinusAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'b'), (2, 'b'), (3, 'c')) as t(x, y))\n"
operator|+
literal|"except all\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'c'), (4, 'x')) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[2, b]"
argument_list|,
literal|"[2, b]"
argument_list|,
literal|"[3, c]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDuplicateRowInterpretMinusAll
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (2, 'b'), (2, 'b')) as t(x, y))\n"
operator|+
literal|"except all\n"
operator|+
literal|"(select x, y from (values (2, 'b')) as t2(x, y))\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[2, b]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretMinusAllWithNullValue
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1))),\n"
operator|+
literal|" (cast(NULL as int), cast(NULL as varchar(1)))) as t(x, y))\n"
operator|+
literal|"except all\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1)))) as t2(x, y))\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[null, null]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretMinusWithNullValue
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1))),\n"
operator|+
literal|"(cast(NULL as int), cast(NULL as varchar(1)))) as t(x, y))\n"
operator|+
literal|"except\n"
operator|+
literal|"(select x, y from (values (cast(NULL as int), cast(NULL as varchar(1)))) as t2(x, y))\n"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretInnerJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)) t\n"
operator|+
literal|"join\n"
operator|+
literal|"(select x, y from (values (1, 'd'), (2, 'c')) as t2(x, y)) t2\n"
operator|+
literal|"on t.x = t2.x"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1, a, 1, d]"
argument_list|,
literal|"[2, b, 2, c]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretLeftOutJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)) t\n"
operator|+
literal|"left join\n"
operator|+
literal|"(select x, y from (values (1, 'd')) as t2(x, y)) t2\n"
operator|+
literal|"on t.x = t2.x"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1, a, 1, d]"
argument_list|,
literal|"[2, b, null, null]"
argument_list|,
literal|"[3, c, null, null]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretRightOutJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (1, 'd')) as t2(x, y)) t2\n"
operator|+
literal|"right join\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)) t\n"
operator|+
literal|"on t2.x = t.x"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1, d, 1, a]"
argument_list|,
literal|"[null, null, 2, b]"
argument_list|,
literal|"[null, null, 3, c]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretSemanticSemiJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, y from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)\n"
operator|+
literal|"where x in\n"
operator|+
literal|"(select x from (values (1, 'd'), (3, 'g')) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1, a]"
argument_list|,
literal|"[3, c]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretSemiJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, y from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)\n"
operator|+
literal|"where x in\n"
operator|+
literal|"(select x from (values (1, 'd'), (3, 'g')) as t2(x, y))"
decl_stmt|;
try|try
block|{
name|SqlNode
name|validate
init|=
name|planner
operator|.
name|validate
argument_list|(
name|planner
operator|.
name|parse
argument_list|(
name|sql
argument_list|)
argument_list|)
decl_stmt|;
name|RelNode
name|convert
init|=
name|planner
operator|.
name|rel
argument_list|(
name|validate
argument_list|)
operator|.
name|rel
decl_stmt|;
specifier|final
name|HepProgram
name|program
init|=
operator|new
name|HepProgramBuilder
argument_list|()
operator|.
name|addRuleInstance
argument_list|(
name|CoreRules
operator|.
name|PROJECT_TO_SEMI_JOIN
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|HepPlanner
name|hepPlanner
init|=
operator|new
name|HepPlanner
argument_list|(
name|program
argument_list|)
decl_stmt|;
name|hepPlanner
operator|.
name|setRoot
argument_list|(
name|convert
argument_list|)
expr_stmt|;
specifier|final
name|RelNode
name|relNode
init|=
name|hepPlanner
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
name|dataContext
argument_list|,
name|relNode
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
argument_list|,
literal|true
argument_list|,
literal|"[1, a]"
argument_list|,
literal|"[3, c]"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ValidationException
decl||
name|SqlParseException
decl||
name|RelConversionException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|throwAsRuntime
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
name|void
name|testInterpretAntiJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, y from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)\n"
operator|+
literal|"where x not in\n"
operator|+
literal|"(select x from (values (1, 'd')) as t2(x, y))"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[2, b]"
argument_list|,
literal|"[3, c]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretFullJoin
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from\n"
operator|+
literal|"(select x, y from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)) t\n"
operator|+
literal|"full join\n"
operator|+
literal|"(select x, y from (values (1, 'd'), (2, 'c'), (4, 'x')) as t2(x, y)) t2\n"
operator|+
literal|"on t.x = t2.x"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1, a, 1, d]"
argument_list|,
literal|"[2, b, 2, c]"
argument_list|,
literal|"[3, c, null, null]"
argument_list|,
literal|"[null, null, 4, x]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretDecimalAggregate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select x, min(y), max(y), sum(y), avg(y)\n"
operator|+
literal|"from (values ('a', -1.2), ('a', 2.3), ('a', 15)) as t(x, y)\n"
operator|+
literal|"group by x"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[a, -1.2, 15.0, 16.1, 5.366666666666667]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testInterpretUnnest
parameter_list|()
block|{
name|sql
argument_list|(
literal|"select * from unnest(array[1, 2])"
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[1]"
argument_list|,
literal|"[2]"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(multiset[1, 2])"
argument_list|)
operator|.
name|returnsRowsUnordered
argument_list|(
literal|"[1]"
argument_list|,
literal|"[2]"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(map['a', 12])"
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[a, 12]"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(\n"
operator|+
literal|"select * from (values array[10, 20], array[30, 40]))\n"
operator|+
literal|"with ordinality as t(i, o)"
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[10, 1]"
argument_list|,
literal|"[20, 2]"
argument_list|,
literal|"[30, 1]"
argument_list|,
literal|"[40, 2]"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(map['a', 12, 'b', 13]) with ordinality as t(a, b, o)"
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[a, 12, 1]"
argument_list|,
literal|"[b, 13, 2]"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(\n"
operator|+
literal|"select * from (values multiset[10, 20], multiset[30, 40]))\n"
operator|+
literal|"with ordinality as t(i, o)"
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[10, 1]"
argument_list|,
literal|"[20, 2]"
argument_list|,
literal|"[30, 1]"
argument_list|,
literal|"[40, 2]"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(array[cast(null as integer), 10])"
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|"[null]"
argument_list|,
literal|"[10]"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(map[cast(null as integer), 10, 10, cast(null as integer)])"
argument_list|)
operator|.
name|returnsRowsUnordered
argument_list|(
literal|"[null, 10]"
argument_list|,
literal|"[10, null]"
argument_list|)
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(multiset[cast(null as integer), 10])"
argument_list|)
operator|.
name|returnsRowsUnordered
argument_list|(
literal|"[null]"
argument_list|,
literal|"[10]"
argument_list|)
expr_stmt|;
try|try
block|{
name|reset
argument_list|()
expr_stmt|;
name|sql
argument_list|(
literal|"select * from unnest(cast(null as int array))"
argument_list|)
operator|.
name|returnsRows
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|"NULL value for unnest."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

