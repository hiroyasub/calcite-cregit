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
name|dialect
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|sql
operator|.
name|SqlDialect
import|;
end_import

begin_comment
comment|/**  * A<code>SqlDialect</code> implementation that produces SQL that can be parsed  * by Apache Calcite.  */
end_comment

begin_class
specifier|public
class|class
name|CalciteSqlDialect
extends|extends
name|SqlDialect
block|{
comment|/**    * A dialect useful for generating SQL which can be parsed by the Apache    * Calcite parser, in particular quoting literals and identifiers. If you    * want a dialect that knows the full capabilities of the database, create    * one from a connection.    */
specifier|public
specifier|static
specifier|final
name|SqlDialect
name|DEFAULT
init|=
operator|new
name|CalciteSqlDialect
argument_list|(
name|emptyContext
argument_list|()
operator|.
name|withDatabaseProduct
argument_list|(
name|DatabaseProduct
operator|.
name|CALCITE
argument_list|)
operator|.
name|withIdentifierQuoteString
argument_list|(
literal|"\""
argument_list|)
argument_list|)
decl_stmt|;
comment|/** Creates a CalciteSqlDialect. */
specifier|public
name|CalciteSqlDialect
parameter_list|(
name|Context
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End CalciteSqlDialect.java
end_comment

end_unit
