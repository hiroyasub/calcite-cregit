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
name|linq4j
operator|.
name|test
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
name|expressions
operator|.
name|Primitive
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link Primitive}.  */
end_comment

begin_class
specifier|public
class|class
name|PrimitiveTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testIsAssignableFrom
parameter_list|()
block|{
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|INT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|BYTE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|INT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|SHORT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|INT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|CHAR
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|INT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|INT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|SHORT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|INT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|LONG
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|BYTE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|LONG
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|SHORT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|LONG
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|CHAR
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|LONG
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|LONG
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
comment|// SHORT and CHAR cannot be assigned to each other
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|SHORT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|BYTE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|SHORT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|SHORT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|SHORT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|CHAR
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|SHORT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|SHORT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|CHAR
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|BYTE
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|CHAR
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|SHORT
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|Primitive
operator|.
name|CHAR
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|CHAR
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|CHAR
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|CHAR
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
comment|// cross-family assignments
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|INT
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|Primitive
operator|.
name|INT
operator|.
name|assignableFrom
argument_list|(
name|Primitive
operator|.
name|BOOLEAN
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testBox
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|String
operator|.
name|class
argument_list|,
name|Primitive
operator|.
name|box
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|Primitive
operator|.
name|box
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Integer
operator|.
name|class
argument_list|,
name|Primitive
operator|.
name|box
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|boolean
index|[]
operator|.
expr|class
argument_list|,
name|Primitive
operator|.
name|box
argument_list|(
name|boolean
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PrimitiveTest.java
end_comment

end_unit

