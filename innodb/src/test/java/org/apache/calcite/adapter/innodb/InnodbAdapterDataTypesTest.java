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
name|test
operator|.
name|CalciteAssert
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
name|Sources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|jupiter
operator|.
name|api
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Instant
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|LocalDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|OffsetDateTime
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|ZoneOffset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|zone
operator|.
name|ZoneRules
import|;
end_import

begin_comment
comment|/**  * Tests for the {@code org.apache.calcite.adapter.innodb} package related to data types.  *  *<p>Will read InnoDB data file {@code test_types.ibd}.  */
end_comment

begin_class
specifier|public
class|class
name|InnodbAdapterDataTypesTest
block|{
specifier|private
specifier|static
specifier|final
name|ImmutableMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|INNODB_MODEL
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"model"
argument_list|,
name|Sources
operator|.
name|of
argument_list|(
name|InnodbAdapterTest
operator|.
name|class
operator|.
name|getResource
argument_list|(
literal|"/model.json"
argument_list|)
argument_list|)
operator|.
name|file
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
annotation|@
name|Test
name|void
name|testTypesRowType
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|INNODB_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_types\""
argument_list|)
operator|.
name|typeIs
argument_list|(
literal|"[id INTEGER NOT NULL, "
operator|+
literal|"f_tinyint TINYINT NOT NULL, "
operator|+
literal|"f_smallint SMALLINT NOT NULL, "
operator|+
literal|"f_mediumint INTEGER NOT NULL, "
operator|+
literal|"f_int INTEGER NOT NULL, "
operator|+
literal|"f_bigint BIGINT NOT NULL, "
operator|+
literal|"f_datetime TIMESTAMP NOT NULL, "
operator|+
literal|"f_timestamp TIMESTAMP_WITH_LOCAL_TIME_ZONE NOT NULL, "
operator|+
literal|"f_time TIME NOT NULL, "
operator|+
literal|"f_year SMALLINT NOT NULL, "
operator|+
literal|"f_date DATE NOT NULL, "
operator|+
literal|"f_float REAL NOT NULL, "
operator|+
literal|"f_double DOUBLE NOT NULL, "
operator|+
literal|"f_decimal1 DECIMAL NOT NULL, "
operator|+
literal|"f_decimal2 DECIMAL NOT NULL, "
operator|+
literal|"f_decimal3 DECIMAL NOT NULL, "
operator|+
literal|"f_decimal4 DECIMAL NOT NULL, "
operator|+
literal|"f_decimal5 DECIMAL NOT NULL, "
operator|+
literal|"f_decimal6 DECIMAL, "
operator|+
literal|"f_varchar VARCHAR NOT NULL, "
operator|+
literal|"f_varchar_overflow VARCHAR NOT NULL, "
operator|+
literal|"f_varchar_null VARCHAR, "
operator|+
literal|"f_char_32 CHAR NOT NULL, "
operator|+
literal|"f_char_255 CHAR NOT NULL, "
operator|+
literal|"f_char_null CHAR, "
operator|+
literal|"f_boolean BOOLEAN NOT NULL, "
operator|+
literal|"f_bool BOOLEAN NOT NULL, "
operator|+
literal|"f_tinytext VARCHAR NOT NULL, "
operator|+
literal|"f_text VARCHAR NOT NULL, "
operator|+
literal|"f_mediumtext VARCHAR NOT NULL, "
operator|+
literal|"f_longtext VARCHAR NOT NULL, "
operator|+
literal|"f_tinyblob VARBINARY NOT NULL, "
operator|+
literal|"f_blob VARBINARY NOT NULL, "
operator|+
literal|"f_mediumblob VARBINARY NOT NULL, "
operator|+
literal|"f_longblob VARBINARY NOT NULL, "
operator|+
literal|"f_varbinary VARBINARY NOT NULL, "
operator|+
literal|"f_varbinary_overflow VARBINARY NOT NULL, "
operator|+
literal|"f_enum VARCHAR NOT NULL, "
operator|+
literal|"f_set VARCHAR NOT NULL]"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
name|void
name|testTypesValues
parameter_list|()
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|INNODB_MODEL
argument_list|)
operator|.
name|query
argument_list|(
literal|"select * from \"test_types\""
argument_list|)
operator|.
name|returnsOrdered
argument_list|(
literal|"id=1; "
operator|+
literal|"f_tinyint=100; "
operator|+
literal|"f_smallint=10000; "
operator|+
literal|"f_mediumint=1000000; "
operator|+
literal|"f_int=10000000; "
operator|+
literal|"f_bigint=100000000000; "
operator|+
literal|"f_datetime=2019-10-02 10:59:59; "
operator|+
literal|"f_timestamp="
operator|+
name|expectedLocalTime
argument_list|(
literal|"1988-11-23 22:10:08"
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_time=00:36:52; "
operator|+
literal|"f_year=2012; "
operator|+
literal|"f_date=2020-01-29; "
operator|+
literal|"f_float=0.9876543; "
operator|+
literal|"f_double=1.23456789012345E9; "
operator|+
literal|"f_decimal1=123456; "
operator|+
literal|"f_decimal2=12345.67890; "
operator|+
literal|"f_decimal3=12345678901; "
operator|+
literal|"f_decimal4=123.100; "
operator|+
literal|"f_decimal5=12346; "
operator|+
literal|"f_decimal6=12345.1234567890123456789012345; "
operator|+
literal|"f_varchar=c"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|'x'
argument_list|,
literal|31
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_varchar_overflow=c"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"ãã¼ã¿"
argument_list|,
literal|300
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_varchar_null=null; "
operator|+
literal|"f_char_32=c"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Ð´Ð°Ð½Ð½ÑÐµ"
argument_list|,
literal|2
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_char_255=c"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"æ°æ®"
argument_list|,
literal|100
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_char_null=null; "
operator|+
literal|"f_boolean=false; "
operator|+
literal|"f_bool=true; "
operator|+
literal|"f_tinytext=c"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Data"
argument_list|,
literal|50
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_text=c"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Daten"
argument_list|,
literal|200
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_mediumtext=c"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Datos"
argument_list|,
literal|200
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_longtext=c"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Les donnÃ©es"
argument_list|,
literal|800
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_tinyblob="
operator|+
name|genByteArrayString
argument_list|(
literal|"63"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0a
argument_list|,
literal|100
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_blob="
operator|+
name|genByteArrayString
argument_list|(
literal|"63"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0b
argument_list|,
literal|400
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_mediumblob="
operator|+
name|genByteArrayString
argument_list|(
literal|"63"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0c
argument_list|,
literal|800
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_longblob="
operator|+
name|genByteArrayString
argument_list|(
literal|"63"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0d
argument_list|,
literal|1000
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_varbinary="
operator|+
name|genByteArrayString
argument_list|(
literal|"63"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0e
argument_list|,
literal|8
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_varbinary_overflow="
operator|+
name|genByteArrayString
argument_list|(
literal|"63"
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|,
literal|100
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_enum=MYSQL; "
operator|+
literal|"f_set=z"
argument_list|,
literal|"id=2; "
operator|+
literal|"f_tinyint=-100; "
operator|+
literal|"f_smallint=-10000; "
operator|+
literal|"f_mediumint=-1000000; "
operator|+
literal|"f_int=-10000000; "
operator|+
literal|"f_bigint=-9223372036854775807; "
operator|+
literal|"f_datetime=2255-01-01 12:12:12; "
operator|+
literal|"f_timestamp="
operator|+
name|expectedLocalTime
argument_list|(
literal|"2020-01-01 00:00:00"
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_time=23:11:00; "
operator|+
literal|"f_year=0; "
operator|+
literal|"f_date=1970-01-01; "
operator|+
literal|"f_float=-1.2345678E7; "
operator|+
literal|"f_double=-1.234567890123456E9; "
operator|+
literal|"f_decimal1=9; "
operator|+
literal|"f_decimal2=-567.89100; "
operator|+
literal|"f_decimal3=987654321; "
operator|+
literal|"f_decimal4=456.000; "
operator|+
literal|"f_decimal5=0; "
operator|+
literal|"f_decimal6=-0.0123456789012345678912345; "
operator|+
literal|"f_varchar=d"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|'y'
argument_list|,
literal|31
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_varchar_overflow=d"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"ãã¼ã¿"
argument_list|,
literal|300
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_varchar_null=null; "
operator|+
literal|"f_char_32=d"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Ð´Ð°Ð½Ð½ÑÐµ"
argument_list|,
literal|2
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_char_255=d"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"æ°æ®"
argument_list|,
literal|100
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_char_null=null; "
operator|+
literal|"f_boolean=false; "
operator|+
literal|"f_bool=true; "
operator|+
literal|"f_tinytext=d"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Data"
argument_list|,
literal|50
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_text=d"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Daten"
argument_list|,
literal|200
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_mediumtext=d"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Datos"
argument_list|,
literal|200
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_longtext=d"
operator|+
name|StringUtils
operator|.
name|repeat
argument_list|(
literal|"Les donnÃ©es"
argument_list|,
literal|800
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_tinyblob="
operator|+
name|genByteArrayString
argument_list|(
literal|"64"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0a
argument_list|,
literal|100
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_blob="
operator|+
name|genByteArrayString
argument_list|(
literal|"64"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0b
argument_list|,
literal|400
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_mediumblob="
operator|+
name|genByteArrayString
argument_list|(
literal|"64"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0c
argument_list|,
literal|800
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_longblob="
operator|+
name|genByteArrayString
argument_list|(
literal|"64"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0d
argument_list|,
literal|1000
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_varbinary="
operator|+
name|genByteArrayString
argument_list|(
literal|"64"
argument_list|,
operator|(
name|byte
operator|)
literal|0x0e
argument_list|,
literal|8
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_varbinary_overflow="
operator|+
name|genByteArrayString
argument_list|(
literal|"64"
argument_list|,
operator|(
name|byte
operator|)
literal|0xff
argument_list|,
literal|100
argument_list|)
operator|+
literal|"; "
operator|+
literal|"f_enum=Hello; "
operator|+
literal|"f_set=a,e,i,o,u"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|genByteArrayString
parameter_list|(
name|String
name|prefix
parameter_list|,
name|byte
name|b
parameter_list|,
name|int
name|repeat
parameter_list|)
block|{
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
name|prefix
argument_list|)
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
name|repeat
condition|;
name|i
operator|++
control|)
block|{
name|String
name|hexString
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
name|b
operator|&
literal|0xFF
argument_list|)
decl_stmt|;
if|if
condition|(
name|hexString
operator|.
name|length
argument_list|()
operator|<
literal|2
condition|)
block|{
name|str
operator|.
name|append
argument_list|(
literal|"0"
argument_list|)
expr_stmt|;
block|}
name|str
operator|.
name|append
argument_list|(
name|hexString
argument_list|)
expr_stmt|;
block|}
return|return
name|str
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|expectedLocalTime
parameter_list|(
name|String
name|dateTime
parameter_list|)
block|{
name|ZoneRules
name|rules
init|=
name|ZoneId
operator|.
name|systemDefault
argument_list|()
operator|.
name|getRules
argument_list|()
decl_stmt|;
name|LocalDateTime
name|ldt
init|=
name|Utils
operator|.
name|parseDateTimeText
argument_list|(
name|dateTime
argument_list|)
decl_stmt|;
name|Instant
name|instant
init|=
name|ldt
operator|.
name|toInstant
argument_list|(
name|ZoneOffset
operator|.
name|of
argument_list|(
literal|"+00:00"
argument_list|)
argument_list|)
decl_stmt|;
name|ZoneOffset
name|standardOffset
init|=
name|rules
operator|.
name|getOffset
argument_list|(
name|instant
argument_list|)
decl_stmt|;
name|OffsetDateTime
name|odt
init|=
name|instant
operator|.
name|atOffset
argument_list|(
name|standardOffset
argument_list|)
decl_stmt|;
return|return
name|odt
operator|.
name|toLocalDateTime
argument_list|()
operator|.
name|format
argument_list|(
name|Utils
operator|.
name|TIME_FORMAT_TIMESTAMP
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

