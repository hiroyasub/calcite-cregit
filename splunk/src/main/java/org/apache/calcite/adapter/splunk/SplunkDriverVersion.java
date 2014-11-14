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
name|splunk
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
name|DriverVersion
import|;
end_import

begin_comment
comment|/**  * Version information for Calcite JDBC Driver for Splunk.  */
end_comment

begin_class
class|class
name|SplunkDriverVersion
extends|extends
name|DriverVersion
block|{
comment|/** Creates a SplunkDriverVersion. */
name|SplunkDriverVersion
parameter_list|()
block|{
name|super
argument_list|(
literal|"Calcite JDBC Driver for Splunk"
argument_list|,
literal|"0.2"
argument_list|,
literal|"Calcite-Splunk"
argument_list|,
literal|"0.2"
argument_list|,
literal|true
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End SplunkDriverVersion.java
end_comment

end_unit

