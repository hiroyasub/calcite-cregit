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
name|linq4j
package|;
end_package

begin_comment
comment|/**  * Abstract implementation of the {@link Queryable} interface that implements  * the extension methods.  *  *<p>It is helpful to derive from this class if you are implementing  * {@code Queryable}, because {@code Queryable} has so many extension methods,  * but it is not required.</p>  *  * @param<T> Element type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|DefaultQueryable
argument_list|<
name|T
argument_list|>
implements|implements
name|Queryable
argument_list|<
name|T
argument_list|>
block|{ }
end_class

end_unit

