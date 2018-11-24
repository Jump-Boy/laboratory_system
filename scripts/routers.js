


/* 功能：路由导航模块，进行页面的跳转和懒加载方式
* 思路：设定页面默认路由路径和页面路由跳转，依据url的哈希值、控制器和文件进行唯一的确定
* 参数：null
* 作者：liao
* 修改时间：2018-05-04
*/
(function () {
    var app = angular.module('app');
    
	   /* 功能：配置页面路由，懒加载依赖文件
		* 思路：利用第三方组件ui-router和懒加载组件，默认情况下自主跳到登录页面，其他情况会根据设定的路由请求进行按需跳转
		* 参数：$stateProvider、$urlRouterProvider----ui-router的路由导航接口；$ocLazyLoadProvider、IdleProvider----懒加载组件接口
		* 作者：liao
		* 修改时间：2018-05-04
		*/
    	app.config([
	        '$stateProvider',
	        '$urlRouterProvider',
	        '$ocLazyLoadProvider',
	        'IdleProvider',
	        function ($stateProvider, $urlRouterProvider, $ocLazyLoadProvider, IdleProvider) {
	          /*配置Idle*/ 
	          IdleProvider.idle(5); /*秒*/ 
	          IdleProvider.timeout(120); /*秒*/ 
	          
            $ocLazyLoadProvider.config({
              /*动态加载*/ 
              debug: true
            });
            /*默认跳转到登录页面*/
			$urlRouterProvider.otherwise("/login");
            
            $stateProvider
                 /*登录页面*/
            	 .state('login', {
	                url: '/login',
	                templateUrl: "views/login.html",
	                controller: "loginCtrl",
		            /*按需加载依赖文件*/
		            resolve: {
	   	                loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                        return $ocLazyLoad.load([{
	                            	files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                           	},
	                            {
	                                name: 'oitozero.ngSweetAlert',
	                                files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                            },
	                            {
	                            	files: ['js/plugins/jquery-particles/js/particles.min.js']
	                           	},
	                           	{
	                            	files: ['js/plugins/jquery-particles/js/app.js']
	                           	},
	                            {
	                             	name: 'login',
                                  	files: ['scripts/login.js']
                                }
	                        ]);
	                    }]
	                }
                 })
            	 /*导航菜单页面*/
	             .state('navigators', {
	                	url: '/navigators',
	                	templateUrl: "views/navigators.html",
	                	controller: "navigatorsCtrl",
	                	resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                	name: 'navigators',
                                    	files: ['scripts/navigators.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*首页页面*/
	             .state('navigators.home', {
	                	url: '/home',
	                	templateUrl: "views/home.html",
	                	controller: "homeCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
	                                	name: 'navigators.home',
                                    	files: ['scripts/home.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*角色管理页面*/
	             .state('navigators.roleManager', {
	                	url: '/roleManager',
	                	templateUrl: "views/roleManager.html",
	                	controller: "roleManagerCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                    name: 'datePicker',
	                                    files: ['css/plugins/datapicker/angular-datapicker.css', 'js/plugins/datapicker/angular-datepicker.js']
	                                },
	                                {
	                                    serie: true,
	                                    files: ['js/plugins/daterangepicker/daterangepicker.js', 'css/plugins/daterangepicker/daterangepicker-bs3.css']
                                	},
	                                {
	                                	name: 'navigators.roleManager',
                                    	files: ['scripts/roleManager.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*课程管理页面*/
	             .state('navigators.subjectManage', {
	                	url: '/subjectManage',
	                	templateUrl: "views/subjectManage.html",
	                	controller: "subjectManageCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                    name: 'datePicker',
	                                    files: ['css/plugins/datapicker/angular-datapicker.css', 'js/plugins/datapicker/angular-datepicker.js']
	                                },
	                                {
	                                    serie: true,
	                                    files: ['js/plugins/daterangepicker/daterangepicker.js', 'css/plugins/daterangepicker/daterangepicker-bs3.css']
                                	},
	                                {
	                                	name: 'navigators.subjectManage',
                                    	files: ['scripts/subjectManage.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*开始选课页面*/
	             .state('navigators.selectSubject', {
	                	url: '/selectSubject',
	                	templateUrl: "views/selectSubject.html",
	                	controller: "selectSubjectCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                    name: 'datePicker',
	                                    files: ['css/plugins/datapicker/angular-datapicker.css', 'js/plugins/datapicker/angular-datepicker.js']
	                                },
	                                {
	                                    serie: true,
	                                    files: ['js/plugins/daterangepicker/daterangepicker.js', 'css/plugins/daterangepicker/daterangepicker-bs3.css']
                                	},
	                                {
	                                	name: 'navigators.selectSubject',
                                    	files: ['scripts/selectSubject.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*查看课表页面*/
	             .state('navigators.readClassSheet', {
	                	url: '/readClassSheet',
	                	templateUrl: "views/readClassSheet.html",
	                	controller: "readClassSheetCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                	name: 'navigators.readClassSheet',
                                    	files: ['scripts/readClassSheet.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*随堂打分页面*/
	             .state('navigators.markScore', {
	                	url: '/markScore',
	                	templateUrl: "views/markScore.html",
	                	controller: "markScoreCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                	name: 'navigators.markScore',
                                    	files: ['scripts/markScore.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*查看成绩页面*/
	             .state('navigators.readScore', {
	                	url: '/readScore',
	                	templateUrl: "views/readScore.html",
	                	controller: "readScoreCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                	name: 'navigators.readScore',
                                    	files: ['scripts/readScore.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*成绩管理页面*/
	             .state('navigators.scoreManage', {
	                	url: '/scoreManage',
	                	templateUrl: "views/scoreManage.html",
	                	controller: "scoreManageCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                	name: 'navigators.scoreManage',
                                    	files: ['scripts/scoreManage.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*个人信息页面*/
	             .state('navigators.personInfo', {
	                	url: '/personInfo',
	                	templateUrl: "views/personInfo.html",
	                	controller: "personInfoCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                	name: 'navigators.personInfo',
                                    	files: ['scripts/personInfo.js']
                                    }
	                            ]);
	                        }]
	                    }
	                })
	             /*修改密码页面*/
	             .state('navigators.resetPassword', {
	                	url: '/resetPassword',
	                	templateUrl: "views/resetPassword.html",
	                	controller: "resetPasswordCtrl",
	                    resolve: {
	                        loadPlugin: ['$ocLazyLoad', function ($ocLazyLoad) {
	                            return $ocLazyLoad.load([{
	                                files: ['js/plugins/sweetalert/sweetalert.min.js', 'css/plugins/sweetalert/sweetalert.css']
	                            	},
	                                {
	                                    name: 'oitozero.ngSweetAlert',
	                                    files: ['js/plugins/sweetalert/angular-sweetalert.min.js']
	                                },
	                                {
		                                serie: true,
		                                files: ['js/plugins/dataTables/datatables.min.js', 'css/plugins/dataTables/datatables.min.css']
		                            },
	                                {
	                                    serie: true,
	                                    name: 'datatables.buttons',
	                                    files: ['js/plugins/dataTables/angular-datatables.buttons.min.js']
	                                },
	                                {
	                                    serie: true,
	                                    name: 'datatables',
	                                    files: ['js/plugins/dataTables/angular-datatables.min.js']
	                                },
	                                {
	                                	name: 'navigators.resetPassword',
                                    	files: ['scripts/resetPassword.js']
                                    }
	                            ]);
	                        }]
	                    }
	                });
        }]);
})();