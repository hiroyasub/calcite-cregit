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
name|RelMultipleTrait
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Description of the physical distribution of a relational expression.  *  *<p>TBD:</p>  *<ul>  *<li>Can we shorten {@link Type#HASH_DISTRIBUTED} to HASH, etc.</li>  *<li>Do we need {@link RelDistributions}.DEFAULT?</li>  *<li>{@link RelDistributionTraitDef#convert}  *       does not create specific physical operators as it does in Drill. Drill  *       will need to create rules; or we could allow "converters" to be  *       registered with the planner that are not trait-defs.  *</ul>  */
end_comment

begin_interface
specifier|public
interface|interface
name|RelDistribution
extends|extends
name|RelMultipleTrait
block|{
comment|/** Returns the type of distribution. */
name|Type
name|getType
parameter_list|()
function_decl|;
comment|/**    * Returns the ordinals of the key columns.    *    *<p>Order is important for some types (RANGE); other types (HASH) consider    * it unimportant but impose an arbitrary order; other types (BROADCAST,    * SINGLETON) never have keys.    */
name|List
argument_list|<
name|Integer
argument_list|>
name|getKeys
parameter_list|()
function_decl|;
comment|/**    * Applies mapping to this distribution trait.    *    *<p>Mapping can change the distribution trait only if it depends on distribution keys.    *    *<p>For example if relation is HASH distributed by keys [0, 1], after applying    * a mapping (3, 2, 1, 0), the relation will have a distribution HASH(2,3) because    * distribution keys changed their ordinals.    *    *<p>If mapping eliminates one of the distribution keys, the {@link Type#ANY}    * distribution will be returned.    *    *<p>If distribution doesn't have keys (BROADCAST or SINGLETON), method will return    * the same distribution.    *    * @param mapping   Mapping    * @return distribution with mapping applied    */
annotation|@
name|Override
name|RelDistribution
name|apply
parameter_list|(
name|Mappings
operator|.
name|TargetMapping
name|mapping
parameter_list|)
function_decl|;
comment|/** Type of distribution. */
enum|enum
name|Type
block|{
comment|/** There is only one instance of the stream. It sees all records. */
name|SINGLETON
argument_list|(
literal|"single"
argument_list|)
block|,
comment|/** There are multiple instances of the stream, and each instance contains      * records whose keys hash to a particular hash value. Instances are      * disjoint; a given record appears on exactly one stream. */
name|HASH_DISTRIBUTED
argument_list|(
literal|"hash"
argument_list|)
block|,
comment|/** There are multiple instances of the stream, and each instance contains      * records whose keys fall into a particular range. Instances are disjoint;      * a given record appears on exactly one stream. */
name|RANGE_DISTRIBUTED
argument_list|(
literal|"range"
argument_list|)
block|,
comment|/** There are multiple instances of the stream, and each instance contains      * randomly chosen records. Instances are disjoint; a given record appears      * on exactly one stream. */
name|RANDOM_DISTRIBUTED
argument_list|(
literal|"random"
argument_list|)
block|,
comment|/** There are multiple instances of the stream, and records are assigned      * to instances in turn. Instances are disjoint; a given record appears      * on exactly one stream. */
name|ROUND_ROBIN_DISTRIBUTED
argument_list|(
literal|"rr"
argument_list|)
block|,
comment|/** There are multiple instances of the stream, and all records appear in      * each instance. */
name|BROADCAST_DISTRIBUTED
argument_list|(
literal|"broadcast"
argument_list|)
block|,
comment|/** Not a valid distribution, but indicates that a consumer will accept any      * distribution. */
name|ANY
argument_list|(
literal|"any"
argument_list|)
block|;
specifier|public
specifier|final
name|String
name|shortName
decl_stmt|;
name|Type
parameter_list|(
name|String
name|shortName
parameter_list|)
block|{
name|this
operator|.
name|shortName
operator|=
name|shortName
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

