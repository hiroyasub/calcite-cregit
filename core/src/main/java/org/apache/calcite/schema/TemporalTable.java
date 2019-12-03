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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * Table that is temporal.  */
end_comment

begin_interface
specifier|public
interface|interface
name|TemporalTable
extends|extends
name|Table
block|{
comment|/** Returns the name of the system column that contains the start effective    * time of each row. */
annotation|@
name|Nonnull
name|String
name|getSysStartFieldName
parameter_list|()
function_decl|;
comment|/** Returns the name of the system column that contains the end effective    * time of each row. */
annotation|@
name|Nonnull
name|String
name|getSysEndFieldName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

