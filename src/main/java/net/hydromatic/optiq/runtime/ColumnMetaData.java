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
name|runtime
package|;
end_package

begin_comment
comment|/**  * Metadata for a column.  * (Compare with {@link java.sql.ResultSetMetaData}.)  */
end_comment

begin_class
specifier|public
class|class
name|ColumnMetaData
block|{
specifier|public
specifier|final
name|int
name|ordinal
decl_stmt|;
comment|// 0-based
specifier|public
specifier|final
name|boolean
name|autoIncrement
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|caseSensitive
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|searchable
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|currency
decl_stmt|;
specifier|public
specifier|final
name|int
name|nullable
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|signed
decl_stmt|;
specifier|public
specifier|final
name|int
name|displaySize
decl_stmt|;
specifier|public
specifier|final
name|String
name|label
decl_stmt|;
specifier|public
specifier|final
name|String
name|columnName
decl_stmt|;
specifier|public
specifier|final
name|String
name|schemaName
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
name|String
name|tableName
decl_stmt|;
specifier|public
specifier|final
name|String
name|catalogName
decl_stmt|;
specifier|public
specifier|final
name|int
name|type
decl_stmt|;
specifier|public
specifier|final
name|String
name|typeName
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|readOnly
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|writable
decl_stmt|;
specifier|public
specifier|final
name|boolean
name|definitelyWritable
decl_stmt|;
specifier|public
specifier|final
name|String
name|columnClassName
decl_stmt|;
comment|/** The type of the field that holds the value. Not a JDBC property. */
specifier|public
specifier|final
name|Class
name|internalClass
decl_stmt|;
specifier|public
name|ColumnMetaData
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|boolean
name|autoIncrement
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|,
name|boolean
name|searchable
parameter_list|,
name|boolean
name|currency
parameter_list|,
name|int
name|nullable
parameter_list|,
name|boolean
name|signed
parameter_list|,
name|int
name|displaySize
parameter_list|,
name|String
name|label
parameter_list|,
name|String
name|columnName
parameter_list|,
name|String
name|schemaName
parameter_list|,
name|int
name|precision
parameter_list|,
name|int
name|scale
parameter_list|,
name|String
name|tableName
parameter_list|,
name|String
name|catalogName
parameter_list|,
name|int
name|type
parameter_list|,
name|String
name|typeName
parameter_list|,
name|boolean
name|readOnly
parameter_list|,
name|boolean
name|writable
parameter_list|,
name|boolean
name|definitelyWritable
parameter_list|,
name|String
name|columnClassName
parameter_list|,
name|Class
name|internalClass
parameter_list|)
block|{
name|this
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
name|this
operator|.
name|autoIncrement
operator|=
name|autoIncrement
expr_stmt|;
name|this
operator|.
name|caseSensitive
operator|=
name|caseSensitive
expr_stmt|;
name|this
operator|.
name|searchable
operator|=
name|searchable
expr_stmt|;
name|this
operator|.
name|currency
operator|=
name|currency
expr_stmt|;
name|this
operator|.
name|nullable
operator|=
name|nullable
expr_stmt|;
name|this
operator|.
name|signed
operator|=
name|signed
expr_stmt|;
name|this
operator|.
name|displaySize
operator|=
name|displaySize
expr_stmt|;
name|this
operator|.
name|label
operator|=
name|label
expr_stmt|;
comment|// Per the JDBC spec this should be just columnName.
comment|// For example, the query
comment|//     select 1 as x, c as y from t
comment|// should give columns
comment|//     (label=x, column=null, table=null)
comment|//     (label=y, column=c table=t)
comment|// But DbUnit requires every column to have a name. Duh.
name|this
operator|.
name|columnName
operator|=
name|first
argument_list|(
name|columnName
argument_list|,
name|label
argument_list|)
expr_stmt|;
name|this
operator|.
name|schemaName
operator|=
name|schemaName
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
name|tableName
operator|=
name|tableName
expr_stmt|;
name|this
operator|.
name|catalogName
operator|=
name|catalogName
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|typeName
operator|=
name|typeName
expr_stmt|;
name|this
operator|.
name|readOnly
operator|=
name|readOnly
expr_stmt|;
name|this
operator|.
name|writable
operator|=
name|writable
expr_stmt|;
name|this
operator|.
name|definitelyWritable
operator|=
name|definitelyWritable
expr_stmt|;
name|this
operator|.
name|columnClassName
operator|=
name|columnClassName
expr_stmt|;
name|this
operator|.
name|internalClass
operator|=
name|internalClass
expr_stmt|;
block|}
specifier|private
specifier|static
parameter_list|<
name|T
parameter_list|>
name|T
name|first
parameter_list|(
name|T
name|t0
parameter_list|,
name|T
name|t1
parameter_list|)
block|{
return|return
name|t0
operator|!=
literal|null
condition|?
name|t0
else|:
name|t1
return|;
block|}
block|}
end_class

begin_comment
comment|// End ColumnMetaData.java
end_comment

end_unit

