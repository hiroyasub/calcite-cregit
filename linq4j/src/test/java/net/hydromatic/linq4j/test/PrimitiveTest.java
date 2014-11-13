begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for {@link Primitive}.  */
end_comment

begin_class
specifier|public
class|class
name|PrimitiveTest
block|{
annotation|@
name|Test
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
annotation|@
name|Test
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
annotation|@
name|Test
specifier|public
name|void
name|testOfBox
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Primitive
operator|.
name|INT
argument_list|,
name|Primitive
operator|.
name|ofBox
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Primitive
operator|.
name|ofBox
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Primitive
operator|.
name|ofBox
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Primitive
operator|.
name|ofBox
argument_list|(
name|Integer
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOfBoxOr
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|Primitive
operator|.
name|INT
argument_list|,
name|Primitive
operator|.
name|ofBox
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Primitive
operator|.
name|ofBox
argument_list|(
name|int
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Primitive
operator|.
name|ofBox
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|Primitive
operator|.
name|ofBox
argument_list|(
name|Integer
index|[]
operator|.
expr|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the {@link Primitive#number(Number)} method. */
annotation|@
name|Test
specifier|public
name|void
name|testNumber
parameter_list|()
block|{
name|Number
name|number
init|=
name|Primitive
operator|.
name|SHORT
operator|.
name|number
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|number
operator|instanceof
name|Short
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|number
operator|.
name|shortValue
argument_list|()
argument_list|)
expr_stmt|;
name|number
operator|=
name|Primitive
operator|.
name|FLOAT
operator|.
name|number
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|number
operator|instanceof
name|Float
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2.0d
argument_list|,
name|number
operator|.
name|doubleValue
argument_list|()
argument_list|,
literal|0d
argument_list|)
expr_stmt|;
try|try
block|{
name|number
operator|=
name|Primitive
operator|.
name|INT
operator|.
name|number
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|number
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
comment|// ok
block|}
comment|// not a number
try|try
block|{
name|number
operator|=
name|Primitive
operator|.
name|CHAR
operator|.
name|number
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|number
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|// ok
block|}
comment|// not a number
try|try
block|{
name|number
operator|=
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|number
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|number
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|e
parameter_list|)
block|{
comment|// ok
block|}
block|}
comment|/** Test for {@link Primitive#send(net.hydromatic.linq4j.expressions.Primitive.Source, net.hydromatic.linq4j.expressions.Primitive.Sink)}. */
annotation|@
name|Test
specifier|public
name|void
name|testSendSource
parameter_list|()
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Object
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Primitive
name|primitive
range|:
name|Primitive
operator|.
name|values
argument_list|()
control|)
block|{
name|primitive
operator|.
name|send
argument_list|(
operator|new
name|Primitive
operator|.
name|Source
argument_list|()
block|{
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|byte
name|getByte
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|byte
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|char
name|getChar
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|char
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|short
name|getShort
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|short
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|int
name|getInt
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|int
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|long
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|float
name|getFloat
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|float
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|double
name|getDouble
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|double
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
specifier|public
name|Object
name|getObject
parameter_list|()
block|{
name|list
operator|.
name|add
argument_list|(
name|Object
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
argument_list|,
operator|new
name|Primitive
operator|.
name|Sink
argument_list|()
block|{
specifier|public
name|void
name|set
parameter_list|(
name|boolean
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|boolean
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|byte
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|byte
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|char
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|char
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|short
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|short
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|int
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|long
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|float
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|float
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|double
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|double
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|set
parameter_list|(
name|Object
name|v
parameter_list|)
block|{
name|list
operator|.
name|add
argument_list|(
name|Object
operator|.
name|class
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"[boolean, boolean, true, "
operator|+
literal|"byte, byte, 0, "
operator|+
literal|"char, char, \u0000, "
operator|+
literal|"short, short, 0, "
operator|+
literal|"int, int, 0, "
operator|+
literal|"long, long, 0, "
operator|+
literal|"float, float, 0.0, "
operator|+
literal|"double, double, 0.0, "
operator|+
literal|"class java.lang.Object, class java.lang.Object, 0, "
operator|+
literal|"class java.lang.Object, class java.lang.Object, 0]"
argument_list|,
name|list
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Test for {@link Primitive#permute(Object, int[])}. */
annotation|@
name|Test
specifier|public
name|void
name|testPermute
parameter_list|()
block|{
name|char
index|[]
name|chars
init|=
block|{
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|,
literal|'g'
block|}
decl_stmt|;
name|int
index|[]
name|sources
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|3
block|,
literal|4
block|,
literal|5
block|,
literal|6
block|,
literal|0
block|}
decl_stmt|;
specifier|final
name|Object
name|permute
init|=
name|Primitive
operator|.
name|CHAR
operator|.
name|permute
argument_list|(
name|chars
argument_list|,
name|sources
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|permute
operator|instanceof
name|char
index|[]
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"bcdefga"
argument_list|,
operator|new
name|String
argument_list|(
operator|(
name|char
index|[]
operator|)
name|permute
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for {@link Primitive#arrayToString(Object)}. */
annotation|@
name|Test
specifier|public
name|void
name|testArrayToString
parameter_list|()
block|{
name|char
index|[]
name|chars
init|=
block|{
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|,
literal|'g'
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[a, b, c, d, e, f, g]"
argument_list|,
name|Primitive
operator|.
name|CHAR
operator|.
name|arrayToString
argument_list|(
name|chars
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test for {@link Primitive#sortArray(Object)}. */
annotation|@
name|Test
specifier|public
name|void
name|testArraySort
parameter_list|()
block|{
name|char
index|[]
name|chars
init|=
block|{
literal|'m'
block|,
literal|'o'
block|,
literal|'n'
block|,
literal|'o'
block|,
literal|'l'
block|,
literal|'a'
block|,
literal|'k'
block|,
literal|'e'
block|}
decl_stmt|;
name|Primitive
operator|.
name|CHAR
operator|.
name|sortArray
argument_list|(
name|chars
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[a, e, k, l, m, n, o, o]"
argument_list|,
name|Primitive
operator|.
name|CHAR
operator|.
name|arrayToString
argument_list|(
name|chars
argument_list|)
argument_list|)
expr_stmt|;
comment|// mixed true and false
name|boolean
index|[]
name|booleans0
init|=
block|{
literal|true
block|,
literal|false
block|,
literal|true
block|,
literal|true
block|,
literal|false
block|}
decl_stmt|;
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|sortArray
argument_list|(
name|booleans0
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[false, false, true, true, true]"
argument_list|,
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|arrayToString
argument_list|(
name|booleans0
argument_list|)
argument_list|)
expr_stmt|;
comment|// all false
name|boolean
index|[]
name|booleans1
init|=
block|{
literal|false
block|,
literal|false
block|,
literal|false
block|,
literal|false
block|,
literal|false
block|}
decl_stmt|;
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|sortArray
argument_list|(
name|booleans1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[false, false, false, false, false]"
argument_list|,
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|arrayToString
argument_list|(
name|booleans1
argument_list|)
argument_list|)
expr_stmt|;
comment|// all true
name|boolean
index|[]
name|booleans2
init|=
block|{
literal|true
block|,
literal|true
block|,
literal|true
block|,
literal|true
block|,
literal|true
block|}
decl_stmt|;
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|sortArray
argument_list|(
name|booleans2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[true, true, true, true, true]"
argument_list|,
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|arrayToString
argument_list|(
name|booleans2
argument_list|)
argument_list|)
expr_stmt|;
comment|// empty
name|boolean
index|[]
name|booleans3
init|=
block|{}
decl_stmt|;
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|sortArray
argument_list|(
name|booleans3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|arrayToString
argument_list|(
name|booleans3
argument_list|)
argument_list|)
expr_stmt|;
comment|// ranges specified
name|boolean
index|[]
name|booleans4
init|=
block|{
literal|true
block|,
literal|true
block|,
literal|false
block|,
literal|false
block|,
literal|true
block|,
literal|false
block|,
literal|false
block|}
decl_stmt|;
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|sortArray
argument_list|(
name|booleans4
argument_list|,
literal|1
argument_list|,
literal|6
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[true, false, false, false, true, true, false]"
argument_list|,
name|Primitive
operator|.
name|BOOLEAN
operator|.
name|arrayToString
argument_list|(
name|booleans4
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

