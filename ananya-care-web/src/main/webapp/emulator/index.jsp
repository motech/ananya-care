<%@page import="org.motechproject.util.DateUtil" %>
<%@page import="java.lang.reflect.Method" %>
<%@page import="java.util.Date" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Properties" %>
<%@page import="org.springframework.context.ApplicationContext" %>
<%@page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%
    boolean fakeTimeAvailable = false;
    String os = (String) System.getenv().get("OS");
    if (os == null || os.indexOf("indows") < 0) {
        try {
            java.lang.reflect.Method m = java.lang.ClassLoader.class.getDeclaredMethod("loadLibrary", Class.class, String.class, Boolean.TYPE);
            m.setAccessible(true);
            m.invoke(null, java.lang.System.class, "jvmfaketime", false);
            System.registerFakeCurrentTimeMillis();


            if (request.getMethod() == "POST") {
                try {

                    String date = request.getParameter("date");
                    String time = request.getParameter("time");
                    Date dateValue = new SimpleDateFormat("yyyy-MM-dd").parse(date);
                    Date timeValue = new SimpleDateFormat("HH:mm:ss").parse(time.substring(1, time.length()));
                    dateValue.setHours(timeValue.getHours());
                    dateValue.setMinutes(timeValue.getMinutes());
                    dateValue.setSeconds(timeValue.getSeconds());
                    System.out.println("Posted date " + time.substring(1, time.length() - 1));

                    System.deregisterFakeCurrentTimeMillis();

                    long diffValue = (dateValue.getTime() - System.currentTimeMillis());

                    System.registerFakeCurrentTimeMillis();
                    System.out.println("offset calculated " + diffValue);
                    System.setTimeOffset(diffValue);
                    System.out.println("Date :" + new Date());
                } catch (java.lang.Exception e) {
                    out.println("Error: " + e.getMessage());
                    return;
                }
                out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                return;
            }
            fakeTimeAvailable = true;
        } catch (Exception ignore) {
        }
    }
    java.util.Date curdate = new java.util.Date();
    if (!fakeTimeAvailable)
        curdate = DateUtil.now().toDate();

%>
<html>
<head>
<script type="text/javascript">
    var djConfig = {parseOnLoad:false, isDebug:false, locale:'en_in'};
</script>
<%
    ApplicationContext appCtx = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
    Properties tamaProperties = (Properties) appCtx.getBean("tamaProperties", Properties.class);
    String applicationVersion = (String) tamaProperties.getProperty("application.version");
%>
<script src='<%= application.getContextPath()%>/resources-<%= applicationVersion%>/dojo/dojo.js' type="text/javascript"
        djConfig="parseOnLoad: true"></script>
<script type="text/javascript">

    dojo.require("dijit.dijit"); // loads the optimized dijit layer
    dojo.require("dijit.form.DateTextBox");
    dojo.require("dijit.form.TimeTextBox");


    dojo.addOnLoad(function () {
        new dijit.form.TimeTextBox({
                    name:"time",
                    value:new Date(0, 0, 0, <%=curdate.getHours()%>, <%=curdate.getMinutes()%>, <%=curdate.getSeconds()%>),
                    constraints:{
                        timePattern:'HH:mm:ss',
                        clickableIncrement:'T00:15:00',
                        visibleIncrement:'T00:15:00',
                        visibleRange:'T01:00:00'
                    }
                },
                "time");
    });
</script>
<link rel="stylesheet" type="text/css" href="<%= application.getContextPath() %>/resources-<%= applicationVersion%>/dijit/themes/tundra/tundra.css"/>
<style>
    .dijitPopup {
        background-color: lightgray;
        border: 0 none;
        margin: 0;
        padding: 0;
        position: absolute;
    }
</style>
<script>


    function displayMsg(data) {
        dojo.byId('timeMessage').style.display = "";
        dojo.byId('timeMessage').innerHTML = data;
        setTimeout(function () {
            dojo.byId('timeMessage').style.display = "none";
        }, 3000);
    }

    function submitTime() {
        if (dojo.byId('fakeTimeOption').checked) {
            dojo.xhrPost({
                form:"timeForm",
                load:function (data, ioArgs) {
                    displayMsg("Updated: " + data);
                },
                error:function (err, ioArgs) {
                    alert(err);
                }
            });
        } else {
            var urlString = "<%=application.getContextPath() %>" + "/motech-delivery-tools/datetime/update?date=" + dojo.byId('date').value + "&hour=" + dijit.byId('time').value.getHours() + "&minute=" + dijit.byId('time').value.getMinutes();
            dojo.xhrGet({
                url:urlString,
                load:function (data) {
                    displayMsg(data);
                }
            });
        }
    }
