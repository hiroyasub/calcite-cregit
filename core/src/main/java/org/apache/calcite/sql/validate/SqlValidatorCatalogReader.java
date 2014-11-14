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
name|sql
operator|.
name|validate
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
name|RelDataTypeField
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
name|sql
operator|.
name|SqlIdentifier
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
comment|/**  * Supplies catalog information for {@link SqlValidator}.  *  *<p>This interface only provides a thin API to the underlying repository, and  * this is intentional. By only presenting the repository information of  * interest to the validator, we reduce the dependency on exact mechanism to  * implement the repository. It is also possible to construct mock  * implementations of this interface for testing purposes.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlValidatorCatalogReader
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Finds a table with the given name, possibly qualified.    *    * @param names Qualified name of table    * @return named table, or null if not found    */
name|SqlValidatorTable
name|getTable
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
comment|/**    * Finds a user-defined type with the given name, possibly qualified.    *    *<p>NOTE jvs 12-Feb-2005: the reason this method is defined here instead    * of on RelDataTypeFactory is that it has to take into account    * context-dependent information such as SQL schema path, whereas a type    * factory is context-independent.    *    * @param typeName Name of type    * @return named type, or null if not found    */
name|RelDataType
name|getNamedType
parameter_list|(
name|SqlIdentifier
name|typeName
parameter_list|)
function_decl|;
comment|/**    * Given fully qualified schema name, returns schema object names as    * specified. They can be schema, table, function, view.    * When names array is empty, the contents of root schema should be returned.    *    * @param names the array contains fully qualified schema name or empty    *              list for root schema    * @return the list of all object (schema, table, function,    *         view) names under the above criteria    */
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|getAllSchemaObjectNames
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
comment|/**    * Returns the name of the current schema.    *    * @return name of the current schema    */
name|List
argument_list|<
name|String
argument_list|>
name|getSchemaName
parameter_list|()
function_decl|;
comment|/**    * Finds a field with a given name, using the case-sensitivity of the current    * session.    */
name|RelDataTypeField
name|field
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|alias
parameter_list|)
function_decl|;
comment|/**    * Finds the ordinal of a field with a given name, using the case-sensitivity    * of the current session.    */
name|int
name|fieldOrdinal
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|alias
parameter_list|)
function_decl|;
name|int
name|match
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|strings
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
name|RelDataType
name|createTypeFromProjection
parameter_list|(
name|RelDataType
name|type
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|columnNameList
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlValidatorCatalogReader.java
end_comment

end_unit

