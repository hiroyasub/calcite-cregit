begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|prepare
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|DataContext
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|StarTable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqPrepare
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
operator|.
name|OptiqSchema
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|rules
operator|.
name|java
operator|.
name|JavaRules
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|Bindable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|Typed
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|tools
operator|.
name|Program
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|tools
operator|.
name|Programs
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
name|rel
operator|.
name|rules
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
name|RelDataType
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
name|RexExecutorImpl
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
name|sql
operator|.
name|validate
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
name|sql2rel
operator|.
name|SqlToRelConverter
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
name|EigenbaseTimingTracer
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
name|EigenbaseTrace
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
name|lang
operator|.
name|reflect
operator|.
name|Type
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
name|Level
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
name|Logger
import|;
end_import

begin_comment
comment|/**  * Abstract base for classes that implement  * the process of preparing and executing SQL expressions.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Prepare
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|EigenbaseTrace
operator|.
name|getStatementTracer
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|RelOptRule
argument_list|>
name|CALC_RULES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|JavaRules
operator|.
name|ENUMERABLE_CALC_RULE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_FILTER_TO_CALC_RULE
argument_list|,
name|JavaRules
operator|.
name|ENUMERABLE_PROJECT_TO_CALC_RULE
argument_list|,
name|MergeCalcRule
operator|.
name|INSTANCE
argument_list|,
name|MergeFilterOntoCalcRule
operator|.
name|INSTANCE
argument_list|,
name|MergeProjectOntoCalcRule
operator|.
name|INSTANCE
argument_list|,
name|FilterToCalcRule
operator|.
name|INSTANCE
argument_list|,
name|ProjectToCalcRule
operator|.
name|INSTANCE
argument_list|,
name|MergeCalcRule
operator|.
name|INSTANCE
argument_list|,
comment|// REVIEW jvs 9-Apr-2006: Do we still need these two?  Doesn't the
comment|// combination of MergeCalcRule, FilterToCalcRule, and
comment|// ProjectToCalcRule have the same effect?
name|MergeFilterOntoCalcRule
operator|.
name|INSTANCE
argument_list|,
name|MergeProjectOntoCalcRule
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|OptiqPrepare
operator|.
name|Context
name|context
decl_stmt|;
specifier|protected
specifier|final
name|CatalogReader
name|catalogReader
decl_stmt|;
specifier|protected
name|String
name|queryString
init|=
literal|null
decl_stmt|;
comment|/**    * Convention via which results should be returned by execution.    */
specifier|protected
specifier|final
name|Convention
name|resultConvention
decl_stmt|;
specifier|protected
name|EigenbaseTimingTracer
name|timingTracer
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldOrigins
decl_stmt|;
specifier|protected
name|RelDataType
name|parameterRowType
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|trim
init|=
literal|false
decl_stmt|;
comment|// temporary. for testing.
specifier|public
name|Prepare
parameter_list|(
name|OptiqPrepare
operator|.
name|Context
name|context
parameter_list|,
name|CatalogReader
name|catalogReader
parameter_list|,
name|Convention
name|resultConvention
parameter_list|)
block|{
assert|assert
name|context
operator|!=
literal|null
assert|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|catalogReader
operator|=
name|catalogReader
expr_stmt|;
name|this
operator|.
name|resultConvention
operator|=
name|resultConvention
expr_stmt|;
block|}
specifier|protected
specifier|abstract
name|PreparedResult
name|createPreparedExplanation
parameter_list|(
name|RelDataType
name|resultType
parameter_list|,
name|RelDataType
name|parameterRowType
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|boolean
name|explainAsXml
parameter_list|,
name|SqlExplainLevel
name|detailLevel
parameter_list|)
function_decl|;
comment|/**    * Optimizes a query plan.    *    * @param logicalRowType logical row type of relational expression (before    * struct fields are flattened, or field names are renamed for uniqueness)    * @param rootRel root of a relational expression    *    * @param materializations Tables known to be populated with a given query    * @return an equivalent optimized relational expression    */
specifier|protected
name|RelNode
name|optimize
parameter_list|(
name|RelDataType
name|logicalRowType
parameter_list|,
specifier|final
name|RelNode
name|rootRel
parameter_list|,
specifier|final
name|List
argument_list|<
name|Materialization
argument_list|>
name|materializations
parameter_list|)
block|{
specifier|final
name|RelOptPlanner
name|planner
init|=
name|rootRel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
decl_stmt|;
name|planner
operator|.
name|setRoot
argument_list|(
name|rootRel
argument_list|)
expr_stmt|;
specifier|final
name|RelTraitSet
name|desiredTraits
init|=
name|getDesiredRootTraitSet
argument_list|(
name|rootRel
argument_list|)
decl_stmt|;
specifier|final
name|Program
name|program1
init|=
operator|new
name|Program
argument_list|()
block|{
specifier|public
name|RelNode
name|run
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTraitSet
name|requiredOutputTraits
parameter_list|)
block|{
specifier|final
name|DataContext
name|dataContext
init|=
name|context
operator|.
name|getDataContext
argument_list|()
decl_stmt|;
name|planner
operator|.
name|setExecutor
argument_list|(
operator|new
name|RexExecutorImpl
argument_list|(
name|dataContext
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|Materialization
name|materialization
range|:
name|materializations
control|)
block|{
name|planner
operator|.
name|addMaterialization
argument_list|(
operator|new
name|RelOptMaterialization
argument_list|(
name|materialization
operator|.
name|tableRel
argument_list|,
name|materialization
operator|.
name|queryRel
argument_list|,
name|materialization
operator|.
name|starRelOptTable
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelNode
name|rootRel2
init|=
name|planner
operator|.
name|changeTraits
argument_list|(
name|rel
argument_list|,
name|requiredOutputTraits
argument_list|)
decl_stmt|;
assert|assert
name|rootRel2
operator|!=
literal|null
assert|;
name|planner
operator|.
name|setRoot
argument_list|(
name|rootRel2
argument_list|)
expr_stmt|;
specifier|final
name|RelOptPlanner
name|planner2
init|=
name|planner
operator|.
name|chooseDelegate
argument_list|()
decl_stmt|;
specifier|final
name|RelNode
name|rootRel3
init|=
name|planner2
operator|.
name|findBestExp
argument_list|()
decl_stmt|;
assert|assert
name|rootRel3
operator|!=
literal|null
operator|:
literal|"could not implement exp"
assert|;
return|return
name|rootRel3
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|RelNode
name|rootRel3
init|=
name|program1
operator|.
name|run
argument_list|(
name|planner
argument_list|,
name|rootRel
argument_list|,
name|desiredTraits
argument_list|)
decl_stmt|;
comment|// Second planner pass to do physical "tweaks". This the first time that
comment|// EnumerableCalcRel is introduced.
specifier|final
name|Program
name|program2
init|=
name|Programs
operator|.
name|hep
argument_list|(
name|CALC_RULES
argument_list|,
literal|true
argument_list|,
operator|new
name|DefaultRelMetadataProvider
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|RelNode
name|rootRel4
init|=
name|program2
operator|.
name|run
argument_list|(
literal|null
argument_list|,
name|rootRel3
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOGGER
operator|.
name|isLoggable
argument_list|(
name|Level
operator|.
name|FINE
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|fine
argument_list|(
literal|"Plan after physical tweaks: "
operator|+
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|rootRel4
argument_list|,
name|SqlExplainLevel
operator|.
name|ALL_ATTRIBUTES
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|rootRel4
return|;
block|}
specifier|protected
name|RelTraitSet
name|getDesiredRootTraitSet
parameter_list|(
name|RelNode
name|rootRel
parameter_list|)
block|{
comment|// Make sure non-CallingConvention traits, if any, are preserved
return|return
name|rootRel
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|resultConvention
argument_list|)
return|;
block|}
comment|/**    * Implements a physical query plan.    *    * @param rowType original row type returned by query validator    * @param rootRel root of the relational expression.    * @param sqlKind SqlKind of the original statement.    * @return an executable plan    */
specifier|protected
specifier|abstract
name|PreparedResult
name|implement
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|SqlKind
name|sqlKind
parameter_list|)
function_decl|;
specifier|public
name|PreparedResult
name|prepareSql
parameter_list|(
name|SqlNode
name|sqlQuery
parameter_list|,
name|Class
name|runtimeContextClass
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|boolean
name|needsValidation
parameter_list|,
name|List
argument_list|<
name|Materialization
argument_list|>
name|materializations
parameter_list|)
block|{
return|return
name|prepareSql
argument_list|(
name|sqlQuery
argument_list|,
name|sqlQuery
argument_list|,
name|runtimeContextClass
argument_list|,
name|validator
argument_list|,
name|needsValidation
argument_list|,
name|materializations
argument_list|)
return|;
block|}
specifier|public
name|PreparedResult
name|prepareSql
parameter_list|(
name|SqlNode
name|sqlQuery
parameter_list|,
name|SqlNode
name|sqlNodeOriginal
parameter_list|,
name|Class
name|runtimeContextClass
parameter_list|,
name|SqlValidator
name|validator
parameter_list|,
name|boolean
name|needsValidation
parameter_list|,
name|List
argument_list|<
name|Materialization
argument_list|>
name|materializations
parameter_list|)
block|{
name|queryString
operator|=
name|sqlQuery
operator|.
name|toString
argument_list|()
expr_stmt|;
name|init
argument_list|(
name|runtimeContextClass
argument_list|)
expr_stmt|;
name|SqlToRelConverter
name|sqlToRelConverter
init|=
name|getSqlToRelConverter
argument_list|(
name|validator
argument_list|,
name|catalogReader
argument_list|)
decl_stmt|;
name|SqlExplain
name|sqlExplain
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|sqlQuery
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|EXPLAIN
condition|)
block|{
comment|// dig out the underlying SQL statement
name|sqlExplain
operator|=
operator|(
name|SqlExplain
operator|)
name|sqlQuery
expr_stmt|;
name|sqlQuery
operator|=
name|sqlExplain
operator|.
name|getExplicandum
argument_list|()
expr_stmt|;
name|sqlToRelConverter
operator|.
name|setIsExplain
argument_list|(
name|sqlExplain
operator|.
name|getDynamicParamCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RelNode
name|rootRel
init|=
name|sqlToRelConverter
operator|.
name|convertQuery
argument_list|(
name|sqlQuery
argument_list|,
name|needsValidation
argument_list|,
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|timingTracer
operator|!=
literal|null
condition|)
block|{
name|timingTracer
operator|.
name|traceTime
argument_list|(
literal|"end sql2rel"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|RelDataType
name|resultType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|sqlQuery
argument_list|)
decl_stmt|;
name|fieldOrigins
operator|=
name|validator
operator|.
name|getFieldOrigins
argument_list|(
name|sqlQuery
argument_list|)
expr_stmt|;
assert|assert
name|fieldOrigins
operator|.
name|size
argument_list|()
operator|==
name|resultType
operator|.
name|getFieldCount
argument_list|()
assert|;
name|parameterRowType
operator|=
name|validator
operator|.
name|getParameterRowType
argument_list|(
name|sqlQuery
argument_list|)
expr_stmt|;
comment|// Display logical plans before view expansion, plugging in physical
comment|// storage and decorrelation
if|if
condition|(
name|sqlExplain
operator|!=
literal|null
condition|)
block|{
name|SqlExplain
operator|.
name|Depth
name|explainDepth
init|=
name|sqlExplain
operator|.
name|getDepth
argument_list|()
decl_stmt|;
name|boolean
name|explainAsXml
init|=
name|sqlExplain
operator|.
name|isXml
argument_list|()
decl_stmt|;
name|SqlExplainLevel
name|detailLevel
init|=
name|sqlExplain
operator|.
name|getDetailLevel
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|explainDepth
condition|)
block|{
case|case
name|TYPE
case|:
return|return
name|createPreparedExplanation
argument_list|(
name|resultType
argument_list|,
name|parameterRowType
argument_list|,
literal|null
argument_list|,
name|explainAsXml
argument_list|,
name|detailLevel
argument_list|)
return|;
case|case
name|LOGICAL
case|:
return|return
name|createPreparedExplanation
argument_list|(
literal|null
argument_list|,
name|parameterRowType
argument_list|,
name|rootRel
argument_list|,
name|explainAsXml
argument_list|,
name|detailLevel
argument_list|)
return|;
default|default:
block|}
block|}
comment|// Structured type flattening, view expansion, and plugging in physical
comment|// storage.
name|rootRel
operator|=
name|flattenTypes
argument_list|(
name|rootRel
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Subquery decorrelation.
name|rootRel
operator|=
name|decorrelate
argument_list|(
name|sqlToRelConverter
argument_list|,
name|sqlQuery
argument_list|,
name|rootRel
argument_list|)
expr_stmt|;
comment|// Trim unused fields.
name|rootRel
operator|=
name|trimUnusedFields
argument_list|(
name|rootRel
argument_list|)
expr_stmt|;
comment|// Display physical plan after decorrelation.
if|if
condition|(
name|sqlExplain
operator|!=
literal|null
condition|)
block|{
name|SqlExplain
operator|.
name|Depth
name|explainDepth
init|=
name|sqlExplain
operator|.
name|getDepth
argument_list|()
decl_stmt|;
name|boolean
name|explainAsXml
init|=
name|sqlExplain
operator|.
name|isXml
argument_list|()
decl_stmt|;
name|SqlExplainLevel
name|detailLevel
init|=
name|sqlExplain
operator|.
name|getDetailLevel
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|explainDepth
condition|)
block|{
case|case
name|PHYSICAL
case|:
default|default:
name|rootRel
operator|=
name|optimize
argument_list|(
name|rootRel
operator|.
name|getRowType
argument_list|()
argument_list|,
name|rootRel
argument_list|,
name|materializations
argument_list|)
expr_stmt|;
return|return
name|createPreparedExplanation
argument_list|(
literal|null
argument_list|,
name|parameterRowType
argument_list|,
name|rootRel
argument_list|,
name|explainAsXml
argument_list|,
name|detailLevel
argument_list|)
return|;
block|}
block|}
name|rootRel
operator|=
name|optimize
argument_list|(
name|resultType
argument_list|,
name|rootRel
argument_list|,
name|materializations
argument_list|)
expr_stmt|;
if|if
condition|(
name|timingTracer
operator|!=
literal|null
condition|)
block|{
name|timingTracer
operator|.
name|traceTime
argument_list|(
literal|"end optimization"
argument_list|)
expr_stmt|;
block|}
comment|// For transformation from DML -> DML, use result of rewrite
comment|// (e.g. UPDATE -> MERGE).  For anything else (e.g. CALL -> SELECT),
comment|// use original kind.
name|SqlKind
name|kind
init|=
name|sqlQuery
operator|.
name|getKind
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|kind
operator|.
name|belongsTo
argument_list|(
name|SqlKind
operator|.
name|DML
argument_list|)
condition|)
block|{
name|kind
operator|=
name|sqlNodeOriginal
operator|.
name|getKind
argument_list|()
expr_stmt|;
block|}
return|return
name|implement
argument_list|(
name|resultType
argument_list|,
name|rootRel
argument_list|,
name|kind
argument_list|)
return|;
block|}
specifier|protected
name|TableModificationRel
operator|.
name|Operation
name|mapTableModOp
parameter_list|(
name|boolean
name|isDml
parameter_list|,
name|SqlKind
name|sqlKind
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isDml
condition|)
block|{
return|return
literal|null
return|;
block|}
switch|switch
condition|(
name|sqlKind
condition|)
block|{
case|case
name|INSERT
case|:
return|return
name|TableModificationRel
operator|.
name|Operation
operator|.
name|INSERT
return|;
case|case
name|DELETE
case|:
return|return
name|TableModificationRel
operator|.
name|Operation
operator|.
name|DELETE
return|;
case|case
name|MERGE
case|:
return|return
name|TableModificationRel
operator|.
name|Operation
operator|.
name|MERGE
return|;
case|case
name|UPDATE
case|:
return|return
name|TableModificationRel
operator|.
name|Operation
operator|.
name|UPDATE
return|;
default|default:
return|return
literal|null
return|;
block|}
block|}
comment|/**    * Protected method to allow subclasses to override construction of    * SqlToRelConverter.    */
specifier|protected
specifier|abstract
name|SqlToRelConverter
name|getSqlToRelConverter
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|CatalogReader
name|catalogReader
parameter_list|)
function_decl|;
comment|/**    * Protected method to allow subclasses to override construction of    * RelImplementor.    */
specifier|protected
specifier|abstract
name|RelImplementor
name|getRelImplementor
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|boolean
name|shouldAlwaysWriteJavaFile
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|RelNode
name|flattenTypes
parameter_list|(
name|RelNode
name|rootRel
parameter_list|,
name|boolean
name|restructure
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|RelNode
name|decorrelate
parameter_list|(
name|SqlToRelConverter
name|sqlToRelConverter
parameter_list|,
name|SqlNode
name|query
parameter_list|,
name|RelNode
name|rootRel
parameter_list|)
function_decl|;
comment|/**    * Walks over a tree of relational expressions, replacing each    * {@link org.eigenbase.rel.RelNode} with a 'slimmed down' relational    * expression that projects    * only the columns required by its consumer.    *    * @param rootRel Relational expression that is at the root of the tree    * @return Trimmed relational expression    */
specifier|protected
name|RelNode
name|trimUnusedFields
parameter_list|(
name|RelNode
name|rootRel
parameter_list|)
block|{
specifier|final
name|SqlToRelConverter
name|converter
init|=
name|getSqlToRelConverter
argument_list|(
name|getSqlValidator
argument_list|()
argument_list|,
name|catalogReader
argument_list|)
decl_stmt|;
name|converter
operator|.
name|setTrimUnusedFields
argument_list|(
name|shouldTrim
argument_list|(
name|rootRel
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|converter
operator|.
name|trimUnusedFields
argument_list|(
name|rootRel
argument_list|)
return|;
block|}
specifier|private
name|boolean
name|shouldTrim
parameter_list|(
name|RelNode
name|rootRel
parameter_list|)
block|{
comment|// For now, don't trim if there are more than 3 joins. The projects
comment|// near the leaves created by trim migrate past joins and seem to
comment|// prevent join-reordering.
return|return
name|trim
operator|||
name|RelOptUtil
operator|.
name|countJoins
argument_list|(
name|rootRel
argument_list|)
operator|<
literal|2
return|;
block|}
comment|/**    * Returns a relational expression which is to be substituted for an access    * to a SQL view.    *    * @param rowType Row type of the view    * @param queryString Body of the view    * @param schemaPath List of schema names wherein to find referenced tables    * @return Relational expression    */
specifier|public
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
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|protected
specifier|abstract
name|void
name|init
parameter_list|(
name|Class
name|runtimeContextClass
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|SqlValidator
name|getSqlValidator
parameter_list|()
function_decl|;
comment|/** Interface by which validator and planner can read table metadata. */
specifier|public
interface|interface
name|CatalogReader
extends|extends
name|RelOptSchema
extends|,
name|SqlValidatorCatalogReader
block|{
name|PreparingTable
name|getTableForMember
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
comment|/** Returns a catalog reader the same as this one but with a possibly      * different schema path. */
name|CatalogReader
name|withSchemaPath
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
parameter_list|)
function_decl|;
name|PreparingTable
name|getTable
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
function_decl|;
block|}
comment|/** Definition of a table, for the purposes of the validator and planner. */
specifier|public
interface|interface
name|PreparingTable
extends|extends
name|RelOptTable
extends|,
name|SqlValidatorTable
block|{   }
comment|/**    * PreparedExplanation is a PreparedResult for an EXPLAIN PLAN statement.    * It's always good to have an explanation prepared.    */
specifier|public
specifier|abstract
specifier|static
class|class
name|PreparedExplain
implements|implements
name|PreparedResult
block|{
specifier|private
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|parameterRowType
decl_stmt|;
specifier|private
specifier|final
name|RelNode
name|rel
decl_stmt|;
specifier|private
specifier|final
name|boolean
name|asXml
decl_stmt|;
specifier|private
specifier|final
name|SqlExplainLevel
name|detailLevel
decl_stmt|;
specifier|public
name|PreparedExplain
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RelDataType
name|parameterRowType
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|boolean
name|asXml
parameter_list|,
name|SqlExplainLevel
name|detailLevel
parameter_list|)
block|{
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|parameterRowType
operator|=
name|parameterRowType
expr_stmt|;
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
name|this
operator|.
name|asXml
operator|=
name|asXml
expr_stmt|;
name|this
operator|.
name|detailLevel
operator|=
name|detailLevel
expr_stmt|;
block|}
specifier|public
name|String
name|getCode
parameter_list|()
block|{
if|if
condition|(
name|rel
operator|==
literal|null
condition|)
block|{
return|return
name|RelOptUtil
operator|.
name|dumpType
argument_list|(
name|rowType
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|RelOptUtil
operator|.
name|dumpPlan
argument_list|(
literal|""
argument_list|,
name|rel
argument_list|,
name|asXml
argument_list|,
name|detailLevel
argument_list|)
return|;
block|}
block|}
specifier|public
name|RelDataType
name|getParameterRowType
parameter_list|()
block|{
return|return
name|parameterRowType
return|;
block|}
specifier|public
name|boolean
name|isDml
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|TableModificationRel
operator|.
name|Operation
name|getTableModOp
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getFieldOrigins
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singletonList
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|nCopies
argument_list|(
literal|4
argument_list|,
literal|null
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|RelNode
name|getRel
parameter_list|()
block|{
return|return
name|rel
return|;
block|}
specifier|public
specifier|abstract
name|Bindable
name|getBindable
parameter_list|()
function_decl|;
block|}
comment|/**    * Result of a call to {@link Prepare#prepareSql}.    */
specifier|public
interface|interface
name|PreparedResult
block|{
comment|/**      * Returns the code generated by preparation.      */
name|String
name|getCode
parameter_list|()
function_decl|;
comment|/**      * Returns whether this result is for a DML statement, in which case the      * result set is one row with one column containing the number of rows      * affected.      */
name|boolean
name|isDml
parameter_list|()
function_decl|;
comment|/**      * Returns the table modification operation corresponding to this      * statement if it is a table modification statement; otherwise null.      */
name|TableModificationRel
operator|.
name|Operation
name|getTableModOp
parameter_list|()
function_decl|;
comment|/**      * Returns a list describing, for each result field, the origin of the      * field as a 4-element list of (database, schema, table, column).      */
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getFieldOrigins
parameter_list|()
function_decl|;
comment|/**      * Returns a record type whose fields are the parameters of this statement.      */
name|RelDataType
name|getParameterRowType
parameter_list|()
function_decl|;
comment|/**      * Executes the prepared result.      *      * @return producer of rows resulting from execution      */
name|Bindable
name|getBindable
parameter_list|()
function_decl|;
block|}
comment|/**    * Abstract implementation of {@link PreparedResult}.    */
specifier|public
specifier|abstract
specifier|static
class|class
name|PreparedResultImpl
implements|implements
name|PreparedResult
implements|,
name|Typed
block|{
specifier|protected
specifier|final
name|RelNode
name|rootRel
decl_stmt|;
specifier|protected
specifier|final
name|RelDataType
name|parameterRowType
decl_stmt|;
specifier|protected
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|protected
specifier|final
name|boolean
name|isDml
decl_stmt|;
specifier|protected
specifier|final
name|TableModificationRel
operator|.
name|Operation
name|tableModOp
decl_stmt|;
specifier|protected
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldOrigins
decl_stmt|;
specifier|public
name|PreparedResultImpl
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|RelDataType
name|parameterRowType
parameter_list|,
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fieldOrigins
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|TableModificationRel
operator|.
name|Operation
name|tableModOp
parameter_list|,
name|boolean
name|isDml
parameter_list|)
block|{
assert|assert
name|rowType
operator|!=
literal|null
assert|;
assert|assert
name|parameterRowType
operator|!=
literal|null
assert|;
assert|assert
name|fieldOrigins
operator|!=
literal|null
assert|;
assert|assert
name|rootRel
operator|!=
literal|null
assert|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|parameterRowType
operator|=
name|parameterRowType
expr_stmt|;
name|this
operator|.
name|fieldOrigins
operator|=
name|fieldOrigins
expr_stmt|;
name|this
operator|.
name|rootRel
operator|=
name|rootRel
expr_stmt|;
name|this
operator|.
name|tableModOp
operator|=
name|tableModOp
expr_stmt|;
name|this
operator|.
name|isDml
operator|=
name|isDml
expr_stmt|;
block|}
specifier|public
name|boolean
name|isDml
parameter_list|()
block|{
return|return
name|isDml
return|;
block|}
specifier|public
name|TableModificationRel
operator|.
name|Operation
name|getTableModOp
parameter_list|()
block|{
return|return
name|tableModOp
return|;
block|}
specifier|public
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|getFieldOrigins
parameter_list|()
block|{
return|return
name|fieldOrigins
return|;
block|}
specifier|public
name|RelDataType
name|getParameterRowType
parameter_list|()
block|{
return|return
name|parameterRowType
return|;
block|}
comment|/**      * Returns the physical row type of this prepared statement. May not be      * identical to the row type returned by the validator; for example, the      * field names may have been made unique.      */
specifier|public
name|RelDataType
name|getPhysicalRowType
parameter_list|()
block|{
return|return
name|rowType
return|;
block|}
specifier|public
specifier|abstract
name|Type
name|getElementType
parameter_list|()
function_decl|;
specifier|public
name|RelNode
name|getRootRel
parameter_list|()
block|{
return|return
name|rootRel
return|;
block|}
specifier|public
specifier|abstract
name|Bindable
name|getBindable
parameter_list|()
function_decl|;
block|}
comment|/** Describes that a given SQL query is materialized by a given table.    * The materialization is currently valid, and can be used in the planning    * process. */
specifier|public
specifier|static
class|class
name|Materialization
block|{
comment|/** The table that holds the materialized data. */
specifier|final
name|OptiqSchema
operator|.
name|TableEntry
name|materializedTable
decl_stmt|;
comment|/** The query that derives the data. */
specifier|final
name|String
name|sql
decl_stmt|;
comment|/** Relational expression for the table. Usually a      * {@link TableAccessRel}. */
name|RelNode
name|tableRel
decl_stmt|;
comment|/** Relational expression for the query to populate the table. */
name|RelNode
name|queryRel
decl_stmt|;
comment|/** Star table identified. */
specifier|private
name|RelOptTable
name|starRelOptTable
decl_stmt|;
specifier|public
name|Materialization
parameter_list|(
name|OptiqSchema
operator|.
name|TableEntry
name|materializedTable
parameter_list|,
name|String
name|sql
parameter_list|)
block|{
assert|assert
name|materializedTable
operator|!=
literal|null
assert|;
assert|assert
name|sql
operator|!=
literal|null
assert|;
name|this
operator|.
name|materializedTable
operator|=
name|materializedTable
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
block|}
specifier|public
name|void
name|materialize
parameter_list|(
name|RelNode
name|queryRel
parameter_list|,
name|RelOptTable
name|starRelOptTable
parameter_list|)
block|{
name|this
operator|.
name|queryRel
operator|=
name|queryRel
expr_stmt|;
name|this
operator|.
name|starRelOptTable
operator|=
name|starRelOptTable
expr_stmt|;
assert|assert
name|starRelOptTable
operator|.
name|unwrap
argument_list|(
name|StarTable
operator|.
name|class
argument_list|)
operator|!=
literal|null
assert|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End Prepare.java
end_comment

end_unit

