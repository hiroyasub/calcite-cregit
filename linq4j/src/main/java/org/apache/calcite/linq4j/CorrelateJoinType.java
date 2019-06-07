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
comment|/**  * Specifies the type of correlation operation: inner, left, semi, or anti.  * @deprecated Use {@link JoinType}  */
end_comment

begin_enum
annotation|@
name|Deprecated
comment|// to be removed before 1.21
specifier|public
enum|enum
name|CorrelateJoinType
block|{
comment|/**    * Inner join    */
name|INNER
block|,
comment|/**    * Left-outer join    */
name|LEFT
block|,
comment|/**    * Semi-join.    *    *<p>Similar to {@code from A ... where a in (select b from B ...)}</p>    */
name|SEMI
block|,
comment|/**    * Anti-join.    *    *<p>Similar to {@code from A ... where a NOT in (select b from B ...)}    *    *<p>Note: if B.b is nullable and B has nulls, no rows must be returned.    */
name|ANTI
block|;
comment|/** Transforms this CorrelateJoinType to JoinType. **/
specifier|public
name|JoinType
name|toJoinType
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|INNER
case|:
return|return
name|JoinType
operator|.
name|INNER
return|;
case|case
name|LEFT
case|:
return|return
name|JoinType
operator|.
name|LEFT
return|;
case|case
name|SEMI
case|:
return|return
name|JoinType
operator|.
name|SEMI
return|;
case|case
name|ANTI
case|:
return|return
name|JoinType
operator|.
name|ANTI
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unable to convert "
operator|+
name|this
operator|+
literal|" to JoinType"
argument_list|)
throw|;
block|}
block|}
end_enum

begin_comment
comment|// End CorrelateJoinType.java
end_comment

end_unit

