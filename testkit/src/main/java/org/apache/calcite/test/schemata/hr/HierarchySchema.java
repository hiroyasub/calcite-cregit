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
name|test
operator|.
name|schemata
operator|.
name|hr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_comment
comment|/**  * A Schema representing a hierarchy of employees.  *  *<p>The Schema is meant to be used with  * {@link org.apache.calcite.adapter.java.ReflectiveSchema} thus all  * fields, and methods, should be public.  */
end_comment

begin_class
specifier|public
class|class
name|HierarchySchema
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"HierarchySchema"
return|;
block|}
specifier|public
specifier|final
name|Employee
index|[]
name|emps
init|=
block|{
operator|new
name|Employee
argument_list|(
literal|1
argument_list|,
literal|10
argument_list|,
literal|"Emp1"
argument_list|,
literal|10000
argument_list|,
literal|1000
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|2
argument_list|,
literal|10
argument_list|,
literal|"Emp2"
argument_list|,
literal|8000
argument_list|,
literal|500
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|3
argument_list|,
literal|10
argument_list|,
literal|"Emp3"
argument_list|,
literal|7000
argument_list|,
literal|null
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|4
argument_list|,
literal|10
argument_list|,
literal|"Emp4"
argument_list|,
literal|8000
argument_list|,
literal|500
argument_list|)
block|,
operator|new
name|Employee
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|,
literal|"Emp5"
argument_list|,
literal|7000
argument_list|,
literal|null
argument_list|)
block|,   }
decl_stmt|;
specifier|public
specifier|final
name|Department
index|[]
name|depts
init|=
block|{
operator|new
name|Department
argument_list|(
literal|10
argument_list|,
literal|"Dept"
argument_list|,
name|Arrays
operator|.
name|asList
argument_list|(
name|emps
index|[
literal|0
index|]
argument_list|,
name|emps
index|[
literal|1
index|]
argument_list|,
name|emps
index|[
literal|2
index|]
argument_list|,
name|emps
index|[
literal|3
index|]
argument_list|,
name|emps
index|[
literal|4
index|]
argument_list|)
argument_list|,
operator|new
name|Location
argument_list|(
operator|-
literal|122
argument_list|,
literal|38
argument_list|)
argument_list|)
block|,   }
decl_stmt|;
comment|//      Emp1
comment|//      /  \
comment|//    Emp2  Emp4
comment|//    /  \
comment|// Emp3   Emp5
specifier|public
specifier|final
name|Hierarchy
index|[]
name|hierarchies
init|=
block|{
operator|new
name|Hierarchy
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
block|,
operator|new
name|Hierarchy
argument_list|(
literal|2
argument_list|,
literal|3
argument_list|)
block|,
operator|new
name|Hierarchy
argument_list|(
literal|2
argument_list|,
literal|5
argument_list|)
block|,
operator|new
name|Hierarchy
argument_list|(
literal|1
argument_list|,
literal|4
argument_list|)
block|,   }
decl_stmt|;
comment|/** Hierarchy representing manager - subordinate. */
specifier|public
specifier|static
class|class
name|Hierarchy
block|{
specifier|public
specifier|final
name|int
name|managerid
decl_stmt|;
specifier|public
specifier|final
name|int
name|subordinateid
decl_stmt|;
specifier|public
name|Hierarchy
parameter_list|(
name|int
name|managerid
parameter_list|,
name|int
name|subordinateid
parameter_list|)
block|{
name|this
operator|.
name|managerid
operator|=
name|managerid
expr_stmt|;
name|this
operator|.
name|subordinateid
operator|=
name|subordinateid
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Hierarchy [managerid: "
operator|+
name|managerid
operator|+
literal|", subordinateid: "
operator|+
name|subordinateid
operator|+
literal|"]"
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|obj
operator|==
name|this
operator|||
name|obj
operator|instanceof
name|Hierarchy
operator|&&
name|managerid
operator|==
operator|(
operator|(
name|Hierarchy
operator|)
name|obj
operator|)
operator|.
name|managerid
operator|&&
name|subordinateid
operator|==
operator|(
operator|(
name|Hierarchy
operator|)
name|obj
operator|)
operator|.
name|subordinateid
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|managerid
argument_list|,
name|subordinateid
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
