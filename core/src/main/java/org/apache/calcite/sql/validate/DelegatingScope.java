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
name|DynamicRecordType
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
name|List
argument_list|<
name|String
argument_list|>
name|names
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
name|names
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
name|Collection
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
name|SqlQualified
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
block|{
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
specifier|final
name|Pair
argument_list|<
name|String
argument_list|,
name|SqlValidatorNamespace
argument_list|>
name|pair
init|=
name|findQualifyingTableName
argument_list|(
name|columnName
argument_list|,
name|identifier
argument_list|)
decl_stmt|;
specifier|final
name|String
name|tableName
init|=
name|pair
operator|.
name|left
decl_stmt|;
specifier|final
name|SqlValidatorNamespace
name|namespace
init|=
name|pair
operator|.
name|right
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
name|namespace
operator|.
name|getRowType
argument_list|()
argument_list|,
name|columnName
argument_list|)
decl_stmt|;
name|checkAmbiguousUnresolvedStar
argument_list|(
name|namespace
operator|.
name|getRowType
argument_list|()
argument_list|,
name|field
argument_list|,
name|identifier
argument_list|,
name|columnName
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
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
comment|// use resolved field name
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
name|SqlQualified
operator|.
name|create
argument_list|(
name|this
argument_list|,
literal|1
argument_list|,
name|namespace
argument_list|,
name|expanded
argument_list|)
return|;
block|}
default|default:
block|{
name|SqlValidatorNamespace
name|fromNs
init|=
literal|null
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|identifier
operator|.
name|names
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|i
init|=
name|size
operator|-
literal|1
decl_stmt|;
for|for
control|(
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
specifier|final
name|SqlIdentifier
name|prefix
init|=
name|identifier
operator|.
name|getComponent
argument_list|(
literal|0
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|fromNs
operator|=
name|resolve
argument_list|(
name|prefix
operator|.
name|names
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|fromNs
operator|!=
literal|null
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|fromNs
operator|==
literal|null
operator|||
name|fromNs
operator|instanceof
name|SchemaNamespace
condition|)
block|{
specifier|final
name|SqlIdentifier
name|prefix1
init|=
name|identifier
operator|.
name|skipLast
argument_list|(
literal|1
argument_list|)
decl_stmt|;
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|prefix1
argument_list|,
name|RESOURCE
operator|.
name|tableNameNotFound
argument_list|(
name|prefix1
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|RelDataType
name|fromRowType
init|=
name|fromNs
operator|.
name|getRowType
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
init|;
name|j
operator|<
name|size
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|SqlIdentifier
name|last
init|=
name|identifier
operator|.
name|getComponent
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|columnName
operator|=
name|last
operator|.
name|getSimple
argument_list|()
expr_stmt|;
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
operator|==
literal|null
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|last
argument_list|,
name|RESOURCE
operator|.
name|columnNotFoundInTable
argument_list|(
name|columnName
argument_list|,
name|identifier
operator|.
name|getComponent
argument_list|(
literal|0
argument_list|,
name|j
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
name|checkAmbiguousUnresolvedStar
argument_list|(
name|fromRowType
argument_list|,
name|field
argument_list|,
name|identifier
argument_list|,
name|columnName
argument_list|)
expr_stmt|;
comment|// normalize case to match definition, in a copy of the identifier
name|identifier
operator|=
name|identifier
operator|.
name|setName
argument_list|(
name|j
argument_list|,
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fromRowType
operator|=
name|field
operator|.
name|getType
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
literal|1
condition|)
block|{
comment|// Simplify overqualified identifiers.
comment|// For example, schema.emp.deptno becomes emp.deptno.
comment|//
comment|// It is safe to convert schema.emp or database.schema.emp to emp
comment|// because it would not have resolved if the FROM item had an alias. The
comment|// following query is invalid:
comment|//   SELECT schema.emp.deptno FROM schema.emp AS e
name|identifier
operator|=
name|identifier
operator|.
name|getComponent
argument_list|(
name|i
operator|-
literal|1
argument_list|,
name|identifier
operator|.
name|names
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|SqlQualified
operator|.
name|create
argument_list|(
name|this
argument_list|,
name|i
argument_list|,
name|fromNs
argument_list|,
name|identifier
argument_list|)
return|;
block|}
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
specifier|private
name|void
name|checkAmbiguousUnresolvedStar
parameter_list|(
name|RelDataType
name|fromRowType
parameter_list|,
name|RelDataTypeField
name|field
parameter_list|,
name|SqlIdentifier
name|identifier
parameter_list|,
name|String
name|columnName
parameter_list|)
block|{
if|if
condition|(
name|field
operator|!=
literal|null
operator|&&
name|field
operator|.
name|isDynamicStar
argument_list|()
operator|&&
operator|!
name|DynamicRecordType
operator|.
name|isDynamicStarColName
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
comment|// Make sure fromRowType only contains one star column.
comment|// Having more than one star columns implies ambiguous column.
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|RelDataTypeField
name|possibleStar
range|:
name|fromRowType
operator|.
name|getFieldList
argument_list|()
control|)
block|{
if|if
condition|(
name|possibleStar
operator|.
name|isDynamicStar
argument_list|()
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|count
operator|>
literal|1
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|identifier
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

