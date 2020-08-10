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
name|List
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
comment|/**  * Department model.  */
end_comment

begin_class
specifier|public
class|class
name|Department
block|{
specifier|public
specifier|final
name|int
name|deptno
decl_stmt|;
specifier|public
specifier|final
name|String
name|name
decl_stmt|;
annotation|@
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|adapter
operator|.
name|java
operator|.
name|Array
argument_list|(
name|component
operator|=
name|Employee
operator|.
name|class
argument_list|)
specifier|public
specifier|final
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
decl_stmt|;
specifier|public
specifier|final
name|Location
name|location
decl_stmt|;
specifier|public
name|Department
parameter_list|(
name|int
name|deptno
parameter_list|,
name|String
name|name
parameter_list|,
name|List
argument_list|<
name|Employee
argument_list|>
name|employees
parameter_list|,
name|Location
name|location
parameter_list|)
block|{
name|this
operator|.
name|deptno
operator|=
name|deptno
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|employees
operator|=
name|employees
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|location
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
literal|"Department [deptno: "
operator|+
name|deptno
operator|+
literal|", name: "
operator|+
name|name
operator|+
literal|", employees: "
operator|+
name|employees
operator|+
literal|", location: "
operator|+
name|location
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
name|Department
operator|&&
name|deptno
operator|==
operator|(
operator|(
name|Department
operator|)
name|obj
operator|)
operator|.
name|deptno
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
name|deptno
argument_list|)
return|;
block|}
block|}
end_class

end_unit

