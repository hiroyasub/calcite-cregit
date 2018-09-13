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
name|adapter
operator|.
name|geode
operator|.
name|rel
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
name|model
operator|.
name|ModelHandler
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
name|runtime
operator|.
name|GeoFunctions
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
name|schema
operator|.
name|Schema
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
name|schema
operator|.
name|SchemaFactory
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
name|schema
operator|.
name|SchemaPlus
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory that creates a {@link GeodeSchema}.  */
end_comment

begin_class
annotation|@
name|SuppressWarnings
argument_list|(
literal|"UnusedDeclaration"
argument_list|)
specifier|public
class|class
name|GeodeSchemaFactory
implements|implements
name|SchemaFactory
block|{
specifier|public
specifier|static
specifier|final
name|String
name|LOCATOR_HOST
init|=
literal|"locatorHost"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LOCATOR_PORT
init|=
literal|"locatorPort"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REGIONS
init|=
literal|"regions"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PDX_SERIALIZABLE_PACKAGE_PATH
init|=
literal|"pdxSerializablePackagePath"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ALLOW_SPATIAL_FUNCTIONS
init|=
literal|"spatialFunction"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COMMA_DELIMITER
init|=
literal|","
decl_stmt|;
specifier|public
name|GeodeSchemaFactory
parameter_list|()
block|{
comment|// Do Nothing
block|}
specifier|public
specifier|synchronized
name|Schema
name|create
parameter_list|(
name|SchemaPlus
name|parentSchema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|)
block|{
name|Map
name|map
init|=
operator|(
name|Map
operator|)
name|operand
decl_stmt|;
name|String
name|locatorHost
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|LOCATOR_HOST
argument_list|)
decl_stmt|;
name|int
name|locatorPort
init|=
name|Integer
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|LOCATOR_PORT
argument_list|)
argument_list|)
decl_stmt|;
name|String
index|[]
name|regionNames
init|=
operator|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|REGIONS
argument_list|)
operator|)
operator|.
name|split
argument_list|(
name|COMMA_DELIMITER
argument_list|)
decl_stmt|;
name|String
name|pbxSerializablePackagePath
init|=
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|PDX_SERIALIZABLE_PACKAGE_PATH
argument_list|)
decl_stmt|;
name|boolean
name|allowSpatialFunctions
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|map
operator|.
name|containsKey
argument_list|(
name|ALLOW_SPATIAL_FUNCTIONS
argument_list|)
condition|)
block|{
name|allowSpatialFunctions
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
operator|(
name|String
operator|)
name|map
operator|.
name|get
argument_list|(
name|ALLOW_SPATIAL_FUNCTIONS
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|allowSpatialFunctions
condition|)
block|{
name|ModelHandler
operator|.
name|addFunctions
argument_list|(
name|parentSchema
argument_list|,
literal|null
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|()
argument_list|,
name|GeoFunctions
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
literal|"*"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|GeodeSchema
argument_list|(
name|locatorHost
argument_list|,
name|locatorPort
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|regionNames
argument_list|)
argument_list|,
name|pbxSerializablePackagePath
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End GeodeSchemaFactory.java
end_comment

end_unit

