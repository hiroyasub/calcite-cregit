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
name|csv
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
name|DataContext
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
name|linq4j
operator|.
name|AbstractEnumerable
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Enumerator
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
name|ScannableTable
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
name|StreamableTable
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * Table based on a CSV file.  *  *<p>It implements the {@link ScannableTable} interface, so Calcite gets  * data by calling the {@link #scan(DataContext)} method.  */
end_comment

begin_class
specifier|public
class|class
name|CsvStreamScannableTable
extends|extends
name|CsvScannableTable
implements|implements
name|StreamableTable
block|{
comment|/** Creates a CsvScannableTable. */
name|CsvStreamScannableTable
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
argument_list|<>
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
argument_list|,
literal|true
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
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"CsvStreamScannableTable"
return|;
block|}
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|fields
init|=
name|CsvEnumerator
operator|.
name|identityList
argument_list|(
name|fieldTypes
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|AtomicBoolean
name|cancelFlag
init|=
name|DataContext
operator|.
name|Variable
operator|.
name|CANCEL_FLAG
operator|.
name|get
argument_list|(
name|root
argument_list|)
decl_stmt|;
return|return
operator|new
name|AbstractEnumerable
argument_list|<
name|Object
index|[]
argument_list|>
argument_list|()
block|{
specifier|public
name|Enumerator
argument_list|<
name|Object
index|[]
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
operator|new
name|CsvEnumerator
argument_list|<>
argument_list|(
name|file
argument_list|,
name|cancelFlag
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
operator|new
name|CsvEnumerator
operator|.
name|ArrayRowConverter
argument_list|(
name|fieldTypes
argument_list|,
name|fields
argument_list|,
literal|true
argument_list|)
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
specifier|public
name|Table
name|stream
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class

begin_comment
comment|// End CsvStreamScannableTable.java
end_comment

end_unit

