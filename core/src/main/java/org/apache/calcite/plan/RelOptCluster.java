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
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|core
operator|.
name|CorrelationId
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
name|hint
operator|.
name|HintStrategyTable
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
name|DefaultRelMetadataProvider
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
name|JaninoRelMetadataProvider
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
name|MetadataFactory
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
name|MetadataFactoryImpl
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
name|RelMetadataProvider
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
name|metadata
operator|.
name|RelMetadataQueryBase
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
name|RelDataTypeFactory
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
name|rex
operator|.
name|RexBuilder
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
name|rex
operator|.
name|RexNode
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * An environment for related relational expressions during the  * optimization of a query.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptCluster
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|RelDataTypeFactory
name|typeFactory
decl_stmt|;
specifier|private
name|RelOptPlanner
name|planner
decl_stmt|;
specifier|private
specifier|final
name|AtomicInteger
name|nextCorrel
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|RelNode
argument_list|>
name|mapCorrelToRel
decl_stmt|;
specifier|private
name|RexNode
name|originalExpression
decl_stmt|;
specifier|private
specifier|final
name|RexBuilder
name|rexBuilder
decl_stmt|;
specifier|private
name|RelMetadataProvider
name|metadataProvider
decl_stmt|;
specifier|private
name|MetadataFactory
name|metadataFactory
decl_stmt|;
specifier|private
name|HintStrategyTable
name|hintStrategies
decl_stmt|;
specifier|private
specifier|final
name|RelTraitSet
name|emptyTraitSet
decl_stmt|;
specifier|private
name|RelMetadataQuery
name|mq
decl_stmt|;
specifier|private
name|Supplier
argument_list|<
name|RelMetadataQuery
argument_list|>
name|mqSupplier
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a cluster.    */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
name|RelOptCluster
parameter_list|(
name|RelOptQuery
name|query
parameter_list|,
name|RelOptPlanner
name|planner
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
name|this
argument_list|(
name|planner
argument_list|,
name|typeFactory
argument_list|,
name|rexBuilder
argument_list|,
name|query
operator|.
name|nextCorrel
argument_list|,
name|query
operator|.
name|mapCorrelToRel
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a cluster.    *    *<p>For use only from {@link #create} and {@link RelOptQuery}.    */
name|RelOptCluster
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|,
name|AtomicInteger
name|nextCorrel
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RelNode
argument_list|>
name|mapCorrelToRel
parameter_list|)
block|{
name|this
operator|.
name|nextCorrel
operator|=
name|nextCorrel
expr_stmt|;
name|this
operator|.
name|mapCorrelToRel
operator|=
name|mapCorrelToRel
expr_stmt|;
name|this
operator|.
name|planner
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|planner
argument_list|)
expr_stmt|;
name|this
operator|.
name|typeFactory
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
name|this
operator|.
name|rexBuilder
operator|=
name|rexBuilder
expr_stmt|;
name|this
operator|.
name|originalExpression
operator|=
name|rexBuilder
operator|.
name|makeLiteral
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
comment|// set up a default rel metadata provider,
comment|// giving the planner first crack at everything
name|setMetadataProvider
argument_list|(
name|DefaultRelMetadataProvider
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
name|setMetadataQuerySupplier
argument_list|(
name|RelMetadataQuery
operator|::
name|instance
argument_list|)
expr_stmt|;
name|this
operator|.
name|emptyTraitSet
operator|=
name|planner
operator|.
name|emptyTraitSet
argument_list|()
expr_stmt|;
assert|assert
name|emptyTraitSet
operator|.
name|size
argument_list|()
operator|==
name|planner
operator|.
name|getRelTraitDefs
argument_list|()
operator|.
name|size
argument_list|()
assert|;
block|}
comment|/** Creates a cluster. */
specifier|public
specifier|static
name|RelOptCluster
name|create
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RexBuilder
name|rexBuilder
parameter_list|)
block|{
return|return
operator|new
name|RelOptCluster
argument_list|(
name|planner
argument_list|,
name|rexBuilder
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|rexBuilder
argument_list|,
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
argument_list|,
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|RelOptQuery
name|getQuery
parameter_list|()
block|{
return|return
operator|new
name|RelOptQuery
argument_list|(
name|planner
argument_list|,
name|nextCorrel
argument_list|,
name|mapCorrelToRel
argument_list|)
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|RexNode
name|getOriginalExpression
parameter_list|()
block|{
return|return
name|originalExpression
return|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|void
name|setOriginalExpression
parameter_list|(
name|RexNode
name|originalExpression
parameter_list|)
block|{
name|this
operator|.
name|originalExpression
operator|=
name|originalExpression
expr_stmt|;
block|}
specifier|public
name|RelOptPlanner
name|getPlanner
parameter_list|()
block|{
return|return
name|planner
return|;
block|}
specifier|public
name|RelDataTypeFactory
name|getTypeFactory
parameter_list|()
block|{
return|return
name|typeFactory
return|;
block|}
specifier|public
name|RexBuilder
name|getRexBuilder
parameter_list|()
block|{
return|return
name|rexBuilder
return|;
block|}
specifier|public
name|RelMetadataProvider
name|getMetadataProvider
parameter_list|()
block|{
return|return
name|metadataProvider
return|;
block|}
comment|/**    * Overrides the default metadata provider for this cluster.    *    * @param metadataProvider custom provider    */
specifier|public
name|void
name|setMetadataProvider
parameter_list|(
name|RelMetadataProvider
name|metadataProvider
parameter_list|)
block|{
name|this
operator|.
name|metadataProvider
operator|=
name|metadataProvider
expr_stmt|;
name|this
operator|.
name|metadataFactory
operator|=
operator|new
name|MetadataFactoryImpl
argument_list|(
name|metadataProvider
argument_list|)
expr_stmt|;
comment|// Wrap the metadata provider as a JaninoRelMetadataProvider
comment|// and set it to the ThreadLocal,
comment|// JaninoRelMetadataProvider is required by the RelMetadataQuery.
name|RelMetadataQueryBase
operator|.
name|THREAD_PROVIDERS
operator|.
name|set
argument_list|(
name|JaninoRelMetadataProvider
operator|.
name|of
argument_list|(
name|metadataProvider
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MetadataFactory
name|getMetadataFactory
parameter_list|()
block|{
return|return
name|metadataFactory
return|;
block|}
comment|/**    * Sets up the customized {@link RelMetadataQuery} instance supplier that to    * use during rule planning.    *    *<p>Note that the {@code mqSupplier} should return    * a fresh new {@link RelMetadataQuery} instance because the instance would be    * cached in this cluster, and we may invalidate and re-generate it    * for each {@link RelOptRuleCall} cycle.    */
specifier|public
name|void
name|setMetadataQuerySupplier
parameter_list|(
name|Supplier
argument_list|<
name|RelMetadataQuery
argument_list|>
name|mqSupplier
parameter_list|)
block|{
name|this
operator|.
name|mqSupplier
operator|=
name|mqSupplier
expr_stmt|;
block|}
comment|/**    * Returns the current RelMetadataQuery.    *    *<p>This method might be changed or moved in future.    * If you have a {@link RelOptRuleCall} available,    * for example if you are in a {@link RelOptRule#onMatch(RelOptRuleCall)}    * method, then use {@link RelOptRuleCall#getMetadataQuery()} instead. */
specifier|public
name|RelMetadataQuery
name|getMetadataQuery
parameter_list|()
block|{
if|if
condition|(
name|mq
operator|==
literal|null
condition|)
block|{
name|mq
operator|=
name|this
operator|.
name|mqSupplier
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
return|return
name|mq
return|;
block|}
comment|/**    * Returns the supplier of RelMetadataQuery.    */
specifier|public
name|Supplier
argument_list|<
name|RelMetadataQuery
argument_list|>
name|getMetadataQuerySupplier
parameter_list|()
block|{
return|return
name|this
operator|.
name|mqSupplier
return|;
block|}
comment|/**    * Should be called whenever the current {@link RelMetadataQuery} becomes    * invalid. Typically invoked from {@link RelOptRuleCall#transformTo}.    */
specifier|public
name|void
name|invalidateMetadataQuery
parameter_list|()
block|{
name|mq
operator|=
literal|null
expr_stmt|;
block|}
comment|/**    * Sets up the hint propagation strategies to be used during rule planning.    *    *<p>Use<code>RelOptNode.getCluster().getHintStrategies()</code> to fetch    * the hint strategies.    *    *<p>Note that this method is only for internal use; the cluster {@code hintStrategies}    * would be always set up with the instance configured by    * {@link org.apache.calcite.sql2rel.SqlToRelConverter.Config}.    *    * @param hintStrategies The specified hint strategies to override the default one(empty)    */
specifier|public
name|void
name|setHintStrategies
parameter_list|(
name|HintStrategyTable
name|hintStrategies
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|hintStrategies
argument_list|)
expr_stmt|;
name|this
operator|.
name|hintStrategies
operator|=
name|hintStrategies
expr_stmt|;
block|}
comment|/**    * Returns the hint strategies of this cluster. It is immutable during the whole planning phrase.    */
specifier|public
name|HintStrategyTable
name|getHintStrategies
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|hintStrategies
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|hintStrategies
operator|=
name|HintStrategyTable
operator|.
name|EMPTY
expr_stmt|;
block|}
return|return
name|this
operator|.
name|hintStrategies
return|;
block|}
comment|/**    * Constructs a new id for a correlating variable. It is unique within the    * whole query.    */
specifier|public
name|CorrelationId
name|createCorrel
parameter_list|()
block|{
return|return
operator|new
name|CorrelationId
argument_list|(
name|nextCorrel
operator|.
name|getAndIncrement
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the default trait set for this cluster. */
specifier|public
name|RelTraitSet
name|traitSet
parameter_list|()
block|{
return|return
name|emptyTraitSet
return|;
block|}
comment|// CHECKSTYLE: IGNORE 2
comment|/** @deprecated For {@code traitSetOf(t1, t2)},    * use {@link #traitSet}().replace(t1).replace(t2). */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|RelTraitSet
name|traitSetOf
parameter_list|(
name|RelTrait
modifier|...
name|traits
parameter_list|)
block|{
name|RelTraitSet
name|traitSet
init|=
name|emptyTraitSet
decl_stmt|;
for|for
control|(
name|RelTrait
name|trait
range|:
name|traits
control|)
block|{
name|traitSet
operator|=
name|traitSet
operator|.
name|replace
argument_list|(
name|trait
argument_list|)
expr_stmt|;
block|}
return|return
name|traitSet
return|;
block|}
specifier|public
name|RelTraitSet
name|traitSetOf
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
return|return
name|emptyTraitSet
operator|.
name|replace
argument_list|(
name|trait
argument_list|)
return|;
block|}
block|}
end_class

end_unit

