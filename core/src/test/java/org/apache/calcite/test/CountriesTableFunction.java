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
name|DataContext
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
name|config
operator|.
name|CalciteConnectionConfig
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
name|ScannableTable
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
name|Schema
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
name|Statistic
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
name|Statistics
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
name|SqlCall
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
name|SqlNode
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
name|org
operator|.
name|apache
operator|.
name|calcite
operator|.
name|util
operator|.
name|ImmutableBitSet
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

begin_comment
comment|/** A table function that returns all countries in the world.  *  *<p>Has same content as  *<code>file/src/test/resources/geo/countries.csv</code>. */
end_comment

begin_class
specifier|public
class|class
name|CountriesTableFunction
block|{
specifier|private
name|CountriesTableFunction
parameter_list|()
block|{
block|}
specifier|private
specifier|static
specifier|final
name|Object
index|[]
index|[]
name|ROWS
init|=
block|{
block|{
literal|"AD"
block|,
literal|42.546245
block|,
literal|1.601554
block|,
literal|"Andorra"
block|}
block|,
block|{
literal|"AE"
block|,
literal|23.424076
block|,
literal|53.847818
block|,
literal|"United Arab Emirates"
block|}
block|,
block|{
literal|"AF"
block|,
literal|33.93911
block|,
literal|67.709953
block|,
literal|"Afghanistan"
block|}
block|,
block|{
literal|"AG"
block|,
literal|17.060816
block|,
operator|-
literal|61.796428
block|,
literal|"Antigua and Barbuda"
block|}
block|,
block|{
literal|"AI"
block|,
literal|18.220554
block|,
operator|-
literal|63.068615
block|,
literal|"Anguilla"
block|}
block|,
block|{
literal|"AL"
block|,
literal|41.153332
block|,
literal|20.168331
block|,
literal|"Albania"
block|}
block|,
block|{
literal|"AM"
block|,
literal|40.069099
block|,
literal|45.038189
block|,
literal|"Armenia"
block|}
block|,
block|{
literal|"AN"
block|,
literal|12.226079
block|,
operator|-
literal|69.060087
block|,
literal|"Netherlands Antilles"
block|}
block|,
block|{
literal|"AO"
block|,
operator|-
literal|11.202692
block|,
literal|17.873887
block|,
literal|"Angola"
block|}
block|,
block|{
literal|"AQ"
block|,
operator|-
literal|75.250973
block|,
operator|-
literal|0.071389
block|,
literal|"Antarctica"
block|}
block|,
block|{
literal|"AR"
block|,
operator|-
literal|38.416097
block|,
operator|-
literal|63.616672
block|,
literal|"Argentina"
block|}
block|,
block|{
literal|"AS"
block|,
operator|-
literal|14.270972
block|,
operator|-
literal|170.132217
block|,
literal|"American Samoa"
block|}
block|,
block|{
literal|"AT"
block|,
literal|47.516231
block|,
literal|14.550072
block|,
literal|"Austria"
block|}
block|,
block|{
literal|"AU"
block|,
operator|-
literal|25.274398
block|,
literal|133.775136
block|,
literal|"Australia"
block|}
block|,
block|{
literal|"AW"
block|,
literal|12.52111
block|,
operator|-
literal|69.968338
block|,
literal|"Aruba"
block|}
block|,
block|{
literal|"AZ"
block|,
literal|40.143105
block|,
literal|47.576927
block|,
literal|"Azerbaijan"
block|}
block|,
block|{
literal|"BA"
block|,
literal|43.915886
block|,
literal|17.679076
block|,
literal|"Bosnia and Herzegovina"
block|}
block|,
block|{
literal|"BB"
block|,
literal|13.193887
block|,
operator|-
literal|59.543198
block|,
literal|"Barbados"
block|}
block|,
block|{
literal|"BD"
block|,
literal|23.684994
block|,
literal|90.356331
block|,
literal|"Bangladesh"
block|}
block|,
block|{
literal|"BE"
block|,
literal|50.503887
block|,
literal|4.469936
block|,
literal|"Belgium"
block|}
block|,
block|{
literal|"BF"
block|,
literal|12.238333
block|,
operator|-
literal|1.561593
block|,
literal|"Burkina Faso"
block|}
block|,
block|{
literal|"BG"
block|,
literal|42.733883
block|,
literal|25.48583
block|,
literal|"Bulgaria"
block|}
block|,
block|{
literal|"BH"
block|,
literal|25.930414
block|,
literal|50.637772
block|,
literal|"Bahrain"
block|}
block|,
block|{
literal|"BI"
block|,
operator|-
literal|3.373056
block|,
literal|29.918886
block|,
literal|"Burundi"
block|}
block|,
block|{
literal|"BJ"
block|,
literal|9.30769
block|,
literal|2.315834
block|,
literal|"Benin"
block|}
block|,
block|{
literal|"BM"
block|,
literal|32.321384
block|,
operator|-
literal|64.75737
block|,
literal|"Bermuda"
block|}
block|,
block|{
literal|"BN"
block|,
literal|4.535277
block|,
literal|114.727669
block|,
literal|"Brunei"
block|}
block|,
block|{
literal|"BO"
block|,
operator|-
literal|16.290154
block|,
operator|-
literal|63.588653
block|,
literal|"Bolivia"
block|}
block|,
block|{
literal|"BR"
block|,
operator|-
literal|14.235004
block|,
operator|-
literal|51.92528
block|,
literal|"Brazil"
block|}
block|,
block|{
literal|"BS"
block|,
literal|25.03428
block|,
operator|-
literal|77.39628
block|,
literal|"Bahamas"
block|}
block|,
block|{
literal|"BT"
block|,
literal|27.514162
block|,
literal|90.433601
block|,
literal|"Bhutan"
block|}
block|,
block|{
literal|"BV"
block|,
operator|-
literal|54.423199
block|,
literal|3.413194
block|,
literal|"Bouvet Island"
block|}
block|,
block|{
literal|"BW"
block|,
operator|-
literal|22.328474
block|,
literal|24.684866
block|,
literal|"Botswana"
block|}
block|,
block|{
literal|"BY"
block|,
literal|53.709807
block|,
literal|27.953389
block|,
literal|"Belarus"
block|}
block|,
block|{
literal|"BZ"
block|,
literal|17.189877
block|,
operator|-
literal|88.49765
block|,
literal|"Belize"
block|}
block|,
block|{
literal|"CA"
block|,
literal|56.130366
block|,
operator|-
literal|106.346771
block|,
literal|"Canada"
block|}
block|,
block|{
literal|"CC"
block|,
operator|-
literal|12.164165
block|,
literal|96.870956
block|,
literal|"Cocos [Keeling] Islands"
block|}
block|,
block|{
literal|"CD"
block|,
operator|-
literal|4.038333
block|,
literal|21.758664
block|,
literal|"Congo [DRC]"
block|}
block|,
block|{
literal|"CF"
block|,
literal|6.611111
block|,
literal|20.939444
block|,
literal|"Central African Republic"
block|}
block|,
block|{
literal|"CG"
block|,
operator|-
literal|0.228021
block|,
literal|15.827659
block|,
literal|"Congo [Republic]"
block|}
block|,
block|{
literal|"CH"
block|,
literal|46.818188
block|,
literal|8.227512
block|,
literal|"Switzerland"
block|}
block|,
block|{
literal|"CI"
block|,
literal|7.539989
block|,
operator|-
literal|5.54708
block|,
literal|"CÃ´te d'Ivoire"
block|}
block|,
block|{
literal|"CK"
block|,
operator|-
literal|21.236736
block|,
operator|-
literal|159.777671
block|,
literal|"Cook Islands"
block|}
block|,
block|{
literal|"CL"
block|,
operator|-
literal|35.675147
block|,
operator|-
literal|71.542969
block|,
literal|"Chile"
block|}
block|,
block|{
literal|"CM"
block|,
literal|7.369722
block|,
literal|12.354722
block|,
literal|"Cameroon"
block|}
block|,
block|{
literal|"CN"
block|,
literal|35.86166
block|,
literal|104.195397
block|,
literal|"China"
block|}
block|,
block|{
literal|"CO"
block|,
literal|4.570868
block|,
operator|-
literal|74.297333
block|,
literal|"Colombia"
block|}
block|,
block|{
literal|"CR"
block|,
literal|9.748917
block|,
operator|-
literal|83.753428
block|,
literal|"Costa Rica"
block|}
block|,
block|{
literal|"CU"
block|,
literal|21.521757
block|,
operator|-
literal|77.781167
block|,
literal|"Cuba"
block|}
block|,
block|{
literal|"CV"
block|,
literal|16.002082
block|,
operator|-
literal|24.013197
block|,
literal|"Cape Verde"
block|}
block|,
block|{
literal|"CX"
block|,
operator|-
literal|10.447525
block|,
literal|105.690449
block|,
literal|"Christmas Island"
block|}
block|,
block|{
literal|"CY"
block|,
literal|35.126413
block|,
literal|33.429859
block|,
literal|"Cyprus"
block|}
block|,
block|{
literal|"CZ"
block|,
literal|49.817492
block|,
literal|15.472962
block|,
literal|"Czech Republic"
block|}
block|,
block|{
literal|"DE"
block|,
literal|51.165691
block|,
literal|10.451526
block|,
literal|"Germany"
block|}
block|,
block|{
literal|"DJ"
block|,
literal|11.825138
block|,
literal|42.590275
block|,
literal|"Djibouti"
block|}
block|,
block|{
literal|"DK"
block|,
literal|56.26392
block|,
literal|9.501785
block|,
literal|"Denmark"
block|}
block|,
block|{
literal|"DM"
block|,
literal|15.414999
block|,
operator|-
literal|61.370976
block|,
literal|"Dominica"
block|}
block|,
block|{
literal|"DO"
block|,
literal|18.735693
block|,
operator|-
literal|70.162651
block|,
literal|"Dominican Republic"
block|}
block|,
block|{
literal|"DZ"
block|,
literal|28.033886
block|,
literal|1.659626
block|,
literal|"Algeria"
block|}
block|,
block|{
literal|"EC"
block|,
operator|-
literal|1.831239
block|,
operator|-
literal|78.183406
block|,
literal|"Ecuador"
block|}
block|,
block|{
literal|"EE"
block|,
literal|58.595272
block|,
literal|25.013607
block|,
literal|"Estonia"
block|}
block|,
block|{
literal|"EG"
block|,
literal|26.820553
block|,
literal|30.802498
block|,
literal|"Egypt"
block|}
block|,
block|{
literal|"EH"
block|,
literal|24.215527
block|,
operator|-
literal|12.885834
block|,
literal|"Western Sahara"
block|}
block|,
block|{
literal|"ER"
block|,
literal|15.179384
block|,
literal|39.782334
block|,
literal|"Eritrea"
block|}
block|,
block|{
literal|"ES"
block|,
literal|40.463667
block|,
operator|-
literal|3.74922
block|,
literal|"Spain"
block|}
block|,
block|{
literal|"ET"
block|,
literal|9.145
block|,
literal|40.489673
block|,
literal|"Ethiopia"
block|}
block|,
block|{
literal|"FI"
block|,
literal|61.92411
block|,
literal|25.748151
block|,
literal|"Finland"
block|}
block|,
block|{
literal|"FJ"
block|,
operator|-
literal|16.578193
block|,
literal|179.414413
block|,
literal|"Fiji"
block|}
block|,
block|{
literal|"FK"
block|,
operator|-
literal|51.796253
block|,
operator|-
literal|59.523613
block|,
literal|"Falkland Islands [Islas Malvinas]"
block|}
block|,
block|{
literal|"FM"
block|,
literal|7.425554
block|,
literal|150.550812
block|,
literal|"Micronesia"
block|}
block|,
block|{
literal|"FO"
block|,
literal|61.892635
block|,
operator|-
literal|6.911806
block|,
literal|"Faroe Islands"
block|}
block|,
block|{
literal|"FR"
block|,
literal|46.227638
block|,
literal|2.213749
block|,
literal|"France"
block|}
block|,
block|{
literal|"GA"
block|,
operator|-
literal|0.803689
block|,
literal|11.609444
block|,
literal|"Gabon"
block|}
block|,
block|{
literal|"GB"
block|,
literal|55.378051
block|,
operator|-
literal|3.435973
block|,
literal|"United Kingdom"
block|}
block|,
block|{
literal|"GD"
block|,
literal|12.262776
block|,
operator|-
literal|61.604171
block|,
literal|"Grenada"
block|}
block|,
block|{
literal|"GE"
block|,
literal|42.315407
block|,
literal|43.356892
block|,
literal|"Georgia"
block|}
block|,
block|{
literal|"GF"
block|,
literal|3.933889
block|,
operator|-
literal|53.125782
block|,
literal|"French Guiana"
block|}
block|,
block|{
literal|"GG"
block|,
literal|49.465691
block|,
operator|-
literal|2.585278
block|,
literal|"Guernsey"
block|}
block|,
block|{
literal|"GH"
block|,
literal|7.946527
block|,
operator|-
literal|1.023194
block|,
literal|"Ghana"
block|}
block|,
block|{
literal|"GI"
block|,
literal|36.137741
block|,
operator|-
literal|5.345374
block|,
literal|"Gibraltar"
block|}
block|,
block|{
literal|"GL"
block|,
literal|71.706936
block|,
operator|-
literal|42.604303
block|,
literal|"Greenland"
block|}
block|,
block|{
literal|"GM"
block|,
literal|13.443182
block|,
operator|-
literal|15.310139
block|,
literal|"Gambia"
block|}
block|,
block|{
literal|"GN"
block|,
literal|9.945587
block|,
operator|-
literal|9.696645
block|,
literal|"Guinea"
block|}
block|,
block|{
literal|"GP"
block|,
literal|16.995971
block|,
operator|-
literal|62.067641
block|,
literal|"Guadeloupe"
block|}
block|,
block|{
literal|"GQ"
block|,
literal|1.650801
block|,
literal|10.267895
block|,
literal|"Equatorial Guinea"
block|}
block|,
block|{
literal|"GR"
block|,
literal|39.074208
block|,
literal|21.824312
block|,
literal|"Greece"
block|}
block|,
block|{
literal|"GS"
block|,
operator|-
literal|54.429579
block|,
operator|-
literal|36.587909
block|,
literal|"South Georgia and the South Sandwich Islands"
block|}
block|,
block|{
literal|"GT"
block|,
literal|15.783471
block|,
operator|-
literal|90.230759
block|,
literal|"Guatemala"
block|}
block|,
block|{
literal|"GU"
block|,
literal|13.444304
block|,
literal|144.793731
block|,
literal|"Guam"
block|}
block|,
block|{
literal|"GW"
block|,
literal|11.803749
block|,
operator|-
literal|15.180413
block|,
literal|"Guinea-Bissau"
block|}
block|,
block|{
literal|"GY"
block|,
literal|4.860416
block|,
operator|-
literal|58.93018
block|,
literal|"Guyana"
block|}
block|,
block|{
literal|"GZ"
block|,
literal|31.354676
block|,
literal|34.308825
block|,
literal|"Gaza Strip"
block|}
block|,
block|{
literal|"HK"
block|,
literal|22.396428
block|,
literal|114.109497
block|,
literal|"Hong Kong"
block|}
block|,
block|{
literal|"HM"
block|,
operator|-
literal|53.08181
block|,
literal|73.504158
block|,
literal|"Heard Island and McDonald Islands"
block|}
block|,
block|{
literal|"HN"
block|,
literal|15.199999
block|,
operator|-
literal|86.241905
block|,
literal|"Honduras"
block|}
block|,
block|{
literal|"HR"
block|,
literal|45.1
block|,
literal|15.2
block|,
literal|"Croatia"
block|}
block|,
block|{
literal|"HT"
block|,
literal|18.971187
block|,
operator|-
literal|72.285215
block|,
literal|"Haiti"
block|}
block|,
block|{
literal|"HU"
block|,
literal|47.162494
block|,
literal|19.503304
block|,
literal|"Hungary"
block|}
block|,
block|{
literal|"ID"
block|,
operator|-
literal|0.789275
block|,
literal|113.921327
block|,
literal|"Indonesia"
block|}
block|,
block|{
literal|"IE"
block|,
literal|53.41291
block|,
operator|-
literal|8.24389
block|,
literal|"Ireland"
block|}
block|,
block|{
literal|"IL"
block|,
literal|31.046051
block|,
literal|34.851612
block|,
literal|"Israel"
block|}
block|,
block|{
literal|"IM"
block|,
literal|54.236107
block|,
operator|-
literal|4.548056
block|,
literal|"Isle of Man"
block|}
block|,
block|{
literal|"IN"
block|,
literal|20.593684
block|,
literal|78.96288
block|,
literal|"India"
block|}
block|,
block|{
literal|"IO"
block|,
operator|-
literal|6.343194
block|,
literal|71.876519
block|,
literal|"British Indian Ocean Territory"
block|}
block|,
block|{
literal|"IQ"
block|,
literal|33.223191
block|,
literal|43.679291
block|,
literal|"Iraq"
block|}
block|,
block|{
literal|"IR"
block|,
literal|32.427908
block|,
literal|53.688046
block|,
literal|"Iran"
block|}
block|,
block|{
literal|"IS"
block|,
literal|64.963051
block|,
operator|-
literal|19.020835
block|,
literal|"Iceland"
block|}
block|,
block|{
literal|"IT"
block|,
literal|41.87194
block|,
literal|12.56738
block|,
literal|"Italy"
block|}
block|,
block|{
literal|"JE"
block|,
literal|49.214439
block|,
operator|-
literal|2.13125
block|,
literal|"Jersey"
block|}
block|,
block|{
literal|"JM"
block|,
literal|18.109581
block|,
operator|-
literal|77.297508
block|,
literal|"Jamaica"
block|}
block|,
block|{
literal|"JO"
block|,
literal|30.585164
block|,
literal|36.238414
block|,
literal|"Jordan"
block|}
block|,
block|{
literal|"JP"
block|,
literal|36.204824
block|,
literal|138.252924
block|,
literal|"Japan"
block|}
block|,
block|{
literal|"KE"
block|,
operator|-
literal|0.023559
block|,
literal|37.906193
block|,
literal|"Kenya"
block|}
block|,
block|{
literal|"KG"
block|,
literal|41.20438
block|,
literal|74.766098
block|,
literal|"Kyrgyzstan"
block|}
block|,
block|{
literal|"KH"
block|,
literal|12.565679
block|,
literal|104.990963
block|,
literal|"Cambodia"
block|}
block|,
block|{
literal|"KI"
block|,
operator|-
literal|3.370417
block|,
operator|-
literal|168.734039
block|,
literal|"Kiribati"
block|}
block|,
block|{
literal|"KM"
block|,
operator|-
literal|11.875001
block|,
literal|43.872219
block|,
literal|"Comoros"
block|}
block|,
block|{
literal|"KN"
block|,
literal|17.357822
block|,
operator|-
literal|62.782998
block|,
literal|"Saint Kitts and Nevis"
block|}
block|,
block|{
literal|"KP"
block|,
literal|40.339852
block|,
literal|127.510093
block|,
literal|"North Korea"
block|}
block|,
block|{
literal|"KR"
block|,
literal|35.907757
block|,
literal|127.766922
block|,
literal|"South Korea"
block|}
block|,
block|{
literal|"KW"
block|,
literal|29.31166
block|,
literal|47.481766
block|,
literal|"Kuwait"
block|}
block|,
block|{
literal|"KY"
block|,
literal|19.513469
block|,
operator|-
literal|80.566956
block|,
literal|"Cayman Islands"
block|}
block|,
block|{
literal|"KZ"
block|,
literal|48.019573
block|,
literal|66.923684
block|,
literal|"Kazakhstan"
block|}
block|,
block|{
literal|"LA"
block|,
literal|19.85627
block|,
literal|102.495496
block|,
literal|"Laos"
block|}
block|,
block|{
literal|"LB"
block|,
literal|33.854721
block|,
literal|35.862285
block|,
literal|"Lebanon"
block|}
block|,
block|{
literal|"LC"
block|,
literal|13.909444
block|,
operator|-
literal|60.978893
block|,
literal|"Saint Lucia"
block|}
block|,
block|{
literal|"LI"
block|,
literal|47.166
block|,
literal|9.555373
block|,
literal|"Liechtenstein"
block|}
block|,
block|{
literal|"LK"
block|,
literal|7.873054
block|,
literal|80.771797
block|,
literal|"Sri Lanka"
block|}
block|,
block|{
literal|"LR"
block|,
literal|6.428055
block|,
operator|-
literal|9.429499
block|,
literal|"Liberia"
block|}
block|,
block|{
literal|"LS"
block|,
operator|-
literal|29.609988
block|,
literal|28.233608
block|,
literal|"Lesotho"
block|}
block|,
block|{
literal|"LT"
block|,
literal|55.169438
block|,
literal|23.881275
block|,
literal|"Lithuania"
block|}
block|,
block|{
literal|"LU"
block|,
literal|49.815273
block|,
literal|6.129583
block|,
literal|"Luxembourg"
block|}
block|,
block|{
literal|"LV"
block|,
literal|56.879635
block|,
literal|24.603189
block|,
literal|"Latvia"
block|}
block|,
block|{
literal|"LY"
block|,
literal|26.3351
block|,
literal|17.228331
block|,
literal|"Libya"
block|}
block|,
block|{
literal|"MA"
block|,
literal|31.791702
block|,
operator|-
literal|7.09262
block|,
literal|"Morocco"
block|}
block|,
block|{
literal|"MC"
block|,
literal|43.750298
block|,
literal|7.412841
block|,
literal|"Monaco"
block|}
block|,
block|{
literal|"MD"
block|,
literal|47.411631
block|,
literal|28.369885
block|,
literal|"Moldova"
block|}
block|,
block|{
literal|"ME"
block|,
literal|42.708678
block|,
literal|19.37439
block|,
literal|"Montenegro"
block|}
block|,
block|{
literal|"MG"
block|,
operator|-
literal|18.766947
block|,
literal|46.869107
block|,
literal|"Madagascar"
block|}
block|,
block|{
literal|"MH"
block|,
literal|7.131474
block|,
literal|171.184478
block|,
literal|"Marshall Islands"
block|}
block|,
block|{
literal|"MK"
block|,
literal|41.608635
block|,
literal|21.745275
block|,
literal|"Macedonia [FYROM]"
block|}
block|,
block|{
literal|"ML"
block|,
literal|17.570692
block|,
operator|-
literal|3.996166
block|,
literal|"Mali"
block|}
block|,
block|{
literal|"MM"
block|,
literal|21.913965
block|,
literal|95.956223
block|,
literal|"Myanmar [Burma]"
block|}
block|,
block|{
literal|"MN"
block|,
literal|46.862496
block|,
literal|103.846656
block|,
literal|"Mongolia"
block|}
block|,
block|{
literal|"MO"
block|,
literal|22.198745
block|,
literal|113.543873
block|,
literal|"Macau"
block|}
block|,
block|{
literal|"MP"
block|,
literal|17.33083
block|,
literal|145.38469
block|,
literal|"Northern Mariana Islands"
block|}
block|,
block|{
literal|"MQ"
block|,
literal|14.641528
block|,
operator|-
literal|61.024174
block|,
literal|"Martinique"
block|}
block|,
block|{
literal|"MR"
block|,
literal|21.00789
block|,
operator|-
literal|10.940835
block|,
literal|"Mauritania"
block|}
block|,
block|{
literal|"MS"
block|,
literal|16.742498
block|,
operator|-
literal|62.187366
block|,
literal|"Montserrat"
block|}
block|,
block|{
literal|"MT"
block|,
literal|35.937496
block|,
literal|14.375416
block|,
literal|"Malta"
block|}
block|,
block|{
literal|"MU"
block|,
operator|-
literal|20.348404
block|,
literal|57.552152
block|,
literal|"Mauritius"
block|}
block|,
block|{
literal|"MV"
block|,
literal|3.202778
block|,
literal|73.22068
block|,
literal|"Maldives"
block|}
block|,
block|{
literal|"MW"
block|,
operator|-
literal|13.254308
block|,
literal|34.301525
block|,
literal|"Malawi"
block|}
block|,
block|{
literal|"MX"
block|,
literal|23.634501
block|,
operator|-
literal|102.552784
block|,
literal|"Mexico"
block|}
block|,
block|{
literal|"MY"
block|,
literal|4.210484
block|,
literal|101.975766
block|,
literal|"Malaysia"
block|}
block|,
block|{
literal|"MZ"
block|,
operator|-
literal|18.665695
block|,
literal|35.529562
block|,
literal|"Mozambique"
block|}
block|,
block|{
literal|"NA"
block|,
operator|-
literal|22.95764
block|,
literal|18.49041
block|,
literal|"Namibia"
block|}
block|,
block|{
literal|"NC"
block|,
operator|-
literal|20.904305
block|,
literal|165.618042
block|,
literal|"New Caledonia"
block|}
block|,
block|{
literal|"NE"
block|,
literal|17.607789
block|,
literal|8.081666
block|,
literal|"Niger"
block|}
block|,
block|{
literal|"NF"
block|,
operator|-
literal|29.040835
block|,
literal|167.954712
block|,
literal|"Norfolk Island"
block|}
block|,
block|{
literal|"NG"
block|,
literal|9.081999
block|,
literal|8.675277
block|,
literal|"Nigeria"
block|}
block|,
block|{
literal|"NI"
block|,
literal|12.865416
block|,
operator|-
literal|85.207229
block|,
literal|"Nicaragua"
block|}
block|,
block|{
literal|"NL"
block|,
literal|52.132633
block|,
literal|5.291266
block|,
literal|"Netherlands"
block|}
block|,
block|{
literal|"NO"
block|,
literal|60.472024
block|,
literal|8.468946
block|,
literal|"Norway"
block|}
block|,
block|{
literal|"NP"
block|,
literal|28.394857
block|,
literal|84.124008
block|,
literal|"Nepal"
block|}
block|,
block|{
literal|"NR"
block|,
operator|-
literal|0.522778
block|,
literal|166.931503
block|,
literal|"Nauru"
block|}
block|,
block|{
literal|"NU"
block|,
operator|-
literal|19.054445
block|,
operator|-
literal|169.867233
block|,
literal|"Niue"
block|}
block|,
block|{
literal|"NZ"
block|,
operator|-
literal|40.900557
block|,
literal|174.885971
block|,
literal|"New Zealand"
block|}
block|,
block|{
literal|"OM"
block|,
literal|21.512583
block|,
literal|55.923255
block|,
literal|"Oman"
block|}
block|,
block|{
literal|"PA"
block|,
literal|8.537981
block|,
operator|-
literal|80.782127
block|,
literal|"Panama"
block|}
block|,
block|{
literal|"PE"
block|,
operator|-
literal|9.189967
block|,
operator|-
literal|75.015152
block|,
literal|"Peru"
block|}
block|,
block|{
literal|"PF"
block|,
operator|-
literal|17.679742
block|,
operator|-
literal|149.406843
block|,
literal|"French Polynesia"
block|}
block|,
block|{
literal|"PG"
block|,
operator|-
literal|6.314993
block|,
literal|143.95555
block|,
literal|"Papua New Guinea"
block|}
block|,
block|{
literal|"PH"
block|,
literal|12.879721
block|,
literal|121.774017
block|,
literal|"Philippines"
block|}
block|,
block|{
literal|"PK"
block|,
literal|30.375321
block|,
literal|69.345116
block|,
literal|"Pakistan"
block|}
block|,
block|{
literal|"PL"
block|,
literal|51.919438
block|,
literal|19.145136
block|,
literal|"Poland"
block|}
block|,
block|{
literal|"PM"
block|,
literal|46.941936
block|,
operator|-
literal|56.27111
block|,
literal|"Saint Pierre and Miquelon"
block|}
block|,
block|{
literal|"PN"
block|,
operator|-
literal|24.703615
block|,
operator|-
literal|127.439308
block|,
literal|"Pitcairn Islands"
block|}
block|,
block|{
literal|"PR"
block|,
literal|18.220833
block|,
operator|-
literal|66.590149
block|,
literal|"Puerto Rico"
block|}
block|,
block|{
literal|"PS"
block|,
literal|31.952162
block|,
literal|35.233154
block|,
literal|"Palestinian Territories"
block|}
block|,
block|{
literal|"PT"
block|,
literal|39.399872
block|,
operator|-
literal|8.224454
block|,
literal|"Portugal"
block|}
block|,
block|{
literal|"PW"
block|,
literal|7.51498
block|,
literal|134.58252
block|,
literal|"Palau"
block|}
block|,
block|{
literal|"PY"
block|,
operator|-
literal|23.442503
block|,
operator|-
literal|58.443832
block|,
literal|"Paraguay"
block|}
block|,
block|{
literal|"QA"
block|,
literal|25.354826
block|,
literal|51.183884
block|,
literal|"Qatar"
block|}
block|,
block|{
literal|"RE"
block|,
operator|-
literal|21.115141
block|,
literal|55.536384
block|,
literal|"RÃ©union"
block|}
block|,
block|{
literal|"RO"
block|,
literal|45.943161
block|,
literal|24.96676
block|,
literal|"Romania"
block|}
block|,
block|{
literal|"RS"
block|,
literal|44.016521
block|,
literal|21.005859
block|,
literal|"Serbia"
block|}
block|,
block|{
literal|"RU"
block|,
literal|61.52401
block|,
literal|105.318756
block|,
literal|"Russia"
block|}
block|,
block|{
literal|"RW"
block|,
operator|-
literal|1.940278
block|,
literal|29.873888
block|,
literal|"Rwanda"
block|}
block|,
block|{
literal|"SA"
block|,
literal|23.885942
block|,
literal|45.079162
block|,
literal|"Saudi Arabia"
block|}
block|,
block|{
literal|"SB"
block|,
operator|-
literal|9.64571
block|,
literal|160.156194
block|,
literal|"Solomon Islands"
block|}
block|,
block|{
literal|"SC"
block|,
operator|-
literal|4.679574
block|,
literal|55.491977
block|,
literal|"Seychelles"
block|}
block|,
block|{
literal|"SD"
block|,
literal|12.862807
block|,
literal|30.217636
block|,
literal|"Sudan"
block|}
block|,
block|{
literal|"SE"
block|,
literal|60.128161
block|,
literal|18.643501
block|,
literal|"Sweden"
block|}
block|,
block|{
literal|"SG"
block|,
literal|1.352083
block|,
literal|103.819836
block|,
literal|"Singapore"
block|}
block|,
block|{
literal|"SH"
block|,
operator|-
literal|24.143474
block|,
operator|-
literal|10.030696
block|,
literal|"Saint Helena"
block|}
block|,
block|{
literal|"SI"
block|,
literal|46.151241
block|,
literal|14.995463
block|,
literal|"Slovenia"
block|}
block|,
block|{
literal|"SJ"
block|,
literal|77.553604
block|,
literal|23.670272
block|,
literal|"Svalbard and Jan Mayen"
block|}
block|,
block|{
literal|"SK"
block|,
literal|48.669026
block|,
literal|19.699024
block|,
literal|"Slovakia"
block|}
block|,
block|{
literal|"SL"
block|,
literal|8.460555
block|,
operator|-
literal|11.779889
block|,
literal|"Sierra Leone"
block|}
block|,
block|{
literal|"SM"
block|,
literal|43.94236
block|,
literal|12.457777
block|,
literal|"San Marino"
block|}
block|,
block|{
literal|"SN"
block|,
literal|14.497401
block|,
operator|-
literal|14.452362
block|,
literal|"Senegal"
block|}
block|,
block|{
literal|"SO"
block|,
literal|5.152149
block|,
literal|46.199616
block|,
literal|"Somalia"
block|}
block|,
block|{
literal|"SR"
block|,
literal|3.919305
block|,
operator|-
literal|56.027783
block|,
literal|"Suriname"
block|}
block|,
block|{
literal|"ST"
block|,
literal|0.18636
block|,
literal|6.613081
block|,
literal|"SÃ£o TomÃ© and PrÃ­ncipe"
block|}
block|,
block|{
literal|"SV"
block|,
literal|13.794185
block|,
operator|-
literal|88.89653
block|,
literal|"El Salvador"
block|}
block|,
block|{
literal|"SY"
block|,
literal|34.802075
block|,
literal|38.996815
block|,
literal|"Syria"
block|}
block|,
block|{
literal|"SZ"
block|,
operator|-
literal|26.522503
block|,
literal|31.465866
block|,
literal|"Swaziland"
block|}
block|,
block|{
literal|"TC"
block|,
literal|21.694025
block|,
operator|-
literal|71.797928
block|,
literal|"Turks and Caicos Islands"
block|}
block|,
block|{
literal|"TD"
block|,
literal|15.454166
block|,
literal|18.732207
block|,
literal|"Chad"
block|}
block|,
block|{
literal|"TF"
block|,
operator|-
literal|49.280366
block|,
literal|69.348557
block|,
literal|"French Southern Territories"
block|}
block|,
block|{
literal|"TG"
block|,
literal|8.619543
block|,
literal|0.824782
block|,
literal|"Togo"
block|}
block|,
block|{
literal|"TH"
block|,
literal|15.870032
block|,
literal|100.992541
block|,
literal|"Thailand"
block|}
block|,
block|{
literal|"TJ"
block|,
literal|38.861034
block|,
literal|71.276093
block|,
literal|"Tajikistan"
block|}
block|,
block|{
literal|"TK"
block|,
operator|-
literal|8.967363
block|,
operator|-
literal|171.855881
block|,
literal|"Tokelau"
block|}
block|,
block|{
literal|"TL"
block|,
operator|-
literal|8.874217
block|,
literal|125.727539
block|,
literal|"Timor-Leste"
block|}
block|,
block|{
literal|"TM"
block|,
literal|38.969719
block|,
literal|59.556278
block|,
literal|"Turkmenistan"
block|}
block|,
block|{
literal|"TN"
block|,
literal|33.886917
block|,
literal|9.537499
block|,
literal|"Tunisia"
block|}
block|,
block|{
literal|"TO"
block|,
operator|-
literal|21.178986
block|,
operator|-
literal|175.198242
block|,
literal|"Tonga"
block|}
block|,
block|{
literal|"TR"
block|,
literal|38.963745
block|,
literal|35.243322
block|,
literal|"Turkey"
block|}
block|,
block|{
literal|"TT"
block|,
literal|10.691803
block|,
operator|-
literal|61.222503
block|,
literal|"Trinidad and Tobago"
block|}
block|,
block|{
literal|"TV"
block|,
operator|-
literal|7.109535
block|,
literal|177.64933
block|,
literal|"Tuvalu"
block|}
block|,
block|{
literal|"TW"
block|,
literal|23.69781
block|,
literal|120.960515
block|,
literal|"Taiwan"
block|}
block|,
block|{
literal|"TZ"
block|,
operator|-
literal|6.369028
block|,
literal|34.888822
block|,
literal|"Tanzania"
block|}
block|,
block|{
literal|"UA"
block|,
literal|48.379433
block|,
literal|31.16558
block|,
literal|"Ukraine"
block|}
block|,
block|{
literal|"UG"
block|,
literal|1.373333
block|,
literal|32.290275
block|,
literal|"Uganda"
block|}
block|,
block|{
literal|"UM"
block|,
literal|null
block|,
literal|null
block|,
literal|"U.S.Minor Outlying Islands"
block|}
block|,
block|{
literal|"US"
block|,
literal|37.09024
block|,
operator|-
literal|95.712891
block|,
literal|"United States"
block|}
block|,
block|{
literal|"UY"
block|,
operator|-
literal|32.522779
block|,
operator|-
literal|55.765835
block|,
literal|"Uruguay"
block|}
block|,
block|{
literal|"UZ"
block|,
literal|41.377491
block|,
literal|64.585262
block|,
literal|"Uzbekistan"
block|}
block|,
block|{
literal|"VA"
block|,
literal|41.902916
block|,
literal|12.453389
block|,
literal|"Vatican City"
block|}
block|,
block|{
literal|"VC"
block|,
literal|12.984305
block|,
operator|-
literal|61.287228
block|,
literal|"Saint Vincent and the Grenadines"
block|}
block|,
block|{
literal|"VE"
block|,
literal|6.42375
block|,
operator|-
literal|66.58973
block|,
literal|"Venezuela"
block|}
block|,
block|{
literal|"VG"
block|,
literal|18.420695
block|,
operator|-
literal|64.639968
block|,
literal|"British Virgin Islands"
block|}
block|,
block|{
literal|"VI"
block|,
literal|18.335765
block|,
operator|-
literal|64.896335
block|,
literal|"U.S. Virgin Islands"
block|}
block|,
block|{
literal|"VN"
block|,
literal|14.058324
block|,
literal|108.277199
block|,
literal|"Vietnam"
block|}
block|,
block|{
literal|"VU"
block|,
operator|-
literal|15.376706
block|,
literal|166.959158
block|,
literal|"Vanuatu"
block|}
block|,
block|{
literal|"WF"
block|,
operator|-
literal|13.768752
block|,
operator|-
literal|177.156097
block|,
literal|"Wallis and Futuna"
block|}
block|,
block|{
literal|"WS"
block|,
operator|-
literal|13.759029
block|,
operator|-
literal|172.104629
block|,
literal|"Samoa"
block|}
block|,
block|{
literal|"XK"
block|,
literal|42.602636
block|,
literal|20.902977
block|,
literal|"Kosovo"
block|}
block|,
block|{
literal|"YE"
block|,
literal|15.552727
block|,
literal|48.516388
block|,
literal|"Yemen"
block|}
block|,
block|{
literal|"YT"
block|,
operator|-
literal|12.8275
block|,
literal|45.166244
block|,
literal|"Mayotte"
block|}
block|,
block|{
literal|"ZA"
block|,
operator|-
literal|30.559482
block|,
literal|22.937506
block|,
literal|"South Africa"
block|}
block|,
block|{
literal|"ZM"
block|,
operator|-
literal|13.133897
block|,
literal|27.849332
block|,
literal|"Zambia"
block|}
block|,
block|{
literal|"ZW"
block|,
operator|-
literal|19.015438
block|,
literal|29.154857
block|,
literal|"Zimbabwe"
block|}
block|,   }
decl_stmt|;
specifier|public
specifier|static
name|ScannableTable
name|eval
parameter_list|(
name|boolean
name|b
parameter_list|)
block|{
return|return
operator|new
name|ScannableTable
argument_list|()
block|{
specifier|public
name|Enumerable
argument_list|<
name|Object
index|[]
argument_list|>
name|scan
parameter_list|(
name|DataContext
name|root
parameter_list|)
block|{
return|return
name|Linq4j
operator|.
name|asEnumerable
argument_list|(
name|ROWS
argument_list|)
return|;
block|}
empty_stmt|;
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
literal|"country"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|add
argument_list|(
literal|"latitude"
argument_list|,
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
operator|.
name|add
argument_list|(
literal|"longitude"
argument_list|,
name|SqlTypeName
operator|.
name|DECIMAL
argument_list|)
operator|.
name|nullable
argument_list|(
literal|true
argument_list|)
operator|.
name|add
argument_list|(
literal|"name"
argument_list|,
name|SqlTypeName
operator|.
name|VARCHAR
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
specifier|public
name|Statistic
name|getStatistic
parameter_list|()
block|{
return|return
name|Statistics
operator|.
name|of
argument_list|(
literal|246D
argument_list|,
name|ImmutableList
operator|.
name|of
argument_list|(
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ImmutableBitSet
operator|.
name|of
argument_list|(
literal|3
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|Schema
operator|.
name|TableType
name|getJdbcTableType
parameter_list|()
block|{
return|return
name|Schema
operator|.
name|TableType
operator|.
name|TABLE
return|;
block|}
specifier|public
name|boolean
name|isRolledUp
parameter_list|(
name|String
name|column
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
specifier|public
name|boolean
name|rolledUpColumnValidInsideAgg
parameter_list|(
name|String
name|column
parameter_list|,
name|SqlCall
name|call
parameter_list|,
name|SqlNode
name|parent
parameter_list|,
name|CalciteConnectionConfig
name|config
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|;
block|}
block|}
end_class

begin_comment
comment|// End CountriesTableFunction.java
end_comment

end_unit

