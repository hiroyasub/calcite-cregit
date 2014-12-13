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
operator|.
name|core
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
name|Ord
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
name|linq4j
operator|.
name|function
operator|.
name|Function1
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
name|linq4j
operator|.
name|function
operator|.
name|Functions
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
name|RelCollationTraitDef
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
name|RelInput
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
name|RelWriter
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
name|SingleRel
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
name|rex
operator|.
name|RexChecker
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
name|RexFieldAccess
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
name|RexInputRef
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
name|RexLocalRef
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
name|RexUtil
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
name|RexVisitorImpl
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
name|Permutation
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
name|mapping
operator|.
name|MappingType
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
comment|/**  * Relational expression that computes a set of  * 'select expressions' from its input relational expression.  *  *<p>The result is usually 'boxed' as a record with one named field for each  * column; if there is precisely one expression, the result may be 'unboxed',  * and consist of the raw value type.  *  * @see org.apache.calcite.rel.logical.LogicalProject  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Project
extends|extends
name|SingleRel
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|exps
decl_stmt|;
comment|/**    * Values defined in {@link Flags}.    */
specifier|protected
name|int
name|flags
decl_stmt|;
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|RelCollation
argument_list|>
name|collationList
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a Project.    *    * @param cluster Cluster that this relational expression belongs to    * @param traits  traits of this rel    * @param input   input relational expression    * @param exps    List of expressions for the input columns    * @param rowType output row type    * @param flags      Flags; values as in {@link Project.Flags},    *                   usually {@link Project.Flags#BOXED}    */
specifier|protected
name|Project
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
argument_list|,
name|input
argument_list|)
expr_stmt|;
assert|assert
name|rowType
operator|!=
literal|null
assert|;
name|this
operator|.
name|exps
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|exps
argument_list|)
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
specifier|final
name|RelCollation
name|collation
init|=
name|traits
operator|.
name|getTrait
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
name|this
operator|.
name|collationList
operator|=
name|collation
operator|==
literal|null
condition|?
name|ImmutableList
operator|.
expr|<
name|RelCollation
operator|>
name|of
argument_list|()
else|:
name|ImmutableList
operator|.
name|of
argument_list|(
name|collation
argument_list|)
expr_stmt|;
assert|assert
name|isValid
argument_list|(
literal|true
argument_list|)
assert|;
block|}
comment|/**    * Creates a Project by parsing serialized output.    */
specifier|protected
name|Project
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|input
operator|.
name|getExpressionList
argument_list|(
literal|"exprs"
argument_list|)
argument_list|,
name|input
operator|.
name|getRowType
argument_list|(
literal|"exprs"
argument_list|,
literal|"fields"
argument_list|)
argument_list|,
name|Flags
operator|.
name|BOXED
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
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
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|exps
argument_list|,
name|rowType
argument_list|)
return|;
block|}
comment|/**    * Copies a project.    *    * @param traitSet Traits    * @param input Input    * @param exps Project expressions    * @param rowType Output row type    * @return New {@code Project} if any parameter differs from the value of this    *   {@code Project}, or just {@code this} if all the parameters are    *   the same    *    * @see #copy(RelTraitSet, List)    */
specifier|public
specifier|abstract
name|Project
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
function_decl|;
specifier|public
name|List
argument_list|<
name|RelCollation
argument_list|>
name|getCollationList
parameter_list|()
block|{
return|return
name|collationList
return|;
block|}
specifier|public
name|boolean
name|isBoxed
parameter_list|()
block|{
return|return
operator|(
name|flags
operator|&
name|Flags
operator|.
name|BOXED
operator|)
operator|==
name|Flags
operator|.
name|BOXED
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getChildExps
parameter_list|()
block|{
return|return
name|exps
return|;
block|}
comment|/**    * Returns the project expressions.    *    * @return Project expressions    */
specifier|public
name|List
argument_list|<
name|RexNode
argument_list|>
name|getProjects
parameter_list|()
block|{
return|return
name|exps
return|;
block|}
comment|/**    * Returns a list of (expression, name) pairs. Convenient for various    * transformations.    *    * @return List of (expression, name) pairs    */
specifier|public
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|RexNode
argument_list|,
name|String
argument_list|>
argument_list|>
name|getNamedProjects
parameter_list|()
block|{
return|return
name|Pair
operator|.
name|zip
argument_list|(
name|getProjects
argument_list|()
argument_list|,
name|getRowType
argument_list|()
operator|.
name|getFieldNames
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|getFlags
parameter_list|()
block|{
return|return
name|flags
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
if|if
condition|(
operator|!
name|super
operator|.
name|isValid
argument_list|(
name|fail
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|RexUtil
operator|.
name|compatibleTypes
argument_list|(
name|exps
argument_list|,
name|getRowType
argument_list|()
argument_list|,
literal|true
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
name|RexChecker
name|checker
init|=
operator|new
name|RexChecker
argument_list|(
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
argument_list|,
name|fail
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|exp
range|:
name|exps
control|)
block|{
name|exp
operator|.
name|accept
argument_list|(
name|checker
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|checker
operator|.
name|getFailureCount
argument_list|()
operator|>
literal|0
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|isBoxed
argument_list|()
condition|)
block|{
if|if
condition|(
name|exps
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|collationList
operator|==
literal|null
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|collationList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|collationList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|!=
name|traitSet
operator|.
name|getTrait
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|Util
operator|.
name|isDistinct
argument_list|(
name|rowType
operator|.
name|getFieldNames
argument_list|()
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
operator|:
name|rowType
assert|;
return|return
literal|false
return|;
block|}
comment|//CHECKSTYLE: IGNORE 1
if|if
condition|(
literal|false
operator|&&
operator|!
name|Util
operator|.
name|isDistinct
argument_list|(
name|Functions
operator|.
name|adapt
argument_list|(
name|exps
argument_list|,
operator|new
name|Function1
argument_list|<
name|RexNode
argument_list|,
name|Object
argument_list|>
argument_list|()
block|{
specifier|public
name|Object
name|apply
parameter_list|(
name|RexNode
name|a0
parameter_list|)
block|{
return|return
name|a0
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block_content|)
block|)
end_class

begin_block
unit|)
block|{
comment|// Projecting the same expression twice is usually a bad idea,
comment|// because it may create expressions downstream which are equivalent
comment|// but which look different. We can't ban duplicate projects,
comment|// because we need to allow
comment|//
comment|//  SELECT a, b FROM c UNION SELECT x, x FROM z
assert|assert
operator|!
name|fail
operator|:
name|exps
assert|;
return|return
literal|false
return|;
block|}
end_block

begin_return
return|return
literal|true
return|;
end_return

begin_function
unit|}    public
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
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|getInput
argument_list|()
argument_list|)
decl_stmt|;
name|double
name|dCpu
init|=
name|dRows
operator|*
name|exps
operator|.
name|size
argument_list|()
decl_stmt|;
name|double
name|dIo
init|=
literal|0
decl_stmt|;
return|return
name|planner
operator|.
name|getCostFactory
argument_list|()
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
end_function

begin_function
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
expr_stmt|;
if|if
condition|(
name|pw
operator|.
name|nest
argument_list|()
condition|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"fields"
argument_list|,
name|rowType
operator|.
name|getFieldNames
argument_list|()
argument_list|)
expr_stmt|;
name|pw
operator|.
name|item
argument_list|(
literal|"exprs"
argument_list|,
name|exps
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Ord
argument_list|<
name|RelDataTypeField
argument_list|>
name|field
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|rowType
operator|.
name|getFieldList
argument_list|()
argument_list|)
control|)
block|{
name|String
name|fieldName
init|=
name|field
operator|.
name|e
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
name|fieldName
operator|=
literal|"field#"
operator|+
name|field
operator|.
name|i
expr_stmt|;
block|}
name|pw
operator|.
name|item
argument_list|(
name|fieldName
argument_list|,
name|exps
operator|.
name|get
argument_list|(
name|field
operator|.
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If we're generating a digest, include the rowtype. If two projects
comment|// differ in return type, we don't want to regard them as equivalent,
comment|// otherwise we will try to put rels of different types into the same
comment|// planner equivalence set.
comment|//CHECKSTYLE: IGNORE 2
if|if
condition|(
operator|(
name|pw
operator|.
name|getDetailLevel
argument_list|()
operator|==
name|SqlExplainLevel
operator|.
name|DIGEST_ATTRIBUTES
operator|)
operator|&&
literal|false
condition|)
block|{
name|pw
operator|.
name|item
argument_list|(
literal|"type"
argument_list|,
name|rowType
argument_list|)
expr_stmt|;
block|}
return|return
name|pw
return|;
block|}
end_function

begin_comment
comment|/**    * Returns a mapping, or null if this projection is not a mapping.    *    * @return Mapping, or null if this projection is not a mapping    */
end_comment

begin_function
specifier|public
name|Mappings
operator|.
name|TargetMapping
name|getMapping
parameter_list|()
block|{
return|return
name|getMapping
argument_list|(
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|exps
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**    * Returns a mapping of a set of project expressions.    *    *<p>The mapping is an inverse surjection.    * Every target has a source field, but    * a source field may appear as zero, one, or more target fields.    * Thus you can safely call    * {@link Mappings.TargetMapping#getTarget(int)}.    *    * @param inputFieldCount Number of input fields    * @param projects Project expressions    * @return Mapping of a set of project expressions    */
end_comment

begin_function
specifier|public
specifier|static
name|Mappings
operator|.
name|TargetMapping
name|getMapping
parameter_list|(
name|int
name|inputFieldCount
parameter_list|,
name|List
argument_list|<
name|RexNode
argument_list|>
name|projects
parameter_list|)
block|{
name|Mappings
operator|.
name|TargetMapping
name|mapping
init|=
name|Mappings
operator|.
name|create
argument_list|(
name|MappingType
operator|.
name|INVERSE_SURJECTION
argument_list|,
name|inputFieldCount
argument_list|,
name|projects
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RexNode
argument_list|>
name|exp
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|projects
argument_list|)
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|exp
operator|.
name|e
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|mapping
operator|.
name|set
argument_list|(
operator|(
operator|(
name|RexInputRef
operator|)
name|exp
operator|.
name|e
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|,
name|exp
operator|.
name|i
argument_list|)
expr_stmt|;
block|}
return|return
name|mapping
return|;
block|}
end_function

begin_comment
comment|/**    * Returns a permutation, if this projection is merely a permutation of its    * input fields, otherwise null.    *    * @return Permutation, if this projection is merely a permutation of its    *   input fields, otherwise null    */
end_comment

begin_function
specifier|public
name|Permutation
name|getPermutation
parameter_list|()
block|{
specifier|final
name|int
name|fieldCount
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|fieldCount
operator|!=
name|getInput
argument_list|()
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Permutation
name|permutation
init|=
operator|new
name|Permutation
argument_list|(
name|fieldCount
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
name|fieldCount
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|RexNode
name|exp
init|=
name|exps
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|exp
operator|instanceof
name|RexInputRef
condition|)
block|{
name|permutation
operator|.
name|set
argument_list|(
name|i
argument_list|,
operator|(
operator|(
name|RexInputRef
operator|)
name|exp
operator|)
operator|.
name|getIndex
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
return|return
name|permutation
return|;
block|}
end_function

begin_comment
comment|/**    * Checks whether this is a functional mapping.    * Every output is a source field, but    * a source field may appear as zero, one, or more output fields.    */
end_comment

begin_function
specifier|public
name|boolean
name|isMapping
parameter_list|()
block|{
for|for
control|(
name|RexNode
name|exp
range|:
name|exps
control|)
block|{
if|if
condition|(
operator|!
operator|(
name|exp
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
end_function

begin_comment
comment|//~ Inner Classes ----------------------------------------------------------
end_comment

begin_comment
comment|/** A collection of integer constants that describe the kind of project. */
end_comment

begin_class
specifier|public
specifier|static
class|class
name|Flags
block|{
specifier|public
specifier|static
specifier|final
name|int
name|ANON_FIELDS
init|=
literal|2
decl_stmt|;
comment|/**      * Whether the resulting row is to be a synthetic class whose fields are      * the aliases of the fields.<code>boxed</code> must be true unless      * there is only one field:<code>select {dept.deptno} from dept</code>      * is boxed,<code>select dept.deptno from dept</code> is not.      */
specifier|public
specifier|static
specifier|final
name|int
name|BOXED
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|NONE
init|=
literal|0
decl_stmt|;
block|}
end_class

begin_comment
comment|//~ Inner Classes ----------------------------------------------------------
end_comment

begin_comment
comment|/**    * Visitor which walks over a program and checks validity.    */
end_comment

begin_class
specifier|private
specifier|static
class|class
name|Checker
extends|extends
name|RexVisitorImpl
argument_list|<
name|Boolean
argument_list|>
block|{
specifier|private
specifier|final
name|boolean
name|fail
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|inputRowType
decl_stmt|;
name|int
name|failCount
init|=
literal|0
decl_stmt|;
comment|/**      * Creates a Checker.      *      * @param inputRowType Input row type to expressions      * @param fail         Whether to throw if checker finds an error      */
specifier|private
name|Checker
parameter_list|(
name|RelDataType
name|inputRowType
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|this
operator|.
name|fail
operator|=
name|fail
expr_stmt|;
name|this
operator|.
name|inputRowType
operator|=
name|inputRowType
expr_stmt|;
block|}
specifier|public
name|Boolean
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|inputRef
operator|.
name|getIndex
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|inputRowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|<
literal|0
operator|)
operator|||
operator|(
name|index
operator|>=
name|fields
operator|.
name|size
argument_list|()
operator|)
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
operator|++
name|failCount
expr_stmt|;
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|!
name|RelOptUtil
operator|.
name|eq
argument_list|(
literal|"inputRef"
argument_list|,
name|inputRef
operator|.
name|getType
argument_list|()
argument_list|,
literal|"underlying field"
argument_list|,
name|fields
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|,
name|fail
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
operator|++
name|failCount
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|Boolean
name|visitLocalRef
parameter_list|(
name|RexLocalRef
name|localRef
parameter_list|)
block|{
assert|assert
operator|!
name|fail
operator|:
literal|"localRef invalid in project"
assert|;
operator|++
name|failCount
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|public
name|Boolean
name|visitFieldAccess
parameter_list|(
name|RexFieldAccess
name|fieldAccess
parameter_list|)
block|{
name|super
operator|.
name|visitFieldAccess
argument_list|(
name|fieldAccess
argument_list|)
expr_stmt|;
specifier|final
name|RelDataType
name|refType
init|=
name|fieldAccess
operator|.
name|getReferenceExpr
argument_list|()
operator|.
name|getType
argument_list|()
decl_stmt|;
assert|assert
name|refType
operator|.
name|isStruct
argument_list|()
assert|;
specifier|final
name|RelDataTypeField
name|field
init|=
name|fieldAccess
operator|.
name|getField
argument_list|()
decl_stmt|;
specifier|final
name|int
name|index
init|=
name|field
operator|.
name|getIndex
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|index
operator|<
literal|0
operator|)
operator|||
operator|(
name|index
operator|>
name|refType
operator|.
name|getFieldList
argument_list|()
operator|.
name|size
argument_list|()
operator|)
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
operator|++
name|failCount
expr_stmt|;
return|return
literal|false
return|;
block|}
specifier|final
name|RelDataTypeField
name|typeField
init|=
name|refType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|RelOptUtil
operator|.
name|eq
argument_list|(
literal|"type1"
argument_list|,
name|typeField
operator|.
name|getType
argument_list|()
argument_list|,
literal|"type2"
argument_list|,
name|fieldAccess
operator|.
name|getType
argument_list|()
argument_list|,
name|fail
argument_list|)
condition|)
block|{
assert|assert
operator|!
name|fail
assert|;
operator|++
name|failCount
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
unit|}
comment|// End Project.java
end_comment

end_unit

