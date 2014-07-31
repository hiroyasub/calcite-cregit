begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to the Apache Software Foundation (ASF) under one or more // contributor license agreements.  See the NOTICE file distributed with // this work for additional information regarding copyright ownership. // The ASF licenses this file to you under the Apache License, Version 2.0 // (the "License"); you may not use this file except in compliance with // the License.  You may obtain a copy of the License at // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|org
operator|.
name|eigenbase
operator|.
name|sql2rel
package|;
end_package

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|rex
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eigenbase
operator|.
name|sql
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * SubqueryConverter provides the interface for classes that convert subqueries  * into equivalent expressions.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SubqueryConverter
block|{
comment|//~ Methods ----------------------------------------------------------------
comment|/**    * @return true if the subquery can be converted    */
name|boolean
name|canConvertSubquery
parameter_list|()
function_decl|;
comment|/**    * Converts the subquery to an equivalent expression.    *    * @param subquery        the SqlNode tree corresponding to a subquery    * @param parentConverter sqlToRelConverter of the parent query    * @param isExists        whether the subquery is part of an EXISTS expression    * @param isExplain       whether the subquery is part of an EXPLAIN PLAN    *                        statement    * @return the equivalent expression or null if the subquery couldn't be    * converted    */
name|RexNode
name|convertSubquery
parameter_list|(
name|SqlCall
name|subquery
parameter_list|,
name|SqlToRelConverter
name|parentConverter
parameter_list|,
name|boolean
name|isExists
parameter_list|,
name|boolean
name|isExplain
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// End SubqueryConverter.java
end_comment

end_unit

