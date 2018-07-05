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
name|chinook
package|;
end_package

begin_comment
comment|/**  * Fairy simulates environment around Calcite.  *  *<p>An example property is the user on whose behalf Calcite is running the  * current query. Other properties can change from one query to another.  * Properties are held in thread-locals, so it is safe to set a property then  * read it from the same thread.  */
end_comment

begin_class
specifier|public
class|class
name|EnvironmentFairy
block|{
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|User
argument_list|>
name|USER
init|=
name|ThreadLocal
operator|.
name|withInitial
argument_list|(
parameter_list|()
lambda|->
name|User
operator|.
name|ADMIN
argument_list|)
decl_stmt|;
specifier|private
name|EnvironmentFairy
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|User
name|getUser
parameter_list|()
block|{
return|return
name|USER
operator|.
name|get
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|login
parameter_list|(
name|User
name|user
parameter_list|)
block|{
name|USER
operator|.
name|set
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
comment|/**    * Who is emulated to being logged in?    */
specifier|public
enum|enum
name|User
block|{
name|ADMIN
block|,
name|SPECIFIC_USER
block|}
block|}
end_class

begin_comment
comment|// End EnvironmentFairy.java
end_comment

end_unit

