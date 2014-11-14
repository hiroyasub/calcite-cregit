begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|avatica
package|;
end_package

begin_comment
comment|/** Syntax for quoting identifiers in SQL statements. */
end_comment

begin_enum
specifier|public
enum|enum
name|Quoting
block|{
comment|/** Quote identifiers in double-quotes. For example, {@code "my id"}. */
name|DOUBLE_QUOTE
argument_list|(
literal|"\""
argument_list|)
block|,
comment|/** Quote identifiers in back-quotes. For example, {@code `my id`}. */
name|BACK_TICK
argument_list|(
literal|"`"
argument_list|)
block|,
comment|/** Quote identifiers in brackets. For example, {@code [my id]}. */
name|BRACKET
argument_list|(
literal|"["
argument_list|)
block|;
specifier|public
name|String
name|string
decl_stmt|;
name|Quoting
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|this
operator|.
name|string
operator|=
name|string
expr_stmt|;
block|}
block|}
end_enum

begin_comment
comment|// End Quoting.java
end_comment

end_unit

