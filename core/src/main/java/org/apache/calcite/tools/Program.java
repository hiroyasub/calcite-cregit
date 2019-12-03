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
name|tools
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
name|RelOptLattice
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
name|RelOptMaterialization
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
name|RelOptPlanner
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Program that transforms a relational expression into another relational  * expression.  *  *<p>A planner is a sequence of programs, each of which is sometimes called  * a "phase".  * The most typical program is an invocation of the volcano planner with a  * particular {@link org.apache.calcite.tools.RuleSet}.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Program
block|{
name|RelNode
name|run
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rel
parameter_list|,
name|RelTraitSet
name|requiredOutputTraits
parameter_list|,
name|List
argument_list|<
name|RelOptMaterialization
argument_list|>
name|materializations
parameter_list|,
name|List
argument_list|<
name|RelOptLattice
argument_list|>
name|lattices
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

