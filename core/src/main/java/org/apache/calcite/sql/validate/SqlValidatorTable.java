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
name|validate
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|Wrapper
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
name|SqlAccessType
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
name|sql2rel
operator|.
name|InitializerContext
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

begin_comment
comment|/**  * Supplies a {@link SqlValidator} with the metadata for a table.  *  * @see SqlValidatorCatalogReader  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlValidatorTable
extends|extends
name|Wrapper
block|{
comment|//~ Methods ----------------------------------------------------------------
name|RelDataType
name|getRowType
parameter_list|()
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
function_decl|;
comment|/**    * Returns whether a given column is monotonic.    */
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
function_decl|;
comment|/**    * Returns the access type of the table.    */
name|SqlAccessType
name|getAllowedAccess
parameter_list|()
function_decl|;
name|boolean
name|supportsModality
parameter_list|(
name|SqlModality
name|modality
parameter_list|)
function_decl|;
comment|/**    * Returns whether the table is temporal.    */
name|boolean
name|isTemporal
parameter_list|()
function_decl|;
comment|/**    * Returns whether the ordinal column has a default value.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|boolean
name|columnHasDefaultValue
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|int
name|ordinal
parameter_list|,
name|InitializerContext
name|initializerContext
parameter_list|)
function_decl|;
comment|/** Returns the table. */
specifier|default
name|Table
name|table
parameter_list|()
block|{
return|return
name|unwrapOrThrow
argument_list|(
name|Table
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

