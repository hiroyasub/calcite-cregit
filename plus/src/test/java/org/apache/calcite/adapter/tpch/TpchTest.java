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
name|tpch
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
name|config
operator|.
name|CalciteSystemProperty
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
name|plan
operator|.
name|RelOptUtil
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
name|TestUtil
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|not
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_comment
comment|/** Unit test for {@link org.apache.calcite.adapter.tpch.TpchSchema}.  *  *<p>Because the TPC-H data generator takes time and memory to instantiate,  * tests that read data (that is, most tests) only run  * if {@link org.apache.calcite.config.CalciteSystemProperty#TEST_SLOW} is set.</p>  */
end_comment

begin_class
specifier|public
class|class
name|TpchTest
block|{
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLE
init|=
name|CalciteSystemProperty
operator|.
name|TEST_SLOW
operator|.
name|value
argument_list|()
operator|&&
name|TestUtil
operator|.
name|getJavaMajorVersion
argument_list|()
operator|>=
literal|7
decl_stmt|;
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
literal|"       factory: 'org.apache.calcite.adapter.tpch.TpchSchemaFactory',\n"
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
name|TPCH_MODEL
init|=
literal|"{\n"
operator|+
literal|"  version: '1.0',\n"
operator|+
literal|"  defaultSchema: 'TPCH',\n"
operator|+
literal|"   schemas: [\n"
operator|+
name|schema
argument_list|(
literal|"TPCH"
argument_list|,
literal|"1.0"
argument_list|)
operator|+
literal|",\n"
operator|+
name|schema
argument_list|(
literal|"TPCH_01"
argument_list|,
literal|"0.01"
argument_list|)
operator|+
literal|",\n"
operator|+
name|schema
argument_list|(
literal|"TPCH_5"
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
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|QUERIES
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
comment|// 01
literal|"select\n"
operator|+
literal|"  l_returnflag,\n"
operator|+
literal|"  l_linestatus,\n"
operator|+
literal|"  sum(l_quantity) as sum_qty,\n"
operator|+
literal|"  sum(l_extendedprice) as sum_base_price,\n"
operator|+
literal|"  sum(l_extendedprice * (1 - l_discount)) as sum_disc_price,\n"
operator|+
literal|"  sum(l_extendedprice * (1 - l_discount) * (1 + l_tax)) as sum_charge,\n"
operator|+
literal|"  avg(l_quantity) as avg_qty,\n"
operator|+
literal|"  avg(l_extendedprice) as avg_price,\n"
operator|+
literal|"  avg(l_discount) as avg_disc,\n"
operator|+
literal|"  count(*) as count_order\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.lineitem\n"
operator|+
literal|"-- where\n"
operator|+
literal|"--  l_shipdate<= date '1998-12-01' - interval '120' day (3)\n"
operator|+
literal|"group by\n"
operator|+
literal|"  l_returnflag,\n"
operator|+
literal|"  l_linestatus\n"
operator|+
literal|"\n"
operator|+
literal|"order by\n"
operator|+
literal|"  l_returnflag,\n"
operator|+
literal|"  l_linestatus"
argument_list|,
comment|// 02
literal|"select\n"
operator|+
literal|"  s.s_acctbal,\n"
operator|+
literal|"  s.s_name,\n"
operator|+
literal|"  n.n_name,\n"
operator|+
literal|"  p.p_partkey,\n"
operator|+
literal|"  p.p_mfgr,\n"
operator|+
literal|"  s.s_address,\n"
operator|+
literal|"  s.s_phone,\n"
operator|+
literal|"  s.s_comment\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.part p,\n"
operator|+
literal|"  tpch.supplier s,\n"
operator|+
literal|"  tpch.partsupp ps,\n"
operator|+
literal|"  tpch.nation n,\n"
operator|+
literal|"  tpch.region r\n"
operator|+
literal|"where\n"
operator|+
literal|"  p.p_partkey = ps.ps_partkey\n"
operator|+
literal|"  and s.s_suppkey = ps.ps_suppkey\n"
operator|+
literal|"  and p.p_size = 41\n"
operator|+
literal|"  and p.p_type like '%NICKEL'\n"
operator|+
literal|"  and s.s_nationkey = n.n_nationkey\n"
operator|+
literal|"  and n.n_regionkey = r.r_regionkey\n"
operator|+
literal|"  and r.r_name = 'EUROPE'\n"
operator|+
literal|"  and ps.ps_supplycost = (\n"
operator|+
literal|"\n"
operator|+
literal|"    select\n"
operator|+
literal|"      min(ps.ps_supplycost)\n"
operator|+
literal|"\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.partsupp ps,\n"
operator|+
literal|"      tpch.supplier s,\n"
operator|+
literal|"      tpch.nation n,\n"
operator|+
literal|"      tpch.region r\n"
operator|+
literal|"    where\n"
operator|+
literal|"      p.p_partkey = ps.ps_partkey\n"
operator|+
literal|"      and s.s_suppkey = ps.ps_suppkey\n"
operator|+
literal|"      and s.s_nationkey = n.n_nationkey\n"
operator|+
literal|"      and n.n_regionkey = r.r_regionkey\n"
operator|+
literal|"      and r.r_name = 'EUROPE'\n"
operator|+
literal|"  )\n"
operator|+
literal|"\n"
operator|+
literal|"order by\n"
operator|+
literal|"  s.s_acctbal desc,\n"
operator|+
literal|"  n.n_name,\n"
operator|+
literal|"  s.s_name,\n"
operator|+
literal|"  p.p_partkey\n"
operator|+
literal|"limit 100"
argument_list|,
comment|// 03
literal|"select\n"
operator|+
literal|"  l.l_orderkey,\n"
operator|+
literal|"  sum(l.l_extendedprice * (1 - l.l_discount)) as revenue,\n"
operator|+
literal|"  o.o_orderdate,\n"
operator|+
literal|"  o.o_shippriority\n"
operator|+
literal|"\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.customer c,\n"
operator|+
literal|"  tpch.orders o,\n"
operator|+
literal|"  tpch.lineitem l\n"
operator|+
literal|"\n"
operator|+
literal|"where\n"
operator|+
literal|"  c.c_mktsegment = 'HOUSEHOLD'\n"
operator|+
literal|"  and c.c_custkey = o.o_custkey\n"
operator|+
literal|"  and l.l_orderkey = o.o_orderkey\n"
operator|+
literal|"--  and o.o_orderdate< date '1995-03-25'\n"
operator|+
literal|"--  and l.l_shipdate> date '1995-03-25'\n"
operator|+
literal|"\n"
operator|+
literal|"group by\n"
operator|+
literal|"  l.l_orderkey,\n"
operator|+
literal|"  o.o_orderdate,\n"
operator|+
literal|"  o.o_shippriority\n"
operator|+
literal|"order by\n"
operator|+
literal|"  revenue desc,\n"
operator|+
literal|"  o.o_orderdate\n"
operator|+
literal|"limit 10"
argument_list|,
comment|// 04
literal|"select\n"
operator|+
literal|"  o_orderpriority,\n"
operator|+
literal|"  count(*) as order_count\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.orders\n"
operator|+
literal|"\n"
operator|+
literal|"where\n"
operator|+
literal|"--  o_orderdate>= date '1996-10-01'\n"
operator|+
literal|"--  and o_orderdate< date '1996-10-01' + interval '3' month\n"
operator|+
literal|"--  and \n"
operator|+
literal|"  exists (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      *\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.lineitem\n"
operator|+
literal|"    where\n"
operator|+
literal|"      l_orderkey = o_orderkey\n"
operator|+
literal|"      and l_commitdate< l_receiptdate\n"
operator|+
literal|"  )\n"
operator|+
literal|"group by\n"
operator|+
literal|"  o_orderpriority\n"
operator|+
literal|"order by\n"
operator|+
literal|"  o_orderpriority"
argument_list|,
comment|// 05
literal|"select\n"
operator|+
literal|"  n.n_name,\n"
operator|+
literal|"  sum(l.l_extendedprice * (1 - l.l_discount)) as revenue\n"
operator|+
literal|"\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.customer c,\n"
operator|+
literal|"  tpch.orders o,\n"
operator|+
literal|"  tpch.lineitem l,\n"
operator|+
literal|"  tpch.supplier s,\n"
operator|+
literal|"  tpch.nation n,\n"
operator|+
literal|"  tpch.region r\n"
operator|+
literal|"\n"
operator|+
literal|"where\n"
operator|+
literal|"  c.c_custkey = o.o_custkey\n"
operator|+
literal|"  and l.l_orderkey = o.o_orderkey\n"
operator|+
literal|"  and l.l_suppkey = s.s_suppkey\n"
operator|+
literal|"  and c.c_nationkey = s.s_nationkey\n"
operator|+
literal|"  and s.s_nationkey = n.n_nationkey\n"
operator|+
literal|"  and n.n_regionkey = r.r_regionkey\n"
operator|+
literal|"  and r.r_name = 'EUROPE'\n"
operator|+
literal|"--  and o.o_orderdate>= date '1997-01-01'\n"
operator|+
literal|"--  and o.o_orderdate< date '1997-01-01' + interval '1' year\n"
operator|+
literal|"group by\n"
operator|+
literal|"  n.n_name\n"
operator|+
literal|"\n"
operator|+
literal|"order by\n"
operator|+
literal|"  revenue desc"
argument_list|,
comment|// 06
literal|"select\n"
operator|+
literal|"  sum(l_extendedprice * l_discount) as revenue\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.lineitem\n"
operator|+
literal|"where\n"
operator|+
literal|"--  l_shipdate>= date '1997-01-01'\n"
operator|+
literal|"--  and l_shipdate< date '1997-01-01' + interval '1' year\n"
operator|+
literal|"--  and\n"
operator|+
literal|"  l_discount between 0.03 - 0.01 and 0.03 + 0.01\n"
operator|+
literal|"  and l_quantity< 24"
argument_list|,
comment|// 07
literal|"select\n"
operator|+
literal|"  supp_nation,\n"
operator|+
literal|"  cust_nation,\n"
operator|+
literal|"  l_year,\n"
operator|+
literal|"  sum(volume) as revenue\n"
operator|+
literal|"from\n"
operator|+
literal|"  (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      n1.n_name as supp_nation,\n"
operator|+
literal|"      n2.n_name as cust_nation,\n"
operator|+
literal|"      extract(year from l.l_shipdate) as l_year,\n"
operator|+
literal|"      l.l_extendedprice * (1 - l.l_discount) as volume\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.supplier s,\n"
operator|+
literal|"      tpch.lineitem l,\n"
operator|+
literal|"      tpch.orders o,\n"
operator|+
literal|"      tpch.customer c,\n"
operator|+
literal|"      tpch.nation n1,\n"
operator|+
literal|"      tpch.nation n2\n"
operator|+
literal|"    where\n"
operator|+
literal|"      s.s_suppkey = l.l_suppkey\n"
operator|+
literal|"      and o.o_orderkey = l.l_orderkey\n"
operator|+
literal|"      and c.c_custkey = o.o_custkey\n"
operator|+
literal|"      and s.s_nationkey = n1.n_nationkey\n"
operator|+
literal|"      and c.c_nationkey = n2.n_nationkey\n"
operator|+
literal|"      and (\n"
operator|+
literal|"        (n1.n_name = 'EGYPT' and n2.n_name = 'UNITED STATES')\n"
operator|+
literal|"        or (n1.n_name = 'UNITED STATES' and n2.n_name = 'EGYPT')\n"
operator|+
literal|"      )\n"
operator|+
literal|"--      and l.l_shipdate between date '1995-01-01' and date '1996-12-31'\n"
operator|+
literal|"  ) as shipping\n"
operator|+
literal|"group by\n"
operator|+
literal|"  supp_nation,\n"
operator|+
literal|"  cust_nation,\n"
operator|+
literal|"  l_year\n"
operator|+
literal|"order by\n"
operator|+
literal|"  supp_nation,\n"
operator|+
literal|"  cust_nation,\n"
operator|+
literal|"  l_year"
argument_list|,
comment|// 08
literal|"select\n"
operator|+
literal|"  o_year,\n"
operator|+
literal|"  sum(case\n"
operator|+
literal|"    when nation = 'EGYPT' then volume\n"
operator|+
literal|"    else 0\n"
operator|+
literal|"  end) / sum(volume) as mkt_share\n"
operator|+
literal|"from\n"
operator|+
literal|"  (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      extract(year from o.o_orderdate) as o_year,\n"
operator|+
literal|"      l.l_extendedprice * (1 - l.l_discount) as volume,\n"
operator|+
literal|"      n2.n_name as nation\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.part p,\n"
operator|+
literal|"      tpch.supplier s,\n"
operator|+
literal|"      tpch.lineitem l,\n"
operator|+
literal|"      tpch.orders o,\n"
operator|+
literal|"      tpch.customer c,\n"
operator|+
literal|"      tpch.nation n1,\n"
operator|+
literal|"      tpch.nation n2,\n"
operator|+
literal|"      tpch.region r\n"
operator|+
literal|"    where\n"
operator|+
literal|"      p.p_partkey = l.l_partkey\n"
operator|+
literal|"      and s.s_suppkey = l.l_suppkey\n"
operator|+
literal|"      and l.l_orderkey = o.o_orderkey\n"
operator|+
literal|"      and o.o_custkey = c.c_custkey\n"
operator|+
literal|"      and c.c_nationkey = n1.n_nationkey\n"
operator|+
literal|"      and n1.n_regionkey = r.r_regionkey\n"
operator|+
literal|"      and r.r_name = 'MIDDLE EAST'\n"
operator|+
literal|"      and s.s_nationkey = n2.n_nationkey\n"
operator|+
literal|"      and o.o_orderdate between date '1995-01-01' and date '1996-12-31'\n"
operator|+
literal|"      and p.p_type = 'PROMO BRUSHED COPPER'\n"
operator|+
literal|"  ) as all_nations\n"
operator|+
literal|"group by\n"
operator|+
literal|"  o_year\n"
operator|+
literal|"order by\n"
operator|+
literal|"  o_year"
argument_list|,
comment|// 09
literal|"select\n"
operator|+
literal|"  nation,\n"
operator|+
literal|"  o_year,\n"
operator|+
literal|"  sum(amount) as sum_profit\n"
operator|+
literal|"from\n"
operator|+
literal|"  (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      n_name as nation,\n"
operator|+
literal|"      extract(year from o_orderdate) as o_year,\n"
operator|+
literal|"      l.l_extendedprice * (1 - l.l_discount) - ps.ps_supplycost * l.l_quantity as amount\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.part p,\n"
operator|+
literal|"      tpch.supplier s,\n"
operator|+
literal|"      tpch.lineitem l,\n"
operator|+
literal|"      tpch.partsupp ps,\n"
operator|+
literal|"      tpch.orders o,\n"
operator|+
literal|"      tpch.nation n\n"
operator|+
literal|"    where\n"
operator|+
literal|"      s.s_suppkey = l.l_suppkey\n"
operator|+
literal|"      and ps.ps_suppkey = l.l_suppkey\n"
operator|+
literal|"      and ps.ps_partkey = l.l_partkey\n"
operator|+
literal|"      and p.p_partkey = l.l_partkey\n"
operator|+
literal|"      and o.o_orderkey = l.l_orderkey\n"
operator|+
literal|"      and s.s_nationkey = n.n_nationkey\n"
operator|+
literal|"      and p.p_name like '%yellow%'\n"
operator|+
literal|"  ) as profit\n"
operator|+
literal|"group by\n"
operator|+
literal|"  nation,\n"
operator|+
literal|"  o_year\n"
operator|+
literal|"order by\n"
operator|+
literal|"  nation,\n"
operator|+
literal|"  o_year desc"
argument_list|,
comment|// 10
literal|"select\n"
operator|+
literal|"  c.c_custkey,\n"
operator|+
literal|"  c.c_name,\n"
operator|+
literal|"  sum(l.l_extendedprice * (1 - l.l_discount)) as revenue,\n"
operator|+
literal|"  c.c_acctbal,\n"
operator|+
literal|"  n.n_name,\n"
operator|+
literal|"  c.c_address,\n"
operator|+
literal|"  c.c_phone,\n"
operator|+
literal|"  c.c_comment\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.customer c,\n"
operator|+
literal|"  tpch.orders o,\n"
operator|+
literal|"  tpch.lineitem l,\n"
operator|+
literal|"  tpch.nation n\n"
operator|+
literal|"where\n"
operator|+
literal|"  c.c_custkey = o.o_custkey\n"
operator|+
literal|"  and l.l_orderkey = o.o_orderkey\n"
operator|+
literal|"  and o.o_orderdate>= date '1994-03-01'\n"
operator|+
literal|"  and o.o_orderdate< date '1994-03-01' + interval '3' month\n"
operator|+
literal|"  and l.l_returnflag = 'R'\n"
operator|+
literal|"  and c.c_nationkey = n.n_nationkey\n"
operator|+
literal|"group by\n"
operator|+
literal|"  c.c_custkey,\n"
operator|+
literal|"  c.c_name,\n"
operator|+
literal|"  c.c_acctbal,\n"
operator|+
literal|"  c.c_phone,\n"
operator|+
literal|"  n.n_name,\n"
operator|+
literal|"  c.c_address,\n"
operator|+
literal|"  c.c_comment\n"
operator|+
literal|"order by\n"
operator|+
literal|"  revenue desc\n"
operator|+
literal|"limit 20"
argument_list|,
comment|// 11
literal|"select\n"
operator|+
literal|"  ps.ps_partkey,\n"
operator|+
literal|"  sum(ps.ps_supplycost * ps.ps_availqty) as \"value\"\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.partsupp ps,\n"
operator|+
literal|"  tpch.supplier s,\n"
operator|+
literal|"  tpch.nation n\n"
operator|+
literal|"where\n"
operator|+
literal|"  ps.ps_suppkey = s.s_suppkey\n"
operator|+
literal|"  and s.s_nationkey = n.n_nationkey\n"
operator|+
literal|"  and n.n_name = 'JAPAN'\n"
operator|+
literal|"group by\n"
operator|+
literal|"  ps.ps_partkey having\n"
operator|+
literal|"    sum(ps.ps_supplycost * ps.ps_availqty)> (\n"
operator|+
literal|"      select\n"
operator|+
literal|"        sum(ps.ps_supplycost * ps.ps_availqty) * 0.0001000000\n"
operator|+
literal|"      from\n"
operator|+
literal|"        tpch.partsupp ps,\n"
operator|+
literal|"        tpch.supplier s,\n"
operator|+
literal|"        tpch.nation n\n"
operator|+
literal|"      where\n"
operator|+
literal|"        ps.ps_suppkey = s.s_suppkey\n"
operator|+
literal|"        and s.s_nationkey = n.n_nationkey\n"
operator|+
literal|"        and n.n_name = 'JAPAN'\n"
operator|+
literal|"    )\n"
operator|+
literal|"order by\n"
operator|+
literal|"  \"value\" desc"
argument_list|,
comment|// 12
literal|"select\n"
operator|+
literal|"  l.l_shipmode,\n"
operator|+
literal|"  sum(case\n"
operator|+
literal|"    when o.o_orderpriority = '1-URGENT'\n"
operator|+
literal|"      or o.o_orderpriority = '2-HIGH'\n"
operator|+
literal|"      then 1\n"
operator|+
literal|"    else 0\n"
operator|+
literal|"  end) as high_line_count,\n"
operator|+
literal|"  sum(case\n"
operator|+
literal|"    when o.o_orderpriority<> '1-URGENT'\n"
operator|+
literal|"      and o.o_orderpriority<> '2-HIGH'\n"
operator|+
literal|"      then 1\n"
operator|+
literal|"    else 0\n"
operator|+
literal|"  end) as low_line_count\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.orders o,\n"
operator|+
literal|"  tpch.lineitem l\n"
operator|+
literal|"where\n"
operator|+
literal|"  o.o_orderkey = l.l_orderkey\n"
operator|+
literal|"  and l.l_shipmode in ('TRUCK', 'REG AIR')\n"
operator|+
literal|"  and l.l_commitdate< l.l_receiptdate\n"
operator|+
literal|"  and l.l_shipdate< l.l_commitdate\n"
operator|+
literal|"--  and l.l_receiptdate>= date '1994-01-01'\n"
operator|+
literal|"--  and l.l_receiptdate< date '1994-01-01' + interval '1' year\n"
operator|+
literal|"group by\n"
operator|+
literal|"  l.l_shipmode\n"
operator|+
literal|"order by\n"
operator|+
literal|"  l.l_shipmode"
argument_list|,
comment|// 13
literal|"select\n"
operator|+
literal|"  c_count,\n"
operator|+
literal|"  count(*) as custdist\n"
operator|+
literal|"from\n"
operator|+
literal|"  (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      c.c_custkey,\n"
operator|+
literal|"      count(o.o_orderkey)\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.customer c \n"
operator|+
literal|"      left outer join tpch.orders o \n"
operator|+
literal|"        on c.c_custkey = o.o_custkey\n"
operator|+
literal|"        and o.o_comment not like '%special%requests%'\n"
operator|+
literal|"    group by\n"
operator|+
literal|"      c.c_custkey\n"
operator|+
literal|"  ) as orders (c_custkey, c_count)\n"
operator|+
literal|"group by\n"
operator|+
literal|"  c_count\n"
operator|+
literal|"order by\n"
operator|+
literal|"  custdist desc,\n"
operator|+
literal|"  c_count desc"
argument_list|,
comment|// 14
literal|"select\n"
operator|+
literal|"  100.00 * sum(case\n"
operator|+
literal|"    when p.p_type like 'PROMO%'\n"
operator|+
literal|"      then l.l_extendedprice * (1 - l.l_discount)\n"
operator|+
literal|"    else 0\n"
operator|+
literal|"  end) / sum(l.l_extendedprice * (1 - l.l_discount)) as promo_revenue\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.lineitem l,\n"
operator|+
literal|"  tpch.part p\n"
operator|+
literal|"where\n"
operator|+
literal|"  l.l_partkey = p.p_partkey\n"
operator|+
literal|"  and l.l_shipdate>= date '1994-08-01'\n"
operator|+
literal|"  and l.l_shipdate< date '1994-08-01' + interval '1' month"
argument_list|,
comment|// 15
literal|"with revenue0 (supplier_no, total_revenue) as (\n"
operator|+
literal|"  select\n"
operator|+
literal|"    l_suppkey,\n"
operator|+
literal|"    sum(l_extendedprice * (1 - l_discount))\n"
operator|+
literal|"  from\n"
operator|+
literal|"    tpch.lineitem\n"
operator|+
literal|"  where\n"
operator|+
literal|"    l_shipdate>= date '1993-05-01'\n"
operator|+
literal|"    and l_shipdate< date '1993-05-01' + interval '3' month\n"
operator|+
literal|"  group by\n"
operator|+
literal|"    l_suppkey)\n"
operator|+
literal|"select\n"
operator|+
literal|"  s.s_suppkey,\n"
operator|+
literal|"  s.s_name,\n"
operator|+
literal|"  s.s_address,\n"
operator|+
literal|"  s.s_phone,\n"
operator|+
literal|"  r.total_revenue\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.supplier s,\n"
operator|+
literal|"  revenue0 r\n"
operator|+
literal|"where\n"
operator|+
literal|"  s.s_suppkey = r.supplier_no\n"
operator|+
literal|"  and r.total_revenue = (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      max(total_revenue)\n"
operator|+
literal|"    from\n"
operator|+
literal|"      revenue0\n"
operator|+
literal|"  )\n"
operator|+
literal|"order by\n"
operator|+
literal|"  s.s_suppkey"
argument_list|,
comment|// 16
literal|"select\n"
operator|+
literal|"  p.p_brand,\n"
operator|+
literal|"  p.p_type,\n"
operator|+
literal|"  p.p_size,\n"
operator|+
literal|"  count(distinct ps.ps_suppkey) as supplier_cnt\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.partsupp ps,\n"
operator|+
literal|"  tpch.part p\n"
operator|+
literal|"where\n"
operator|+
literal|"  p.p_partkey = ps.ps_partkey\n"
operator|+
literal|"  and p.p_brand<> 'Brand#21'\n"
operator|+
literal|"  and p.p_type not like 'MEDIUM PLATED%'\n"
operator|+
literal|"  and p.p_size in (38, 2, 8, 31, 44, 5, 14, 24)\n"
operator|+
literal|"  and ps.ps_suppkey not in (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      s_suppkey\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.supplier\n"
operator|+
literal|"    where\n"
operator|+
literal|"      s_comment like '%Customer%Complaints%'\n"
operator|+
literal|"  )\n"
operator|+
literal|"group by\n"
operator|+
literal|"  p.p_brand,\n"
operator|+
literal|"  p.p_type,\n"
operator|+
literal|"  p.p_size\n"
operator|+
literal|"order by\n"
operator|+
literal|"  supplier_cnt desc,\n"
operator|+
literal|"  p.p_brand,\n"
operator|+
literal|"  p.p_type,\n"
operator|+
literal|"  p.p_size"
argument_list|,
comment|// 17
literal|"select\n"
operator|+
literal|"  sum(l.l_extendedprice) / 7.0 as avg_yearly\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.lineitem l,\n"
operator|+
literal|"  tpch.part p\n"
operator|+
literal|"where\n"
operator|+
literal|"  p.p_partkey = l.l_partkey\n"
operator|+
literal|"  and p.p_brand = 'Brand#13'\n"
operator|+
literal|"  and p.p_container = 'JUMBO CAN'\n"
operator|+
literal|"  and l.l_quantity< (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      0.2 * avg(l2.l_quantity)\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.lineitem l2\n"
operator|+
literal|"    where\n"
operator|+
literal|"      l2.l_partkey = p.p_partkey\n"
operator|+
literal|"  )"
argument_list|,
comment|// 18
literal|"select\n"
operator|+
literal|"  c.c_name,\n"
operator|+
literal|"  c.c_custkey,\n"
operator|+
literal|"  o.o_orderkey,\n"
operator|+
literal|"  o.o_orderdate,\n"
operator|+
literal|"  o.o_totalprice,\n"
operator|+
literal|"  sum(l.l_quantity)\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.customer c,\n"
operator|+
literal|"  tpch.orders o,\n"
operator|+
literal|"  tpch.lineitem l\n"
operator|+
literal|"where\n"
operator|+
literal|"  o.o_orderkey in (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      l_orderkey\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.lineitem\n"
operator|+
literal|"    group by\n"
operator|+
literal|"      l_orderkey having\n"
operator|+
literal|"        sum(l_quantity)> 313\n"
operator|+
literal|"  )\n"
operator|+
literal|"  and c.c_custkey = o.o_custkey\n"
operator|+
literal|"  and o.o_orderkey = l.l_orderkey\n"
operator|+
literal|"group by\n"
operator|+
literal|"  c.c_name,\n"
operator|+
literal|"  c.c_custkey,\n"
operator|+
literal|"  o.o_orderkey,\n"
operator|+
literal|"  o.o_orderdate,\n"
operator|+
literal|"  o.o_totalprice\n"
operator|+
literal|"order by\n"
operator|+
literal|"  o.o_totalprice desc,\n"
operator|+
literal|"  o.o_orderdate\n"
operator|+
literal|"limit 100"
argument_list|,
comment|// 19
literal|"select\n"
operator|+
literal|"  sum(l.l_extendedprice* (1 - l.l_discount)) as revenue\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.lineitem l,\n"
operator|+
literal|"  tpch.part p\n"
operator|+
literal|"where\n"
operator|+
literal|"  (\n"
operator|+
literal|"    p.p_partkey = l.l_partkey\n"
operator|+
literal|"    and p.p_brand = 'Brand#41'\n"
operator|+
literal|"    and p.p_container in ('SM CASE', 'SM BOX', 'SM PACK', 'SM PKG')\n"
operator|+
literal|"    and l.l_quantity>= 2 and l.l_quantity<= 2 + 10\n"
operator|+
literal|"    and p.p_size between 1 and 5\n"
operator|+
literal|"    and l.l_shipmode in ('AIR', 'AIR REG')\n"
operator|+
literal|"    and l.l_shipinstruct = 'DELIVER IN PERSON'\n"
operator|+
literal|"  )\n"
operator|+
literal|"  or\n"
operator|+
literal|"  (\n"
operator|+
literal|"    p.p_partkey = l.l_partkey\n"
operator|+
literal|"    and p.p_brand = 'Brand#13'\n"
operator|+
literal|"    and p.p_container in ('MED BAG', 'MED BOX', 'MED PKG', 'MED PACK')\n"
operator|+
literal|"    and l.l_quantity>= 14 and l.l_quantity<= 14 + 10\n"
operator|+
literal|"    and p.p_size between 1 and 10\n"
operator|+
literal|"    and l.l_shipmode in ('AIR', 'AIR REG')\n"
operator|+
literal|"    and l.l_shipinstruct = 'DELIVER IN PERSON'\n"
operator|+
literal|"  )\n"
operator|+
literal|"  or\n"
operator|+
literal|"  (\n"
operator|+
literal|"    p.p_partkey = l.l_partkey\n"
operator|+
literal|"    and p.p_brand = 'Brand#55'\n"
operator|+
literal|"    and p.p_container in ('LG CASE', 'LG BOX', 'LG PACK', 'LG PKG')\n"
operator|+
literal|"    and l.l_quantity>= 23 and l.l_quantity<= 23 + 10\n"
operator|+
literal|"    and p.p_size between 1 and 15\n"
operator|+
literal|"    and l.l_shipmode in ('AIR', 'AIR REG')\n"
operator|+
literal|"    and l.l_shipinstruct = 'DELIVER IN PERSON'\n"
operator|+
literal|"  )"
argument_list|,
comment|// 20
literal|"select\n"
operator|+
literal|"  s.s_name,\n"
operator|+
literal|"  s.s_address\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.supplier s,\n"
operator|+
literal|"  tpch.nation n\n"
operator|+
literal|"where\n"
operator|+
literal|"  s.s_suppkey in (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      ps.ps_suppkey\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.partsupp ps\n"
operator|+
literal|"    where\n"
operator|+
literal|"      ps. ps_partkey in (\n"
operator|+
literal|"        select\n"
operator|+
literal|"          p.p_partkey\n"
operator|+
literal|"        from\n"
operator|+
literal|"          tpch.part p\n"
operator|+
literal|"        where\n"
operator|+
literal|"          p.p_name like 'antique%'\n"
operator|+
literal|"      )\n"
operator|+
literal|"      and ps.ps_availqty> (\n"
operator|+
literal|"        select\n"
operator|+
literal|"          0.5 * sum(l.l_quantity)\n"
operator|+
literal|"        from\n"
operator|+
literal|"          tpch.lineitem l\n"
operator|+
literal|"        where\n"
operator|+
literal|"          l.l_partkey = ps.ps_partkey\n"
operator|+
literal|"          and l.l_suppkey = ps.ps_suppkey\n"
operator|+
literal|"          and l.l_shipdate>= date '1993-01-01'\n"
operator|+
literal|"          and l.l_shipdate< date '1993-01-01' + interval '1' year\n"
operator|+
literal|"      )\n"
operator|+
literal|"  )\n"
operator|+
literal|"  and s.s_nationkey = n.n_nationkey\n"
operator|+
literal|"  and n.n_name = 'KENYA'\n"
operator|+
literal|"order by\n"
operator|+
literal|"  s.s_name"
argument_list|,
comment|// 21
literal|"select\n"
operator|+
literal|"  s.s_name,\n"
operator|+
literal|"  count(*) as numwait\n"
operator|+
literal|"from\n"
operator|+
literal|"  tpch.supplier s,\n"
operator|+
literal|"  tpch.lineitem l1,\n"
operator|+
literal|"  tpch.orders o,\n"
operator|+
literal|"  tpch.nation n\n"
operator|+
literal|"where\n"
operator|+
literal|"  s.s_suppkey = l1.l_suppkey\n"
operator|+
literal|"  and o.o_orderkey = l1.l_orderkey\n"
operator|+
literal|"  and o.o_orderstatus = 'F'\n"
operator|+
literal|"  and l1.l_receiptdate> l1.l_commitdate\n"
operator|+
literal|"  and exists (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      *\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.lineitem l2\n"
operator|+
literal|"    where\n"
operator|+
literal|"      l2.l_orderkey = l1.l_orderkey\n"
operator|+
literal|"      and l2.l_suppkey<> l1.l_suppkey\n"
operator|+
literal|"  )\n"
operator|+
literal|"  and not exists (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      *\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.lineitem l3\n"
operator|+
literal|"    where\n"
operator|+
literal|"      l3.l_orderkey = l1.l_orderkey\n"
operator|+
literal|"      and l3.l_suppkey<> l1.l_suppkey\n"
operator|+
literal|"      and l3.l_receiptdate> l3.l_commitdate\n"
operator|+
literal|"  )\n"
operator|+
literal|"  and s.s_nationkey = n.n_nationkey\n"
operator|+
literal|"  and n.n_name = 'BRAZIL'\n"
operator|+
literal|"group by\n"
operator|+
literal|"  s.s_name\n"
operator|+
literal|"order by\n"
operator|+
literal|"  numwait desc,\n"
operator|+
literal|"  s.s_name\n"
operator|+
literal|"limit 100"
argument_list|,
comment|// 22
literal|"select\n"
operator|+
literal|"  cntrycode,\n"
operator|+
literal|"  count(*) as numcust,\n"
operator|+
literal|"  sum(c_acctbal) as totacctbal\n"
operator|+
literal|"from\n"
operator|+
literal|"  (\n"
operator|+
literal|"    select\n"
operator|+
literal|"      substring(c_phone from 1 for 2) as cntrycode,\n"
operator|+
literal|"      c_acctbal\n"
operator|+
literal|"    from\n"
operator|+
literal|"      tpch.customer c\n"
operator|+
literal|"    where\n"
operator|+
literal|"      substring(c_phone from 1 for 2) in\n"
operator|+
literal|"        ('24', '31', '11', '16', '21', '20', '34')\n"
operator|+
literal|"      and c_acctbal> (\n"
operator|+
literal|"        select\n"
operator|+
literal|"          avg(c_acctbal)\n"
operator|+
literal|"        from\n"
operator|+
literal|"          tpch.customer\n"
operator|+
literal|"        where\n"
operator|+
literal|"          c_acctbal> 0.00\n"
operator|+
literal|"          and substring(c_phone from 1 for 2) in\n"
operator|+
literal|"            ('24', '31', '11', '16', '21', '20', '34')\n"
operator|+
literal|"      )\n"
operator|+
literal|"      and not exists (\n"
operator|+
literal|"        select\n"
operator|+
literal|"          *\n"
operator|+
literal|"        from\n"
operator|+
literal|"          tpch.orders o\n"
operator|+
literal|"        where\n"
operator|+
literal|"          o.o_custkey = c.c_custkey\n"
operator|+
literal|"      )\n"
operator|+
literal|"  ) as custsale\n"
operator|+
literal|"group by\n"
operator|+
literal|"  cntrycode\n"
operator|+
literal|"order by\n"
operator|+
literal|"  cntrycode"
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testRegion
parameter_list|()
block|{
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpch.region"
argument_list|)
operator|.
name|returnsUnordered
argument_list|(
literal|"R_REGIONKEY=0; R_NAME=AFRICA; R_COMMENT=lar deposits. blithely final packages cajole. regular waters are final requests. regular accounts are according to "
argument_list|,
literal|"R_REGIONKEY=1; R_NAME=AMERICA; R_COMMENT=hs use ironic, even requests. s"
argument_list|,
literal|"R_REGIONKEY=2; R_NAME=ASIA; R_COMMENT=ges. thinly even pinto beans ca"
argument_list|,
literal|"R_REGIONKEY=3; R_NAME=EUROPE; R_COMMENT=ly final courts cajole furiously final excuse"
argument_list|,
literal|"R_REGIONKEY=4; R_NAME=MIDDLE EAST; R_COMMENT=uickly special accounts cajole carefully blithely close requests. carefully final asymptotes haggle furiousl"
argument_list|)
expr_stmt|;
block|}
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
literal|"select * from tpch.lineitem"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|6001215
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOrders
parameter_list|()
block|{
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpch.orders"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|1500000
argument_list|)
expr_stmt|;
block|}
comment|/** Test case for    *<a href="https://issues.apache.org/jira/browse/CALCITE-1543">[CALCITE-1543]    * Correlated scalar sub-query with multiple aggregates gives    * AssertionError</a>. */
annotation|@
name|Ignore
argument_list|(
literal|"planning succeeds, but gives OutOfMemoryError during execution"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testDecorrelateScalarAggregate
parameter_list|()
block|{
specifier|final
name|String
name|sql
init|=
literal|"select sum(l_extendedprice)\n"
operator|+
literal|"from lineitem, part\n"
operator|+
literal|"where\n"
operator|+
literal|"     p_partkey = l_partkey\n"
operator|+
literal|"     and l_quantity> (\n"
operator|+
literal|"       select avg(l_quantity)\n"
operator|+
literal|"       from lineitem\n"
operator|+
literal|"       where l_partkey = p_partkey\n"
operator|+
literal|"    )\n"
decl_stmt|;
name|with
argument_list|()
operator|.
name|query
argument_list|(
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCustomer
parameter_list|()
block|{
name|with
argument_list|()
operator|.
name|query
argument_list|(
literal|"select * from tpch.customer"
argument_list|)
operator|.
name|returnsCount
argument_list|(
literal|150000
argument_list|)
expr_stmt|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|with
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
return|return
name|CalciteAssert
operator|.
name|model
argument_list|(
name|TPCH_MODEL
argument_list|)
operator|.
name|enable
argument_list|(
name|enable
argument_list|)
return|;
block|}
specifier|private
name|CalciteAssert
operator|.
name|AssertThat
name|with
parameter_list|()
block|{
comment|// Only run on JDK 1.7 or higher. The io.airlift.tpch library requires it.
comment|// Only run if slow tests are enabled; the library uses lots of memory.
return|return
name|with
argument_list|(
name|ENABLE
argument_list|)
return|;
block|}
comment|/** Tests the customer table with scale factor 5. */
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
literal|"select * from tpch_5.customer"
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
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery02
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery02Conversion
parameter_list|()
block|{
name|query
argument_list|(
literal|2
argument_list|,
literal|true
argument_list|)
operator|.
name|enable
argument_list|(
name|ENABLE
argument_list|)
operator|.
name|convertMatches
argument_list|(
name|relNode
lambda|->
block|{
name|String
name|s
init|=
name|RelOptUtil
operator|.
name|toString
argument_list|(
name|relNode
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|s
argument_list|,
name|not
argument_list|(
name|containsString
argument_list|(
literal|"Correlator"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery03
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"NoSuchMethodException: SqlFunctions.lt(Date, Date)"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery04
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|4
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"OutOfMemoryError"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery05
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery06
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|6
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery07
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|7
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery08
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|8
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"no method found"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery09
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|9
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery10
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"CannotPlanException"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery11
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|11
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"NoSuchMethodException: SqlFunctions.lt(Date, Date)"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery12
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|12
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"CannotPlanException"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery13
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|13
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery14
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|14
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"AssertionError"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery15
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|15
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery16
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|16
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery17
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|17
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery18
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|18
argument_list|)
expr_stmt|;
block|}
comment|// a bit slow
annotation|@
name|Test
specifier|public
name|void
name|testQuery19
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|19
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testQuery20
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|20
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"slow"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery21
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|21
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"IllegalArgumentException during decorrelation"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|testQuery22
parameter_list|()
block|{
name|checkQuery
argument_list|(
literal|22
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkQuery
parameter_list|(
name|int
name|i
parameter_list|)
block|{
name|query
argument_list|(
name|i
argument_list|,
literal|null
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
comment|/** Runs with query #i.    *    * @param i Ordinal of query, per the benchmark, 1-based    * @param enable Whether to enable query execution.    *     If null, use the value of {@link #ENABLE}.    *     Pass true only for 'fast' tests that do not read any data.    */
specifier|private
name|CalciteAssert
operator|.
name|AssertQuery
name|query
parameter_list|(
name|int
name|i
parameter_list|,
name|Boolean
name|enable
parameter_list|)
block|{
return|return
name|with
argument_list|(
name|Util
operator|.
name|first
argument_list|(
name|enable
argument_list|,
name|ENABLE
argument_list|)
argument_list|)
operator|.
name|query
argument_list|(
name|QUERIES
operator|.
name|get
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"tpch\\."
argument_list|,
literal|"tpch_01."
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

begin_comment
comment|// End TpchTest.java
end_comment

end_unit

