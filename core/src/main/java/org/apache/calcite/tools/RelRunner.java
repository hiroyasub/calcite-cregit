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
name|tools
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
name|RelNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
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
comment|/**  * Runs a relational expression.  *  *<p>Experimental.  *  * @see RelRunners  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelRunner
block|{
comment|/** Prepares a statement based on a relational expression. */
annotation|@
name|Deprecated
comment|// to be removed before 1.28
name|PreparedStatement
name|prepare
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
comment|/** Prepares a statement based on a relational expression.    *    * @param rel Relational expression    * @throws SQLException on error */
specifier|default
name|PreparedStatement
name|prepareStatement
parameter_list|(
name|RelNode
name|rel
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|prepare
argument_list|(
name|rel
argument_list|)
return|;
block|}
block|}
end_interface

end_unit

