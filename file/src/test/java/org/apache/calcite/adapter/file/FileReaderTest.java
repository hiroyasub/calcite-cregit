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
name|adapter
operator|.
name|file
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
name|util
operator|.
name|Source
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
name|Sources
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
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jsoup
operator|.
name|select
operator|.
name|Elements
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
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
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
name|assertThat
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
name|assertTrue
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
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Connection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|DriverManager
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|ResultSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Statement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * Unit tests for FileReader.  */
end_comment

begin_class
specifier|public
class|class
name|FileReaderTest
block|{
specifier|private
specifier|static
specifier|final
name|Source
name|CITIES_SOURCE
init|=
name|Sources
operator|.
name|url
argument_list|(
literal|"http://en.wikipedia.org/wiki/List_of_United_States_cities_by_population"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Source
name|STATES_SOURCE
init|=
name|Sources
operator|.
name|url
argument_list|(
literal|"http://en.wikipedia.org/wiki/List_of_states_and_territories_of_the_United_States"
argument_list|)
decl_stmt|;
comment|/** Converts a path that is relative to the module into a path that is    * relative to where the test is running. */
specifier|public
specifier|static
name|String
name|file
parameter_list|(
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
operator|new
name|File
argument_list|(
literal|"file"
argument_list|)
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|"file/"
operator|+
name|s
return|;
block|}
else|else
block|{
return|return
name|s
return|;
block|}
block|}
specifier|private
specifier|static
name|String
name|resourcePath
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|URL
name|url
init|=
name|FileReaderTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/"
operator|+
name|path
argument_list|)
decl_stmt|;
specifier|final
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|url
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|file
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
comment|/** Tests {@link FileReader} URL instantiation - no path. */
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderUrlNoPath
parameter_list|()
throws|throws
name|FileReaderException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|FileSuite
operator|.
name|hazNetwork
argument_list|()
argument_list|)
expr_stmt|;
comment|// Under OpenJDK, test fails with the following, so skip test:
comment|//   javax.net.ssl.SSLHandshakeException:
comment|//   sun.security.validator.ValidatorException: PKIX path building failed:
comment|//   sun.security.provider.certpath.SunCertPathBuilderException:
comment|//   unable to find valid certification path to requested target
specifier|final
name|String
name|r
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.runtime.name"
argument_list|)
decl_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
operator|!
name|r
operator|.
name|equals
argument_list|(
literal|"OpenJDK Runtime Environment"
argument_list|)
argument_list|)
expr_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|STATES_SOURCE
argument_list|)
decl_stmt|;
name|t
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|/** Tests {@link FileReader} URL instantiation - with path. */
annotation|@
name|Ignore
argument_list|(
literal|"[CALCITE-1789] Wikipedia format change breaks file adapter test"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderUrlWithPath
parameter_list|()
throws|throws
name|FileReaderException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|FileSuite
operator|.
name|hazNetwork
argument_list|()
argument_list|)
expr_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|CITIES_SOURCE
argument_list|,
literal|"#mw-content-text> table.wikitable.sortable"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|t
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|/** Tests {@link FileReader} URL fetch. */
annotation|@
name|Ignore
argument_list|(
literal|"[CALCITE-1789] Wikipedia format change breaks file adapter test"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderUrlFetch
parameter_list|()
throws|throws
name|FileReaderException
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|FileSuite
operator|.
name|hazNetwork
argument_list|()
argument_list|)
expr_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|STATES_SOURCE
argument_list|,
literal|"#mw-content-text> table.wikitable.sortable"
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Elements
name|row
range|:
name|t
control|)
block|{
name|i
operator|++
expr_stmt|;
block|}
name|assertThat
argument_list|(
name|i
argument_list|,
name|is
argument_list|(
literal|51
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests failed {@link FileReader} instantiation - malformed URL. */
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderMalUrl
parameter_list|()
throws|throws
name|FileReaderException
block|{
try|try
block|{
specifier|final
name|Source
name|badSource
init|=
name|Sources
operator|.
name|url
argument_list|(
literal|"bad"
operator|+
name|CITIES_SOURCE
operator|.
name|path
argument_list|()
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"expected exception, got "
operator|+
name|badSource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
argument_list|,
name|instanceOf
argument_list|(
name|MalformedURLException
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|,
name|is
argument_list|(
literal|"unknown protocol: badhttp"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Tests failed {@link FileReader} instantiation - bad URL. */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileReaderException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testFileReaderBadUrl
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|String
name|uri
init|=
literal|"http://ex.wikipedia.org/wiki/List_of_United_States_cities_by_population"
decl_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|Sources
operator|.
name|url
argument_list|(
name|uri
argument_list|)
argument_list|,
literal|"table:eq(4)"
argument_list|)
decl_stmt|;
name|t
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|/** Tests failed {@link FileReader} instantiation - bad selector. */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|FileReaderException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testFileReaderBadSelector
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|Source
name|source
init|=
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|file
argument_list|(
literal|"target/test-classes/tableOK.html"
argument_list|)
argument_list|)
decl_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|source
argument_list|,
literal|"table:eq(1)"
argument_list|)
decl_stmt|;
name|t
operator|.
name|refresh
argument_list|()
expr_stmt|;
block|}
comment|/** Test {@link FileReader} with static file - headings. */
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderHeadings
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|Source
name|source
init|=
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|file
argument_list|(
literal|"target/test-classes/tableOK.html"
argument_list|)
argument_list|)
decl_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Elements
name|headings
init|=
name|t
operator|.
name|getHeadings
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|headings
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"H1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Test {@link FileReader} with static file - data. */
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderData
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|Source
name|source
init|=
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|file
argument_list|(
literal|"target/test-classes/tableOK.html"
argument_list|)
argument_list|)
decl_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Elements
argument_list|>
name|i
init|=
name|t
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Elements
name|row
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|row
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"R0C2"
argument_list|)
argument_list|)
expr_stmt|;
name|row
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|row
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"R1C0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@link FileReader} with bad static file - headings. */
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderHeadingsBadFile
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|Source
name|source
init|=
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|file
argument_list|(
literal|"target/test-classes/tableNoTheadTbody.html"
argument_list|)
argument_list|)
decl_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Elements
name|headings
init|=
name|t
operator|.
name|getHeadings
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|headings
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"H1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@link FileReader} with bad static file - data. */
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderDataBadFile
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|Source
name|source
init|=
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|file
argument_list|(
literal|"target/test-classes/tableNoTheadTbody.html"
argument_list|)
argument_list|)
decl_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Elements
argument_list|>
name|i
init|=
name|t
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Elements
name|row
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|row
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"R0C2"
argument_list|)
argument_list|)
expr_stmt|;
name|row
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|row
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"R1C0"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@link FileReader} with no headings static file - data. */
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderDataNoTh
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|Source
name|source
init|=
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|file
argument_list|(
literal|"target/test-classes/tableNoTH.html"
argument_list|)
argument_list|)
decl_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|Elements
argument_list|>
name|i
init|=
name|t
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|Elements
name|row
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|row
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"R0C2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests {@link FileReader} iterator with static file, */
annotation|@
name|Test
specifier|public
name|void
name|testFileReaderIterator
parameter_list|()
throws|throws
name|FileReaderException
block|{
specifier|final
name|Source
name|source
init|=
name|Sources
operator|.
name|file
argument_list|(
literal|null
argument_list|,
name|file
argument_list|(
literal|"target/test-classes/tableOK.html"
argument_list|)
argument_list|)
decl_stmt|;
name|FileReader
name|t
init|=
operator|new
name|FileReader
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Elements
name|row
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Elements
name|aT
range|:
name|t
control|)
block|{
name|row
operator|=
name|aT
expr_stmt|;
block|}
name|assertFalse
argument_list|(
name|row
operator|==
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|row
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|text
argument_list|()
operator|.
name|equals
argument_list|(
literal|"R2C1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Tests reading a CSV file via the file adapter. Based on the test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1952">[CALCITE-1952]    * NPE in planner</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCsvFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|String
name|path
init|=
name|resourcePath
argument_list|(
literal|"sales-csv"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|model
init|=
literal|"inline:"
operator|+
literal|"{\n"
operator|+
literal|"  \"version\": \"1.0\",\n"
operator|+
literal|"  \"defaultSchema\": \"XXX\",\n"
operator|+
literal|"  \"schemas\": [\n"
operator|+
literal|"    {\n"
operator|+
literal|"      \"name\": \"FILES\",\n"
operator|+
literal|"      \"type\": \"custom\",\n"
operator|+
literal|"      \"factory\": \"org.apache.calcite.adapter.file.FileSchemaFactory\",\n"
operator|+
literal|"      \"operand\": {\n"
operator|+
literal|"        \"directory\": "
operator|+
name|TestUtil
operator|.
name|escapeString
argument_list|(
name|path
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"      }\n"
operator|+
literal|"    }\n"
operator|+
literal|"  ]\n"
operator|+
literal|"}"
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
name|model
argument_list|)
expr_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"lex"
argument_list|,
literal|"JAVA"
argument_list|)
expr_stmt|;
try|try
init|(
name|Connection
name|connection
init|=
name|DriverManager
operator|.
name|getConnection
argument_list|(
literal|"jdbc:calcite:"
argument_list|,
name|info
argument_list|)
init|;
name|Statement
name|stmt
init|=
name|connection
operator|.
name|createStatement
argument_list|()
init|)
block|{
specifier|final
name|String
name|sql
init|=
literal|"select * from FILES.DEPTS"
decl_stmt|;
specifier|final
name|ResultSet
name|rs
init|=
name|stmt
operator|.
name|executeQuery
argument_list|(
name|sql
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|rs
operator|.
name|next
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
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"10"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
operator|.
name|next
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
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"20"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
operator|.
name|next
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
name|rs
operator|.
name|getString
argument_list|(
literal|1
argument_list|)
argument_list|,
name|is
argument_list|(
literal|"30"
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|rs
operator|.
name|next
argument_list|()
argument_list|,
name|is
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|rs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End FileReaderTest.java
end_comment

end_unit

