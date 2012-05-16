$enrollmentAlerts:{
enrollmentAlert|
Schedule Name : $enrollmentAlert.scheduleName$
Current Milestone : $enrollmentAlert.currentMilestoneName$
Alert will be raised at : $enrollmentAlert.dueWindowAlertTimings$
Status : $enrollmentAlert.status$
$if(enrollmentAlert.showAlertWarning)$
    WARNING - Your preferred alert time for today : $enrollmentAlert.preferredAlertTime$ has already passed. This alert will be raised tomorrow.
$endif$
---------------------------------------------------------------------------------------------------------------------

}
$