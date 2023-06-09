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
package|;
end_package

begin_comment
comment|/**  * SqlNullSemantics defines the possible comparison rules for values which might  * be null. In SQL (and internal plans used to process SQL) different rules are  * used depending on the context.  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlNullSemantics
block|{
comment|/**    * Predicate semantics: e.g. in the expression (WHERE X=5), if X is NULL,    * the comparison result is unknown, and so a filter used to evaluate the    * WHERE clause rejects the row.    */
name|NULL_MATCHES_NOTHING
block|,
comment|/**    * GROUP BY key semantics: e.g. in the expression (GROUP BY A,B), the key    * (null,5) is treated as equal to another key (null,5).    */
name|NULL_MATCHES_NULL
block|,
comment|/**    * Wildcard semantics: logically, this is not present in any SQL construct.    * However, it is required internally, for example to rewrite NOT IN to NOT    * EXISTS; when we negate a predicate, we invert the null semantics, so    * NULL_MATCHES_NOTHING must become NULL_MATCHES_ANYTHING.    */
name|NULL_MATCHES_ANYTHING
block|}
end_enum

end_unit

