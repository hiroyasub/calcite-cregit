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
name|lambda
operator|.
name|streams
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|lambda
operator|.
name|functions
operator|.
name|Sink
import|;
end_import

begin_comment
comment|/**  * StatefulSink.  *  *<p>Based on {@code java.util.streams.StatefulSink}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|StatefulSink
parameter_list|<
name|T
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Sink
argument_list|<
name|T
argument_list|>
block|{
name|void
name|begin
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
name|V
name|end
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End StatefulSink.java
end_comment

end_unit

