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
name|util
operator|.
name|format
package|;
end_package

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
name|Map
import|;
end_import

begin_comment
comment|/**  * Describes the format strings used by a formatting function such as  * {@code FORMAT_TIMESTAMP} or {@code CAST(string AS DATE FORMAT formatString)}.  *  *<p>Can parse a format string to a list of format elements.  *  * @see FormatModels  */
end_comment

begin_interface
specifier|public
interface|interface
name|FormatModel
block|{
comment|/** Returns the map used to create the {@link FormatModel} instance. */
name|Map
argument_list|<
name|String
argument_list|,
name|FormatElement
argument_list|>
name|getElementMap
parameter_list|()
function_decl|;
comment|/** Parses a format string using element identifiers supplied by    * {@code format}. */
name|List
argument_list|<
name|FormatElement
argument_list|>
name|parse
parameter_list|(
name|String
name|format
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

