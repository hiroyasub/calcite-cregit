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
name|fun
package|;
end_package

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
name|type
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
name|validate
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * SqlCursorConstructor defines the non-standard CURSOR(&lt;query&gt;)  * constructor.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SqlCursorConstructor
extends|extends
name|SqlSpecialOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlCursorConstructor
parameter_list|()
block|{
name|super
argument_list|(
literal|"CURSOR"
argument_list|,
name|SqlKind
operator|.
name|CURSOR
argument_list|,
name|MaxPrec
argument_list|,
literal|false
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiCursor
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcAny
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|RelDataType
name|deriveType
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
name|SqlSelect
name|subSelect
init|=
operator|(
name|SqlSelect
operator|)
name|call
operator|.
name|operands
index|[
literal|0
index|]
decl_stmt|;
name|validator
operator|.
name|declareCursor
argument_list|(
name|subSelect
argument_list|,
name|scope
argument_list|)
expr_stmt|;
name|subSelect
operator|.
name|validateExpr
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|)
expr_stmt|;
name|RelDataType
name|type
init|=
name|super
operator|.
name|deriveType
argument_list|(
name|validator
argument_list|,
name|scope
argument_list|,
name|call
argument_list|)
decl_stmt|;
return|return
name|type
return|;
block|}
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlNode
index|[]
name|operands
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
name|writer
operator|.
name|keyword
argument_list|(
literal|"CURSOR"
argument_list|)
expr_stmt|;
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startList
argument_list|(
literal|"("
argument_list|,
literal|")"
argument_list|)
decl_stmt|;
assert|assert
name|operands
operator|.
name|length
operator|==
literal|1
assert|;
name|operands
index|[
literal|0
index|]
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
name|leftPrec
argument_list|,
name|rightPrec
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endList
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|argumentMustBeScalar
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCursorConstructor.java
end_comment

end_unit

