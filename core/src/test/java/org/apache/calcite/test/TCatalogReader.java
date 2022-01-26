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
name|test
operator|.
name|catalog
operator|.
name|MockCatalogReader
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
name|NonNull
import|;
end_import

begin_comment
comment|/** A catalog reader with tables "T1" and "T2" whose schema contains all  * test data types. */
end_comment

begin_class
specifier|public
class|class
name|TCatalogReader
extends|extends
name|MockCatalogReader
block|{
specifier|private
specifier|final
name|boolean
name|caseSensitive
decl_stmt|;
name|TCatalogReader
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|super
argument_list|(
name|typeFactory
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
block|}
comment|/** Creates and initializes a TCatalogReader. */
specifier|public
specifier|static
annotation|@
name|NonNull
name|TCatalogReader
name|create
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
return|return
operator|new
name|TCatalogReader
argument_list|(
name|typeFactory
argument_list|,
name|caseSensitive
argument_list|)
operator|.
name|init
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|TCatalogReader
name|init
parameter_list|()
block|{
specifier|final
name|TypeCoercionTest
operator|.
name|Fixture
name|f
init|=
name|TypeCoercionTest
operator|.
name|DEFAULT_FIXTURE
operator|.
name|withTypeFactory
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
name|MockSchema
name|tSchema
init|=
operator|new
name|MockSchema
argument_list|(
literal|"SALES"
argument_list|)
decl_stmt|;
name|registerSchema
argument_list|(
name|tSchema
argument_list|)
expr_stmt|;
comment|// Register "T1" table.
specifier|final
name|MockTable
name|t1
init|=
name|MockTable
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|tSchema
argument_list|,
literal|"T1"
argument_list|,
literal|false
argument_list|,
literal|7.0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_varchar20"
argument_list|,
name|f
operator|.
name|varchar20Type
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_smallint"
argument_list|,
name|f
operator|.
name|smallintType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_int"
argument_list|,
name|f
operator|.
name|intType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_bigint"
argument_list|,
name|f
operator|.
name|bigintType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_float"
argument_list|,
name|f
operator|.
name|floatType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_double"
argument_list|,
name|f
operator|.
name|doubleType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_decimal"
argument_list|,
name|f
operator|.
name|decimalType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_timestamp"
argument_list|,
name|f
operator|.
name|timestampType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_date"
argument_list|,
name|f
operator|.
name|dateType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_binary"
argument_list|,
name|f
operator|.
name|binaryType
argument_list|)
expr_stmt|;
name|t1
operator|.
name|addColumn
argument_list|(
literal|"t1_boolean"
argument_list|,
name|f
operator|.
name|booleanType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|t1
argument_list|)
expr_stmt|;
specifier|final
name|MockTable
name|t2
init|=
name|MockTable
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|tSchema
argument_list|,
literal|"T2"
argument_list|,
literal|false
argument_list|,
literal|7.0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_varchar20"
argument_list|,
name|f
operator|.
name|varchar20Type
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_smallint"
argument_list|,
name|f
operator|.
name|smallintType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_int"
argument_list|,
name|f
operator|.
name|intType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_bigint"
argument_list|,
name|f
operator|.
name|bigintType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_float"
argument_list|,
name|f
operator|.
name|floatType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_double"
argument_list|,
name|f
operator|.
name|doubleType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_decimal"
argument_list|,
name|f
operator|.
name|decimalType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_timestamp"
argument_list|,
name|f
operator|.
name|timestampType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_date"
argument_list|,
name|f
operator|.
name|dateType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_binary"
argument_list|,
name|f
operator|.
name|binaryType
argument_list|)
expr_stmt|;
name|t2
operator|.
name|addColumn
argument_list|(
literal|"t2_boolean"
argument_list|,
name|f
operator|.
name|booleanType
argument_list|)
expr_stmt|;
name|registerTable
argument_list|(
name|t2
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCaseSensitive
parameter_list|()
block|{
return|return
name|caseSensitive
return|;
block|}
block|}
end_class

end_unit
