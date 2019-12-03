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
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Tests for user-defined types.  */
end_comment

begin_class
specifier|public
class|class
name|UdtTest
block|{
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|withUdt
parameter_list|()
block|{
specifier|final
name|String
name|model
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"   schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       name: 'adhoc',\n"
operator|+
literal|"       types: [\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'mytype1',\n"
operator|+
literal|"           type: 'BIGINT'\n"
operator|+
literal|"         },\n"
operator|+
literal|"         {\n"
operator|+
literal|"           name: 'mytype2',\n"
operator|+
literal|"           attributes: [\n"
operator|+
literal|"             {\n"
operator|+
literal|"               name: 'ii',\n"
operator|+
literal|"               type: 'INTEGER'\n"
operator|+
literal|"             },\n"
operator|+
literal|"             {\n"
operator|+
literal|"               name: 'jj',\n"
operator|+
literal|"               type: 'INTEGER'\n"
operator|+
literal|"             }\n"
operator|+
literal|"           ]\n"
operator|+
literal|"         }\n"
operator|+
literal|"       ]\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
return|return
name|CalciteAssert
operator|.
name|model
argument_list|(
name|model
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testUdt
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select CAST(\"id\" AS \"adhoc\".mytype1) as ld "
operator|+
literal|"from (VALUES ROW(1, 'SameName')) AS \"t\" (\"id\", \"desc\")"
decl_stmt|;
name|withUdt
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"LD=1\n"
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-3045">[CALCITE-3045]    * NullPointerException when casting null literal to composite user defined type</a>. */
annotation|@
name|Test
specifier|public
name|void
name|testCastNullLiteralToCompositeUdt
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select CAST(null AS \"adhoc\".mytype2) as c "
operator|+
literal|"from (VALUES (1))"
decl_stmt|;
name|withUdt
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|returns
argument_list|(
literal|"C=null\n"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

