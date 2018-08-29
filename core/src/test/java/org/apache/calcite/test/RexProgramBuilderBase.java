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
name|jdbc
operator|.
name|JavaTypeFactoryImpl
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
name|RelOptPredicateList
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeSystem
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexCall
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
name|rex
operator|.
name|RexDynamicParam
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
name|rex
operator|.
name|RexExecutor
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
name|rex
operator|.
name|RexExecutorImpl
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
name|rex
operator|.
name|RexLiteral
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
name|rex
operator|.
name|RexNode
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
name|rex
operator|.
name|RexSimplify
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
name|SqlTypeName
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
name|ImmutableMap
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * This class provides helper methods to build rex expressions.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RexProgramBuilderBase
block|{
comment|/**    * Input variables for tests should come from a struct type, so    * a struct is created where the first {@code MAX_FIELDS} are nullable,    * and the next {@code MAX_FIELDS} are not nullable.    */
specifier|protected
specifier|static
specifier|final
name|int
name|MAX_FIELDS
init|=
literal|10
decl_stmt|;
specifier|protected
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|protected
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|protected
name|RexExecutor
name|executor
decl_stmt|;
specifier|protected
name|RexSimplify
name|simplify
decl_stmt|;
specifier|protected
name|RexLiteral
name|trueLiteral
decl_stmt|;
specifier|protected
name|RexLiteral
name|falseLiteral
decl_stmt|;
specifier|protected
name|RexLiteral
name|nullBool
decl_stmt|;
specifier|protected
name|RexLiteral
name|nullInt
decl_stmt|;
specifier|protected
name|RexLiteral
name|nullVarchar
decl_stmt|;
specifier|private
name|RelDataType
name|nullableBool
decl_stmt|;
specifier|private
name|RelDataType
name|nonNullableBool
decl_stmt|;
specifier|private
name|RelDataType
name|nullableInt
decl_stmt|;
specifier|private
name|RelDataType
name|nonNullableInt
decl_stmt|;
specifier|private
name|RelDataType
name|nullableVarchar
decl_stmt|;
specifier|private
name|RelDataType
name|nonNullableVarchar
decl_stmt|;
comment|// Note: JUnit 4 creates new instance for each test method,
comment|// so we initialize these structures on demand
comment|// It maps non-nullable type to struct of (10 nullable, 10 non-nullable) fields
specifier|private
name|Map
argument_list|<
name|RelDataType
argument_list|,
name|RexDynamicParam
argument_list|>
name|dynamicParams
decl_stmt|;
comment|/**    * Dummy data context for test.    */
specifier|private
specifier|static
class|class
name|DummyTestDataContext
implements|implements
name|DataContext
block|{
specifier|private
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
decl_stmt|;
name|DummyTestDataContext
parameter_list|()
block|{
name|this
operator|.
name|map
operator|=
name|ImmutableMap
operator|.
name|of
argument_list|(
name|Variable
operator|.
name|TIME_ZONE
operator|.
name|camelName
argument_list|,
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"America/Los_Angeles"
argument_list|)
argument_list|,
name|Variable
operator|.
name|CURRENT_TIMESTAMP
operator|.
name|camelName
argument_list|,
literal|1311120000000L
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SchemaPlus
name|getRootSchema
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
literal|null
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
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|setUp
parameter_list|()
block|{
name|typeFactory
operator|=
operator|new
name|JavaTypeFactoryImpl
argument_list|(
name|RelDataTypeSystem
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
name|rexBuilder
operator|=
operator|new
name|RexBuilder
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
name|executor
operator|=
operator|new
name|RexExecutorImpl
argument_list|(
operator|new
name|DummyTestDataContext
argument_list|()
argument_list|)
expr_stmt|;
name|simplify
operator|=
operator|new
name|RexSimplify
argument_list|(
name|rexBuilder
argument_list|,
name|RelOptPredicateList
operator|.
name|EMPTY
argument_list|,
literal|false
argument_list|,
name|executor
argument_list|)
operator|.
name|withParanoid
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|trueLiteral
operator|=
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|falseLiteral
operator|=
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|nonNullableInt
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
expr_stmt|;
name|nullableInt
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|nonNullableInt
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nullInt
operator|=
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|nullableInt
argument_list|)
expr_stmt|;
name|nonNullableBool
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BOOLEAN
argument_list|)
expr_stmt|;
name|nullableBool
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|nonNullableBool
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nullBool
operator|=
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|nullableBool
argument_list|)
expr_stmt|;
name|nonNullableVarchar
operator|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
expr_stmt|;
name|nullableVarchar
operator|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|nonNullableVarchar
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|nullVarchar
operator|=
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|nullableVarchar
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RexDynamicParam
name|getDynamicParam
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|String
name|fieldNamePrefix
parameter_list|)
block|{
if|if
condition|(
name|dynamicParams
operator|==
literal|null
condition|)
block|{
name|dynamicParams
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
return|return
name|dynamicParams
operator|.
name|computeIfAbsent
argument_list|(
name|type
argument_list|,
name|k
lambda|->
block|{
name|RelDataType
name|nullableType
init|=
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|k
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|RelDataTypeFactory
operator|.
name|Builder
name|builder
init|=
name|typeFactory
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
name|MAX_FIELDS
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|fieldNamePrefix
operator|+
name|i
argument_list|,
name|nullableType
argument_list|)
expr_stmt|;
block|}
name|String
name|notNullPrefix
init|=
literal|"notNull"
operator|+
name|Character
operator|.
name|toUpperCase
argument_list|(
name|fieldNamePrefix
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
operator|+
name|fieldNamePrefix
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
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
name|MAX_FIELDS
condition|;
name|i
operator|++
control|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|notNullPrefix
operator|+
name|i
argument_list|,
name|k
argument_list|)
expr_stmt|;
block|}
return|return
name|rexBuilder
operator|.
name|makeDynamicParam
argument_list|(
name|builder
operator|.
name|build
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
block|}
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isNull
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NULL
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isUnknown
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_UNKNOWN
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isNotNull
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_NULL
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isFalse
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_FALSE
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isNotFalse
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_FALSE
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isTrue
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_TRUE
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isNotTrue
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_TRUE
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isDistinctFrom
parameter_list|(
name|RexNode
name|a
parameter_list|,
name|RexNode
name|b
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_DISTINCT_FROM
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|isNotDistinctFrom
parameter_list|(
name|RexNode
name|a
parameter_list|,
name|RexNode
name|b
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IS_NOT_DISTINCT_FROM
argument_list|,
name|a
argument_list|,
name|b
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|nullIf
parameter_list|(
name|RexNode
name|node1
parameter_list|,
name|RexNode
name|node2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NULLIF
argument_list|,
name|node1
argument_list|,
name|node2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|not
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|unaryMinus
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_MINUS
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|unaryPlus
parameter_list|(
name|RexNode
name|node
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|UNARY_PLUS
argument_list|,
name|node
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|and
parameter_list|(
name|RexNode
modifier|...
name|nodes
parameter_list|)
block|{
return|return
name|and
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|nodes
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|and
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|nodes
parameter_list|)
block|{
comment|// Does not flatten nested ANDs. We want test input to contain nested ANDs.
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|AND
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|nodes
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|or
parameter_list|(
name|RexNode
modifier|...
name|nodes
parameter_list|)
block|{
return|return
name|or
argument_list|(
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|nodes
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|or
parameter_list|(
name|Iterable
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|nodes
parameter_list|)
block|{
comment|// Does not flatten nested ORs. We want test input to contain nested ORs.
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|OR
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|nodes
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|case_
parameter_list|(
name|RexNode
modifier|...
name|nodes
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|CASE
argument_list|,
name|nodes
argument_list|)
return|;
block|}
comment|/**    * Creates a call to the CAST operator.    *    *<p>This method enables to create {@code CAST(42 nullable int)} expressions.</p>    *    * @param e input node    * @param type type to cast to    * @return call to CAST operator    */
specifier|protected
name|RexNode
name|abstractCast
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeAbstractCast
argument_list|(
name|type
argument_list|,
name|e
argument_list|)
return|;
block|}
comment|/**    * Creates a call to the CAST operator, expanding if possible, and not    * preserving nullability.    *    *<p>Tries to expand the cast, and therefore the result may be something    * other than a {@link RexCall} to the CAST operator, such as a    * {@link RexLiteral}.</p>     * @param e input node    * @param type type to cast to    * @return input node converted to given type    */
specifier|protected
name|RexNode
name|cast
parameter_list|(
name|RexNode
name|e
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCast
argument_list|(
name|type
argument_list|,
name|e
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|eq
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|EQUALS
argument_list|,
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|ne
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|NOT_EQUALS
argument_list|,
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|le
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN_OR_EQUAL
argument_list|,
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|lt
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|LESS_THAN
argument_list|,
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|ge
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN_OR_EQUAL
argument_list|,
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|gt
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|GREATER_THAN
argument_list|,
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|plus
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
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
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|coalesce
parameter_list|(
name|RexNode
modifier|...
name|nodes
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|COALESCE
argument_list|,
name|nodes
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|divInt
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|DIVIDE_INTEGER
argument_list|,
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|sub
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|MINUS
argument_list|,
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|add
parameter_list|(
name|RexNode
name|n1
parameter_list|,
name|RexNode
name|n2
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
name|n1
argument_list|,
name|n2
argument_list|)
return|;
block|}
comment|/**    * Generates {@code x IN (y, z)} expression when called as {@code in(x, y, z)}.    * @param node left side of the IN expression    * @param nodes nodes in the right side of IN expression    * @return IN expression    */
specifier|protected
name|RexNode
name|in
parameter_list|(
name|RexNode
name|node
parameter_list|,
name|RexNode
modifier|...
name|nodes
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeCall
argument_list|(
name|SqlStdOperatorTable
operator|.
name|IN
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RexNode
operator|>
name|builder
argument_list|()
operator|.
name|add
argument_list|(
name|node
argument_list|)
operator|.
name|add
argument_list|(
name|nodes
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
return|;
block|}
comment|// Types
specifier|protected
name|RelDataType
name|nullable
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
if|if
condition|(
name|type
operator|.
name|isNullable
argument_list|()
condition|)
block|{
return|return
name|type
return|;
block|}
return|return
name|typeFactory
operator|.
name|createTypeWithNullability
argument_list|(
name|type
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|protected
name|RelDataType
name|tVarchar
parameter_list|()
block|{
return|return
name|nonNullableVarchar
return|;
block|}
specifier|protected
name|RelDataType
name|tVarchar
parameter_list|(
name|boolean
name|nullable
parameter_list|)
block|{
return|return
name|nullable
condition|?
name|nullableVarchar
else|:
name|nonNullableVarchar
return|;
block|}
specifier|protected
name|RelDataType
name|tBoolean
parameter_list|()
block|{
return|return
name|nonNullableBool
return|;
block|}
specifier|protected
name|RelDataType
name|tBoolean
parameter_list|(
name|boolean
name|nullable
parameter_list|)
block|{
return|return
name|nullable
condition|?
name|nullableBool
else|:
name|nonNullableBool
return|;
block|}
specifier|protected
name|RelDataType
name|tInt
parameter_list|()
block|{
return|return
name|nonNullableInt
return|;
block|}
specifier|protected
name|RelDataType
name|tInt
parameter_list|(
name|boolean
name|nullable
parameter_list|)
block|{
return|return
name|nullable
condition|?
name|nullableInt
else|:
name|nonNullableInt
return|;
block|}
comment|// Literals
comment|/**    * Creates null literal with given type.    * For instance: {@code null_(tInt())}    *    * @param type type of required null    * @return null literal of a given type    */
specifier|protected
name|RexLiteral
name|null_
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|nullable
argument_list|(
name|type
argument_list|)
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|literal
parameter_list|(
name|boolean
name|value
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
name|value
argument_list|,
name|nonNullableBool
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|literal
parameter_list|(
name|Boolean
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|nullableBool
argument_list|)
return|;
block|}
return|return
name|literal
argument_list|(
name|value
operator|.
name|booleanValue
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|literal
parameter_list|(
name|int
name|value
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
name|value
argument_list|,
name|nonNullableInt
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|literal
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|literal
parameter_list|(
name|BigDecimal
name|value
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeExactLiteral
argument_list|(
name|value
argument_list|,
name|type
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|literal
parameter_list|(
name|Integer
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|nullableInt
argument_list|)
return|;
block|}
return|return
name|literal
argument_list|(
name|value
operator|.
name|intValue
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
name|RexNode
name|literal
parameter_list|(
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
name|rexBuilder
operator|.
name|makeNullLiteral
argument_list|(
name|nullableVarchar
argument_list|)
return|;
block|}
return|return
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
name|value
argument_list|,
name|nonNullableVarchar
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// Variables
comment|/**    * Generates input ref with given type and index.    *    *<p>Prefer {@link #vBool()}, {@link #vInt()} and so on.    *    *<p>The problem with "input refs" is {@code input(tInt(), 0).toString()}    * yields {@code $0}, so the type of the expression is not printed, and it    * makes it hard to analyze the expressions.    *    * @param type desired type of the node    * @param arg argument index (0-based)    * @return input ref with given type and index    */
specifier|protected
name|RexNode
name|input
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|int
name|arg
parameter_list|)
block|{
return|return
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|type
argument_list|,
name|arg
argument_list|)
return|;
block|}
specifier|private
name|void
name|assertArgValue
parameter_list|(
name|int
name|arg
parameter_list|)
block|{
assert|assert
name|arg
operator|>=
literal|0
operator|&&
name|arg
operator|<
name|MAX_FIELDS
operator|:
literal|"arg should be in 0.."
operator|+
operator|(
name|MAX_FIELDS
operator|-
literal|1
operator|)
operator|+
literal|" range. Actual value was "
operator|+
name|arg
assert|;
block|}
comment|/**    * Creates {@code nullable boolean variable} with index of 0.    * If you need several distinct variables, use {@link #vBool(int)}    * @return nullable boolean variable with index of 0    */
specifier|protected
name|RexNode
name|vBool
parameter_list|()
block|{
return|return
name|vBool
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Creates {@code nullable boolean variable} with index of {@code arg} (0-based).    * The resulting node would look like {@code ?0.bool3} if {@code arg} is {@code 3}.    *    * @return nullable boolean variable with given index (0-based)    */
specifier|protected
name|RexNode
name|vBool
parameter_list|(
name|int
name|arg
parameter_list|)
block|{
name|assertArgValue
argument_list|(
name|arg
argument_list|)
expr_stmt|;
return|return
name|rexBuilder
operator|.
name|makeFieldAccess
argument_list|(
name|getDynamicParam
argument_list|(
name|nonNullableBool
argument_list|,
literal|"bool"
argument_list|)
argument_list|,
name|arg
argument_list|)
return|;
block|}
comment|/**    * Creates {@code non-nullable boolean variable} with index of 0.    * If you need several distinct variables, use {@link #vBoolNotNull(int)}.    * The resulting node would look like {@code ?0.notNullBool0}    *    * @return non-nullable boolean variable with index of 0    */
specifier|protected
name|RexNode
name|vBoolNotNull
parameter_list|()
block|{
return|return
name|vBoolNotNull
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Creates {@code non-nullable boolean variable} with index of {@code arg} (0-based).    * The resulting node would look like {@code ?0.notNullBool3} if {@code arg} is {@code 3}.    *    * @return non-nullable boolean variable with given index (0-based)    */
specifier|protected
name|RexNode
name|vBoolNotNull
parameter_list|(
name|int
name|arg
parameter_list|)
block|{
name|assertArgValue
argument_list|(
name|arg
argument_list|)
expr_stmt|;
return|return
name|rexBuilder
operator|.
name|makeFieldAccess
argument_list|(
name|getDynamicParam
argument_list|(
name|nonNullableBool
argument_list|,
literal|"bool"
argument_list|)
argument_list|,
name|arg
operator|+
name|MAX_FIELDS
argument_list|)
return|;
block|}
comment|/**    * Creates {@code nullable int variable} with index of 0.    * If you need several distinct variables, use {@link #vInt(int)}.    * The resulting node would look like {@code ?0.notNullInt0}    *    * @return nullable int variable with index of 0    */
specifier|protected
name|RexNode
name|vInt
parameter_list|()
block|{
return|return
name|vInt
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Creates {@code nullable int variable} with index of {@code arg} (0-based).    * The resulting node would look like {@code ?0.int3} if {@code arg} is {@code 3}.    *    * @return nullable int variable with given index (0-based)    */
specifier|protected
name|RexNode
name|vInt
parameter_list|(
name|int
name|arg
parameter_list|)
block|{
name|assertArgValue
argument_list|(
name|arg
argument_list|)
expr_stmt|;
return|return
name|rexBuilder
operator|.
name|makeFieldAccess
argument_list|(
name|getDynamicParam
argument_list|(
name|nonNullableInt
argument_list|,
literal|"int"
argument_list|)
argument_list|,
name|arg
argument_list|)
return|;
block|}
comment|/**    * Creates {@code non-nullable int variable} with index of 0.    * If you need several distinct variables, use {@link #vIntNotNull(int)}.    * The resulting node would look like {@code ?0.notNullInt0}    *    * @return non-nullable int variable with index of 0    */
specifier|protected
name|RexNode
name|vIntNotNull
parameter_list|()
block|{
return|return
name|vIntNotNull
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Creates {@code non-nullable int variable} with index of {@code arg} (0-based).    * The resulting node would look like {@code ?0.notNullInt3} if {@code arg} is {@code 3}.    *    * @return non-nullable int variable with given index (0-based)    */
specifier|protected
name|RexNode
name|vIntNotNull
parameter_list|(
name|int
name|arg
parameter_list|)
block|{
name|assertArgValue
argument_list|(
name|arg
argument_list|)
expr_stmt|;
return|return
name|rexBuilder
operator|.
name|makeFieldAccess
argument_list|(
name|getDynamicParam
argument_list|(
name|nonNullableInt
argument_list|,
literal|"int"
argument_list|)
argument_list|,
name|arg
operator|+
name|MAX_FIELDS
argument_list|)
return|;
block|}
comment|/**    * Creates {@code nullable varchar variable} with index of 0.    * If you need several distinct variables, use {@link #vVarchar(int)}.    * The resulting node would look like {@code ?0.notNullVarchar0}    *    * @return nullable varchar variable with index of 0    */
specifier|protected
name|RexNode
name|vVarchar
parameter_list|()
block|{
return|return
name|vVarchar
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Creates {@code nullable varchar variable} with index of {@code arg} (0-based).    * The resulting node would look like {@code ?0.varchar3} if {@code arg} is {@code 3}.    *    * @return nullable varchar variable with given index (0-based)    */
specifier|protected
name|RexNode
name|vVarchar
parameter_list|(
name|int
name|arg
parameter_list|)
block|{
name|assertArgValue
argument_list|(
name|arg
argument_list|)
expr_stmt|;
return|return
name|rexBuilder
operator|.
name|makeFieldAccess
argument_list|(
name|getDynamicParam
argument_list|(
name|nonNullableVarchar
argument_list|,
literal|"varchar"
argument_list|)
argument_list|,
name|arg
argument_list|)
return|;
block|}
comment|/**    * Creates {@code non-nullable varchar variable} with index of 0.    * If you need several distinct variables, use {@link #vVarcharNotNull(int)}.    * The resulting node would look like {@code ?0.notNullVarchar0}    *    * @return non-nullable varchar variable with index of 0    */
specifier|protected
name|RexNode
name|vVarcharNotNull
parameter_list|()
block|{
return|return
name|vVarcharNotNull
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|/**    * Creates {@code non-nullable varchar variable} with index of {@code arg} (0-based).    * The resulting node would look like {@code ?0.notNullVarchar3} if {@code arg} is {@code 3}.    *    * @return non-nullable varchar variable with given index (0-based)    */
specifier|protected
name|RexNode
name|vVarcharNotNull
parameter_list|(
name|int
name|arg
parameter_list|)
block|{
name|assertArgValue
argument_list|(
name|arg
argument_list|)
expr_stmt|;
return|return
name|rexBuilder
operator|.
name|makeFieldAccess
argument_list|(
name|getDynamicParam
argument_list|(
name|nonNullableVarchar
argument_list|,
literal|"varchar"
argument_list|)
argument_list|,
name|arg
operator|+
name|MAX_FIELDS
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexProgramBuilderBase.java
end_comment

end_unit

