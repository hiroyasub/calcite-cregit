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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

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
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
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
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/**  *<code>SqlDialect</code> encapsulates the differences between dialects of SQL.  *  *<p>It is used by classes such as {@link SqlWriter} and  * {@link org.apache.calcite.sql.util.SqlBuilder}.  */
end_comment

begin_class
specifier|public
class|class
name|SqlDialect
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * A dialect useful for generating generic SQL. If you need to do something    * database-specific like quoting identifiers, don't rely on this dialect to    * do what you want.    */
specifier|public
specifier|static
specifier|final
name|SqlDialect
name|DUMMY
init|=
name|DatabaseProduct
operator|.
name|UNKNOWN
operator|.
name|getDialect
argument_list|()
decl_stmt|;
comment|/**    * A dialect useful for generating SQL which can be parsed by the    * Calcite parser, in particular quoting literals and identifiers. If you    * want a dialect that knows the full capabilities of the database, create    * one from a connection.    */
specifier|public
specifier|static
specifier|final
name|SqlDialect
name|CALCITE
init|=
name|DatabaseProduct
operator|.
name|CALCITE
operator|.
name|getDialect
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|String
name|identifierQuoteString
decl_stmt|;
specifier|private
specifier|final
name|String
name|identifierEndQuoteString
decl_stmt|;
specifier|private
specifier|final
name|String
name|identifierEscapedQuote
decl_stmt|;
specifier|private
specifier|final
name|DatabaseProduct
name|databaseProduct
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a<code>SqlDialect</code> from a DatabaseMetaData.    *    *<p>Does not maintain a reference to the DatabaseMetaData -- or, more    * importantly, to its {@link java.sql.Connection} -- after this call has    * returned.    *    * @param databaseMetaData used to determine which dialect of SQL to    *                         generate    */
specifier|public
specifier|static
name|SqlDialect
name|create
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|)
block|{
name|String
name|identifierQuoteString
decl_stmt|;
try|try
block|{
name|identifierQuoteString
operator|=
name|databaseMetaData
operator|.
name|getIdentifierQuoteString
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|FakeUtil
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"while quoting identifier"
argument_list|)
throw|;
block|}
name|String
name|databaseProductName
decl_stmt|;
try|try
block|{
name|databaseProductName
operator|=
name|databaseMetaData
operator|.
name|getDatabaseProductName
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
name|FakeUtil
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"while detecting database product"
argument_list|)
throw|;
block|}
specifier|final
name|DatabaseProduct
name|databaseProduct
init|=
name|getProduct
argument_list|(
name|databaseProductName
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
operator|new
name|SqlDialect
argument_list|(
name|databaseProduct
argument_list|,
name|databaseProductName
argument_list|,
name|identifierQuoteString
argument_list|)
return|;
block|}
comment|/**    * Creates a SqlDialect.    *    * @param databaseProduct       Database product; may be UNKNOWN, never null    * @param databaseProductName   Database product name from JDBC driver    * @param identifierQuoteString String to quote identifiers. Null if quoting    *                              is not supported. If "[", close quote is    *                              deemed to be "]".    */
specifier|public
name|SqlDialect
parameter_list|(
name|DatabaseProduct
name|databaseProduct
parameter_list|,
name|String
name|databaseProductName
parameter_list|,
name|String
name|identifierQuoteString
parameter_list|)
block|{
assert|assert
name|databaseProduct
operator|!=
literal|null
assert|;
assert|assert
name|databaseProductName
operator|!=
literal|null
assert|;
name|this
operator|.
name|databaseProduct
operator|=
name|databaseProduct
expr_stmt|;
if|if
condition|(
name|identifierQuoteString
operator|!=
literal|null
condition|)
block|{
name|identifierQuoteString
operator|=
name|identifierQuoteString
operator|.
name|trim
argument_list|()
expr_stmt|;
if|if
condition|(
name|identifierQuoteString
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|identifierQuoteString
operator|=
literal|null
expr_stmt|;
block|}
block|}
name|this
operator|.
name|identifierQuoteString
operator|=
name|identifierQuoteString
expr_stmt|;
name|this
operator|.
name|identifierEndQuoteString
operator|=
name|identifierQuoteString
operator|==
literal|null
condition|?
literal|null
else|:
name|identifierQuoteString
operator|.
name|equals
argument_list|(
literal|"["
argument_list|)
condition|?
literal|"]"
else|:
name|identifierQuoteString
expr_stmt|;
name|this
operator|.
name|identifierEscapedQuote
operator|=
name|identifierQuoteString
operator|==
literal|null
condition|?
literal|null
else|:
name|this
operator|.
name|identifierEndQuoteString
operator|+
name|this
operator|.
name|identifierEndQuoteString
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Converts a product name and version (per the JDBC driver) into a product    * enumeration.    *    * @param productName    Product name    * @param productVersion Product version    * @return database product    */
specifier|public
specifier|static
name|DatabaseProduct
name|getProduct
parameter_list|(
name|String
name|productName
parameter_list|,
name|String
name|productVersion
parameter_list|)
block|{
specifier|final
name|String
name|upperProductName
init|=
name|productName
operator|.
name|toUpperCase
argument_list|()
decl_stmt|;
if|if
condition|(
name|productName
operator|.
name|equals
argument_list|(
literal|"ACCESS"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|ACCESS
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|"APACHE DERBY"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|DERBY
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|"DBMS:CLOUDSCAPE"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|DERBY
return|;
block|}
if|else if
condition|(
name|productName
operator|.
name|startsWith
argument_list|(
literal|"DB2"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|DB2
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"FIREBIRD"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|FIREBIRD
return|;
block|}
if|else if
condition|(
name|productName
operator|.
name|equals
argument_list|(
literal|"Hive"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|HIVE
return|;
block|}
if|else if
condition|(
name|productName
operator|.
name|startsWith
argument_list|(
literal|"Informix"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|INFORMIX
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|equals
argument_list|(
literal|"INGRES"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|INGRES
return|;
block|}
if|else if
condition|(
name|productName
operator|.
name|equals
argument_list|(
literal|"Interbase"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|INTERBASE
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|equals
argument_list|(
literal|"LUCIDDB"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|LUCIDDB
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"SQL SERVER"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|MSSQL
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"PARACCEL"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|PARACCEL
return|;
block|}
if|else if
condition|(
name|productName
operator|.
name|equals
argument_list|(
literal|"Oracle"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|ORACLE
return|;
block|}
if|else if
condition|(
name|productName
operator|.
name|equals
argument_list|(
literal|"Phoenix"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|PHOENIX
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"POSTGRE"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|POSTGRESQL
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"NETEZZA"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|NETEZZA
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|equals
argument_list|(
literal|"MYSQL (INFOBRIGHT)"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|INFOBRIGHT
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|equals
argument_list|(
literal|"MYSQL"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|MYSQL
return|;
block|}
if|else if
condition|(
name|productName
operator|.
name|startsWith
argument_list|(
literal|"HP Neoview"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|NEOVIEW
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"SYBASE"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|SYBASE
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"TERADATA"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|TERADATA
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"HSQL"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|HSQLDB
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"VERTICA"
argument_list|)
condition|)
block|{
return|return
name|DatabaseProduct
operator|.
name|VERTICA
return|;
block|}
else|else
block|{
return|return
name|DatabaseProduct
operator|.
name|UNKNOWN
return|;
block|}
block|}
comment|// -- detect various databases --
comment|/**    * Encloses an identifier in quotation marks appropriate for the current SQL    * dialect.    *    *<p>For example,<code>quoteIdentifier("emp")</code> yields a string    * containing<code>"emp"</code> in Oracle, and a string containing<code>    * [emp]</code> in Access.    *    * @param val Identifier to quote    * @return Quoted identifier    */
specifier|public
name|String
name|quoteIdentifier
parameter_list|(
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|identifierQuoteString
operator|==
literal|null
condition|)
block|{
return|return
name|val
return|;
comment|// quoting is not supported
block|}
name|String
name|val2
init|=
name|val
operator|.
name|replaceAll
argument_list|(
name|identifierEndQuoteString
argument_list|,
name|identifierEscapedQuote
argument_list|)
decl_stmt|;
return|return
name|identifierQuoteString
operator|+
name|val2
operator|+
name|identifierEndQuoteString
return|;
block|}
comment|/**    * Encloses an identifier in quotation marks appropriate for the current SQL    * dialect, writing the result to a {@link StringBuilder}.    *    *<p>For example,<code>quoteIdentifier("emp")</code> yields a string    * containing<code>"emp"</code> in Oracle, and a string containing<code>    * [emp]</code> in Access.    *    * @param buf Buffer    * @param val Identifier to quote    * @return The buffer    */
specifier|public
name|StringBuilder
name|quoteIdentifier
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|identifierQuoteString
operator|==
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|val
argument_list|)
expr_stmt|;
comment|// quoting is not supported
return|return
name|buf
return|;
block|}
name|String
name|val2
init|=
name|val
operator|.
name|replaceAll
argument_list|(
name|identifierEndQuoteString
argument_list|,
name|identifierEscapedQuote
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|identifierQuoteString
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|val2
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|identifierEndQuoteString
argument_list|)
expr_stmt|;
return|return
name|buf
return|;
block|}
comment|/**    * Quotes a multi-part identifier.    *    * @param buf         Buffer    * @param identifiers List of parts of the identifier to quote    * @return The buffer    */
specifier|public
name|StringBuilder
name|quoteIdentifier
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|identifiers
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|identifier
range|:
name|identifiers
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
name|quoteIdentifier
argument_list|(
name|buf
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
return|;
block|}
comment|/**    * Returns whether a given identifier needs to be quoted.    */
specifier|public
name|boolean
name|identifierNeedsToBeQuoted
parameter_list|(
name|String
name|val
parameter_list|)
block|{
return|return
operator|!
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^[A-Z_$0-9]+"
argument_list|)
operator|.
name|matcher
argument_list|(
name|val
argument_list|)
operator|.
name|matches
argument_list|()
return|;
block|}
comment|/**    * Converts a string into a string literal. For example,<code>can't    * run</code> becomes<code>'can''t run'</code>.    */
specifier|public
name|String
name|quoteStringLiteral
parameter_list|(
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|containsNonAscii
argument_list|(
name|val
argument_list|)
condition|)
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|quoteStringLiteralUnicode
argument_list|(
name|buf
argument_list|,
name|val
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
name|val
operator|=
name|FakeUtil
operator|.
name|replace
argument_list|(
name|val
argument_list|,
literal|"'"
argument_list|,
literal|"''"
argument_list|)
expr_stmt|;
return|return
literal|"'"
operator|+
name|val
operator|+
literal|"'"
return|;
block|}
block|}
comment|/**    * Returns whether the string contains any characters outside the    * comfortable 7-bit ASCII range (32 through 127).    *    * @param s String    * @return Whether string contains any non-7-bit-ASCII characters    */
specifier|private
specifier|static
name|boolean
name|containsNonAscii
parameter_list|(
name|String
name|s
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
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
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|32
operator|||
name|c
operator|>=
literal|128
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
comment|/**    * Converts a string into a unicode string literal. For example,    *<code>can't{tab}run\</code> becomes<code>u'can''t\0009run\\'</code>.    */
specifier|public
name|void
name|quoteStringLiteralUnicode
parameter_list|(
name|StringBuilder
name|buf
parameter_list|,
name|String
name|val
parameter_list|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"u&'"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|val
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
name|val
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|32
operator|||
name|c
operator|>=
literal|128
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEXITS
index|[
operator|(
name|c
operator|>>
literal|12
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEXITS
index|[
operator|(
name|c
operator|>>
literal|8
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEXITS
index|[
operator|(
name|c
operator|>>
literal|4
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEXITS
index|[
name|c
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|c
operator|==
literal|'\''
operator|||
name|c
operator|==
literal|'\\'
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|HEXITS
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
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
block|,   }
decl_stmt|;
comment|/**    * Converts a string literal back into a string. For example,<code>'can''t    * run'</code> becomes<code>can't run</code>.    */
specifier|public
name|String
name|unquoteStringLiteral
parameter_list|(
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
operator|(
name|val
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|val
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'\''
operator|)
operator|&&
operator|(
name|val
operator|.
name|charAt
argument_list|(
name|val
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'\''
operator|)
condition|)
block|{
if|if
condition|(
name|val
operator|.
name|length
argument_list|()
operator|>
literal|2
condition|)
block|{
name|val
operator|=
name|FakeUtil
operator|.
name|replace
argument_list|(
name|val
argument_list|,
literal|"''"
argument_list|,
literal|"'"
argument_list|)
expr_stmt|;
return|return
name|val
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|val
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
else|else
block|{
comment|// zero length string
return|return
literal|""
return|;
block|}
block|}
return|return
name|val
return|;
block|}
specifier|protected
name|boolean
name|allowsAs
parameter_list|()
block|{
switch|switch
condition|(
name|databaseProduct
condition|)
block|{
case|case
name|ORACLE
case|:
case|case
name|HIVE
case|:
return|return
literal|false
return|;
default|default:
return|return
literal|true
return|;
block|}
block|}
comment|// -- behaviors --
specifier|protected
name|boolean
name|requiresAliasForFromItems
parameter_list|()
block|{
return|return
name|getDatabaseProduct
argument_list|()
operator|==
name|DatabaseProduct
operator|.
name|POSTGRESQL
return|;
block|}
comment|/**    * Converts a timestamp to a SQL timestamp literal, e.g.    * {@code TIMESTAMP '2009-12-17 12:34:56'}.    *    *<p>Timestamp values do not have a time zone. We therefore interpret them    * as the number of milliseconds after the UTC epoch, and the formatted    * value is that time in UTC.    *    *<p>In particular,    *    *<blockquote><code>quoteTimestampLiteral(new Timestamp(0));</code>    *</blockquote>    *    * returns {@code TIMESTAMP '1970-01-01 00:00:00'}, regardless of the JVM's    * timezone.    *    * @param timestamp Timestamp    * @return SQL timestamp literal    */
specifier|public
name|String
name|quoteTimestampLiteral
parameter_list|(
name|Timestamp
name|timestamp
parameter_list|)
block|{
specifier|final
name|SimpleDateFormat
name|format
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"'TIMESTAMP' ''yyyy-MM-DD HH:mm:SS''"
argument_list|)
decl_stmt|;
name|format
operator|.
name|setTimeZone
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|format
operator|.
name|format
argument_list|(
name|timestamp
argument_list|)
return|;
block|}
comment|/**    * Returns the database this dialect belongs to,    * {@link SqlDialect.DatabaseProduct#UNKNOWN} if not known, never null.    *    * @return Database product    */
specifier|public
name|DatabaseProduct
name|getDatabaseProduct
parameter_list|()
block|{
return|return
name|databaseProduct
return|;
block|}
comment|/**    * Returns whether the dialect supports character set names as part of a    * data type, for instance {@code VARCHAR(30) CHARACTER SET `ISO-8859-1`}.    */
specifier|public
name|boolean
name|supportsCharSet
parameter_list|()
block|{
switch|switch
condition|(
name|databaseProduct
condition|)
block|{
case|case
name|MYSQL
case|:
case|case
name|HSQLDB
case|:
case|case
name|PHOENIX
case|:
return|return
literal|false
return|;
default|default:
return|return
literal|true
return|;
block|}
block|}
comment|/**    * A few utility functions copied from org.apache.calcite.util.Util. We have    * copied them because we wish to keep SqlDialect's dependencies to a    * minimum.    */
specifier|public
specifier|static
class|class
name|FakeUtil
block|{
specifier|public
specifier|static
name|Error
name|newInternal
parameter_list|(
name|Throwable
name|e
parameter_list|,
name|String
name|s
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Internal error: \u0000"
operator|+
name|s
decl_stmt|;
name|AssertionError
name|ae
init|=
operator|new
name|AssertionError
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|ae
operator|.
name|initCause
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return
name|ae
return|;
block|}
comment|/**      * Replaces every occurrence of<code>find</code> in<code>s</code> with      *<code>replace</code>.      */
specifier|public
specifier|static
name|String
name|replace
parameter_list|(
name|String
name|s
parameter_list|,
name|String
name|find
parameter_list|,
name|String
name|replace
parameter_list|)
block|{
comment|// let's be optimistic
name|int
name|found
init|=
name|s
operator|.
name|indexOf
argument_list|(
name|find
argument_list|)
decl_stmt|;
if|if
condition|(
name|found
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|s
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|s
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|start
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
for|for
control|(
init|;
name|start
operator|<
name|found
condition|;
name|start
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|s
operator|.
name|charAt
argument_list|(
name|start
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|found
operator|==
name|s
operator|.
name|length
argument_list|()
condition|)
block|{
break|break;
block|}
name|sb
operator|.
name|append
argument_list|(
name|replace
argument_list|)
expr_stmt|;
name|start
operator|+=
name|find
operator|.
name|length
argument_list|()
expr_stmt|;
name|found
operator|=
name|s
operator|.
name|indexOf
argument_list|(
name|find
argument_list|,
name|start
argument_list|)
expr_stmt|;
if|if
condition|(
name|found
operator|==
operator|-
literal|1
condition|)
block|{
name|found
operator|=
name|s
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Rough list of flavors of database.    *    *<p>These values cannot help you distinguish between features that exist    * in different versions or ports of a database, but they are sufficient    * to drive a {@code switch} statement if behavior is broadly different    * between say, MySQL and Oracle.    *    *<p>If possible, you should not refer to particular database at all; write    * extend the dialect to describe the particular capability, for example,    * whether the database allows expressions to appear in the GROUP BY clause.    */
specifier|public
enum|enum
name|DatabaseProduct
block|{
name|ACCESS
argument_list|(
literal|"Access"
argument_list|,
literal|"\""
argument_list|)
block|,
name|CALCITE
argument_list|(
literal|"Apache Calcite"
argument_list|,
literal|"\""
argument_list|)
block|,
name|MSSQL
argument_list|(
literal|"Microsoft SQL Server"
argument_list|,
literal|"["
argument_list|)
block|,
name|MYSQL
argument_list|(
literal|"MySQL"
argument_list|,
literal|"`"
argument_list|)
block|,
name|ORACLE
argument_list|(
literal|"Oracle"
argument_list|,
literal|"\""
argument_list|)
block|,
name|DERBY
argument_list|(
literal|"Apache Derby"
argument_list|,
literal|null
argument_list|)
block|,
name|DB2
argument_list|(
literal|"IBM DB2"
argument_list|,
literal|null
argument_list|)
block|,
name|FIREBIRD
argument_list|(
literal|"Firebird"
argument_list|,
literal|null
argument_list|)
block|,
name|HIVE
argument_list|(
literal|"Apache Hive"
argument_list|,
literal|null
argument_list|)
block|,
name|INFORMIX
argument_list|(
literal|"Informix"
argument_list|,
literal|null
argument_list|)
block|,
name|INGRES
argument_list|(
literal|"Ingres"
argument_list|,
literal|null
argument_list|)
block|,
name|LUCIDDB
argument_list|(
literal|"LucidDB"
argument_list|,
literal|"\""
argument_list|)
block|,
name|INTERBASE
argument_list|(
literal|"Interbase"
argument_list|,
literal|null
argument_list|)
block|,
name|PHOENIX
argument_list|(
literal|"Phoenix"
argument_list|,
literal|"\""
argument_list|)
block|,
name|POSTGRESQL
argument_list|(
literal|"PostgreSQL"
argument_list|,
literal|"\""
argument_list|)
block|,
name|NETEZZA
argument_list|(
literal|"Netezza"
argument_list|,
literal|"\""
argument_list|)
block|,
name|INFOBRIGHT
argument_list|(
literal|"Infobright"
argument_list|,
literal|"`"
argument_list|)
block|,
name|NEOVIEW
argument_list|(
literal|"Neoview"
argument_list|,
literal|null
argument_list|)
block|,
name|SYBASE
argument_list|(
literal|"Sybase"
argument_list|,
literal|null
argument_list|)
block|,
name|TERADATA
argument_list|(
literal|"Teradata"
argument_list|,
literal|"\""
argument_list|)
block|,
name|HSQLDB
argument_list|(
literal|"Hsqldb"
argument_list|,
literal|null
argument_list|)
block|,
name|VERTICA
argument_list|(
literal|"Vertica"
argument_list|,
literal|"\""
argument_list|)
block|,
name|SQLSTREAM
argument_list|(
literal|"SQLstream"
argument_list|,
literal|"\""
argument_list|)
block|,
name|PARACCEL
argument_list|(
literal|"Paraccel"
argument_list|,
literal|"\""
argument_list|)
block|,
comment|/**      * Placeholder for the unknown database.      *      *<p>Its dialect is useful for generating generic SQL. If you need to      * do something database-specific like quoting identifiers, don't rely      * on this dialect to do what you want.      */
name|UNKNOWN
argument_list|(
literal|"Unknown"
argument_list|,
literal|"`"
argument_list|)
block|;
specifier|private
name|SqlDialect
name|dialect
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|databaseProductName
decl_stmt|;
specifier|private
name|String
name|quoteString
decl_stmt|;
name|DatabaseProduct
parameter_list|(
name|String
name|databaseProductName
parameter_list|,
name|String
name|quoteString
parameter_list|)
block|{
name|this
operator|.
name|databaseProductName
operator|=
name|databaseProductName
expr_stmt|;
name|this
operator|.
name|quoteString
operator|=
name|quoteString
expr_stmt|;
block|}
comment|/**      * Returns a dummy dialect for this database.      *      *<p>Since databases have many versions and flavors, this dummy dialect      * is at best an approximation. If you want exact information, better to      * use a dialect created from an actual connection's metadata      * (see {@link SqlDialect#create(java.sql.DatabaseMetaData)}).      *      * @return Dialect representing lowest-common-demoninator behavior for      * all versions of this database      */
specifier|public
name|SqlDialect
name|getDialect
parameter_list|()
block|{
if|if
condition|(
name|dialect
operator|==
literal|null
condition|)
block|{
name|dialect
operator|=
operator|new
name|SqlDialect
argument_list|(
name|this
argument_list|,
name|databaseProductName
argument_list|,
name|quoteString
argument_list|)
expr_stmt|;
block|}
return|return
name|dialect
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SqlDialect.java
end_comment

end_unit

