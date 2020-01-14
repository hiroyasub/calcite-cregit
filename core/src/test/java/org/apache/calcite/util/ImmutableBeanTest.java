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
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|hamcrest
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|MatcherAssert
operator|.
name|assertThat
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|IsNull
operator|.
name|nullValue
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Assertions
operator|.
name|fail
import|;
end_import

begin_comment
comment|/** Unit test for {@link ImmutableBeans}. */
end_comment

begin_class
specifier|public
class|class
name|ImmutableBeanTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSimple
parameter_list|()
block|{
specifier|final
name|MyBean
name|b
init|=
name|ImmutableBeans
operator|.
name|create
argument_list|(
name|MyBean
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withFoo
argument_list|(
literal|1
argument_list|)
operator|.
name|getFoo
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withBar
argument_list|(
literal|false
argument_list|)
operator|.
name|isBar
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withBaz
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getBaz
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withBaz
argument_list|(
literal|"a"
argument_list|)
operator|.
name|withBaz
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getBaz
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Calling "with" on b2 does not change the "foo" property
specifier|final
name|MyBean
name|b2
init|=
name|b
operator|.
name|withFoo
argument_list|(
literal|2
argument_list|)
decl_stmt|;
specifier|final
name|MyBean
name|b3
init|=
name|b2
operator|.
name|withFoo
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|b3
operator|.
name|getFoo
argument_list|()
argument_list|,
name|is
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b2
operator|.
name|getFoo
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|MyBean
name|b4
init|=
name|b2
operator|.
name|withFoo
argument_list|(
literal|3
argument_list|)
operator|.
name|withBar
argument_list|(
literal|true
argument_list|)
operator|.
name|withBaz
argument_list|(
literal|"xyz"
argument_list|)
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|map
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"Foo"
argument_list|,
name|b4
operator|.
name|getFoo
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"Bar"
argument_list|,
name|b4
operator|.
name|isBar
argument_list|()
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
literal|"Baz"
argument_list|,
name|b4
operator|.
name|getBaz
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b4
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
name|map
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b4
operator|.
name|hashCode
argument_list|()
argument_list|,
name|is
argument_list|(
name|map
operator|.
name|hashCode
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|MyBean
name|b5
init|=
name|b2
operator|.
name|withFoo
argument_list|(
literal|3
argument_list|)
operator|.
name|withBar
argument_list|(
literal|true
argument_list|)
operator|.
name|withBaz
argument_list|(
literal|"xyz"
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|b4
operator|.
name|equals
argument_list|(
name|b5
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b4
operator|.
name|equals
argument_list|(
name|b
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b4
operator|.
name|equals
argument_list|(
name|b2
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b4
operator|.
name|equals
argument_list|(
name|b3
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefault
parameter_list|()
block|{
specifier|final
name|Bean2
name|b
init|=
name|ImmutableBeans
operator|.
name|create
argument_list|(
name|Bean2
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// int, no default
try|try
block|{
specifier|final
name|int
name|v
init|=
name|b
operator|.
name|getIntSansDefault
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected error, got "
operator|+
name|v
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"property 'IntSansDefault' is required and has no default value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|b
operator|.
name|withIntSansDefault
argument_list|(
literal|4
argument_list|)
operator|.
name|getIntSansDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|4
argument_list|)
argument_list|)
expr_stmt|;
comment|// int, with default
name|assertThat
argument_list|(
name|b
operator|.
name|getIntWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withIntWithDefault
argument_list|(
literal|10
argument_list|)
operator|.
name|getIntWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withIntWithDefault
argument_list|(
literal|1
argument_list|)
operator|.
name|getIntWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
comment|// boolean, no default
try|try
block|{
specifier|final
name|boolean
name|v
init|=
name|b
operator|.
name|isBooleanSansDefault
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected error, got "
operator|+
name|v
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"property 'BooleanSansDefault' is required and has no default "
operator|+
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|b
operator|.
name|withBooleanSansDefault
argument_list|(
literal|false
argument_list|)
operator|.
name|isBooleanSansDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
comment|// boolean, with default
name|assertThat
argument_list|(
name|b
operator|.
name|isBooleanWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withBooleanWithDefault
argument_list|(
literal|false
argument_list|)
operator|.
name|isBooleanWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withBooleanWithDefault
argument_list|(
literal|true
argument_list|)
operator|.
name|isBooleanWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
comment|// string, no default
try|try
block|{
specifier|final
name|String
name|v
init|=
name|b
operator|.
name|getStringSansDefault
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected error, got "
operator|+
name|v
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"property 'StringSansDefault' is required and has no default "
operator|+
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|b
operator|.
name|withStringSansDefault
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getStringSansDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
comment|// string, no default
try|try
block|{
specifier|final
name|String
name|v
init|=
name|b
operator|.
name|getNonnullString
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected error, got "
operator|+
name|v
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"property 'NonnullString' is required and has no default value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|b
operator|.
name|withNonnullString
argument_list|(
literal|"a"
argument_list|)
operator|.
name|getNonnullString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
comment|// string, with default
name|assertThat
argument_list|(
name|b
operator|.
name|getStringWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withStringWithDefault
argument_list|(
literal|""
argument_list|)
operator|.
name|getStringWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withStringWithDefault
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getStringWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withStringWithDefault
argument_list|(
literal|"abc"
argument_list|)
operator|.
name|getStringWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Bean2
name|v
init|=
name|b
operator|.
name|withStringWithDefault
argument_list|(
literal|null
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected error, got "
operator|+
name|v
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"cannot set required property 'StringWithDefault' to null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// string, optional
name|assertThat
argument_list|(
name|b
operator|.
name|getOptionalString
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withOptionalString
argument_list|(
literal|""
argument_list|)
operator|.
name|getOptionalString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withOptionalString
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getOptionalString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withOptionalString
argument_list|(
literal|"abc"
argument_list|)
operator|.
name|getOptionalString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withOptionalString
argument_list|(
literal|null
argument_list|)
operator|.
name|getOptionalString
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// string, optional
name|assertThat
argument_list|(
name|b
operator|.
name|getStringWithNullDefault
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withStringWithNullDefault
argument_list|(
literal|""
argument_list|)
operator|.
name|getStringWithNullDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withStringWithNullDefault
argument_list|(
literal|"x"
argument_list|)
operator|.
name|getStringWithNullDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withStringWithNullDefault
argument_list|(
literal|"abc"
argument_list|)
operator|.
name|getStringWithNullDefault
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"abc"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withStringWithNullDefault
argument_list|(
literal|null
argument_list|)
operator|.
name|getStringWithNullDefault
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// enum, with default
name|assertThat
argument_list|(
name|b
operator|.
name|getColorWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorWithDefault
argument_list|(
name|Color
operator|.
name|GREEN
argument_list|)
operator|.
name|getColorWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
name|Color
operator|.
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorWithDefault
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
operator|.
name|getColorWithDefault
argument_list|()
argument_list|,
name|is
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|Bean2
name|v
init|=
name|b
operator|.
name|withColorWithDefault
argument_list|(
literal|null
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"expected error, got "
operator|+
name|v
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"cannot set required property 'ColorWithDefault' to null"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// color, optional
name|assertThat
argument_list|(
name|b
operator|.
name|getColorOptional
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorOptional
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
operator|.
name|getColorOptional
argument_list|()
argument_list|,
name|is
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorOptional
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
operator|.
name|withColorOptional
argument_list|(
literal|null
argument_list|)
operator|.
name|getColorOptional
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorOptional
argument_list|(
literal|null
argument_list|)
operator|.
name|getColorOptional
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorOptional
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
operator|.
name|withColorOptional
argument_list|(
name|Color
operator|.
name|GREEN
argument_list|)
operator|.
name|getColorOptional
argument_list|()
argument_list|,
name|is
argument_list|(
name|Color
operator|.
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
comment|// color, optional with null default
name|assertThat
argument_list|(
name|b
operator|.
name|getColorWithNullDefault
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorWithNullDefault
argument_list|(
literal|null
argument_list|)
operator|.
name|getColorWithNullDefault
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorWithNullDefault
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
operator|.
name|getColorWithNullDefault
argument_list|()
argument_list|,
name|is
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorWithNullDefault
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
operator|.
name|withColorWithNullDefault
argument_list|(
literal|null
argument_list|)
operator|.
name|getColorWithNullDefault
argument_list|()
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withColorWithNullDefault
argument_list|(
name|Color
operator|.
name|RED
argument_list|)
operator|.
name|withColorWithNullDefault
argument_list|(
name|Color
operator|.
name|GREEN
argument_list|)
operator|.
name|getColorWithNullDefault
argument_list|()
argument_list|,
name|is
argument_list|(
name|Color
operator|.
name|GREEN
argument_list|)
argument_list|)
expr_stmt|;
comment|// Default values do not appear in toString().
comment|// (Maybe they should... but then they'd be initial values?)
name|assertThat
argument_list|(
name|b
operator|.
name|toString
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"{}"
argument_list|)
argument_list|)
expr_stmt|;
comment|// Beans with values explicitly set are not equal to
comment|// beans with the same values via defaults.
comment|// (I could be persuaded that this is the wrong behavior.)
name|assertThat
argument_list|(
name|b
operator|.
name|equals
argument_list|(
name|b
operator|.
name|withIntWithDefault
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withIntWithDefault
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|b
operator|.
name|withIntWithDefault
argument_list|(
literal|1
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|b
operator|.
name|withIntWithDefault
argument_list|(
literal|1
argument_list|)
operator|.
name|equals
argument_list|(
name|b
operator|.
name|withIntWithDefault
argument_list|(
literal|2
argument_list|)
argument_list|)
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|check
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|beanClass
parameter_list|,
name|Matcher
argument_list|<
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Object
name|v
init|=
name|ImmutableBeans
operator|.
name|create
argument_list|(
name|beanClass
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected error, got "
operator|+
name|v
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidate
parameter_list|()
block|{
name|check
argument_list|(
name|BeanWhoseDefaultIsBadEnumValue
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"property 'Color' is an enum but its default value YELLOW is not a "
operator|+
literal|"valid enum constant"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseWithMethodHasBadReturnType
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'withFoo' should return the bean class 'interface "
operator|+
literal|"org.apache.calcite.util.ImmutableBeanTest$"
operator|+
literal|"BeanWhoseWithMethodHasBadReturnType', actually returns "
operator|+
literal|"'interface org.apache.calcite.util.ImmutableBeanTest$MyBean'"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseWithMethodDoesNotMatchProperty
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'withFoo' should return the bean class 'interface "
operator|+
literal|"org.apache.calcite.util.ImmutableBeanTest$"
operator|+
literal|"BeanWhoseWithMethodDoesNotMatchProperty', actually returns "
operator|+
literal|"'interface org.apache.calcite.util.ImmutableBeanTest$MyBean'"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseWithMethodHasArgOfWrongType
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'withFoo' should return the bean class 'interface "
operator|+
literal|"org.apache.calcite.util.ImmutableBeanTest$"
operator|+
literal|"BeanWhoseWithMethodHasArgOfWrongType', actually returns "
operator|+
literal|"'interface org.apache.calcite.util.ImmutableBeanTest$"
operator|+
literal|"BeanWhoseWithMethodHasTooManyArgs'"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseWithMethodHasTooManyArgs
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'withFoo' should have one parameter, actually has 2"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseWithMethodHasTooFewArgs
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'withFoo' should have one parameter, actually has 0"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseSetMethodHasBadReturnType
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'setFoo' should return void, actually returns "
operator|+
literal|"'interface org.apache.calcite.util.ImmutableBeanTest$MyBean'"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseGetMethodHasTooManyArgs
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'getFoo' has too many parameters"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseSetMethodDoesNotMatchProperty
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"cannot find property 'Foo' for method 'setFoo'; maybe add a method "
operator|+
literal|"'getFoo'?'"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseSetMethodHasArgOfWrongType
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'setFoo' should have parameter of type int, actually has "
operator|+
literal|"float"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseSetMethodHasTooManyArgs
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'setFoo' should have one parameter, actually has 2"
argument_list|)
argument_list|)
expr_stmt|;
name|check
argument_list|(
name|BeanWhoseSetMethodHasTooFewArgs
operator|.
name|class
argument_list|,
name|is
argument_list|(
literal|"method 'setFoo' should have one parameter, actually has 0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultMethod
parameter_list|()
block|{
name|assertThat
argument_list|(
name|ImmutableBeans
operator|.
name|create
argument_list|(
name|BeanWithDefault
operator|.
name|class
argument_list|)
operator|.
name|withChar
argument_list|(
literal|'a'
argument_list|)
operator|.
name|nTimes
argument_list|(
literal|2
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"aa"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Bean whose default value is not a valid value for the enum;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseDefaultIsBadEnumValue
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|EnumDefault
argument_list|(
literal|"YELLOW"
argument_list|)
name|Color
name|getColor
parameter_list|()
function_decl|;
name|BeanWhoseDefaultIsBadEnumValue
name|withColor
parameter_list|(
name|Color
name|color
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'with' method that has a bad return type;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseWithMethodHasBadReturnType
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|MyBean
name|withFoo
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'with' method that does not correspond to a property    * (declared using a {@link ImmutableBeans.Property} annotation on a    * 'get' method;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseWithMethodDoesNotMatchProperty
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|MyBean
name|withFoo
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'with' method whose argument type is not the same as the    * type of the property (the return type of a 'get{PropertyName}' method);    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseWithMethodHasArgOfWrongType
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|BeanWhoseWithMethodHasTooManyArgs
name|withFoo
parameter_list|(
name|float
name|x
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'with' method that has too many arguments;    * it should have just one;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseWithMethodHasTooManyArgs
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|BeanWhoseWithMethodHasTooManyArgs
name|withFoo
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'with' method that has too few arguments;    * it should have just one;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseWithMethodHasTooFewArgs
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|BeanWhoseWithMethodHasTooFewArgs
name|withFoo
parameter_list|()
function_decl|;
block|}
comment|/** Bean that has a 'set' method that has a bad return type;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseSetMethodHasBadReturnType
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|MyBean
name|setFoo
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'get' method that has one arg, whereas 'get' must have no    * args;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseGetMethodHasTooManyArgs
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
name|void
name|setFoo
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'set' method that does not correspond to a property    * (declared using a {@link ImmutableBeans.Property} annotation on a    * 'get' method;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseSetMethodDoesNotMatchProperty
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getBar
parameter_list|()
function_decl|;
name|void
name|setFoo
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'set' method whose argument type is not the same as the    * type of the property (the return type of a 'get{PropertyName}' method);    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseSetMethodHasArgOfWrongType
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|void
name|setFoo
parameter_list|(
name|float
name|x
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'set' method that has too many arguments;    * it should have just one;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseSetMethodHasTooManyArgs
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|void
name|setFoo
parameter_list|(
name|int
name|x
parameter_list|,
name|int
name|y
parameter_list|)
function_decl|;
block|}
comment|/** Bean that has a 'set' method that has too few arguments;    * it should have just one;    * used in {@link #testValidate()}. */
interface|interface
name|BeanWhoseSetMethodHasTooFewArgs
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|void
name|setFoo
parameter_list|()
function_decl|;
block|}
comment|// ditto setXxx
comment|// TODO it is an error to declare an int property to be not required
comment|// TODO it is an error to declare an boolean property to be not required
comment|/** A simple bean with properties of various types, no defaults. */
interface|interface
name|MyBean
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getFoo
parameter_list|()
function_decl|;
name|MyBean
name|withFoo
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|boolean
name|isBar
parameter_list|()
function_decl|;
name|MyBean
name|withBar
parameter_list|(
name|boolean
name|x
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|String
name|getBaz
parameter_list|()
function_decl|;
name|MyBean
name|withBaz
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
block|}
comment|/** A bean class with just about every combination of default values    * missing and present, and required or not. */
interface|interface
name|Bean2
block|{
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|IntDefault
argument_list|(
literal|1
argument_list|)
name|int
name|getIntWithDefault
parameter_list|()
function_decl|;
name|Bean2
name|withIntWithDefault
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|int
name|getIntSansDefault
parameter_list|()
function_decl|;
name|Bean2
name|withIntSansDefault
parameter_list|(
name|int
name|x
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|BooleanDefault
argument_list|(
literal|true
argument_list|)
name|boolean
name|isBooleanWithDefault
parameter_list|()
function_decl|;
name|Bean2
name|withBooleanWithDefault
parameter_list|(
name|boolean
name|x
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|boolean
name|isBooleanSansDefault
parameter_list|()
function_decl|;
name|Bean2
name|withBooleanSansDefault
parameter_list|(
name|boolean
name|x
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|(
name|required
operator|=
literal|true
argument_list|)
name|String
name|getStringSansDefault
parameter_list|()
function_decl|;
name|Bean2
name|withStringSansDefault
parameter_list|(
name|String
name|x
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|String
name|getOptionalString
parameter_list|()
function_decl|;
name|Bean2
name|withOptionalString
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
comment|/** Property is required because it has 'Nonnull' annotation. */
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|Nonnull
name|String
name|getNonnullString
parameter_list|()
function_decl|;
name|Bean2
name|withNonnullString
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|StringDefault
argument_list|(
literal|"abc"
argument_list|)
annotation|@
name|Nonnull
name|String
name|getStringWithDefault
parameter_list|()
function_decl|;
name|Bean2
name|withStringWithDefault
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|NullDefault
name|String
name|getStringWithNullDefault
parameter_list|()
function_decl|;
name|Bean2
name|withStringWithNullDefault
parameter_list|(
name|String
name|s
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|EnumDefault
argument_list|(
literal|"RED"
argument_list|)
annotation|@
name|Nonnull
name|Color
name|getColorWithDefault
parameter_list|()
function_decl|;
name|Bean2
name|withColorWithDefault
parameter_list|(
name|Color
name|color
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
annotation|@
name|ImmutableBeans
operator|.
name|NullDefault
name|Color
name|getColorWithNullDefault
parameter_list|()
function_decl|;
name|Bean2
name|withColorWithNullDefault
parameter_list|(
name|Color
name|color
parameter_list|)
function_decl|;
annotation|@
name|ImmutableBeans
operator|.
name|Property
argument_list|()
name|Color
name|getColorOptional
parameter_list|()
function_decl|;
name|Bean2
name|withColorOptional
parameter_list|(
name|Color
name|color
parameter_list|)
function_decl|;
block|}
comment|/** Red, blue, green. */
enum|enum
name|Color
block|{
name|RED
block|,
name|BLUE
block|,
name|GREEN
block|}
comment|/** Bean interface that has a default method and one property. */
interface|interface
name|BeanWithDefault
block|{
specifier|default
name|String
name|nTimes
parameter_list|(
name|int
name|x
parameter_list|)
block|{
if|if
condition|(
name|x
operator|<=
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
specifier|final
name|char
name|c
init|=
name|getChar
argument_list|()
decl_stmt|;
return|return
name|c
operator|+
name|nTimes
argument_list|(
name|x
operator|-
literal|1
argument_list|)
return|;
block|}
annotation|@
name|ImmutableBeans
operator|.
name|Property
name|char
name|getChar
parameter_list|()
function_decl|;
name|BeanWithDefault
name|withChar
parameter_list|(
name|char
name|c
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

