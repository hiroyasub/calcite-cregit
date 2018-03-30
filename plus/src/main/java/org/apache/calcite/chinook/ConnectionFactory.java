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
name|chinook
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|chinook
operator|.
name|data
operator|.
name|hsqldb
operator|.
name|ChinookHsqldb
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|quidem
operator|.
name|Quidem
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_comment
comment|/**  * Wrapping connection factory for quidem  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionFactory
implements|implements
name|Quidem
operator|.
name|ConnectionFactory
block|{
specifier|private
specifier|static
specifier|final
name|CalciteConnectionProvider
name|CALCITE
init|=
operator|new
name|CalciteConnectionProvider
argument_list|()
decl_stmt|;
specifier|public
name|Connection
name|connect
parameter_list|(
name|String
name|db
parameter_list|,
name|boolean
name|bln
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|DBWrapper
operator|.
name|valueOf
argument_list|(
name|db
argument_list|)
operator|.
name|connection
argument_list|()
return|;
block|}
comment|/**    * Wrapping with Fairy environmental decoration    */
specifier|private
enum|enum
name|DBWrapper
block|{
name|CALCITE_AS_ADMIN
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|connection
parameter_list|()
throws|throws
name|Exception
block|{
name|EnvironmentFairy
operator|.
name|login
argument_list|(
name|EnvironmentFairy
operator|.
name|User
operator|.
name|ADMIN
argument_list|)
expr_stmt|;
return|return
name|CALCITE
operator|.
name|connection
argument_list|()
return|;
block|}
block|}
block|,
name|CALCITE_AS_SPECIFIC_USER
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|connection
parameter_list|()
throws|throws
name|Exception
block|{
name|EnvironmentFairy
operator|.
name|login
argument_list|(
name|EnvironmentFairy
operator|.
name|User
operator|.
name|SPECIFIC_USER
argument_list|)
expr_stmt|;
return|return
name|CALCITE
operator|.
name|connection
argument_list|()
return|;
block|}
block|}
block|,
name|RAW
block|{
annotation|@
name|Override
specifier|public
name|Connection
name|connection
parameter_list|()
throws|throws
name|Exception
block|{
return|return
name|DriverManager
operator|.
name|getConnection
argument_list|(
name|ChinookHsqldb
operator|.
name|URI
argument_list|,
name|ChinookHsqldb
operator|.
name|USER
argument_list|,
name|ChinookHsqldb
operator|.
name|PASSWORD
argument_list|)
return|;
block|}
block|}
block|;
specifier|public
specifier|abstract
name|Connection
name|connection
argument_list|()
throws|throws
name|Exception
block|;   }
block|}
end_class

begin_comment
comment|// End ConnectionFactory.java
end_comment

end_unit
