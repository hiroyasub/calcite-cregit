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
name|RexUtil
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
name|ImmutableMap
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

begin_comment
comment|/**  * Predicates that are known to hold in the output of a particular relational  * expression.  *  *<p><b>Pulled up predicates</b> (field {@link #pulledUpPredicates} are  * predicates that apply to every row output by the relational expression. They  * are inferred from the input relational expression(s) and the relational  * operator.  *  *<p>For example, if you apply {@code Filter(x> 1)} to a relational  * expression that has a predicate {@code y< 10} then the pulled up predicates  * for the Filter are {@code [y< 10, x> 1]}.  *  *<p><b>Inferred predicates</b> only apply to joins. If there there is a  * predicate on the left input to a join, and that predicate is over columns  * used in the join condition, then a predicate can be inferred on the right  * input to the join. (And vice versa.)  *  *<p>For example, in the query  *<blockquote>SELECT *<br>  * FROM emp<br>  * JOIN dept ON emp.deptno = dept.deptno  * WHERE emp.gender = 'F' AND emp.deptno&lt; 10</blockquote>  * we have  *<ul>  *<li>left: {@code Filter(Scan(EMP), deptno< 10},  *       predicates: {@code [deptno< 10]}  *<li>right: {@code Scan(DEPT)}, predicates: {@code []}  *<li>join: {@code Join(left, right, emp.deptno = dept.deptno},  *      leftInferredPredicates: [],  *      rightInferredPredicates: [deptno&lt; 10],  *      pulledUpPredicates: [emp.gender = 'F', emp.deptno&lt; 10,  *      emp.deptno = dept.deptno, dept.deptno&lt; 10]  *</ul>  *  *<p>Note that the predicate from the left input appears in  * {@code rightInferredPredicates}. Predicates from several sources appear in  * {@code pulledUpPredicates}.  */
end_comment

