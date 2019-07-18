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
name|util
operator|.
name|IntegerIntervalSet
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
name|Assertions
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
name|Disabled
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
name|Tag
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
name|params
operator|.
name|ParameterizedTest
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
name|params
operator|.
name|provider
operator|.
name|MethodSource
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Duration
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * Test case that runs the FoodMart reference queries.  */
end_comment

begin_class
annotation|@
name|Tag
argument_list|(
literal|"slow"
argument_list|)
specifier|public
class|class
name|FoodmartTest
block|{
specifier|private
specifier|static
specifier|final
name|int
index|[]
name|DISABLED_IDS
init|=
block|{
literal|58
block|,
literal|83
block|,
literal|202
block|,
literal|204
block|,
literal|205
block|,
literal|206
block|,
literal|207
block|,
literal|209
block|,
literal|211
block|,
literal|231
block|,
literal|247
block|,
literal|275
block|,
literal|309
block|,
literal|383
block|,
literal|384
block|,
literal|385
block|,
literal|448
block|,
literal|449
block|,
literal|471
block|,
literal|494
block|,
literal|495
block|,
literal|496
block|,
literal|497
block|,
literal|499
block|,
literal|500
block|,
literal|501
block|,
literal|502
block|,
literal|503
block|,
literal|505
block|,
literal|506
block|,
literal|507
block|,
literal|514
block|,
literal|516
block|,
literal|518
block|,
literal|520
block|,
literal|534
block|,
literal|551
block|,
literal|563
block|,
literal|566
block|,
literal|571
block|,
literal|628
block|,
literal|629
block|,
literal|630
block|,
literal|644
block|,
literal|649
block|,
literal|650
block|,
literal|651
block|,
literal|653
block|,
literal|654
block|,
literal|655
block|,
literal|656
block|,
literal|657
block|,
literal|658
block|,
literal|659
block|,
literal|669
block|,
literal|722
block|,
literal|731
block|,
literal|732
block|,
literal|737
block|,
literal|748
block|,
literal|750
block|,
literal|756
block|,
literal|774
block|,
literal|777
block|,
literal|778
block|,
literal|779
block|,
literal|781
block|,
literal|782
block|,
literal|783
block|,
literal|811
block|,
literal|818
block|,
literal|819
block|,
literal|820
block|,
literal|2057
block|,
literal|2059
block|,
literal|2060
block|,
literal|2073
block|,
literal|2088
block|,
literal|2098
block|,
literal|2099
block|,
literal|2136
block|,
literal|2151
block|,
literal|2158
block|,
literal|2162
block|,
literal|2163
block|,
literal|2164
block|,
literal|2165
block|,
literal|2166
block|,
literal|2167
block|,
literal|2168
block|,
literal|2169
block|,
literal|2170
block|,
literal|2171
block|,
literal|2172
block|,
literal|2173
block|,
literal|2174
block|,
literal|2175
block|,
literal|2176
block|,
literal|2177
block|,
literal|2178
block|,
literal|2179
block|,
literal|2180
block|,
literal|2181
block|,
literal|2187
block|,
literal|2190
block|,
literal|2191
block|,
literal|2235
block|,
literal|2245
block|,
literal|2264
block|,
literal|2265
block|,
literal|2266
block|,
literal|2267
block|,
literal|2268
block|,
literal|2270
block|,
literal|2271
block|,
literal|2279
block|,
literal|2327
block|,
literal|2328
block|,
literal|2341
block|,
literal|2356
block|,
literal|2365
block|,
literal|2374
block|,
literal|2415
block|,
literal|2416
block|,
literal|2424
block|,
literal|2432
block|,
literal|2455
block|,
literal|2456
block|,
literal|2457
block|,
literal|2518
block|,
literal|2521
block|,
literal|2528
block|,
literal|2542
block|,
literal|2570
block|,
literal|2578
block|,
literal|2579
block|,
literal|2580
block|,
literal|2581
block|,
literal|2594
block|,
literal|2598
block|,
literal|2749
block|,
literal|2774
block|,
literal|2778
block|,
literal|2780
block|,
literal|2781
block|,
literal|2786
block|,
literal|2787
block|,
literal|2790
block|,
literal|2791
block|,
literal|2876
block|,
literal|2883
block|,
literal|5226
block|,
literal|5227
block|,
literal|5228
block|,
literal|5229
block|,
literal|5230
block|,
literal|5238
block|,
literal|5239
block|,
literal|5249
block|,
literal|5279
block|,
literal|5281
block|,
literal|5282
block|,
literal|5283
block|,
literal|5284
block|,
literal|5286
block|,
literal|5288
block|,
literal|5291
block|,
literal|5415
block|,
literal|5444
block|,
literal|5445
block|,
literal|5446
block|,
literal|5447
block|,
literal|5448
block|,
literal|5452
block|,
literal|5459
block|,
literal|5460
block|,
literal|5461
block|,
literal|5517
block|,
literal|5519
block|,
literal|5558
block|,
literal|5560
block|,
literal|5561
block|,
literal|5562
block|,
literal|5572
block|,
literal|5573
block|,
literal|5576
block|,
literal|5577
block|,
literal|5607
block|,
literal|5644
block|,
literal|5648
block|,
literal|5657
block|,
literal|5664
block|,
literal|5665
block|,
literal|5667
block|,
literal|5671
block|,
literal|5682
block|,
literal|5700
block|,
literal|5743
block|,
literal|5748
block|,
literal|5749
block|,
literal|5750
block|,
literal|5751
block|,
literal|5775
block|,
literal|5776
block|,
literal|5777
block|,
literal|5785
block|,
literal|5793
block|,
literal|5796
block|,
literal|5797
block|,
literal|5810
block|,
literal|5811
block|,
literal|5814
block|,
literal|5816
block|,
literal|5852
block|,
literal|5874
block|,
literal|5875
block|,
literal|5910
block|,
literal|5953
block|,
literal|5960
block|,
literal|5971
block|,
literal|5975
block|,
literal|5983
block|,
literal|6016
block|,
literal|6028
block|,
literal|6030
block|,
literal|6031
block|,
literal|6033
block|,
literal|6034
block|,
literal|6081
block|,
literal|6082
block|,
literal|6083
block|,
literal|6084
block|,
literal|6085
block|,
literal|6086
block|,
literal|6087
block|,
literal|6088
block|,
literal|6089
block|,
literal|6090
block|,
literal|6097
block|,
literal|6098
block|,
literal|6099
block|,
literal|6100
block|,
literal|6101
block|,
literal|6102
block|,
literal|6103
block|,
literal|6104
block|,
literal|6105
block|,
literal|6106
block|,
literal|6107
block|,
literal|6108
block|,
literal|6109
block|,
literal|6110
block|,
literal|6111
block|,
literal|6112
block|,
literal|6113
block|,
literal|6114
block|,
literal|6115
block|,
literal|6141
block|,
literal|6150
block|,
literal|6156
block|,
literal|6160
block|,
literal|6164
block|,
literal|6168
block|,
literal|6169
block|,
literal|6172
block|,
literal|6177
block|,
literal|6180
block|,
literal|6181
block|,
literal|6185
block|,
literal|6187
block|,
literal|6188
block|,
literal|6190
block|,
literal|6191
block|,
literal|6193
block|,
literal|6194
block|,
literal|6196
block|,
literal|6197
block|,
literal|6199
block|,
literal|6200
block|,
literal|6202
block|,
literal|6203
block|,
literal|6205
block|,
literal|6206
block|,
literal|6208
block|,
literal|6209
block|,
literal|6211
block|,
literal|6212
block|,
literal|6214
block|,
literal|6215
block|,
literal|6217
block|,
literal|6218
block|,
literal|6220
block|,
literal|6221
block|,
literal|6223
block|,
literal|6224
block|,
literal|6226
block|,
literal|6227
block|,
literal|6229
block|,
literal|6230
block|,
literal|6232
block|,
literal|6233
block|,
literal|6235
block|,
literal|6236
block|,
literal|6238
block|,
literal|6239
block|,
literal|6241
block|,
literal|6242
block|,
literal|6244
block|,
literal|6245
block|,
literal|6247
block|,
literal|6248
block|,
literal|6250
block|,
literal|6251
block|,
literal|6253
block|,
literal|6254
block|,
literal|6256
block|,
literal|6257
block|,
literal|6259
block|,
literal|6260
block|,
literal|6262
block|,
literal|6263
block|,
literal|6265
block|,
literal|6266
block|,
literal|6268
block|,
literal|6269
block|,
comment|// failed
literal|5677
block|,
literal|5681
block|,
comment|// 2nd run
literal|6271
block|,
literal|6272
block|,
literal|6274
block|,
literal|6275
block|,
literal|6277
block|,
literal|6278
block|,
literal|6280
block|,
literal|6281
block|,
literal|6283
block|,
literal|6284
block|,
literal|6286
block|,
literal|6287
block|,
literal|6289
block|,
literal|6290
block|,
literal|6292
block|,
literal|6293
block|,
literal|6295
block|,
literal|6296
block|,
literal|6298
block|,
literal|6299
block|,
literal|6301
block|,
literal|6302
block|,
literal|6304
block|,
literal|6305
block|,
literal|6307
block|,
literal|6308
block|,
literal|6310
block|,
literal|6311
block|,
literal|6313
block|,
literal|6314
block|,
literal|6316
block|,
literal|6317
block|,
literal|6319
block|,
literal|6327
block|,
literal|6328
block|,
literal|6337
block|,
literal|6338
block|,
literal|6339
block|,
literal|6341
block|,
literal|6345
block|,
literal|6346
block|,
literal|6348
block|,
literal|6349
block|,
literal|6354
block|,
literal|6355
block|,
literal|6359
block|,
literal|6366
block|,
literal|6368
block|,
literal|6369
block|,
literal|6375
block|,
literal|6376
block|,
literal|6377
block|,
literal|6389
block|,
literal|6396
block|,
literal|6400
block|,
literal|6422
block|,
literal|6424
block|,
literal|6445
block|,
literal|6447
block|,
literal|6449
block|,
literal|6450
block|,
literal|6454
block|,
literal|6456
block|,
literal|6470
block|,
literal|6479
block|,
literal|6480
block|,
literal|6491
block|,
literal|6509
block|,
literal|6518
block|,
literal|6522
block|,
literal|6561
block|,
literal|6562
block|,
literal|6564
block|,
literal|6566
block|,
literal|6578
block|,
literal|6581
block|,
literal|6582
block|,
literal|6583
block|,
literal|6587
block|,
literal|6591
block|,
literal|6594
block|,
literal|6603
block|,
literal|6610
block|,
literal|6613
block|,
literal|6615
block|,
literal|6618
block|,
literal|6619
block|,
literal|6622
block|,
literal|6627
block|,
literal|6632
block|,
literal|6635
block|,
literal|6643
block|,
literal|6650
block|,
literal|6651
block|,
literal|6652
block|,
literal|6653
block|,
literal|6656
block|,
literal|6659
block|,
literal|6668
block|,
literal|6670
block|,
literal|6720
block|,
literal|6726
block|,
literal|6735
block|,
literal|6737
block|,
literal|6739
block|,
comment|// timeout oor OOM
literal|420
block|,
literal|423
block|,
literal|5218
block|,
literal|5219
block|,
literal|5616
block|,
literal|5617
block|,
literal|5618
block|,
literal|5891
block|,
literal|5892
block|,
literal|5895
block|,
literal|5896
block|,
literal|5898
block|,
literal|5899
block|,
literal|5900
block|,
literal|5901
block|,
literal|5902
block|,
literal|6080
block|,
literal|6091
block|,
comment|// bugs
literal|6597
block|,
comment|// CALCITE-403
block|}
decl_stmt|;
comment|// Interesting tests. (We need to fix and remove from the disabled list.)
comment|// 2452, 2453, 2454, 2457 only: RTRIM
comment|// 2436-2453,2455: agg_
comment|// 2518, 5960 only: "every derived table must have its own alias"
comment|// 2542: timeout. Running big, simple SQL cartesian product.
comment|//
comment|// 202 and others: strip away "CAST(the_year AS UNSIGNED) = 1997"
specifier|public
specifier|static
name|Stream
argument_list|<
name|FoodMartQuerySet
operator|.
name|FoodmartQuery
argument_list|>
name|queries
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|idList
init|=
name|CalciteSystemProperty
operator|.
name|TEST_FOODMART_QUERY_IDS
operator|.
name|value
argument_list|()
decl_stmt|;
specifier|final
name|FoodMartQuerySet
name|set
init|=
name|FoodMartQuerySet
operator|.
name|instance
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FoodMartQuerySet
operator|.
name|FoodmartQuery
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|idList
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|idList
operator|.
name|endsWith
argument_list|(
literal|",-disabled"
argument_list|)
condition|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|(
name|idList
argument_list|)
decl_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|",-disabled"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|disabledId
range|:
name|DISABLED_IDS
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|",-"
argument_list|)
operator|.
name|append
argument_list|(
name|disabledId
argument_list|)
expr_stmt|;
block|}
name|idList
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Integer
name|id
range|:
name|IntegerIntervalSet
operator|.
name|of
argument_list|(
name|idList
argument_list|)
control|)
block|{
specifier|final
name|FoodMartQuerySet
operator|.
name|FoodmartQuery
name|query1
init|=
name|set
operator|.
name|queries
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|query1
operator|!=
literal|null
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|query1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
for|for
control|(
name|FoodMartQuerySet
operator|.
name|FoodmartQuery
name|query1
range|:
name|set
operator|.
name|queries
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|Primitive
operator|.
name|asList
argument_list|(
name|DISABLED_IDS
argument_list|)
operator|.
name|contains
argument_list|(
name|query1
operator|.
name|id
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|list
operator|.
name|add
argument_list|(
name|query1
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
operator|.
name|stream
argument_list|()
return|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|MethodSource
argument_list|(
literal|"queries"
argument_list|)
specifier|public
name|void
name|test
parameter_list|(
name|FoodMartQuerySet
operator|.
name|FoodmartQuery
name|query
parameter_list|)
block|{
name|Assertions
operator|.
name|assertTimeoutPreemptively
argument_list|(
name|Duration
operator|.
name|ofMinutes
argument_list|(
literal|2
argument_list|)
argument_list|,
parameter_list|()
lambda|->
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|FOODMART_CLONE
argument_list|)
operator|.
name|pooled
argument_list|()
operator|.
name|query
argument_list|(
name|query
operator|.
name|sql
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParameterizedTest
annotation|@
name|Disabled
annotation|@
name|MethodSource
argument_list|(
literal|"queries"
argument_list|)
specifier|public
name|void
name|testWithLattice
parameter_list|(
name|FoodMartQuerySet
operator|.
name|FoodmartQuery
name|query
parameter_list|)
block|{
name|Assertions
operator|.
name|assertTimeoutPreemptively
argument_list|(
name|Duration
operator|.
name|ofMinutes
argument_list|(
literal|2
argument_list|)
argument_list|,
parameter_list|()
lambda|->
block|{
name|CalciteAssert
operator|.
name|that
argument_list|()
operator|.
name|with
argument_list|(
name|CalciteAssert
operator|.
name|Config
operator|.
name|JDBC_FOODMART_WITH_LATTICE
argument_list|)
operator|.
name|pooled
argument_list|()
operator|.
name|withDefaultSchema
argument_list|(
literal|"foodmart"
argument_list|)
operator|.
name|query
argument_list|(
name|query
operator|.
name|sql
argument_list|)
operator|.
name|enableMaterializations
argument_list|(
literal|true
argument_list|)
operator|.
name|runs
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|// End FoodmartTest.java
end_comment

end_unit

