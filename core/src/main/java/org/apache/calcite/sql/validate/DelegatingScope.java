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
name|SqlSelect
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
name|sql
operator|.
name|parser
operator|.
name|SqlParserPos
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
name|List
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
comment|/**  * A scope which delegates all requests to its parent scope. Use this as a base  * class for defining nested scopes.  */
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
comment|/**    * Parent scope. This is where to look next to resolve an identifier; it is    * not always the parent object in the parse tree.    *    *<p>This is never null: at the top of the tree, it is an    * {@link EmptyScope}.    */
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
comment|/**    * Creates a<code>DelegatingScope</code>.    *    * @param parent Parent scope    */
name|DelegatingScope
parameter_list|(
name|SqlValidatorScope
name|parent
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
assert|assert
name|parent
operator|!=
literal|null
assert|;
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
for|for
control|(
name|RelDataTypeField
name|field
range|:
name|rowType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
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
name|COLUMN
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
name|parent
operator|.
name|nullifyType
argument_list|(
name|node
argument_list|,
name|type
argument_list|)
return|;
block|}
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
return|return
name|parent
operator|.
name|getTableNamespace
argument_list|(
name|names
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
comment|/**    * Converts an identifier into a fully-qualified identifier. For example,    * the "empno" in "select empno from emp natural join dept" becomes    * "emp.empno".    *    *<p>If the identifier cannot be resolved, throws. Never returns null.    */
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
name|size
argument_list|()
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
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
comment|// todo: do implicit collation here
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
name|ImmutableList
operator|.
name|of
argument_list|(
name|tableName
argument_list|,
name|columnName
argument_list|)
argument_list|,
literal|null
argument_list|,
name|pos
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|SqlParserPos
operator|.
name|ZERO
argument_list|,
name|pos
argument_list|)
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
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
name|RESOURCE
operator|.
name|tableNameNotFound
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
operator|.
name|get
argument_list|(
literal|1
argument_list|)
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
name|RelDataTypeField
name|field
init|=
name|validator
operator|.
name|catalogReader
operator|.
name|field
argument_list|(
name|fromRowType
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
name|identifier
operator|.
name|setName
argument_list|(
literal|1
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
comment|// normalize case to match defn
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
name|RESOURCE
operator|.
name|columnNotFoundInTable
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
name|size
argument_list|()
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
comment|/**    * Returns the parent scope of this<code>DelegatingScope</code>.    */
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

