begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
comment|/**  *<code>TableAccessRelBase</code> is an abstract base class for implementations  * of {@link TableAccessRel}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TableAccessRelBase
extends|extends
name|AbstractRelNode
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * The connection to the optimizing session.      */
specifier|protected
name|RelOptConnection
name|connection
decl_stmt|;
comment|/**      * The table definition.      */
specifier|protected
name|RelOptTable
name|table
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|TableAccessRelBase
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|RelOptConnection
name|connection
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|)
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
name|this
operator|.
name|connection
operator|=
name|connection
expr_stmt|;
if|if
condition|(
name|table
operator|.
name|getRelOptSchema
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|getPlanner
argument_list|()
operator|.
name|registerSchema
argument_list|(
name|table
operator|.
name|getRelOptSchema
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelOptConnection
name|getConnection
parameter_list|()
block|{
return|return
name|connection
return|;
block|}
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|table
operator|.
name|getRowCount
argument_list|()
return|;
block|}
specifier|public
name|RelOptTable
name|getTable
parameter_list|()
block|{
return|return
name|table
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|table
operator|.
name|getCollationList
argument_list|()
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|double
name|dRows
init|=
name|table
operator|.
name|getRowCount
argument_list|()
decl_stmt|;
name|double
name|dCpu
init|=
name|dRows
operator|+
literal|1
decl_stmt|;
comment|// ensure non-zero cost
name|double
name|dIo
init|=
literal|0
decl_stmt|;
return|return
name|planner
operator|.
name|makeCost
argument_list|(
name|dRows
argument_list|,
name|dCpu
argument_list|,
name|dIo
argument_list|)
return|;
block|}
specifier|public
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
return|return
name|table
operator|.
name|getRowType
argument_list|()
return|;
block|}
specifier|public
name|void
name|explain
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
name|pw
operator|.
name|explain
argument_list|(
name|this
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"table"
block|}
argument_list|,
operator|new
name|Object
index|[]
block|{
name|Arrays
operator|.
name|asList
argument_list|(
name|table
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End TableAccessRelBase.java
end_comment

end_unit

