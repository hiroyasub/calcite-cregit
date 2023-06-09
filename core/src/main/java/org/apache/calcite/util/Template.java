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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

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
name|text
operator|.
name|MessageFormat
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
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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

begin_comment
comment|/**  * String template.  *  *<p>It is extended from {@link java.text.MessageFormat} to allow parameters  * to be substituted by name as well as by position.  *  *<p>The following example, using MessageFormat, yields "Happy 64th birthday,  * Ringo!":  *  *<blockquote>MessageFormat f =  * new MessageFormat("Happy {0,number}th birthday, {1}!");<br>  * Object[] args = {64, "Ringo"};<br>  * System.out.println(f.format(args);</blockquote>  *  *<p>Here is the same example using a Template and named parameters:  *  *<blockquote>Template f =  * new Template("Happy {age,number}th birthday, {name}!");<br>  * Map&lt;Object, Object&gt; args = new HashMap&lt;Object, Object&gt;();<br>  * args.put("age", 64);<br>  * args.put("name", "Ringo");<br>  * System.out.println(f.format(args);</blockquote>  *  *<p>Using a Template you can also use positional parameters:  *  *<blockquote>Template f =  * new Template("Happy {age,number}th birthday, {name}!");<br>  * Object[] args = {64, "Ringo"};<br>  * System.out.println(f.format(args);</blockquote>  *  *<p>Or a hybrid; here, one argument is specified by name, another by position:  *  *<blockquote>Template f =  * new Template("Happy {age,number}th birthday, {name}!");<br>  * Map&lt;Object, Object&gt; args = new HashMap&lt;Object, Object&gt;();<br>  * args.put(0, 64);<br>  * args.put("name", "Ringo");<br>  * System.out.println(f.format(args);</blockquote>  */
end_comment

