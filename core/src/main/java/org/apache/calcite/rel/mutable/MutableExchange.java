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
name|mutable
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
name|RelDistribution
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
comment|/** Mutable equivalent of {@link org.apache.calcite.rel.core.Exchange}. */
end_comment

begin_class
specifier|public
class|class
name|MutableExchange
extends|extends
name|MutableSingleRel
block|{
specifier|public
specifier|final
name|RelDistribution
name|distribution
decl_stmt|;
specifier|private
name|MutableExchange
parameter_list|(
name|MutableRel
name|input
parameter_list|,
name|RelDistribution
name|distribution
parameter_list|)
block|{
name|super
argument_list|(
name|MutableRelType
operator|.
name|EXCHANGE
argument_list|,
name|input
operator|.
name|rowType
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|this
operator|.
name|distribution
operator|=
name|distribution
expr_stmt|;
block|}
comment|/**    * Creates a MutableExchange.    *    * @param input         Input relational expression    * @param distribution  Distribution specification    */
specifier|public
specifier|static
name|MutableExchange
name|of
parameter_list|(
name|MutableRel
name|input
parameter_list|,
name|RelDistribution
name|distribution
parameter_list|)
block|{
return|return
operator|new
name|MutableExchange
argument_list|(
name|input
argument_list|,
name|distribution
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
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|MutableExchange
operator|&&
name|distribution
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableExchange
operator|)
name|obj
operator|)
operator|.
name|distribution
argument_list|)
operator|&&
name|input
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|MutableExchange
operator|)
name|obj
operator|)
operator|.
name|input
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
name|Objects
operator|.
name|hash
argument_list|(
name|input
argument_list|,
name|distribution
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|StringBuilder
name|digest
parameter_list|(
name|StringBuilder
name|buf
parameter_list|)
block|{
return|return
name|buf
operator|.
name|append
argument_list|(
literal|"Exchange(distribution: "
argument_list|)
operator|.
name|append
argument_list|(
name|distribution
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|MutableRel
name|clone
parameter_list|()
block|{
return|return
name|MutableExchange
operator|.
name|of
argument_list|(
name|input
operator|.
name|clone
argument_list|()
argument_list|,
name|distribution
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End MutableExchange.java
end_comment

end_unit

