
/* 功能：首页功能控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var app = angular.module("navigators.home", []);
	
	    /* 功能：首页功能控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netConnector----自定义前后端交互服务, netStorage----自定义缓存服务
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    app.controller("homeCtrl", ["$scope", "netConnector", "netStorage", function($scope, netConnector, netStorage) {
	    	
	    	/*****************************************调用缓存信息******************************************/
	    	var userInfo = netStorage.getItem("userInfo");
	    	$scope.personInfo = {
	    		name: userInfo.name,
	    		userType: userInfo.userType,
	    		major: userInfo.major
	    	};
	    	$scope.addAuthority = $scope.personInfo.userType === "student" ? "false" : "true";
	    	
	    	/*信息列表和信息详情的切换标志*/
			$scope.toggle = true;

			init();
			/*****************************************初始化信息列表*****************************************/
			function init() {
				netConnector.get("/loadAnnouncementsAction.do")
							.then(function(res) {
								$scope.tipsData = res.data.announcements;
							})
							.catch(function(err) {
								console.log(err);
							});
			}
			
			/****************************************添加信息:只作打开弹框的功能****************************************/
			$scope.addTip = function() {
				$scope.addTipData = {};
				$scope.submitted = false;
				$("#addNewsModal").modal("show");
			};
			
			/****************************************消息保存****************************************/
			/* 功能：进行消息的添加并发送到后台进行数据的保存
			 * 思路：点击保存按钮，将新创建消息发送到后台，消息包括标题（必要）、摘要、文件，只有在符合条件下才可以发送,系统自动生成创建人和创建日期
			 * 参数：
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
			$scope.saveAddTip =function() {
				$scope.submitted = true;
				var sendTip = {
					title: $scope.addTipData.title,
					abstract: $scope.addTipData.abstract,
					attachment: $scope.addTipData.fileInfo
				};
				console.log("发送添加数据", sendTip);
				if($scope.addTipForm.$valid) {
					netConnector.post("/addAnnouncementAction.do", sendTip)
								.then(function(res) {
									if(res.data.addResult === "success") {
										$("#addNewsModal").modal("hide");
										swal("提示！", "消息添加成功", "success");
										init();
									} else{
										swal("提示！"," 消息添加失败", "error");
									}
								})
								.catch(function(err) {
									console.log(err);
									swal("提示！"," 消息添加失败", "error");
								});
				}
			};
			
			/****************************************文件上传****************************************/
			/* 功能：在添加消息的时候进行相关附件的上传
			 * 思路：使用HTML5的accept属性进行文件的类型限定，对符合上传的文件调用HTML5的文件处理接口将其保存为URL的形式，并送到保存函数发送到后台
			 * 参数：null
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
			document.getElementById("tipFile").addEventListener("change", function(event) {
				/*上传控件*/
			 	var inputObj = document.getElementById("tipFile");
			 	/*上传控件的最新文件对象*/
				var fileObj = document.getElementById("tipFile").files[0];
				console.log("元素", inputObj);
				console.log("文件", fileObj)
				/*上传的文件信息*/
				$scope.addTipData.fileInfo = {
					attachmentName: fileObj.name,
					type: fileObj.name.substr(fileObj.name.lastIndexOf('.') + 1, 3)
				};
				/*读取文件对象*/
				var reader = new FileReader();
				/*将文件读取成为URL的形式*/
				reader.readAsDataURL(fileObj);
				/*当文件上传完成后触发，将上传的结果抽出来送到保存文件函数发送到后台*/
				reader.addEventListener("load",function(e) {
					$scope.addTipData.fileInfo.attachmentURL = e.target.result;
					/*模拟数据测试使用*/
					$scope.downloadTest = $scope.addTipData.fileInfo;
				});
			});
			
			/***************************************查看详情***************************************/
			/* 功能：进行信息列表和信息详情的相互切换
			 * 思路：使用一个切换标志对页面进行切换控制，并只有个点击信息查看详情的时候才会对信息进行请求数据
			 * 参数：
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
			$scope.tipObj = {}
			$scope.readDetail = function(item){
				/*视图的切换*/
				$scope.toggle = ! $scope.toggle;
				
				/*当是详细页面时才会进行请求的发起和数据获取*/
				if($scope.toggle === false) {
					var sendDetail = {
						announcementId: item.announcementId,
						name: item.name
					};
					console.log("发送详细信息", sendDetail);
					netConnector.get("/loadAnnounDetailAction.do", sendDetail)
								.then(function(res) {
									console.log("res.detail", res);
									$scope.tipObj = res.data;
									/*如果存在下载，那么就将当前的item送到下载函数*/
									if(res.data.isHaveAttachment) {
										$scope.currentTip = res.data;
									}
								})
								.catch(function(err) {
									console.log(err);
								});
				}
			};
			
			/*****************************************文件下载*****************************************/
			/* 功能：消息的附件下载，包括列表的下载和详细信息页面的附近下载
			 * 思路：
			 * 参数：
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
			$scope.downloadFile = function(event,item) {
				/*这里的item有两个来源：列表和详情*/
				$scope.currentTip = item ? item : $scope.currentTip;
				var sendTarget = {
					announcementId: $scope.currentTip.announcementId
				};
				
				netConnector.get("/attachmentDownloadAction.do", sendTarget)
							.then(function(res) {
								/*返回一个文件对象，包括url和文件名*/
								event.target.href = res.data.attachmentURL;
								event.target.download = res.data.attachmentName;
							})
							.catch(function(err){
								console.log(err);
							});
			};
			
			/*****************************************删除消息*****************************************/
			/* 功能：删除已经添加的消息
			 * 思路：通过id来对消息进行删除
			 * 参数：item----需要删除的消息
			 * 作者：liao
			 * 修改时间：2018-05-08
			 */
			$scope.deleteTip = function(item) {
				swal({
                        title: "你确定删除吗?",
                        text: "你将删除此消息!",
                        type: "warning",
                        showCancelButton: true,
                        confirmButtonText: "确定",
                        cancelButtonText: "取消",
                        closeOnConfirm: false,
                        closeOnCancel: true
                    },
                    function(isConfirm) {
                    	if(isConfirm) {
                    		var sendTipData = {
                    			announcementId: item.announcementId
                    		};
                    		netConnector.post("/deleteAnnouncementAction.do", sendTipData)
                    					.then(function(res) {
                    						/*删除成功*/
	                    					var isdeleted = res.data.deleteResult;
	                    					if(isdeleted === "success"){
	                    						init();
	                    						swal("删除成功", "", "success");
	                    					} else{
	                    						swal("删除失败", "未知错误", "error");
	                    					}
                    					})
                    					.catch(function(err) {
                    						swal("删除失败", "未知错误", "error");
                    						console.error(err);
                    					});
                    	}
                    });
			};
			
		    //ending controller
		    }]);
}());