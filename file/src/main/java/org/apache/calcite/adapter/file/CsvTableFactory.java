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
name|model
operator|.
name|ModelHandler
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
name|RelDataTypeImpl
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
name|SchemaPlus
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
name|TableFactory
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Sources
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory that creates a {@link CsvTranslatableTable}.  *  *<p>Allows a file-based table to be included in a model.json file, even in a  * schema that is not based upon {@link FileSchema}.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
class|class
name|CsvTableFactory
implements|implements
name|TableFactory
argument_list|<
name|CsvTable
argument_list|>
block|{
comment|// public constructor, per factory contract
specifier|public
name|CsvTableFactory
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|CsvTable
name|create
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|String
name|fileName
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|base
init|=
operator|(
name|File
operator|)
name|operand
operator|.
name|get
argument_list|(
name|ModelHandler
operator|.
name|ExtraOperand
operator|.
name|BASE_DIRECTORY
operator|.
name|camelName
argument_list|)
decl_stmt|;
specifier|final
name|Source
name|source
init|=
name|Sources
operator|.
name|file
argument_list|(
name|base
argument_list|,
name|fileName
argument_list|)
decl_stmt|;
specifier|final
name|RelProtoDataType
name|protoRowType
init|=
name|rowType
operator|!=
literal|null
condition|?
name|RelDataTypeImpl
operator|.
name|proto
argument_list|(
name|rowType
argument_list|)
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|CsvTranslatableTable
argument_list|(
name|source
argument_list|,
name|protoRowType
argument_list|)
return|;
block|}
block|}
end_class

end_unit

