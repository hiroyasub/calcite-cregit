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
name|kafka
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
name|test
operator|.
name|CalciteAssert
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|Resources
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UncheckedIOException
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
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * Unit test cases for Kafka adapter.  */
end_comment

begin_class
specifier|public
class|class
name|KafkaAdapterTest
block|{
specifier|protected
specifier|static
specifier|final
name|URL
name|MODEL
init|=
name|KafkaAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/kafka.model.json"
argument_list|)
decl_stmt|;
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|assertModel
parameter_list|(
name|String
name|model
parameter_list|)
block|{
comment|// ensure that Schema from this instance is being used
name|model
operator|=
name|model
operator|.
name|replace
argument_list|(
name|KafkaAdapterTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|KafkaAdapterTest
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
argument_list|(
name|model
argument_list|)
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|assertModel
parameter_list|(
name|URL
name|url
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|url
argument_list|,
literal|"url"
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|assertModel
argument_list|(
name|Resources
operator|.
name|toString
argument_list|(
name|url
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|UncheckedIOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSelect
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT STREAM * FROM KAFKA.MOCKTABLE"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[MSG_PARTITION INTEGER NOT NULL"
operator|+
literal|", MSG_TIMESTAMP BIGINT NOT NULL"
operator|+
literal|", MSG_OFFSET BIGINT NOT NULL"
operator|+
literal|", MSG_KEY_BYTES VARBINARY"
operator|+
literal|", MSG_VALUE_BYTES VARBINARY NOT NULL]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"MSG_PARTITION=0; MSG_TIMESTAMP=-1; MSG_OFFSET=0; MSG_KEY_BYTES=mykey0; MSG_VALUE_BYTES=myvalue0"
argument_list|,
literal|"MSG_PARTITION=0; MSG_TIMESTAMP=-1; MSG_OFFSET=1"
operator|+
literal|"; MSG_KEY_BYTES=mykey1; MSG_VALUE_BYTES=myvalue1"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableTableScan(table=[[KAFKA, MOCKTABLE, (STREAM)]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFilterWithProject
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT STREAM MSG_PARTITION,MSG_OFFSET,MSG_VALUE_BYTES FROM KAFKA.MOCKTABLE"
operator|+
literal|" WHERE MSG_OFFSET>0"
argument_list|)
operator|.
name|limit
argument_list|(
literal|1
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"MSG_PARTITION=0; MSG_OFFSET=1; MSG_VALUE_BYTES=myvalue1"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableCalc(expr#0..4=[{inputs}], expr#5=[0], expr#6=[>($t2, $t5)], MSG_PARTITION=[$t0], MSG_OFFSET=[$t2], MSG_VALUE_BYTES=[$t4], $condition=[$t6])\n"
operator|+
literal|"  EnumerableInterpreter\n"
operator|+
literal|"    BindableTableScan(table=[[KAFKA, MOCKTABLE, (STREAM)]])"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustRowConverter
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT STREAM * FROM KAFKA.MOCKTABLE_CUST_ROW_CONVERTER"
argument_list|)
operator|.
name|limit
argument_list|(
literal|2
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[TOPIC_NAME VARCHAR NOT NULL"
operator|+
literal|", PARTITION_ID INTEGER NOT NULL"
operator|+
literal|", TIMESTAMP_TYPE VARCHAR]"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"TOPIC_NAME=testtopic; PARTITION_ID=0; TIMESTAMP_TYPE=NoTimestampType"
argument_list|,
literal|"TOPIC_NAME=testtopic; PARTITION_ID=0; TIMESTAMP_TYPE=NoTimestampType"
argument_list|)
operator|.
name|explainContains
argument_list|(
literal|"PLAN=EnumerableInterpreter\n"
operator|+
literal|"  BindableTableScan(table=[[KAFKA, MOCKTABLE_CUST_ROW_CONVERTER, (STREAM)]])\n"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testAsBatch
parameter_list|()
block|{
name|assertModel
argument_list|(
name|MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"SELECT * FROM KAFKA.MOCKTABLE"
argument_list|)
operator|.
name|failsAtValidation
argument_list|(
literal|"Cannot convert stream 'MOCKTABLE' to relation"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End KafkaAdapterTest.java
end_comment

end_unit

