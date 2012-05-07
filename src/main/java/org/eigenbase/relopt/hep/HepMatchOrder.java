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
name|relopt
operator|.
name|hep
package|;
end_package

begin_comment
comment|/**  * HepMatchOrder specifies the order of graph traversal when looking for rule  * matches.  *  * @author John V. Sichi  * @version $Id$  */
end_comment

begin_enum
specifier|public
enum|enum
name|HepMatchOrder
block|{
comment|/**      * Match in arbitrary order. This is the default because it is the most      * efficient, and most rules don't care about order.      */
name|ARBITRARY
block|,
comment|/**      * Match from leaves up. A match attempt at a descendant precedes all match      * attempts at its ancestors.      */
name|BOTTOM_UP
block|,
comment|/**      * Match from root down. A match attempt at an ancestor always precedes all      * match attempts at its descendants.      */
name|TOP_DOWN
block|}
end_enum

begin_comment
comment|// End HepMatchOrder.java
end_comment

end_unit

