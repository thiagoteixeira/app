(function() {
    'use strict';

    angular
        .module('appApp')
        .controller('ProdutoDetailController', ProdutoDetailController);

    ProdutoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Produto', 'Fornecedor'];

    function ProdutoDetailController($scope, $rootScope, $stateParams, previousState, entity, Produto, Fornecedor) {
        var vm = this;

        vm.produto = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('appApp:produtoUpdate', function(event, result) {
            vm.produto = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
