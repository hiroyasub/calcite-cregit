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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Extension to {@link Table} that specifies a list of column names for  * custom star expansion. The columns specified in the list can be any  * top-level column from the Table or any field or nested field under a  * top-level column, thus each column name is returned as a list of String  * objects representing the full name of the column or field. This expansion  * list will also be used as target columns in INSERT if the original target  * column list is not present.  *  *<p>It is optional for a Table to implement this interface. If Table does  * not implement this interface, star expansion will be performed in the  * default way according to the Table's row type.</p>  *  *<p><strong>NOTE: This class is experimental and subject to  * change/removal without notice</strong>.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|CustomExpansionTable
extends|extends
name|Table
block|{
comment|/** Returns a list of column names for custom star expansion. */
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getCustomStarExpansion
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End CustomExpansionTable.java
end_comment

end_unit

