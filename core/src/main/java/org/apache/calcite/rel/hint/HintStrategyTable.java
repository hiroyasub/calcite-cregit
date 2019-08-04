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
name|hint
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
name|RelNode
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
name|java
operator|.
name|util
operator|.
name|HashMap
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
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * {@code HintStrategy} collection indicating which kind of  * {@link org.apache.calcite.rel.RelNode} a hint can apply to.  *  *<p>Typically, every supported hints should register a {@code HintStrategy}  * in this collection. For example, {@link HintStrategies#JOIN} implies that this hint  * would be propagated and applied to the {@link org.apache.calcite.rel.core.Join}  * relational expressions.  *  *<p>A {@code HintStrategy} can be used independently or cascaded with other strategies  * with method {@link HintStrategies#cascade}.  *  *<p>The matching for hint name is case in-sensitive.  *  * @see HintStrategy  */
end_comment

begin_class
specifier|public
class|class
name|HintStrategyTable
block|{
comment|//~ Static fields/initializers ---------------------------------------------
comment|/** Empty strategies. */
comment|// Need to replace the EMPTY with DEFAULT if we have any hint implementations.
specifier|public
specifier|static
specifier|final
name|HintStrategyTable
name|EMPTY
init|=
operator|new
name|HintStrategyTable
argument_list|(
operator|new
name|HashMap
argument_list|<>
argument_list|()
argument_list|)
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
comment|/** Mapping from hint name to strategy. */
specifier|private
specifier|final
name|Map
argument_list|<
name|Key
argument_list|,
name|HintStrategy
argument_list|>
name|hintStrategyMap
decl_stmt|;
specifier|private
name|HintStrategyTable
parameter_list|(
name|Map
argument_list|<
name|Key
argument_list|,
name|HintStrategy
argument_list|>
name|strategies
parameter_list|)
block|{
name|this
operator|.
name|hintStrategyMap
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|strategies
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Apply this {@link HintStrategyTable} to the given relational    * expression for the {@code hints}.    *    * @param hints Hints that may attach to the {@code rel}    * @param rel   Relational expression    * @return A hints list that can be attached to the {@code rel}    */
specifier|public
name|List
argument_list|<
name|RelHint
argument_list|>
name|apply
parameter_list|(
name|List
argument_list|<
name|RelHint
argument_list|>
name|hints
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
return|return
name|hints
operator|.
name|stream
argument_list|()
operator|.
name|filter
argument_list|(
name|relHint
lambda|->
name|supportsRel
argument_list|(
name|relHint
argument_list|,
name|rel
argument_list|)
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Check if the give hint name is valid.    *    * @param name The hint name    */
specifier|public
name|void
name|validateHint
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|hintStrategyMap
operator|.
name|containsKey
argument_list|(
name|Key
operator|.
name|of
argument_list|(
name|name
argument_list|)
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Hint: "
operator|+
name|name
operator|+
literal|" should be registered in the HintStrategies."
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|supportsRel
parameter_list|(
name|RelHint
name|hint
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
specifier|final
name|Key
name|key
init|=
name|Key
operator|.
name|of
argument_list|(
name|hint
operator|.
name|hintName
argument_list|)
decl_stmt|;
assert|assert
name|this
operator|.
name|hintStrategyMap
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
assert|;
return|return
name|this
operator|.
name|hintStrategyMap
operator|.
name|get
argument_list|(
name|key
argument_list|)
operator|.
name|supportsRel
argument_list|(
name|hint
argument_list|,
name|rel
argument_list|)
return|;
block|}
comment|/**    * @return A strategies builder    */
specifier|public
specifier|static
name|Builder
name|builder
parameter_list|()
block|{
return|return
operator|new
name|Builder
argument_list|()
return|;
block|}
comment|/**    * Key used to keep the strategies which ignores the case sensitivity.    */
specifier|private
specifier|static
class|class
name|Key
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|Key
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|static
name|Key
name|of
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Key
argument_list|(
name|name
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|Key
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|this
operator|.
name|name
argument_list|,
operator|(
operator|(
name|Key
operator|)
name|obj
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|//~ Inner Class ------------------------------------------------------------
comment|/**    * Builder for {@code HintStrategyTable}.    */
specifier|public
specifier|static
class|class
name|Builder
block|{
specifier|private
name|Map
argument_list|<
name|Key
argument_list|,
name|HintStrategy
argument_list|>
name|hintStrategyMap
decl_stmt|;
specifier|public
name|Builder
parameter_list|()
block|{
name|this
operator|.
name|hintStrategyMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Builder
name|addHintStrategy
parameter_list|(
name|String
name|hintName
parameter_list|,
name|HintStrategy
name|strategy
parameter_list|)
block|{
name|this
operator|.
name|hintStrategyMap
operator|.
name|put
argument_list|(
name|Key
operator|.
name|of
argument_list|(
name|hintName
argument_list|)
argument_list|,
name|strategy
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
specifier|public
name|HintStrategyTable
name|build
parameter_list|()
block|{
return|return
operator|new
name|HintStrategyTable
argument_list|(
name|this
operator|.
name|hintStrategyMap
argument_list|)
return|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End HintStrategyTable.java
end_comment

end_unit

