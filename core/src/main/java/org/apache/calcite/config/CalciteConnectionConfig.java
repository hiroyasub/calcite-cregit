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
comment|/** @see CalciteConnectionProperty#AUTO_TEMP */
name|boolean
name|autoTemp
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#MATERIALIZATIONS_ENABLED */
name|boolean
name|materializationsEnabled
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#CREATE_MATERIALIZATIONS */
name|boolean
name|createMaterializations
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#DEFAULT_NULL_COLLATION */
name|NullCollation
name|defaultNullCollation
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#FUN */
parameter_list|<
name|T
parameter_list|>
name|T
name|fun
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|operatorTableClass
parameter_list|,
name|T
name|defaultOperatorTable
parameter_list|)
function_decl|;
comment|/** @see CalciteConnectionProperty#MODEL */
name|String
name|model
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#LEX */
name|Lex
name|lex
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#QUOTING */
name|Quoting
name|quoting
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#UNQUOTED_CASING */
name|Casing
name|unquotedCasing
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#QUOTED_CASING */
name|Casing
name|quotedCasing
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#CASE_SENSITIVE */
name|boolean
name|caseSensitive
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#PARSER_FACTORY */
parameter_list|<
name|T
parameter_list|>
name|T
name|parserFactory
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|parserFactoryClass
parameter_list|,
name|T
name|defaultParserFactory
parameter_list|)
function_decl|;
comment|/** @see CalciteConnectionProperty#SCHEMA_FACTORY */
parameter_list|<
name|T
parameter_list|>
name|T
name|schemaFactory
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|schemaFactoryClass
parameter_list|,
name|T
name|defaultSchemaFactory
parameter_list|)
function_decl|;
comment|/** @see CalciteConnectionProperty#SCHEMA_TYPE */
name|JsonSchema
operator|.
name|Type
name|schemaType
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#SPARK */
name|boolean
name|spark
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#FORCE_DECORRELATE */
name|boolean
name|forceDecorrelate
parameter_list|()
function_decl|;
comment|/** @see CalciteConnectionProperty#TYPE_SYSTEM */
parameter_list|<
name|T
parameter_list|>
name|T
name|typeSystem
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|typeSystemClass
parameter_list|,
name|T
name|defaultTypeSystem
parameter_list|)
function_decl|;
comment|/** @see CalciteConnectionProperty#CONFORMANCE */
name|SqlConformance
name|conformance
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End CalciteConnectionConfig.java
end_comment

end_unit

