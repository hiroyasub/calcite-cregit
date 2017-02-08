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
name|rex
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
name|avatica
operator|.
name|util
operator|.
name|ByteString
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
name|DateTimeUtils
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
name|RelOptCluster
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
name|RelOptSchema
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
name|RelDataTypeFactory
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
name|schema
operator|.
name|Schemas
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
name|server
operator|.
name|CalciteServerStatement
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
name|SqlBinaryOperator
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
name|SqlKind
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
name|fun
operator|.
name|SqlMonotonicBinaryOperator
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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|type
operator|.
name|InferTypes
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
name|type
operator|.
name|OperandTypes
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
name|type
operator|.
name|ReturnTypes
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
name|type
operator|.
name|SqlTypeName
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
name|util
operator|.
name|NlsString
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
name|Function
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
name|org
operator|.
name|junit
operator|.
name|Assert
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
name|math
operator|.
name|BigDecimal
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
name|Calendar
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
name|Random
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
name|CoreMatchers
operator|.
name|instanceOf
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
name|assertTrue
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link org.apache.calcite.rex.RexExecutorImpl}.  */
end_comment

begin_class
specifier|public
class|class
name|RexExecutorTest
block|{
specifier|public
name|RexExecutorTest
parameter_list|()
block|{
block|}
specifier|protected
name|void
name|check
parameter_list|(
specifier|final
name|Action
name|action
parameter_list|)
throws|throws
name|Exception
block|{
name|Frameworks
operator|.
name|withPrepare
argument_list|(
operator|new
name|Frameworks
operator|.
name|PrepareAction
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
specifier|public
name|Void
name|apply
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptSchema
name|relOptSchema
parameter_list|,
name|SchemaPlus
name|rootSchema
parameter_list|,
name|CalciteServerStatement
name|statement
parameter_list|)
block|{
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|cluster
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
name|DataContext
name|dataContext
init|=
name|Schemas
operator|.
name|createDataContext
argument_list|(
name|statement
operator|.
name|getConnection
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RexExecutorImpl
name|executor
init|=
operator|new
name|RexExecutorImpl
argument_list|(
name|dataContext
argument_list|)
decl_stmt|;
name|action
operator|.
name|check
argument_list|(
name|rexBuilder
argument_list|,
name|executor
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Tests an executor that uses variables stored in a {@link DataContext}.    * Can change the value of the variable and execute again. */
annotation|@
name|Test
specifier|public
name|void
name|testVariableExecution
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|Action
argument_list|()
block|{
specifier|public
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
block|{
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
literal|1
index|]
decl_stmt|;
specifier|final
name|DataContext
name|testContext
init|=
operator|new
name|TestDataContext
argument_list|(
name|values
argument_list|)
decl_stmt|;
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|varchar
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|integer
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
decl_stmt|;
comment|// Calcite is internally creating the input ref via a RexRangeRef
comment|// which eventually leads to a RexInputRef. So we are good.
specifier|final
name|RexInputRef
name|input
init|=
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|varchar
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|lengthArg
init|=
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|3
argument_list|,
name|integer
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|substr
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|,
name|input
argument_list|,
name|lengthArg
argument_list|)
decl_stmt|;
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|constExps
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|substr
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"someStr"
argument_list|,
name|varchar
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|RexExecutable
name|exec
init|=
name|executor
operator|.
name|getExecutable
argument_list|(
name|rexBuilder
argument_list|,
name|constExps
argument_list|,
name|rowType
argument_list|)
decl_stmt|;
name|exec
operator|.
name|setDataContext
argument_list|(
name|testContext
argument_list|)
expr_stmt|;
name|values
index|[
literal|0
index|]
operator|=
literal|"Hello World"
expr_stmt|;
name|Object
index|[]
name|result
init|=
name|exec
operator|.
name|execute
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|result
index|[
literal|0
index|]
operator|instanceof
name|String
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|result
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|"llo World"
argument_list|)
argument_list|)
expr_stmt|;
name|values
index|[
literal|0
index|]
operator|=
literal|"Calcite"
expr_stmt|;
name|result
operator|=
name|exec
operator|.
name|execute
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|result
index|[
literal|0
index|]
operator|instanceof
name|String
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
name|String
operator|)
name|result
index|[
literal|0
index|]
argument_list|,
name|equalTo
argument_list|(
literal|"lcite"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testConstant
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|Action
argument_list|()
block|{
specifier|public
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|reducedValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RexLiteral
name|ten
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|TEN
argument_list|)
decl_stmt|;
name|executor
operator|.
name|reduce
argument_list|(
name|rexBuilder
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RexNode
operator|>
name|of
argument_list|(
name|ten
argument_list|)
argument_list|,
name|reducedValues
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|RexLiteral
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|reducedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValue2
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
literal|10L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
comment|/** Reduces several expressions to constants. */
annotation|@
name|Test
specifier|public
name|void
name|testConstant2
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Same as testConstant; 10 -> 10
name|checkConstant
argument_list|(
literal|10L
argument_list|,
operator|new
name|Function
argument_list|<
name|RexBuilder
argument_list|,
name|RexNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RexNode
name|apply
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|TEN
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// 10 + 1 -> 11
name|checkConstant
argument_list|(
literal|11L
argument_list|,
operator|new
name|Function
argument_list|<
name|RexBuilder
argument_list|,
name|RexNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RexNode
name|apply
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|TEN
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// date 'today'<= date 'today' -> true
name|checkConstant
argument_list|(
literal|true
argument_list|,
operator|new
name|Function
argument_list|<
name|RexBuilder
argument_list|,
name|RexNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RexNode
name|apply
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|DateTimeUtils
operator|.
name|GMT_ZONE
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
name|calendar
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
name|calendar
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
comment|// date 'today'< date 'today' -> false
name|checkConstant
argument_list|(
literal|false
argument_list|,
operator|new
name|Function
argument_list|<
name|RexBuilder
argument_list|,
name|RexNode
argument_list|>
argument_list|()
block|{
specifier|public
name|RexNode
name|apply
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|Calendar
name|calendar
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|DateTimeUtils
operator|.
name|GMT_ZONE
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
name|calendar
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeDateLiteral
argument_list|(
name|calendar
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkConstant
parameter_list|(
specifier|final
name|Object
name|operand
parameter_list|,
specifier|final
name|Function
argument_list|<
name|RexBuilder
argument_list|,
name|RexNode
argument_list|>
name|function
parameter_list|)
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|Action
argument_list|()
block|{
specifier|public
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|reducedValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RexNode
name|expression
init|=
name|function
operator|.
name|apply
argument_list|(
name|rexBuilder
argument_list|)
decl_stmt|;
assert|assert
name|expression
operator|!=
literal|null
assert|;
name|executor
operator|.
name|reduce
argument_list|(
name|rexBuilder
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|expression
argument_list|)
argument_list|,
name|reducedValues
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|RexLiteral
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|reducedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValue2
argument_list|()
argument_list|,
name|equalTo
argument_list|(
name|operand
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSubstring
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|Action
argument_list|()
block|{
specifier|public
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|reducedValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|RexLiteral
name|hello
init|=
name|rexBuilder
operator|.
name|makeCharLiteral
argument_list|(
operator|new
name|NlsString
argument_list|(
literal|"Hello world!"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|plus
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
decl_stmt|;
name|RexLiteral
name|four
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|substring
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|,
name|hello
argument_list|,
name|plus
argument_list|,
name|four
argument_list|)
decl_stmt|;
name|executor
operator|.
name|reduce
argument_list|(
name|rexBuilder
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|substring
argument_list|,
name|plus
argument_list|)
argument_list|,
name|reducedValues
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|RexLiteral
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|reducedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValue2
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
literal|"ello"
argument_list|)
argument_list|)
expr_stmt|;
comment|// substring('Hello world!, 2, 4)
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|RexLiteral
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|reducedValues
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getValue2
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBinarySubstring
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|Action
argument_list|()
block|{
specifier|public
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|reducedValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// hello world! -> 48656c6c6f20776f726c6421
specifier|final
name|RexLiteral
name|binaryHello
init|=
name|rexBuilder
operator|.
name|makeBinaryLiteral
argument_list|(
operator|new
name|ByteString
argument_list|(
literal|"Hello world!"
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|plus
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
decl_stmt|;
name|RexLiteral
name|four
init|=
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|valueOf
argument_list|(
literal|4
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexNode
name|substring
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|SUBSTRING
argument_list|,
name|binaryHello
argument_list|,
name|plus
argument_list|,
name|four
argument_list|)
decl_stmt|;
name|executor
operator|.
name|reduce
argument_list|(
name|rexBuilder
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|substring
argument_list|,
name|plus
argument_list|)
argument_list|,
name|reducedValues
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|size
argument_list|()
argument_list|,
name|equalTo
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|RexLiteral
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|reducedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValue2
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
literal|"656c6c6f"
argument_list|)
argument_list|)
expr_stmt|;
comment|// substring('Hello world!, 2, 4)
name|assertThat
argument_list|(
name|reducedValues
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|instanceOf
argument_list|(
name|RexLiteral
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
operator|(
operator|(
name|RexLiteral
operator|)
name|reducedValues
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getValue2
argument_list|()
argument_list|,
name|equalTo
argument_list|(
operator|(
name|Object
operator|)
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeterministic1
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|Action
argument_list|()
block|{
specifier|public
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
block|{
specifier|final
name|RexNode
name|plus
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|RexUtil
operator|.
name|isDeterministic
argument_list|(
name|plus
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeterministic2
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|Action
argument_list|()
block|{
specifier|public
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
block|{
specifier|final
name|RexNode
name|plus
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|PLUS_RANDOM
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|RexUtil
operator|.
name|isDeterministic
argument_list|(
name|plus
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeterministic3
parameter_list|()
throws|throws
name|Exception
block|{
name|check
argument_list|(
operator|new
name|Action
argument_list|()
block|{
specifier|public
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
block|{
specifier|final
name|RexNode
name|plus
init|=
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|PLUS
argument_list|,
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|PLUS_RANDOM
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
argument_list|,
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|BigDecimal
operator|.
name|ONE
argument_list|)
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|RexUtil
operator|.
name|isDeterministic
argument_list|(
name|plus
argument_list|)
argument_list|,
name|equalTo
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|SqlBinaryOperator
name|PLUS_RANDOM
init|=
operator|new
name|SqlMonotonicBinaryOperator
argument_list|(
literal|"+"
argument_list|,
name|SqlKind
operator|.
name|PLUS
argument_list|,
literal|40
argument_list|,
literal|true
argument_list|,
name|ReturnTypes
operator|.
name|NULLABLE_SUM
argument_list|,
name|InferTypes
operator|.
name|FIRST_KNOWN
argument_list|,
name|OperandTypes
operator|.
name|PLUS_OPERATOR
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|isDeterministic
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
decl_stmt|;
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1009">[CALCITE-1009]    * SelfPopulatingList is not thread-safe</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testSelfPopulatingList
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Thread
argument_list|>
name|threads
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//noinspection MismatchedQueryAndUpdateOfCollection
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|RexSlot
operator|.
name|SelfPopulatingList
argument_list|(
literal|"$"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|threads
operator|.
name|add
argument_list|(
operator|new
name|Thread
argument_list|()
block|{
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|1000
condition|;
name|j
operator|++
control|)
block|{
comment|// Random numbers between 0 and ~1m, smaller values more common
specifier|final
name|int
name|index
init|=
name|random
operator|.
name|nextInt
argument_list|(
literal|1234567
argument_list|)
operator|>>
name|random
operator|.
name|nextInt
argument_list|(
literal|16
argument_list|)
operator|>>
name|random
operator|.
name|nextInt
argument_list|(
literal|16
argument_list|)
decl_stmt|;
name|list
operator|.
name|get
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|runnable
range|:
name|threads
control|)
block|{
name|runnable
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|runnable
range|:
name|threads
control|)
block|{
try|try
block|{
name|runnable
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|size
init|=
name|list
operator|.
name|size
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|assertThat
argument_list|(
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"$"
operator|+
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelfPopulatingList30
parameter_list|()
block|{
comment|//noinspection MismatchedQueryAndUpdateOfCollection
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|RexSlot
operator|.
name|SelfPopulatingList
argument_list|(
literal|"$"
argument_list|,
literal|30
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
init|=
name|list
operator|.
name|get
argument_list|(
literal|30
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
argument_list|,
name|is
argument_list|(
literal|"$30"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Callback for {@link #check}. Test code will typically use {@code builder}    * to create some expressions, call    * {@link org.apache.calcite.rex.RexExecutorImpl#reduce} to evaluate them into    * a list, then check that the results are as expected. */
interface|interface
name|Action
block|{
name|void
name|check
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|)
function_decl|;
block|}
comment|/**    * ArrayList-based DataContext to check Rex execution.    */
specifier|public
specifier|static
class|class
name|TestDataContext
implements|implements
name|DataContext
block|{
specifier|private
specifier|final
name|Object
index|[]
name|values
decl_stmt|;
specifier|public
name|TestDataContext
parameter_list|(
name|Object
index|[]
name|values
parameter_list|)
block|{
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
block|}
specifier|public
name|SchemaPlus
name|getRootSchema
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unsupported"
argument_list|)
throw|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unsupported"
argument_list|)
throw|;
block|}
specifier|public
name|QueryProvider
name|getQueryProvider
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unsupported"
argument_list|)
throw|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"inputRecord"
argument_list|)
condition|)
block|{
return|return
name|values
return|;
block|}
else|else
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Wrong DataContext access"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

begin_comment
comment|// End RexExecutorTest.java
end_comment

end_unit

