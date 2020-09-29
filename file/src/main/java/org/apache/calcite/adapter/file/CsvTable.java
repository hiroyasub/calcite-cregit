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
name|adapter
operator|.
name|file
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
name|adapter
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|rel
operator|.
name|type
operator|.
name|RelProtoDataType
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
name|impl
operator|.
name|AbstractTable
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
name|Source
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
comment|/**  * Base class for table that reads CSV files.  *  *<p>Copied from {@code CsvFilterableTable} in demo CSV adapter,  * with more advanced features.  */
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
name|Source
name|source
decl_stmt|;
specifier|protected
specifier|final
name|RelProtoDataType
name|protoRowType
decl_stmt|;
specifier|private
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|fieldTypes
decl_stmt|;
comment|/** Creates a CsvTable. */
name|CsvTable
parameter_list|(
name|Source
name|source
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|protoRowType
operator|=
name|protoRowType
expr_stmt|;
block|}
annotation|@
name|Override
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
name|rowType
operator|==
literal|null
condition|)
block|{
name|rowType
operator|=
name|CsvEnumerator
operator|.
name|deduceRowType
argument_list|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
argument_list|,
name|source
argument_list|,
literal|null
argument_list|,
name|isStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|rowType
return|;
block|}
comment|/** Returns the field types of this CSV table. */
specifier|public
name|List
argument_list|<
name|CsvFieldType
argument_list|>
name|getFieldTypes
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
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
argument_list|<>
argument_list|()
expr_stmt|;
name|CsvEnumerator
operator|.
name|deduceRowType
argument_list|(
operator|(
name|JavaTypeFactory
operator|)
name|typeFactory
argument_list|,
name|source
argument_list|,
name|fieldTypes
argument_list|,
name|isStream
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldTypes
return|;
block|}
comment|/** Returns whether the table represents a stream. */
specifier|protected
name|boolean
name|isStream
parameter_list|()
block|{
return|return
literal|false
return|;
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

end_unit

