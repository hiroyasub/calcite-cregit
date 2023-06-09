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
name|sql
operator|.
name|util
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
name|sql
operator|.
name|SqlDialect
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
name|util
operator|.
name|UnmodifiableArrayList
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
name|List
import|;
end_import

begin_comment
comment|/**  * Extension to {@link StringBuilder} for the purposes of creating SQL queries  * and expressions.  *  *<p>Using this class helps to prevent SQL injection attacks, incorrectly  * quoted identifiers and strings. These problems occur when you build SQL by  * concatenating strings, and you forget to treat identifers and string literals  * correctly. SqlBuilder has special methods for appending identifiers and  * literals.  */
end_comment

begin_class
specifier|public
class|class
name|SqlBuilder
block|{
specifier|private
specifier|final
name|StringBuilder
name|buf
decl_stmt|;
specifier|private
specifier|final
name|SqlDialect
name|dialect
decl_stmt|;
comment|/**    * Creates a SqlBuilder.    *    * @param dialect Dialect    */
specifier|public
name|SqlBuilder
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|)
block|{
assert|assert
name|dialect
operator|!=
literal|null
assert|;
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|this
operator|.
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|()
expr_stmt|;
block|}
comment|/**    * Creates a SqlBuilder with a given string.    *    * @param dialect Dialect    * @param s       Initial contents of the buffer    */
specifier|public
name|SqlBuilder
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|,
name|String
name|s
parameter_list|)
block|{
assert|assert
name|dialect
operator|!=
literal|null
assert|;
name|this
operator|.
name|dialect
operator|=
name|dialect
expr_stmt|;
name|this
operator|.
name|buf
operator|=
operator|new
name|StringBuilder
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the dialect.    *    * @return dialect    */
specifier|public
name|SqlDialect
name|getDialect
parameter_list|()
block|{
return|return
name|dialect
return|;
block|}
comment|/**    * Returns the length (character count).    *    * @return the length of the sequence of characters currently    * represented by this object    */
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|buf
operator|.
name|length
argument_list|()
return|;
block|}
comment|/**    * Clears the contents of the buffer.    */
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>Returns the SQL string.    *    * @return SQL string    * @see #getSql()    */
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getSql
argument_list|()
return|;
block|}
comment|/**    * Returns the SQL.    */
specifier|public
name|String
name|getSql
parameter_list|()
block|{
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Returns the SQL and clears the buffer.    *    *<p>Convenient if you are reusing the same SQL builder in a loop.    */
specifier|public
name|String
name|getSqlAndClear
parameter_list|()
block|{
specifier|final
name|String
name|str
init|=
name|buf
operator|.
name|toString
argument_list|()
decl_stmt|;
name|clear
argument_list|()
expr_stmt|;
return|return
name|str
return|;
block|}
comment|/**    * Appends a hygienic SQL string.    *    * @param s SQL string to append    * @return This builder    */
specifier|public
name|SqlBuilder
name|append
parameter_list|(
name|SqlString
name|s
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|s
operator|.
name|getSql
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Appends a string, without any quoting.    *    *<p>Calls to this method are dubious.    *    * @param s String to append    * @return This builder    */
specifier|public
name|SqlBuilder
name|append
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Appends a character, without any quoting.    *    * @param c Character to append    * @return This builder    */
specifier|public
name|SqlBuilder
name|append
parameter_list|(
name|char
name|c
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Appends a number, per {@link StringBuilder#append(long)}.    */
specifier|public
name|SqlBuilder
name|append
parameter_list|(
name|long
name|n
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|n
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Appends an identifier to this buffer, quoting accordingly.    *    * @param name Identifier    * @return This builder    */
specifier|public
name|SqlBuilder
name|identifier
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|dialect
operator|.
name|quoteIdentifier
argument_list|(
name|buf
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Appends one or more identifiers to this buffer, quoting accordingly.    *    * @param names Varargs array of identifiers    * @return This builder    */
specifier|public
name|SqlBuilder
name|identifier
parameter_list|(
name|String
modifier|...
name|names
parameter_list|)
block|{
name|dialect
operator|.
name|quoteIdentifier
argument_list|(
name|buf
argument_list|,
name|UnmodifiableArrayList
operator|.
name|of
argument_list|(
name|names
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Appends a compound identifier to this buffer, quoting accordingly.    *    * @param names Parts of a compound identifier    * @return This builder    */
specifier|public
name|SqlBuilder
name|identifier
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|dialect
operator|.
name|quoteIdentifier
argument_list|(
name|buf
argument_list|,
name|names
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the contents of this SQL buffer as a 'certified kocher' SQL    * string.    *    *<p>Use this method in preference to {@link #toString()}. It indicates    * that the SQL string has been constructed using good hygiene, and is    * therefore less likely to contain SQL injection or badly quoted    * identifiers or strings.    *    * @return Contents of this builder as a SQL string.    */
specifier|public
name|SqlString
name|toSqlString
parameter_list|()
block|{
return|return
operator|new
name|SqlString
argument_list|(
name|dialect
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Appends a string literal to this buffer.    *    *<p>For example, calling<code>literal(&quot;can't&quot;)</code>    * would convert the buffer    *<blockquote><code>SELECT</code></blockquote>    * to    *<blockquote><code>SELECT 'can''t'</code></blockquote>    *    * @param s String to append    * @return This buffer    */
specifier|public
name|SqlBuilder
name|literal
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|s
operator|==
literal|null
condition|?
literal|"null"
else|:
name|dialect
operator|.
name|quoteStringLiteral
argument_list|(
name|s
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Appends a timestamp literal to this buffer.    *    * @param timestamp Timestamp to append    * @return This buffer    */
specifier|public
name|SqlBuilder
name|literal
parameter_list|(
name|Timestamp
name|timestamp
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|timestamp
operator|==
literal|null
condition|?
literal|"null"
else|:
name|dialect
operator|.
name|quoteTimestampLiteral
argument_list|(
name|timestamp
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Returns the index within this string of the first occurrence of the    * specified substring.    *    * @see StringBuilder#indexOf(String)    */
specifier|public
name|int
name|indexOf
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|buf
operator|.
name|indexOf
argument_list|(
name|str
argument_list|)
return|;
block|}
comment|/**    * Returns the index within this string of the first occurrence of the    * specified substring, starting at the specified index.    *    * @see StringBuilder#indexOf(String, int)    */
specifier|public
name|int
name|indexOf
parameter_list|(
name|String
name|str
parameter_list|,
name|int
name|fromIndex
parameter_list|)
block|{
return|return
name|buf
operator|.
name|indexOf
argument_list|(
name|str
argument_list|,
name|fromIndex
argument_list|)
return|;
block|}
comment|/**    * Inserts the string into this character sequence.    *    * @see StringBuilder#insert(int, String)    */
specifier|public
name|SqlBuilder
name|insert
parameter_list|(
name|int
name|offset
parameter_list|,
name|String
name|str
parameter_list|)
block|{
name|buf
operator|.
name|insert
argument_list|(
name|offset
argument_list|,
name|str
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

