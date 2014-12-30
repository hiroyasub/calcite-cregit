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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Unit tests for {@link org.apache.calcite.interpreter.Interpreter}.  */
end_comment

begin_class
specifier|public
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
specifier|public
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
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
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
annotation|@
name|Before
specifier|public
name|void
name|setUp
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
block|}
annotation|@
name|After
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
block|}
comment|/** Tests executing a simple plan using an interpreter. */
annotation|@
name|Test
specifier|public
name|void
name|testInterpretProjectFilterValues
parameter_list|()
throws|throws
name|Exception
block|{
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select y, x\n"
operator|+
literal|"from (values (1, 'a'), (2, 'b'), (3, 'c')) as t(x, y)\n"
operator|+
literal|"where x> 1"
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
name|RelNode
name|convert
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
literal|null
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
argument_list|,
literal|"[b, 2]"
argument_list|,
literal|"[c, 3]"
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
name|String
modifier|...
name|rows
parameter_list|)
block|{
name|assertRows
argument_list|(
name|interpreter
argument_list|,
literal|false
argument_list|,
name|rows
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|assertRowsUnordered
parameter_list|(
name|Interpreter
name|interpreter
parameter_list|,
name|String
modifier|...
name|rows
parameter_list|)
block|{
name|assertRows
argument_list|(
name|interpreter
argument_list|,
literal|true
argument_list|,
name|rows
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
name|Lists
operator|.
name|newArrayList
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
specifier|public
name|void
name|testInterpretTable
parameter_list|()
throws|throws
name|Exception
block|{
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"hr\".\"emps\" order by \"empid\""
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
name|RelNode
name|convert
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
operator|new
name|MyDataContext
argument_list|(
name|planner
argument_list|)
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
argument_list|,
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
specifier|public
name|void
name|testInterpretScannableTable
parameter_list|()
throws|throws
name|Exception
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
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"beatles\" order by \"i\""
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
name|RelNode
name|convert
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
operator|new
name|MyDataContext
argument_list|(
name|planner
argument_list|)
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
argument_list|,
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
specifier|public
name|void
name|testAggregate
parameter_list|()
throws|throws
name|Exception
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
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select  count(*) from \"beatles\""
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
name|RelNode
name|convert
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
operator|new
name|MyDataContext
argument_list|(
name|planner
argument_list|)
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
argument_list|,
literal|"[4]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAggregateGroup
parameter_list|()
throws|throws
name|Exception
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
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select \"j\", count(*) from \"beatles\" group by \"j\""
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
name|RelNode
name|convert
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
operator|new
name|MyDataContext
argument_list|(
name|planner
argument_list|)
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRowsUnordered
argument_list|(
name|interpreter
argument_list|,
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
comment|/** Tests executing a plan on a single-column    * {@link org.apache.calcite.schema.ScannableTable} using an interpreter. */
annotation|@
name|Test
specifier|public
name|void
name|testInterpretSimpleScannableTable
parameter_list|()
throws|throws
name|Exception
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
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"simple\" limit 2"
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
name|RelNode
name|convert
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
operator|new
name|MyDataContext
argument_list|(
name|planner
argument_list|)
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
argument_list|,
literal|"[0]"
argument_list|,
literal|"[10]"
argument_list|)
expr_stmt|;
block|}
comment|/** Tests executing a UNION ALL query using an interpreter. */
annotation|@
name|Test
specifier|public
name|void
name|testInterpretUnionAll
parameter_list|()
throws|throws
name|Exception
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
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"simple\"\n"
operator|+
literal|"union all\n"
operator|+
literal|"select * from \"simple\"\n"
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
name|RelNode
name|convert
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
operator|new
name|MyDataContext
argument_list|(
name|planner
argument_list|)
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
argument_list|,
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
specifier|public
name|void
name|testInterpretUnion
parameter_list|()
throws|throws
name|Exception
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
name|SqlNode
name|parse
init|=
name|planner
operator|.
name|parse
argument_list|(
literal|"select * from \"simple\"\n"
operator|+
literal|"union\n"
operator|+
literal|"select * from \"simple\"\n"
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
name|RelNode
name|convert
init|=
name|planner
operator|.
name|convert
argument_list|(
name|validate
argument_list|)
decl_stmt|;
specifier|final
name|Interpreter
name|interpreter
init|=
operator|new
name|Interpreter
argument_list|(
operator|new
name|MyDataContext
argument_list|(
name|planner
argument_list|)
argument_list|,
name|convert
argument_list|)
decl_stmt|;
name|assertRows
argument_list|(
name|interpreter
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
block|}
end_class

begin_comment
comment|// End InterpreterTest.java
end_comment

end_unit