begin_class
specifier|public
class|class
name|Template
extends|extends
name|MessageFormat
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|parameterNames
decl_stmt|;
comment|/**    * Creates a Template for the default locale and the    * specified pattern.    *    * @param pattern the pattern for this message format    * @throws IllegalArgumentException if the pattern is invalid    */
specifier|public
specifier|static
name|Template
name|of
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
return|return
name|of
argument_list|(
name|pattern
argument_list|,
name|Locale
operator|.
name|getDefault
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Creates a Template for the specified locale and    * pattern.    *    * @param pattern the pattern for this message format    * @param locale  the locale for this message format    * @throws IllegalArgumentException if the pattern is invalid    */
specifier|public
specifier|static
name|Template
name|of
parameter_list|(
name|String
name|pattern
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|parameterNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|String
name|processedPattern
init|=
name|process
argument_list|(
name|pattern
argument_list|,
name|parameterNames
argument_list|)
decl_stmt|;
return|return
operator|new
name|Template
argument_list|(
name|processedPattern
argument_list|,
name|parameterNames
argument_list|,
name|locale
argument_list|)
return|;
block|}
specifier|private
name|Template
parameter_list|(
name|String
name|pattern
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|parameterNames
parameter_list|,
name|Locale
name|locale
parameter_list|)
block|{
name|super
argument_list|(
name|pattern
argument_list|,
name|locale
argument_list|)
expr_stmt|;
name|this
operator|.
name|parameterNames
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|parameterNames
argument_list|)
expr_stmt|;
block|}
comment|/**    * Parses the pattern, populates the parameter names, and returns the    * pattern with parameter names converted to parameter ordinals.    *    *<p>To ensure that the same parsing rules apply, this code is copied from    * {@link java.text.MessageFormat#applyPattern(String)} but with different    * actions when a parameter is recognized.    *    * @param pattern        Pattern    * @param parameterNames Names of parameters (output)    * @return Pattern with named parameters substituted with ordinals    */
specifier|private
specifier|static
name|String
name|process
parameter_list|(
name|String
name|pattern
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|parameterNames
parameter_list|)
block|{
name|StringBuilder
index|[]
name|segments
init|=
operator|new
name|StringBuilder
index|[
literal|4
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
name|segments
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|segments
index|[
name|i
index|]
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
name|int
name|part
init|=
literal|0
decl_stmt|;
name|boolean
name|inQuote
init|=
literal|false
decl_stmt|;
name|int
name|braceStack
init|=
literal|0
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
name|pattern
operator|.
name|length
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
name|char
name|ch
init|=
name|pattern
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|part
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|ch
operator|==
literal|'\''
condition|)
block|{
name|segments
index|[
name|part
index|]
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
comment|// jhyde: don't lose orig quote
if|if
condition|(
name|i
operator|+
literal|1
operator|<
name|pattern
operator|.
name|length
argument_list|()
operator|&&
name|pattern
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|==
literal|'\''
condition|)
block|{
name|segments
index|[
name|part
index|]
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
comment|// handle doubles
operator|++
name|i
expr_stmt|;
block|}
else|else
block|{
name|inQuote
operator|=
operator|!
name|inQuote
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|ch
operator|==
literal|'{'
operator|&&
operator|!
name|inQuote
condition|)
block|{
name|part
operator|=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|segments
index|[
name|part
index|]
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|inQuote
condition|)
block|{
comment|// just copy quotes in parts
name|segments
index|[
name|part
index|]
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'\''
condition|)
block|{
name|inQuote
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|','
case|:
if|if
condition|(
name|part
operator|<
literal|3
condition|)
block|{
name|part
operator|+=
literal|1
expr_stmt|;
block|}
else|else
block|{
name|segments
index|[
name|part
index|]
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|'{'
case|:
operator|++
name|braceStack
expr_stmt|;
name|segments
index|[
name|part
index|]
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'}'
case|:
if|if
condition|(
name|braceStack
operator|==
literal|0
condition|)
block|{
name|part
operator|=
literal|0
expr_stmt|;
name|makeFormat
argument_list|(
name|segments
argument_list|,
name|parameterNames
argument_list|)
expr_stmt|;
block|}
else|else
block|{
operator|--
name|braceStack
expr_stmt|;
name|segments
index|[
name|part
index|]
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
literal|'\''
case|:
name|inQuote
operator|=
literal|true
expr_stmt|;
comment|// fall through, so we keep quotes in other parts
default|default:
name|segments
index|[
name|part
index|]
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
if|if
condition|(
name|braceStack
operator|==
literal|0
operator|&&
name|part
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unmatched braces in the pattern."
argument_list|)
throw|;
block|}
return|return
name|segments
index|[
literal|0
index|]
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Called when a complete parameter has been seen.    *    * @param segments       Comma-separated segments of the parameter definition    * @param parameterNames List of parameter names seen so far    */
specifier|private
specifier|static
name|void
name|makeFormat
parameter_list|(
name|StringBuilder
index|[]
name|segments
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|parameterNames
parameter_list|)
block|{
specifier|final
name|String
name|parameterName
init|=
name|segments
index|[
literal|1
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
name|int
name|parameterOrdinal
init|=
name|parameterNames
operator|.
name|size
argument_list|()
decl_stmt|;
name|parameterNames
operator|.
name|add
argument_list|(
name|parameterName
argument_list|)
expr_stmt|;
name|segments
index|[
literal|0
index|]
operator|.
name|append
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|segments
index|[
literal|0
index|]
operator|.
name|append
argument_list|(
name|parameterOrdinal
argument_list|)
expr_stmt|;
specifier|final
name|String
name|two
init|=
name|segments
index|[
literal|2
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|two
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|segments
index|[
literal|0
index|]
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|two
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|three
init|=
name|segments
index|[
literal|3
index|]
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|three
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|segments
index|[
literal|0
index|]
operator|.
name|append
argument_list|(
literal|","
argument_list|)
operator|.
name|append
argument_list|(
name|three
argument_list|)
expr_stmt|;
block|}
name|segments
index|[
literal|0
index|]
operator|.
name|append
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
name|segments
index|[
literal|1
index|]
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// throw away other segments
name|segments
index|[
literal|2
index|]
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|segments
index|[
literal|3
index|]
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Formats a set of arguments to produce a string.    *    *<p>Arguments may appear in the map using named keys (of type String), or    * positional keys (0-based ordinals represented either as type String or    * Integer).    *    * @param argMap A map containing the arguments as (key, value) pairs    * @return Formatted string.    * @throws IllegalArgumentException if the Format cannot format the given    *                                  object    */
specifier|public
name|String
name|format
parameter_list|(
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|argMap
parameter_list|)
block|{
annotation|@
name|Nullable
name|Object
index|[]
name|args
init|=
operator|new
name|Object
index|[
name|parameterNames
operator|.
name|size
argument_list|()
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
name|parameterNames
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|args
index|[
name|i
index|]
operator|=
name|getArg
argument_list|(
name|argMap
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|format
argument_list|(
name|args
argument_list|)
return|;
block|}
comment|/**    * Returns the value of the {@code ordinal}th argument.    *    * @param argMap  Map of argument values    * @param ordinal Ordinal of argument    * @return Value of argument    */
specifier|private
annotation|@
name|Nullable
name|Object
name|getArg
parameter_list|(
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|argMap
parameter_list|,
name|int
name|ordinal
parameter_list|)
block|{
comment|// First get by name.
name|String
name|parameterName
init|=
name|parameterNames
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
name|Object
name|arg
init|=
name|argMap
operator|.
name|get
argument_list|(
name|parameterName
argument_list|)
decl_stmt|;
if|if
condition|(
name|arg
operator|!=
literal|null
condition|)
block|{
return|return
name|arg
return|;
block|}
comment|// Next by integer ordinal.
name|arg
operator|=
name|argMap
operator|.
name|get
argument_list|(
name|ordinal
argument_list|)
expr_stmt|;
if|if
condition|(
name|arg
operator|!=
literal|null
condition|)
block|{
return|return
name|arg
return|;
block|}
comment|// Next by string ordinal.
return|return
name|argMap
operator|.
name|get
argument_list|(
name|ordinal
operator|+
literal|""
argument_list|)
return|;
block|}
comment|/**    * Creates a Template with the given pattern and uses it    * to format the given arguments. This is equivalent to    *<blockquote>    *<code>{@link #of(String) Template}(pattern).{@link #format}(args)</code>    *</blockquote>    *    * @throws IllegalArgumentException if the pattern is invalid,    *                                  or if an argument in the    *<code>arguments</code> array is not of the    *                                  type expected by the format element(s)    *                                  that use it.    */
specifier|public
specifier|static
name|String
name|formatByName
parameter_list|(
name|String
name|pattern
parameter_list|,
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|argMap
parameter_list|)
block|{
return|return
name|Template
operator|.
name|of
argument_list|(
name|pattern
argument_list|)
operator|.
name|format
argument_list|(
name|argMap
argument_list|)
return|;
block|}
comment|/**    * Returns the names of the parameters, in the order that they appeared in    * the template string.    *    * @return List of parameter names    */
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getParameterNames
parameter_list|()
block|{
return|return
name|parameterNames
return|;
block|}
block|}
end_class

end_unit

