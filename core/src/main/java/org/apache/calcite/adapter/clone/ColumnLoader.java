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
name|adapter
operator|.
name|clone
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
name|JavaTypeFactory
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
name|avatica
operator|.
name|ColumnMetaData
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
name|avatica
operator|.
name|util
operator|.
name|DateTimeUtils
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
name|Enumerable
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
name|Ord
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
name|Primitive
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
name|RelDataTypeField
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
name|RelProtoDataType
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
name|util
operator|.
name|Util
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
name|sql
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|sql
operator|.
name|Time
import|;
end_import

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
name|AbstractList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
comment|/**  * Column loader.  *  * @param<T> Element type of source table  */
end_comment

begin_class
class|class
name|ColumnLoader
parameter_list|<
name|T
parameter_list|>
block|{
specifier|static
specifier|final
name|int
index|[]
name|INT_B
init|=
block|{
literal|0x2
block|,
literal|0xC
block|,
literal|0xF0
block|,
literal|0xFF00
block|,
literal|0xFFFF0000
block|}
decl_stmt|;
specifier|static
specifier|final
name|int
index|[]
name|INT_S
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|16
block|}
decl_stmt|;
specifier|static
specifier|final
name|long
index|[]
name|LONG_B
init|=
block|{
literal|0x2
block|,
literal|0xC
block|,
literal|0xF0
block|,
literal|0xFF00
block|,
literal|0xFFFF0000
block|,
literal|0xFFFFFFFF00000000L
block|}
decl_stmt|;
specifier|static
specifier|final
name|int
index|[]
name|LONG_S
init|=
block|{
literal|1
block|,
literal|2
block|,
literal|4
block|,
literal|8
block|,
literal|16
block|,
literal|32
block|}
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|T
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
specifier|final
name|List
argument_list|<
name|ArrayTable
operator|.
name|Column
argument_list|>
name|representationValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|JavaTypeFactory
name|typeFactory
decl_stmt|;
specifier|public
specifier|final
name|int
name|sortField
decl_stmt|;
comment|/** Creates a column loader, and performs the load.    *    * @param typeFactory Type factory    * @param sourceTable Source data    * @param protoRowType Logical row type    * @param repList Physical row types, or null if not known */
name|ColumnLoader
parameter_list|(
name|JavaTypeFactory
name|typeFactory
parameter_list|,
name|Enumerable
argument_list|<
name|T
argument_list|>
name|sourceTable
parameter_list|,
name|RelProtoDataType
name|protoRowType
parameter_list|,
name|List
argument_list|<
name|ColumnMetaData
operator|.
name|Rep
argument_list|>
name|repList
parameter_list|)
block|{
name|this
operator|.
name|typeFactory
operator|=
name|typeFactory
expr_stmt|;
specifier|final
name|RelDataType
name|rowType
init|=
name|protoRowType
operator|.
name|apply
argument_list|(
name|typeFactory
argument_list|)
decl_stmt|;
if|if
condition|(
name|repList
operator|==
literal|null
condition|)
block|{
name|repList
operator|=
name|Collections
operator|.
name|nCopies
argument_list|(
name|rowType
operator|.
name|getFieldCount
argument_list|()
argument_list|,
name|ColumnMetaData
operator|.
name|Rep
operator|.
name|OBJECT
argument_list|)
expr_stmt|;
block|}
name|sourceTable
operator|.
name|into
argument_list|(
name|list
argument_list|)
expr_stmt|;
specifier|final
name|int
index|[]
name|sorts
init|=
block|{
operator|-
literal|1
block|}
decl_stmt|;
name|load
argument_list|(
name|rowType
argument_list|,
name|repList
argument_list|,
name|sorts
argument_list|)
expr_stmt|;
name|this
operator|.
name|sortField
operator|=
name|sorts
index|[
literal|0
index|]
expr_stmt|;
block|}
specifier|static
name|int
name|nextPowerOf2
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|v
operator|--
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|1
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|2
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|4
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|8
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|16
expr_stmt|;
name|v
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
specifier|static
name|long
name|nextPowerOf2
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|v
operator|--
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|1
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|2
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|4
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|8
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|16
expr_stmt|;
name|v
operator||=
name|v
operator|>>>
literal|32
expr_stmt|;
name|v
operator|++
expr_stmt|;
return|return
name|v
return|;
block|}
specifier|static
name|int
name|log2
parameter_list|(
name|int
name|v
parameter_list|)
block|{
name|int
name|r
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|4
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|(
name|v
operator|&
name|INT_B
index|[
name|i
index|]
operator|)
operator|!=
literal|0
condition|)
block|{
name|v
operator|>>=
name|INT_S
index|[
name|i
index|]
expr_stmt|;
name|r
operator||=
name|INT_S
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
specifier|static
name|int
name|log2
parameter_list|(
name|long
name|v
parameter_list|)
block|{
name|int
name|r
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|(
name|v
operator|&
name|LONG_B
index|[
name|i
index|]
operator|)
operator|!=
literal|0
condition|)
block|{
name|v
operator|>>=
name|LONG_S
index|[
name|i
index|]
expr_stmt|;
name|r
operator||=
name|LONG_S
index|[
name|i
index|]
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
specifier|static
name|int
index|[]
name|invert
parameter_list|(
name|int
index|[]
name|targets
parameter_list|)
block|{
specifier|final
name|int
index|[]
name|sources
init|=
operator|new
name|int
index|[
name|targets
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|targets
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sources
index|[
name|targets
index|[
name|i
index|]
index|]
operator|=
name|i
expr_stmt|;
block|}
return|return
name|sources
return|;
block|}
specifier|static
name|boolean
name|isIdentity
parameter_list|(
name|int
index|[]
name|sources
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|sources
index|[
name|i
index|]
operator|!=
name|i
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
specifier|private
name|void
name|load
parameter_list|(
specifier|final
name|RelDataType
name|elementType
parameter_list|,
name|List
argument_list|<
name|ColumnMetaData
operator|.
name|Rep
argument_list|>
name|repList
parameter_list|,
name|int
index|[]
name|sort
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Type
argument_list|>
name|types
init|=
operator|new
name|AbstractList
argument_list|<
name|Type
argument_list|>
argument_list|()
block|{
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fields
init|=
name|elementType
operator|.
name|getFieldList
argument_list|()
decl_stmt|;
specifier|public
name|Type
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|typeFactory
operator|.
name|getJavaClass
argument_list|(
name|fields
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|fields
operator|.
name|size
argument_list|()
return|;
block|}
block|}
decl_stmt|;
name|int
index|[]
name|sources
init|=
literal|null
decl_stmt|;
for|for
control|(
specifier|final
name|Ord
argument_list|<
name|Type
argument_list|>
name|pair
range|:
name|Ord
operator|.
name|zip
argument_list|(
name|types
argument_list|)
control|)
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|List
argument_list|<
name|?
argument_list|>
name|sliceList
init|=
name|types
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
name|list
else|:
operator|new
name|AbstractList
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
specifier|final
name|int
name|slice
init|=
name|pair
operator|.
name|i
decl_stmt|;
specifier|public
name|Object
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
operator|(
name|Object
index|[]
operator|)
name|list
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|)
index|[
name|slice
index|]
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|List
argument_list|<
name|?
argument_list|>
name|list2
init|=
name|wrap
argument_list|(
name|repList
operator|.
name|get
argument_list|(
name|pair
operator|.
name|i
argument_list|)
argument_list|,
name|sliceList
argument_list|,
name|elementType
operator|.
name|getFieldList
argument_list|()
operator|.
name|get
argument_list|(
name|pair
operator|.
name|i
argument_list|)
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Class
name|clazz
init|=
name|pair
operator|.
name|e
operator|instanceof
name|Class
condition|?
operator|(
name|Class
operator|)
name|pair
operator|.
name|e
else|:
name|Object
operator|.
name|class
decl_stmt|;
name|ValueSet
name|valueSet
init|=
operator|new
name|ValueSet
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
for|for
control|(
name|Object
name|o
range|:
name|list2
control|)
block|{
name|valueSet
operator|.
name|add
argument_list|(
operator|(
name|Comparable
operator|)
name|o
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sort
operator|!=
literal|null
operator|&&
name|sort
index|[
literal|0
index|]
operator|<
literal|0
operator|&&
name|valueSet
operator|.
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
operator|==
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
comment|// We have discovered a the first unique key in the table.
name|sort
index|[
literal|0
index|]
operator|=
name|pair
operator|.
name|i
expr_stmt|;
specifier|final
name|Comparable
index|[]
name|values
init|=
name|valueSet
operator|.
name|values
operator|.
name|toArray
argument_list|(
operator|new
name|Comparable
index|[
name|list
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|final
name|Kev
index|[]
name|kevs
init|=
operator|new
name|Kev
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|kevs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|kevs
index|[
name|i
index|]
operator|=
operator|new
name|Kev
argument_list|(
name|i
argument_list|,
name|values
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|kevs
argument_list|)
expr_stmt|;
name|sources
operator|=
operator|new
name|int
index|[
name|list
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|sources
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sources
index|[
name|i
index|]
operator|=
name|kevs
index|[
name|i
index|]
operator|.
name|source
expr_stmt|;
block|}
if|if
condition|(
name|isIdentity
argument_list|(
name|sources
argument_list|)
condition|)
block|{
comment|// Table was already sorted. Clear the permutation.
comment|// We've already set sort[0], so we won't check for another
comment|// sorted column.
name|sources
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// Re-sort all previous columns.
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pair
operator|.
name|i
condition|;
name|i
operator|++
control|)
block|{
name|representationValues
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|representationValues
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|permute
argument_list|(
name|sources
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|representationValues
operator|.
name|add
argument_list|(
name|valueSet
operator|.
name|freeze
argument_list|(
name|pair
operator|.
name|i
argument_list|,
name|sources
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Adapt for some types that we represent differently internally than their    * JDBC types. {@link java.sql.Timestamp} values that are not null are    * converted to {@code long}, but nullable timestamps are acquired using    * {@link java.sql.ResultSet#getObject(int)} and therefore the Timestamp    * value needs to be converted to a {@link Long}. Similarly    * {@link java.sql.Date} and {@link java.sql.Time} values to    * {@link Integer}. */
specifier|private
specifier|static
name|List
name|wrap
parameter_list|(
name|ColumnMetaData
operator|.
name|Rep
name|rep
parameter_list|,
name|List
name|list
parameter_list|,
name|RelDataType
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
operator|.
name|getSqlTypeName
argument_list|()
condition|)
block|{
case|case
name|TIMESTAMP
case|:
switch|switch
condition|(
name|rep
condition|)
block|{
case|case
name|OBJECT
case|:
case|case
name|JAVA_SQL_TIMESTAMP
case|:
return|return
name|Util
operator|.
name|transform
argument_list|(
name|list
argument_list|,
operator|(
name|Timestamp
name|t
operator|)
operator|->
name|t
operator|==
literal|null
condition|?
literal|null
else|:
name|t
operator|.
name|getTime
argument_list|()
argument_list|)
return|;
block|}
break|break;
case|case
name|TIME
case|:
switch|switch
condition|(
name|rep
condition|)
block|{
case|case
name|OBJECT
case|:
case|case
name|JAVA_SQL_TIME
case|:
return|return
name|Util
operator|.
name|transform
argument_list|(
name|list
argument_list|,
operator|(
name|Time
name|t
operator|)
operator|->
name|t
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|int
operator|)
operator|(
name|t
operator|.
name|getTime
argument_list|()
operator|%
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
operator|)
argument_list|)
return|;
block|}
break|break;
case|case
name|DATE
case|:
switch|switch
condition|(
name|rep
condition|)
block|{
case|case
name|OBJECT
case|:
case|case
name|JAVA_SQL_DATE
case|:
return|return
name|Util
operator|.
name|transform
argument_list|(
name|list
argument_list|,
operator|(
name|Date
name|d
operator|)
operator|->
name|d
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|int
operator|)
operator|(
name|d
operator|.
name|getTime
argument_list|()
operator|/
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
operator|)
argument_list|)
return|;
block|}
break|break;
block|}
return|return
name|list
return|;
block|}
comment|/**    * Set of values of a column, created during the load process, and converted    * to a serializable (and more compact) form before load completes.    */
specifier|static
class|class
name|ValueSet
block|{
specifier|final
name|Class
name|clazz
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|Comparable
argument_list|,
name|Comparable
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Comparable
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|Comparable
name|min
decl_stmt|;
name|Comparable
name|max
decl_stmt|;
name|boolean
name|containsNull
decl_stmt|;
name|ValueSet
parameter_list|(
name|Class
name|clazz
parameter_list|)
block|{
name|this
operator|.
name|clazz
operator|=
name|clazz
expr_stmt|;
block|}
name|void
name|add
parameter_list|(
name|Comparable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
specifier|final
name|Comparable
name|old
init|=
name|e
decl_stmt|;
name|e
operator|=
name|map
operator|.
name|get
argument_list|(
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|==
literal|null
condition|)
block|{
name|e
operator|=
name|old
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|e
argument_list|,
name|e
argument_list|)
expr_stmt|;
comment|//noinspection unchecked
if|if
condition|(
name|min
operator|==
literal|null
operator|||
name|min
operator|.
name|compareTo
argument_list|(
name|e
argument_list|)
operator|>
literal|0
condition|)
block|{
name|min
operator|=
name|e
expr_stmt|;
block|}
comment|//noinspection unchecked
if|if
condition|(
name|max
operator|==
literal|null
operator|||
name|max
operator|.
name|compareTo
argument_list|(
name|e
argument_list|)
operator|<
literal|0
condition|)
block|{
name|max
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|containsNull
operator|=
literal|true
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
comment|/** Freezes the contents of this value set into a column, optionally      * re-ordering if {@code sources} is specified. */
name|ArrayTable
operator|.
name|Column
name|freeze
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|int
index|[]
name|sources
parameter_list|)
block|{
name|ArrayTable
operator|.
name|Representation
name|representation
init|=
name|chooseRep
argument_list|(
name|ordinal
argument_list|)
decl_stmt|;
specifier|final
name|int
name|cardinality
init|=
name|map
operator|.
name|size
argument_list|()
operator|+
operator|(
name|containsNull
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
specifier|final
name|Object
name|data
init|=
name|representation
operator|.
name|freeze
argument_list|(
name|this
argument_list|,
name|sources
argument_list|)
decl_stmt|;
return|return
operator|new
name|ArrayTable
operator|.
name|Column
argument_list|(
name|representation
argument_list|,
name|data
argument_list|,
name|cardinality
argument_list|)
return|;
block|}
name|ArrayTable
operator|.
name|Representation
name|chooseRep
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|Primitive
name|primitive
init|=
name|Primitive
operator|.
name|of
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|Primitive
name|boxPrimitive
init|=
name|Primitive
operator|.
name|ofBox
argument_list|(
name|clazz
argument_list|)
decl_stmt|;
name|Primitive
name|p
init|=
name|primitive
operator|!=
literal|null
condition|?
name|primitive
else|:
name|boxPrimitive
decl_stmt|;
if|if
condition|(
operator|!
name|containsNull
operator|&&
name|p
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|p
condition|)
block|{
case|case
name|FLOAT
case|:
case|case
name|DOUBLE
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|p
argument_list|,
name|p
argument_list|)
return|;
case|case
name|OTHER
case|:
case|case
name|VOID
case|:
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"wtf?!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|canBeLong
argument_list|(
name|min
argument_list|)
operator|&&
name|canBeLong
argument_list|(
name|max
argument_list|)
condition|)
block|{
return|return
name|chooseFixedRep
argument_list|(
name|ordinal
argument_list|,
name|p
argument_list|,
name|toLong
argument_list|(
name|min
argument_list|)
argument_list|,
name|toLong
argument_list|(
name|max
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|// We don't want to use a dictionary if:
comment|// (a) there are so many values that an object pointer (with one
comment|//     indirection) has about as many bits as a code (with two
comment|//     indirections); or
comment|// (b) if there are very few copies of each value.
comment|// The condition kind of captures this, but needs to be tuned.
specifier|final
name|int
name|codeCount
init|=
name|map
operator|.
name|size
argument_list|()
operator|+
operator|(
name|containsNull
condition|?
literal|1
else|:
literal|0
operator|)
decl_stmt|;
specifier|final
name|int
name|codeBitCount
init|=
name|log2
argument_list|(
name|nextPowerOf2
argument_list|(
name|codeCount
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|codeBitCount
operator|<
literal|10
operator|&&
name|values
operator|.
name|size
argument_list|()
operator|>
literal|2000
condition|)
block|{
specifier|final
name|ArrayTable
operator|.
name|Representation
name|representation
init|=
name|chooseFixedRep
argument_list|(
operator|-
literal|1
argument_list|,
name|Primitive
operator|.
name|INT
argument_list|,
literal|0
argument_list|,
name|codeCount
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|ArrayTable
operator|.
name|ObjectDictionary
argument_list|(
name|ordinal
argument_list|,
name|representation
argument_list|)
return|;
block|}
return|return
operator|new
name|ArrayTable
operator|.
name|ObjectArray
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
specifier|private
name|long
name|toLong
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
comment|// We treat Boolean and Character as if they were subclasses of
comment|// Number but actually they are not.
if|if
condition|(
name|o
operator|instanceof
name|Boolean
condition|)
block|{
return|return
operator|(
name|Boolean
operator|)
name|o
condition|?
literal|1
else|:
literal|0
return|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Character
condition|)
block|{
return|return
operator|(
name|long
operator|)
operator|(
name|Character
operator|)
name|o
return|;
block|}
else|else
block|{
return|return
operator|(
operator|(
name|Number
operator|)
name|o
operator|)
operator|.
name|longValue
argument_list|()
return|;
block|}
block|}
specifier|private
name|boolean
name|canBeLong
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|instanceof
name|Boolean
operator|||
name|o
operator|instanceof
name|Character
operator|||
name|o
operator|instanceof
name|Number
return|;
block|}
comment|/** Chooses a representation for a fixed-precision primitive type      * (boolean, byte, char, short, int, long).      *      * @param ordinal Ordinal of this column in table      * @param p Type that values are to be returned as (not necessarily the      *     same as they will be stored)      * @param min Minimum value to be encoded      * @param max Maximum value to be encoded (inclusive)      */
specifier|private
name|ArrayTable
operator|.
name|Representation
name|chooseFixedRep
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|Primitive
name|p
parameter_list|,
name|long
name|min
parameter_list|,
name|long
name|max
parameter_list|)
block|{
if|if
condition|(
name|min
operator|==
name|max
condition|)
block|{
return|return
operator|new
name|ArrayTable
operator|.
name|Constant
argument_list|(
name|ordinal
argument_list|)
return|;
block|}
specifier|final
name|int
name|bitCountMax
init|=
name|log2
argument_list|(
name|nextPowerOf2
argument_list|(
name|abs2
argument_list|(
name|max
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|bitCount
decl_stmt|;
comment|// 1 for sign
name|boolean
name|signed
decl_stmt|;
if|if
condition|(
name|min
operator|>=
literal|0
condition|)
block|{
name|signed
operator|=
literal|false
expr_stmt|;
name|bitCount
operator|=
name|bitCountMax
expr_stmt|;
block|}
else|else
block|{
name|signed
operator|=
literal|true
expr_stmt|;
name|int
name|bitCountMin
init|=
name|log2
argument_list|(
name|nextPowerOf2
argument_list|(
name|abs2
argument_list|(
name|min
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|bitCount
operator|=
name|Math
operator|.
name|max
argument_list|(
name|bitCountMin
argument_list|,
name|bitCountMax
argument_list|)
operator|+
literal|1
expr_stmt|;
block|}
comment|// Must be a fixed point primitive.
if|if
condition|(
name|bitCount
operator|>
literal|21
operator|&&
name|bitCount
operator|<
literal|32
condition|)
block|{
comment|// Can't get more than 2 into a word.
name|signed
operator|=
literal|true
expr_stmt|;
name|bitCount
operator|=
literal|32
expr_stmt|;
block|}
if|if
condition|(
name|bitCount
operator|>=
literal|33
operator|&&
name|bitCount
operator|<
literal|64
condition|)
block|{
comment|// Can't get more than one into a word.
name|signed
operator|=
literal|true
expr_stmt|;
name|bitCount
operator|=
literal|64
expr_stmt|;
block|}
if|if
condition|(
name|signed
condition|)
block|{
switch|switch
condition|(
name|bitCount
condition|)
block|{
case|case
literal|8
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|Primitive
operator|.
name|BYTE
argument_list|,
name|p
argument_list|)
return|;
case|case
literal|16
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|Primitive
operator|.
name|SHORT
argument_list|,
name|p
argument_list|)
return|;
case|case
literal|32
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|Primitive
operator|.
name|INT
argument_list|,
name|p
argument_list|)
return|;
case|case
literal|64
case|:
return|return
operator|new
name|ArrayTable
operator|.
name|PrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|Primitive
operator|.
name|LONG
argument_list|,
name|p
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|ArrayTable
operator|.
name|BitSlicedPrimitiveArray
argument_list|(
name|ordinal
argument_list|,
name|bitCount
argument_list|,
name|p
argument_list|,
name|signed
argument_list|)
return|;
block|}
comment|/** Two's complement absolute on int value. */
specifier|private
specifier|static
name|int
name|abs2
parameter_list|(
name|int
name|v
parameter_list|)
block|{
comment|// -128 becomes +127
return|return
name|v
operator|<
literal|0
condition|?
operator|~
name|v
else|:
name|v
return|;
block|}
comment|/** Two's complement absolute on long value. */
specifier|private
specifier|static
name|long
name|abs2
parameter_list|(
name|long
name|v
parameter_list|)
block|{
comment|// -128 becomes +127
return|return
name|v
operator|<
literal|0
condition|?
operator|~
name|v
else|:
name|v
return|;
block|}
block|}
comment|/** Key-value pair. */
specifier|private
specifier|static
class|class
name|Kev
implements|implements
name|Comparable
argument_list|<
name|Kev
argument_list|>
block|{
specifier|private
specifier|final
name|int
name|source
decl_stmt|;
specifier|private
specifier|final
name|Comparable
name|key
decl_stmt|;
name|Kev
parameter_list|(
name|int
name|source
parameter_list|,
name|Comparable
name|key
parameter_list|)
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
block|}
specifier|public
name|int
name|compareTo
parameter_list|(
name|Kev
name|o
parameter_list|)
block|{
comment|//noinspection unchecked
return|return
name|key
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|key
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

