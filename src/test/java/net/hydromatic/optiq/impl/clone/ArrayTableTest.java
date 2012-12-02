begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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

begin_comment
comment|/**  * Unit test for {@link ArrayTable}.  */
end_comment

begin_class
specifier|public
class|class
name|ArrayTableTest
extends|extends
name|TestCase
block|{
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|Long
operator|.
name|toHexString
argument_list|(
literal|2619
argument_list|)
argument_list|)
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
block|}
end_class

begin_comment
comment|// End ArrayTableTest.java
end_comment

end_unit

