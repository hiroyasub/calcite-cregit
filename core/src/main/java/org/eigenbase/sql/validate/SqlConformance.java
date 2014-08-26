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
name|sql
operator|.
name|validate
package|;
end_package

begin_comment
comment|/**  * Enumeration of valid SQL compatiblity modes.  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlConformance
block|{
name|DEFAULT
block|,
name|STRICT_92
block|,
name|STRICT_99
block|,
name|PRAGMATIC_99
block|,
name|ORACLE_10
block|,
name|STRICT_2003
block|,
name|PRAGMATIC_2003
block|;
comment|/**    * Whether 'order by 2' is interpreted to mean 'sort by the 2nd column in    * the select list'.    */
specifier|public
name|boolean
name|isSortByOrdinal
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|DEFAULT
case|:
case|case
name|ORACLE_10
case|:
case|case
name|STRICT_92
case|:
case|case
name|PRAGMATIC_99
case|:
case|case
name|PRAGMATIC_2003
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Whether 'order by x' is interpreted to mean 'sort by the select list item    * whose alias is x' even if there is a column called x.    */
specifier|public
name|boolean
name|isSortByAlias
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|DEFAULT
case|:
case|case
name|ORACLE_10
case|:
case|case
name|STRICT_92
case|:
return|return
literal|true
return|;
default|default:
return|return
literal|false
return|;
block|}
block|}
comment|/**    * Whether "empno" is invalid in "select empno as x from emp order by empno"    * because the alias "x" obscures it.    */
specifier|public
name|boolean
name|isSortByAliasObscures
parameter_list|()
block|{
return|return
name|this
operator|==
name|SqlConformance
operator|.
name|STRICT_92
return|;
block|}
block|}
end_enum

begin_comment
comment|// End SqlConformance.java
end_comment

end_unit

