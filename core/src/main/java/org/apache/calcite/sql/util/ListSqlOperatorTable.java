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
name|util
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
name|SqlFunction
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
name|SqlFunctionCategory
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
name|SqlIdentifier
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
name|SqlOperator
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
name|SqlOperatorTable
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
name|SqlSyntax
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
name|validate
operator|.
name|SqlNameMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/** Creates an empty, mutable ListSqlOperatorTable.    *    * @deprecated Use {@link SqlOperatorTables#of}, which creates an immutable    * table. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
specifier|public
name|ListSqlOperatorTable
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/** Creates a mutable ListSqlOperatorTable backed by a given list.    *    * @deprecated Use {@link SqlOperatorTables#of}, which creates an immutable    * table. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
argument_list|(
name|operatorList
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|// internal constructor
name|ListSqlOperatorTable
parameter_list|(
name|List
argument_list|<
name|SqlOperator
argument_list|>
name|operatorList
parameter_list|,
name|boolean
name|ignored
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
comment|/** Adds an operator to this table.    *    * @deprecated Use {@link SqlOperatorTables#of}, which creates an immutable    * table. */
annotation|@
name|Deprecated
comment|// to be removed before 2.0
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
annotation|@
name|Override
specifier|public
name|void
name|lookupOperatorOverloads
parameter_list|(
name|SqlIdentifier
name|opName
parameter_list|,
annotation|@
name|Nullable
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
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|)
block|{
for|for
control|(
name|SqlOperator
name|operator
range|:
name|this
operator|.
name|operatorList
control|)
block|{
if|if
condition|(
name|operator
operator|.
name|getSyntax
argument_list|()
operator|.
name|family
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
name|nameMatcher
operator|.
name|matches
argument_list|(
name|operator
operator|.
name|getName
argument_list|()
argument_list|,
name|opName
operator|.
name|getSimple
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
name|category
operator|!=
literal|null
operator|&&
name|category
operator|!=
name|category
argument_list|(
name|operator
argument_list|)
operator|&&
operator|!
name|category
operator|.
name|isUserDefinedNotSpecificFunction
argument_list|()
condition|)
block|{
continue|continue;
block|}
name|operatorList
operator|.
name|add
argument_list|(
name|operator
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|static
name|SqlFunctionCategory
name|category
parameter_list|(
name|SqlOperator
name|operator
parameter_list|)
block|{
if|if
condition|(
name|operator
operator|instanceof
name|SqlFunction
condition|)
block|{
return|return
operator|(
operator|(
name|SqlFunction
operator|)
name|operator
operator|)
operator|.
name|getFunctionType
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|SqlFunctionCategory
operator|.
name|SYSTEM
return|;
block|}
block|}
annotation|@
name|Override
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

end_unit

