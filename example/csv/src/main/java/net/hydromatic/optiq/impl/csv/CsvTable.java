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
name|impl
operator|.
name|csv
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|AbstractTable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|*
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * Base class for table that reads CSV files.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CsvTable
extends|extends
name|AbstractTable
block|{
specifier|protected
specifier|final
name|File
name|file
decl_stmt|;
specifier|private
specifier|final
name|RelProtoDataType
name|protoRowType
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
decl_stmt|;
comment|/** Creates a CsvAbstractTable. */
name|CsvTable
parameter_list|(
name|File
name|file
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|)
block|{
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|protoRowType
operator|=
name|protoRowType
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
if|if
condition|(
name|protoRowType
operator|!=
literal|null
condition|)
block|{
return|return
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
return|;
block|}
if|if
condition|(
name|fieldTypes
operator|==
literal|null
condition|)
block|{
name|fieldTypes
operator|=
operator|new
name|ArrayList
argument_list|<
name|CsvFieldType
argument_list|>
argument_list|()
expr_stmt|;
return|return
name|CsvEnumerator
operator|.
name|deduceRowType
argument_list|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
argument_list|,
name|file
argument_list|,
name|fieldTypes
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|CsvEnumerator
operator|.
name|deduceRowType
argument_list|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
argument_list|,
name|file
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
comment|/** Various degrees of table "intelligence". */
specifier|public
enum|enum
name|Flavor
block|{
name|SCANNABLE
block|,
name|FILTERABLE
block|,
name|TRANSLATABLE
block|}
block|}
end_class

begin_comment
comment|// End CsvTable.java
end_comment

end_unit

