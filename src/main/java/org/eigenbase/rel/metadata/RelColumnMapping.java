begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|rel
operator|.
name|metadata
package|;
end_package

begin_comment
comment|/**  * RelColumnMapping records a mapping from an input column of a RelNode to one  * of its output columns.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_class
specifier|public
class|class
name|RelColumnMapping
block|{
comment|//~ Instance fields --------------------------------------------------------
comment|/**      * 0-based ordinal of mapped output column.      */
specifier|public
name|int
name|iOutputColumn
decl_stmt|;
comment|/**      * 0-based ordinal of mapped input rel.      */
specifier|public
name|int
name|iInputRel
decl_stmt|;
comment|/**      * 0-based ordinal of mapped column within input rel.      */
specifier|public
name|int
name|iInputColumn
decl_stmt|;
comment|/**      * Whether the column mapping transforms the input.      */
specifier|public
name|boolean
name|isDerived
decl_stmt|;
block|}
end_class

begin_comment
comment|// End RelColumnMapping.java
end_comment

end_unit

