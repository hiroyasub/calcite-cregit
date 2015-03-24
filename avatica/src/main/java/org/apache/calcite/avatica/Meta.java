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
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonCreator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonIgnore
import|;
end_import

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|annotation
operator|.
name|JsonProperty
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
name|sql
operator|.
name|SQLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/**  * Command handler for getting various metadata. Should be implemented by each  * driver.  *  *<p>Also holds other abstract methods that are not related to metadata  * that each provider must implement. This is not ideal.</p>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Meta
block|{
name|String
name|getSqlKeywords
parameter_list|()
function_decl|;
name|String
name|getNumericFunctions
parameter_list|()
function_decl|;
name|String
name|getStringFunctions
parameter_list|()
function_decl|;
name|String
name|getSystemFunctions
parameter_list|()
function_decl|;
name|String
name|getTimeDateFunctions
parameter_list|()
function_decl|;
name|MetaResultSet
name|getTables
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|typeList
parameter_list|)
function_decl|;
name|MetaResultSet
name|getColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getSchemas
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getCatalogs
parameter_list|()
function_decl|;
name|MetaResultSet
name|getTableTypes
parameter_list|()
function_decl|;
name|MetaResultSet
name|getProcedures
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|procedureNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getProcedureColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|procedureNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getColumnPrivileges
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getTablePrivileges
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getBestRowIdentifier
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|,
name|int
name|scope
parameter_list|,
name|boolean
name|nullable
parameter_list|)
function_decl|;
name|MetaResultSet
name|getVersionColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|)
function_decl|;
name|MetaResultSet
name|getPrimaryKeys
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|)
function_decl|;
name|MetaResultSet
name|getImportedKeys
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|)
function_decl|;
name|MetaResultSet
name|getExportedKeys
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|)
function_decl|;
name|MetaResultSet
name|getCrossReference
parameter_list|(
name|String
name|parentCatalog
parameter_list|,
name|String
name|parentSchema
parameter_list|,
name|String
name|parentTable
parameter_list|,
name|String
name|foreignCatalog
parameter_list|,
name|String
name|foreignSchema
parameter_list|,
name|String
name|foreignTable
parameter_list|)
function_decl|;
name|MetaResultSet
name|getTypeInfo
parameter_list|()
function_decl|;
name|MetaResultSet
name|getIndexInfo
parameter_list|(
name|String
name|catalog
parameter_list|,
name|String
name|schema
parameter_list|,
name|String
name|table
parameter_list|,
name|boolean
name|unique
parameter_list|,
name|boolean
name|approximate
parameter_list|)
function_decl|;
name|MetaResultSet
name|getUDTs
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|typeNamePattern
parameter_list|,
name|int
index|[]
name|types
parameter_list|)
function_decl|;
name|MetaResultSet
name|getSuperTypes
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|typeNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getSuperTables
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getAttributes
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|typeNamePattern
parameter_list|,
name|Pat
name|attributeNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getClientInfoProperties
parameter_list|()
function_decl|;
name|MetaResultSet
name|getFunctions
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|functionNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getFunctionColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|functionNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
name|MetaResultSet
name|getPseudoColumns
parameter_list|(
name|String
name|catalog
parameter_list|,
name|Pat
name|schemaPattern
parameter_list|,
name|Pat
name|tableNamePattern
parameter_list|,
name|Pat
name|columnNamePattern
parameter_list|)
function_decl|;
comment|/** Creates an iterable for a result set.    *    *<p>The default implementation just returns {@code iterable}, which it    * requires to be not null; derived classes may instead choose to execute the    * relational expression in {@code signature}. */
name|Iterable
argument_list|<
name|Object
argument_list|>
name|createIterable
parameter_list|(
name|StatementHandle
name|handle
parameter_list|,
name|Signature
name|signature
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|parameterValues
parameter_list|,
name|Frame
name|firstFrame
parameter_list|)
function_decl|;
comment|/** Prepares a statement.    *    * @param ch Connection handle    * @param sql SQL query    * @param maxRowCount Negative for no limit (different meaning than JDBC)    * @return Signature of prepared statement    */
name|StatementHandle
name|prepare
parameter_list|(
name|ConnectionHandle
name|ch
parameter_list|,
name|String
name|sql
parameter_list|,
name|int
name|maxRowCount
parameter_list|)
function_decl|;
comment|/** Prepares and executes a statement.    *    * @param ch Connection handle    * @param sql SQL query    * @param maxRowCount Negative for no limit (different meaning than JDBC)    * @param callback Callback to lock, clear and assign cursor    * @return MetaResultSet containing statement ID and first frame of data    */
name|MetaResultSet
name|prepareAndExecute
parameter_list|(
name|ConnectionHandle
name|ch
parameter_list|,
name|String
name|sql
parameter_list|,
name|int
name|maxRowCount
parameter_list|,
name|PrepareCallback
name|callback
parameter_list|)
function_decl|;
comment|/** Returns a frame of rows.    *    *<p>The frame describes whether there may be another frame. If there is not    * another frame, the current iteration is done when we have finished the    * rows in the this frame.    *    *<p>The default implementation always returns null.    *    * @param h Statement handle    * @param parameterValues A list of parameter values, if statement is to be    *                        executed; otherwise null    * @param offset Zero-based offset of first row in the requested frame    * @param fetchMaxRowCount Maximum number of rows to return; negative means    * no limit    * @return Frame, or null if there are no more    */
name|Frame
name|fetch
parameter_list|(
name|StatementHandle
name|h
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|parameterValues
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|fetchMaxRowCount
parameter_list|)
function_decl|;
comment|/** Called during the creation of a statement to allocate a new handle.    *    * @param ch Connection handle    */
name|StatementHandle
name|createStatement
parameter_list|(
name|ConnectionHandle
name|ch
parameter_list|)
function_decl|;
comment|/** Close a statement.    */
name|void
name|closeStatement
parameter_list|(
name|StatementHandle
name|h
parameter_list|)
function_decl|;
comment|/** Close a connection */
name|void
name|closeConnection
parameter_list|(
name|ConnectionHandle
name|ch
parameter_list|)
function_decl|;
comment|/** Factory to create instances of {@link Meta}. */
interface|interface
name|Factory
block|{
name|Meta
name|create
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|args
parameter_list|)
function_decl|;
block|}
comment|/** Wrapper to remind API calls that a parameter is a pattern (allows '%' and    * '_' wildcards, per the JDBC spec) rather than a string to be matched    * exactly. */
class|class
name|Pat
block|{
specifier|public
specifier|final
name|String
name|s
decl_stmt|;
specifier|private
name|Pat
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|this
operator|.
name|s
operator|=
name|s
expr_stmt|;
block|}
annotation|@
name|JsonCreator
specifier|public
specifier|static
name|Pat
name|of
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"s"
argument_list|)
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|Pat
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
comment|/** Meta data from which a result set can be constructed. */
class|class
name|MetaResultSet
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|int
name|statementId
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|ownStatement
decl_stmt|;
specifier|public
specifier|final
name|Frame
name|firstFrame
decl_stmt|;
specifier|public
specifier|final
name|Signature
name|signature
decl_stmt|;
specifier|public
name|MetaResultSet
parameter_list|(
name|String
name|connectionId
parameter_list|,
name|int
name|statementId
parameter_list|,
name|boolean
name|ownStatement
parameter_list|,
name|Signature
name|signature
parameter_list|,
name|Frame
name|firstFrame
parameter_list|)
block|{
name|this
operator|.
name|signature
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|signature
argument_list|)
expr_stmt|;
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|statementId
operator|=
name|statementId
expr_stmt|;
name|this
operator|.
name|ownStatement
operator|=
name|ownStatement
expr_stmt|;
name|this
operator|.
name|firstFrame
operator|=
name|firstFrame
expr_stmt|;
comment|// may be null
block|}
block|}
comment|/** Information necessary to convert an {@link Iterable} into a    * {@link org.apache.calcite.avatica.util.Cursor}. */
specifier|final
class|class
name|CursorFactory
block|{
specifier|public
specifier|final
name|Style
name|style
decl_stmt|;
specifier|public
specifier|final
name|Class
name|clazz
decl_stmt|;
annotation|@
name|JsonIgnore
specifier|public
specifier|final
name|List
argument_list|<
name|Field
argument_list|>
name|fields
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
decl_stmt|;
specifier|private
name|CursorFactory
parameter_list|(
name|Style
name|style
parameter_list|,
name|Class
name|clazz
parameter_list|,
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
assert|assert
operator|(
name|fieldNames
operator|!=
literal|null
operator|)
operator|==
operator|(
name|style
operator|==
name|Style
operator|.
name|RECORD_PROJECTION
operator|||
name|style
operator|==
name|Style
operator|.
name|MAP
operator|)
assert|;
assert|assert
operator|(
name|fields
operator|!=
literal|null
operator|)
operator|==
operator|(
name|style
operator|==
name|Style
operator|.
name|RECORD_PROJECTION
operator|)
assert|;
name|this
operator|.
name|style
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|style
argument_list|)
expr_stmt|;
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
name|this
operator|.
name|fields
operator|=
name|fields
expr_stmt|;
name|this
operator|.
name|fieldNames
operator|=
name|fieldNames
expr_stmt|;
block|}
annotation|@
name|JsonCreator
specifier|public
specifier|static
name|CursorFactory
name|create
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"style"
argument_list|)
name|Style
name|style
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"clazz"
argument_list|)
name|Class
name|clazz
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"fieldNames"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
switch|switch
condition|(
name|style
condition|)
block|{
case|case
name|OBJECT
case|:
return|return
name|OBJECT
return|;
case|case
name|ARRAY
case|:
return|return
name|ARRAY
return|;
case|case
name|LIST
case|:
return|return
name|LIST
return|;
case|case
name|RECORD
case|:
return|return
name|record
argument_list|(
name|clazz
argument_list|)
return|;
case|case
name|RECORD_PROJECTION
case|:
return|return
name|record
argument_list|(
name|clazz
argument_list|,
literal|null
argument_list|,
name|fieldNames
argument_list|)
return|;
case|case
name|MAP
case|:
return|return
name|map
argument_list|(
name|fieldNames
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown style: "
operator|+
name|style
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
specifier|final
name|CursorFactory
name|OBJECT
init|=
operator|new
name|CursorFactory
argument_list|(
name|Style
operator|.
name|OBJECT
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|CursorFactory
name|ARRAY
init|=
operator|new
name|CursorFactory
argument_list|(
name|Style
operator|.
name|ARRAY
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|CursorFactory
name|LIST
init|=
operator|new
name|CursorFactory
argument_list|(
name|Style
operator|.
name|LIST
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|CursorFactory
name|record
parameter_list|(
name|Class
name|resultClazz
parameter_list|)
block|{
return|return
operator|new
name|CursorFactory
argument_list|(
name|Style
operator|.
name|RECORD
argument_list|,
name|resultClazz
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|CursorFactory
name|record
parameter_list|(
name|Class
name|resultClass
parameter_list|,
name|List
argument_list|<
name|Field
argument_list|>
name|fields
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
if|if
condition|(
name|fields
operator|==
literal|null
condition|)
block|{
name|fields
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|String
name|fieldName
range|:
name|fieldNames
control|)
block|{
try|try
block|{
name|fields
operator|.
name|add
argument_list|(
name|resultClass
operator|.
name|getField
argument_list|(
name|fieldName
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchFieldException
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
block|}
return|return
operator|new
name|CursorFactory
argument_list|(
name|Style
operator|.
name|RECORD_PROJECTION
argument_list|,
name|resultClass
argument_list|,
name|fields
argument_list|,
name|fieldNames
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|CursorFactory
name|map
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|fieldNames
parameter_list|)
block|{
return|return
operator|new
name|CursorFactory
argument_list|(
name|Style
operator|.
name|MAP
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|fieldNames
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|CursorFactory
name|deduce
parameter_list|(
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columns
parameter_list|,
name|Class
name|resultClazz
parameter_list|)
block|{
if|if
condition|(
name|columns
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|OBJECT
return|;
block|}
if|else if
condition|(
name|resultClazz
operator|!=
literal|null
operator|&&
operator|!
name|resultClazz
operator|.
name|isArray
argument_list|()
condition|)
block|{
return|return
name|record
argument_list|(
name|resultClazz
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|ARRAY
return|;
block|}
block|}
block|}
comment|/** How logical fields are represented in the objects returned by the    * iterator. */
enum|enum
name|Style
block|{
name|OBJECT
block|,
name|RECORD
block|,
name|RECORD_PROJECTION
block|,
name|ARRAY
block|,
name|LIST
block|,
name|MAP
block|}
comment|/** Result of preparing a statement. */
class|class
name|Signature
block|{
specifier|public
specifier|final
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columns
decl_stmt|;
specifier|public
specifier|final
name|String
name|sql
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|AvaticaParameter
argument_list|>
name|parameters
decl_stmt|;
specifier|public
specifier|final
specifier|transient
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|internalParameters
decl_stmt|;
specifier|public
specifier|final
name|CursorFactory
name|cursorFactory
decl_stmt|;
comment|/** Creates a Signature. */
specifier|public
name|Signature
parameter_list|(
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columns
parameter_list|,
name|String
name|sql
parameter_list|,
name|List
argument_list|<
name|AvaticaParameter
argument_list|>
name|parameters
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|internalParameters
parameter_list|,
name|CursorFactory
name|cursorFactory
parameter_list|)
block|{
name|this
operator|.
name|columns
operator|=
name|columns
expr_stmt|;
name|this
operator|.
name|sql
operator|=
name|sql
expr_stmt|;
name|this
operator|.
name|parameters
operator|=
name|parameters
expr_stmt|;
name|this
operator|.
name|internalParameters
operator|=
name|internalParameters
expr_stmt|;
name|this
operator|.
name|cursorFactory
operator|=
name|cursorFactory
expr_stmt|;
block|}
comment|/** Used by Jackson to create a Signature by de-serializing JSON. */
annotation|@
name|JsonCreator
specifier|public
specifier|static
name|Signature
name|create
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"columns"
argument_list|)
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|columns
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"sql"
argument_list|)
name|String
name|sql
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"parameters"
argument_list|)
name|List
argument_list|<
name|AvaticaParameter
argument_list|>
name|parameters
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"cursorFactory"
argument_list|)
name|CursorFactory
name|cursorFactory
parameter_list|)
block|{
return|return
operator|new
name|Signature
argument_list|(
name|columns
argument_list|,
name|sql
argument_list|,
name|parameters
argument_list|,
name|Collections
operator|.
expr|<
name|String
argument_list|,
name|Object
operator|>
name|emptyMap
argument_list|()
argument_list|,
name|cursorFactory
argument_list|)
return|;
block|}
comment|/** Returns a copy of this Signature, substituting given CursorFactory. */
specifier|public
name|Signature
name|setCursorFactory
parameter_list|(
name|CursorFactory
name|cursorFactory
parameter_list|)
block|{
return|return
operator|new
name|Signature
argument_list|(
name|columns
argument_list|,
name|sql
argument_list|,
name|parameters
argument_list|,
name|internalParameters
argument_list|,
name|cursorFactory
argument_list|)
return|;
block|}
comment|/** Creates a copy of this Signature with null lists and maps converted to      * empty. */
specifier|public
name|Signature
name|sanitize
parameter_list|()
block|{
if|if
condition|(
name|columns
operator|==
literal|null
operator|||
name|parameters
operator|==
literal|null
operator|||
name|internalParameters
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|Signature
argument_list|(
name|sanitize
argument_list|(
name|columns
argument_list|)
argument_list|,
name|sql
argument_list|,
name|sanitize
argument_list|(
name|parameters
argument_list|)
argument_list|,
name|sanitize
argument_list|(
name|internalParameters
argument_list|)
argument_list|,
name|cursorFactory
argument_list|)
return|;
block|}
return|return
name|this
return|;
block|}
specifier|private
parameter_list|<
name|E
parameter_list|>
name|List
argument_list|<
name|E
argument_list|>
name|sanitize
parameter_list|(
name|List
argument_list|<
name|E
argument_list|>
name|list
parameter_list|)
block|{
return|return
name|list
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|E
operator|>
name|emptyList
argument_list|()
else|:
name|list
return|;
block|}
specifier|private
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|sanitize
parameter_list|(
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
parameter_list|)
block|{
return|return
name|map
operator|==
literal|null
condition|?
name|Collections
operator|.
expr|<
name|K
operator|,
name|V
operator|>
name|emptyMap
argument_list|()
operator|:
name|map
return|;
block|}
block|}
comment|/** A collection of rows. */
class|class
name|Frame
block|{
comment|/** Frame that has zero rows and is the last frame. */
specifier|public
specifier|static
specifier|final
name|Frame
name|EMPTY
init|=
operator|new
name|Frame
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Frame that has zero rows but may have another frame. */
specifier|public
specifier|static
specifier|final
name|Frame
name|MORE
init|=
operator|new
name|Frame
argument_list|(
literal|0
argument_list|,
literal|false
argument_list|,
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
comment|/** Zero-based offset of first row. */
specifier|public
specifier|final
name|int
name|offset
decl_stmt|;
comment|/** Whether this is definitely the last frame of rows.      * If true, there are no more rows.      * If false, there may or may not be more rows. */
specifier|public
specifier|final
name|boolean
name|done
decl_stmt|;
comment|/** The rows. */
specifier|public
specifier|final
name|Iterable
argument_list|<
name|Object
argument_list|>
name|rows
decl_stmt|;
specifier|public
name|Frame
parameter_list|(
name|int
name|offset
parameter_list|,
name|boolean
name|done
parameter_list|,
name|Iterable
argument_list|<
name|Object
argument_list|>
name|rows
parameter_list|)
block|{
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|done
operator|=
name|done
expr_stmt|;
name|this
operator|.
name|rows
operator|=
name|rows
expr_stmt|;
block|}
annotation|@
name|JsonCreator
specifier|public
specifier|static
name|Frame
name|create
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"offset"
argument_list|)
name|int
name|offset
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"done"
argument_list|)
name|boolean
name|done
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"rows"
argument_list|)
name|List
argument_list|<
name|Object
argument_list|>
name|rows
parameter_list|)
block|{
if|if
condition|(
name|offset
operator|==
literal|0
operator|&&
name|done
operator|&&
name|rows
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|EMPTY
return|;
block|}
return|return
operator|new
name|Frame
argument_list|(
name|offset
argument_list|,
name|done
argument_list|,
name|rows
argument_list|)
return|;
block|}
block|}
comment|/** Connection handle. */
class|class
name|ConnectionHandle
block|{
specifier|public
specifier|final
name|String
name|id
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|JsonCreator
specifier|public
name|ConnectionHandle
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"id"
argument_list|)
name|String
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
block|}
comment|/** Statement handle. */
class|class
name|StatementHandle
block|{
specifier|public
specifier|final
name|String
name|connectionId
decl_stmt|;
specifier|public
specifier|final
name|int
name|id
decl_stmt|;
comment|// not final because LocalService#apply(PrepareRequest)
comment|/** Only present for PreparedStatement handles, null otherwise. */
specifier|public
name|Signature
name|signature
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|connectionId
operator|+
literal|"::"
operator|+
name|Integer
operator|.
name|toString
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|JsonCreator
specifier|public
name|StatementHandle
parameter_list|(
annotation|@
name|JsonProperty
argument_list|(
literal|"connectionId"
argument_list|)
name|String
name|connectionId
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"id"
argument_list|)
name|int
name|id
parameter_list|,
annotation|@
name|JsonProperty
argument_list|(
literal|"signature"
argument_list|)
name|Signature
name|signature
parameter_list|)
block|{
name|this
operator|.
name|connectionId
operator|=
name|connectionId
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|signature
operator|=
name|signature
expr_stmt|;
block|}
block|}
comment|/** API to put a result set into a statement, being careful to enforce    * thread-safety and not to overwrite existing open result sets. */
interface|interface
name|PrepareCallback
block|{
name|Object
name|getMonitor
parameter_list|()
function_decl|;
name|void
name|clear
parameter_list|()
throws|throws
name|SQLException
function_decl|;
name|void
name|assign
parameter_list|(
name|Signature
name|signature
parameter_list|,
name|Frame
name|firstFrame
parameter_list|)
throws|throws
name|SQLException
function_decl|;
name|void
name|execute
parameter_list|()
throws|throws
name|SQLException
function_decl|;
block|}
block|}
end_interface

begin_comment
comment|// End Meta.java
end_comment

end_unit

