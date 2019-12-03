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
name|rules
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
name|RelFactories
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
name|logical
operator|.
name|LogicalJoin
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
name|tools
operator|.
name|RelBuilderFactory
import|;
end_import

begin_comment
comment|/**  * Rule to convert an  * {@link org.apache.calcite.rel.logical.LogicalJoin inner join} to a  * {@link org.apache.calcite.rel.logical.LogicalFilter filter} on top of a  * {@link org.apache.calcite.rel.logical.LogicalJoin cartesian inner join}.  *  *<p>One benefit of this transformation is that after it, the join condition  * can be combined with conditions and expressions above the join. It also makes  * the<code>FennelCartesianJoinRule</code> applicable.  *  *<p>The constructor is parameterized to allow any sub-class of  * {@link org.apache.calcite.rel.core.Join}, not just  * {@link org.apache.calcite.rel.logical.LogicalJoin}.</p>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|JoinExtractFilterRule
extends|extends
name|AbstractJoinExtractFilterRule
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** The singleton. */
specifier|public
specifier|static
specifier|final
name|JoinExtractFilterRule
name|INSTANCE
init|=
operator|new
name|JoinExtractFilterRule
argument_list|(
name|LogicalJoin
operator|.
name|class
argument_list|,
name|RelFactories
operator|.
name|LOGICAL_BUILDER
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a JoinExtractFilterRule.    */
specifier|public
name|JoinExtractFilterRule
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Join
argument_list|>
name|clazz
parameter_list|,
name|RelBuilderFactory
name|relBuilderFactory
parameter_list|)
block|{
name|super
argument_list|(
name|operand
argument_list|(
name|clazz
argument_list|,
name|any
argument_list|()
argument_list|)
argument_list|,
name|relBuilderFactory
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
block|}
end_class

end_unit

