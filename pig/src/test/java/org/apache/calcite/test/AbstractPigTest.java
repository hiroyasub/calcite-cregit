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
name|util
operator|.
name|Sources
import|;
end_import

begin_comment
comment|/**  * Common methods inheritable by all Pig-specific test classes.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractPigTest
block|{
specifier|protected
name|String
name|getFullPathForTestDataFile
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
return|return
name|Sources
operator|.
name|of
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/"
operator|+
name|fileName
argument_list|)
argument_list|)
operator|.
name|file
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
return|;
block|}
block|}
end_class

end_unit

