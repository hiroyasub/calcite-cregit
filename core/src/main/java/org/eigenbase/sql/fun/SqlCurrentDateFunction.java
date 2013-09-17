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
comment|/**  * The<code>CURRENT_DATE</code> function.  *  * @author Wael Chatila  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|SqlCurrentDateFunction
extends|extends
name|SqlFunction
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlCurrentDateFunction
parameter_list|()
block|{
name|super
argument_list|(
literal|"CURRENT_DATE"
argument_list|,
name|SqlKind
operator|.
name|OTHER_FUNCTION
argument_list|,
name|SqlTypeStrategies
operator|.
name|rtiDate
argument_list|,
literal|null
argument_list|,
name|SqlTypeStrategies
operator|.
name|otcNiladic
argument_list|,
name|SqlFunctionCategory
operator|.
name|TimeDate
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|SqlSyntax
name|getSyntax
parameter_list|()
block|{
return|return
name|SqlSyntax
operator|.
name|FunctionId
return|;
block|}
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
return|return
name|SqlMonotonicity
operator|.
name|Increasing
return|;
block|}
comment|// Plans referencing context variables should never be cached
specifier|public
name|boolean
name|isDynamicFunction
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlCurrentDateFunction.java
end_comment

end_unit

