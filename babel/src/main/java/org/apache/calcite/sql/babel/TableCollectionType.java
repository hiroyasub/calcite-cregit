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
name|babel
package|;
end_package

begin_comment
comment|/**  * Enumerates the collection type of a table: {@code MULTISET} allows duplicates  * and {@code SET} does not.  *  *<p>This feature is supported in Teradata, which originally required rows in a  * table to be unique, and later added the {@code MULTISET} keyword to  * its {@code CREATE TABLE} command to allow the duplicate rows.  *  *<p>In other databases and in the SQL standard, {@code MULTISET} is the only  * supported option, so there is no explicit syntax.  */
end_comment

begin_enum
specifier|public
enum|enum
name|TableCollectionType
block|{
comment|/**    * Table collection type is not specified.    *    *<p>Defaults to {@code MULTISET} in ANSI mode,    * and {@code SET} in Teradata mode.    */
name|UNSPECIFIED
block|,
comment|/**    * Duplicate rows are not permitted.    */
name|SET
block|,
comment|/**    * Duplicate rows are permitted, in compliance with the ANSI SQL:2011 standard.    */
name|MULTISET
block|, }
end_enum

end_unit

