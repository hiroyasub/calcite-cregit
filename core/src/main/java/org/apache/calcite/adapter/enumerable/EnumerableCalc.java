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
name|adapter
operator|.
name|enumerable
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
name|DataContext
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
name|adapter
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
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Enumerator
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
name|tree
operator|.
name|BlockBuilder
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
name|tree
operator|.
name|BlockStatement
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
name|tree
operator|.
name|Blocks
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
name|linq4j
operator|.
name|tree
operator|.
name|Expressions
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
name|tree
operator|.
name|MemberDeclaration
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
name|tree
operator|.
name|ParameterExpression
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
name|tree
operator|.
name|Types
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
name|RelOptPredicateList
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
name|RelDistributionTraitDef
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
name|core
operator|.
name|Calc
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
name|RelMdCollation
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
name|RelMdDistribution
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|rex
operator|.
name|RexProgram
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
name|RexSimplify
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
name|sql
operator|.
name|validate
operator|.
name|SqlConformance
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
name|validate
operator|.
name|SqlConformanceEnum
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
name|BuiltInMethod
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
name|Modifier
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
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|enumerable
operator|.
name|EnumUtils
operator|.
name|BRIDGE_METHODS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|enumerable
operator|.
name|EnumUtils
operator|.
name|NO_EXPRS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|enumerable
operator|.
name|EnumUtils
operator|.
name|NO_PARAMS
import|;
end_import

