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
name|avatica
operator|.
name|remote
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
name|avatica
operator|.
name|Meta
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
name|avatica
operator|.
name|Meta
operator|.
name|Frame
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
name|avatica
operator|.
name|metrics
operator|.
name|noop
operator|.
name|NoopMetricsSystem
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
name|avatica
operator|.
name|proto
operator|.
name|Common
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
name|avatica
operator|.
name|proto
operator|.
name|Common
operator|.
name|ColumnValue
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
name|avatica
operator|.
name|proto
operator|.
name|Requests
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
name|avatica
operator|.
name|proto
operator|.
name|Responses
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
name|avatica
operator|.
name|remote
operator|.
name|Handler
operator|.
name|HandlerResponse
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
name|avatica
operator|.
name|remote
operator|.
name|Service
operator|.
name|FetchRequest
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
name|avatica
operator|.
name|remote
operator|.
name|Service
operator|.
name|FetchResponse
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
name|avatica
operator|.
name|remote
operator|.
name|Service
operator|.
name|RpcMetadataResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|List
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
name|assertEquals
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
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Test basic serialization of objects with protocol buffers.  */
end_comment

begin_class
specifier|public
class|class
name|ProtobufHandlerTest
block|{
comment|// Mocks
specifier|private
name|Service
name|service
decl_stmt|;
specifier|private
name|ProtobufTranslation
name|translation
decl_stmt|;
comment|// Real objects
specifier|private
name|ProtobufHandler
name|handler
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setupMocks
parameter_list|()
block|{
comment|// Mocks
name|service
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Service
operator|.
name|class
argument_list|)
expr_stmt|;
name|translation
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ProtobufTranslation
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Real objects
name|handler
operator|=
operator|new
name|ProtobufHandler
argument_list|(
name|service
argument_list|,
name|translation
argument_list|,
name|NoopMetricsSystem
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFetch
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|connectionId
init|=
literal|"cnxn1"
decl_stmt|;
specifier|final
name|int
name|statementId
init|=
literal|30
decl_stmt|;
specifier|final
name|long
name|offset
init|=
literal|10
decl_stmt|;
specifier|final
name|int
name|fetchMaxRowCount
init|=
literal|100
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Common
operator|.
name|TypedValue
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
name|Common
operator|.
name|TypedValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|Common
operator|.
name|Rep
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|setBoolValue
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
name|Common
operator|.
name|TypedValue
operator|.
name|newBuilder
argument_list|()
operator|.
name|setType
argument_list|(
name|Common
operator|.
name|Rep
operator|.
name|STRING
argument_list|)
operator|.
name|setStringValue
argument_list|(
literal|"my_string"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|Requests
operator|.
name|FetchRequest
name|protoRequest
init|=
name|Requests
operator|.
name|FetchRequest
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConnectionId
argument_list|(
name|connectionId
argument_list|)
operator|.
name|setStatementId
argument_list|(
name|statementId
argument_list|)
operator|.
name|setOffset
argument_list|(
name|offset
argument_list|)
operator|.
name|setFetchMaxRowCount
argument_list|(
name|fetchMaxRowCount
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|byte
index|[]
name|serializedRequest
init|=
name|protoRequest
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|FetchRequest
name|request
init|=
operator|new
name|FetchRequest
argument_list|()
operator|.
name|deserialize
argument_list|(
name|protoRequest
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Object
argument_list|>
name|frameRows
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|frameRows
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|true
block|,
literal|"my_string"
block|}
argument_list|)
expr_stmt|;
name|Meta
operator|.
name|Frame
name|frame
init|=
name|Frame
operator|.
name|create
argument_list|(
literal|0
argument_list|,
literal|true
argument_list|,
name|frameRows
argument_list|)
decl_stmt|;
name|RpcMetadataResponse
name|metadata
init|=
operator|new
name|RpcMetadataResponse
argument_list|(
literal|"localhost:8765"
argument_list|)
decl_stmt|;
name|FetchResponse
name|response
init|=
operator|new
name|FetchResponse
argument_list|(
name|frame
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|translation
operator|.
name|parseRequest
argument_list|(
name|serializedRequest
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|service
operator|.
name|apply
argument_list|(
name|request
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|translation
operator|.
name|serializeResponse
argument_list|(
name|response
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
operator|.
name|serialize
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|HandlerResponse
argument_list|<
name|byte
index|[]
argument_list|>
name|handlerResponse
init|=
name|handler
operator|.
name|apply
argument_list|(
name|serializedRequest
argument_list|)
decl_stmt|;
name|byte
index|[]
name|serializedResponse
init|=
name|handlerResponse
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|200
argument_list|,
name|handlerResponse
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|Responses
operator|.
name|FetchResponse
name|protoResponse
init|=
name|Responses
operator|.
name|FetchResponse
operator|.
name|parseFrom
argument_list|(
name|serializedResponse
argument_list|)
decl_stmt|;
name|Common
operator|.
name|Frame
name|protoFrame
init|=
name|protoResponse
operator|.
name|getFrame
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|frame
operator|.
name|offset
argument_list|,
name|protoFrame
operator|.
name|getOffset
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|frame
operator|.
name|done
argument_list|,
name|protoFrame
operator|.
name|getDone
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Common
operator|.
name|Row
argument_list|>
name|rows
init|=
name|protoFrame
operator|.
name|getRowsList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rows
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Common
operator|.
name|Row
name|row
init|=
name|rows
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Common
operator|.
name|ColumnValue
argument_list|>
name|columnValues
init|=
name|row
operator|.
name|getValueList
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|columnValues
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|Common
operator|.
name|ColumnValue
argument_list|>
name|iter
init|=
name|columnValues
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|Common
operator|.
name|ColumnValue
name|column
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The Column should have contained a scalar: "
operator|+
name|column
argument_list|,
name|ProtobufService
operator|.
name|hasField
argument_list|(
name|column
argument_list|,
name|column
operator|.
name|getDescriptorForType
argument_list|()
argument_list|,
name|ColumnValue
operator|.
name|SCALAR_VALUE_FIELD_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|Common
operator|.
name|TypedValue
name|value
init|=
name|column
operator|.
name|getScalarValue
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|Common
operator|.
name|Rep
operator|.
name|BOOLEAN
argument_list|,
name|value
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|true
argument_list|,
name|value
operator|.
name|getBoolValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|iter
operator|.
name|hasNext
argument_list|()
argument_list|)
expr_stmt|;
name|column
operator|=
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"The Column should have contained a scalar: "
operator|+
name|column
argument_list|,
name|ProtobufService
operator|.
name|hasField
argument_list|(
name|column
argument_list|,
name|column
operator|.
name|getDescriptorForType
argument_list|()
argument_list|,
name|ColumnValue
operator|.
name|SCALAR_VALUE_FIELD_NUMBER
argument_list|)
argument_list|)
expr_stmt|;
name|value
operator|=
name|column
operator|.
name|getScalarValue
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
name|Common
operator|.
name|Rep
operator|.
name|STRING
argument_list|,
name|value
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"my_string"
argument_list|,
name|value
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ProtobufHandlerTest.java
end_comment

end_unit

