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
name|test
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
name|piglet
operator|.
name|Handler
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
name|rel
operator|.
name|RelNode
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
name|tools
operator|.
name|PigRelBuilder
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
name|tools
operator|.
name|RelRunners
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Array
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|PreparedStatement
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
name|sql
operator|.
name|Types
import|;
end_import

begin_comment
comment|/**  * Extension to {@link org.apache.calcite.piglet.Handler} that can execute  * commands using Calcite.  */
end_comment

begin_class
class|class
name|CalciteHandler
extends|extends
name|Handler
block|{
specifier|private
specifier|final
name|PrintWriter
name|writer
decl_stmt|;
name|CalciteHandler
parameter_list|(
name|PigRelBuilder
name|builder
parameter_list|,
name|Writer
name|writer
parameter_list|)
block|{
name|super
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
operator|new
name|PrintWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|dump
parameter_list|(
name|RelNode
name|rel
parameter_list|)
block|{
try|try
init|(
name|PreparedStatement
name|preparedStatement
init|=
name|RelRunners
operator|.
name|run
argument_list|(
name|rel
argument_list|)
init|)
block|{
specifier|final
name|ResultSet
name|resultSet
init|=
name|preparedStatement
operator|.
name|executeQuery
argument_list|()
decl_stmt|;
name|dump
argument_list|(
name|resultSet
argument_list|,
literal|true
argument_list|)
expr_stmt|;
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
specifier|private
name|void
name|dump
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|boolean
name|newline
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|int
name|columnCount
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnCount
argument_list|()
decl_stmt|;
name|int
name|r
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|newline
operator|&&
name|r
operator|++
operator|>
literal|0
condition|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|columnCount
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|newline
condition|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|"()"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|print
argument_list|(
literal|"()"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|writer
operator|.
name|print
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|dumpColumn
argument_list|(
name|resultSet
argument_list|,
literal|1
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|2
init|;
name|i
operator|<=
name|columnCount
condition|;
name|i
operator|++
control|)
block|{
name|writer
operator|.
name|print
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|dumpColumn
argument_list|(
name|resultSet
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|newline
condition|)
block|{
name|writer
operator|.
name|println
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|print
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/** Dumps a column value.    *    * @param i Column ordinal, 1-based    */
specifier|private
name|void
name|dumpColumn
parameter_list|(
name|ResultSet
name|resultSet
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|SQLException
block|{
specifier|final
name|int
name|t
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
operator|.
name|getColumnType
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|t
condition|)
block|{
case|case
name|Types
operator|.
name|ARRAY
case|:
specifier|final
name|Array
name|array
init|=
name|resultSet
operator|.
name|getArray
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"{"
argument_list|)
expr_stmt|;
name|dump
argument_list|(
name|array
operator|.
name|getResultSet
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
return|return;
case|case
name|Types
operator|.
name|REAL
case|:
name|writer
operator|.
name|print
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|"F"
argument_list|)
expr_stmt|;
return|return;
default|default:
name|writer
operator|.
name|print
argument_list|(
name|resultSet
operator|.
name|getString
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End CalciteHandler.java
end_comment

end_unit

