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
name|apache
operator|.
name|calcite
operator|.
name|avatica
operator|.
name|util
operator|.
name|ByteString
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Deterministic
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Experimental
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Strict
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
name|GeometryFactory
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
name|io
operator|.
name|ParseException
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
name|io
operator|.
name|WKBReader
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
name|io
operator|.
name|WKBWriter
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
name|io
operator|.
name|WKTReader
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
name|io
operator|.
name|WKTWriter
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
name|io
operator|.
name|geojson
operator|.
name|GeoJsonReader
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
name|io
operator|.
name|geojson
operator|.
name|GeoJsonWriter
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
name|io
operator|.
name|gml2
operator|.
name|GMLReader
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
name|io
operator|.
name|gml2
operator|.
name|GMLWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_comment
comment|/**  * Utilities for spatial types.  */
end_comment

begin_class
annotation|@
name|Deterministic
annotation|@
name|Strict
annotation|@
name|Experimental
specifier|public
class|class
name|SpatialTypeUtils
block|{
specifier|static
specifier|final
name|int
name|NO_SRID
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|GeometryFactory
name|GEOMETRY_FACTORY
init|=
operator|new
name|GeometryFactory
argument_list|()
decl_stmt|;
specifier|private
name|SpatialTypeUtils
parameter_list|()
block|{
block|}
comment|/** Geometry types, with the names and codes assigned by OGC. */
specifier|public
enum|enum
name|SpatialType
block|{
name|GEOMETRY
argument_list|(
literal|0
argument_list|)
block|,
name|POINT
argument_list|(
literal|1
argument_list|)
block|,
name|LINESTRING
argument_list|(
literal|2
argument_list|)
block|,
name|POLYGON
argument_list|(
literal|3
argument_list|)
block|,
name|MULTIPOINT
argument_list|(
literal|4
argument_list|)
block|,
name|MULTILINESTRING
argument_list|(
literal|5
argument_list|)
block|,
name|MULTIPOLYGON
argument_list|(
literal|6
argument_list|)
block|,
name|GEOMETRYCOLLECTION
argument_list|(
literal|7
argument_list|)
block|;
specifier|private
specifier|final
name|int
name|code
decl_stmt|;
name|SpatialType
parameter_list|(
name|int
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
specifier|public
name|int
name|code
parameter_list|()
block|{
return|return
name|code
return|;
block|}
comment|/** Returns the OGC type of a geometry. */
specifier|public
specifier|static
name|SpatialType
name|fromGeometry
parameter_list|(
name|Geometry
name|g
parameter_list|)
block|{
switch|switch
condition|(
name|g
operator|.
name|getGeometryType
argument_list|()
condition|)
block|{
case|case
literal|"Geometry"
case|:
return|return
name|SpatialType
operator|.
name|GEOMETRY
return|;
case|case
literal|"Point"
case|:
return|return
name|SpatialType
operator|.
name|POINT
return|;
case|case
literal|"LineString"
case|:
return|return
name|SpatialType
operator|.
name|LINESTRING
return|;
case|case
literal|"Polygon"
case|:
return|return
name|SpatialType
operator|.
name|POLYGON
return|;
case|case
literal|"MultiPoint"
case|:
return|return
name|SpatialType
operator|.
name|MULTIPOINT
return|;
case|case
literal|"MultiLineString"
case|:
return|return
name|SpatialType
operator|.
name|MULTILINESTRING
return|;
case|case
literal|"MultiPolygon"
case|:
return|return
name|SpatialType
operator|.
name|MULTIPOLYGON
return|;
case|case
literal|"GeometryCollection"
case|:
return|return
name|SpatialType
operator|.
name|GEOMETRYCOLLECTION
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
name|g
argument_list|)
throw|;
block|}
block|}
block|}
specifier|private
specifier|static
name|int
name|dimension
parameter_list|(
name|Geometry
name|geometry
parameter_list|)
block|{
name|int
name|dimension
init|=
literal|3
decl_stmt|;
for|for
control|(
name|Coordinate
name|coordinate
range|:
name|geometry
operator|.
name|getCoordinates
argument_list|()
control|)
block|{
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|coordinate
operator|.
name|getZ
argument_list|()
argument_list|)
condition|)
block|{
name|dimension
operator|=
literal|2
expr_stmt|;
break|break;
block|}
block|}
return|return
name|dimension
return|;
block|}
comment|/**    * Constructs a geometry from a GeoJson representation.    *    * @param geoJson a GeoJson    * @return a geometry    */
specifier|public
specifier|static
name|Geometry
name|fromGeoJson
parameter_list|(
name|String
name|geoJson
parameter_list|)
block|{
try|try
block|{
name|GeoJsonReader
name|reader
init|=
operator|new
name|GeoJsonReader
argument_list|()
decl_stmt|;
return|return
name|reader
operator|.
name|read
argument_list|(
name|geoJson
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to parse GeoJSON"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Constructs a geometry from a GML representation.    *    * @param gml a GML    * @return a geometry    */
specifier|public
specifier|static
name|Geometry
name|fromGml
parameter_list|(
name|String
name|gml
parameter_list|)
block|{
try|try
block|{
name|GMLReader
name|reader
init|=
operator|new
name|GMLReader
argument_list|()
decl_stmt|;
return|return
name|reader
operator|.
name|read
argument_list|(
name|gml
argument_list|,
name|GEOMETRY_FACTORY
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
decl||
name|IOException
decl||
name|ParserConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to parse GML"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Constructs a geometry from a Well-Known binary (WKB) representation.    *    * @param wkb a WKB    * @return a geometry    */
specifier|public
specifier|static
name|Geometry
name|fromWkb
parameter_list|(
name|ByteString
name|wkb
parameter_list|)
block|{
try|try
block|{
name|WKBReader
name|reader
init|=
operator|new
name|WKBReader
argument_list|()
decl_stmt|;
return|return
name|reader
operator|.
name|read
argument_list|(
name|wkb
operator|.
name|getBytes
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to parse WKB"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Constructs a geometry from an Extended Well-Known text (EWKT) representation.    * EWKT representations are prefixed with the SRID.    *    * @param ewkt an EWKT    * @return a geometry    */
specifier|public
specifier|static
name|Geometry
name|fromEwkt
parameter_list|(
name|String
name|ewkt
parameter_list|)
block|{
name|Pattern
name|pattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"^(?:srid:(\\d*);)?(.*)$"
argument_list|,
name|Pattern
operator|.
name|DOTALL
argument_list|)
decl_stmt|;
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
name|matcher
init|=
name|pattern
operator|.
name|matcher
argument_list|(
name|ewkt
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|matcher
operator|.
name|matches
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to parse EWKT"
argument_list|)
throw|;
block|}
name|String
name|wkt
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|wkt
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to parse EWKT"
argument_list|)
throw|;
block|}
name|Geometry
name|geometry
init|=
name|fromWkt
argument_list|(
name|wkt
argument_list|)
decl_stmt|;
name|String
name|srid
init|=
name|matcher
operator|.
name|group
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|srid
operator|!=
literal|null
condition|)
block|{
name|geometry
operator|.
name|setSRID
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|srid
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|geometry
return|;
block|}
comment|/**    * Constructs a geometry from a Well-Known text (WKT) representation.    *    * @param wkt a WKT    * @return a geometry    */
specifier|public
specifier|static
name|Geometry
name|fromWkt
parameter_list|(
name|String
name|wkt
parameter_list|)
block|{
try|try
block|{
name|WKTReader
name|reader
init|=
operator|new
name|WKTReader
argument_list|()
decl_stmt|;
return|return
name|reader
operator|.
name|read
argument_list|(
name|wkt
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to parse WKT"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Returns the GeoJson representation of the geometry.    *    * @param geometry a geometry    * @return a GeoJson    */
specifier|public
specifier|static
name|String
name|asGeoJson
parameter_list|(
name|Geometry
name|geometry
parameter_list|)
block|{
name|GeoJsonWriter
name|geoJsonWriter
init|=
operator|new
name|GeoJsonWriter
argument_list|()
decl_stmt|;
return|return
name|geoJsonWriter
operator|.
name|write
argument_list|(
name|geometry
argument_list|)
return|;
block|}
comment|/**    * Returns the GML representation of the geometry.    *    * @param geometry a geometry    * @return a GML    */
specifier|public
specifier|static
name|String
name|asGml
parameter_list|(
name|Geometry
name|geometry
parameter_list|)
block|{
name|GMLWriter
name|gmlWriter
init|=
operator|new
name|GMLWriter
argument_list|()
decl_stmt|;
comment|// remove line breaks and indentation
name|String
name|minified
init|=
name|gmlWriter
operator|.
name|write
argument_list|(
name|geometry
argument_list|)
operator|.
name|replace
argument_list|(
literal|"\n"
argument_list|,
literal|""
argument_list|)
operator|.
name|replace
argument_list|(
literal|"  "
argument_list|,
literal|""
argument_list|)
decl_stmt|;
return|return
name|minified
return|;
block|}
comment|/**    * Returns the Extended Well-Known binary (WKB) representation of the geometry.    *    * @param geometry a geometry    * @return an WKB    */
specifier|public
specifier|static
name|ByteString
name|asWkb
parameter_list|(
name|Geometry
name|geometry
parameter_list|)
block|{
name|int
name|outputDimension
init|=
name|dimension
argument_list|(
name|geometry
argument_list|)
decl_stmt|;
name|WKBWriter
name|wkbWriter
init|=
operator|new
name|WKBWriter
argument_list|(
name|outputDimension
argument_list|)
decl_stmt|;
return|return
operator|new
name|ByteString
argument_list|(
name|wkbWriter
operator|.
name|write
argument_list|(
name|geometry
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the Extended Well-Known text (EWKT) representation of the geometry.    * EWKT representations are prefixed with the SRID.    *    * @param geometry a geometry    * @return an EWKT    */
specifier|public
specifier|static
name|String
name|asEwkt
parameter_list|(
name|Geometry
name|geometry
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"srid:%s;%s"
argument_list|,
name|geometry
operator|.
name|getSRID
argument_list|()
argument_list|,
name|asWkt
argument_list|(
name|geometry
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Returns the Well-Known text (WKT) representation of the geometry.    *    * @param geometry a geometry    * @return a WKT    */
specifier|public
specifier|static
name|String
name|asWkt
parameter_list|(
name|Geometry
name|geometry
parameter_list|)
block|{
name|int
name|outputDimension
init|=
name|dimension
argument_list|(
name|geometry
argument_list|)
decl_stmt|;
name|WKTWriter
name|wktWriter
init|=
operator|new
name|WKTWriter
argument_list|(
name|outputDimension
argument_list|)
decl_stmt|;
return|return
name|wktWriter
operator|.
name|write
argument_list|(
name|geometry
argument_list|)
return|;
block|}
block|}
end_class

end_unit

