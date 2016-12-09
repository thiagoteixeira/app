(function() {
    'use strict';

    angular
        .module('appApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('fornecedor', {
            parent: 'entity',
            url: '/fornecedor',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'appApp.fornecedor.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/fornecedor/fornecedors.html',
                    controller: 'FornecedorController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('fornecedor');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('fornecedor-detail', {
            parent: 'entity',
            url: '/fornecedor/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'appApp.fornecedor.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/fornecedor/fornecedor-detail.html',
                    controller: 'FornecedorDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('fornecedor');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Fornecedor', function($stateParams, Fornecedor) {
                    return Fornecedor.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'fornecedor',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('fornecedor-detail.edit', {
            parent: 'fornecedor-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/fornecedor/fornecedor-dialog.html',
                    controller: 'FornecedorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Fornecedor', function(Fornecedor) {
                            return Fornecedor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('fornecedor.new', {
            parent: 'fornecedor',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/fornecedor/fornecedor-dialog.html',
                    controller: 'FornecedorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                nome: null,
                                cidade: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('fornecedor', null, { reload: 'fornecedor' });
                }, function() {
                    $state.go('fornecedor');
                });
            }]
        })
        .state('fornecedor.edit', {
            parent: 'fornecedor',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/fornecedor/fornecedor-dialog.html',
                    controller: 'FornecedorDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Fornecedor', function(Fornecedor) {
                            return Fornecedor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('fornecedor', null, { reload: 'fornecedor' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('fornecedor.delete', {
            parent: 'fornecedor',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/fornecedor/fornecedor-delete-dialog.html',
                    controller: 'FornecedorDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Fornecedor', function(Fornecedor) {
                            return Fornecedor.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('fornecedor', null, { reload: 'fornecedor' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
