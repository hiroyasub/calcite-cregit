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

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|runtime
operator|.
name|SpatialTypeUtils
operator|.
name|GEOMETRY_FACTORY
import|;
end_import

begin_comment
comment|/**  * Used at run time by the ST_Collect function.  */
end_comment

begin_class
specifier|public
class|class
name|CollectOperation
block|{
specifier|public
name|List
argument_list|<
name|Geometry
argument_list|>
name|init
parameter_list|()
block|{
return|return
operator|new
name|ArrayList
argument_list|<>
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|Geometry
argument_list|>
name|add
parameter_list|(
name|List
argument_list|<
name|Geometry
argument_list|>
name|accumulator
parameter_list|,
name|Geometry
name|geometry
parameter_list|)
block|{
name|accumulator
operator|.
name|add
argument_list|(
name|geometry
argument_list|)
expr_stmt|;
return|return
name|accumulator
return|;
block|}
specifier|public
name|Geometry
name|result
parameter_list|(
name|List
argument_list|<
name|Geometry
argument_list|>
name|accumulator
parameter_list|)
block|{
name|Geometry
index|[]
name|array
init|=
name|accumulator
operator|.
name|toArray
argument_list|(
operator|new
name|Geometry
index|[
name|accumulator
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
return|return
name|GEOMETRY_FACTORY
operator|.
name|createGeometryCollection
argument_list|(
name|array
argument_list|)
return|;
block|}
block|}
end_class

end_unit

