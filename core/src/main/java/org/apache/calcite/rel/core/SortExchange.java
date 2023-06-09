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
name|core
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
name|RelDistribution
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
name|RelWriter
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
comment|/**  * Relational expression that performs {@link Exchange} and {@link Sort}  * simultaneously.  *  *<p>Whereas a Sort produces output with a particular  * {@link org.apache.calcite.rel.RelCollation} and an Exchange produces output  * with a particular {@link org.apache.calcite.rel.RelDistribution}, the output  * of a SortExchange has both the required collation and distribution.  *  *<p>Several implementations of SortExchange are possible; the purpose of this  * base class allows rules to be written that apply to all of those  * implementations.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SortExchange
extends|extends
name|Exchange
block|{
specifier|protected
specifier|final
name|RelCollation
name|collation
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SortExchange.    *    * @param cluster   Cluster this relational expression belongs to    * @param traitSet  Trait set    * @param input     Input relational expression    * @param distribution Distribution specification    */
specifier|protected
name|SortExchange
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
name|RelDistribution
name|distribution
parameter_list|,
name|RelCollation
name|collation
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
name|distribution
argument_list|)
expr_stmt|;
name|this
operator|.
name|collation
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|collation
argument_list|,
literal|"collation"
argument_list|)
expr_stmt|;
assert|assert
name|traitSet
operator|.
name|containsIfApplicable
argument_list|(
name|collation
argument_list|)
operator|:
literal|"traits="
operator|+
name|traitSet
operator|+
literal|", collation="
operator|+
name|collation
assert|;
block|}
comment|/**    * Creates a SortExchange by parsing serialized output.    */
specifier|protected
name|SortExchange
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
operator|.
name|plus
argument_list|(
name|input
operator|.
name|getCollation
argument_list|()
argument_list|)
operator|.
name|plus
argument_list|(
name|input
operator|.
name|getDistribution
argument_list|()
argument_list|)
argument_list|,
name|input
operator|.
name|getInput
argument_list|()
argument_list|,
name|RelDistributionTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|input
operator|.
name|getDistribution
argument_list|()
argument_list|)
argument_list|,
name|RelCollationTraitDef
operator|.
name|INSTANCE
operator|.
name|canonize
argument_list|(
name|input
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
specifier|final
name|SortExchange
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelDistribution
name|newDistribution
parameter_list|)
block|{
return|return
name|copy
argument_list|(
name|traitSet
argument_list|,
name|newInput
argument_list|,
name|newDistribution
argument_list|,
name|collation
argument_list|)
return|;
block|}
specifier|public
specifier|abstract
name|SortExchange
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|newInput
parameter_list|,
name|RelDistribution
name|newDistribution
parameter_list|,
name|RelCollation
name|newCollation
parameter_list|)
function_decl|;
comment|/**    * Returns the array of {@link org.apache.calcite.rel.RelFieldCollation}s    * asked for by the sort specification, from most significant to least    * significant.    *    *<p>See also    * {@link org.apache.calcite.rel.metadata.RelMetadataQuery#collations(RelNode)},    * which lists all known collations. For example,    *<code>ORDER BY time_id</code> might also be sorted by    *<code>the_year, the_month</code> because of a known monotonicity    * constraint among the columns. {@code getCollation} would return    *<code>[time_id]</code> and {@code collations} would return    *<code>[ [time_id], [the_year, the_month] ]</code>.</p>    */
specifier|public
name|RelCollation
name|getCollation
parameter_list|()
block|{
return|return
name|collation
return|;
block|}
annotation|@
name|Override
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
literal|"collation"
argument_list|,
name|collation
argument_list|)
return|;
block|}
block|}
end_class

end_unit

