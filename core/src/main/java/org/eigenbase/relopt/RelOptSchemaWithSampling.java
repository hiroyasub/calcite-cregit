begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
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
comment|/**      * Retrieves a {@link RelOptTable} based upon a member access, using a      * sample dataset if it exists.      *      * @param names Compound name of table      * @param datasetName Name of sample dataset to substitute, if it exists;      * null to not look for a sample      * @param usedDataset Output parameter which is set to true if a sample      * dataset is found; may be null      *      * @return Table, or null if not found      */
name|RelOptTable
name|getTableForMember
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|String
name|datasetName
parameter_list|,
name|boolean
index|[]
name|usedDataset
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelOptSchemaWithSampling.java
end_comment

end_unit

