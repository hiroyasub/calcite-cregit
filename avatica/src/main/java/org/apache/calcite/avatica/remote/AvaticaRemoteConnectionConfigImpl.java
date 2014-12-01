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
operator|.
name|remote
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
name|ConnectionConfigImpl
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
comment|/** Implementation of {@link org.apache.calcite.avatica.ConnectionConfig}  * with extra properties specific to Remote Driver. */
end_comment

begin_class
specifier|public
class|class
name|AvaticaRemoteConnectionConfigImpl
extends|extends
name|ConnectionConfigImpl
block|{
specifier|public
name|AvaticaRemoteConnectionConfigImpl
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|super
argument_list|(
name|properties
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Service
operator|.
name|Factory
name|factory
parameter_list|()
block|{
return|return
name|AvaticaRemoteConnectionProperty
operator|.
name|FACTORY
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getPlugin
argument_list|(
name|Service
operator|.
name|Factory
operator|.
name|class
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AvaticaRemoteConnectionConfigImpl.java
end_comment

end_unit

