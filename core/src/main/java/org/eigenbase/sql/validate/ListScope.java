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
name|util
operator|.
name|*
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
specifier|protected
specifier|final
name|List
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
argument_list|>
name|children
init|=
operator|new
name|ArrayList
argument_list|<
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
specifier|public
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
specifier|public
name|void
name|addChild
parameter_list|(
name|SqlValidatorNamespace
name|ns
parameter_list|,
name|String
name|alias
parameter_list|)
block|{
assert|assert
name|alias
operator|!=
literal|null
assert|;
name|children
operator|.
name|add
argument_list|(
name|Pair
operator|.
name|of
argument_list|(
name|alias
argument_list|,
name|ns
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
name|Pair
operator|.
name|right
argument_list|(
name|children
argument_list|)
return|;
block|}
specifier|protected
name|SqlValidatorNamespace
name|getChild
parameter_list|(
name|String
name|alias
parameter_list|)
block|{
if|if
condition|(
name|alias
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|children
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
block|{
throw|throw
name|Util
operator|.
name|newInternal
argument_list|(
literal|"no alias specified, but more than one table in from list"
argument_list|)
throw|;
block|}
return|return
name|children
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|right
return|;
block|}
else|else
block|{
specifier|final
name|int
name|i
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|match
argument_list|(
name|Pair
operator|.
name|left
argument_list|(
name|children
argument_list|)
argument_list|,
name|alias
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
return|return
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|right
return|;
block|}
return|return
literal|null
return|;
block|}
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
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
name|pair
range|:
name|children
control|)
block|{
name|addColumnNames
argument_list|(
name|pair
operator|.
name|right
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
specifier|public
name|void
name|findAliases
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
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
name|pair
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
name|pair
operator|.
name|left
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
specifier|public
name|String
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
name|int
name|count
init|=
literal|0
decl_stmt|;
name|String
name|tableName
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
name|child
range|:
name|children
control|)
block|{
specifier|final
name|RelDataType
name|rowType
init|=
name|child
operator|.
name|right
operator|.
name|getRowType
argument_list|()
decl_stmt|;
if|if
condition|(
name|validator
operator|.
name|catalogReader
operator|.
name|field
argument_list|(
name|rowType
argument_list|,
name|columnName
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|tableName
operator|=
name|child
operator|.
name|left
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
switch|switch
condition|(
name|count
condition|)
block|{
case|case
literal|0
case|:
return|return
name|parent
operator|.
name|findQualifyingTableName
argument_list|(
name|columnName
argument_list|,
name|ctx
argument_list|)
return|;
case|case
literal|1
case|:
return|return
name|tableName
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
specifier|public
name|SqlValidatorNamespace
name|resolve
parameter_list|(
name|String
name|name
parameter_list|,
name|SqlValidatorScope
index|[]
name|ancestorOut
parameter_list|,
name|int
index|[]
name|offsetOut
parameter_list|)
block|{
comment|// First resolve by looking through the child namespaces.
specifier|final
name|int
name|i
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|match
argument_list|(
name|Pair
operator|.
name|left
argument_list|(
name|children
argument_list|)
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
if|if
condition|(
name|ancestorOut
operator|!=
literal|null
condition|)
block|{
name|ancestorOut
index|[
literal|0
index|]
operator|=
name|this
expr_stmt|;
block|}
if|if
condition|(
name|offsetOut
operator|!=
literal|null
condition|)
block|{
name|offsetOut
index|[
literal|0
index|]
operator|=
name|i
expr_stmt|;
block|}
return|return
name|children
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|right
return|;
block|}
comment|// Then call the base class method, which will delegate to the
comment|// parent scope.
return|return
name|parent
operator|.
name|resolve
argument_list|(
name|name
argument_list|,
name|ancestorOut
argument_list|,
name|offsetOut
argument_list|)
return|;
block|}
specifier|public
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
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
name|pair
range|:
name|children
control|)
block|{
name|SqlValidatorNamespace
name|childNs
init|=
name|pair
operator|.
name|right
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
name|validator
operator|.
name|catalogReader
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

begin_comment
comment|// End ListScope.java
end_comment

end_unit

