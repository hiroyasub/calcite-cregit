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
name|server
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|security
operator|.
name|SpnegoLoginService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * A customization of {@link SpnegoLoginService} which directly specifies the server's  * principal instead of requiring a file to exist. Known to work with Jetty-9.2.x, any other  * version would require testing/inspection to ensure the logic is still sound.  */
end_comment

begin_class
specifier|public
class|class
name|PropertyBasedSpnegoLoginService
extends|extends
name|SpnegoLoginService
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TARGET_NAME_FIELD_NAME
init|=
literal|"_targetName"
decl_stmt|;
specifier|private
specifier|final
name|String
name|serverPrincipal
decl_stmt|;
specifier|public
name|PropertyBasedSpnegoLoginService
parameter_list|(
name|String
name|realm
parameter_list|,
name|String
name|serverPrincipal
parameter_list|)
block|{
name|super
argument_list|(
name|realm
argument_list|)
expr_stmt|;
name|this
operator|.
name|serverPrincipal
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|serverPrincipal
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|doStart
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Override the parent implementation, setting _targetName to be the serverPrincipal
comment|// without the need for a one-line file to do the same thing.
comment|//
comment|// AbstractLifeCycle's doStart() method does nothing, so we aren't missing any extra logic.
specifier|final
name|Field
name|targetNameField
init|=
name|SpnegoLoginService
operator|.
name|class
operator|.
name|getDeclaredField
argument_list|(
name|TARGET_NAME_FIELD_NAME
argument_list|)
decl_stmt|;
name|targetNameField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|targetNameField
operator|.
name|set
argument_list|(
name|this
argument_list|,
name|serverPrincipal
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End PropertyBasedSpnegoLoginService.java
end_comment

end_unit

