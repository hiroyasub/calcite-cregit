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
name|SqlKind
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
name|parser
operator|.
name|SqlParserPos
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
name|Collections
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
name|Objects
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
comment|/**  * Namespace whose contents are defined by the type of an  * {@link org.apache.calcite.sql.SqlIdentifier identifier}.  */
end_comment

begin_class
specifier|public
class|class
name|IdentifierNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|SqlIdentifier
name|id
decl_stmt|;
specifier|private
specifier|final
name|SqlValidatorScope
name|parentScope
decl_stmt|;
specifier|public
specifier|final
name|SqlNodeList
name|extendList
decl_stmt|;
comment|/**    * The underlying namespace. Often a {@link TableNamespace}.    * Set on validate.    */
specifier|private
name|SqlValidatorNamespace
name|resolvedNamespace
decl_stmt|;
comment|/**    * List of monotonic expressions. Set on validate.    */
specifier|private
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
argument_list|>
name|monotonicExprs
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an IdentifierNamespace.    *    * @param validator     Validator    * @param id            Identifier node (or "identifier EXTEND column-list")    * @param extendList    Extension columns, or null    * @param enclosingNode Enclosing node    * @param parentScope   Parent scope which this namespace turns to in order to    */
name|IdentifierNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlIdentifier
name|id
parameter_list|,
annotation|@
name|Nullable
name|SqlNodeList
name|extendList
parameter_list|,
name|SqlNode
name|enclosingNode
parameter_list|,
name|SqlValidatorScope
name|parentScope
parameter_list|)
block|{
name|super
argument_list|(
name|validator
argument_list|,
name|enclosingNode
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|extendList
operator|=
name|extendList
expr_stmt|;
name|this
operator|.
name|parentScope
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|parentScope
argument_list|)
expr_stmt|;
block|}
name|IdentifierNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlNode
name|node
parameter_list|,
name|SqlNode
name|enclosingNode
parameter_list|,
name|SqlValidatorScope
name|parentScope
parameter_list|)
block|{
name|this
argument_list|(
name|validator
argument_list|,
name|split
argument_list|(
name|node
argument_list|)
operator|.
name|left
argument_list|,
name|split
argument_list|(
name|node
argument_list|)
operator|.
name|right
argument_list|,
name|enclosingNode
argument_list|,
name|parentScope
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
specifier|static
name|Pair
argument_list|<
name|SqlIdentifier
argument_list|,
name|SqlNodeList
argument_list|>
name|split
parameter_list|(
name|SqlNode
name|node
parameter_list|)
block|{
switch|switch
condition|(
name|node
operator|.
name|getKind
argument_list|()
condition|)
block|{
case|case
name|EXTEND
case|:
specifier|final
name|SqlCall
name|call
init|=
operator|(
name|SqlCall
operator|)
name|node
decl_stmt|;
specifier|final
name|SqlNode
name|operand0
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|SqlIdentifier
name|identifier
init|=
name|operand0
operator|.
name|getKind
argument_list|()
operator|==
name|SqlKind
operator|.
name|TABLE_REF
condition|?
operator|(
operator|(
name|SqlCall
operator|)
name|operand0
operator|)
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
else|:
operator|(
name|SqlIdentifier
operator|)
name|operand0
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|identifier
argument_list|,
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
case|case
name|TABLE_REF
case|:
specifier|final
name|SqlCall
name|tableRef
init|=
operator|(
name|SqlCall
operator|)
name|node
decl_stmt|;
return|return
name|Pair
operator|.
name|of
argument_list|(
name|tableRef
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|,
literal|null
argument_list|)
return|;
default|default:
return|return
name|Pair
operator|.
name|of
argument_list|(
operator|(
name|SqlIdentifier
operator|)
name|node
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
specifier|private
name|SqlValidatorNamespace
name|resolveImpl
parameter_list|(
name|SqlIdentifier
name|id
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
name|SqlValidatorScope
operator|.
name|ResolvedImpl
name|resolved
init|=
operator|new
name|SqlValidatorScope
operator|.
name|ResolvedImpl
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|names
init|=
name|SqlIdentifier
operator|.
name|toStar
argument_list|(
name|id
operator|.
name|names
argument_list|)
decl_stmt|;
try|try
block|{
name|parentScope
operator|.
name|resolveTable
argument_list|(
name|names
argument_list|,
name|nameMatcher
argument_list|,
name|SqlValidatorScope
operator|.
name|Path
operator|.
name|EMPTY
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CyclicDefinitionException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|depth
operator|==
literal|1
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|id
argument_list|,
name|RESOURCE
operator|.
name|cyclicDefinition
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|,
name|SqlIdentifier
operator|.
name|getString
argument_list|(
name|e
operator|.
name|path
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|CyclicDefinitionException
argument_list|(
name|e
operator|.
name|depth
operator|-
literal|1
argument_list|,
name|e
operator|.
name|path
argument_list|)
throw|;
block|}
block|}
name|SqlValidatorScope
operator|.
name|Resolve
name|previousResolve
init|=
literal|null
decl_stmt|;
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
specifier|final
name|SqlValidatorScope
operator|.
name|Resolve
name|resolve
init|=
name|previousResolve
operator|=
name|resolved
operator|.
name|only
argument_list|()
decl_stmt|;
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
return|return
name|resolve
operator|.
name|namespace
return|;
block|}
comment|// If we're not case sensitive, give an error.
comment|// If we're case sensitive, we'll shortly try again and give an error
comment|// then.
if|if
condition|(
operator|!
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|id
argument_list|,
name|RESOURCE
operator|.
name|objectNotFoundWithin
argument_list|(
name|resolve
operator|.
name|remainingNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|SqlIdentifier
operator|.
name|getString
argument_list|(
name|resolve
operator|.
name|path
operator|.
name|stepNames
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
block|}
comment|// Failed to match.  If we're matching case-sensitively, try a more
comment|// lenient match. If we find something we can offer a helpful hint.
if|if
condition|(
name|nameMatcher
operator|.
name|isCaseSensitive
argument_list|()
condition|)
block|{
specifier|final
name|SqlNameMatcher
name|liberalMatcher
init|=
name|SqlNameMatchers
operator|.
name|liberal
argument_list|()
decl_stmt|;
name|resolved
operator|.
name|clear
argument_list|()
expr_stmt|;
name|parentScope
operator|.
name|resolveTable
argument_list|(
name|names
argument_list|,
name|liberalMatcher
argument_list|,
name|SqlValidatorScope
operator|.
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
specifier|final
name|SqlValidatorScope
operator|.
name|Resolve
name|resolve
init|=
name|resolved
operator|.
name|only
argument_list|()
decl_stmt|;
if|if
condition|(
name|resolve
operator|.
name|remainingNames
operator|.
name|isEmpty
argument_list|()
operator|||
name|previousResolve
operator|==
literal|null
condition|)
block|{
comment|// We didn't match it case-sensitive, so they must have had the
comment|// right identifier, wrong case.
comment|//
comment|// If previousResolve is null, we matched nothing case-sensitive and
comment|// everything case-insensitive, so the mismatch must have been at
comment|// position 0.
specifier|final
name|int
name|i
init|=
name|previousResolve
operator|==
literal|null
condition|?
literal|0
else|:
name|previousResolve
operator|.
name|path
operator|.
name|stepCount
argument_list|()
decl_stmt|;
specifier|final
name|int
name|offset
init|=
name|resolve
operator|.
name|path
operator|.
name|stepCount
argument_list|()
operator|+
name|resolve
operator|.
name|remainingNames
operator|.
name|size
argument_list|()
operator|-
name|names
operator|.
name|size
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|prefix
init|=
name|resolve
operator|.
name|path
operator|.
name|stepNames
argument_list|()
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|offset
operator|+
name|i
argument_list|)
decl_stmt|;
specifier|final
name|String
name|next
init|=
name|resolve
operator|.
name|path
operator|.
name|stepNames
argument_list|()
operator|.
name|get
argument_list|(
name|i
operator|+
name|offset
argument_list|)
decl_stmt|;
if|if
condition|(
name|prefix
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|id
argument_list|,
name|RESOURCE
operator|.
name|objectNotFoundDidYouMean
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|next
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|id
argument_list|,
name|RESOURCE
operator|.
name|objectNotFoundWithinDidYouMean
argument_list|(
name|names
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|SqlIdentifier
operator|.
name|getString
argument_list|(
name|prefix
argument_list|)
argument_list|,
name|next
argument_list|)
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|id
argument_list|,
name|RESOURCE
operator|.
name|objectNotFoundWithin
argument_list|(
name|resolve
operator|.
name|remainingNames
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|SqlIdentifier
operator|.
name|getString
argument_list|(
name|resolve
operator|.
name|path
operator|.
name|stepNames
argument_list|()
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|id
argument_list|,
name|RESOURCE
operator|.
name|objectNotFound
argument_list|(
name|id
operator|.
name|getComponent
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
specifier|public
name|RelDataType
name|validateImpl
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|)
block|{
name|resolvedNamespace
operator|=
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|resolveImpl
argument_list|(
name|id
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|resolvedNamespace
operator|instanceof
name|TableNamespace
condition|)
block|{
name|SqlValidatorTable
name|table
init|=
name|resolvedNamespace
operator|.
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|validator
operator|.
name|shouldExpandIdentifiers
argument_list|()
condition|)
block|{
comment|// TODO:  expand qualifiers for column references also
name|List
argument_list|<
name|String
argument_list|>
name|qualifiedNames
init|=
name|table
operator|.
name|getQualifiedName
argument_list|()
decl_stmt|;
if|if
condition|(
name|qualifiedNames
operator|!=
literal|null
condition|)
block|{
comment|// Assign positions to the components of the fully-qualified
comment|// identifier, as best we can. We assume that qualification
comment|// adds names to the front, e.g. FOO.BAR becomes BAZ.FOO.BAR.
name|List
argument_list|<
name|SqlParserPos
argument_list|>
name|poses
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|Collections
operator|.
name|nCopies
argument_list|(
name|qualifiedNames
operator|.
name|size
argument_list|()
argument_list|,
name|id
operator|.
name|getParserPosition
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|offset
init|=
name|qualifiedNames
operator|.
name|size
argument_list|()
operator|-
name|id
operator|.
name|names
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// Test offset in case catalog supports fewer qualifiers than catalog
comment|// reader.
if|if
condition|(
name|offset
operator|>=
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|id
operator|.
name|names
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|poses
operator|.
name|set
argument_list|(
name|i
operator|+
name|offset
argument_list|,
name|id
operator|.
name|getComponentParserPosition
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|id
operator|.
name|setNames
argument_list|(
name|qualifiedNames
argument_list|,
name|poses
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|RelDataType
name|rowType
init|=
name|resolvedNamespace
operator|.
name|getRowType
argument_list|()
decl_stmt|;
if|if
condition|(
name|extendList
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
operator|(
name|resolvedNamespace
operator|instanceof
name|TableNamespace
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"cannot convert"
argument_list|)
throw|;
block|}
name|resolvedNamespace
operator|=
operator|(
operator|(
name|TableNamespace
operator|)
name|resolvedNamespace
operator|)
operator|.
name|extend
argument_list|(
name|extendList
argument_list|)
expr_stmt|;
name|rowType
operator|=
name|resolvedNamespace
operator|.
name|getRowType
argument_list|()
expr_stmt|;
block|}
comment|// Build a list of monotonic expressions.
specifier|final
name|ImmutableList
operator|.
name|Builder
argument_list|<
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
argument_list|>
name|builder
init|=
name|ImmutableList
operator|.
name|builder
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|rowType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|fields
control|)
block|{
specifier|final
name|String
name|fieldName
init|=
name|field
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|SqlMonotonicity
name|monotonicity
init|=
name|resolvedNamespace
operator|.
name|getMonotonicity
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|monotonicity
operator|!=
name|SqlMonotonicity
operator|.
name|NOT_MONOTONIC
condition|)
block|{
name|builder
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
operator|(
name|SqlNode
operator|)
operator|new
name|SqlIdentifier
argument_list|(
name|fieldName
argument_list|,
name|SqlParserPos
operator|.
name|ZERO
argument_list|)
argument_list|,
name|monotonicity
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|monotonicExprs
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
comment|// Validation successful.
return|return
name|rowType
return|;
block|}
specifier|public
name|SqlIdentifier
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlValidatorNamespace
name|resolve
parameter_list|()
block|{
assert|assert
name|resolvedNamespace
operator|!=
literal|null
operator|:
literal|"must call validate first"
assert|;
return|return
name|resolvedNamespace
operator|.
name|resolve
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlValidatorTable
name|getTable
parameter_list|()
block|{
return|return
name|resolvedNamespace
operator|==
literal|null
condition|?
literal|null
else|:
name|resolve
argument_list|()
operator|.
name|getTable
argument_list|()
return|;
block|}
specifier|public
name|List
argument_list|<
name|Pair
argument_list|<
name|SqlNode
argument_list|,
name|SqlMonotonicity
argument_list|>
argument_list|>
name|getMonotonicExprs
parameter_list|()
block|{
return|return
name|monotonicExprs
return|;
block|}
annotation|@
name|Override
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
specifier|final
name|SqlValidatorTable
name|table
init|=
name|getTable
argument_list|()
decl_stmt|;
return|return
name|table
operator|.
name|getMonotonicity
argument_list|(
name|columnName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|supportsModality
parameter_list|(
name|SqlModality
name|modality
parameter_list|)
block|{
specifier|final
name|SqlValidatorTable
name|table
init|=
name|getTable
argument_list|()
decl_stmt|;
if|if
condition|(
name|table
operator|==
literal|null
condition|)
block|{
return|return
name|modality
operator|==
name|SqlModality
operator|.
name|RELATION
return|;
block|}
return|return
name|table
operator|.
name|supportsModality
argument_list|(
name|modality
argument_list|)
return|;
block|}
block|}
end_class

end_unit

