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
name|test
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
name|test
operator|.
name|SqlOperatorFixture
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
name|DelegatingInvocationHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Proxy
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_comment
comment|/** Utilities for {@link SqlOperatorFixture}. */
end_comment

begin_class
class|class
name|SqlOperatorFixtures
block|{
specifier|private
name|SqlOperatorFixtures
parameter_list|()
block|{
block|}
comment|/** Returns a fixture that converts each CAST test into a test for    * SAFE_CAST. */
specifier|static
name|SqlOperatorFixture
name|safeCastWrapper
parameter_list|(
name|SqlOperatorFixture
name|fixture
parameter_list|)
block|{
return|return
operator|(
name|SqlOperatorFixture
operator|)
name|Proxy
operator|.
name|newProxyInstance
argument_list|(
name|SqlOperatorTest
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|,
operator|new
name|Class
index|[]
block|{
name|SqlOperatorFixture
operator|.
name|class
block|}
argument_list|,
operator|new
name|SqlOperatorFixtureInvocationHandler
argument_list|(
name|fixture
argument_list|)
argument_list|)
return|;
block|}
comment|/** A helper for {@link #safeCastWrapper(SqlOperatorFixture)} that provides    * alternative implementations of methods in {@link SqlOperatorFixture}.    *    *<p>Must be public, so that its methods can be seen via reflection. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|public
specifier|static
class|class
name|SqlOperatorFixtureInvocationHandler
extends|extends
name|DelegatingInvocationHandler
block|{
specifier|static
specifier|final
name|Pattern
name|CAST_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(?i)\\bCAST\\("
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|Pattern
name|NOT_NULL_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|" NOT NULL"
argument_list|)
decl_stmt|;
specifier|final
name|SqlOperatorFixture
name|f
decl_stmt|;
name|SqlOperatorFixtureInvocationHandler
parameter_list|(
name|SqlOperatorFixture
name|f
parameter_list|)
block|{
name|this
operator|.
name|f
operator|=
name|f
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Object
name|getTarget
parameter_list|()
block|{
return|return
name|f
return|;
block|}
name|String
name|addSafe
parameter_list|(
name|String
name|sql
parameter_list|)
block|{
return|return
name|CAST_PATTERN
operator|.
name|matcher
argument_list|(
name|sql
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"SAFE_CAST("
argument_list|)
return|;
block|}
name|String
name|removeNotNull
parameter_list|(
name|String
name|type
parameter_list|)
block|{
return|return
name|NOT_NULL_PATTERN
operator|.
name|matcher
argument_list|(
name|type
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|""
argument_list|)
return|;
block|}
comment|/** Proxy for      * {@link SqlOperatorFixture#checkCastToString(String, String, String, boolean)}. */
specifier|public
name|void
name|checkCastToString
parameter_list|(
name|String
name|value
parameter_list|,
annotation|@
name|Nullable
name|String
name|type
parameter_list|,
annotation|@
name|Nullable
name|String
name|expected
parameter_list|,
name|boolean
name|safe
parameter_list|)
block|{
name|f
operator|.
name|checkCastToString
argument_list|(
name|addSafe
argument_list|(
name|value
argument_list|)
argument_list|,
name|type
operator|==
literal|null
condition|?
literal|null
else|:
name|removeNotNull
argument_list|(
name|type
argument_list|)
argument_list|,
name|expected
argument_list|,
name|safe
argument_list|)
expr_stmt|;
block|}
comment|/** Proxy for {@link SqlOperatorFixture#checkBoolean(String, Boolean)}. */
specifier|public
name|void
name|checkFails
parameter_list|(
name|String
name|expression
parameter_list|,
annotation|@
name|Nullable
name|Boolean
name|result
parameter_list|)
block|{
name|f
operator|.
name|checkBoolean
argument_list|(
name|addSafe
argument_list|(
name|expression
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/** Proxy for {@link SqlOperatorFixture#checkNull(String)}. */
specifier|public
name|void
name|checkNull
parameter_list|(
name|String
name|expression
parameter_list|)
block|{
name|f
operator|.
name|checkNull
argument_list|(
name|addSafe
argument_list|(
name|expression
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Proxy for      * {@link SqlOperatorFixture#checkFails(String, String, boolean)}. */
specifier|public
name|void
name|checkFails
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedError
parameter_list|,
name|boolean
name|runtime
parameter_list|)
block|{
name|f
operator|.
name|checkFails
argument_list|(
name|addSafe
argument_list|(
name|expression
argument_list|)
argument_list|,
name|expectedError
argument_list|,
name|runtime
argument_list|)
expr_stmt|;
block|}
comment|/** Proxy for      * {@link SqlOperatorFixture#checkScalar(String, Object, String)}. */
specifier|public
name|void
name|checkScalar
parameter_list|(
name|String
name|expression
parameter_list|,
name|Object
name|result
parameter_list|,
name|String
name|resultType
parameter_list|)
block|{
name|f
operator|.
name|checkScalar
argument_list|(
name|addSafe
argument_list|(
name|expression
argument_list|)
argument_list|,
name|result
argument_list|,
name|removeNotNull
argument_list|(
name|resultType
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Proxy for {@link SqlOperatorFixture#checkScalarExact(String, int)}. */
specifier|public
name|void
name|checkScalarExact
parameter_list|(
name|String
name|expression
parameter_list|,
name|int
name|result
parameter_list|)
block|{
name|f
operator|.
name|checkScalarExact
argument_list|(
name|addSafe
argument_list|(
name|expression
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/** Proxy for      * {@link SqlOperatorFixture#checkScalarExact(String, String, String)}. */
specifier|public
name|void
name|checkScalarExact
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|expectedType
parameter_list|,
name|String
name|result
parameter_list|)
block|{
name|f
operator|.
name|checkScalarExact
argument_list|(
name|addSafe
argument_list|(
name|expression
argument_list|)
argument_list|,
name|removeNotNull
argument_list|(
name|expectedType
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
comment|/** Proxy for      * {@link SqlOperatorFixture#checkString(String, String, String)}. */
specifier|public
name|void
name|checkString
parameter_list|(
name|String
name|expression
parameter_list|,
name|String
name|result
parameter_list|,
name|String
name|resultType
parameter_list|)
block|{
name|f
operator|.
name|checkString
argument_list|(
name|addSafe
argument_list|(
name|expression
argument_list|)
argument_list|,
name|result
argument_list|,
name|removeNotNull
argument_list|(
name|resultType
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

