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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_comment
comment|/**  * Creates a<code>SqlDialect</code> appropriate  * for a given database metadata object.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SqlDialectFactory
block|{
comment|/**    * Creates a<code>SqlDialect</code> from a DatabaseMetaData.    *    *<p>Does not maintain a reference to the DatabaseMetaData -- or, more    * importantly, to its {@link Connection} -- after this call has    * returned.    *    * @param databaseMetaData used to determine which dialect of SQL to    *                         generate    *    * @throws RuntimeException if there was an error creating the dialect    */
name|SqlDialect
name|create
parameter_list|(
name|DatabaseMetaData
name|databaseMetaData
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

