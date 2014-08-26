begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util14
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Utility functions for converting from one type to another  */
end_comment

begin_class
specifier|public
class|class
name|ConversionUtil
block|{
specifier|private
name|ConversionUtil
parameter_list|()
block|{
block|}
comment|//~ Static fields/initializers ---------------------------------------------
specifier|public
specifier|static
specifier|final
name|String
name|NATIVE_UTF16_CHARSET_NAME
init|=
operator|(
name|ByteOrder
operator|.
name|nativeOrder
argument_list|()
operator|==
name|ByteOrder
operator|.
name|BIG_ENDIAN
operator|)
condition|?
literal|"UTF-16BE"
else|:
literal|"UTF-16LE"
decl_stmt|;
comment|/**    * A constant string which can be used wherever a Java string containing    * Unicode characters is needed in a test. It spells 'anthropos' in Greek.    */
specifier|public
specifier|static
specifier|final
name|String
name|TEST_UNICODE_STRING
init|=
literal|"\u03B1\u03BD\u03B8\u03C1\u03C9\u03C0\u03BF\u03C2"
decl_stmt|;
comment|/**    * A constant string which can be used wherever a SQL literal containing    * Unicode escape characters is needed in a test. It spells 'anthropos' in    * Greek. The escape character is the SQL default (backslash); note that the    * backslash-doubling here is for Java only, so by the time the SQL parser    * gets it, there is only one backslash.    */
specifier|public
specifier|static
specifier|final
name|String
name|TEST_UNICODE_SQL_ESCAPED_LITERAL
init|=
literal|"\\03B1\\03BD\\03B8\\03C1\\03C9\\03C0\\03BF\\03C2"
decl_stmt|;
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Converts a byte array into a bit string or a hex string.    *    *<p>For example,<code>toStringFromByteArray(new byte[] {0xAB, 0xCD},    * 16)</code> returns<code>ABCD</code>.    */
specifier|public
specifier|static
name|String
name|toStringFromByteArray
parameter_list|(
name|byte
index|[]
name|value
parameter_list|,
name|int
name|radix
parameter_list|)
block|{
assert|assert
operator|(
literal|2
operator|==
name|radix
operator|)
operator|||
operator|(
literal|16
operator|==
name|radix
operator|)
operator|:
literal|"Make sure that the algorithm below works for your radix"
assert|;
if|if
condition|(
literal|0
operator|==
name|value
operator|.
name|length
condition|)
block|{
return|return
literal|""
return|;
block|}
name|int
name|trick
init|=
name|radix
operator|*
name|radix
decl_stmt|;
name|StringBuffer
name|ret
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|value
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|ret
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|trick
operator||
operator|(
literal|0x0ff
operator|&
name|value
index|[
name|i
index|]
operator|)
argument_list|,
name|radix
argument_list|)
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
operator|.
name|toString
argument_list|()
operator|.
name|toUpperCase
argument_list|()
return|;
block|}
comment|/**    * Converts a string into a byte array. The inverse of {@link    * #toStringFromByteArray(byte[], int)}.    */
specifier|public
specifier|static
name|byte
index|[]
name|toByteArrayFromString
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|radix
parameter_list|)
block|{
assert|assert
literal|16
operator|==
name|radix
operator|:
literal|"Specified string to byte array conversion not supported yet"
assert|;
assert|assert
operator|(
name|value
operator|.
name|length
argument_list|()
operator|%
literal|2
operator|)
operator|==
literal|0
operator|:
literal|"Hex binary string must contain even number of characters"
assert|;
name|byte
index|[]
name|ret
init|=
operator|new
name|byte
index|[
name|value
operator|.
name|length
argument_list|()
operator|/
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ret
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|digit1
init|=
name|Character
operator|.
name|digit
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
name|i
operator|*
literal|2
argument_list|)
argument_list|,
name|radix
argument_list|)
decl_stmt|;
name|int
name|digit2
init|=
name|Character
operator|.
name|digit
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
operator|(
name|i
operator|*
literal|2
operator|)
operator|+
literal|1
argument_list|)
argument_list|,
name|radix
argument_list|)
decl_stmt|;
assert|assert
operator|(
name|digit1
operator|!=
operator|-
literal|1
operator|)
operator|&&
operator|(
name|digit2
operator|!=
operator|-
literal|1
operator|)
operator|:
literal|"String could not be converted to byte array"
assert|;
name|ret
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|digit1
operator|*
name|radix
operator|)
operator|+
name|digit2
operator|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
comment|/**    * Converts an approximate value into a string, following the SQL 2003    * standard.    */
specifier|public
specifier|static
name|String
name|toStringFromApprox
parameter_list|(
name|double
name|d
parameter_list|,
name|boolean
name|isFloat
parameter_list|)
block|{
name|NumberFormat
name|nf
init|=
name|NumberUtil
operator|.
name|getApproxFormatter
argument_list|(
name|isFloat
argument_list|)
decl_stmt|;
return|return
name|nf
operator|.
name|format
argument_list|(
name|d
argument_list|)
return|;
block|}
comment|/**    * Converts a string into a boolean    */
specifier|public
specifier|static
name|Boolean
name|toBoolean
parameter_list|(
name|String
name|str
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|str
operator|=
name|str
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|str
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"TRUE"
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|TRUE
return|;
block|}
if|else if
condition|(
name|str
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"FALSE"
argument_list|)
condition|)
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
if|else if
condition|(
name|str
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"UNKNOWN"
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
throw|throw
name|RESOURCE
operator|.
name|invalidBoolean
argument_list|(
name|str
argument_list|)
operator|.
name|ex
argument_list|()
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ConversionUtil.java
end_comment

end_unit

