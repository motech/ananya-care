<?xml version='1.0' ?>
<data uiVersion="1" version="1" name="Registration" xmlns:cc="http://commcarehq.org/xforms" xmlns="http://bihar.commcarehq.org/pregnancy/registration">
<n0:meta xmlns:n0="http://openrosa.org/jr/xforms">
<n0:deviceID>T5Y0ATKMW970QJ63YHF1V1QW1</n0:deviceID>
<n0:timeStart>$dob$T12:44:36.013Z</n0:timeStart>
<n0:timeEnd>$dob$T12:48:06.019Z</n0:timeEnd>
<n0:username>care</n0:username>
<n0:userID>$userId$</n0:userID>
<n0:instanceID>$instanceId$</n0:instanceID>
<cc:appVersion>v1.4.0dev (b0a68b-908411-unvers-2.1.0-Nokia/S40-generic) #12 b:2012-Apr-02 r:2012-Apr-03</cc:appVersion>
</n0:meta>
<n1:case case_id="$motherCaseId$" date_modified="$dob$" user_id="$userId$" xmlns:n1="http://commcarehq.org/case/transaction/v2">
<n1:update>
<n1:mobile_number>111111</n1:mobile_number>
<n1:mother_dob>2000-02-02</n1:mother_dob>
<n1:num_boys>1</n1:num_boys>
<n1:add>$dob$</n1:add>
<n1:last_visit_type>reg</n1:last_visit_type>
<n1:date_last_visit>$dob$</n1:date_last_visit>
<n1:family_planning_type>ppiud_at_delivery</n1:family_planning_type>
<n1:edd />
<n1:age>12</n1:age>
<n1:lmp />
<n1:date_next_pnc>$dob$</n1:date_next_pnc>
</n1:update>
</n1:case>
<jsy_beneficiary>no</jsy_beneficiary>
<display_name>OK</display_name>
<children>yes</children>
<num_girls>1</num_girls>
<num_boys>1</num_boys>
<education>primary</education>
<mobile_number_whose>mobile_mine</mobile_number_whose>
<resident>resident</resident>
<vegetarian>yes</vegetarian>
<is_pregnant>no</is_pregnant>
<last_preg_full_term>yes</last_preg_full_term>
<last_preg_c_section>no</last_preg_c_section>
<recently_delivered>yes</recently_delivered>
<how_many_children>1</how_many_children>
<num_children>1</num_children>
<child_dob>$dob$</child_dob>
<child_info>
<n2:case case_id="$caseId$" date_modified="$dob$" user_id="$userId$" xmlns:n2="http://commcarehq.org/case/transaction/v2">
<n2:create>
<n2:case_type>cc_bihar_newborn</n2:case_type>
<n2:owner_id>$ownerId$</n2:owner_id>
<n2:case_name>$name$</n2:case_name>
</n2:create>
<n2:update>
<n2:gender>male</n2:gender>
<n2:dob>$dob$</n2:dob>
</n2:update>
<n2:index>
<n2:mother_id case_type="cc_bihar_pregnancy">$motherCaseId$</n2:mother_id>
</n2:index>
</n2:case>
<child_have_a_name>yes</child_have_a_name>
<child_name>$name$</child_name>
<gender>male</gender>
<weight>3.0</weight>
<add_vaccinations>yes</add_vaccinations>
<baby_bcg>no</baby_bcg>
<baby_opv0>no</baby_opv0>
<baby_hep_b_0>no</baby_hep_b_0>
<abnormalities>no</abnormalities>
</child_info>
<delivery_nature>vaginal</delivery_nature>
<where_born>hospital</where_born>
<which_hospital>block_phc</which_hospital>
<in_district>yes</in_district>
<jsy_money>yes</jsy_money>
<complications>no</complications>
<used_fp>yes</used_fp>
<iud_used>yes</iud_used>
<ocp_used>yes</ocp_used>
<injectible>yes</injectible>
<condoms>yes</condoms>
<good_to_register>yes</good_to_register>
<success>OK</success>
<dob_est />
<age_calc>12</age_calc>
<lmp_calc />
<nextvisit_bp>OK</nextvisit_bp>
<nextvisittype>pnc</nextvisittype>
<temp />
</data>