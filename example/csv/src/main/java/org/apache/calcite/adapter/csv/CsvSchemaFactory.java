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
name|SchemaFactory
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
name|Locale
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
comment|/**  * Factory that creates a {@link CsvSchema}.  *  *<p>Allows a custom schema to be included in a<code><i>model</i>.json</code>  * file.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
class|class
name|CsvSchemaFactory
implements|implements
name|SchemaFactory
block|{
comment|/** Public singleton, per factory contract. */
specifier|public
specifier|static
specifier|final
name|CsvSchemaFactory
name|INSTANCE
init|=
operator|new
name|CsvSchemaFactory
argument_list|()
decl_stmt|;
specifier|private
name|CsvSchemaFactory
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|Schema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
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
parameter_list|)
block|{
specifier|final
name|String
name|directory
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"directory"
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
name|File
name|directoryFile
init|=
operator|new
name|File
argument_list|(
name|directory
argument_list|)
decl_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
operator|&&
operator|!
name|directoryFile
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|directoryFile
operator|=
operator|new
name|File
argument_list|(
name|base
argument_list|,
name|directory
argument_list|)
expr_stmt|;
block|}
name|String
name|flavorName
init|=
operator|(
name|String
operator|)
name|operand
operator|.
name|get
argument_list|(
literal|"flavor"
argument_list|)
decl_stmt|;
name|CsvTable
operator|.
name|Flavor
name|flavor
decl_stmt|;
if|if
condition|(
name|flavorName
operator|==
literal|null
condition|)
block|{
name|flavor
operator|=
name|CsvTable
operator|.
name|Flavor
operator|.
name|SCANNABLE
expr_stmt|;
block|}
else|else
block|{
name|flavor
operator|=
name|CsvTable
operator|.
name|Flavor
operator|.
name|valueOf
argument_list|(
name|flavorName
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|CsvSchema
argument_list|(
name|directoryFile
argument_list|,
name|flavor
argument_list|)
return|;
block|}
block|}
end_class

end_unit

