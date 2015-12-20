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
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * ConnectStringParser is a utility class that parses or creates a JDBC connect  * string according to the OLE DB connect string syntax described at<a  * href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/oledb/htm/oledbconnectionstringsyntax.asp">  * OLE DB Connection String Syntax</a>.  *  *<p>This code was adapted from Mondrian's mondrian.olap.Util class.  * The primary differences between this and its Mondrian progenitor are:  *  *<ul>  *<li>use of regular {@link Properties} for compatibility with the JDBC API  * (replaces Mondrian's use of its own order-preserving and case-insensitive  * PropertyList, found in Util.java at link above)</li>  *  *<li>ability to pass to {@link #parse} a pre-existing Properties object into  * which properties are to be parsed, possibly overriding prior values</li>  *  *<li>use of {@link SQLException}s rather than unchecked  * {@link RuntimeException}s</li>  *  *<li>static members for parsing and creating connect strings</li>  *  *</ul>  *  *<p>ConnectStringParser has a private constructor. Callers use the static  * members:  *  *<dl>  *<dt>{@link #parse(String)}  *<dd>Parses the connect string into a new Properties object.  *  *<dt>{@link #parse(String, Properties)}  *<dd>Parses the connect string into an existing Properties object.  *  *<dt>{@link #getParamString(Properties)}  *<dd>Returns a param string, quoted and escaped as needed, to represent the  * supplied name-value pairs.  *</dl>  */
end_comment