</script>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
<script src="js/Recording.js"></script>
<script type="text/javascript">
    $(document).ready(function () {
        $('#missedCallButton').click(function () {
            dojo.xhrPost({
                url:'<%=application.getContextPath() %>/ivr/reply/callback?external_id=' + $('#missedCall').val() + "&call_type=Outbox",
                content:{ 'phone_no':$('#phone').val(), 'status':'ring', 'sid':callId},
                load:function () {
                    alert('Posted missed call');
                }
            })
        });

        window.cacheControl = new CacheControl();
        window.recording = new Recording(window.cacheControl);
    });
</script>
<script type="text/javascript">
    /**
     * jQuery Cookie plugin
     *
     * Copyright (c) 2010 Klaus Hartl (stilbuero.de)
     * Dual licensed under the MIT and GPL licenses:
     * http://www.opensource.org/licenses/mit-license.php
     * http://www.gnu.org/licenses/gpl.html
     *
     */
    jQuery.cookie = function (key, value, options) {

        // key and at least value given, set cookie...
        if (arguments.length > 1 && String(value) !== "[object Object]") {
            options = jQuery.extend({}, options);

            if (value === null || value === undefined) {
                options.expires = -1;
            }

            if (typeof options.expires === 'number') {
                var days = options.expires, t = options.expires = new Date();
                t.setDate(t.getDate() + days);
            }

            value = String(value);

            return (document.cookie = [
                encodeURIComponent(key), '=',
                options.raw ? value : encodeURIComponent(value),
                options.expires ? '; expires=' + options.expires.toUTCString() : '', // use expires attribute, max-age is not supported by IE
                options.path ? '; path=' + options.path : '',
                options.domain ? '; domain=' + options.domain : '',
                options.secure ? '; secure' : ''
            ].join(''));
        }

        // key and possibly options given, get cookie...
        options = value || {};
        var result, decode = options.raw ? function (s) {
            return s;
        } : decodeURIComponent;
        return (result = new RegExp('(?:^|; )' + encodeURIComponent(key) + '=([^;]*)').exec(document.cookie)) ? decode(result[1]) : null;
    };
</script>

<script>
    var contextRoot = "<%=application.getContextPath() %>/";
    var collectdtmf = 1;
    var dtmf = "";
    var callId;

    function deleteCookie() {
        var d = new Date();
        document.cookie = "v0=1;expires=" + d.toGMTString() + ";" + "path=<%=application.getContextPath() %>/ivr/;";

        alert(document.cookie);
    }
    function pollCall() {
        if ($('#poll_call').is(':checked'))
            $.getJSON("<%=application.getContextPath() %>/emulator/calls.jsp?phone=" + $('#phone').val(), function (data) {
                if (data.phone_no) {
                    if (confirm("Incoming call.. Receive?")) {
                        $('#phone').val(data.phone_no);
                        $('#dosage_id').val(data.dosage_id);
                        $('#regimen_id').val(data.regimen_id);
                        $('#times_sent').val(data.times_sent);
                        $('#total').val(data.total_times_to_send);
                        $('#call_id').val(data.call_id);
                        $('#retry_interval').val(data.retry_interval);
                        $('#is_outbound_call').attr('checked', (data.is_outbound_call === "true"));
                        $('#outbox_call').attr('checked', (data.outbox_call === "true"));
                    }
                }
                setTimeout(pollCall, 1000);
            });
    }

    $(function () {
        setTimeout(pollCall, 500);
    });
</script>
<style>
    .optional {
        display: none;
    }
</style>
</head>
<body>
<div>
    <div id="result">
    </div>
    <br/>
    <table>
        <tr>
            <td style="width:450px;"></td>
            <td>
                <form id="timeForm" action="index.jsp" method="POST">
                    <table>
                        <tr>
                            <td><input id="fakeTimeOption" type="radio" name="type"
                                       value="Use Faketime" <%=(fakeTimeAvailable?"checked=\'true\'":"disabled") %>> Use Faketime
                            </td>
                            <td><input type="radio" name="type" value=""/ <%=(fakeTimeAvailable ? "" : "checked=\'true\'") %>>Use DateUtil hack</td>
                        </tr>
                        <tr>
                            <td align="center" colspan="2" id="timeMessage" style="background-color:lightBlue;display:none;"></td>
                        </tr>
                        <tr>
                            <td>Date</td>
                            <td><input dojoType="dijit.form.DateTextBox" id="date" name="date"
                                       value="<%=new java.text.SimpleDateFormat("yyyy-MM-dd").format(curdate)%>" style="width:12em;"/></td>
                        </tr>
                        <tr>
                            <td>Time</td>
                            <td><input id="time" name="time" timePattern='HH:mm:ss'
                                       value="T<%=new java.text.SimpleDateFormat("HH:mm:ss").format(curdate)%>" style="width:12em;"/></td>
                        </tr>
                        <tr>
                            <td colspan="2" align="right"></td>
                        </tr>
                    </table>

                </form>
                <button onclick="submitTime();">Set Time</button>
            </td>
        </tr>
    </table>
    <br><br>
</span></div>
</div>
</body>
</html>