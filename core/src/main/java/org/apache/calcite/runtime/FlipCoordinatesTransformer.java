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
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * Flips the coordinates of a geometry.  */
end_comment

begin_class
specifier|public
class|class
name|FlipCoordinatesTransformer
extends|extends
name|GeometryTransformer
block|{
annotation|@
name|Override
specifier|protected
name|CoordinateSequence
name|transformCoordinates
parameter_list|(
name|CoordinateSequence
name|coordinateSequence
parameter_list|,
name|Geometry
name|parent
parameter_list|)
block|{
name|Coordinate
index|[]
name|coordinateArray
init|=
name|Stream
operator|.
name|of
argument_list|(
name|coordinateSequence
operator|.
name|toCoordinateArray
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|c
lambda|->
operator|new
name|Coordinate
argument_list|(
name|c
operator|.
name|y
argument_list|,
name|c
operator|.
name|x
argument_list|)
argument_list|)
operator|.
name|toArray
argument_list|(
name|Coordinate
index|[]
operator|::
operator|new
argument_list|)
decl_stmt|;
return|return
operator|new
name|CoordinateArraySequence
argument_list|(
name|coordinateArray
argument_list|)
return|;
block|}
block|}
end_class

end_unit

