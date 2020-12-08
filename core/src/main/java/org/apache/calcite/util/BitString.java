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
name|util
package|;
end_package

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
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
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
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * String of bits.  *  *<p>A bit string logically consists of a set of '0' and '1' values, of a  * specified length. The length is preserved even if this means that the bit  * string has leading '0's.  *  *<p>You can create a bit string from a string of 0s and 1s  * ({@link #BitString(String, int)} or {@link #createFromBitString}), or from a  * string of hex digits ({@link #createFromHexString}). You can convert it to a  * byte array ({@link #getAsByteArray}), to a bit string ({@link #toBitString}),  * or to a hex string ({@link #toHexString}). A utility method  * {@link #toByteArrayFromBitString} converts a bit string directly to a byte  * array.  *  *<p>This class is immutable: once created, none of the methods modify the  * value.  */
end_comment

begin_class
specifier|public
class|class
name|BitString
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|String
name|bits
decl_stmt|;
specifier|private
specifier|final
name|int
name|bitCount
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|BitString
parameter_list|(
name|String
name|bits
parameter_list|,
name|int
name|bitCount
parameter_list|)
block|{
assert|assert
name|bits
operator|.
name|replace
argument_list|(
literal|"1"
argument_list|,
literal|""
argument_list|)
operator|.
name|replace
argument_list|(
literal|"0"
argument_list|,
literal|""
argument_list|)
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|:
literal|"bit string '"
operator|+
name|bits
operator|+
literal|"' contains digits other than {0, 1}"
assert|;
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|bitCount
operator|=
name|bitCount
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Creates a BitString representation out of a Hex String. Initial zeros are    * be preserved. Hex String is defined in the SQL standard to be a string    * with odd number of hex digits. An even number of hex digits is in the    * standard a Binary String.    *    * @param s a string, in hex notation    * @throws NumberFormatException if<code>s</code> is invalid.    */
specifier|public
specifier|static
name|BitString
name|createFromHexString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|bitCount
init|=
name|s
operator|.
name|length
argument_list|()
operator|*
literal|4
decl_stmt|;
name|String
name|bits
init|=
operator|(
name|bitCount
operator|==
literal|0
operator|)
condition|?
literal|""
else|:
operator|new
name|BigInteger
argument_list|(
name|s
argument_list|,
literal|16
argument_list|)
operator|.
name|toString
argument_list|(
literal|2
argument_list|)
decl_stmt|;
return|return
operator|new
name|BitString
argument_list|(
name|bits
argument_list|,
name|bitCount
argument_list|)
return|;
block|}
comment|/**    * Creates a BitString representation out of a Bit String. Initial zeros are    * be preserved.    *    * @param s a string of 0s and 1s.    * @throws NumberFormatException if<code>s</code> is invalid.    */
specifier|public
specifier|static
name|BitString
name|createFromBitString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|int
name|n
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|>
literal|0
condition|)
block|{
comment|// check that S is valid
name|Util
operator|.
name|discard
argument_list|(
operator|new
name|BigInteger
argument_list|(
name|s
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BitString
argument_list|(
name|s
argument_list|,
name|n
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toBitString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|bits
operator|.
name|hashCode
argument_list|()
operator|+
name|bitCount
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|==
name|this
operator|||
name|o
operator|instanceof
name|BitString
operator|&&
name|bits
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|BitString
operator|)
name|o
operator|)
operator|.
name|bits
argument_list|)
operator|&&
name|bitCount
operator|==
operator|(
operator|(
name|BitString
operator|)
name|o
operator|)
operator|.
name|bitCount
return|;
block|}
specifier|public
name|int
name|getBitCount
parameter_list|()
block|{
return|return
name|bitCount
return|;
block|}
specifier|public
name|byte
index|[]
name|getAsByteArray
parameter_list|()
block|{
return|return
name|toByteArrayFromBitString
argument_list|(
name|bits
argument_list|,
name|bitCount
argument_list|)
return|;
block|}
comment|/**    * Returns this bit string as a bit string, such as "10110".    */
specifier|public
name|String
name|toBitString
parameter_list|()
block|{
return|return
name|bits
return|;
block|}
comment|/**    * Converts this bit string to a hex string, such as "7AB".    */
specifier|public
name|String
name|toHexString
parameter_list|()
block|{
name|byte
index|[]
name|bytes
init|=
name|getAsByteArray
argument_list|()
decl_stmt|;
name|String
name|s
init|=
name|ConversionUtil
operator|.
name|toStringFromByteArray
argument_list|(
name|bytes
argument_list|,
literal|16
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|bitCount
operator|%
literal|8
condition|)
block|{
case|case
literal|1
case|:
comment|// B'1' -> X'1'
case|case
literal|2
case|:
comment|// B'10' -> X'2'
case|case
literal|3
case|:
comment|// B'100' -> X'4'
case|case
literal|4
case|:
comment|// B'1000' -> X'8'
return|return
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
case|case
literal|5
case|:
comment|// B'10000' -> X'10'
case|case
literal|6
case|:
comment|// B'100000' -> X'20'
case|case
literal|7
case|:
comment|// B'1000000' -> X'40'
case|case
literal|0
case|:
comment|// B'10000000' -> X'80', and B'' -> X''
return|return
name|s
return|;
default|default:
break|break;
block|}
if|if
condition|(
operator|(
name|bitCount
operator|%
literal|8
operator|)
operator|==
literal|4
condition|)
block|{
return|return
name|s
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|s
return|;
block|}
block|}
comment|/**    * Converts a bit string to an array of bytes.    */
specifier|public
specifier|static
name|byte
index|[]
name|toByteArrayFromBitString
parameter_list|(
name|String
name|bits
parameter_list|,
name|int
name|bitCount
parameter_list|)
block|{
if|if
condition|(
name|bitCount
operator|<
literal|0
condition|)
block|{
return|return
operator|new
name|byte
index|[
literal|0
index|]
return|;
block|}
name|int
name|byteCount
init|=
operator|(
name|bitCount
operator|+
literal|7
operator|)
operator|/
literal|8
decl_stmt|;
name|byte
index|[]
name|srcBytes
decl_stmt|;
if|if
condition|(
name|bits
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|BigInteger
name|bigInt
init|=
operator|new
name|BigInteger
argument_list|(
name|bits
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|srcBytes
operator|=
name|bigInt
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|srcBytes
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
name|byte
index|[]
name|dest
init|=
operator|new
name|byte
index|[
name|byteCount
index|]
decl_stmt|;
comment|// If the number started with 0s, the array won't be very long. Assume
comment|// that ret is already initialized to 0s, and just copy into the
comment|// RHS of it.
name|int
name|bytesToCopy
init|=
name|Math
operator|.
name|min
argument_list|(
name|byteCount
argument_list|,
name|srcBytes
operator|.
name|length
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|srcBytes
argument_list|,
name|srcBytes
operator|.
name|length
operator|-
name|bytesToCopy
argument_list|,
name|dest
argument_list|,
name|dest
operator|.
name|length
operator|-
name|bytesToCopy
argument_list|,
name|bytesToCopy
argument_list|)
expr_stmt|;
return|return
name|dest
return|;
block|}
comment|/**    * Concatenates some BitStrings. Concatenates all at once, not pairwise, to    * avoid string copies.    *    * @param args BitString[]    */
specifier|public
specifier|static
name|BitString
name|concat
parameter_list|(
name|List
argument_list|<
name|BitString
argument_list|>
name|args
parameter_list|)
block|{
if|if
condition|(
name|args
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
block|{
return|return
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|int
name|length
init|=
literal|0
decl_stmt|;
for|for
control|(
name|BitString
name|arg
range|:
name|args
control|)
block|{
name|length
operator|+=
name|arg
operator|.
name|bitCount
expr_stmt|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|BitString
name|arg1
range|:
name|args
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|arg1
operator|.
name|bits
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BitString
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/**    * Creates a BitString from an array of bytes.    *    * @param bytes Bytes    * @return BitString    */
specifier|public
specifier|static
name|BitString
name|createFromBytes
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|int
name|bitCount
init|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|bytes
argument_list|,
literal|"bytes"
argument_list|)
operator|.
name|length
operator|*
literal|8
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|bitCount
argument_list|)
decl_stmt|;
for|for
control|(
name|byte
name|b
range|:
name|bytes
control|)
block|{
specifier|final
name|String
name|s
init|=
name|Integer
operator|.
name|toBinaryString
argument_list|(
name|Byte
operator|.
name|toUnsignedInt
argument_list|(
name|b
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|s
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
literal|8
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
comment|// pad to length 8
block|}
name|sb
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|BitString
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|bitCount
argument_list|)
return|;
block|}
block|}
end_class

end_unit

