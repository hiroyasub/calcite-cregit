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
name|sql
operator|.
name|SqlAggFunction
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
name|SqlFunction
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
name|SqlFunctionCategory
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
name|SqlIdentifier
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
name|SqlOperatorBinding
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
name|SqlOperatorTable
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
name|SqlTableFunction
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
name|SqlParserPos
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
name|SqlOperandCountRanges
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
name|SqlReturnTypeInference
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
name|SqlTypeFamily
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
name|sql
operator|.
name|util
operator|.
name|ChainedSqlOperatorTable
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
name|util
operator|.
name|ListSqlOperatorTable
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
name|Optionality
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

begin_comment
comment|/**  * Mock operator table for testing purposes. Contains the standard SQL operator  * table, plus a list of operators.  */
end_comment

begin_class
specifier|public
class|class
name|MockSqlOperatorTable
extends|extends
name|ChainedSqlOperatorTable
block|{
specifier|private
specifier|final
name|ListSqlOperatorTable
name|listOpTab
decl_stmt|;
specifier|public
name|MockSqlOperatorTable
parameter_list|(
name|SqlOperatorTable
name|parentTable
parameter_list|)
block|{
name|super
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|parentTable
argument_list|,
operator|new
name|ListSqlOperatorTable
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|listOpTab
operator|=
operator|(
name|ListSqlOperatorTable
operator|)
name|tableList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**    * Adds an operator to this table.    */
specifier|public
name|void
name|addOperator
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
name|listOpTab
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|void
name|addRamp
parameter_list|(
name|MockSqlOperatorTable
name|opTab
parameter_list|)
block|{
comment|// Don't use anonymous inner classes. They can't be instantiated
comment|// using reflection when we are deserializing from JSON.
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|RampFunction
argument_list|()
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|DedupFunction
argument_list|()
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|MyFunction
argument_list|()
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|MyAvgAggFunction
argument_list|()
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|RowFunction
argument_list|()
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|NotATableFunction
argument_list|()
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|BadTableFunction
argument_list|()
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|StructuredFunction
argument_list|()
argument_list|)
expr_stmt|;
name|opTab
operator|.
name|addOperator
argument_list|(
operator|new
name|CompositeFunction
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** "RAMP" user-defined table function. */
specifier|public
specifier|static
class|class
name|RampFunction
extends|extends
name|SqlFunction
implements|implements
name|SqlTableFunction
block|{
specifier|public
name|RampFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"RAMP"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|CURSOR
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|NUMERIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlReturnTypeInference
name|getRowTypeInference
parameter_list|()
block|{
return|return
name|opBinding
lambda|->
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"I"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|/** "DYNTYPE" user-defined table function. */
specifier|public
specifier|static
class|class
name|DynamicTypeFunction
extends|extends
name|SqlFunction
implements|implements
name|SqlTableFunction
block|{
specifier|public
name|DynamicTypeFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"RAMP"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|CURSOR
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|NUMERIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlReturnTypeInference
name|getRowTypeInference
parameter_list|()
block|{
return|return
name|opBinding
lambda|->
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"I"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|/** Not valid as a table function, even though it returns CURSOR, because    * it does not implement {@link SqlTableFunction}. */
specifier|public
specifier|static
class|class
name|NotATableFunction
extends|extends
name|SqlFunction
block|{
specifier|public
name|NotATableFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"BAD_RAMP"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|CURSOR
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|NUMERIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Another bad table function: declares itself as a table function but does    * not return CURSOR. */
specifier|public
specifier|static
class|class
name|BadTableFunction
extends|extends
name|SqlFunction
implements|implements
name|SqlTableFunction
block|{
specifier|public
name|BadTableFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"BAD_TABLE_FUNCTION"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|NUMERIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
comment|// This is wrong. A table function should return CURSOR.
return|return
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"I"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlReturnTypeInference
name|getRowTypeInference
parameter_list|()
block|{
return|return
name|this
operator|::
name|inferReturnType
return|;
block|}
block|}
comment|/** "DEDUP" user-defined table function. */
specifier|public
specifier|static
class|class
name|DedupFunction
extends|extends
name|SqlFunction
implements|implements
name|SqlTableFunction
block|{
specifier|public
name|DedupFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"DEDUP"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|CURSOR
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|VARIADIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|SqlReturnTypeInference
name|getRowTypeInference
parameter_list|()
block|{
return|return
name|opBinding
lambda|->
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"NAME"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|1024
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|/** "MYFUN" user-defined scalar function. */
specifier|public
specifier|static
class|class
name|MyFunction
extends|extends
name|SqlFunction
block|{
specifier|public
name|MyFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"MYFUN"
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
literal|"MYFUN"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|NUMERIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
return|;
block|}
block|}
comment|/** "MYAGGFUNC" user-defined aggregate function. This agg function accept one or more arguments    * in order to reproduce the throws of CALCITE-3929. */
specifier|public
specifier|static
class|class
name|MyAggFunc
extends|extends
name|SqlAggFunction
block|{
specifier|public
name|MyAggFunc
parameter_list|()
block|{
name|super
argument_list|(
literal|"myAggFunc"
argument_list|,
literal|null
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|BIGINT
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|ONE_OR_MORE
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|Optionality
operator|.
name|FORBIDDEN
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * "SPLIT" user-defined function. This function return array type    * in order to reproduce the throws of CALCITE-4062.    */
specifier|public
specifier|static
class|class
name|SplitFunction
extends|extends
name|SqlFunction
block|{
specifier|public
name|SplitFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"SPLIT"
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
literal|"SPLIT"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|STRING
argument_list|,
name|SqlTypeFamily
operator|.
name|STRING
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createArrayType
argument_list|(
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
comment|/** "MYAGG" user-defined aggregate function. This agg function accept two numeric arguments    * in order to reproduce the throws of CALCITE-2744. */
specifier|public
specifier|static
class|class
name|MyAvgAggFunction
extends|extends
name|SqlAggFunction
block|{
specifier|public
name|MyAvgAggFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"MYAGG"
argument_list|,
literal|null
argument_list|,
name|SqlKind
operator|.
name|AVG
argument_list|,
name|ReturnTypes
operator|.
name|AVG_AGG_FUNCTION
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|family
argument_list|(
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|,
name|SqlTypeFamily
operator|.
name|NUMERIC
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|NUMERIC
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|Optionality
operator|.
name|FORBIDDEN
argument_list|)
expr_stmt|;
block|}
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
comment|/** "ROW_FUNC" user-defined table function whose return type is    * row type with nullable and non-nullable fields. */
specifier|public
specifier|static
class|class
name|RowFunction
extends|extends
name|SqlFunction
implements|implements
name|SqlTableFunction
block|{
name|RowFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"ROW_FUNC"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|ReturnTypes
operator|.
name|CURSOR
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|NILADIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_TABLE_FUNCTION
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|RelDataType
name|inferRowType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|bigintType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"NOT_NULL_FIELD"
argument_list|,
name|bigintType
argument_list|)
operator|.
name|add
argument_list|(
literal|"NULLABLE_FIELD"
argument_list|,
name|bigintType
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlReturnTypeInference
name|getRowTypeInference
parameter_list|()
block|{
return|return
name|RowFunction
operator|::
name|inferRowType
return|;
block|}
block|}
comment|/** "STRUCTURED_FUNC" user-defined function whose return type is structured type. */
specifier|public
specifier|static
class|class
name|StructuredFunction
extends|extends
name|SqlFunction
block|{
name|StructuredFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"STRUCTURED_FUNC"
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
literal|"STRUCTURED_FUNC"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|NILADIC
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|bigintType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|varcharType
init|=
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|,
literal|20
argument_list|)
decl_stmt|;
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"F0"
argument_list|,
name|bigintType
argument_list|)
operator|.
name|add
argument_list|(
literal|"F1"
argument_list|,
name|varcharType
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
comment|/** "COMPOSITE" user-defined scalar function. **/
specifier|public
specifier|static
class|class
name|CompositeFunction
extends|extends
name|SqlFunction
block|{
specifier|public
name|CompositeFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"COMPOSITE"
argument_list|,
operator|new
name|SqlIdentifier
argument_list|(
literal|"COMPOSITE"
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|or
argument_list|(
name|OperandTypes
operator|.
name|variadic
argument_list|(
name|SqlOperandCountRanges
operator|.
name|from
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|OperandTypes
operator|.
name|variadic
argument_list|(
name|SqlOperandCountRanges
operator|.
name|from
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|inferReturnType
parameter_list|(
name|SqlOperatorBinding
name|opBinding
parameter_list|)
block|{
specifier|final
name|RelDataTypeFactory
name|typeFactory
init|=
name|opBinding
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|BIGINT
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
