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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|SqlOperator
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
name|StringAndPos
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
name|ResultCheckers
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
name|SqlOperatorFixture
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
name|SqlTests
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
name|SqlValidatorTester
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
name|util
operator|.
name|JdbcType
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
name|UnaryOperator
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
name|sql
operator|.
name|test
operator|.
name|ResultCheckers
operator|.
name|isNullValue
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
name|sql
operator|.
name|test
operator|.
name|ResultCheckers
operator|.
name|isSingle
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

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
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
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertEquals
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
name|assertNotNull
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
comment|/**  * Implementation of {@link SqlOperatorFixture}.  */
end_comment

begin_class
class|class
name|SqlOperatorFixtureImpl
implements|implements
name|SqlOperatorFixture
block|{
specifier|public
specifier|static
specifier|final
name|SqlOperatorFixtureImpl
name|DEFAULT
init|=
operator|new
name|SqlOperatorFixtureImpl
argument_list|(
name|SqlTestFactory
operator|.
name|INSTANCE
argument_list|,
name|SqlValidatorTester
operator|.
name|DEFAULT
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|SqlTestFactory
name|factory
decl_stmt|;
specifier|private
specifier|final
name|SqlTester
name|tester
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|brokenTestsEnabled
decl_stmt|;
name|SqlOperatorFixtureImpl
parameter_list|(
name|SqlTestFactory
name|factory
parameter_list|,
name|SqlTester
name|tester
parameter_list|,
name|boolean
name|brokenTestsEnabled
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
name|brokenTestsEnabled
operator|=
name|brokenTestsEnabled
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|SqlTestFactory
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlTester
name|getTester
parameter_list|()
block|{
return|return
name|tester
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperatorFixtureImpl
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
name|SqlOperatorFixtureImpl
argument_list|(
name|factory
argument_list|,
name|tester
argument_list|,
name|brokenTestsEnabled
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperatorFixture
name|withTester
parameter_list|(
name|UnaryOperator
argument_list|<
name|SqlTester
argument_list|>
name|transform
parameter_list|)
block|{
specifier|final
name|SqlTester
name|tester
init|=
name|transform
operator|.
name|apply
argument_list|(
name|this
operator|.
name|tester
argument_list|)
decl_stmt|;
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
name|SqlOperatorFixtureImpl
argument_list|(
name|factory
argument_list|,
name|tester
argument_list|,
name|brokenTestsEnabled
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|brokenTestsEnabled
parameter_list|()
block|{
return|return
name|brokenTestsEnabled
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperatorFixture
name|withBrokenTestsEnabled
parameter_list|(
name|boolean
name|brokenTestsEnabled
parameter_list|)
block|{
if|if
condition|(
name|brokenTestsEnabled
operator|==
name|this
operator|.
name|brokenTestsEnabled
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|SqlOperatorFixtureImpl
argument_list|(
name|factory
argument_list|,
name|tester
argument_list|,
name|brokenTestsEnabled
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlOperatorFixture
name|setFor
parameter_list|(
name|SqlOperator
name|operator
parameter_list|,
name|VmName
modifier|...
name|unimplementedVmNames
parameter_list|)
block|{
return|return
name|this
return|;
block|}
name|SqlNode
name|parseAndValidate
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
name|SqlNode
name|sqlNode
decl_stmt|;
try|try
block|{
name|sqlNode
operator|=
name|tester
operator|.
name|parseQuery
argument_list|(
name|factory
argument_list|,
name|sql
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error while parsing query: "
operator|+
name|sql
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|validator
operator|.
name|validate
argument_list|(
name|sqlNode
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
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
name|validateAndThen
argument_list|(
name|factory
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
name|checkColumnTypeAction
argument_list|(
name|is
argument_list|(
name|expected
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkType
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|forEachQueryValidateAndThen
argument_list|(
name|StringAndPos
operator|.
name|of
argument_list|(
name|expression
argument_list|)
argument_list|,
name|checkColumnTypeAction
argument_list|(
name|is
argument_list|(
name|type
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|SqlTester
operator|.
name|ValidatedNodeConsumer
name|checkColumnTypeAction
parameter_list|(
name|Matcher
argument_list|<
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
return|return
parameter_list|(
name|sql
parameter_list|,
name|validator
parameter_list|,
name|validatedNode
parameter_list|)
lambda|->
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|validatedNode
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fields
operator|.
name|size
argument_list|()
argument_list|,
literal|"expected query to return 1 field"
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|actualType
init|=
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getType
argument_list|()
decl_stmt|;
name|String
name|actual
init|=
name|SqlTests
operator|.
name|getTypeString
argument_list|(
name|actualType
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|actual
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkQuery
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|factory
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|void
name|forEachQueryValidateAndThen
parameter_list|(
name|StringAndPos
name|expression
parameter_list|,
name|SqlTester
operator|.
name|ValidatedNodeConsumer
name|consumer
parameter_list|)
block|{
name|tester
operator|.
name|forEachQuery
argument_list|(
name|factory
argument_list|,
name|expression
operator|.
name|addCarets
argument_list|()
argument_list|,
name|query
lambda|->
name|tester
operator|.
name|validateAndThen
argument_list|(
name|factory
argument_list|,
name|StringAndPos
operator|.
name|of
argument_list|(
name|query
argument_list|)
argument_list|,
name|consumer
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|String
name|expectedError
parameter_list|,
name|boolean
name|runtime
parameter_list|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"values ("
operator|+
name|sap
operator|.
name|addCarets
argument_list|()
operator|+
literal|")"
decl_stmt|;
if|if
condition|(
name|runtime
condition|)
block|{
comment|// We need to test that the expression fails at runtime.
comment|// Ironically, that means that it must succeed at prepare time.
name|SqlValidator
name|validator
init|=
name|factory
operator|.
name|createValidator
argument_list|()
decl_stmt|;
name|SqlNode
name|n
init|=
name|parseAndValidate
argument_list|(
name|validator
argument_list|,
name|sql
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkQueryFails
argument_list|(
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
name|expectedError
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkQueryFails
parameter_list|(
name|StringAndPos
name|sap
parameter_list|,
name|String
name|expectedError
parameter_list|)
block|{
name|tester
operator|.
name|assertExceptionIsThrown
argument_list|(
name|factory
argument_list|,
name|sap
argument_list|,
name|expectedError
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkAggFails
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
name|inputValues
parameter_list|,
name|String
name|expectedError
parameter_list|,
name|boolean
name|runtime
parameter_list|)
block|{
specifier|final
name|String
name|sql
init|=
name|SqlTests
operator|.
name|generateAggQuery
argument_list|(
name|expr
argument_list|,
name|inputValues
argument_list|)
decl_stmt|;
if|if
condition|(
name|runtime
condition|)
block|{
name|SqlValidator
name|validator
init|=
name|factory
operator|.
name|createValidator
argument_list|()
decl_stmt|;
name|SqlNode
name|n
init|=
name|parseAndValidate
argument_list|(
name|validator
argument_list|,
name|sql
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|n
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|checkQueryFails
argument_list|(
name|StringAndPos
operator|.
name|of
argument_list|(
name|sql
argument_list|)
argument_list|,
name|expectedError
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkAgg
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
name|inputValues
parameter_list|,
name|SqlTester
operator|.
name|ResultChecker
name|checker
parameter_list|)
block|{
name|String
name|query
init|=
name|SqlTests
operator|.
name|generateAggQuery
argument_list|(
name|expr
argument_list|,
name|inputValues
argument_list|)
decl_stmt|;
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|query
argument_list|,
name|SqlTests
operator|.
name|ANY_TYPE_CHECKER
argument_list|,
name|checker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkAggWithMultipleArgs
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
index|[]
name|inputValues
parameter_list|,
name|SqlTester
operator|.
name|ResultChecker
name|resultChecker
parameter_list|)
block|{
name|String
name|query
init|=
name|SqlTests
operator|.
name|generateAggQueryWithMultipleArgs
argument_list|(
name|expr
argument_list|,
name|inputValues
argument_list|)
decl_stmt|;
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|query
argument_list|,
name|SqlTests
operator|.
name|ANY_TYPE_CHECKER
argument_list|,
name|resultChecker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkWinAgg
parameter_list|(
name|String
name|expr
parameter_list|,
name|String
index|[]
name|inputValues
parameter_list|,
name|String
name|windowSpec
parameter_list|,
name|String
name|type
parameter_list|,
name|SqlTester
operator|.
name|ResultChecker
name|resultChecker
parameter_list|)
block|{
name|String
name|query
init|=
name|SqlTests
operator|.
name|generateWinAggQuery
argument_list|(
name|expr
argument_list|,
name|windowSpec
argument_list|,
name|inputValues
argument_list|)
decl_stmt|;
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|query
argument_list|,
name|SqlTests
operator|.
name|ANY_TYPE_CHECKER
argument_list|,
name|resultChecker
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkScalar
parameter_list|(
name|String
name|expression
parameter_list|,
name|SqlTester
operator|.
name|TypeChecker
name|typeChecker
parameter_list|,
name|SqlTester
operator|.
name|ResultChecker
name|resultChecker
parameter_list|)
block|{
name|tester
operator|.
name|forEachQuery
argument_list|(
name|factory
argument_list|,
name|expression
argument_list|,
name|sql
lambda|->
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|sql
argument_list|,
name|typeChecker
argument_list|,
name|resultChecker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkScalarExact
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedType
parameter_list|,
name|SqlTester
operator|.
name|ResultChecker
name|resultChecker
parameter_list|)
block|{
specifier|final
name|SqlTester
operator|.
name|TypeChecker
name|typeChecker
init|=
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
name|expectedType
argument_list|)
decl_stmt|;
name|tester
operator|.
name|forEachQuery
argument_list|(
name|factory
argument_list|,
name|expression
argument_list|,
name|sql
lambda|->
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|sql
argument_list|,
name|typeChecker
argument_list|,
name|resultChecker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkScalarApprox
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedType
parameter_list|,
name|Object
name|result
parameter_list|)
block|{
name|SqlTester
operator|.
name|TypeChecker
name|typeChecker
init|=
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
name|expectedType
argument_list|)
decl_stmt|;
specifier|final
name|SqlTester
operator|.
name|ResultChecker
name|checker
init|=
name|ResultCheckers
operator|.
name|createChecker
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|tester
operator|.
name|forEachQuery
argument_list|(
name|factory
argument_list|,
name|expression
argument_list|,
name|sql
lambda|->
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|sql
argument_list|,
name|typeChecker
argument_list|,
name|checker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkBoolean
parameter_list|(
name|String
name|expression
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|result
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|result
condition|)
block|{
name|checkNull
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|SqlTester
operator|.
name|ResultChecker
name|resultChecker
init|=
name|ResultCheckers
operator|.
name|createChecker
argument_list|(
name|is
argument_list|(
name|result
argument_list|)
argument_list|,
name|JdbcType
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
name|tester
operator|.
name|forEachQuery
argument_list|(
name|factory
argument_list|,
name|expression
argument_list|,
name|sql
lambda|->
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|sql
argument_list|,
name|SqlTests
operator|.
name|BOOLEAN_TYPE_CHECKER
argument_list|,
name|SqlTests
operator|.
name|ANY_PARAMETER_CHECKER
argument_list|,
name|resultChecker
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkString
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|result
parameter_list|,
name|String
name|expectedType
parameter_list|)
block|{
name|SqlTester
operator|.
name|TypeChecker
name|typeChecker
init|=
operator|new
name|SqlTests
operator|.
name|StringTypeChecker
argument_list|(
name|expectedType
argument_list|)
decl_stmt|;
name|SqlTester
operator|.
name|ResultChecker
name|resultChecker
init|=
name|isSingle
argument_list|(
name|result
argument_list|)
decl_stmt|;
name|tester
operator|.
name|forEachQuery
argument_list|(
name|factory
argument_list|,
name|expression
argument_list|,
name|sql
lambda|->
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|sql
argument_list|,
name|typeChecker
argument_list|,
name|resultChecker
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|checkNull
parameter_list|(
name|String
name|expression
parameter_list|)
block|{
name|tester
operator|.
name|forEachQuery
argument_list|(
name|factory
argument_list|,
name|expression
argument_list|,
name|sql
lambda|->
name|tester
operator|.
name|check
argument_list|(
name|factory
argument_list|,
name|sql
argument_list|,
name|SqlTests
operator|.
name|ANY_TYPE_CHECKER
argument_list|,
name|isNullValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

