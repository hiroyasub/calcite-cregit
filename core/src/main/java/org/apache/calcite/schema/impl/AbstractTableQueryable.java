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
operator|.
name|impl
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
name|linq4j
operator|.
name|AbstractQueryable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Linq4j
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|QueryProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|Queryable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|linq4j
operator|.
name|tree
operator|.
name|Expression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|QueryableTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|SchemaPlus
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Abstract implementation of {@link org.apache.calcite.linq4j.Queryable} for  * {@link QueryableTable}.  *  *<p>Not to be confused with  * {@link org.apache.calcite.adapter.java.AbstractQueryableTable}.</p>  *  * @param<T> element type  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractTableQueryable
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractQueryable
argument_list|<
name|T
argument_list|>
block|{
specifier|public
specifier|final
name|QueryProvider
name|queryProvider
decl_stmt|;
specifier|public
specifier|final
name|SchemaPlus
name|schema
decl_stmt|;
specifier|public
specifier|final
name|QueryableTable
name|table
decl_stmt|;
specifier|public
specifier|final
name|String
name|tableName
decl_stmt|;
specifier|public
name|AbstractTableQueryable
parameter_list|(
name|QueryProvider
name|queryProvider
parameter_list|,
name|SchemaPlus
name|schema
parameter_list|,
name|QueryableTable
name|table
parameter_list|,
name|String
name|tableName
parameter_list|)
block|{
name|this
operator|.
name|queryProvider
operator|=
name|queryProvider
expr_stmt|;
name|this
operator|.
name|schema
operator|=
name|schema
expr_stmt|;
name|this
operator|.
name|table
operator|=
name|table
expr_stmt|;
name|this
operator|.
name|tableName
operator|=
name|tableName
expr_stmt|;
block|}
specifier|public
name|Expression
name|getExpression
parameter_list|()
block|{
return|return
name|table
operator|.
name|getExpression
argument_list|(
name|schema
argument_list|,
name|tableName
argument_list|,
name|Queryable
operator|.
name|class
argument_list|)
return|;
block|}
specifier|public
name|QueryProvider
name|getProvider
parameter_list|()
block|{
return|return
name|queryProvider
return|;
block|}
specifier|public
name|Type
name|getElementType
parameter_list|()
block|{
return|return
name|table
operator|.
name|getElementType
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Linq4j
operator|.
name|enumeratorIterator
argument_list|(
name|enumerator
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End AbstractTableQueryable.java
end_comment

end_unit

