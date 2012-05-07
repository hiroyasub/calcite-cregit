begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sarg
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * SargIntervalBase is a common base for {@link SargInterval} and {@link  * SargIntervalExpr}.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SargIntervalBase
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|SargFactory
name|factory
decl_stmt|;
specifier|protected
specifier|final
name|SargMutableEndpoint
name|lowerBound
decl_stmt|;
specifier|protected
specifier|final
name|SargMutableEndpoint
name|upperBound
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * @see SargFactory#newIntervalExpr      */
name|SargIntervalBase
parameter_list|(
name|SargFactory
name|factory
parameter_list|,
name|RelDataType
name|dataType
parameter_list|)
block|{
name|this
operator|.
name|factory
operator|=
name|factory
expr_stmt|;
name|lowerBound
operator|=
name|factory
operator|.
name|newEndpoint
argument_list|(
name|dataType
argument_list|)
expr_stmt|;
name|upperBound
operator|=
name|factory
operator|.
name|newEndpoint
argument_list|(
name|dataType
argument_list|)
expr_stmt|;
name|unsetLower
argument_list|()
expr_stmt|;
name|unsetUpper
argument_list|()
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * @return an immutable reference to the endpoint representing this      * interval's lower bound      */
specifier|public
name|SargEndpoint
name|getLowerBound
parameter_list|()
block|{
return|return
name|lowerBound
return|;
block|}
comment|/**      * @return an immutable reference to the endpoint representing this      * interval's upper bound      */
specifier|public
name|SargEndpoint
name|getUpperBound
parameter_list|()
block|{
return|return
name|upperBound
return|;
block|}
comment|/**      * @return whether this represents a single point      */
specifier|public
name|boolean
name|isPoint
parameter_list|()
block|{
return|return
name|lowerBound
operator|.
name|isClosed
argument_list|()
operator|&&
name|upperBound
operator|.
name|isClosed
argument_list|()
operator|&&
name|lowerBound
operator|.
name|isTouching
argument_list|(
name|upperBound
argument_list|)
return|;
block|}
comment|/**      * @return whether this represents the empty interval      */
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
operator|!
name|lowerBound
operator|.
name|isClosed
argument_list|()
operator|&&
operator|!
name|upperBound
operator|.
name|isClosed
argument_list|()
operator|&&
name|lowerBound
operator|.
name|isNull
argument_list|()
operator|&&
name|upperBound
operator|.
name|isNull
argument_list|()
return|;
block|}
comment|/**      * @return whether this represents a (non-empty, non-point) range interval      */
specifier|public
name|boolean
name|isRange
parameter_list|()
block|{
return|return
operator|(
operator|!
name|isPoint
argument_list|()
operator|&&
operator|!
name|isEmpty
argument_list|()
operator|)
return|;
block|}
comment|/**      * @return whether this represents the universal set      */
specifier|public
name|boolean
name|isUnconstrained
parameter_list|()
block|{
return|return
operator|!
name|lowerBound
operator|.
name|isFinite
argument_list|()
operator|&&
operator|!
name|upperBound
operator|.
name|isFinite
argument_list|()
return|;
block|}
comment|/**      * @return the factory which produced this expression      */
specifier|public
name|SargFactory
name|getFactory
parameter_list|()
block|{
return|return
name|factory
return|;
block|}
comment|/**      * Sets this interval to represent a single point (possibly the null value).      *      * @param coordinate coordinate of point to set, or null for the null value      */
name|void
name|setPoint
parameter_list|(
name|RexNode
name|coordinate
parameter_list|)
block|{
name|setLower
argument_list|(
name|coordinate
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
name|setUpper
argument_list|(
name|coordinate
argument_list|,
name|SargStrictness
operator|.
name|CLOSED
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets this interval to represent a single point matching the null value.      */
name|void
name|setNull
parameter_list|()
block|{
name|setPoint
argument_list|(
name|factory
operator|.
name|newNullLiteral
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the lower bound for this interval.      *      * @param coordinate coordinate of point to set, must not be null      * @param strictness strictness      */
name|void
name|setLower
parameter_list|(
name|RexNode
name|coordinate
parameter_list|,
name|SargStrictness
name|strictness
parameter_list|)
block|{
name|lowerBound
operator|.
name|setFinite
argument_list|(
name|SargBoundType
operator|.
name|LOWER
argument_list|,
name|strictness
argument_list|,
name|coordinate
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets the upper bound for this interval.      *      * @param coordinate coordinate of point to set      * @param strictness boundary strictness      */
name|void
name|setUpper
parameter_list|(
name|RexNode
name|coordinate
parameter_list|,
name|SargStrictness
name|strictness
parameter_list|)
block|{
name|upperBound
operator|.
name|setFinite
argument_list|(
name|SargBoundType
operator|.
name|UPPER
argument_list|,
name|strictness
argument_list|,
name|coordinate
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes the lower bound for this interval, setting it to -infinity.      */
name|void
name|unsetLower
parameter_list|()
block|{
name|lowerBound
operator|.
name|setInfinity
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Removes the upper bound for this interval, setting it to +infinity.      */
name|void
name|unsetUpper
parameter_list|()
block|{
name|upperBound
operator|.
name|setInfinity
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sets this interval to unconstrained (matching everything, including      * null).      */
name|void
name|setUnconstrained
parameter_list|()
block|{
name|unsetLower
argument_list|()
expr_stmt|;
name|unsetUpper
argument_list|()
expr_stmt|;
block|}
comment|/**      * Sets this interval to empty (matching nothing at all).      */
name|void
name|setEmpty
parameter_list|()
block|{
name|setLower
argument_list|(
name|factory
operator|.
name|newNullLiteral
argument_list|()
argument_list|,
name|SargStrictness
operator|.
name|OPEN
argument_list|)
expr_stmt|;
name|setUpper
argument_list|(
name|factory
operator|.
name|newNullLiteral
argument_list|()
argument_list|,
name|SargStrictness
operator|.
name|OPEN
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RelDataType
name|getDataType
parameter_list|()
block|{
return|return
name|lowerBound
operator|.
name|getDataType
argument_list|()
return|;
block|}
comment|// implement SargExpr
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|lowerBound
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|isEmpty
argument_list|()
condition|)
block|{
name|printBound
argument_list|(
name|sb
argument_list|,
name|lowerBound
argument_list|)
expr_stmt|;
if|if
condition|(
name|isPoint
argument_list|()
condition|)
block|{
comment|// point has both endpoints same; don't repeat
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|printBound
argument_list|(
name|sb
argument_list|,
name|upperBound
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|upperBound
operator|.
name|isClosed
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|printBound
parameter_list|(
name|StringBuilder
name|sb
parameter_list|,
name|SargEndpoint
name|endpoint
parameter_list|)
block|{
if|if
condition|(
name|endpoint
operator|.
name|isFinite
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|endpoint
operator|.
name|getCoordinate
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|endpoint
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End SargIntervalBase.java
end_comment

end_unit

