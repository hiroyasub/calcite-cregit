begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/** * Map where each key can have one or more values. */
end_comment

begin_interface
interface|interface
name|MultiMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Map
argument_list|<
name|K
argument_list|,
name|Enumerable
argument_list|<
name|V
argument_list|>
argument_list|>
block|{  }
end_interface

begin_comment
comment|// End MultiMap.java
end_comment

end_unit

