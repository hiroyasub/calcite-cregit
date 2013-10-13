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
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|java
operator|.
name|JavaTypeFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * Runtime context allowing access to the tables in a database.  */
end_comment

begin_interface
specifier|public
interface|interface
name|DataContext
block|{
comment|/**    * Returns a sub-schema with a given name, or null.    */
name|Schema
name|getRootSchema
parameter_list|()
function_decl|;
comment|/**    * Returns the type factory.    */
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
comment|/**    * Returns a context variable.    *    *<p>Supported variables include: "sparkContext", "currentTimestamp",    * "localTimestamp".</p>    *    * @param name Name of variable    */
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
enum|enum
name|Variable
block|{
comment|/** The time at which the current statement started executing. In      * milliseconds after 1970-01-01 00:00:00, UTC. Required. */
name|CURRENT_TIMESTAMP
argument_list|(
literal|"currentTimestamp"
argument_list|,
name|Long
operator|.
name|class
argument_list|)
block|,
comment|/** The time at which the current statement started executing. In      * milliseconds after 1970-01-01 00:00:00, in the time zone of the current      * statement. Required. */
name|LOCAL_TIMESTAMP
argument_list|(
literal|"localTimestamp"
argument_list|,
name|Long
operator|.
name|class
argument_list|)
block|,
comment|/** The Spark engine. Available if Spark is on the class path. */
name|SPARK_CONTEXT
argument_list|(
literal|"sparkContext"
argument_list|,
name|Object
operator|.
name|class
argument_list|)
block|,
comment|/** Time zone in which the current statement is executing. Required;      * defaults to the time zone of the JVM if the connection does not specify a      * time zone. */
name|TIME_ZONE
argument_list|(
literal|"timeZone"
argument_list|,
name|TimeZone
operator|.
name|class
argument_list|)
block|;
specifier|public
specifier|final
name|String
name|camelName
decl_stmt|;
specifier|public
specifier|final
name|Class
name|clazz
decl_stmt|;
name|Variable
parameter_list|(
name|String
name|camelName
parameter_list|,
name|Class
name|clazz
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
name|clazz
operator|=
name|clazz
expr_stmt|;
assert|assert
name|camelName
operator|.
name|equals
argument_list|(
name|Util
operator|.
name|toCamelCase
argument_list|(
name|name
argument_list|()
argument_list|)
argument_list|)
assert|;
block|}
comment|/** Returns the value of this variable in a given data context. */
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|get
parameter_list|(
name|DataContext
name|dataContext
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
operator|(
name|T
operator|)
name|clazz
operator|.
name|cast
argument_list|(
name|dataContext
operator|.
name|get
argument_list|(
name|camelName
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_interface

begin_comment
comment|// End DataContext.java
end_comment

end_unit

