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
name|util
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
name|DateTimeUtils
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
name|tree
operator|.
name|Primitive
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|rel
operator|.
name|type
operator|.
name|RelDataTypeField
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
name|Util
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|CacheClosedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|GemFireCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|Region
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|RegionExistsException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|client
operator|.
name|ClientCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|client
operator|.
name|ClientCacheFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|client
operator|.
name|ClientRegionShortcut
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|cache
operator|.
name|query
operator|.
name|Struct
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|pdx
operator|.
name|PdxInstance
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|geode
operator|.
name|pdx
operator|.
name|ReflectionBasedAutoSerializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Field
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
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_comment
comment|/**  * Utilities for the Geode adapter.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeUtils
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GeodeUtils
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Cache for the client proxy regions created in the current ClientCache.    */
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Region
argument_list|>
name|REGION_MAP
init|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|String
name|currentLocatorHost
init|=
literal|""
decl_stmt|;
specifier|private
specifier|static
name|int
name|currentLocatorPort
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|JavaTypeFactoryExtImpl
name|JAVA_TYPE_FACTORY
init|=
operator|new
name|JavaTypeFactoryExtImpl
argument_list|()
decl_stmt|;
specifier|private
name|GeodeUtils
parameter_list|()
block|{
block|}
comment|/**    * Creates a Geode client instance connected to locator and configured to    * support PDX instances.    *    *<p>If an old instance exists, it will be destroyed and re-created.    *    * @param locatorHost               Locator's host address    * @param locatorPort               Locator's port    * @param autoSerializerPackagePath package name of the Domain classes loaded in the regions    * @return Returns a Geode {@link ClientCache} instance connected to Geode cluster    */
specifier|public
specifier|static
specifier|synchronized
name|ClientCache
name|createClientCache
parameter_list|(
name|String
name|locatorHost
parameter_list|,
name|int
name|locatorPort
parameter_list|,
name|String
name|autoSerializerPackagePath
parameter_list|,
name|boolean
name|readSerialized
parameter_list|)
block|{
if|if
condition|(
name|locatorPort
operator|!=
name|currentLocatorPort
operator|||
operator|!
name|StringUtils
operator|.
name|equalsIgnoreCase
argument_list|(
name|currentLocatorHost
argument_list|,
name|locatorHost
argument_list|)
condition|)
block|{
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Close existing ClientCache ["
operator|+
name|currentLocatorHost
operator|+
literal|":"
operator|+
name|currentLocatorPort
operator|+
literal|"] for new Locator connection at: ["
operator|+
name|locatorHost
operator|+
literal|":"
operator|+
name|locatorPort
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|currentLocatorHost
operator|=
name|locatorHost
expr_stmt|;
name|currentLocatorPort
operator|=
name|locatorPort
expr_stmt|;
name|closeClientCache
argument_list|()
expr_stmt|;
block|}
try|try
block|{
comment|// If exists returns the existing client cache. This requires that the pre-created
comment|// client proxy regions can also be resolved from the regionMap
return|return
name|ClientCacheFactory
operator|.
name|getAnyInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|CacheClosedException
name|cce
parameter_list|)
block|{
comment|// Do nothing if there is no existing instance
block|}
return|return
operator|new
name|ClientCacheFactory
argument_list|()
operator|.
name|addPoolLocator
argument_list|(
name|locatorHost
argument_list|,
name|locatorPort
argument_list|)
operator|.
name|setPdxSerializer
argument_list|(
operator|new
name|ReflectionBasedAutoSerializer
argument_list|(
name|autoSerializerPackagePath
argument_list|)
argument_list|)
operator|.
name|setPdxReadSerialized
argument_list|(
name|readSerialized
argument_list|)
operator|.
name|create
argument_list|()
return|;
block|}
specifier|public
specifier|static
specifier|synchronized
name|void
name|closeClientCache
parameter_list|()
block|{
try|try
block|{
name|ClientCacheFactory
operator|.
name|getAnyInstance
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CacheClosedException
name|cce
parameter_list|)
block|{
comment|// Do nothing if there is no existing instance
block|}
name|REGION_MAP
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|/**    * Obtains a proxy pointing to an existing Region on the server.    *    * @param cache {@link GemFireCache} instance to interact with the Geode server    * @param regionName  Name of the region to create proxy for.    * @return Returns a Region proxy to a remote (on the Server) regions.    */
specifier|public
specifier|static
specifier|synchronized
name|Region
name|createRegion
parameter_list|(
name|GemFireCache
name|cache
parameter_list|,
name|String
name|regionName
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|cache
argument_list|,
literal|"cache"
argument_list|)
expr_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|regionName
argument_list|,
literal|"regionName"
argument_list|)
expr_stmt|;
name|Region
name|region
init|=
name|REGION_MAP
operator|.
name|get
argument_list|(
name|regionName
argument_list|)
decl_stmt|;
if|if
condition|(
name|region
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|region
operator|=
operator|(
operator|(
name|ClientCache
operator|)
name|cache
operator|)
operator|.
name|createClientRegionFactory
argument_list|(
name|ClientRegionShortcut
operator|.
name|PROXY
argument_list|)
operator|.
name|create
argument_list|(
name|regionName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
decl||
name|RegionExistsException
name|e
parameter_list|)
block|{
comment|// means this is a server cache (probably part of embedded testing
comment|// or clientCache is passed directly)
name|region
operator|=
name|cache
operator|.
name|getRegion
argument_list|(
name|regionName
argument_list|)
expr_stmt|;
block|}
name|REGION_MAP
operator|.
name|put
argument_list|(
name|regionName
argument_list|,
name|region
argument_list|)
expr_stmt|;
block|}
return|return
name|region
return|;
block|}
comment|/**    * Converts a Geode object into a Row tuple.    *    * @param relDataTypeFields Table relation types    * @param geodeResultObject Object value returned by Geode query    * @return List of objects values corresponding to the relDataTypeFields    */
specifier|public
specifier|static
name|Object
name|convertToRowValues
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|relDataTypeFields
parameter_list|,
name|Object
name|geodeResultObject
parameter_list|)
block|{
name|Object
name|values
decl_stmt|;
if|if
condition|(
name|geodeResultObject
operator|instanceof
name|Struct
condition|)
block|{
name|values
operator|=
name|handleStructEntry
argument_list|(
name|relDataTypeFields
argument_list|,
name|geodeResultObject
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|geodeResultObject
operator|instanceof
name|PdxInstance
condition|)
block|{
name|values
operator|=
name|handlePdxInstanceEntry
argument_list|(
name|relDataTypeFields
argument_list|,
name|geodeResultObject
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
name|handleJavaObjectEntry
argument_list|(
name|relDataTypeFields
argument_list|,
name|geodeResultObject
argument_list|)
expr_stmt|;
block|}
return|return
name|values
return|;
block|}
specifier|private
specifier|static
name|Object
name|handleStructEntry
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|relDataTypeFields
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|Struct
name|struct
init|=
operator|(
name|Struct
operator|)
name|obj
decl_stmt|;
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
name|relDataTypeFields
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|relDataTypeField
range|:
name|relDataTypeFields
control|)
block|{
name|Type
name|javaType
init|=
name|JAVA_TYPE_FACTORY
operator|.
name|getJavaClass
argument_list|(
name|relDataTypeField
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|rawValue
decl_stmt|;
try|try
block|{
name|rawValue
operator|=
name|struct
operator|.
name|get
argument_list|(
name|relDataTypeField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|rawValue
operator|=
literal|"<error>"
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Could find field : "
operator|+
name|relDataTypeField
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|values
index|[
name|index
operator|++
index|]
operator|=
name|convert
argument_list|(
name|rawValue
argument_list|,
operator|(
name|Class
operator|)
name|javaType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|values
index|[
literal|0
index|]
return|;
block|}
return|return
name|values
return|;
block|}
specifier|private
specifier|static
name|Object
name|handlePdxInstanceEntry
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|relDataTypeFields
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|PdxInstance
name|pdxEntry
init|=
operator|(
name|PdxInstance
operator|)
name|obj
decl_stmt|;
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
name|relDataTypeFields
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|relDataTypeField
range|:
name|relDataTypeFields
control|)
block|{
name|Type
name|javaType
init|=
name|JAVA_TYPE_FACTORY
operator|.
name|getJavaClass
argument_list|(
name|relDataTypeField
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
name|Object
name|rawValue
init|=
name|pdxEntry
operator|.
name|getField
argument_list|(
name|relDataTypeField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|values
index|[
name|index
operator|++
index|]
operator|=
name|convert
argument_list|(
name|rawValue
argument_list|,
operator|(
name|Class
operator|)
name|javaType
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|values
index|[
literal|0
index|]
return|;
block|}
return|return
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"CatchAndPrintStackTrace"
argument_list|)
specifier|private
specifier|static
name|Object
name|handleJavaObjectEntry
parameter_list|(
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|relDataTypeFields
parameter_list|,
name|Object
name|obj
parameter_list|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|obj
operator|.
name|getClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|relDataTypeFields
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
try|try
block|{
name|Field
name|javaField
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
name|relDataTypeFields
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|javaField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|javaField
operator|.
name|get
argument_list|(
name|obj
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
name|Object
index|[]
name|values
init|=
operator|new
name|Object
index|[
name|relDataTypeFields
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|relDataTypeField
range|:
name|relDataTypeFields
control|)
block|{
try|try
block|{
name|Field
name|javaField
init|=
name|clazz
operator|.
name|getDeclaredField
argument_list|(
name|relDataTypeField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|javaField
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|values
index|[
name|index
operator|++
index|]
operator|=
name|javaField
operator|.
name|get
argument_list|(
name|obj
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|values
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"JavaUtilDate"
argument_list|)
specifier|private
specifier|static
name|Object
name|convert
parameter_list|(
name|Object
name|o
parameter_list|,
name|Class
name|clazz
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Primitive
name|primitive
init|=
name|Primitive
operator|.
name|of
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
if|if
condition|(
name|primitive
operator|!=
literal|null
condition|)
block|{
name|clazz
operator|=
name|primitive
operator|.
name|boxClass
expr_stmt|;
block|}
else|else
block|{
name|primitive
operator|=
name|Primitive
operator|.
name|ofBox
argument_list|(
name|clazz
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clazz
operator|==
literal|null
condition|)
block|{
return|return
name|o
operator|.
name|toString
argument_list|()
return|;
block|}
if|if
condition|(
name|Map
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|clazz
argument_list|)
operator|&&
name|o
operator|instanceof
name|PdxInstance
condition|)
block|{
comment|// This is in case of nested Objects!
return|return
name|Util
operator|.
name|toString
argument_list|(
operator|(
operator|(
name|PdxInstance
operator|)
name|o
operator|)
operator|.
name|getFieldNames
argument_list|()
argument_list|,
literal|"PDX["
argument_list|,
literal|","
argument_list|,
literal|"]"
argument_list|)
return|;
block|}
if|if
condition|(
name|clazz
operator|.
name|isInstance
argument_list|(
name|o
argument_list|)
condition|)
block|{
return|return
name|o
return|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Date
operator|&&
name|primitive
operator|!=
literal|null
condition|)
block|{
name|o
operator|=
operator|(
operator|(
name|Date
operator|)
name|o
operator|)
operator|.
name|getTime
argument_list|()
operator|/
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
expr_stmt|;
block|}
if|if
condition|(
name|o
operator|instanceof
name|Number
operator|&&
name|primitive
operator|!=
literal|null
condition|)
block|{
return|return
name|primitive
operator|.
name|number
argument_list|(
operator|(
name|Number
operator|)
name|o
argument_list|)
return|;
block|}
return|return
name|o
return|;
block|}
comment|/**    * Extract the first entity of each Regions and use it to build a table types.    *    * @param region existing region    * @return derived data type.    */
specifier|public
specifier|static
name|RelDataType
name|autodetectRelTypeFromRegion
parameter_list|(
name|Region
argument_list|<
name|?
argument_list|,
name|?
argument_list|>
name|region
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|region
argument_list|,
literal|"region"
argument_list|)
expr_stmt|;
comment|// try to detect type using value constraints (if they exists)
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|constraint
init|=
name|region
operator|.
name|getAttributes
argument_list|()
operator|.
name|getValueConstraint
argument_list|()
decl_stmt|;
if|if
condition|(
name|constraint
operator|!=
literal|null
operator|&&
operator|!
name|PdxInstance
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|constraint
argument_list|)
condition|)
block|{
return|return
operator|new
name|JavaTypeFactoryExtImpl
argument_list|()
operator|.
name|createStructType
argument_list|(
name|constraint
argument_list|)
return|;
block|}
specifier|final
name|Iterator
argument_list|<
name|?
argument_list|>
name|iter
decl_stmt|;
if|if
condition|(
name|region
operator|.
name|getAttributes
argument_list|()
operator|.
name|getPoolName
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// means current cache is server (not ClientCache)
name|iter
operator|=
name|region
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// for ClientCache
name|iter
operator|=
name|region
operator|.
name|keySetOnServer
argument_list|()
operator|.
name|iterator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|iter
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|String
name|message
init|=
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"Region %s is empty, can't "
operator|+
literal|"autodetect type(s)"
argument_list|,
name|region
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|IllegalStateException
argument_list|(
name|message
argument_list|)
throw|;
block|}
specifier|final
name|Object
name|entry
init|=
name|region
operator|.
name|get
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|createRelDataType
argument_list|(
name|entry
argument_list|)
return|;
block|}
comment|// Create Relational Type by inferring a Geode entry or response instance.
specifier|private
specifier|static
name|RelDataType
name|createRelDataType
parameter_list|(
name|Object
name|regionEntry
parameter_list|)
block|{
name|JavaTypeFactoryExtImpl
name|typeFactory
init|=
operator|new
name|JavaTypeFactoryExtImpl
argument_list|()
decl_stmt|;
if|if
condition|(
name|regionEntry
operator|instanceof
name|PdxInstance
condition|)
block|{
return|return
name|typeFactory
operator|.
name|createPdxType
argument_list|(
operator|(
name|PdxInstance
operator|)
name|regionEntry
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|typeFactory
operator|.
name|createStructType
argument_list|(
name|regionEntry
operator|.
name|getClass
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

