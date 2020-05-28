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
name|chinook
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
name|adapter
operator|.
name|java
operator|.
name|AbstractQueryableTable
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
name|apache
operator|.
name|calcite
operator|.
name|rel
operator|.
name|type
operator|.
name|RelDataTypeFactory
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|schema
operator|.
name|TableFactory
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
name|sql
operator|.
name|type
operator|.
name|SqlTypeName
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ContiguousSet
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|DiscreteDomain
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Range
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
comment|/**  * Factory for the table of albums preferred by the current user.  */
end_comment

begin_class
specifier|public
class|class
name|PreferredAlbumsTableFactory
implements|implements
name|TableFactory
argument_list|<
name|AbstractQueryableTable
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Integer
index|[]
name|SPECIFIC_USER_PREFERRED_ALBUMS
init|=
block|{
literal|4
block|,
literal|56
block|,
literal|154
block|,
literal|220
block|,
literal|321
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|FIRST_ID
init|=
literal|1
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|LAST_ID
init|=
literal|347
decl_stmt|;
annotation|@
name|Override
specifier|public
name|AbstractQueryableTable
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
block|{
return|return
operator|new
name|AbstractQueryableTable
argument_list|(
name|Integer
operator|.
name|class
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|RelDataType
name|getRowType
parameter_list|(
name|RelDataTypeFactory
name|typeFactory
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|builder
argument_list|()
operator|.
name|add
argument_list|(
literal|"ID"
argument_list|,
name|SqlTypeName
operator|.
name|INTEGER
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Queryable
argument_list|<
name|Integer
argument_list|>
name|asQueryable
parameter_list|(
name|QueryProvider
name|qp
parameter_list|,
name|SchemaPlus
name|sp
parameter_list|,
name|String
name|string
parameter_list|)
block|{
return|return
name|fetchPreferredAlbums
argument_list|()
return|;
block|}
block|}
return|;
block|}
specifier|private
name|Queryable
argument_list|<
name|Integer
argument_list|>
name|fetchPreferredAlbums
parameter_list|()
block|{
if|if
condition|(
name|EnvironmentFairy
operator|.
name|getUser
argument_list|()
operator|==
name|EnvironmentFairy
operator|.
name|User
operator|.
name|SPECIFIC_USER
condition|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|SPECIFIC_USER_PREFERRED_ALBUMS
argument_list|)
operator|.
name|asQueryable
argument_list|()
return|;
block|}
else|else
block|{
specifier|final
name|ContiguousSet
argument_list|<
name|Integer
argument_list|>
name|set
init|=
name|ContiguousSet
operator|.
name|create
argument_list|(
name|Range
operator|.
name|closed
argument_list|(
name|FIRST_ID
argument_list|,
name|LAST_ID
argument_list|)
argument_list|,
name|DiscreteDomain
operator|.
name|integers
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|set
argument_list|)
operator|.
name|asQueryable
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

