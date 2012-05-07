begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
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
name|resource
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

begin_comment
comment|/**  * A scope which delegates all requests to its parent scope. Use this as a base  * class for defining nested scopes.  *  * @author jhyde  * @version $Id$  * @since Mar 25, 2003  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DelegatingScope
implements|implements
name|SqlValidatorScope
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * Parent scope. This is where to look next to resolve an identifier; it is      * not always the parent object in the parse tree.      *      *<p>This is never null: at the top of the tree, it is an {@link      * EmptyScope}.      */
specifier|protected
specifier|final
name|SqlValidatorScope
name|parent
decl_stmt|;
specifier|protected
specifier|final
name|SqlValidatorImpl
name|validator
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**      * Creates a<code>DelegatingScope</code>.      *      * @param parent Parent scope      */
name|DelegatingScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|Util
operator|.
name|pre
argument_list|(
name|parent
operator|!=
literal|null
argument_list|,
literal|"parent != null"
argument_list|)
expr_stmt|;
name|this
operator|.
name|validator
operator|=
operator|(
name|SqlValidatorImpl
operator|)
name|parent
operator|.
name|getValidator
argument_list|()
expr_stmt|;
name|this
operator|.
name|parent
operator|=
name|parent
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
comment|// By default, you cannot add to a scope. Derived classes can
comment|// override.
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
specifier|protected
name|void
name|addColumnNames
parameter_list|(
name|SqlValidatorNamespace
name|ns
parameter_list|,
name|List
argument_list|<
name|SqlMoniker
argument_list|>
name|colNames
parameter_list|)
block|{
specifier|final
name|RelDataType
name|rowType
decl_stmt|;
try|try
block|{
name|rowType
operator|=
name|ns
operator|.
name|getRowType
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
comment|// namespace is not good - bail out.
return|return;
block|}
specifier|final
name|RelDataTypeField
index|[]
name|fields
init|=
name|rowType
operator|.
name|getFields
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|fields
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|RelDataTypeField
name|field
init|=
name|fields
index|[
name|i
index|]
decl_stmt|;
name|colNames
operator|.
name|add
argument_list|(
operator|new
name|SqlMonikerImpl
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|,
name|SqlMonikerType
operator|.
name|Column
argument_list|)
argument_list|)
expr_stmt|;
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
name|String
name|columnName
parameter_list|,
name|SqlNode
name|ctx
parameter_list|)
block|{
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
name|parent
operator|.
name|resolveColumn
argument_list|(
name|name
argument_list|,
name|ctx
argument_list|)
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
if|if
condition|(
name|call
operator|instanceof
name|SqlSelect
condition|)
block|{
return|return
name|validator
operator|.
name|getSelectScope
argument_list|(
operator|(
name|SqlSelect
operator|)
name|call
argument_list|)
return|;
block|}
return|return
name|this
return|;
block|}
specifier|public
name|SqlValidator
name|getValidator
parameter_list|()
block|{
return|return
name|validator
return|;
block|}
comment|/**      * Converts an identifier into a fully-qualified identifier. For example,      * the "empno" in "select empno from emp natural join dept" becomes      * "emp.empno".      *      *<p>If the identifier cannot be resolved, throws. Never returns null.      */
specifier|public
name|SqlIdentifier
name|fullyQualify
parameter_list|(
name|SqlIdentifier
name|identifier
parameter_list|)
block|{
if|if
condition|(
name|identifier
operator|.
name|isStar
argument_list|()
condition|)
block|{
return|return
name|identifier
return|;
block|}
name|String
name|tableName
decl_stmt|;
name|String
name|columnName
decl_stmt|;
switch|switch
condition|(
name|identifier
operator|.
name|names
operator|.
name|length
condition|)
block|{
case|case
literal|1
case|:
name|columnName
operator|=
name|identifier
operator|.
name|names
index|[
literal|0
index|]
expr_stmt|;
name|tableName
operator|=
name|findQualifyingTableName
argument_list|(
name|columnName
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
comment|//todo: do implicit collation here
specifier|final
name|SqlParserPos
name|pos
init|=
name|identifier
operator|.
name|getParserPosition
argument_list|()
decl_stmt|;
name|SqlIdentifier
name|expanded
init|=
operator|new
name|SqlIdentifier
argument_list|(
operator|new
name|String
index|[]
block|{
name|tableName
block|,
name|columnName
block|}
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|,
operator|new
name|SqlParserPos
index|[]
block|{
name|SqlParserPos
operator|.
name|ZERO
block|,
name|pos
block|}
argument_list|)
decl_stmt|;
name|validator
operator|.
name|setOriginal
argument_list|(
name|expanded
argument_list|,
name|identifier
argument_list|)
expr_stmt|;
return|return
name|expanded
return|;
case|case
literal|2
case|:
name|tableName
operator|=
name|identifier
operator|.
name|names
index|[
literal|0
index|]
expr_stmt|;
specifier|final
name|SqlValidatorNamespace
name|fromNs
init|=
name|resolve
argument_list|(
name|tableName
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|fromNs
operator|==
literal|null
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|identifier
operator|.
name|getComponent
argument_list|(
literal|0
argument_list|)
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|TableNameNotFound
operator|.
name|ex
argument_list|(
name|tableName
argument_list|)
argument_list|)
throw|;
block|}
name|columnName
operator|=
name|identifier
operator|.
name|names
index|[
literal|1
index|]
expr_stmt|;
specifier|final
name|RelDataType
name|fromRowType
init|=
name|fromNs
operator|.
name|getRowType
argument_list|()
decl_stmt|;
specifier|final
name|RelDataType
name|type
init|=
name|SqlValidatorUtil
operator|.
name|lookupFieldType
argument_list|(
name|fromRowType
argument_list|,
name|columnName
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
return|return
name|identifier
return|;
comment|// it was fine already
block|}
else|else
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|identifier
operator|.
name|getComponent
argument_list|(
literal|1
argument_list|)
argument_list|,
name|EigenbaseResource
operator|.
name|instance
argument_list|()
operator|.
name|ColumnNotFoundInTable
operator|.
name|ex
argument_list|(
name|columnName
argument_list|,
name|tableName
argument_list|)
argument_list|)
throw|;
block|}
default|default:
comment|// NOTE jvs 26-May-2004:  lengths greater than 2 are possible
comment|// for row and structured types
assert|assert
name|identifier
operator|.
name|names
operator|.
name|length
operator|>
literal|0
assert|;
return|return
name|identifier
return|;
block|}
block|}
specifier|public
name|void
name|validateExpr
parameter_list|(
name|SqlNode
name|expr
parameter_list|)
block|{
comment|// Do not delegate to parent. An expression valid in this scope may not
comment|// be valid in the parent scope.
block|}
specifier|public
name|SqlWindow
name|lookupWindow
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|parent
operator|.
name|lookupWindow
argument_list|(
name|name
argument_list|)
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
name|parent
operator|.
name|getMonotonicity
argument_list|(
name|expr
argument_list|)
return|;
block|}
specifier|public
name|SqlNodeList
name|getOrderList
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getOrderList
argument_list|()
return|;
block|}
comment|/**      * Returns the parent scope of this<code>DelegatingScope</code>.      */
specifier|public
name|SqlValidatorScope
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
block|}
end_class

begin_comment
comment|// End DelegatingScope.java
end_comment

end_unit

