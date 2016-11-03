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
name|geode
operator|.
name|rel
package|;
end_package

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
name|java
operator|.
name|sql
operator|.
name|SQLException
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.geode} package.  *  *<p>Before calling this rel, you need to populate Geode, as follows:  *  *<blockquote><code>  * git clone https://github.com/vlsi/calcite-test-dataset<br>  * cd calcite-rel-dataset<br>  * mvn install  *</code></blockquote>  *  *<p>This will create a virtual machine with Geode and the "bookshop" and "zips" rel dataset.  */
end_comment

begin_class
specifier|public
class|class
name|GeodeAdapterIT
extends|extends
name|BaseGeodeAdapterIT
block|{
annotation|@
name|Test
specifier|public
name|void
name|testSqlSimple
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"SELECT \"itemNumber\" "
operator|+
literal|"FROM \"BookMaster\" WHERE \"itemNumber\"> 123"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlSingleNumberWhereFilter
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"SELECT * FROM \"BookMaster\" "
operator|+
literal|"WHERE \"itemNumber\" = 123"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlDistinctSort
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"SELECT DISTINCT \"itemNumber\", \"author\" "
operator|+
literal|"FROM \"BookMaster\" ORDER BY \"itemNumber\", \"author\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlDistinctSort2
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"SELECT \"itemNumber\", \"author\" "
operator|+
literal|"FROM \"BookMaster\" GROUP BY \"itemNumber\", \"author\" ORDER BY \"itemNumber\", "
operator|+
literal|"\"author\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlDistinctSort3
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"SELECT DISTINCT * FROM \"BookMaster\""
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlLimit2
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"SELECT DISTINCT * FROM \"BookMaster\" LIMIT 2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlDisjunciton
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"SELECT \"author\" FROM \"BookMaster\" "
operator|+
literal|"WHERE \"itemNumber\" = 789 OR \"itemNumber\" = 123"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlConjunciton
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"SELECT \"author\" FROM \"BookMaster\" "
operator|+
literal|"WHERE \"itemNumber\" = 789 AND \"author\" = 'Jim Heavisides'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlBookMasterWhere
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"select \"author\", \"title\" from \"BookMaster\" "
operator|+
literal|"WHERE \"author\" = \'Jim Heavisides\' LIMIT 2"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSqlBookMasterCount
parameter_list|()
throws|throws
name|SQLException
block|{
name|checkSql
argument_list|(
literal|"model-bookshop"
argument_list|,
literal|"select count(*) from \"BookMaster\""
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End GeodeAdapterIT.java
end_comment

end_unit

