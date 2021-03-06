<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE html>
<html>
<head>
    <base href="<%=basePath%>">

    <title>My JSP 'caseMain.jsp' starting page</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <link rel="stylesheet" type="text/css" href="css/metro.css">
    <link rel="stylesheet" href="css/metro-icons.css">
    <link href="css/mobiscroll.animation.css" rel="stylesheet" type="text/css" />
    <link href="css/mobiscroll.icons.css" rel="stylesheet" type="text/css" />
    <link href="css/mobiscroll.frame.css" rel="stylesheet" type="text/css" />
    <link rel="stylesheet" href="css/bootstrap-select.css"/>
    <link rel="stylesheet" href="css/bootstrap.css">
    <link href="css/mobiscroll.frame.ios.css" rel="stylesheet" type="text/css" />
    <link href="css/mobiscroll.scroller.css" rel="stylesheet" type="text/css" />
    <link href="css/mobiscroll.scroller.ios.css" rel="stylesheet" type="text/css" />
    <link href="css/mobiscroll.image.css" rel="stylesheet" type="text/css" />
    <style type="text/css">
        body {
            padding: 1em;
            margin: 0;
            font-size: 16px;
            font-family: arial, verdana, sans-serif;
        }

        input,
        select {
            width: 100%;
            padding: .625em;
            margin: 0 0 .625em 0;
            border: 1px solid #aaa;
            -webkit-box-sizing: border-box;
            -moz-box-sizing: border-box;
            box-sizing: border-box;

        }
    </style>
    <link rel="stylesheet" href="css/metro-responsive.css">
    <link rel="stylesheet" href="css/metro-schemes.css">
    <script type="text/javascript" src="js/jquery-3.0.0.min.js"></script>
    <script type="text/javascript" src="js/metro.min.js"></script>
    <script src="js/bootstrap.js"></script>
    <script type="text/javascript">
        function showDialog(id){
            var dialog = $("#"+id).data('dialog');
            if (!dialog.element.data('opened')) {
                dialog.open();
            } else {
                dialog.close();
            }
        }
        function check(value){
            $.ajax({
                //这里的需要Struts.xml的<action/>的name属性一致。
                url:'checkStudent.action',
                //提交类型
                type:'POST',
                //提交数据给Action传入数据
                data:{'s_no':value},
                //返回的数据类型
                dataType:'json',
                //成功是调用的方法
                success:function(data){
                    //获取Action返回的数据用  data.Action中的属性名 获取
                    var studentInfo = eval("("+data+")");
                    var name = document.getElementById("name");
                    var className = document.getElementById("class");
                    var grade = document.getElementById("grade");
                    var no = document.getElementById("no");
                    name.textContent = studentInfo.name;
                    className.textContent = studentInfo.class;
                    grade.textContent = studentInfo.grade;
                    no.textContent = studentInfo.no;
                },
                error:function () {
                    alert("错误")
                }
            });

        }
    </script>
</head>
<body>
<jsp:include page="header.jsp"></jsp:include>
<div hidden="true">
    <label for="theme">Theme</label>
    <select name="theme" id="theme" class="settings" >
    </select>
</div>

<%--<div data-role="dialog" id="dialog" class="padding20" data-close-button="true" data-type="alert">--%>
<%--<h1>提示信息</h1>--%>
<%--<p>--%>
<%--没有查询到该学号，请确认是否输入错误--%>
<%--</p>--%>
<%--</div>--%>
<div data-role="dialog" id="dialog1" class="padding20" data-close-button="true" data-type="alert">
    <h1>提示信息</h1>
    <p>
        有评分项未输入
    </p>
</div>
<script>
    function showDialog(id){
        var dialog = $(id).data('dialog');
        dialog.open();
    }
