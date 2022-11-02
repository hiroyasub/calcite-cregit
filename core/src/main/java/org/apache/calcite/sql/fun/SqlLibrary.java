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
name|fun
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
name|config
operator|.
name|CalciteConnectionProperty
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
name|base
operator|.
name|Preconditions
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
name|ImmutableList
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
name|ImmutableMap
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
comment|/**  * A library is a collection of SQL functions and operators.  *  *<p>Typically, such collections are associated with a particular dialect or  * database. For example, {@link SqlLibrary#ORACLE} is a collection of functions  * that are in the Oracle database but not the SQL standard.  *  *<p>In {@link SqlLibraryOperatorTableFactory} this annotation is applied to  * function definitions to include them in a particular library. It allows  * an operator to belong to more than one library.  *  * @see LibraryOperator  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlLibrary
block|{
comment|/** The standard operators. */
name|STANDARD
argument_list|(
literal|""
argument_list|,
literal|"standard"
argument_list|)
block|,
comment|/** Geospatial operators. */
name|SPATIAL
argument_list|(
literal|"s"
argument_list|,
literal|"spatial"
argument_list|)
block|,
comment|/** A collection of operators that are in Google BigQuery but not in standard    * SQL. */
name|BIG_QUERY
argument_list|(
literal|"b"
argument_list|,
literal|"bigquery"
argument_list|)
block|,
comment|/** A collection of operators that are in Apache Hive but not in standard    * SQL. */
name|HIVE
argument_list|(
literal|"h"
argument_list|,
literal|"hive"
argument_list|)
block|,
comment|/** A collection of operators that are in MySQL but not in standard SQL. */
name|MYSQL
argument_list|(
literal|"m"
argument_list|,
literal|"mysql"
argument_list|)
block|,
comment|/** A collection of operators that are in Microsoft SQL Server (MSSql) but not    * in standard SQL. */
name|MSSQL
argument_list|(
literal|"q"
argument_list|,
literal|"mssql"
argument_list|)
block|,
comment|/** A collection of operators that are in Oracle but not in standard SQL. */
name|ORACLE
argument_list|(
literal|"o"
argument_list|,
literal|"oracle"
argument_list|)
block|,
comment|/** A collection of operators that are in PostgreSQL but not in standard    * SQL. */
name|POSTGRESQL
argument_list|(
literal|"p"
argument_list|,
literal|"postgresql"
argument_list|)
block|,
comment|/** A collection of operators that are in Apache Spark but not in standard    * SQL. */
name|SPARK
argument_list|(
literal|"s"
argument_list|,
literal|"spark"
argument_list|)
block|;
comment|/** Abbreviation for the library used in SQL reference. */
specifier|public
specifier|final
name|String
name|abbrev
decl_stmt|;
comment|/** Name of this library when it appears in the connect string;    * see {@link CalciteConnectionProperty#FUN}. */
specifier|public
specifier|final
name|String
name|fun
decl_stmt|;
name|SqlLibrary
parameter_list|(
name|String
name|abbrev
parameter_list|,
name|String
name|fun
parameter_list|)
block|{
name|this
operator|.
name|abbrev
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|abbrev
argument_list|,
literal|"abbrev"
argument_list|)
expr_stmt|;
name|this
operator|.
name|fun
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|fun
argument_list|,
literal|"fun"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|fun
operator|.
name|equals
argument_list|(
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|replace
argument_list|(
literal|"_"
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Looks up a value.    * Returns null if not found.    * You can use upper- or lower-case name. */
specifier|public
specifier|static
annotation|@
name|Nullable
name|SqlLibrary
name|of
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|MAP
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/** Parses a comma-separated string such as "standard,oracle". */
specifier|public
specifier|static
name|List
argument_list|<
name|SqlLibrary
argument_list|>
name|parse
parameter_list|(
name|String
name|libraryNameList
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|SqlLibrary
argument_list|>
name|list
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|libraryName
range|:
name|libraryNameList
operator|.
name|split
argument_list|(
literal|","
argument_list|)
control|)
block|{
name|SqlLibrary
name|library
init|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|SqlLibrary
operator|.
name|of
argument_list|(
name|libraryName
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"library does not exist: "
operator|+
name|libraryName
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|library
argument_list|)
expr_stmt|;
block|}
return|return
name|list
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SqlLibrary
argument_list|>
name|MAP
decl_stmt|;
static|static
block|{
specifier|final
name|ImmutableMap
operator|.
name|Builder
argument_list|<
name|String
argument_list|,
name|SqlLibrary
argument_list|>
name|builder
init|=
name|ImmutableMap
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlLibrary
name|value
range|:
name|values
argument_list|()
control|)
block|{
name|builder
operator|.
name|put
argument_list|(
name|value
operator|.
name|name
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|put
argument_list|(
name|value
operator|.
name|fun
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|MAP
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
end_enum

end_unit

