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
name|AvaticaResultSet
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
implements|implements
name|CalciteServerStatement
block|{
name|CalciteStatement
parameter_list|(
name|CalciteConnectionImpl
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
block|{
name|super
argument_list|(
name|connection
argument_list|,
name|resultSetType
argument_list|,
name|resultSetConcurrency
argument_list|,
name|resultSetHoldability
argument_list|)
expr_stmt|;
block|}
comment|// implement Statement
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
specifier|public
name|CalciteConnectionImpl
operator|.
name|ContextImpl
name|createPrepareContext
parameter_list|()
block|{
return|return
operator|new
name|CalciteConnectionImpl
operator|.
name|ContextImpl
argument_list|(
name|getConnection
argument_list|()
argument_list|)
return|;
block|}
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|CalcitePrepare
operator|.
name|PrepareResult
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
name|CalcitePrepare
name|prepare
init|=
name|getConnection
argument_list|()
operator|.
name|prepareFactory
operator|.
name|apply
argument_list|()
decl_stmt|;
return|return
name|prepare
operator|.
name|prepareQueryable
argument_list|(
name|createPrepareContext
argument_list|()
argument_list|,
name|queryable
argument_list|)
return|;
block|}
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
name|closed
operator|=
literal|true
expr_stmt|;
specifier|final
name|CalciteConnectionImpl
name|connection1
init|=
operator|(
name|CalciteConnectionImpl
operator|)
name|connection
decl_stmt|;
name|connection1
operator|.
name|server
operator|.
name|removeStatement
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
name|openResultSet
operator|!=
literal|null
condition|)
block|{
name|AvaticaResultSet
name|c
init|=
name|openResultSet
decl_stmt|;
name|openResultSet
operator|=
literal|null
expr_stmt|;
name|c
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// If onStatementClose throws, this method will throw an exception (later
comment|// converted to SQLException), but this statement still gets closed.
name|connection1
operator|.
name|getDriver
argument_list|()
operator|.
name|handler
operator|.
name|onStatementClose
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CalciteStatement.java
end_comment

end_unit

