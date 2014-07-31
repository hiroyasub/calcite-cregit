begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|clone
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Linq4j
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
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
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataTypeImpl
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link ArrayTable} and {@link ColumnLoader}.  */
end_comment

begin_class
specifier|public
class|class
name|ArrayTableTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testPrimitiveArray
parameter_list|()
block|{
name|long
index|[]
name|values
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|0
block|}
decl_stmt|;
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|.
name|orLong
argument_list|(
literal|4
argument_list|,
name|values
argument_list|,
literal|0
argument_list|,
literal|0x0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x0F
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|.
name|orLong
argument_list|(
literal|4
argument_list|,
name|values
argument_list|,
literal|2
argument_list|,
literal|0x0F
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0xF0F
argument_list|,
name|values
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|values
operator|=
operator|new
name|long
index|[]
block|{
literal|0x1213141516171819L
block|,
literal|0x232425262728292AL
block|,
literal|0x3435363738393A3BL
block|}
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x324
argument_list|,
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|.
name|getLong
argument_list|(
literal|12
argument_list|,
name|values
argument_list|,
literal|9
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0xa3b
argument_list|,
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|.
name|getLong
argument_list|(
literal|12
argument_list|,
name|values
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|values
argument_list|,
literal|0
argument_list|)
expr_stmt|;
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
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|.
name|orLong
argument_list|(
literal|10
argument_list|,
name|values
argument_list|,
name|i
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
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
name|assertEquals
argument_list|(
name|i
argument_list|,
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|.
name|getLong
argument_list|(
literal|10
argument_list|,
name|values
argument_list|,
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
name|testNextPowerOf2
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ColumnLoader
operator|.
name|nextPowerOf2
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ColumnLoader
operator|.
name|nextPowerOf2
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|ColumnLoader
operator|.
name|nextPowerOf2
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|ColumnLoader
operator|.
name|nextPowerOf2
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x40000000
argument_list|,
name|ColumnLoader
operator|.
name|nextPowerOf2
argument_list|(
literal|0x3456789a
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x40000000
argument_list|,
name|ColumnLoader
operator|.
name|nextPowerOf2
argument_list|(
literal|0x40000000
argument_list|)
argument_list|)
expr_stmt|;
comment|// overflow
name|assertEquals
argument_list|(
literal|0x80000000
argument_list|,
name|ColumnLoader
operator|.
name|nextPowerOf2
argument_list|(
literal|0x7fffffff
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0x80000000
argument_list|,
name|ColumnLoader
operator|.
name|nextPowerOf2
argument_list|(
literal|0x7ffffffe
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLog2
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|65536
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|15
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|65535
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|16
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|65537
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
operator|-
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|29
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|0x3fffffff
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|ColumnLoader
operator|.
name|log2
argument_list|(
literal|0x40000000
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValueSetInt
parameter_list|()
block|{
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
name|representation
decl_stmt|;
name|ArrayTable
operator|.
name|Column
name|pair
decl_stmt|;
specifier|final
name|ColumnLoader
operator|.
name|ValueSet
name|valueSet
init|=
operator|new
name|ColumnLoader
operator|.
name|ValueSet
argument_list|(
name|int
operator|.
name|class
argument_list|)
decl_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|10
argument_list|)
expr_stmt|;
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
argument_list|)
expr_stmt|;
name|representation
operator|=
operator|(
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|)
name|pair
operator|.
name|representation
expr_stmt|;
comment|// unsigned 4 bit integer (values 0..15)
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|representation
operator|.
name|bitCount
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|representation
operator|.
name|signed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|representation
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// -32 takes us to 6 bit signed
name|valueSet
operator|.
name|add
argument_list|(
operator|-
literal|32
argument_list|)
expr_stmt|;
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
argument_list|)
expr_stmt|;
name|representation
operator|=
operator|(
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|)
name|pair
operator|.
name|representation
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|representation
operator|.
name|bitCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|representation
operator|.
name|signed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|10
argument_list|,
name|representation
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|32
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|32
argument_list|,
name|representation
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
comment|// 63 takes us to 7 bit signed
name|valueSet
operator|.
name|add
argument_list|(
literal|63
argument_list|)
expr_stmt|;
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
argument_list|)
expr_stmt|;
name|representation
operator|=
operator|(
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|)
name|pair
operator|.
name|representation
expr_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|representation
operator|.
name|bitCount
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|representation
operator|.
name|signed
argument_list|)
expr_stmt|;
comment|// 128 pushes us to 8 bit signed, i.e. byte
name|valueSet
operator|.
name|add
argument_list|(
literal|64
argument_list|)
expr_stmt|;
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|)
expr_stmt|;
name|ArrayTable
operator|.
name|PrimitiveArray
name|representation2
init|=
operator|(
name|ArrayTable
operator|.
name|PrimitiveArray
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|representation2
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|32
argument_list|,
name|representation2
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|32
argument_list|,
name|representation2
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|64
argument_list|,
name|representation2
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|64
argument_list|,
name|representation2
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValueSetBoolean
parameter_list|()
block|{
specifier|final
name|ColumnLoader
operator|.
name|ValueSet
name|valueSet
init|=
operator|new
name|ColumnLoader
operator|.
name|ValueSet
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
decl_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|Column
name|pair
init|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
name|representation
init|=
operator|(
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|representation
operator|.
name|bitCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValueSetZero
parameter_list|()
block|{
specifier|final
name|ColumnLoader
operator|.
name|ValueSet
name|valueSet
init|=
operator|new
name|ColumnLoader
operator|.
name|ValueSet
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
decl_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|Column
name|pair
init|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|Constant
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|Constant
name|representation
init|=
operator|(
name|ArrayTable
operator|.
name|Constant
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|representation
operator|.
name|getInt
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pair
operator|.
name|cardinality
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStrings
parameter_list|()
block|{
name|ArrayTable
operator|.
name|Column
name|pair
decl_stmt|;
specifier|final
name|ColumnLoader
operator|.
name|ValueSet
name|valueSet
init|=
operator|new
name|ColumnLoader
operator|.
name|ValueSet
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|ObjectArray
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|ObjectArray
name|representation
init|=
operator|(
name|ArrayTable
operator|.
name|ObjectArray
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|representation
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|representation
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pair
operator|.
name|cardinality
argument_list|)
expr_stmt|;
comment|// Large number of the same string. ObjectDictionary backed by Constant.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|2000
condition|;
name|i
operator|++
control|)
block|{
name|valueSet
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
block|}
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|ObjectDictionary
name|representation2
init|=
operator|(
name|ArrayTable
operator|.
name|ObjectDictionary
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertTrue
argument_list|(
name|representation2
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|Constant
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|representation2
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|representation2
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pair
operator|.
name|cardinality
argument_list|)
expr_stmt|;
comment|// One different string. ObjectDictionary backed by 1-bit
comment|// BitSlicedPrimitiveArray
name|valueSet
operator|.
name|add
argument_list|(
literal|"bar"
argument_list|)
expr_stmt|;
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|ObjectDictionary
name|representation3
init|=
operator|(
name|ArrayTable
operator|.
name|ObjectDictionary
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertTrue
argument_list|(
name|representation3
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
name|representation4
init|=
operator|(
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|)
name|representation3
operator|.
name|representation
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|representation4
operator|.
name|bitCount
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|representation4
operator|.
name|signed
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|representation3
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|representation3
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|1000
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
name|representation3
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|2003
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|pair
operator|.
name|cardinality
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAllNull
parameter_list|()
block|{
name|ArrayTable
operator|.
name|Column
name|pair
decl_stmt|;
specifier|final
name|ColumnLoader
operator|.
name|ValueSet
name|valueSet
init|=
operator|new
name|ColumnLoader
operator|.
name|ValueSet
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|ObjectArray
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|ObjectArray
name|representation
init|=
operator|(
name|ArrayTable
operator|.
name|ObjectArray
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertNull
argument_list|(
name|representation
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pair
operator|.
name|cardinality
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3000
condition|;
name|i
operator|++
control|)
block|{
name|valueSet
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|ObjectDictionary
name|representation2
init|=
operator|(
name|ArrayTable
operator|.
name|ObjectDictionary
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertTrue
argument_list|(
name|representation2
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|Constant
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|pair
operator|.
name|cardinality
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOneValueOneNull
parameter_list|()
block|{
name|ArrayTable
operator|.
name|Column
name|pair
decl_stmt|;
specifier|final
name|ColumnLoader
operator|.
name|ValueSet
name|valueSet
init|=
operator|new
name|ColumnLoader
operator|.
name|ValueSet
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|valueSet
operator|.
name|add
argument_list|(
literal|"foo"
argument_list|)
expr_stmt|;
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|pair
operator|.
name|representation
operator|instanceof
name|ArrayTable
operator|.
name|ObjectArray
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|ObjectArray
name|representation
init|=
operator|(
name|ArrayTable
operator|.
name|ObjectArray
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertNull
argument_list|(
name|representation
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|pair
operator|.
name|cardinality
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|3000
condition|;
name|i
operator|++
control|)
block|{
name|valueSet
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|pair
operator|=
name|valueSet
operator|.
name|freeze
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|ArrayTable
operator|.
name|ObjectDictionary
name|representation2
init|=
operator|(
name|ArrayTable
operator|.
name|ObjectDictionary
operator|)
name|pair
operator|.
name|representation
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
operator|)
name|representation2
operator|.
name|representation
operator|)
operator|.
name|bitCount
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|representation2
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|representation2
operator|.
name|getObject
argument_list|(
name|pair
operator|.
name|dataSet
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|pair
operator|.
name|cardinality
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testLoadSorted
parameter_list|()
block|{
specifier|final
name|JavaTypeFactoryImpl
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|()
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
literal|"empid"
argument_list|,
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"deptno"
argument_list|,
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|typeFactory
operator|.
name|createType
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerable
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|100
block|,
literal|10
block|,
literal|"Bill"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|200
block|,
literal|20
block|,
literal|"Eric"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|150
block|,
literal|10
block|,
literal|"Sebastian"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|160
block|,
literal|10
block|,
literal|"Theodore"
block|}
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ColumnLoader
argument_list|<
name|Object
index|[]
argument_list|>
name|loader
init|=
operator|new
name|ColumnLoader
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|(
name|typeFactory
argument_list|,
name|enumerable
argument_list|,
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|rowType
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|checkColumn
argument_list|(
name|loader
operator|.
name|representationValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ArrayTable
operator|.
name|RepresentationType
operator|.
name|BIT_SLICED_PRIMITIVE_ARRAY
argument_list|,
literal|"Column(representation=BitSlicedPrimitiveArray(ordinal=0, bitCount=8, primitive=INT, signed=false), value=[100, 150, 160, 200, 0, 0, 0, 0])"
argument_list|)
expr_stmt|;
name|checkColumn
argument_list|(
name|loader
operator|.
name|representationValues
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ArrayTable
operator|.
name|RepresentationType
operator|.
name|BIT_SLICED_PRIMITIVE_ARRAY
argument_list|,
literal|"Column(representation=BitSlicedPrimitiveArray(ordinal=1, bitCount=5, primitive=INT, signed=false), value=[10, 10, 10, 20, 0, 0, 0, 0, 0, 0, 0, 0])"
argument_list|)
expr_stmt|;
name|checkColumn
argument_list|(
name|loader
operator|.
name|representationValues
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|ArrayTable
operator|.
name|RepresentationType
operator|.
name|OBJECT_ARRAY
argument_list|,
literal|"Column(representation=ObjectArray(ordinal=2), value=[Bill, Sebastian, Theodore, Eric])"
argument_list|)
expr_stmt|;
block|}
comment|/** As {@link #testLoadSorted()} but column #1 is the unique column, not    * column #0. The algorithm needs to go back and permute the values of    * column #0 after it discovers that column #1 is unique and sorts by it. */
annotation|@
name|Test
specifier|public
name|void
name|testLoadSorted2
parameter_list|()
block|{
specifier|final
name|JavaTypeFactoryImpl
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryImpl
argument_list|()
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
literal|"deptno"
argument_list|,
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"empid"
argument_list|,
name|typeFactory
operator|.
name|createType
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|typeFactory
operator|.
name|createType
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|final
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerable
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|10
block|,
literal|100
block|,
literal|"Bill"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|20
block|,
literal|200
block|,
literal|"Eric"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|30
block|,
literal|150
block|,
literal|"Sebastian"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
literal|10
block|,
literal|160
block|,
literal|"Theodore"
block|}
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|ColumnLoader
argument_list|<
name|Object
index|[]
argument_list|>
name|loader
init|=
operator|new
name|ColumnLoader
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|(
name|typeFactory
argument_list|,
name|enumerable
argument_list|,
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|rowType
argument_list|)
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// Note that values have been sorted with {20, 200, Eric} last because the
comment|// value 200 is the highest value of empid, the unique column.
name|checkColumn
argument_list|(
name|loader
operator|.
name|representationValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ArrayTable
operator|.
name|RepresentationType
operator|.
name|BIT_SLICED_PRIMITIVE_ARRAY
argument_list|,
literal|"Column(representation=BitSlicedPrimitiveArray(ordinal=0, bitCount=5, primitive=INT, signed=false), value=[10, 30, 10, 20, 0, 0, 0, 0, 0, 0, 0, 0])"
argument_list|)
expr_stmt|;
name|checkColumn
argument_list|(
name|loader
operator|.
name|representationValues
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|ArrayTable
operator|.
name|RepresentationType
operator|.
name|BIT_SLICED_PRIMITIVE_ARRAY
argument_list|,
literal|"Column(representation=BitSlicedPrimitiveArray(ordinal=1, bitCount=8, primitive=INT, signed=false), value=[100, 150, 160, 200, 0, 0, 0, 0])"
argument_list|)
expr_stmt|;
name|checkColumn
argument_list|(
name|loader
operator|.
name|representationValues
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|,
name|ArrayTable
operator|.
name|RepresentationType
operator|.
name|OBJECT_ARRAY
argument_list|,
literal|"Column(representation=ObjectArray(ordinal=2), value=[Bill, Sebastian, Theodore, Eric])"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkColumn
parameter_list|(
name|ArrayTable
operator|.
name|Column
name|x
parameter_list|,
name|ArrayTable
operator|.
name|RepresentationType
name|expectedRepresentationType
parameter_list|,
name|String
name|expectedString
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expectedRepresentationType
argument_list|,
name|x
operator|.
name|representation
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedString
argument_list|,
name|x
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ArrayTableTest.java
end_comment

end_unit

