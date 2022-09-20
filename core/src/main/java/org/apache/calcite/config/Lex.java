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
name|config
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
name|avatica
operator|.
name|util
operator|.
name|Casing
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
name|avatica
operator|.
name|util
operator|.
name|Quoting
import|;
end_import

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
name|ImmutableSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/** Named, built-in lexical policy. A lexical policy describes how  * identifiers are quoted, whether they are converted to upper- or  * lower-case when they are read, and whether they are matched  * case-sensitively. */
end_comment

begin_enum
specifier|public
enum|enum
name|Lex
block|{
comment|/** Lexical policy similar to BigQuery.    * The case of identifiers is preserved whether or not they quoted;    * after which, identifiers are matched case-insensitively.    * Back-ticks allow identifiers to contain non-alphanumeric characters;    * a back-tick is escaped using a backslash.    * Character literals may be enclosed in single or double quotes. */
name|BIG_QUERY
argument_list|(
name|Quoting
operator|.
name|BACK_TICK_BACKSLASH
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
literal|false
argument_list|,
name|CharLiteralStyle
operator|.
name|BQ_SINGLE
argument_list|,
name|CharLiteralStyle
operator|.
name|BQ_DOUBLE
argument_list|)
block|,
comment|/** Lexical policy similar to Oracle. The case of identifiers enclosed in    * double-quotes is preserved; unquoted identifiers are converted to    * upper-case; after which, identifiers are matched case-sensitively. */
name|ORACLE
argument_list|(
name|Quoting
operator|.
name|DOUBLE_QUOTE
argument_list|,
name|Casing
operator|.
name|TO_UPPER
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
literal|true
argument_list|,
name|CharLiteralStyle
operator|.
name|STANDARD
argument_list|)
block|,
comment|/** Lexical policy similar to MySQL. (To be precise: MySQL on Windows;    * MySQL on Linux uses case-sensitive matching, like the Linux file system.)    * The case of identifiers is preserved whether or not they quoted;    * after which, identifiers are matched case-insensitively.    * Back-ticks allow identifiers to contain non-alphanumeric characters;    * a back-tick is escaped using a back-tick. */
name|MYSQL
argument_list|(
name|Quoting
operator|.
name|BACK_TICK
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
literal|false
argument_list|,
name|CharLiteralStyle
operator|.
name|STANDARD
argument_list|)
block|,
comment|/** Lexical policy similar to MySQL with ANSI_QUOTES option enabled. (To be    * precise: MySQL on Windows; MySQL on Linux uses case-sensitive matching,    * like the Linux file system.) The case of identifiers is preserved whether    * or not they quoted; after which, identifiers are matched    * case-insensitively. Double quotes allow identifiers to contain    * non-alphanumeric characters. */
name|MYSQL_ANSI
argument_list|(
name|Quoting
operator|.
name|DOUBLE_QUOTE
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
literal|false
argument_list|,
name|CharLiteralStyle
operator|.
name|STANDARD
argument_list|)
block|,
comment|/** Lexical policy similar to Microsoft SQL Server.    * The case of identifiers is preserved whether or not they are quoted;    * after which, identifiers are matched case-insensitively.    * Brackets allow identifiers to contain non-alphanumeric characters. */
name|SQL_SERVER
argument_list|(
name|Quoting
operator|.
name|BRACKET
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
literal|false
argument_list|,
name|CharLiteralStyle
operator|.
name|STANDARD
argument_list|)
block|,
comment|/** Lexical policy similar to Java.    * The case of identifiers is preserved whether or not they are quoted;    * after which, identifiers are matched case-sensitively.    * Unlike Java, back-ticks allow identifiers to contain non-alphanumeric    * characters; a back-tick is escaped using a back-tick. */
name|JAVA
argument_list|(
name|Quoting
operator|.
name|BACK_TICK
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
name|Casing
operator|.
name|UNCHANGED
argument_list|,
literal|true
argument_list|,
name|CharLiteralStyle
operator|.
name|STANDARD
argument_list|)
block|;
specifier|public
specifier|final
name|Quoting
name|quoting
decl_stmt|;
specifier|public
specifier|final
name|Casing
name|unquotedCasing
decl_stmt|;
specifier|public
specifier|final
name|Casing
name|quotedCasing
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|caseSensitive
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"ImmutableEnumChecker"
argument_list|)
specifier|public
specifier|final
name|Set
argument_list|<
name|CharLiteralStyle
argument_list|>
name|charLiteralStyles
decl_stmt|;
name|Lex
parameter_list|(
name|Quoting
name|quoting
parameter_list|,
name|Casing
name|unquotedCasing
parameter_list|,
name|Casing
name|quotedCasing
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|CharLiteralStyle
modifier|...
name|charLiteralStyles
parameter_list|)
block|{
name|this
operator|.
name|quoting
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|quoting
argument_list|,
literal|"quoting"
argument_list|)
expr_stmt|;
name|this
operator|.
name|unquotedCasing
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|unquotedCasing
argument_list|,
literal|"unquotedCasing"
argument_list|)
expr_stmt|;
name|this
operator|.
name|quotedCasing
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|quotedCasing
argument_list|,
literal|"quotedCasing"
argument_list|)
expr_stmt|;
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
name|this
operator|.
name|charLiteralStyles
operator|=
name|ImmutableSet
operator|.
name|copyOf
argument_list|(
name|charLiteralStyles
argument_list|)
expr_stmt|;
block|}
block|}
end_enum

end_unit

