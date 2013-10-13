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
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Enumerator
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Linq4j
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Queryable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|RawEnumerable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|ClassDeclaration
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|Function0
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|*
import|;
end_import

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
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|prepare
operator|.
name|OptiqPrepareImpl
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|Bindable
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|runtime
operator|.
name|ColumnMetaData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
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
name|eigenbase
operator|.
name|relopt
operator|.
name|RelOptPlanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|relopt
operator|.
name|volcano
operator|.
name|VolcanoPlanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|reltype
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|SqlNode
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
name|Reader
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
name|InvocationTargetException
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
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
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
name|*
import|;
end_import

begin_comment
comment|/**  * API for a service that prepares statements for execution.  */
end_comment

begin_interface
specifier|public
interface|interface
name|OptiqPrepare
block|{
name|Function0
argument_list|<
name|OptiqPrepare
argument_list|>
name|DEFAULT_FACTORY
init|=
operator|new
name|Function0
argument_list|<
name|OptiqPrepare
argument_list|>
argument_list|()
block|{
specifier|public
name|OptiqPrepare
name|apply
parameter_list|()
block|{
return|return
operator|new
name|OptiqPrepareImpl
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|ParseResult
name|parse
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|sql
parameter_list|)
function_decl|;
parameter_list|<
name|T
parameter_list|>
name|PrepareResult
argument_list|<
name|T
argument_list|>
name|prepareSql
parameter_list|(
name|Context
name|context
parameter_list|,
name|String
name|sql
parameter_list|,
name|Queryable
argument_list|<
name|T
argument_list|>
name|expression
parameter_list|,
name|Type
name|elementType
parameter_list|,
name|int
name|maxRowCount
parameter_list|)
function_decl|;
parameter_list|<
name|T
parameter_list|>
name|PrepareResult
argument_list|<
name|T
argument_list|>
name|prepareQueryable
parameter_list|(
name|Context
name|context
parameter_list|,
name|Queryable
argument_list|<
name|T
argument_list|>
name|queryable
parameter_list|)
function_decl|;
comment|/** Context for preparing a statement. */
interface|interface
name|Context
block|{
name|JavaTypeFactory
name|getTypeFactory
parameter_list|()
function_decl|;
name|Schema
name|getRootSchema
parameter_list|()
function_decl|;
name|List
argument_list|<
name|String
argument_list|>
name|getDefaultSchemaPath
parameter_list|()
function_decl|;
name|ConnectionProperty
operator|.
name|ConnectionConfig
name|config
parameter_list|()
function_decl|;
comment|/** Returns the spark handler. Never null. */
name|SparkHandler
name|spark
parameter_list|()
function_decl|;
name|DataContext
name|createDataContext
parameter_list|()
function_decl|;
block|}
comment|/** Callback to register Spark as the main engine. */
specifier|public
interface|interface
name|SparkHandler
block|{
name|RelNode
name|flattenTypes
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|boolean
name|restructure
parameter_list|)
function_decl|;
name|void
name|registerRules
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
function_decl|;
name|boolean
name|enabled
parameter_list|()
function_decl|;
name|Bindable
name|compile
parameter_list|(
name|ClassDeclaration
name|expr
parameter_list|,
name|String
name|s
parameter_list|)
function_decl|;
name|Object
name|sparkContext
parameter_list|()
function_decl|;
block|}
specifier|public
specifier|static
class|class
name|Dummy
block|{
specifier|private
specifier|static
name|SparkHandler
name|handler
decl_stmt|;
specifier|public
specifier|static
specifier|synchronized
name|SparkHandler
name|getSparkHandler
parameter_list|()
block|{
if|if
condition|(
name|handler
operator|==
literal|null
condition|)
block|{
name|handler
operator|=
name|createHandler
argument_list|()
expr_stmt|;
block|}
return|return
name|handler
return|;
block|}
specifier|private
specifier|static
name|SparkHandler
name|createHandler
parameter_list|()
block|{
try|try
block|{
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"net.hydromatic.optiq.impl.spark.SparkHandlerImpl"
argument_list|)
decl_stmt|;
name|Method
name|method
init|=
name|clazz
operator|.
name|getMethod
argument_list|(
literal|"INSTANCE"
argument_list|)
decl_stmt|;
return|return
operator|(
name|OptiqPrepare
operator|.
name|SparkHandler
operator|)
name|method
operator|.
name|invoke
argument_list|(
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
return|return
operator|new
name|TrivialSparkHandler
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|ClassCastException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
class|class
name|TrivialSparkHandler
implements|implements
name|SparkHandler
block|{
specifier|public
name|RelNode
name|flattenTypes
parameter_list|(
name|RelOptPlanner
name|planner
parameter_list|,
name|RelNode
name|rootRel
parameter_list|,
name|boolean
name|restructure
parameter_list|)
block|{
return|return
name|rootRel
return|;
block|}
specifier|public
name|void
name|registerRules
parameter_list|(
name|VolcanoPlanner
name|planner
parameter_list|)
block|{
block|}
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|Bindable
name|compile
parameter_list|(
name|ClassDeclaration
name|expr
parameter_list|,
name|String
name|s
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|Object
name|sparkContext
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
specifier|public
specifier|static
class|class
name|ParseResult
block|{
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
comment|// for debug
specifier|public
specifier|final
name|SqlNode
name|sqlNode
decl_stmt|;
specifier|public
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|public
name|ParseResult
parameter_list|(
name|String
name|sql
parameter_list|,
name|SqlNode
name|sqlNode
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|sqlNode
operator|=
name|sqlNode
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
class|class
name|PrepareResult
parameter_list|<
name|T
parameter_list|>
block|{
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
comment|// for debug
specifier|public
specifier|final
name|List
argument_list|<
name|Parameter
argument_list|>
name|parameterList
decl_stmt|;
specifier|public
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columnList
decl_stmt|;
specifier|private
specifier|final
name|RawEnumerable
argument_list|<
name|T
argument_list|>
name|enumerable
decl_stmt|;
specifier|public
specifier|final
name|Class
name|resultClazz
decl_stmt|;
specifier|public
name|PrepareResult
parameter_list|(
name|String
name|sql
parameter_list|,
name|List
argument_list|<
name|Parameter
argument_list|>
name|parameterList
parameter_list|,
name|RelDataType
name|rowType
parameter_list|,
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columnList
parameter_list|,
name|RawEnumerable
argument_list|<
name|T
argument_list|>
name|enumerable
parameter_list|,
name|Class
name|resultClazz
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|parameterList
operator|=
name|parameterList
expr_stmt|;
name|this
operator|.
name|rowType
operator|=
name|rowType
expr_stmt|;
name|this
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
name|this
operator|.
name|enumerable
operator|=
name|enumerable
expr_stmt|;
name|this
operator|.
name|resultClazz
operator|=
name|resultClazz
expr_stmt|;
block|}
specifier|public
name|Enumerator
argument_list|<
name|T
argument_list|>
name|enumerator
parameter_list|()
block|{
return|return
name|enumerable
operator|.
name|enumerator
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
if|if
condition|(
name|enumerable
operator|instanceof
name|Iterable
condition|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|Iterable
argument_list|<
name|T
argument_list|>
name|iterable
init|=
operator|(
name|Iterable
operator|)
name|enumerable
decl_stmt|;
return|return
name|iterable
operator|.
name|iterator
argument_list|()
return|;
block|}
return|return
name|Linq4j
operator|.
name|enumeratorIterator
argument_list|(
name|enumerable
operator|.
name|enumerator
argument_list|()
argument_list|)
return|;
block|}
comment|/** Returns the executable object. You may use this method to access the      * result of the preparation, but if you need to execute it please call      * {@link #enumerator()} or {@link #iterator()}. */
specifier|public
name|Object
name|getExecutable
parameter_list|()
block|{
return|return
name|enumerable
return|;
block|}
block|}
comment|/**    * Metadata for a parameter. Plus a slot to hold its value.    */
specifier|public
specifier|static
class|class
name|Parameter
block|{
specifier|public
specifier|final
name|boolean
name|signed
decl_stmt|;
specifier|public
specifier|final
name|int
name|precision
decl_stmt|;
specifier|public
specifier|final
name|int
name|scale
decl_stmt|;
specifier|public
specifier|final
name|int
name|parameterType
decl_stmt|;
specifier|public
specifier|final
name|String
name|typeName
decl_stmt|;
specifier|public
specifier|final
name|String
name|className
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
name|Object
name|value
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Object
name|DUMMY_VALUE
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|public
name|Parameter
parameter_list|(
name|boolean
name|signed
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|,
name|int
name|parameterType
parameter_list|,
name|String
name|typeName
parameter_list|,
name|String
name|className
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|signed
operator|=
name|signed
expr_stmt|;
name|this
operator|.
name|precision
operator|=
name|precision
expr_stmt|;
name|this
operator|.
name|scale
operator|=
name|scale
expr_stmt|;
name|this
operator|.
name|parameterType
operator|=
name|parameterType
expr_stmt|;
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|className
operator|=
name|className
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|void
name|setByte
parameter_list|(
name|byte
name|o
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|char
name|o
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setShort
parameter_list|(
name|short
name|o
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setInt
parameter_list|(
name|int
name|o
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|long
name|o
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|byte
index|[]
name|o
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBoolean
parameter_list|(
name|boolean
name|o
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|==
literal|null
condition|)
block|{
name|o
operator|=
name|DUMMY_VALUE
expr_stmt|;
block|}
name|this
operator|.
name|value
operator|=
name|o
expr_stmt|;
block|}
specifier|public
name|boolean
name|isSet
parameter_list|()
block|{
return|return
name|value
operator|!=
literal|null
return|;
block|}
specifier|public
name|void
name|setRowId
parameter_list|(
name|RowId
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNCharacterStream
parameter_list|(
name|Reader
name|value
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|NClob
name|value
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setClob
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setSQLXML
parameter_list|(
name|SQLXML
name|xmlObject
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|InputStream
name|x
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|InputStream
name|x
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|Reader
name|reader
parameter_list|,
name|long
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setAsciiStream
parameter_list|(
name|InputStream
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBinaryStream
parameter_list|(
name|InputStream
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setCharacterStream
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNCharacterStream
parameter_list|(
name|Reader
name|value
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setClob
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|InputStream
name|inputStream
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNClob
parameter_list|(
name|Reader
name|reader
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setUnicodeStream
parameter_list|(
name|InputStream
name|x
parameter_list|,
name|int
name|length
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|Timestamp
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setTime
parameter_list|(
name|Time
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setFloat
parameter_list|(
name|float
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setDouble
parameter_list|(
name|double
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBigDecimal
parameter_list|(
name|BigDecimal
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setString
parameter_list|(
name|String
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBytes
parameter_list|(
name|byte
index|[]
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setDate
parameter_list|(
name|Date
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setDate
parameter_list|(
name|Date
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setObject
parameter_list|(
name|Object
name|x
parameter_list|,
name|int
name|targetSqlType
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setObject
parameter_list|(
name|Object
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNull
parameter_list|(
name|int
name|sqlType
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setTime
parameter_list|(
name|Time
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setRef
parameter_list|(
name|Ref
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setBlob
parameter_list|(
name|Blob
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setClob
parameter_list|(
name|Clob
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setArray
parameter_list|(
name|Array
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|Timestamp
name|x
parameter_list|,
name|Calendar
name|cal
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setNull
parameter_list|(
name|int
name|sqlType
parameter_list|,
name|String
name|typeName
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setURL
parameter_list|(
name|URL
name|x
parameter_list|)
block|{
block|}
specifier|public
name|void
name|setObject
parameter_list|(
name|Object
name|x
parameter_list|,
name|int
name|targetSqlType
parameter_list|,
name|int
name|scaleOrLength
parameter_list|)
block|{
block|}
block|}
block|}
end_interface

begin_comment
comment|// End OptiqPrepare.java
end_comment

end_unit

