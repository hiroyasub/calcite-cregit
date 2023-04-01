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

begin_comment
comment|/**  * Operation that adds a z value to a geometry.  */
end_comment

begin_class
specifier|public
class|class
name|AddZTransformer
extends|extends
name|GeometryTransformer
block|{
specifier|private
specifier|final
name|double
name|zToAdd
decl_stmt|;
specifier|public
name|AddZTransformer
parameter_list|(
name|double
name|zToAdd
parameter_list|)
block|{
name|this
operator|.
name|zToAdd
operator|=
name|zToAdd
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
name|Coordinate
index|[]
name|newCoordinates
init|=
operator|new
name|Coordinate
index|[
name|coordinates
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
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
if|if
condition|(
operator|!
name|Double
operator|.
name|isNaN
argument_list|(
name|current
operator|.
name|z
argument_list|)
condition|)
block|{
name|newCoordinates
index|[
name|i
index|]
operator|=
operator|new
name|Coordinate
argument_list|(
name|current
operator|.
name|x
argument_list|,
name|current
operator|.
name|y
argument_list|,
name|current
operator|.
name|z
operator|+
name|zToAdd
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|CoordinateArraySequence
argument_list|(
name|newCoordinates
argument_list|)
return|;
block|}
block|}
end_class

end_unit