</script>
<center> <h2>修改考生成绩 </h2></center>
<s:form  action="updateScore.action" method="post" 	enctype="multipart/form-data" onsubmit="return valid()">
    <div class="col-lg-7 col-lg-offset-4  col-lg-6 col-sm-offset-3 col-xs-8 col-xs-offset-2">
        <label>学号:<s:property value="#request.student.SNo"></s:property></label>
        <span id="no"></span>
        <label>姓名:<s:property value="#request.student.SName"></s:property></label>
        <span id="name"></span>
        <label>年级:<s:property value="#request.student.SGrade"></s:property></label>
        <span id="grade"></span>
        <label>班级:<s:property value="#request.student.mclass.className"></s:property></label>
        <span id="class"></span>
    </div>
    <table class="table striped hovered cell-hovered border bordered" id="t">
        <thead>
        <tr>
            <th class="sortable-column">
                <center>
                    <h3>
                        <s:property value="#request.RName"/>
                    </h3>
                </center>
            </th>
            <th class="sortable-column">
                <center>
                    <h3>
                        <s:property value="#request.RContent"/>
                    </h3>
                </center>
            </th>

            <th class="sortable-column">
                <h3>
                    <s:property value="#request.RScore"/>
                </h3>
            </th>
            <th class="sortable-column" width="10%">
                <h3>
                    打分
                </h3>
            </th>
        </tr>
        </thead>


        <s:iterator value="#request.RequireAndScore" id="RequireAndScore" status="st">
            <tr>
                <td>
                    <s:property value="#RequireAndScore.name"/>
                </td>
                <td>
                    <s:property value="#RequireAndScore.content"/>
                </td>
                <td>
                    <h4 id="<s:property value="#st.index"/>"><s:property value="#RequireAndScore.score"/></h4>
                </td>
                <td>
                        <%--<div class="input-control text info">--%>
                    <input type="number"  max="<s:property value="#RequireAndScore.score"/>" id="number<s:property value="#st.index"/>" width="5%" name="part" value="<s:property value="#RequireAndScore.uncommitedScore"/>"/>
                        <%--</div>--%>
                </td>
            </tr>
        </s:iterator>
    </table>
    <input type="hidden" id="score" name="score.scScore">
    <input type="hidden" id="totalScore" name="score.scTotalScore">
    <input type="hidden" id="stId" name="score.scId" value="<s:property value="#request.stc_id"/>">
    <input type="hidden" id="TId" name="TId" value="<s:property value="#session.user.TId"/>">
    <input type="hidden" name="testId" value="<s:property value="#request.testId"/>">
    <center>
        <button class="button loading-pulse lighten primary" type="submit" >确认无误，提交</button>
    </center>
</s:form>
<script type="text/javascript">
    function valid(){
        var score = "air";
        var totalScore = 0;
        var flag = 1;
        $("input[name='part']").each(function(){
            if($(this).val()==""){
                showDialog(dialog1);
                flag = 0;
                return false;
            }
            else{
                score = score+"," + $(this).val();
                totalScore = totalScore + parseInt($(this).val());
            }
        });
        score = score.substring(4,score.length);
        if(flag==1){
            alert(score);
            console.log(totalScore);
            $('#score').val(score);
            $('#totalScore').val(totalScore);
            return true;
        }
        else{
            return false;
        }
    }
</script>
<script src="js/mobiscroll.dom.js"></script>
<script src="js/mobiscroll.core.js"></script>
<script src="js/mobiscroll.scrollview.js"></script>
<script src="js/mobiscroll.frame.js"></script>
<script src="js/mobiscroll.frame.ios.js"></script>
<script src="js/mobiscroll.scroller.js"></script>
<script src="js/mobiscroll.i18n.zh.js"></script>
<script>
    var tab=document.getElementById("t");
    for(var i = 1;i<=tab.rows.length-1;i++)
    {
        var a = document.getElementById("select"+i);
        a.setAttribute("multiple", true);
        a.setAttribute("data-done-button", true);
    }
    (function ($) {
        var t = document.getElementById("t");
        var l = t.rows.length;
        function init() {
            for(var j = 0;j<=l-2;j++) {
                var maxNum = Number($("#number"+j).attr("max"));
                var d = new Array;
                for (var i = 0; i <= maxNum; i++) {
                    d.push(i);
                }
                console.log(maxNum);
                mobiscroll.scroller("#number"+j, {
                    theme: theme,
                    display: display,
                    lang: lang,
                    wheels: [
                        [{
                            label: 'First wheel',
                            data: d
                        }
                        ]
                    ]
                });
            }
        }
        var theme, display, lang;
        $('.settings').on('change', function () {
            theme = "ios";
            display = "center";
            lang = "zh";
            init();
        });
        $('#theme').trigger('change');
    })(mobiscroll.$);
</script>
<script src="js/bootstrap-select.js"></script>
<script type="text/javascript">
    function addError(id) {
        var error = document.getElementById("error"+id);
        var obj=document.getElementById("select"+id);
        var hidden = document.getElementById("hidden"+id);
        hidden.value = hidden.value + "," + error.value;
        console.log(hidden.value);

        obj.options.add(new Option(error.value,error.value));
        error.value = "";
        $('.selectpicker').selectpicker('render');
        $('.selectpicker').selectpicker('refresh');
    }
</script>
</body>
</html>
