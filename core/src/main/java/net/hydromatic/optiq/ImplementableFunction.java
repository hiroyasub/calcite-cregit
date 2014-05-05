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
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|CallImplementor
import|;
end_import

begin_comment
comment|/**  * Function that can be translated to java code.  *<p>  * @see net.hydromatic.optiq.ScalarFunction  * @see net.hydromatic.optiq.TableFunction  */
end_comment

begin_interface
specifier|public
interface|interface
name|ImplementableFunction
extends|extends
name|Function
block|{
comment|/**    * Returns implementor that translates the function to linq4j expression.    * @return implementor that translates the function to linq4j expression.    */
name|CallImplementor
name|getImplementor
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End ImplementableFunction.java
end_comment

end_unit

