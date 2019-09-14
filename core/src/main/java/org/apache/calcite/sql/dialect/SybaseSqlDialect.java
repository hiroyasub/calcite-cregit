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
name|sql
operator|.
name|dialect
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
name|sql
operator|.
name|SqlDialect
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
name|SqlNode
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
name|SqlWriter
import|;
end_import

begin_comment
comment|/**  * A<code>SqlDialect</code> implementation for the Sybase database.  */
end_comment

begin_class
specifier|public
class|class
name|SybaseSqlDialect
extends|extends
name|SqlDialect
block|{
specifier|public
specifier|static
specifier|final
name|SqlDialect
name|DEFAULT
init|=
operator|new
name|SybaseSqlDialect
argument_list|(
name|EMPTY_CONTEXT
operator|.
name|withDatabaseProduct
argument_list|(
name|DatabaseProduct
operator|.
name|SYBASE
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Creates a SybaseSqlDialect. */
specifier|public
name|SybaseSqlDialect
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseOffsetFetch
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
name|offset
parameter_list|,
name|SqlNode
name|fetch
parameter_list|)
block|{
comment|// No-op; see unparseTopN.
comment|// Sybase uses "SELECT TOP (n)" rather than "FETCH NEXT n ROWS".
block|}
annotation|@
name|Override
specifier|public
name|void
name|unparseTopN
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
name|offset
parameter_list|,
name|SqlNode
name|fetch
parameter_list|)
block|{
comment|// Parentheses are not required, but we use them to be consistent with
comment|// Microsoft SQL Server, which recommends them but does not require them.
comment|//
comment|// Note that "fetch" is ignored.
name|writer
operator|.
name|keyword
argument_list|(
literal|"TOP"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
name|fetch
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
name|writer
operator|.
name|keyword
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SybaseSqlDialect.java
end_comment

end_unit

