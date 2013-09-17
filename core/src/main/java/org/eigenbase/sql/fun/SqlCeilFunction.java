begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
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
comment|/**  * Support for the CEIL/CEILING builtin function.  *  * @author jack  * @version $Id$  * @since Apr 13, 2005  */
end_comment

begin_class
specifier|public
class|class
name|SqlCeilFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlCeilFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"CEIL"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiFirstArgType
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcNumeric
argument_list|,
name|SqlFunctionCategory
operator|.
name|Numeric
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlMonotonicity
name|getMonotonicity
parameter_list|(
name|SqlCall
name|call
parameter_list|,
name|SqlValidatorScope
name|scope
parameter_list|)
block|{
name|SqlNode
name|node
init|=
operator|(
name|SqlNode
operator|)
name|call
operator|.
name|operands
index|[
literal|0
index|]
decl_stmt|;
return|return
name|scope
operator|.
name|getMonotonicity
argument_list|(
name|node
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCeilFunction.java
end_comment

end_unit

