begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
name|io
operator|.
name|*
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|logging
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
name|metadata
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
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
name|trace
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
name|util
operator|.
name|*
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
comment|// TODO jvs 10-Oct-2003:  Make this thread safe.  Either synchronize, or
comment|// keep this per-VolcanoPlanner.
comment|/**      * generator for {@link #id} values      */
specifier|static
name|int
name|nextId
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|tracer
init|=
name|EigenbaseTrace
operator|.
name|getPlannerTracer
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * Description, consists of id plus digest.      */
specifier|private
name|String
name|desc
decl_stmt|;
comment|/**      * Cached type of this relational expression.      */
specifier|protected
name|RelDataType
name|rowType
decl_stmt|;
comment|/**      * A short description of this relational expression's type, inputs, and      * other properties. The string uniquely identifies the node; another node      * is equivalent if and only if it has the same value. Computed by {@link      * #computeDigest}, assigned by {@link #onRegister}, returned by {@link      * #getDigest()}.      *      * @see #desc      */
specifier|protected
name|String
name|digest
decl_stmt|;
specifier|private
specifier|final
name|RelOptCluster
name|cluster
decl_stmt|;
comment|/**      * unique id of this object -- for debugging      */
specifier|protected
name|int
name|id
decl_stmt|;
comment|/**      * The variable by which to refer to rows from this relational expression,      * as correlating expressions; null if this expression is not correlated on.      */
specifier|private
name|String
name|correlVariable
decl_stmt|;
comment|/**      * The RelTraitSet that describes the traits of this RelNode.      */
specifier|protected
name|RelTraitSet
name|traitSet
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates an<code>AbstractRelNode</code>.      */
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
operator|(
name|cluster
operator|!=
literal|null
operator|)
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
name|nextId
operator|++
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
if|if
condition|(
name|tracer
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINEST
argument_list|)
condition|)
block|{
name|tracer
operator|.
name|finest
argument_list|(
literal|"new "
operator|+
name|digest
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelNode
name|clone
parameter_list|()
block|{
return|return
name|copy
argument_list|(
name|getTraitSet
argument_list|()
argument_list|,
name|getInputs
argument_list|()
argument_list|)
return|;
block|}
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
literal|"Relational expression should override copy. Class=["
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
specifier|public
name|boolean
name|isAccessTo
parameter_list|(
name|RelOptTable
name|table
parameter_list|)
block|{
return|return
name|getTable
argument_list|()
operator|==
name|table
return|;
block|}
specifier|public
name|RexNode
index|[]
name|getChildExps
parameter_list|()
block|{
return|return
name|RexUtil
operator|.
name|emptyExpressionArray
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
name|instance
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
name|void
name|setCorrelVariable
parameter_list|(
name|String
name|correlVariable
parameter_list|)
block|{
name|this
operator|.
name|correlVariable
operator|=
name|correlVariable
expr_stmt|;
block|}
specifier|public
name|String
name|getCorrelVariable
parameter_list|()
block|{
return|return
name|correlVariable
return|;
block|}
specifier|public
name|boolean
name|isDistinct
parameter_list|()
block|{
return|return
literal|false
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
specifier|public
name|String
name|getOrCreateCorrelVariable
parameter_list|()
block|{
if|if
condition|(
name|correlVariable
operator|==
literal|null
condition|)
block|{
name|correlVariable
operator|=
name|getQuery
argument_list|()
operator|.
name|createCorrel
argument_list|()
expr_stmt|;
name|getQuery
argument_list|()
operator|.
name|mapCorrel
argument_list|(
name|correlVariable
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
return|return
name|correlVariable
return|;
block|}
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
name|className
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
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|"$"
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|className
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
name|i
operator|=
name|className
operator|.
name|lastIndexOf
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|className
operator|.
name|substring
argument_list|(
name|i
operator|+
literal|1
argument_list|)
return|;
block|}
return|return
name|className
return|;
block|}
specifier|public
name|boolean
name|isValid
parameter_list|(
name|boolean
name|fail
parameter_list|)
block|{
return|return
literal|true
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
name|Collections
operator|.
name|emptyList
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
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
literal|1.0
return|;
block|}
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getVariablesStopped
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
specifier|public
name|void
name|collectVariablesUsed
parameter_list|(
name|Set
argument_list|<
name|String
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
name|String
argument_list|>
name|variableSet
parameter_list|)
block|{
if|if
condition|(
name|correlVariable
operator|!=
literal|null
condition|)
block|{
name|variableSet
operator|.
name|add
argument_list|(
name|correlVariable
argument_list|)
expr_stmt|;
block|}
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
name|RelOptCost
name|computeSelfCost
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
comment|// by default, assume cost is proportional to number of rows
name|double
name|rowCount
init|=
name|RelMetadataQuery
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
name|void
name|explain
parameter_list|(
name|RelOptPlanWriter
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
comment|/** Describes the inputs and attributes of this relational expression.      * Each node should call {@code super.explainTerms}, then call the      * {@link RelOptPlanWriter#input(String, RelNode)}      * and {@link RelOptPlanWriter#item(String, Object)} methods for each input      * and attribute.      *      * @param pw Plan writer      */
specifier|public
name|RelOptPlanWriter
name|explainTerms
parameter_list|(
name|RelOptPlanWriter
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
argument_list|<
name|RelNode
argument_list|>
argument_list|(
name|oldInputs
operator|.
name|size
argument_list|()
argument_list|)
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
name|oldInputs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RelNode
name|input
init|=
name|oldInputs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
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
literal|true
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
literal|true
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
literal|"post: return != null"
assert|;
name|String
name|prefix
init|=
literal|"rel#"
operator|+
name|id
operator|+
literal|":"
decl_stmt|;
comment|// Substring uses the same underlying array of chars, so saves a bit
comment|// of memory.
name|this
operator|.
name|desc
operator|=
name|prefix
operator|+
name|tempDigest
expr_stmt|;
name|this
operator|.
name|digest
operator|=
name|this
operator|.
name|desc
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|digest
return|;
block|}
specifier|public
name|void
name|registerCorrelVariable
parameter_list|(
name|String
name|correlVariable
parameter_list|)
block|{
assert|assert
operator|(
name|this
operator|.
name|correlVariable
operator|==
literal|null
operator|)
assert|;
name|this
operator|.
name|correlVariable
operator|=
name|correlVariable
expr_stmt|;
name|getQuery
argument_list|()
operator|.
name|mapCorrel
argument_list|(
name|correlVariable
argument_list|,
name|this
argument_list|)
expr_stmt|;
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
name|Util
operator|.
name|newInternal
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
comment|/**      * Computes the digest. Does not modify this object.      */
specifier|protected
name|String
name|computeDigest
parameter_list|()
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|RelOptPlanWriter
name|pw
init|=
operator|new
name|RelOptPlanWriter
argument_list|(
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
argument_list|,
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
argument_list|)
block|{
specifier|protected
name|void
name|explain_
parameter_list|(
name|RelNode
name|rel
parameter_list|,
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
parameter_list|)
block|{
name|pw
operator|.
name|write
argument_list|(
name|getRelTypeName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RelTrait
name|trait
range|:
name|traitSet
control|)
block|{
name|pw
operator|.
name|write
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|pw
operator|.
name|write
argument_list|(
name|trait
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|write
argument_list|(
literal|"("
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
name|pw
operator|.
name|write
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|write
argument_list|(
name|value
operator|.
name|left
operator|+
literal|"="
operator|+
name|value
operator|.
name|right
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|write
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
name|explain
argument_list|(
name|pw
argument_list|)
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractRelNode.java
end_comment

end_unit

