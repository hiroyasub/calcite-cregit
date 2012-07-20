begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerator
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|QueryProvider
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|QueryProviderImpl
import|;
end_import

begin_comment
comment|/**  * Implementation of {@link QueryProvider} that invokes a query planner  * when converting a {@link net.hydromatic.linq4j.Queryable} into an  * executable plan.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|OptiqQueryProvider
extends|extends
name|QueryProviderImpl
block|{
specifier|public
specifier|static
specifier|final
name|OptiqQueryProvider
name|INSTANCE
init|=
operator|new
name|OptiqQueryProvider
argument_list|()
decl_stmt|;
specifier|private
name|OptiqQueryProvider
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|protected
parameter_list|<
name|T
parameter_list|>
name|Enumerator
argument_list|<
name|T
argument_list|>
name|executeQuery
parameter_list|(
name|QueryableImpl
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
comment|// TODO:
block|}
block|}
end_class

begin_comment
comment|// End OptiqQueryProvider.java
end_comment

end_unit

