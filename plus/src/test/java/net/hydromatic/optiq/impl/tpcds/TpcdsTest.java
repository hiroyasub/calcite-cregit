begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* // Licensed to Julian Hyde under one or more contributor license // agreements. See the NOTICE file distributed with this work for // additional information regarding copyright ownership. // // Julian Hyde licenses this file to you under the Apache License, // Version 2.0 (the "License"); you may not use this file except in // compliance with the License. You may obtain a copy of the License at: // // http://www.apache.org/licenses/LICENSE-2.0 // // Unless required by applicable law or agreed to in writing, software // distributed under the License is distributed on an "AS IS" BASIS, // WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. // See the License for the specific language governing permissions and // limitations under the License. */
end_comment

begin_package
package|package
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|impl
operator|.
name|tpcds
package|;
end_package

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|optiq
operator|.
name|test
operator|.
name|OptiqAssert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Ignore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|net
operator|.
name|hydromatic
operator|.
name|tpcds
operator|.
name|query
operator|.
name|Query
import|;
end_import

begin_comment
comment|/** Unit test for {@link net.hydromatic.optiq.impl.tpcds.TpcdsSchema}.  *  *<p>Only runs if {@code -Doptiq.test.slow=true} is specified on the  * command-line.  * (See {@link net.hydromatic.optiq.test.OptiqAssert#ENABLE_SLOW}.)</p> */
end_comment

begin_class
specifier|public
class|class
name|TpcdsTest
block|{
specifier|private
specifier|static
name|String
name|schema
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|scaleFactor
parameter_list|)
block|{
return|return
literal|"     {\n"
operator|+
literal|"       type: 'custom',\n"
operator|+
literal|"       name: '"
operator|+
name|name
operator|+
literal|"',\n"
operator|+
literal|"       factory: 'net.hydromatic.optiq.impl.tpcds.TpcdsSchemaFactory',\n"
operator|+
literal|"       operand: {\n"
operator|+
literal|"         columnPrefix: true,\n"
operator|+
literal|"         scale: "
operator|+
name|scaleFactor
operator|+
literal|"\n"
operator|+
literal|"       }\n"
operator|+
literal|"     }"
return|;
block|}
specifier|public
specifier|static
specifier|final
name|String
name|TPCDS_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'TPCDS',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|schema
argument_list|(
literal|"TPCDS"
argument_list|,
literal|"1.0"
argument_list|)
operator|+
literal|",\n"
operator|+
name|schema
argument_list|(
literal|"TPCDS_01"
argument_list|,
literal|"0.01"
argument_list|)
operator|+
literal|",\n"
operator|+
name|schema
argument_list|(
literal|"TPCDS_5"
argument_list|,
literal|"5.0"
argument_list|)
operator|+
literal|"\n"
operator|+
literal|"   ]\n"
operator|+
literal|"}"
decl_stmt|;
specifier|private
name|OptiqAssert
operator|.
name|AssertThat
name|with
parameter_list|()
block|{
return|return
name|OptiqAssert
operator|.
name|that
argument_list|()
operator|.
name|withModel
argument_list|(
name|TPCDS_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|OptiqAssert
operator|.
name|ENABLE_SLOW
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCallCenter
parameter_list|()
block|{
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpcds.call_center"
argument_list|)
operator|.
name|returnsUnordered
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"add tests like this that count each table"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testLineItem
parameter_list|()
block|{
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpcds.lineitem"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|6001215
argument_list|)
expr_stmt|;
block|}
comment|/** Tests the customer table with scale factor 5. */
annotation|@
name|Ignore
argument_list|(
literal|"add tests like this that count each table"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testCustomer5
parameter_list|()
block|{
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpcds_5.customer"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|750000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery01
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|1
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"takes too long to optimize"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery72
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|72
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
specifier|private
name|OptiqAssert
operator|.
name|AssertQuery
name|checkQuery
parameter_list|(
name|int
name|i
parameter_list|)
block|{
specifier|final
name|Query
name|query
init|=
name|Query
operator|.
name|of
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|sql
init|=
name|query
operator|.
name|sql
argument_list|(
operator|-
literal|1
argument_list|,
operator|new
name|Random
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|i
condition|)
block|{
case|case
literal|72
case|:
comment|// Work around OPTIQ-304: Support '<DATE> +<INTEGER>'.
name|sql
operator|=
name|sql
operator|.
name|replace
argument_list|(
literal|"+ 5"
argument_list|,
literal|"+ interval '5' day"
argument_list|)
expr_stmt|;
block|}
return|return
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|sql
operator|.
name|replaceAll
argument_list|(
literal|"tpcds\\."
argument_list|,
literal|"tpcds_01."
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End TpcdsTest.java
end_comment

end_unit

