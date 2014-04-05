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
name|lang
operator|.
name|reflect
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

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * ReflectiveSqlOperatorTable implements the {@link SqlOperatorTable } interface  * by reflecting the public fields of a subclass.  */
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
name|String
argument_list|,
name|SqlOperator
argument_list|>
name|operators
init|=
name|HashMultimap
operator|.
name|create
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlSyntax
argument_list|>
argument_list|,
name|SqlOperator
argument_list|>
name|mapNameToOp
init|=
operator|new
name|HashMap
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlSyntax
argument_list|>
argument_list|,
name|SqlOperator
argument_list|>
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
name|register
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"Error while initializing operator table"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
name|e
argument_list|,
literal|"Error while initializing operator table"
argument_list|)
throw|;
block|}
block|}
block|}
comment|// implement SqlOperatorTable
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
comment|// Always look up built-in operators case-insensitively. Even in sessions
comment|// with unquotedCasing=UNCHANGED and caseSensitive=true.
specifier|final
name|Collection
argument_list|<
name|SqlOperator
argument_list|>
name|list
init|=
name|operators
operator|.
name|get
argument_list|(
name|simpleName
operator|.
name|toUpperCase
argument_list|()
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
name|SqlOperator
name|extra
init|=
name|mapNameToOp
operator|.
name|get
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|simpleName
argument_list|,
name|syntax
argument_list|)
argument_list|)
decl_stmt|;
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
break|break;
block|}
block|}
specifier|public
name|void
name|register
parameter_list|(
name|SqlOperator
name|op
parameter_list|)
block|{
name|operators
operator|.
name|put
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|,
name|op
argument_list|)
expr_stmt|;
if|if
condition|(
name|op
operator|instanceof
name|SqlBinaryOperator
condition|)
block|{
name|mapNameToOp
operator|.
name|put
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|,
name|SqlSyntax
operator|.
name|BINARY
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|op
operator|instanceof
name|SqlPrefixOperator
condition|)
block|{
name|mapNameToOp
operator|.
name|put
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|,
name|SqlSyntax
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|op
operator|instanceof
name|SqlPostfixOperator
condition|)
block|{
name|mapNameToOp
operator|.
name|put
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|op
operator|.
name|getName
argument_list|()
argument_list|,
name|SqlSyntax
operator|.
name|POSTFIX
argument_list|)
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Registers a function in the table.    *    * @param function Function to register    */
specifier|public
name|void
name|register
parameter_list|(
name|SqlFunction
name|function
parameter_list|)
block|{
name|operators
operator|.
name|put
argument_list|(
name|function
operator|.
name|getName
argument_list|()
argument_list|,
name|function
argument_list|)
expr_stmt|;
name|SqlFunctionCategory
name|funcType
init|=
name|function
operator|.
name|getFunctionType
argument_list|()
decl_stmt|;
assert|assert
name|funcType
operator|!=
literal|null
operator|:
literal|"Function type for "
operator|+
name|function
operator|.
name|getName
argument_list|()
operator|+
literal|" not set"
assert|;
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
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|operators
operator|.
name|values
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ReflectiveSqlOperatorTable.java
end_comment

end_unit