begin_class
specifier|public
class|class
name|ConnectStringParser
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|String
name|s
decl_stmt|;
specifier|private
specifier|final
name|int
name|n
decl_stmt|;
specifier|private
name|int
name|i
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|nameBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|valueBuf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a new connect string parser.    *    * @param s connect string to parse    *    * @see #parse(String)    * @see #parse(String, Properties)    */
specifier|private
name|ConnectStringParser
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
name|this
operator|.
name|i
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|n
operator|=
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Parses the connect string into a new Properties object.    *    * @param s connect string to parse    *    * @return properties object with parsed params    *    * @throws SQLException error parsing name-value pairs    */
specifier|public
specifier|static
name|Properties
name|parse
parameter_list|(
name|String
name|s
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
operator|new
name|ConnectStringParser
argument_list|(
name|s
argument_list|)
operator|.
name|parseInternal
argument_list|(
literal|null
argument_list|)
return|;
block|}
comment|/**    * Parses the connect string into an existing Properties object.    *    * @param s connect string to parse    * @param props optional properties object, may be<code>null</code>    *    * @return properties object with parsed params; if an input<code>    * props</code> was supplied, any duplicate properties will have been    * replaced by those from the connect string.    *    * @throws SQLException error parsing name-value pairs    */
specifier|public
specifier|static
name|Properties
name|parse
parameter_list|(
name|String
name|s
parameter_list|,
name|Properties
name|props
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
operator|new
name|ConnectStringParser
argument_list|(
name|s
argument_list|)
operator|.
name|parseInternal
argument_list|(
name|props
argument_list|)
return|;
block|}
comment|/**    * Parses the connect string into a Properties object. Note that the string    * can only be parsed once. Subsequent calls return empty/unchanged    * Properties.    *    * @param props optional properties object, may be<code>null</code>    *    * @return properties object with parsed params; if an input<code>    * props</code> was supplied, any duplicate properties will have been    * replaced by those from the connect string.    *    * @throws SQLException error parsing name-value pairs    */
name|Properties
name|parseInternal
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
name|props
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
block|}
while|while
condition|(
name|i
operator|<
name|n
condition|)
block|{
name|parsePair
argument_list|(
name|props
argument_list|)
expr_stmt|;
block|}
return|return
name|props
return|;
block|}
comment|/**    * Reads "name=value;" or "name=value&lt;EOF&gt;".    *    * @throws SQLException error parsing value    */
name|void
name|parsePair
parameter_list|(
name|Properties
name|props
parameter_list|)
throws|throws
name|SQLException
block|{
name|String
name|name
init|=
name|parseName
argument_list|()
decl_stmt|;
name|String
name|value
decl_stmt|;
if|if
condition|(
name|i
operator|>=
name|n
condition|)
block|{
name|value
operator|=
literal|""
expr_stmt|;
block|}
if|else if
condition|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|';'
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|value
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|parseValue
argument_list|()
expr_stmt|;
block|}
name|props
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Reads "name=". Name can contain equals sign if equals sign is doubled.    */
name|String
name|parseName
parameter_list|()
block|{
name|nameBuf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'='
case|:
name|i
operator|++
expr_stmt|;
if|if
condition|(
operator|(
name|i
operator|<
name|n
operator|)
operator|&&
operator|(
operator|(
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|)
operator|==
literal|'='
operator|)
condition|)
block|{
comment|// doubled equals sign; take one of them, and carry on
name|i
operator|++
expr_stmt|;
name|nameBuf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
break|break;
block|}
name|String
name|name
init|=
name|nameBuf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|name
operator|=
name|name
operator|.
name|trim
argument_list|()
expr_stmt|;
return|return
name|name
return|;
case|case
literal|' '
case|:
if|if
condition|(
name|nameBuf
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
comment|// ignore preceding spaces
name|i
operator|++
expr_stmt|;
break|break;
block|}
comment|// fall through
default|default:
name|nameBuf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|n
condition|)
block|{
return|return
name|nameBuf
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
block|}
block|}
comment|/**    * Reads "value;" or "value&lt;EOF&gt;"    *    * @throws SQLException if find an unterminated quoted value    */
name|String
name|parseValue
parameter_list|()
throws|throws
name|SQLException
block|{
name|char
name|c
decl_stmt|;
comment|// skip over leading white space
while|while
condition|(
operator|(
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|)
operator|==
literal|' '
condition|)
block|{
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|>=
name|n
condition|)
block|{
return|return
literal|""
return|;
block|}
block|}
if|if
condition|(
operator|(
name|c
operator|==
literal|'"'
operator|)
operator|||
operator|(
name|c
operator|==
literal|'\''
operator|)
condition|)
block|{
name|String
name|value
init|=
name|parseQuoted
argument_list|(
name|c
argument_list|)
decl_stmt|;
comment|// skip over trailing white space
while|while
condition|(
operator|(
name|i
operator|<
name|n
operator|)
operator|&&
operator|(
operator|(
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|)
operator|==
literal|' '
operator|)
condition|)
block|{
name|i
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>=
name|n
condition|)
block|{
return|return
name|value
return|;
block|}
if|else if
condition|(
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|';'
condition|)
block|{
name|i
operator|++
expr_stmt|;
return|return
name|value
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"quoted value ended too soon, at position "
operator|+
name|i
operator|+
literal|" in '"
operator|+
name|s
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|String
name|value
decl_stmt|;
name|int
name|semi
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|,
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|semi
operator|>=
literal|0
condition|)
block|{
name|value
operator|=
name|s
operator|.
name|substring
argument_list|(
name|i
argument_list|,
name|semi
argument_list|)
expr_stmt|;
name|i
operator|=
name|semi
operator|+
literal|1
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|s
operator|.
name|substring
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|i
operator|=
name|n
expr_stmt|;
block|}
return|return
name|value
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
comment|/**    * Reads a string quoted by a given character. Occurrences of the quoting    * character must be doubled. For example,<code>parseQuoted('"')</code>    * reads<code>"a ""new"" string"</code> and returns<code>a "new"    * string</code>.    *    * @throws SQLException if find an unterminated quoted value    */
name|String
name|parseQuoted
parameter_list|(
name|char
name|q
parameter_list|)
throws|throws
name|SQLException
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
name|q
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"c != q: c="
operator|+
name|c
operator|+
literal|" q="
operator|+
name|q
argument_list|)
throw|;
block|}
name|valueBuf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
while|while
condition|(
name|i
operator|<
name|n
condition|)
block|{
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
name|q
condition|)
block|{
name|i
operator|++
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|n
condition|)
block|{
name|c
operator|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|c
operator|==
name|q
condition|)
block|{
name|valueBuf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
continue|continue;
block|}
block|}
return|return
name|valueBuf
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
name|valueBuf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|SQLException
argument_list|(
literal|"Connect string '"
operator|+
name|s
operator|+
literal|"' contains unterminated quoted value '"
operator|+
name|valueBuf
operator|.
name|toString
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
comment|/**    * Returns a param string, quoted and escaped as needed, to represent the    * supplied name-value pairs.    *    * @param props name-value pairs    *    * @return param string, never<code>null</code>    */
specifier|public
specifier|static
name|String
name|getParamString
parameter_list|(
name|Properties
name|props
parameter_list|)
block|{
if|if
condition|(
name|props
operator|==
literal|null
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|toMap
argument_list|(
name|props
argument_list|)
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|name
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
specifier|final
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|quote
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|';'
argument_list|)
expr_stmt|;
block|}
comment|// write parameter name
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|" "
argument_list|)
operator|||
name|name
operator|.
name|endsWith
argument_list|(
literal|" "
argument_list|)
condition|)
block|{
name|quote
operator|=
literal|"'"
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|quote
argument_list|)
expr_stmt|;
block|}
name|int
name|len
init|=
name|name
operator|.
name|length
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
name|len
condition|;
operator|++
name|i
control|)
block|{
name|char
name|c
init|=
name|name
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'='
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|quote
argument_list|)
expr_stmt|;
comment|// might be empty
name|quote
operator|=
literal|""
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
comment|// write parameter value
name|len
operator|=
name|value
operator|.
name|length
argument_list|()
expr_stmt|;
name|boolean
name|hasSemi
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|';'
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|hasSQ
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|"'"
argument_list|)
operator|>=
literal|0
decl_stmt|;
name|boolean
name|hasDQ
init|=
name|value
operator|.
name|indexOf
argument_list|(
literal|'"'
argument_list|)
operator|>=
literal|0
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|" "
argument_list|)
operator|||
name|value
operator|.
name|endsWith
argument_list|(
literal|" "
argument_list|)
condition|)
block|{
name|quote
operator|=
literal|"'"
expr_stmt|;
block|}
if|else if
condition|(
name|hasSemi
operator|||
name|hasSQ
operator|||
name|hasDQ
condition|)
block|{
comment|// try to choose the least painful quote
if|if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"\""
argument_list|)
condition|)
block|{
name|quote
operator|=
literal|"'"
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|.
name|startsWith
argument_list|(
literal|"'"
argument_list|)
condition|)
block|{
name|quote
operator|=
literal|"\""
expr_stmt|;
block|}
else|else
block|{
name|quote
operator|=
name|hasSQ
condition|?
literal|"\""
else|:
literal|"'"
expr_stmt|;
block|}
block|}
name|char
name|q
decl_stmt|;
if|if
condition|(
name|quote
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|quote
argument_list|)
expr_stmt|;
name|q
operator|=
name|quote
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|q
operator|=
literal|'\0'
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
name|len
condition|;
operator|++
name|i
control|)
block|{
name|char
name|c
init|=
name|value
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
name|q
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|q
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
name|quote
argument_list|)
expr_stmt|;
comment|// might be empty
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Converts a {@link Properties} object to a<code>{@link Map}&lt;String,    * String&gt;</code>.    *    *<p>This is necessary because {@link Properties} is a dinosaur class. It    * ought to extend<code>Map&lt;String,String&gt;</code>, but instead    * extends<code>{@link java.util.Hashtable}&lt;Object,Object&gt;</code>.    *    *<p>Typical usage, to iterate over a {@link Properties}:    *    *<blockquote>    *<code>    * Properties properties;<br>    * for (Map.Entry&lt;String, String&gt; entry =    * Util.toMap(properties).entrySet()) {<br>    *   println("key=" + entry.getKey() + ", value=" + entry.getValue());<br>    * }    *</code>    *</blockquote>    */
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|toMap
parameter_list|(
specifier|final
name|Properties
name|properties
parameter_list|)
block|{
return|return
operator|(
name|Map
operator|)
name|properties
return|;
block|}
block|}
end_class

begin_comment
comment|// End ConnectStringParser.java
end_comment

end_unit

