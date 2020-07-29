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
name|file
operator|.
name|JsonEnumerator
operator|.
name|JsonDataConverter
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
name|schema
operator|.
name|Statistic
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
name|Statistics
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
name|List
import|;
end_import

begin_comment
comment|/**  * Table based on a JSON file.  */
end_comment

begin_class
specifier|public
class|class
name|JsonTable
extends|extends
name|AbstractTable
block|{
specifier|private
specifier|final
name|Source
name|source
decl_stmt|;
specifier|private
name|RelDataType
name|rowType
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Object
argument_list|>
name|dataList
decl_stmt|;
specifier|public
name|JsonTable
parameter_list|(
name|Source
name|source
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
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
name|rowType
operator|==
literal|null
condition|)
block|{
name|rowType
operator|=
name|JsonEnumerator
operator|.
name|deduceRowType
argument_list|(
name|typeFactory
argument_list|,
name|source
argument_list|)
operator|.
name|getRelDataType
argument_list|()
expr_stmt|;
block|}
return|return
name|rowType
return|;
block|}
comment|/** Returns the data list of the table. */
specifier|public
name|List
argument_list|<
name|Object
argument_list|>
name|getDataList
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
if|if
condition|(
name|dataList
operator|==
literal|null
condition|)
block|{
name|JsonDataConverter
name|jsonDataConverter
init|=
name|JsonEnumerator
operator|.
name|deduceRowType
argument_list|(
name|typeFactory
argument_list|,
name|source
argument_list|)
decl_stmt|;
name|dataList
operator|=
name|jsonDataConverter
operator|.
name|getDataList
argument_list|()
expr_stmt|;
block|}
return|return
name|dataList
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|UNKNOWN
return|;
block|}
block|}
end_class

end_unit
