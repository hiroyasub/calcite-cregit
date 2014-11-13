begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to you under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|com
operator|.
name|example
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|Linq4j
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|linq4j
operator|.
name|function
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Example using linq4j to query in-memory collections.  */
end_comment

begin_class
specifier|public
class|class
name|Linq4jExample
block|{
specifier|private
name|Linq4jExample
parameter_list|()
block|{
block|}
comment|/** Employee. */
specifier|public
specifier|static
class|class
name|Employee
block|{
specifier|public
specifier|final
name|int
name|empno
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|public
name|Employee
parameter_list|(
name|int
name|empno
parameter_list|,
name|String
name|name
parameter_list|,
name|int
name|deptno
parameter_list|)
block|{
name|this
operator|.
name|empno
operator|=
name|empno
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Employee(name: "
operator|+
name|name
operator|+
literal|", deptno:"
operator|+
name|deptno
operator|+
literal|")"
return|;
block|}
block|}
specifier|public
specifier|static
specifier|final
name|Employee
index|[]
name|EMPS
init|=
block|{
operator|new
name|Employee
argument_list|(
literal|100
argument_list|,
literal|"Fred"
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|110
argument_list|,
literal|"Bill"
argument_list|,
literal|30
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|120
argument_list|,
literal|"Eric"
argument_list|,
literal|10
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|130
argument_list|,
literal|"Janet"
argument_list|,
literal|10
argument_list|)
block|,   }
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|>
name|EMP_DEPTNO_SELECTOR
init|=
operator|new
name|Function1
argument_list|<
name|Employee
argument_list|,
name|Integer
argument_list|>
argument_list|()
block|{
specifier|public
name|Integer
name|apply
parameter_list|(
name|Employee
name|employee
parameter_list|)
block|{
return|return
name|employee
operator|.
name|deptno
return|;
block|}
block|}
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|String
name|s
init|=
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|EMPS
argument_list|)
operator|.
name|groupBy
argument_list|(
name|EMP_DEPTNO_SELECTOR
argument_list|,
operator|new
name|Function0
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
argument_list|,
operator|new
name|Function2
argument_list|<
name|String
argument_list|,
name|Employee
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|String
name|v1
parameter_list|,
name|Employee
name|e0
parameter_list|)
block|{
return|return
name|v1
operator|==
literal|null
condition|?
name|e0
operator|.
name|name
else|:
operator|(
name|v1
operator|+
literal|"+"
operator|+
name|e0
operator|.
name|name
operator|)
return|;
block|}
block|}
argument_list|,
operator|new
name|Function2
argument_list|<
name|Integer
argument_list|,
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
specifier|public
name|String
name|apply
parameter_list|(
name|Integer
name|v1
parameter_list|,
name|String
name|v2
parameter_list|)
block|{
return|return
name|v1
operator|+
literal|": "
operator|+
name|v2
return|;
block|}
block|}
argument_list|)
operator|.
name|orderBy
argument_list|(
name|Functions
operator|.
expr|<
name|String
operator|>
name|identitySelector
argument_list|()
argument_list|)
operator|.
name|toList
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
assert|assert
name|s
operator|.
name|equals
argument_list|(
literal|"[10: Fred+Eric+Janet, 30: Bill]"
argument_list|)
assert|;
block|}
block|}
end_class

begin_comment
comment|// End Linq4jExample.java
end_comment

end_unit

