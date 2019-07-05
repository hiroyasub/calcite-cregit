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
name|rel
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
name|plan
operator|.
name|Convention
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
name|plan
operator|.
name|ConventionTraitDef
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
name|plan
operator|.
name|RelOptCluster
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
name|plan
operator|.
name|RelOptCost
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
name|plan
operator|.
name|RelOptPlanner
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
name|plan
operator|.
name|RelOptQuery
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
name|plan
operator|.
name|RelOptTable
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
name|plan
operator|.
name|RelOptUtil
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
name|plan
operator|.
name|RelTrait
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
name|plan
operator|.
name|RelTraitSet
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
name|metadata
operator|.
name|Metadata
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
name|rex
operator|.
name|RexNode
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
name|RexShuttle
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
name|sql
operator|.
name|SqlExplainLevel
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Litmus
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
name|Pair
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
name|Util
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
name|trace
operator|.
name|CalciteTrace
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
name|ImmutableSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/**  * Base class for every relational expression ({@link RelNode}).  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractRelNode
implements|implements
name|RelNode
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** Generator for {@link #id} values. */
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|NEXT_ID
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|CalciteTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * Description, consists of id plus digest.    */
specifier|private
name|String
name|desc
decl_stmt|;
comment|/**    * Cached type of this relational expression.    */
specifier|protected
name|RelDataType
name|rowType
decl_stmt|;
comment|/**    * A short description of this relational expression's type, inputs, and    * other properties. The string uniquely identifies the node; another node    * is equivalent if and only if it has the same value. Computed by    * {@link #computeDigest}, assigned by {@link #onRegister}, returned by    * {@link #getDigest()}.    *    * @see #desc    */
specifier|protected
name|String
name|digest
decl_stmt|;
specifier|private
specifier|final
name|RelOptCluster
name|cluster
decl_stmt|;
comment|/**    * unique id of this object -- for debugging    */
specifier|protected
specifier|final
name|int
name|id
decl_stmt|;
comment|/**    * The RelTraitSet that describes the traits of this RelNode.    */
specifier|protected
name|RelTraitSet
name|traitSet
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an<code>AbstractRelNode</code>.    */
specifier|public
name|AbstractRelNode
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
assert|assert
name|cluster
operator|!=
literal|null
assert|;
name|this
operator|.
name|cluster
operator|=
name|cluster
expr_stmt|;
name|this
operator|.
name|traitSet
operator|=
name|traitSet
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|NEXT_ID
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|getRelTypeName
argument_list|()
operator|+
literal|"#"
operator|+
name|id
expr_stmt|;
name|this
operator|.
name|desc
operator|=
name|digest
expr_stmt|;
name|LOGGER
operator|.
name|trace
argument_list|(
literal|"new {}"
argument_list|,
name|digest
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelNode
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
parameter_list|)
block|{
comment|// Note that empty set equals empty set, so relational expressions
comment|// with zero inputs do not generally need to implement their own copy
comment|// method.
if|if
condition|(
name|getInputs
argument_list|()
operator|.
name|equals
argument_list|(
name|inputs
argument_list|)
operator|&&
name|traitSet
operator|==
name|getTraitSet
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Relational expression should override copy. "
operator|+
literal|"Class=["
operator|+
name|getClass
argument_list|()
operator|+
literal|"]; traits=["
operator|+
name|getTraitSet
argument_list|()
operator|+
literal|"]; desired traits=["
operator|+
name|traitSet
operator|+
literal|"]"
argument_list|)
throw|;
block|}
specifier|protected
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|sole
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|collection
parameter_list|)
block|{
assert|assert
name|collection
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
return|return
name|collection
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getChildExps
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|RelOptCluster
name|getCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
specifier|public
specifier|final
name|Convention
name|getConvention
parameter_list|()
block|{
return|return
name|traitSet
operator|.
name|getTrait
argument_list|(
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|)
return|;
block|}
specifier|public
name|RelTraitSet
name|getTraitSet
parameter_list|()
block|{
return|return
name|traitSet
return|;
block|}
specifier|public
name|String
name|getCorrelVariable
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
decl_stmt|;
return|return
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|mq
operator|.
name|areRowsUnique
argument_list|(
name|this
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|boolean
name|isKey
parameter_list|(
name|ImmutableBitSet
name|columns
parameter_list|)
block|{
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
decl_stmt|;
return|return
name|Boolean
operator|.
name|TRUE
operator|.
name|equals
argument_list|(
name|mq
operator|.
name|areColumnsUnique
argument_list|(
name|this
argument_list|,
name|columns
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|RelNode
name|getInput
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|getInputs
argument_list|()
decl_stmt|;
return|return
name|inputs
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
specifier|final
name|RelOptQuery
name|getQuery
parameter_list|()
block|{
return|return
name|getCluster
argument_list|()
operator|.
name|getQuery
argument_list|()
return|;
block|}
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|planner
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|String
name|getRelTypeName
parameter_list|()
block|{
name|String
name|cn
init|=
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|cn
operator|.
name|length
argument_list|()
decl_stmt|;
while|while
condition|(
operator|--
name|i
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|cn
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'$'
operator|||
name|cn
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
operator|==
literal|'.'
condition|)
block|{
return|return
name|cn
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
block|}
return|return
name|cn
return|;
block|}
specifier|public
name|boolean
name|isValid
parameter_list|(
name|Litmus
name|litmus
parameter_list|,
name|Context
name|context
parameter_list|)
block|{
return|return
name|litmus
operator|.
name|succeed
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|boolean
name|isValid
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
return|return
name|isValid
argument_list|(
name|Litmus
operator|.
name|THROW
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/** @deprecated Use {@link RelMetadataQuery#collations(RelNode)} */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|ImmutableList
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
specifier|final
name|RelDataType
name|getRowType
parameter_list|()
block|{
if|if
condition|(
name|rowType
operator|==
literal|null
condition|)
block|{
name|rowType
operator|=
name|deriveRowType
argument_list|()
expr_stmt|;
assert|assert
name|rowType
operator|!=
literal|null
operator|:
name|this
assert|;
block|}
return|return
name|rowType
return|;
block|}
specifier|protected
name|RelDataType
name|deriveRowType
parameter_list|()
block|{
comment|// This method is only called if rowType is null, so you don't NEED to
comment|// implement it if rowType is always set.
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|RelDataType
name|getExpectedInputRowType
parameter_list|(
name|int
name|ordinalInParent
parameter_list|)
block|{
return|return
name|getRowType
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
specifier|final
name|double
name|getRows
parameter_list|()
block|{
return|return
name|estimateRowCount
argument_list|(
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|double
name|estimateRowCount
parameter_list|(
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
return|return
literal|1.0
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|getVariablesStopped
parameter_list|()
block|{
return|return
name|CorrelationId
operator|.
name|names
argument_list|(
name|getVariablesSet
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|getVariablesSet
parameter_list|()
block|{
return|return
name|ImmutableSet
operator|.
name|of
argument_list|()
return|;
block|}
specifier|public
name|void
name|collectVariablesUsed
parameter_list|(
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variableSet
parameter_list|)
block|{
comment|// for default case, nothing to do
block|}
specifier|public
name|void
name|collectVariablesSet
parameter_list|(
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variableSet
parameter_list|)
block|{
block|}
specifier|public
name|void
name|childrenAccept
parameter_list|(
name|RelVisitor
name|visitor
parameter_list|)
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
name|getInputs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|inputs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|visitor
operator|.
name|visit
argument_list|(
name|inputs
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|i
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RelShuttle
name|shuttle
parameter_list|)
block|{
comment|// Call fall-back method. Specific logical types (such as LogicalProject
comment|// and LogicalJoin) have their own RelShuttle.visit methods.
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RexShuttle
name|shuttle
parameter_list|)
block|{
return|return
name|this
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
specifier|final
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
return|return
name|computeSelfCost
argument_list|(
name|planner
argument_list|,
name|RelMetadataQuery
operator|.
name|instance
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
comment|// by default, assume cost is proportional to number of rows
name|double
name|rowCount
init|=
name|mq
operator|.
name|getRowCount
argument_list|(
name|this
argument_list|)
decl_stmt|;
name|double
name|bytesPerRow
init|=
literal|1
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
operator|.
name|makeCost
argument_list|(
name|rowCount
argument_list|,
name|rowCount
argument_list|,
literal|0
argument_list|)
return|;
block|}
specifier|public
specifier|final
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
name|M
name|metadata
parameter_list|(
name|Class
argument_list|<
name|M
argument_list|>
name|metadataClass
parameter_list|,
name|RelMetadataQuery
name|mq
parameter_list|)
block|{
specifier|final
name|MetadataFactory
name|factory
init|=
name|cluster
operator|.
name|getMetadataFactory
argument_list|()
decl_stmt|;
specifier|final
name|M
name|metadata
init|=
name|factory
operator|.
name|query
argument_list|(
name|this
argument_list|,
name|mq
argument_list|,
name|metadataClass
argument_list|)
decl_stmt|;
assert|assert
name|metadata
operator|!=
literal|null
operator|:
literal|"no provider found (rel="
operator|+
name|this
operator|+
literal|", m="
operator|+
name|metadataClass
operator|+
literal|"); a backstop provider is recommended"
assert|;
comment|// Usually the metadata belongs to the rel that created it. RelSubset and
comment|// HepRelVertex are notable exceptions, so disable the assert. It's not
comment|// worth the performance hit to override this method for them.
comment|//   assert metadata.rel() == this : "someone else's metadata";
return|return
name|metadata
return|;
block|}
specifier|public
name|void
name|explain
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|done
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**    * Describes the inputs and attributes of this relational expression.    * Each node should call {@code super.explainTerms}, then call the    * {@link org.apache.calcite.rel.externalize.RelWriterImpl#input(String, RelNode)}    * and    * {@link org.apache.calcite.rel.externalize.RelWriterImpl#item(String, Object)}    * methods for each input and attribute.    *    * @param pw Plan writer    * @return Plan writer for fluent-explain pattern    */
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|pw
return|;
block|}
specifier|public
name|RelNode
name|onRegister
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
name|List
argument_list|<
name|RelNode
argument_list|>
name|oldInputs
init|=
name|getInputs
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|oldInputs
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|RelNode
name|input
range|:
name|oldInputs
control|)
block|{
name|RelNode
name|e
init|=
name|planner
operator|.
name|ensureRegistered
argument_list|(
name|input
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|e
operator|!=
name|input
condition|)
block|{
comment|// TODO: change 'equal' to 'eq', which is stronger.
assert|assert
name|RelOptUtil
operator|.
name|equal
argument_list|(
literal|"rowtype of rel before registration"
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
argument_list|,
literal|"rowtype of rel after registration"
argument_list|,
name|e
operator|.
name|getRowType
argument_list|()
argument_list|,
name|Litmus
operator|.
name|THROW
argument_list|)
assert|;
block|}
name|inputs
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|RelNode
name|r
init|=
name|this
decl_stmt|;
if|if
condition|(
operator|!
name|Util
operator|.
name|equalShallow
argument_list|(
name|oldInputs
argument_list|,
name|inputs
argument_list|)
condition|)
block|{
name|r
operator|=
name|copy
argument_list|(
name|getTraitSet
argument_list|()
argument_list|,
name|inputs
argument_list|)
expr_stmt|;
block|}
name|r
operator|.
name|recomputeDigest
argument_list|()
expr_stmt|;
assert|assert
name|r
operator|.
name|isValid
argument_list|(
name|Litmus
operator|.
name|THROW
argument_list|,
literal|null
argument_list|)
assert|;
return|return
name|r
return|;
block|}
specifier|public
name|String
name|recomputeDigest
parameter_list|()
block|{
name|String
name|tempDigest
init|=
name|computeDigest
argument_list|()
decl_stmt|;
assert|assert
name|tempDigest
operator|!=
literal|null
operator|:
literal|"computeDigest() should be non-null"
assert|;
name|this
operator|.
name|desc
operator|=
literal|"rel#"
operator|+
name|id
operator|+
literal|":"
operator|+
name|tempDigest
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|tempDigest
expr_stmt|;
return|return
name|this
operator|.
name|digest
return|;
block|}
specifier|public
name|void
name|replaceInput
parameter_list|(
name|int
name|ordinalInParent
parameter_list|,
name|RelNode
name|p
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"replaceInput called on "
operator|+
name|this
argument_list|)
throw|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|desc
return|;
block|}
specifier|public
specifier|final
name|String
name|getDescription
parameter_list|()
block|{
return|return
name|desc
return|;
block|}
specifier|public
specifier|final
name|String
name|getDigest
parameter_list|()
block|{
return|return
name|digest
return|;
block|}
specifier|public
name|RelOptTable
name|getTable
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**    * Computes the digest. Does not modify this object.    *    * @return Digest    */
specifier|protected
name|String
name|computeDigest
parameter_list|()
block|{
name|RelDigestWriter
name|rdw
init|=
operator|new
name|RelDigestWriter
argument_list|()
decl_stmt|;
name|explain
argument_list|(
name|rdw
argument_list|)
expr_stmt|;
return|return
name|rdw
operator|.
name|digest
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>This method (and {@link #hashCode} is intentionally final. We do not want    * sub-classes of {@link RelNode} to redefine identity. Various algorithms    * (e.g. visitors, planner) can define the identity as meets their needs.    */
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
comment|/**    * {@inheritDoc}    *    *<p>This method (and {@link #equals} is intentionally final. We do not want    * sub-classes of {@link RelNode} to redefine identity. Various algorithms    * (e.g. visitors, planner) can define the identity as meets their needs.    */
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * A writer object used exclusively for computing the digest of a RelNode.    *    *<p>The writer is meant to be used only for computing a single digest and then thrown away.    * After calling {@link #done(RelNode)} the writer should be used only to obtain the computed    * {@link #digest}. Any other action is prohibited.</p>    *    */
specifier|private
specifier|static
specifier|final
class|class
name|RelDigestWriter
implements|implements
name|RelWriter
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|digest
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|explain
parameter_list|(
specifier|final
name|RelNode
name|rel
parameter_list|,
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|valueList
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Should not be called for computing digest"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|SqlExplainLevel
name|getDetailLevel
parameter_list|()
block|{
return|return
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|item
parameter_list|(
name|String
name|term
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|values
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|term
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelWriter
name|done
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|node
operator|.
name|getRelTypeName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RelTrait
name|trait
range|:
name|node
operator|.
name|getTraitSet
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|trait
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|j
operator|++
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|value
operator|.
name|left
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'='
argument_list|)
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|right
operator|instanceof
name|RelNode
condition|)
block|{
name|RelNode
name|input
init|=
operator|(
name|RelNode
operator|)
name|value
operator|.
name|right
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|input
operator|.
name|getRelTypeName
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'#'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|input
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|value
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|digest
operator|=
name|sb
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End AbstractRelNode.java
end_comment

end_unit

