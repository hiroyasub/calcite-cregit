begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|volcano
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<code>VolcanoCost</code> represents the cost of a plan node.  *  *<p>This class is immutable: none of the methods (besides {@link #set})  * modifies any member variables.</p>  */
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
comment|//~ Instance fields --------------------------------------------------------
name|double
name|dCpu
decl_stmt|;
name|double
name|dIo
decl_stmt|;
name|double
name|dRows
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|VolcanoCost
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
name|set
argument_list|(
name|dRows
argument_list|,
name|dCpu
argument_list|,
name|dIo
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|double
name|getCpu
parameter_list|()
block|{
return|return
name|dCpu
return|;
block|}
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
name|dRows
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
operator|)
operator|||
operator|(
name|this
operator|.
name|dCpu
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
operator|)
operator|||
operator|(
name|this
operator|.
name|dIo
operator|==
name|Double
operator|.
name|POSITIVE_INFINITY
operator|)
return|;
block|}
specifier|public
name|double
name|getIo
parameter_list|()
block|{
return|return
name|dIo
return|;
block|}
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
name|dRows
operator|<=
name|that
operator|.
name|dRows
operator|)
operator|&&
operator|(
name|this
operator|.
name|dCpu
operator|<=
name|that
operator|.
name|dCpu
operator|)
operator|&&
operator|(
name|this
operator|.
name|dIo
operator|<=
name|that
operator|.
name|dIo
operator|)
operator|)
return|;
block|}
specifier|public
name|boolean
name|isLt
parameter_list|(
name|RelOptCost
name|other
parameter_list|)
block|{
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
specifier|public
name|double
name|getRows
parameter_list|()
block|{
return|return
name|dRows
return|;
block|}
specifier|public
name|boolean
name|equals
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
name|this
operator|.
name|dRows
operator|==
name|that
operator|.
name|dRows
operator|)
operator|&&
operator|(
name|this
operator|.
name|dCpu
operator|==
name|that
operator|.
name|dCpu
operator|)
operator|&&
operator|(
name|this
operator|.
name|dIo
operator|==
name|that
operator|.
name|dIo
operator|)
operator|)
return|;
block|}
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
name|dRows
operator|-
name|that
operator|.
name|dRows
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
name|dCpu
operator|-
name|that
operator|.
name|dCpu
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
name|dIo
operator|-
name|that
operator|.
name|dIo
argument_list|)
operator|<
name|RelOptUtil
operator|.
name|EPSILON
operator|)
operator|)
return|;
block|}
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
name|dRows
operator|-
name|that
operator|.
name|dRows
argument_list|,
name|this
operator|.
name|dCpu
operator|-
name|that
operator|.
name|dCpu
argument_list|,
name|this
operator|.
name|dIo
operator|-
name|that
operator|.
name|dIo
argument_list|)
return|;
block|}
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
name|dRows
operator|*
name|factor
argument_list|,
name|dCpu
operator|*
name|factor
argument_list|,
name|dIo
operator|*
name|factor
argument_list|)
return|;
block|}
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
name|dRows
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
name|dRows
argument_list|)
operator|&&
operator|(
name|that
operator|.
name|dRows
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
name|dRows
argument_list|)
condition|)
block|{
name|d
operator|*=
name|this
operator|.
name|dRows
operator|/
name|that
operator|.
name|dRows
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
name|dCpu
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
name|dCpu
argument_list|)
operator|&&
operator|(
name|that
operator|.
name|dCpu
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
name|dCpu
argument_list|)
condition|)
block|{
name|d
operator|*=
name|this
operator|.
name|dCpu
operator|/
name|that
operator|.
name|dCpu
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
name|dIo
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
name|dIo
argument_list|)
operator|&&
operator|(
name|that
operator|.
name|dIo
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
name|dIo
argument_list|)
condition|)
block|{
name|d
operator|*=
name|this
operator|.
name|dIo
operator|/
name|that
operator|.
name|dIo
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
name|dRows
operator|+
name|that
operator|.
name|dRows
argument_list|,
name|this
operator|.
name|dCpu
operator|+
name|that
operator|.
name|dCpu
argument_list|,
name|this
operator|.
name|dIo
operator|+
name|that
operator|.
name|dIo
argument_list|)
return|;
block|}
specifier|public
name|void
name|set
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
name|this
operator|.
name|dRows
operator|=
name|dRows
expr_stmt|;
name|this
operator|.
name|dCpu
operator|=
name|dCpu
expr_stmt|;
name|this
operator|.
name|dIo
operator|=
name|dIo
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"{"
operator|+
name|dRows
operator|+
literal|" rows, "
operator|+
name|dCpu
operator|+
literal|" cpu, "
operator|+
name|dIo
operator|+
literal|" io}"
return|;
block|}
block|}
end_class

begin_comment
comment|// End VolcanoCost.java
end_comment

end_unit

