begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|rex
operator|.
name|*
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

begin_comment
comment|/**  * A<code>CorrelatorRel</code> behaves like a kind of {@link JoinRel}, but  * works by setting variables in its environment and restarting its right-hand  * input.  *  *<p>A CorrelatorRel is used to represent a correlated query. One  * implementation strategy is to de-correlate the expression.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|CorrelatorRel
extends|extends
name|JoinRelBase
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|ImmutableList
argument_list|<
name|Correlation
argument_list|>
name|correlations
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a CorrelatorRel.    *    * @param cluster      cluster this relational expression belongs to    * @param left         left input relational expression    * @param right        right input relational expression    * @param joinCond     join condition    * @param correlations set of expressions to set as variables each time a    *                     row arrives from the left input    * @param joinType     join type    */
specifier|public
name|CorrelatorRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|RexNode
name|joinCond
parameter_list|,
name|List
argument_list|<
name|Correlation
argument_list|>
name|correlations
parameter_list|,
name|JoinRelType
name|joinType
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
name|Convention
operator|.
name|NONE
argument_list|)
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|joinCond
argument_list|,
name|joinType
argument_list|,
name|ImmutableSet
operator|.
expr|<
name|String
operator|>
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|correlations
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|correlations
argument_list|)
expr_stmt|;
assert|assert
operator|(
name|joinType
operator|==
name|JoinRelType
operator|.
name|LEFT
operator|)
operator|||
operator|(
name|joinType
operator|==
name|JoinRelType
operator|.
name|INNER
operator|)
assert|;
block|}
comment|/**    * Creates a CorrelatorRel with no join condition.    *    * @param cluster      cluster this relational expression belongs to    * @param left         left input relational expression    * @param right        right input relational expression    * @param correlations set of expressions to set as variables each time a    *                     row arrives from the left input    * @param joinType     join type    */
specifier|public
name|CorrelatorRel
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|left
parameter_list|,
name|RelNode
name|right
parameter_list|,
name|List
argument_list|<
name|Correlation
argument_list|>
name|correlations
parameter_list|,
name|JoinRelType
name|joinType
parameter_list|)
block|{
name|this
argument_list|(
name|cluster
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|cluster
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeLiteral
argument_list|(
literal|true
argument_list|)
argument_list|,
name|correlations
argument_list|,
name|joinType
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a CorrelatorRel by parsing serialized output.    */
specifier|public
name|CorrelatorRel
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
name|getCorrelations
argument_list|(
name|input
argument_list|)
argument_list|,
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
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|List
argument_list|<
name|Correlation
argument_list|>
name|getCorrelations
parameter_list|(
name|RelInput
name|input
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Correlation
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|Correlation
argument_list|>
argument_list|()
decl_stmt|;
comment|//noinspection unchecked
specifier|final
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|correlations1
init|=
operator|(
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
operator|)
name|input
operator|.
name|get
argument_list|(
literal|"correlations"
argument_list|)
decl_stmt|;
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|correlation
range|:
name|correlations1
control|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|Correlation
argument_list|(
operator|(
name|Integer
operator|)
name|correlation
operator|.
name|get
argument_list|(
literal|"correlation"
argument_list|)
argument_list|,
operator|(
name|Integer
operator|)
name|correlation
operator|.
name|get
argument_list|(
literal|"offset"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|CorrelatorRel
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
name|CorrelatorRel
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|correlations
argument_list|,
name|this
operator|.
name|joinType
argument_list|)
return|;
block|}
specifier|public
name|RelWriter
name|explainTerms
parameter_list|(
name|RelWriter
name|pw
parameter_list|)
block|{
return|return
name|super
operator|.
name|explainTerms
argument_list|(
name|pw
argument_list|)
operator|.
name|item
argument_list|(
literal|"correlations"
argument_list|,
name|correlations
argument_list|)
return|;
block|}
comment|/**    * Returns the correlating expressions.    *    * @return correlating expressions    */
specifier|public
name|List
argument_list|<
name|Correlation
argument_list|>
name|getCorrelations
parameter_list|()
block|{
return|return
name|correlations
return|;
block|}
comment|//~ Inner Classes ----------------------------------------------------------
comment|/**    * Describes the neccessary parameters for an implementation in order to    * identify and set dynamic variables    */
specifier|public
specifier|static
class|class
name|Correlation
implements|implements
name|Cloneable
implements|,
name|Comparable
argument_list|<
name|Correlation
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
specifier|private
specifier|final
name|int
name|offset
decl_stmt|;
comment|/**      * Creates a correlation.      *      * @param id     Identifier      * @param offset Offset      */
specifier|public
name|Correlation
parameter_list|(
name|int
name|id
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
comment|/**      * Returns the identifier.      *      * @return identifier      */
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Returns this correlation's offset.      *      * @return offset      */
specifier|public
name|int
name|getOffset
parameter_list|()
block|{
return|return
name|offset
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"var"
operator|+
name|id
operator|+
literal|"=offset"
operator|+
name|offset
return|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Correlation
name|other
parameter_list|)
block|{
return|return
name|id
operator|-
name|other
operator|.
name|id
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CorrelatorRel.java
end_comment

end_unit

