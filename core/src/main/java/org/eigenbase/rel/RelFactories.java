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
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|AbstractList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|reltype
operator|.
name|RelDataTypeField
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
name|RexBuilder
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
name|RexNode
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|mapping
operator|.
name|Mappings
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  * Contains factory interface and default implementation for creating various  * rel nodes.  */
end_comment

begin_class
specifier|public
class|class
name|RelFactories
block|{
specifier|public
specifier|static
specifier|final
name|ProjectFactory
name|DEFAULT_PROJECT_FACTORY
init|=
operator|new
name|ProjectFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FilterFactory
name|DEFAULT_FILTER_FACTORY
init|=
operator|new
name|FilterFactoryImpl
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|JoinFactory
name|DEFAULT_JOIN_FACTORY
init|=
operator|new
name|JoinFactoryImpl
argument_list|()
decl_stmt|;
specifier|private
name|RelFactories
parameter_list|()
block|{
block|}
comment|/**    * Can create a {@link org.eigenbase.rel.ProjectRel} of the appropriate type    * for this rule's calling convention.    */
specifier|public
interface|interface
name|ProjectFactory
block|{
comment|/**      * Can create a {@link org.eigenbase.rel.ProjectRel} of the appropriate type      * for this rule's calling convention.      */
name|RelNode
name|createProject
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|childExprs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link ProjectFactory} that returns vanilla    * {@link ProjectRel}.    */
specifier|private
specifier|static
class|class
name|ProjectFactoryImpl
implements|implements
name|ProjectFactory
block|{
specifier|public
name|RelNode
name|createProject
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|childExprs
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
return|return
name|CalcRel
operator|.
name|createProject
argument_list|(
name|child
argument_list|,
name|childExprs
argument_list|,
name|fieldNames
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a {@link org.eigenbase.rel.FilterRel} of the appropriate type    * for this rule's calling convention.    */
specifier|public
interface|interface
name|FilterFactory
block|{
comment|/**      * Can create a {@link org.eigenbase.rel.FilterRel} of the appropriate type      * for this rule's calling convention.      */
name|RelNode
name|createFilter
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|RexNode
name|condition
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link org.eigenbase.rel.RelFactories.FilterFactory} that    * returns vanilla {@link FilterRel}.    */
specifier|private
specifier|static
class|class
name|FilterFactoryImpl
implements|implements
name|FilterFactory
block|{
specifier|public
name|RelNode
name|createFilter
parameter_list|(
name|RelNode
name|child
parameter_list|,
name|RexNode
name|condition
parameter_list|)
block|{
return|return
operator|new
name|FilterRel
argument_list|(
name|child
operator|.
name|getCluster
argument_list|()
argument_list|,
name|child
argument_list|,
name|condition
argument_list|)
return|;
block|}
block|}
comment|/**    * Can create a {@link org.eigenbase.rel.JoinRelBase} of the appropriate type    * for this rule's calling convention.    */
specifier|public
interface|interface
name|JoinFactory
block|{
comment|/**      * Creates a join.      *      * @param left             Left input      * @param right            Right input      * @param condition        Join condition      * @param joinType         Join type      * @param variablesStopped Set of names of variables which are set by the      *                         LHS and used by the RHS and are not available to      *                         nodes above this JoinRel in the tree      * @param semiJoinDone     Whether this join has been translated to a      *                         semi-join      */
name|RelNode
name|createJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
function_decl|;
block|}
comment|/**    * Implementation of {@link JoinFactory} that returns vanilla    * {@link JoinRel}.    */
specifier|private
specifier|static
class|class
name|JoinFactoryImpl
implements|implements
name|JoinFactory
block|{
specifier|public
name|RelNode
name|createJoin
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|condition
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|variablesStopped
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|left
operator|.
name|getCluster
argument_list|()
decl_stmt|;
return|return
operator|new
name|JoinRel
argument_list|(
name|cluster
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|joinType
argument_list|,
name|variablesStopped
argument_list|,
name|semiJoinDone
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelDataTypeField
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * Creates a relational expression that projects the given fields of the    * input.    *    *<p>Optimizes if the fields are the identity projection.    *    * @param factory    *          ProjectFactory    * @param child    *          Input relational expression    * @param posList    *          Source of each projected field    * @return Relational expression that projects given fields    */
specifier|public
specifier|static
name|RelNode
name|createProject
parameter_list|(
specifier|final
name|ProjectFactory
name|factory
parameter_list|,
specifier|final
name|RelNode
name|child
parameter_list|,
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|posList
parameter_list|)
block|{
if|if
condition|(
name|Mappings
operator|.
name|isIdentity
argument_list|(
name|posList
argument_list|,
name|child
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|child
return|;
block|}
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|child
operator|.
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
return|return
name|factory
operator|.
name|createProject
argument_list|(
name|child
argument_list|,
operator|new
name|AbstractList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|posList
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|RexNode
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
specifier|final
name|int
name|pos
init|=
name|posList
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
name|rexBuilder
operator|.
name|makeInputRef
argument_list|(
name|child
argument_list|,
name|pos
argument_list|)
return|;
block|}
block|}
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End RelFactories.java
end_comment

end_unit

