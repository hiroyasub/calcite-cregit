begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|function
operator|.
name|Function1
import|;
end_import

begin_import
import|import
name|openjava
operator|.
name|mop
operator|.
name|OJClass
import|;
end_import

begin_import
import|import
name|openjava
operator|.
name|ptree
operator|.
name|*
import|;
end_import

begin_import
import|import
name|openjava
operator|.
name|ptree
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|oj
operator|.
name|rel
operator|.
name|JavaRel
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|oj
operator|.
name|rel
operator|.
name|JavaRelImplementor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|oj
operator|.
name|util
operator|.
name|OJUtil
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
name|rex
operator|.
name|RexLocalRef
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
name|JAVA_PROJECT_ROLE
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
name|JavaRel
block|{
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
name|ParseTree
name|implement
parameter_list|(
name|JavaRelImplementor
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
return|return
operator|new
name|MethodCall
argument_list|(
name|implementor
operator|.
name|visitJavaChild
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
operator|(
name|JavaRel
operator|)
name|left
argument_list|)
argument_list|,
literal|"join"
argument_list|,
name|OJUtil
operator|.
name|expressionList
argument_list|(
name|implementor
operator|.
name|visitJavaChild
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
operator|(
name|JavaRel
operator|)
name|right
argument_list|)
argument_list|,
name|EnumUtil
operator|.
name|generateAccessor
argument_list|(
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
name|right
operator|.
name|getRowType
argument_list|()
argument_list|,
name|rightKeys
argument_list|)
argument_list|,
name|EnumUtil
operator|.
name|generateSelector
argument_list|(
name|rowType
argument_list|)
argument_list|)
argument_list|)
return|;
comment|/*             input1.join(              input2,               */
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
return|return
operator|new
name|AllocationExpression
argument_list|(
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|OJClass
operator|.
name|forClass
argument_list|(
name|Function1
operator|.
name|class
argument_list|)
argument_list|)
argument_list|,
operator|new
name|ExpressionList
argument_list|()
argument_list|,
operator|new
name|MemberDeclarationList
argument_list|(
operator|new
name|MethodDeclaration
argument_list|(
operator|new
name|ModifierList
argument_list|(
name|ModifierList
operator|.
name|PUBLIC
argument_list|)
argument_list|,
name|EnumUtil
operator|.
name|toTypeName
argument_list|(
name|rowType
operator|.
name|getFields
argument_list|()
index|[
name|field
index|]
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|,
literal|"apply"
argument_list|,
operator|new
name|ParameterList
argument_list|(
operator|new
name|Parameter
argument_list|(
name|EnumUtil
operator|.
name|toTypeName
argument_list|(
name|rowType
argument_list|)
argument_list|,
literal|"v1"
argument_list|)
argument_list|)
argument_list|,
operator|new
name|TypeName
index|[
literal|0
index|]
argument_list|,
operator|new
name|StatementList
argument_list|(
operator|new
name|ReturnStatement
argument_list|()
argument_list|)
argument_list|)
argument_list|)
argument_list|)
return|;
comment|/*             return new net.hydromatic.linq4j.function.Function1<Employee, Integer>() {                 public Integer apply(Employee p0) {                     return                 }             }             */
block|}
specifier|private
specifier|static
name|TypeName
name|toTypeName
parameter_list|(
name|RelDataType
name|type
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
specifier|static
name|Expression
name|generateSelector
parameter_list|(
name|RelDataType
name|rowType
parameter_list|)
block|{
return|return
literal|null
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
name|JavaRel
block|{
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
block|}
specifier|public
name|ParseTree
name|implement
parameter_list|(
name|JavaRelImplementor
name|implementor
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
comment|// REVIEW: want to move canTranslate into RelImplementor
comment|// and implement it for Java& C++ calcs.
specifier|final
name|JavaRelImplementor
name|relImplementor
init|=
name|rel
operator|.
name|getCluster
argument_list|()
operator|.
name|getPlanner
argument_list|()
operator|.
name|getJavaRelImplementor
argument_list|(
name|rel
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|relImplementor
operator|.
name|canTranslate
argument_list|(
name|convertedChild
argument_list|,
name|calc
operator|.
name|getProgram
argument_list|()
argument_list|)
condition|)
block|{
comment|// Some of the expressions cannot be translated into Java
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
name|JavaRel
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
comment|/**          * Burrows into a synthetic record and returns the underlying relation which          * provides the field called<code>fieldName</code>.          */
specifier|public
name|JavaRel
name|implementFieldAccess
parameter_list|(
name|JavaRelImplementor
name|implementor
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isBoxed
argument_list|()
condition|)
block|{
return|return
name|implementor
operator|.
name|implementFieldAccess
argument_list|(
operator|(
name|JavaRel
operator|)
name|getChild
argument_list|()
argument_list|,
name|fieldName
argument_list|)
return|;
block|}
name|RelDataType
name|type
init|=
name|getRowType
argument_list|()
decl_stmt|;
name|int
name|field
init|=
name|type
operator|.
name|getFieldOrdinal
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
name|RexLocalRef
name|ref
init|=
name|program
operator|.
name|getProjectList
argument_list|()
operator|.
name|get
argument_list|(
name|field
argument_list|)
decl_stmt|;
specifier|final
name|int
name|index
init|=
name|ref
operator|.
name|getIndex
argument_list|()
decl_stmt|;
return|return
name|implementor
operator|.
name|findRel
argument_list|(
name|this
argument_list|,
name|program
operator|.
name|getExprList
argument_list|()
operator|.
name|get
argument_list|(
name|index
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ParseTree
name|implement
parameter_list|(
name|JavaRelImplementor
name|implementor
parameter_list|)
block|{
name|Expression
name|childExp
init|=
name|implementor
operator|.
name|visitJavaChild
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
operator|(
name|JavaRel
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
name|Variable
name|varInputRow
init|=
name|implementor
operator|.
name|newVariable
argument_list|()
decl_stmt|;
name|implementor
operator|.
name|bind
argument_list|(
name|getChild
argument_list|()
argument_list|,
name|varInputRow
argument_list|)
expr_stmt|;
return|return
literal|null
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
specifier|private
specifier|static
name|Statement
name|assignInputRow
parameter_list|(
name|OJClass
name|inputRowClass
parameter_list|,
name|Variable
name|varInputRow
parameter_list|,
name|Variable
name|varInputObj
parameter_list|)
block|{
return|return
operator|new
name|ExpressionStatement
argument_list|(
operator|new
name|AssignmentExpression
argument_list|(
name|varInputRow
argument_list|,
name|AssignmentExpression
operator|.
name|EQUALS
argument_list|,
operator|new
name|CastExpression
argument_list|(
name|TypeName
operator|.
name|forOJClass
argument_list|(
name|inputRowClass
argument_list|)
argument_list|,
name|varInputObj
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End JavaRules.java
end_comment

end_unit

