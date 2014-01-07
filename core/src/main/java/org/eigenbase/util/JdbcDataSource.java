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
name|util
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
name|logging
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util14
operator|.
name|Unwrappable
import|;
end_import

begin_comment
comment|/**  * Adapter to make a JDBC connection into a {@link javax.sql.DataSource}.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcDataSource
extends|extends
name|Unwrappable
implements|implements
name|DataSource
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|String
name|url
decl_stmt|;
specifier|private
name|PrintWriter
name|logWriter
decl_stmt|;
specifier|private
name|int
name|loginTimeout
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a JDBC data source.      *      * @param url URL of JDBC connection (must not be null)      *      * @pre url != null      */
specifier|public
name|JdbcDataSource
parameter_list|(
name|String
name|url
parameter_list|)
block|{
assert|assert
operator|(
name|url
operator|!=
literal|null
operator|)
assert|;
name|this
operator|.
name|url
operator|=
name|url
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Connection
name|getConnection
parameter_list|()
throws|throws
name|SQLException
block|{
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"jdbc:hsqldb:"
argument_list|)
condition|)
block|{
comment|// Hsqldb requires a username, but doesn't support username as part
comment|// of the URL, durn it. Assume that the username is "sa".
return|return
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|,
literal|"sa"
argument_list|,
literal|""
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|)
return|;
block|}
block|}
specifier|public
name|String
name|getUrl
parameter_list|()
block|{
return|return
name|url
return|;
block|}
specifier|public
name|Connection
name|getConnection
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|SQLException
block|{
return|return
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|url
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
return|;
block|}
specifier|public
name|void
name|setLogWriter
parameter_list|(
name|PrintWriter
name|out
parameter_list|)
throws|throws
name|SQLException
block|{
name|logWriter
operator|=
name|out
expr_stmt|;
block|}
specifier|public
name|PrintWriter
name|getLogWriter
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|logWriter
return|;
block|}
specifier|public
name|void
name|setLoginTimeout
parameter_list|(
name|int
name|seconds
parameter_list|)
throws|throws
name|SQLException
block|{
name|loginTimeout
operator|=
name|seconds
expr_stmt|;
block|}
specifier|public
name|int
name|getLoginTimeout
parameter_list|()
throws|throws
name|SQLException
block|{
return|return
name|loginTimeout
return|;
block|}
comment|// for JDK 1.7
specifier|public
name|Logger
name|getParentLogger
parameter_list|()
throws|throws
name|SQLFeatureNotSupportedException
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcDataSource.java
end_comment

end_unit

