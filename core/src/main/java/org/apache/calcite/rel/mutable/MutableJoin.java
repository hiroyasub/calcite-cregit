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
name|mutable
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
name|RexNode
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
comment|/** Mutable equivalent of {@link org.apache.calcite.rel.core.Join}. */
end_comment

begin_class
specifier|public
class|class
name|MutableJoin
extends|extends
name|MutableBiRel
block|{
specifier|public
specifier|final
name|RexNode
name|condition
decl_stmt|;
specifier|public
specifier|final
name|Set
argument_list|<
name|CorrelationId
argument_list|>
name|variablesSet
decl_stmt|;
specifier|public
specifier|final
name|JoinRelType
name|joinType
decl_stmt|;
specifier|private
name|MutableJoin
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|MutableRel
name|left
parameter_list|,
name|MutableRel
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
name|CorrelationId
argument_list|>
name|variablesSet
parameter_list|)
block|{
name|super
argument_list|(
name|MutableRelType
operator|.
name|JOIN
argument_list|,
name|left
operator|.
name|cluster
argument_list|,
name|rowType
argument_list|,
name|left
argument_list|,
name|right
argument_list|)
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
name|this
operator|.
name|variablesSet
operator|=
name|variablesSet
expr_stmt|;
name|this
operator|.
name|joinType
operator|=
name|joinType
expr_stmt|;
block|}
comment|/**    * Creates a MutableJoin.    *    * @param rowType           Row type    * @param left              Left input relational expression    * @param right             Right input relational expression    * @param condition         Join condition    * @param joinType          Join type    * @param variablesStopped  Set of variables that are set by the LHS and    *                          used by the RHS and are not available to    *                          nodes above this join in the tree    */
specifier|public
specifier|static
name|MutableJoin
name|of
parameter_list|(
name|RelDataType
name|rowType
parameter_list|,
name|MutableRel
name|left
parameter_list|,
name|MutableRel
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
name|CorrelationId
argument_list|>
name|variablesStopped
parameter_list|)
block|{
return|return
operator|new
name|MutableJoin
argument_list|(
name|rowType
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
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|MutableJoin
operator|&&
name|joinType
operator|==
operator|(
operator|(
name|MutableJoin
operator|)
name|obj
operator|)
operator|.
name|joinType
operator|&&
name|condition
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableJoin
operator|)
name|obj
operator|)
operator|.
name|condition
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|variablesSet
argument_list|,
operator|(
operator|(
name|MutableJoin
operator|)
name|obj
operator|)
operator|.
name|variablesSet
argument_list|)
operator|&&
name|left
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableJoin
operator|)
name|obj
operator|)
operator|.
name|left
argument_list|)
operator|&&
name|right
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableJoin
operator|)
name|obj
operator|)
operator|.
name|right
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|left
argument_list|,
name|right
argument_list|,
name|condition
argument_list|,
name|joinType
argument_list|,
name|variablesSet
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringBuilder
name|digest
parameter_list|(
name|StringBuilder
name|buf
parameter_list|)
block|{
return|return
name|buf
operator|.
name|append
argument_list|(
literal|"Join(joinType: "
argument_list|)
operator|.
name|append
argument_list|(
name|joinType
argument_list|)
operator|.
name|append
argument_list|(
literal|", condition: "
argument_list|)
operator|.
name|append
argument_list|(
name|condition
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MutableRel
name|clone
parameter_list|()
block|{
return|return
name|MutableJoin
operator|.
name|of
argument_list|(
name|rowType
argument_list|,
name|left
operator|.
name|clone
argument_list|()
argument_list|,
name|right
operator|.
name|clone
argument_list|()
argument_list|,
name|condition
argument_list|,
name|joinType
argument_list|,
name|variablesSet
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End MutableJoin.java
end_comment

end_unit

