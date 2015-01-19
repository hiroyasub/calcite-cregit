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
name|interpreter
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
name|SingleRel
import|;
end_import

begin_comment
comment|/**  * An interpreter that takes expects one incoming source relational expression.  *  * @param<T> Type of relational expression  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractSingleNode
parameter_list|<
name|T
extends|extends
name|SingleRel
parameter_list|>
implements|implements
name|Node
block|{
specifier|protected
specifier|final
name|Source
name|source
decl_stmt|;
specifier|protected
specifier|final
name|Sink
name|sink
decl_stmt|;
specifier|protected
specifier|final
name|T
name|rel
decl_stmt|;
specifier|public
name|AbstractSingleNode
parameter_list|(
name|Interpreter
name|interpreter
parameter_list|,
name|T
name|rel
parameter_list|)
block|{
name|this
operator|.
name|rel
operator|=
name|rel
expr_stmt|;
name|this
operator|.
name|source
operator|=
name|interpreter
operator|.
name|source
argument_list|(
name|rel
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|sink
operator|=
name|interpreter
operator|.
name|sink
argument_list|(
name|rel
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractSingleNode.java
end_comment

end_unit

