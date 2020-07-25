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
name|adapter
operator|.
name|enumerable
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|tree
operator|.
name|ConstantExpression
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
name|tree
operator|.
name|Expressions
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
name|tree
operator|.
name|FunctionExpression
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
name|tree
operator|.
name|Node
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
name|tree
operator|.
name|ParameterExpression
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
name|tree
operator|.
name|UnaryExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|BaseMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Description
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
name|lang
operator|.
name|reflect
operator|.
name|Type
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
name|HashSet
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
name|Objects
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
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
comment|/**  * Test for  * {@link org.apache.calcite.adapter.enumerable.EnumerableRelImplementor.TypeFinder}.  */
end_comment

begin_class
class|class
name|TypeFinderTest
block|{
annotation|@
name|Test
name|void
name|testConstantExpression
parameter_list|()
block|{
name|ConstantExpression
name|expr
init|=
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|,
name|Integer
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertJavaCodeContains
argument_list|(
literal|"(Integer) null\n"
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|assertTypeContains
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|expr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testConvertExpression
parameter_list|()
block|{
name|UnaryExpression
name|expr
init|=
name|Expressions
operator|.
name|convert_
argument_list|(
name|Expressions
operator|.
name|new_
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|Object
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertJavaCodeContains
argument_list|(
literal|"(Object) new String()\n"
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|assertTypeContains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|expr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFunctionExpression1
parameter_list|()
block|{
name|ParameterExpression
name|param
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"input"
argument_list|)
decl_stmt|;
name|FunctionExpression
name|expr
init|=
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|Expressions
operator|.
name|block
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|param
argument_list|)
argument_list|)
argument_list|,
name|param
argument_list|)
decl_stmt|;
name|assertJavaCodeContains
argument_list|(
literal|"new org.apache.calcite.linq4j.function.Function1() {\n"
operator|+
literal|"  public String apply(String input) {\n"
operator|+
literal|"    return input;\n"
operator|+
literal|"  }\n"
operator|+
literal|"  public Object apply(Object input) {\n"
operator|+
literal|"    return apply(\n"
operator|+
literal|"      (String) input);\n"
operator|+
literal|"  }\n"
operator|+
literal|"}\n"
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|assertTypeContains
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|expr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testFunctionExpression2
parameter_list|()
block|{
name|FunctionExpression
name|expr
init|=
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|Expressions
operator|.
name|block
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|1L
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Expressions
operator|.
name|parameter
argument_list|(
name|String
operator|.
name|class
argument_list|,
literal|"input"
argument_list|)
argument_list|)
decl_stmt|;
name|assertJavaCodeContains
argument_list|(
literal|"new org.apache.calcite.linq4j.function.Function1() {\n"
operator|+
literal|"  public Long apply(String input) {\n"
operator|+
literal|"    return Long.valueOf(1L);\n"
operator|+
literal|"  }\n"
operator|+
literal|"  public Object apply(Object input) {\n"
operator|+
literal|"    return apply(\n"
operator|+
literal|"      (String) input);\n"
operator|+
literal|"  }\n"
operator|+
literal|"}\n"
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|assertTypeContains
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Long
operator|.
name|class
argument_list|)
argument_list|,
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertJavaCodeContains
parameter_list|(
name|String
name|expected
parameter_list|,
name|Node
name|node
parameter_list|)
block|{
name|assertJavaCodeContains
argument_list|(
name|expected
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertJavaCodeContains
parameter_list|(
name|String
name|expected
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|)
block|{
specifier|final
name|String
name|javaCode
init|=
name|Expressions
operator|.
name|toString
argument_list|(
name|nodes
argument_list|,
literal|"\n"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|javaCode
argument_list|,
name|containsString
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertTypeContains
parameter_list|(
name|Type
name|expectedType
parameter_list|,
name|Node
name|node
parameter_list|)
block|{
name|assertTypeContains
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|expectedType
argument_list|)
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertTypeContains
parameter_list|(
name|List
argument_list|<
name|Type
argument_list|>
name|expectedType
parameter_list|,
name|Node
name|node
parameter_list|)
block|{
name|assertTypeContains
argument_list|(
name|expectedType
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertTypeContains
parameter_list|(
name|List
argument_list|<
name|Type
argument_list|>
name|expectedTypes
parameter_list|,
name|List
argument_list|<
name|Node
argument_list|>
name|nodes
parameter_list|)
block|{
specifier|final
name|HashSet
argument_list|<
name|Type
argument_list|>
name|types
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|EnumerableRelImplementor
operator|.
name|TypeFinder
name|typeFinder
init|=
operator|new
name|EnumerableRelImplementor
operator|.
name|TypeFinder
argument_list|(
name|types
argument_list|)
decl_stmt|;
for|for
control|(
name|Node
name|node
range|:
name|nodes
control|)
block|{
name|node
operator|.
name|accept
argument_list|(
name|typeFinder
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|types
argument_list|,
operator|new
name|BaseMatcher
argument_list|<
name|HashSet
argument_list|<
name|Type
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|matches
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|Type
argument_list|>
name|actual
init|=
operator|(
name|HashSet
argument_list|<
name|Type
argument_list|>
operator|)
name|o
decl_stmt|;
return|return
name|actual
operator|.
name|containsAll
argument_list|(
name|expectedTypes
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|describeTo
parameter_list|(
name|Description
name|description
parameter_list|)
block|{
name|description
operator|.
name|appendText
argument_list|(
literal|"Expected a set of types containing all of: "
argument_list|)
operator|.
name|appendText
argument_list|(
name|Objects
operator|.
name|toString
argument_list|(
name|expectedTypes
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

