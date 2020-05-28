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
name|plan
package|;
end_package

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

begin_comment
comment|/**  * Extension to {@link RelOptSchema} with support for sample data-sets.  *  * @see RelOptSchema  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptSchemaWithSampling
extends|extends
name|RelOptSchema
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Retrieves a {@link RelOptTable} based upon a member access, using a    * sample dataset if it exists.    *    * @param names       Compound name of table    * @param datasetName Name of sample dataset to substitute, if it exists;    *                    null to not look for a sample    * @param usedDataset Output parameter which is set to true if a sample    *                    dataset is found; may be null    * @return Table, or null if not found    */
annotation|@
name|Nullable
name|RelOptTable
name|getTableForMember
argument_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
argument_list|,
annotation|@
name|Nullable
name|String
name|datasetName
argument_list|,
name|boolean
expr|@
name|Nullable
index|[]
name|usedDataset
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

