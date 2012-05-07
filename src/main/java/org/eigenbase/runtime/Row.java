begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|runtime
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * When a relational expression obeys the {@link  * org.eigenbase.relopt.CallingConvention#RESULT_SET result set calling  * convention}, and does not explicitly specify a row type, the results are  * object of type<code>Row</code>.  */
end_comment

begin_class
specifier|public
class|class
name|Row
block|{
comment|//~ Instance fields --------------------------------------------------------
name|ResultSet
name|resultSet
decl_stmt|;
name|Object
index|[]
name|values
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|Row
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|)
throws|throws
name|SQLException
block|{
name|this
operator|.
name|resultSet
operator|=
name|resultSet
expr_stmt|;
name|ResultSetMetaData
name|resultSetMetaData
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
specifier|final
name|int
name|count
init|=
name|resultSetMetaData
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
name|this
operator|.
name|values
operator|=
operator|new
name|Object
index|[
name|count
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|values
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
name|resultSet
operator|.
name|getObject
argument_list|(
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * Returns the value of a given column, similar to {@link      * ResultSet#getObject(int)}.      *      * @param columnIndex the first column is 1, the second is 2, ...      *      * @return a<code>java.lang.Object</code> holding the column value      */
specifier|public
name|Object
name|getObject
parameter_list|(
name|int
name|columnIndex
parameter_list|)
block|{
return|return
name|values
index|[
name|columnIndex
operator|-
literal|1
index|]
return|;
block|}
comment|/**      * Returns the result set that this row belongs to.      */
specifier|public
name|ResultSet
name|getResultSet
parameter_list|()
block|{
return|return
name|resultSet
return|;
block|}
block|}
end_class

begin_comment
comment|// End Row.java
end_comment

end_unit

