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
name|avatica
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DatabaseMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Avatica additions to the JDBC {@link DatabaseMetaData} interface. An instance of this is can be  * obtained by using {@link #unwrap(Class)} to cast an instance of {@link DatabaseMetaData} to  * {@link AvaticaDatabaseMetaData}. {@link #isWrapperFor(Class)} can be used to ensure that  * the generic interface can be cast to the desired class.  *  *<p>A list of all available server-side properties is enumerated by  * {@link org.apache.calcite.avatica.Meta.DatabaseProperty}. The name of the enum value will be  * the name of the key in the {@link Properties} returned.  *  *<p>Some properties defined in {@link org.apache.calcite.avatica.Meta.DatabaseProperty} do not  * correspond to a typical JDBC method/property. Those are enumerated here:  *<table>  *<tr><th>Property</th><th>Method</th></tr>  *<tr><td>AVATICA_VERSION</td><td>getAvaticaServerVersion()</td></tr>  *</table>  */
end_comment

begin_interface
specifier|public
interface|interface
name|AvaticaDatabaseMetaData
extends|extends
name|DatabaseMetaData
block|{
comment|/**    * Retrieves all Avatica-centric properties from the server. See    * {@link org.apache.calcite.avatica.Meta.DatabaseProperty} for a list of properties that will be    * returned.    *    * @return A {@link Properties} instance containing Avatica properties.    */
name|Properties
name|getRemoteAvaticaProperties
parameter_list|()
function_decl|;
comment|/**    * Retrieves the Avatica version from the server.    * @return A string corresponding to the server's version.    */
name|String
name|getAvaticaServerVersion
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End AvaticaDatabaseMetaData.java
end_comment

end_unit

