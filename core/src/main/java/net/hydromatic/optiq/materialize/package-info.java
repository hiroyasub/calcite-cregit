begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_comment
comment|/**  * Management of materialized query results.  *  *<p>An actor ({@link MaterializeActor} maintains the state of all  * materializations in the system and is wrapped in a service  * ({@link MaterializationService}) for access from other parts of the system.  *  *<p>Optimizer rules allow Optiq to rewrite queries using materializations, if  * they are valid (that is, contain the same result as executing their defining  * query) and lower cost.  *  *<p>In future, the actor may manage the process of updating materializations,  * instantiating materializations from the intermediate results of queries, and  * recognize what materializations would be useful based on actual query load.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|materialize
package|;
end_package

begin_comment
comment|// End package-info.java
end_comment

end_unit

