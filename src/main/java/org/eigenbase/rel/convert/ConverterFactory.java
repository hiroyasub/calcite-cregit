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
name|rel
operator|.
name|convert
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>ConverterFactory</code> is ...  *  * @author jhyde  * @version $Id$  * @since Jun 18, 2003  */
end_comment

begin_interface
specifier|public
interface|interface
name|ConverterFactory
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the calling convention this converter converts to.      */
name|CallingConvention
name|getConvention
parameter_list|()
function_decl|;
comment|/**      * Returns the calling convention this converter converts from.      */
name|CallingConvention
name|getInConvention
parameter_list|()
function_decl|;
comment|/**      * Converts a relational expression, which must have {@link      * #getInConvention} calling convention, to a relational expression of      * {@link #getConvention} calling convention.      */
name|ConverterRel
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End ConverterFactory.java
end_comment

end_unit

