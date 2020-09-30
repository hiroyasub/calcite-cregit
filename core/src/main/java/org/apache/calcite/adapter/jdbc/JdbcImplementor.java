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
name|adapter
operator|.
name|jdbc
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
name|adapter
operator|.
name|java
operator|.
name|JavaTypeFactory
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
name|rel
operator|.
name|rel2sql
operator|.
name|RelToSqlConverter
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
name|util
operator|.
name|Util
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_comment
comment|/**  * State for generating a SQL statement.  */
end_comment

begin_class
specifier|public
class|class
name|JdbcImplementor
extends|extends
name|RelToSqlConverter
block|{
specifier|public
name|JdbcImplementor
parameter_list|(
name|SqlDialect
name|dialect
parameter_list|,
name|JavaTypeFactory
name|typeFactory
parameter_list|)
block|{
name|super
argument_list|(
name|dialect
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|typeFactory
argument_list|)
expr_stmt|;
block|}
comment|// CHECKSTYLE: IGNORE 1
comment|/** @see #dispatch */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"MissingSummary"
argument_list|)
specifier|public
name|Result
name|visit
parameter_list|(
name|JdbcTableScan
name|scan
parameter_list|)
block|{
return|return
name|result
argument_list|(
name|scan
operator|.
name|jdbcTable
operator|.
name|tableName
argument_list|()
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|Clause
operator|.
name|FROM
argument_list|)
argument_list|,
name|scan
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Result
name|implement
parameter_list|(
name|RelNode
name|node
parameter_list|)
block|{
return|return
name|dispatch
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
end_class

end_unit

