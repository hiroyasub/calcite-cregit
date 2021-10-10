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
operator|.
name|schemata
operator|.
name|catchall
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
name|linq4j
operator|.
name|Enumerable
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
name|Linq4j
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
name|tree
operator|.
name|Primitive
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
name|schemata
operator|.
name|hr
operator|.
name|Employee
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
name|schemata
operator|.
name|hr
operator|.
name|HrSchema
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
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
name|sql
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Timestamp
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
name|BitSet
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
name|List
import|;
end_import

begin_comment
comment|/**  * Object whose fields are relations. Called "catch-all" because it's OK  * if tests add new fields.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedVariable"
argument_list|)
specifier|public
class|class
name|CatchallSchema
block|{
specifier|public
specifier|final
name|Enumerable
argument_list|<
name|Employee
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
name|HrSchema
argument_list|()
operator|.
name|emps
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Employee
argument_list|>
name|list
init|=
name|Arrays
operator|.
name|asList
argument_list|(
operator|new
name|HrSchema
argument_list|()
operator|.
name|emps
argument_list|)
decl_stmt|;
specifier|public
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|(
literal|1
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"JavaUtilDate"
argument_list|)
specifier|public
specifier|final
name|EveryType
index|[]
name|everyTypes
init|=
block|{
operator|new
name|EveryType
argument_list|(
literal|false
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|,
operator|(
name|char
operator|)
literal|0
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|,
literal|0
argument_list|,
literal|0L
argument_list|,
literal|0F
argument_list|,
literal|0D
argument_list|,
literal|false
argument_list|,
operator|(
name|byte
operator|)
literal|0
argument_list|,
operator|(
name|char
operator|)
literal|0
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|,
literal|0
argument_list|,
literal|0L
argument_list|,
literal|0F
argument_list|,
literal|0D
argument_list|,
operator|new
name|java
operator|.
name|sql
operator|.
name|Date
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Time
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Timestamp
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|Date
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|"1"
argument_list|,
name|BigDecimal
operator|.
name|ZERO
argument_list|)
block|,
operator|new
name|EveryType
argument_list|(
literal|true
argument_list|,
name|Byte
operator|.
name|MAX_VALUE
argument_list|,
name|Character
operator|.
name|MAX_VALUE
argument_list|,
name|Short
operator|.
name|MAX_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|Float
operator|.
name|MAX_VALUE
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,   }
decl_stmt|;
specifier|public
specifier|final
name|AllPrivate
index|[]
name|allPrivates
init|=
block|{
operator|new
name|AllPrivate
argument_list|()
block|}
decl_stmt|;
specifier|public
specifier|final
name|BadType
index|[]
name|badTypes
init|=
block|{
operator|new
name|BadType
argument_list|()
block|}
decl_stmt|;
specifier|public
specifier|final
name|Employee
index|[]
name|prefixEmps
init|=
block|{
operator|new
name|Employee
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|,
literal|"A"
argument_list|,
literal|0f
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|,
literal|"Ab"
argument_list|,
literal|0f
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|,
literal|"Abc"
argument_list|,
literal|0f
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|4
argument_list|,
literal|10
argument_list|,
literal|"Abd"
argument_list|,
literal|0f
argument_list|,
literal|null
argument_list|)
block|,   }
decl_stmt|;
specifier|public
specifier|final
name|Integer
index|[]
name|primesBoxed
init|=
block|{
literal|1
block|,
literal|3
block|,
literal|5
block|}
decl_stmt|;
specifier|public
specifier|final
name|int
index|[]
name|primes
init|=
block|{
literal|1
block|,
literal|3
block|,
literal|5
block|}
decl_stmt|;
specifier|public
specifier|final
name|IntHolder
index|[]
name|primesCustomBoxed
init|=
block|{
operator|new
name|IntHolder
argument_list|(
literal|1
argument_list|)
block|,
operator|new
name|IntHolder
argument_list|(
literal|3
argument_list|)
block|,
operator|new
name|IntHolder
argument_list|(
literal|5
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|final
name|IntAndString
index|[]
name|nullables
init|=
block|{
operator|new
name|IntAndString
argument_list|(
literal|1
argument_list|,
literal|"A"
argument_list|)
block|,
operator|new
name|IntAndString
argument_list|(
literal|2
argument_list|,
literal|"B"
argument_list|)
block|,
operator|new
name|IntAndString
argument_list|(
literal|2
argument_list|,
literal|"C"
argument_list|)
block|,
operator|new
name|IntAndString
argument_list|(
literal|3
argument_list|,
literal|null
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|final
name|IntAndString
index|[]
name|bools
init|=
block|{
operator|new
name|IntAndString
argument_list|(
literal|1
argument_list|,
literal|"T"
argument_list|)
block|,
operator|new
name|IntAndString
argument_list|(
literal|2
argument_list|,
literal|"F"
argument_list|)
block|,
operator|new
name|IntAndString
argument_list|(
literal|3
argument_list|,
literal|null
argument_list|)
block|}
decl_stmt|;
specifier|private
specifier|static
name|boolean
name|isNumeric
parameter_list|(
name|Class
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|Primitive
operator|.
name|flavor
argument_list|(
name|type
argument_list|)
condition|)
block|{
case|case
name|BOX
case|:
return|return
name|Primitive
operator|.
name|ofBox
argument_list|(
name|type
argument_list|)
operator|.
name|isNumeric
argument_list|()
return|;
case|case
name|PRIMITIVE
case|:
return|return
name|Primitive
operator|.
name|of
argument_list|(
name|type
argument_list|)
operator|.
name|isNumeric
argument_list|()
return|;
default|default:
return|return
name|Number
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|type
argument_list|)
return|;
comment|// e.g. BigDecimal
block|}
block|}
comment|/** Record that has a field of every interesting type. */
specifier|public
specifier|static
class|class
name|EveryType
block|{
specifier|public
specifier|final
name|boolean
name|primitiveBoolean
decl_stmt|;
specifier|public
specifier|final
name|byte
name|primitiveByte
decl_stmt|;
specifier|public
specifier|final
name|char
name|primitiveChar
decl_stmt|;
specifier|public
specifier|final
name|short
name|primitiveShort
decl_stmt|;
specifier|public
specifier|final
name|int
name|primitiveInt
decl_stmt|;
specifier|public
specifier|final
name|long
name|primitiveLong
decl_stmt|;
specifier|public
specifier|final
name|float
name|primitiveFloat
decl_stmt|;
specifier|public
specifier|final
name|double
name|primitiveDouble
decl_stmt|;
specifier|public
specifier|final
name|Boolean
name|wrapperBoolean
decl_stmt|;
specifier|public
specifier|final
name|Byte
name|wrapperByte
decl_stmt|;
specifier|public
specifier|final
name|Character
name|wrapperCharacter
decl_stmt|;
specifier|public
specifier|final
name|Short
name|wrapperShort
decl_stmt|;
specifier|public
specifier|final
name|Integer
name|wrapperInteger
decl_stmt|;
specifier|public
specifier|final
name|Long
name|wrapperLong
decl_stmt|;
specifier|public
specifier|final
name|Float
name|wrapperFloat
decl_stmt|;
specifier|public
specifier|final
name|Double
name|wrapperDouble
decl_stmt|;
specifier|public
specifier|final
name|java
operator|.
name|sql
operator|.
name|Date
name|sqlDate
decl_stmt|;
specifier|public
specifier|final
name|Time
name|sqlTime
decl_stmt|;
specifier|public
specifier|final
name|Timestamp
name|sqlTimestamp
decl_stmt|;
specifier|public
specifier|final
name|Date
name|utilDate
decl_stmt|;
specifier|public
specifier|final
name|String
name|string
decl_stmt|;
specifier|public
specifier|final
name|BigDecimal
name|bigDecimal
decl_stmt|;
specifier|public
name|EveryType
parameter_list|(
name|boolean
name|primitiveBoolean
parameter_list|,
name|byte
name|primitiveByte
parameter_list|,
name|char
name|primitiveChar
parameter_list|,
name|short
name|primitiveShort
parameter_list|,
name|int
name|primitiveInt
parameter_list|,
name|long
name|primitiveLong
parameter_list|,
name|float
name|primitiveFloat
parameter_list|,
name|double
name|primitiveDouble
parameter_list|,
name|Boolean
name|wrapperBoolean
parameter_list|,
name|Byte
name|wrapperByte
parameter_list|,
name|Character
name|wrapperCharacter
parameter_list|,
name|Short
name|wrapperShort
parameter_list|,
name|Integer
name|wrapperInteger
parameter_list|,
name|Long
name|wrapperLong
parameter_list|,
name|Float
name|wrapperFloat
parameter_list|,
name|Double
name|wrapperDouble
parameter_list|,
name|java
operator|.
name|sql
operator|.
name|Date
name|sqlDate
parameter_list|,
name|Time
name|sqlTime
parameter_list|,
name|Timestamp
name|sqlTimestamp
parameter_list|,
name|Date
name|utilDate
parameter_list|,
name|String
name|string
parameter_list|,
name|BigDecimal
name|bigDecimal
parameter_list|)
block|{
name|this
operator|.
name|primitiveBoolean
operator|=
name|primitiveBoolean
expr_stmt|;
name|this
operator|.
name|primitiveByte
operator|=
name|primitiveByte
expr_stmt|;
name|this
operator|.
name|primitiveChar
operator|=
name|primitiveChar
expr_stmt|;
name|this
operator|.
name|primitiveShort
operator|=
name|primitiveShort
expr_stmt|;
name|this
operator|.
name|primitiveInt
operator|=
name|primitiveInt
expr_stmt|;
name|this
operator|.
name|primitiveLong
operator|=
name|primitiveLong
expr_stmt|;
name|this
operator|.
name|primitiveFloat
operator|=
name|primitiveFloat
expr_stmt|;
name|this
operator|.
name|primitiveDouble
operator|=
name|primitiveDouble
expr_stmt|;
name|this
operator|.
name|wrapperBoolean
operator|=
name|wrapperBoolean
expr_stmt|;
name|this
operator|.
name|wrapperByte
operator|=
name|wrapperByte
expr_stmt|;
name|this
operator|.
name|wrapperCharacter
operator|=
name|wrapperCharacter
expr_stmt|;
name|this
operator|.
name|wrapperShort
operator|=
name|wrapperShort
expr_stmt|;
name|this
operator|.
name|wrapperInteger
operator|=
name|wrapperInteger
expr_stmt|;
name|this
operator|.
name|wrapperLong
operator|=
name|wrapperLong
expr_stmt|;
name|this
operator|.
name|wrapperFloat
operator|=
name|wrapperFloat
expr_stmt|;
name|this
operator|.
name|wrapperDouble
operator|=
name|wrapperDouble
expr_stmt|;
name|this
operator|.
name|sqlDate
operator|=
name|sqlDate
expr_stmt|;
name|this
operator|.
name|sqlTime
operator|=
name|sqlTime
expr_stmt|;
name|this
operator|.
name|sqlTimestamp
operator|=
name|sqlTimestamp
expr_stmt|;
name|this
operator|.
name|utilDate
operator|=
name|utilDate
expr_stmt|;
name|this
operator|.
name|string
operator|=
name|string
expr_stmt|;
name|this
operator|.
name|bigDecimal
operator|=
name|bigDecimal
expr_stmt|;
block|}
specifier|public
specifier|static
name|Enumerable
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|EveryType
operator|.
name|class
operator|.
name|getFields
argument_list|()
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Enumerable
argument_list|<
name|Field
argument_list|>
name|numericFields
parameter_list|()
block|{
return|return
name|fields
argument_list|()
operator|.
name|where
argument_list|(
name|v1
lambda|->
name|isNumeric
argument_list|(
name|v1
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** All field are private, therefore the resulting record has no fields. */
specifier|public
specifier|static
class|class
name|AllPrivate
block|{
specifier|private
specifier|final
name|int
name|x
init|=
literal|0
decl_stmt|;
block|}
comment|/** Table that has a field that cannot be recognized as a SQL type. */
specifier|public
specifier|static
class|class
name|BadType
block|{
specifier|public
specifier|final
name|int
name|integer
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
name|BitSet
name|bitSet
init|=
operator|new
name|BitSet
argument_list|(
literal|0
argument_list|)
decl_stmt|;
block|}
comment|/** Table that has integer and string fields. */
specifier|public
specifier|static
class|class
name|IntAndString
block|{
specifier|public
specifier|final
name|int
name|id
decl_stmt|;
specifier|public
specifier|final
name|String
name|value
decl_stmt|;
specifier|public
name|IntAndString
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
comment|/**    * Custom java class that holds just a single field.    */
specifier|public
specifier|static
class|class
name|IntHolder
block|{
specifier|public
specifier|final
name|int
name|value
decl_stmt|;
specifier|public
name|IntHolder
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
