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
name|config
package|;
end_package

begin_comment
comment|/** Strategy for how NULL values are to be sorted if NULLS FIRST or NULLS LAST  * are not specified in an item in the ORDER BY clause. */
end_comment

begin_enum
specifier|public
enum|enum
name|NullCollation
block|{
comment|/** Nulls first for DESC, nulls last for ASC. */
name|HIGH
block|,
comment|/** Nulls last for DESC, nulls first for ASC. */
name|LOW
block|,
comment|/** Nulls first for DESC and ASC. */
name|FIRST
block|,
comment|/** Nulls last for DESC and ASC. */
name|LAST
block|;
comment|/** Returns whether NULL values should appear last.    *    * @param desc Whether sort is descending    */
specifier|public
name|boolean
name|last
parameter_list|(
name|boolean
name|desc
parameter_list|)
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|FIRST
case|:
return|return
literal|false
return|;
case|case
name|LAST
case|:
return|return
literal|true
return|;
case|case
name|LOW
case|:
return|return
name|desc
return|;
case|case
name|HIGH
case|:
default|default:
return|return
operator|!
name|desc
return|;
block|}
block|}
block|}
end_enum

begin_comment
comment|// End NullCollation.java
end_comment

end_unit

