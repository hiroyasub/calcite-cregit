begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
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
name|ResultSetMetaData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|SQLException
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * Factory for JDBC objects.  *  *<p>There is an implementation for each supported JDBC version.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|AvaticaFactory
block|{
name|int
name|getJdbcMajorVersion
parameter_list|()
function_decl|;
name|int
name|getJdbcMinorVersion
parameter_list|()
function_decl|;
name|AvaticaConnection
name|newConnection
parameter_list|(
name|UnregisteredDriver
name|driver
parameter_list|,
name|AvaticaFactory
name|factory
parameter_list|,
name|String
name|url
parameter_list|,
name|Properties
name|info
parameter_list|)
throws|throws
name|SQLException
function_decl|;
name|AvaticaStatement
name|newStatement
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|,
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|,
name|int
name|resultSetHoldability
parameter_list|)
throws|throws
name|SQLException
function_decl|;
name|AvaticaPreparedStatement
name|newPreparedStatement
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|,
name|AvaticaPrepareResult
name|prepareResult
parameter_list|,
name|int
name|resultSetType
parameter_list|,
name|int
name|resultSetConcurrency
parameter_list|,
name|int
name|resultSetHoldability
parameter_list|)
throws|throws
name|SQLException
function_decl|;
comment|/**    * Creates a result set. You will then need to call    * {@link AvaticaResultSet#execute()} on it.    *    * @param statement Statement    * @param prepareResult Prepared statement    * @param timeZone Time zone    * @return Result set    */
name|AvaticaResultSet
name|newResultSet
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|AvaticaPrepareResult
name|prepareResult
parameter_list|,
name|TimeZone
name|timeZone
parameter_list|)
throws|throws
name|SQLException
function_decl|;
name|AvaticaDatabaseMetaData
name|newDatabaseMetaData
parameter_list|(
name|AvaticaConnection
name|connection
parameter_list|)
function_decl|;
name|ResultSetMetaData
name|newResultSetMetaData
parameter_list|(
name|AvaticaStatement
name|statement
parameter_list|,
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columnMetaDataList
parameter_list|)
throws|throws
name|SQLException
function_decl|;
block|}
end_interface

begin_comment
comment|// End AvaticaFactory.java
end_comment

end_unit

