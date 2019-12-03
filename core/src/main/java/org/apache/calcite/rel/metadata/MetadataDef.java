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
name|rel
operator|.
name|metadata
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
name|rel
operator|.
name|RelNode
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
name|util
operator|.
name|Pair
import|;
end_import

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
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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
comment|/**  * Definition of metadata.  *  * @param<M> Kind of metadata  */
end_comment

begin_class
specifier|public
class|class
name|MetadataDef
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
block|{
specifier|public
specifier|final
name|Class
argument_list|<
name|M
argument_list|>
name|metadataClass
decl_stmt|;
specifier|public
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|M
argument_list|>
argument_list|>
name|handlerClass
decl_stmt|;
specifier|public
specifier|final
name|ImmutableList
argument_list|<
name|Method
argument_list|>
name|methods
decl_stmt|;
specifier|private
name|MetadataDef
parameter_list|(
name|Class
argument_list|<
name|M
argument_list|>
name|metadataClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|M
argument_list|>
argument_list|>
name|handlerClass
parameter_list|,
name|Method
modifier|...
name|methods
parameter_list|)
block|{
name|this
operator|.
name|metadataClass
operator|=
name|metadataClass
expr_stmt|;
name|this
operator|.
name|handlerClass
operator|=
name|handlerClass
expr_stmt|;
name|this
operator|.
name|methods
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|methods
argument_list|)
expr_stmt|;
specifier|final
name|Method
index|[]
name|handlerMethods
init|=
name|handlerClass
operator|.
name|getDeclaredMethods
argument_list|()
decl_stmt|;
comment|// Handler must have the same methods as Metadata, each method having
comment|// additional "subclass-of-RelNode, RelMetadataQuery" parameters.
assert|assert
name|handlerMethods
operator|.
name|length
operator|==
name|methods
operator|.
name|length
assert|;
for|for
control|(
name|Pair
argument_list|<
name|Method
argument_list|,
name|Method
argument_list|>
name|pair
range|:
name|Pair
operator|.
name|zip
argument_list|(
name|methods
argument_list|,
name|handlerMethods
argument_list|)
control|)
block|{
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|leftTypes
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|pair
operator|.
name|left
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|rightTypes
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|pair
operator|.
name|right
operator|.
name|getParameterTypes
argument_list|()
argument_list|)
decl_stmt|;
assert|assert
name|leftTypes
operator|.
name|size
argument_list|()
operator|+
literal|2
operator|==
name|rightTypes
operator|.
name|size
argument_list|()
assert|;
assert|assert
name|RelNode
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|rightTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
assert|;
assert|assert
name|RelMetadataQuery
operator|.
name|class
operator|==
name|rightTypes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
assert|;
assert|assert
name|leftTypes
operator|.
name|equals
argument_list|(
name|rightTypes
operator|.
name|subList
argument_list|(
literal|2
argument_list|,
name|rightTypes
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
assert|;
block|}
block|}
comment|/** Creates a {@link org.apache.calcite.rel.metadata.MetadataDef}. */
specifier|public
specifier|static
parameter_list|<
name|M
extends|extends
name|Metadata
parameter_list|>
name|MetadataDef
argument_list|<
name|M
argument_list|>
name|of
parameter_list|(
name|Class
argument_list|<
name|M
argument_list|>
name|metadataClass
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|MetadataHandler
argument_list|<
name|M
argument_list|>
argument_list|>
name|handlerClass
parameter_list|,
name|Method
modifier|...
name|methods
parameter_list|)
block|{
return|return
operator|new
name|MetadataDef
argument_list|<>
argument_list|(
name|metadataClass
argument_list|,
name|handlerClass
argument_list|,
name|methods
argument_list|)
return|;
block|}
block|}
end_class

end_unit

