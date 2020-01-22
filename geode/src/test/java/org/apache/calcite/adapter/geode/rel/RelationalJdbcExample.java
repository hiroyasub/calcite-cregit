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
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|ResultSetMetaData
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
name|Properties
import|;
end_import

begin_comment
comment|/**  * Example of using Geode via JDBC.  *  *<p>Before using this example, you need to populate Geode, as follows:  *  *<blockquote><code>  * git clone https://github.com/vlsi/calcite-test-dataset<br>  * cd calcite-test-dataset<br>  * mvn install  *</code></blockquote>  *  *<p>This will create a virtual machine with Geode and the "bookshop" and "zips"  * test data sets.  */
end_comment

begin_class
specifier|public
class|class
name|RelationalJdbcExample
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RelationalJdbcExample
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|RelationalJdbcExample
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|String
name|geodeModelJson
init|=
literal|"inline:"
operator|+
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  schemas: [\n"
operator|+
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: 'TEST',\n"
operator|+
literal|"       factory: 'org.apache.calcite.adapter.geode.rel.GeodeSchemaFactory',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         locatorHost: 'localhost',\n"
operator|+
literal|"         locatorPort: '10334',\n"
operator|+
literal|"         regions: 'BookMaster,BookCustomer,BookInventory,BookOrder',\n"
operator|+
literal|"         pdxSerializablePackagePath: 'org.apache.calcite.adapter.geode.domain.*'\n"
operator|+
literal|"       }\n"
operator|+
literal|"     }\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
name|Class
operator|.
name|forName
argument_list|(
literal|"org.apache.calcite.jdbc.Driver"
argument_list|)
expr_stmt|;
name|Properties
name|info
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|info
operator|.
name|put
argument_list|(
literal|"model"
argument_list|,
name|geodeModelJson
argument_list|)
expr_stmt|;
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
decl_stmt|;
name|Statement
name|statement
init|=
name|connection
operator|.
name|createStatement
argument_list|()
decl_stmt|;
name|ResultSet
name|resultSet
init|=
name|statement
operator|.
name|executeQuery
argument_list|(
literal|"SELECT \"b\".\"author\", \"b\".\"retailCost\", \"i\".\"quantityInStock\"\n"
operator|+
literal|"FROM \"TEST\".\"BookMaster\" AS \"b\" "
operator|+
literal|" INNER JOIN \"TEST\".\"BookInventory\" AS \"i\""
operator|+
literal|"  ON \"b\".\"itemNumber\" = \"i\".\"itemNumber\"\n "
operator|+
literal|"WHERE  \"b\".\"retailCost\"> 0"
argument_list|)
decl_stmt|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
while|while
condition|(
name|resultSet
operator|.
name|next
argument_list|()
condition|)
block|{
name|ResultSetMetaData
name|metaData
init|=
name|resultSet
operator|.
name|getMetaData
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
name|metaData
operator|.
name|getColumnCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|i
operator|>
literal|1
condition|?
literal|"; "
else|:
literal|""
argument_list|)
operator|.
name|append
argument_list|(
name|metaData
operator|.
name|getColumnLabel
argument_list|(
name|i
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"="
argument_list|)
operator|.
name|append
argument_list|(
name|resultSet
operator|.
name|getObject
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|LOGGER
operator|.
name|info
argument_list|(
literal|"Result entry: "
operator|+
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|resultSet
operator|.
name|close
argument_list|()
expr_stmt|;
name|statement
operator|.
name|close
argument_list|()
expr_stmt|;
name|connection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

