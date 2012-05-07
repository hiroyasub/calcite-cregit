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
name|oj
operator|.
name|rex
package|;
end_package

begin_import
import|import
name|openjava
operator|.
name|mop
operator|.
name|*
import|;
end_import

begin_import
import|import
name|openjava
operator|.
name|ptree
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
name|oj
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
name|rex
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * OJRexCastImplementor implements {@link OJRexImplementor} for the CAST  * operator.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|OJRexCastImplementor
implements|implements
name|OJRexImplementor
block|{
comment|//~ Methods ----------------------------------------------------------------
specifier|public
name|Expression
name|implement
parameter_list|(
name|RexToOJTranslator
name|translator
parameter_list|,
name|RexCall
name|call
parameter_list|,
name|Expression
index|[]
name|operands
parameter_list|)
block|{
name|OJTypeFactory
name|typeFactory
init|=
operator|(
name|OJTypeFactory
operator|)
name|translator
operator|.
name|getTypeFactory
argument_list|()
decl_stmt|;
name|OJClass
name|type
init|=
name|typeFactory
operator|.
name|toOJClass
argument_list|(
literal|null
argument_list|,
name|call
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|CastExpression
argument_list|(
name|type
argument_list|,
name|operands
index|[
literal|0
index|]
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|canImplement
parameter_list|(
name|RexCall
name|call
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

begin_comment
comment|// End OJRexCastImplementor.java
end_comment

end_unit

