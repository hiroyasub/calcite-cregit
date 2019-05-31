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
comment|/**  * Enumeration of join types.  */
end_comment

begin_enum
specifier|public
enum|enum
name|JoinType
block|{
comment|/**    * Inner join.    */
name|INNER
block|,
comment|/**    * Left-outer join.    */
name|LEFT
block|,
comment|/**    * Right-outer join.    */
name|RIGHT
block|,
comment|/**    * Full-outer join.    */
name|FULL
block|,
comment|/**    * Semi-join.    *    *<p>For example, {@code EMP semi-join DEPT} finds all {@code EMP} records    * that have a corresponding {@code DEPT} record:    *    *<blockquote><pre>    * SELECT * FROM EMP    * WHERE EXISTS (SELECT 1 FROM DEPT    *     WHERE DEPT.DEPTNO = EMP.DEPTNO)</pre>    *</blockquote>    */
name|SEMI
block|,
comment|/**    * Anti-join (also known as Anti-semi-join).    *    *<p>For example, {@code EMP anti-join DEPT} finds all {@code EMP} records    * that do not have a corresponding {@code DEPT} record:    *    *<blockquote><pre>    * SELECT * FROM EMP    * WHERE NOT EXISTS (SELECT 1 FROM DEPT    *     WHERE DEPT.DEPTNO = EMP.DEPTNO)</pre>    *</blockquote>    */
name|ANTI
block|;
comment|/**    * Returns whether a join of this type may generate NULL values on the    * right-hand side.    */
specifier|public
name|boolean
name|generatesNullsOnRight
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|LEFT
operator|)
operator|||
operator|(
name|this
operator|==
name|FULL
operator|)
return|;
block|}
comment|/**    * Returns whether a join of this type may generate NULL values on the    * left-hand side.    */
specifier|public
name|boolean
name|generatesNullsOnLeft
parameter_list|()
block|{
return|return
operator|(
name|this
operator|==
name|RIGHT
operator|)
operator|||
operator|(
name|this
operator|==
name|FULL
operator|)
return|;
block|}
block|}
end_enum

begin_comment
comment|// End JoinType.java
end_comment

end_unit

