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
name|runtime
package|;
end_package

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
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|BacktrackingQueryBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|BitVector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|BitVectorFactories
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|CompactHilbertCurve
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|FilteredIndexRange
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|LongContent
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|PlainFilterCombiner
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|SimpleRegionInspector
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|ZoomingSpaceVisitorAdapter
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|ranges
operator|.
name|LongRange
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|uzaygezen
operator|.
name|core
operator|.
name|ranges
operator|.
name|LongRangeHome
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/**  * 2-dimensional Hilbert space-filling curve.  *  *<p>Includes code from  *<a href="https://github.com/locationtech/sfcurve">LocationTech SFCurve</a>,  * Copyright (c) 2015 Azavea.  */
end_comment

begin_class
specifier|public
class|class
name|HilbertCurve2D
implements|implements
name|SpaceFillingCurve2D
block|{
specifier|final
name|long
name|precision
decl_stmt|;
specifier|final
name|CompactHilbertCurve
name|chc
decl_stmt|;
specifier|private
specifier|final
name|int
name|resolution
decl_stmt|;
specifier|public
name|HilbertCurve2D
parameter_list|(
name|int
name|resolution
parameter_list|)
block|{
name|this
operator|.
name|resolution
operator|=
name|resolution
expr_stmt|;
name|precision
operator|=
operator|(
name|long
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|resolution
argument_list|)
expr_stmt|;
name|chc
operator|=
operator|new
name|CompactHilbertCurve
argument_list|(
operator|new
name|int
index|[]
block|{
name|resolution
block|,
name|resolution
block|}
argument_list|)
expr_stmt|;
block|}
name|long
name|getNormalizedLongitude
parameter_list|(
name|double
name|x
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
operator|(
operator|(
name|x
operator|+
literal|180
operator|)
operator|*
operator|(
name|precision
operator|-
literal|1
operator|)
operator|/
literal|360d
operator|)
return|;
block|}
name|long
name|getNormalizedLatitude
parameter_list|(
name|double
name|y
parameter_list|)
block|{
return|return
operator|(
name|long
operator|)
operator|(
operator|(
name|y
operator|+
literal|90
operator|)
operator|*
operator|(
name|precision
operator|-
literal|1
operator|)
operator|/
literal|180d
operator|)
return|;
block|}
name|long
name|setNormalizedLatitude
parameter_list|(
name|long
name|latNormal
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|latNormal
operator|>=
literal|0
operator|&&
name|latNormal
operator|<=
name|precision
operator|)
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Normalized latitude must be greater than 0 and less than the maximum precision"
argument_list|)
throw|;
block|}
return|return
operator|(
name|long
operator|)
operator|(
name|latNormal
operator|*
literal|180d
operator|/
operator|(
name|precision
operator|-
literal|1
operator|)
operator|)
return|;
block|}
name|long
name|setNormalizedLongitude
parameter_list|(
name|long
name|lonNormal
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|lonNormal
operator|>=
literal|0
operator|&&
name|lonNormal
operator|<=
name|precision
operator|)
condition|)
block|{
throw|throw
operator|new
name|NumberFormatException
argument_list|(
literal|"Normalized longitude must be greater than 0 and less than the maximum precision"
argument_list|)
throw|;
block|}
return|return
operator|(
name|long
operator|)
operator|(
name|lonNormal
operator|*
literal|360d
operator|/
operator|(
name|precision
operator|-
literal|1
operator|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toIndex
parameter_list|(
name|double
name|x
parameter_list|,
name|double
name|y
parameter_list|)
block|{
specifier|final
name|long
name|normX
init|=
name|getNormalizedLongitude
argument_list|(
name|x
argument_list|)
decl_stmt|;
specifier|final
name|long
name|normY
init|=
name|getNormalizedLatitude
argument_list|(
name|y
argument_list|)
decl_stmt|;
specifier|final
name|BitVector
index|[]
name|p
init|=
block|{
name|BitVectorFactories
operator|.
name|OPTIMAL
operator|.
name|apply
argument_list|(
name|resolution
argument_list|)
block|,
name|BitVectorFactories
operator|.
name|OPTIMAL
operator|.
name|apply
argument_list|(
name|resolution
argument_list|)
block|}
decl_stmt|;
name|p
index|[
literal|0
index|]
operator|.
name|copyFrom
argument_list|(
name|normX
argument_list|)
expr_stmt|;
name|p
index|[
literal|1
index|]
operator|.
name|copyFrom
argument_list|(
name|normY
argument_list|)
expr_stmt|;
specifier|final
name|BitVector
name|hilbert
init|=
name|BitVectorFactories
operator|.
name|OPTIMAL
operator|.
name|apply
argument_list|(
name|resolution
operator|*
literal|2
argument_list|)
decl_stmt|;
name|chc
operator|.
name|index
argument_list|(
name|p
argument_list|,
literal|0
argument_list|,
name|hilbert
argument_list|)
expr_stmt|;
return|return
name|hilbert
operator|.
name|toLong
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Point
name|toPoint
parameter_list|(
name|long
name|i
parameter_list|)
block|{
specifier|final
name|BitVector
name|h
init|=
name|BitVectorFactories
operator|.
name|OPTIMAL
operator|.
name|apply
argument_list|(
name|resolution
operator|*
literal|2
argument_list|)
decl_stmt|;
name|h
operator|.
name|copyFrom
argument_list|(
name|i
argument_list|)
expr_stmt|;
specifier|final
name|BitVector
index|[]
name|p
init|=
block|{
name|BitVectorFactories
operator|.
name|OPTIMAL
operator|.
name|apply
argument_list|(
name|resolution
argument_list|)
block|,
name|BitVectorFactories
operator|.
name|OPTIMAL
operator|.
name|apply
argument_list|(
name|resolution
argument_list|)
block|}
decl_stmt|;
name|chc
operator|.
name|indexInverse
argument_list|(
name|h
argument_list|,
name|p
argument_list|)
expr_stmt|;
specifier|final
name|long
name|x
init|=
name|setNormalizedLongitude
argument_list|(
name|p
index|[
literal|0
index|]
operator|.
name|toLong
argument_list|()
argument_list|)
operator|-
literal|180
decl_stmt|;
specifier|final
name|long
name|y
init|=
name|setNormalizedLatitude
argument_list|(
name|p
index|[
literal|1
index|]
operator|.
name|toLong
argument_list|()
argument_list|)
operator|-
literal|90
decl_stmt|;
return|return
operator|new
name|Point
argument_list|(
operator|(
name|double
operator|)
name|x
argument_list|,
operator|(
name|double
operator|)
name|y
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|IndexRange
argument_list|>
name|toRanges
parameter_list|(
name|double
name|xMin
parameter_list|,
name|double
name|yMin
parameter_list|,
name|double
name|xMax
parameter_list|,
name|double
name|yMax
parameter_list|,
name|RangeComputeHints
name|hints
parameter_list|)
block|{
specifier|final
name|CompactHilbertCurve
name|chc
init|=
operator|new
name|CompactHilbertCurve
argument_list|(
operator|new
name|int
index|[]
block|{
name|resolution
block|,
name|resolution
block|}
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|LongRange
argument_list|>
name|region
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|long
name|minNormalizedLongitude
init|=
name|getNormalizedLongitude
argument_list|(
name|xMin
argument_list|)
decl_stmt|;
specifier|final
name|long
name|minNormalizedLatitude
init|=
name|getNormalizedLatitude
argument_list|(
name|yMin
argument_list|)
decl_stmt|;
specifier|final
name|long
name|maxNormalizedLongitude
init|=
name|getNormalizedLongitude
argument_list|(
name|xMax
argument_list|)
decl_stmt|;
specifier|final
name|long
name|maxNormalizedLatitude
init|=
name|getNormalizedLatitude
argument_list|(
name|yMax
argument_list|)
decl_stmt|;
name|region
operator|.
name|add
argument_list|(
name|LongRange
operator|.
name|of
argument_list|(
name|minNormalizedLongitude
argument_list|,
name|maxNormalizedLongitude
argument_list|)
argument_list|)
expr_stmt|;
name|region
operator|.
name|add
argument_list|(
name|LongRange
operator|.
name|of
argument_list|(
name|minNormalizedLatitude
argument_list|,
name|maxNormalizedLatitude
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|LongContent
name|zero
init|=
operator|new
name|LongContent
argument_list|(
literal|0L
argument_list|)
decl_stmt|;
specifier|final
name|SimpleRegionInspector
argument_list|<
name|LongRange
argument_list|,
name|Long
argument_list|,
name|LongContent
argument_list|,
name|LongRange
argument_list|>
name|inspector
init|=
name|SimpleRegionInspector
operator|.
name|create
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|region
argument_list|)
argument_list|,
operator|new
name|LongContent
argument_list|(
literal|1L
argument_list|)
argument_list|,
name|range
lambda|->
name|range
argument_list|,
name|LongRangeHome
operator|.
name|INSTANCE
argument_list|,
name|zero
argument_list|)
decl_stmt|;
specifier|final
name|PlainFilterCombiner
argument_list|<
name|LongRange
argument_list|,
name|Long
argument_list|,
name|LongContent
argument_list|,
name|LongRange
argument_list|>
name|combiner
init|=
operator|new
name|PlainFilterCombiner
argument_list|<>
argument_list|(
name|LongRange
operator|.
name|of
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|BacktrackingQueryBuilder
argument_list|<
name|LongRange
argument_list|,
name|Long
argument_list|,
name|LongContent
argument_list|,
name|LongRange
argument_list|>
name|queryBuilder
init|=
name|BacktrackingQueryBuilder
operator|.
name|create
argument_list|(
name|inspector
argument_list|,
name|combiner
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|true
argument_list|,
name|LongRangeHome
operator|.
name|INSTANCE
argument_list|,
name|zero
argument_list|)
decl_stmt|;
name|chc
operator|.
name|accept
argument_list|(
operator|new
name|ZoomingSpaceVisitorAdapter
argument_list|(
name|chc
argument_list|,
name|queryBuilder
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Query
argument_list|<
name|LongRange
argument_list|,
name|LongRange
argument_list|>
name|query
init|=
name|queryBuilder
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FilteredIndexRange
argument_list|<
name|LongRange
argument_list|,
name|LongRange
argument_list|>
argument_list|>
name|ranges
init|=
name|query
operator|.
name|getFilteredIndexRanges
argument_list|()
decl_stmt|;
comment|// result
specifier|final
name|List
argument_list|<
name|IndexRange
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FilteredIndexRange
argument_list|<
name|LongRange
argument_list|,
name|LongRange
argument_list|>
name|l
range|:
name|ranges
control|)
block|{
specifier|final
name|LongRange
name|range
init|=
name|l
operator|.
name|getIndexRange
argument_list|()
decl_stmt|;
specifier|final
name|Long
name|start
init|=
name|range
operator|.
name|getStart
argument_list|()
decl_stmt|;
specifier|final
name|Long
name|end
init|=
name|range
operator|.
name|getEnd
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|contained
init|=
name|l
operator|.
name|isPotentialOverSelectivity
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|IndexRanges
operator|.
name|create
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|contained
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

