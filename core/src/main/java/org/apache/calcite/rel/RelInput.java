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
name|core
operator|.
name|AggregateCall
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
name|util
operator|.
name|ImmutableBitSet
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
comment|/**  * Context from which a relational expression can initialize itself,  * reading from a serialized form of the relational expression.  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelInput
block|{
name|RelOptCluster
name|getCluster
parameter_list|()
function_decl|;
name|RelTraitSet
name|getTraitSet
parameter_list|()
function_decl|;
name|RelOptTable
name|getTable
parameter_list|(
name|String
name|table
parameter_list|)
function_decl|;
comment|/**    * Returns the input relational expression. Throws if there is not precisely    * one input.    */
name|RelNode
name|getInput
parameter_list|()
function_decl|;
name|List
argument_list|<
name|RelNode
argument_list|>
name|getInputs
parameter_list|()
function_decl|;
comment|/**    * Returns an expression.    */
name|RexNode
name|getExpression
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|ImmutableBitSet
name|getBitSet
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|List
argument_list|<
name|ImmutableBitSet
argument_list|>
name|getBitSetList
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|List
argument_list|<
name|AggregateCall
argument_list|>
name|getAggregateCalls
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|Object
name|get
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
comment|/**    * Returns a {@code float} value. Throws if wrong type.    */
name|String
name|getString
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
comment|/**    * Returns a {@code float} value. Throws if not present or wrong type.    */
name|float
name|getFloat
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
comment|/**    * Returns an enum value. Throws if not a valid member.    */
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|E
name|getEnum
parameter_list|(
name|String
name|tag
parameter_list|,
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
parameter_list|)
function_decl|;
name|List
argument_list|<
name|RexNode
argument_list|>
name|getExpressionList
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|getStringList
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|List
argument_list|<
name|Integer
argument_list|>
name|getIntegerList
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|List
argument_list|<
name|List
argument_list|<
name|Integer
argument_list|>
argument_list|>
name|getIntegerListList
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|RelDataType
name|getRowType
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|RelDataType
name|getRowType
parameter_list|(
name|String
name|expressionsTag
parameter_list|,
name|String
name|fieldsTag
parameter_list|)
function_decl|;
name|RelCollation
name|getCollation
parameter_list|()
function_decl|;
name|RelDistribution
name|getDistribution
parameter_list|()
function_decl|;
name|ImmutableList
argument_list|<
name|ImmutableList
argument_list|<
name|RexLiteral
argument_list|>
argument_list|>
name|getTuples
parameter_list|(
name|String
name|tag
parameter_list|)
function_decl|;
name|boolean
name|getBoolean
parameter_list|(
name|String
name|tag
parameter_list|,
name|boolean
name|default_
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End RelInput.java
end_comment

end_unit

