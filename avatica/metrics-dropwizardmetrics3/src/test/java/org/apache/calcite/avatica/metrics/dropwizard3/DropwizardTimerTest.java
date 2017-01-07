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
name|metrics
operator|.
name|dropwizard3
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
name|metrics
operator|.
name|dropwizard3
operator|.
name|DropwizardTimer
operator|.
name|DropwizardContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
import|;
end_import

begin_import
import|import
name|com
operator|.
name|codahale
operator|.
name|metrics
operator|.
name|Timer
operator|.
name|Context
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

begin_comment
comment|/**  * Test class for {@link DropwizardTimer}  */
end_comment

begin_class
specifier|public
class|class
name|DropwizardTimerTest
block|{
specifier|private
name|Timer
name|timer
decl_stmt|;
specifier|private
name|Context
name|context
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
block|{
comment|// Disabled on JDK9 due to Mockito bug; see [CALCITE-1567].
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.version"
argument_list|)
operator|.
name|compareTo
argument_list|(
literal|"9"
argument_list|)
operator|<
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|timer
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Timer
operator|.
name|class
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Context
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|test
parameter_list|()
block|{
name|DropwizardTimer
name|dwTimer
init|=
operator|new
name|DropwizardTimer
argument_list|(
name|timer
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|timer
operator|.
name|time
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|DropwizardContext
name|dwContext
init|=
name|dwTimer
operator|.
name|start
argument_list|()
decl_stmt|;
name|dwContext
operator|.
name|close
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|timer
argument_list|)
operator|.
name|time
argument_list|()
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|context
argument_list|)
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End DropwizardTimerTest.java
end_comment

end_unit

