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
name|remote
operator|.
name|KerberosConnection
operator|.
name|RenewalTask
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|login
operator|.
name|LoginContext
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
name|assertNotNull
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
name|ArgumentMatchers
operator|.
name|any
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
name|mock
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
name|nullable
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
name|verify
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
comment|/**  * Test case for KerberosConnection  */
end_comment

begin_class
specifier|public
class|class
name|KerberosConnectionTest
block|{
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|NullPointerException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testNullArgs
parameter_list|()
block|{
operator|new
name|KerberosConnection
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testThreadConfiguration
parameter_list|()
block|{
name|KerberosConnection
name|krbUtil
init|=
operator|new
name|KerberosConnection
argument_list|(
literal|"foo"
argument_list|,
operator|new
name|File
argument_list|(
literal|"/bar.keytab"
argument_list|)
argument_list|)
decl_stmt|;
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|LoginContext
name|context
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|LoginContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|Entry
argument_list|<
name|RenewalTask
argument_list|,
name|Thread
argument_list|>
name|entry
init|=
name|krbUtil
operator|.
name|createRenewalThread
argument_list|(
name|context
argument_list|,
name|subject
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"RenewalTask should not be null"
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|Thread
name|t
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Thread name should contain 'Avatica', but is '"
operator|+
name|t
operator|.
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|,
name|t
operator|.
name|getName
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Avatica"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|t
operator|.
name|isDaemon
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|t
operator|.
name|getUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|noPreviousContextOnLogin
parameter_list|()
throws|throws
name|Exception
block|{
name|KerberosConnection
name|krbUtil
init|=
name|mock
argument_list|(
name|KerberosConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|Subject
name|loggedInSubject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|LoginContext
name|context
init|=
name|mock
argument_list|(
name|LoginContext
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Call the real login(LoginContext, Configuration, Subject) method
name|when
argument_list|(
name|krbUtil
operator|.
name|login
argument_list|(
name|nullable
argument_list|(
name|LoginContext
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
comment|// Return a fake LoginContext
name|when
argument_list|(
name|krbUtil
operator|.
name|createLoginContext
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// Return a fake Subject from that fake LoginContext
name|when
argument_list|(
name|context
operator|.
name|getSubject
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|loggedInSubject
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|LoginContext
argument_list|,
name|Subject
argument_list|>
name|pair
init|=
name|krbUtil
operator|.
name|login
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|,
name|subject
argument_list|)
decl_stmt|;
comment|// Verify we get the fake LoginContext and Subject
name|assertEquals
argument_list|(
name|context
argument_list|,
name|pair
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|loggedInSubject
argument_list|,
name|pair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
comment|// login should be called on the LoginContext
name|verify
argument_list|(
name|context
argument_list|)
operator|.
name|login
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|previousContextLoggedOut
parameter_list|()
throws|throws
name|Exception
block|{
name|KerberosConnection
name|krbUtil
init|=
name|mock
argument_list|(
name|KerberosConnection
operator|.
name|class
argument_list|)
decl_stmt|;
name|Subject
name|subject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|Subject
name|loggedInSubject
init|=
operator|new
name|Subject
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|LoginContext
name|originalContext
init|=
name|mock
argument_list|(
name|LoginContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|LoginContext
name|context
init|=
name|mock
argument_list|(
name|LoginContext
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Call the real login(LoginContext, Configuration, Subject) method
name|when
argument_list|(
name|krbUtil
operator|.
name|login
argument_list|(
name|any
argument_list|(
name|LoginContext
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Subject
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
comment|// Return a fake LoginContext
name|when
argument_list|(
name|krbUtil
operator|.
name|createLoginContext
argument_list|(
name|conf
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// Return a fake Subject from that fake LoginContext
name|when
argument_list|(
name|context
operator|.
name|getSubject
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|loggedInSubject
argument_list|)
expr_stmt|;
name|Entry
argument_list|<
name|LoginContext
argument_list|,
name|Subject
argument_list|>
name|pair
init|=
name|krbUtil
operator|.
name|login
argument_list|(
name|originalContext
argument_list|,
name|conf
argument_list|,
name|subject
argument_list|)
decl_stmt|;
comment|// Verify we get the fake LoginContext and Subject
name|assertEquals
argument_list|(
name|context
argument_list|,
name|pair
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|loggedInSubject
argument_list|,
name|pair
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|originalContext
argument_list|)
operator|.
name|logout
argument_list|()
expr_stmt|;
comment|// login should be called on the LoginContext
name|verify
argument_list|(
name|context
argument_list|)
operator|.
name|login
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTicketRenewalTime
parameter_list|()
block|{
name|RenewalTask
name|renewal
init|=
name|mock
argument_list|(
name|RenewalTask
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|renewal
operator|.
name|shouldRenew
argument_list|(
name|any
argument_list|(
name|long
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|long
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|long
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenCallRealMethod
argument_list|()
expr_stmt|;
name|long
name|start
init|=
literal|0
decl_stmt|;
name|long
name|end
init|=
literal|200
decl_stmt|;
name|long
name|now
init|=
literal|100
decl_stmt|;
name|assertFalse
argument_list|(
name|renewal
operator|.
name|shouldRenew
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|now
argument_list|)
argument_list|)
expr_stmt|;
comment|// Renewal should happen at 80%
name|start
operator|=
literal|0
expr_stmt|;
name|end
operator|=
literal|100
expr_stmt|;
name|now
operator|=
literal|80
expr_stmt|;
name|assertTrue
argument_list|(
name|renewal
operator|.
name|shouldRenew
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|now
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
literal|5000
expr_stmt|;
comment|// One day
name|end
operator|=
name|start
operator|+
literal|1000
operator|*
literal|60
operator|*
literal|60
operator|*
literal|24
expr_stmt|;
comment|// Ten minutes prior to expiration
name|now
operator|=
name|end
operator|-
literal|1000
operator|*
literal|60
operator|*
literal|10
expr_stmt|;
name|assertTrue
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|,
literal|"start=%d, end=%d, now=%d"
argument_list|,
name|start
argument_list|,
name|end
argument_list|,
name|now
argument_list|)
argument_list|,
name|renewal
operator|.
name|shouldRenew
argument_list|(
name|start
argument_list|,
name|end
argument_list|,
name|now
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End KerberosConnectionTest.java
end_comment

end_unit

