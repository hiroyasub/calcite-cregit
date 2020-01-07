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
name|runtime
operator|.
name|CalciteException
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
name|runtime
operator|.
name|SqlFunctions
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
name|runtime
operator|.
name|XmlFunctions
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
name|BuiltInMethod
import|;
end_import

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
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
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
name|CoreMatchers
operator|.
name|nullValue
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
comment|/**  * Unit test for the methods in {@link SqlFunctions} that implement Xml processing functions.  */
end_comment

begin_class
specifier|public
class|class
name|SqlXmlFunctionsTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testExtractValue
parameter_list|()
block|{
name|assertExtractValue
argument_list|(
literal|"<a>ccc<b>ddd</b></a>"
argument_list|,
literal|"/a"
argument_list|,
name|is
argument_list|(
literal|"ccc"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|input
init|=
literal|"<a>ccc<b>ddd</b></a>"
decl_stmt|;
name|String
name|message
init|=
literal|"Invalid input for EXTRACTVALUE: xml: '"
operator|+
name|input
operator|+
literal|"', xpath expression: '#'"
decl_stmt|;
name|CalciteException
name|expected
init|=
operator|new
name|CalciteException
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertExtractValueFailed
argument_list|(
name|input
argument_list|,
literal|"#"
argument_list|,
name|Matchers
operator|.
name|expectThrowable
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testXmlTransform
parameter_list|()
block|{
name|assertXmlTransform
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertXmlTransform
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xslt
init|=
literal|"<"
decl_stmt|;
name|String
name|message
init|=
literal|"Illegal xslt specified : '"
operator|+
name|xslt
operator|+
literal|"'"
decl_stmt|;
name|CalciteException
name|expected
init|=
operator|new
name|CalciteException
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertXmlTransformFailed
argument_list|(
literal|""
argument_list|,
name|xslt
argument_list|,
name|Matchers
operator|.
name|expectThrowable
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExtractXml
parameter_list|()
block|{
name|assertExtractXml
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertExtractXml
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xpath
init|=
literal|"<"
decl_stmt|;
name|String
name|namespace
init|=
literal|"a"
decl_stmt|;
name|String
name|message
init|=
literal|"Invalid input for EXTRACT xpath: '"
operator|+
name|xpath
operator|+
literal|"', namespace: '"
operator|+
name|namespace
operator|+
literal|"'"
decl_stmt|;
name|CalciteException
name|expected
init|=
operator|new
name|CalciteException
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertExtractXmlFailed
argument_list|(
literal|""
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|,
name|Matchers
operator|.
name|expectThrowable
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExistsNode
parameter_list|()
block|{
name|assertExistsNode
argument_list|(
literal|null
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertExistsNode
argument_list|(
literal|""
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|xpath
init|=
literal|"<"
decl_stmt|;
name|String
name|namespace
init|=
literal|"a"
decl_stmt|;
name|String
name|message
init|=
literal|"Invalid input for EXISTSNODE xpath: '"
operator|+
name|xpath
operator|+
literal|"', namespace: '"
operator|+
name|namespace
operator|+
literal|"'"
decl_stmt|;
name|CalciteException
name|expected
init|=
operator|new
name|CalciteException
argument_list|(
name|message
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertExistsNodeFailed
argument_list|(
literal|""
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|,
name|Matchers
operator|.
name|expectThrowable
argument_list|(
name|expected
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExistsNode
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|xpath
parameter_list|,
name|String
name|namespace
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|Integer
argument_list|>
name|matcher
parameter_list|)
block|{
name|String
name|methodDesc
init|=
name|BuiltInMethod
operator|.
name|EXISTS_NODE
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|xml
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertThat
argument_list|(
name|methodDesc
argument_list|,
name|XmlFunctions
operator|.
name|existsNode
argument_list|(
name|xml
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExistsNodeFailed
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|xpath
parameter_list|,
name|String
name|namespace
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|Throwable
argument_list|>
name|matcher
parameter_list|)
block|{
name|String
name|methodDesc
init|=
name|BuiltInMethod
operator|.
name|EXISTS_NODE
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|xml
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertFailed
argument_list|(
name|methodDesc
argument_list|,
parameter_list|()
lambda|->
name|XmlFunctions
operator|.
name|existsNode
argument_list|(
name|xml
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExtractXml
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|xpath
parameter_list|,
name|String
name|namespace
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
name|String
name|methodDesc
init|=
name|BuiltInMethod
operator|.
name|EXTRACT_XML
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|xml
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertThat
argument_list|(
name|methodDesc
argument_list|,
name|XmlFunctions
operator|.
name|extractXml
argument_list|(
name|xml
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExtractXmlFailed
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|xpath
parameter_list|,
name|String
name|namespace
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|Throwable
argument_list|>
name|matcher
parameter_list|)
block|{
name|String
name|methodDesc
init|=
name|BuiltInMethod
operator|.
name|EXTRACT_XML
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|xml
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertFailed
argument_list|(
name|methodDesc
argument_list|,
parameter_list|()
lambda|->
name|XmlFunctions
operator|.
name|extractXml
argument_list|(
name|xml
argument_list|,
name|xpath
argument_list|,
name|namespace
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertXmlTransform
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|xslt
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
name|String
name|methodDesc
init|=
name|BuiltInMethod
operator|.
name|XML_TRANSFORM
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|xml
argument_list|,
name|xslt
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertThat
argument_list|(
name|methodDesc
argument_list|,
name|XmlFunctions
operator|.
name|xmlTransform
argument_list|(
name|xml
argument_list|,
name|xslt
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertXmlTransformFailed
parameter_list|(
name|String
name|xml
parameter_list|,
name|String
name|xslt
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|Throwable
argument_list|>
name|matcher
parameter_list|)
block|{
name|String
name|methodDesc
init|=
name|BuiltInMethod
operator|.
name|XML_TRANSFORM
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|xml
argument_list|,
name|xslt
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertFailed
argument_list|(
name|methodDesc
argument_list|,
parameter_list|()
lambda|->
name|XmlFunctions
operator|.
name|xmlTransform
argument_list|(
name|xml
argument_list|,
name|xslt
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExtractValue
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|xpath
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|String
argument_list|>
name|matcher
parameter_list|)
block|{
name|String
name|extractMethodDesc
init|=
name|BuiltInMethod
operator|.
name|EXTRACT_VALUE
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|input
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertThat
argument_list|(
name|extractMethodDesc
argument_list|,
name|XmlFunctions
operator|.
name|extractValue
argument_list|(
name|input
argument_list|,
name|xpath
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertExtractValueFailed
parameter_list|(
name|String
name|input
parameter_list|,
name|String
name|xpath
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|Throwable
argument_list|>
name|matcher
parameter_list|)
block|{
name|String
name|extractMethodDesc
init|=
name|BuiltInMethod
operator|.
name|EXTRACT_VALUE
operator|.
name|getMethodName
argument_list|()
operator|+
literal|"("
operator|+
name|String
operator|.
name|join
argument_list|(
literal|", "
argument_list|,
name|input
argument_list|,
name|xpath
argument_list|)
operator|+
literal|")"
decl_stmt|;
name|assertFailed
argument_list|(
name|extractMethodDesc
argument_list|,
parameter_list|()
lambda|->
name|XmlFunctions
operator|.
name|extractValue
argument_list|(
name|input
argument_list|,
name|xpath
argument_list|)
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|assertFailed
parameter_list|(
name|String
name|invocationDesc
parameter_list|,
name|Supplier
argument_list|<
name|?
argument_list|>
name|supplier
parameter_list|,
name|Matcher
argument_list|<
name|?
super|super
name|Throwable
argument_list|>
name|matcher
parameter_list|)
block|{
try|try
block|{
name|supplier
operator|.
name|get
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"expect exception, but not: "
operator|+
name|invocationDesc
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|assertThat
argument_list|(
name|invocationDesc
argument_list|,
name|t
argument_list|,
name|matcher
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

