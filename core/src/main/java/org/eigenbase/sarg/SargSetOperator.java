begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sarg
package|;
end_package

begin_comment
comment|/**  * SargSetOperator defines the supported set operators which can be used to  * combine instances of {@link SargExpr}.  */
end_comment

begin_enum
specifier|public
enum|enum
name|SargSetOperator
block|{
comment|/**    * Set intersection over any number of children (no children&rarr; universal    * set).    */
name|INTERSECTION
block|,
comment|/**    * Set union over any number of children (no children&rarr; empty set).    */
name|UNION
block|,
comment|/**    * Set complement over exactly one child.    */
name|COMPLEMENT
block|}
end_enum

begin_comment
comment|// End SargSetOperator.java
end_comment

end_unit

