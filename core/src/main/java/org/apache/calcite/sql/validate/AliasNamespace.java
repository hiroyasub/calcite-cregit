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
name|Util
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
comment|/**  * Namespace for an<code>AS t(c1, c2, ...)</code> clause.  *  *<p>A namespace is necessary only if there is a column list, in order to  * re-map column names; a<code>relation AS t</code> clause just uses the same  * namespace as<code>relation</code>.  */
end_comment

begin_class
specifier|public
class|class
name|AliasNamespace
extends|extends
name|AbstractNamespace
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|protected
specifier|final
name|SqlCall
name|call
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates an AliasNamespace.    *    * @param validator     Validator    * @param call          Call to AS operator    * @param enclosingNode Enclosing node    */
specifier|protected
name|AliasNamespace
parameter_list|(
name|SqlValidatorImpl
name|validator
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlNode
name|enclosingNode
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
name|call
operator|=
name|call
expr_stmt|;
assert|assert
name|call
operator|.
name|getOperator
argument_list|()
operator|==
name|SqlStdOperatorTable
operator|.
name|AS
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|protected
name|RelDataType
name|validateImpl
parameter_list|(
name|RelDataType
name|targetRowType
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|nameList
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|operands
init|=
name|call
operator|.
name|getOperandList
argument_list|()
decl_stmt|;
specifier|final
name|SqlValidatorNamespace
name|childNs
init|=
name|validator
operator|.
name|getNamespace
argument_list|(
name|operands
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|childNs
operator|.
name|getRowTypeSansSystemColumns
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|SqlNode
argument_list|>
name|columnNames
init|=
name|Util
operator|.
name|skip
argument_list|(
name|operands
argument_list|,
literal|2
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SqlNode
name|operand
range|:
name|columnNames
control|)
block|{
name|String
name|name
init|=
operator|(
operator|(
name|SqlIdentifier
operator|)
name|operand
operator|)
operator|.
name|getSimple
argument_list|()
decl_stmt|;
if|if
condition|(
name|nameList
operator|.
name|contains
argument_list|(
name|name
argument_list|)
condition|)
block|{
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|operand
argument_list|,
name|RESOURCE
operator|.
name|aliasListDuplicate
argument_list|(
name|name
argument_list|)
argument_list|)
throw|;
block|}
name|nameList
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nameList
operator|.
name|size
argument_list|()
operator|!=
name|rowType
operator|.
name|getFieldCount
argument_list|()
condition|)
block|{
comment|// Position error over all column names
specifier|final
name|SqlNode
name|node
init|=
name|operands
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|?
name|operands
operator|.
name|get
argument_list|(
literal|2
argument_list|)
else|:
operator|new
name|SqlNodeList
argument_list|(
name|columnNames
argument_list|,
name|SqlParserPos
operator|.
name|sum
argument_list|(
name|columnNames
argument_list|)
argument_list|)
decl_stmt|;
throw|throw
name|validator
operator|.
name|newValidationError
argument_list|(
name|node
argument_list|,
name|RESOURCE
operator|.
name|aliasListDegree
argument_list|(
name|rowType
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|getString
argument_list|(
name|rowType
argument_list|)
argument_list|,
name|nameList
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|List
argument_list|<
name|RelDataType
argument_list|>
name|typeList
init|=
operator|new
name|ArrayList
argument_list|<
name|RelDataType
argument_list|>
argument_list|()
decl_stmt|;
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
name|typeList
operator|.
name|add
argument_list|(
name|field
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|validator
operator|.
name|getTypeFactory
argument_list|()
operator|.
name|createStructType
argument_list|(
name|typeList
argument_list|,
name|nameList
argument_list|)
return|;
block|}
specifier|private
name|String
name|getString
parameter_list|(
name|RelDataType
name|rowType
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|field
operator|.
name|getIndex
argument_list|()
operator|>
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|SqlNode
name|getNode
parameter_list|()
block|{
return|return
name|call
return|;
block|}
specifier|public
name|String
name|translate
parameter_list|(
name|String
name|name
parameter_list|)
block|{
specifier|final
name|RelDataType
name|underlyingRowType
init|=
name|validator
operator|.
name|getValidatedNodeType
argument_list|(
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
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
if|if
condition|(
name|field
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|underlyingRowType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|getName
argument_list|()
return|;
block|}
operator|++
name|i
expr_stmt|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"unknown field '"
operator|+
name|name
operator|+
literal|"' in rowtype "
operator|+
name|underlyingRowType
argument_list|)
throw|;
block|}
block|}
end_class

begin_comment
comment|// End AliasNamespace.java
end_comment

end_unit

