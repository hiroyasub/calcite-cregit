begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import static
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
operator|.
name|OptiqAssert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/**  * Tests for a JDBC front-end and JDBC back-end.  *  *<p>The idea is that as much as possible of the query is pushed down  * to the JDBC data source, in the form of a large (and hopefully efficient)  * SQL statement.</p>  *  * @see JdbcFrontJdbcBackLinqMiddleTest  *  * @author jhyde  */
end_comment

begin_class
specifier|public
class|class
name|JdbcFrontJdbcBackTest
extends|extends
name|TestCase
block|{
specifier|public
name|void
name|testWhere2
parameter_list|()
block|{
name|assertThat
argument_list|()
operator|.
name|with
argument_list|(
name|OptiqAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART2
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"foodmart\".\"days\" where \"day\"< 3"
argument_list|)
operator|.
name|returns
argument_list|(
literal|"day=1; week_day=Sunday\n"
operator|+
literal|"day=2; week_day=Monday\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End JdbcFrontJdbcBackTest.java
end_comment

end_unit

