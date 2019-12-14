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

begin_comment
comment|/**  * Table whose row type can be extended to include extra fields.  *  *<p>In some storage systems, especially those with "late schema", there may  * exist columns that have values in the table but which are not declared in  * the table schema. However, a particular query may wish to reference these  * columns as if they were defined in the schema. Calling the {@link #extend}  * method creates a temporarily extended table schema.  *  *<p>If the table implements extended interfaces such as  * {@link org.apache.calcite.schema.ScannableTable},  * {@link org.apache.calcite.schema.FilterableTable} or  * {@link org.apache.calcite.schema.ProjectableFilterableTable}, you may wish  * to make the table returned from {@link #extend} implement these interfaces  * as well.  */
end_comment

begin_interface
specifier|public
interface|interface
name|ExtensibleTable
extends|extends
name|Table
block|{
comment|/** Returns a table that has the row type of this table plus the given    * fields. */
name|Table
name|extend
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
function_decl|;
comment|/** Returns the starting offset of the first extended column, which may differ    * from the field count when the table stores metadata columns that are not    * counted in the row-type field count. */
name|int
name|getExtendedColumnOffset
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

