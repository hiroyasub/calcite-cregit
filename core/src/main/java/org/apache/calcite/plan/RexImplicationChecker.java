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
name|RexExecutable
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
name|RexExecutorImpl
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
name|SqlKind
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
name|SqlOperator
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
name|fun
operator|.
name|SqlCastFunction
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
name|trace
operator|.
name|CalciteLogger
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|HashMap
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
name|Set
import|;
end_import

begin_comment
comment|/**  * Checks whether one condition logically implies another.  *  *<p>If A&rArr; B, whenever A is true, B will be true also.  *  *<p>For example:  *<ul>  *<li>(x&gt; 10)&rArr; (x&gt; 5)  *<li>(y = 10)&rArr; (y&lt; 30 OR x&gt; 30)  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|RexImplicationChecker
block|{
specifier|private
specifier|static
specifier|final
name|CalciteLogger
name|LOGGER
init|=
operator|new
name|CalciteLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RexImplicationChecker
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RexBuilder
name|builder
decl_stmt|;
specifier|final
name|RexExecutorImpl
name|executor
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|public
name|RexImplicationChecker
parameter_list|(
name|RexBuilder
name|builder
parameter_list|,
name|RexExecutorImpl
name|executor
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|this
operator|.
name|builder
operator|=
name|builder
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
block|}
comment|/**    * Checks if condition first implies (&rArr;) condition second.    *    *<p>This reduces to SAT problem which is NP-Complete.    * When this method says first implies second then it is definitely true.    * But it cannot prove that first does not imply second.    *    * @param first first condition    * @param second second condition    * @return true if it can prove first&rArr; second; otherwise false i.e.,    * it doesn't know if implication holds    */
specifier|public
name|boolean
name|implies
parameter_list|(
name|RexNode
name|first
parameter_list|,
name|RexNode
name|second
parameter_list|)
block|{
comment|// Validation
if|if
condition|(
operator|!
name|validate
argument_list|(
name|first
argument_list|,
name|second
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"Checking if {} => {}"
argument_list|,
name|first
operator|.
name|toString
argument_list|()
argument_list|,
name|second
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|RexCall
name|firstCond
init|=
operator|(
name|RexCall
operator|)
name|first
decl_stmt|;
name|RexCall
name|secondCond
init|=
operator|(
name|RexCall
operator|)
name|second
decl_stmt|;
comment|// Get DNF
name|RexNode
name|firstDnf
init|=
name|RexUtil
operator|.
name|toDnf
argument_list|(
name|builder
argument_list|,
name|first
argument_list|)
decl_stmt|;
name|RexNode
name|secondDnf
init|=
name|RexUtil
operator|.
name|toDnf
argument_list|(
name|builder
argument_list|,
name|second
argument_list|)
decl_stmt|;
comment|// Check Trivial Cases
if|if
condition|(
name|firstDnf
operator|.
name|isAlwaysFalse
argument_list|()
operator|||
name|secondDnf
operator|.
name|isAlwaysTrue
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
comment|/** Decomposes DNF into List of Conjunctions.      *      *<p>For example,      * {@code x> 10 AND y> 30) OR (z> 90)}      * will be converted to      * list of 2 conditions:      *      *<ul>      *<li>(x> 10 AND y> 30)</li>      *<li>z> 90</li>      *</ul>      */
name|List
argument_list|<
name|RexNode
argument_list|>
name|firstDnfs
init|=
name|RelOptUtil
operator|.
name|disjunctions
argument_list|(
name|firstDnf
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|secondDnfs
init|=
name|RelOptUtil
operator|.
name|disjunctions
argument_list|(
name|secondDnf
argument_list|)
decl_stmt|;
for|for
control|(
name|RexNode
name|f
range|:
name|firstDnfs
control|)
block|{
if|if
condition|(
operator|!
name|f
operator|.
name|isAlwaysFalse
argument_list|()
condition|)
block|{
comment|// Check if f implies at least
comment|// one of the conjunctions in list secondDnfs
name|boolean
name|implyOneConjunction
init|=
literal|false
decl_stmt|;
for|for
control|(
name|RexNode
name|s
range|:
name|secondDnfs
control|)
block|{
if|if
condition|(
name|s
operator|.
name|isAlwaysFalse
argument_list|()
condition|)
block|{
comment|// f cannot imply s
continue|continue;
block|}
if|if
condition|(
name|impliesConjunction
argument_list|(
name|f
argument_list|,
name|s
argument_list|)
condition|)
block|{
comment|// Satisfies one of the condition, so lets
comment|// move to next conjunction in firstDnfs
name|implyOneConjunction
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
comment|// If f could not imply even one conjunction in
comment|// secondDnfs, then final implication may be false
if|if
condition|(
operator|!
name|implyOneConjunction
condition|)
block|{
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"{} doesnot imply {}"
argument_list|,
name|first
argument_list|,
name|second
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
name|LOGGER
operator|.
name|debug
argument_list|(
literal|"{} implies {}"
argument_list|,
name|first
argument_list|,
name|second
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/** Returns whether first implies second (both are conjunctions). */
specifier|private
name|boolean
name|impliesConjunction
parameter_list|(
name|RexNode
name|first
parameter_list|,
name|RexNode
name|second
parameter_list|)
block|{
specifier|final
name|InputUsageFinder
name|firstUsageFinder
init|=
operator|new
name|InputUsageFinder
argument_list|()
decl_stmt|;
specifier|final
name|InputUsageFinder
name|secondUsageFinder
init|=
operator|new
name|InputUsageFinder
argument_list|()
decl_stmt|;
name|RexUtil
operator|.
name|apply
argument_list|(
name|firstUsageFinder
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
argument_list|,
name|first
argument_list|)
expr_stmt|;
name|RexUtil
operator|.
name|apply
argument_list|(
name|secondUsageFinder
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|RexNode
argument_list|>
argument_list|()
argument_list|,
name|second
argument_list|)
expr_stmt|;
comment|// Check Support
if|if
condition|(
operator|!
name|checkSupport
argument_list|(
name|firstUsageFinder
argument_list|,
name|secondUsageFinder
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Support for checking {} => {} is not there"
argument_list|,
name|first
argument_list|,
name|second
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Set
argument_list|<
name|Pair
argument_list|<
name|RexInputRef
argument_list|,
name|RexNode
argument_list|>
argument_list|>
argument_list|>
name|usagesBuilder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RexInputRef
argument_list|,
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|entry
range|:
name|firstUsageFinder
operator|.
name|usageMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ImmutableSet
operator|.
name|Builder
argument_list|<
name|Pair
argument_list|<
name|RexInputRef
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|usageBuilder
init|=
name|ImmutableSet
operator|.
name|builder
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|usageList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
specifier|final
name|Pair
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
name|pair
range|:
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|usageList
control|)
block|{
name|usageBuilder
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|pair
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|usagesBuilder
operator|.
name|add
argument_list|(
name|usageBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|Set
argument_list|<
name|List
argument_list|<
name|Pair
argument_list|<
name|RexInputRef
argument_list|,
name|RexNode
argument_list|>
argument_list|>
argument_list|>
name|usages
init|=
name|Sets
operator|.
name|cartesianProduct
argument_list|(
name|usagesBuilder
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|List
name|usageList
range|:
name|usages
control|)
block|{
comment|// Get the literals from first conjunction and executes second conjunction
comment|// using them.
comment|//
comment|// E.g., for
comment|//   x> 30&rArr; x> 10,
comment|// we will replace x by 30 in second expression and execute it i.e.,
comment|//   30> 10
comment|//
comment|// If it's true then we infer implication.
specifier|final
name|DataContext
name|dataValues
init|=
name|VisitorDataContext
operator|.
name|of
argument_list|(
name|rowType
argument_list|,
name|usageList
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isSatisfiable
argument_list|(
name|second
argument_list|,
name|dataValues
argument_list|)
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
specifier|private
name|boolean
name|isSatisfiable
parameter_list|(
name|RexNode
name|second
parameter_list|,
name|DataContext
name|dataValues
parameter_list|)
block|{
if|if
condition|(
name|dataValues
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ImmutableList
argument_list|<
name|RexNode
argument_list|>
name|constExps
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|second
argument_list|)
decl_stmt|;
specifier|final
name|RexExecutable
name|exec
init|=
name|executor
operator|.
name|getExecutable
argument_list|(
name|builder
argument_list|,
name|constExps
argument_list|,
name|rowType
argument_list|)
decl_stmt|;
name|Object
index|[]
name|result
decl_stmt|;
name|exec
operator|.
name|setDataContext
argument_list|(
name|dataValues
argument_list|)
expr_stmt|;
try|try
block|{
name|result
operator|=
name|exec
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// TODO: CheckSupport should not allow this exception to be thrown
comment|// Need to monitor it and handle all the cases raising them.
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"Exception thrown while checking if => {}: {}"
argument_list|,
name|second
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
name|result
operator|!=
literal|null
operator|&&
name|result
operator|.
name|length
operator|==
literal|1
operator|&&
name|result
index|[
literal|0
index|]
operator|instanceof
name|Boolean
operator|&&
operator|(
name|Boolean
operator|)
name|result
index|[
literal|0
index|]
return|;
block|}
comment|/**    * Looks at the usage of variables in first and second conjunction to decide    * whether this kind of expression is currently supported for proving first    * implies second.    *    *<ol>    *<li>Variables should be used only once in both the conjunction against    * given set of operations only:&gt;,&lt;,&le;,&ge;, =;&ne;.    *    *<li>All the variables used in second condition should be used even in the    * first.    *    *<li>If operator used for variable in first is op1 and op2 for second, then    * we support these combination for conjunction (op1, op2) then op1, op2    * belongs to one of the following sets:    *    *<ul>    *<li>(&lt;,&le;) X (&lt;,&le;)<i>note: X represents cartesian product</i>    *<li>(&gt; /&ge;) X (&gt;,&ge;)    *<li>(=) X (&gt;,&ge;,&lt;,&le;, =,&ne;)    *<li>(&ne;, =)    *</ul>    *    *<li>We support at most 2 operators to be be used for a variable in first    * and second usages.    *    *</ol>    *    * @return whether input usage pattern is supported    */
specifier|private
name|boolean
name|checkSupport
parameter_list|(
name|InputUsageFinder
name|firstUsageFinder
parameter_list|,
name|InputUsageFinder
name|secondUsageFinder
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|RexInputRef
argument_list|,
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|firstUsageMap
init|=
name|firstUsageFinder
operator|.
name|usageMap
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|RexInputRef
argument_list|,
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|secondUsageMap
init|=
name|secondUsageFinder
operator|.
name|usageMap
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|RexInputRef
argument_list|,
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|entry
range|:
name|secondUsageMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
name|secondUsage
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|secondUsageList
init|=
name|secondUsage
operator|.
name|usageList
decl_stmt|;
specifier|final
name|int
name|secondLen
init|=
name|secondUsageList
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|secondUsage
operator|.
name|usageCount
operator|!=
name|secondLen
operator|||
name|secondLen
operator|>
literal|2
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
name|firstUsage
init|=
name|firstUsageMap
operator|.
name|get
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstUsage
operator|==
literal|null
operator|||
name|firstUsage
operator|.
name|usageList
operator|.
name|size
argument_list|()
operator|!=
name|firstUsage
operator|.
name|usageCount
operator|||
name|firstUsage
operator|.
name|usageCount
operator|>
literal|2
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|firstUsageList
init|=
name|firstUsage
operator|.
name|usageList
decl_stmt|;
specifier|final
name|int
name|firstLen
init|=
name|firstUsageList
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|SqlKind
name|fKind
init|=
name|firstUsageList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
operator|.
name|getKind
argument_list|()
decl_stmt|;
specifier|final
name|SqlKind
name|sKind
init|=
name|secondUsageList
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getKey
argument_list|()
operator|.
name|getKind
argument_list|()
decl_stmt|;
specifier|final
name|SqlKind
name|fKind2
init|=
operator|(
name|firstUsageList
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|)
condition|?
name|firstUsageList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getKey
argument_list|()
operator|.
name|getKind
argument_list|()
else|:
literal|null
decl_stmt|;
specifier|final
name|SqlKind
name|sKind2
init|=
operator|(
name|secondUsageList
operator|.
name|size
argument_list|()
operator|==
literal|2
operator|)
condition|?
name|secondUsageList
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getKey
argument_list|()
operator|.
name|getKind
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
name|firstLen
operator|==
literal|2
operator|&&
name|secondLen
operator|==
literal|2
operator|&&
operator|!
operator|(
name|isEquivalentOp
argument_list|(
name|fKind
argument_list|,
name|sKind
argument_list|)
operator|&&
name|isEquivalentOp
argument_list|(
name|fKind2
argument_list|,
name|sKind2
argument_list|)
operator|)
operator|&&
operator|!
operator|(
name|isEquivalentOp
argument_list|(
name|fKind
argument_list|,
name|sKind2
argument_list|)
operator|&&
name|isEquivalentOp
argument_list|(
name|fKind2
argument_list|,
name|sKind
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|firstLen
operator|==
literal|1
operator|&&
name|secondLen
operator|==
literal|1
operator|&&
name|fKind
operator|!=
name|SqlKind
operator|.
name|EQUALS
operator|&&
operator|!
name|isEquivalentOp
argument_list|(
name|fKind
argument_list|,
name|sKind
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|firstLen
operator|==
literal|1
operator|&&
name|secondLen
operator|==
literal|2
operator|&&
name|fKind
operator|!=
name|SqlKind
operator|.
name|EQUALS
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|firstLen
operator|==
literal|2
operator|&&
name|secondLen
operator|==
literal|1
condition|)
block|{
comment|// Allow only cases like
comment|// x< 30 and x< 40 implies x< 70
comment|// x> 30 and x< 40 implies x< 70
comment|// But disallow cases like
comment|// x> 30 and x> 40 implies x< 70
if|if
condition|(
operator|!
name|isOppositeOp
argument_list|(
name|fKind
argument_list|,
name|fKind2
argument_list|)
operator|&&
operator|!
operator|(
name|isEquivalentOp
argument_list|(
name|fKind
argument_list|,
name|fKind2
argument_list|)
operator|&&
name|isEquivalentOp
argument_list|(
name|fKind
argument_list|,
name|sKind
argument_list|)
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|isEquivalentOp
parameter_list|(
name|SqlKind
name|fKind
parameter_list|,
name|SqlKind
name|sKind
parameter_list|)
block|{
switch|switch
condition|(
name|sKind
condition|)
block|{
case|case
name|GREATER_THAN
case|:
case|case
name|GREATER_THAN_OR_EQUAL
case|:
if|if
condition|(
operator|!
operator|(
name|fKind
operator|==
name|SqlKind
operator|.
name|GREATER_THAN
operator|)
operator|&&
operator|!
operator|(
name|fKind
operator|==
name|SqlKind
operator|.
name|GREATER_THAN_OR_EQUAL
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
case|case
name|LESS_THAN
case|:
case|case
name|LESS_THAN_OR_EQUAL
case|:
if|if
condition|(
operator|!
operator|(
name|fKind
operator|==
name|SqlKind
operator|.
name|LESS_THAN
operator|)
operator|&&
operator|!
operator|(
name|fKind
operator|==
name|SqlKind
operator|.
name|LESS_THAN_OR_EQUAL
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
default|default:
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|isOppositeOp
parameter_list|(
name|SqlKind
name|fKind
parameter_list|,
name|SqlKind
name|sKind
parameter_list|)
block|{
switch|switch
condition|(
name|sKind
condition|)
block|{
case|case
name|GREATER_THAN
case|:
case|case
name|GREATER_THAN_OR_EQUAL
case|:
if|if
condition|(
operator|!
operator|(
name|fKind
operator|==
name|SqlKind
operator|.
name|LESS_THAN
operator|)
operator|&&
operator|!
operator|(
name|fKind
operator|==
name|SqlKind
operator|.
name|LESS_THAN_OR_EQUAL
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
case|case
name|LESS_THAN
case|:
case|case
name|LESS_THAN_OR_EQUAL
case|:
if|if
condition|(
operator|!
operator|(
name|fKind
operator|==
name|SqlKind
operator|.
name|GREATER_THAN
operator|)
operator|&&
operator|!
operator|(
name|fKind
operator|==
name|SqlKind
operator|.
name|GREATER_THAN_OR_EQUAL
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
break|break;
default|default:
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|validate
parameter_list|(
name|RexNode
name|first
parameter_list|,
name|RexNode
name|second
parameter_list|)
block|{
return|return
name|first
operator|instanceof
name|RexCall
operator|&&
name|second
operator|instanceof
name|RexCall
return|;
block|}
comment|/**    * Visitor that builds a usage map of inputs used by an expression.    *    *<p>E.g: for x&gt; 10 AND y&lt; 20 AND x = 40, usage map is as follows:    *<ul>    *<li>key: x value: {(&gt;, 10),(=, 40), usageCount = 2}    *<li>key: y value: {(&gt;, 20), usageCount = 1}    *</ul>    */
specifier|private
specifier|static
class|class
name|InputUsageFinder
extends|extends
name|RexVisitorImpl
argument_list|<
name|Void
argument_list|>
block|{
specifier|public
specifier|final
name|Map
argument_list|<
name|RexInputRef
argument_list|,
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
argument_list|>
name|usageMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|InputUsageFinder
parameter_list|()
block|{
name|super
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Void
name|visitInputRef
parameter_list|(
name|RexInputRef
name|inputRef
parameter_list|)
block|{
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
name|inputRefUse
init|=
name|getUsageMap
argument_list|(
name|inputRef
argument_list|)
decl_stmt|;
name|inputRefUse
operator|.
name|usageCount
operator|++
expr_stmt|;
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Void
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
switch|switch
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|GREATER_THAN
case|:
case|case
name|GREATER_THAN_OR_EQUAL
case|:
case|case
name|LESS_THAN
case|:
case|case
name|LESS_THAN_OR_EQUAL
case|:
case|case
name|EQUALS
case|:
case|case
name|NOT_EQUALS
case|:
name|updateUsage
argument_list|(
name|call
argument_list|)
expr_stmt|;
break|break;
default|default:
block|}
return|return
name|super
operator|.
name|visitCall
argument_list|(
name|call
argument_list|)
return|;
block|}
specifier|private
name|void
name|updateUsage
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperands
argument_list|()
decl_stmt|;
name|RexNode
name|first
init|=
name|removeCast
argument_list|(
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|RexNode
name|second
init|=
name|removeCast
argument_list|(
name|operands
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|INPUT_REF
argument_list|)
operator|&&
name|second
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|LITERAL
argument_list|)
condition|)
block|{
name|updateUsage
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
argument_list|,
operator|(
name|RexInputRef
operator|)
name|first
argument_list|,
name|second
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|first
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|LITERAL
argument_list|)
operator|&&
name|second
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|INPUT_REF
argument_list|)
condition|)
block|{
name|updateUsage
argument_list|(
name|reverse
argument_list|(
name|call
operator|.
name|getOperator
argument_list|()
argument_list|)
argument_list|,
operator|(
name|RexInputRef
operator|)
name|second
argument_list|,
name|first
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|SqlOperator
name|reverse
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
return|return
name|RelOptUtil
operator|.
name|op
argument_list|(
name|op
operator|.
name|getKind
argument_list|()
operator|.
name|reverse
argument_list|()
argument_list|,
name|op
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|RexNode
name|removeCast
parameter_list|(
name|RexNode
name|inputRef
parameter_list|)
block|{
if|if
condition|(
name|inputRef
operator|instanceof
name|RexCall
condition|)
block|{
specifier|final
name|RexCall
name|castedRef
init|=
operator|(
name|RexCall
operator|)
name|inputRef
decl_stmt|;
specifier|final
name|SqlOperator
name|operator
init|=
name|castedRef
operator|.
name|getOperator
argument_list|()
decl_stmt|;
if|if
condition|(
name|operator
operator|instanceof
name|SqlCastFunction
condition|)
block|{
name|inputRef
operator|=
name|castedRef
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|inputRef
return|;
block|}
specifier|private
name|void
name|updateUsage
parameter_list|(
name|SqlOperator
name|op
parameter_list|,
name|RexInputRef
name|inputRef
parameter_list|,
name|RexNode
name|literal
parameter_list|)
block|{
specifier|final
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
name|inputRefUse
init|=
name|getUsageMap
argument_list|(
name|inputRef
argument_list|)
decl_stmt|;
name|Pair
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
name|use
init|=
name|Pair
operator|.
name|of
argument_list|(
name|op
argument_list|,
name|literal
argument_list|)
decl_stmt|;
name|inputRefUse
operator|.
name|usageList
operator|.
name|add
argument_list|(
name|use
argument_list|)
expr_stmt|;
block|}
specifier|private
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
name|getUsageMap
parameter_list|(
name|RexInputRef
name|rex
parameter_list|)
block|{
name|InputRefUsage
argument_list|<
name|SqlOperator
argument_list|,
name|RexNode
argument_list|>
name|inputRefUse
init|=
name|usageMap
operator|.
name|get
argument_list|(
name|rex
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputRefUse
operator|==
literal|null
condition|)
block|{
name|inputRefUse
operator|=
operator|new
name|InputRefUsage
argument_list|<>
argument_list|()
expr_stmt|;
name|usageMap
operator|.
name|put
argument_list|(
name|rex
argument_list|,
name|inputRefUse
argument_list|)
expr_stmt|;
block|}
return|return
name|inputRefUse
return|;
block|}
block|}
comment|/**    * Usage of a {@link RexInputRef} in an expression.    */
specifier|private
specifier|static
class|class
name|InputRefUsage
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|>
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|T1
argument_list|,
name|T2
argument_list|>
argument_list|>
name|usageList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|usageCount
init|=
literal|0
decl_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End RexImplicationChecker.java
end_comment

end_unit

