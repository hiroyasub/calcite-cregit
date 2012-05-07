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
name|relopt
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
name|reltype
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Represents a relational dataset in a {@link RelOptSchema}. It has methods to  * describe and implement itself.  *  * @author jhyde  * @version $Id$  * @since 10 November, 2001  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptTable
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Obtains an identifier for this table. The identifier must be unique with      * respect to the Connection producing this table.      *      * @return qualified name      */
name|String
index|[]
name|getQualifiedName
parameter_list|()
function_decl|;
comment|/**      * Returns an estimate of the number of rows in the table.      */
name|double
name|getRowCount
parameter_list|()
function_decl|;
comment|/**      * Describes the type of rows returned by this table.      */
name|RelDataType
name|getRowType
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link RelOptSchema} this table belongs to.      */
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
function_decl|;
comment|/**      * Converts this table into a {@link RelNode relational expression}.      *      *<p>The {@link org.eigenbase.relopt.RelOptPlanner planner} calls this      * method to convert a table into an initial relational expression,      * generally something abstract, such as a {@link      * org.eigenbase.rel.TableAccessRel}, then optimizes this expression by      * applying {@link org.eigenbase.relopt.RelOptRule rules} to transform it      * into more efficient access methods for this table.</p>      *      * @param cluster the cluster the relational expression will belong to      * @param connection the parse tree of the expression which evaluates to a      * connection object      *      * @pre cluster != null      * @pre connection != null      */
name|RelNode
name|toRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptConnection
name|connection
parameter_list|)
function_decl|;
comment|/**      * Returns a description of the physical ordering (or orderings) of the rows      * returned from this table.      *      * @see RelNode#getCollationList()      * @post return != null      */
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelOptTable.java
end_comment

end_unit

