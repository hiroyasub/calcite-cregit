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
name|avatica
operator|.
name|remote
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
name|ConnectionProperty
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|avatica
operator|.
name|ConnectionConfigImpl
operator|.
name|PropEnv
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
name|avatica
operator|.
name|ConnectionConfigImpl
operator|.
name|parse
import|;
end_import

begin_comment
comment|/**  * Enumeration of Avatica remote driver's built-in connection properties.  */
end_comment

begin_enum
specifier|public
enum|enum
name|AvaticaRemoteConnectionProperty
implements|implements
name|ConnectionProperty
block|{
comment|/** Factory. */
name|FACTORY
argument_list|(
literal|"factory"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|null
argument_list|)
block|;
specifier|private
specifier|final
name|String
name|camelName
decl_stmt|;
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|private
specifier|final
name|Object
name|defaultValue
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|AvaticaRemoteConnectionProperty
argument_list|>
name|NAME_TO_PROPS
decl_stmt|;
static|static
block|{
name|NAME_TO_PROPS
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|AvaticaRemoteConnectionProperty
name|p
range|:
name|AvaticaRemoteConnectionProperty
operator|.
name|values
argument_list|()
control|)
block|{
name|NAME_TO_PROPS
operator|.
name|put
argument_list|(
name|p
operator|.
name|camelName
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|NAME_TO_PROPS
operator|.
name|put
argument_list|(
name|p
operator|.
name|name
argument_list|()
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
name|AvaticaRemoteConnectionProperty
parameter_list|(
name|String
name|camelName
parameter_list|,
name|Type
name|type
parameter_list|,
name|Object
name|defaultValue
parameter_list|)
block|{
name|this
operator|.
name|camelName
operator|=
name|camelName
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|defaultValue
operator|=
name|defaultValue
expr_stmt|;
assert|assert
name|type
operator|.
name|valid
argument_list|(
name|defaultValue
argument_list|,
name|type
operator|.
name|defaultValueClass
argument_list|()
argument_list|)
assert|;
block|}
specifier|public
name|String
name|camelName
parameter_list|()
block|{
return|return
name|camelName
return|;
block|}
specifier|public
name|Object
name|defaultValue
parameter_list|()
block|{
return|return
name|defaultValue
return|;
block|}
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|Class
name|valueClass
parameter_list|()
block|{
return|return
name|type
operator|.
name|defaultValueClass
argument_list|()
return|;
block|}
specifier|public
name|PropEnv
name|wrap
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
return|return
operator|new
name|PropEnv
argument_list|(
name|parse
argument_list|(
name|properties
argument_list|,
name|NAME_TO_PROPS
argument_list|)
argument_list|,
name|this
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|required
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_enum

begin_comment
comment|// End AvaticaRemoteConnectionProperty.java
end_comment

end_unit

