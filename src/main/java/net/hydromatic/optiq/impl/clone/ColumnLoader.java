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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|Table
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
name|impl
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
name|RelDataTypeField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Pair
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
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Column loader.  *  * @author jhyde */
end_comment

begin_class
class|class
name|ColumnLoader
parameter_list|<
name|T
parameter_list|>
block|{
specifier|static
specifier|final
name|int
index|[]
name|INT_B
init|=
block|{
literal|0x2
block|,
literal|0xC
block|,
literal|0xF0
block|,
literal|0xFF00
block|,
literal|0xFFFF0000
block|}
decl_stmt|;
specifier|static
specifier|final
name|int
index|[]
name|INT_S
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|16
block|}
decl_stmt|;
specifier|static
specifier|final
name|long
index|[]
name|LONG_B
init|=
block|{
literal|0x2
block|,
literal|0xC
block|,
literal|0xF0
block|,
literal|0xFF00
block|,
literal|0xFFFF0000
block|,
literal|0xFFFFFFFF00000000L
block|}
decl_stmt|;
specifier|static
specifier|final
name|int
index|[]
name|LONG_S
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|16
block|,
literal|32
block|}
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|ArrayTable
operator|.
name|Representation
argument_list|,
name|Object
argument_list|>
argument_list|>
name|representationValues
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|ArrayTable
operator|.
name|Representation
argument_list|,
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
comment|/** Creates a column loader, and performs the load. */
name|ColumnLoader
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Table
argument_list|<
name|T
argument_list|>
name|sourceTable
parameter_list|,
name|RelDataType
name|elementType
parameter_list|)
block|{
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
name|sourceTable
operator|.
name|into
argument_list|(
name|list
argument_list|)
expr_stmt|;
name|load
argument_list|(
name|elementType
argument_list|)
expr_stmt|;
block|}
specifier|static
name|int
name|nextPowerOf2
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|v
operator|--
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|1
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|2
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|4
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|8
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|16
expr_stmt|;
name|v
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
specifier|static
name|long
name|nextPowerOf2
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|v
operator|--
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|1
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|2
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|4
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|8
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|16
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|32
expr_stmt|;
name|v
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
specifier|static
name|int
name|log2
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|int
name|r
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|4
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|(
name|v
operator|&
name|INT_B
index|[
name|i
index|]
operator|)
operator|!=
literal|0
condition|)
block|{
name|v
operator|>>=
name|INT_S
index|[
name|i
index|]
expr_stmt|;
name|r
operator||=
name|INT_S
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
specifier|static
name|int
name|log2
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|int
name|r
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|(
name|v
operator|&
name|LONG_B
index|[
name|i
index|]
operator|)
operator|!=
literal|0
condition|)
block|{
name|v
operator|>>=
name|LONG_S
index|[
name|i
index|]
expr_stmt|;
name|r
operator||=
name|LONG_S
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|void
name|load
parameter_list|(
specifier|final
name|RelDataType
name|elementType
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Type
argument_list|>
name|types
init|=
operator|new
name|AbstractList
argument_list|<
name|Type
argument_list|>
argument_list|()
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|elementType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|public
name|Type
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|fields
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|Integer
argument_list|,
name|Type
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|types
argument_list|)
control|)
block|{
specifier|final
name|int
name|i
init|=
name|pair
operator|.
name|left
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|?
argument_list|>
name|sliceList
init|=
name|types
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|list
else|:
operator|new
name|AbstractList
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Object
index|[]
operator|)
name|list
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|)
index|[
name|i
index|]
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|Type
name|type
init|=
name|pair
operator|.
name|right
decl_stmt|;
specifier|final
name|Class
name|clazz
init|=
name|type
operator|instanceof
name|Class
condition|?
operator|(
name|Class
operator|)
name|type
else|:
name|Object
operator|.
name|class
decl_stmt|;
name|ValueSet
name|valueSet
init|=
operator|new
name|ValueSet
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|sliceList
control|)
block|{
name|valueSet
operator|.
name|add
argument_list|(
operator|(
name|Comparable
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
name|representationValues
operator|.
name|add
argument_list|(
name|valueSet
operator|.
name|freeze
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Set of values of a column, created during the load process, and converted      * to a serializable (and more compact) form before load completes.      */
specifier|static
class|class
name|ValueSet
block|{
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Comparable
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|Comparable
argument_list|,
name|Comparable
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<
name|Comparable
argument_list|>
argument_list|()
decl_stmt|;
name|Comparable
name|min
decl_stmt|;
name|Comparable
name|max
decl_stmt|;
name|boolean
name|containsNull
decl_stmt|;
name|ValueSet
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
block|}
name|void
name|add
parameter_list|(
name|Comparable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Comparable
name|old
init|=
name|e
decl_stmt|;
name|e
operator|=
name|map
operator|.
name|get
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|e
operator|=
name|old
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
if|if
condition|(
name|min
operator|==
literal|null
operator|||
name|min
operator|.
name|compareTo
argument_list|(
name|e
argument_list|)
operator|>
literal|0
condition|)
block|{
name|min
operator|=
name|e
expr_stmt|;
block|}
comment|//noinspection unchecked
if|if
condition|(
name|max
operator|==
literal|null
operator|||
name|max
operator|.
name|compareTo
argument_list|(
name|e
argument_list|)
operator|<
literal|0
condition|)
block|{
name|max
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|containsNull
operator|=
literal|true
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|Pair
argument_list|<
name|ArrayTable
operator|.
name|Representation
argument_list|,
name|Object
argument_list|>
name|freeze
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|ArrayTable
operator|.
name|Representation
name|representation
init|=
name|chooseRep
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|representation
argument_list|,
name|representation
operator|.
name|freeze
argument_list|(
name|this
argument_list|)
argument_list|)
return|;
block|}
name|ArrayTable
operator|.
name|Representation
name|chooseRep
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|Primitive
name|primitive
init|=
name|Primitive
operator|.
name|of
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|Primitive
name|boxPrimitive
init|=
name|Primitive
operator|.
name|ofBox
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|Primitive
name|p
init|=
name|primitive
operator|!=
literal|null
condition|?
name|primitive
else|:
name|boxPrimitive
decl_stmt|;
if|if
condition|(
operator|!
name|containsNull
operator|&&
name|p
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|p
condition|)
block|{
case|case
name|FLOAT
case|:
case|case
name|DOUBLE
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|p
argument_list|,
name|p
argument_list|)
return|;
case|case
name|OTHER
case|:
case|case
name|VOID
case|:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"wtf?!"
argument_list|)
throw|;
block|}
return|return
name|chooseFixedRep
argument_list|(
name|ordinal
argument_list|,
name|p
argument_list|,
name|toLong
argument_list|(
name|min
argument_list|)
argument_list|,
name|toLong
argument_list|(
name|max
argument_list|)
argument_list|)
return|;
block|}
comment|// We don't want to use a dictionary if:
comment|// (a) there are so many values that an object pointer (with one
comment|//     indirection) has about as many bits as a code (with two
comment|//     indirections); or
comment|// (b) if there are very few copies of each value.
comment|// The condition kind of captures this, but needs to be tuned.
specifier|final
name|int
name|codeBitCount
init|=
name|log2
argument_list|(
name|nextPowerOf2
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|codeBitCount
operator|<
literal|10
operator|&&
name|values
operator|.
name|size
argument_list|()
operator|>
literal|2000
condition|)
block|{
specifier|final
name|ArrayTable
operator|.
name|Representation
name|representation
init|=
name|chooseFixedRep
argument_list|(
operator|-
literal|1
argument_list|,
name|Primitive
operator|.
name|INT
argument_list|,
literal|0
argument_list|,
name|map
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|ArrayTable
operator|.
name|ObjectDictionary
argument_list|(
name|ordinal
argument_list|,
name|representation
argument_list|)
return|;
block|}
return|return
operator|new
name|ArrayTable
operator|.
name|ObjectArray
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
specifier|private
name|long
name|toLong
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
comment|// We treat Boolean and Character as if they were subclasses of
comment|// Number but actually they are not.
if|if
condition|(
name|o
operator|instanceof
name|Boolean
condition|)
block|{
return|return
operator|(
operator|(
name|Boolean
operator|)
name|o
condition|?
literal|1
else|:
literal|0
operator|)
return|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Character
condition|)
block|{
return|return
operator|(
operator|(
name|Character
operator|)
name|o
operator|)
operator|.
name|charValue
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
block|}
comment|/** Chooses a representation for a fixed-precision primitive type          * (boolean, byte, char, short, int, long).          *          * @param ordinal Ordinal of this column in table          * @param p Type that values are to be returned as (not necessarily the          *     same as they will be stored)          * @param min Minimum value to be encoded          * @param max Maximum value to be encoded (inclusive)          */
specifier|private
name|ArrayTable
operator|.
name|Representation
name|chooseFixedRep
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Primitive
name|p
parameter_list|,
name|long
name|min
parameter_list|,
name|long
name|max
parameter_list|)
block|{
name|int
name|bitCountMax
init|=
name|log2
argument_list|(
name|nextPowerOf2
argument_list|(
name|abs2
argument_list|(
name|max
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|bitCountMin
init|=
name|log2
argument_list|(
name|nextPowerOf2
argument_list|(
name|abs2
argument_list|(
name|min
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|bitCount
init|=
name|Math
operator|.
name|max
argument_list|(
name|bitCountMin
argument_list|,
name|bitCountMax
argument_list|)
operator|+
literal|1
decl_stmt|;
comment|// 1 for sign
comment|// Must be a fixed point primitive.
if|if
condition|(
name|bitCount
operator|>
literal|21
operator|&&
name|bitCount
operator|<
literal|32
condition|)
block|{
comment|// Can't get more than 2 into a word.
name|bitCount
operator|=
literal|32
expr_stmt|;
block|}
if|if
condition|(
name|bitCount
operator|>=
literal|33
operator|&&
name|bitCount
operator|<
literal|64
condition|)
block|{
comment|// Can't get more than one into a word.
name|bitCount
operator|=
literal|64
expr_stmt|;
block|}
switch|switch
condition|(
name|bitCount
condition|)
block|{
case|case
literal|8
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|Primitive
operator|.
name|BYTE
argument_list|,
name|p
argument_list|)
return|;
case|case
literal|16
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|Primitive
operator|.
name|SHORT
argument_list|,
name|p
argument_list|)
return|;
case|case
literal|32
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|Primitive
operator|.
name|INT
argument_list|,
name|p
argument_list|)
return|;
case|case
literal|64
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|Primitive
operator|.
name|LONG
argument_list|,
name|p
argument_list|)
return|;
default|default:
return|return
operator|new
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|bitCount
argument_list|,
name|p
argument_list|)
return|;
block|}
block|}
comment|/** Two's complement absolute on int value. */
specifier|private
specifier|static
name|int
name|abs2
parameter_list|(
name|int
name|v
parameter_list|)
block|{
comment|// -128 becomes +127
return|return
name|v
operator|<
literal|0
condition|?
operator|~
name|v
else|:
name|v
return|;
block|}
comment|/** Two's complement absolute on long value. */
specifier|private
specifier|static
name|long
name|abs2
parameter_list|(
name|long
name|v
parameter_list|)
block|{
comment|// -128 becomes +127
return|return
name|v
operator|<
literal|0
condition|?
operator|~
name|v
else|:
name|v
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ColumnLoader.java
end_comment

end_unit

