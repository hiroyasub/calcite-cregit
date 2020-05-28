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
name|logical
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
name|config
operator|.
name|CalciteSystemProperty
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
name|Convention
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
name|RelInput
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
name|RelShuttle
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
name|Correlate
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
name|Litmus
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Objects
operator|.
name|requireNonNull
import|;
end_import

begin_comment
comment|/**  * A relational operator that performs nested-loop joins.  *  *<p>It behaves like a kind of {@link org.apache.calcite.rel.core.Join},  * but works by setting variables in its environment and restarting its  * right-hand input.  *  *<p>A LogicalCorrelate is used to represent a correlated query. One  * implementation strategy is to de-correlate the expression.  *  * @see org.apache.calcite.rel.core.CorrelationId  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|LogicalCorrelate
extends|extends
name|Correlate
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a LogicalCorrelate.    * @param cluster      cluster this relational expression belongs to    * @param left         left input relational expression    * @param right        right input relational expression    * @param correlationId variable name for the row of left input    * @param requiredColumns Required columns    * @param joinType     join type    */
specifier|public
name|LogicalCorrelate
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
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
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
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
assert|assert
operator|!
name|CalciteSystemProperty
operator|.
name|DEBUG
operator|.
name|value
argument_list|()
operator|||
name|isValid
argument_list|(
name|Litmus
operator|.
name|THROW
argument_list|,
literal|null
argument_list|)
assert|;
block|}
comment|/**    * Creates a LogicalCorrelate by parsing serialized output.    */
specifier|public
name|LogicalCorrelate
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
name|this
argument_list|(
name|input
operator|.
name|getCluster
argument_list|()
argument_list|,
name|input
operator|.
name|getTraitSet
argument_list|()
argument_list|,
name|input
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|input
operator|.
name|getInputs
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
operator|new
name|CorrelationId
argument_list|(
operator|(
name|Integer
operator|)
name|requireNonNull
argument_list|(
name|input
operator|.
name|get
argument_list|(
literal|"correlation"
argument_list|)
argument_list|,
literal|"correlation"
argument_list|)
argument_list|)
argument_list|,
name|input
operator|.
name|getBitSet
argument_list|(
literal|"requiredColumns"
argument_list|)
argument_list|,
name|requireNonNull
argument_list|(
name|input
operator|.
name|getEnum
argument_list|(
literal|"joinType"
argument_list|,
name|JoinRelType
operator|.
name|class
argument_list|)
argument_list|,
literal|"joinType"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a LogicalCorrelate. */
specifier|public
specifier|static
name|LogicalCorrelate
name|create
parameter_list|(
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
specifier|final
name|RelOptCluster
name|cluster
init|=
name|left
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
name|Convention
operator|.
name|NONE
argument_list|)
decl_stmt|;
return|return
operator|new
name|LogicalCorrelate
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|LogicalCorrelate
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|CorrelationId
name|correlationId
parameter_list|,
name|ImmutableBitSet
name|requiredColumns
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
assert|assert
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|Convention
operator|.
name|NONE
argument_list|)
assert|;
return|return
operator|new
name|LogicalCorrelate
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
name|correlationId
argument_list|,
name|requiredColumns
argument_list|,
name|joinType
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|accept
parameter_list|(
name|RelShuttle
name|shuttle
parameter_list|)
block|{
return|return
name|shuttle
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

