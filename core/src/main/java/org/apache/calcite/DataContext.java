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
name|adapter
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
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|QueryProvider
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
name|Expressions
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
name|ParameterExpression
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|advise
operator|.
name|SqlAdvisor
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
name|base
operator|.
name|CaseFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
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
name|Modifier
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
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
name|ParameterExpression
name|ROOT
init|=
name|Expressions
operator|.
name|parameter
argument_list|(
name|Modifier
operator|.
name|FINAL
argument_list|,
name|DataContext
operator|.
name|class
argument_list|,
literal|"root"
argument_list|)
decl_stmt|;
comment|/**    * Returns a sub-schema with a given name, or null.    */
name|SchemaPlus
name|getRootSchema
parameter_list|()
function_decl|;
comment|/**    * Returns the type factory.    */
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
comment|/**    * Returns the query provider.    */
name|QueryProvider
name|getQueryProvider
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
comment|/** Variable that may be asked for in a call to {@link DataContext#get}. */
enum|enum
name|Variable
block|{
name|UTC_TIMESTAMP
argument_list|(
literal|"utcTimestamp"
argument_list|,
name|Long
operator|.
name|class
argument_list|)
block|,
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
comment|/** A mutable flag that indicates whether user has requested that the      * current statement be canceled. Cancellation may not be immediate, but      * implementations of relational operators should check the flag fairly      * frequently and cease execution (e.g. by returning end of data). */
name|CANCEL_FLAG
argument_list|(
literal|"cancelFlag"
argument_list|,
name|AtomicBoolean
operator|.
name|class
argument_list|)
block|,
comment|/** Query timeout in milliseconds.      * When no timeout is set, the value is 0 or not present. */
name|TIMEOUT
argument_list|(
literal|"timeout"
argument_list|,
name|Long
operator|.
name|class
argument_list|)
block|,
comment|/** Advisor that suggests completion hints for SQL statements. */
name|SQL_ADVISOR
argument_list|(
literal|"sqlAdvisor"
argument_list|,
name|SqlAdvisor
operator|.
name|class
argument_list|)
block|,
comment|/** Writer to the standard error (stderr). */
name|STDERR
argument_list|(
literal|"stderr"
argument_list|,
name|OutputStream
operator|.
name|class
argument_list|)
block|,
comment|/** Reader on the standard input (stdin). */
name|STDIN
argument_list|(
literal|"stdin"
argument_list|,
name|InputStream
operator|.
name|class
argument_list|)
block|,
comment|/** Writer to the standard output (stdout). */
name|STDOUT
argument_list|(
literal|"stdout"
argument_list|,
name|OutputStream
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
block|,
comment|/** The query user.      *      *<p>Default value is "sa". */
name|USER
argument_list|(
literal|"user"
argument_list|,
name|String
operator|.
name|class
argument_list|)
block|,
comment|/** The system user.      *      *<p>Default value is "user.name" from      * {@link System#getProperty(String)}. */
name|SYSTEM_USER
argument_list|(
literal|"systemUser"
argument_list|,
name|String
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
name|CaseFormat
operator|.
name|UPPER_UNDERSCORE
operator|.
name|to
argument_list|(
name|CaseFormat
operator|.
name|LOWER_CAMEL
argument_list|,
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

