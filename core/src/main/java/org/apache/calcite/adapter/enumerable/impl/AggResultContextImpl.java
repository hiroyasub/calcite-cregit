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
operator|.
name|impl
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
name|adapter
operator|.
name|enumerable
operator|.
name|AggResultContext
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
name|adapter
operator|.
name|enumerable
operator|.
name|PhysType
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
name|tree
operator|.
name|BlockBuilder
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
name|tree
operator|.
name|Expression
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
name|tree
operator|.
name|ParameterExpression
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
name|AggregateCall
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
comment|/**  * Implementation of  * {@link org.apache.calcite.adapter.enumerable.AggResultContext}.  */
end_comment

begin_class
specifier|public
class|class
name|AggResultContextImpl
extends|extends
name|AggResetContextImpl
implements|implements
name|AggResultContext
block|{
specifier|private
specifier|final
annotation|@
name|Nullable
name|AggregateCall
name|call
decl_stmt|;
specifier|private
specifier|final
annotation|@
name|Nullable
name|ParameterExpression
name|key
decl_stmt|;
specifier|private
specifier|final
annotation|@
name|Nullable
name|PhysType
name|keyPhysType
decl_stmt|;
comment|/**    * Creates aggregate result context.    *    * @param block Code block that will contain the result calculation statements    * @param call Aggregate call    * @param accumulator Accumulator variables that store the intermediate    *                    aggregate state    * @param key Key    */
specifier|public
name|AggResultContextImpl
parameter_list|(
name|BlockBuilder
name|block
parameter_list|,
annotation|@
name|Nullable
name|AggregateCall
name|call
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|accumulator
parameter_list|,
annotation|@
name|Nullable
name|ParameterExpression
name|key
parameter_list|,
annotation|@
name|Nullable
name|PhysType
name|keyPhysType
parameter_list|)
block|{
name|super
argument_list|(
name|block
argument_list|,
name|accumulator
argument_list|)
expr_stmt|;
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
comment|// null for AggAddContextImpl
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|keyPhysType
operator|=
name|keyPhysType
expr_stmt|;
comment|// null for AggAddContextImpl
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|Expression
name|key
parameter_list|()
block|{
return|return
name|key
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|keyField
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|requireNonNull
argument_list|(
name|keyPhysType
argument_list|,
literal|"keyPhysType"
argument_list|)
operator|.
name|fieldReference
argument_list|(
name|requireNonNull
argument_list|(
name|key
argument_list|,
literal|"key"
argument_list|)
argument_list|,
name|i
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|AggregateCall
name|call
parameter_list|()
block|{
return|return
name|requireNonNull
argument_list|(
name|call
argument_list|,
literal|"call"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

