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
name|rel
operator|.
name|mutable
package|;
end_package

begin_comment
comment|/** Type of {@code MutableRel}. */
end_comment

begin_enum
enum|enum
name|MutableRelType
block|{
name|AGGREGATE
block|,
name|CALC
block|,
name|COLLECT
block|,
name|CORRELATE
block|,
name|EXCHANGE
block|,
name|FILTER
block|,
name|INTERSECT
block|,
name|JOIN
block|,
name|MATCH
block|,
name|MINUS
block|,
name|PROJECT
block|,
name|SAMPLE
block|,
name|SORT
block|,
name|TABLE_FUNCTION_SCAN
block|,
name|TABLE_MODIFY
block|,
name|TABLE_SCAN
block|,
name|UNCOLLECT
block|,
name|UNION
block|,
name|VALUES
block|,
name|WINDOW
block|,
name|HOLDER
block|}
end_enum

begin_comment
comment|// End MutableRelType.java
end_comment

end_unit

