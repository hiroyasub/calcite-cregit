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
name|runtime
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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

begin_comment
comment|/** SQL language feature. Expressed as the exception that would be thrown if it  * were used while disabled. */
end_comment

begin_class
specifier|public
class|class
name|Feature
extends|extends
name|Resources
operator|.
name|ExInstWithCause
argument_list|<
name|CalciteContextException
argument_list|>
block|{
specifier|public
name|Feature
parameter_list|(
name|String
name|base
parameter_list|,
name|Locale
name|locale
parameter_list|,
name|Method
name|method
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|base
argument_list|,
name|locale
argument_list|,
name|method
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

