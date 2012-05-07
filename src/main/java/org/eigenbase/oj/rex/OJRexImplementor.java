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
name|oj
operator|.
name|rex
package|;
end_package

begin_import
import|import
name|openjava
operator|.
name|ptree
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
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * OJRexImplementor translates a call to a particular operator to OpenJava code.  *  *<p>Implementors are held in a {@link OJRexImplementorTable}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_interface
specifier|public
interface|interface
name|OJRexImplementor
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Implements a single {@link RexCall}.      *      * @param translator provides translation context      * @param call the call to be translated      * @param operands call's operands, which have already been translated      * independently      */
specifier|public
name|Expression
name|implement
parameter_list|(
name|RexToOJTranslator
name|translator
parameter_list|,
name|RexCall
name|call
parameter_list|,
name|Expression
index|[]
name|operands
parameter_list|)
function_decl|;
comment|/**      * Tests whether it is possible to implement a call.      *      * @param call the call for which translation is being considered      *      * @return whether the call can be implemented      */
specifier|public
name|boolean
name|canImplement
parameter_list|(
name|RexCall
name|call
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End OJRexImplementor.java
end_comment

end_unit

