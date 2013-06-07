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
name|runtime
operator|.
name|ColumnMetaData
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
name|Cursor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
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

begin_comment
comment|/**  * Utility methods, mainly concerning error-handling.  */
end_comment

begin_class
specifier|public
class|class
name|Helper
block|{
specifier|public
specifier|static
specifier|final
name|Helper
name|INSTANCE
init|=
operator|new
name|Helper
argument_list|()
decl_stmt|;
specifier|private
name|Helper
parameter_list|()
block|{
block|}
specifier|public
name|RuntimeException
name|todo
parameter_list|()
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
literal|"todo: implement this method"
argument_list|)
return|;
block|}
specifier|public
name|RuntimeException
name|wrap
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|new
name|RuntimeException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
return|;
block|}
specifier|public
name|SQLException
name|createException
parameter_list|(
name|String
name|message
parameter_list|,
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|new
name|SQLException
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
return|;
block|}
specifier|public
name|SQLException
name|createException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
return|return
operator|new
name|SQLException
argument_list|(
name|message
argument_list|)
return|;
block|}
specifier|public
name|SQLException
name|toSQLException
parameter_list|(
name|SQLException
name|exception
parameter_list|)
block|{
return|return
name|exception
return|;
block|}
comment|/** Creates an empty result set. Useful for JDBC metadata methods that are    * not implemented or which query entities that are not supported (e.g.    * triggers in Lingual). */
specifier|public
name|ResultSet
name|createEmptyResultSet
parameter_list|(
name|OptiqConnection
name|connection
parameter_list|)
block|{
try|try
block|{
specifier|final
name|OptiqConnectionImpl
name|connection1
init|=
operator|(
name|OptiqConnectionImpl
operator|)
name|connection
decl_stmt|;
return|return
name|connection1
operator|.
name|driver
operator|.
name|factory
operator|.
name|newResultSet
argument_list|(
name|connection1
operator|.
name|createStatement
argument_list|()
argument_list|,
name|Collections
operator|.
expr|<
name|ColumnMetaData
operator|>
name|emptyList
argument_list|()
argument_list|,
operator|new
name|Function0
argument_list|<
name|Cursor
argument_list|>
argument_list|()
block|{
specifier|public
name|Cursor
name|apply
parameter_list|()
block|{
return|return
operator|new
name|Cursor
argument_list|()
block|{
specifier|public
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
parameter_list|)
block|{
assert|assert
name|types
operator|.
name|isEmpty
argument_list|()
assert|;
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|next
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
argument_list|)
operator|.
name|execute
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|SQLException
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
end_class

begin_comment
comment|// End Helper.java
end_comment

end_unit

