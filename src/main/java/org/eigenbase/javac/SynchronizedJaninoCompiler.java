begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|javac
package|;
end_package

begin_comment
comment|/**  * SynchronizedJaninoCompiler exists as a fallback in case Janino has  * more multi-threading bugs.  We hope never to have to use it, but  * if necessary:  *<code>alter system set "javaCompilerClassName" =  * 'org.eigenbase.javac.SynchronizedJaninoCompiler';</code>  *  * @author John Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SynchronizedJaninoCompiler
extends|extends
name|JaninoCompiler
block|{
comment|// override JaninoCompiler
specifier|public
name|void
name|compile
parameter_list|()
block|{
synchronized|synchronized
init|(
name|SynchronizedJaninoCompiler
operator|.
name|class
init|)
block|{
name|super
operator|.
name|compile
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SynchronizedJaninoCompiler.java
end_comment

end_unit

