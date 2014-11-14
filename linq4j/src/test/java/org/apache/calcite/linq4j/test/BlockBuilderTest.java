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
name|OptimizeVisitor
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
name|Visitor
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
name|Assert
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
name|Before
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
name|Visitor
name|createOptimizeVisitor
parameter_list|()
block|{
return|return
operator|new
name|OptimizeVisitor
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
block|}
end_class

begin_comment
comment|// End BlockBuilderTest.java
end_comment

end_unit

