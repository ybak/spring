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
</head>
<body>
	<div class="row wrapper border-bottom white-bg page-heading">
		<div class="col-lg-10 location-nav" >
                <div class="pull-left">当前位置</div>
                <em class=""></em>
                <div class="pull-left">活动账户调账管理</div>
                <em class=""></em>
                <div class="pull-left active">调账余额查询</div>
        </div>
	</div>
	<!-- 填充内容开始 -->
	<div class="row wrapper wrapper-content  animated fadeInRight">
		<div class="col-lg-12">
			<div class="ibox float-e-margins">
				<div class="ibox-content">
					 <form class="form-horizontal" id="agentsProfitAccountForm">
                          <div class="form-group" >
                          	 
								<label class="col-sm-2 control-label">代理商名称:</label>
		                           <div class="col-sm-3">
                                   <select id="agentNo" class="form-control" name="agentNo">
									</select>
								</div>

							  <label class="col-sm-2 control-label">代理商级别:</label>
							  <div class="col-sm-2">
								  <select class="form-control" name="agentLevel" id="agentLevel">
									  <option value="ALL" selected="selected">全部</option>
									  <option value="1">1级</option>
									  <option value="2">2级</option>
									  <option value="3">3级</option>
									  <option value="4">4级</option>
									  <option value="5">5级</option>
									  <option value="6">6级</option>
									  <option value="7">7级</option>
									  <option value="8">8级</option>
									  <option value="9">9级</option>
									  <option value="10">10级</option>
									  <option value="11">11级</option>
									  <option value="12">12级</option>
									  <option value="13">13级</option>
									  <option value="14">14级</option>
									  <option value="15">15级</option>
									  <option value="16">16级</option>
									  <option value="17">17级</option>
									  <option value="18">18级</option>
									  <option value="19">19级</option>
									  <option value="20">20级</option>
								  </select>
							  </div>

						  </div>
						 <div class="form-group" >

							 <label class="col-sm-2 control-label">剩余预调账金额:</label>
							 <div class="col-sm-4 form-inline">
								 <input type="text" class="form-control" name="adjustAmount1" onkeyup="amount(this)"  >-
								 <input type="text" class="form-control" name="adjustAmount2" onkeyup="amount(this)"  >
							 </div>

							 <label class="col-sm-2 control-label">活动补贴账户余额:</label>
							 <div class="col-sm-4 form-inline">
								 <input type="text" class="form-control" name="activitySubsidyBalance1" onkeyup="amount(this)"  >-
								 <input type="text" class="form-control" name="activitySubsidyBalance2" onkeyup="amount(this)"  >
							 </div>

						 </div>

                            <div class="clearfix lastbottom"></div>
                            <div class="form-group" style="margin-bottom:0">
                                    <label class="col-sm-2 control-label aaa"></label>
                                   <!-- <div class="col-sm-12 col-sm-offset-13  "> -->
                                   	   <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                       	<button class="btn btn-success" type="submit"><span class="glyphicon gly-search"></span>查询</button> 
                                       	<button class="btn btn-default col-sm-offset-14" type="reset" id="reset"><span class="glyphicon gly-trash"></span>清空</button>
										<button class="btn btn-danger col-sm-offset-14" type="button" onclick="exportExcel()"><span class="glyphicon gly-out"></span>导出</button>
                                   <!-- </div> -->
                             </div>
                           
                    </form>
				</div>
			</div>
		</div>


		<div class="col-lg-12">
			<div class="ibox ">
				<div class="ibox-content">

						<span>汇总：剩余调账金额：<span id="allAdjustAmount">0.00</span> ,&nbsp;&nbsp;活动补贴账户余额：<span  id="activitySubsidyBalance">0</span>,&nbsp;&nbsp;活动补贴账户已冻结金额：<span  id="activitySubsidyFreeze">0.00</span>,&nbsp;&nbsp;
							活动补贴账户可用余额：<span  id="activitySubsidyAvailableBalance">0.00</span>&nbsp;&nbsp;</span>
					<br/>
					<br/>

					<div class="jqGrid_wrapper">
					<table id="table_list_agentsProfitAccount"></table>
					<div id="pager_list_agentsProfitAccount"></div>
                    <br /><br /><br /><br /><br /><br /><br /><br /><br /><br />
					</div>
				</div>
			</div>
		</div>
		
	</div>
	
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


        function IWS_CheckDecimal(obj) {
            if(obj.value){
                var temp = /^\-?\d+\.?\d*$/;
                if (temp.test(obj.value)) {
                    console.log("正则正确");
                } else {
                    obj.value = obj.value.substr(0, obj.value.length - 1);
                    IWS_CheckDecimal(obj);
                    // return false;
                }
			}
        }

        function amount(th){
            var regStrs = [
                ['^0(\\d+)$', '$1'], //禁止录入整数部分两位以上，但首位为0
                ['[^\\d\\.\\-]+$', ''], //禁止录入任何非数字和点
                ['\\.(\\d?)\\.+', '.$1'], //禁止录入两个以上的点
                ['\\-(\\d?)\\-+', '-$1'], //禁止录入两个以上的负号
                ['^(\\d+\\.\\d{2}).+', '$1'] //禁止录入小数点后两位以上
            ];
            for(var i=0; i<regStrs.length; i++){
                var reg = new RegExp(regStrs[i][0]);
                th.value = th.value.replace(reg, regStrs[i][1]);
            }
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
	/*表单提交时的处理*/
		function exportExcel() {  
			var data = $("#agentsProfitAccountForm").serialize();
			$.download('${ctx}/activityAccAdjustment/exportAdjustmentAmount.do',data,'post');
	    }
	
		$("#agentsProfitAccountForm").submit(function() {
			$("#table_list_agentsProfitAccount").setGridParam({
				datatype : 'json',
				page : 1
			//Replace the '1' here
			}).trigger("reloadGrid");
            //汇总
            collection();
            return false;
		});
		
		function getParams(o) {
			var data = $("#agentsProfitAccountForm").serializeArray();
			$.each(data, function() {
				o[this.name] = this.value || '';
			});
			o.agentNo = $("#agentNo").select2("val");
		}
		
		//调账明细
		function detail(id) {
            var agentNo=$("#table_list_agentsProfitAccount").jqGrid('getRowData',id).agentNo;
            if(agentNo){
                location.href='${ctx}/activityAccAdjustment/toAdjustmentDetail.do?agentNo='+agentNo;
			}
		};


		//汇总
		function collection(){
			$.ajax({
				cache: false,
				type: "POST",
				url:"${ctx}/activityAccAdjustment/findAdjustmentAmountCollection.do",
				data:$('#agentsProfitAccountForm').serialize(),// formid
				async: false,
				success: function(data) {
					$("#allAdjustAmount").html(data.allAdjustAmount);
					$("#activitySubsidyFreeze").html(data.activitySubsidyFreeze);
					$("#activitySubsidyBalance").html(data.activitySubsidyBalance);
					$("#activitySubsidyAvailableBalance").html(data.activitySubsidyAvailableBalance);
				}
			});

		}

		$(document).ready(function() {
			var lastsel;
			var data = $("#agentsProfitAccountForm").serialize();
			// 初始化表格
			$("#table_list_agentsProfitAccount")
					.jqGrid({url : "${ctx}/activityAccAdjustment/adjustmentAmount.do",
								datatype : "json",
								mtype : "POST",
								height:"auto",
								autowidth: true,
								shrinkToFit: false,
								autoScroll: false,
								rowNum: 10,
								rowList: [ 10, 20 ],
								colNames : ['代理商编号','代理商名称','代理商级别','剩余预调账金额 ','活动补贴账户余额 ','活动补贴账户已冻结金额 ','活动补贴账户可用余额 ', '操作'],
								colModel : [
										{name : 'agentNo',index : 'agentNo',width : 150,align : "right"},
                                    {name : 'agentName',index : 'agentName',width : 200,align : "right"},
                                    {name : 'agentLevel',index : 'agentLevel',width : 100,align : "right"},
										{name : 'adjustAmount',index : 'adjustAmount',width : 180,align : "right",formatter : 'number'},
										{name : 'activitySubsidyBalance',index : 'activitySubsidyBalance',width : 180,align : "right",formatter : 'number'},
										{name : 'activitySubsidyFreeze',index : 'activitySubsidyFreeze',width : 180,align : "right",formatter : 'number'},
										{name : 'activitySubsidyAvailableBalance',index : 'activitySubsidyAvailableBalance',width : 180,align : "right",formatter : 'number'},
										{name:'Detail',index:'id',width:100,align:"center",sortable:false, },
										 ],
								multiselect : false,//支持多项选择
								multiselectWidth : 80,
								multiboxonly: false,
								pager : "#pager_list_agentsProfitAccount",
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
									var ids=$("#table_list_agentsProfitAccount").jqGrid('getDataIDs');
				                    for(var i=0; i<ids.length; i++){
				                        var id=ids[i];
				                        var detail = "" ;
				                        detail += "<a href='javascript:void(0);'  title='调账明细' onclick='detail(" + id + ")' class='default-details'>调账明细</a>";
				                        jQuery("#table_list_agentsProfitAccount").jqGrid('setRowData', ids[i], { Detail: detail });
				                    }
								},
							});
            //汇总
            collection();
			jQuery("#table_list_agentsProfitAccount").jqGrid('setFrozenColumns');
			$(window).bind('resize',
					function() {
						var width = $('.jqGrid_wrapper').width();
						$('#table_list_agentsProfitAccount').setGridWidth(width);
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
		    		  minimumInputLength: 2,
		    		  language: "zh-CN",
		    		  templateResult: formatRepo, // omitted for brevity, see the source of this page
		    		  templateSelection: formatRepoSelection // omitted for brevity, see the source of this page
		    		}
	    		  );	

	</script>
	 </title>
</html>  
      