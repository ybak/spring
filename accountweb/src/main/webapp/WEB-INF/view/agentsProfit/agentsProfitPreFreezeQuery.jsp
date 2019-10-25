
<%@ page pageEncoding="utf-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix='sec' uri='http://www.springframework.org/security/tags'%>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
	<!-- jqGrid plugin -->
	<link href="${ctx}/css/plugins/select2/select2.min.css" rel="stylesheet">
	<link href="${ctx}/css/plugins/select2/select2-skins.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${ctx}/css/icheck-css/custom.css" />
	<link rel="stylesheet" href="${ctx}/css/skins/all.css" />
	 <!-- Sweet Alert -->
    <link href="${ctx}/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
	<link href="${ctx}/css/plugins/jQueryUI/jquery-ui-1.10.4.custom.min.css" rel="stylesheet">
	<link href="${ctx}/css/plugins/jqGrid/ui.jqgrid.css" rel="stylesheet">
	<link href="${ctx}/css/plugins/bootstrapTour/bootstrap-tour.min.css" rel="stylesheet">
	<link href="${ctx}/css/plugins/webuploader/webuploader.css" rel="stylesheet">
	<link href="${ctx}/css/plugins/sweetalert/sweetalert.css" rel="stylesheet">
	<link href="${ctx}/css/plugins/bootstrap-datepicker/bootstrap-datepicker3.min.css" rel="stylesheet">
	<link href="${ctx}/css/plugins/select2/select2.min.css" rel="stylesheet">
	</head>
<body>
	<div class="row wrapper border-bottom white-bg page-heading">
		<div class="col-lg-10 location-nav" >
            <div class="pull-left">当前位置</div>
            <em class=""></em>
            <div class="pull-left">代理商分润管理</div>
            <em class=""></em>
            <div class="pull-left active">代理商分润预冻结记录查询</div>
        </div>
	</div>
	<!-- 填充内容开始 -->
	<div class="row wrapper wrapper-content  animated fadeInRight">
		<div class="col-lg-12">
			<div class="ibox float-e-margins">
				<div class="ibox-content">
					 <form class="form-horizontal" id="agentsProfitPreFreezeForm">
                          <div class="form-group" >
                          	 
								<label class="col-sm-2 control-label">代理商名称:</label>
		                           <div class="col-sm-3">
                                    <select id="agentNo" class="form-control" name="agentNo">
								    
									</select>
								    
								</div>
								 <label class="col-sm-2 control-label">操作时间:</label>
                           		<div class="col-sm-4">
		                            <div class="input-daterange input-group" id="datepicker">
									    <input type="text" class="input-sm form-control" name="freezeTime1" />
									    <span class="input-group-addon">~</span>
									    <input type="text" class="input-sm form-control" name="freezeTime2" />
									</div>   
								</div>
							</div> 
                           <div class="form-group" >
								<label class="col-sm-2 control-label">冻结原因:</label>
    							<div class="col-sm-2">
		                            <select class="form-control" name="freezeReason" id="freezeReason"> 
									         <option value="ALL" selected="selected">全部</option>
                                             <c:forEach var="vf" items="${freezeReasonList}">
                                                <option value="${vf.sysValue}">
                                                    ${vf.sysName}
                                                </option>
                                             </c:forEach>
										</select>       
								</div>
							</div> 
                            <div class="clearfix lastbottom"></div>
                            <div class="form-group" style="margin-bottom:0">
                                    <label class="col-sm-2 control-label aaa"></label>
                                   <!-- <div class="col-sm-12 col-sm-offset-13  "> -->
                                   	   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                       	<button class="btn btn-success" type="submit"><span class="glyphicon gly-search"></span>查询</button> 
                                       	<sec:authorize access="hasAuthority('agentsProfitPreFreezeQuery:preFreeze')">
                                       	<button class="btn btn-success" type="button" id="preFreeze" style="margin-left:10px;"><span class="glyphicon"></span>预冻结</button>
                                       	</sec:authorize>
                                       	<sec:authorize access="hasAuthority('agentsProfitPreFreezeQuery:batchPreFreeze')">
                                       	<button class="btn btn-success" type="button" id="batchPreFreeze" style="margin-left:10px;"><span class="glyphicon"></span>批量预冻结</button>
                                       	</sec:authorize>
                                       	<sec:authorize access="hasAuthority('agentsProfitPreFreezeQuery:export')">
                                       	<button class="btn btn-danger col-sm-offset-14" type="button" onclick="exportExcel()"><span class="glyphicon gly-out"></span>导出</button>
                                       	</sec:authorize>
                                       	<button class="btn btn-default col-sm-offset-14" type="reset" id="reset"><span class="glyphicon gly-trash"></span>清空</button>
                                   <!-- </div> -->
                             </div>
                           
                    </form>
				</div>
			</div>
		</div>
		
		<div class="col-lg-12">
			<div class="ibox ">
				<div class="ibox-content">
					<div class="jqGrid_wrapper">
					<table id="table_list_agentsProfitPreFreeze"></table>
					<div id="pager_list_agentsProfitPreFreeze"></div>
                    <br /><br /><br /><br /><br /><br /><br /><br /><br /><br />
					</div>
				</div>
			</div>
		</div>
		
	</div>
	<!-- 填充内容结束 -->
		
