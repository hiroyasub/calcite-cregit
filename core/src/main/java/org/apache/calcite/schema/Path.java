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
name|schema
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
name|util
operator|.
name|Pair
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
name|RandomAccess
import|;
end_import

begin_comment
comment|/**  * Path from a root schema to a particular object (schema, table, function).  *  *<p>Examples:  *<ul>  *<li>The root schema has a single element [(root, "")].  *<li>A direct child "foo" of the root schema has a two elements  *   [(root, ""), (child, "foo")].  *</ul>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Path
extends|extends
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Schema
argument_list|>
argument_list|>
extends|,
name|RandomAccess
block|{
comment|/** Returns the parent path, or null if the path is empty. */
name|Path
name|parent
parameter_list|()
function_decl|;
comment|/** Returns the names of this path, not including the name of the root. */
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|()
function_decl|;
comment|/** Returns the schemas of this path. */
name|List
argument_list|<
name|Schema
argument_list|>
name|schemas
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End Path.java
end_comment

end_unit

