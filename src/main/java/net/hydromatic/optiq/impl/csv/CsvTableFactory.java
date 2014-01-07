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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory that creates a {@link CsvTable}.  *  *<p>Allows a CSV table to be included in a model.json file, even in a  * schema that is not based upon {@link CsvSchema}.</p>  */
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
name|map
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
name|map
operator|.
name|get
argument_list|(
literal|"file"
argument_list|)
decl_stmt|;
name|Boolean
name|smart
init|=
operator|(
name|Boolean
operator|)
name|map
operator|.
name|get
argument_list|(
literal|"smart"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|file
init|=
operator|new
name|File
argument_list|(
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
if|if
condition|(
name|smart
operator|!=
literal|null
operator|&&
name|smart
condition|)
block|{
return|return
operator|new
name|CsvSmartTable
argument_list|(
name|file
argument_list|,
name|protoRowType
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|CsvTable
argument_list|(
name|file
argument_list|,
name|protoRowType
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CsvTableFactory.java
end_comment

end_unit

