begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to DynamoBI Corporation (DynamoBI) under one // or more contributor license agreements.  See the NOTICE file // distributed with this work for additional information // regarding copyright ownership.  DynamoBI licenses this file // to you under the Apache License, Version 2.0 (the // "License"); you may not use this file except in compliance // with the License.  You may obtain a copy of the License at  //   http://www.apache.org/licenses/LICENSE-2.0  // Unless required by applicable law or agreed to in writing, // software distributed under the License is distributed on an // "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY // KIND, either express or implied.  See the License for the // specific language governing permissions and limitations // under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *<code>SqlFunctionCategory</codde> is an enumeration of the categories of  * SQL-invoked routines.  *  * @version $Id$  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlFunctionCategory
block|{
name|String
argument_list|(
literal|"STRING"
argument_list|,
literal|"String function"
argument_list|)
block|,
name|Numeric
argument_list|(
literal|"NUMERIC"
argument_list|,
literal|"Numeric function"
argument_list|)
block|,
name|TimeDate
argument_list|(
literal|"TIMEDATE"
argument_list|,
literal|"Time and date function"
argument_list|)
block|,
name|System
argument_list|(
literal|"SYSTEM"
argument_list|,
literal|"System function"
argument_list|)
block|,
name|UserDefinedFunction
argument_list|(
literal|"UDF"
argument_list|,
literal|"User-defined function"
argument_list|)
block|,
name|UserDefinedProcedure
argument_list|(
literal|"UDP"
argument_list|,
literal|"User-defined procedure"
argument_list|)
block|,
name|UserDefinedConstructor
argument_list|(
literal|"UDC"
argument_list|,
literal|"User-defined constructor"
argument_list|)
block|,
name|UserDefinedSpecificFunction
argument_list|(
literal|"UDF_SPECIFIC"
argument_list|,
literal|"User-defined function with SPECIFIC name"
argument_list|)
block|;
name|SqlFunctionCategory
parameter_list|(
name|String
name|abbrev
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|Util
operator|.
name|discard
argument_list|(
name|abbrev
argument_list|)
expr_stmt|;
name|Util
operator|.
name|discard
argument_list|(
name|description
argument_list|)
expr_stmt|;
block|}
block|}
end_enum

begin_comment
comment|// End SqlFunctionCategory.java
end_comment

end_unit

