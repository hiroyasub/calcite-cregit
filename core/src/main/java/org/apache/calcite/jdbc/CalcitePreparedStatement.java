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
name|jdbc
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
name|avatica
operator|.
name|AvaticaPreparedStatement
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
name|avatica
operator|.
name|Meta
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

begin_comment
comment|/**  * Implementation of {@link java.sql.PreparedStatement}  * for the Calcite engine.  *  *<p>This class has sub-classes which implement JDBC 3.0 and JDBC 4.0 APIs;  * it is instantiated using  * {@link org.apache.calcite.avatica.AvaticaFactory#newPreparedStatement}.  */
end_comment

begin_class
specifier|abstract
class|class
name|CalcitePreparedStatement
extends|extends
name|AvaticaPreparedStatement
block|{
comment|/**    * Creates a CalcitePreparedStatement.    *    * @param connection Connection    * @param h Statement handle    * @param signature Result of preparing statement    * @param resultSetType Result set type    * @param resultSetConcurrency Result set concurrency    * @param resultSetHoldability Result set holdability    * @throws SQLException if database error occurs    */
specifier|protected
name|CalcitePreparedStatement
parameter_list|(
name|CalciteConnectionImpl
name|connection
parameter_list|,
name|Meta
operator|.
name|StatementHandle
name|h
parameter_list|,
name|Meta
operator|.
name|Signature
name|signature
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
block|{
name|super
argument_list|(
name|connection
argument_list|,
name|h
argument_list|,
name|signature
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|CalciteConnectionImpl
name|getConnection
parameter_list|()
block|{
return|return
operator|(
name|CalciteConnectionImpl
operator|)
name|super
operator|.
name|getConnection
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End CalcitePreparedStatement.java
end_comment

end_unit

