<?xml version="1.0"?>
<case xmlns="http://commcarehq.org/case/transaction/v2" case_id="$caseId$" date_modified="2012-04-03" user_id="e819879aaf53a3787e0fd88993ac105d">
  <create>
    <case_type>cc_bihar_newborn</case_type>
    <case_name>RAM</case_name>
    <owner_id>d823ea3d392a06f8b991e9e49394ce45</owner_id>
  </create>
  <update>
    <gender>male</gender>
    <dob>$dobDate$</dob>
  </update>
    <index>
      <mother_id case_type="cc_bihar_pregnancy">$motherCaseId$</mother_id>
    </index>
</case>