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
name|rel
operator|.
name|hint
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
name|RelNode
import|;
end_import

begin_comment
comment|/**  * A hint strategy whose rules are totally customized.  *  * @see ExplicitHintMatcher  */
end_comment

begin_class
specifier|public
class|class
name|ExplicitHintStrategy
implements|implements
name|HintStrategy
block|{
comment|//~ Instance fields --------------------------------------------------------
specifier|private
specifier|final
name|ExplicitHintMatcher
name|matcher
decl_stmt|;
comment|/**    * Creates an {@code ExplicitHintStrategy} with specified {@code matcher}.    *    *<p>Make this constructor package-protected intentionally, use    * {@link HintStrategies#explicit(ExplicitHintMatcher)}.    *    * @param matcher ExplicitHintMatcher instance to test    *                if a hint can be applied to a rel    */
name|ExplicitHintStrategy
parameter_list|(
name|ExplicitHintMatcher
name|matcher
parameter_list|)
block|{
name|this
operator|.
name|matcher
operator|=
name|matcher
expr_stmt|;
block|}
comment|//~ Methods ----------------------------------------------------------------
annotation|@
name|Override
specifier|public
name|boolean
name|supportsRel
parameter_list|(
name|RelHint
name|hint
parameter_list|,
name|RelNode
name|rel
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|this
operator|.
name|matcher
operator|.
name|apply
argument_list|(
name|hint
argument_list|,
name|rel
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End ExplicitHintStrategy.java
end_comment

end_unit

