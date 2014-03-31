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
comment|/**  * Implementation of the {@link SqlOperatorTable} interface by using a list of  * {@link SqlOperator operators}.  */
end_comment

begin_class
specifier|public
class|class
name|ListSqlOperatorTable
implements|implements
name|SqlOperatorTable
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|ListSqlOperatorTable
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ArrayList
argument_list|<
name|SqlOperator
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ListSqlOperatorTable
parameter_list|(
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
parameter_list|)
block|{
name|this
operator|.
name|operatorList
operator|=
name|operatorList
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|add
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
name|operatorList
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|SqlOperator
argument_list|>
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
parameter_list|)
block|{
specifier|final
name|ArrayList
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
name|SqlOperator
name|operator
range|:
name|operatorList
control|)
block|{
if|if
condition|(
name|operator
operator|.
name|getSyntax
argument_list|()
operator|!=
name|syntax
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|opName
operator|.
name|isSimple
argument_list|()
operator|||
operator|!
name|operator
operator|.
name|isName
argument_list|(
name|opName
operator|.
name|getSimple
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|SqlFunctionCategory
name|functionCategory
decl_stmt|;
if|if
condition|(
name|operator
operator|instanceof
name|SqlFunction
condition|)
block|{
name|functionCategory
operator|=
operator|(
operator|(
name|SqlFunction
operator|)
name|operator
operator|)
operator|.
name|getFunctionType
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|functionCategory
operator|=
name|SqlFunctionCategory
operator|.
name|SYSTEM
expr_stmt|;
block|}
if|if
condition|(
name|category
operator|!=
name|functionCategory
operator|&&
name|category
operator|!=
name|SqlFunctionCategory
operator|.
name|USER_DEFINED_FUNCTION
condition|)
block|{
continue|continue;
block|}
name|list
operator|.
name|add
argument_list|(
name|operator
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|public
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|getOperatorList
parameter_list|()
block|{
return|return
name|operatorList
return|;
block|}
block|}
end_class

begin_comment
comment|// End ListSqlOperatorTable.java
end_comment

end_unit

