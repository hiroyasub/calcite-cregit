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
name|innodb
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
name|avatica
operator|.
name|util
operator|.
name|ByteString
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
name|Enumerator
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
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|page
operator|.
name|index
operator|.
name|GenericRecord
import|;
end_import

begin_import
import|import
name|com
operator|.
name|alibaba
operator|.
name|innodb
operator|.
name|java
operator|.
name|reader
operator|.
name|util
operator|.
name|Utils
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
name|time
operator|.
name|LocalDate
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
name|TimeZone
import|;
end_import

begin_comment
comment|/**  * Enumerator that reads from InnoDB data file.  */
end_comment

begin_class
class|class
name|InnodbEnumerator
implements|implements
name|Enumerator
argument_list|<
name|Object
argument_list|>
block|{
specifier|private
specifier|final
name|Iterator
argument_list|<
name|GenericRecord
argument_list|>
name|iterator
decl_stmt|;
specifier|private
name|GenericRecord
name|current
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|RelDataTypeField
argument_list|>
name|fieldTypes
decl_stmt|;
comment|/**    * Creates an InnodbEnumerator.    *    * @param resultIterator result iterator    * @param rowType   the type of resulting rows    */
name|InnodbEnumerator
parameter_list|(
name|Iterator
argument_list|<
name|GenericRecord
argument_list|>
name|resultIterator
parameter_list|,
name|RelDataType
name|rowType
parameter_list|)
block|{
name|this
operator|.
name|iterator
operator|=
name|resultIterator
expr_stmt|;
name|this
operator|.
name|current
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|fieldTypes
operator|=
name|rowType
operator|.
name|getFieldList
argument_list|()
expr_stmt|;
block|}
comment|/**    * Produces the next row from the results.    *    * @return a new row from the results    */
annotation|@
name|Override
specifier|public
name|Object
name|current
parameter_list|()
block|{
if|if
condition|(
name|fieldTypes
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
comment|// If we just have one field, produce it directly
return|return
name|currentRowField
argument_list|(
name|fieldTypes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
comment|// Build an array with all fields in this row
name|Object
index|[]
name|row
init|=
operator|new
name|Object
index|[
name|fieldTypes
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
name|fieldTypes
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|row
index|[
name|i
index|]
operator|=
name|currentRowField
argument_list|(
name|fieldTypes
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|row
return|;
block|}
block|}
comment|/**    * Get a field for the current row from the underlying object.    */
specifier|private
name|Object
name|currentRowField
parameter_list|(
name|RelDataTypeField
name|relDataTypeField
parameter_list|)
block|{
specifier|final
name|Object
name|o
init|=
name|current
operator|.
name|get
argument_list|(
name|relDataTypeField
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|convertToEnumeratorObject
argument_list|(
name|o
argument_list|,
name|relDataTypeField
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Convert an object into the expected internal representation.    *    * @param obj         object to convert, if needed    * @param relDataType data type    */
specifier|private
name|Object
name|convertToEnumeratorObject
parameter_list|(
name|Object
name|obj
parameter_list|,
name|RelDataType
name|relDataType
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SqlTypeName
name|sqlTypeName
init|=
name|relDataType
operator|.
name|getSqlTypeName
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|sqlTypeName
condition|)
block|{
case|case
name|BINARY
case|:
case|case
name|VARBINARY
case|:
return|return
operator|new
name|ByteString
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|obj
argument_list|)
return|;
case|case
name|TIMESTAMP
case|:
name|Timestamp
name|timestamp
init|=
name|Utils
operator|.
name|convertDateTime
argument_list|(
operator|(
name|String
operator|)
name|obj
argument_list|,
name|relDataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|shift
argument_list|(
name|timestamp
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
case|case
name|TIME
case|:
name|Time
name|time
init|=
name|Utils
operator|.
name|convertTime
argument_list|(
operator|(
name|String
operator|)
name|obj
argument_list|,
name|relDataType
operator|.
name|getPrecision
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|shift
argument_list|(
name|time
argument_list|)
operator|.
name|getTime
argument_list|()
return|;
case|case
name|DATE
case|:
name|Date
name|date
init|=
name|Date
operator|.
name|valueOf
argument_list|(
name|LocalDate
operator|.
name|parse
argument_list|(
operator|(
name|String
operator|)
name|obj
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|DateTimeUtils
operator|.
name|dateStringToUnixDate
argument_list|(
name|date
operator|.
name|toString
argument_list|()
argument_list|)
return|;
default|default:
return|return
name|obj
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|moveNext
parameter_list|()
block|{
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|current
operator|=
name|iterator
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// Nothing to do here
block|}
specifier|private
specifier|static
name|Timestamp
name|shift
parameter_list|(
name|Timestamp
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|time
init|=
name|v
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
decl_stmt|;
return|return
operator|new
name|Timestamp
argument_list|(
name|time
operator|+
name|offset
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|Time
name|shift
parameter_list|(
name|Time
name|v
parameter_list|)
block|{
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|long
name|time
init|=
name|v
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|int
name|offset
init|=
name|TimeZone
operator|.
name|getDefault
argument_list|()
operator|.
name|getOffset
argument_list|(
name|time
argument_list|)
decl_stmt|;
return|return
operator|new
name|Time
argument_list|(
operator|(
name|time
operator|+
name|offset
operator|)
operator|%
name|DateTimeUtils
operator|.
name|MILLIS_PER_DAY
argument_list|)
return|;
block|}
block|}
end_class

end_unit

