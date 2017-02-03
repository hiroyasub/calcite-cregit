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
name|linq4j
operator|.
name|tree
operator|.
name|Expression
import|;
end_import

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
name|RelCollation
import|;
end_import

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
name|RelDistribution
import|;
end_import

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
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|RelRoot
import|;
end_import

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
name|metadata
operator|.
name|RelMetadataQuery
import|;
end_import

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
name|type
operator|.
name|RelDataType
import|;
end_import

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
name|type
operator|.
name|RelDataTypeField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|Wrapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|ImmutableBitSet
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
comment|/**  * Represents a relational dataset in a {@link RelOptSchema}. It has methods to  * describe and implement itself.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelOptTable
extends|extends
name|Wrapper
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
comment|/**    * Converts this table into a {@link RelNode relational expression}.    *    *<p>The {@link org.apache.calcite.plan.RelOptPlanner planner} calls this    * method to convert a table into an initial relational expression,    * generally something abstract, such as a    * {@link org.apache.calcite.rel.logical.LogicalTableScan},    * then optimizes this expression by    * applying {@link org.apache.calcite.plan.RelOptRule rules} to transform it    * into more efficient access methods for this table.</p>    */
name|RelNode
name|toRel
parameter_list|(
name|ToRelContext
name|context
parameter_list|)
function_decl|;
comment|/**    * Returns a description of the physical ordering (or orderings) of the rows    * returned from this table.    *    * @see RelMetadataQuery#collations(RelNode)    */
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
function_decl|;
comment|/**    * Returns a description of the physical distribution of the rows    * in this table.    *    * @see RelMetadataQuery#distribution(RelNode)    */
name|RelDistribution
name|getDistribution
parameter_list|()
function_decl|;
comment|/**    * Returns whether the given columns are a key or a superset of a unique key    * of this table.    *    * @param columns Ordinals of key columns    * @return Whether the given columns are a key or a superset of a key    */
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
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
comment|/** Returns a table with the given extra fields. */
name|RelOptTable
name|extend
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|extendedFields
parameter_list|)
function_decl|;
comment|/** Can expand a view into relational expressions. */
interface|interface
name|ViewExpander
block|{
comment|/**      * Returns a relational expression that is to be substituted for an access      * to a SQL view.      *      * @param rowType Row type of the view      * @param queryString Body of the view      * @param schemaPath Path of a schema wherein to find referenced tables      * @param viewPath Path of the view, ending with its name; may be null      * @return Relational expression      */
name|RelRoot
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
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|viewPath
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

