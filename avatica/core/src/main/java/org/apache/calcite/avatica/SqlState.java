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
name|avatica
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|Map
import|;
end_import

begin_comment
comment|/**  * SQL error codes.  *  *<p>Based upon Table 33 â SQLSTATE class and subclass values in SQL:2014 section 24.1, which is  * as follows.  *  *<table border=1>  *<caption>Table 33 â SQLSTATE class and subclass values</caption>  *<tr>  *<th>Category</th>  *<th>Condition</th>  *<th>Class</th>  *<th>Subcondition</th>  *<th>Subclass</th>  *</tr>  *<tr>  *<td>X</td>  *<td>ambiguous cursor name</td>  *<td>3C</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>attempt to assign to non-updatable column</td>  *<td>0U</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>attempt to assign to ordering column</td>  *<td>0V</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>cli specific condition</td>  *<td>HY</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>cardinality violation</td>  *<td>21</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>connection exception</td>  *<td>08</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>connection does not exist</td>  *<td>003</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>connection failure</td>  *<td>006</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>connection name in use</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>SQL-client unable to establish SQL-connection</td>  *<td>001</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>SQL-server rejected establishment of SQL-connection</td>  *<td>004</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>transaction resolution unknown</td>  *<td>007</td>  *</tr>  *<tr>  *<td>X</td>  *<td>cursor sensitivity exception</td>  *<td>36</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>request failed</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>request rejected</td>  *<td>001</td>  *</tr>  *<tr>  *<td>X</td>  *<td>data exception</td>  *<td>22</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>array data, right truncation</td>  *<td>02F</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>array element error</td>  *<td>02E</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>attempt to replace a zero-length string</td>  *<td>01U</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>character not in repertoire</td>  *<td>021</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>datetime field overflow</td>  *<td>008</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>division by zero</td>  *<td>012</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>error in assignment</td>  *<td>005</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>escape character conflict</td>  *<td>00B</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>indicator overflow</td>  *<td>022</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>interval field overflow</td>  *<td>015</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>interval value out of range</td>  *<td>00P</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid argument for natural logarithm</td>  *<td>01E</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid argument for NTILE function</td>  *<td>014</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid argument for NTH_VALUE function</td>  *<td>016</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid argument for power function</td>  *<td>01F</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid argument for row pattern navigation operation</td>  *<td>02J</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid argument for width bucket function</td>  *<td>01G</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid character value for cast</td>  *<td>018</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid datetime format</td>  *<td>007</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid escape character</td>  *<td>019</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid escape octet</td>  *<td>00D</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid escape sequence</td>  *<td>025</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid indicator parameter value</td>  *<td>010</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid interval format</td>  *<td>006</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid parameter value</td>  *<td>023</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid period value</td>  *<td>020</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid preceding or following size in window function</td>  *<td>013</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid regular expression</td>  *<td>01B</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid repeat argument in a sample clause</td>  *<td>02G</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid row count in fetch first clause</td>  *<td>01W</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid row count in result offset clause</td>  *<td>01X</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid row version</td>  *<td>01H</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid sample size</td>  *<td>02H</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid time zone displacement value</td>  *<td>009</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid use of escape character</td>  *<td>00C</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid XQuery option flag</td>  *<td>01T</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid XQuery regular expression</td>  *<td>01S</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid XQuery replacement string</td>  *<td>01V</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>most specific type mismatch</td>  *<td>00G</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>multiset value overflow</td>  *<td>00Q</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>noncharacter in UCS string</td>  *<td>029</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>null value substituted for mutator subject parameter</td>  *<td>02D</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>null row not permitted in table</td>  *<td>01C</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>null value in array target</td>  *<td>00E</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>null value, no indicator parameter</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>null value not allowed</td>  *<td>004</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>numeric value out of range</td>  *<td>003</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>sequence generator limit exceeded</td>  *<td>00H</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>skip to non-existent row</td>  *<td>02K</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>skip to first row of match</td>  *<td>02L</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>string data, length mismatch</td>  *<td>026</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>string data, right truncation</td>  *<td>001</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>substring error</td>  *<td>011</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>trim error</td>  *<td>027</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>unterminated C string</td>  *<td>024</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>zero-length character string</td>  *<td>00F</td>  *</tr>  *<tr>  *<td>X</td>  *<td>dependent privilege descriptors still exist</td>  *<td>2B</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>diagnostics exception</td>  *<td>0Z</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>maximum number of stacked diagnostics areas exceeded</td>  *<td>001</td>  *</tr>  *<tr>  *<td>X</td>  *<td>dynamic SQL error</td>  *<td>07</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>cursor specification cannot be executed</td>  *<td>003</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>data type transform function violation</td>  *<td>00B</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid DATA target</td>  *<td>00D</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid DATETIME_INTERVAL_CODE</td>  *<td>00F</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid descriptor count</td>  *<td>008</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid descriptor index</td>  *<td>009</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid LEVEL value</td>  *<td>00E</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>prepared statement not a cursor specification</td>  *<td>005</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>restricted data type attribute violation</td>  *<td>006</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>undefined DATA value</td>  *<td>00C</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>using clause does not match dynamic parameter specifications</td>  *<td>001</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>using clause does not match target specifications</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>using clause required for dynamic parameters</td>  *<td>004</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>using clause required for result fields</td>  *<td>007</td>  *</tr>  *<tr>  *<td>X</td>  *<td>external routine exception</td>  *<td>38</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>containing SQL not permitted</td>  *<td>001</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>modifying SQL-data not permitted</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>prohibited SQL-statement attempted</td>  *<td>003</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>reading SQL-data not permitted</td>  *<td>004</td>  *</tr>  *<tr>  *<td>X</td>  *<td>external routine invocation exception</td>  *<td>39</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>null value not allowed</td>  *<td>004</td>  *</tr>  *<tr>  *<td>X</td>  *<td>feature not supported</td>  *<td>0A</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>multiple server transactions</td>  *<td>001</td>  *</tr>  *<tr>  *<td>X</td>  *<td>integrity constraint violation</td>  *<td>23</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>restrict violation</td>  *<td>001</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid authorization specification</td>  *<td>28</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid catalog name</td>  *<td>3D</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid character set name</td>  *<td>2C</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>cannot drop SQL-session default character set</td>  *<td>001</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid condition number</td>  *<td>35</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid connection name</td>  *<td>2E</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid cursor name</td>  *<td>34</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid cursor state</td>  *<td>24</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid grantor</td>  *<td>0L</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid role specification</td>  *<td>0P</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid schema name</td>  *<td>3F</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid schema name list specification</td>  *<td>0E</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid collation name</td>  *<td>2H</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid SQL descriptor name</td>  *<td>33</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid SQL-invoked procedure reference</td>  *<td>0M</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid SQL statement name</td>  *<td>26</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid SQL statement identifier</td>  *<td>30</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid target type specification</td>  *<td>0D</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid transaction state</td>  *<td>25</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>active SQL-transaction</td>  *<td>001</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>branch transaction already active</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>held cursor requires same isolation level</td>  *<td>008</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>inappropriate access mode for branch transaction</td>  *<td>003</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>inappropriate isolation level for branch transaction</td>  *<td>004</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>no active SQL-transaction for branch transaction</td>  *<td>005</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>read-only SQL-transaction</td>  *<td>006</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>schema and data statement mixing not supported</td>  *<td>007</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid transaction termination</td>  *<td>2D</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>invalid transform group name specification</td>  *<td>0S</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>locator exception</td>  *<td>0F</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid specification</td>  *<td>001</td>  *</tr>  *<tr>  *<td>N</td>  *<td>no data</td>  *<td>02</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>no additional result sets returned</td>  *<td>001</td>  *</tr>  *<tr>  *<td>X</td>  *<td>prohibited statement encountered during trigger execution</td>  *<td>0W</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>modify table modified by data change delta table</td>  *<td>001</td>  *</tr>  *<tr>  *<td>X</td>  *<td>Remote Database Access</td>  *<td>HZ</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>savepoint exception</td>  *<td>3B</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid specification</td>  *<td>001</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>too many</td>  *<td>002</td>  *</tr>  *<tr>  *<td>X</td>  *<td>SQL routine exception</td>  *<td>2F</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>function executed no return statement</td>  *<td>005</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>modifying SQL-data not permitted</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>prohibited SQL-statement attempted</td>  *<td>003</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>reading SQL-data not permitted</td>  *<td>004</td>  *</tr>  *<tr>  *<td>S</td>  *<td>successful completion</td>  *<td>00</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>syntax error or access rule violation</td>  *<td>42</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>syntax error or access rule violation in direct statement</td>  *<td>2A</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>syntax error or access rule violation in dynamic statement</td>  *<td>37</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>target table disagrees with cursor specification</td>  *<td>0T</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>transaction rollback</td>  *<td>40</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>integrity constraint violation</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>serialization failure</td>  *<td>001</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>statement completion unknown</td>  *<td>003</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>triggered action exception</td>  *<td>004</td>  *</tr>  *<tr>  *<td>X</td>  *<td>triggered action exception</td>  *<td>09</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>X</td>  *<td>triggered data change violation</td>  *<td>27</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>modify table modified by data change delta table</td>  *<td>001</td>  *</tr>  *<tr>  *<td>W</td>  *<td>warning</td>  *<td>01</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>additional result sets returned</td>  *<td>00D</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>array data, right truncation</td>  *<td>02F</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>attempt to return too many result sets</td>  *<td>00E</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>cursor operation conflict</td>  *<td>001</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>default value too long for information schema</td>  *<td>00B</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>disconnect error</td>  *<td>002</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>insufficient item descriptor areas</td>  *<td>005</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>invalid number of conditions</td>  *<td>012</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>null value eliminated in set function</td>  *<td>003</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>privilege not granted</td>  *<td>007</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>privilege not revoked</td>  *<td>006</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>query expression too long for information schema</td>  *<td>00A</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>result sets returned</td>  *<td>00C</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>search condition too long for information schema</td>  *<td>009</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>statement too long for information schema</td>  *<td>00F</td>  *</tr>  *<tr>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>&nbsp;</td>  *<td>string data, right truncation</td>  *<td>004</td>  *</tr>  *<tr>  *<td>X</td>  *<td>with check option violation</td>  *<td>44</td>  *<td>(no subclass)</td>  *<td>000</td>  *</tr>  *</table>  */
end_comment

