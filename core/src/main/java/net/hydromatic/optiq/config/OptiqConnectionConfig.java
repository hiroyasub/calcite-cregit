begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|config
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|Casing
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|ConnectionConfig
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|avatica
operator|.
name|Quoting
import|;
end_import

begin_comment
comment|/** Interface for reading connection properties within Optiq code. There is  * a method for every property. At some point there will be similar config  * classes for system and statement properties. */
end_comment

begin_interface
specifier|public
interface|interface
name|OptiqConnectionConfig
extends|extends
name|ConnectionConfig
block|{
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#AUTO_TEMP */
name|boolean
name|autoTemp
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#MATERIALIZATIONS_ENABLED */
name|boolean
name|materializationsEnabled
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#CREATE_MATERIALIZATIONS */
name|boolean
name|createMaterializations
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#MODEL */
name|String
name|model
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#LEX */
name|Lex
name|lex
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#QUOTING */
name|Quoting
name|quoting
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#UNQUOTED_CASING */
name|Casing
name|unquotedCasing
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#QUOTED_CASING */
name|Casing
name|quotedCasing
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#CASE_SENSITIVE */
name|boolean
name|caseSensitive
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#SPARK */
name|boolean
name|spark
parameter_list|()
function_decl|;
comment|/** @see net.hydromatic.optiq.config.OptiqConnectionProperty#TYPE_SYSTEM */
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
block|}
end_interface

begin_comment
comment|// End OptiqConnectionConfig.java
end_comment

end_unit

