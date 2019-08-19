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
name|test
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
name|parser
operator|.
name|SqlParserImplFactory
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
name|parser
operator|.
name|SqlParserTest
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
name|parser
operator|.
name|ddl
operator|.
name|SqlDdlParserImpl
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

begin_comment
comment|/**  * Tests SQL parser extensions for DDL.  *  *<p>Remaining tasks:  *<ul>  *  *<li>"create table x (a int) as values 1, 2" should fail validation;  * data type not allowed in "create table ... as".  *  *<li>"create table x (a int, b int as (a + 1)) stored"  * should not allow b to be specified in insert;  * should generate check constraint on b;  * should populate b in insert as if it had a default  *  *<li>"create table as select" should store constraints  * deduced by planner  *  *<li>during CREATE VIEW, check for a table and a materialized view  * with the same name (they have the same namespace)  *  *</ul>  */
end_comment

begin_class
specifier|public
class|class
name|ServerParserTest
extends|extends
name|SqlParserTest
block|{
annotation|@
name|Override
specifier|protected
name|SqlParserImplFactory
name|parserImplFactory
parameter_list|()
block|{
return|return
name|SqlDdlParserImpl
operator|.
name|FACTORY
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateSchema
parameter_list|()
block|{
name|sql
argument_list|(
literal|"create schema x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CREATE SCHEMA `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateOrReplaceSchema
parameter_list|()
block|{
name|sql
argument_list|(
literal|"create or replace schema x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CREATE OR REPLACE SCHEMA `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateForeignSchema
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create or replace foreign schema x\n"
operator|+
literal|"type 'jdbc'\n"
operator|+
literal|"options (\n"
operator|+
literal|"  aBoolean true,\n"
operator|+
literal|"  anInteger -45,\n"
operator|+
literal|"  aDate DATE '1970-03-21',\n"
operator|+
literal|"  \"quoted.id\" TIMESTAMP '1970-03-21 12:4:56.78',\n"
operator|+
literal|"  aString 'foo''bar')"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE OR REPLACE FOREIGN SCHEMA `X` TYPE 'jdbc' "
operator|+
literal|"OPTIONS (`ABOOLEAN` TRUE,"
operator|+
literal|" `ANINTEGER` -45,"
operator|+
literal|" `ADATE` DATE '1970-03-21',"
operator|+
literal|" `quoted.id` TIMESTAMP '1970-03-21 12:04:56.78',"
operator|+
literal|" `ASTRING` 'foo''bar')"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateForeignSchema2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create or replace foreign schema x\n"
operator|+
literal|"library 'com.example.ExampleSchemaFactory'\n"
operator|+
literal|"options ()"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE OR REPLACE FOREIGN SCHEMA `X` "
operator|+
literal|"LIBRARY 'com.example.ExampleSchemaFactory' "
operator|+
literal|"OPTIONS ()"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTypeWithAttributeList
parameter_list|()
block|{
name|sql
argument_list|(
literal|"create type x.mytype1 as (i int not null, j varchar(5) null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CREATE TYPE `X`.`MYTYPE1` AS (`I` INTEGER NOT NULL, `J` VARCHAR(5))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTypeWithBaseType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"create type mytype1 as varchar(5)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CREATE TYPE `MYTYPE1` AS VARCHAR(5)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateOrReplaceTypeWith
parameter_list|()
block|{
name|sql
argument_list|(
literal|"create or replace type mytype1 as varchar(5)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CREATE OR REPLACE TYPE `MYTYPE1` AS VARCHAR(5)"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"create table x (i int not null, j varchar(5) null)"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"CREATE TABLE `X` (`I` INTEGER NOT NULL, `J` VARCHAR(5))"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTableAsSelect
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"CREATE TABLE `X` AS\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`"
decl_stmt|;
name|sql
argument_list|(
literal|"create table x as select * from emp"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTableIfNotExistsAsSelect
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"CREATE TABLE IF NOT EXISTS `X`.`Y` AS\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`"
decl_stmt|;
name|sql
argument_list|(
literal|"create table if not exists x.y as select * from emp"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTableAsValues
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"CREATE TABLE `X` AS\n"
operator|+
literal|"VALUES (ROW(1)),\n"
operator|+
literal|"(ROW(2))"
decl_stmt|;
name|sql
argument_list|(
literal|"create table x as values 1, 2"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTableAsSelectColumnList
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"CREATE TABLE `X` (`A`, `B`) AS\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`"
decl_stmt|;
name|sql
argument_list|(
literal|"create table x (a, b) as select * from emp"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTableCheck
parameter_list|()
block|{
specifier|final
name|String
name|expected
init|=
literal|"CREATE TABLE `X` (`I` INTEGER NOT NULL,"
operator|+
literal|" CONSTRAINT `C1` CHECK (`I`< 10), `J` INTEGER)"
decl_stmt|;
name|sql
argument_list|(
literal|"create table x (i int not null, constraint c1 check (i< 10), j int)"
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTableVirtualColumn
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create table if not exists x (\n"
operator|+
literal|" i int not null,\n"
operator|+
literal|" j int generated always as (i + 1) stored,\n"
operator|+
literal|" k int as (j + 1) virtual,\n"
operator|+
literal|" m int as (k + 1))"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE TABLE IF NOT EXISTS `X` "
operator|+
literal|"(`I` INTEGER NOT NULL,"
operator|+
literal|" `J` INTEGER AS (`I` + 1) STORED,"
operator|+
literal|" `K` INTEGER AS (`J` + 1) VIRTUAL,"
operator|+
literal|" `M` INTEGER AS (`K` + 1) VIRTUAL)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateTableWithUDT
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create table if not exists t (\n"
operator|+
literal|"  f0 MyType0 not null,\n"
operator|+
literal|"  f1 db_name.MyType1,\n"
operator|+
literal|"  f2 catalog_name.db_name.MyType2)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE TABLE IF NOT EXISTS `T` ("
operator|+
literal|"`F0` `MYTYPE0` NOT NULL,"
operator|+
literal|" `F1` `DB_NAME`.`MYTYPE1`,"
operator|+
literal|" `F2` `CATALOG_NAME`.`DB_NAME`.`MYTYPE2`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create or replace view v as\n"
operator|+
literal|"select * from (values (1, '2'), (3, '45')) as t (x, y)"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE OR REPLACE VIEW `V` AS\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM (VALUES (ROW(1, '2')),\n"
operator|+
literal|"(ROW(3, '45'))) AS `T` (`X`, `Y`)"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateMaterializedView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create materialized view mv (d, v) as\n"
operator|+
literal|"select deptno, count(*) from emp\n"
operator|+
literal|"group by deptno order by deptno desc"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE MATERIALIZED VIEW `MV` (`D`, `V`) AS\n"
operator|+
literal|"SELECT `DEPTNO`, COUNT(*)\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY `DEPTNO`\n"
operator|+
literal|"ORDER BY `DEPTNO` DESC"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateMaterializedView2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create materialized view if not exists mv as\n"
operator|+
literal|"select deptno, count(*) from emp\n"
operator|+
literal|"group by deptno order by deptno desc"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE MATERIALIZED VIEW IF NOT EXISTS `MV` AS\n"
operator|+
literal|"SELECT `DEPTNO`, COUNT(*)\n"
operator|+
literal|"FROM `EMP`\n"
operator|+
literal|"GROUP BY `DEPTNO`\n"
operator|+
literal|"ORDER BY `DEPTNO` DESC"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
comment|// "OR REPLACE" is allowed by the parser, but the validator will give an
comment|// error later
annotation|@
name|Test
specifier|public
name|void
name|testCreateOrReplaceMaterializedView
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create or replace materialized view mv as\n"
operator|+
literal|"select * from emp"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE MATERIALIZED VIEW `MV` AS\n"
operator|+
literal|"SELECT *\n"
operator|+
literal|"FROM `EMP`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateOrReplaceFunction
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create or replace function if not exists x.udf\n"
operator|+
literal|" as 'org.apache.calcite.udf.TableFun.demoUdf'\n"
operator|+
literal|"using jar 'file:/path/udf/udf-0.0.1-SNAPSHOT.jar',\n"
operator|+
literal|" jar 'file:/path/udf/udf2-0.0.1-SNAPSHOT.jar',\n"
operator|+
literal|" file 'file:/path/udf/logback.xml'"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE OR REPLACE FUNCTION"
operator|+
literal|" IF NOT EXISTS `X`.`UDF`"
operator|+
literal|" AS 'org.apache.calcite.udf.TableFun.demoUdf'"
operator|+
literal|" USING JAR 'file:/path/udf/udf-0.0.1-SNAPSHOT.jar',"
operator|+
literal|" JAR 'file:/path/udf/udf2-0.0.1-SNAPSHOT.jar',"
operator|+
literal|" FILE 'file:/path/udf/logback.xml'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateOrReplaceFunction2
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"create function \"my Udf\"\n"
operator|+
literal|" as 'org.apache.calcite.udf.TableFun.demoUdf'"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"CREATE FUNCTION `my Udf`"
operator|+
literal|" AS 'org.apache.calcite.udf.TableFun.demoUdf'"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropSchema
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop schema x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP SCHEMA `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropSchemaIfExists
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop schema if exists x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP SCHEMA IF EXISTS `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropForeignSchema
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop foreign schema x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP FOREIGN SCHEMA `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropType
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop type X"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP TYPE `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropTypeIfExists
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop type if exists X"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP TYPE IF EXISTS `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropTypeTrailingIfExistsFails
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop type X ^if^ exists"
argument_list|)
operator|.
name|fails
argument_list|(
literal|"(?s)Encountered \"if\" at.*"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropTable
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop table x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP TABLE `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropTableComposite
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop table x.y"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP TABLE `X`.`Y`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropTableIfExists
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop table if exists x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP TABLE IF EXISTS `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropView
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop view x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP VIEW `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropMaterializedView
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop materialized view x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP MATERIALIZED VIEW `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropMaterializedViewIfExists
parameter_list|()
block|{
name|sql
argument_list|(
literal|"drop materialized view if exists x"
argument_list|)
operator|.
name|ok
argument_list|(
literal|"DROP MATERIALIZED VIEW IF EXISTS `X`"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropFunction
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"drop function x.udf"
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"DROP FUNCTION `X`.`UDF`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDropFunctionIfExists
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"drop function if exists \"my udf\""
decl_stmt|;
specifier|final
name|String
name|expected
init|=
literal|"DROP FUNCTION IF EXISTS `my udf`"
decl_stmt|;
name|sql
argument_list|(
name|sql
argument_list|)
operator|.
name|ok
argument_list|(
name|expected
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End ServerParserTest.java
end_comment

end_unit

