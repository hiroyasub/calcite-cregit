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
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * OJRexImplementorTable contains, for each operator, an implementor which can  * convert a call to that operator into OpenJava code.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_interface
specifier|public
interface|interface
name|OJRexImplementorTable
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Retrieves the implementor of an operator, or null if there is no      * implementor registered.      */
specifier|public
name|OJRexImplementor
name|get
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
function_decl|;
comment|/**      * Retrieves the implementor of an aggregate, or null if there is no      * implementor registered.      */
specifier|public
name|OJAggImplementor
name|get
parameter_list|(
name|Aggregation
name|aggregation
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End OJRexImplementorTable.java
end_comment

end_unit

