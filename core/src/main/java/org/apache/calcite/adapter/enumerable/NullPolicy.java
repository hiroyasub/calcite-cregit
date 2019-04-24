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
name|adapter
operator|.
name|enumerable
package|;
end_package

begin_comment
comment|/**  * Describes when a function/operator will return null.  *  *<p>STRICT and ANY are similar. STRICT says f(a0, a1) will NEVER return  * null if a0 and a1 are not null. This means that we can check whether f  * returns null just by checking its arguments. Use STRICT in preference to  * ANY whenever possible.</p>  */
end_comment

begin_enum
specifier|public
enum|enum
name|NullPolicy
block|{
comment|/** Returns null if and only if one of the arguments are null. */
name|STRICT
block|,
comment|/** Returns null if one of the arguments is null, and possibly other times. */
name|SEMI_STRICT
block|,
comment|/** If any of the arguments are null, return null. */
name|ANY
block|,
comment|/** If the first argument is null, return null. */
name|ARG0
block|,
comment|/** If any of the arguments are false, result is false; else if any    * arguments are null, result is null; else true. */
name|AND
block|,
comment|/** If any of the arguments are true, result is true; else if any    * arguments are null, result is null; else false. */
name|OR
block|,
comment|/** If any argument is true, result is false; else if any argument is null,    * result is null; else true. */
name|NOT
block|,
name|NONE
block|}
end_enum

begin_comment
comment|// End NullPolicy.java
end_comment

end_unit

