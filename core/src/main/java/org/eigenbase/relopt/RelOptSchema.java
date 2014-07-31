begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * A<code>RelOptSchema</code> is a set of {@link RelOptTable} objects.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptSchema
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Retrieves a {@link RelOptTable} based upon a member access.    *    *<p>For example, the Saffron expression<code>salesSchema.emps</code>    * would be resolved using a call to<code>salesSchema.getTableForMember(new    * String[]{"emps" })</code>.</p>    *    *<p>Note that name.length is only greater than 1 for queries originating    * from JDBC.</p>    *    * @param names Qualified name    */
name|RelOptTable
name|getTableForMember
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
comment|/**    * Returns the {@link RelDataTypeFactory type factory} used to generate    * types for this schema.    */
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
comment|/**    * Registers all of the rules supported by this schema. Only called by    * {@link RelOptPlanner#registerSchema}.    */
name|void
name|registerRules
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelOptSchema.java
end_comment

end_unit

