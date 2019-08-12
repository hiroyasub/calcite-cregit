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
name|Match
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
name|RexCall
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
name|RexLiteral
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
name|runtime
operator|.
name|Enumerables
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
name|Pair
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
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
name|BiPredicate
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
name|Consumer
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

begin_comment
comment|/** Implementation of {@link org.apache.calcite.rel.core.Match} in  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention enumerable calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|EnumerableMatch
extends|extends
name|Match
implements|implements
name|EnumerableRel
block|{
comment|/**    * Creates an EnumerableMatch.    *    *<p>Use {@link #create} unless you know what you're doing.    */
specifier|public
name|EnumerableMatch
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
name|RelDataType
name|rowType
parameter_list|,
name|RexNode
name|pattern
parameter_list|,
name|boolean
name|strictStart
parameter_list|,
name|boolean
name|strictEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|patternDefinitions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|measures
parameter_list|,
name|RexNode
name|after
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SortedSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|subsets
parameter_list|,
name|boolean
name|allRows
parameter_list|,
name|ImmutableBitSet
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RexNode
name|interval
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|rowType
argument_list|,
name|pattern
argument_list|,
name|strictStart
argument_list|,
name|strictEnd
argument_list|,
name|patternDefinitions
argument_list|,
name|measures
argument_list|,
name|after
argument_list|,
name|subsets
argument_list|,
name|allRows
argument_list|,
name|partitionKeys
argument_list|,
name|orderKeys
argument_list|,
name|interval
argument_list|)
expr_stmt|;
block|}
comment|/** Creates an EnumerableMatch. */
specifier|public
specifier|static
name|EnumerableMatch
name|create
parameter_list|(
name|RelNode
name|input
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|RexNode
name|pattern
parameter_list|,
name|boolean
name|strictStart
parameter_list|,
name|boolean
name|strictEnd
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|patternDefinitions
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|measures
parameter_list|,
name|RexNode
name|after
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|SortedSet
argument_list|<
name|String
argument_list|>
argument_list|>
name|subsets
parameter_list|,
name|boolean
name|allRows
parameter_list|,
name|ImmutableBitSet
name|partitionKeys
parameter_list|,
name|RelCollation
name|orderKeys
parameter_list|,
name|RexNode
name|interval
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
name|RelTraitSet
name|traitSet
init|=
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|)
decl_stmt|;
return|return
operator|new
name|EnumerableMatch
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|input
argument_list|,
name|rowType
argument_list|,
name|pattern
argument_list|,
name|strictStart
argument_list|,
name|strictEnd
argument_list|,
name|patternDefinitions
argument_list|,
name|measures
argument_list|,
name|after
argument_list|,
name|subsets
argument_list|,
name|allRows
argument_list|,
name|partitionKeys
argument_list|,
name|orderKeys
argument_list|,
name|interval
argument_list|)
return|;
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
return|return
operator|new
name|EnumerableMatch
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
name|rowType
argument_list|,
name|pattern
argument_list|,
name|strictStart
argument_list|,
name|strictEnd
argument_list|,
name|patternDefinitions
argument_list|,
name|measures
argument_list|,
name|after
argument_list|,
name|subsets
argument_list|,
name|allRows
argument_list|,
name|partitionKeys
argument_list|,
name|orderKeys
argument_list|,
name|interval
argument_list|)
return|;
block|}
specifier|public
name|EnumerableRel
operator|.
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|EnumerableRel
operator|.
name|Prefer
name|pref
parameter_list|)
block|{
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
name|input
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
name|input
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
name|implementor
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|getRowType
argument_list|()
argument_list|,
name|result
operator|.
name|format
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|inputExp
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"input"
argument_list|,
name|result
operator|.
name|block
argument_list|)
decl_stmt|;
name|PhysType
name|inputPhysType
init|=
name|result
operator|.
name|physType
decl_stmt|;
specifier|final
name|PhysType
name|keyPhysType
init|=
name|inputPhysType
operator|.
name|project
argument_list|(
name|partitionKeys
operator|.
name|asList
argument_list|()
argument_list|,
name|JavaRowFormat
operator|.
name|LIST
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|row_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"row"
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|keySelector_
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"keySelector"
argument_list|,
name|inputPhysType
operator|.
name|generateSelector
argument_list|(
name|row_
argument_list|,
name|partitionKeys
operator|.
name|asList
argument_list|()
argument_list|,
name|keyPhysType
operator|.
name|getFormat
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|matcher_
init|=
name|implementMatcher
argument_list|(
name|builder
argument_list|,
name|row_
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|emitter_
init|=
name|implementEmitter
argument_list|(
name|implementor
argument_list|,
name|physType
argument_list|,
literal|null
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
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|MATCH
operator|.
name|method
argument_list|,
name|inputExp
argument_list|,
name|keySelector_
argument_list|,
name|matcher_
argument_list|,
name|emitter_
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
specifier|private
name|Expression
name|implementEmitter
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|PhysType
name|physType
parameter_list|,
name|PhysType
name|inputPhysType
parameter_list|)
block|{
specifier|final
name|ParameterExpression
name|rows_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"rows"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|rowStates_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"rowStates"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|match_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|int
operator|.
name|class
argument_list|,
literal|"match"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|consumer_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Consumer
operator|.
name|class
argument_list|,
literal|"consumer"
argument_list|)
decl_stmt|;
specifier|final
name|ParameterExpression
name|row_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"row"
argument_list|)
decl_stmt|;
specifier|final
name|BlockBuilder
name|builder2
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
init|=
name|RexToLixTranslator
operator|.
name|translateProjects
argument_list|(
literal|null
argument_list|,
operator|(
name|JavaTypeFactory
operator|)
name|getCluster
argument_list|()
operator|.
name|getTypeFactory
argument_list|()
argument_list|,
name|implementor
operator|.
name|getConformance
argument_list|()
argument_list|,
name|builder2
argument_list|,
name|physType
argument_list|,
name|implementor
operator|.
name|getRootExpression
argument_list|()
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
name|row_
argument_list|,
name|inputPhysType
argument_list|)
argument_list|)
argument_list|)
argument_list|,
name|implementor
operator|.
name|allCorrelateVariables
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|measure
range|:
name|measures
operator|.
name|values
argument_list|()
control|)
block|{
name|arguments
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|builder2
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|statement
argument_list|(
name|Expressions
operator|.
name|call
argument_list|(
name|consumer_
argument_list|,
name|BuiltInMethod
operator|.
name|CONSUMER_ACCEPT
operator|.
name|method
argument_list|,
name|physType
operator|.
name|record
argument_list|(
name|arguments
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|BlockBuilder
name|builder
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|add
argument_list|(
name|Expressions
operator|.
name|forEach
argument_list|(
name|row_
argument_list|,
name|rows_
argument_list|,
name|builder2
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|Expressions
operator|.
name|new_
argument_list|(
name|Types
operator|.
name|of
argument_list|(
name|Enumerables
operator|.
name|Emitter
operator|.
name|class
argument_list|)
argument_list|,
name|NO_EXPRS
argument_list|,
name|Expressions
operator|.
name|list
argument_list|(
name|EnumUtils
operator|.
name|overridingMethodDecl
argument_list|(
name|BuiltInMethod
operator|.
name|EMITTER_EMIT
operator|.
name|method
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|rows_
argument_list|,
name|rowStates_
argument_list|,
name|match_
argument_list|,
name|consumer_
argument_list|)
argument_list|,
name|builder
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Expression
name|implementMatcher
parameter_list|(
name|BlockBuilder
name|builder
parameter_list|,
name|ParameterExpression
name|row_
parameter_list|)
block|{
specifier|final
name|Expression
name|patternBuilder_
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"patternBuilder"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|PATTERN_BUILDER
operator|.
name|method
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|automaton_
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"automaton"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|implementPattern
argument_list|(
name|patternBuilder_
argument_list|,
name|pattern
argument_list|)
argument_list|,
name|BuiltInMethod
operator|.
name|PATTERN_TO_AUTOMATON
operator|.
name|method
argument_list|)
argument_list|)
decl_stmt|;
name|Expression
name|matcherBuilder_
init|=
name|builder
operator|.
name|append
argument_list|(
literal|"matcherBuilder"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|BuiltInMethod
operator|.
name|MATCHER_BUILDER
operator|.
name|method
argument_list|,
name|automaton_
argument_list|)
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|RexNode
argument_list|>
name|entry
range|:
name|patternDefinitions
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|Expression
name|predicate_
init|=
name|implementPredicate
argument_list|(
name|row_
argument_list|)
decl_stmt|;
name|matcherBuilder_
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|matcherBuilder_
argument_list|,
name|BuiltInMethod
operator|.
name|MATCHER_BUILDER_ADD
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
argument_list|,
name|predicate_
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
operator|.
name|append
argument_list|(
literal|"matcher"
argument_list|,
name|Expressions
operator|.
name|call
argument_list|(
name|matcherBuilder_
argument_list|,
name|BuiltInMethod
operator|.
name|MATCHER_BUILDER_BUILD
operator|.
name|method
argument_list|)
argument_list|)
return|;
block|}
comment|/** Generates code for a predicate. */
specifier|private
name|Expression
name|implementPredicate
parameter_list|(
name|ParameterExpression
name|row_
parameter_list|)
block|{
specifier|final
name|ParameterExpression
name|rows_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|List
operator|.
name|class
argument_list|,
literal|"rows"
argument_list|)
decl_stmt|;
comment|// "List<E> rows"
specifier|final
name|BlockBuilder
name|builder2
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|builder2
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
name|constant
argument_list|(
literal|true
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|MemberDeclaration
argument_list|>
name|memberDeclarations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Add a predicate method:
comment|//
comment|//   public boolean test(E row, List<E> rows) {
comment|//     return ...;
comment|//   }
name|memberDeclarations
operator|.
name|add
argument_list|(
name|EnumUtils
operator|.
name|overridingMethodDecl
argument_list|(
name|BuiltInMethod
operator|.
name|BI_PREDICATE_TEST
operator|.
name|method
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|row_
argument_list|,
name|rows_
argument_list|)
argument_list|,
name|builder2
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|EnumerableRules
operator|.
name|BRIDGE_METHODS
condition|)
block|{
comment|// Add a bridge method:
comment|//
comment|//   public boolean test(Object row, Object rows) {
comment|//     return this.test(row, (List) rows);
comment|//   }
specifier|final
name|ParameterExpression
name|rowsO_
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Object
operator|.
name|class
argument_list|,
literal|"rows"
argument_list|)
decl_stmt|;
name|BlockBuilder
name|bridgeBody
init|=
operator|new
name|BlockBuilder
argument_list|()
decl_stmt|;
name|bridgeBody
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
name|call
argument_list|(
name|Expressions
operator|.
name|parameter
argument_list|(
name|Comparable
operator|.
name|class
argument_list|,
literal|"this"
argument_list|)
argument_list|,
name|BuiltInMethod
operator|.
name|BI_PREDICATE_TEST
operator|.
name|method
argument_list|,
name|row_
argument_list|,
name|Expressions
operator|.
name|convert_
argument_list|(
name|rowsO_
argument_list|,
name|List
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|memberDeclarations
operator|.
name|add
argument_list|(
name|EnumUtils
operator|.
name|overridingMethodDecl
argument_list|(
name|BuiltInMethod
operator|.
name|BI_PREDICATE_TEST
operator|.
name|method
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|row_
argument_list|,
name|rowsO_
argument_list|)
argument_list|,
name|bridgeBody
operator|.
name|toBlock
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|Expressions
operator|.
name|new_
argument_list|(
name|Types
operator|.
name|of
argument_list|(
name|BiPredicate
operator|.
name|class
argument_list|)
argument_list|,
name|NO_EXPRS
argument_list|,
name|memberDeclarations
argument_list|)
return|;
block|}
comment|/** Generates code for a pattern.    *    *<p>For example, for the pattern {@code (A B)}, generates    * {@code patternBuilder.symbol("A").symbol("B").seq()}. */
specifier|private
name|Expression
name|implementPattern
parameter_list|(
name|Expression
name|patternBuilder_
parameter_list|,
name|RexNode
name|pattern
parameter_list|)
block|{
switch|switch
condition|(
name|pattern
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|LITERAL
case|:
specifier|final
name|String
name|symbol
init|=
operator|(
operator|(
name|RexLiteral
operator|)
name|pattern
operator|)
operator|.
name|getValueAs
argument_list|(
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|Expressions
operator|.
name|call
argument_list|(
name|patternBuilder_
argument_list|,
name|BuiltInMethod
operator|.
name|PATTERN_BUILDER_SYMBOL
operator|.
name|method
argument_list|,
name|Expressions
operator|.
name|constant
argument_list|(
name|symbol
argument_list|)
argument_list|)
return|;
case|case
name|PATTERN_CONCAT
case|:
specifier|final
name|RexCall
name|concat
init|=
operator|(
name|RexCall
operator|)
name|pattern
decl_stmt|;
for|for
control|(
name|Ord
argument_list|<
name|RexNode
argument_list|>
name|operand
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|concat
operator|.
name|operands
argument_list|)
control|)
block|{
name|patternBuilder_
operator|=
name|implementPattern
argument_list|(
name|patternBuilder_
argument_list|,
name|operand
operator|.
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|operand
operator|.
name|i
operator|>
literal|0
condition|)
block|{
name|patternBuilder_
operator|=
name|Expressions
operator|.
name|call
argument_list|(
name|patternBuilder_
argument_list|,
name|BuiltInMethod
operator|.
name|PATTERN_BUILDER_SEQ
operator|.
name|method
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|patternBuilder_
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown kind: "
operator|+
name|pattern
argument_list|)
throw|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End EnumerableMatch.java
end_comment

end_unit

