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
name|config
operator|.
name|NullCollation
import|;
end_import

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

begin_comment
comment|/**  * Utilities related to {@link SqlDialect}.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|SqlDialects
block|{
specifier|private
name|SqlDialects
parameter_list|()
block|{
comment|// utility class
block|}
comment|/**    * Extracts information from {@link DatabaseMetaData} into {@link SqlDialect.Context}.    *    * @param databaseMetaData the database metadata    * @return a context with information populated from the database metadata    */
specifier|public
specifier|static
name|SqlDialect
operator|.
name|Context
name|createContext
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|)
block|{
name|String
name|databaseProductName
decl_stmt|;
name|int
name|databaseMajorVersion
decl_stmt|;
name|int
name|databaseMinorVersion
decl_stmt|;
name|String
name|databaseVersion
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
name|databaseMajorVersion
operator|=
name|databaseMetaData
operator|.
name|getDatabaseMajorVersion
argument_list|()
expr_stmt|;
name|databaseMinorVersion
operator|=
name|databaseMetaData
operator|.
name|getDatabaseMinorVersion
argument_list|()
expr_stmt|;
name|databaseVersion
operator|=
name|databaseMetaData
operator|.
name|getDatabaseProductVersion
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
operator|new
name|RuntimeException
argument_list|(
literal|"while detecting database product"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|String
name|quoteString
init|=
name|getIdentifierQuoteString
argument_list|(
name|databaseMetaData
argument_list|)
decl_stmt|;
specifier|final
name|NullCollation
name|nullCollation
init|=
name|getNullCollation
argument_list|(
name|databaseMetaData
argument_list|)
decl_stmt|;
specifier|final
name|Casing
name|unquotedCasing
init|=
name|getCasing
argument_list|(
name|databaseMetaData
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Casing
name|quotedCasing
init|=
name|getCasing
argument_list|(
name|databaseMetaData
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|caseSensitive
init|=
name|isCaseSensitive
argument_list|(
name|databaseMetaData
argument_list|)
decl_stmt|;
specifier|final
name|SqlDialect
operator|.
name|Context
name|c
init|=
name|SqlDialect
operator|.
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProductName
argument_list|(
name|databaseProductName
argument_list|)
operator|.
name|withDatabaseMajorVersion
argument_list|(
name|databaseMajorVersion
argument_list|)
operator|.
name|withDatabaseMinorVersion
argument_list|(
name|databaseMinorVersion
argument_list|)
operator|.
name|withDatabaseVersion
argument_list|(
name|databaseVersion
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
name|quoteString
argument_list|)
operator|.
name|withUnquotedCasing
argument_list|(
name|unquotedCasing
argument_list|)
operator|.
name|withQuotedCasing
argument_list|(
name|quotedCasing
argument_list|)
operator|.
name|withCaseSensitive
argument_list|(
name|caseSensitive
argument_list|)
operator|.
name|withNullCollation
argument_list|(
name|nullCollation
argument_list|)
decl_stmt|;
return|return
name|c
return|;
block|}
specifier|private
specifier|static
name|String
name|getIdentifierQuoteString
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|)
block|{
try|try
block|{
return|return
name|databaseMetaData
operator|.
name|getIdentifierQuoteString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot deduce identifier quote string"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|Casing
name|getCasing
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|,
name|boolean
name|quoted
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|quoted
condition|?
name|databaseMetaData
operator|.
name|storesUpperCaseQuotedIdentifiers
argument_list|()
else|:
name|databaseMetaData
operator|.
name|storesUpperCaseIdentifiers
argument_list|()
condition|)
block|{
return|return
name|Casing
operator|.
name|TO_UPPER
return|;
block|}
if|else if
condition|(
name|quoted
condition|?
name|databaseMetaData
operator|.
name|storesLowerCaseQuotedIdentifiers
argument_list|()
else|:
name|databaseMetaData
operator|.
name|storesLowerCaseIdentifiers
argument_list|()
condition|)
block|{
return|return
name|Casing
operator|.
name|TO_LOWER
return|;
block|}
if|else if
condition|(
name|quoted
condition|?
operator|(
name|databaseMetaData
operator|.
name|storesMixedCaseQuotedIdentifiers
argument_list|()
operator|||
name|databaseMetaData
operator|.
name|supportsMixedCaseQuotedIdentifiers
argument_list|()
operator|)
else|:
operator|(
name|databaseMetaData
operator|.
name|storesMixedCaseIdentifiers
argument_list|()
operator|||
name|databaseMetaData
operator|.
name|supportsMixedCaseIdentifiers
argument_list|()
operator|)
condition|)
block|{
return|return
name|Casing
operator|.
name|UNCHANGED
return|;
block|}
else|else
block|{
return|return
name|Casing
operator|.
name|UNCHANGED
return|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot deduce casing"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isCaseSensitive
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|)
block|{
try|try
block|{
return|return
name|databaseMetaData
operator|.
name|supportsMixedCaseIdentifiers
argument_list|()
operator|||
name|databaseMetaData
operator|.
name|supportsMixedCaseQuotedIdentifiers
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot deduce case-sensitivity"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|NullCollation
name|getNullCollation
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|databaseMetaData
operator|.
name|nullsAreSortedAtEnd
argument_list|()
condition|)
block|{
return|return
name|NullCollation
operator|.
name|LAST
return|;
block|}
if|else if
condition|(
name|databaseMetaData
operator|.
name|nullsAreSortedAtStart
argument_list|()
condition|)
block|{
return|return
name|NullCollation
operator|.
name|FIRST
return|;
block|}
if|else if
condition|(
name|databaseMetaData
operator|.
name|nullsAreSortedLow
argument_list|()
condition|)
block|{
return|return
name|NullCollation
operator|.
name|LOW
return|;
block|}
if|else if
condition|(
name|databaseMetaData
operator|.
name|nullsAreSortedHigh
argument_list|()
condition|)
block|{
return|return
name|NullCollation
operator|.
name|HIGH
return|;
block|}
if|else if
condition|(
name|isBigQuery
argument_list|(
name|databaseMetaData
argument_list|)
condition|)
block|{
return|return
name|NullCollation
operator|.
name|LOW
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot deduce null collation"
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot deduce null collation"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|isBigQuery
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|databaseMetaData
operator|.
name|getDatabaseProductName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Google Big Query"
argument_list|)
return|;
block|}
block|}
end_class

end_unit
