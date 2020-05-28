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
name|validate
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
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
name|type
operator|.
name|RelDataTypeField
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
name|type
operator|.
name|StructKind
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
name|ImmutableList
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
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Abstract base for a scope which is defined by a list of child namespaces and  * which inherits from a parent scope.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ListScope
extends|extends
name|DelegatingScope
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**    * List of child {@link SqlValidatorNamespace} objects and their names.    */
specifier|public
specifier|final
name|List
argument_list|<
name|ScopeChild
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|protected
name|ListScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|void
name|addChild
parameter_list|(
name|SqlValidatorNamespace
name|ns
parameter_list|,
name|String
name|alias
parameter_list|,
name|boolean
name|nullable
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|alias
argument_list|)
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
operator|new
name|ScopeChild
argument_list|(
name|children
operator|.
name|size
argument_list|()
argument_list|,
name|alias
argument_list|,
name|ns
argument_list|,
name|nullable
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns an immutable list of child namespaces.    *    * @return list of child namespaces    */
specifier|public
name|List
argument_list|<
name|SqlValidatorNamespace
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Util
operator|.
name|transform
argument_list|(
name|children
argument_list|,
name|scopeChild
lambda|->
name|scopeChild
operator|.
name|namespace
argument_list|)
return|;
block|}
comment|/**    * Returns an immutable list of child names.    *    * @return list of child namespaces    */
name|List
argument_list|<
annotation|@
name|Nullable
name|String
argument_list|>
name|getChildNames
parameter_list|()
block|{
return|return
name|Util
operator|.
name|transform
argument_list|(
name|children
argument_list|,
name|scopeChild
lambda|->
name|scopeChild
operator|.
name|name
argument_list|)
return|;
block|}
specifier|private
annotation|@
name|Nullable
name|ScopeChild
name|findChild
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|)
block|{
for|for
control|(
name|ScopeChild
name|child
range|:
name|children
control|)
block|{
name|String
name|lastName
init|=
name|Util
operator|.
name|last
argument_list|(
name|names
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|.
name|name
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|nameMatcher
operator|.
name|matches
argument_list|(
name|child
operator|.
name|name
argument_list|,
name|lastName
argument_list|)
condition|)
block|{
comment|// Alias does not match last segment. Don't consider the
comment|// fully-qualified name. E.g.
comment|//    SELECT sales.emp.name FROM sales.emp AS otherAlias
continue|continue;
block|}
if|if
condition|(
name|names
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|child
return|;
block|}
block|}
comment|// Look up the 2 tables independently, in case one is qualified with
comment|// catalog& schema and the other is not.
specifier|final
name|SqlValidatorTable
name|table
init|=
name|child
operator|.
name|namespace
operator|.
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|table
operator|!=
literal|null
condition|)
block|{
specifier|final
name|ResolvedImpl
name|resolved
init|=
operator|new
name|ResolvedImpl
argument_list|()
decl_stmt|;
name|resolveTable
argument_list|(
name|names
argument_list|,
name|nameMatcher
argument_list|,
name|Path
operator|.
name|EMPTY
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
if|if
condition|(
name|resolved
operator|.
name|count
argument_list|()
operator|==
literal|1
condition|)
block|{
name|Resolve
name|only
init|=
name|resolved
operator|.
name|only
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|qualifiedName
init|=
name|table
operator|.
name|getQualifiedName
argument_list|()
decl_stmt|;
if|if
condition|(
name|only
operator|.
name|remainingNames
operator|.
name|isEmpty
argument_list|()
operator|&&
name|only
operator|.
name|namespace
operator|instanceof
name|TableNamespace
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|qualifiedName
argument_list|,
name|getQualifiedName
argument_list|(
name|only
operator|.
name|namespace
operator|.
name|getTable
argument_list|()
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|child
return|;
block|}
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
annotation|@
name|Nullable
name|List
argument_list|<
name|String
argument_list|>
name|getQualifiedName
parameter_list|(
annotation|@
name|Nullable
name|SqlValidatorTable
name|table
parameter_list|)
block|{
return|return
name|table
operator|==
literal|null
condition|?
literal|null
else|:
name|table
operator|.
name|getQualifiedName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|findAllColumnNames
parameter_list|(
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|result
parameter_list|)
block|{
for|for
control|(
name|ScopeChild
name|child
range|:
name|children
control|)
block|{
name|addColumnNames
argument_list|(
name|child
operator|.
name|namespace
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|findAllColumnNames
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|findAliases
parameter_list|(
name|Collection
argument_list|<
name|SqlMoniker
argument_list|>
name|result
parameter_list|)
block|{
for|for
control|(
name|ScopeChild
name|child
range|:
name|children
control|)
block|{
name|result
operator|.
name|add
argument_list|(
operator|new
name|SqlMonikerImpl
argument_list|(
name|child
operator|.
name|name
argument_list|,
name|SqlMonikerType
operator|.
name|TABLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|parent
operator|.
name|findAliases
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
name|findQualifyingTableName
parameter_list|(
specifier|final
name|String
name|columnName
parameter_list|,
name|SqlNode
name|ctx
parameter_list|)
block|{
specifier|final
name|SqlNameMatcher
name|nameMatcher
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|nameMatcher
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ScopeChild
argument_list|>
name|map
init|=
name|findQualifyingTableNames
argument_list|(
name|columnName
argument_list|,
name|ctx
argument_list|,
name|nameMatcher
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|map
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|ctx
argument_list|,
name|RESOURCE
operator|.
name|columnNotFound
argument_list|(
name|columnName
argument_list|)
argument_list|)
throw|;
case|case
literal|1
case|:
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|ScopeChild
argument_list|>
name|entry
init|=
name|map
operator|.
name|entrySet
argument_list|()
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|namespace
argument_list|)
return|;
default|default:
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|ctx
argument_list|,
name|RESOURCE
operator|.
name|columnAmbiguous
argument_list|(
name|columnName
argument_list|)
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ScopeChild
argument_list|>
name|findQualifyingTableNames
parameter_list|(
name|String
name|columnName
parameter_list|,
name|SqlNode
name|ctx
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|ScopeChild
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|ScopeChild
name|child
range|:
name|children
control|)
block|{
specifier|final
name|ResolvedImpl
name|resolved
init|=
operator|new
name|ResolvedImpl
argument_list|()
decl_stmt|;
name|resolve
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
name|child
operator|.
name|name
argument_list|,
name|columnName
argument_list|)
argument_list|,
name|nameMatcher
argument_list|,
literal|true
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
if|if
condition|(
name|resolved
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|child
operator|.
name|name
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|map
operator|.
name|size
argument_list|()
condition|)
block|{
case|case
literal|0
case|:
return|return
name|parent
operator|.
name|findQualifyingTableNames
argument_list|(
name|columnName
argument_list|,
name|ctx
argument_list|,
name|nameMatcher
argument_list|)
return|;
default|default:
return|return
name|map
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|resolve
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|,
name|boolean
name|deep
parameter_list|,
name|Resolved
name|resolved
parameter_list|)
block|{
comment|// First resolve by looking through the child namespaces.
specifier|final
name|ScopeChild
name|child0
init|=
name|findChild
argument_list|(
name|names
argument_list|,
name|nameMatcher
argument_list|)
decl_stmt|;
if|if
condition|(
name|child0
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Step
name|path
init|=
name|Path
operator|.
name|EMPTY
operator|.
name|plus
argument_list|(
name|child0
operator|.
name|namespace
operator|.
name|getRowType
argument_list|()
argument_list|,
name|child0
operator|.
name|ordinal
argument_list|,
name|child0
operator|.
name|name
argument_list|,
name|StructKind
operator|.
name|FULLY_QUALIFIED
argument_list|)
decl_stmt|;
name|resolved
operator|.
name|found
argument_list|(
name|child0
operator|.
name|namespace
argument_list|,
name|child0
operator|.
name|nullable
argument_list|,
name|this
argument_list|,
name|path
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Recursively look deeper into the record-valued fields of the namespace,
comment|// if it allows skipping fields.
if|if
condition|(
name|deep
condition|)
block|{
for|for
control|(
name|ScopeChild
name|child
range|:
name|children
control|)
block|{
comment|// If identifier starts with table alias, remove the alias.
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names2
init|=
name|nameMatcher
operator|.
name|matches
argument_list|(
name|child
operator|.
name|name
argument_list|,
name|names
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
condition|?
name|names
operator|.
name|subList
argument_list|(
literal|1
argument_list|,
name|names
operator|.
name|size
argument_list|()
argument_list|)
else|:
name|names
decl_stmt|;
name|resolveInNamespace
argument_list|(
name|child
operator|.
name|namespace
argument_list|,
name|child
operator|.
name|nullable
argument_list|,
name|names2
argument_list|,
name|nameMatcher
argument_list|,
name|Path
operator|.
name|EMPTY
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|resolved
operator|.
name|count
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return;
block|}
block|}
comment|// Then call the base class method, which will delegate to the
comment|// parent scope.
name|super
operator|.
name|resolve
argument_list|(
name|names
argument_list|,
name|nameMatcher
argument_list|,
name|deep
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
annotation|@
name|Nullable
name|RelDataType
name|resolveColumn
parameter_list|(
name|String
name|columnName
parameter_list|,
name|SqlNode
name|ctx
parameter_list|)
block|{
specifier|final
name|SqlNameMatcher
name|nameMatcher
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|nameMatcher
argument_list|()
decl_stmt|;
name|int
name|found
init|=
literal|0
decl_stmt|;
name|RelDataType
name|type
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ScopeChild
name|child
range|:
name|children
control|)
block|{
name|SqlValidatorNamespace
name|childNs
init|=
name|child
operator|.
name|namespace
decl_stmt|;
specifier|final
name|RelDataType
name|childRowType
init|=
name|childNs
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|RelDataTypeField
name|field
init|=
name|nameMatcher
operator|.
name|field
argument_list|(
name|childRowType
argument_list|,
name|columnName
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
name|found
operator|++
expr_stmt|;
name|type
operator|=
name|field
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|found
condition|)
block|{
case|case
literal|0
case|:
return|return
literal|null
return|;
case|case
literal|1
case|:
return|return
name|type
return|;
default|default:
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|ctx
argument_list|,
name|RESOURCE
operator|.
name|columnAmbiguous
argument_list|(
name|columnName
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

