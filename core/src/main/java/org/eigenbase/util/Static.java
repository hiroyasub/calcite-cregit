begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|resource
operator|.
name|EigenbaseNewResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|resource
operator|.
name|Resources
import|;
end_import

begin_comment
comment|/**  * Definitions of objects to be statically imported.  *  *<p>Developers: Give careful consideration before including an object in this  * class.  * Pros: Code that uses these objects will be terser.  * Cons: Namespace pollution,  * code that is difficult to understand (a general problem with static imports),  * potential cyclic initialization.</p>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Static
block|{
specifier|private
name|Static
parameter_list|()
block|{
block|}
comment|/** Resources. */
specifier|public
specifier|static
specifier|final
name|EigenbaseNewResource
name|RESOURCE
init|=
name|Resources
operator|.
name|create
argument_list|(
literal|"org.eigenbase.resource.EigenbaseResource"
argument_list|,
name|EigenbaseNewResource
operator|.
name|class
argument_list|)
decl_stmt|;
block|}
end_class

begin_comment
comment|// End Static.java
end_comment

end_unit

