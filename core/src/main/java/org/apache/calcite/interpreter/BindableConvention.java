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
name|interpreter
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
name|ConventionTraitDef
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
name|RelTrait
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
name|RelTraitDef
import|;
end_import

begin_comment
comment|/**  * Calling convention that returns results as an  * {@link org.apache.calcite.linq4j.Enumerable} of object arrays.  *  *<p>The relational expression needs to implement  * {@link org.apache.calcite.runtime.ArrayBindable}.  * Unlike {@link org.apache.calcite.adapter.enumerable.EnumerableConvention},  * no code generation is required.  */
end_comment

begin_enum
specifier|public
enum|enum
name|BindableConvention
implements|implements
name|Convention
block|{
name|INSTANCE
block|;
comment|/** Cost of a bindable node versus implementing an equivalent node in a    * "typical" calling convention. */
specifier|public
specifier|static
specifier|final
name|double
name|COST_MULTIPLIER
init|=
literal|2.0d
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
specifier|public
name|Class
name|getInterface
parameter_list|()
block|{
return|return
name|BindableRel
operator|.
name|class
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"BINDABLE"
return|;
block|}
specifier|public
name|RelTraitDef
name|getTraitDef
parameter_list|()
block|{
return|return
name|ConventionTraitDef
operator|.
name|INSTANCE
return|;
block|}
specifier|public
name|boolean
name|satisfies
parameter_list|(
name|RelTrait
name|trait
parameter_list|)
block|{
return|return
name|this
operator|==
name|trait
return|;
block|}
specifier|public
name|void
name|register
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|)
block|{
block|}
block|}
end_enum

begin_comment
comment|// End BindableConvention.java
end_comment

end_unit

