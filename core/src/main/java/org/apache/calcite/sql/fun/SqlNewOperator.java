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
name|fun
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
name|SqlKind
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
name|SqlPrefixOperator
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
name|SqlValidator
import|;
end_import

begin_comment
comment|/**  * SqlNewOperator represents an SQL<code>new specification</code> such as  *<code>NEW UDT(1, 2)</code>. When used in an SqlCall, SqlNewOperator takes a  * single operand, which is an invocation of the constructor method; but when  * used in a RexCall, the operands are the initial values to be used for the new  * instance.  */
end_comment

begin_class
specifier|public
class|class
name|SqlNewOperator
extends|extends
name|SqlPrefixOperator
block|{
comment|//~ Constructors -----------------------------------------------------------
specifier|public
name|SqlNewOperator
parameter_list|()
block|{
name|super
argument_list|(
literal|"NEW"
argument_list|,
name|SqlKind
operator|.
name|NEW_SPECIFICATION
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
comment|// override SqlOperator
specifier|public
name|SqlNode
name|rewriteCall
parameter_list|(
name|SqlValidator
name|validator
parameter_list|,
name|SqlCall
name|call
parameter_list|)
block|{
comment|// New specification is purely syntactic, so we rewrite it as a
comment|// direct call to the constructor method.
return|return
name|call
operator|.
name|operand
argument_list|(
literal|0
argument_list|)
return|;
block|}
comment|// override SqlOperator
specifier|public
name|boolean
name|requiresDecimalExpansion
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

begin_comment
comment|// End SqlNewOperator.java
end_comment

end_unit

