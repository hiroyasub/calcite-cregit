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
name|javac
package|;
end_package

begin_comment
comment|/**  * The interface<code>JavaCompiler</code> represents an interface to invoke a  * regular Java compiler. Classes implementing this interface should accept the  * same arguments as Sun's javac.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JavaCompiler
block|{
comment|//~ Methods ----------------------------------------------------------------
name|void
name|compile
parameter_list|()
function_decl|;
name|JavaCompilerArgs
name|getArgs
parameter_list|()
function_decl|;
name|ClassLoader
name|getClassLoader
parameter_list|()
function_decl|;
name|int
name|getTotalByteCodeSize
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End JavaCompiler.java
end_comment

end_unit

