begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|avatica
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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

begin_comment
comment|/** Implementation of {@link ConnectionConfig}. */
end_comment

begin_class
specifier|public
class|class
name|ConnectionConfigImpl
implements|implements
name|ConnectionConfig
block|{
specifier|protected
specifier|final
name|Properties
name|properties
decl_stmt|;
specifier|public
name|ConnectionConfigImpl
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|this
operator|.
name|properties
operator|=
name|properties
expr_stmt|;
block|}
specifier|public
name|String
name|schema
parameter_list|()
block|{
return|return
name|BuiltInConnectionProperty
operator|.
name|SCHEMA
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getString
argument_list|()
return|;
block|}
specifier|public
name|String
name|timeZone
parameter_list|()
block|{
return|return
name|BuiltInConnectionProperty
operator|.
name|TIMEZONE
operator|.
name|wrap
argument_list|(
name|properties
argument_list|)
operator|.
name|getString
argument_list|()
return|;
block|}
comment|/** Converts a {@link Properties} object containing (name, value)    * pairs into a map whose keys are    * {@link net.hydromatic.avatica.InternalProperty} objects.    *    *<p>Matching is case-insensitive. Throws if a property is not known.    * If a property occurs more than once, takes the last occurrence.</p>    *    * @param properties Properties    * @return Map    * @throws RuntimeException if a property is not known    */
specifier|public
specifier|static
name|Map
argument_list|<
name|ConnectionProperty
argument_list|,
name|String
argument_list|>
name|parse
parameter_list|(
name|Properties
name|properties
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|?
extends|extends
name|ConnectionProperty
argument_list|>
name|nameToProps
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|ConnectionProperty
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|ConnectionProperty
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|properties
operator|.
name|stringPropertyNames
argument_list|()
control|)
block|{
specifier|final
name|ConnectionProperty
name|connectionProperty
init|=
name|nameToProps
operator|.
name|get
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|connectionProperty
operator|==
literal|null
condition|)
block|{
comment|// For now, don't throw. It messes up sub-projects.
comment|//throw new RuntimeException("Unknown property '" + name + "'");
continue|continue;
block|}
name|map
operator|.
name|put
argument_list|(
name|connectionProperty
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
comment|/** The combination of a property definition and a map of property values. */
specifier|public
specifier|static
class|class
name|PropEnv
block|{
specifier|final
name|Map
argument_list|<
name|?
extends|extends
name|ConnectionProperty
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
specifier|private
specifier|final
name|ConnectionProperty
name|property
decl_stmt|;
specifier|public
name|PropEnv
parameter_list|(
name|Map
argument_list|<
name|?
extends|extends
name|ConnectionProperty
argument_list|,
name|String
argument_list|>
name|map
parameter_list|,
name|ConnectionProperty
name|property
parameter_list|)
block|{
name|this
operator|.
name|map
operator|=
name|map
expr_stmt|;
name|this
operator|.
name|property
operator|=
name|property
expr_stmt|;
block|}
specifier|private
parameter_list|<
name|T
parameter_list|>
name|T
name|get_
parameter_list|(
name|Converter
argument_list|<
name|T
argument_list|>
name|converter
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
specifier|final
name|String
name|s
init|=
name|map
operator|.
name|get
argument_list|(
name|property
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
return|return
name|converter
operator|.
name|apply
argument_list|(
name|property
argument_list|,
name|s
argument_list|)
return|;
block|}
return|return
name|converter
operator|.
name|apply
argument_list|(
name|property
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
comment|/** Returns the string value of this property, or null if not specified and      * no default. */
specifier|public
name|String
name|getString
parameter_list|()
block|{
return|return
name|getString
argument_list|(
operator|(
name|String
operator|)
name|property
operator|.
name|defaultValue
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the string value of this property, or null if not specified and      * no default. */
specifier|public
name|String
name|getString
parameter_list|(
name|String
name|defaultValue
parameter_list|)
block|{
assert|assert
name|property
operator|.
name|type
argument_list|()
operator|==
name|ConnectionProperty
operator|.
name|Type
operator|.
name|STRING
assert|;
return|return
name|get_
argument_list|(
name|IDENTITY_CONVERTER
argument_list|,
name|defaultValue
argument_list|)
return|;
block|}
comment|/** Returns the boolean value of this property. Throws if not set and no      * default. */
specifier|public
name|boolean
name|getBoolean
parameter_list|()
block|{
return|return
name|getBoolean
argument_list|(
operator|(
name|Boolean
operator|)
name|property
operator|.
name|defaultValue
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the boolean value of this property. Throws if not set and no      * default. */
specifier|public
name|boolean
name|getBoolean
parameter_list|(
name|boolean
name|defaultValue
parameter_list|)
block|{
assert|assert
name|property
operator|.
name|type
argument_list|()
operator|==
name|ConnectionProperty
operator|.
name|Type
operator|.
name|BOOLEAN
assert|;
return|return
name|get_
argument_list|(
name|BOOLEAN_CONVERTER
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|defaultValue
argument_list|)
argument_list|)
return|;
block|}
comment|/** Returns the enum value of this property. Throws if not set and no      * default. */
specifier|public
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|E
name|getEnum
parameter_list|(
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|getEnum
argument_list|(
name|enumClass
argument_list|,
operator|(
name|E
operator|)
name|property
operator|.
name|defaultValue
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the enum value of this property. Throws if not set and no      * default. */
specifier|public
parameter_list|<
name|E
extends|extends
name|Enum
argument_list|<
name|E
argument_list|>
parameter_list|>
name|E
name|getEnum
parameter_list|(
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
parameter_list|,
name|E
name|defaultValue
parameter_list|)
block|{
assert|assert
name|property
operator|.
name|type
argument_list|()
operator|==
name|ConnectionProperty
operator|.
name|Type
operator|.
name|ENUM
assert|;
comment|//noinspection unchecked
return|return
name|get_
argument_list|(
name|enumConverter
argument_list|(
name|enumClass
argument_list|)
argument_list|,
name|defaultValue
operator|.
name|name
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/** Callback to parse a property from string to its native type. */
specifier|public
interface|interface
name|Converter
parameter_list|<
name|T
parameter_list|>
block|{
name|T
name|apply
parameter_list|(
name|ConnectionProperty
name|connectionProperty
parameter_list|,
name|String
name|s
parameter_list|)
function_decl|;
block|}
specifier|public
specifier|static
specifier|final
name|Converter
argument_list|<
name|Boolean
argument_list|>
name|BOOLEAN_CONVERTER
init|=
operator|new
name|Converter
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
specifier|public
name|Boolean
name|apply
parameter_list|(
name|ConnectionProperty
name|connectionProperty
parameter_list|,
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Required property '"
operator|+
name|connectionProperty
operator|.
name|camelName
argument_list|()
operator|+
literal|"' not specified"
argument_list|)
throw|;
block|}
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Converter
argument_list|<
name|String
argument_list|>
name|IDENTITY_CONVERTER
init|=
operator|new
name|Converter
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|ConnectionProperty
name|connectionProperty
parameter_list|,
name|String
name|s
parameter_list|)
block|{
return|return
name|s
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
parameter_list|<
name|E
extends|extends
name|Enum
parameter_list|>
name|Converter
argument_list|<
name|E
argument_list|>
name|enumConverter
parameter_list|(
specifier|final
name|Class
argument_list|<
name|E
argument_list|>
name|enumClass
parameter_list|)
block|{
return|return
operator|new
name|Converter
argument_list|<
name|E
argument_list|>
argument_list|()
block|{
specifier|public
name|E
name|apply
parameter_list|(
name|ConnectionProperty
name|connectionProperty
parameter_list|,
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Required property '"
operator|+
name|connectionProperty
operator|.
name|camelName
argument_list|()
operator|+
literal|"' not specified"
argument_list|)
throw|;
block|}
try|try
block|{
return|return
operator|(
name|E
operator|)
name|Enum
operator|.
name|valueOf
argument_list|(
name|enumClass
argument_list|,
name|s
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Property '"
operator|+
name|s
operator|+
literal|"' not valid for enum "
operator|+
name|enumClass
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End ConnectionConfigImpl.java
end_comment

end_unit

