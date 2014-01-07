begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|csv
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelProtoDataType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_comment
comment|/**  * Refinement of {@link CsvTable} that plans itself.  */
end_comment

begin_class
class|class
name|CsvSmartTable
extends|extends
name|CsvTable
block|{
comment|/** Creates a CsvSmartTable. */
name|CsvSmartTable
parameter_list|(
name|File
name|file
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|)
block|{
name|super
argument_list|(
name|file
argument_list|,
name|protoRowType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
block|{
comment|// Request all fields.
specifier|final
name|int
name|fieldCount
init|=
name|relOptTable
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|fields
init|=
name|CsvEnumerator
operator|.
name|identityList
argument_list|(
name|fieldCount
argument_list|)
decl_stmt|;
return|return
operator|new
name|CsvTableScan
argument_list|(
name|context
operator|.
name|getCluster
argument_list|()
argument_list|,
name|relOptTable
argument_list|,
name|this
argument_list|,
name|fields
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End CsvSmartTable.java
end_comment

end_unit

