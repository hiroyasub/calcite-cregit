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
name|plan
operator|.
name|volcano
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
name|RelOptCost
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
name|RelOptCostFactory
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
name|RelOptUtil
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
name|Objects
import|;
end_import

begin_comment
comment|/**  *<code>VolcanoCost</code> represents the cost of a plan node.  *  *<p>This class is immutable: none of the methods modify any member  * variables.</p>  */
end_comment

begin_class
class|class
name|VolcanoCost
implements|implements
name|RelOptCost
block|{
comment|//~ Static fields/initializers ---------------------------------------------
specifier|static
specifier|final
name|VolcanoCost
name|INFINITY
init|=
operator|new
name|VolcanoCost
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|,
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{inf}"
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|VolcanoCost
name|HUGE
init|=
operator|new
name|VolcanoCost
argument_list|(
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|,
name|Double
operator|.
name|MAX_VALUE
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{huge}"
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|VolcanoCost
name|ZERO
init|=
operator|new
name|VolcanoCost
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|0.0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{0}"
return|;
block|}
block|}
decl_stmt|;
specifier|static
specifier|final
name|VolcanoCost
name|TINY
init|=
operator|new
name|VolcanoCost
argument_list|(
literal|1.0
argument_list|,
literal|1.0
argument_list|,
literal|0.0
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{tiny}"
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|RelOptCostFactory
name|FACTORY
init|=
operator|new
name|Factory
argument_list|()
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|final
name|double
name|cpu
decl_stmt|;
specifier|final
name|double
name|io
decl_stmt|;
specifier|final
name|double
name|rowCount
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|VolcanoCost
parameter_list|(
name|double
name|rowCount
parameter_list|,
name|double
name|cpu
parameter_list|,
name|double
name|io
parameter_list|)
block|{
name|this
operator|.
name|rowCount
operator|=
name|rowCount
expr_stmt|;
name|this
operator|.
name|cpu
operator|=
name|cpu
expr_stmt|;
name|this
operator|.
name|io
operator|=
name|io
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|double
name|getCpu
parameter_list|()
block|{
return|return
name|cpu
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isInfinite
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|INFINITY
operator|)
operator|||
operator|(
name|this
operator|.
name|rowCount
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
operator|)
operator|||
operator|(
name|this
operator|.
name|cpu
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
operator|)
operator|||
operator|(
name|this
operator|.
name|io
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getIo
parameter_list|()
block|{
return|return
name|io
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLe
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
name|VolcanoCost
name|that
init|=
operator|(
name|VolcanoCost
operator|)
name|other
decl_stmt|;
if|if
condition|(
literal|true
condition|)
block|{
return|return
name|this
operator|==
name|that
operator|||
name|this
operator|.
name|rowCount
operator|<=
name|that
operator|.
name|rowCount
return|;
block|}
return|return
operator|(
name|this
operator|==
name|that
operator|)
operator|||
operator|(
operator|(
name|this
operator|.
name|rowCount
operator|<=
name|that
operator|.
name|rowCount
operator|)
operator|&&
operator|(
name|this
operator|.
name|cpu
operator|<=
name|that
operator|.
name|cpu
operator|)
operator|&&
operator|(
name|this
operator|.
name|io
operator|<=
name|that
operator|.
name|io
operator|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isLt
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
if|if
condition|(
literal|true
condition|)
block|{
name|VolcanoCost
name|that
init|=
operator|(
name|VolcanoCost
operator|)
name|other
decl_stmt|;
return|return
name|this
operator|.
name|rowCount
operator|<
name|that
operator|.
name|rowCount
return|;
block|}
return|return
name|isLe
argument_list|(
name|other
argument_list|)
operator|&&
operator|!
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|rowCount
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
name|rowCount
argument_list|,
name|cpu
argument_list|,
name|io
argument_list|)
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"NonOverridingEquals"
argument_list|)
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
return|return
name|this
operator|==
name|other
operator|||
name|other
operator|instanceof
name|VolcanoCost
operator|&&
operator|(
name|this
operator|.
name|rowCount
operator|==
operator|(
operator|(
name|VolcanoCost
operator|)
name|other
operator|)
operator|.
name|rowCount
operator|)
operator|&&
operator|(
name|this
operator|.
name|cpu
operator|==
operator|(
operator|(
name|VolcanoCost
operator|)
name|other
operator|)
operator|.
name|cpu
operator|)
operator|&&
operator|(
name|this
operator|.
name|io
operator|==
operator|(
operator|(
name|VolcanoCost
operator|)
name|other
operator|)
operator|.
name|io
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
annotation|@
name|Nullable
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|VolcanoCost
condition|)
block|{
return|return
name|equals
argument_list|(
operator|(
name|VolcanoCost
operator|)
name|obj
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEqWithEpsilon
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|VolcanoCost
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|VolcanoCost
name|that
init|=
operator|(
name|VolcanoCost
operator|)
name|other
decl_stmt|;
return|return
operator|(
name|this
operator|==
name|that
operator|)
operator|||
operator|(
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|this
operator|.
name|rowCount
operator|-
name|that
operator|.
name|rowCount
argument_list|)
operator|<
name|RelOptUtil
operator|.
name|EPSILON
operator|)
operator|&&
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|this
operator|.
name|cpu
operator|-
name|that
operator|.
name|cpu
argument_list|)
operator|<
name|RelOptUtil
operator|.
name|EPSILON
operator|)
operator|&&
operator|(
name|Math
operator|.
name|abs
argument_list|(
name|this
operator|.
name|io
operator|-
name|that
operator|.
name|io
argument_list|)
operator|<
name|RelOptUtil
operator|.
name|EPSILON
operator|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|minus
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|INFINITY
condition|)
block|{
return|return
name|this
return|;
block|}
name|VolcanoCost
name|that
init|=
operator|(
name|VolcanoCost
operator|)
name|other
decl_stmt|;
return|return
operator|new
name|VolcanoCost
argument_list|(
name|this
operator|.
name|rowCount
operator|-
name|that
operator|.
name|rowCount
argument_list|,
name|this
operator|.
name|cpu
operator|-
name|that
operator|.
name|cpu
argument_list|,
name|this
operator|.
name|io
operator|-
name|that
operator|.
name|io
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|multiplyBy
parameter_list|(
name|double
name|factor
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|INFINITY
condition|)
block|{
return|return
name|this
return|;
block|}
return|return
operator|new
name|VolcanoCost
argument_list|(
name|rowCount
operator|*
name|factor
argument_list|,
name|cpu
operator|*
name|factor
argument_list|,
name|io
operator|*
name|factor
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|divideBy
parameter_list|(
name|RelOptCost
name|cost
parameter_list|)
block|{
comment|// Compute the geometric average of the ratios of all of the factors
comment|// which are non-zero and finite.
name|VolcanoCost
name|that
init|=
operator|(
name|VolcanoCost
operator|)
name|cost
decl_stmt|;
name|double
name|d
init|=
literal|1
decl_stmt|;
name|double
name|n
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|(
name|this
operator|.
name|rowCount
operator|!=
literal|0
operator|)
operator|&&
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|this
operator|.
name|rowCount
argument_list|)
operator|&&
operator|(
name|that
operator|.
name|rowCount
operator|!=
literal|0
operator|)
operator|&&
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|that
operator|.
name|rowCount
argument_list|)
condition|)
block|{
name|d
operator|*=
name|this
operator|.
name|rowCount
operator|/
name|that
operator|.
name|rowCount
expr_stmt|;
operator|++
name|n
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|this
operator|.
name|cpu
operator|!=
literal|0
operator|)
operator|&&
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|this
operator|.
name|cpu
argument_list|)
operator|&&
operator|(
name|that
operator|.
name|cpu
operator|!=
literal|0
operator|)
operator|&&
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|that
operator|.
name|cpu
argument_list|)
condition|)
block|{
name|d
operator|*=
name|this
operator|.
name|cpu
operator|/
name|that
operator|.
name|cpu
expr_stmt|;
operator|++
name|n
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|this
operator|.
name|io
operator|!=
literal|0
operator|)
operator|&&
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|this
operator|.
name|io
argument_list|)
operator|&&
operator|(
name|that
operator|.
name|io
operator|!=
literal|0
operator|)
operator|&&
operator|!
name|Double
operator|.
name|isInfinite
argument_list|(
name|that
operator|.
name|io
argument_list|)
condition|)
block|{
name|d
operator|*=
name|this
operator|.
name|io
operator|/
name|that
operator|.
name|io
expr_stmt|;
operator|++
name|n
expr_stmt|;
block|}
if|if
condition|(
name|n
operator|==
literal|0
condition|)
block|{
return|return
literal|1.0
return|;
block|}
return|return
name|Math
operator|.
name|pow
argument_list|(
name|d
argument_list|,
literal|1
operator|/
name|n
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|plus
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
name|VolcanoCost
name|that
init|=
operator|(
name|VolcanoCost
operator|)
name|other
decl_stmt|;
if|if
condition|(
operator|(
name|this
operator|==
name|INFINITY
operator|)
operator|||
operator|(
name|that
operator|==
name|INFINITY
operator|)
condition|)
block|{
return|return
name|INFINITY
return|;
block|}
return|return
operator|new
name|VolcanoCost
argument_list|(
name|this
operator|.
name|rowCount
operator|+
name|that
operator|.
name|rowCount
argument_list|,
name|this
operator|.
name|cpu
operator|+
name|that
operator|.
name|cpu
argument_list|,
name|this
operator|.
name|io
operator|+
name|that
operator|.
name|io
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{"
operator|+
name|rowCount
operator|+
literal|" rows, "
operator|+
name|cpu
operator|+
literal|" cpu, "
operator|+
name|io
operator|+
literal|" io}"
return|;
block|}
comment|/** Implementation of {@link org.apache.calcite.plan.RelOptCostFactory}    * that creates {@link org.apache.calcite.plan.volcano.VolcanoCost}s. */
specifier|private
specifier|static
class|class
name|Factory
implements|implements
name|RelOptCostFactory
block|{
annotation|@
name|Override
specifier|public
name|RelOptCost
name|makeCost
parameter_list|(
name|double
name|dRows
parameter_list|,
name|double
name|dCpu
parameter_list|,
name|double
name|dIo
parameter_list|)
block|{
return|return
operator|new
name|VolcanoCost
argument_list|(
name|dRows
argument_list|,
name|dCpu
argument_list|,
name|dIo
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|makeHugeCost
parameter_list|()
block|{
return|return
name|VolcanoCost
operator|.
name|HUGE
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|makeInfiniteCost
parameter_list|()
block|{
return|return
name|VolcanoCost
operator|.
name|INFINITY
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|makeTinyCost
parameter_list|()
block|{
return|return
name|VolcanoCost
operator|.
name|TINY
return|;
block|}
annotation|@
name|Override
specifier|public
name|RelOptCost
name|makeZeroCost
parameter_list|()
block|{
return|return
name|VolcanoCost
operator|.
name|ZERO
return|;
block|}
block|}
block|}
end_class

end_unit

