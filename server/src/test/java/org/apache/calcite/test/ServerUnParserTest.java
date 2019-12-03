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
name|test
package|;
end_package

begin_comment
comment|/**  * Extension to {@link ServerParserTest} that ensures that every expression can  * un-parse successfully.  */
end_comment

begin_class
specifier|public
class|class
name|ServerUnParserTest
extends|extends
name|ServerParserTest
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|ServerUnParserTest
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|protected
name|Tester
name|getTester
parameter_list|()
block|{
return|return
operator|new
name|UnparsingTesterImpl
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isUnparserTest
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

