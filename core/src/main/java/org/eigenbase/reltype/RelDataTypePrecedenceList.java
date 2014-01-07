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
name|reltype
package|;
end_package

begin_comment
comment|/**  * RelDataTypePrecedenceList defines a type precedence list for a particular  * type.  *  * @sql.99 Part 2 Section 9.5  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDataTypePrecedenceList
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Determines whether a type appears in this precedence list.    *    * @param type type to check    * @return true iff this list contains type    */
specifier|public
name|boolean
name|containsType
parameter_list|(
name|RelDataType
name|type
parameter_list|)
function_decl|;
comment|/**    * Compares the precedence of two types.    *    * @param type1 first type to compare    * @param type2 second type to compare    * @return positive if type1 has higher precedence; negative if type2 has    * higher precedence; 0 if types have equal precedence    * @pre containsType(type1)&& containsType(type2)    */
specifier|public
name|int
name|compareTypePrecedence
parameter_list|(
name|RelDataType
name|type1
parameter_list|,
name|RelDataType
name|type2
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelDataTypePrecedenceList.java
end_comment

end_unit

