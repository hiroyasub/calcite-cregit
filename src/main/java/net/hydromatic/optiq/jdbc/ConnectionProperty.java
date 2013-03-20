begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|jdbc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Properties that may be specified on the JDBC connect string.  */
end_comment

begin_enum
enum|enum
name|ConnectionProperty
block|{
comment|/** Whether to store query results in temporary tables. */
name|AUTO_TEMP
argument_list|(
literal|"autoTemp"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|"false"
argument_list|)
block|,
comment|/** URI of the model. */
name|MODEL
argument_list|(
literal|"model"
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
name|String
name|defaultValue
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ConnectionProperty
argument_list|>
name|NAME_TO_PROPS
decl_stmt|;
static|static
block|{
name|NAME_TO_PROPS
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ConnectionProperty
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|ConnectionProperty
name|property
range|:
name|ConnectionProperty
operator|.
name|values
argument_list|()
control|)
block|{
name|NAME_TO_PROPS
operator|.
name|put
argument_list|(
name|property
operator|.
name|camelName
operator|.
name|toUpperCase
argument_list|()
argument_list|,
name|property
argument_list|)
expr_stmt|;
name|NAME_TO_PROPS
operator|.
name|put
argument_list|(
name|property
operator|.
name|name
argument_list|()
argument_list|,
name|property
argument_list|)
expr_stmt|;
block|}
block|}
name|ConnectionProperty
parameter_list|(
name|String
name|camelName
parameter_list|,
name|Type
name|type
parameter_list|,
name|String
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
block|}
specifier|private
name|String
name|_get
parameter_list|(
name|Properties
name|properties
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
name|parse
argument_list|(
name|properties
argument_list|)
decl_stmt|;
specifier|final
name|String
name|s
init|=
name|map
operator|.
name|get
argument_list|(
name|this
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
name|s
return|;
block|}
return|return
name|defaultValue
return|;
block|}
comment|/** Returns the string value of this property, or null if not specified and      * no default. */
specifier|public
name|String
name|getString
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
assert|assert
name|type
operator|==
name|Type
operator|.
name|STRING
assert|;
return|return
name|_get
argument_list|(
name|properties
argument_list|)
return|;
block|}
comment|/** Returns the boolean value of this property. Throws if not set and no      * default. */
specifier|public
name|boolean
name|getBoolean
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
assert|assert
name|type
operator|==
name|Type
operator|.
name|BOOLEAN
assert|;
name|String
name|s
init|=
name|_get
argument_list|(
name|properties
argument_list|)
decl_stmt|;
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
name|camelName
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
comment|/** Converts a {@link Properties} object containing (name, value) pairs      * into a map whose keys are {@link ConnectionProperty} objects.      *      *<p>Matching is case-insensitive. Throws if a property is not known.      * If a property occurs more than once, takes the last occurrence.</p>      *      * @param properties Properties      * @return Map      * @throws RuntimeException if a property is not known      */
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
name|NAME_TO_PROPS
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
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unknown property '"
operator|+
name|name
operator|+
literal|"'"
argument_list|)
throw|;
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
enum|enum
name|Type
block|{
name|BOOLEAN
block|,
name|STRING
block|}
block|}
end_enum

begin_comment
comment|// End ConnectionProperty.java
end_comment

end_unit

