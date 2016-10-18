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
name|advise
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
name|RelDataTypeFactory
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
name|runtime
operator|.
name|CalciteException
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
name|SqlOperatorTable
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeUtil
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
name|validate
operator|.
name|OverScope
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
name|validate
operator|.
name|SqlConformance
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
name|validate
operator|.
name|SqlModality
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
name|validate
operator|.
name|SqlValidatorCatalogReader
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
name|validate
operator|.
name|SqlValidatorImpl
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
name|validate
operator|.
name|SqlValidatorNamespace
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
name|validate
operator|.
name|SqlValidatorScope
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
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *<code>SqlAdvisorValidator</code> is used by {@link SqlAdvisor} to traverse  * the parse tree of a SQL statement, not for validation purpose but for setting  * up the scopes and namespaces to facilitate retrieval of SQL statement  * completion hints.  */
end_comment

begin_class
specifier|public
class|class
name|SqlAdvisorValidator
extends|extends
name|SqlValidatorImpl
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|Set
argument_list|<
name|SqlValidatorNamespace
argument_list|>
name|activeNamespaces
init|=
operator|new
name|HashSet
argument_list|<
name|SqlValidatorNamespace
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|RelDataType
name|emptyStructType
init|=
name|SqlTypeUtil
operator|.
name|createEmptyStructType
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
comment|//~ Constructors -----------------------------------------------------------
comment|/**    * Creates a SqlAdvisor validator.    *    * @param opTab         Operator table    * @param catalogReader Catalog reader    * @param typeFactory   Type factory    * @param conformance   Compatibility mode    */
specifier|public
name|SqlAdvisorValidator
parameter_list|(
name|SqlOperatorTable
name|opTab
parameter_list|,
name|SqlValidatorCatalogReader
name|catalogReader
parameter_list|,
name|RelDataTypeFactory
name|typeFactory
parameter_list|,
name|SqlConformance
name|conformance
parameter_list|)
block|{
name|super
argument_list|(
name|opTab
argument_list|,
name|catalogReader
argument_list|,
name|typeFactory
argument_list|,
name|conformance
argument_list|)
expr_stmt|;
assert|assert
name|opTab
operator|!=
literal|null
assert|;
assert|assert
name|catalogReader
operator|!=
literal|null
assert|;
assert|assert
name|typeFactory
operator|!=
literal|null
assert|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * Registers the identifier and its scope into a map keyed by ParserPostion.    */
specifier|public
name|void
name|validateIdentifier
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|registerId
argument_list|(
name|id
argument_list|,
name|scope
argument_list|)
expr_stmt|;
try|try
block|{
name|super
operator|.
name|validateIdentifier
argument_list|(
name|id
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CalciteException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
name|TRACER
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|registerId
parameter_list|(
name|SqlIdentifier
name|id
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
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
specifier|final
name|SqlParserPos
name|subPos
init|=
name|id
operator|.
name|getComponentParserPosition
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|SqlIdentifier
name|subId
init|=
name|i
operator|==
name|id
operator|.
name|names
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|?
name|id
else|:
operator|new
name|SqlIdentifier
argument_list|(
name|id
operator|.
name|names
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
name|i
operator|+
literal|1
argument_list|)
argument_list|,
name|subPos
argument_list|)
decl_stmt|;
name|idPositions
operator|.
name|put
argument_list|(
name|subPos
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|IdInfo
argument_list|(
name|scope
argument_list|,
name|subId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|SqlNode
name|expand
parameter_list|(
name|SqlNode
name|expr
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
comment|// Disable expansion. It doesn't help us come up with better hints.
return|return
name|expr
return|;
block|}
specifier|public
name|SqlNode
name|expandOrderExpr
parameter_list|(
name|SqlSelect
name|select
parameter_list|,
name|SqlNode
name|orderExpr
parameter_list|)
block|{
comment|// Disable expansion. It doesn't help us come up with better hints.
return|return
name|orderExpr
return|;
block|}
comment|/**    * Calls the parent class method and mask Farrago exception thrown.    */
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlNode
name|operand
parameter_list|)
block|{
comment|// REVIEW Do not mask Error (indicates a serious system problem) or
comment|// UnsupportedOperationException (a bug). I have to mask
comment|// UnsupportedOperationException because
comment|// SqlValidatorImpl.getValidatedNodeType throws it for an unrecognized
comment|// identifier node I have to mask Error as well because
comment|// AbstractNamespace.getRowType  called in super.deriveType can do a
comment|// Util.permAssert that throws Error
try|try
block|{
return|return
name|super
operator|.
name|deriveType
argument_list|(
name|scope
argument_list|,
name|operand
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|CalciteException
name|e
parameter_list|)
block|{
return|return
name|unknownType
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|e
parameter_list|)
block|{
return|return
name|unknownType
return|;
block|}
catch|catch
parameter_list|(
name|Error
name|e
parameter_list|)
block|{
return|return
name|unknownType
return|;
block|}
block|}
comment|// we do not need to validate from clause for traversing the parse tree
comment|// because there is no SqlIdentifier in from clause that need to be
comment|// registered into {@link #idPositions} map
specifier|protected
name|void
name|validateFrom
parameter_list|(
name|SqlNode
name|node
parameter_list|,
name|RelDataType
name|targetRowType
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|validateFrom
argument_list|(
name|node
argument_list|,
name|targetRowType
argument_list|,
name|scope
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CalciteException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
name|TRACER
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Calls the parent class method and masks Farrago exception thrown.    */
specifier|protected
name|void
name|validateWhereClause
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|validateWhereClause
argument_list|(
name|select
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CalciteException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
name|TRACER
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Calls the parent class method and masks Farrago exception thrown.    */
specifier|protected
name|void
name|validateHavingClause
parameter_list|(
name|SqlSelect
name|select
parameter_list|)
block|{
try|try
block|{
name|super
operator|.
name|validateHavingClause
argument_list|(
name|select
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CalciteException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
name|TRACER
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|validateOver
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
try|try
block|{
specifier|final
name|OverScope
name|overScope
init|=
operator|(
name|OverScope
operator|)
name|getOverScope
argument_list|(
name|call
argument_list|)
decl_stmt|;
specifier|final
name|SqlNode
name|relation
init|=
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|validateFrom
argument_list|(
name|relation
argument_list|,
name|unknownType
argument_list|,
name|scope
argument_list|)
expr_stmt|;
specifier|final
name|SqlNode
name|window
init|=
name|call
operator|.
name|operand
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|SqlValidatorScope
name|opScope
init|=
name|scopes
operator|.
name|get
argument_list|(
name|relation
argument_list|)
decl_stmt|;
if|if
condition|(
name|opScope
operator|==
literal|null
condition|)
block|{
name|opScope
operator|=
name|overScope
expr_stmt|;
block|}
name|validateWindow
argument_list|(
name|window
argument_list|,
name|opScope
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CalciteException
name|e
parameter_list|)
block|{
name|Util
operator|.
name|swallow
argument_list|(
name|e
argument_list|,
name|TRACER
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|validateNamespace
parameter_list|(
specifier|final
name|SqlValidatorNamespace
name|namespace
parameter_list|,
name|RelDataType
name|targetRowType
parameter_list|)
block|{
comment|// Only attempt to validate each namespace once. Otherwise if
comment|// validation fails, we may end up cycling.
if|if
condition|(
name|activeNamespaces
operator|.
name|add
argument_list|(
name|namespace
argument_list|)
condition|)
block|{
name|super
operator|.
name|validateNamespace
argument_list|(
name|namespace
argument_list|,
name|targetRowType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|namespace
operator|.
name|setType
argument_list|(
name|emptyStructType
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|validateModality
parameter_list|(
name|SqlSelect
name|select
parameter_list|,
name|SqlModality
name|modality
parameter_list|,
name|boolean
name|fail
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|shouldUseCustomStarExpansion
parameter_list|()
block|{
comment|// Disable custom star expansion otherwise SqlValidatorNamespace.getTable()
comment|// could be called on a SqlValidatorNamespace that was not successfully
comment|// validated.
return|return
literal|false
return|;
block|}
specifier|protected
name|boolean
name|shouldAllowOverRelation
parameter_list|()
block|{
return|return
literal|true
return|;
comment|// no reason not to be lenient
block|}
block|}
end_class

begin_comment
comment|// End SqlAdvisorValidator.java
end_comment

end_unit

