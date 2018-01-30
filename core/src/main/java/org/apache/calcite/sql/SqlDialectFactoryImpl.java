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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|dialect
operator|.
name|AccessSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|AnsiSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|BigQuerySqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|CalciteSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|ClickHouseSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|Db2SqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|DerbySqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|FirebirdSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|H2SqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|HiveSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|HsqldbSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|InfobrightSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|InformixSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|IngresSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|InterbaseSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|JethroDataSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|LucidDbSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|MssqlSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|MysqlSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|NeoviewSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|NetezzaSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|OracleSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|ParaccelSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|PhoenixSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|PostgresqlSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|RedshiftSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|SnowflakeSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|SparkSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|SybaseSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|TeradataSqlDialect
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
name|sql
operator|.
name|dialect
operator|.
name|VerticaSqlDialect
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * The default implementation of a<code>SqlDialectFactory</code>.  */
end_comment

begin_class
specifier|public
class|class
name|SqlDialectFactoryImpl
implements|implements
name|SqlDialectFactory
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SqlDialectFactoryImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|SqlDialectFactoryImpl
name|INSTANCE
init|=
operator|new
name|SqlDialectFactoryImpl
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|JethroDataSqlDialect
operator|.
name|JethroInfoCache
name|jethroCache
init|=
name|JethroDataSqlDialect
operator|.
name|createCache
argument_list|()
decl_stmt|;
specifier|public
name|SqlDialect
name|create
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
name|upperProductName
init|=
name|databaseProductName
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
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
switch|switch
condition|(
name|upperProductName
condition|)
block|{
case|case
literal|"ACCESS"
case|:
return|return
operator|new
name|AccessSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"APACHE DERBY"
case|:
return|return
operator|new
name|DerbySqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"CLICKHOUSE"
case|:
return|return
operator|new
name|ClickHouseSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"DBMS:CLOUDSCAPE"
case|:
return|return
operator|new
name|DerbySqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"HIVE"
case|:
return|return
operator|new
name|HiveSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"INGRES"
case|:
return|return
operator|new
name|IngresSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"INTERBASE"
case|:
return|return
operator|new
name|InterbaseSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"JETHRODATA"
case|:
return|return
operator|new
name|JethroDataSqlDialect
argument_list|(
name|c
operator|.
name|withJethroInfo
argument_list|(
name|jethroCache
operator|.
name|get
argument_list|(
name|databaseMetaData
argument_list|)
argument_list|)
argument_list|)
return|;
case|case
literal|"LUCIDDB"
case|:
return|return
operator|new
name|LucidDbSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"ORACLE"
case|:
return|return
operator|new
name|OracleSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"PHOENIX"
case|:
return|return
operator|new
name|PhoenixSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"MYSQL (INFOBRIGHT)"
case|:
return|return
operator|new
name|InfobrightSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"MYSQL"
case|:
return|return
operator|new
name|MysqlSqlDialect
argument_list|(
name|c
operator|.
name|withDataTypeSystem
argument_list|(
name|MysqlSqlDialect
operator|.
name|MYSQL_TYPE_SYSTEM
argument_list|)
argument_list|)
return|;
case|case
literal|"REDSHIFT"
case|:
return|return
operator|new
name|RedshiftSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"SNOWFLAKE"
case|:
return|return
operator|new
name|SnowflakeSqlDialect
argument_list|(
name|c
argument_list|)
return|;
case|case
literal|"SPARK"
case|:
return|return
operator|new
name|SparkSqlDialect
argument_list|(
name|c
argument_list|)
return|;
block|}
comment|// Now the fuzzy matches.
if|if
condition|(
name|databaseProductName
operator|.
name|startsWith
argument_list|(
literal|"DB2"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Db2SqlDialect
argument_list|(
name|c
argument_list|)
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
operator|new
name|FirebirdSqlDialect
argument_list|(
name|c
argument_list|)
return|;
block|}
if|else if
condition|(
name|databaseProductName
operator|.
name|startsWith
argument_list|(
literal|"Informix"
argument_list|)
condition|)
block|{
return|return
operator|new
name|InformixSqlDialect
argument_list|(
name|c
argument_list|)
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
operator|new
name|NetezzaSqlDialect
argument_list|(
name|c
argument_list|)
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
operator|new
name|ParaccelSqlDialect
argument_list|(
name|c
argument_list|)
return|;
block|}
if|else if
condition|(
name|databaseProductName
operator|.
name|startsWith
argument_list|(
literal|"HP Neoview"
argument_list|)
condition|)
block|{
return|return
operator|new
name|NeoviewSqlDialect
argument_list|(
name|c
argument_list|)
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
operator|new
name|PostgresqlSqlDialect
argument_list|(
name|c
operator|.
name|withDataTypeSystem
argument_list|(
name|PostgresqlSqlDialect
operator|.
name|POSTGRESQL_TYPE_SYSTEM
argument_list|)
argument_list|)
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
operator|new
name|MssqlSqlDialect
argument_list|(
name|c
argument_list|)
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
operator|new
name|SybaseSqlDialect
argument_list|(
name|c
argument_list|)
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
operator|new
name|TeradataSqlDialect
argument_list|(
name|c
argument_list|)
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
operator|new
name|HsqldbSqlDialect
argument_list|(
name|c
argument_list|)
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"H2"
argument_list|)
condition|)
block|{
return|return
operator|new
name|H2SqlDialect
argument_list|(
name|c
argument_list|)
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
operator|new
name|VerticaSqlDialect
argument_list|(
name|c
argument_list|)
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"SNOWFLAKE"
argument_list|)
condition|)
block|{
return|return
operator|new
name|SnowflakeSqlDialect
argument_list|(
name|c
argument_list|)
return|;
block|}
if|else if
condition|(
name|upperProductName
operator|.
name|contains
argument_list|(
literal|"SPARK"
argument_list|)
condition|)
block|{
return|return
operator|new
name|SparkSqlDialect
argument_list|(
name|c
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|AnsiSqlDialect
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
specifier|private
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
specifier|private
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
comment|/** Returns a basic dialect for a given product, or null if none is known. */
specifier|static
name|SqlDialect
name|simple
parameter_list|(
name|SqlDialect
operator|.
name|DatabaseProduct
name|databaseProduct
parameter_list|)
block|{
switch|switch
condition|(
name|databaseProduct
condition|)
block|{
case|case
name|ACCESS
case|:
return|return
name|AccessSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|BIG_QUERY
case|:
return|return
name|BigQuerySqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|CALCITE
case|:
return|return
name|CalciteSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|CLICKHOUSE
case|:
return|return
name|ClickHouseSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|DB2
case|:
return|return
name|Db2SqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|DERBY
case|:
return|return
name|DerbySqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|FIREBIRD
case|:
return|return
name|FirebirdSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|H2
case|:
return|return
name|H2SqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|HIVE
case|:
return|return
name|HiveSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|HSQLDB
case|:
return|return
name|HsqldbSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|INFOBRIGHT
case|:
return|return
name|InfobrightSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|INFORMIX
case|:
return|return
name|InformixSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|INGRES
case|:
return|return
name|IngresSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|INTERBASE
case|:
return|return
name|InterbaseSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|JETHRO
case|:
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Jethro does not support simple creation"
argument_list|)
throw|;
case|case
name|LUCIDDB
case|:
return|return
name|LucidDbSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|MSSQL
case|:
return|return
name|MssqlSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|MYSQL
case|:
return|return
name|MysqlSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|NEOVIEW
case|:
return|return
name|NeoviewSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|NETEZZA
case|:
return|return
name|NetezzaSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|ORACLE
case|:
return|return
name|OracleSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|PARACCEL
case|:
return|return
name|ParaccelSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|PHOENIX
case|:
return|return
name|PhoenixSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|POSTGRESQL
case|:
return|return
name|PostgresqlSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|REDSHIFT
case|:
return|return
name|RedshiftSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|SYBASE
case|:
return|return
name|SybaseSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|TERADATA
case|:
return|return
name|TeradataSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|VERTICA
case|:
return|return
name|VerticaSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|SPARK
case|:
return|return
name|SparkSqlDialect
operator|.
name|DEFAULT
return|;
case|case
name|SQLSTREAM
case|:
case|case
name|UNKNOWN
case|:
default|default:
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

