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
name|ConnectionProperty
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|model
operator|.
name|JsonSchema
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
name|validate
operator|.
name|SqlConformanceEnum
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
name|Bug
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|Properties
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|ConnectionConfigImpl
operator|.
name|PropEnv
import|;
end_import

begin_comment
comment|/**  * Properties that may be specified on the JDBC connect string.  */
end_comment

begin_enum
specifier|public
enum|enum
name|CalciteConnectionProperty
implements|implements
name|ConnectionProperty
block|{
comment|/** Whether approximate results from {@code COUNT(DISTINCT ...)} aggregate    * functions are acceptable. */
name|APPROXIMATE_DISTINCT_COUNT
argument_list|(
literal|"approximateDistinctCount"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether approximate results from "Top N" queries    * ({@code ORDER BY aggFun DESC LIMIT n}) are acceptable. */
name|APPROXIMATE_TOP_N
argument_list|(
literal|"approximateTopN"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether approximate results from aggregate functions on    * DECIMAL types are acceptable. */
name|APPROXIMATE_DECIMAL
argument_list|(
literal|"approximateDecimal"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether to treat empty strings as null for Druid Adapter.    */
name|NULL_EQUAL_TO_EMPTY
argument_list|(
literal|"nullEqualToEmpty"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether to store query results in temporary tables. */
name|AUTO_TEMP
argument_list|(
literal|"autoTemp"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether Calcite should use materializations. */
name|MATERIALIZATIONS_ENABLED
argument_list|(
literal|"materializationsEnabled"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether Calcite should create materializations. */
name|CREATE_MATERIALIZATIONS
argument_list|(
literal|"createMaterializations"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
block|,
comment|/** How NULL values should be sorted if neither NULLS FIRST nor NULLS LAST are    * specified. The default, HIGH, sorts NULL values the same as Oracle. */
name|DEFAULT_NULL_COLLATION
argument_list|(
literal|"defaultNullCollation"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
name|NullCollation
operator|.
name|HIGH
argument_list|,
literal|true
argument_list|,
name|NullCollation
operator|.
name|class
argument_list|)
block|,
comment|/** How many rows the Druid adapter should fetch at a time when executing    * "select" queries. */
name|DRUID_FETCH
argument_list|(
literal|"druidFetch"
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|,
literal|16384
argument_list|,
literal|false
argument_list|)
block|,
comment|/** URI of the model. */
name|MODEL
argument_list|(
literal|"model"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Lexical policy. */
name|LEX
argument_list|(
literal|"lex"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
name|Lex
operator|.
name|ORACLE
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Collection of built-in functions and operators. Valid values include    * "standard", "mysql", "oracle", "postgresql" and "spatial", and also    * comma-separated lists, for example "oracle,spatial". */
name|FUN
argument_list|(
literal|"fun"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"standard"
argument_list|,
literal|true
argument_list|)
block|,
comment|/** How identifiers are quoted.    *  If not specified, value from {@link #LEX} is used. */
name|QUOTING
argument_list|(
literal|"quoting"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|Quoting
operator|.
name|class
argument_list|)
block|,
comment|/** How identifiers are stored if they are quoted.    *  If not specified, value from {@link #LEX} is used. */
name|QUOTED_CASING
argument_list|(
literal|"quotedCasing"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|Casing
operator|.
name|class
argument_list|)
block|,
comment|/** How identifiers are stored if they are not quoted.    *  If not specified, value from {@link #LEX} is used. */
name|UNQUOTED_CASING
argument_list|(
literal|"unquotedCasing"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|Casing
operator|.
name|class
argument_list|)
block|,
comment|/** Whether identifiers are matched case-sensitively.    *  If not specified, value from {@link #LEX} is used. */
name|CASE_SENSITIVE
argument_list|(
literal|"caseSensitive"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Parser factory.    *    *<p>The name of a class that implements    * {@link org.apache.calcite.sql.parser.SqlParserImplFactory}. */
name|PARSER_FACTORY
argument_list|(
literal|"parserFactory"
argument_list|,
name|Type
operator|.
name|PLUGIN
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Name of initial schema. */
name|SCHEMA
argument_list|(
literal|"schema"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Schema factory.    *    *<p>The name of a class that implements    * {@link org.apache.calcite.schema.SchemaFactory}.    *    *<p>Ignored if {@link #MODEL} is specified. */
name|SCHEMA_FACTORY
argument_list|(
literal|"schemaFactory"
argument_list|,
name|Type
operator|.
name|PLUGIN
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Schema type.    *    *<p>Value may be null, "MAP", "JDBC", or "CUSTOM"    * (implicit if {@link #SCHEMA_FACTORY} is specified).    *    *<p>Ignored if {@link #MODEL} is specified. */
name|SCHEMA_TYPE
argument_list|(
literal|"schemaType"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|,
name|JsonSchema
operator|.
name|Type
operator|.
name|class
argument_list|)
block|,
comment|/** Specifies whether Spark should be used as the engine for processing that    * cannot be pushed to the source system. If false (the default), Calcite    * generates code that implements the Enumerable interface. */
name|SPARK
argument_list|(
literal|"spark"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Returns the time zone from the connect string, for example 'gmt-3'.    * If the time zone is not set then the JVM time zone is returned.    * Never null. */
name|TIME_ZONE
argument_list|(
literal|"timeZone"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|TimeZone
operator|.
name|getDefault
argument_list|()
operator|.
name|getID
argument_list|()
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Returns the locale from the connect string.    * If the locale is not set, returns the root locale.    * Never null.    * Examples of valid locales: 'en', 'en_US',    * 'de_DE', '_GB', 'en_US_WIN', 'de__POSIX', 'fr__MAC', ''. */
name|LOCALE
argument_list|(
literal|"locale"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Locale
operator|.
name|ROOT
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
block|,
comment|/** If the planner should try de-correlating as much as it is possible.    * If true (the default), Calcite de-correlates the plan. */
name|FORCE_DECORRELATE
argument_list|(
literal|"forceDecorrelate"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Type system. The name of a class that implements    * {@link org.apache.calcite.rel.type.RelDataTypeSystem} and has a public    * default constructor or an {@code INSTANCE} constant. */
name|TYPE_SYSTEM
argument_list|(
literal|"typeSystem"
argument_list|,
name|Type
operator|.
name|PLUGIN
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
block|,
comment|/** SQL conformance level. */
name|CONFORMANCE
argument_list|(
literal|"conformance"
argument_list|,
name|Type
operator|.
name|ENUM
argument_list|,
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether to make implicit type coercion when type mismatch    * for validation, default true. */
name|TYPE_COERCION
argument_list|(
literal|"typeCoercion"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
block|,
comment|/** Whether to make create implicit functions if functions do not exist    * in the operator table, default false. */
name|LENIENT_OPERATOR_LOOKUP
argument_list|(
literal|"lenientOperatorLookup"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|camelName
decl_stmt|;
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|private
specifier|final
name|Object
name|defaultValue
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|required
decl_stmt|;
specifier|private
specifier|final
name|Class
name|valueClass
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|CalciteConnectionProperty
argument_list|>
name|NAME_TO_PROPS
decl_stmt|;
comment|/** Deprecated; use {@link #TIME_ZONE}. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
specifier|static
specifier|final
name|CalciteConnectionProperty
name|TIMEZONE
init|=
name|TIME_ZONE
decl_stmt|;
static|static
block|{
name|NAME_TO_PROPS
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|CalciteConnectionProperty
name|p
range|:
name|CalciteConnectionProperty
operator|.
name|values
argument_list|()
control|)
block|{
name|NAME_TO_PROPS
operator|.
name|put
argument_list|(
name|p
operator|.
name|camelName
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|NAME_TO_PROPS
operator|.
name|put
argument_list|(
name|p
operator|.
name|name
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|CalciteConnectionProperty
parameter_list|(
name|String
name|camelName
parameter_list|,
name|Type
name|type
parameter_list|,
name|Object
name|defaultValue
parameter_list|,
name|boolean
name|required
parameter_list|)
block|{
name|this
argument_list|(
name|camelName
argument_list|,
name|type
argument_list|,
name|defaultValue
argument_list|,
name|required
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|CalciteConnectionProperty
parameter_list|(
name|String
name|camelName
parameter_list|,
name|Type
name|type
parameter_list|,
name|Object
name|defaultValue
parameter_list|,
name|boolean
name|required
parameter_list|,
name|Class
name|valueClass
parameter_list|)
block|{
name|this
operator|.
name|camelName
operator|=
name|camelName
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
name|this
operator|.
name|required
operator|=
name|required
expr_stmt|;
name|this
operator|.
name|valueClass
operator|=
name|type
operator|.
name|deduceValueClass
argument_list|(
name|defaultValue
argument_list|,
name|valueClass
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|valid
argument_list|(
name|defaultValue
argument_list|,
name|this
operator|.
name|valueClass
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|camelName
argument_list|)
throw|;
block|}
block|}
specifier|public
name|String
name|camelName
parameter_list|()
block|{
return|return
name|camelName
return|;
block|}
specifier|public
name|Object
name|defaultValue
parameter_list|()
block|{
return|return
name|defaultValue
return|;
block|}
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|Class
name|valueClass
parameter_list|()
block|{
return|return
name|valueClass
return|;
block|}
specifier|public
name|boolean
name|required
parameter_list|()
block|{
return|return
name|required
return|;
block|}
specifier|public
name|PropEnv
name|wrap
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
return|return
operator|new
name|PropEnv
argument_list|(
name|parse2
argument_list|(
name|properties
argument_list|,
name|NAME_TO_PROPS
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
comment|/** Fixed version of    * {@link org.apache.calcite.avatica.ConnectionConfigImpl#parse}    * until we upgrade Avatica. */
specifier|private
specifier|static
name|Map
argument_list|<
name|ConnectionProperty
argument_list|,
name|String
argument_list|>
name|parse2
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|ConnectionProperty
argument_list|>
name|nameToProps
parameter_list|)
block|{
name|Bug
operator|.
name|upgrade
argument_list|(
literal|"avatica-1.10"
argument_list|)
expr_stmt|;
specifier|final
name|Map
argument_list|<
name|ConnectionProperty
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|properties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
specifier|final
name|ConnectionProperty
name|connectionProperty
init|=
name|nameToProps
operator|.
name|get
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|connectionProperty
operator|==
literal|null
condition|)
block|{
comment|// For now, don't throw. It messes up sub-projects.
comment|//throw new RuntimeException("Unknown property '" + name + "'");
continue|continue;
block|}
name|map
operator|.
name|put
argument_list|(
name|connectionProperty
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
block|}
end_enum

begin_comment
comment|// End CalciteConnectionProperty.java
end_comment

end_unit

