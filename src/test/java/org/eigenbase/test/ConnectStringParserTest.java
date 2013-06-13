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
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
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
name|util14
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|JUnit4
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Unit test for JDBC connect string parser, {@link ConnectStringParser}. The  * ConnectStringParser is adapted from code in Mondrian, but most of the tests  * below were unfortunately "reinvented" prior to having the Mondrian unit tests  * in hand.  */
end_comment

begin_class
specifier|public
class|class
name|ConnectStringParserTest
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**      * tests simple connect string, adapted from Mondrian tests.      */
annotation|@
name|Test
specifier|public
name|void
name|testSimpleStrings
parameter_list|()
throws|throws
name|Throwable
block|{
name|Properties
name|props
init|=
name|ConnectStringParser
operator|.
name|parse
argument_list|(
literal|"foo=x;bar=y;foo=z"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"bar"
argument_list|,
literal|"y"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"BAR"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"BAR"
argument_list|)
argument_list|)
expr_stmt|;
comment|// case-sensitive, unlike Mondrian
name|assertEquals
argument_list|(
literal|"last foo"
argument_list|,
literal|"z"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"key=\" bar\""
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|" bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
literal|"bogus key"
argument_list|,
name|props
operator|.
name|get
argument_list|(
literal|"kipper"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"param count"
argument_list|,
literal|2
argument_list|,
name|props
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|synth
init|=
name|ConnectStringParser
operator|.
name|getParamString
argument_list|(
name|props
argument_list|)
decl_stmt|;
name|Properties
name|synthProps
init|=
name|ConnectStringParser
operator|.
name|parse
argument_list|(
name|synth
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"reversible"
argument_list|,
name|props
argument_list|,
name|synthProps
argument_list|)
expr_stmt|;
block|}
comment|/**      * tests complex connect strings, adapted directly from Mondrian tests.      */
annotation|@
name|Test
specifier|public
name|void
name|testComplexStrings
parameter_list|()
throws|throws
name|Throwable
block|{
name|Properties
name|props
init|=
name|ConnectStringParser
operator|.
name|parse
argument_list|(
literal|"normalProp=value;"
operator|+
literal|"emptyValue=;"
operator|+
literal|" spaceBeforeProp=abc;"
operator|+
literal|" spaceBeforeAndAfterProp =def;"
operator|+
literal|" space in prop = foo bar ;"
operator|+
literal|"equalsInValue=foo=bar;"
operator|+
literal|"semiInProp;Name=value;"
operator|+
literal|" singleQuotedValue = 'single quoted value ending in space ' ;"
operator|+
literal|" doubleQuotedValue = "
operator|+
literal|"\"=double quoted value preceded by equals\" ;"
operator|+
literal|" singleQuotedValueWithSemi = 'one; two';"
operator|+
literal|" singleQuotedValueWithSpecials = 'one; two \"three''four=five'"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"param count"
argument_list|,
literal|11
argument_list|,
name|props
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|value
decl_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"normalProp"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"emptyValue"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|value
argument_list|)
expr_stmt|;
comment|// empty string, not null!
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"spaceBeforeProp"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"abc"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"spaceBeforeAndAfterProp"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"def"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"space in prop"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
literal|"foo bar"
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"equalsInValue"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo=bar"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"semiInProp;Name"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"value"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"singleQuotedValue"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"single quoted value ending in space "
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"doubleQuotedValue"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"=double quoted value preceded by equals"
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"singleQuotedValueWithSemi"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
literal|"one; two"
argument_list|)
expr_stmt|;
name|value
operator|=
operator|(
name|String
operator|)
name|props
operator|.
name|get
argument_list|(
literal|"singleQuotedValueWithSpecials"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|value
argument_list|,
literal|"one; two \"three'four=five"
argument_list|)
expr_stmt|;
block|}
comment|/**      * tests for specific errors thrown by the parser.      */
annotation|@
name|Test
specifier|public
name|void
name|testConnectStringErrors
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// force some parsing errors
try|try
block|{
name|ConnectStringParser
operator|.
name|parse
argument_list|(
literal|"key='can't parse'"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"quoted value ended too soon"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|assertExceptionMatches
argument_list|(
name|e
argument_list|,
literal|".*quoted value ended.*position 9.*"
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|ConnectStringParser
operator|.
name|parse
argument_list|(
literal|"key='\"can''t parse\""
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"unterminated quoted value"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SQLException
name|e
parameter_list|)
block|{
name|assertExceptionMatches
argument_list|(
name|e
argument_list|,
literal|".*unterminated quoted value.*"
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Tests most of the examples from the<a      * href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/oledb/htm/oledbconnectionstringsyntax.asp">      * OLE DB spec</a>. Omitted are cases for Window handles, returning multiple      * values, and special handling of "Provider" keyword.      *      * @throws Throwable      */
annotation|@
name|Test
specifier|public
name|void
name|testOleDbExamples
parameter_list|()
throws|throws
name|Throwable
block|{
comment|// test the parser with examples from OLE DB documentation
name|String
index|[]
index|[]
name|quads
init|=
block|{
comment|// {reason for test, key, val, string to parse},
block|{
literal|"printable chars"
block|,
literal|"Jet OLE DB:System Database"
block|,
literal|"c:\\system.mda"
block|,
literal|"Jet OLE DB:System Database=c:\\system.mda"
block|}
block|,
block|{
literal|"key embedded semi"
block|,
literal|"Authentication;Info"
block|,
literal|"Column 5"
block|,
literal|"Authentication;Info=Column 5"
block|}
block|,
block|{
literal|"key embedded equal"
block|,
literal|"Verification=Security"
block|,
literal|"True"
block|,
literal|"Verification==Security=True"
block|}
block|,
block|{
literal|"key many equals"
block|,
literal|"Many==One"
block|,
literal|"Valid"
block|,
literal|"Many====One=Valid"
block|}
block|,
block|{
literal|"key too many equal"
block|,
literal|"TooMany="
block|,
literal|"False"
block|,
literal|"TooMany===False"
block|}
block|,
block|{
literal|"value embedded quote and semi"
block|,
literal|"ExtProps"
block|,
literal|"Data Source='localhost';Key Two='value 2'"
block|,
literal|"ExtProps=\"Data Source='localhost';Key Two='value 2'\""
block|}
block|,
block|{
literal|"value embedded double quote and semi"
block|,
literal|"ExtProps"
block|,
literal|"Integrated Security=\"SSPI\";Key Two=\"value 2\""
block|,
literal|"ExtProps='Integrated Security=\"SSPI\";Key Two=\"value 2\"'"
block|}
block|,
block|{
literal|"value double quoted"
block|,
literal|"DataSchema"
block|,
literal|"\"MyCustTable\""
block|,
literal|"DataSchema='\"MyCustTable\"'"
block|}
block|,
block|{
literal|"value single quoted"
block|,
literal|"DataSchema"
block|,
literal|"'MyCustTable'"
block|,
literal|"DataSchema=\"'MyCustTable'\""
block|}
block|,
block|{
literal|"value double quoted double trouble"
block|,
literal|"Caption"
block|,
literal|"\"Company's \"new\" customer\""
block|,
literal|"Caption=\"\"\"Company's \"\"new\"\" customer\"\"\""
block|}
block|,
block|{
literal|"value single quoted double trouble"
block|,
literal|"Caption"
block|,
literal|"\"Company's \"new\" customer\""
block|,
literal|"Caption='\"Company''s \"new\" customer\"'"
block|}
block|,
block|{
literal|"embedded blanks and trim"
block|,
literal|"My Keyword"
block|,
literal|"My Value"
block|,
literal|" My Keyword = My Value ;MyNextValue=Value"
block|}
block|,
block|{
literal|"value single quotes preserve blanks"
block|,
literal|"My Keyword"
block|,
literal|" My Value "
block|,
literal|" My Keyword =' My Value ';MyNextValue=Value"
block|}
block|,
block|{
literal|"value double quotes preserve blanks"
block|,
literal|"My Keyword"
block|,
literal|" My Value "
block|,
literal|" My Keyword =\" My Value \";MyNextValue=Value"
block|}
block|,
block|{
literal|"last redundant key wins"
block|,
literal|"SomeKey"
block|,
literal|"NextValue"
block|,
literal|"SomeKey=FirstValue;SomeKey=NextValue"
block|}
block|,         }
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|quads
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|String
name|why
init|=
name|quads
index|[
name|i
index|]
index|[
literal|0
index|]
decl_stmt|;
name|String
name|key
init|=
name|quads
index|[
name|i
index|]
index|[
literal|1
index|]
decl_stmt|;
name|String
name|val
init|=
name|quads
index|[
name|i
index|]
index|[
literal|2
index|]
decl_stmt|;
name|String
name|str
init|=
name|quads
index|[
name|i
index|]
index|[
literal|3
index|]
decl_stmt|;
comment|//            tracer.info("parse: " +str);
name|Properties
name|props
init|=
name|ConnectStringParser
operator|.
name|parse
argument_list|(
name|str
argument_list|)
decl_stmt|;
comment|//            tracer.info("props: " +toStringProperties(props));
name|assertEquals
argument_list|(
name|why
argument_list|,
name|val
argument_list|,
name|props
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|synth
init|=
name|ConnectStringParser
operator|.
name|getParamString
argument_list|(
name|props
argument_list|)
decl_stmt|;
comment|//            tracer.info("synth: " +synth);
try|try
block|{
name|assertEquals
argument_list|(
literal|"reversible "
operator|+
name|why
argument_list|,
name|str
argument_list|,
name|synth
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
comment|// it's OK that the strings don't match as long as the
comment|// two strings parse out the same way and are thus
comment|// "semantically reversible"
name|Properties
name|synthProps
init|=
name|ConnectStringParser
operator|.
name|parse
argument_list|(
name|synth
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"equivalent "
operator|+
name|why
argument_list|,
name|props
argument_list|,
name|synthProps
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|static
name|void
name|assertExceptionMatches
parameter_list|(
name|Throwable
name|e
parameter_list|,
name|String
name|expectedPattern
parameter_list|)
block|{
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|fail
argument_list|(
literal|"Expected an error which matches pattern '"
operator|+
name|expectedPattern
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|String
name|msg
init|=
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|msg
operator|.
name|matches
argument_list|(
name|expectedPattern
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Got a different error '"
operator|+
name|msg
operator|+
literal|"' than expected '"
operator|+
name|expectedPattern
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End ConnectStringParserTest.java
end_comment

end_unit

