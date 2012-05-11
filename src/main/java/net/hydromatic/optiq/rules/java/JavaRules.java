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
name|rules
operator|.
name|java
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerable
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
name|ExtendedEnumerable
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
name|Queryable
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

begin_import
import|import
name|net
operator|.
name|hydromatic
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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Function2
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
name|java
operator|.
name|JavaTypeFactory
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
name|convert
operator|.
name|ConverterRule
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
name|RelMetadataQuery
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
name|RexMultisetUtil
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
name|rex
operator|.
name|RexProgram
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
name|Field
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
name|Method
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

begin_comment
comment|/**  * Rules and relational operators for the {@link Enumerable} calling convention.  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|JavaRules
block|{
comment|// not used
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|JAVA_PROJECT_RULE
init|=
operator|new
name|RelOptRule
argument_list|(
operator|new
name|RelOptRuleOperand
argument_list|(
name|ProjectRel
operator|.
name|class
argument_list|,
name|CallingConvention
operator|.
name|NONE
argument_list|,
operator|new
name|RelOptRuleOperand
argument_list|(
name|RelNode
operator|.
name|class
argument_list|,
name|RelOptRuleOperand
operator|.
name|Dummy
operator|.
name|ANY
argument_list|)
argument_list|)
argument_list|,
literal|"JavaProjectRule"
argument_list|)
block|{
specifier|public
name|void
name|onMatch
parameter_list|(
name|RelOptRuleCall
name|call
parameter_list|)
block|{
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptRule
name|ENUMERABLE_JOIN_RULE
init|=
operator|new
name|ConverterRule
argument_list|(
name|JoinRel
operator|.
name|class
argument_list|,
name|CallingConvention
operator|.
name|NONE
argument_list|,
name|CallingConvention
operator|.
name|ENUMERABLE
argument_list|,
literal|"IterJoinRule"
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
name|JoinRel
name|join
init|=
operator|(
name|JoinRel
operator|)
name|rel
decl_stmt|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|newInputs
init|=
name|convert
argument_list|(
name|CallingConvention
operator|.
name|ENUMERABLE
argument_list|,
name|join
operator|.
name|getInputs
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|newInputs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|EnumerableJoinRel
argument_list|(
name|join
operator|.
name|getCluster
argument_list|()
argument_list|,
name|join
operator|.
name|getTraitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|CallingConvention
operator|.
name|ENUMERABLE
argument_list|)
argument_list|,
name|newInputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|newInputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|join
operator|.
name|getCondition
argument_list|()
argument_list|,
name|join
operator|.
name|getJoinType
argument_list|()
argument_list|,
name|join
operator|.
name|getVariablesStopped
argument_list|()
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
class|class
name|EnumerableJoinRel
extends|extends
name|JoinRelBase
implements|implements
name|EnumerableRel
block|{
specifier|static
specifier|final
name|Method
name|join
decl_stmt|;
static|static
block|{
try|try
block|{
name|join
operator|=
name|ExtendedEnumerable
operator|.
name|class
operator|.
name|getMethod
argument_list|(
literal|"join"
argument_list|,
name|Enumerable
operator|.
name|class
argument_list|,
name|Function1
operator|.
name|class
argument_list|,
name|Function1
operator|.
name|class
argument_list|,
name|Function2
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|protected
name|EnumerableJoinRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traits
parameter_list|,
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
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traits
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
assert|assert
name|inputs
operator|.
name|size
argument_list|()
operator|==
literal|2
assert|;
return|return
operator|new
name|EnumerableJoinRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|condition
argument_list|,
name|joinType
argument_list|,
name|variablesStopped
argument_list|)
return|;
block|}
specifier|public
name|Expression
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Integer
argument_list|>
name|rightKeys
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|()
decl_stmt|;
name|RexNode
name|remaining
init|=
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|leftKeys
argument_list|,
name|rightKeys
argument_list|)
decl_stmt|;
assert|assert
name|remaining
operator|.
name|isAlwaysTrue
argument_list|()
operator|:
literal|"EnumerableJoin is equi only"
assert|;
comment|// TODO: stricter pre-check
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
operator|(
name|JavaTypeFactory
operator|)
name|left
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|left
argument_list|)
argument_list|,
name|join
argument_list|,
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|right
argument_list|)
argument_list|,
name|EnumUtil
operator|.
name|generateAccessor
argument_list|(
name|typeFactory
argument_list|,
name|left
operator|.
name|getRowType
argument_list|()
argument_list|,
name|leftKeys
argument_list|)
argument_list|,
name|EnumUtil
operator|.
name|generateAccessor
argument_list|(
name|typeFactory
argument_list|,
name|right
operator|.
name|getRowType
argument_list|()
argument_list|,
name|rightKeys
argument_list|)
argument_list|,
name|generateSelector
argument_list|(
name|typeFactory
argument_list|)
argument_list|)
return|;
block|}
name|Expression
name|generateSelector
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
comment|// A parameter for each input.
specifier|final
name|List
argument_list|<
name|ParameterExpression
argument_list|>
name|parameters
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|Expressions
operator|.
name|parameter
argument_list|(
name|EnumUtil
operator|.
name|javaClass
argument_list|(
name|typeFactory
argument_list|,
name|left
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|,
literal|"left"
argument_list|)
argument_list|,
name|Expressions
operator|.
name|parameter
argument_list|(
name|EnumUtil
operator|.
name|javaClass
argument_list|(
name|typeFactory
argument_list|,
name|right
operator|.
name|getRowType
argument_list|()
argument_list|)
argument_list|,
literal|"right"
argument_list|)
argument_list|)
decl_stmt|;
comment|// Generate all fields.
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelNode
name|rel
range|:
name|getInputs
argument_list|()
control|)
block|{
name|RelDataType
name|inputRowType
init|=
name|rel
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|ParameterExpression
name|parameter
init|=
name|parameters
operator|.
name|get
argument_list|(
name|i
operator|++
argument_list|)
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|inputRowType
operator|.
name|getFields
argument_list|()
control|)
block|{
name|expressions
operator|.
name|add
argument_list|(
name|fieldReference
argument_list|(
name|parameter
argument_list|,
name|field
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function2
operator|.
name|class
argument_list|,
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|newArrayInit
argument_list|(
name|Object
operator|.
name|class
argument_list|,
name|expressions
argument_list|)
argument_list|)
argument_list|,
name|parameters
argument_list|)
return|;
block|}
specifier|private
name|Expression
name|fieldReference
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|RelDataTypeField
name|field
parameter_list|)
block|{
return|return
name|parameter
operator|.
name|getClass
argument_list|()
operator|.
name|isArray
argument_list|()
condition|?
name|Expressions
operator|.
name|arrayIndex
argument_list|(
name|parameter
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|field
operator|.
name|getIndex
argument_list|()
argument_list|)
argument_list|)
else|:
name|Expressions
operator|.
name|field
argument_list|(
name|parameter
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Utilities for generating programs in the Enumerable (functional)      * style.      */
specifier|public
specifier|static
class|class
name|EnumUtil
block|{
specifier|static
name|Expression
name|generateAccessor
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|fields
parameter_list|)
block|{
assert|assert
name|fields
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|:
literal|"composite keys not implemented yet"
assert|;
name|int
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/*             new Function1<Employee, Res> {                 public Res apply(Employee v1) {                     return v1.<fieldN>;                 }             }              */
name|ParameterExpression
name|v1
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|rowType
argument_list|)
argument_list|,
literal|"v1"
argument_list|)
decl_stmt|;
return|return
name|Expressions
operator|.
name|lambda
argument_list|(
name|Function1
operator|.
name|class
argument_list|,
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|field
argument_list|(
name|v1
argument_list|,
name|nthField
argument_list|(
name|field
argument_list|,
name|v1
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|v1
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Field
name|nthField
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
return|return
name|clazz
operator|.
name|getFields
argument_list|()
index|[
name|ordinal
index|]
return|;
block|}
specifier|static
name|Class
name|javaClass
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
specifier|final
name|Class
name|clazz
init|=
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|type
argument_list|)
decl_stmt|;
return|return
name|clazz
operator|==
literal|null
condition|?
name|Object
index|[]
operator|.
name|class
else|:
name|clazz
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|EnumerableTableAccessRel
extends|extends
name|TableAccessRelBase
implements|implements
name|EnumerableRel
block|{
specifier|private
specifier|final
name|Expression
name|expression
decl_stmt|;
specifier|private
specifier|final
name|Queryable
name|queryable
decl_stmt|;
specifier|public
name|EnumerableTableAccessRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelOptTable
name|table
parameter_list|,
name|RelOptConnection
name|connection
parameter_list|,
name|Expression
name|expression
parameter_list|,
name|Queryable
name|queryable
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|CallingConvention
operator|.
name|ENUMERABLE
argument_list|)
argument_list|,
name|table
argument_list|,
name|connection
argument_list|)
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|queryable
operator|=
name|queryable
expr_stmt|;
block|}
specifier|public
name|Expression
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|)
block|{
return|return
name|implementor
operator|.
name|register
argument_list|(
name|queryable
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
specifier|final
name|EnumerableCalcRule
name|ENUMERABLE_CALC_RULE
init|=
operator|new
name|EnumerableCalcRule
argument_list|()
decl_stmt|;
comment|/**      * Rule to convert a {@link CalcRel} to an      * {@link net.hydromatic.optiq.rules.java.JavaRules.EnumerableCalcRel}.      */
specifier|private
specifier|static
class|class
name|EnumerableCalcRule
extends|extends
name|ConverterRule
block|{
specifier|private
name|EnumerableCalcRule
parameter_list|()
block|{
name|super
argument_list|(
name|CalcRel
operator|.
name|class
argument_list|,
name|CallingConvention
operator|.
name|NONE
argument_list|,
name|CallingConvention
operator|.
name|ENUMERABLE
argument_list|,
literal|"EnumerableCalcRule"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|CalcRel
name|calc
init|=
operator|(
name|CalcRel
operator|)
name|rel
decl_stmt|;
specifier|final
name|RelNode
name|convertedChild
init|=
name|mergeTraitsAndConvert
argument_list|(
name|calc
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|CallingConvention
operator|.
name|ENUMERABLE
argument_list|,
name|calc
operator|.
name|getChild
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|convertedChild
operator|==
literal|null
condition|)
block|{
comment|// We can't convert the child, so we can't convert rel.
return|return
literal|null
return|;
block|}
comment|// If there's a multiset, let FarragoMultisetSplitter work on it
comment|// first.
if|if
condition|(
name|RexMultisetUtil
operator|.
name|containsMultiset
argument_list|(
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|EnumerableCalcRel
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|convertedChild
argument_list|,
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|,
name|ProjectRelBase
operator|.
name|Flags
operator|.
name|Boxed
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|static
class|class
name|EnumerableCalcRel
extends|extends
name|SingleRel
implements|implements
name|EnumerableRel
block|{
specifier|private
specifier|final
name|RexProgram
name|program
decl_stmt|;
comment|/**          * Values defined in {@link org.eigenbase.rel.ProjectRelBase.Flags}.          */
specifier|protected
name|int
name|flags
decl_stmt|;
specifier|public
name|EnumerableCalcRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|,
name|int
name|flags
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
operator|.
name|plus
argument_list|(
name|CallingConvention
operator|.
name|ENUMERABLE
argument_list|)
argument_list|,
name|child
argument_list|)
expr_stmt|;
name|this
operator|.
name|flags
operator|=
name|flags
expr_stmt|;
name|this
operator|.
name|program
operator|=
name|program
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|program
operator|.
name|getOutputRowType
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|explain
parameter_list|(
name|RelOptPlanWriter
name|pw
parameter_list|)
block|{
name|program
operator|.
name|explainCalc
argument_list|(
name|this
argument_list|,
name|pw
argument_list|)
expr_stmt|;
block|}
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|FilterRel
operator|.
name|estimateFilteredRows
argument_list|(
name|getChild
argument_list|()
argument_list|,
name|program
argument_list|)
return|;
block|}
specifier|public
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
name|this
argument_list|)
decl_stmt|;
name|double
name|dCpu
init|=
name|RelMetadataQuery
operator|.
name|getRowCount
argument_list|(
name|getChild
argument_list|()
argument_list|)
operator|*
name|program
operator|.
name|getExprCount
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
return|return
operator|new
name|EnumerableCalcRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|,
name|program
operator|.
name|copy
argument_list|()
argument_list|,
name|getFlags
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
name|isBoxed
parameter_list|()
block|{
return|return
operator|(
name|flags
operator|&
name|ProjectRelBase
operator|.
name|Flags
operator|.
name|Boxed
operator|)
operator|==
name|ProjectRelBase
operator|.
name|Flags
operator|.
name|Boxed
return|;
block|}
specifier|public
name|Expression
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|)
block|{
name|Expression
name|childExp
init|=
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|getChild
argument_list|()
argument_list|)
decl_stmt|;
name|RelDataType
name|outputRowType
init|=
name|getRowType
argument_list|()
decl_stmt|;
name|RelDataType
name|inputRowType
init|=
name|getChild
argument_list|()
operator|.
name|getRowType
argument_list|()
decl_stmt|;
return|return
name|childExp
comment|/* implementAbstract(                 implementor,                 this,                 childExp,                 varInputRow,                 inputRowType,                 outputRowType,                 program,                 null) */
return|;
block|}
specifier|public
name|RexProgram
name|getProgram
parameter_list|()
block|{
return|return
name|program
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JavaRules.java
end_comment

end_unit

