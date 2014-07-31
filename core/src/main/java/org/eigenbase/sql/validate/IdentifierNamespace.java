begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|validate
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
name|reltype
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
name|sql
operator|.
name|parser
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
name|ImmutableList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|Static
operator|.
name|RESOURCE
import|;
end_import

begin_comment
comment|/**  * Namespace whose contents are defined by the type of an {@link  * org.eigenbase.sql.SqlIdentifier identifier}.  */
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
comment|/**    * Creates an IdentifierNamespace.    *    * @param validator     Validator    * @param id            Identifier node    * @param enclosingNode Enclosing node    * @param parentScope   Parent scope which this namespace turns to in order to    *                      resolve objects    */
name|IdentifierNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlIdentifier
name|id
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
name|parentScope
operator|=
name|parentScope
expr_stmt|;
assert|assert
name|parentScope
operator|!=
literal|null
assert|;
assert|assert
name|id
operator|!=
literal|null
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|validateImpl
parameter_list|()
block|{
name|resolvedNamespace
operator|=
name|parentScope
operator|.
name|getTableNamespace
argument_list|(
name|id
operator|.
name|names
argument_list|)
expr_stmt|;
if|if
condition|(
name|resolvedNamespace
operator|==
literal|null
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
name|tableNameNotFound
argument_list|(
name|id
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
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
argument_list|<
name|SqlParserPos
argument_list|>
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
name|String
name|translate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|resolvedNamespace
operator|.
name|translate
argument_list|(
name|name
argument_list|)
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
block|}
end_class

begin_comment
comment|// End IdentifierNamespace.java
end_comment

end_unit

