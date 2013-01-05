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
name|org
operator|.
name|eigenbase
operator|.
name|oj
operator|.
name|stmt
operator|.
name|OJPreparingStmt
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|TableModificationRelBase
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
name|RelOptCluster
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
name|RelOptTable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Extension to {@link Table} that specifies how it is to be translated to  * a {@link org.eigenbase.rel.RelNode planner node}.  *  *<p>It is optional for a Table to implement this interface. A Table that does  * not implement this interface, a Table will be converted to an  * EnumerableTableAccessRel. Generally a Table will implements this interface to  * create a particular subclass of RelNode, and also register rules that act  * on that particular subclass of RelNode.</p>  *  * @author jhyde  */
end_comment

begin_interface
specifier|public
interface|interface
name|TranslatableTable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|Table
argument_list|<
name|T
argument_list|>
block|{
comment|/** Converts this table into a {@link RelNode relational expression}. */
name|RelNode
name|toRel
parameter_list|(
name|RelOptTable
operator|.
name|ToRelContext
name|context
parameter_list|,
name|RelOptTable
name|relOptTable
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End TranslatableTable.java
end_comment

end_unit

