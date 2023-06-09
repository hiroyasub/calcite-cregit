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
name|plan
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apiguardian
operator|.
name|api
operator|.
name|API
import|;
end_import

begin_comment
comment|/**  * The digest is the exact representation of the corresponding {@code RelNode},  * at anytime, anywhere. The only difference is that digest is compared using  * {@code #equals} and {@code #hashCode}, which are prohibited to override  * for RelNode, for legacy reasons.  *  *<p>INTERNAL USE ONLY.</p>  */
end_comment

begin_interface
annotation|@
name|API
argument_list|(
name|since
operator|=
literal|"1.24"
argument_list|,
name|status
operator|=
name|API
operator|.
name|Status
operator|.
name|INTERNAL
argument_list|)
specifier|public
interface|interface
name|RelDigest
block|{
comment|/**    * Reset state, possibly cache of hash code.    */
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * Returns the relnode that this digest is associated with.    */
name|RelNode
name|getRel
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

