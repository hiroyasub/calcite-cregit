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
name|avatica
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
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
comment|/**  * Command handler for getting various metadata. Should be implemented by each  * driver.  *  *<p>Also holds other abstract methods that are not related to metadata  * that each provider must implement. This is not ideal.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Meta
block|{
name|String
name|getSqlKeywords
parameter_list|()
function_decl|;
name|String
name|getNumericFunctions
parameter_list|()
function_decl|;
name|String
name|getStringFunctions
parameter_list|()
function_decl|;
name|String
name|getSystemFunctions
parameter_list|()
function_decl|;
name|String
name|getTimeDateFunctions
parameter_list|()
function_decl|;
name|ResultSet
name|getTables
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|typeList
parameter_list|)
function_decl|;
name|ResultSet
name|getColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getSchemas
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|)
function_decl|;
name|ResultSet
name|getCatalogs
parameter_list|()
function_decl|;
name|ResultSet
name|getTableTypes
parameter_list|()
function_decl|;
name|ResultSet
name|getProcedures
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|procedureNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getProcedureColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|procedureNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getColumnPrivileges
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getTablePrivileges
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getBestRowIdentifier
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|,
name|int
name|scope
parameter_list|,
name|boolean
name|nullable
parameter_list|)
function_decl|;
name|ResultSet
name|getVersionColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|)
function_decl|;
name|ResultSet
name|getPrimaryKeys
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|)
function_decl|;
name|ResultSet
name|getImportedKeys
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|)
function_decl|;
name|ResultSet
name|getExportedKeys
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|)
function_decl|;
name|ResultSet
name|getCrossReference
parameter_list|(
name|String
name|parentCatalog
parameter_list|,
name|String
name|parentSchema
parameter_list|,
name|String
name|parentTable
parameter_list|,
name|String
name|foreignCatalog
parameter_list|,
name|String
name|foreignSchema
parameter_list|,
name|String
name|foreignTable
parameter_list|)
function_decl|;
name|ResultSet
name|getTypeInfo
parameter_list|()
function_decl|;
name|ResultSet
name|getIndexInfo
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|,
name|boolean
name|unique
parameter_list|,
name|boolean
name|approximate
parameter_list|)
function_decl|;
name|ResultSet
name|getUDTs
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|typeNamePattern
parameter_list|,
name|int
index|[]
name|types
parameter_list|)
function_decl|;
name|ResultSet
name|getSuperTypes
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|typeNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getSuperTables
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getAttributes
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|typeNamePattern
parameter_list|,
name|Pat
name|attributeNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getClientInfoProperties
parameter_list|()
function_decl|;
name|ResultSet
name|getFunctions
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|functionNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getFunctionColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|functionNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
name|ResultSet
name|getPseudoColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
comment|/** Creates a cursor for a result set. */
name|Cursor
name|createCursor
parameter_list|(
name|AvaticaResultSet
name|resultSet
parameter_list|)
function_decl|;
name|AvaticaPrepareResult
name|prepare
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|String
name|sql
parameter_list|)
function_decl|;
comment|/** Wrapper to remind API calls that a parameter is a pattern (allows '%' and    * '_' wildcards, per the JDBC spec) rather than a string to be matched    * exactly. */
class|class
name|Pat
block|{
specifier|public
specifier|final
name|String
name|s
decl_stmt|;
specifier|private
name|Pat
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
block|}
specifier|public
specifier|static
name|Pat
name|of
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Pat
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End Meta.java
end_comment

end_unit

