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
name|Expression
import|;
end_import

begin_comment
comment|/**  * Represents a relational dataset in a {@link RelOptSchema}. It has methods to  * describe and implement itself.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptTable
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Obtains an identifier for this table. The identifier must be unique with    * respect to the Connection producing this table.    *    * @return qualified name    */
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|()
function_decl|;
comment|/**    * Returns an estimate of the number of rows in the table.    */
name|double
name|getRowCount
parameter_list|()
function_decl|;
comment|/**    * Describes the type of rows returned by this table.    */
name|RelDataType
name|getRowType
parameter_list|()
function_decl|;
comment|/**    * Returns the {@link RelOptSchema} this table belongs to.    */
name|RelOptSchema
name|getRelOptSchema
parameter_list|()
function_decl|;
comment|/**    * Converts this table into a {@link RelNode relational expression}.    *    *<p>The {@link org.eigenbase.relopt.RelOptPlanner planner} calls this    * method to convert a table into an initial relational expression,    * generally something abstract, such as a {@link    * org.eigenbase.rel.TableAccessRel}, then optimizes this expression by    * applying {@link org.eigenbase.relopt.RelOptRule rules} to transform it    * into more efficient access methods for this table.</p>    */
name|RelNode
name|toRel
parameter_list|(
name|ToRelContext
name|context
parameter_list|)
function_decl|;
comment|/**    * Returns a description of the physical ordering (or orderings) of the rows    * returned from this table.    *    * @see RelNode#getCollationList()    */
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
function_decl|;
comment|/**    * Returns whether the given columns are a key or a superset of a unique key    * of this table.    *    * @param columns Ordinals of key columns    * @return Whether the given columns are a key or a superset of a key    */
name|boolean
name|isKey
parameter_list|(
name|BitSet
name|columns
parameter_list|)
function_decl|;
comment|/**    * Finds an interface implemented by this table.    */
parameter_list|<
name|T
parameter_list|>
name|T
name|unwrap
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|clazz
parameter_list|)
function_decl|;
comment|/**    * Generates code for this table.    *    * @param clazz The desired collection class; for example {@code Queryable}.    */
name|Expression
name|getExpression
parameter_list|(
name|Class
name|clazz
parameter_list|)
function_decl|;
comment|/** Can expand a view into relational expressions. */
interface|interface
name|ViewExpander
block|{
name|RelNode
name|expandView
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|String
name|queryString
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|)
function_decl|;
block|}
comment|/** Contains the context needed to convert a a table into a relational    * expression. */
interface|interface
name|ToRelContext
extends|extends
name|ViewExpander
block|{
name|RelOptCluster
name|getCluster
parameter_list|()
function_decl|;
block|}
block|}
end_interface

begin_comment
comment|// End RelOptTable.java
end_comment

end_unit

