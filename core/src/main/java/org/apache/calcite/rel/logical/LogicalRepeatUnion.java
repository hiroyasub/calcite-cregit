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
name|linq4j
operator|.
name|function
operator|.
name|Experimental
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
name|RepeatUnion
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
comment|/**  * Sub-class of {@link org.apache.calcite.rel.core.RepeatUnion}  * not targeted at any particular engine or calling convention.  *  *<p>NOTE: The current API is experimental and subject to change without notice.</p>  */
end_comment

begin_class
annotation|@
name|Experimental
specifier|public
class|class
name|LogicalRepeatUnion
extends|extends
name|RepeatUnion
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|private
name|LogicalRepeatUnion
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelTraitSet
name|traitSet
parameter_list|,
name|RelNode
name|seed
parameter_list|,
name|RelNode
name|iterative
parameter_list|,
name|boolean
name|all
parameter_list|,
name|int
name|maxRep
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|seed
argument_list|,
name|iterative
argument_list|,
name|all
argument_list|,
name|maxRep
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a LogicalRepeatUnion. */
specifier|public
specifier|static
name|LogicalRepeatUnion
name|create
parameter_list|(
name|RelNode
name|seed
parameter_list|,
name|RelNode
name|iterative
parameter_list|,
name|boolean
name|all
parameter_list|)
block|{
return|return
name|create
argument_list|(
name|seed
argument_list|,
name|iterative
argument_list|,
name|all
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/** Creates a LogicalRepeatUnion. */
specifier|public
specifier|static
name|LogicalRepeatUnion
name|create
parameter_list|(
name|RelNode
name|seed
parameter_list|,
name|RelNode
name|iterative
parameter_list|,
name|boolean
name|all
parameter_list|,
name|int
name|maxRep
parameter_list|)
block|{
name|RelOptCluster
name|cluster
init|=
name|seed
operator|.
name|getCluster
argument_list|()
decl_stmt|;
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
name|LogicalRepeatUnion
argument_list|(
name|cluster
argument_list|,
name|traitSet
argument_list|,
name|seed
argument_list|,
name|iterative
argument_list|,
name|all
argument_list|,
name|maxRep
argument_list|)
return|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|LogicalRepeatUnion
name|copy
parameter_list|(
name|RelTraitSet
name|traitSet
parameter_list|,
name|List
argument_list|<
name|RelNode
argument_list|>
name|inputs
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
assert|assert
name|inputs
operator|.
name|size
argument_list|()
operator|==
literal|2
assert|;
return|return
operator|new
name|LogicalRepeatUnion
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|traitSet
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|inputs
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|all
argument_list|,
name|maxRep
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End LogicalRepeatUnion.java
end_comment

end_unit
