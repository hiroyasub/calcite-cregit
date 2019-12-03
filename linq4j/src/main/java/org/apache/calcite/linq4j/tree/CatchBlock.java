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
name|linq4j
operator|.
name|tree
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Represents a catch statement in a try block.  */
end_comment

begin_class
specifier|public
class|class
name|CatchBlock
block|{
specifier|public
specifier|final
name|ParameterExpression
name|parameter
decl_stmt|;
specifier|public
specifier|final
name|Statement
name|body
decl_stmt|;
specifier|public
name|CatchBlock
parameter_list|(
name|ParameterExpression
name|parameter
parameter_list|,
name|Statement
name|body
parameter_list|)
block|{
name|this
operator|.
name|parameter
operator|=
name|parameter
expr_stmt|;
name|this
operator|.
name|body
operator|=
name|body
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|CatchBlock
name|that
init|=
operator|(
name|CatchBlock
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|?
operator|!
name|body
operator|.
name|equals
argument_list|(
name|that
operator|.
name|body
argument_list|)
else|:
name|that
operator|.
name|body
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|parameter
operator|!=
literal|null
condition|?
operator|!
name|parameter
operator|.
name|equals
argument_list|(
name|that
operator|.
name|parameter
argument_list|)
else|:
name|that
operator|.
name|parameter
operator|!=
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|parameter
argument_list|,
name|body
argument_list|)
return|;
block|}
block|}
end_class

end_unit