begin_enum
specifier|public
enum|enum
name|SqlState
block|{
comment|/** 3C000: ambiguous cursor name */
name|AMBIGUOUS_CURSOR_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"ambiguous cursor name"
argument_list|,
literal|"3C"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0U000: attempt to assign to non-updatable column */
name|ATTEMPT_TO_ASSIGN_TO_NON_UPDATABLE_COLUMN_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"attempt to assign to non-updatable column"
argument_list|,
literal|"0U"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0V000: attempt to assign to ordering column */
name|ATTEMPT_TO_ASSIGN_TO_ORDERING_COLUMN_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"attempt to assign to ordering column"
argument_list|,
literal|"0V"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** HY000: cli specific condition */
name|CLI_SPECIFIC_CONDITION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"cli specific condition"
argument_list|,
literal|"HY"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 21000: cardinality violation */
name|CARDINALITY_VIOLATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"cardinality violation"
argument_list|,
literal|"21"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 08000: connection exception */
name|CONNECTION_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"connection exception"
argument_list|,
literal|"08"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 08003: connection exception: connection does not exist */
name|CONNECTION_EXCEPTION_CONNECTION_DOES_NOT_EXIST
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"connection exception"
argument_list|,
literal|"08"
argument_list|,
literal|"connection does not exist"
argument_list|,
literal|"003"
argument_list|)
block|,
comment|/** 08006: connection exception: connection failure */
name|CONNECTION_EXCEPTION_CONNECTION_FAILURE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"connection exception"
argument_list|,
literal|"08"
argument_list|,
literal|"connection failure"
argument_list|,
literal|"006"
argument_list|)
block|,
comment|/** 08002: connection exception: connection name in use */
name|CONNECTION_EXCEPTION_CONNECTION_NAME_IN_USE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"connection exception"
argument_list|,
literal|"08"
argument_list|,
literal|"connection name in use"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 08001: connection exception: SQL-client unable to establish SQL-connection */
name|CONNECTION_EXCEPTION_SQLCLIENT_UNABLE_TO_ESTABLISH_SQLCONNECTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"connection exception"
argument_list|,
literal|"08"
argument_list|,
literal|"SQL-client unable to establish SQL-connection"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 08004: connection exception: SQL-server rejected establishment of SQL-connection */
name|CONNECTION_EXCEPTION_SQLSERVER_REJECTED_ESTABLISHMENT_OF_SQLCONNECTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"connection exception"
argument_list|,
literal|"08"
argument_list|,
literal|"SQL-server rejected establishment of SQL-connection"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 08007: connection exception: transaction resolution unknown */
name|CONNECTION_EXCEPTION_TRANSACTION_RESOLUTION_UNKNOWN
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"connection exception"
argument_list|,
literal|"08"
argument_list|,
literal|"transaction resolution unknown"
argument_list|,
literal|"007"
argument_list|)
block|,
comment|/** 36000: cursor sensitivity exception */
name|CURSOR_SENSITIVITY_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"cursor sensitivity exception"
argument_list|,
literal|"36"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 36002: cursor sensitivity exception: request failed */
name|CURSOR_SENSITIVITY_EXCEPTION_REQUEST_FAILED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"cursor sensitivity exception"
argument_list|,
literal|"36"
argument_list|,
literal|"request failed"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 36001: cursor sensitivity exception: request rejected */
name|CURSOR_SENSITIVITY_EXCEPTION_REQUEST_REJECTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"cursor sensitivity exception"
argument_list|,
literal|"36"
argument_list|,
literal|"request rejected"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 22000: data exception */
name|DATA_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 2202F: data exception: array data, right truncation */
name|DATA_EXCEPTION_ARRAY_DATA_RIGHT_TRUNCATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"array data, right truncation"
argument_list|,
literal|"02F"
argument_list|)
block|,
comment|/** 2202E: data exception: array element error */
name|DATA_EXCEPTION_ARRAY_ELEMENT_ERROR
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"array element error"
argument_list|,
literal|"02E"
argument_list|)
block|,
comment|/** 2201U: data exception: attempt to replace a zero-length string */
name|DATA_EXCEPTION_ATTEMPT_TO_REPLACE_A_ZERO_LENGTH_STRING
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"attempt to replace a zero-length string"
argument_list|,
literal|"01U"
argument_list|)
block|,
comment|/** 22021: data exception: character not in repertoire */
name|DATA_EXCEPTION_CHARACTER_NOT_IN_REPERTOIRE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"character not in repertoire"
argument_list|,
literal|"021"
argument_list|)
block|,
comment|/** 22008: data exception: datetime field overflow */
name|DATA_EXCEPTION_DATETIME_FIELD_OVERFLOW
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"datetime field overflow"
argument_list|,
literal|"008"
argument_list|)
block|,
comment|/** 22012: data exception: division by zero */
name|DATA_EXCEPTION_DIVISION_BY_ZERO
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"division by zero"
argument_list|,
literal|"012"
argument_list|)
block|,
comment|/** 22005: data exception: error in assignment */
name|DATA_EXCEPTION_ERROR_IN_ASSIGNMENT
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"error in assignment"
argument_list|,
literal|"005"
argument_list|)
block|,
comment|/** 2200B: data exception: escape character conflict */
name|DATA_EXCEPTION_ESCAPE_CHARACTER_CONFLICT
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"escape character conflict"
argument_list|,
literal|"00B"
argument_list|)
block|,
comment|/** 22022: data exception: indicator overflow */
name|DATA_EXCEPTION_INDICATOR_OVERFLOW
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"indicator overflow"
argument_list|,
literal|"022"
argument_list|)
block|,
comment|/** 22015: data exception: interval field overflow */
name|DATA_EXCEPTION_INTERVAL_FIELD_OVERFLOW
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"interval field overflow"
argument_list|,
literal|"015"
argument_list|)
block|,
comment|/** 2200P: data exception: interval value out of range */
name|DATA_EXCEPTION_INTERVAL_VALUE_OUT_OF_RANGE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"interval value out of range"
argument_list|,
literal|"00P"
argument_list|)
block|,
comment|/** 2201E: data exception: invalid argument for natural logarithm */
name|DATA_EXCEPTION_INVALID_ARGUMENT_FOR_NATURAL_LOGARITHM
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid argument for natural logarithm"
argument_list|,
literal|"01E"
argument_list|)
block|,
comment|/** 22014: data exception: invalid argument for NTILE function */
name|DATA_EXCEPTION_INVALID_ARGUMENT_FOR_NTILE_FUNCTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid argument for NTILE function"
argument_list|,
literal|"014"
argument_list|)
block|,
comment|/** 22016: data exception: invalid argument for NTH_VALUE function */
name|DATA_EXCEPTION_INVALID_ARGUMENT_FOR_NTH_VALUE_FUNCTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid argument for NTH_VALUE function"
argument_list|,
literal|"016"
argument_list|)
block|,
comment|/** 2201F: data exception: invalid argument for power function */
name|DATA_EXCEPTION_INVALID_ARGUMENT_FOR_POWER_FUNCTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid argument for power function"
argument_list|,
literal|"01F"
argument_list|)
block|,
comment|/** 2202J: data exception: invalid argument for row pattern navigation operation */
name|DATA_EXCEPTION_INVALID_ARGUMENT_FOR_ROW_PATTERN_NAVIGATION_OPERATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid argument for row pattern navigation operation"
argument_list|,
literal|"02J"
argument_list|)
block|,
comment|/** 2201G: data exception: invalid argument for width bucket function */
name|DATA_EXCEPTION_INVALID_ARGUMENT_FOR_WIDTH_BUCKET_FUNCTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid argument for width bucket function"
argument_list|,
literal|"01G"
argument_list|)
block|,
comment|/** 22018: data exception: invalid character value for cast */
name|DATA_EXCEPTION_INVALID_CHARACTER_VALUE_FOR_CAST
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid character value for cast"
argument_list|,
literal|"018"
argument_list|)
block|,
comment|/** 22007: data exception: invalid datetime format */
name|DATA_EXCEPTION_INVALID_DATETIME_FORMAT
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid datetime format"
argument_list|,
literal|"007"
argument_list|)
block|,
comment|/** 22019: data exception: invalid escape character */
name|DATA_EXCEPTION_INVALID_ESCAPE_CHARACTER
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid escape character"
argument_list|,
literal|"019"
argument_list|)
block|,
comment|/** 2200D: data exception: invalid escape octet */
name|DATA_EXCEPTION_INVALID_ESCAPE_OCTET
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid escape octet"
argument_list|,
literal|"00D"
argument_list|)
block|,
comment|/** 22025: data exception: invalid escape sequence */
name|DATA_EXCEPTION_INVALID_ESCAPE_SEQUENCE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid escape sequence"
argument_list|,
literal|"025"
argument_list|)
block|,
comment|/** 22010: data exception: invalid indicator parameter value */
name|DATA_EXCEPTION_INVALID_INDICATOR_PARAMETER_VALUE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid indicator parameter value"
argument_list|,
literal|"010"
argument_list|)
block|,
comment|/** 22006: data exception: invalid interval format */
name|DATA_EXCEPTION_INVALID_INTERVAL_FORMAT
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid interval format"
argument_list|,
literal|"006"
argument_list|)
block|,
comment|/** 22023: data exception: invalid parameter value */
name|DATA_EXCEPTION_INVALID_PARAMETER_VALUE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid parameter value"
argument_list|,
literal|"023"
argument_list|)
block|,
comment|/** 22020: data exception: invalid period value */
name|DATA_EXCEPTION_INVALID_PERIOD_VALUE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid period value"
argument_list|,
literal|"020"
argument_list|)
block|,
comment|/** 22013: data exception: invalid preceding or following size in window function */
name|DATA_EXCEPTION_INVALID_PRECEDING_OR_FOLLOWING_SIZE_IN_WINDOW_FUNCTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid preceding or following size in window function"
argument_list|,
literal|"013"
argument_list|)
block|,
comment|/** 2201B: data exception: invalid regular expression */
name|DATA_EXCEPTION_INVALID_REGULAR_EXPRESSION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid regular expression"
argument_list|,
literal|"01B"
argument_list|)
block|,
comment|/** 2202G: data exception: invalid repeat argument in a sample clause */
name|DATA_EXCEPTION_INVALID_REPEAT_ARGUMENT_IN_A_SAMPLE_CLAUSE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid repeat argument in a sample clause"
argument_list|,
literal|"02G"
argument_list|)
block|,
comment|/** 2201W: data exception: invalid row count in fetch first clause */
name|DATA_EXCEPTION_INVALID_ROW_COUNT_IN_FETCH_FIRST_CLAUSE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid row count in fetch first clause"
argument_list|,
literal|"01W"
argument_list|)
block|,
comment|/** 2201X: data exception: invalid row count in result offset clause */
name|DATA_EXCEPTION_INVALID_ROW_COUNT_IN_RESULT_OFFSET_CLAUSE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid row count in result offset clause"
argument_list|,
literal|"01X"
argument_list|)
block|,
comment|/** 2201H: data exception: invalid row version */
name|DATA_EXCEPTION_INVALID_ROW_VERSION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid row version"
argument_list|,
literal|"01H"
argument_list|)
block|,
comment|/** 2202H: data exception: invalid sample size */
name|DATA_EXCEPTION_INVALID_SAMPLE_SIZE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid sample size"
argument_list|,
literal|"02H"
argument_list|)
block|,
comment|/** 22009: data exception: invalid time zone displacement value */
name|DATA_EXCEPTION_INVALID_TIME_ZONE_DISPLACEMENT_VALUE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid time zone displacement value"
argument_list|,
literal|"009"
argument_list|)
block|,
comment|/** 2200C: data exception: invalid use of escape character */
name|DATA_EXCEPTION_INVALID_USE_OF_ESCAPE_CHARACTER
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid use of escape character"
argument_list|,
literal|"00C"
argument_list|)
block|,
comment|/** 2201T: data exception: invalid XQuery option flag */
name|DATA_EXCEPTION_INVALID_XQUERY_OPTION_FLAG
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid XQuery option flag"
argument_list|,
literal|"01T"
argument_list|)
block|,
comment|/** 2201S: data exception: invalid XQuery regular expression */
name|DATA_EXCEPTION_INVALID_XQUERY_REGULAR_EXPRESSION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid XQuery regular expression"
argument_list|,
literal|"01S"
argument_list|)
block|,
comment|/** 2201V: data exception: invalid XQuery replacement string */
name|DATA_EXCEPTION_INVALID_XQUERY_REPLACEMENT_STRING
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"invalid XQuery replacement string"
argument_list|,
literal|"01V"
argument_list|)
block|,
comment|/** 2200G: data exception: most specific type mismatch */
name|DATA_EXCEPTION_MOST_SPECIFIC_TYPE_MISMATCH
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"most specific type mismatch"
argument_list|,
literal|"00G"
argument_list|)
block|,
comment|/** 2200Q: data exception: multiset value overflow */
name|DATA_EXCEPTION_MULTISET_VALUE_OVERFLOW
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"multiset value overflow"
argument_list|,
literal|"00Q"
argument_list|)
block|,
comment|/** 22029: data exception: noncharacter in UCS string */
name|DATA_EXCEPTION_NONCHARACTER_IN_UCS_STRING
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"noncharacter in UCS string"
argument_list|,
literal|"029"
argument_list|)
block|,
comment|/** 2202D: data exception: null value substituted for mutator subject parameter */
name|DATA_EXCEPTION_NULL_VALUE_SUBSTITUTED_FOR_MUTATOR_SUBJECT_PARAMETER
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"null value substituted for mutator subject parameter"
argument_list|,
literal|"02D"
argument_list|)
block|,
comment|/** 2201C: data exception: null row not permitted in table */
name|DATA_EXCEPTION_NULL_ROW_NOT_PERMITTED_IN_TABLE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"null row not permitted in table"
argument_list|,
literal|"01C"
argument_list|)
block|,
comment|/** 2200E: data exception: null value in array target */
name|DATA_EXCEPTION_NULL_VALUE_IN_ARRAY_TARGET
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"null value in array target"
argument_list|,
literal|"00E"
argument_list|)
block|,
comment|/** 22002: data exception: null value, no indicator parameter */
name|DATA_EXCEPTION_NULL_VALUE_NO_INDICATOR_PARAMETER
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"null value, no indicator parameter"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 22004: data exception: null value not allowed */
name|DATA_EXCEPTION_NULL_VALUE_NOT_ALLOWED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"null value not allowed"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 22003: data exception: numeric value out of range */
name|DATA_EXCEPTION_NUMERIC_VALUE_OUT_OF_RANGE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"numeric value out of range"
argument_list|,
literal|"003"
argument_list|)
block|,
comment|/** 2200H: data exception: sequence generator limit exceeded */
name|DATA_EXCEPTION_SEQUENCE_GENERATOR_LIMIT_EXCEEDED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"sequence generator limit exceeded"
argument_list|,
literal|"00H"
argument_list|)
block|,
comment|/** 2202K: data exception: skip to non-existent row */
name|DATA_EXCEPTION_SKIP_TO_NON_EXISTENT_ROW
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"skip to non-existent row"
argument_list|,
literal|"02K"
argument_list|)
block|,
comment|/** 2202L: data exception: skip to first row of match */
name|DATA_EXCEPTION_SKIP_TO_FIRST_ROW_OF_MATCH
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"skip to first row of match"
argument_list|,
literal|"02L"
argument_list|)
block|,
comment|/** 22026: data exception: string data, length mismatch */
name|DATA_EXCEPTION_STRING_DATA_LENGTH_MISMATCH
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"string data, length mismatch"
argument_list|,
literal|"026"
argument_list|)
block|,
comment|/** 22001: data exception: string data, right truncation */
name|DATA_EXCEPTION_STRING_DATA_RIGHT_TRUNCATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"string data, right truncation"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 22011: data exception: substring error */
name|DATA_EXCEPTION_SUBSTRING_ERROR
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"substring error"
argument_list|,
literal|"011"
argument_list|)
block|,
comment|/** 22027: data exception: trim error */
name|DATA_EXCEPTION_TRIM_ERROR
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"trim error"
argument_list|,
literal|"027"
argument_list|)
block|,
comment|/** 22024: data exception: unterminated C string */
name|DATA_EXCEPTION_UNTERMINATED_C_STRING
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"unterminated C string"
argument_list|,
literal|"024"
argument_list|)
block|,
comment|/** 2200F: data exception: zero-length character string */
name|DATA_EXCEPTION_ZERO_LENGTH_CHARACTER_STRING
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"data exception"
argument_list|,
literal|"22"
argument_list|,
literal|"zero-length character string"
argument_list|,
literal|"00F"
argument_list|)
block|,
comment|/** 2B000: dependent privilege descriptors still exist */
name|DEPENDENT_PRIVILEGE_DESCRIPTORS_STILL_EXIST_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dependent privilege descriptors still exist"
argument_list|,
literal|"2B"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0Z000: diagnostics exception */
name|DIAGNOSTICS_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"diagnostics exception"
argument_list|,
literal|"0Z"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0Z001: diagnostics exception: maximum number of stacked diagnostics areas exceeded */
name|DIAGNOSTICS_EXCEPTION_MAXIMUM_NUMBER_OF_DIAGNOSTICS_AREAS_EXCEEDED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"diagnostics exception"
argument_list|,
literal|"0Z"
argument_list|,
literal|"maximum number of stacked diagnostics areas exceeded"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 07000: dynamic SQL error */
name|DYNAMIC_SQL_ERROR_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 07003: dynamic SQL error: cursor specification cannot be executed */
name|DYNAMIC_SQL_ERROR_CURSOR_SPECIFICATION_CANNOT_BE_EXECUTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"cursor specification cannot be executed"
argument_list|,
literal|"003"
argument_list|)
block|,
comment|/** 0700B: dynamic SQL error: data type transform function violation */
name|DYNAMIC_SQL_ERROR_DATA_TYPE_TRANSFORM_FUNCTION_VIOLATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"data type transform function violation"
argument_list|,
literal|"00B"
argument_list|)
block|,
comment|/** 0700D: dynamic SQL error: invalid DATA target */
name|DYNAMIC_SQL_ERROR_INVALID_DATA_TARGET
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"invalid DATA target"
argument_list|,
literal|"00D"
argument_list|)
block|,
comment|/** 0700F: dynamic SQL error: invalid DATETIME_INTERVAL_CODE */
name|DYNAMIC_SQL_ERROR_INVALID_DATETIME_INTERVAL_CODE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"invalid DATETIME_INTERVAL_CODE"
argument_list|,
literal|"00F"
argument_list|)
block|,
comment|/** 07008: dynamic SQL error: invalid descriptor count */
name|DYNAMIC_SQL_ERROR_INVALID_DESCRIPTOR_COUNT
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"invalid descriptor count"
argument_list|,
literal|"008"
argument_list|)
block|,
comment|/** 07009: dynamic SQL error: invalid descriptor index */
name|DYNAMIC_SQL_ERROR_INVALID_DESCRIPTOR_INDEX
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"invalid descriptor index"
argument_list|,
literal|"009"
argument_list|)
block|,
comment|/** 0700E: dynamic SQL error: invalid LEVEL value */
name|DYNAMIC_SQL_ERROR_INVALID_LEVEL_VALUE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"invalid LEVEL value"
argument_list|,
literal|"00E"
argument_list|)
block|,
comment|/** 07005: dynamic SQL error: prepared statement not a cursor specification */
name|DYNAMIC_SQL_ERROR_PREPARED_STATEMENT_NOT_A_CURSOR_SPECIFICATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"prepared statement not a cursor specification"
argument_list|,
literal|"005"
argument_list|)
block|,
comment|/** 07006: dynamic SQL error: restricted data type attribute violation */
name|DYNAMIC_SQL_ERROR_RESTRICTED_DATA_TYPE_ATTRIBUTE_VIOLATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"restricted data type attribute violation"
argument_list|,
literal|"006"
argument_list|)
block|,
comment|/** 0700C: dynamic SQL error: undefined DATA value */
name|DYNAMIC_SQL_ERROR_UNDEFINED_DATA_VALUE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"undefined DATA value"
argument_list|,
literal|"00C"
argument_list|)
block|,
comment|/** 07001: dynamic SQL error: using clause does not match dynamic parameter specifications */
name|DYNAMIC_SQL_ERROR_USING_CLAUSE_DOES_NOT_MATCH_DYNAMIC_PARAMETER_SPEC
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"using clause does not match dynamic parameter specifications"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 07002: dynamic SQL error: using clause does not match target specifications */
name|DYNAMIC_SQL_ERROR_USING_CLAUSE_DOES_NOT_MATCH_TARGET_SPEC
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"using clause does not match target specifications"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 07004: dynamic SQL error: using clause required for dynamic parameters */
name|DYNAMIC_SQL_ERROR_USING_CLAUSE_REQUIRED_FOR_DYNAMIC_PARAMETERS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"using clause required for dynamic parameters"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 07007: dynamic SQL error: using clause required for result fields */
name|DYNAMIC_SQL_ERROR_USING_CLAUSE_REQUIRED_FOR_RESULT_FIELDS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"dynamic SQL error"
argument_list|,
literal|"07"
argument_list|,
literal|"using clause required for result fields"
argument_list|,
literal|"007"
argument_list|)
block|,
comment|/** 38000: external routine exception */
name|EXTERNAL_ROUTINE_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"external routine exception"
argument_list|,
literal|"38"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 38001: external routine exception: containing SQL not permitted */
name|EXTERNAL_ROUTINE_EXCEPTION_CONTAINING_SQL_NOT_PERMITTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"external routine exception"
argument_list|,
literal|"38"
argument_list|,
literal|"containing SQL not permitted"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 38002: external routine exception: modifying SQL-data not permitted */
name|EXTERNAL_ROUTINE_EXCEPTION_MODIFYING_SQL_DATA_NOT_PERMITTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"external routine exception"
argument_list|,
literal|"38"
argument_list|,
literal|"modifying SQL-data not permitted"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 38003: external routine exception: prohibited SQL-statement attempted */
name|EXTERNAL_ROUTINE_EXCEPTION_PROHIBITED_SQL_STATEMENT_ATTEMPTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"external routine exception"
argument_list|,
literal|"38"
argument_list|,
literal|"prohibited SQL-statement attempted"
argument_list|,
literal|"003"
argument_list|)
block|,
comment|/** 38004: external routine exception: reading SQL-data not permitted */
name|EXTERNAL_ROUTINE_EXCEPTION_READING_SQL_DATA_NOT_PERMITTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"external routine exception"
argument_list|,
literal|"38"
argument_list|,
literal|"reading SQL-data not permitted"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 39000: external routine invocation exception */
name|EXTERNAL_ROUTINE_INVOCATION_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"external routine invocation exception"
argument_list|,
literal|"39"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 39004: external routine invocation exception: null value not allowed */
name|EXTERNAL_ROUTINE_INVOCATION_EXCEPTION_NULL_VALUE_NOT_ALLOWED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"external routine invocation exception"
argument_list|,
literal|"39"
argument_list|,
literal|"null value not allowed"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 0A000: feature not supported */
name|FEATURE_NOT_SUPPORTED_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"feature not supported"
argument_list|,
literal|"0A"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0A001: feature not supported: multiple server transactions */
name|FEATURE_NOT_SUPPORTED_MULTIPLE_ENVIRONMENT_TRANSACTIONS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"feature not supported"
argument_list|,
literal|"0A"
argument_list|,
literal|"multiple server transactions"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 23000: integrity constraint violation */
name|INTEGRITY_CONSTRAINT_VIOLATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"integrity constraint violation"
argument_list|,
literal|"23"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 23001: integrity constraint violation: restrict violation */
name|INTEGRITY_CONSTRAINT_VIOLATION_RESTRICT_VIOLATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"integrity constraint violation"
argument_list|,
literal|"23"
argument_list|,
literal|"restrict violation"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 28000: invalid authorization specification */
name|INVALID_AUTHORIZATION_SPECIFICATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid authorization specification"
argument_list|,
literal|"28"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 3D000: invalid catalog name */
name|INVALID_CATALOG_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid catalog name"
argument_list|,
literal|"3D"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 2C000: invalid character set name */
name|INVALID_CHARACTER_SET_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid character set name"
argument_list|,
literal|"2C"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 2C001: invalid character set name: cannot drop SQL-session default character set */
name|INVALID_CHARACTER_SET_NAME_CANNOT_DROP_SQLSESSION_DEFAULT_CHARACTER_SET
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid character set name"
argument_list|,
literal|"2C"
argument_list|,
literal|"cannot drop SQL-session default character set"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 35000: invalid condition number */
name|INVALID_CONDITION_NUMBER_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid condition number"
argument_list|,
literal|"35"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 2E000: invalid connection name */
name|INVALID_CONNECTION_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid connection name"
argument_list|,
literal|"2E"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 34000: invalid cursor name */
name|INVALID_CURSOR_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid cursor name"
argument_list|,
literal|"34"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 24000: invalid cursor state */
name|INVALID_CURSOR_STATE_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid cursor state"
argument_list|,
literal|"24"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0L000: invalid grantor */
name|INVALID_GRANTOR_STATE_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid grantor"
argument_list|,
literal|"0L"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0P000: invalid role specification */
name|INVALID_ROLE_SPECIFICATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid role specification"
argument_list|,
literal|"0P"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 3F000: invalid schema name */
name|INVALID_SCHEMA_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid schema name"
argument_list|,
literal|"3F"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0E000: invalid schema name list specification */
name|INVALID_SCHEMA_NAME_LIST_SPECIFICATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid schema name list specification"
argument_list|,
literal|"0E"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 2H000: invalid collation name */
name|INVALID_COLLATION_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid collation name"
argument_list|,
literal|"2H"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 33000: invalid SQL descriptor name */
name|INVALID_SQL_DESCRIPTOR_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid SQL descriptor name"
argument_list|,
literal|"33"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0M000: invalid SQL-invoked procedure reference */
name|INVALID_SQL_INVOKED_PROCEDURE_REFERENCE_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid SQL-invoked procedure reference"
argument_list|,
literal|"0M"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 26000: invalid SQL statement name */
name|INVALID_SQL_STATEMENT_NAME_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid SQL statement name"
argument_list|,
literal|"26"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 30000: invalid SQL statement identifier */
name|INVALID_SQL_STATEMENT_IDENTIFIER_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid SQL statement identifier"
argument_list|,
literal|"30"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0D000: invalid target type specification */
name|INVALID_TARGET_TYPE_SPECIFICATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid target type specification"
argument_list|,
literal|"0D"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 25000: invalid transaction state */
name|INVALID_TRANSACTION_STATE_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 25001: invalid transaction state: active SQL-transaction */
name|INVALID_TRANSACTION_STATE_ACTIVE_SQL_TRANSACTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|"active SQL-transaction"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 25002: invalid transaction state: branch transaction already active */
name|INVALID_TRANSACTION_STATE_BRANCH_TRANSACTION_ALREADY_ACTIVE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|"branch transaction already active"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 25008: invalid transaction state: held cursor requires same isolation level */
name|INVALID_TRANSACTION_STATE_HELD_CURSOR_REQUIRES_SAME_ISOLATION_LEVEL
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|"held cursor requires same isolation level"
argument_list|,
literal|"008"
argument_list|)
block|,
comment|/** 25003: invalid transaction state: inappropriate access mode for branch transaction */
name|INVALID_TRANSACTION_STATE_INAPPROPRIATE_ACCESS_MODE_FOR_BRANCH_TRANSACTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|"inappropriate access mode for branch transaction"
argument_list|,
literal|"003"
argument_list|)
block|,
comment|/** 25004: invalid transaction state: inappropriate isolation level for branch transaction */
name|INVALID_TRANSACTION_STATE_INAPPROPRIATE_ISOLATION_LEVEL_FOR_BRANCH_TRANSACTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|"inappropriate isolation level for branch transaction"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 25005: invalid transaction state: no active SQL-transaction for branch transaction */
name|INVALID_TRANSACTION_STATE_NO_ACTIVE_SQL_TRANSACTION_FOR_BRANCH_TRANSACTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|"no active SQL-transaction for branch transaction"
argument_list|,
literal|"005"
argument_list|)
block|,
comment|/** 25006: invalid transaction state: read-only SQL-transaction */
name|INVALID_TRANSACTION_STATE_READ_ONLY_SQL_TRANSACTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|"read-only SQL-transaction"
argument_list|,
literal|"006"
argument_list|)
block|,
comment|/** 25007: invalid transaction state: schema and data statement mixing not supported */
name|INVALID_TRANSACTION_STATE_SCHEMA_AND_DATA_STATEMENT_MIXING_NOT_SUPPORTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction state"
argument_list|,
literal|"25"
argument_list|,
literal|"schema and data statement mixing not supported"
argument_list|,
literal|"007"
argument_list|)
block|,
comment|/** 2D000: invalid transaction termination */
name|INVALID_TRANSACTION_TERMINATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transaction termination"
argument_list|,
literal|"2D"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0S000: invalid transform group name specification */
name|INVALID_TRANSFORM_GROUP_NAME_SPECIFICATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"invalid transform group name specification"
argument_list|,
literal|"0S"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0F000: locator exception */
name|LOCATOR_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"locator exception"
argument_list|,
literal|"0F"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0F001: locator exception: invalid specification */
name|LOCATOR_EXCEPTION_INVALID_SPECIFICATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"locator exception"
argument_list|,
literal|"0F"
argument_list|,
literal|"invalid specification"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 02000: no data */
name|NO_DATA_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|N
argument_list|,
literal|"no data"
argument_list|,
literal|"02"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 02001: no data: no additional result sets returned */
name|NO_DATA_NO_ADDITIONAL_RESULT_SETS_RETURNED
argument_list|(
name|Category
operator|.
name|N
argument_list|,
literal|"no data"
argument_list|,
literal|"02"
argument_list|,
literal|"no additional result sets returned"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 0W000: prohibited statement encountered during trigger execution */
name|PROHIBITED_STATEMENT_DURING_TRIGGER_EXECUTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"prohibited statement encountered during trigger execution"
argument_list|,
literal|"0W"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0W001: prohibited statement encountered during trigger execution: modify table modified by    * data change delta table */
name|PROHIBITED_STATEMENT_DURING_TRIGGER_EXECUTION_MODIFY_TABLE_MODIFIED_BY_DATA_CHANGE_DELTA_TABLE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"prohibited statement encountered during trigger execution"
argument_list|,
literal|"0W"
argument_list|,
literal|"modify table modified by data change delta table"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** HZ: Remote Database Access    *    *<p>(See Table 12, 'SQLSTATE class and subclass values for RDA-specific conditions' in    * [ISO9579], Subclause 8.1, 'Exception codes for RDA-specific Conditions', for the definition of    * protocol subconditions and subclass code values.) */
name|REMOTE_DATABASE_ACCESS_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"Remote Database Access"
argument_list|,
literal|"HZ"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 3B000: savepoint exception */
name|SAVEPOINT_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"savepoint exception"
argument_list|,
literal|"3B"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 3B001: savepoint exception: invalid specification */
name|SAVEPOINT_EXCEPTION_INVALID_SPECIFICATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"savepoint exception"
argument_list|,
literal|"3B"
argument_list|,
literal|"invalid specification"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 3B002: savepoint exception: too many */
name|SAVEPOINT_EXCEPTION_TOO_MANY
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"savepoint exception"
argument_list|,
literal|"3B"
argument_list|,
literal|"too many"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 2F000: SQL routine exception */
name|SQL_ROUTINE_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"SQL routine exception"
argument_list|,
literal|"2F"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 2F005: SQL routine exception: function executed no return statement */
name|SQL_ROUTINE_EXCEPTION_FUNCTION_EXECUTED_NO_RETURN_STATEMENT
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"SQL routine exception"
argument_list|,
literal|"2F"
argument_list|,
literal|"function executed no return statement"
argument_list|,
literal|"005"
argument_list|)
block|,
comment|/** 2F002: SQL routine exception: modifying SQL-data not permitted */
name|SQL_ROUTINE_EXCEPTION_MODIFYING_SQL_DATA_NOT_PERMITTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"SQL routine exception"
argument_list|,
literal|"2F"
argument_list|,
literal|"modifying SQL-data not permitted"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 2F003: SQL routine exception: prohibited SQL-statement attempted */
name|SQL_ROUTINE_EXCEPTION_PROHIBITED_SQL_STATEMENT_ATTEMPTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"SQL routine exception"
argument_list|,
literal|"2F"
argument_list|,
literal|"prohibited SQL-statement attempted"
argument_list|,
literal|"003"
argument_list|)
block|,
comment|/** 2F004: SQL routine exception: reading SQL-data not permitted */
name|SQL_ROUTINE_EXCEPTION_READING_SQL_DATA_NOT_PERMITTED
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"SQL routine exception"
argument_list|,
literal|"2F"
argument_list|,
literal|"reading SQL-data not permitted"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 00000: successful completion */
name|SUCCESSFUL_COMPLETION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|S
argument_list|,
literal|"successful completion"
argument_list|,
literal|"00"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 42000: syntax error or access rule violation */
name|SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"syntax error or access rule violation"
argument_list|,
literal|"42"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 2A000: syntax error or access rule violation */
name|SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION_IN_DIRECT_STATEMENT_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"syntax error or access rule violation in direct statement"
argument_list|,
literal|"2A"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 37000: syntax error or access rule violation */
name|SYNTAX_ERROR_OR_ACCESS_RULE_VIOLATION_IN_DYNAMIC_STATEMENT_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"syntax error or access rule violation in dynamic statement"
argument_list|,
literal|"37"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0T000: target table disagrees with cursor specification */
name|TARGET_TABLE_DISAGREES_WITH_CURSOR_SPECIFICATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"target table disagrees with cursor specification"
argument_list|,
literal|"0T"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 40000: transaction rollback */
name|TRANSACTION_ROLLBACK_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"transaction rollback"
argument_list|,
literal|"40"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 40002: transaction rollback: integrity constraint violation */
name|TRANSACTION_ROLLBACK_INTEGRITY_CONSTRAINT_VIOLATION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"transaction rollback"
argument_list|,
literal|"40"
argument_list|,
literal|"integrity constraint violation"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 40001: transaction rollback: serialization failure */
name|TRANSACTION_ROLLBACK_SERIALIZATION_FAILURE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"transaction rollback"
argument_list|,
literal|"40"
argument_list|,
literal|"serialization failure"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 40003: transaction rollback: statement completion unknown */
name|TRANSACTION_ROLLBACK_STATEMENT_COMPLETION_UNKNOWN
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"transaction rollback"
argument_list|,
literal|"40"
argument_list|,
literal|"statement completion unknown"
argument_list|,
literal|"003"
argument_list|)
block|,
comment|/** 40004: transaction rollback: triggered action exception */
name|TRANSACTION_ROLLBACK_TRIGGERED_ACTION_EXCEPTION
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"transaction rollback"
argument_list|,
literal|"40"
argument_list|,
literal|"triggered action exception"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 09000: triggered action exception */
name|TRIGGERED_ACTION_EXCEPTION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"triggered action exception"
argument_list|,
literal|"09"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 27000: triggered data change violation */
name|TRIGGERED_DATA_CHANGE_VIOLATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"triggered data change violation"
argument_list|,
literal|"27"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 27001: triggered data change violation: modify table modified by data change delta table */
name|TRIGGERED_DATA_CHANGE_VIOLATION_MODIFY_TABLE_MODIFIED_BY_DATA_CHANGE_DELTA_TABLE
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"triggered data change violation"
argument_list|,
literal|"27"
argument_list|,
literal|"modify table modified by data change delta table"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 01000: warning */
name|WARNING_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|,
comment|/** 0100D: warning: additional result sets returned */
name|WARNING_ADDITIONAL_RESULT_SETS_RETURNED
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"additional result sets returned"
argument_list|,
literal|"00D"
argument_list|)
block|,
comment|/** 0102F: warning: array data, right truncation */
name|WARNING_ARRAY_DATA_RIGHT_TRUNCATION
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"array data, right truncation"
argument_list|,
literal|"02F"
argument_list|)
block|,
comment|/** 0100E: warning: attempt to return too many result sets */
name|WARNING_ATTEMPT_TO_RETURN_TOO_MANY_RESULT_SETS
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"attempt to return too many result sets"
argument_list|,
literal|"00E"
argument_list|)
block|,
comment|/** 01001: warning: cursor operation conflict */
name|WARNING_CURSOR_OPERATION_CONFLICT
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"cursor operation conflict"
argument_list|,
literal|"001"
argument_list|)
block|,
comment|/** 0100B: warning: default value too long for information schema */
name|WARNING_DEFAULT_VALUE_TOO_LONG_FOR_INFORMATION_SCHEMA
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"default value too long for information schema"
argument_list|,
literal|"00B"
argument_list|)
block|,
comment|/** 01002: warning: disconnect error */
name|WARNING_DISCONNECT_ERROR
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"disconnect error"
argument_list|,
literal|"002"
argument_list|)
block|,
comment|/** 01005: warning: insufficient item descriptor areas */
name|WARNING_INSUFFICIENT_ITEM_DESCRIPTOR_AREAS
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"insufficient item descriptor areas"
argument_list|,
literal|"005"
argument_list|)
block|,
comment|/** 01012: warning: invalid number of conditions */
name|WARNING_INVALID_NUMBER_OF_CONDITIONS
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"invalid number of conditions"
argument_list|,
literal|"012"
argument_list|)
block|,
comment|/** 01003: warning: null value eliminated in set function */
name|WARNING_NULL_VALUE_ELIMINATED_IN_SET_FUNCTION
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"null value eliminated in set function"
argument_list|,
literal|"003"
argument_list|)
block|,
comment|/** 01007: warning: privilege not granted */
name|WARNING_PRIVILEGE_NOT_GRANTED
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"privilege not granted"
argument_list|,
literal|"007"
argument_list|)
block|,
comment|/** 01006: warning: privilege not revoked */
name|WARNING_PRIVILEGE_NOT_REVOKED
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"privilege not revoked"
argument_list|,
literal|"006"
argument_list|)
block|,
comment|/** 0100A: warning: query expression too long for information schema */
name|WARNING_QUERY_EXPRESSION_TOO_LONG_FOR_INFORMATION_SCHEMA
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"query expression too long for information schema"
argument_list|,
literal|"00A"
argument_list|)
block|,
comment|/** 0100C: warning: result sets returned */
name|WARNING_DYNAMIC_RESULT_SETS_RETURNED
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"result sets returned"
argument_list|,
literal|"00C"
argument_list|)
block|,
comment|/** 01009: warning: search condition too long for information schema */
name|WARNING_SEARCH_CONDITION_TOO_LONG_FOR_INFORMATION_SCHEMA
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"search condition too long for information schema"
argument_list|,
literal|"009"
argument_list|)
block|,
comment|/** 0100F: warning: statement too long for information schema */
name|WARNING_STATEMENT_TOO_LONG_FOR_INFORMATION_SCHEMA
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"statement too long for information schema"
argument_list|,
literal|"00F"
argument_list|)
block|,
comment|/** 01004: warning: string data, right truncation */
name|WARNING_STRING_DATA_RIGHT_TRUNCATION_WARNING
argument_list|(
name|Category
operator|.
name|W
argument_list|,
literal|"warning"
argument_list|,
literal|"01"
argument_list|,
literal|"string data, right truncation"
argument_list|,
literal|"004"
argument_list|)
block|,
comment|/** 44000: with check option violation */
name|WITH_CHECK_OPTION_VIOLATION_NO_SUBCLASS
argument_list|(
name|Category
operator|.
name|X
argument_list|,
literal|"with check option violation"
argument_list|,
literal|"44"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
block|;
specifier|public
specifier|final
name|Category
name|category
decl_stmt|;
specifier|public
specifier|final
name|String
name|condition
decl_stmt|;
specifier|public
specifier|final
name|String
name|klass
decl_stmt|;
specifier|public
specifier|final
name|String
name|subCondition
decl_stmt|;
specifier|public
specifier|final
name|String
name|subClass
decl_stmt|;
specifier|public
specifier|final
name|String
name|code
decl_stmt|;
comment|/** Alias for backwards compatibility with previous versions of SQL spec. */
specifier|public
specifier|static
specifier|final
name|SqlState
name|INVALID_SQL_STATEMENT
init|=
name|INVALID_SQL_STATEMENT_IDENTIFIER_NO_SUBCLASS
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SqlState
argument_list|>
name|BY_CODE
decl_stmt|;
static|static
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|SqlState
argument_list|>
name|m
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|SqlState
name|s
range|:
name|values
argument_list|()
control|)
block|{
name|m
operator|.
name|put
argument_list|(
name|s
operator|.
name|code
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
name|BY_CODE
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
name|SqlState
parameter_list|(
name|Category
name|category
parameter_list|,
name|String
name|condition
parameter_list|,
name|String
name|klass
parameter_list|,
name|String
name|subCondition
parameter_list|,
name|String
name|subClass
parameter_list|)
block|{
name|this
operator|.
name|category
operator|=
name|category
expr_stmt|;
name|this
operator|.
name|condition
operator|=
name|condition
expr_stmt|;
name|this
operator|.
name|klass
operator|=
name|klass
expr_stmt|;
name|this
operator|.
name|subCondition
operator|=
name|subCondition
expr_stmt|;
name|this
operator|.
name|subClass
operator|=
name|subClass
expr_stmt|;
name|this
operator|.
name|code
operator|=
name|klass
operator|+
operator|(
name|subClass
operator|==
literal|null
condition|?
literal|"000"
else|:
name|subClass
operator|)
expr_stmt|;
block|}
comment|/** Validates the data, and generates the HTML table. */
specifier|private
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|PrintWriter
name|pw
init|=
operator|new
name|PrintWriter
argument_list|(
name|System
operator|.
name|out
argument_list|)
decl_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" *<table>"
argument_list|)
expr_stmt|;
name|SqlState
name|parent
init|=
literal|null
decl_stmt|;
for|for
control|(
name|SqlState
name|s
range|:
name|values
argument_list|()
control|)
block|{
assert|assert
name|s
operator|.
name|klass
operator|.
name|length
argument_list|()
operator|==
literal|2
assert|;
assert|assert
name|s
operator|.
name|subClass
operator|==
literal|null
operator|||
name|s
operator|.
name|subClass
operator|.
name|length
argument_list|()
operator|==
literal|3
assert|;
if|if
condition|(
name|s
operator|.
name|subClass
operator|==
literal|null
condition|)
block|{
assert|assert
name|s
operator|.
name|subCondition
operator|==
literal|null
assert|;
name|parent
operator|=
name|s
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|parent
operator|!=
literal|null
assert|;
assert|assert
name|s
operator|.
name|subCondition
operator|!=
literal|null
assert|;
assert|assert
name|s
operator|.
name|category
operator|==
name|parent
operator|.
name|category
assert|;
assert|assert
name|s
operator|.
name|klass
operator|.
name|equals
argument_list|(
name|parent
operator|.
name|klass
argument_list|)
assert|;
assert|assert
name|s
operator|.
name|condition
operator|.
name|equals
argument_list|(
name|parent
operator|.
name|condition
argument_list|)
assert|;
block|}
name|pw
operator|.
name|println
argument_list|(
literal|" *<tr>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" *<td>"
operator|+
operator|(
name|parent
operator|==
name|s
condition|?
name|s
operator|.
name|category
else|:
literal|"&nbsp;"
operator|)
operator|+
literal|"</td>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" *<td>"
operator|+
operator|(
name|parent
operator|==
name|s
condition|?
name|s
operator|.
name|condition
else|:
literal|"&nbsp;"
operator|)
operator|+
literal|"</td>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" *<td>"
operator|+
operator|(
name|parent
operator|==
name|s
condition|?
name|s
operator|.
name|klass
else|:
literal|"&nbsp;"
operator|)
operator|+
literal|"</td>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" *<td>"
operator|+
operator|(
name|s
operator|.
name|subCondition
operator|==
literal|null
condition|?
literal|"(no subclass)"
else|:
name|s
operator|.
name|subCondition
operator|)
operator|+
literal|"</td>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" *<td>"
operator|+
operator|(
name|s
operator|.
name|subCondition
operator|==
literal|null
condition|?
literal|"000"
else|:
name|s
operator|.
name|subClass
operator|)
operator|+
literal|"</td>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|println
argument_list|(
literal|" *</tr>"
argument_list|)
expr_stmt|;
block|}
name|pw
operator|.
name|println
argument_list|(
literal|" *</table>"
argument_list|)
expr_stmt|;
name|pw
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/** Severity types. */
enum|enum
name|Category
block|{
comment|/** Success (class 00). */
name|S
block|,
comment|/** Warning (class 01). */
name|W
block|,
comment|/** No data (class 02). */
name|N
block|,
comment|/** Exception (all other classes). */
name|X
block|,   }
block|}
end_enum

begin_comment
comment|// End SqlState.java
end_comment

end_unit

