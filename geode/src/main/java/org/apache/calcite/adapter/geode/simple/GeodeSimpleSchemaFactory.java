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
name|simple
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Geode Simple Table Schema Factory.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeSimpleSchemaFactory
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
name|COMMA_DELIMITER
init|=
literal|","
decl_stmt|;
specifier|public
name|GeodeSimpleSchemaFactory
parameter_list|()
block|{
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
specifier|public
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
name|pdxSerializablePackagePath
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
return|return
operator|new
name|GeodeSimpleSchema
argument_list|(
name|locatorHost
argument_list|,
name|locatorPort
argument_list|,
name|regionNames
argument_list|,
name|pdxSerializablePackagePath
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End GeodeSimpleSchemaFactory.java
end_comment

end_unit
