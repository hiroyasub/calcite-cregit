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
name|fun
operator|.
name|SqlStdOperatorTable
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
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Pair
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
name|HashMultimap
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
name|Multimap
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
name|lang
operator|.
name|reflect
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * ReflectiveSqlOperatorTable implements the {@link SqlOperatorTable} interface  * by reflecting the public fields of a subclass.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ReflectiveSqlOperatorTable
implements|implements
name|SqlOperatorTable
block|{
specifier|public
specifier|static
specifier|final
name|String
name|IS_NAME
init|=
literal|"INFORMATION_SCHEMA"
decl_stmt|;
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Multimap
argument_list|<
name|CaseSensitiveKey
argument_list|,
name|SqlOperator
argument_list|>
name|caseSensitiveOperators
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Multimap
argument_list|<
name|CaseInsensitiveKey
argument_list|,
name|SqlOperator
argument_list|>
name|caseInsensitiveOperators
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|ReflectiveSqlOperatorTable
parameter_list|()
block|{
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Performs post-constructor initialization of an operator table. It can't    * be part of the constructor, because the subclass constructor needs to    * complete first.    */
specifier|public
specifier|final
name|void
name|init
parameter_list|()
block|{
comment|// Use reflection to register the expressions stored in public fields.
for|for
control|(
name|Field
name|field
range|:
name|getClass
argument_list|()
operator|.
name|getFields
argument_list|()
control|)
block|{
try|try
block|{
if|if
condition|(
name|SqlFunction
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|SqlFunction
name|op
init|=
operator|(
name|SqlFunction
operator|)
name|field
operator|.
name|get
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|!=
literal|null
condition|)
block|{
name|register
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|SqlOperator
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|SqlOperator
name|op
init|=
operator|(
name|SqlOperator
operator|)
name|field
operator|.
name|get
argument_list|(
name|this
argument_list|)
decl_stmt|;
if|if
condition|(
name|op
operator|!=
literal|null
condition|)
block|{
name|register
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
decl||
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|throwAsRuntime
argument_list|(
name|Util
operator|.
name|causeOrSelf
argument_list|(
name|e
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
comment|// implement SqlOperatorTable
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
comment|// NOTE jvs 3-Mar-2005:  ignore category until someone cares
name|String
name|simpleName
decl_stmt|;
if|if
condition|(
name|opName
operator|.
name|names
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
if|if
condition|(
name|opName
operator|.
name|names
operator|.
name|get
argument_list|(
name|opName
operator|.
name|names
operator|.
name|size
argument_list|()
operator|-
literal|2
argument_list|)
operator|.
name|equals
argument_list|(
name|IS_NAME
argument_list|)
condition|)
block|{
comment|// per SQL99 Part 2 Section 10.4 Syntax Rule 7.b.ii.1
name|simpleName
operator|=
name|Util
operator|.
name|last
argument_list|(
name|opName
operator|.
name|names
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return;
block|}
block|}
else|else
block|{
name|simpleName
operator|=
name|opName
operator|.
name|getSimple
argument_list|()
expr_stmt|;
block|}
specifier|final
name|Collection
argument_list|<
name|SqlOperator
argument_list|>
name|list
init|=
name|lookUpOperators
argument_list|(
name|simpleName
argument_list|,
name|syntax
argument_list|,
name|nameMatcher
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
for|for
control|(
name|SqlOperator
name|op
range|:
name|list
control|)
block|{
if|if
condition|(
name|op
operator|.
name|getSyntax
argument_list|()
operator|==
name|syntax
condition|)
block|{
name|operatorList
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|syntax
operator|==
name|SqlSyntax
operator|.
name|FUNCTION
operator|&&
name|op
operator|instanceof
name|SqlFunction
condition|)
block|{
comment|// this special case is needed for operators like CAST,
comment|// which are treated as functions but have special syntax
name|operatorList
operator|.
name|add
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
comment|// REVIEW jvs 1-Jan-2005:  why is this extra lookup required?
comment|// Shouldn't it be covered by search above?
switch|switch
condition|(
name|syntax
condition|)
block|{
case|case
name|BINARY
case|:
case|case
name|PREFIX
case|:
case|case
name|POSTFIX
case|:
for|for
control|(
name|SqlOperator
name|extra
range|:
name|lookUpOperators
argument_list|(
name|simpleName
argument_list|,
name|syntax
argument_list|,
name|nameMatcher
argument_list|)
control|)
block|{
comment|// REVIEW: should only search operators added during this method?
if|if
condition|(
name|extra
operator|!=
literal|null
operator|&&
operator|!
name|operatorList
operator|.
name|contains
argument_list|(
name|extra
argument_list|)
condition|)
block|{
name|operatorList
operator|.
name|add
argument_list|(
name|extra
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
break|break;
block|}
block|}
comment|/**    * Look up operators based on case-sensitiveness.    */
specifier|private
name|Collection
argument_list|<
name|SqlOperator
argument_list|>
name|lookUpOperators
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|)
block|{
comment|// Case sensitive only works for UDFs.
comment|// Always look up built-in operators case-insensitively. Even in sessions
comment|// with unquotedCasing=UNCHANGED and caseSensitive=true.
if|if
condition|(
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
operator|&&
operator|!
operator|(
name|this
operator|instanceof
name|SqlStdOperatorTable
operator|)
condition|)
block|{
return|return
name|caseSensitiveOperators
operator|.
name|get
argument_list|(
operator|new
name|CaseSensitiveKey
argument_list|(
name|name
argument_list|,
name|syntax
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|caseInsensitiveOperators
operator|.
name|get
argument_list|(
operator|new
name|CaseInsensitiveKey
argument_list|(
name|name
argument_list|,
name|syntax
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/**    * Registers a function or operator in the table.    */
specifier|public
name|void
name|register
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
comment|// Register both for case-sensitive and case-insensitive look up.
name|caseSensitiveOperators
operator|.
name|put
argument_list|(
operator|new
name|CaseSensitiveKey
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|,
name|op
operator|.
name|getSyntax
argument_list|()
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|caseInsensitiveOperators
operator|.
name|put
argument_list|(
operator|new
name|CaseInsensitiveKey
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|,
name|op
operator|.
name|getSyntax
argument_list|()
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
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
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|caseSensitiveOperators
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
comment|/** Key for looking up operators. The name is stored in upper-case because we    * store case-insensitively, even in a case-sensitive session. */
specifier|private
specifier|static
class|class
name|CaseInsensitiveKey
extends|extends
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlSyntax
argument_list|>
block|{
name|CaseInsensitiveKey
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|)
block|{
name|super
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|,
name|normalize
argument_list|(
name|syntax
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Key for looking up operators. The name kept as what it is to look up case-sensitively. */
specifier|private
specifier|static
class|class
name|CaseSensitiveKey
extends|extends
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlSyntax
argument_list|>
block|{
name|CaseSensitiveKey
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlSyntax
name|syntax
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|normalize
argument_list|(
name|syntax
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|SqlSyntax
name|normalize
parameter_list|(
name|SqlSyntax
name|syntax
parameter_list|)
block|{
switch|switch
condition|(
name|syntax
condition|)
block|{
case|case
name|BINARY
case|:
case|case
name|PREFIX
case|:
case|case
name|POSTFIX
case|:
return|return
name|syntax
return|;
default|default:
return|return
name|SqlSyntax
operator|.
name|FUNCTION
return|;
block|}
block|}
block|}
end_class

end_unit

