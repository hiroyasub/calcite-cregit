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
name|schema
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
name|rel
operator|.
name|type
operator|.
name|RelDataType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|checkerframework
operator|.
name|checker
operator|.
name|nullness
operator|.
name|qual
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Factory for {@link Table} objects.  *  *<p>A table factory allows you to include custom tables in a model file.  * For example, here is a model that contains a custom table that generates  * a range of integers.</p>  *  *<blockquote><pre>{  *   version: '1.0',  *   defaultSchema: 'MATH',  *   schemas: [  *     {  *       name: 'MATH',  *       tables: [  *         {  *           name: 'INTEGERS',  *           type: 'custom',  *           factory: 'com.acme.IntegerTable',  *           operand: {  *             start: 3,  *             end: 7,  *             column: 'N'  *           }  *         }  *       ]  *     }  *   ]  * }</pre></blockquote>  *  *<p>Given that schema, the query</p>  *  *<blockquote><pre>SELECT * FROM math.integers</pre></blockquote>  *  *<p>returns</p>  *  *<blockquote><pre>  * +---+  * | N |  * +---+  * | 3 |  * | 4 |  * | 5 |  * | 6 |  * +---+  *</pre></blockquote>  *  *<p>A class that implements TableFactory specified in a schema must have a  * public default constructor.</p>  *  * @param<T> Sub-type of table created by this factory  */
end_comment

begin_interface
specifier|public
interface|interface
name|TableFactory
parameter_list|<
name|T
extends|extends
name|Table
parameter_list|>
block|{
comment|/** Creates a Table.    *    * @param schema Schema this table belongs to    * @param name Name of this table    * @param operand The "operand" JSON property    * @param rowType Row type. Specified if the "columns" JSON property.    * @return created table    */
name|T
name|create
parameter_list|(
name|SchemaPlus
name|schema
parameter_list|,
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|operand
parameter_list|,
annotation|@
name|Nullable
name|RelDataType
name|rowType
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

