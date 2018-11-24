/* 功能：导航控制模块
 * 思路：应用自调用函数分离全局作用域，利用路由进行加载
 * 参数：null
 * 作者：liao
 * 修改时间：2018-05-04
 */
(function() {
	var app = angular.module("navigators", []);
	 /* 功能：导航控制器
		 * 思路：AngularJS控制器
		 * 参数：$scope----AngularJS作用域, netConnector----自定义前后端交互服务, netStorage----自定义缓存服务
		 * 作者：liao
		 * 修改时间：2018-05-04
		 */
	    app.controller("navigatorsCtrl",["$scope", "$state", "netStorage", "netConnector", function($scope, $state, netStorage, netConnector) {
		
		/*****************************************调用缓存信息******************************************/
		var userInfo = netStorage.getItem("userInfo");
		$scope.username = userInfo.name;
		
		/*****************************************会话没有建立******************************************/
		if(userInfo == null) {
			$state.go("login");
		}
		
		$scope.isManager = false;
		$scope.isTeacher = false;
		
		/*开学日期输入元素 且显示默认值为当天的日期*/
		var inputDate = document.getElementById("startDate")
		inputDate.value = moment(new Date()).format("YYYY-MM-DD");
		
		/* 功能：控制角色权限的功能
		 * 思路：设置管理员和教师的标志，如果当前的角色是管理员或者老师，那么就对相应的标志进行设置，使得页面的权限功能得到相应的显示或者隐藏
		 * 参数：null
		 * 作者：liao
		 * 修改时间：2018-05-07
		 */
		if(userInfo.userType === "manager") {
			$scope.isManager = true;
		} else if(userInfo.userType === "teacher") {
			$scope.isTeacher = true;
		} else{
			$scope.isStudent = true;
		}
		
		/* 功能：主要登出功能
		 * 思路：点击登出按钮，清楚浏览器相关信息
		 * 参数：null
		 * 作者：liao
		 * 修改时间：2018-05-07
		 */
		$scope.logout = function() {
			$state.go("login");
			if(netStorage.getItem("userInfo")) {
				netStorage.removeItem("userInfo");
			}
			netConnector.get("/logoutAction.do");
		};
		
		//数据初始化
		init();
		
		/* 功能：管理员设定每一个学期的开学日期，用来给后端进行周次的参考计算
		 * 思路： 1、分为三种情况：未设定、已设定可修改、已设定不可修改
		 *       2、未设定:管理员还没有进行开学日期的设定，此时，当管理员进入系统时有弹框提示设定
		 *          已设定可修改：管理员已经设定了开学时间，但是在最初次设定开始的24小时内可以对开学日期进行进一步的修改，此时提示剩余修改日期
		 *          已设定不可修改：管理员已经设定了开学日期，但已经超过了可以修改日期，日期将在这一整个学期里都不可以修改知道后台自动清除
		 * 参数：null
		 * 作者：liao
		 * 修改时间：2018-05-07
		 */
		function init() {
			netConnector.get("/loadTermStartDateAction.do")
						.then(function(res) {
							var resData = res.data;
							console.log("获取日期", res);
							/*管理员*/
							if($scope.isManager) {
								/*未设定*/
								if(resData.noStartTime) {
									swal("提示！", "请管理员设定开学时间", "warning");
								} else {/*已设定的两种情况*/
									
									/* 将之前设置了的日期显示在页面上*/
									inputDate.value = moment(new Date(resData.termStartDate)).format("YYYY-MM-DD");
									/*可修改*/
									if(resData.settedStartTime) {
										document.getElementById("startDate").setAttribute("disabled", true);
									} 
								}
							} else {/*非非管理员角色,只能读取日期信息*/
								inputDate.value = resData.termStartDate ? moment(new Date(resData.termStartDate)).format("YYYY-MM-DD") : moment(new Date("2018-2-25")).format("YYYY-MM-DD");
								document.getElementById("startDate").setAttribute("disabled", true);
							}
							
						})
						.catch(function(err) {
							console.log(err);
						});
		}
		
		/* 功能：对开学日期进行设定
		 * 思路：当开学日期可以修改的时候，对开学日期进行设定，并且当失焦的时候向后台发送确认修改请求，日期格式为"YYYY-MM-DD"
		 * 参数：null
		 * 作者：liao
		 * 修改时间：2018-05-07
		 */
		inputDate.addEventListener("change",function(event) {
			var sendStartTime = {
				termStartDate: moment(inputDate.value).format("YYYY-MM-DD")
			};
			console.log("发送开学时间sendStartTime", sendStartTime);
			netConnector.post("/setTermStartDateAction.do", sendStartTime)
			 			.then(function(res) {
			 				console.log("设置日期");
			 				if(res.data.setResult === "success") {
			 					swal("提示！", "开学日期修改成功", "success");
			 				} else {
			 					swal("提示！", "开学日期修改失败", "error");
			 				}
			 			})
			 			.catch(function(err) {
			 				swal("提示！", "开学日期修改失败", "error");
			 				console.log(err);
			 			});
		});
		
		/* 功能：根据屏幕分辨率来控制导航的出现和隐藏
		 * 思路：到分辨率小于一定程度时，显示导航按钮来控制导航的显示或者隐藏
		 * 参数：null
		 * 作者：liao
		 * 修改时间：2018-05-25
		 */
		var nav = document.getElementById("navigation");
		var navBtn = document.getElementById("navigation-btn");
		window.addEventListener("resize", function() {
			if(document.body.clientWidth <= 750) {
				navBtn.style.display = "inline-block";
				nav.style.left = -220 + "px";
			} else {
				navBtn.style.display = "none";
				nav.style.left = 0;
			}
		});
		
		/* 功能：点击导航按钮，切换导航
		 * 思路：调节导航的左距离
		 * 参数：null
		 * 作者：liao
		 * 修改时间：2018-05-25
		 */
		navBtn.addEventListener("click", function() {
			var nav = document.getElementById("navigation");
			if(nav.style.left === 0 +"px") {
				nav.style.left = -220 + "px";
			} else {
				nav.style.left = 0;
			}
		}); 
		
		
		//ending controller
	}]);
	//function ending
}());
