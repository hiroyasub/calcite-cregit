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
name|adapter
operator|.
name|enumerable
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
name|DataContext
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
name|interpreter
operator|.
name|BindableConvention
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
name|interpreter
operator|.
name|BindableRel
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
name|interpreter
operator|.
name|Node
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
name|interpreter
operator|.
name|Row
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
name|interpreter
operator|.
name|Sink
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
name|linq4j
operator|.
name|Enumerable
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
name|linq4j
operator|.
name|Enumerator
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
name|convert
operator|.
name|ConverterImpl
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
name|convert
operator|.
name|ConverterRule
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
name|runtime
operator|.
name|ArrayBindable
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
name|runtime
operator|.
name|Bindable
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
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
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
comment|/**  * Relational expression that converts an enumerable input to interpretable  * calling convention.  *  * @see org.apache.calcite.adapter.enumerable.EnumerableConvention  * @see org.apache.calcite.interpreter.BindableConvention  */
end_comment

begin_class
specifier|public
class|class
name|EnumerableBindable
extends|extends
name|ConverterImpl
implements|implements
name|BindableRel
block|{
specifier|protected
name|EnumerableBindable
parameter_list|(
name|RelOptCluster
name|cluster
parameter_list|,
name|RelNode
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|cluster
argument_list|,
name|ConventionTraitDef
operator|.
name|INSTANCE
argument_list|,
name|cluster
operator|.
name|traitSetOf
argument_list|(
name|BindableConvention
operator|.
name|INSTANCE
argument_list|)
argument_list|,
name|input
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|EnumerableBindable
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
return|return
operator|new
name|EnumerableBindable
argument_list|(
name|getCluster
argument_list|()
argument_list|,
name|sole
argument_list|(
name|inputs
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|Object
index|[]
argument_list|>
name|getElementType
parameter_list|()
block|{
return|return
name|Object
index|[]
operator|.
name|class
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumerable
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
name|bind
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
name|ImmutableMap
operator|.
name|of
argument_list|()
decl_stmt|;
specifier|final
name|Bindable
name|bindable
init|=
name|EnumerableInterpretable
operator|.
name|toBindable
argument_list|(
name|map
argument_list|,
literal|null
argument_list|,
operator|(
name|EnumerableRel
operator|)
name|getInput
argument_list|()
argument_list|,
name|EnumerableRel
operator|.
name|Prefer
operator|.
name|ARRAY
argument_list|)
decl_stmt|;
specifier|final
name|ArrayBindable
name|arrayBindable
init|=
name|EnumerableInterpretable
operator|.
name|box
argument_list|(
name|bindable
argument_list|)
decl_stmt|;
return|return
name|arrayBindable
operator|.
name|bind
argument_list|(
name|dataContext
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|implement
parameter_list|(
specifier|final
name|InterpreterImplementor
name|implementor
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
specifier|final
name|Sink
name|sink
init|=
name|requireNonNull
argument_list|(
name|implementor
operator|.
name|relSinks
operator|.
name|get
argument_list|(
name|EnumerableBindable
operator|.
name|this
argument_list|)
argument_list|,
parameter_list|()
lambda|->
literal|"relSinks.get is null for "
operator|+
name|EnumerableBindable
operator|.
name|this
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Enumerable
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
name|enumerable
init|=
name|bind
argument_list|(
name|implementor
operator|.
name|dataContext
argument_list|)
decl_stmt|;
specifier|final
name|Enumerator
argument_list|<
annotation|@
name|Nullable
name|Object
index|[]
argument_list|>
name|enumerator
init|=
name|enumerable
operator|.
name|enumerator
argument_list|()
decl_stmt|;
while|while
condition|(
name|enumerator
operator|.
name|moveNext
argument_list|()
condition|)
block|{
name|sink
operator|.
name|send
argument_list|(
name|Row
operator|.
name|asCopy
argument_list|(
name|enumerator
operator|.
name|current
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|;
block|}
comment|/**    * Rule that converts any enumerable relational expression to bindable.    *    * @see EnumerableRules#TO_BINDABLE    */
specifier|public
specifier|static
class|class
name|EnumerableToBindableConverterRule
extends|extends
name|ConverterRule
block|{
comment|/** Default configuration. */
specifier|public
specifier|static
specifier|final
name|Config
name|DEFAULT_CONFIG
init|=
name|Config
operator|.
name|INSTANCE
operator|.
name|withConversion
argument_list|(
name|EnumerableRel
operator|.
name|class
argument_list|,
name|EnumerableConvention
operator|.
name|INSTANCE
argument_list|,
name|BindableConvention
operator|.
name|INSTANCE
argument_list|,
literal|"EnumerableToBindableConverterRule"
argument_list|)
operator|.
name|withRuleFactory
argument_list|(
name|EnumerableToBindableConverterRule
operator|::
operator|new
argument_list|)
decl_stmt|;
specifier|protected
name|EnumerableToBindableConverterRule
parameter_list|(
name|Config
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RelNode
name|convert
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
return|return
operator|new
name|EnumerableBindable
argument_list|(
name|rel
operator|.
name|getCluster
argument_list|()
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

