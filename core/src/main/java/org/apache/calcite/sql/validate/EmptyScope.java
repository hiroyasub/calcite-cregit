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
name|jdbc
operator|.
name|CalciteSchema
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
name|plan
operator|.
name|RelOptSchema
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
name|prepare
operator|.
name|Prepare
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
name|prepare
operator|.
name|RelOptTableImpl
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
name|schema
operator|.
name|Table
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
name|schema
operator|.
name|Wrapper
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
name|SqlCall
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
name|SqlDataTypeSpec
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
name|SqlDynamicParam
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
name|SqlLiteral
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
name|SqlNodeList
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
name|SqlWindow
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
comment|/**  * Deviant implementation of {@link SqlValidatorScope} for the top of the scope  * stack.  *  *<p>It is convenient, because we never need to check whether a scope's parent  * is null. (This scope knows not to ask about its parents, just like Adam.)  */
end_comment

begin_class
class|class
name|EmptyScope
implements|implements
name|SqlValidatorScope
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|SqlValidatorImpl
name|validator
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
name|EmptyScope
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|)
block|{
name|this
operator|.
name|validator
operator|=
name|validator
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlValidator
name|getValidator
parameter_list|()
block|{
return|return
name|validator
return|;
block|}
specifier|public
name|SqlQualified
name|fullyQualify
parameter_list|(
name|SqlIdentifier
name|identifier
parameter_list|)
block|{
return|return
name|SqlQualified
operator|.
name|create
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|,
name|identifier
argument_list|)
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
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
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|SqlValidatorNamespace
name|getTableNamespace
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|)
block|{
name|SqlValidatorTable
name|table
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|getTable
argument_list|(
name|names
argument_list|)
decl_stmt|;
return|return
name|table
operator|!=
literal|null
condition|?
operator|new
name|TableNamespace
argument_list|(
name|validator
argument_list|,
name|table
argument_list|)
else|:
literal|null
return|;
block|}
specifier|public
name|void
name|resolveTable
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
name|Path
name|path
parameter_list|,
name|Resolved
name|resolved
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Resolve
argument_list|>
name|imperfectResolves
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Resolve
argument_list|>
name|resolves
init|=
operator|(
operator|(
name|ResolvedImpl
operator|)
name|resolved
operator|)
operator|.
name|resolves
decl_stmt|;
comment|// Look in the default schema, then default catalog, then root schema.
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|schemaPath
range|:
name|validator
operator|.
name|catalogReader
operator|.
name|getSchemaPaths
argument_list|()
control|)
block|{
name|resolve_
argument_list|(
name|validator
operator|.
name|catalogReader
operator|.
name|getRootSchema
argument_list|()
argument_list|,
name|names
argument_list|,
name|schemaPath
argument_list|,
name|nameMatcher
argument_list|,
name|path
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
for|for
control|(
name|Resolve
name|resolve
range|:
name|resolves
control|)
block|{
if|if
condition|(
name|resolve
operator|.
name|remainingNames
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// There is a full match. Return it as the only match.
operator|(
operator|(
name|ResolvedImpl
operator|)
name|resolved
operator|)
operator|.
name|clear
argument_list|()
expr_stmt|;
name|resolves
operator|.
name|add
argument_list|(
name|resolve
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
name|imperfectResolves
operator|.
name|addAll
argument_list|(
name|resolves
argument_list|)
expr_stmt|;
block|}
comment|// If there were no matches in the last round, restore those found in
comment|// previous rounds
if|if
condition|(
name|resolves
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|resolves
operator|.
name|addAll
argument_list|(
name|imperfectResolves
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|resolve_
parameter_list|(
specifier|final
name|CalciteSchema
name|rootSchema
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|schemaNames
parameter_list|,
name|SqlNameMatcher
name|nameMatcher
parameter_list|,
name|Path
name|path
parameter_list|,
name|Resolved
name|resolved
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|concat
init|=
name|ImmutableList
operator|.
expr|<
name|String
operator|>
name|builder
argument_list|()
operator|.
name|addAll
argument_list|(
name|schemaNames
argument_list|)
operator|.
name|addAll
argument_list|(
name|names
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|CalciteSchema
name|schema
init|=
name|rootSchema
decl_stmt|;
name|SqlValidatorNamespace
name|namespace
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|remainingNames
init|=
name|concat
decl_stmt|;
for|for
control|(
name|String
name|schemaName
range|:
name|concat
control|)
block|{
if|if
condition|(
name|schema
operator|==
name|rootSchema
operator|&&
name|nameMatcher
operator|.
name|matches
argument_list|(
name|schemaName
argument_list|,
name|schema
operator|.
name|name
argument_list|)
condition|)
block|{
name|remainingNames
operator|=
name|Util
operator|.
name|skip
argument_list|(
name|remainingNames
argument_list|)
expr_stmt|;
continue|continue;
block|}
specifier|final
name|CalciteSchema
name|subSchema
init|=
name|schema
operator|.
name|getSubSchema
argument_list|(
name|schemaName
argument_list|,
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|subSchema
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|plus
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|subSchema
operator|.
name|name
argument_list|,
name|StructKind
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|remainingNames
operator|=
name|Util
operator|.
name|skip
argument_list|(
name|remainingNames
argument_list|)
expr_stmt|;
name|schema
operator|=
name|subSchema
expr_stmt|;
name|namespace
operator|=
operator|new
name|SchemaNamespace
argument_list|(
name|validator
argument_list|,
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|path
operator|.
name|stepNames
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|CalciteSchema
operator|.
name|TableEntry
name|entry
init|=
name|schema
operator|.
name|getTable
argument_list|(
name|schemaName
argument_list|,
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|entry
operator|=
name|schema
operator|.
name|getTableBasedOnNullaryFunction
argument_list|(
name|schemaName
argument_list|,
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|plus
argument_list|(
literal|null
argument_list|,
operator|-
literal|1
argument_list|,
name|entry
operator|.
name|name
argument_list|,
name|StructKind
operator|.
name|NONE
argument_list|)
expr_stmt|;
name|remainingNames
operator|=
name|Util
operator|.
name|skip
argument_list|(
name|remainingNames
argument_list|)
expr_stmt|;
specifier|final
name|Table
name|table
init|=
name|entry
operator|.
name|getTable
argument_list|()
decl_stmt|;
name|SqlValidatorTable
name|table2
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|table
operator|instanceof
name|Wrapper
condition|)
block|{
name|table2
operator|=
operator|(
operator|(
name|Wrapper
operator|)
name|table
operator|)
operator|.
name|unwrap
argument_list|(
name|Prepare
operator|.
name|PreparingTable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|table2
operator|==
literal|null
condition|)
block|{
specifier|final
name|RelOptSchema
name|relOptSchema
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|unwrap
argument_list|(
name|RelOptSchema
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|table
operator|.
name|getRowType
argument_list|(
name|validator
operator|.
name|typeFactory
argument_list|)
decl_stmt|;
name|table2
operator|=
name|RelOptTableImpl
operator|.
name|create
argument_list|(
name|relOptSchema
argument_list|,
name|rowType
argument_list|,
name|entry
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|namespace
operator|=
operator|new
name|TableNamespace
argument_list|(
name|validator
argument_list|,
name|table2
argument_list|)
expr_stmt|;
name|resolved
operator|.
name|found
argument_list|(
name|namespace
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|path
argument_list|,
name|remainingNames
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// neither sub-schema nor table
if|if
condition|(
name|namespace
operator|!=
literal|null
operator|&&
operator|!
name|remainingNames
operator|.
name|equals
argument_list|(
name|names
argument_list|)
condition|)
block|{
name|resolved
operator|.
name|found
argument_list|(
name|namespace
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
name|path
argument_list|,
name|remainingNames
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
specifier|public
name|RelDataType
name|nullifyType
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
return|return
name|type
return|;
block|}
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
block|}
specifier|public
name|void
name|findAllTableNames
parameter_list|(
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|result
parameter_list|)
block|{
block|}
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
block|}
specifier|public
name|RelDataType
name|resolveColumn
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlNode
name|ctx
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|SqlValidatorScope
name|getOperandScope
parameter_list|(
name|SqlCall
name|call
parameter_list|)
block|{
return|return
name|this
return|;
block|}
specifier|public
name|void
name|validateExpr
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
block|{
comment|// valid
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
specifier|public
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
name|findQualifyingTableName
parameter_list|(
name|String
name|columnName
parameter_list|,
name|SqlNode
name|ctx
parameter_list|)
block|{
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
block|}
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
return|return
name|ImmutableMap
operator|.
name|of
argument_list|()
return|;
block|}
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
comment|// cannot add to the empty scope
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
specifier|public
name|SqlWindow
name|lookupWindow
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// No windows defined in this scope.
return|return
literal|null
return|;
block|}
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
block|{
return|return
operator|(
operator|(
name|expr
operator|instanceof
name|SqlLiteral
operator|)
operator|||
operator|(
name|expr
operator|instanceof
name|SqlDynamicParam
operator|)
operator|||
operator|(
name|expr
operator|instanceof
name|SqlDataTypeSpec
operator|)
operator|)
condition|?
name|SqlMonotonicity
operator|.
name|CONSTANT
else|:
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
return|;
block|}
specifier|public
name|SqlNodeList
name|getOrderList
parameter_list|()
block|{
comment|// scope is not ordered
return|return
literal|null
return|;
block|}
block|}
end_class

begin_comment
comment|// End EmptyScope.java
end_comment

end_unit

