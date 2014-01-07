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
name|avatica
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|util
operator|.
name|Calendar
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

begin_comment
comment|/**  * Interface to an iteration that is similar to, and can easily support,  * a JDBC {@link ResultSet}, but is simpler to implement.  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cursor
extends|extends
name|Closeable
block|{
comment|/**    * Creates a list of accessors, one per column.    *    *    * @param types List of column types, per {@link java.sql.Types}.    * @param localCalendar Calendar in local timezone    * @return List of column accessors    */
name|List
argument_list|<
name|Accessor
argument_list|>
name|createAccessors
parameter_list|(
name|List
argument_list|<
name|ColumnMetaData
argument_list|>
name|types
parameter_list|,
name|Calendar
name|localCalendar
parameter_list|)
function_decl|;
comment|/**    * Moves to the next row.    *    * @return Whether moved    */
name|boolean
name|next
parameter_list|()
function_decl|;
comment|/**    * Closes this cursor and releases resources.    */
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Returns whether the last value returned was null.    */
name|boolean
name|wasNull
parameter_list|()
function_decl|;
comment|/**    * Accessor of a column value.    */
specifier|public
interface|interface
name|Accessor
block|{
name|boolean
name|wasNull
parameter_list|()
function_decl|;
name|String
name|getString
parameter_list|()
function_decl|;
name|boolean
name|getBoolean
parameter_list|()
function_decl|;
name|byte
name|getByte
parameter_list|()
function_decl|;
name|short
name|getShort
parameter_list|()
function_decl|;
name|int
name|getInt
parameter_list|()
function_decl|;
name|long
name|getLong
parameter_list|()
function_decl|;
name|float
name|getFloat
parameter_list|()
function_decl|;
name|double
name|getDouble
parameter_list|()
function_decl|;
name|BigDecimal
name|getBigDecimal
parameter_list|()
function_decl|;
name|BigDecimal
name|getBigDecimal
parameter_list|(
name|int
name|scale
parameter_list|)
function_decl|;
name|byte
index|[]
name|getBytes
parameter_list|()
function_decl|;
name|InputStream
name|getAsciiStream
parameter_list|()
function_decl|;
name|InputStream
name|getUnicodeStream
parameter_list|()
function_decl|;
name|InputStream
name|getBinaryStream
parameter_list|()
function_decl|;
name|Object
name|getObject
parameter_list|()
function_decl|;
name|Reader
name|getCharacterStream
parameter_list|()
function_decl|;
name|Object
name|getObject
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|Class
argument_list|<
name|?
argument_list|>
argument_list|>
name|map
parameter_list|)
function_decl|;
name|Ref
name|getRef
parameter_list|()
function_decl|;
name|Blob
name|getBlob
parameter_list|()
function_decl|;
name|Clob
name|getClob
parameter_list|()
function_decl|;
name|Array
name|getArray
parameter_list|()
function_decl|;
name|Date
name|getDate
parameter_list|(
name|Calendar
name|calendar
parameter_list|)
function_decl|;
name|Time
name|getTime
parameter_list|(
name|Calendar
name|calendar
parameter_list|)
function_decl|;
name|Timestamp
name|getTimestamp
parameter_list|(
name|Calendar
name|calendar
parameter_list|)
function_decl|;
name|URL
name|getURL
parameter_list|()
function_decl|;
name|NClob
name|getNClob
parameter_list|()
function_decl|;
name|SQLXML
name|getSQLXML
parameter_list|()
function_decl|;
name|String
name|getNString
parameter_list|()
function_decl|;
name|Reader
name|getNCharacterStream
parameter_list|()
function_decl|;
parameter_list|<
name|T
parameter_list|>
name|T
name|getObject
parameter_list|(
name|Class
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
function_decl|;
block|}
block|}
end_interface

begin_comment
comment|// End Cursor.java
end_comment

end_unit

