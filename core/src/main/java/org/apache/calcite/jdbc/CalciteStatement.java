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
name|AvaticaStatement
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|NoSuchStatementException
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
name|linq4j
operator|.
name|Queryable
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
name|server
operator|.
name|CalciteServerStatement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
comment|/**  * Implementation of {@link java.sql.Statement}  * for the Calcite engine.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|CalciteStatement
extends|extends
name|AvaticaStatement
block|{
comment|/**    * Creates a CalciteStatement.    *    * @param connection Connection    * @param h Statement handle    * @param resultSetType Result set type    * @param resultSetConcurrency Result set concurrency    * @param resultSetHoldability Result set holdability    */
name|CalciteStatement
argument_list|(
name|CalciteConnectionImpl
name|connection
argument_list|,
name|Meta
operator|.
expr|@
name|Nullable
name|StatementHandle
name|h
argument_list|,
name|int
name|resultSetType
argument_list|,
name|int
name|resultSetConcurrency
argument_list|,
name|int
name|resultSetHoldability
argument_list|)
block|{
name|super
argument_list|(
name|connection
argument_list|,
name|h
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
block|;   }
comment|// implement Statement
expr|@
name|Override
specifier|public
operator|<
name|T
operator|>
name|T
name|unwrap
argument_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|iface
argument_list|)
throws|throws
name|SQLException
block|{
if|if
condition|(
name|iface
operator|==
name|CalciteServerStatement
operator|.
name|class
condition|)
block|{
specifier|final
name|CalciteServerStatement
name|statement
decl_stmt|;
try|try
block|{
name|statement
operator|=
name|getConnection
argument_list|()
operator|.
name|server
operator|.
name|getStatement
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchStatementException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"invalid statement"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|iface
operator|.
name|cast
argument_list|(
name|statement
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|unwrap
argument_list|(
name|iface
argument_list|)
return|;
block|}
end_class

begin_function
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
name|connection
return|;
block|}
end_function

begin_function
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|CalcitePrepare
operator|.
name|CalciteSignature
argument_list|<
name|T
argument_list|>
name|prepare
parameter_list|(
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
block|{
specifier|final
name|CalciteConnectionImpl
name|calciteConnection
init|=
name|getConnection
argument_list|()
decl_stmt|;
specifier|final
name|CalcitePrepare
name|prepare
init|=
name|calciteConnection
operator|.
name|prepareFactory
operator|.
name|apply
argument_list|()
decl_stmt|;
specifier|final
name|CalciteServerStatement
name|serverStatement
decl_stmt|;
try|try
block|{
name|serverStatement
operator|=
name|calciteConnection
operator|.
name|server
operator|.
name|getStatement
argument_list|(
name|handle
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchStatementException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"invalid statement"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|CalcitePrepare
operator|.
name|Context
name|prepareContext
init|=
name|serverStatement
operator|.
name|createPrepareContext
argument_list|()
decl_stmt|;
return|return
name|prepare
operator|.
name|prepareQueryable
argument_list|(
name|prepareContext
argument_list|,
name|queryable
argument_list|)
return|;
block|}
end_function

begin_function
annotation|@
name|Override
specifier|protected
name|void
name|close_
parameter_list|()
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
operator|(
operator|(
name|CalciteConnectionImpl
operator|)
name|connection
operator|)
operator|.
name|server
operator|.
name|removeStatement
argument_list|(
name|handle
argument_list|)
expr_stmt|;
name|super
operator|.
name|close_
argument_list|()
expr_stmt|;
block|}
block|}
end_function

unit|}
end_unit

