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
name|jdbc
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
comment|/**  * Provides information on the current Calcite version.  */
end_comment

begin_class
class|class
name|CalciteDriverVersion
block|{
specifier|static
specifier|final
name|DriverVersion
name|INSTANCE
init|=
operator|new
name|DriverVersion
argument_list|(
literal|"Calcite JDBC Driver"
argument_list|,
literal|"1.0.0-unprocessed-SNAPSHOT"
comment|/* :version */
argument_list|,
literal|"Calcite"
argument_list|,
literal|"1.0.0-unprocessed-SNAPSHOT"
comment|/* :version */
argument_list|,
literal|true
argument_list|,
literal|1
comment|/* :major */
argument_list|,
literal|0
comment|/* :minor */
argument_list|,
literal|1
comment|/* :major */
argument_list|,
literal|0
comment|/* :minor */
argument_list|)
decl_stmt|;
specifier|private
name|CalciteDriverVersion
parameter_list|()
block|{
block|}
block|}
end_class

end_unit