begin_class
specifier|public
class|class
name|RelOptPredicateList
block|{
specifier|private
specifier|static
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|EMPTY_LIST
init|=
name|ImmutableList
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptPredicateList
name|EMPTY
init|=
operator|new
name|RelOptPredicateList
argument_list|(
name|EMPTY_LIST
argument_list|,
name|EMPTY_LIST
argument_list|,
name|EMPTY_LIST
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Predicates that can be pulled up from the relational expression and its    * inputs. */
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|pulledUpPredicates
decl_stmt|;
comment|/** Predicates that were inferred from the right input.    * Empty if the relational expression is not a join. */
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|leftInferredPredicates
decl_stmt|;
comment|/** Predicates that were inferred from the left input.    * Empty if the relational expression is not a join. */
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|rightInferredPredicates
decl_stmt|;
comment|/** A map of each (e, constant) pair that occurs within    * {@link #pulledUpPredicates}. */
specifier|public
specifier|final
name|ImmutableMap
argument_list|<
name|RexNode
argument_list|,
name|RexNode
argument_list|>
name|constantMap
decl_stmt|;
specifier|private
name|RelOptPredicateList
parameter_list|(
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|pulledUpPredicates
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|leftInferredPredicates
parameter_list|,
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|rightInferredPredicates
parameter_list|,
name|ImmutableMap
argument_list|<
name|RexNode
argument_list|,
name|RexNode
argument_list|>
name|constantMap
parameter_list|)
block|{
name|this
operator|.
name|pulledUpPredicates
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|pulledUpPredicates
argument_list|)
expr_stmt|;
name|this
operator|.
name|leftInferredPredicates
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|leftInferredPredicates
argument_list|)
expr_stmt|;
name|this
operator|.
name|rightInferredPredicates
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|rightInferredPredicates
argument_list|)
expr_stmt|;
name|this
operator|.
name|constantMap
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|constantMap
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a RelOptPredicateList with only pulled-up predicates, no inferred    * predicates.    *    *<p>Use this for relational expressions other than joins.    *    * @param pulledUpPredicates Predicates that apply to the rows returned by the    * relational expression    */
specifier|public
specifier|static
name|RelOptPredicateList
name|of
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|Iterable
argument_list|<
name|RexNode
argument_list|>
name|pulledUpPredicates
parameter_list|)
block|{
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|pulledUpPredicatesList
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|pulledUpPredicates
argument_list|)
decl_stmt|;
if|if
condition|(
name|pulledUpPredicatesList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
return|return
name|of
argument_list|(
name|rexBuilder
argument_list|,
name|pulledUpPredicatesList
argument_list|,
name|EMPTY_LIST
argument_list|,
name|EMPTY_LIST
argument_list|)
return|;
block|}
comment|/** Creates a RelOptPredicateList for a join.    *    * @param rexBuilder Rex builder    * @param pulledUpPredicates Predicates that apply to the rows returned by the    * relational expression    * @param leftInferredPredicates Predicates that were inferred from the right    *                               input    * @param rightInferredPredicates Predicates that were inferred from the left    *                                input    */
specifier|public
specifier|static
name|RelOptPredicateList
name|of
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|Iterable
argument_list|<
name|RexNode
argument_list|>
name|pulledUpPredicates
parameter_list|,
name|Iterable
argument_list|<
name|RexNode
argument_list|>
name|leftInferredPredicates
parameter_list|,
name|Iterable
argument_list|<
name|RexNode
argument_list|>
name|rightInferredPredicates
parameter_list|)
block|{
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|pulledUpPredicatesList
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|pulledUpPredicates
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|leftInferredPredicateList
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|leftInferredPredicates
argument_list|)
decl_stmt|;
specifier|final
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|rightInferredPredicatesList
init|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|rightInferredPredicates
argument_list|)
decl_stmt|;
if|if
condition|(
name|pulledUpPredicatesList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|leftInferredPredicateList
operator|.
name|isEmpty
argument_list|()
operator|&&
name|rightInferredPredicatesList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
specifier|final
name|ImmutableMap
argument_list|<
name|RexNode
argument_list|,
name|RexNode
argument_list|>
name|constantMap
init|=
name|RexUtil
operator|.
name|predicateConstants
argument_list|(
name|RexNode
operator|.
name|class
argument_list|,
name|rexBuilder
argument_list|,
name|pulledUpPredicatesList
argument_list|)
decl_stmt|;
return|return
operator|new
name|RelOptPredicateList
argument_list|(
name|pulledUpPredicatesList
argument_list|,
name|leftInferredPredicateList
argument_list|,
name|rightInferredPredicatesList
argument_list|,
name|constantMap
argument_list|)
return|;
block|}
specifier|public
name|RelOptPredicateList
name|union
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|RelOptPredicateList
name|list
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|EMPTY
condition|)
block|{
return|return
name|list
return|;
block|}
if|else if
condition|(
name|list
operator|==
name|EMPTY
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
name|RelOptPredicateList
operator|.
name|of
argument_list|(
name|rexBuilder
argument_list|,
name|concat
argument_list|(
name|pulledUpPredicates
argument_list|,
name|list
operator|.
name|pulledUpPredicates
argument_list|)
argument_list|,
name|concat
argument_list|(
name|leftInferredPredicates
argument_list|,
name|list
operator|.
name|leftInferredPredicates
argument_list|)
argument_list|,
name|concat
argument_list|(
name|rightInferredPredicates
argument_list|,
name|list
operator|.
name|rightInferredPredicates
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/** Concatenates two immutable lists, avoiding a copy it possible. */
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|ImmutableList
argument_list|<
name|E
argument_list|>
name|concat
parameter_list|(
name|ImmutableList
argument_list|<
name|E
argument_list|>
name|list1
parameter_list|,
name|ImmutableList
argument_list|<
name|E
argument_list|>
name|list2
parameter_list|)
block|{
if|if
condition|(
name|list1
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|list2
return|;
block|}
if|else if
condition|(
name|list2
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|list1
return|;
block|}
else|else
block|{
return|return
name|ImmutableList
operator|.
expr|<
name|E
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|list1
argument_list|)
operator|.
name|addAll
argument_list|(
name|list2
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
specifier|public
name|RelOptPredicateList
name|shift
parameter_list|(
name|RexBuilder
name|rexBuilder
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|RelOptPredicateList
operator|.
name|of
argument_list|(
name|rexBuilder
argument_list|,
name|RexUtil
operator|.
name|shift
argument_list|(
name|pulledUpPredicates
argument_list|,
name|offset
argument_list|)
argument_list|,
name|RexUtil
operator|.
name|shift
argument_list|(
name|leftInferredPredicates
argument_list|,
name|offset
argument_list|)
argument_list|,
name|RexUtil
operator|.
name|shift
argument_list|(
name|rightInferredPredicates
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

