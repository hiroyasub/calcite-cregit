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
name|AvaticaSeverity
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
name|ErrorResponse
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
name|Request
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
name|Response
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
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
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

begin_comment
comment|/**  * Test for common functionality across {@link Handler} implementations.  */
end_comment

begin_class
specifier|public
class|class
name|AbstractHandlerTest
block|{
specifier|private
name|String
name|exceptionToString
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|StringWriter
name|sw
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|sw
argument_list|)
decl_stmt|;
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|e
argument_list|)
operator|.
name|printStackTrace
argument_list|(
name|pw
argument_list|)
expr_stmt|;
return|return
name|sw
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExceptionUnwrappingWithoutContext
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|AbstractHandler
argument_list|<
name|String
argument_list|>
name|handler
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AbstractHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|unwrapException
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
name|Exception
name|e
init|=
operator|new
name|RuntimeException
argument_list|()
decl_stmt|;
name|Response
name|resp
init|=
name|handler
operator|.
name|unwrapException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Response should be ErrorResponse, but was "
operator|+
name|resp
operator|.
name|getClass
argument_list|()
argument_list|,
name|resp
operator|instanceof
name|ErrorResponse
argument_list|)
expr_stmt|;
name|ErrorResponse
name|errorResp
init|=
operator|(
name|ErrorResponse
operator|)
name|resp
decl_stmt|;
name|assertEquals
argument_list|(
name|ErrorResponse
operator|.
name|UNKNOWN_ERROR_CODE
argument_list|,
name|errorResp
operator|.
name|errorCode
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AvaticaSeverity
operator|.
name|UNKNOWN
argument_list|,
name|errorResp
operator|.
name|severity
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|exceptionToString
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|,
name|errorResp
operator|.
name|exceptions
argument_list|)
expr_stmt|;
name|e
operator|=
operator|new
name|AvaticaRuntimeException
argument_list|()
expr_stmt|;
name|resp
operator|=
name|handler
operator|.
name|unwrapException
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Response should be ErrorResponse, but was "
operator|+
name|resp
operator|.
name|getClass
argument_list|()
argument_list|,
name|resp
operator|instanceof
name|ErrorResponse
argument_list|)
expr_stmt|;
name|errorResp
operator|=
operator|(
name|ErrorResponse
operator|)
name|resp
expr_stmt|;
name|assertEquals
argument_list|(
name|ErrorResponse
operator|.
name|UNKNOWN_ERROR_CODE
argument_list|,
name|errorResp
operator|.
name|errorCode
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AvaticaSeverity
operator|.
name|UNKNOWN
argument_list|,
name|errorResp
operator|.
name|severity
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|exceptionToString
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|,
name|errorResp
operator|.
name|exceptions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testExceptionUnwrappingWithContext
parameter_list|()
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|AbstractHandler
argument_list|<
name|String
argument_list|>
name|handler
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AbstractHandler
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|unwrapException
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|Exception
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
specifier|final
name|String
name|msg
init|=
literal|"Something failed!"
decl_stmt|;
name|AvaticaRuntimeException
name|e
init|=
operator|new
name|AvaticaRuntimeException
argument_list|(
name|msg
argument_list|,
name|ErrorResponse
operator|.
name|UNKNOWN_ERROR_CODE
argument_list|,
name|ErrorResponse
operator|.
name|UNKNOWN_SQL_STATE
argument_list|,
name|AvaticaSeverity
operator|.
name|FATAL
argument_list|)
decl_stmt|;
name|Response
name|resp
init|=
name|handler
operator|.
name|unwrapException
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Response should be ErrorResponse, but was "
operator|+
name|resp
operator|.
name|getClass
argument_list|()
argument_list|,
name|resp
operator|instanceof
name|ErrorResponse
argument_list|)
expr_stmt|;
name|ErrorResponse
name|errorResp
init|=
operator|(
name|ErrorResponse
operator|)
name|resp
decl_stmt|;
name|assertEquals
argument_list|(
name|ErrorResponse
operator|.
name|UNKNOWN_ERROR_CODE
argument_list|,
name|errorResp
operator|.
name|errorCode
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|AvaticaSeverity
operator|.
name|FATAL
argument_list|,
name|errorResp
operator|.
name|severity
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|exceptionToString
argument_list|(
name|e
argument_list|)
argument_list|)
argument_list|,
name|errorResp
operator|.
name|exceptions
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|msg
argument_list|,
name|errorResp
operator|.
name|errorMessage
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailedResponseSerialization
parameter_list|()
throws|throws
name|IOException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|AbstractHandler
argument_list|<
name|String
argument_list|>
name|handler
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AbstractHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Request
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Request
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Response
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Response
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|IOException
name|exception
init|=
operator|new
name|IOException
argument_list|()
decl_stmt|;
specifier|final
name|ErrorResponse
name|errorResponse
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ErrorResponse
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|String
name|serializedErrorResponse
init|=
literal|"An ErrorResponse"
decl_stmt|;
comment|// Accept a serialized request
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|apply
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
comment|// Deserialize it back into a POJO
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|decode
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// Construct the Response for that Request
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|accept
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|Service
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
comment|// Throw an IOException when serializing the Response.
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|encode
argument_list|(
name|response
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|exception
argument_list|)
expr_stmt|;
comment|// Convert the IOException into an ErrorResponse
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|unwrapException
argument_list|(
name|exception
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|errorResponse
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|encode
argument_list|(
name|errorResponse
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|serializedErrorResponse
argument_list|)
expr_stmt|;
name|HandlerResponse
argument_list|<
name|String
argument_list|>
name|handlerResp
init|=
name|handler
operator|.
name|apply
argument_list|(
literal|"this is mocked out"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|500
argument_list|,
name|handlerResp
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|serializedErrorResponse
argument_list|,
name|handlerResp
operator|.
name|getResponse
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailedErrorResponseSerialization
parameter_list|()
throws|throws
name|IOException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|AbstractHandler
argument_list|<
name|String
argument_list|>
name|handler
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AbstractHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Request
name|request
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Request
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|Response
name|response
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Response
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|IOException
name|exception
init|=
operator|new
name|IOException
argument_list|()
decl_stmt|;
specifier|final
name|ErrorResponse
name|errorResponse
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|ErrorResponse
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Accept a serialized request
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|apply
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
comment|// Deserialize it back into a POJO
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|decode
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// Construct the Response for that Request
name|Mockito
operator|.
name|when
argument_list|(
name|request
operator|.
name|accept
argument_list|(
name|Mockito
operator|.
name|any
argument_list|(
name|Service
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|response
argument_list|)
expr_stmt|;
comment|// Throw an IOException when serializing the Response.
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|encode
argument_list|(
name|response
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|exception
argument_list|)
expr_stmt|;
comment|// Convert the IOException into an ErrorResponse
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|unwrapException
argument_list|(
name|exception
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|errorResponse
argument_list|)
expr_stmt|;
comment|// Fail to serialize the ErrorResponse
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|encode
argument_list|(
name|errorResponse
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|exception
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|apply
argument_list|(
literal|"this is mocked out"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|exception
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testFailedRequestDeserialization
parameter_list|()
throws|throws
name|IOException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|AbstractHandler
argument_list|<
name|String
argument_list|>
name|handler
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|AbstractHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|IOException
name|exception
init|=
operator|new
name|IOException
argument_list|()
decl_stmt|;
comment|// Accept a serialized request
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|apply
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
comment|// Throw an Exception trying to convert it back into a POJO
name|Mockito
operator|.
name|when
argument_list|(
name|handler
operator|.
name|decode
argument_list|(
name|Mockito
operator|.
name|anyString
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|exception
argument_list|)
expr_stmt|;
try|try
block|{
name|handler
operator|.
name|apply
argument_list|(
literal|"this is mocked out"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|exception
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

begin_comment
comment|// End AbstractHandlerTest.java
end_comment

end_unit

