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
name|sql
operator|.
name|validate
package|;
end_package

begin_comment
comment|/**  * Enumeration of built-in SQL compatibility modes.  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlConformanceEnum
implements|implements
name|SqlConformance
block|{
comment|/** Calcite's default SQL behavior. */
name|DEFAULT
block|,
comment|/** Conformance value that instructs Calcite to use SQL semantics strictly    * consistent with the SQL:92 standard. */
name|STRICT_92
block|,
comment|/** Conformance value that instructs Calcite to use SQL semantics strictly    * consistent with the SQL:99 standard. */
name|STRICT_99
block|,
comment|/** Conformance value that instructs Calcite to use SQL semantics    * consistent with the SQL:99 standard, but ignoring its more    * inconvenient or controversial dicta. */
name|PRAGMATIC_99
block|,
comment|/** Conformance value that instructs Calcite to use SQL semantics    * consistent with Oracle version 10. */
name|ORACLE_10
block|,
comment|/** Conformance value that instructs Calcite to use SQL semantics strictly    * consistent with the SQL:2003 standard. */
name|STRICT_2003
block|,
comment|/** Conformance value that instructs Calcite to use SQL semantics    * consistent with the SQL:2003 standard, but ignoring its more    * inconvenient or controversial dicta. */
name|PRAGMATIC_2003
block|;
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
specifier|public
name|boolean
name|isSortByAliasObscures
parameter_list|()
block|{
return|return
name|this
operator|==
name|SqlConformanceEnum
operator|.
name|STRICT_92
return|;
block|}
specifier|public
name|boolean
name|isFromRequired
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|ORACLE_10
case|:
case|case
name|STRICT_92
case|:
case|case
name|STRICT_99
case|:
case|case
name|STRICT_2003
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
specifier|public
name|boolean
name|isBangEqualAllowed
parameter_list|()
block|{
switch|switch
condition|(
name|this
condition|)
block|{
case|case
name|ORACLE_10
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
block|}
end_enum

begin_comment
comment|// End SqlConformanceEnum.java
end_comment

end_unit

