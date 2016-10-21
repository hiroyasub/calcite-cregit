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
name|avatica
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
name|avatica
operator|.
name|Meta
operator|.
name|Frame
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
name|proto
operator|.
name|Common
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
name|proto
operator|.
name|Common
operator|.
name|ColumnValue
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
name|proto
operator|.
name|Common
operator|.
name|TypedValue
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
name|ArrayList
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
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertArrayEquals
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  * Tests serialization of {@link Frame}.  */
end_comment

begin_class
specifier|public
class|class
name|FrameTest
block|{
specifier|private
specifier|static
specifier|final
name|TypedValue
name|NUMBER_VALUE
init|=
name|TypedValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setNumberValue
argument_list|(
literal|1
argument_list|)
operator|.
name|setType
argument_list|(
name|Common
operator|.
name|Rep
operator|.
name|LONG
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
name|void
name|serializeAndTestEquality
parameter_list|(
name|Frame
name|frame
parameter_list|)
block|{
name|Frame
name|frameCopy
init|=
name|Frame
operator|.
name|fromProto
argument_list|(
name|frame
operator|.
name|toProto
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|frame
operator|.
name|done
argument_list|,
name|frameCopy
operator|.
name|done
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|frame
operator|.
name|offset
argument_list|,
name|frameCopy
operator|.
name|offset
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|Object
argument_list|>
name|origRows
init|=
name|frame
operator|.
name|rows
decl_stmt|;
name|Iterable
argument_list|<
name|Object
argument_list|>
name|copiedRows
init|=
name|frameCopy
operator|.
name|rows
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Expected rows to both be null, or both be non-null"
argument_list|,
name|origRows
operator|==
literal|null
argument_list|,
name|copiedRows
operator|==
literal|null
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Object
argument_list|>
name|origIter
init|=
name|origRows
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|Object
argument_list|>
name|copiedIter
init|=
name|copiedRows
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|origIter
operator|.
name|hasNext
argument_list|()
operator|&&
name|copiedIter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Object
name|orig
init|=
name|origIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|Object
name|copy
init|=
name|copiedIter
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|orig
operator|==
literal|null
argument_list|,
name|copy
operator|==
literal|null
argument_list|)
expr_stmt|;
comment|// This is goofy, but it seems like an Array comes from the JDBC implementation but then
comment|// the resulting Frame has a List to support the Avatica typed Accessors
name|assertEquals
argument_list|(
name|Object
index|[]
operator|.
expr|class
argument_list|,
name|orig
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Expected List but got "
operator|+
name|copy
operator|.
name|getClass
argument_list|()
argument_list|,
name|copy
operator|instanceof
name|List
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|Object
argument_list|>
name|copyList
init|=
operator|(
name|List
argument_list|<
name|Object
argument_list|>
operator|)
name|copy
decl_stmt|;
name|assertArrayEquals
argument_list|(
operator|(
name|Object
index|[]
operator|)
name|orig
argument_list|,
name|copyList
operator|.
name|toArray
argument_list|(
operator|new
name|Object
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|origIter
operator|.
name|hasNext
argument_list|()
argument_list|,
name|copiedIter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmpty
parameter_list|()
block|{
name|serializeAndTestEquality
argument_list|(
name|Frame
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSingleRow
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"string"
block|,
name|Integer
operator|.
name|MAX_VALUE
block|,
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|Frame
name|singleRow
init|=
operator|new
name|Frame
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
name|rows
argument_list|)
decl_stmt|;
name|serializeAndTestEquality
argument_list|(
name|singleRow
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMultipleRows
parameter_list|()
block|{
name|ArrayList
argument_list|<
name|Object
argument_list|>
name|rows
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|rows
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"string"
block|,
name|Integer
operator|.
name|MAX_VALUE
block|,
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"gnirts"
block|,
literal|0
block|,
name|Long
operator|.
name|MIN_VALUE
block|}
argument_list|)
expr_stmt|;
name|rows
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|""
block|,
literal|null
block|,
name|Long
operator|.
name|MAX_VALUE
block|}
argument_list|)
expr_stmt|;
name|Frame
name|singleRow
init|=
operator|new
name|Frame
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
name|rows
argument_list|)
decl_stmt|;
name|serializeAndTestEquality
argument_list|(
name|singleRow
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testMalformedColumnValue
parameter_list|()
block|{
comment|// Invalid ColumnValue: has an array and scalar
specifier|final
name|ColumnValue
name|bothAttributesColumnValue
init|=
name|ColumnValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHasArrayValue
argument_list|(
literal|true
argument_list|)
operator|.
name|setScalarValue
argument_list|(
name|NUMBER_VALUE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// Note omission of setScalarValue(TypedValue).
specifier|final
name|ColumnValue
name|neitherAttributeColumnValue
init|=
name|ColumnValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setHasArrayValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|Frame
operator|.
name|validateColumnValue
argument_list|(
name|bothAttributesColumnValue
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Validating the ColumnValue should have failed as it has an array and scalar"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Pass
block|}
try|try
block|{
name|Frame
operator|.
name|validateColumnValue
argument_list|(
name|neitherAttributeColumnValue
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Validating the ColumnValue should have failed as it has neither an array nor scalar"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// Pass
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnValueBackwardsCompatibility
parameter_list|()
block|{
comment|// 1
specifier|final
name|ColumnValue
name|oldStyleScalarValue
init|=
name|ColumnValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|addValue
argument_list|(
name|NUMBER_VALUE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// [1, 1]
specifier|final
name|ColumnValue
name|oldStyleArrayValue
init|=
name|ColumnValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|addValue
argument_list|(
name|NUMBER_VALUE
argument_list|)
operator|.
name|addValue
argument_list|(
name|NUMBER_VALUE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|Frame
operator|.
name|isNewStyleColumn
argument_list|(
name|oldStyleScalarValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Frame
operator|.
name|isNewStyleColumn
argument_list|(
name|oldStyleArrayValue
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|scalar
init|=
name|Frame
operator|.
name|parseOldStyleColumn
argument_list|(
name|oldStyleScalarValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|scalar
argument_list|)
expr_stmt|;
name|Object
name|array
init|=
name|Frame
operator|.
name|parseOldStyleColumn
argument_list|(
name|oldStyleArrayValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|1L
argument_list|,
literal|1L
argument_list|)
argument_list|,
name|array
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testColumnValueParsing
parameter_list|()
block|{
comment|// 1
specifier|final
name|ColumnValue
name|scalarValue
init|=
name|ColumnValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setScalarValue
argument_list|(
name|NUMBER_VALUE
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|// [1, 1]
specifier|final
name|ColumnValue
name|arrayValue
init|=
name|ColumnValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|addArrayValue
argument_list|(
name|NUMBER_VALUE
argument_list|)
operator|.
name|addArrayValue
argument_list|(
name|NUMBER_VALUE
argument_list|)
operator|.
name|setHasArrayValue
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|Frame
operator|.
name|isNewStyleColumn
argument_list|(
name|scalarValue
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Frame
operator|.
name|isNewStyleColumn
argument_list|(
name|arrayValue
argument_list|)
argument_list|)
expr_stmt|;
name|Object
name|scalar
init|=
name|Frame
operator|.
name|parseColumn
argument_list|(
name|scalarValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|scalar
argument_list|)
expr_stmt|;
name|Object
name|array
init|=
name|Frame
operator|.
name|parseColumn
argument_list|(
name|arrayValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
literal|1L
argument_list|,
literal|1L
argument_list|)
argument_list|,
name|array
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeprecatedValueAttributeForScalars
parameter_list|()
block|{
comment|// Create a row with schema: [VARCHAR, INTEGER, DATE]
name|List
argument_list|<
name|Object
argument_list|>
name|rows
init|=
name|Collections
operator|.
expr|<
name|Object
operator|>
name|singletonList
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"string"
block|,
name|Integer
operator|.
name|MAX_VALUE
block|,
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|Meta
operator|.
name|Frame
name|frame
init|=
name|Meta
operator|.
name|Frame
operator|.
name|create
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
name|rows
argument_list|)
decl_stmt|;
comment|// Convert it to a protobuf
name|Common
operator|.
name|Frame
name|protoFrame
init|=
name|frame
operator|.
name|toProto
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|protoFrame
operator|.
name|getRowsCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get that row we created
name|Common
operator|.
name|Row
name|protoRow
init|=
name|protoFrame
operator|.
name|getRows
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// One row has many columns
name|List
argument_list|<
name|Common
operator|.
name|ColumnValue
argument_list|>
name|protoColumns
init|=
name|protoRow
operator|.
name|getValueList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|protoColumns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Verify that the scalar value is also present in the deprecated values attributes.
name|List
argument_list|<
name|Common
operator|.
name|TypedValue
argument_list|>
name|deprecatedValues
init|=
name|protoColumns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getValueList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|deprecatedValues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Common
operator|.
name|TypedValue
name|scalarValue
init|=
name|protoColumns
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getScalarValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|deprecatedValues
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|scalarValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDeprecatedValueAttributeForArrays
parameter_list|()
block|{
comment|// Create a row with schema: [VARCHAR, ARRAY]
name|List
argument_list|<
name|Object
argument_list|>
name|rows
init|=
name|Collections
operator|.
expr|<
name|Object
operator|>
name|singletonList
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|"string"
block|,
name|Arrays
operator|.
name|asList
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|,
literal|3
argument_list|)
block|}
argument_list|)
decl_stmt|;
name|Meta
operator|.
name|Frame
name|frame
init|=
name|Meta
operator|.
name|Frame
operator|.
name|create
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
name|rows
argument_list|)
decl_stmt|;
comment|// Convert it to a protobuf
name|Common
operator|.
name|Frame
name|protoFrame
init|=
name|frame
operator|.
name|toProto
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|protoFrame
operator|.
name|getRowsCount
argument_list|()
argument_list|)
expr_stmt|;
comment|// Get that row we created
name|Common
operator|.
name|Row
name|protoRow
init|=
name|protoFrame
operator|.
name|getRows
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// One row has many columns
name|List
argument_list|<
name|Common
operator|.
name|ColumnValue
argument_list|>
name|protoColumns
init|=
name|protoRow
operator|.
name|getValueList
argument_list|()
decl_stmt|;
comment|// We should have two columns
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|protoColumns
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Fetch the ARRAY column
name|Common
operator|.
name|ColumnValue
name|protoColumn
init|=
name|protoColumns
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// We should have the 3 ARRAY elements in the array_values attribute as well as the deprecated
comment|// values attribute.
name|List
argument_list|<
name|Common
operator|.
name|TypedValue
argument_list|>
name|deprecatedValues
init|=
name|protoColumn
operator|.
name|getValueList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|deprecatedValues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Column 2 should have an array_value"
argument_list|,
name|protoColumns
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getHasArrayValue
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Common
operator|.
name|TypedValue
argument_list|>
name|arrayValues
init|=
name|protoColumns
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getArrayValueList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|arrayValues
argument_list|,
name|deprecatedValues
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End FrameTest.java
end_comment

end_unit

