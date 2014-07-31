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

begin_comment
comment|/**  * An internal operator that throws an exception.  *  *<p>The exception is thrown with a (localized) error message which is the only  * input parameter to the operator.</p>  *  *<p>The return type is defined as a<code>BOOLEAN</code> to facilitate the use  * of it in constructs such as the following:</p>  *  *<blockquote><code>CASE<br>  * WHEN&lt;conditionn&gt; THEN true<br>  * ELSE throw("what's wrong with you man?")<br>  * END</code></blockquote>  */
end_comment

begin_class
specifier|public
class|class
name|SqlThrowOperator
extends|extends
name|SqlInternalOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlThrowOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"$throw"
argument_list|,
name|SqlKind
operator|.
name|OTHER
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
name|ReturnTypes
operator|.
name|BOOLEAN
argument_list|,
literal|null
argument_list|,
name|OperandTypes
operator|.
name|CHARACTER
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|void
name|unparse
parameter_list|(
name|SqlWriter
name|writer
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|int
name|leftPrec
parameter_list|,
name|int
name|rightPrec
parameter_list|)
block|{
specifier|final
name|SqlWriter
operator|.
name|Frame
name|frame
init|=
name|writer
operator|.
name|startFunCall
argument_list|(
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
operator|.
name|unparse
argument_list|(
name|writer
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|writer
operator|.
name|endFunCall
argument_list|(
name|frame
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SqlThrowOperator.java
end_comment

end_unit

