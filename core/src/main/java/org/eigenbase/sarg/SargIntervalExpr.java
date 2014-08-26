begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|util
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
name|*
import|;
end_import

begin_comment
comment|/**  * SargIntervalExpr represents an expression which can be resolved to a fixed  * {@link SargInterval}.  *  *<p>Null values require special treatment in expressions. Normally, for  * intervals of any kind, nulls are not considered to be within the domain of  * search values. This behavior can be modified by setting the {@link  * SqlNullSemantics} to a value other than the default. This happens implicitly  * when a point interval is created matching the null value. When null values  * are considered to be part of the domain, the ordering is defined as for  * {@link SargInterval}.  */
end_comment

begin_class
specifier|public
class|class
name|SargIntervalExpr
extends|extends
name|SargIntervalBase
implements|implements
name|SargExpr
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
name|SqlNullSemantics
name|nullSemantics
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * @see SargFactory#newIntervalExpr    */
name|SargIntervalExpr
parameter_list|(
name|SargFactory
name|factory
parameter_list|,
name|RelDataType
name|dataType
parameter_list|,
name|SqlNullSemantics
name|nullSemantics
parameter_list|)
block|{
name|super
argument_list|(
name|factory
argument_list|,
name|dataType
argument_list|)
expr_stmt|;
name|this
operator|.
name|nullSemantics
operator|=
name|nullSemantics
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return null semantics which apply for searches on this interval    */
specifier|public
name|SqlNullSemantics
name|getNullSemantics
parameter_list|()
block|{
return|return
name|nullSemantics
return|;
block|}
comment|// publicize SargIntervalBase
specifier|public
name|void
name|setPoint
parameter_list|(
name|RexNode
name|coordinate
parameter_list|)
block|{
name|super
operator|.
name|setPoint
argument_list|(
name|coordinate
argument_list|)
expr_stmt|;
if|if
condition|(
name|RexLiteral
operator|.
name|isNullLiteral
argument_list|(
name|coordinate
argument_list|)
condition|)
block|{
comment|// since they explicitly asked for the null value as a point,
comment|// adjust the null semantics to match
if|if
condition|(
name|nullSemantics
operator|==
name|SqlNullSemantics
operator|.
name|NULL_MATCHES_NOTHING
condition|)
block|{
name|nullSemantics
operator|=
name|SqlNullSemantics
operator|.
name|NULL_MATCHES_NULL
expr_stmt|;
block|}
block|}
block|}
comment|// publicize SargIntervalBase
specifier|public
name|void
name|setNull
parameter_list|()
block|{
name|super
operator|.
name|setNull
argument_list|()
expr_stmt|;
block|}
comment|// publicize SargIntervalBase
specifier|public
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
name|super
operator|.
name|setLower
argument_list|(
name|coordinate
argument_list|,
name|strictness
argument_list|)
expr_stmt|;
block|}
comment|// publicize SargIntervalBase
specifier|public
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
name|super
operator|.
name|setUpper
argument_list|(
name|coordinate
argument_list|,
name|strictness
argument_list|)
expr_stmt|;
block|}
comment|// publicize SargIntervalBase
specifier|public
name|void
name|unsetLower
parameter_list|()
block|{
name|super
operator|.
name|unsetLower
argument_list|()
expr_stmt|;
block|}
comment|// publicize SargIntervalBase
specifier|public
name|void
name|unsetUpper
parameter_list|()
block|{
name|super
operator|.
name|unsetUpper
argument_list|()
expr_stmt|;
block|}
comment|// publicize SargIntervalBase
specifier|public
name|void
name|setUnconstrained
parameter_list|()
block|{
name|super
operator|.
name|setUnconstrained
argument_list|()
expr_stmt|;
block|}
comment|// publicize SargIntervalBase
specifier|public
name|void
name|setEmpty
parameter_list|()
block|{
name|super
operator|.
name|setEmpty
argument_list|()
expr_stmt|;
block|}
comment|// implement SargExpr
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|s
init|=
name|super
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|nullSemantics
operator|==
name|SqlNullSemantics
operator|.
name|NULL_MATCHES_NOTHING
condition|)
block|{
comment|// default semantics, so omit them for brevity
return|return
name|s
return|;
block|}
else|else
block|{
return|return
name|s
operator|+
literal|" "
operator|+
name|nullSemantics
return|;
block|}
block|}
comment|// implement SargExpr
specifier|public
name|SargIntervalSequence
name|evaluate
parameter_list|()
block|{
name|SargIntervalSequence
name|seq
init|=
operator|new
name|SargIntervalSequence
argument_list|()
decl_stmt|;
comment|// If at least one of the bounds got flipped by overflow, the
comment|// result is empty.
if|if
condition|(
operator|(
name|lowerBound
operator|.
name|getBoundType
argument_list|()
operator|!=
name|SargBoundType
operator|.
name|LOWER
operator|)
operator|||
operator|(
name|upperBound
operator|.
name|getBoundType
argument_list|()
operator|!=
name|SargBoundType
operator|.
name|UPPER
operator|)
condition|)
block|{
comment|// empty sequence
return|return
name|seq
return|;
block|}
comment|// Under the default null semantics, if one of the endpoints is
comment|// known to be null, the result is empty.
if|if
condition|(
operator|(
name|nullSemantics
operator|==
name|SqlNullSemantics
operator|.
name|NULL_MATCHES_NOTHING
operator|)
operator|&&
operator|(
name|lowerBound
operator|.
name|isNull
argument_list|()
operator|||
name|upperBound
operator|.
name|isNull
argument_list|()
operator|)
condition|)
block|{
comment|// empty sequence
return|return
name|seq
return|;
block|}
comment|// Copy the endpoints to the new interval.
name|SargInterval
name|interval
init|=
operator|new
name|SargInterval
argument_list|(
name|factory
argument_list|,
name|getDataType
argument_list|()
argument_list|)
decl_stmt|;
name|interval
operator|.
name|copyFrom
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Then adjust for null semantics.
comment|// REVIEW jvs 17-Jan-2006:  For unconstrained intervals, we
comment|// include null values.  Why is this?
if|if
condition|(
operator|(
name|nullSemantics
operator|==
name|SqlNullSemantics
operator|.
name|NULL_MATCHES_NOTHING
operator|)
operator|&&
name|getDataType
argument_list|()
operator|.
name|isNullable
argument_list|()
operator|&&
operator|(
name|lowerBound
operator|.
name|isFinite
argument_list|()
operator|||
name|upperBound
operator|.
name|isFinite
argument_list|()
operator|)
operator|&&
operator|(
operator|!
name|lowerBound
operator|.
name|isFinite
argument_list|()
operator|||
name|lowerBound
operator|.
name|isNull
argument_list|()
operator|)
condition|)
block|{
comment|// The test above says that this is a constrained range
comment|// with no lower bound (or null for the lower bound).  Since nulls
comment|// aren't supposed to match anything, adjust the lower bound
comment|// to exclude null.
name|interval
operator|.
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
block|}
if|else if
condition|(
name|nullSemantics
operator|==
name|SqlNullSemantics
operator|.
name|NULL_MATCHES_ANYTHING
condition|)
block|{
if|if
condition|(
operator|!
name|lowerBound
operator|.
name|isFinite
argument_list|()
operator|||
name|lowerBound
operator|.
name|isNull
argument_list|()
operator|||
name|upperBound
operator|.
name|isNull
argument_list|()
condition|)
block|{
comment|// Since null is supposed to match anything, and it
comment|// is included in the interval, expand the interval to
comment|// match anything.
name|interval
operator|.
name|setUnconstrained
argument_list|()
expr_stmt|;
block|}
block|}
comment|// NOTE jvs 27-Jan-2006: We don't currently filter out the empty
comment|// interval here because we rely on being able to create them
comment|// explicitly.  See related comment in FennelRelUtil.convertSargExpr;
comment|// if that changes, we could filter out the empty interval here.
name|seq
operator|.
name|addInterval
argument_list|(
name|interval
argument_list|)
expr_stmt|;
return|return
name|seq
return|;
block|}
comment|// implement SargExpr
specifier|public
name|void
name|collectDynamicParams
parameter_list|(
name|Set
argument_list|<
name|RexDynamicParam
argument_list|>
name|dynamicParams
parameter_list|)
block|{
if|if
condition|(
name|lowerBound
operator|.
name|getCoordinate
argument_list|()
operator|instanceof
name|RexDynamicParam
condition|)
block|{
name|dynamicParams
operator|.
name|add
argument_list|(
operator|(
name|RexDynamicParam
operator|)
name|lowerBound
operator|.
name|getCoordinate
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|upperBound
operator|.
name|getCoordinate
argument_list|()
operator|instanceof
name|RexDynamicParam
condition|)
block|{
name|dynamicParams
operator|.
name|add
argument_list|(
operator|(
name|RexDynamicParam
operator|)
name|upperBound
operator|.
name|getCoordinate
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement SargExpr
specifier|public
name|SargIntervalSequence
name|evaluateComplemented
parameter_list|()
block|{
name|SargIntervalSequence
name|originalSeq
init|=
name|evaluate
argument_list|()
decl_stmt|;
name|SargIntervalSequence
name|seq
init|=
operator|new
name|SargIntervalSequence
argument_list|()
decl_stmt|;
comment|// Complement of empty set is unconstrained set.
if|if
condition|(
name|originalSeq
operator|.
name|getList
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|seq
operator|.
name|addInterval
argument_list|(
operator|new
name|SargInterval
argument_list|(
name|factory
argument_list|,
name|getDataType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|seq
return|;
block|}
assert|assert
name|originalSeq
operator|.
name|getList
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
assert|;
name|SargInterval
name|originalInterval
init|=
name|originalSeq
operator|.
name|getList
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// Complement of universal set is empty set.
if|if
condition|(
name|originalInterval
operator|.
name|isUnconstrained
argument_list|()
condition|)
block|{
return|return
name|seq
return|;
block|}
comment|// Use null as a lower bound rather than infinity (see
comment|// http://issues.eigenbase.org/browse/LDB-60).
comment|// REVIEW jvs 17-Apr-2006:  This assumes NULL_MATCHES_NOTHING
comment|// semantics.  We've lost the original null semantics
comment|// flag by now.  Is there ever a case where other null
comment|// semantics are required here?
name|SargInterval
name|interval
init|=
operator|new
name|SargInterval
argument_list|(
name|factory
argument_list|,
name|getDataType
argument_list|()
argument_list|)
decl_stmt|;
name|interval
operator|.
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
if|if
condition|(
name|originalInterval
operator|.
name|getUpperBound
argument_list|()
operator|.
name|isFinite
argument_list|()
operator|&&
name|originalInterval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|isFinite
argument_list|()
condition|)
block|{
comment|// Complement of a fully bounded range is the union of two
comment|// disjoint half-bounded ranges.
name|interval
operator|.
name|setUpper
argument_list|(
name|originalInterval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|getCoordinate
argument_list|()
argument_list|,
name|originalInterval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|getStrictnessComplement
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|originalInterval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|isNull
argument_list|()
condition|)
block|{
name|seq
operator|.
name|addInterval
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Don't bother adding an empty interval.
block|}
name|interval
operator|=
operator|new
name|SargInterval
argument_list|(
name|factory
argument_list|,
name|getDataType
argument_list|()
argument_list|)
expr_stmt|;
name|interval
operator|.
name|setLower
argument_list|(
name|originalInterval
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getCoordinate
argument_list|()
argument_list|,
name|originalInterval
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getStrictnessComplement
argument_list|()
argument_list|)
expr_stmt|;
name|seq
operator|.
name|addInterval
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|originalInterval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|isFinite
argument_list|()
condition|)
block|{
comment|// Complement of a half-bounded range is the opposite
comment|// half-bounded range (with open for closed and vice versa)
name|interval
operator|.
name|setUpper
argument_list|(
name|originalInterval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|getCoordinate
argument_list|()
argument_list|,
name|originalInterval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|getStrictnessComplement
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|originalInterval
operator|.
name|getLowerBound
argument_list|()
operator|.
name|isNull
argument_list|()
condition|)
block|{
name|seq
operator|.
name|addInterval
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Don't bother adding an empty interval.
block|}
block|}
else|else
block|{
comment|// Mirror image of previous case.
assert|assert
name|originalInterval
operator|.
name|getUpperBound
argument_list|()
operator|.
name|isFinite
argument_list|()
assert|;
name|interval
operator|.
name|setLower
argument_list|(
name|originalInterval
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getCoordinate
argument_list|()
argument_list|,
name|originalInterval
operator|.
name|getUpperBound
argument_list|()
operator|.
name|getStrictnessComplement
argument_list|()
argument_list|)
expr_stmt|;
name|seq
operator|.
name|addInterval
argument_list|(
name|interval
argument_list|)
expr_stmt|;
block|}
return|return
name|seq
return|;
block|}
block|}
end_class

begin_comment
comment|// End SargIntervalExpr.java
end_comment

end_unit

