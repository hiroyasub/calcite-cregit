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
name|optiq
operator|.
name|rules
operator|.
name|java
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|expressions
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|Aggregation
import|;
end_import

begin_comment
comment|/** Implements a windowed aggregate function. */
end_comment

begin_interface
interface|interface
name|WinAggImplementor
extends|extends
name|AggImplementor
block|{
name|Expression
name|implementResultPlus
parameter_list|(
name|RexToLixTranslator
name|translator
parameter_list|,
name|Aggregation
name|aggregation
parameter_list|,
name|Expression
name|accumulator
parameter_list|,
name|Expression
name|start
parameter_list|,
name|Expression
name|end
parameter_list|,
name|Expression
name|rows
parameter_list|,
name|Expression
name|current
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End WinAggImplementor.java
end_comment

end_unit

