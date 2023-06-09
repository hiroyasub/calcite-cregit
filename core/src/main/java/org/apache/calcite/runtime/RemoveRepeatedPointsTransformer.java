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
name|org
operator|.
name|locationtech
operator|.
name|jts
operator|.
name|geom
operator|.
name|Coordinate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|jts
operator|.
name|geom
operator|.
name|CoordinateSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|jts
operator|.
name|geom
operator|.
name|Geometry
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|jts
operator|.
name|geom
operator|.
name|impl
operator|.
name|CoordinateArraySequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|locationtech
operator|.
name|jts
operator|.
name|geom
operator|.
name|util
operator|.
name|GeometryTransformer
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
comment|/**  * Removes repeated points from a geometry.  */
end_comment

begin_class
specifier|public
class|class
name|RemoveRepeatedPointsTransformer
extends|extends
name|GeometryTransformer
block|{
specifier|private
name|double
name|tolerance
init|=
literal|0
decl_stmt|;
specifier|public
name|RemoveRepeatedPointsTransformer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|RemoveRepeatedPointsTransformer
parameter_list|(
name|double
name|tolerance
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|tolerance
operator|=
name|tolerance
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|CoordinateSequence
name|transformCoordinates
parameter_list|(
name|CoordinateSequence
name|coordinates
parameter_list|,
name|Geometry
name|parent
parameter_list|)
block|{
name|List
argument_list|<
name|Coordinate
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Coordinate
name|previous
init|=
name|coordinates
operator|.
name|getCoordinate
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|previous
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|coordinates
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Coordinate
name|current
init|=
name|coordinates
operator|.
name|getCoordinate
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|double
name|distance
init|=
name|current
operator|.
name|distance
argument_list|(
name|previous
argument_list|)
decl_stmt|;
if|if
condition|(
name|distance
operator|>
name|tolerance
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|previous
operator|=
name|current
expr_stmt|;
block|}
block|}
name|Coordinate
name|last
init|=
name|coordinates
operator|.
name|getCoordinate
argument_list|(
name|coordinates
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
name|double
name|distance
init|=
name|last
operator|.
name|distance
argument_list|(
name|previous
argument_list|)
decl_stmt|;
if|if
condition|(
name|distance
operator|<=
name|tolerance
condition|)
block|{
name|list
operator|.
name|set
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|,
name|last
argument_list|)
expr_stmt|;
block|}
name|Coordinate
index|[]
name|array
init|=
name|list
operator|.
name|toArray
argument_list|(
operator|new
name|Coordinate
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
operator|new
name|CoordinateArraySequence
argument_list|(
name|array
argument_list|)
return|;
block|}
block|}
end_class

end_unit

