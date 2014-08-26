begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
comment|/**  * A<code>RelOptQuery</code> represents a set of {@link RelNode relational  * expressions} which derive from the same<code>select</code> statement.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptQuery
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/**    * Prefix to the name of correlating variables.    */
specifier|public
specifier|static
specifier|final
name|String
name|CORREL_PREFIX
init|=
literal|"$cor"
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Maps name of correlating variable (e.g. "$cor3") to the {@link RelNode}    * which implements it.    */
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelNode
argument_list|>
name|mapCorrelToRel
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RelNode
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|private
name|int
name|nextCorrel
init|=
literal|0
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a query.    *    * @param planner Planner    */
specifier|public
name|RelOptQuery
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|this
operator|.
name|planner
operator|=
name|planner
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Converts a correlating variable name into an ordinal, unqiue within the    * query.    *    * @param correlName Name of correlating variable    * @return Correlating variable ordinal    */
specifier|public
specifier|static
name|int
name|getCorrelOrdinal
parameter_list|(
name|String
name|correlName
parameter_list|)
block|{
assert|assert
name|correlName
operator|.
name|startsWith
argument_list|(
name|CORREL_PREFIX
argument_list|)
assert|;
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|correlName
operator|.
name|substring
argument_list|(
name|CORREL_PREFIX
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Creates a cluster.    *    * @param typeFactory Type factory    * @param rexBuilder  Expression builder    * @return New cluster    */
specifier|public
name|RelOptCluster
name|createCluster
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
return|return
operator|new
name|RelOptCluster
argument_list|(
name|this
argument_list|,
name|planner
argument_list|,
name|typeFactory
argument_list|,
name|rexBuilder
argument_list|)
return|;
block|}
comment|/**    * Constructs a new name for a correlating variable. It is unique within the    * whole query.    */
specifier|public
name|String
name|createCorrel
parameter_list|()
block|{
name|int
name|n
init|=
name|nextCorrel
operator|++
decl_stmt|;
return|return
name|CORREL_PREFIX
operator|+
name|n
return|;
block|}
comment|/**    * Returns the relational expression which populates a correlating variable.    */
specifier|public
name|RelNode
name|lookupCorrel
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|mapCorrelToRel
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Maps a correlating variable to a {@link RelNode}.    */
specifier|public
name|void
name|mapCorrel
parameter_list|(
name|String
name|name
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
name|mapCorrelToRel
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RelOptQuery.java
end_comment

end_unit

