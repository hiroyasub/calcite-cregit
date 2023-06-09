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
name|LinearRing
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
name|Polygon
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
comment|/**  * Removes the holes of a geometry.  */
end_comment

begin_class
specifier|public
class|class
name|RemoveHoleTransformer
extends|extends
name|GeometryTransformer
block|{
annotation|@
name|Override
specifier|protected
name|Geometry
name|transformPolygon
parameter_list|(
name|Polygon
name|geom
parameter_list|,
name|Geometry
name|parent
parameter_list|)
block|{
if|if
condition|(
name|geom
operator|==
literal|null
operator|||
name|geom
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|factory
operator|.
name|createPolygon
argument_list|()
return|;
block|}
name|LinearRing
name|exteriorRing
init|=
name|geom
operator|.
name|getExteriorRing
argument_list|()
decl_stmt|;
if|if
condition|(
name|exteriorRing
operator|==
literal|null
operator|||
name|exteriorRing
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|factory
operator|.
name|createPolygon
argument_list|()
return|;
block|}
return|return
name|factory
operator|.
name|createPolygon
argument_list|(
name|exteriorRing
argument_list|)
return|;
block|}
block|}
end_class

end_unit

