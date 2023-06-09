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
name|jdbc
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
name|schema
operator|.
name|Schema
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
name|impl
operator|.
name|AbstractSchema
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|MetaImpl
operator|.
name|MetaColumn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|MetaImpl
operator|.
name|MetaTable
import|;
end_import

begin_comment
comment|/** Schema that contains metadata tables such as "TABLES" and "COLUMNS". */
end_comment

begin_class
class|class
name|MetadataSchema
extends|extends
name|AbstractSchema
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|TABLE_MAP
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"COLUMNS"
argument_list|,
operator|new
name|CalciteMetaImpl
operator|.
name|MetadataTable
argument_list|<
name|MetaColumn
argument_list|>
argument_list|(
name|MetaColumn
operator|.
name|class
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|MetaColumn
argument_list|>
name|enumerator
parameter_list|(
specifier|final
name|CalciteMetaImpl
name|meta
parameter_list|)
block|{
specifier|final
name|String
name|catalog
decl_stmt|;
try|try
block|{
name|catalog
operator|=
name|meta
operator|.
name|getConnection
argument_list|()
operator|.
name|getCatalog
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|meta
operator|.
name|tables
argument_list|(
name|catalog
argument_list|)
operator|.
name|selectMany
argument_list|(
name|meta
operator|::
name|columns
argument_list|)
operator|.
name|enumerator
argument_list|()
return|;
block|}
block|}
argument_list|,
literal|"TABLES"
argument_list|,
operator|new
name|CalciteMetaImpl
operator|.
name|MetadataTable
argument_list|<
name|MetaTable
argument_list|>
argument_list|(
name|MetaTable
operator|.
name|class
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|Enumerator
argument_list|<
name|MetaTable
argument_list|>
name|enumerator
parameter_list|(
name|CalciteMetaImpl
name|meta
parameter_list|)
block|{
specifier|final
name|String
name|catalog
decl_stmt|;
try|try
block|{
name|catalog
operator|=
name|meta
operator|.
name|getConnection
argument_list|()
operator|.
name|getCatalog
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|meta
operator|.
name|tables
argument_list|(
name|catalog
argument_list|)
operator|.
name|enumerator
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Schema
name|INSTANCE
init|=
operator|new
name|MetadataSchema
argument_list|()
decl_stmt|;
comment|/** Creates the data dictionary, also called the information schema. It is a    * schema called "metadata" that contains tables "TABLES", "COLUMNS" etc. */
specifier|private
name|MetadataSchema
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Table
argument_list|>
name|getTableMap
parameter_list|()
block|{
return|return
name|TABLE_MAP
return|;
block|}
block|}
end_class

end_unit

