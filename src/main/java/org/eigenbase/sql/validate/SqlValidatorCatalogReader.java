begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Supplies catalog information for {@link SqlValidator}.  *  *<p>This interface only provides a thin API to the underlying repository, and  * this is intentional. By only presenting the repository information of  * interest to the validator, we reduce the dependency on exact mechanism to  * implement the repository. It is also possible to construct mock  * implementations of this interface for testing purposes.  *  * @author jhyde  * @version $Id$  * @since Mar 25, 2003  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlValidatorCatalogReader
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Finds a table with the given name, possibly qualified.      *      * @param names Name of table      *      * @return named table, or null if not found      */
name|SqlValidatorTable
name|getTable
parameter_list|(
name|String
index|[]
name|names
parameter_list|)
function_decl|;
comment|/**      * Finds a user-defined type with the given name, possibly qualified.      *      *<p>NOTE jvs 12-Feb-2005: the reason this method is defined here instead      * of on RelDataTypeFactory is that it has to take into account      * context-dependent information such as SQL schema path, whereas a type      * factory is context-independent.      *      * @param typeName Name of type      *      * @return named type, or null if not found      */
name|RelDataType
name|getNamedType
parameter_list|(
name|SqlIdentifier
name|typeName
parameter_list|)
function_decl|;
comment|/**      * Gets schema object names as specified. They can be schema or table      * object. If names array contain 1 element, return all schema names and all      * table names under the default schema (if that is set) If names array      * contain 2 elements, treat 1st element as schema name and return all table      * names in this schema      *      * @param names the array contains either 2 elements representing a      * partially qualified object name in the format of 'schema.object', or an      * unqualified name in the format of 'object'      *      * @return the list of all object (schema and table) names under the above      * criteria      */
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
comment|/**      * Returns the name of the current schema.      *      * @return name of the current schema      */
name|String
name|getSchemaName
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlValidatorCatalogReader.java
end_comment

end_unit

