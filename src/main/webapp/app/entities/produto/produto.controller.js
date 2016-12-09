(function() {
    'use strict';

    angular
        .module('appApp')
        .controller('ProdutoController', ProdutoController);

    ProdutoController.$inject = ['$scope', '$state', 'Produto', 'ProdutoSearch'];

    function ProdutoController ($scope, $state, Produto, ProdutoSearch) {
        var vm = this;
        
        vm.produtos = [];
        vm.search = search;
        vm.loadAll = loadAll;

        loadAll();

        function loadAll() {
            Produto.query(function(result) {
                vm.produtos = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            ProdutoSearch.query({query: vm.searchQuery}, function(result) {
                vm.produtos = result;
            });
        }    }
})();
