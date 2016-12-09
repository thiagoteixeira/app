(function() {
    'use strict';

    angular
        .module('appApp')
        .controller('FornecedorDetailController', FornecedorDetailController);

    FornecedorDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Fornecedor'];

    function FornecedorDetailController($scope, $rootScope, $stateParams, previousState, entity, Fornecedor) {
        var vm = this;

        vm.fornecedor = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('appApp:fornecedorUpdate', function(event, result) {
            vm.fornecedor = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