begin_comment
comment|/** Implementation of {@link org.apache.calcite.rel.core.Calc} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableCalc
extends|extends
name|Calc
implements|implements
name|EnumerableRel
block|{
comment|/**    * Creates an EnumerableCalc.    *    *<p>Use {@link #create} unless you know what you're doing.    */
specifier|public
name|EnumerableCalc
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|instanceof
name|EnumerableConvention
assert|;
assert|assert
operator|!
name|program
operator|.
name|containsAggs
argument_list|()
assert|;
block|}
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|EnumerableCalc
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|input
parameter_list|,
name|RexProgram
name|program
parameter_list|,
name|List
argument_list|<
name|RelCollation
argument_list|>
name|collationList
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|collationList
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an EnumerableCalc. */
specifier|public
specifier|static
name|EnumerableCalc
name|create
parameter_list|(
specifier|final
name|RelNode
name|input
parameter_list|,
specifier|final
name|RexProgram
name|program
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|input
operator|.
name|getCluster
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|cluster
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSet
argument_list|()
operator|.
name|replace
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
operator|.
name|replaceIfs
argument_list|(
name|RelCollationTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|RelMdCollation
operator|.
name|calc
argument_list|(
name|mq
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
argument_list|)
operator|.
name|replaceIf
argument_list|(
name|RelDistributionTraitDef
operator|.
name|INSTANCE
argument_list|,
parameter_list|()
lambda|->
name|RelMdDistribution
operator|.
name|calc
argument_list|(
name|mq
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumerableCalc
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|program
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableCalc
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|child
parameter_list|,
name|RexProgram
name|program
parameter_list|)
block|{
comment|// we do not need to copy program; it is immutable
return|return
operator|new
name|EnumerableCalc
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|child
argument_list|,
name|program
argument_list|)
return|;
block|}
specifier|public
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
block|{
specifier|final
name|JavaTypeFactory
name|typeFactory
init|=
name|implementor
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
specifier|final
name|BlockBuilder
name|builder
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|EnumerableRel
name|child
init|=
operator|(
name|EnumerableRel
operator|)
name|getInput
argument_list|()
decl_stmt|;
specifier|final
name|Result
name|result
init|=
name|implementor
operator|.
name|visitChild
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|child
argument_list|,
name|pref
argument_list|)
decl_stmt|;
specifier|final
name|PhysType
name|physType
init|=
name|PhysTypeImpl
operator|.
name|of
argument_list|(
name|typeFactory
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|pref
operator|.
name|prefer
argument_list|(
name|result
operator|.
name|format
argument_list|)
argument_list|)
decl_stmt|;
comment|// final Enumerable<Employee> inputEnumerable =<<child adapter>>;
comment|// return new Enumerable<IntString>() {
comment|//     Enumerator<IntString> enumerator() {
comment|//         return new Enumerator<IntString>() {
comment|//             public void reset() {
comment|// ...
name|Type
name|outputJavaType
init|=
name|physType
operator|.
name|getJavaRowType
argument_list|()
decl_stmt|;
specifier|final
name|Type
name|enumeratorType
init|=
name|Types
operator|.
name|of
argument_list|(
name|Enumerator
operator|.
name|class
argument_list|,
name|outputJavaType
argument_list|)
decl_stmt|;
name|Type
name|inputJavaType
init|=
name|result
operator|.
name|physType
operator|.
name|getJavaRowType
argument_list|()
decl_stmt|;
name|ParameterExpression
name|inputEnumerator
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Types
operator|.
name|of
argument_list|(
name|Enumerator
operator|.
name|class
argument_list|,
name|inputJavaType
argument_list|)
argument_list|,
literal|"inputEnumerator"
argument_list|)
decl_stmt|;
name|Expression
name|input
init|=
name|EnumUtils
operator|.
name|convert
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|inputEnumerator
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERATOR_CURRENT
operator|.
name|method
argument_list|)
argument_list|,
name|inputJavaType
argument_list|)
decl_stmt|;
specifier|final
name|RexBuilder
name|rexBuilder
init|=
name|getCluster
argument_list|()
operator|.
name|getRexBuilder
argument_list|()
decl_stmt|;
specifier|final
name|RelMetadataQuery
name|mq
init|=
name|getCluster
argument_list|()
operator|.
name|getMetadataQuery
argument_list|()
decl_stmt|;
specifier|final
name|RelOptPredicateList
name|predicates
init|=
name|mq
operator|.
name|getPulledUpPredicates
argument_list|(
name|child
argument_list|)
decl_stmt|;
specifier|final
name|RexSimplify
name|simplify
init|=
operator|new
name|RexSimplify
argument_list|(
name|rexBuilder
argument_list|,
name|predicates
argument_list|,
name|RexUtil
operator|.
name|EXECUTOR
argument_list|)
decl_stmt|;
specifier|final
name|RexProgram
name|program
init|=
name|this
operator|.
name|program
operator|.
name|normalize
argument_list|(
name|rexBuilder
argument_list|,
name|simplify
argument_list|)
decl_stmt|;
name|BlockStatement
name|moveNextBody
decl_stmt|;
if|if
condition|(
name|program
operator|.
name|getCondition
argument_list|()
operator|==
literal|null
condition|)
block|{
name|moveNextBody
operator|=
name|Blocks
operator|.
name|toFunctionBlock
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|inputEnumerator
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERATOR_MOVE_NEXT
operator|.
name|method
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|BlockBuilder
name|builder2
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|Expression
name|condition
init|=
name|RexToLixTranslator
operator|.
name|translateCondition
argument_list|(
name|program
argument_list|,
name|typeFactory
argument_list|,
name|builder2
argument_list|,
operator|new
name|RexToLixTranslator
operator|.
name|InputGetterImpl
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|input
argument_list|,
name|result
operator|.
name|physType
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|implementor
operator|.
name|allCorrelateVariables
argument_list|,
name|implementor
operator|.
name|getConformance
argument_list|()
argument_list|)
decl_stmt|;
name|builder2
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|ifThen
argument_list|(
name|condition
argument_list|,
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|moveNextBody
operator|=
name|Expressions
operator|.
name|block
argument_list|(
name|Expressions
operator|.
name|while_
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|inputEnumerator
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERATOR_MOVE_NEXT
operator|.
name|method
argument_list|)
argument_list|,
name|builder2
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|,
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
literal|false
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BlockBuilder
name|builder3
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|SqlConformance
name|conformance
init|=
operator|(
name|SqlConformance
operator|)
name|implementor
operator|.
name|map
operator|.
name|getOrDefault
argument_list|(
literal|"_conformance"
argument_list|,
name|SqlConformanceEnum
operator|.
name|DEFAULT
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|expressions
init|=
name|RexToLixTranslator
operator|.
name|translateProjects
argument_list|(
name|program
argument_list|,
name|typeFactory
argument_list|,
name|conformance
argument_list|,
name|builder3
argument_list|,
name|physType
argument_list|,
name|DataContext
operator|.
name|ROOT
argument_list|,
operator|new
name|RexToLixTranslator
operator|.
name|InputGetterImpl
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|input
argument_list|,
name|result
operator|.
name|physType
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|implementor
operator|.
name|allCorrelateVariables
argument_list|)
decl_stmt|;
name|builder3
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|physType
operator|.
name|record
argument_list|(
name|expressions
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|BlockStatement
name|currentBody
init|=
name|builder3
operator|.
name|toBlock
argument_list|()
decl_stmt|;
specifier|final
name|Expression
name|inputEnumerable
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"inputEnumerable"
argument_list|,
name|result
operator|.
name|block
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|body
init|=
name|Expressions
operator|.
name|new_
argument_list|(
name|enumeratorType
argument_list|,
name|NO_EXPRS
argument_list|,
name|Expressions
operator|.
name|list
argument_list|(
name|Expressions
operator|.
name|fieldDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
operator||
name|Modifier
operator|.
name|FINAL
argument_list|,
name|inputEnumerator
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|inputEnumerable
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERABLE_ENUMERATOR
operator|.
name|method
argument_list|)
argument_list|)
argument_list|,
name|EnumUtils
operator|.
name|overridingMethodDecl
argument_list|(
name|BuiltInMethod
operator|.
name|ENUMERATOR_RESET
operator|.
name|method
argument_list|,
name|NO_PARAMS
argument_list|,
name|Blocks
operator|.
name|toFunctionBlock
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|inputEnumerator
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERATOR_RESET
operator|.
name|method
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|EnumUtils
operator|.
name|overridingMethodDecl
argument_list|(
name|BuiltInMethod
operator|.
name|ENUMERATOR_MOVE_NEXT
operator|.
name|method
argument_list|,
name|NO_PARAMS
argument_list|,
name|moveNextBody
argument_list|)
argument_list|,
name|EnumUtils
operator|.
name|overridingMethodDecl
argument_list|(
name|BuiltInMethod
operator|.
name|ENUMERATOR_CLOSE
operator|.
name|method
argument_list|,
name|NO_PARAMS
argument_list|,
name|Blocks
operator|.
name|toFunctionBlock
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|inputEnumerator
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERATOR_CLOSE
operator|.
name|method
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|BRIDGE_METHODS
condition|?
name|Object
operator|.
name|class
else|:
name|outputJavaType
argument_list|,
literal|"current"
argument_list|,
name|NO_PARAMS
argument_list|,
name|currentBody
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|return_
argument_list|(
literal|null
argument_list|,
name|Expressions
operator|.
name|new_
argument_list|(
name|BuiltInMethod
operator|.
name|ABSTRACT_ENUMERABLE_CTOR
operator|.
name|constructor
argument_list|,
comment|// TODO: generics
comment|//   Collections.singletonList(inputRowType),
name|NO_EXPRS
argument_list|,
name|ImmutableList
operator|.
expr|<
name|MemberDeclaration
operator|>
name|of
argument_list|(
name|Expressions
operator|.
name|methodDecl
argument_list|(
name|Modifier
operator|.
name|PUBLIC
argument_list|,
name|enumeratorType
argument_list|,
name|BuiltInMethod
operator|.
name|ENUMERABLE_ENUMERATOR
operator|.
name|method
operator|.
name|getName
argument_list|()
argument_list|,
name|NO_PARAMS
argument_list|,
name|Blocks
operator|.
name|toFunctionBlock
argument_list|(
name|body
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|implementor
operator|.
name|result
argument_list|(
name|physType
argument_list|,
name|builder
operator|.
name|toBlock
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
name|RelTraitSet
argument_list|,
name|List
argument_list|<
name|RelTraitSet
argument_list|>
argument_list|>
name|passThroughTraits
parameter_list|(
specifier|final
name|RelTraitSet
name|required
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
init|=
name|Util
operator|.
name|transform
argument_list|(
name|program
operator|.
name|getProjectList
argument_list|()
argument_list|,
name|program
operator|::
name|expandLocalRef
argument_list|)
decl_stmt|;
return|return
name|EnumerableTraitsUtils
operator|.
name|passThroughTraitsForProject
argument_list|(
name|required
argument_list|,
name|exps
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
argument_list|,
name|input
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|traitSet
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
name|RelTraitSet
argument_list|,
name|List
argument_list|<
name|RelTraitSet
argument_list|>
argument_list|>
name|deriveTraits
parameter_list|(
specifier|final
name|RelTraitSet
name|childTraits
parameter_list|,
specifier|final
name|int
name|childId
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|exps
init|=
name|Util
operator|.
name|transform
argument_list|(
name|program
operator|.
name|getProjectList
argument_list|()
argument_list|,
name|program
operator|::
name|expandLocalRef
argument_list|)
decl_stmt|;
return|return
name|EnumerableTraitsUtils
operator|.
name|deriveTraitsForProject
argument_list|(
name|childTraits
argument_list|,
name|childId
argument_list|,
name|exps
argument_list|,
name|input
operator|.
name|getRowType
argument_list|()
argument_list|,
name|input
operator|.
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|traitSet
argument_list|)
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
end_class

end_unit

