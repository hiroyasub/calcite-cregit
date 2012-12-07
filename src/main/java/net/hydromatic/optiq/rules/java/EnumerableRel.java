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
operator|.
name|rules
operator|.
name|java
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|BlockExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|RelNode
import|;
end_import

begin_comment
comment|/**  * A relational expression of the  * {@link JavaRules#CONVENTION} calling convention.  *  * @author jhyde  */
end_comment

begin_interface
specifier|public
interface|interface
name|EnumerableRel
extends|extends
name|RelNode
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Creates a plan for this expression according to a calling convention.      *      * @param implementor implementor      */
name|BlockExpression
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End EnumerableRel.java
end_comment

end_unit