</body>
    
<title>
<script src="${ctx}/js/icheck.min.js"></script>
<script src="${ctx}/js/custom.min.js"></script>
<script src="${ctx}/js/plugins/select2/select2.full.min.js"></script>
<script src="${ctx}/js/plugins/select2/i18n/zh-CN.js"></script>
<!-- Sweet alert -->
<script src="${ctx}/js/plugins/sweetalert/sweetalert.min.js"></script>
<script src="${ctx}/js/plugins/bootstrap-datepicker/bootstrap-datepicker.min.js"></script>
<script src="${ctx}/js/plugins/bootstrap-datepicker/locales/bootstrap-datepicker.zh-CN.min.js"></script>
	<script type="text/javascript">

    $("#preFreeze").click(function() {
    	location.href='${ctx}/agentsProfitAction/toAgentsProfitPreFreeze.do';
    });
    $("#batchPreFreeze").click(function() {
    	location.href='${ctx}/agentsProfitAction/toAgentsProfitBatchPreFreeze.do';
    });
    
	/*表单提交时的处理*/
	function exportExcel() {  
		var data = $("#agentsProfitPreFreezeForm").serialize();
		$.download('${ctx}/agentsProfitAction/exportPreFreezeResult.do',data,'post');
    }
    
 // 去除空格啊
 	$('input').blur(function() {
		replaceSpace(this);
	})
	function replaceSpace(obj) {
		obj.value = obj.value.replace(/\s/gi, '')
	} 
	// Ajax 文件下载
	jQuery.download = function(url, data, method){
	    // 获得url和data
	    if( url && data ){ 
	        // data 是 string 或者 array/object
	        data = typeof data == 'string' ? data : jQuery.param(data);
	        // 把参数组装成 form的  input
	        var inputs = '';
	        jQuery.each(data.split('&'), function(){ 
	            var pair = this.split('=');
	            inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />';
	        });
	        // request发送请求
	        jQuery('<form action="'+ url +'" method="'+ (method||'post') +'">'+inputs+'</form>')
	        .appendTo('body').submit().remove();
	    };
	};

		$("#agentsProfitPreFreezeForm").submit(function() {
			$("#table_list_agentsProfitPreFreeze").setGridParam({
				datatype : 'json',
				page : 1
			//Replace the '1' here
			}).trigger("reloadGrid");
			return false;
		});
		
		function getParams(o) {
			var data = $("#agentsProfitPreFreezeForm").serializeArray();
			$.each(data, function() {
				o[this.name] = this.value || '';
			});
			o.agentNo = $("#agentNo").select2("val");
			console.info($("#agentNo").select2("val"));
		}
    
		$(document).ready(function() {
			var lastsel;
			var data = $("#agentsProfitPreFreezeForm").serialize();
			// 初始化表格
			$("#table_list_agentsProfitPreFreeze")
					.jqGrid({url : "${ctx}/agentsProfitAction/findAgentsProfitPreFreezeList.do",
								datatype : "local",
								mtype : "POST",
								height:"auto",
								autowidth: true,
								shrinkToFit: false,
								autoScroll: false,
								rowNum: 10,
								rowList: [ 10, 20 ],
								colNames : [ '代理商编号', '代理商名称', '冻结时间',  '机具款预冻结金额', '其他预冻结金额', '分润账户冻结金额', '活动补贴冻结金额', '预冻金额', '操作人', '备注' ],
								colModel : [
										{ name : 'agentNo', index : 'agentNo', width : 150, align : "center" },
										{ name : 'agentName', index : 'agentName', width : 150, align : "center" },
										{ name : 'freezeTime', index : 'freezeTime', width : 200, align : "center",formatter : function(val) {return myFormatDate(val,"yyyy-MM-dd hh:mm:ss");} },
										//{ name : 'freezeReason', index : 'freezeReason', width : 150, align : "center", formatter:  customFreezeReasonFmatter },
										{ name : 'terminalFreezeAmount', index : 'terminalFreezeAmount', width : 150, align : "center" ,
											formatter : 'number'},
										{ name : 'otherFreezeAmount', index : 'otherFreezeAmount', width : 150, align : "center",
												formatter : 'number' },
										{ name : 'fenFreezeAmount', index : 'fenFreezeAmount', width : 150, align : "center",
											formatter : 'number' },
										{ name : 'activityFreezeAmount', index : 'activityFreezeAmount', width : 150, align : "center",
											formatter : 'number' },
										{ name : 'freezeAmount', index : 'freezeAmount', width : 150, align : "center",
											formatter : 'number' },
										{ name : 'operater', index : 'operater', width : 150, align : "center" },
										{ name : 'remark', index : 'remark', width : 200, align : "center" }
										 ],
								multiselect : false,//支持多项选择
								multiselectWidth : 80,
								multiboxonly: false,
								pager : "#pager_list_agentsProfitPreFreeze",
								viewrecords : true,
								hidegrid : false,
								reloadAfterSubmit: true,
								jsonReader : {
									root : "result",
									total : "totalPages",
									page : "pageNo",
									pageSize : "pageSize",
									records : "totalCount",
									repeatitems : false
								},
								prmNames : {
									page : "pageNo",
									rows : "pageSize"
								},
								serializeGridData : function(postData) {
									getParams(postData);
									return postData;
								},
								gridComplete : function() { //在此事件中循环为每一行添加修改和删除链接
									//hideCheckBox();
								},
							});
					jQuery("#table_list_agentsProfitPreFreeze").jqGrid('setFrozenColumns');
					$(window).bind('resize',
					function() {
						var width = $('.jqGrid_wrapper').width();
						$('#table_list_agentsProfitPreFreeze').setGridWidth(width);
					});
			
            $('.input-daterange').datepicker({
                format: "yyyy-mm-dd",
                language: "zh-CN",
                todayHighlight: true,
                autoclose: true,
                clearBtn: true
            });
		});
		 $("#reset").on("click", function () { 
	            //$exampleMulti.val(null).trigger("change");
	            //$('#agentNo').select2("val", "ALL");
	            $("#agentNo").empty().trigger("change");
	        });
		function formatRepo (repo) {
	        if (repo.loading) return repo.text;
			//console.info(repo.id);
			return repo.id+'('+repo.text+')';  
	      }

	      function formatRepoSelection (repo) {
	    	  //console.info("formatRepoSelection:"+ repo.text);
	    	  return repo.id+'('+repo.text+')';
	        //return repo.full_name || repo.text;
	      }
	      
	      function parseSelectParams(params){
	    	  if(params && params.term){
	    		  return encodeURI(params.term);
	    	  }
	    	  else
	    		  return null;
	      }
	
	   // turn the element to select2 select style
	      $('#agentNo').select2({
		    	  		ajax: {
			    		  	url: "${ctx}/agentsProfitAction/queryAgentName.do",
			    		    dataType: 'json',
			    		    delay: 250,
			    		    data: function (params) {
			    		      return {
			    		        q: parseSelectParams(params), // search term
			    		        page: params.page
			    		      };
			    		    },
			    		    processResults: function (data, params) {
			    		      // parse the results into the format expected by Select2
			    		      // since we are using custom formatting functions we do not need to
			    		      // alter the remote JSON data, except to indicate that infinite
			    		      // scrolling can be used
			    		      params.page = params.page || 1;

			    		      return {
			    		        results: data,
			    		        pagination: {
			    		          more: (params.page * 30) < data.total_count
			    		        }
			    		      };
			    		    },
			    		    cache: true
		    		  },
		    		  placeholder: "选择代理商名称",  
		    		  allowClear: true,
		    		  width: '100%',
		    		  // containerCssClass: 'tpx-select2-container',
		    		  // dropdownCssClass: 'tpx-select2-drop',
		    		  escapeMarkup: function (markup) { return markup; }, // let our custom formatter work
		    		  minimumInputLength: 0,
		    		  language: "zh-CN",
		    		  templateResult: formatRepo, // omitted for brevity, see the source of this page
		    		  templateSelection: formatRepoSelection // omitted for brevity, see the source of this page
		    		}
	    		  );	
	
		function customFreezeReasonFmatter(cellvalue, options, rowObject){  
			<c:forEach var="freezeReason" items="${freezeReasonList}">
				  if(cellvalue == '${freezeReason.sysValue}'){
					  return '${freezeReason.sysName}';
				  }
			 </c:forEach>	
			 return "" ;
		}
	</script>
	 </title>
</html>  
      