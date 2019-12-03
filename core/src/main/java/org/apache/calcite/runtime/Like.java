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
name|runtime
package|;
end_package

begin_comment
comment|/**  * Utilities for converting SQL {@code LIKE} and {@code SIMILAR} operators  * to regular expressions.  */
end_comment

begin_class
specifier|public
class|class
name|Like
block|{
specifier|private
specifier|static
specifier|final
name|String
name|JAVA_REGEX_SPECIALS
init|=
literal|"[]()|^-+*?{}$\\."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|SQL_SIMILAR_SPECIALS
init|=
literal|"[]()|^-+*_%?{}"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|REG_CHAR_CLASSES
init|=
block|{
literal|"[:ALPHA:]"
block|,
literal|"\\p{Alpha}"
block|,
literal|"[:alpha:]"
block|,
literal|"\\p{Alpha}"
block|,
literal|"[:UPPER:]"
block|,
literal|"\\p{Upper}"
block|,
literal|"[:upper:]"
block|,
literal|"\\p{Upper}"
block|,
literal|"[:LOWER:]"
block|,
literal|"\\p{Lower}"
block|,
literal|"[:lower:]"
block|,
literal|"\\p{Lower}"
block|,
literal|"[:DIGIT:]"
block|,
literal|"\\d"
block|,
literal|"[:digit:]"
block|,
literal|"\\d"
block|,
literal|"[:SPACE:]"
block|,
literal|" "
block|,
literal|"[:space:]"
block|,
literal|" "
block|,
literal|"[:WHITESPACE:]"
block|,
literal|"\\s"
block|,
literal|"[:whitespace:]"
block|,
literal|"\\s"
block|,
literal|"[:ALNUM:]"
block|,
literal|"\\p{Alnum}"
block|,
literal|"[:alnum:]"
block|,
literal|"\\p{Alnum}"
block|}
decl_stmt|;
specifier|private
name|Like
parameter_list|()
block|{
block|}
comment|/**    * Translates a SQL LIKE pattern to Java regex pattern, with optional    * escape string.    */
specifier|static
name|String
name|sqlToRegexLike
parameter_list|(
name|String
name|sqlPattern
parameter_list|,
name|CharSequence
name|escapeStr
parameter_list|)
block|{
specifier|final
name|char
name|escapeChar
decl_stmt|;
if|if
condition|(
name|escapeStr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|escapeStr
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
name|invalidEscapeCharacter
argument_list|(
name|escapeStr
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|escapeChar
operator|=
name|escapeStr
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|escapeChar
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|sqlToRegexLike
argument_list|(
name|sqlPattern
argument_list|,
name|escapeChar
argument_list|)
return|;
block|}
comment|/**    * Translates a SQL LIKE pattern to Java regex pattern.    */
specifier|static
name|String
name|sqlToRegexLike
parameter_list|(
name|String
name|sqlPattern
parameter_list|,
name|char
name|escapeChar
parameter_list|)
block|{
name|int
name|i
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|sqlPattern
operator|.
name|length
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|javaPattern
init|=
operator|new
name|StringBuilder
argument_list|(
name|len
operator|+
name|len
argument_list|)
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|sqlPattern
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|JAVA_REGEX_SPECIALS
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|c
operator|==
name|escapeChar
condition|)
block|{
if|if
condition|(
name|i
operator|==
operator|(
name|sqlPattern
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|)
condition|)
block|{
throw|throw
name|invalidEscapeSequence
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
name|char
name|nextChar
init|=
name|sqlPattern
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|nextChar
operator|==
literal|'_'
operator|)
operator|||
operator|(
name|nextChar
operator|==
literal|'%'
operator|)
operator|||
operator|(
name|nextChar
operator|==
name|escapeChar
operator|)
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
name|nextChar
argument_list|)
expr_stmt|;
name|i
operator|++
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|invalidEscapeSequence
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|c
operator|==
literal|'_'
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|c
operator|==
literal|'%'
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
literal|"(?s:.*)"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|javaPattern
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|javaPattern
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|RuntimeException
name|invalidEscapeCharacter
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid escape character '"
operator|+
name|s
operator|+
literal|"'"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RuntimeException
name|invalidEscapeSequence
parameter_list|(
name|String
name|s
parameter_list|,
name|int
name|i
parameter_list|)
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid escape sequence '"
operator|+
name|s
operator|+
literal|"', "
operator|+
name|i
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|similarEscapeRuleChecking
parameter_list|(
name|String
name|sqlPattern
parameter_list|,
name|char
name|escapeChar
parameter_list|)
block|{
if|if
condition|(
name|escapeChar
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|SQL_SIMILAR_SPECIALS
operator|.
name|indexOf
argument_list|(
name|escapeChar
argument_list|)
operator|>=
literal|0
condition|)
block|{
comment|// The the escape character is a special character
comment|// SQL 2003 Part 2 Section 8.6 General Rule 3.b
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sqlPattern
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sqlPattern
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
name|escapeChar
condition|)
block|{
if|if
condition|(
name|i
operator|==
operator|(
name|sqlPattern
operator|.
name|length
argument_list|()
operator|-
literal|1
operator|)
condition|)
block|{
throw|throw
name|invalidEscapeSequence
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
name|char
name|c
init|=
name|sqlPattern
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|SQL_SIMILAR_SPECIALS
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|<
literal|0
operator|)
operator|&&
operator|(
name|c
operator|!=
name|escapeChar
operator|)
condition|)
block|{
throw|throw
name|invalidEscapeSequence
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
block|}
block|}
block|}
comment|// SQL 2003 Part 2 Section 8.6 General Rule 3.c
if|if
condition|(
name|escapeChar
operator|==
literal|':'
condition|)
block|{
name|int
name|position
decl_stmt|;
name|position
operator|=
name|sqlPattern
operator|.
name|indexOf
argument_list|(
literal|"[:"
argument_list|)
expr_stmt|;
if|if
condition|(
name|position
operator|>=
literal|0
condition|)
block|{
name|position
operator|=
name|sqlPattern
operator|.
name|indexOf
argument_list|(
literal|":]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|position
operator|<
literal|0
condition|)
block|{
throw|throw
name|invalidEscapeSequence
argument_list|(
name|sqlPattern
argument_list|,
name|position
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
name|RuntimeException
name|invalidRegularExpression
parameter_list|(
name|String
name|pattern
parameter_list|,
name|int
name|i
parameter_list|)
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
literal|"Invalid regular expression '"
operator|+
name|pattern
operator|+
literal|"'"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|int
name|sqlSimilarRewriteCharEnumeration
parameter_list|(
name|String
name|sqlPattern
parameter_list|,
name|StringBuilder
name|javaPattern
parameter_list|,
name|int
name|pos
parameter_list|,
name|char
name|escapeChar
parameter_list|)
block|{
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
name|pos
operator|+
literal|1
init|;
name|i
operator|<
name|sqlPattern
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|sqlPattern
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
literal|']'
condition|)
block|{
return|return
name|i
operator|-
literal|1
return|;
block|}
if|else if
condition|(
name|c
operator|==
name|escapeChar
condition|)
block|{
name|i
operator|++
expr_stmt|;
name|char
name|nextChar
init|=
name|sqlPattern
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|SQL_SIMILAR_SPECIALS
operator|.
name|indexOf
argument_list|(
name|nextChar
argument_list|)
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|JAVA_REGEX_SPECIALS
operator|.
name|indexOf
argument_list|(
name|nextChar
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|javaPattern
operator|.
name|append
argument_list|(
name|nextChar
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|escapeChar
operator|==
name|nextChar
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
name|nextChar
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|invalidRegularExpression
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|c
operator|==
literal|'-'
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|c
operator|==
literal|'^'
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
literal|'^'
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|sqlPattern
operator|.
name|startsWith
argument_list|(
literal|"[:"
argument_list|,
name|i
argument_list|)
condition|)
block|{
name|int
name|numOfRegCharSets
init|=
name|REG_CHAR_CLASSES
operator|.
name|length
operator|/
literal|2
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|numOfRegCharSets
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|sqlPattern
operator|.
name|startsWith
argument_list|(
name|REG_CHAR_CLASSES
index|[
name|j
operator|+
name|j
index|]
argument_list|,
name|i
argument_list|)
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
name|REG_CHAR_CLASSES
index|[
name|j
operator|+
name|j
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
name|i
operator|+=
name|REG_CHAR_CLASSES
index|[
name|j
operator|+
name|j
index|]
operator|.
name|length
argument_list|()
operator|-
literal|1
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
throw|throw
name|invalidRegularExpression
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|SQL_SIMILAR_SPECIALS
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|>=
literal|0
condition|)
block|{
throw|throw
name|invalidRegularExpression
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
else|else
block|{
name|javaPattern
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|i
operator|-
literal|1
return|;
block|}
comment|/**    * Translates a SQL SIMILAR pattern to Java regex pattern, with optional    * escape string.    */
specifier|static
name|String
name|sqlToRegexSimilar
parameter_list|(
name|String
name|sqlPattern
parameter_list|,
name|CharSequence
name|escapeStr
parameter_list|)
block|{
specifier|final
name|char
name|escapeChar
decl_stmt|;
if|if
condition|(
name|escapeStr
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|escapeStr
operator|.
name|length
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
name|invalidEscapeCharacter
argument_list|(
name|escapeStr
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|escapeChar
operator|=
name|escapeStr
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|escapeChar
operator|=
literal|0
expr_stmt|;
block|}
return|return
name|sqlToRegexSimilar
argument_list|(
name|sqlPattern
argument_list|,
name|escapeChar
argument_list|)
return|;
block|}
comment|/**    * Translates SQL SIMILAR pattern to Java regex pattern.    */
specifier|static
name|String
name|sqlToRegexSimilar
parameter_list|(
name|String
name|sqlPattern
parameter_list|,
name|char
name|escapeChar
parameter_list|)
block|{
name|similarEscapeRuleChecking
argument_list|(
name|sqlPattern
argument_list|,
name|escapeChar
argument_list|)
expr_stmt|;
name|boolean
name|insideCharacterEnumeration
init|=
literal|false
decl_stmt|;
specifier|final
name|StringBuilder
name|javaPattern
init|=
operator|new
name|StringBuilder
argument_list|(
name|sqlPattern
operator|.
name|length
argument_list|()
operator|*
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|sqlPattern
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
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|sqlPattern
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
name|escapeChar
condition|)
block|{
if|if
condition|(
name|i
operator|==
operator|(
name|len
operator|-
literal|1
operator|)
condition|)
block|{
comment|// It should never reach here after the escape rule
comment|// checking.
throw|throw
name|invalidEscapeSequence
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
name|char
name|nextChar
init|=
name|sqlPattern
operator|.
name|charAt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|SQL_SIMILAR_SPECIALS
operator|.
name|indexOf
argument_list|(
name|nextChar
argument_list|)
operator|>=
literal|0
condition|)
block|{
comment|// special character, use \ to replace the escape char.
if|if
condition|(
name|JAVA_REGEX_SPECIALS
operator|.
name|indexOf
argument_list|(
name|nextChar
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|javaPattern
operator|.
name|append
argument_list|(
name|nextChar
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|nextChar
operator|==
name|escapeChar
condition|)
block|{
name|javaPattern
operator|.
name|append
argument_list|(
name|nextChar
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// It should never reach here after the escape rule
comment|// checking.
throw|throw
name|invalidEscapeSequence
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
name|i
operator|++
expr_stmt|;
comment|// we already process the next char.
block|}
else|else
block|{
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'_'
case|:
name|javaPattern
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'%'
case|:
name|javaPattern
operator|.
name|append
argument_list|(
literal|"(?s:.*)"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'['
case|:
name|javaPattern
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|insideCharacterEnumeration
operator|=
literal|true
expr_stmt|;
name|i
operator|=
name|sqlSimilarRewriteCharEnumeration
argument_list|(
name|sqlPattern
argument_list|,
name|javaPattern
argument_list|,
name|i
argument_list|,
name|escapeChar
argument_list|)
expr_stmt|;
break|break;
case|case
literal|']'
case|:
if|if
condition|(
operator|!
name|insideCharacterEnumeration
condition|)
block|{
throw|throw
name|invalidRegularExpression
argument_list|(
name|sqlPattern
argument_list|,
name|i
argument_list|)
throw|;
block|}
name|insideCharacterEnumeration
operator|=
literal|false
expr_stmt|;
name|javaPattern
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'\\'
case|:
name|javaPattern
operator|.
name|append
argument_list|(
literal|"\\\\"
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'$'
case|:
comment|// $ is special character in java regex, but regular in
comment|// SQL regex.
name|javaPattern
operator|.
name|append
argument_list|(
literal|"\\$"
argument_list|)
expr_stmt|;
break|break;
default|default:
name|javaPattern
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|insideCharacterEnumeration
condition|)
block|{
throw|throw
name|invalidRegularExpression
argument_list|(
name|sqlPattern
argument_list|,
name|len
argument_list|)
throw|;
block|}
return|return
name|javaPattern
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

