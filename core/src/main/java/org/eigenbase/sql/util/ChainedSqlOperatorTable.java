begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
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
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * ChainedSqlOperatorTable implements the {@link SqlOperatorTable} interface by  * chaining together any number of underlying operator table instances.  */
end_comment

begin_class
specifier|public
class|class
name|ChainedSqlOperatorTable
implements|implements
name|SqlOperatorTable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|List
argument_list|<
name|SqlOperatorTable
argument_list|>
name|tableList
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a table based on a given list.    */
specifier|public
name|ChainedSqlOperatorTable
parameter_list|(
name|List
argument_list|<
name|SqlOperatorTable
argument_list|>
name|tableList
parameter_list|)
block|{
name|this
operator|.
name|tableList
operator|=
name|tableList
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Adds an underlying table. The order in which tables are added is    * significant; tables added earlier have higher lookup precedence. A table    * is not added if it is already on the list.    *    * @param table table to add    */
specifier|public
name|void
name|add
parameter_list|(
name|SqlOperatorTable
name|table
parameter_list|)
block|{
if|if
condition|(
operator|!
name|tableList
operator|.
name|contains
argument_list|(
name|table
argument_list|)
condition|)
block|{
name|tableList
operator|.
name|add
argument_list|(
name|table
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|lookupOperatorOverloads
parameter_list|(
name|SqlIdentifier
name|opName
parameter_list|,
name|SqlFunctionCategory
name|category
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|,
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
parameter_list|)
block|{
for|for
control|(
name|SqlOperatorTable
name|table
range|:
name|tableList
control|)
block|{
name|table
operator|.
name|lookupOperatorOverloads
argument_list|(
name|opName
argument_list|,
name|category
argument_list|,
name|syntax
argument_list|,
name|operatorList
argument_list|)
expr_stmt|;
block|}
block|}
comment|// implement SqlOperatorTable
specifier|public
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|getOperatorList
parameter_list|()
block|{
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|SqlOperator
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlOperatorTable
name|table
range|:
name|tableList
control|)
block|{
name|list
operator|.
name|addAll
argument_list|(
name|table
operator|.
name|getOperatorList
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
block|}
end_class

begin_comment
comment|// End ChainedSqlOperatorTable.java
end_comment

end_unit

