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
name|ConnectionConfig
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
name|SqlConformance
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
name|PolyNull
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
comment|/** Interface for reading connection properties within Calcite code. There is  * a method for every property. At some point there will be similar config  * classes for system and statement properties. */
end_comment

begin_interface
specifier|public
interface|interface
name|CalciteConnectionConfig
extends|extends
name|ConnectionConfig
block|{
comment|/** Default configuration. */
name|CalciteConnectionConfigImpl
name|DEFAULT
init|=
operator|new
name|CalciteConnectionConfigImpl
argument_list|(
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#APPROXIMATE_DISTINCT_COUNT}. */
name|boolean
name|approximateDistinctCount
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#APPROXIMATE_TOP_N}. */
name|boolean
name|approximateTopN
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#APPROXIMATE_DECIMAL}. */
name|boolean
name|approximateDecimal
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#NULL_EQUAL_TO_EMPTY}. */
name|boolean
name|nullEqualToEmpty
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#AUTO_TEMP}. */
name|boolean
name|autoTemp
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#MATERIALIZATIONS_ENABLED}. */
name|boolean
name|materializationsEnabled
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#CREATE_MATERIALIZATIONS}. */
name|boolean
name|createMaterializations
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#DEFAULT_NULL_COLLATION}. */
name|NullCollation
name|defaultNullCollation
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#FUN},    * or a default operator table if not set. If {@code defaultOperatorTable}    * is not null, the result is never null. */
parameter_list|<
name|T
parameter_list|>
annotation|@
name|PolyNull
name|T
name|fun
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|operatorTableClass
parameter_list|,
annotation|@
name|PolyNull
name|T
name|defaultOperatorTable
parameter_list|)
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#MODEL}. */
annotation|@
name|Nullable
name|String
name|model
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#LEX}. */
name|Lex
name|lex
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#QUOTING}. */
name|Quoting
name|quoting
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#UNQUOTED_CASING}. */
name|Casing
name|unquotedCasing
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#QUOTED_CASING}. */
name|Casing
name|quotedCasing
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#CASE_SENSITIVE}. */
name|boolean
name|caseSensitive
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#PARSER_FACTORY},    * or a default parser if not set. If {@code defaultParserFactory}    * is not null, the result is never null. */
parameter_list|<
name|T
parameter_list|>
annotation|@
name|PolyNull
name|T
name|parserFactory
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|parserFactoryClass
parameter_list|,
annotation|@
name|PolyNull
name|T
name|defaultParserFactory
parameter_list|)
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#SCHEMA_FACTORY},    * or a default schema factory if not set. If {@code defaultSchemaFactory}    * is not null, the result is never null. */
parameter_list|<
name|T
parameter_list|>
annotation|@
name|PolyNull
name|T
name|schemaFactory
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|schemaFactoryClass
parameter_list|,
annotation|@
name|PolyNull
name|T
name|defaultSchemaFactory
parameter_list|)
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#SCHEMA_TYPE}. */
name|JsonSchema
operator|.
name|Type
name|schemaType
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#SPARK}. */
name|boolean
name|spark
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#FORCE_DECORRELATE}. */
name|boolean
name|forceDecorrelate
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#TYPE_SYSTEM},    * or a default type system if not set. If {@code defaultTypeSystem}    * is not null, the result is never null. */
parameter_list|<
name|T
parameter_list|>
annotation|@
name|PolyNull
name|T
name|typeSystem
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|typeSystemClass
parameter_list|,
annotation|@
name|PolyNull
name|T
name|defaultTypeSystem
parameter_list|)
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#CONFORMANCE}. */
name|SqlConformance
name|conformance
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#TIME_ZONE}. */
annotation|@
name|Override
name|String
name|timeZone
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#LOCALE}. */
name|String
name|locale
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#TYPE_COERCION}. */
name|boolean
name|typeCoercion
parameter_list|()
function_decl|;
comment|/** Returns the value of    * {@link CalciteConnectionProperty#LENIENT_OPERATOR_LOOKUP}. */
name|boolean
name|lenientOperatorLookup
parameter_list|()
function_decl|;
comment|/** Returns the value of {@link CalciteConnectionProperty#TOPDOWN_OPT}. */
name|boolean
name|topDownOpt
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

