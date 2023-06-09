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
name|linq4j
operator|.
name|tree
operator|.
name|BlockStatement
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
name|DeriveMode
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
name|PhysicalNode
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
name|Pair
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

begin_comment
comment|/**  * A relational expression of one of the  * {@link org.apache.calcite.adapter.enumerable.EnumerableConvention} calling  * conventions.  */
end_comment

begin_interface
specifier|public
interface|interface
name|EnumerableRel
extends|extends
name|PhysicalNode
block|{
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|default
annotation|@
name|Nullable
name|Pair
argument_list|<
name|RelTraitSet
argument_list|,
name|List
argument_list|<
name|RelTraitSet
argument_list|>
argument_list|>
name|passThroughTraits
parameter_list|(
name|RelTraitSet
name|required
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|default
annotation|@
name|Nullable
name|Pair
argument_list|<
name|RelTraitSet
argument_list|,
name|List
argument_list|<
name|RelTraitSet
argument_list|>
argument_list|>
name|deriveTraits
parameter_list|(
name|RelTraitSet
name|childTraits
parameter_list|,
name|int
name|childId
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|default
name|DeriveMode
name|getDeriveMode
parameter_list|()
block|{
return|return
name|DeriveMode
operator|.
name|LEFT_FIRST
return|;
block|}
comment|/**    * Creates a plan for this expression according to a calling convention.    *    * @param implementor Implementor    * @param pref Preferred representation for rows in result expression    * @return Plan for this expression according to a calling convention    */
name|Result
name|implement
parameter_list|(
name|EnumerableRelImplementor
name|implementor
parameter_list|,
name|Prefer
name|pref
parameter_list|)
function_decl|;
comment|/** Preferred physical type. */
enum|enum
name|Prefer
block|{
comment|/** Records must be represented as arrays. */
name|ARRAY
block|,
comment|/** Consumer would prefer that records are represented as arrays, but can      * accommodate records represented as objects. */
name|ARRAY_NICE
block|,
comment|/** Records must be represented as objects. */
name|CUSTOM
block|,
comment|/** Consumer would prefer that records are represented as objects, but can      * accommodate records represented as arrays. */
name|CUSTOM_NICE
block|,
comment|/** Consumer has no preferred representation. */
name|ANY
block|;
specifier|public
name|JavaRowFormat
name|preferCustom
parameter_list|()
block|{
return|return
name|prefer
argument_list|(
name|JavaRowFormat
operator|.
name|CUSTOM
argument_list|)
return|;
block|}
specifier|public
name|JavaRowFormat
name|preferArray
parameter_list|()
block|{
return|return
name|prefer
argument_list|(
name|JavaRowFormat
operator|.
name|ARRAY
argument_list|)
return|;
block|}
specifier|public
name|JavaRowFormat
name|prefer
parameter_list|(
name|JavaRowFormat
name|format
parameter_list|)
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|CUSTOM
case|:
return|return
name|JavaRowFormat
operator|.
name|CUSTOM
return|;
case|case
name|ARRAY
case|:
return|return
name|JavaRowFormat
operator|.
name|ARRAY
return|;
default|default:
return|return
name|format
return|;
block|}
block|}
specifier|public
name|Prefer
name|of
parameter_list|(
name|JavaRowFormat
name|format
parameter_list|)
block|{
switch|switch
condition|(
name|format
condition|)
block|{
case|case
name|ARRAY
case|:
return|return
name|ARRAY
return|;
default|default:
return|return
name|CUSTOM
return|;
block|}
block|}
block|}
comment|/** Result of implementing an enumerable relational expression by generating    * Java code. */
class|class
name|Result
block|{
specifier|public
specifier|final
name|BlockStatement
name|block
decl_stmt|;
comment|/**      * Describes the Java type returned by this relational expression, and the      * mapping between it and the fields of the logical row type.      */
specifier|public
specifier|final
name|PhysType
name|physType
decl_stmt|;
specifier|public
specifier|final
name|JavaRowFormat
name|format
decl_stmt|;
specifier|public
name|Result
parameter_list|(
name|BlockStatement
name|block
parameter_list|,
name|PhysType
name|physType
parameter_list|,
name|JavaRowFormat
name|format
parameter_list|)
block|{
name|this
operator|.
name|block
operator|=
name|block
expr_stmt|;
name|this
operator|.
name|physType
operator|=
name|physType
expr_stmt|;
name|this
operator|.
name|format
operator|=
name|format
expr_stmt|;
block|}
block|}
block|}
end_interface

end_unit

