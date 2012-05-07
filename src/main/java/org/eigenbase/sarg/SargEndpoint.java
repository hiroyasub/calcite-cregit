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
name|sarg
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|type
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
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * SargEndpoint represents an endpoint of a ({@link SargInterval}).  *  *<p>Instances of SargEndpoint are immutable from outside this package.  * Subclass {@link SargMutableEndpoint} is provided for manipulation from  * outside the package.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SargEndpoint
implements|implements
name|Comparable
argument_list|<
name|SargEndpoint
argument_list|>
block|{
comment|// TODO jvs 16-Jan-2006:  special pattern prefix support for LIKE operator
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * Factory which produced this endpoint.      */
specifier|protected
specifier|final
name|SargFactory
name|factory
decl_stmt|;
comment|/**      * Datatype for endpoint value.      */
specifier|protected
specifier|final
name|RelDataType
name|dataType
decl_stmt|;
comment|/**      * Coordinate for this endpoint, constrained to be either {@link      * RexLiteral}, {@link RexInputRef}, {@link RexDynamicParam}, or null to      * represent infinity (positive or negative infinity is implied by      * boundType).      */
specifier|protected
name|RexNode
name|coordinate
decl_stmt|;
comment|/**      * @see #getBoundType      */
specifier|protected
name|SargBoundType
name|boundType
decl_stmt|;
comment|/**      * @see #getStrictness      */
specifier|protected
name|SargStrictness
name|strictness
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * @see SargFactory#newEndpoint      */
name|SargEndpoint
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
name|this
operator|.
name|dataType
operator|=
name|dataType
expr_stmt|;
name|boundType
operator|=
name|SargBoundType
operator|.
name|LOWER
expr_stmt|;
name|strictness
operator|=
name|SargStrictness
operator|.
name|OPEN
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
name|void
name|copyFrom
parameter_list|(
name|SargEndpoint
name|other
parameter_list|)
block|{
assert|assert
operator|(
name|getDataType
argument_list|()
operator|==
name|other
operator|.
name|getDataType
argument_list|()
operator|)
assert|;
if|if
condition|(
name|other
operator|.
name|isFinite
argument_list|()
condition|)
block|{
name|setFinite
argument_list|(
name|other
operator|.
name|getBoundType
argument_list|()
argument_list|,
name|other
operator|.
name|getStrictness
argument_list|()
argument_list|,
name|other
operator|.
name|getCoordinate
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|setInfinity
argument_list|(
name|other
operator|.
name|getInfinitude
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Sets this endpoint to either negative or positive infinity. An infinite      * endpoint implies an open bound (negative infinity implies a lower bound,      * while positive infinity implies an upper bound).      *      * @param infinitude either -1 or +1      */
name|void
name|setInfinity
parameter_list|(
name|int
name|infinitude
parameter_list|)
block|{
assert|assert
operator|(
operator|(
name|infinitude
operator|==
operator|-
literal|1
operator|)
operator|||
operator|(
name|infinitude
operator|==
literal|1
operator|)
operator|)
assert|;
if|if
condition|(
name|infinitude
operator|==
operator|-
literal|1
condition|)
block|{
name|boundType
operator|=
name|SargBoundType
operator|.
name|LOWER
expr_stmt|;
block|}
else|else
block|{
name|boundType
operator|=
name|SargBoundType
operator|.
name|UPPER
expr_stmt|;
block|}
name|strictness
operator|=
name|SargStrictness
operator|.
name|OPEN
expr_stmt|;
name|coordinate
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Sets a finite value for this endpoint.      *      * @param boundType bound type (upper/lower)      * @param strictness boundary strictness      * @param coordinate endpoint position      */
name|void
name|setFinite
parameter_list|(
name|SargBoundType
name|boundType
parameter_list|,
name|SargStrictness
name|strictness
parameter_list|,
name|RexNode
name|coordinate
parameter_list|)
block|{
comment|// validate the input
assert|assert
operator|(
name|coordinate
operator|!=
literal|null
operator|)
assert|;
if|if
condition|(
operator|!
operator|(
name|coordinate
operator|instanceof
name|RexDynamicParam
operator|)
operator|&&
operator|!
operator|(
name|coordinate
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
assert|assert
operator|(
name|coordinate
operator|instanceof
name|RexLiteral
operator|)
assert|;
name|RexLiteral
name|literal
init|=
operator|(
name|RexLiteral
operator|)
name|coordinate
decl_stmt|;
if|if
condition|(
operator|!
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|literal
argument_list|)
condition|)
block|{
assert|assert
operator|(
name|SqlTypeUtil
operator|.
name|canAssignFrom
argument_list|(
name|dataType
argument_list|,
name|literal
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
assert|;
block|}
block|}
name|this
operator|.
name|boundType
operator|=
name|boundType
expr_stmt|;
name|this
operator|.
name|coordinate
operator|=
name|coordinate
expr_stmt|;
name|this
operator|.
name|strictness
operator|=
name|strictness
expr_stmt|;
name|convertToTargetType
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|convertToTargetType
parameter_list|()
block|{
if|if
condition|(
operator|!
operator|(
name|coordinate
operator|instanceof
name|RexLiteral
operator|)
condition|)
block|{
comment|// Dynamic parameters and RexInputRefs are always cast to the
comment|// target type before comparison, so they are guaranteed not to
comment|// need any special conversion logic.
return|return;
block|}
name|RexLiteral
name|literal
init|=
operator|(
name|RexLiteral
operator|)
name|coordinate
decl_stmt|;
if|if
condition|(
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|literal
argument_list|)
condition|)
block|{
return|return;
block|}
name|Comparable
name|value
init|=
name|literal
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|int
name|roundingCompensation
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|BigDecimal
condition|)
block|{
name|roundingCompensation
operator|=
name|convertNumber
argument_list|(
operator|(
name|BigDecimal
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|NlsString
condition|)
block|{
name|roundingCompensation
operator|=
name|convertString
argument_list|(
operator|(
name|NlsString
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|ByteBuffer
condition|)
block|{
name|roundingCompensation
operator|=
name|convertBytes
argument_list|(
operator|(
name|ByteBuffer
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// NOTE jvs 19-Feb-2006:  Once we support fractional time
comment|// precision, need to handle that here.
return|return;
block|}
comment|// rounding takes precedence over the strictness flag.
comment|//  Input        round    strictness    output    effective strictness
comment|//>5.9        down            -1>=6        0
comment|//>=5.9       down             0>=6        0
comment|//>6.1          up            -1>6        1
comment|//>=6.1         up             0>6        1
comment|//<6.1          up             1<=6        0
comment|//<=6.1         up             0<=6        0
comment|//<5.9        down             1<6       -1
comment|//<=5.9       down             0<6       -1
if|if
condition|(
name|roundingCompensation
operator|==
literal|0
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|boundType
operator|==
name|SargBoundType
operator|.
name|LOWER
condition|)
block|{
if|if
condition|(
name|roundingCompensation
operator|<
literal|0
condition|)
block|{
name|strictness
operator|=
name|SargStrictness
operator|.
name|CLOSED
expr_stmt|;
block|}
else|else
block|{
name|strictness
operator|=
name|SargStrictness
operator|.
name|OPEN
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|boundType
operator|==
name|SargBoundType
operator|.
name|UPPER
condition|)
block|{
if|if
condition|(
name|roundingCompensation
operator|>
literal|0
condition|)
block|{
name|strictness
operator|=
name|SargStrictness
operator|.
name|CLOSED
expr_stmt|;
block|}
else|else
block|{
name|strictness
operator|=
name|SargStrictness
operator|.
name|OPEN
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|int
name|convertString
parameter_list|(
name|NlsString
name|value
parameter_list|)
block|{
comment|// For character strings, have to deal with truncation (complicated by
comment|// padding rules).
name|boolean
name|fixed
init|=
name|dataType
operator|.
name|getSqlTypeName
argument_list|()
operator|==
name|SqlTypeName
operator|.
name|CHAR
decl_stmt|;
name|String
name|s
init|=
name|value
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|trimmed
init|=
name|Util
operator|.
name|rtrim
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|fixed
condition|)
block|{
comment|// For CHAR, canonical representation is padded.  This is
comment|// required during execution of comparisons.
name|s
operator|=
name|Util
operator|.
name|rpad
argument_list|(
name|s
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// For PAD SPACE, we can use trimmed representation as
comment|// canonical for VARCHAR.  This may shave off cycles
comment|// during execution of comparisons.  If we ever support the NO PAD
comment|// attribute, we'll have to do something different for collations
comment|// with that attribute enabled.
name|s
operator|=
name|trimmed
expr_stmt|;
block|}
comment|// Truncate if needed
if|if
condition|(
name|s
operator|.
name|length
argument_list|()
operator|>
name|dataType
operator|.
name|getPrecision
argument_list|()
condition|)
block|{
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
comment|// Post-truncation, need to trim again if truncation
comment|// left spaces on the end
if|if
condition|(
operator|!
name|fixed
condition|)
block|{
name|s
operator|=
name|Util
operator|.
name|rtrim
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
block|}
name|coordinate
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeCharLiteral
argument_list|(
operator|new
name|NlsString
argument_list|(
name|s
argument_list|,
name|value
operator|.
name|getCharsetName
argument_list|()
argument_list|,
name|value
operator|.
name|getCollation
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|trimmed
operator|.
name|length
argument_list|()
operator|>
name|dataType
operator|.
name|getPrecision
argument_list|()
condition|)
block|{
comment|// Truncation is always "down" in coordinate space, so rounding
comment|// compensation is always "up".  Note that this calculation is
comment|// intentionally with respect to the pre-truncation trim (not the
comment|// post-truncation trim) because that's what tells us whether
comment|// we truncated anything of significance.
return|return
literal|1
return|;
block|}
else|else
block|{
comment|// For PAD SPACE, trailing spaces have no effect on comparison,
comment|// so it's the same as if no truncation took place.
return|return
literal|0
return|;
block|}
block|}
specifier|private
name|int
name|convertBytes
parameter_list|(
name|ByteBuffer
name|value
parameter_list|)
block|{
comment|// REVIEW jvs 11-Sept-2006:  What about 0-padding for BINARY?
comment|// For binary strings, have to deal with truncation.
name|byte
index|[]
name|a
init|=
name|value
operator|.
name|array
argument_list|()
decl_stmt|;
if|if
condition|(
name|a
operator|.
name|length
operator|<=
name|dataType
operator|.
name|getPrecision
argument_list|()
condition|)
block|{
comment|// No truncation required.
return|return
literal|0
return|;
block|}
name|byte
index|[]
name|truncated
init|=
operator|new
name|byte
index|[
name|dataType
operator|.
name|getPrecision
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|a
argument_list|,
literal|0
argument_list|,
name|truncated
argument_list|,
literal|0
argument_list|,
name|dataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
expr_stmt|;
name|coordinate
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeBinaryLiteral
argument_list|(
name|truncated
argument_list|)
expr_stmt|;
comment|// Truncation is always "down" in coordinate space, so rounding
comment|// compensation is always "up".
return|return
literal|1
return|;
block|}
specifier|private
name|int
name|convertNumber
parameter_list|(
name|BigDecimal
name|value
parameter_list|)
block|{
if|if
condition|(
name|SqlTypeUtil
operator|.
name|isApproximateNumeric
argument_list|(
name|dataType
argument_list|)
condition|)
block|{
comment|// REVIEW jvs 18-Jan-2006: is it necessary to do anything for
comment|// approx types here?  Wait until someone complains.  May at least
comment|// have to deal with case of double->float overflow.
return|return
literal|0
return|;
block|}
comment|// For exact numerics, we have to deal with rounding/overflow fun and
comment|// games.
comment|// REVIEW jvs 19-Feb-2006: Why do we make things complicated with
comment|// RoundingMode.HALF_UP?  We could just use RoundingMode.FLOOR so the
comment|// compensation direction would always be the same.  Maybe because this
comment|// code was ported from Broadbase, where the conversion library didn't
comment|// support multiple rounding modes.
name|BigDecimal
name|roundedValue
init|=
name|value
operator|.
name|setScale
argument_list|(
name|dataType
operator|.
name|getScale
argument_list|()
argument_list|,
name|RoundingMode
operator|.
name|HALF_UP
argument_list|)
decl_stmt|;
if|if
condition|(
name|roundedValue
operator|.
name|precision
argument_list|()
operator|>
name|dataType
operator|.
name|getPrecision
argument_list|()
condition|)
block|{
comment|// Overflow.  Convert to infinity.  Note that this may
comment|// flip the bound type, which isn't really correct,
comment|// but we handle that outside in SargIntervalExpr.evaluate.
name|setInfinity
argument_list|(
name|roundedValue
operator|.
name|signum
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
name|coordinate
operator|=
name|factory
operator|.
name|getRexBuilder
argument_list|()
operator|.
name|makeExactLiteral
argument_list|(
name|roundedValue
argument_list|,
name|dataType
argument_list|)
expr_stmt|;
comment|// The sign of roundingCompensation should be the opposite of the
comment|// rounding direction, so subtract post-rounding value from
comment|// pre-rounding.
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|roundedValue
argument_list|)
return|;
block|}
comment|/**      * @return true if this endpoint represents a closed (exact) bound; false if      * open (strict)      */
specifier|public
name|boolean
name|isClosed
parameter_list|()
block|{
return|return
name|strictness
operator|==
name|SargStrictness
operator|.
name|CLOSED
return|;
block|}
comment|/**      * @return opposite of isClosed      */
specifier|public
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|strictness
operator|==
name|SargStrictness
operator|.
name|OPEN
return|;
block|}
comment|/**      * @return false if this endpoint represents infinity (either positive or      * negative); true if a finite coordinate      */
specifier|public
name|boolean
name|isFinite
parameter_list|()
block|{
return|return
name|coordinate
operator|!=
literal|null
return|;
block|}
comment|/**      * @return -1 for negative infinity, +1 for positive infinity, 0 for a      * finite endpoint      */
specifier|public
name|int
name|getInfinitude
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isFinite
argument_list|()
condition|)
block|{
if|if
condition|(
name|boundType
operator|==
name|SargBoundType
operator|.
name|LOWER
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
comment|/**      * @return coordinate of this endpoint      */
specifier|public
name|RexNode
name|getCoordinate
parameter_list|()
block|{
return|return
name|coordinate
return|;
block|}
comment|/**      * @return true if this endpoint has the null value for its coordinate      */
specifier|public
name|boolean
name|isNull
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isFinite
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|coordinate
argument_list|)
return|;
block|}
comment|/**      * @return target datatype for coordinate      */
specifier|public
name|RelDataType
name|getDataType
parameter_list|()
block|{
return|return
name|dataType
return|;
block|}
comment|/**      * @return boundary type this endpoint represents      */
specifier|public
name|SargBoundType
name|getBoundType
parameter_list|()
block|{
return|return
name|boundType
return|;
block|}
comment|/**      * Tests whether this endpoint "touches" another one (not necessarily      * overlapping). For example, the upper bound of the interval (1, 10)      * touches the lower bound of the interval [10, 20), but not of the interval      * (10, 20).      *      * @param other the other endpoint to test      *      * @return true if touching; false if discontinuous      */
specifier|public
name|boolean
name|isTouching
parameter_list|(
name|SargEndpoint
name|other
parameter_list|)
block|{
assert|assert
operator|(
name|getDataType
argument_list|()
operator|==
name|other
operator|.
name|getDataType
argument_list|()
operator|)
assert|;
if|if
condition|(
operator|!
name|isFinite
argument_list|()
operator|||
operator|!
name|other
operator|.
name|isFinite
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
operator|(
name|coordinate
operator|instanceof
name|RexDynamicParam
operator|)
operator|||
operator|(
name|other
operator|.
name|coordinate
operator|instanceof
name|RexDynamicParam
operator|)
condition|)
block|{
if|if
condition|(
operator|(
name|coordinate
operator|instanceof
name|RexDynamicParam
operator|)
operator|&&
operator|(
name|other
operator|.
name|coordinate
operator|instanceof
name|RexDynamicParam
operator|)
condition|)
block|{
comment|// make sure it's the same param
name|RexDynamicParam
name|p1
init|=
operator|(
name|RexDynamicParam
operator|)
name|coordinate
decl_stmt|;
name|RexDynamicParam
name|p2
init|=
operator|(
name|RexDynamicParam
operator|)
name|other
operator|.
name|coordinate
decl_stmt|;
if|if
condition|(
name|p1
operator|.
name|getIndex
argument_list|()
operator|!=
name|p2
operator|.
name|getIndex
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|// one is a dynamic param but the other isn't
return|return
literal|false
return|;
block|}
block|}
if|else if
condition|(
operator|(
name|coordinate
operator|instanceof
name|RexInputRef
operator|)
operator|||
operator|(
name|other
operator|.
name|coordinate
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
if|if
condition|(
operator|(
name|coordinate
operator|instanceof
name|RexInputRef
operator|)
operator|&&
operator|(
name|other
operator|.
name|coordinate
operator|instanceof
name|RexInputRef
operator|)
condition|)
block|{
comment|// make sure it's the same RexInputRef
name|RexInputRef
name|r1
init|=
operator|(
name|RexInputRef
operator|)
name|coordinate
decl_stmt|;
name|RexInputRef
name|r2
init|=
operator|(
name|RexInputRef
operator|)
name|other
operator|.
name|coordinate
decl_stmt|;
if|if
condition|(
name|r1
operator|.
name|getIndex
argument_list|()
operator|!=
name|r2
operator|.
name|getIndex
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
comment|// one is a RexInputRef but the other isn't
return|return
literal|false
return|;
block|}
block|}
if|else if
condition|(
name|compareCoordinates
argument_list|(
name|coordinate
argument_list|,
name|other
operator|.
name|coordinate
argument_list|)
operator|!=
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|isClosed
argument_list|()
operator|||
name|other
operator|.
name|isClosed
argument_list|()
return|;
block|}
specifier|static
name|int
name|compareCoordinates
parameter_list|(
name|RexNode
name|coord1
parameter_list|,
name|RexNode
name|coord2
parameter_list|)
block|{
assert|assert
operator|(
name|coord1
operator|instanceof
name|RexLiteral
operator|)
assert|;
assert|assert
operator|(
name|coord2
operator|instanceof
name|RexLiteral
operator|)
assert|;
comment|// null values always sort lowest
name|boolean
name|isNull1
init|=
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|coord1
argument_list|)
decl_stmt|;
name|boolean
name|isNull2
init|=
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|coord2
argument_list|)
decl_stmt|;
if|if
condition|(
name|isNull1
operator|&&
name|isNull2
condition|)
block|{
return|return
literal|0
return|;
block|}
if|else if
condition|(
name|isNull1
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|else if
condition|(
name|isNull2
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
name|RexLiteral
name|lit1
init|=
operator|(
name|RexLiteral
operator|)
name|coord1
decl_stmt|;
name|RexLiteral
name|lit2
init|=
operator|(
name|RexLiteral
operator|)
name|coord2
decl_stmt|;
return|return
name|lit1
operator|.
name|getValue
argument_list|()
operator|.
name|compareTo
argument_list|(
name|lit2
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|// implement Object
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isFinite
argument_list|()
condition|)
block|{
if|if
condition|(
name|boundType
operator|==
name|SargBoundType
operator|.
name|LOWER
condition|)
block|{
return|return
literal|"-infinity"
return|;
block|}
else|else
block|{
return|return
literal|"+infinity"
return|;
block|}
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|boundType
operator|==
name|SargBoundType
operator|.
name|LOWER
condition|)
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|">="
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|isClosed
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"<="
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"<"
argument_list|)
expr_stmt|;
block|}
block|}
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|coordinate
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// implement Comparable
specifier|public
name|int
name|compareTo
parameter_list|(
name|SargEndpoint
name|other
parameter_list|)
block|{
if|if
condition|(
name|getInfinitude
argument_list|()
operator|!=
name|other
operator|.
name|getInfinitude
argument_list|()
condition|)
block|{
comment|// at least one is infinite; result is based on comparison of
comment|// infinitudes
return|return
name|getInfinitude
argument_list|()
operator|-
name|other
operator|.
name|getInfinitude
argument_list|()
return|;
block|}
if|if
condition|(
operator|!
name|isFinite
argument_list|()
condition|)
block|{
comment|// both are the same infinity:  equals
return|return
literal|0
return|;
block|}
comment|// both are finite:  compare coordinates
name|int
name|c
init|=
name|compareCoordinates
argument_list|(
name|getCoordinate
argument_list|()
argument_list|,
name|other
operator|.
name|getCoordinate
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|!=
literal|0
condition|)
block|{
return|return
name|c
return|;
block|}
comment|// if coordinates are the same, then result is based on comparison of
comment|// strictness
return|return
name|getStrictnessSign
argument_list|()
operator|-
name|other
operator|.
name|getStrictnessSign
argument_list|()
return|;
block|}
comment|/**      * @return SargStrictness of this bound      */
specifier|public
name|SargStrictness
name|getStrictness
parameter_list|()
block|{
return|return
name|strictness
return|;
block|}
comment|/**      * @return complement of SargStrictness of this bound      */
specifier|public
name|SargStrictness
name|getStrictnessComplement
parameter_list|()
block|{
return|return
operator|(
name|strictness
operator|==
name|SargStrictness
operator|.
name|OPEN
operator|)
condition|?
name|SargStrictness
operator|.
name|CLOSED
else|:
name|SargStrictness
operator|.
name|OPEN
return|;
block|}
comment|/**      * @return -1 for infinitesimally below (open upper bound, strictly less      * than), 0 for exact equality (closed bound), 1 for infinitesimally above      * (open lower bound, strictly greater than)      */
specifier|public
name|int
name|getStrictnessSign
parameter_list|()
block|{
if|if
condition|(
name|strictness
operator|==
name|SargStrictness
operator|.
name|CLOSED
condition|)
block|{
return|return
literal|0
return|;
block|}
else|else
block|{
if|if
condition|(
name|boundType
operator|==
name|SargBoundType
operator|.
name|LOWER
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
comment|// override Object
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|SargEndpoint
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|compareTo
argument_list|(
operator|(
name|SargEndpoint
operator|)
name|other
argument_list|)
operator|==
literal|0
return|;
block|}
comment|// override Object
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

begin_comment
comment|// End SargEndpoint.java
end_comment

end_unit

