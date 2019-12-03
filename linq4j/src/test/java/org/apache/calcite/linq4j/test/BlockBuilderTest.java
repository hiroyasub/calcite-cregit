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
name|linq4j
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
name|linq4j
operator|.
name|tree
operator|.
name|BinaryExpression
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
name|BlockBuilder
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
name|Expression
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
name|ExpressionType
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
name|OptimizeShuttle
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
name|Shuttle
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
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Function
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|test
operator|.
name|BlockBuilderBase
operator|.
name|FOUR
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|test
operator|.
name|BlockBuilderBase
operator|.
name|ONE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|test
operator|.
name|BlockBuilderBase
operator|.
name|TWO
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests BlockBuilder.  */
end_comment

begin_class
specifier|public
class|class
name|BlockBuilderTest
block|{
name|BlockBuilder
name|b
decl_stmt|;
annotation|@
name|BeforeEach
specifier|public
name|void
name|prepareBuilder
parameter_list|()
block|{
name|b
operator|=
operator|new
name|BlockBuilder
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testReuseExpressionsFromUpperLevel
parameter_list|()
block|{
name|Expression
name|x
init|=
name|b
operator|.
name|append
argument_list|(
literal|"x"
argument_list|,
name|Expressions
operator|.
name|add
argument_list|(
name|ONE
argument_list|,
name|TWO
argument_list|)
argument_list|)
decl_stmt|;
name|BlockBuilder
name|nested
init|=
operator|new
name|BlockBuilder
argument_list|(
literal|true
argument_list|,
name|b
argument_list|)
decl_stmt|;
name|Expression
name|y
init|=
name|nested
operator|.
name|append
argument_list|(
literal|"y"
argument_list|,
name|Expressions
operator|.
name|add
argument_list|(
name|ONE
argument_list|,
name|TWO
argument_list|)
argument_list|)
decl_stmt|;
name|nested
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|add
argument_list|(
name|y
argument_list|,
name|y
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|b
operator|.
name|add
argument_list|(
name|nested
operator|.
name|toBlock
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  final int x = 1 + 2;\n"
operator|+
literal|"  {\n"
operator|+
literal|"    return x + x;\n"
operator|+
literal|"  }\n"
operator|+
literal|"}\n"
argument_list|,
name|b
operator|.
name|toBlock
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTestCustomOptimizer
parameter_list|()
block|{
name|BlockBuilder
name|b
init|=
operator|new
name|BlockBuilder
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Shuttle
name|createOptimizeShuttle
parameter_list|()
block|{
return|return
operator|new
name|OptimizeShuttle
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Expression
name|visit
parameter_list|(
name|BinaryExpression
name|binary
parameter_list|,
name|Expression
name|expression0
parameter_list|,
name|Expression
name|expression1
parameter_list|)
block|{
if|if
condition|(
name|binary
operator|.
name|getNodeType
argument_list|()
operator|==
name|ExpressionType
operator|.
name|Add
operator|&&
name|ONE
operator|.
name|equals
argument_list|(
name|expression0
argument_list|)
operator|&&
name|TWO
operator|.
name|equals
argument_list|(
name|expression1
argument_list|)
condition|)
block|{
return|return
name|FOUR
return|;
block|}
return|return
name|super
operator|.
name|visit
argument_list|(
name|binary
argument_list|,
name|expression0
argument_list|,
name|expression1
argument_list|)
return|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|b
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|add
argument_list|(
name|ONE
argument_list|,
name|TWO
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n  return 4;\n}\n"
argument_list|,
name|b
operator|.
name|toBlock
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BlockBuilder
name|appendBlockWithSameVariable
parameter_list|(
name|Expression
name|initializer1
parameter_list|,
name|Expression
name|initializer2
parameter_list|)
block|{
name|BlockBuilder
name|outer
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|ParameterExpression
name|outerX
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|int
operator|.
name|class
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
name|outer
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
literal|0
argument_list|,
name|outerX
argument_list|,
name|initializer1
argument_list|)
argument_list|)
expr_stmt|;
name|outer
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|outerX
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BlockBuilder
name|inner
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|ParameterExpression
name|innerX
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|int
operator|.
name|class
argument_list|,
literal|"x"
argument_list|)
decl_stmt|;
name|inner
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|declare
argument_list|(
literal|0
argument_list|,
name|innerX
argument_list|,
name|initializer2
argument_list|)
argument_list|)
expr_stmt|;
name|inner
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|assign
argument_list|(
name|innerX
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|42
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|inner
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|innerX
argument_list|)
argument_list|)
expr_stmt|;
name|outer
operator|.
name|append
argument_list|(
literal|"x"
argument_list|,
name|inner
operator|.
name|toBlock
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|outer
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRenameVariablesWithEmptyInitializer
parameter_list|()
block|{
name|BlockBuilder
name|outer
init|=
name|appendBlockWithSameVariable
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  int x;\n"
operator|+
literal|"  x = 1;\n"
operator|+
literal|"  int x0;\n"
operator|+
literal|"  x0 = 42;\n"
operator|+
literal|"}\n"
argument_list|,
name|Expressions
operator|.
name|toString
argument_list|(
name|outer
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|,
literal|"x in the second block should be renamed to avoid name clash"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRenameVariablesWithInitializer
parameter_list|()
block|{
name|BlockBuilder
name|outer
init|=
name|appendBlockWithSameVariable
argument_list|(
name|Expressions
operator|.
name|constant
argument_list|(
literal|7
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|8
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  int x = 7;\n"
operator|+
literal|"  x = 1;\n"
operator|+
literal|"  int x0 = 8;\n"
operator|+
literal|"  x0 = 42;\n"
operator|+
literal|"}\n"
argument_list|,
name|Expressions
operator|.
name|toString
argument_list|(
name|outer
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|,
literal|"x in the second block should be renamed to avoid name clash"
argument_list|)
expr_stmt|;
block|}
comment|/**    * CALCITE-2413: RexToLixTranslator does not generate correct declaration of Methods with    * generic return types    */
annotation|@
name|Test
specifier|public
name|void
name|genericMethodCall
parameter_list|()
throws|throws
name|NoSuchMethodException
block|{
name|BlockBuilder
name|bb
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|bb
operator|.
name|append
argument_list|(
literal|"_i"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|Expressions
operator|.
name|new_
argument_list|(
name|Identity
operator|.
name|class
argument_list|)
argument_list|,
name|Identity
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"apply"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|"test"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  final Object _i = new org.apache.calcite.linq4j.test.BlockBuilderTest.Identity()"
operator|+
literal|".apply(\"test\");\n"
operator|+
literal|"}\n"
argument_list|,
name|Expressions
operator|.
name|toString
argument_list|(
name|bb
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** CALCITE-2611: unknown on one side of an or may lead to uncompilable code */
annotation|@
name|Test
specifier|public
name|void
name|testOptimizeBoxedFalseEqNull
parameter_list|()
block|{
name|BlockBuilder
name|outer
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|outer
operator|.
name|append
argument_list|(
name|Expressions
operator|.
name|equal
argument_list|(
name|OptimizeShuttle
operator|.
name|BOXED_FALSE_EXPR
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|null
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"{\n"
operator|+
literal|"  return false;\n"
operator|+
literal|"}\n"
argument_list|,
name|Expressions
operator|.
name|toString
argument_list|(
name|outer
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|,
literal|"Expected to optimize Boolean.FALSE = null to false"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Class with generics to validate if {@link Expressions#call(Method, Expression...)} works.    * @param<I> result type    */
specifier|static
class|class
name|Identity
parameter_list|<
name|I
parameter_list|>
implements|implements
name|Function
argument_list|<
name|I
argument_list|,
name|I
argument_list|>
block|{
annotation|@
name|Override
specifier|public
name|I
name|apply
parameter_list|(
name|I
name|i
parameter_list|)
block|{
return|return
name|i
return|;
block|}
block|}
block|}
end_class

end_unit

