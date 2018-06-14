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
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|mongodb
operator|.
name|MongoAdapterTest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|assumeTrue
import|;
end_import

begin_comment
comment|/**  * Used to trigger integration tests from maven (thus class name is suffixed with {@code IT}).  *  * If you want to run integration tests from IDE manually set  * {@code -Dcalcite.integrationTest=true} system property.  *<br>  * For command line use:  *<pre>  *     $ mvn install -Pit  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|MongoAdapterIT
extends|extends
name|MongoAdapterTest
block|{
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|enforceMongo
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|MongoAssertions
operator|.
name|useMongo
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End MongoAdapterIT.java
end_comment

end_unit

