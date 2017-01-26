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
name|pig
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
name|core
operator|.
name|Join
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
name|JoinRelType
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
name|sql
operator|.
name|SqlKind
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
name|HashSet
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
comment|/** Implementation of {@link org.apache.calcite.rel.core.Join} in  * {@link PigRel#CONVENTION Pig calling convention}. */
end_comment

begin_class
specifier|public
class|class
name|PigJoin
extends|extends
name|Join
implements|implements
name|PigRel
block|{
comment|/** Creates a PigJoin. */
specifier|public
name|PigJoin
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
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
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
operator|new
name|HashSet
argument_list|<
name|CorrelationId
argument_list|>
argument_list|(
literal|0
argument_list|)
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
assert|assert
name|getConvention
argument_list|()
operator|==
name|PigRel
operator|.
name|CONVENTION
assert|;
block|}
annotation|@
name|Override
specifier|public
name|Join
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RexNode
name|conditionExpr
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|,
name|boolean
name|semiJoinDone
parameter_list|)
block|{
return|return
operator|new
name|PigJoin
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|conditionExpr
argument_list|,
name|joinType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|implement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getLeft
argument_list|()
argument_list|)
expr_stmt|;
name|implementor
operator|.
name|visitChild
argument_list|(
literal|0
argument_list|,
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
name|implementor
operator|.
name|addStatement
argument_list|(
name|getPigJoinStatement
argument_list|(
name|implementor
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * The Pig alias of the joined relation will have the same name as one from    * the left side of the join.    */
annotation|@
name|Override
specifier|public
name|RelOptTable
name|getTable
parameter_list|()
block|{
return|return
name|getLeft
argument_list|()
operator|.
name|getTable
argument_list|()
return|;
block|}
comment|/**    * Constructs a Pig JOIN statement in the form of    *<pre>    * {@code    * A = JOIN A BY f1 LEFT OUTER, B BY f2;    * }    *</pre>    * Only supports simple equi-joins with single column on both sides of    *<code>=</code>.    */
specifier|private
name|String
name|getPigJoinStatement
parameter_list|(
name|Implementor
name|implementor
parameter_list|)
block|{
if|if
condition|(
operator|!
name|getCondition
argument_list|()
operator|.
name|isA
argument_list|(
name|SqlKind
operator|.
name|EQUALS
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Only equi-join are supported"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|RexNode
argument_list|>
name|operands
init|=
operator|(
operator|(
name|RexCall
operator|)
name|getCondition
argument_list|()
operator|)
operator|.
name|getOperands
argument_list|()
decl_stmt|;
if|if
condition|(
name|operands
operator|.
name|size
argument_list|()
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Only equi-join are supported"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|Integer
argument_list|>
name|leftKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|rightKeys
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Boolean
argument_list|>
name|filterNulls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|RelOptUtil
operator|.
name|splitJoinCondition
argument_list|(
name|getLeft
argument_list|()
argument_list|,
name|getRight
argument_list|()
argument_list|,
name|getCondition
argument_list|()
argument_list|,
name|leftKeys
argument_list|,
name|rightKeys
argument_list|,
name|filterNulls
argument_list|)
expr_stmt|;
name|String
name|leftRelAlias
init|=
name|implementor
operator|.
name|getPigRelationAlias
argument_list|(
operator|(
name|PigRel
operator|)
name|getLeft
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|rightRelAlias
init|=
name|implementor
operator|.
name|getPigRelationAlias
argument_list|(
operator|(
name|PigRel
operator|)
name|getRight
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|leftJoinFieldName
init|=
name|implementor
operator|.
name|getFieldName
argument_list|(
operator|(
name|PigRel
operator|)
name|getLeft
argument_list|()
argument_list|,
name|leftKeys
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|rightJoinFieldName
init|=
name|implementor
operator|.
name|getFieldName
argument_list|(
operator|(
name|PigRel
operator|)
name|getRight
argument_list|()
argument_list|,
name|rightKeys
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|implementor
operator|.
name|getPigRelationAlias
argument_list|(
operator|(
name|PigRel
operator|)
name|getLeft
argument_list|()
argument_list|)
operator|+
literal|" = JOIN "
operator|+
name|leftRelAlias
operator|+
literal|" BY "
operator|+
name|leftJoinFieldName
operator|+
literal|' '
operator|+
name|getPigJoinType
argument_list|()
operator|+
literal|", "
operator|+
name|rightRelAlias
operator|+
literal|" BY "
operator|+
name|rightJoinFieldName
operator|+
literal|';'
return|;
block|}
comment|/**    * Get a string representation of the type of join for use in a Pig script.    * Pig does not have an explicit "inner" marker, so return an empty string in    * this case.    */
specifier|private
name|String
name|getPigJoinType
parameter_list|()
block|{
switch|switch
condition|(
name|getJoinType
argument_list|()
condition|)
block|{
case|case
name|INNER
case|:
return|return
literal|""
return|;
default|default:
return|return
name|getJoinType
argument_list|()
operator|.
name|name
argument_list|()
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End PigJoin.java
end_comment

end_unit

