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
name|rex
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
comment|/**  * Shuttle which applies a permutation to its input fields.  *  * @see RexPermutationShuttle  * @see RexUtil#apply(org.apache.calcite.util.mapping.Mappings.TargetMapping, RexNode)  */
end_comment

begin_class
specifier|public
class|class
name|RexPermuteInputsShuttle
extends|extends
name|RexShuttle
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Mappings
operator|.
name|TargetMapping
name|mapping
decl_stmt|;
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a RexPermuteInputsShuttle.    *    *<p>The mapping provides at most one target for every source. If a source    * has no targets and is referenced in the expression,    * {@link org.apache.calcite.util.mapping.Mappings.TargetMapping#getTarget(int)}    * will give an error. Otherwise the mapping gives a unique target.    *    * @param mapping Mapping    * @param inputs  Input relational expressions    */
specifier|public
name|RexPermuteInputsShuttle
parameter_list|(
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|,
name|RelNode
modifier|...
name|inputs
parameter_list|)
block|{
name|this
argument_list|(
name|mapping
argument_list|,
name|fields
argument_list|(
name|inputs
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|RexPermuteInputsShuttle
parameter_list|(
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|,
name|ImmutableList
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|)
block|{
name|this
operator|.
name|mapping
operator|=
name|mapping
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
block|}
comment|/** Creates a shuttle with an empty field list. It cannot handle GET calls but    * otherwise works OK. */
specifier|public
specifier|static
name|RexPermuteInputsShuttle
name|of
parameter_list|(
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
block|{
return|return
operator|new
name|RexPermuteInputsShuttle
argument_list|(
name|mapping
argument_list|,
name|ImmutableList
operator|.
expr|<
name|RelDataTypeField
operator|>
name|of
argument_list|()
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|private
specifier|static
name|ImmutableList
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|(
name|RelNode
index|[]
name|inputs
parameter_list|)
block|{
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
for|for
control|(
name|RelNode
name|input
range|:
name|inputs
control|)
block|{
name|fields
operator|.
name|addAll
argument_list|(
name|input
operator|.
name|getRowType
argument_list|()
operator|.
name|getFieldList
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|visitInputRef
parameter_list|(
name|RexInputRef
name|local
parameter_list|)
block|{
specifier|final
name|int
name|index
init|=
name|local
operator|.
name|getIndex
argument_list|()
decl_stmt|;
name|int
name|target
init|=
name|mapping
operator|.
name|getTarget
argument_list|(
name|index
argument_list|)
decl_stmt|;
return|return
operator|new
name|RexInputRef
argument_list|(
name|target
argument_list|,
name|local
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RexNode
name|visitCall
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
if|if
condition|(
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|RexBuilder
operator|.
name|GET_OPERATOR
condition|)
block|{
specifier|final
name|String
name|name
init|=
operator|(
name|String
operator|)
operator|(
operator|(
name|RexLiteral
operator|)
name|call
operator|.
name|getOperands
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getValue2
argument_list|()
decl_stmt|;
specifier|final
name|int
name|i
init|=
name|lookup
argument_list|(
name|fields
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|RexInputRef
operator|.
name|of
argument_list|(
name|i
argument_list|,
name|fields
argument_list|)
return|;
block|}
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
specifier|static
name|int
name|lookup
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
parameter_list|,
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|RelDataTypeField
name|field
init|=
name|fields
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|i
return|;
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
block|}
end_class

begin_comment
comment|// End RexPermuteInputsShuttle.java
end_comment

end_unit

