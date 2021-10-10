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
name|sql
operator|.
name|Timestamp
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Department with inception date model.  */
end_comment

begin_class
specifier|public
class|class
name|DepartmentPlus
extends|extends
name|Department
block|{
specifier|public
specifier|final
name|Timestamp
name|inceptionDate
decl_stmt|;
specifier|public
name|DepartmentPlus
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
parameter_list|,
name|Timestamp
name|inceptionDate
parameter_list|)
block|{
name|super
argument_list|(
name|deptno
argument_list|,
name|name
argument_list|,
name|employees
argument_list|,
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|inceptionDate
operator|=
name|inceptionDate
expr_stmt|;
block|}
block|}
end_class

end_unit
