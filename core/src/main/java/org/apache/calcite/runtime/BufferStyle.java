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
name|operation
operator|.
name|buffer
operator|.
name|BufferParameters
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

begin_comment
comment|/**  * A parser for buffer styles as defined by PostGIS.  */
end_comment

begin_class
specifier|public
class|class
name|BufferStyle
block|{
specifier|private
name|int
name|quadrantSegments
init|=
name|BufferParameters
operator|.
name|DEFAULT_QUADRANT_SEGMENTS
decl_stmt|;
specifier|private
name|int
name|endCapStyle
init|=
name|BufferParameters
operator|.
name|CAP_ROUND
decl_stmt|;
specifier|private
name|int
name|joinStyle
init|=
name|BufferParameters
operator|.
name|JOIN_ROUND
decl_stmt|;
specifier|private
name|int
name|side
init|=
literal|0
decl_stmt|;
specifier|public
name|BufferStyle
parameter_list|(
name|String
name|style
parameter_list|)
block|{
name|String
index|[]
name|parameters
init|=
name|style
operator|.
name|toLowerCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
operator|.
name|split
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|parameter
range|:
name|parameters
control|)
block|{
if|if
condition|(
name|parameter
operator|==
literal|null
operator|||
name|parameter
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|String
index|[]
name|keyValue
init|=
name|parameter
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyValue
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid buffer style: "
operator|+
name|style
argument_list|)
throw|;
block|}
name|String
name|key
init|=
name|keyValue
index|[
literal|0
index|]
decl_stmt|;
name|String
name|value
init|=
name|keyValue
index|[
literal|1
index|]
decl_stmt|;
switch|switch
condition|(
name|key
condition|)
block|{
case|case
literal|"quad_segs"
case|:
try|try
block|{
name|quadrantSegments
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid buffer style: "
operator|+
name|style
argument_list|)
throw|;
block|}
case|case
literal|"endcap"
case|:
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|"round"
case|:
name|endCapStyle
operator|=
name|BufferParameters
operator|.
name|CAP_ROUND
expr_stmt|;
break|break;
case|case
literal|"flat"
case|:
name|endCapStyle
operator|=
name|BufferParameters
operator|.
name|CAP_FLAT
expr_stmt|;
break|break;
case|case
literal|"square"
case|:
name|endCapStyle
operator|=
name|BufferParameters
operator|.
name|CAP_SQUARE
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid buffer style: "
operator|+
name|style
argument_list|)
throw|;
block|}
break|break;
case|case
literal|"join"
case|:
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|"round"
case|:
name|joinStyle
operator|=
name|BufferParameters
operator|.
name|JOIN_ROUND
expr_stmt|;
break|break;
case|case
literal|"mitre"
case|:
name|joinStyle
operator|=
name|BufferParameters
operator|.
name|JOIN_MITRE
expr_stmt|;
break|break;
case|case
literal|"bevel"
case|:
name|joinStyle
operator|=
name|BufferParameters
operator|.
name|JOIN_BEVEL
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid buffer style: "
operator|+
name|style
argument_list|)
throw|;
block|}
break|break;
case|case
literal|"side"
case|:
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|"left"
case|:
name|side
operator|+=
literal|1
expr_stmt|;
break|break;
case|case
literal|"right"
case|:
name|side
operator|-=
literal|1
expr_stmt|;
break|break;
case|case
literal|"both"
case|:
name|side
operator|=
literal|0
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid buffer style: "
operator|+
name|style
argument_list|)
throw|;
block|}
break|break;
block|}
block|}
block|}
comment|/**    * Returns a sided distance.    */
specifier|public
name|double
name|asSidedDistance
parameter_list|(
name|double
name|distance
parameter_list|)
block|{
return|return
name|side
operator|!=
literal|0
condition|?
name|distance
operator|*
name|side
else|:
name|distance
return|;
block|}
comment|/**    * Returns buffer parameters.    */
specifier|public
name|BufferParameters
name|asBufferParameters
parameter_list|()
block|{
name|BufferParameters
name|params
init|=
operator|new
name|BufferParameters
argument_list|()
decl_stmt|;
name|params
operator|.
name|setQuadrantSegments
argument_list|(
name|quadrantSegments
argument_list|)
expr_stmt|;
name|params
operator|.
name|setEndCapStyle
argument_list|(
name|endCapStyle
argument_list|)
expr_stmt|;
name|params
operator|.
name|setJoinStyle
argument_list|(
name|joinStyle
argument_list|)
expr_stmt|;
name|params
operator|.
name|setSingleSided
argument_list|(
name|side
operator|!=
literal|0
argument_list|)
expr_stmt|;
return|return
name|params
return|;
block|}
block|}
end_class

end_unit
