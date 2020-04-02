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
name|sql
operator|.
name|type
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeSystemImpl
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
name|jupiter
operator|.
name|api
operator|.
name|Test
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
comment|/**  * Tests return type inference using {@code RelDataTypeSystem}  */
end_comment

begin_class
class|class
name|RelDataTypeSystemTest
block|{
specifier|private
specifier|static
specifier|final
name|SqlTypeFixture
name|TYPE_FIXTURE
init|=
operator|new
name|SqlTypeFixture
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SqlTypeFactoryImpl
name|TYPE_FACTORY
init|=
name|TYPE_FIXTURE
operator|.
name|typeFactory
decl_stmt|;
comment|/**    * Custom type system class that overrides the default decimal plus type derivation.    */
specifier|private
specifier|static
specifier|final
class|class
name|CustomTypeSystem
extends|extends
name|RelDataTypeSystemImpl
block|{
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveDecimalPlusType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
operator|!
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
operator|!
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|resultScale
init|=
name|Math
operator|.
name|max
argument_list|(
name|type1
operator|.
name|getScale
argument_list|()
argument_list|,
name|type2
operator|.
name|getScale
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|resultPrecision
init|=
name|resultScale
operator|+
name|Math
operator|.
name|max
argument_list|(
name|type1
operator|.
name|getPrecision
argument_list|()
operator|-
name|type1
operator|.
name|getScale
argument_list|()
argument_list|,
name|type2
operator|.
name|getPrecision
argument_list|()
operator|-
name|type2
operator|.
name|getScale
argument_list|()
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|resultPrecision
operator|>
literal|38
condition|)
block|{
name|int
name|minScale
init|=
name|Math
operator|.
name|min
argument_list|(
name|resultScale
argument_list|,
literal|6
argument_list|)
decl_stmt|;
name|int
name|delta
init|=
name|resultPrecision
operator|-
literal|38
decl_stmt|;
name|resultPrecision
operator|=
literal|38
expr_stmt|;
name|resultScale
operator|=
name|Math
operator|.
name|max
argument_list|(
name|resultScale
operator|-
name|delta
argument_list|,
name|minScale
argument_list|)
expr_stmt|;
block|}
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|resultPrecision
argument_list|,
name|resultScale
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveDecimalMultiplyType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
operator|!
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
operator|!
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|type1
operator|.
name|getPrecision
argument_list|()
operator|*
name|type2
operator|.
name|getPrecision
argument_list|()
argument_list|,
name|type1
operator|.
name|getScale
argument_list|()
operator|*
name|type2
operator|.
name|getScale
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveDecimalDivideType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
operator|!
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
operator|!
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|typeFactory
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|type1
operator|.
name|getPrecision
argument_list|()
operator|-
name|type2
operator|.
name|getPrecision
argument_list|()
argument_list|)
argument_list|,
name|Math
operator|.
name|abs
argument_list|(
name|type1
operator|.
name|getScale
argument_list|()
operator|-
name|type2
operator|.
name|getScale
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelDataType
name|deriveDecimalModType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
block|{
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type1
argument_list|)
operator|&&
operator|!
name|SqlTypeUtil
operator|.
name|isExactNumeric
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
operator|!
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type1
argument_list|)
operator|||
operator|!
name|SqlTypeUtil
operator|.
name|isDecimal
argument_list|(
name|type2
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|type1
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getMaxNumericPrecision
parameter_list|()
block|{
return|return
literal|38
return|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|SqlTypeFactoryImpl
name|CUSTOM_FACTORY
init|=
operator|new
name|SqlTypeFactoryImpl
argument_list|(
operator|new
name|CustomTypeSystem
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
name|void
name|testDecimalAdditionReturnTypeInference
parameter_list|()
block|{
name|RelDataType
name|operand1
init|=
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RelDataType
name|operand2
init|=
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|10
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|RelDataType
name|dataType
init|=
name|SqlStdOperatorTable
operator|.
name|MINUS
operator|.
name|inferReturnType
argument_list|(
name|TYPE_FACTORY
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|operand1
argument_list|,
name|operand2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|12
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dataType
operator|.
name|getScale
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDecimalModReturnTypeInference
parameter_list|()
block|{
name|RelDataType
name|operand1
init|=
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|10
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|RelDataType
name|operand2
init|=
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|19
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|RelDataType
name|dataType
init|=
name|SqlStdOperatorTable
operator|.
name|MOD
operator|.
name|inferReturnType
argument_list|(
name|TYPE_FACTORY
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|operand1
argument_list|,
name|operand2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|11
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|dataType
operator|.
name|getScale
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testDoubleModReturnTypeInference
parameter_list|()
block|{
name|RelDataType
name|operand1
init|=
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|RelDataType
name|operand2
init|=
name|TYPE_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
name|RelDataType
name|dataType
init|=
name|SqlStdOperatorTable
operator|.
name|MOD
operator|.
name|inferReturnType
argument_list|(
name|TYPE_FACTORY
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|operand1
argument_list|,
name|operand2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SqlTypeName
operator|.
name|DOUBLE
argument_list|,
name|dataType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCustomDecimalPlusReturnTypeInference
parameter_list|()
block|{
name|RelDataType
name|operand1
init|=
name|CUSTOM_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|38
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|RelDataType
name|operand2
init|=
name|CUSTOM_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|38
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|RelDataType
name|dataType
init|=
name|SqlStdOperatorTable
operator|.
name|PLUS
operator|.
name|inferReturnType
argument_list|(
name|CUSTOM_FACTORY
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|operand1
argument_list|,
name|operand2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|dataType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|38
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|9
argument_list|,
name|dataType
operator|.
name|getScale
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCustomDecimalMultiplyReturnTypeInference
parameter_list|()
block|{
name|RelDataType
name|operand1
init|=
name|CUSTOM_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|2
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|RelDataType
name|operand2
init|=
name|CUSTOM_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|3
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|RelDataType
name|dataType
init|=
name|SqlStdOperatorTable
operator|.
name|MULTIPLY
operator|.
name|inferReturnType
argument_list|(
name|CUSTOM_FACTORY
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|operand1
argument_list|,
name|operand2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|dataType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|dataType
operator|.
name|getScale
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCustomDecimalDivideReturnTypeInference
parameter_list|()
block|{
name|RelDataType
name|operand1
init|=
name|CUSTOM_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|28
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|RelDataType
name|operand2
init|=
name|CUSTOM_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|38
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|RelDataType
name|dataType
init|=
name|SqlStdOperatorTable
operator|.
name|DIVIDE
operator|.
name|inferReturnType
argument_list|(
name|CUSTOM_FACTORY
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|operand1
argument_list|,
name|operand2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|dataType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|dataType
operator|.
name|getScale
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testCustomDecimalModReturnTypeInference
parameter_list|()
block|{
name|RelDataType
name|operand1
init|=
name|CUSTOM_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|28
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|RelDataType
name|operand2
init|=
name|CUSTOM_FACTORY
operator|.
name|createSqlType
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
literal|38
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|RelDataType
name|dataType
init|=
name|SqlStdOperatorTable
operator|.
name|MOD
operator|.
name|inferReturnType
argument_list|(
name|CUSTOM_FACTORY
argument_list|,
name|Lists
operator|.
name|newArrayList
argument_list|(
name|operand1
argument_list|,
name|operand2
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|,
name|dataType
operator|.
name|getSqlTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|28
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|dataType
operator|.
name|getScale
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

