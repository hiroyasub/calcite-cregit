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
name|java
operator|.
name|util
operator|.
name|List
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Checks whether two names are the same according to a case-sensitivity policy.  *  * @see SqlNameMatchers  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlNameMatcher
block|{
comment|/** Returns whether name matching is case-sensitive. */
name|boolean
name|isCaseSensitive
parameter_list|()
function_decl|;
comment|/** Returns a name matches another.    *    * @param string Name written in code    * @param name Name of object we are trying to match    * @return Whether matches    */
name|boolean
name|matches
parameter_list|(
name|String
name|string
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/** Looks up an item in a map. */
parameter_list|<
name|K
extends|extends
name|List
argument_list|<
name|String
argument_list|>
parameter_list|,
name|V
parameter_list|>
name|V
name|get
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|prefixNames
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
comment|/** Returns the most recent match.    *    *<p>In the default implementation,    * throws {@link UnsupportedOperationException}. */
name|String
name|bestString
parameter_list|()
function_decl|;
comment|/** Finds a field with a given name, using the current case-sensitivity,    * returning null if not found.    *    * @param rowType    Row type    * @param fieldName Field name    * @return Field, or null if not found    */
name|RelDataTypeField
name|field
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|fieldName
parameter_list|)
function_decl|;
comment|/** Returns how many times a string occurs in a collection.    *    *<p>Similar to {@link java.util.Collections#frequency}. */
name|int
name|frequency
parameter_list|(
name|Iterable
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|String
name|name
parameter_list|)
function_decl|;
comment|/** Creates a set that has the same case-sensitivity as this matcher. */
name|Set
argument_list|<
name|String
argument_list|>
name|createSet
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End SqlNameMatcher.java
end_comment

end_unit

